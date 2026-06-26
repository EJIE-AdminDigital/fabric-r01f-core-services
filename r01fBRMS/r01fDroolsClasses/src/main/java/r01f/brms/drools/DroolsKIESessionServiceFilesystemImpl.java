package r01f.brms.drools;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DroolsKIESessionServiceFilesystemImpl
 	 extends DroolsKIESessionServiceBase
  implements DroolsKIESessionService {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public DroolsKIESessionServiceFilesystemImpl (final DroolsKIEConfig kieConfig) {		
		super(kieConfig);
	}	
	public DroolsKIESessionServiceFilesystemImpl (final DroolsKIEConfig kieConfig,
												  final KieServices kieServices) {		
		super(kieConfig,kieServices);
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public KieModule buildKieModule() {
		//1. New KIE File System.
		KieFileSystem kieFileSystem = _kieServices.newKieFileSystem();
		//2. Initialize the rules.
		_kieConfig.getApiData()
				  .getRuleSet().forEach( r -> {
											log.warn("Addding knowledge rule.... {} ", r.asRelativeString());									   
											kieFileSystem.write(ResourceFactory.newClassPathResource(r.asRelativeString()));
										});
	  
		//3. Build the KIE Modules.
		KieBuilder kb = _kieServices.newKieBuilder(kieFileSystem);
		kb.buildAll();
		KieModule kieModule = kb.getKieModule();
		return kieModule;
	}
	@Override
	public KieContainer buildKieContainer(final KieModule module) {
		return _kieServices.newKieContainer(module.getReleaseId());
	}
}
