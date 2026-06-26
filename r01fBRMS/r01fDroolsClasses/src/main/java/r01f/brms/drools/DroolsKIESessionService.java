package r01f.brms.drools;

import org.kie.api.runtime.KieSession;

import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface DroolsKIESessionService {
	
	 public KieSession newKieSession();
}
