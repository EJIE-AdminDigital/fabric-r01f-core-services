package r01f.core.fileexplorer.config;

import r01f.config.ContainsConfigData;
import r01f.debug.Debuggable;
import r01f.filestore.api.FileStoreType;
import r01f.types.Path;

/**
 * File explorer config is splitted in TWO data structures:
 * 		- file explorer: the file-explorer component behavior
 * 		- file store: the access to the underlying storage
 * This object models the [file-store] part
 * 
 * The file-store config is something like:
 * <pre class='brush:xml'>
 *     <!-- file storage -->                                                                        
 *     <fileStore impl="HDFS"> 	<!-- LOCAL / HDFS / S3 -->                                            
 *     	<rootPath>/r01</rootPath>                                                                
 *                                                                                                  
 *     	<!-- hadoop config -->                                                                   
 *     	<hadoop>                                                                                 
 *     		<home_dir></home_dir>	<!-- Only necessary for Windows -->                          
 *                                                                                                  
 *     		<user>[USER]</user>                                                                  
 *     		<config_home>[CONFIG_HOME]</config_home>                                             
 *                                                                                                  
 *     		<credentialsRefreshPeriod>9h</credentialsRefreshPeriod>                              
 *                                                                                                  
 *     		<auth_mode>BASIC_AUTH_MODE</auth_mode>	<!-- BASIC_AUTH_MODE / KERBEROS_AUTH_MODE -->
 *                                                                                                  
 *     		<!-- if using kerberos credentials -->                                               
 *     		<kerberos>                                                                           
 *     			<realm>[REALM]</realm>                                                           
 *     			<domain>[DOMAIN]</domain>                                                        
 *     			<ticketPath>[TICKET_PATH]</ticketPath>                                           
 *     		</kerberos>                                                                          
 *     	</hadoop>                                                                                
 *     </fileStore>
 * </pre>
 */
public interface FileExplorerStoreConfig
		 extends ContainsConfigData,
  			 	 Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the file store type
	 */
	public FileStoreType getType();
	/**
	 * @return the filesystem root full path
	 */
	public Path getFsRootFullPath();
}
