package me.BerylliumOranges.bosses.actions;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

public class AttackChargeForwardFar extends BossAction {

	public AttackChargeForwardFar(LivingEntity source) {
		super(source, 500, 100, 20, 0.2);
		this.currentTick = 490;
	}

	@Override
	public void execute(LivingEntity target) {
		source.getWorld().playSound(source.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1.5F, 1F);
	}

	@Override
	public void tick() {
	}
}
