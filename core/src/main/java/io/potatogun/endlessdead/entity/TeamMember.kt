package io.potatogun.endlessdead.entity;

/**
 * 특정 팀에 속할 수 있는 개체
 */
interface TeamMember {
	/**
	 * 개체가 속한 그룹 또는 팀 (null: 중립)
	 *
	 * 자바에서는 getTeam, setTeam 사용
	 */
	var team: String?;
}
