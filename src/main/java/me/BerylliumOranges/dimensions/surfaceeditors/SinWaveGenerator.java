package me.BerylliumOranges.dimensions.surfaceeditors;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class SinWaveGenerator {
	private ArrayList<Vector> blockPlacementMap = new ArrayList<>();
	private double difficultyMultiplier;
	private World world;
	private Random random = new Random();

	public SinWaveGenerator(World world, double difficultyMultiplier) {
		this.world = world;
		this.difficultyMultiplier = difficultyMultiplier;
	}

	public void generateWaves() {
		for (Vector v : blockPlacementMap) {
			world.getBlockAt(v.toLocation(world)).setType(Material.AIR);
		}
		blockPlacementMap.clear();

		difficultyMultiplier += 0.1;
		int numberOfWaves = (int) Math.min(Math.ceil(difficultyMultiplier * 10), 5);

		int totalWidth = 70;
		int waveSpacing = totalWidth / numberOfWaves;
		int length = 70;

		int selectedWave = random.nextInt(numberOfWaves);
		double selectedFrequency = 0;
		String selectedType = "";

		for (int waveIndex = 0; waveIndex < numberOfWaves; waveIndex++) {
			int centerZ = waveIndex * waveSpacing - (totalWidth / 2) + (waveSpacing / 2);
			double amplitude = 5 + random.nextDouble() * 10;
			double frequency = 0.1 + random.nextDouble() * 0.1;
			String type = getRandomFunctionType();

			if (waveIndex == selectedWave) {
				selectedFrequency = frequency;
				selectedType = type;
			}

			generateWave(centerZ, 0, amplitude, frequency, length, Material.WHITE_CONCRETE, false, type);
		}

		// Generate the negative wave in front of other waves
		generateWave(0, (totalWidth / 2) + 40, 8, selectedFrequency, length, Material.YELLOW_CONCRETE, true, selectedType);
	}

	private String getRandomFunctionType() {
		String[] types = { "sin", "cos", "tan", "parabolic" };
		return types[random.nextInt(types.length)];
	}

	private void generateWave(int centerZ, int centerX, double amplitude, double frequency, int length, Material material, boolean rotate,
			String type) {
		int dX = rotate ? 0 : 1;
		int dZ = rotate ? 1 : 0;

		double start = rotate ? centerZ - length / 2 : centerX - length / 2;
		double end = rotate ? centerZ + length / 2 : centerX + length / 2;

		for (double i = start; i < end; i += 0.5) {
			double funcValue = getFunctionValue(type, frequency * i);
			int y = (int) (amplitude * funcValue) + 64;
			double x = rotate ? centerX : i;
			double z = rotate ? i : centerZ;

			Location blockLocation = new Location(world, x, y, z);
			blockLocation.getBlock().setType(material);
			blockPlacementMap.add(new Vector(x, y, z));
		}
	}

	private double getFunctionValue(String type, double input) {
		switch (type) {
		case "sin":
			return Math.sin(input);
		case "cos":
			return Math.cos(input);
		case "tan":
			try {
				return Math.tan(input);
			} catch (Exception e) {
				return 0; // Handle undefined tan values
			}
		case "parabolic":
			return input * input; // Simplified parabolic function for visual effect
		default:
			return Math.sin(input); // Default to sine if type is unknown
		}
	}
}
