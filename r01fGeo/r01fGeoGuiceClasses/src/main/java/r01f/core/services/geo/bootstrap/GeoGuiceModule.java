package r01f.core.services.geo.bootstrap;

import java.util.Iterator;
import java.util.ServiceLoader;

import jakarta.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import r01f.core.services.geo.GeoDataService;
import r01f.core.services.geo.GeoDataServiceConfig;
import r01f.core.services.geo.spi.GeoDataServiceSPIProvider;


@Slf4j
public class GeoGuiceModule
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final GeoDataServiceConfig _geoServiceConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoGuiceModule(final GeoDataServiceConfig geoServiceConfig) {
		_geoServiceConfig = geoServiceConfig;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BINDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// nothing
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GEO SERVICE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Provides a {@link GeoDataService} implementation
	 * @param props
	 * @return
	 */
	@Provides @Singleton	// creates a single instance of the java mail sender
	GeoDataService _provideGeoDataServices() {
		log.info("[GeoDataService]: SPI finding {} implementations",
				  GeoDataService.class);
		// BEWARE! there MUST exists a file named as the spi provider interface FQN at the META-INF folder
		//		   of every implementation project
		GeoDataService outSrvc = null;
		for (Iterator<GeoDataServiceSPIProvider> pIt = ServiceLoader.load(GeoDataServiceSPIProvider.class).iterator(); pIt.hasNext(); ) {
			GeoDataServiceSPIProvider prov = pIt.next();

			outSrvc = prov.provideGeoDataService(_geoServiceConfig);
		}
		if (outSrvc == null) throw new IllegalStateException("Could NOT find any SMS notifier implementation!");
		return outSrvc;
	}
}
