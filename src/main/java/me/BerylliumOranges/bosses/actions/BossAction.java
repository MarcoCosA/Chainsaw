package me.BerylliumOranges.bosses.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.main.PluginMain;

public abstract class BossAction implements Listener {

	protected LivingEntity source;
	protected HashMap<LivingEntity, Integer> targetsOnCooldown = new HashMap<>();
	public ArrayList<LivingEntity> summonedEntities = new ArrayList<>();

	protected int animationDuration = 5;
	protected int maxTicksUntilAttack;
	protected int currentTick;
	protected int attackRange;
	protected double attackFrequencyModifier;
	protected boolean tryAgainOnMiss = true;
	protected boolean playAnimation = false;
	protected int currentAnimationTick = 0;

	protected int hitCooldown = 8; // 8 ticks
	protected double damage;
	protected double knockback;

	protected BossAction(LivingEntity source, int maxTicksUntilAttack, int attackRange, double damage, double knockback) {
		this.source = source;
		this.maxTicksUntilAttack = maxTicksUntilAttack;
		this.currentTick = maxTicksUntilAttack / 3; // Assuming you still want to initialize it relative to maxTicks
		this.attackRange = attackRange;
		this.attackFrequencyModifier = 1; // Assuming default modifier
		this.damage = damage;
		this.knockback = knockback;

		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	public void tick() {
		// Logic to tick the attack. This method should be implemented as needed.
	}

	public List<LivingEntity> selectTargets() {
		List<LivingEntity> targets = new ArrayList<>();
		LivingEntity p = BossUtils.getNearestEnemy(source, attackRange);

		if (p != null && source.hasLineOfSight(p))
			targets.add(p);

		return targets;
	}

	public abstract void execute(LivingEntity target);

	public void applyDamage(LivingEntity target, Location loc) {
		if (!targetsOnCooldown.containsKey(target) && !target.equals(source)) {
			targetsOnCooldown.put(target, hitCooldown);
			target.damage(damage, source);

			Vector dir = loc.subtract(target.getLocation()).toVector().normalize();
			dir.setY(dir.getY() - 0.1);
			target.setVelocity(target.getVelocity().clone().add(dir).multiply(-knockback));

		}
	}

	public void applyDamage(LivingEntity target) {
		applyDamage(target, source.getLocation());
	}

	@EventHandler
	public void onTick(TickEvent e) {
		tick();
		currentTick++;
		if (!source.isDead()) {
			if (currentTick + animationDuration >= maxTicksUntilAttack * (1.0 / attackFrequencyModifier)) {
				playAnimation = true;
				currentAnimationTick++;
			}
			if (currentTick >= maxTicksUntilAttack * (1.0 / attackFrequencyModifier)) {
				playAnimation = false;
				currentAnimationTick = 0;
				// Assuming you'll implement a method to select a single target
				List<LivingEntity> targets = selectTargets(); // You'll need to define 'source' context or pass it as a parameter
				if (!targets.isEmpty()) {
					for (LivingEntity target : targets) {
						execute(target);
					}
					currentTick = 0;
				} else if (tryAgainOnMiss)
					currentTick = maxTicksUntilAttack - animationDuration * 2;
				else
					currentTick = 0;
			}
		}

		// This decrements targetsOnCooldown and removes them if cooldown is 0
		Iterator<Entry<LivingEntity, Integer>> iterator = targetsOnCooldown.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<LivingEntity, Integer> entry = iterator.next();
			int newValue = entry.getValue() - 1;
			if (newValue <= 0) {
				iterator.remove();
			} else {
				entry.setValue(newValue);
			}
		}
	}

	public int getMaxTicksUntilAttack() {
		return maxTicksUntilAttack;
	}

	public void setMaxTicksUntilAttack(int maxTicksUntilAttack) {
		this.maxTicksUntilAttack = maxTicksUntilAttack;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}

	public double getAttackFrequencyModifier() {
		return attackFrequencyModifier;
	}

	public void setAttackFrequencyModifier(double attackFrequencyModifier) {
		this.attackFrequencyModifier = attackFrequencyModifier;
	}

	public boolean isTryAgainOnMiss() {
		return tryAgainOnMiss;
	}

	public void setTryAgainOnMiss(boolean tryAgainOnMiss) {
		this.tryAgainOnMiss = tryAgainOnMiss;
	}

	public LivingEntity getSource() {
		return source;
	}

	public HashMap<LivingEntity, Integer> getTargetsOnCooldown() {
		return targetsOnCooldown;
	}

	public int getAnimationDuration() {
		return animationDuration;
	}

	public int getCurrentTick() {
		return currentTick;
	}

	public int getHitCooldown() {
		return hitCooldown;
	}

	public ArrayList<LivingEntity> getSummonedEntities() {
		return summonedEntities;
	}

	public void setSummonedEntities(ArrayList<LivingEntity> summonedEntities) {
		this.summonedEntities = summonedEntities;
	}
}
