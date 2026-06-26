package r01f.cloud.aws.s3.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;


@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AWSS3TransferSettingsBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static AWSS3TransferSettingsBuilderFieldSetterStep create() {
		AWSS3TransferSettings transfer = new AWSS3TransferSettings();
		return new AWSS3TransferSettingsBuilder() { /* nothing */ }
					.new AWSS3TransferSettingsBuilderFieldSetterStep(transfer);
	}
	public static AWSS3TransferSettingsBuilderFieldSetterStep createWithDefaultValues() {
		AWSS3TransferSettings transfer = new AWSS3TransferSettings();
		transfer.setMinimumUploadPartSize(AWSS3TransferSettings.DEFAULT_MINIMUM_UPLOAD_PART_SIZE);
		transfer.setMultipartUploadThreshold(AWSS3TransferSettings.DEFAULT_MULTIPART_UPLOAD_THRESHOLD);
		transfer.setMultipartCopyPartSize(AWSS3TransferSettings.DEFAULT_MINIMUM_UPLOAD_PART_SIZE);
		transfer.setMultipartCopyThreshold(AWSS3TransferSettings.DEFAULT_MULTIPART_COPY_THRESHOLD);
		transfer.setDisableParallelDownloads(AWSS3TransferSettings.DISABLE_PARALLEL_DOWNLOADS);
		transfer.setShutDownThreadPools(AWSS3TransferSettings.SHUT_DOWN_THEAD_POOLS);
		transfer.setThreadPoolSize(AWSS3TransferSettings.DEFAULT_THREAD_POOL_SIZE);

		return new AWSS3TransferSettingsBuilder() { /* nothing */ }
					.new AWSS3TransferSettingsBuilderFieldSetterStep(transfer);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3TransferSettingsBuilderLastStep {
		private final AWSS3TransferSettings _transfer;

		public AWSS3TransferSettings build() {
			return _transfer;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AWSS3TransferSettingsBuilderFieldSetterStep {
		private final AWSS3TransferSettings _transfer;
		public AWSS3TransferSettings build() {
			return new AWSS3TransferSettingsBuilderLastStep(_transfer).build();
		}
		public AWSS3TransferSettingsBuilderFieldSetterStep withMinimumUploadPartSize(final long value) {
			_transfer.setMinimumUploadPartSize(value);
			return this;
		}
		public AWSS3TransferSettingsBuilderFieldSetterStep withMultipartUploadThreshold(final long value) {
			_transfer.setMultipartUploadThreshold(value);
			return this;
		}
		public AWSS3TransferSettingsBuilderFieldSetterStep withMultipartCopyPartSize(final long value) {
			_transfer.setMultipartCopyPartSize(value);
			return this;
		}
		public AWSS3TransferSettingsBuilderFieldSetterStep withMultipartCopyThreshold(final long value) {
			_transfer.setMultipartCopyThreshold(value);
			return this;
		}
		public AWSS3TransferSettingsBuilderFieldSetterStep disablingParallelDownloads() {
			_transfer.setDisableParallelDownloads(true);
			return this;

		}
		public AWSS3TransferSettingsBuilderFieldSetterStep enablingParallelDownloads() {
			_transfer.setDisableParallelDownloads(false);
			return this;
		}
		public AWSS3TransferSettingsBuilderFieldSetterStep shutingDownThreadPools() {
			_transfer.setShutDownThreadPools(true);
			return this;
		}
		public AWSS3TransferSettingsBuilderFieldSetterStep NotShutingDownThreadPools() {
			_transfer.setShutDownThreadPools(false);
			return this;
		}
		public AWSS3TransferSettingsBuilderFieldSetterStep withThreadPoolSize(int thSize) {
			_transfer.setThreadPoolSize(thSize);
			return this;
		}
	}
}
