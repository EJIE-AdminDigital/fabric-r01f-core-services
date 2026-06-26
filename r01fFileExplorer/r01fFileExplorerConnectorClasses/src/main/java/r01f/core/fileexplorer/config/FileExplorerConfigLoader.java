package r01f.core.fileexplorer.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.filestore.api.FileStoreType;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Helper class for [file explorer] config load
 * File explorer config is splitted in TWO data structures:
 * 		- file explorer: the file-explorer component behavior > {@link FileExplorerConfig}
 * 		- file store: the access to the underlying storage > {@link FileExplorerStoreConfig}
 * 
 * Usually there are TWO properties files:
 * 
 * [1] - The file-store config is something like:
 * <pre class='brush:xml'>
 *          <!-- file storage -->                                                                        
 *          <fileStore impl="HDFS"> 	<!-- LOCAL / HDFS / S3 -->                                            
 *          	<rootPath>/r01</rootPath>                                                                
 *                                                                                                       
 *          	<!-- hadoop config -->                                                                   
 *          	<hadoop>                                                                                 
 *          		<home_dir></home_dir>	<!-- Only necessary for Windows -->                          
 *                                                                                                       
 *          		<user>[USER]</user>                                                                  
 *          		<config_home>[CONFIG_HOME]</config_home>                                             
 *                                                                                                       
 *          		<credentialsRefreshPeriod>9h</credentialsRefreshPeriod>                              
 *                                                                                                       
 *          		<auth_mode>BASIC_AUTH_MODE</auth_mode>	<!-- BASIC_AUTH_MODE / KERBEROS_AUTH_MODE -->
 *                                                                                                       
 *          		<!-- if using kerberos credentials -->                                               
 *          		<kerberos>                                                                           
 *          			<realm>[REALM]</realm>                                                           
 *          			<domain>[DOMAIN]</domain>                                                        
 *          			<ticketPath>[TICKET_PATH]</ticketPath>                                           
 *          		</kerberos>                                                                          
 *          	</hadoop>                                                                                
 *          </fileStore>                                                                                 	 
 * </pre>
 * 
 * [2] - the file-explorer config is something like:
 * <pre class='brush:xml'>
 *		<file-explorer>
 *			<thumbnail>
 *				<width>80</width>
 *			</thumbnail>
 *			<volumes>
 *				<volume>
 *					<source>r01fs</source>
 *					<alias>/</alias>
 *					<path>/</path>
 *					<locale>en_US</locale>
 *					<constraint locked="false" readable="true" writable="true"/>
 *				</volume>
 *			</volumes>
 *			<!-- a collection of read-only files -->
 *			<!--
 *			<read-only-files> 
 *				<file-regex>a_file</file-regex>
 *				<file-regex>another_file</file-regex>
 *			</read-only-files>
 *			-->
 *		</file-explorer>
 * </pre>
 * 
 * Usage:
 * <pre class='brush:java'>
 * 		// load the file-store config
 * 		FileExplorerStoreConfig storeConfig = FileExplorerConfigLoader.loadFileExplorerStoreConfigFrom(fsStoreProps,xPathPrefix);
 * 
 * 		// load the file-explorer config
 * 		FileExplorerConfig fsConfig = FileExplorerConfigLoader.loadFileExplorerConfigFrom(fileExplorerProps,xPathPrefix)
 * 															  .using(marshaller);
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class FileExplorerConfigLoader {
/////////////////////////////////////////////////////////////////////////////////////////
//	FILE EXPLORER CONFIG
/////////////////////////////////////////////////////////////////////////////////////////	
	public static FileExplorerConfigLoaderMarshallerStep loadFileExplorerConfigFrom(final XMLPropertiesForAppComponent fileExplorerProps,final String xPathPrefix) {
		return new FileExplorerConfigLoader() { /* nothing */ }
						.new FileExplorerConfigLoaderMarshallerStep(fileExplorerProps,xPathPrefix);
	}
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public class FileExplorerConfigLoaderMarshallerStep {
		private final XMLPropertiesForAppComponent _fileExplorerProps;
		private final String _xPathPrefix;
		
		public FileExplorerConfig using(final Marshaller marshaller) {
			Path fileExplorerConfigPath = Strings.isNOTNullOrEmpty(_xPathPrefix) ? Path.from(_xPathPrefix,"file-explorer")
																				 : Path.from("file-explorer");
			return _fileExplorerProps.propertyAt(fileExplorerConfigPath)
						 			 .asObject(marshaller,
						 					   FileExplorerConfig.class);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FILE STORE CONFIG
/////////////////////////////////////////////////////////////////////////////////////////
	public static FileExplorerStoreConfig loadFileExplorerStoreConfigFrom(final XMLPropertiesForAppComponent fileStoreProps,final String xPathPrefix) {
		// [1] - Get the file-store config
		Path fileStoreTypeXPath = Path.from(xPathPrefix,"/fileStore/@impl");
		FileStoreType fileStoreType = fileStoreProps.propertyAt(fileStoreTypeXPath)
													.asEnumElement(FileStoreType.class,
															  	   FileStoreType.LOCAL);
		FileExplorerStoreConfig outStoreConfig = null;
		if (fileStoreType.is(FileStoreType.LOCAL)) {
			outStoreConfig = FileExplorerStoreConfigForLocal.from(fileStoreProps,xPathPrefix);
		} else if (fileStoreType.is(FileStoreType.HDFS)) {
			outStoreConfig = FileExplorerStoreConfigForHDFS.from(fileStoreProps,xPathPrefix);
		} else if (fileStoreType.is(FileStoreType.S3)) {
			outStoreConfig = FileExplorerStoreConfigForS3.from(fileStoreProps,xPathPrefix);
		} else {
			throw new IllegalStateException(fileStoreType + " is NOT a supported file store!");
		}
		return outStoreConfig;
	}
}
