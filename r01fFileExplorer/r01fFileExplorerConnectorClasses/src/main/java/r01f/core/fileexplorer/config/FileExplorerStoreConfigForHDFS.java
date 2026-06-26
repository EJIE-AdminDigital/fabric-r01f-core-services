package r01f.core.fileexplorer.config;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.apache.hadoop.conf.Configuration;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import r01f.filestore.api.FileStoreType;
import r01f.filestore.api.hdfs.HDFSConfigurationBuilder;
import r01f.types.Path;
import r01f.types.TimeLapse;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class FileExplorerStoreConfigForHDFS
	 extends FileExplorerStoreConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final Configuration _hdfsConf;
	@Getter protected final TimeLapse _hdfsCredentialsRefreshPeriod;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerStoreConfigForHDFS(final Path fsRootFullPath,
										  final Configuration hdfsConfig,
										  final TimeLapse hdfsCredentialsRefreshPeriod) {
		super(FileStoreType.HDFS,
			  fsRootFullPath);
		_hdfsConf = hdfsConfig;
		_hdfsCredentialsRefreshPeriod = hdfsCredentialsRefreshPeriod;
	}
	public FileExplorerStoreConfigForHDFS(final XMLPropertiesForAppComponent xmlProps,final String xPathPrefix) {
		this(_fileExplorerRootFullPathFrom(xmlProps,xPathPrefix),
			 _hdfsConfigurationUsing(xmlProps,xPathPrefix),
			 _hdfsCredentialRefreshPeriodFrom(xmlProps,xPathPrefix));
	}
	public static FileExplorerStoreConfigForHDFS from(final XMLPropertiesForAppComponent xmlProps,final String xPathPrefix) {
		return new FileExplorerStoreConfigForHDFS(xmlProps,xPathPrefix);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected static TimeLapse _hdfsCredentialRefreshPeriodFrom(final XMLPropertiesForAppComponent props,final String xpathPrefix) {
		return HDFSConfigurationBuilder.fromXMLPropertiesBuilderUsingDefaultXPathPrefix("file-explorer")
									   .hdfsCredentialRefreshPeriodFrom(props,xpathPrefix);
	}
	/**
	 * Properties:
	 * 	<fileStore>
	 * 		<hadoop>
	 *			<hadoop_home_dir></hadoop_home_dir>	<!-- Only necessary for Windows -->
	 *
	 *			<user>pcontenidos</user>
	 *			<credentialsRefreshPeriod>6h</credentialsRefreshPeriod>
	 *
	 *			<!-- if using kerberos credentials -->
	 *			<kerberos>
	 *				<realm></realm>
	 *				<domain></domain>
	 *				<ticketPath></ticketPath>
	 *			</kerberos>
	 *		</hadoop>
	 *	</fileStore>
	 * @param props
	 * @return
	 * @throws IOException
	 * @throws LoginException
	 */
	@SneakyThrows
	protected static Configuration _hdfsConfigurationUsing(final XMLPropertiesForAppComponent props,final String xpathPrefix) {
		return HDFSConfigurationBuilder.fromXMLPropertiesBuilderUsingDefaultXPathPrefix("file-explorer")
									   .hdfsConfigurationUsing(props,xpathPrefix);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{}\n{}",
								  super.debugInfo(),
								  _hdfsConf);
	}
}
