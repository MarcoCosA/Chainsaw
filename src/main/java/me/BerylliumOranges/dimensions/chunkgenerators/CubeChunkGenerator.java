package me.BerylliumOranges.dimensions.chunkgenerators;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class CubeChunkGenerator extends ChunkGenerator {
	public Biome biomeType;
	public List<Material> surfaceMaterials;
	public List<Material> interiorMaterials;
	public int islandSize;

	public CubeChunkGenerator(List<Material> surfaceMaterials, List<Material> interiorMaterials, Biome biomeType, int islandSize) {
		this.surfaceMaterials = surfaceMaterials;
		this.interiorMaterials = interiorMaterials;
		this.biomeType = biomeType;
		this.islandSize = islandSize;
	}

	@Override
	public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
		// Calculate the center of the cube in chunk coordinates
		int centerChunkX = (islandSize / 2) / 16;
		int centerChunkZ = (islandSize / 2) / 16;

		// Calculate the maximum distance in chunks from the center to the edge of the
		// cube
		int maxChunkDistance = (int) Math.ceil(islandSize / 16.0 / 2.0);

		// Check if the current chunk is far from the cube
		if (Math.abs(chunkX - centerChunkX) > maxChunkDistance || Math.abs(chunkZ - centerChunkZ) > maxChunkDistance) {
			return; // Skip this chunk as it's too far from the cube
		}

		// Calculate the cube's start and end points in world coordinates
		int centerX = islandSize / 2;
		int centerY = 39; // Arbitrary height for the cube to float
		int centerZ = islandSize / 2;
		int startX = centerX - islandSize / 2;
		int endX = centerX + islandSize / 2;
		int startY = centerY - islandSize / 2;
		int endY = centerY + islandSize / 2;
		int startZ = centerZ - islandSize / 2;
		int endZ = centerZ + islandSize / 2;

		// Loop through each block in the chunk and set it if it's within the cube
		// bounds
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = chunkData.getMinHeight(); y < chunkData.getMaxHeight(); y++) {
					int worldX = chunkX * 16 + x;
					int worldZ = chunkZ * 16 + z;

					// Check if the current block is within the cube bounds
					if (worldX >= startX && worldX <= endX && y >= startY && y <= endY && worldZ >= startZ && worldZ <= endZ) {
						Material blockMaterial = (x == startX || x == endX || y == startY || y == endY || z == startZ || z == endZ)
								? surfaceMaterials.get(random.nextInt(surfaceMaterials.size()))
								: interiorMaterials.get(random.nextInt(interiorMaterials.size()));
						chunkData.setBlock(x, y, z, blockMaterial);
					}
				}
			}
		}
	}
}
