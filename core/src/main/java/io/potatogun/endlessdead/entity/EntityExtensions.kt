@file:JvmName("EntityUtils")
package io.potatogun.endlessdead.entity;

import io.potatogun.gdxhelper.entity.Entity;

/**
 * 지정한 대상과 같은 팀인지 확인한다.
 *
 * @param entity 비교 대상
 * @return       같은 팀 여부
 */
inline fun Entity.isSameTeamWith(entity: Entity): Boolean = (this is TeamMember && entity is TeamMember && team != null && entity.team != null && team == entity.team);
