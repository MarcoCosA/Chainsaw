package me.BerylliumOranges.bosses.Boss02;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.util.Vector;

import me.BerylliumOranges.bosses.actions.BossAction;
import me.BerylliumOranges.bosses.utils.Hazards;
import me.BerylliumOranges.bosses.utils.Hazards.Hazard;

public class AttackBlockWhip extends BossAction {

	private Location startLoc;

	public AttackBlockWhip(LivingEntity source) {
		super(source, 400, 100, 50); // Adjust maxTicksUntilAttack and attackRange as needed, damage is set to 30
		animationDuration = 20;
		startLoc = source.getLocation();
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
		double width = 2.7;
		int height = 20;

		Set<LivingEntity> entitiesToDamage = new HashSet<>();
		// Loop over the length of the line
		int clearStartDistance = 4;

		// Loop over the length of the line
		for (int i = 0; i < length; i++) {
			// Skip blocks too close to the source entity
			if (i < clearStartDistance) {
				continue;
			}

			// Calculate the point along the line by scaling the direction
			Vector currentPoint = direction.clone().multiply(i);
			Location currentLocation = sourceLocation.clone().add(currentPoint);

			// Loop over the width and height to create a 3D rectangular prism
			for (double w = -width / 2.0; w <= width / 2.0; w += 0.5) {
				for (double h = -height / 2.0 - (Math.random() * 2); h <= height / 2.0 + (Math.random() * 2); h += 0.5) {

					// Create the width in a perpendicular direction to the line (use cross product
					// with up vector)
					Vector widthVector = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(w);
					Vector heightVector = new Vector(0, 1, 0).multiply(h);

					// Calculate the location of the current block in the rectangular prism
					Location blockLocation = currentLocation.clone().add(widthVector).add(heightVector);

					// Set the block at this location to air

					if (hasHazard && blockLocation.getBlock().getType().equals(Material.COAL_BLOCK)) {
						if (Math.random() <= 2 / 100.0) {
							Spider s = blockLocation.getWorld().spawn(blockLocation, Spider.class);
							s.setTarget(target);
						}
					}
					blockLocation.getBlock().setType(Material.AIR);

					for (Entity e : blockLocation.getWorld().getNearbyEntities(blockLocation, 1, 1, 1)) {
						if (e instanceof LivingEntity)
							entitiesToDamage.add((LivingEntity) e);
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
}
