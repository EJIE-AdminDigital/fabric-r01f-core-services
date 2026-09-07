package r01.kv.transform.namestrategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01.api.filestore.model.oids.S3KEYs.IsS3Key;
import r01.kv.transform.namestrategy.KVNameStrategies.KVNameStrategyWrapper.KVNameStrategyForKeyType;
import r01f.mime.MimeType;
import r01f.reflection.ReflectionUtils;

@Accessors(prefix="_")
public enum KVNameStrategies {
	Default (KVNameStrategyDefault.class),
	ExtensionForFileIncluded(KVNameStrategyExtensionBased.class);

	@Getter private final KVNameStrategyForKeyType _strategy;

	KVNameStrategies(final Class<? extends  KVNameStrategy > impl) {
		_strategy= new KVNameStrategyWrapper().create(impl);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static class KVNameStrategyWrapper {
		public KVNameStrategyForKeyType create(final Class<? extends  KVNameStrategy > impl) {
			return new KVNameStrategyWrapper() {//
												   }.new KVNameStrategyForKeyType(impl);
		}
	    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
		public class KVNameStrategyForKeyType {
	        private final  Class<?> _impl;
			public KVNameStrategyForMimeType forKeyType(final Class<? extends IsS3Key> oidKeyType) {
				return new KVNameStrategyForMimeType(_impl,oidKeyType);
			}
		}
		@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
		public class KVNameStrategyForMimeType {
			private final  Class<?> _impl;
			private final  Class<?> _modelObjectKeyType;

			public KVNameStrategyImpl withMimeType(final MimeType mime) {
				return new KVNameStrategyImpl(_impl,_modelObjectKeyType,mime);
			}
		}
	    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
		public class KVNameStrategyImpl {
			private final  Class<?> _impl;
			private final  Class<?> _modelObjectKeyType;
			private final  MimeType _mimeType;

			@SuppressWarnings("rawtypes")
			public KVNameStrategy build() {
				Class[] constructorArgsTypes = {Class.class ,MimeType.class};
				Object[] args =  {_modelObjectKeyType,_mimeType};
				KVNameStrategy strategy =
						ReflectionUtils.createInstanceOf(_impl, constructorArgsTypes,args);
	            return strategy;
			}
		}
	}
}


