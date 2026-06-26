package r01f.cloud.aws.sns.model;

import java.text.DecimalFormat;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AWSSNSSmsMessageAttributesBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static AWSSNSSmsMessageAttributesBuilderMaxPriceStep forSender(final AWSSNSSmsSenderID senderId) {
		return new AWSSNSSmsMessageAttributesBuilder() { /* nothing */ }
						.new AWSSNSSmsMessageAttributesBuilderMaxPriceStep(senderId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public class AWSSNSSmsMessageAttributesBuilderMaxPriceStep {
		private final AWSSNSSmsSenderID _senderId;

		public AWSSNSSmsMessageAttributesBuilderTypeStep maxPrice(final float maxPrice) {
			return new AWSSNSSmsMessageAttributesBuilderTypeStep(_senderId,
																 maxPrice);
		}
		public AWSSNSSmsMessageAttributesBuilderTypeStep noMaxPrice() {
			return new AWSSNSSmsMessageAttributesBuilderTypeStep(_senderId,
																 0f);
		}
	}
	@RequiredArgsConstructor
	public class AWSSNSSmsMessageAttributesBuilderTypeStep {
		private final AWSSNSSmsSenderID _senderId;
		private final float _maxPrice;

		public AWSSNSSmsMessageAttributesBuilderBuildStep usingSmsOfType(final AWSSNSSmsType type) {
			return new AWSSNSSmsMessageAttributesBuilderBuildStep(_senderId,
																  _maxPrice,
																  type);
		}
		public AWSSNSSmsMessageAttributesBuilderBuildStep promotionalNonCritical() {
			return this.usingSmsOfType(AWSSNSSmsType.PROMOTIONAL_NON_CRITICAL);
		}
		public AWSSNSSmsMessageAttributesBuilderBuildStep transactionalCritical() {
			return this.usingSmsOfType(AWSSNSSmsType.TRANSACTIONAL_CRITICAL);
		}
	}
	@RequiredArgsConstructor
	public class AWSSNSSmsMessageAttributesBuilderBuildStep {
		private final AWSSNSSmsSenderID _senderId;
		private final float _maxPrice;
		private final AWSSNSSmsType _smsType;

		public Map<String,MessageAttributeValue> build() {
			Map<String,MessageAttributeValue> outMap = Maps.newHashMap();
			if (_senderId != null) outMap.put("AWS.SNS.SMS.SenderID",MessageAttributeValue.builder()
																			.stringValue(_senderId.asString()) 		// The sender ID shown on the device.
																	        .dataType("String")
																	        .build());
			if (_maxPrice > 0f)    outMap.put("AWS.SNS.SMS.MaxPrice",MessageAttributeValue.builder()
																			.stringValue(new DecimalFormat("#.##")
																								.format(_maxPrice)) // the max price
																	        .dataType("Number")
																	        .build());
			if (_smsType != null)	outMap.put("AWS.SNS.SMS.SMSType",MessageAttributeValue.builder()
																			.stringValue(_smsType.getCode()) 		// the max price
																	        .dataType("String")
																	        .build());
			return outMap;
		}
	}
}
