package r01f.core.fileexplorer.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.config.FileExplorerStoreConfig;
import r01f.core.fileexplorer.config.FileExplorerVolumeSpec;
import r01f.guids.OID;
import r01f.patterns.FactoryFrom;
import r01f.patterns.ThrowingFunction;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.types.Path;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * A servlet for a single {@link FileExplorerVolume} based file-explorer
 * Just extend like:
 * <pre class='brush:java'>
 *		@WebServlet(name = "FileExplorerConnectorServlet",
 *					urlPatterns = {"/elfinder-connector"})
 *		@MultipartConfig(fileSizeThreshold=1024*1024*100, 	// 100 MB 
 *		                  maxFileSize=1024*1024*1024*5,     	// 5 GB
 *		                  maxRequestSize=1024*1024*100)   	// 100 MB
 *		public class FileExplorerConnectorServlet 
 *			 extends FileExplorerForSingleVolumeConnectorServletBase<MyOID> {
 *			 
 *			public FileExplorerConnectorServlet() throws IOException {
 *				super(List.of(_createFileStoreConfig()),List.of(_createVolumeSpec()),
 *					  MyOID.class,oidStr -> MyOID.forId(oidStr.toString()),
 *					  // given a [business object oid] returns a volume-root relative path
 *					  // that is added to the volume-root path defined at the volume spec
 *					  (oid,businessObjShortName,theVols) -> {
 *						  return theVols.stream()
 *								  		.map(volSpec -> {
 *								  				String volAlias = businessObjShortName;
 *
 *												// use a [busineess service] to get a path relative
 *												// to the volume's store root path
 *								  				Path volOidDependentRelPathFromStoreRoot = _bussinessServiceThatReturnsRelPathFromStoreRootThatDependsUponTheBusinessOid(oid);
 *								  					
 *								  				return volSpec.cloneWith(volAlias,
 *								  										 volOidDependentRelPathFromStoreRoot);
 *								  			 })
 *								  		.toList();
 *					  });
 *		 	}
 *		}
 * </pre>
 * @param <O>
 */
public abstract class FileExplorerForBusinessObjectConnectorServletBase<O extends OID> 
	 		  extends FileExplorerConnectorServletBase {

	private static final long serialVersionUID = -5340956015454682517L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Class<O> _oidType;
	protected final FactoryFrom<CharSequence,O> _oidFromStringFactory;
	
	// given a business object oid (ie a contentOid or a serviceOid) 
	// returns the Path of the folder containing the file assets
	protected final FileExplorerBusinessObjToVolumes<O> _businessObjOidToVolumes;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR	
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerForBusinessObjectConnectorServletBase(// file stores & volumes
															 final Collection<FileExplorerStoreConfig> fileStores,
															 final Collection<FileExplorerVolumeSpec> vols,
															 // business 
															 final Class<O> oidType,final FactoryFrom<CharSequence,O> oidFromStringFactory,
															 final FileExplorerBusinessObjToVolumes<O> businessObjOidToVolumes) throws IOException {
		super(fileStores,vols);
		
		_oidType = oidType;
		_oidFromStringFactory = oidFromStringFactory;
		_businessObjOidToVolumes = businessObjOidToVolumes;
	}
	public FileExplorerForBusinessObjectConnectorServletBase(// file stores & volumes
															 final XMLPropertiesForAppComponent fileStoreProps,final String fileStorePropsXPathPrefix,
															 final XMLPropertiesForAppComponent fileExplorerVolProps,final String fileExplorerPropsXPathPrefix,
															 // business 
															 final Class<O> oidType,final FactoryFrom<CharSequence,O> oidFromStringFactory,
															 final FileExplorerBusinessObjToVolumes<O> businessObjOidToVolumes) {
		super(fileStoreProps,fileStorePropsXPathPrefix,
			  fileExplorerVolProps,fileExplorerPropsXPathPrefix);
		
		_oidType = oidType;
		_oidFromStringFactory = oidFromStringFactory;
		_businessObjOidToVolumes = businessObjOidToVolumes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET / POST
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	protected void _service(final HttpServletRequest request,final HttpServletResponse response) throws ServletException, 
																									  	IOException {
		HttpRequestParamsWrapper reqWrap = new HttpRequestParamsWrapper(request);
		
		// if the request contains a parameter named "businessObjOid" use the FileExplorerBusinessObjToVolumes 
		// to customize the volumes (ie set a starting path relative from the [storage] root)
		// note: add to the file-explorer options:
		//			defaultOpts : {
		//				customData : {
		//					businessObjShortName: "my_obj",
		//					businessObjOid: "the_business_obj_oid"	
		//				}
		O businessObjOid = reqWrap.getMandatoryParameter(FileExplorerConstants.REQ_PARAMETER_BUSINESS_OBJ_OID)
								  .asOid(_oidType)
								  .usingFactory(_oidFromStringFactory);
		String businessObjShortName = reqWrap.getParameter(FileExplorerConstants.REQ_PARAMETER_BUSINESS_OBJ_NAME)
											 .asString()
											 .orNull();
		Path businessObjTargetPath = reqWrap.getParameter(FileExplorerConstants.REQ_PARAMETER_BUSINESS_OBJ_TARGET_PATH)
											.asPath()
											.orNull();
		// get the volumes for the business object oid
		Collection<FileExplorerVolume> vols = _businessObjOidToVolumes.volumesFor(businessObjOid,
																				  businessObjShortName,
																				  businessObjTargetPath,
																				  _vols,
																				  request)
																	  .stream()
																	  .map(ThrowingFunction.exceptionIgnoredAndReturnNull(volSpec -> _createVolume(volSpec)))
																	  .filter(Objects::nonNull)
																	  .toList();
		// create the connector
		FileExplorerConnectorWebController fileExplorerConnector = new FileExplorerConnectorWebController(_marshaller,
																										  vols);
		fileExplorerConnector.execute(request,response);
	}
}
