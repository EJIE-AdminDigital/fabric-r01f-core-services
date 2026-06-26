package r01f.cloud.aws.sns.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum AWSSNSSmsType {
	PROMOTIONAL_NON_CRITICAL("Promotional"),
	TRANSACTIONAL_CRITICAL("Transactional");

	@Getter private final String _code;
}
