package r01f.cloud.aws.s3.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.cloud.aws.s3.AWSS3ProgressListener;
import r01f.patterns.IsBuilder;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AWSS3OperationsSettingsBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static AWSS3OperationsSettingsBuilderTransferSetterStep create() {
		AWSS3OperationSettings transfer = new AWSS3OperationSettings();
		return new AWSS3OperationsSettingsBuilder() { /* nothing */ }
					.new AWSS3OperationsSettingsBuilderTransferSetterStep(transfer);
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3OperationsSettingsBuilderLastStep {
		private final AWSS3OperationSettings _uploadConfig;

		public AWSS3OperationSettings build() {
			return _uploadConfig;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3OperationsSettingsBuilderTransferSetterStep {
		private final AWSS3OperationSettings _uploadConfig;
		public AWSS3OperationsSettingsBuilderWaitStep usingTransferSettings(final AWSS3TransferSettings tranferConfig) {
			;
			_uploadConfig.setTranferConfig(tranferConfig);
			return new AWSS3OperationsSettingsBuilderWaitStep(_uploadConfig);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3OperationsSettingsBuilderWaitStep {
		private final AWSS3OperationSettings _uploadConfig;
		public AWSS3OperationsSettingsBuilderProgressListenerStep waitUntilFinish() {
			_uploadConfig.setWait(true);
			return new AWSS3OperationsSettingsBuilderProgressListenerStep(_uploadConfig);
		}
		public AWSS3OperationsSettingsBuilderProgressListenerStep nowait() {
			_uploadConfig.setWait(false);
			return new AWSS3OperationsSettingsBuilderProgressListenerStep(_uploadConfig);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3OperationsSettingsBuilderProgressListenerStep {
		private final AWSS3OperationSettings _uploadConfig;

		public AWSS3OperationsSettingsBuilderLastStep usingProgressListener(final AWSS3ProgressListener progressListener) {
			_uploadConfig.setProgressListener(progressListener);
			return new AWSS3OperationsSettingsBuilderLastStep(_uploadConfig);
		}
	}
}
