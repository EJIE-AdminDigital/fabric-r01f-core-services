package r01f.core.services.geo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import r01f.locale.Language;
import r01f.locale.LanguageTextsMapBacked;
import r01f.securitycontext.SecurityContext;
import r01f.types.geo.GeoIDs.GeoRegionID;
import r01f.types.geo.GeoIDs.GeoStateID;
import r01f.types.geo.GeoRegion;
import r01f.types.geo.GeoState;

@Singleton
public class GeoDataServiceMockImpl
  implements GeoDataService {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public GeoDataServiceMockImpl() {
		// nothing
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public GeoState loadGeoState(final SecurityContext securityContext,
								 final GeoStateID id) {
		GeoState outState = new GeoState(id);
		String mockOfficialName = "State " + id;
		outState.setNameByLanguage(new LanguageTextsMapBacked()
											.add(Language.ENGLISH,mockOfficialName)
											.add(Language.BASQUE,"[eu] " + mockOfficialName));
		outState.setOfficialName(mockOfficialName);
		return outState;
	}
	@Override
	public GeoRegion loadGeoRegion(final SecurityContext securityContext,
								   final GeoStateID stateId,final GeoRegionID id) {
		GeoRegion outRegion = new GeoRegion(id);
		outRegion.setStateId(stateId);
		String mockOfficialName = "Region " + id;
		outRegion.setNameByLanguage(new LanguageTextsMapBacked()
											.add(Language.ENGLISH,mockOfficialName)
											.add(Language.BASQUE,"[eu] " + mockOfficialName));
		outRegion.setOfficialName(mockOfficialName);
		return outRegion;
	}
}
