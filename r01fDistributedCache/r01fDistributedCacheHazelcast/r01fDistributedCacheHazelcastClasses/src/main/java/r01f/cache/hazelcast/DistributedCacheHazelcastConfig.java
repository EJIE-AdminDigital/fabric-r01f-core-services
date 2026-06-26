package r01f.cache.hazelcast;

import java.nio.charset.Charset;

import org.w3c.dom.Node;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryXmlConfig;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cache.DistributedCacheConfig;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.xml.XMLStringSerializer;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * <pre class='brush:java>
 * 		DistributedCacheConfig cfg = DistributedCacheHazelcastConfig.createFrom(props);
 * </pre>
 */
@Slf4j
@Accessors(prefix="_")
public class DistributedCacheHazelcastConfig
  implements DistributedCacheConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AppCode _appCode;
	@Getter private final AppComponent _appComponent;
	@Getter private final Config _HZConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//	HAZELCAST CONFIG FILE: contains client and server data.
//	In order to complain with R01FB architecture this file will be located at server core classes
//	but read from client (f.e some api classes)  shared between ALL internal client projects of payment gateway -
//
//		http://docs.hazelcast.org/docs/3.5/manual/html/configuringhazelcast.html
//		http://docs.hazelcast.org/docs/3.4/manual/html/configurationoverview.html
/////////////////////////////////////////////////////////////////////////////////////////
	public DistributedCacheHazelcastConfig(final AppCode appCode,final AppComponent appComponent,
								           final Config hzConfig) {
		_appCode = appCode;
		_appComponent = appComponent;
		_HZConfig = hzConfig;
	}
	public DistributedCacheHazelcastConfig(final AppCode appCode,final AppComponent appComponent,
								           final String hzXmlConfig) {
		this(appCode,appComponent,
			 new InMemoryXmlConfig(hzXmlConfig));
	}
	public static DistributedCacheHazelcastConfig createFrom(final AppCode appCode,final AppComponent appComponent,
								           					 final Config hzConfig) {
		return new DistributedCacheHazelcastConfig(appCode,appComponent,
												   hzConfig);
	}
	public static DistributedCacheHazelcastConfig createFrom(final XMLPropertiesForAppComponent cacheProps) {
		return DistributedCacheHazelcastConfig.createFrom(cacheProps,
										  		 		  "hazelcast");
	}
	public static DistributedCacheHazelcastConfig createFrom(final XMLPropertiesForAppComponent cacheProps,
												             final String propsXPath) {
		log.warn("[DistributedCache (hazelcast)]: loading config of app/component={}/{}",
				 cacheProps.getAppCode(),cacheProps.getAppComponent());

		String outHZXmlConfig = null;
		Node  hzConfigXMLNode  = cacheProps.propertyAt("/*").node();
		if (hzConfigXMLNode != null) {
			outHZXmlConfig = XMLStringSerializer.writeNodeNoFail(hzConfigXMLNode,
													       		 Charset.defaultCharset());
		} else {		
			 log.error("The hazelcast config file is NOT valid!");
		}
		return new DistributedCacheHazelcastConfig(cacheProps.getAppCode(),cacheProps.getAppComponent(),
										           outHZXmlConfig);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <C extends DistributedCacheConfig> C as(final Class<C> type) {
		return (C) this;
	}	
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(_HZConfig.toString())	;	
		return sb;
	}
}
