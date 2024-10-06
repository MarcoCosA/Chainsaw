package me.BerylliumOranges.bosses.Boss02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;

public class TowerPopulator {
	ArrayList<TowerNode> towers = new ArrayList<>();
	ArrayList<LeverEffect> levers = new ArrayList<>();
	int maxHeight = 40;
	double maxRadius = 70;
	final Random random = new Random(80085);

	public static final Material TOWER_MATERIAL = Material.DEEPSLATE_BRICKS;
	public static final Material TOWER_PEAK_MATERIAL = Material.DEEPSLATE_BRICK_SLAB;
	public static final Material TOWER_STAIRS_MATERIAL = Material.DEEPSLATE_BRICK_STAIRS;
	public static final Material TOWER_UNUSED = Material.DEEPSLATE_GOLD_ORE;
	public static final Material TOWER_USED = Material.DEEPSLATE_COAL_ORE;

	public static final List<Material> TOWER_MATERIALS = Arrays.asList(TOWER_MATERIAL, TOWER_PEAK_MATERIAL, TOWER_STAIRS_MATERIAL,
			TOWER_UNUSED, TOWER_USED);

	public void makeTowers(Location loc, ArrayList<LeverEffect> levers) {
		this.levers = levers;
		int numberOfTowers = levers.size();
		double alpha = 2; // Adjust this to spread out the outermost points more or less
		double phi = (Math.sqrt(5) + 1) / 2; // Golden ratio
		int b = (int) Math.round(alpha * Math.sqrt(numberOfTowers)); // Number of boundary points

		for (int i = 0; i < numberOfTowers; i++) {
			double r = calculateRadius(i + 1, numberOfTowers, b) * maxRadius;
			double theta = 2 * Math.PI * (i + 1) / (phi * phi);
			int x = (int) (r * Math.cos(theta));
			int z = (int) (r * Math.sin(theta));
			int y = loc.getWorld().getHighestBlockYAt(loc.getBlockX() + x, loc.getBlockZ() + z);
			Location towerLocation = new Location(loc.getWorld(), loc.getBlockX() + x, y, loc.getBlockZ() + z);
			TowerNode tower = new TowerNode(null, towerLocation, null, levers.get(i), random, TOWER_MATERIAL, TOWER_PEAK_MATERIAL,
					TOWER_STAIRS_MATERIAL, maxHeight, maxRadius);
			towers.add(tower);
			tower.buildTower();
		}
	}

	private static double calculateRadius(int k, int n, int b) {
		if (k > n - b) {
			return 1; // Put on the boundary
		} else {
			return Math.sqrt(k - 0.5) / Math.sqrt(n - (b + 0.5)); // Apply square root scaling
		}
	}
}
