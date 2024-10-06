package me.BerylliumOranges.bosses.Boss02;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.BerylliumOranges.bosses.actions.BossAction;
import me.BerylliumOranges.bosses.utils.Hazards;
import me.BerylliumOranges.bosses.utils.Hazards.Hazard;
import me.BerylliumOranges.customEvents.TickEvent;

public class AttackBlockWhip extends BossAction {

	private Location startLoc;

	public AttackBlockWhip(LivingEntity source) {
		super(source, 150, 50, 30, 0.5);
		animationDuration = 20;
		startLoc = source.getLocation();
		Bukkit.broadcastMessage("has Whip");
	}

	@Override
	public void execute(LivingEntity target) {
		final boolean hasHazard = Hazards.hasHazard(target.getWorld(), Hazard.SPIDER_SPAWN);

		Bukkit.broadcastMessage("Targetting " + target.getName());
		Location sourceLocation = source.getLocation();
		Location targetLocation = target.getLocation();

		// Calculate the direction vector from source to target
		Vector direction = targetLocation.toVector().subtract(sourceLocation.toVector()).normalize();

		// Length of the line
		int length = attackRange;
		// Define the width and height of the rectangular prism
//		double width = 2.7;
//		int height = 17;
		double width = 2;
		int height = 10;

		Set<LivingEntity> entitiesToDamage = new HashSet<>();
		// Loop over the length of the line
		int clearStartDistance = 4;
		// Loop over the length of the line
		int noDamageCount = 0;
		for (int i = 0; i < length; i++) {
			// Skip blocks too close to the source entity
			if (i < clearStartDistance) {
				continue;
			}
			if (noDamageCount > 0) {
				noDamageCount--;
				continue;
			}

			// Calculate the point along the line by scaling the direction
			Vector currentPoint = direction.clone().multiply(i);
			Location currentLocation = sourceLocation.clone().add(currentPoint);

			if (i % 3 == 0) {
				currentLocation.getWorld().spawnParticle(Particle.BLOCK, currentLocation, 17, width, height / 4, width, 0,
						Bukkit.createBlockData(Material.STONE));
				currentLocation.getWorld().playSound(currentLocation, Sound.ITEM_TOTEM_USE, SoundCategory.RECORDS, 0.05f, 2f);
			}
			// Loop over the width and height to create a 3D rectangular prism
			widthLoop: for (double w = -width / 2.0; w <= width / 2.0; w += 0.5) {
				for (double h = -height / 2.0 - (Math.random() * 2); h <= height / 2.0 + (Math.random() * 2); h += 0.5) {
					// Create the width in a perpendicular direction to the line (use cross product
					// with up vector)
					Vector widthVector = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(w);
					Vector heightVector = new Vector(0, 1, 0).multiply(h);

					// Calculate the location of the current block in the rectangular prism
					Location blockLocation = currentLocation.clone().add(widthVector).add(heightVector);
					for (Entity e : blockLocation.getWorld().getNearbyEntities(blockLocation, 0.5, 0.5, 0.5)) {
						if (e instanceof LivingEntity && !(e instanceof Spider))
							entitiesToDamage.add((LivingEntity) e);
					}

					if (hasHazard && blockLocation.getBlock().getType().equals(Material.COAL_BLOCK)) {
						if (Math.random() <= 1 / 100.0) {
							Spider s = blockLocation.getWorld().spawn(blockLocation, Spider.class);
							s.setTarget(target);
							summonedEntities.add(s);
						}
					}

					if (TowerPopulator.TOWER_MATERIALS.contains(blockLocation.getBlock().getType())) {
						if (!LeverEffect.invulBlocks.contains(blockLocation.getBlock()))
							blockLocation.getBlock().breakNaturally(new ItemStack(Material.AIR));

						blockLocation.getWorld().spawnParticle(Particle.FALLING_DUST, blockLocation, 100, 1, 1, 1, 0,
								Bukkit.createBlockData(blockLocation.getBlock().getType()));
						blockLocation.getWorld().spawnParticle(Particle.BLOCK, blockLocation, 100, 1, 1, 1, 0,
								Bukkit.createBlockData(Material.RED_WOOL));
						if (Math.random() > 0.9) {
							noDamageCount = 6;
							break widthLoop;
						}
						blockLocation.getWorld().playSound(blockLocation, Sound.ITEM_SHIELD_BREAK, SoundCategory.RECORDS, 0.6f, 0f);
					} else if (!blockLocation.getBlock().getType().equals(Material.LEVER)) {
						blockLocation.getBlock().setType(Material.AIR);
					}
				}
			}
		}

		for (LivingEntity liv : entitiesToDamage) {
			applyDamage(liv);
		}
	}

