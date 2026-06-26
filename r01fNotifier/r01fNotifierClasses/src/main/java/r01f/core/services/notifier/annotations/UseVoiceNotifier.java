package r01f.core.services.notifier.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

/**
 * Annotation that tells guice to inject the the voice-based notifier services
 * It's usually used at event listeners
 */
@Qualifier // @BindingAnnotation
@Target({ ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseVoiceNotifier {
	// nothing
}
