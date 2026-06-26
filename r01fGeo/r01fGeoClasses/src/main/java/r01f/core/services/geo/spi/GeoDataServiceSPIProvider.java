package r01f.core.services.geo.spi;

import r01f.core.services.geo.GeoDataService;
import r01f.core.services.geo.GeoDataServiceConfig;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * spi provider for {geo data services]
 * (see: https://www.baeldung.com/java-spi)
 * BEWARE!!	There MUST exist a file named as the FQN of the spi provider INTERFACE at META-INF folder
 * 			of every concrete implementation
 * 			The content of this file must be the FQN of the spi provider interface IMPLEMENTATION
 * see:
 * 		- Mock: [r01fGeoClasses]/src/main/resources/META-INF/services
 * 		- NORA: [r01fNORAClasses]/src/main/resources/META-INF/services
 */
public interface GeoDataServiceSPIProvider {
	GeoDataService provideGeoDataService(final GeoDataServiceConfig config);
	
	GeoDataServiceConfig provideGeoDataServiceConfig(final XMLPropertiesForAppComponent props);
}