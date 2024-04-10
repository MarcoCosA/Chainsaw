package me.BerylliumOranges.dimensions.surfaceeditors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.BerylliumOranges.dimensions.chunkgenerators.CubeChunkGenerator;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class TopographyGenerator {
	private ArrayList<Vector> blockPlacementMap = new ArrayList<>();
	private int lastMax = Integer.MIN_VALUE;
	private int lastMin = Integer.MAX_VALUE;
	private double difficultyMultilpier = 0.4;

	public enum TopographyType {
		WATER_DROP_RIPPLE(generator -> generator.generateWaterDropRipple()),
		STABLE_SOUND_WAVE(generator -> generator.generateStableSoundWave()),
		SAND_ON_OSCILLATING_SURFACE(generator -> generator.generateSandOnOscillatingSurface()),
		SPIRAL_VORTEX(generator -> generator.generateSpiralVortex());

		private final Consumer<TopographyGenerator> action;

		TopographyType(Consumer<TopographyGenerator> action) {
			this.action = action;
		}

		public void generate(TopographyGenerator generator) {
			action.accept(generator);
		}
	}

	private World world;
	private int cubeSize;
	private TopographyType lastTopographyType = null;

	public TopographyGenerator(World world, int cubeSize) {
		this.world = world;
		this.cubeSize = cubeSize;
	}

	public void generateRandomTopographyExcludingLast() {
		List<TopographyType> values = Arrays.stream(TopographyType.values()).filter(t -> t != lastTopographyType)
				.collect(Collectors.toList());

		TopographyType selected = values.get(new Random().nextInt(values.size()));
		Bukkit.broadcastMessage(selected.toString() + " " + ChatColor.RED + Math.floor(difficultyMultilpier * 10.0) / 10.0);
		clearTopography();
		selected.generate(this);

		addGreenAndApplyStoredPattern((int) (Math.random() * 3));
		difficultyMultilpier += 0.1;
		lastTopographyType = selected;
	}

	public void addGreenAndApplyStoredPattern(int rnd) {
		int temp = lastMax;
		if (rnd == 1) {
			temp = (lastMax + lastMin) / 2;
			Bukkit.broadcastMessage("Mid");
		} else if (rnd == 2) {
			temp = lastMin;
			Bukkit.broadcastMessage("Bottom");
		} else
			Bukkit.broadcastMessage("Top");
		final int greenY = temp;
		new BukkitRunnable() {
			private int ticksElapsed = 0;

			@Override
			public void run() {
				if (ticksElapsed >= 20) {
					this.cancel();
					return;
				}
				int yMod = ticksElapsed - 10;

				for (Vector position : blockPlacementMap) {
					Block block = world.getBlockAt(position.getBlockX(), position.getBlockY() + yMod - 1, position.getBlockZ());

					if (!block.getType().equals(Material.OBSIDIAN)) {

						block.setType(Material.AIR);
//						for (Entity ent : world.getEntities()) {
//							if (ent.getLocation().getBlock().equals(block)) {
//								ent.teleport(ent.getLocation().add(0, 1, 0));
//							}
//						}
					}
				}

				for (Vector position : blockPlacementMap) {
					Material mat = Material.RED_CONCRETE;
					Block block = world.getBlockAt(position.getBlockX(), position.getBlockY() + yMod, position.getBlockZ());

					if (!block.getType().equals(Material.OBSIDIAN) && !block.getType().equals(Material.GREEN_CONCRETE)) {
						if (position.getBlockY() < greenY + 1 && position.getBlockY() >= greenY) {
							mat = Material.GREEN_CONCRETE;
						} else {
							mat = Material.RED_CONCRETE;
						}

						block.setType(mat);
//						for (Entity ent : world.getEntities()) {
//							if (ent.getLocation().getBlock().equals(block)) {
//								ent.teleport(ent.getLocation().add(0, 1, 0));
//							}
//						}
					}
				}

				ticksElapsed++;
			}
		}.runTaskTimer(PluginMain.getInstance(), 0L, 0L);
	}

	private void addBlockToMap(Vector v) {
		if (v.getBlockY() > lastMax)
			lastMax = v.getBlockY();

		if (v.getBlockY() < lastMin && v.getBlockY() > CubeChunkGenerator.CUBE_SURFACE_Y - 20)
			lastMin = v.getBlockY();
		blockPlacementMap.add(v);
	}

	public void clearTopography() {
		int centerX = 0;
		int centerZ = 0;

		Material resetMaterial = Material.AIR; // Change this to whatever baseline material you want
		for (int y = CubeChunkGenerator.CUBE_SURFACE_Y; y < 100; y++)
			for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
				for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
					Block block = world.getBlockAt(centerX + x, y, centerZ + z);
					block.setType(resetMaterial);
				}
			}
		lastMax = Integer.MIN_VALUE;
		lastMin = Integer.MAX_VALUE;
		blockPlacementMap.clear();
	}

	public void generateWaterDropRipple() {
		int centerX = 0;
		int centerY = CubeChunkGenerator.CUBE_SURFACE_Y;
		int centerZ = 0;

		double frequency = 0.34 + getRandom() / 1.8; // Frequency of the ripples
		double amplitude = 4 + getRandom() * 4; // Height of the ripples

		blockPlacementMap.clear(); // Clear previous placements

		for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
			for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
				double distance = Math.sqrt(x * x + z * z);
				double y = Math.sin(distance * frequency) * amplitude;

				int blockY = centerY + (int) y;
				addBlockToMap(new Vector(centerX + x, blockY, centerZ + z));
			}
		}
	}

	public void generateStableSoundWave() {
		int centerX = 0;
		int centerY = CubeChunkGenerator.CUBE_SURFACE_Y; // The y-level where the pattern is generated
		int centerZ = 0;

		double amplitude = 4.5 + getRandom() * 4.5; // This ranges from 4.5 to 9
		double frequency = 0.25 + getRandom() / 4.0; // Frequency of the sound waves

		for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
			for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
				// Generating a stable wave pattern across the z-axis
				double y = Math.sin(z * frequency) * amplitude;

				int blockY = centerY + (int) y;

				addBlockToMap(new Vector(centerX + x, blockY, centerZ + z));
			}
		}
	}

	public void generateSandOnOscillatingSurface() {
		int centerX = 0;
		int centerY = CubeChunkGenerator.CUBE_SURFACE_Y; // The y-level where the pattern is generated
		int centerZ = 0;

		// Parameters for oscillation
		double frequencyX = 0.15 + getRandom();
		double frequencyZ = 0.15 + getRandom();
		double amplitudeX = 1.5 + getRandom() * 4;
		double amplitudeZ = 1.5 + getRandom() * 4;
		double phaseShift = Math.PI / 4; // Adding a phase shift to create more complex patterns

		for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
			for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
				// Combining two sine waves at different axes
				double y = Math.sin(x * frequencyX + phaseShift) * amplitudeX + Math.sin(z * frequencyZ) * amplitudeZ;

				int blockY = centerY + (int) y;
				addBlockToMap(new Vector(centerX + x, blockY, centerZ + z));
			}
		}
	}

	public void generateSpiralVortex() {
		int centerX = 0;
		int centerY = CubeChunkGenerator.CUBE_SURFACE_Y; // The y-level where the pattern is generated
		int centerZ = 0;

		// Parameters for the spiral
		double amplitude = 1 + getRandom() * 3; // Amplitude ranging from 5 to 10
		double frequency = 0.1 + getRandom() * 2.0; // Frequency of the spiral
		double maxRadius = cubeSize / 2; // Maximum radius to fit within the cube
		double heightIncrease = 0.05 + getRandom() / 10.0; // Height increase per unit length of the spiral

		for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
			for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
				addBlockToMap(new Vector(centerX + x, CubeChunkGenerator.CUBE_SURFACE_Y - 7, centerZ + z));
			}
		}

		// Loop to fill the spiral within the cube
		for (double radius = 0; radius <= maxRadius; radius += 0.5) { // Increment radius gradually to avoid gaps
			for (int angleDegrees = 0; angleDegrees < 360; angleDegrees++) { // Full circle
				double angleRadians = Math.toRadians(angleDegrees);

				int x = (int) (radius * Math.cos(angleRadians)); // Convert polar coordinates to Cartesian
				int z = (int) (radius * Math.sin(angleRadians));
				double y = Math.sin(frequency * angleRadians) * amplitude + heightIncrease * radius; // Sinusoidal variation with a vertical
																										// increase

				int blockY = centerY + (int) y;
				addBlockToMap(new Vector(centerX + x, blockY, centerZ + z));
			}
		}
	}

	public double getDifficultyMultilpier() {
		return difficultyMultilpier;
	}

	public void setDifficultyMultilpier(double difficultyMultilpier) {
		this.difficultyMultilpier = difficultyMultilpier;
	}

	public double getRandom() {
		return Math.max(Math.random() * difficultyMultilpier, 0.2);
	}

