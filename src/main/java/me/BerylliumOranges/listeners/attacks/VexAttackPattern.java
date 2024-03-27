package me.BerylliumOranges.listeners.attacks;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.util.Vector;

import me.BerylliumOranges.main.PluginMain;

public class VexAttackPattern extends AttackPatternAbstract {
	public static final int IDLE_TICKS = 20;
	public static final int PREPARE_TICKS = IDLE_TICKS + 20;
	public static final int ATTACK_TICKS = PREPARE_TICKS + 10;

	public static final DustOptions DUST = new DustOptions(Color.RED, 0.8F);
	public Vector attackDirection = new Vector(0, 0, 0);

	public VexAttackPattern(LivingEntity entity, LivingEntity target) {
		super(entity, target);
	}

	@Override
	public void tick() {
		Vex vex = (Vex) entity;
		ticks++;

		if (ticks < IDLE_TICKS) {
			// idle
		} else if (ticks < PREPARE_TICKS) {
			// prepare
			if (ticks == IDLE_TICKS) {
				LivingEntity t = vex.getTarget();
				if (t == null)
					t = target;
				else
					target = vex.getTarget();
				if (t == null) {
					return;
				}

				Location ploc = t.getEyeLocation();
				ploc.subtract(vex.getEyeLocation());
				Vector v = ploc.toVector().normalize();
				Location turn = vex.getLocation();
				turn.setDirection(v);
				vex.teleport(turn);
				vex.setAI(false);
				vex.setSilent(true);

				attackDirection = turn.getDirection();
			}
			Location loc = vex.getEyeLocation();
			for (int i = 0; i < 10; i++) {
				loc.add(attackDirection.clone().multiply(1 + (0.25 - (0.5 * Math.random()))));
				vex.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, 0, DUST, false);
			}

			vex.setVelocity(new Vector(0, 0, 0));
		} else if (ticks < ATTACK_TICKS) {
			Location loc = vex.getLocation();
			for (int i = 0; i < 10; i++) {
				loc.add(attackDirection.clone().multiply(0.1));
				vex.teleport(loc);
				for (Entity e : PluginMain.getNearbyEntities(loc, 1.3)) {
					if (e instanceof Breedable || e instanceof Player) {
						LivingEntity liv = (LivingEntity) e;
						liv.damage(7, vex);
						liv.setNoDamageTicks(1);
						loc.getWorld().playSound(loc, Sound.BLOCK_AZALEA_LEAVES_BREAK, 1, 1);
					}
				}
			}
		} else if (ticks == ATTACK_TICKS) {
			vex.setAI(true);
			vex.setSilent(false);
		} else {
			// reset
			ticks = (int) (IDLE_TICKS * Math.random());
		}
	}
}