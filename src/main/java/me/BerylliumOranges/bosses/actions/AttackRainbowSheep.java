package me.BerylliumOranges.bosses.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class AttackRainbowSheep extends BossAction {

	private Map<Sheep, Vector> sheepDirections = new HashMap<>();
	private int sheepTicks = 0;

	private static final DyeColor[] colors = { DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.LIGHT_BLUE,
			DyeColor.BLUE, DyeColor.PURPLE };

	public AttackRainbowSheep(LivingEntity source) {
		super(source, 90, 40, 30); // Adjust maxTicksUntilAttack and attackRange as needed, damage is set to 30
	}

	@Override
	public void execute(LivingEntity target) {
		spawnRainbowSheep(source.getLocation());
	}

	@Override

	public void applyDamage(LivingEntity target) {
		if (!targetsOnCooldown.containsKey(target) && !sheepDirections.containsKey(target)) {
			targetsOnCooldown.put(target, hitCooldown);
			target.damage(damage, source);
		}
	}

	private void spawnRainbowSheep(Location location) {
		Vector direction = location.getDirection().normalize();
		Location spawnLocation = location.clone();

		// Calculate a perpendicular direction for spacing the sheep
		Vector perpendicularDirection = new Vector(-direction.getZ(), direction.getY(), direction.getX()).normalize().multiply(1.5);
		spawnLocation = spawnLocation.clone().subtract(perpendicularDirection.clone().multiply(3));
		for (int i = 0; i < colors.length; i++) {
			Sheep sheep = location.getWorld().spawn(spawnLocation, Sheep.class);
			sheep.setAdult();
			sheep.setColor(colors[i]);
			sheep.setAI(false);
			sheep.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(45);
			sheep.setHealth(45);
			sheep.setInvulnerable(true);
			// Store the sheep and its direction
			sheepDirections.put(sheep, direction.clone().setY(0).normalize().multiply(0.45));

			// Adjust the next spawn location two blocks to the side
			spawnLocation = spawnLocation.clone().add(perpendicularDirection);
		}
	}

	@Override
	public void tick() {
		// Move each sheep forward a bit in its stored direction
		sheepDirections.forEach((sheep, direction) -> {
			if (!sheep.isDead()) {
				sheep.getWorld().spawnParticle(Particle.REDSTONE, sheep.getLocation().add(0, 1, 0), 10,
						new Particle.DustOptions(sheep.getColor().getColor(), 1));

				Location originalLocation = sheep.getLocation().add(direction);
				Location checkLocation = originalLocation.clone().add(0, 3, 0); // Start checking from 3 blocks above
				boolean suitableLocationFound = false;

				for (int i = 0; i <= 6; i++) { // Check up to 3 blocks up and 3 blocks down
					// If the block is not passable (solid), and the block above is passable, we
					// found a surface
					if (!checkLocation.getBlock().isPassable() && checkLocation.clone().add(0, 1, 0).getBlock().isPassable()) {
						sheep.teleport(checkLocation.add(0, 1, 0)); // Teleport the sheep to the block above the solid surface
						suitableLocationFound = true;
						break;
					}
					checkLocation.add(0, -1, 0); // Move down for the next check
				}

				if (!suitableLocationFound) {
					if (originalLocation.clone().add(0, -1, 0).getBlock().isPassable()) {
						originalLocation = originalLocation.clone().add(0, -1, 0);
					}
					sheep.teleport(originalLocation);
				}

				for (Entity entity : sheep.getNearbyEntities(1, 1, 1)) {
					if (entity instanceof LivingEntity && !entity.equals(source)) {
						applyDamage((LivingEntity) entity);
					}
				}
			}
		});

		if (!sheepDirections.isEmpty()) {
			sheepTicks++;
			if (sheepTicks > 30) {
				if (sheepTicks % 4 == 0) {
					if (!sheepDirections.isEmpty()) {
						List<Sheep> sheepList = new ArrayList<>(sheepDirections.keySet());
						int randomIndex = (int) (Math.random() * sheepList.size()); // Correctly calculate a random index
						LivingEntity sheep = sheepList.get(randomIndex); // Get a random sheep
						if (!sheep.isDead())
							sheep.getWorld().playSound(sheep.getLocation(), Sound.ENTITY_SHEEP_HURT, 10F, 1F);
					}

				}

				sheepDirections.forEach((sheep, direction) -> {
					if (!sheep.isDead() && (sheepTicks > 70 || Math.random() > 0.94))
						sheep.remove();
				});
				if (sheepTicks > 70) {
					sheepDirections.clear();
					sheepTicks = 0;
				}
			} else if (sheepTicks == 1)
				sheepDirections.forEach((sheep, direction) -> {
					sheep.setInvulnerable(false);
				});
		}
	}

	public void makeSheepExplode(LivingEntity sheep) {
		sheep.remove();
		sheep.getWorld().createExplosion(sheep.getLocation(), 1F);
	}

	@EventHandler
	public void onSheepDamage(EntityDamageEvent e) {
		if (e.getCause().equals(DamageCause.SUFFOCATION) || e.getCause().equals(DamageCause.CRAMMING)
				|| e.getCause().equals(DamageCause.CONTACT) || e.getCause().equals(DamageCause.FLY_INTO_WALL)
				|| e.getCause().equals(DamageCause.FALL) || e.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
			if (sheepDirections.containsKey(e.getEntity())) {
				e.setCancelled(true);
			}
		}
	}
}
