package me.BerylliumOranges.dimensions.surfaceeditors;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.main.PluginMain;

public class SurfacePopulator {

	public static void placeCacti(World world, int islandSize) {
		final int totalTicks = 120; // Total time over which to spread out the cactus placement
		final Random random = new Random();

		new BukkitRunnable() {
			private int tick = 0;

			@Override
			public void run() {
				if (tick >= totalTicks) {
					this.cancel(); // Stop the task after the period ends
					return;
				}

				// Calculate number of cacti to place this tick. Gradually decreases the count
				// as ticks progress.
				int cactiThisTick = (int) (Math.sin(Math.PI * tick / totalTicks) * islandSize * 0.06);
				for (int i = 0; i < cactiThisTick; i++) {
					int x = random.nextInt(islandSize * 2) - islandSize;
					int z = random.nextInt(islandSize * 2) - islandSize;
					int y = world.getHighestBlockYAt(x, z) + 1;
					Block surfaceBlock = world.getBlockAt(x, y, z);

					if (canPlaceCactus(surfaceBlock)) {
						surfaceBlock.setType(Material.CACTUS);
						surfaceBlock.getRelative(0, 1, 0).setType(Material.CACTUS);
						surfaceBlock.getRelative(0, 2, 0).setType(Material.CACTUS);
					}
				}
				tick++;
			}
		}.runTaskTimer(PluginMain.getInstance(), 20L, 1L); // Start after 1 second, then run every tick
	}

//	public static void placeCacti(World w, int islandSize) {
//		
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				Bukkit.getScheduler().runTaskLater(PluginMain.getInstance(), new Runnable() {
//					@Override
//					public void run() {
//					//put place cacti in here and make it generate cacti from the center outwards over the period of 6 seconds (120 ticks)
//						//make sure the center of the island doesnt have more cacti than the outside
//					}
//
//				}, 20L);
//
//			}
//		};
//		Random random = new Random();
//		int cactiCount = (int) (islandSize * islandSize * 0.05); // Adjust density as needed
//
//		for (int i = 0; i < cactiCount; i++) {
//			int x = random.nextInt(islandSize * 2) - islandSize;
//			int z = random.nextInt(islandSize * 2) - islandSize;
//			int y = w.getHighestBlockYAt(x, z);
//
//			Block baseBlock = w.getBlockAt(x, y, z);
//			Block surfaceBlock = w.getBlockAt(x, y + 1, z);
//
//			if (surfaceBlock.getType() == Material.AIR
//					&& (baseBlock.getType() == Material.SAND || baseBlock.getType() == Material.RED_SAND)) {
//				int cactusHeight = random.nextInt(3) + 2; // Generates cacti of heights 2 to 4
//
//				// Prefer cacti of height 3
//				if (random.nextFloat() > 0.5) {
//					cactusHeight = 3;
//				}
//
//				for (int j = 0; j < cactusHeight; j++) {
//					Block cactusBlock = w.getBlockAt(x, y + 1 + j, z);
//					if (canPlaceCactus(cactusBlock)) {
//						cactusBlock.setType(Material.CACTUS);
//					} else {
//						break; // Stop building the cactus if we hit a spot we can't build
//					}
//				}
//			}
//		}
//	}

	public static void placeTrees(World w, int islandSize) {
		Random random = new Random();
		int cactiCount = (int) (islandSize * islandSize * 0.04); // Adjust density as needed

		for (int i = 0; i < cactiCount; i++) {
			int x = random.nextInt(islandSize * 2) - islandSize;
			int z = random.nextInt(islandSize * 2) - islandSize;
			int y = w.getHighestBlockYAt(x, z);

			Block baseBlock = w.getBlockAt(x, y, z);
			if (baseBlock.getType().equals(Material.GRASS_BLOCK) || baseBlock.getType().equals(Material.DIRT))
				w.generateTree(new Location(w, x, y + 1, z), TreeType.TREE);
		}
	}

	public static boolean canPlaceCactus(Block block) {
		// Ensure surrounding blocks are empty (no adjacent cacti or other blocks)
		World w = block.getWorld();
		return block.getType() == Material.AIR && w.getBlockAt(block.getX() + 1, block.getY(), block.getZ()).getType() == Material.AIR
				&& w.getBlockAt(block.getX() - 1, block.getY(), block.getZ()).getType() == Material.AIR
				&& w.getBlockAt(block.getX(), block.getY(), block.getZ() + 1).getType() == Material.AIR
				&& w.getBlockAt(block.getX(), block.getY(), block.getZ() - 1).getType() == Material.AIR
				&& (w.getBlockAt(block.getX(), block.getY() - 1, block.getZ()).getType() == Material.SAND
						|| w.getBlockAt(block.getX(), block.getY() - 1, block.getZ()).getType() == Material.RED_SAND
						|| w.getBlockAt(block.getX(), block.getY() - 2, block.getZ()).getType() == Material.SAND
						|| w.getBlockAt(block.getX(), block.getY() - 2, block.getZ()).getType() == Material.RED_SAND
						|| w.getBlockAt(block.getX(), block.getY() - 3, block.getZ()).getType() == Material.SAND
						|| w.getBlockAt(block.getX(), block.getY() - 3, block.getZ()).getType() == Material.RED_SAND
						|| w.getBlockAt(block.getX(), block.getY() - 4, block.getZ()).getType() == Material.SAND
						|| w.getBlockAt(block.getX(), block.getY() - 4, block.getZ()).getType() == Material.RED_SAND);
	}
}
