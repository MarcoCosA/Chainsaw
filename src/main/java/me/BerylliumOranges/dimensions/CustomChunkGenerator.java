package me.BerylliumOranges.dimensions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class CustomChunkGenerator extends ChunkGenerator {
	private final FastNoiseLite terrainNoise = new FastNoiseLite();
	private final FastNoiseLite detailNoise = new FastNoiseLite();
	private final HashMap<Integer, List<Material>> layers = new HashMap<Integer, List<Material>>() {
		private static final long serialVersionUID = 1L;

		{
			put(0, Arrays.asList(Material.SMOOTH_BASALT, Material.BASALT, Material.POLISHED_BASALT, Material.BLACKSTONE));
		}
	};

	public CustomChunkGenerator() {
		// Set frequencies
		terrainNoise.SetFrequency(0.001f);
		detailNoise.SetFrequency(0.05f);

		// Add fractals
		terrainNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
		terrainNoise.SetFractalOctaves(5);
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
		ChunkData chunk = createChunkData(world);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				biome.setBiome(x, z, Biome.THE_END);
			}
		}
		return chunk;
	}

	@Override
	public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {

		for (int y = chunkData.getMinHeight(); y < chunkData.getMaxHeight(); y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					int totalX = chunkX * 16 + x;
					int totalZ = chunkZ * 16 + z;

					// Determine the radial distance from the cone's center axis.
					double distanceFromCenter = Math.sqrt(totalX * totalX + totalZ * totalZ);
					double radiusAtY = 55 * Math.sqrt((double) y / 20);

					// Noise application variables
					float noise2 = (terrainNoise.GetNoise(totalX, totalZ) * 2) + (detailNoise.GetNoise(totalX, totalZ) / 10);
					double taperFactor = Math.max(0, (100 - distanceFromCenter) / 100);
					float maxYAtPosition = (float) (45 + noise2 * 8 * taperFactor);

					if (distanceFromCenter <= radiusAtY && y < maxYAtPosition) {
						chunkData.setBlock(x, y, z, layers.get(0).get(random.nextInt(layers.get(0).size())));
					} else {
						chunkData.setBlock(x, y, z, Material.AIR);
					}

					if (y < 42 - random.nextInt(5) && random.nextFloat() < 0.002) {
						if (distanceFromCenter > radiusAtY - 10 && distanceFromCenter <= radiusAtY) {
							int tendrilLength = random.nextInt(5) + 4;
							for (int tendrilY = y; tendrilY > y - tendrilLength; tendrilY--) {
								chunkData.setBlock(x, tendrilY, z, Material.GLOWSTONE);
							}
						}
					}
				}
			}
		}
	}
}