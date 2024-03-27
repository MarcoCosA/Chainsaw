package me.BerylliumOranges.listeners.attacks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;

public abstract class BossAttack {
	protected List<LivingEntity> entities = new ArrayList<>();
	int maxTicksUntilAttack;
	double attackFrequencyModifier;

	public BossAttack(int maxTicksUntilAttack, double attackFrequencyModifier) {
		this.maxTicksUntilAttack = maxTicksUntilAttack;
		this.attackFrequencyModifier = attackFrequencyModifier;
	}

	public boolean removeEntity(LivingEntity entity) {
		return entities.remove(entity);
	}

	public void addEntity(LivingEntity entity) {
		entities.add(entity);
	}
	

	public abstract void execute();
}
