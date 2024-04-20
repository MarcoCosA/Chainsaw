package me.BerylliumOranges.bosses.Boss02;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.main.PluginMain;

public class LeverPopulator {
	ArrayList<Location> towers = new ArrayList<>();
	ArrayList<LeverEffect> levers = new ArrayList<>();

	public void placeTowers(Location loc) {
		final int totalTicks = 210;
		final int maxHeight = 30;
		final double maxRadius = 30;
		final int interval = totalTicks / (maxHeight - 1);
		final Random random = new Random();

		for (int i = 0; i < LeverEffectType.values().length; i++) {
			double radius = Math.sqrt(random.nextDouble()) * maxRadius;
			double angle = random.nextDouble() * Math.PI * 2;

			// Convert polar coordinates (radius, angle) to Cartesian coordinates (x, z)
			int x = (int) (radius * Math.cos(angle));
			int z = (int) (radius * Math.sin(angle));

			int y = loc.getWorld().getHighestBlockYAt(x, z) + 1;
			Block surfaceBlock = loc.getWorld().getBlockAt(x, y, z);
			towers.add(surfaceBlock.getLocation());
		}

		new BukkitRunnable() {
			private int tick = 0;

			@Override
			public void run() {
				if (tick % interval == 0) {
					if (tick >= totalTicks) {
						BlockFace[] faces = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };
						for (int i = towers.size() - 1; i >= 0; i--) {
							Location t = towers.get(i);
							t.getBlock().getRelative(faces[(int) (faces.length * random.nextDouble())]).setType(Material.LEVER);
						}

						this.cancel(); // Stop the task after the period ends
						return;
					}
				}

				for (int i = towers.size() - 1; i >= 0; i--) {
					Location t = towers.get(i);
					double adjust = ((Math.abs(t.getX()) + Math.abs(t.getZ())) / maxRadius) - 0.05;
					if (random.nextDouble() > adjust) {
						t.getBlock().setType(Material.BLACKSTONE);
						t.add(0, 1, 0);
					}
				}

				tick++;
			}
		}.runTaskTimer(PluginMain.getInstance(), 20L, 1L); // Start after 1 second, then run every tick
	}
}
