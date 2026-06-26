package r01f.core.services.geo;

import r01f.securitycontext.SecurityContext;
import r01f.types.geo.GeoIDs.GeoRegionID;
import r01f.types.geo.GeoIDs.GeoStateID;
import r01f.types.geo.GeoRegion;
import r01f.types.geo.GeoState;

public interface GeoDataService {
	/**
	 * Loads a {@link GeoState}
	 * @param securityContext
	 * @param id
	 * @return
	 */
	public GeoState loadGeoState(final SecurityContext securityContext,
								 final GeoStateID id);
	/**
	 * Loads a {@link GeoRegion}
	 * @param securityContext
	 * @param stateId
	 * @param id
	 * @return
	 */
	public GeoRegion loadGeoRegion(final SecurityContext securityContext,
								   final GeoStateID stateId,final GeoRegionID id);
}
