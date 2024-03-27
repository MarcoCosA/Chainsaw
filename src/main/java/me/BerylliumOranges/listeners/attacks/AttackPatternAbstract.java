package me.BerylliumOranges.listeners.attacks;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

import me.BerylliumOranges.main.PluginMain;

public abstract class AttackPatternAbstract implements Listener {
	protected LivingEntity entity;
	protected LivingEntity target;
	protected int attackIndex = 0;
	protected int ticks = 0;

	public AttackPatternAbstract(LivingEntity entity, LivingEntity target) {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
		this.entity = entity;
		this.target = target;
	}

	public abstract void tick();

	public LivingEntity getEntity() {
		return entity;
	}

	public void setEntity(LivingEntity entity) {
		this.entity = entity;
	}

	public LivingEntity getTarget() {
		return target;
	}

	public void setTarget(LivingEntity target) {
		this.target = target;
	}

	public int getTicks() {
		return ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public int getAttackIndex() {
		return attackIndex;
	}

	public void setAttackIndex(int attackIndex) {
		this.attackIndex = attackIndex;
	}
}
