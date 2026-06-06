package io.potatogun.endlessdead.world;

import io.potatogun.endlessdead.entity.InventoryEntity;
import io.potatogun.endlessdead.entity.LivingEntity;
import io.potatogun.endlessdead.entity.container.Container;
import io.potatogun.gdxhelper.world.World;

/**
 * 살아있는 개체와 아이템에 대한 처리가 추가된 월드이다.
 *
 * 내가 만든 gdxhelper 프레임워크 자체에는 아이템이나 살아있는 개체 개념이 없다.
 * 이는 이 Endless Dead 게임만의 기능이다.
 *
 * 그냥 ZombieWorld에서 다 하면 된다고 할 수도 있지만 만약 우리 게임에
 * 살아있는 개체가 있는 월드가 또 생길 수도 있으니 확장성을 고려하여 이렇게 했다.
 *
 * 그럼 gdxhelper에 LivingEntity를 넣으면 안 되냐고 할 수도 있지만
 * LivingEntity의 내용은 범용적이지 못하고 우리 게임만의 구현이라고 생각한다.
 * 누가 미래에 이 프레임워크를 갖다 쓰더라도 자신의 목적에 맞게 직접 다시 구현할 것이다.
 *
 * 즉, 이 추상 클래스가 이 게임 자체의 월드의 직접적인 기반이 된다.
 *
 * @param width   월드 전체 너비
 * @param height  월드 전체 높이
 */
abstract class EndlessDeadWorld(width: Float, height: Float) : World(width, height) {
    override fun update(delta: Float) {
		super.update(delta);  // updateEntities
		removeDead();
    }

    /**
     * isAlive가 false인 객체들을 한꺼번에 제거한다.
     *
     * update()에서 호출 — 상호작용 결과 죽음을 표시한 객체를 정리.
     *
     * 순회 도중 삭제 시 인덱스 꼬임을 막으려고 '먼저 모아 두고 → 한꺼번에 삭제' 패턴.
	 *
	 * update 내에서만 한 번 쓰이기 때문에 inline이다.
     */
    private inline fun removeDead() {
		val toRemove = mutableListOf<LivingEntity>();
        for(entity in getEntities())
            if(entity is LivingEntity && !entity.isAlive)
                toRemove.add(entity);
        for(entity in toRemove)
			removeEntity(entity);
    }

    override fun dispose() {
		// 아이템 정리.
		//   super.dispose는 entities 목록을 비우므로 그 전에 처리해야 한다.
		for(entity in getEntities()) {
			if(entity is InventoryEntity)
				for(item in entity.getInventory())
					item.destroy();
			if(entity is Container)
				entity.containedItem?.destroy();
		}
        super.dispose();
    }
}