//	public double getRandomDifficultyMultplier() {
//		return Math.ran
//	}
}
//
//public void generateWaterDropRipple() {
//	int centerX = 0;
//	int centerY = CubeChunkGenerator.CUBE_SURFACE_Y;
//	int centerZ = 0;
//
//	double frequency = 0.7; // Frequency of the ripples
//	double amplitude = 7; // Height of the ripples
//
//	blockPlacementMap.clear(); // Clear previous placements
//
//	for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
//		for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
//			double distance = Math.sqrt(x * x + z * z);
//			double y = Math.sin(distance * frequency) * amplitude;
//
//			int blockY = centerY + (int) y;
//			addBlockToMap(new Vector(centerX + x, blockY, centerZ + z));
//		}
//	}
//}
//
//public void generateStableSoundWave() {
//	int centerX = 0;
//	int centerY = CubeChunkGenerator.CUBE_SURFACE_Y; // The y-level where the pattern is generated
//	int centerZ = 0;
//
//	double amplitude = 8; // Amplitude of the sound wave
//	double frequency = 0.2; // Frequency of the sound waves
//
//	for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
//		for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
//			// Generating a stable wave pattern across the z-axis
//			double y = Math.sin(z * frequency) * amplitude;
//
//			int blockY = centerY + (int) y;
//			Block block = world.getBlockAt(centerX + x, blockY, centerZ + z);
//
//			addBlockToMap(new Vector(centerX + x, blockY, centerZ + z));
//		}
//	}
//}
//
//public void generateSandOnOscillatingSurface() {
//	int centerX = 0;
//	int centerY = CubeChunkGenerator.CUBE_SURFACE_Y; // The y-level where the pattern is generated
//	int centerZ = 0;
//
//	// Parameters for oscillation
//	double frequencyX = 0.5;
//	double frequencyZ = 0.5;
//	double amplitudeX = 4;
//	double amplitudeZ = 4;
//	double phaseShift = Math.PI / 4; // Adding a phase shift to create more complex patterns
//
//	for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
//		for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
//			// Combining two sine waves at different axes
//			double y = Math.sin(x * frequencyX + phaseShift) * amplitudeX + Math.sin(z * frequencyZ) * amplitudeZ;
//
//			int blockY = centerY + (int) y;
//			addBlockToMap(new Vector(centerX + x, blockY, centerZ + z));
//		}
//	}
//}
//
//public void generateCymaticPattern() {
//	int centerX = 0;
//	int centerY = CubeChunkGenerator.CUBE_SURFACE_Y; // The y-level where the pattern is generated
//	int centerZ = 0;
//
//	// Parameters for the pattern
//	double frequency = 0.3; // Lower frequency for more 'wavelength'
//	double amplitude = 7; // Amplitude of the wave patterns
//
//	for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
//		for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
//			// Calculate distance from center to get radial symmetry
//			double distance = Math.sqrt(x * x + z * z);
//
//			// Generating a pattern with radial symmetry
//			double y = Math.sin(distance * frequency) * amplitude;
//			int blockY = centerY + (int) y;
//
//			addBlockToMap(new Vector(centerX + x, blockY, centerZ + z));
//		}
//	}
//}
//
//}
