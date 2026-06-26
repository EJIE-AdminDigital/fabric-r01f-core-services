package r01f.core.services.notifier.properties;

public interface NotifierImplDependentProperties {
	
	@SuppressWarnings("unchecked")
	default <N extends NotifierImplDependentProperties> N as(final Class<N> notifier) {
		return (N)this;
	}
	
	public static interface NotifierImplRecordProperties {
		@SuppressWarnings("unchecked")
		default <N extends NotifierImplRecordProperties> N as(final Class<N> notifier) {
			return (N)this;
		}
	}
	
}
