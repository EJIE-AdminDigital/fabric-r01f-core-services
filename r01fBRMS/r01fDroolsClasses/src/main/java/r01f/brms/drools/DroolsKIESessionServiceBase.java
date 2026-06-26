package r01f.brms.drools;

import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import lombok.experimental.Accessors;


@Accessors(prefix="_")
public abstract class DroolsKIESessionServiceBase
  		   implements  DroolsKIESessionService {	
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
    protected final KieServices _kieServices;
    protected final DroolsKIEConfig _kieConfig;
    protected KieModule _kieModule;
    protected KieContainer _kieContainer;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////    
	public DroolsKIESessionServiceBase (final DroolsKIEConfig kieConfig) {		
		_kieServices = KieServices.Factory.get();
		_kieConfig = kieConfig;
		_init();
	}	
	public DroolsKIESessionServiceBase (final DroolsKIEConfig kieConfig,
			                            final KieServices kieServices) {		
		_kieServices = kieServices;
		_kieConfig = kieConfig;
		_init();
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////	
	public abstract  KieModule buildKieModule();
	public abstract  KieContainer buildKieContainer(final KieModule module);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public KieSession newKieSession() {
        return _kieContainer.newKieSession();
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
    protected void getKieRepository() {
        KieRepository kieRepository = _kieServices.getRepository();
        kieRepository.addKieModule(new KieModule() {
				                        @Override
										public ReleaseId getReleaseId() {
				                        			return kieRepository.getDefaultReleaseId();
				                        }
        						   });
	 }
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////    
    private void _init() {
    	_kieModule = buildKieModule();
    	_kieContainer = buildKieContainer(_kieModule);
    }
}
