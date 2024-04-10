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
	public static final int CUBE_SURFACE_Y = 50;

	public CubeChunkGenerator(List<Material> surfaceMaterials, List<Material> interiorMaterials, Biome biomeType, int islandSize) {
		this.surfaceMaterials = surfaceMaterials;
		this.interiorMaterials = interiorMaterials;
		this.biomeType = biomeType;
		this.islandSize = islandSize;
	}

	@Override
	public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
		if ((Math.abs(chunkX) + Math.abs(chunkZ) + 1) * 16 > islandSize * islandSize)
			return;

		int halfIslandLength = islandSize / 2;
		for (int y = CUBE_SURFACE_Y - islandSize; y < CUBE_SURFACE_Y; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					int totalX = chunkX * 16 + x;
					int totalZ = chunkZ * 16 + z;
					if (totalX > -halfIslandLength && totalX < halfIslandLength) {
						if (totalZ > -halfIslandLength && totalZ < halfIslandLength) {
							chunkData.setBlock(x, y, z, surfaceMaterials.get(random.nextInt(surfaceMaterials.size())));
						}
					}
				}
			}
		}
	}
}
