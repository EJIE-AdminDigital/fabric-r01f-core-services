package r01f.core.services.mail.model;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class EMailMessageBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    public static final EMaiMessagelBuilderToStep from(final EMailRFC822Address from) {
        return new EMailMessageBuilder() { /* nothing */ }
        				.new EMaiMessagelBuilderToStep(from);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class EMaiMessagelBuilderToStep {
        private final EMailRFC822Address _from;

        public EMailMessageBuilderCCStep noCC() {
        	return new EMailMessageBuilderCCStep(_from,
        										 null);
        }
        public EMailMessageBuilderCCStep to(final EMailRFC822Address... to) {
        	return new EMailMessageBuilderCCStep(_from,
        										 Lists.newArrayList(to));
        }
        public EMailMessageBuilderCCStep to(final Collection<EMailRFC822Address> to) {
        	return new EMailMessageBuilderCCStep(_from,
        										 to);
        }
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class EMailMessageBuilderCCStep {
        private final EMailRFC822Address _from;
        private final Collection<EMailRFC822Address> _to;

        public EMailMessageBuilderBCCStep noCC() {
        	return new EMailMessageBuilderBCCStep(_from,_to,
        										  null);
        }
        public EMailMessageBuilderBCCStep cc(final EMailRFC822Address... cc) {
        	return new EMailMessageBuilderBCCStep(_from,_to,
        										  Lists.newArrayList(cc));
        }
        public EMailMessageBuilderBCCStep cc(final Collection<EMailRFC822Address> cc) {
        	return new EMailMessageBuilderBCCStep(_from,_to,
        										  cc);
        }
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class EMailMessageBuilderBCCStep {
        private final EMailRFC822Address _from;
        private final Collection<EMailRFC822Address> _to;
        private final Collection<EMailRFC822Address> _cc;

        public EMailMessageBuilderSubjectStep noBCC() {
        	return new EMailMessageBuilderSubjectStep(_from,_to,
        										 	  _cc,null);
        }
        public EMailMessageBuilderSubjectStep bcc(final EMailRFC822Address... bcc) {
        	return new EMailMessageBuilderSubjectStep(_from,_to,
        										      _cc,Lists.newArrayList(bcc));
        }
        public EMailMessageBuilderSubjectStep bcc(final Collection<EMailRFC822Address> bcc) {
        	return new EMailMessageBuilderSubjectStep(_from,_to,
        										 	  _cc,bcc);
        }
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class EMailMessageBuilderSubjectStep {
        private final EMailRFC822Address _from;
        private final Collection<EMailRFC822Address> _to;
        private final Collection<EMailRFC822Address> _cc;
        private final Collection<EMailRFC822Address> _bcc;

        public EMailMessageBuilderBodyStep withSubject(final String subject) {
        	return new EMailMessageBuilderBodyStep(_from,_to,
        										   _cc,_bcc,
										           subject);
        }
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class EMailMessageBuilderBodyStep {
        private final EMailRFC822Address _from;
        private final Collection<EMailRFC822Address> _to;
        private final Collection<EMailRFC822Address> _cc;
        private final Collection<EMailRFC822Address> _bcc;
        private final String _subject;

        public EMailMessageBuilderBuildStep withPlainTextBody(final String plainTextBody) {
        	return new EMailMessageBuilderBuildStep(_from,_to,
    												_cc,_bcc,
    												_subject,
    												plainTextBody,null);
        }
        public EMailMessageBuilderBuildStep withPlainTextAndHTMLBody(final String plainTextBody,final String htmlBody) {
        	return new EMailMessageBuilderBuildStep(_from,_to,
        											_cc,_bcc,
        											_subject,
        											plainTextBody,htmlBody);
        }
    }
    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    public class EMailMessageBuilderBuildStep {
        private final EMailRFC822Address _from;
        private final Collection<EMailRFC822Address> _to;
        private final Collection<EMailRFC822Address> _cc;
        private final Collection<EMailRFC822Address> _bcc;
        private final String _subject;
        private final String _plaintTextBody;
        private final String _htmlBody;

        public EMailMessage build() {
        	return new EMailMessage(_from,_to,
		        					_cc,_bcc,
		        					_subject,
		        					_plaintTextBody,_htmlBody);
        }
    }
}
