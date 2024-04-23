package me.BerylliumOranges.bosses.actions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import me.BerylliumOranges.customEvents.TickEvent;

public class DefenseProjectile extends BossAction {

	public DefenseProjectile(LivingEntity source) {
		super(source, Integer.MAX_VALUE, 10, 0);
	}

	@Override
	public void execute(LivingEntity target) {
	}

	@Override
	@EventHandler
	public void onTick(TickEvent e) {

		for (Entity ent : source.getWorld().getEntities()) {
			if (ent instanceof Projectile) {
				Projectile projectile = (Projectile) ent;
				// Get entities within 8 blocks of the projectile
				if (!projectile.isOnGround() && isProjectileApproaching(projectile, source)) {
					// Logic here if the projectile is approaching the entity
					System.out.println("Projectile is approaching " + source.getName());
					source.getWorld().playSound(source.getLocation(), Sound.BLOCK_HANGING_SIGN_HIT, 1F, 1F);
					Location loc = ent.getLocation();
					loc.add(ent.getLocation().getDirection());
					loc.getBlock().setType(Material.COBBLESTONE);
				}
			}
		}
	}

	private boolean isProjectileApproaching(Projectile projectile, Entity entity) {
		Vector toEntity = entity.getLocation().toVector().subtract(projectile.getLocation().toVector());
		Vector projectileVelocity = projectile.getVelocity();
		double distance = toEntity.length();
		double speed = projectileVelocity.length();

		// Normalize vectors for direction comparison
		Vector toEntityDirection = toEntity.normalize();
		Vector projectileDirection = projectileVelocity.normalize();

		// Calculate the dot product to determine if the projectile is moving towards
		// the entity
		double dot = toEntityDirection.dot(projectileDirection);

		// Check if the projectile is moving directly towards the entity (dot product
		// close to 1)
		if (dot > 0.85) {
			// Predict the entity's bounding box radius as a simple hit detection mechanism
			double entitySize = (entity.getWidth() + entity.getHeight()) * 1.75; // Rough approximation of entity size

			// Calculate the closest approach of the projectile to the entity's current
			// position
			double approachDistance = Math.sin(Math.acos(dot)) * distance;

			// If the closest approach distance is less than the entity's size, and the
			// entity is within the next tick's travel distance, there's a chance of a hit
			if (approachDistance < entitySize && distance < speed + entitySize) {
				return true;
			}
		}

		return false;
	}
}
