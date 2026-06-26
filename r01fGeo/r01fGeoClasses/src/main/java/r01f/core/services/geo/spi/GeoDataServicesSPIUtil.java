package r01f.core.services.geo.spi;

import java.util.Collection;
import java.util.ServiceLoader;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.geo.GeoDataServiceConfig;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Discovers all notifier impls and return the enabled impl
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class GeoDataServicesSPIUtil {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static GeoDataServiceConfig configFrom(final XMLPropertiesForAppComponent props) {
		log.info("[GeoData service] discovering service impls");

		// Use java's SPI to get the available configs
		final Collection<GeoDataServiceConfig> cfgs = Lists.newArrayList();
		ServiceLoader.load(GeoDataServiceSPIProvider.class)
			 .forEach(// consummer
					  prov -> {
							log.info("\t...found geo data service provided by {}",
									 prov.getClass());
							cfgs.add(prov.provideGeoDataServiceConfig(props));
					  });
		// Get the config for the selected service impl
		GeoDataServiceConfig selectedImplConfig = cfgs.stream()
													  .findFirst().orElse(null);
		if (selectedImplConfig == null) throw new IllegalStateException("Could NOT find [geo data] config (check there exists a [geo data] dependency)!");
		return selectedImplConfig;
	}
}
