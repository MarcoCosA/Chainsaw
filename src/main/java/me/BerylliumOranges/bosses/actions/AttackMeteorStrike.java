package me.BerylliumOranges.bosses.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

public class AttackMeteorStrike extends BossAction {

	private static final double EFFECT_RADIUS = 12.0;
	private static final double DAMAGE_RADIUS = 12.0;
	private static final double DAMAGE = 80.0;
	private static final double TRANSFORMATION_CHANCE = 0.5; // 50% chance to transform surrounding blocks

	public ArrayList<Fireball> fireballs = new ArrayList<>();

	public AttackMeteorStrike(LivingEntity source) {
		super(source, 200, 100, 0); // Cool down, range, damage (not used directly here)
	}

	@Override
	public void playAnimation() {
		source.getWorld().playSound(source.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 0.5F);
	}

	@Override
	public void execute(LivingEntity target) {
		World world = target.getWorld();
		// Simulating the fireball coming from the sky, setting the launch position high
		// above the target.
		Location launchLocation = target.getLocation().clone().add(0, 50, 0);
		Fireball fireball = world.spawn(launchLocation, Fireball.class, fb -> {
			fb.setIsIncendiary(false);
			fb.setYield(0); // No explosion yield to control the explosion effect manually
			fb.setVelocity(new Vector(0, -1, 0)); // Directly downwards
			fb.setShooter(source);
		});
		fireballs.add(fireball);
	}

	@EventHandler
	public void onExplosion(EntityExplodeEvent e) {
		if (fireballs.contains(e.getEntity())) {
			e.setCancelled(true);
			handleExplosion(e.getEntity());
		}
	}

	// Custom explosion logic to handle block destruction and player damage
	public void handleExplosion(Entity fireball) {
		Location impactLocation = fireball.getLocation();
		List<Player> nearbyPlayers = fireball.getWorld().getPlayers().stream()
				.filter(p -> p.getLocation().distance(impactLocation) <= DAMAGE_RADIUS).toList();

		// Damage players
		for (Player player : nearbyPlayers) {
			applyDamage(player);
		}

		// Create a visual effect
		fireball.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, impactLocation, 5);
		fireball.getWorld().playSound(fireball.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5F, 0);

		// Block destruction and transformation
		int effectRadius = (int) Math.round(EFFECT_RADIUS);
		for (int dx = -effectRadius; dx <= effectRadius; dx++) {
			for (int dy = -effectRadius; dy <= effectRadius; dy++) {
				for (int dz = -effectRadius; dz <= effectRadius; dz++) {
					if (dx * dx + dy * dy + dz * dz <= EFFECT_RADIUS * EFFECT_RADIUS) {
						Location blockLocation = impactLocation.clone().add(dx, dy, dz);
						blockLocation.getBlock().setType(Material.AIR);
					}
				}
			}
		}

		// Block transformation within a slightly larger radius
		int blockEffectDistance = (int) Math.round(EFFECT_RADIUS) + 1;
		for (int dx = -blockEffectDistance; dx <= blockEffectDistance; dx++) {
			for (int dy = -blockEffectDistance; dy <= blockEffectDistance; dy++) {
				for (int dz = -blockEffectDistance; dz <= blockEffectDistance; dz++) {
					if (dx * dx + dy * dy + dz * dz <= (blockEffectDistance) * (blockEffectDistance)) {
						Location blockLocation = impactLocation.clone().add(dx, dy, dz);
						if (new Random().nextDouble() < TRANSFORMATION_CHANCE) {
							blockLocation.getBlock().setType(Material.MAGMA_BLOCK);
						}
					}
				}
			}
		}
	}
}
