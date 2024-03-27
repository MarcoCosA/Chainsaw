package me.BerylliumOranges.dimensions.populators;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SurfacePopulator {

	public static void placeCacti(World w, int islandSize) {
		Random random = new Random();
		int cactiCount = (int) (islandSize * islandSize * 0.05); // Adjust density as needed

		for (int i = 0; i < cactiCount; i++) {
			int x = random.nextInt(islandSize * 2) - islandSize;
			int z = random.nextInt(islandSize * 2) - islandSize;
			int y = w.getHighestBlockYAt(x, z);

			Block baseBlock = w.getBlockAt(x, y, z);
			Block surfaceBlock = w.getBlockAt(x, y + 1, z);

			if (surfaceBlock.getType() == Material.AIR && baseBlock.getType() == Material.SAND) {
				int cactusHeight = random.nextInt(3) + 2; // Generates cacti of heights 2 to 4

				// Prefer cacti of height 3
				if (random.nextFloat() > 0.5) {
					cactusHeight = 3;
				}

				for (int j = 0; j < cactusHeight; j++) {
					Block cactusBlock = w.getBlockAt(x, y + j, z);
					if (canPlaceCactus(cactusBlock)) {
						cactusBlock.setType(Material.CACTUS);
					} else {
						break; // Stop building the cactus if we hit a spot we can't build
					}
				}
			}
		}
	}

	private static boolean canPlaceCactus(Block block) {
		// Ensure surrounding blocks are empty (no adjacent cacti or other blocks)
		World w = block.getWorld();
		return block.getType() == Material.AIR && w.getBlockAt(block.getX() + 1, block.getY(), block.getZ()).getType() == Material.AIR
				&& w.getBlockAt(block.getX() - 1, block.getY(), block.getZ()).getType() == Material.AIR
				&& w.getBlockAt(block.getX(), block.getY(), block.getZ() + 1).getType() == Material.AIR
				&& w.getBlockAt(block.getX(), block.getY(), block.getZ() - 1).getType() == Material.AIR
				&& w.getBlockAt(block.getX(), block.getY() - 1, block.getZ()).getType() == Material.SAND;
	}
}
