package goorm.eagle7.stelligence.domain.member.model;

import lombok.Getter;

@Getter
public enum Role {
	ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

	private final String value;

	Role(String value) {
		this.value = value;
	}

	public static Role getRoleFromString(String roleStr) {
		try {
			return Role.valueOf(roleStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			return Role.USER;
		}
	}
}
