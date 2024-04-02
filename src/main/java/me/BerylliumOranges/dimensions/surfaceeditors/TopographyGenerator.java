package me.BerylliumOranges.dimensions.surfaceeditors;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class TopographyGenerator {
	public enum TopographyType {
		WATER_DROP_RIPPLE(generator -> generator.generateWaterDropRipple()),
		STABLE_SOUND_WAVE(generator -> generator.generateStableSoundWave()),
		SAND_ON_OSCILLATING_SURFACE(generator -> generator.generateSandOnOscillatingSurface());

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
		clearTopography();
		selected.generate(this);
		lastTopographyType = selected;
	}

	public void clearTopography() {
		int centerX = 0;
		int centerZ = 0;

		Material resetMaterial = Material.AIR; // Change this to whatever baseline material you want
		for (int y = 50; y < 75; y++)
			for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
				for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
					// Calculate the y position based on your clearing logic
					// For simplicity, we're just resetting the block at centerY to resetMaterial
					Block block = world.getBlockAt(centerX + x, y, centerZ + z);
					block.setType(resetMaterial);
				}
			}
	}

	public void generateWaterDropRipple() {
		int centerX = 0;
		int centerY = 50;
		int centerZ = 0;

		// Parameters for the ripple effect
		double frequency = 0.2; // Frequency of the ripples
		double amplitude = 20; // Height of the ripples

		for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
			for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
				// Calculate distance from center
				double distance = Math.sqrt(x * x + z * z);

				// Calculate y position based on a sine wave
				double y = Math.sin(distance * frequency) * amplitude;

				// Set the block at the calculated position
				int blockY = centerY + (int) y;
				Block block = world.getBlockAt(centerX + x, blockY, centerZ + z);
				block.setType(Material.RED_CONCRETE);
			}
		}
	}

	public void generateStableSoundWave() {
		int centerX = 0;
		int centerY = 50; // The y-level where the pattern is generated
		int centerZ = 0;

		double amplitude = 10; // Amplitude of the sound wave
		double frequency = 0.1; // Frequency of the sound waves

		for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
			for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
				// Generating a stable wave pattern across the z-axis
				double y = Math.sin(z * frequency) * amplitude;

				int blockY = centerY + (int) y;
				Block block = world.getBlockAt(centerX + x, blockY, centerZ + z);
				block.setType(Material.RED_CONCRETE);
			}
		}
	}

	public void generateSandOnOscillatingSurface() {
		int centerX = 0;
		int centerY = 50; // The y-level where the pattern is generated
		int centerZ = 0;

		// Parameters for oscillation
		double frequencyX = 0.2;
		double frequencyZ = 0.15;
		double amplitudeX = 10;
		double amplitudeZ = 5;
		double phaseShift = Math.PI / 4; // Adding a phase shift to create more complex patterns

		for (int x = -cubeSize / 2; x <= cubeSize / 2; x++) {
			for (int z = -cubeSize / 2; z <= cubeSize / 2; z++) {
				// Combining two sine waves at different axes
				double y = Math.sin(x * frequencyX + phaseShift) * amplitudeX + Math.sin(z * frequencyZ) * amplitudeZ;

				int blockY = centerY + (int) y;
				Block block = world.getBlockAt(centerX + x, blockY, centerZ + z);
				block.setType(Material.RED_CONCRETE);
			}
		}
	}
}
