package r01f.core.services.geo.spi;

import r01f.core.services.geo.GeoDataServiceMockImpl;
import r01f.core.services.geo.GeoDataService;
import r01f.core.services.geo.GeoDataServiceConfig;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * SPI provider for AWS SNS based SMS notifier
 */
public class SPIProviderForGeoDataServiceMockImpl
  implements GeoDataServiceSPIProvider {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public GeoDataService provideGeoDataService(final GeoDataServiceConfig config) {
		return new GeoDataServiceMockImpl();
	}
	@Override
	public GeoDataServiceConfig provideGeoDataServiceConfig(final XMLPropertiesForAppComponent props) {
		return new GeoDataServiceConfig() {
					// nothing
			   };
	}
}
