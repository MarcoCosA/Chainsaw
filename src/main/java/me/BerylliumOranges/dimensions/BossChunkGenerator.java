package me.BerylliumOranges.dimensions;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class BossChunkGenerator extends ChunkGenerator {
	private final FastNoiseLite terrainNoise = new FastNoiseLite();
	private final FastNoiseLite detailNoise = new FastNoiseLite();

	public Biome biomeType;
	public List<Material> surfaceMaterials;
	public List<Material> interiorMaterials;
	public int islandSize;

	public BossChunkGenerator(List<Material> surfaceMaterials, List<Material> interiorMaterials, Biome biomeType, int islandSize) {
		this.surfaceMaterials = surfaceMaterials;
		this.interiorMaterials = interiorMaterials;
		this.biomeType = biomeType;
		this.islandSize = islandSize;

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
				biome.setBiome(x, z, biomeType);
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
					double radiusAtY = islandSize * Math.sqrt((double) y / 20);

					// Noise application variables
					float noise2 = (terrainNoise.GetNoise(totalX, totalZ) * 2) + (detailNoise.GetNoise(totalX, totalZ) / 10);
					double taperFactor = Math.max(0, (100 - distanceFromCenter) / 100);
					float maxYAtPosition = (float) (45 + noise2 * 8 * taperFactor);

					float currentY = (float) (45 + noise2 * 8);
					float distanceToSurface = Math.abs(y - currentY);

					if (distanceFromCenter <= radiusAtY && y < maxYAtPosition) {
						if (distanceToSurface < 5) {
							// Set the top block to be one of the surface materials
							chunkData.setBlock(x, y, z, surfaceMaterials.get(random.nextInt(surfaceMaterials.size())));
						} else {
							// Set other blocks as interior materials
							chunkData.setBlock(x, y, z, interiorMaterials.get(random.nextInt(interiorMaterials.size())));
						}
					} else {
						// Set the block to air if it's outside the terrain
						chunkData.setBlock(x, y, z, Material.AIR);
					}
				}
			}
		}
	}

}