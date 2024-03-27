package me.BerylliumOranges.listeners.attacks;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class CoalGoblinAttack extends AttackPatternAbstract {
	public static final DustOptions DUST = new DustOptions(Color.BLACK, 10F);
	public Vector attackDirection = new Vector(0, 0, 0);

	public static final int IDLE_TICKS = 20;
	public static final int PREPARE_TICKS = IDLE_TICKS + 20;
	public static final int ATTACK_TICKS = PREPARE_TICKS + 10;

	public Location damageArea;

	public CoalGoblinAttack(LivingEntity entity, LivingEntity target) {
		super(entity, target);
	}

	@Override
	public void tick() {
		if (entity instanceof Mob) {
			Mob m = (Mob) entity;
			LivingEntity t = m.getTarget();
			if (t == null)
				t = target;
			else
				target = m.getTarget();
			if (t == null) {
				return;
			}
		}

		ticks++;
		if (ticks < IDLE_TICKS) {

		} else if (ticks < PREPARE_TICKS) {
			if (ticks < PREPARE_TICKS - 5) {
				if (entity.hasLineOfSight(target)) {
					Vector direction = target.getLocation().clone().add(0, 0, 0).toVector()
							.subtract(entity.getLocation().clone().add(0, -1, 0).toVector()).normalize();

					entity.getLocation().getWorld().spawnParticle(Particle.SQUID_INK, entity.getLocation().clone().add(0, 0.5, 0), 0,
							direction.getX(), direction.getY(), direction.getZ(), 0.75);

					damageArea = entity.getLocation().clone().add(direction.clone().multiply(5));
				}
			} else {

				damageArea.getWorld().spawnParticle(Particle.REDSTONE, damageArea, 0, 0, 0, 0, 0, DUST);
			}

		} else if (ticks == ATTACK_TICKS) {
			if (damageArea != null) {
				if (target.getWorld().equals(damageArea.getWorld()) && target.getLocation().distanceSquared(damageArea) <= 9) {
					Vector direction = target.getLocation().clone().add(0, 0, 0).toVector()
							.subtract(entity.getLocation().clone().add(0, -1, 0).toVector()).normalize();
					target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, true, true));
					target.setVelocity(target.getVelocity().add(direction.multiply(-1)));
					target.damage(3.0);

				}
			}
			ticks = (int) (IDLE_TICKS * Math.random());
		}

	}
}