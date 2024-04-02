package me.BerylliumOranges.bosses.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;

import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import net.md_5.bungee.api.ChatColor;

public class HazardsChestGenerator {

	public static List<LocationDistancePair> locations = new ArrayList<>();

	public static void placeChests(World w) {
		locations.clear();

		// Settings
		int numberOfChests = BossType.values().length;
		double radius = 60.0; // Adjust if you want a different scale
		double alpha = 2; // Adjust based on your preference for the boundary
		double phi = (Math.sqrt(5) + 1) / 2; // Golden ratio
		int b = (int) Math.round(alpha * Math.sqrt(numberOfChests)); // Number of boundary points

		// Calculating positions using sunflower pattern
		for (int i = 0; i < numberOfChests; i++) {
			double r = radius(i + 1, numberOfChests, b) * radius; // Adapted for the specified radius
			double theta = 2 * Math.PI * (i + 1) / (phi * phi);
			double x = r * Math.cos(theta);
			double z = r * Math.sin(theta);

			Location loc = new Location(w, x, 64, z); // Assuming Y is constant

			// Check distance from other locations
			boolean validLocationFound = true; // Start with true, only invalidate if too close
			for (LocationDistancePair existing : locations) {
				if (existing.location.distance(loc) < 10.0) {
					// Location is too close to an existing one
					validLocationFound = false;
					break;
				}
			}

			// Add the found valid location if it's valid
			if (validLocationFound) {
				locations.add(new LocationDistancePair(loc, loc.distance(new Location(w, 0, 64, 0))));
			}
		}

		locations.sort(Comparator.comparingDouble(LocationDistancePair::getDistance));

		for (int i = 0; i < locations.size(); i++) {

			LocationDistancePair pair = locations.get(i);

			Block block = w.getHighestBlockAt(pair.getLocation().clone());
			// Prepare a 2x2 base of dirt
			for (int dx = 0; dx < 2; dx++) {
				for (int dz = 0; dz < 2; dz++) {
					Block newB = block.getLocation().clone().add(dx, 0, dz).getBlock();
					newB.setType(Material.DIRT);

					Location treeLocation = newB.getLocation().clone().add(0, 1, 0); // Adjust as necessary for correct tree placement
					w.generateTree(treeLocation, TreeType.CHERRY);
				}
			}

			BlockFace face = BlockFace.WEST;
			int xAbs = (int) Math.abs(pair.getLocation().getX());
			int zAbs = (int) Math.abs(pair.getLocation().getZ());
			if (xAbs <= zAbs && pair.getLocation().getX() > 0) {
				face = BlockFace.NORTH;
			} else if (pair.getLocation().getX() < 0 && pair.getLocation().getZ() <= 0) {
				face = BlockFace.EAST;
			} else if (pair.getLocation().getX() >= 0 && pair.getLocation().getZ() < 0) {
				face = BlockFace.SOUTH;
			}

			double newX = block.getLocation().getX();
			if (newX > 0) {
				newX -= 1;
			} else if (newX < 0) {
				newX += 1;
			}
			double newZ = block.getLocation().getZ();
			if (newZ > 0) {
				newZ -= 1;
			} else if (newZ < 0) {
				newZ += 1;
			}

			pair.setLocation(new Location(pair.getLocation().getWorld(), newX, block.getLocation().getY() + 2, newZ));
			pair.getLocation().clone().add(0, -1, 0).getBlock().setType(Material.CHERRY_LOG);
			pair.getLocation().clone().add(0, 1, 0).getBlock().setType(Material.AIR);
			Block chestBlock = pair.getLocation().getBlock(); // Adjust for the right height
			chestBlock.setType(Material.CHEST);
			Chest chest = (Chest) chestBlock.getState();
			Directional directional = (Directional) chestBlock.getBlockData();
			directional.setFacing(face);
			chest.setBlockData(directional);
			BossType t = BossType.values()[i >= BossType.values().length ? 0 : i];
			chest.setCustomName(t.getName() + DUNGEON_TAG);
			chest.update();

			new HazardInventoryGenerator(chest.getInventory(), t);

		}
	}

	public static final String DUNGEON_TAG = ChatColor.DARK_GRAY + " Dungeon";

	private static double radius(int k, int n, int b) {
		if (k > n - b) {
			return 1; // Put on the boundary
		} else {
			return Math.sqrt(k - 0.5) / Math.sqrt(n - (b + 0.5)); // Apply square root scaling
		}
	}

	public static class LocationDistancePair {
		private Location location;
		private final double distance;

		public LocationDistancePair(Location location, double distance) {
			this.location = location;
			this.distance = distance;
		}

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		public double getDistance() {
			return distance;
		}
	}
}