	ArrayList<Block> blocksToChange = new ArrayList<>();

	int arm = 1;

	@Override
	public void tick() {
		for (Block b : blocksToChange) {
			b.setType(Material.AIR);
		}
		blocksToChange.clear();

		World world = source.getWorld();

		int length = 7; // Number of blocks in the wave
		double baseAmplitude = 1; // Base amplitude of the sine wave
		double frequency = Math.PI / 4.0; // Frequency of the sine wave

		Location loc = source.getLocation().add(-0.5, 0.5, -0.5);

		for (int w = -1; w <= 1; w += 2) {
			int yawAdd = 90;
			if (playAnimation) {
				if (w == arm) {
					if (currentAnimationTick < animationDuration / 1.5) {
						yawAdd = 0;
					} else if (currentAnimationTick >= animationDuration / 1.5) {
						yawAdd = 180;
						source.getWorld().playSound(source, Sound.ENTITY_BREEZE_CHARGE, Float.MAX_VALUE, 0f);
					} else if (currentAnimationTick == animationDuration) {
						arm = arm == 1 ? -1 : 1;
					}
				}
			}
			double yawRadians = Math.toRadians(loc.getYaw() + yawAdd); // Offset by 90 degrees to align the wave side-to-side

			// Direction vectors based on yaw, rotated by 90 degrees for perpendicular
			// generation
			double sideX = Math.cos(yawRadians);
			double sideZ = Math.sin(yawRadians);
			// Create vector perpendicular to the entity's new side-facing direction
			double perpX = -sideZ * w; // This now aligns perpendicular to the side
			double perpZ = sideX * w; // This now aligns perpendicular to the side

			Vector offset = new Vector(perpX, 0, perpZ).normalize().multiply(5.5); // Start the wave at 5 blocks to the side
			Location startLocation = loc.clone().add(offset);

			double amplitudeAdjust = 2;
			for (int i = -length / 2; i <= length / 2; i++) {

				int y = (int) (Math.sin((frequency * i) + (currentTick / 3)) * baseAmplitude + startLocation.getY() + amplitudeAdjust);
				int x = (int) (startLocation.getX() + i * perpX);
				int z = (int) (startLocation.getZ() + i * perpZ);

				// Change the block above the ground to white wool (or any material you prefer)
				Location newLoc = new Location(world, x, y + 1, z);
				if (newLoc.getBlock().getType() == Material.AIR) {
					newLoc.getBlock().setType(Material.WHITE_WOOL);
					blocksToChange.add(newLoc.getBlock());
				}

				amplitudeAdjust += 0.5;
			}
		}
	}

	@EventHandler
	public void onSummonedEntityDamaged(EntityDamageEvent e) {
		if (e.getEntity() instanceof Spider && e.getEntity().getWorld().equals(source.getWorld())
				&& e.getEntity().getLocation().distanceSquared(source.getLocation()) < 40000
				&& (e.getCause().equals(DamageCause.SUFFOCATION) || e.getCause().equals(DamageCause.FALL))) {
			e.getEntity().teleport(e.getEntity().getLocation().add(0, 1, 0));
			e.setCancelled(true);
		}
	}

	int targetTicks = 0;

	@EventHandler
	public void onTick2(TickEvent e) {
		targetTicks++;
		if (targetTicks % 5 == 0) {
			for (Entity ent : source.getNearbyEntities(100, 100, 100)) {
				if (ent instanceof Spider) {
					((Spider) ent).setRemoveWhenFarAway(false);
					for (Entity targetOption : ent.getNearbyEntities(15, 15, 15)) {
						if (targetOption instanceof Player) {
							Player p = (Player) targetOption;
							Spider s = (Spider) ent;
							if (s.getTarget() == null && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
								s.setTarget(p);
							} else if (s.getTarget() == p
									&& (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR)) {
								s.setTarget(null);
							}
						}
					}
				}
			}
		}

	}
}
