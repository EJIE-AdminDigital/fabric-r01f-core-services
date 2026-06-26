package r01f.core.fileexplorer.web;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.core.fileexplorer.FileExplorerStoreMediator;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.config.FileExplorerConfig;
import r01f.core.fileexplorer.config.FileExplorerConfigLoader;
import r01f.core.fileexplorer.config.FileExplorerStoreConfig;
import r01f.core.fileexplorer.config.FileExplorerVolumeSpec;
import r01f.filestore.api.FileStoreType;
import r01f.internal.R01FAppCodes;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.objectstreamer.MarshallerImpl;
import r01f.patterns.ThrowingFunction;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;
import r01f.validation.ObjectValidationResult;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public abstract class FileExplorerConnectorServletBase
	 		  extends HttpServlet {

	private static final long serialVersionUID = 9129230897105924049L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Map<FileStoreType,FileExplorerStoreMediator> _fsMediatorByStoreType;
	protected final Collection<FileExplorerVolumeSpec> _vols;

	protected final Marshaller _marshaller;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerConnectorServletBase(final Collection<FileExplorerStoreConfig> fileStores,
											final Collection<FileExplorerVolumeSpec> vols) throws IOException {
		super();

		log.warn("[File Explorer] (connector servlet) > INIT...");

		// init marshaller used to return json
		// 	- build it and set custom configuration
	    MarshallerImpl marshallerImplementation = MarshallerBuilder.findTypesToMarshallAtJavaPackages(JavaPackage.named(R01FAppCodes.APP_CODE))
																   .build();
	    _marshaller = marshallerImplementation;

		// validations
		if (CollectionUtils.isNullOrEmpty(fileStores)) throw new IllegalArgumentException("The [file store] config is mandatory!");
		_validateVolumes(vols);

		// init file explorer mediator and vols
		_fsMediatorByStoreType = fileStores.stream()
										   // create the [file explorer mediator] from the [file store config]
										   .map(ThrowingFunction.unchecked(fsConfig -> FileExplorerStoreMediator.createUsing(fsConfig)))
										   // transform to map
										   .collect(Collectors.toMap(fsMediator -> fsMediator.getStoreType(),Function.identity()));
		_vols = vols;
	}
	public FileExplorerConnectorServletBase(final XMLPropertiesForAppComponent fileStoreProps,final String fileStorePropsXPathPrefix,
											final XMLPropertiesForAppComponent fileExplorerVolProps,final String fileExplorerPropsXPathPrefix) {
		// init marshaller used to return json
	    _marshaller = MarshallerBuilder.findTypesToMarshallAtJavaPackages(JavaPackage.named(R01FAppCodes.APP_CODE))
									   .build();
	    // init store & volumes
		FileExplorerStoreConfig storeCfg = FileExplorerConfigLoader.loadFileExplorerStoreConfigFrom(fileStoreProps,fileStorePropsXPathPrefix);
		FileExplorerConfig fileExplorerConfig = FileExplorerConfigLoader.loadFileExplorerConfigFrom(fileExplorerVolProps,fileExplorerPropsXPathPrefix)
																		.using(_marshaller);
		_validateVolumes(fileExplorerConfig.getVolumeList());

		// init file explorer mediator and vols
		_fsMediatorByStoreType = List.of(storeCfg)
									 .stream()
									 // create the [file explorer mediator] from the [file store config]
									 .map(ThrowingFunction.unchecked(fsConfig -> FileExplorerStoreMediator.createUsing(fsConfig)))
									 // transform to map
									 .collect(Collectors.toMap(fsMediator -> fsMediator.getStoreType(),Function.identity()));
		_vols = fileExplorerConfig.getVolumeList();
	}
	private void _validateVolumes(final Collection<FileExplorerVolumeSpec> vols) {
		if (CollectionUtils.isNullOrEmpty(vols)) throw new IllegalArgumentException("The [file explorer] volumes are mandatory!");
		boolean anyVolNotValid = vols.stream()
									 // validate all vols
									 .map(vol -> {
										 		ObjectValidationResult<FileExplorerVolumeSpec> volValid = vol.validate();
										 		if (volValid.isNOTValid()) log.error("[file explorer] volume (alias={}) is NOT valid: {}",
										 											 vol.getAlias(),
										 											 volValid.asNOKValidationResult().getReason());
										 		return volValid;
									 	  })
									 // find one not valid
									 .anyMatch(valid -> valid.isNOTValid());
		if (anyVolNotValid) throw new IllegalArgumentException("A [file explorer] volume is NOT valid");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET / POST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void doGet(final HttpServletRequest request,final HttpServletResponse response) throws ServletException,
																									 IOException {
		_service(request,response);
	}
	@Override
	protected void doPost(final HttpServletRequest request,final HttpServletResponse response) throws ServletException,
																									  IOException {
		_service(request,response);
	}
	protected void _service(final HttpServletRequest request,final HttpServletResponse response) throws ServletException,
																									  	IOException {
		// create the connector by creating the volumes by default
		// this function can be overridden to implement custom logic
		// when creating the volumes
		Collection<FileExplorerVolume> vols = _vols.stream()
												   .map(ThrowingFunction.exceptionIgnoredAndReturnNull(volSpec -> _createVolume(volSpec)))
												   .filter(Objects::nonNull)
												   .toList();
		FileExplorerConnectorWebController fileExplorerConnector = new FileExplorerConnectorWebController(_marshaller,
																										  vols);
		fileExplorerConnector.execute(request,response);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTILS
/////////////////////////////////////////////////////////////////////////////////////////
	protected FileExplorerVolume _createVolume(final FileExplorerVolumeSpec volSpec) throws IOException {
		FileExplorerStoreMediator fsMediator = _fsMediatorByStoreType.get(volSpec.getStoreType());
		if (fsMediator == null) throw new IllegalStateException(String.format("There does NOT exist a file-store with type %s",
																			  volSpec.getStoreType()));
		FileExplorerVolume outVol = new FileExplorerVolume(volSpec,
									  					   fsMediator);
		return outVol;
	}
}
