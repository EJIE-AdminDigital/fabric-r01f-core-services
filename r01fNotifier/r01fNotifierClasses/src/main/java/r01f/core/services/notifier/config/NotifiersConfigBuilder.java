package r01f.core.services.notifier.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.config.ContainsConfigData;
import r01f.core.services.notifier.spi.NotifierSPIUtil;
import r01f.patterns.FactoryFrom;
import r01f.patterns.IsBuilder;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class NotifiersConfigBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static NotifiersConfigsBuilderMailStep loadNotifiersConfigFrom(final XMLPropertiesForAppComponent props) {

		return new NotifiersConfigBuilder() { /* nothing */ }
						.new NotifiersConfigsBuilderMailStep(props);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class NotifiersConfigsBuilderMailStep {

		@Getter private final XMLPropertiesForAppComponent props;

		public NotifiersConfigsBuilderSMSStep forEMail(final FactoryFrom<XMLPropertiesForAppComponent,ContainsConfigData> cfgFactory) {
			NotifierConfigForEMail forEMail = NotifierSPIUtil.emailNotifierConfigFrom(props,
																					  // creates the app dependent config part
																					  (impl,properties) -> cfgFactory.from(properties));
			return new NotifiersConfigsBuilderSMSStep(props,
													  forEMail);
		}
		public NotifiersConfigsBuilderSMSStep noEMail() {
			return new NotifiersConfigsBuilderSMSStep(props,
													  null);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class NotifiersConfigsBuilderSMSStep {

		@Getter private final XMLPropertiesForAppComponent props;
		@Getter private final NotifierConfigForEMail _forEMail;

		public NotifiersConfigsBuilderVoiceStep forSMS(final FactoryFrom<XMLPropertiesForAppComponent,ContainsConfigData> cfgFactory) {
			NotifierConfigForSMS forSMS = NotifierSPIUtil.smsNotifierConfigFrom(props,
																				// creates the app dependent config part
																				(impl,properties) -> cfgFactory.from(properties));
			return new NotifiersConfigsBuilderVoiceStep(props,
														_forEMail,
														forSMS);
		}
		public NotifiersConfigsBuilderVoiceStep noSMS() {
			return new NotifiersConfigsBuilderVoiceStep(props,
														_forEMail,
														null);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class NotifiersConfigsBuilderVoiceStep {

		@Getter private final XMLPropertiesForAppComponent props;
		@Getter private final NotifierConfigForEMail _forEMail;
		@Getter private final NotifierConfigForSMS _forSMS;

		public NotifiersConfigsBuilderLogStep forVoice(final FactoryFrom<XMLPropertiesForAppComponent,ContainsConfigData> cfgFactory) {
			NotifierConfigForVoice forVoice = NotifierSPIUtil.voiceNotifierConfigFrom(props,
																					  // creates the app dependent config part
																					  (impl,properties) -> cfgFactory.from(properties));
			return new NotifiersConfigsBuilderLogStep(props,
													  _forEMail,
													  _forSMS,
													  forVoice);
		}

		public NotifiersConfigsBuilderLogStep noVoice() {
			return new NotifiersConfigsBuilderLogStep(props,
													  _forEMail,
													  _forSMS,
													  null);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class NotifiersConfigsBuilderLogStep {

		@Getter private final XMLPropertiesForAppComponent props;
		@Getter private final NotifierConfigForEMail _forEMail;
		@Getter private final NotifierConfigForSMS _forSMS;
		@Getter private final NotifierConfigForVoice _forVoice;

		public NotifiersConfigsBuilderPushStep forLog() {
			return new NotifiersConfigsBuilderPushStep(props,
													   _forEMail,
													   _forSMS,
													   _forVoice,
													   new NotifierConfigForLog(props));
		}

		public NotifiersConfigsBuilderPushStep noLog() {
			return new NotifiersConfigsBuilderPushStep(props,
													   _forEMail,
													   _forSMS,
													   _forVoice,
													   null);
		}
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class NotifiersConfigsBuilderPushStep {

		@Getter private final XMLPropertiesForAppComponent _props;
		@Getter private final NotifierConfigForEMail _forEMail;
		@Getter private final NotifierConfigForSMS _forSMS;
		@Getter private final NotifierConfigForVoice _forVoice;
		@Getter private final NotifierConfigForLog _forLog;


		public NotifiersConfigsBuilderBuildStep forPush(final FactoryFrom<XMLPropertiesForAppComponent,ContainsConfigData> cfgFactory) {
			NotifierConfigForPushMessage forPUSH = NotifierSPIUtil.pushMessageNotifierConfigFrom(_props,
																								 // creates the app dependent config part
																								 (impl,properties) -> cfgFactory.from(properties));
			return new NotifiersConfigsBuilderBuildStep(_forEMail,
														_forSMS,
														_forVoice,
														_forLog,
														forPUSH);
		}
		public NotifiersConfigsBuilderBuildStep noPush() {
			return new NotifiersConfigsBuilderBuildStep(_forEMail,
														_forSMS,
														_forVoice,
														_forLog,
														null);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class NotifiersConfigsBuilderBuildStep {

		@Getter private final NotifierConfigForEMail _forEMail;
		@Getter private final NotifierConfigForSMS _forSMS;
		@Getter private final NotifierConfigForVoice _forVoice;
		@Getter private final NotifierConfigForLog _forLog;
		@Getter private final NotifierConfigForPushMessage _forPushMessage;


		public NotifiersConfigs build() {
			// Assemble all notifier configs
			NotifiersConfigs outCfg = new NotifiersConfigs(_forEMail,
														   _forSMS,
														   _forVoice,
														   _forLog,
														   _forPushMessage);
			log.warn("\n Loading Notifier Configs :\n {}",
					 outCfg.debugInfo());
			return outCfg;
		}
	}
}
