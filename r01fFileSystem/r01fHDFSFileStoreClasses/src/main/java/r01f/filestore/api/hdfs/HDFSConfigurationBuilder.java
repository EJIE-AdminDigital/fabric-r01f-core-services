package r01f.filestore.api.hdfs;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import r01f.types.Path;
import r01f.types.TimeLapse;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Utils for creating HDFS Configuration
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class HDFSConfigurationBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static Configuration hdfsConfigurationFor(final Path hdfsHomeDirPath,final Path configHomePath) {
		if (hdfsHomeDirPath != null) System.setProperty("hadoop.home.dir",hdfsHomeDirPath.asAbsoluteString());

		Path coreSiteXmlFilePath = configHomePath.joinedWith("core-site.xml");
		Path hdfsSiteXmlFilePath = configHomePath.joinedWith("hdfs-site.xml");

		Configuration conf = new Configuration();
		conf.addResource(coreSiteXmlFilePath.asRelativeString());
		conf.addResource(hdfsSiteXmlFilePath.asRelativeString());
		conf.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem");
		conf.set("fs.file.impl","org.apache.hadoop.fs.LocalFileSystem");

		// log
		log.info("[hadoop config]: hadoop.home.dir={}",hdfsHomeDirPath);
		log.info("[hadoop config]: core-site.xml={}",coreSiteXmlFilePath.asRelativeString());
		log.info("[hadoop config]: hdfs-site.xml={}",hdfsSiteXmlFilePath.asRelativeString());

		// return
		return conf;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	AUTH
/////////////////////////////////////////////////////////////////////////////////////////
	public static void hdfsNoCredentialsAuth(final Configuration config,
											 final String hdfsUser) {
		log.warn("[hadoop config]: USER={}",hdfsUser);

		// set some env dependent vars
		if (hdfsUser != null) System.setProperty("HADOOP_USER_NAME",hdfsUser);

		// create hdfs auth object
		UserGroupInformation noCredentialsAuth = UserGroupInformation.createRemoteUser(hdfsUser);
		UserGroupInformation.setLoginUser(noCredentialsAuth);

		// config: set the auth
		//UserGroupInformation.setConfiguration(config);
	}
	public static void hdfsKerberosAuth(final Configuration config,
										final String hdfsUser,final String realm,final String domain,final Path ticketPath) throws IOException {
		log.info("[hadoop config]: USER={}",hdfsUser);
		log.info("[hadoop config]: KERBEROS REALM={}",realm);
		log.info("[hadoop config]: KERBEROS DOMAIN={}",domain);
		log.info("[hadoop config]: KERBEROS TICKET PATH={}",ticketPath.asAbsoluteString());

		// set some env dependent vars
		System.setProperty("java.security.krb5.realm", realm.toUpperCase());
		System.setProperty("java.security.krb5.kdc",domain);

		// create the hdfs auth object
		UserGroupInformation.loginUserFromKeytab(hdfsUser,ticketPath.asAbsoluteString());

		// config: set the auth
		UserGroupInformation.setConfiguration(config);
		config.set("hadoop.security.authentication","kerberos");
		// Obtain pattern value from dfs.namenode.kerberos.principal property in hdfs-site.xml file.
		// This pattern is different in Ambari and Cloudera
		// Ambari: config.set("dfs.namenode.kerberos.principal.pattern", "nn/*@" + realm.toUpperCase());
		// Cloudera: config.set("dfs.namenode.kerberos.principal.pattern", "hdfs/*@" + realm.toUpperCase());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static HDFSConfigurationBuilderFromProperties fromXMLPropertiesBuilderUsingDefaultXPathPrefix(final String xpathPrefix) {
		return new HDFSConfigurationBuilderFromProperties(xpathPrefix);
	}
	@RequiredArgsConstructor
	public static class HDFSConfigurationBuilderFromProperties {
		private final String _defaultPropsXPathPrefix;

		public TimeLapse hdfsCredentialRefreshPeriodFrom(final XMLPropertiesForAppComponent props) {
			return this.hdfsCredentialRefreshPeriodFrom(props,null);
		}
		public TimeLapse hdfsCredentialRefreshPeriodFrom(final XMLPropertiesForAppComponent props,final String xpathPrefix) {
			// build xpaths
			String timeLapseXPath = Strings.isNOTNullOrEmpty(xpathPrefix)
											? Path.from(xpathPrefix,"fileStore/hadoop/credentialsRefreshPeriod")
												  .asAbsoluteString()
											: Path.from(_defaultPropsXPathPrefix,"/fileStore/hadoop/credentialsRefreshPeriod")
												  .asAbsoluteString();
			// get data
			TimeLapse timeLapse = props.propertyAt(timeLapseXPath)
								   	   .asTimeLapse();
			return timeLapse;
		}
		public Configuration hdfsConfigurationFor(final XMLPropertiesForAppComponent props) {
			return this.hdfsConfigurationUsing(props,null);
		}
		public Configuration hdfsConfigurationFor(final XMLPropertiesForAppComponent props,final String xpathPrefix) {
			return this.hdfsConfigurationUsing(props,xpathPrefix);
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
		public Configuration hdfsConfigurationUsing(final XMLPropertiesForAppComponent props,final String xpathPrefix) {
			log.info("[HDFS Config loader]: loading HDFS config from properties component={}/{} base xPath={}",
					 props.getAppCode(),props.getAppComponent(),
					 xpathPrefix);
			// build xpaths
			String authModeXPath = Strings.isNOTNullOrEmpty(xpathPrefix)
											? Path.from(xpathPrefix,"fileStore/hadoop/auth_mode")
												  .asAbsoluteString()
											: Path.from(_defaultPropsXPathPrefix,"/fileStore/hadoop/auth_mode")
												  .asAbsoluteString();
			// get data
			HDFSAuthMode authMode = props.propertyAt(authModeXPath)
										 .asEnumElement(HDFSAuthMode.class,HDFSAuthMode.BASIC_AUTH_MODE);

			// build the config
			Configuration config = _hdfsConfigFor(props,
									    		  xpathPrefix);
			// auth
			if (authMode == HDFSAuthMode.BASIC_AUTH_MODE) {
				log.info("\t-HDFS Auth mode: {}",HDFSAuthMode.BASIC_AUTH_MODE);
				_hdfsNoCredentialsAuth(config,
									   props,xpathPrefix);
			} else if (authMode == HDFSAuthMode.KERBEROS_AUTH_MODE) {
				log.info("\t-HDFS Auth mode: {}",HDFSAuthMode.KERBEROS_AUTH_MODE);
				_hdfsKerberosAuth(config,
								  props,xpathPrefix);
			} else {
				throw new IllegalStateException(authMode + " is NOT a supported hdfs auth mode!");
			}

			// return
			return config;
		}
		private Configuration _hdfsConfigFor(final XMLPropertiesForAppComponent props,final String xpathPrefix) {
			// build xpaths
			String hadoopHomeDirPathXPath = Strings.isNOTNullOrEmpty(xpathPrefix)
												? Path.from(xpathPrefix,"fileStore/hadoop/home_dir")
													  .asAbsoluteString()
												: Path.from(_defaultPropsXPathPrefix,"/fileStore/hadoop/home_dir")
													  .asAbsoluteString();
			String configHomePathXPath = Strings.isNOTNullOrEmpty(xpathPrefix)
												? Path.from(xpathPrefix,"fileStore/hadoop/config_home")
													  .asAbsoluteString()
												: Path.from(_defaultPropsXPathPrefix,"/fileStore/hadoop/config_home")
													  .asAbsoluteString();
			// get the data
			Path hdfsHomeDirPath = props.propertyAt(hadoopHomeDirPathXPath)
									  	  .asPath();	// usually for windows
			Path configHomePath = props.propertyAt(configHomePathXPath)
								  	   .asPath();

			// create the config
			return HDFSConfigurationBuilder.hdfsConfigurationFor(hdfsHomeDirPath,configHomePath);
		}
		private void _hdfsNoCredentialsAuth(final Configuration config,
											final XMLPropertiesForAppComponent props,final String xpathPrefix) {
			// build xpaths
			String hdfsUserXPath = Strings.isNOTNullOrEmpty(xpathPrefix)
											? Path.from(xpathPrefix,"fileStore/hadoop/user")
												  .asAbsoluteString()
											: Path.from(_defaultPropsXPathPrefix,"/fileStore/hadoop/user")
												  .asAbsoluteString();
			// get data
			String hdfsUser = props.propertyAt(hdfsUserXPath)
								   .asString("no-user");

			HDFSConfigurationBuilder.hdfsNoCredentialsAuth(config,
														   hdfsUser);
		}
		private void _hdfsKerberosAuth(final Configuration config,
									   final XMLPropertiesForAppComponent props,final String xpathPrefix) throws IOException {
			// build xpaths
			String hdfsUserXPath = Strings.isNOTNullOrEmpty(xpathPrefix)
											? Path.from(xpathPrefix,"fileStore/hadoop/user")
												  .asAbsoluteString()
											: Path.from(_defaultPropsXPathPrefix,"/fileStore/hadoop/user")
												  .asAbsoluteString();
			String realmXPath = Strings.isNOTNullOrEmpty(xpathPrefix)
											? Path.from(xpathPrefix,"fileStore/hadoop/kerberos/realm")
												  .asAbsoluteString()
											: Path.from(_defaultPropsXPathPrefix,"/fileStore/hadoop/kerberos/realmDomain")
												  .asAbsoluteString();
			String domainXPath = Strings.isNOTNullOrEmpty(xpathPrefix)
											? Path.from(xpathPrefix,"fileStore/hadoop/kerberos/domain")
												  .asAbsoluteString()
											: Path.from(_defaultPropsXPathPrefix,"/fileStore/hadoop/kerberos/domain")
												  .asAbsoluteString();
			String ticketPathXPath = Strings.isNOTNullOrEmpty(xpathPrefix)
											? Path.from(xpathPrefix,"fileStore/hadoop/kerberos/ticketPath")
												  .asAbsoluteString()
											: Path.from(_defaultPropsXPathPrefix,"/fileStore/hadoop/kerberos/ticketPath")
												  .asAbsoluteString();
			// get data
			String hdfsUser = props.propertyAt(hdfsUserXPath)
								   .asString();
			String realm = props.propertyAt(realmXPath)
									  .asString();
			String domain = props.propertyAt(domainXPath)
									  .asString();
			Path ticketPath = props.propertyAt(ticketPathXPath)
									  .asPath();

			HDFSConfigurationBuilder.hdfsKerberosAuth(config,
													  hdfsUser,realm,domain,ticketPath);
		}
	//	public static LoginContext login() throws LoginException {
	//		final LoginContext lc = new LoginContext(AB72FileStoreConfigForHDFS.class.getSimpleName(),
	//												 new CallbackHandler() {
	//														@Override
	//														public void handle(final Callback[] callbacks) throws IOException,
	//																											  UnsupportedCallbackException {
	//															for (Callback c : callbacks) {
	//															      if (c instanceof NameCallback)
	//															        ((NameCallback)c).setName("pContenidos");
	//															      if (c instanceof PasswordCallback)
	//															        ((PasswordCallback)c).setPassword("pContenidos".toCharArray());
	//															    }
	//															}
	//												 });
	//
	//		 lc.login();
	//		 return lc;
	//	}
	}
}
