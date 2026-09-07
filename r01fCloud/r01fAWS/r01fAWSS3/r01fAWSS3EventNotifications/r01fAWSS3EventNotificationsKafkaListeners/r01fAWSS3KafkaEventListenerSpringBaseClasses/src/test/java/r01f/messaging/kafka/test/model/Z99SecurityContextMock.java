package r01f.messaging.kafka.test.model;

import java.time.Instant;

import com.google.common.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.OID;
import r01f.patterns.FactoryFrom;
import r01f.securitycontext.SecurityContext;
import r01f.securitycontext.SecurityContextAuthenticatedActor;
import r01f.securitycontext.SecurityContextForApiKey;
import r01f.securitycontext.SecurityContextForApp;
import r01f.securitycontext.SecurityContextForRegisteredDevice;
import r01f.securitycontext.SecurityContextForUser;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.securitycontext.SecurityIDS.SecurityProviderID;
import r01f.securitycontext.SecurityIDS.SecurityToken;
import r01f.securitycontext.SecurityIDS.UserRole;
import r01f.types.url.Url; // Adjust packages to match your exact core layout

@Slf4j
public class Z99SecurityContextMock 
  implements SecurityContext {

    private static final long serialVersionUID = 8091359592526754123L;

    private boolean _valid = true;

    @SuppressWarnings("unchecked")
    @Override
    public <CTX extends SecurityContext> CTX as(final Class<CTX> type) {
        if (type != null && type.isInstance(this)) {
            return (CTX) this;
        }
        log.warn("[SecurityContextMock] Cannot cast mock context type to '{}'", type != null ? type.getName() : "null");
        return null;
    }

    @Override
    public <A extends SecurityContextAuthenticatedActor> A getAuthenticatedActorAs(final Class<A> actorType) {
        log.debug("[SecurityContextMock] Mocking authenticated actor representation for class: {}", actorType.getSimpleName());
        return null; // Override in a sub-mock if concrete test assertions need actor fields
    }

    @Override
    public <A extends SecurityContextAuthenticatedActor> A getAuthenticatedActorAs(final TypeToken<A> actorType) {
        return null;
    }

    @Override
    public SecurityProviderID getSecurityProviderId() {
        return SecurityProviderID.forId("mock_security_provider");
    }

    @Override
    public LoginID getLoginId() {
        return LoginID.forId("mock_login_id");
    }

    @Override
    public Instant getCreateDate() {
        return Instant.now();
    }

    @Override
    public SecurityToken getSecurityToken() {
        return SecurityToken.forId("mock_synthetic_security_token_payload");
    }

    @Override
    public Url getLoginUrl() {
        return Url.from("http://localhost/security/mock/login");
    }

    @Override
    public Url getLogoutUrl() {
        return Url.from("http://localhost/security/mock/logout");
    }

    @Override
    public boolean isValid() {
        return _valid;
    }

    @Override
    public void invalidate() {
        log.info("[SecurityContextMock] Invalidating testing context session.");
        _valid = false;
    }

    @Override
    public boolean userHasSystemWideRoleIn(final AppComponent appComp, final UserRole role) {
        // Grant all permissions by default for standard testing comfort
        return true; 
    }

    @Override
    public boolean userHasAnySystemWideRoleIn(final AppComponent module) {
        return true;
    }

    @Override
    public UserRole getUserSystemWideRoleIn(final AppComponent module) {
        return UserRole.forId("MOCK_ADMIN");
    }

    @Override
    public boolean isForUser() {
        return false;
    }

    @Override
    public SecurityContextForUser asForUser() {
        return null;
    }

    @Override
    public boolean isForApiKey() {
        return false;
    }

    @Override
    public SecurityContextForApiKey asForApiKey() {
        return null;
    }

    @Override
    public boolean isForApp() {
        return false;
    }

    @Override
    public SecurityContextForApp asForApp() {
        return null;
    }

    @Override
    public boolean isForRegisteredDevice() {
        return false;
    }

    @Override
    public <O extends OID> SecurityContextForRegisteredDevice<O> asForRegisteredDevice(
            final FactoryFrom<LoginID, O> deviceOidFactory) {
        return null;
    }

    @Override
    public boolean isForSystem() {
        // Matches your original true flag baseline configuration
        return true; 
    }
}