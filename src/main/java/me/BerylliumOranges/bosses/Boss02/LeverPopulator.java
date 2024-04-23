package me.BerylliumOranges.bosses.Boss02;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Lever;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.main.PluginMain;

public class LeverPopulator {
	ArrayList<Location> towers = new ArrayList<>();
	ArrayList<LeverEffect> levers = new ArrayList<>();
	int totalTicks = 210;
	int maxHeight = 30;
	double maxRadius = 30;
	final Random random = new Random();

	public void makeTowers(Location loc, int count) {
		for (int i = 0; i < count; i++) {
			double radius = Math.sqrt(random.nextDouble()) * maxRadius;
			double angle = random.nextDouble() * Math.PI * 2;
			int x = (int) (radius * Math.cos(angle));
			int z = (int) (radius * Math.sin(angle));
			int y = loc.getWorld().getHighestBlockYAt(x, z) + 1;
			Block surfaceBlock = loc.getWorld().getBlockAt(x, y, z);
			towers.add(surfaceBlock.getLocation());
		}
	}

	public void addLeverEffect(LeverEffect effect) {
		levers.add(effect);
	}

	public void placeTowersAndLevers() {
		final int interval = totalTicks / (maxHeight - 1);
		new BukkitRunnable() {
			private int tick = 0;

			@Override
			public void run() {
				if (tick >= totalTicks) {
					BlockFace[] faces = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };
					for (int i = 0; i < towers.size(); i++) {
						Location t = towers.get(i);
						BlockFace face = faces[random.nextInt(faces.length)];
						Block blockToAttach = t.getBlock().getRelative(face);
						blockToAttach.setType(Material.LEVER);

						BlockState state = blockToAttach.getState();
						Lever lever = (Lever) state.getData();
						lever.setPowered(false);
						lever.setFacingDirection(face.getOppositeFace());
						state.setData(lever);
						state.update(true);

						// Create or retrieve a lever effect
						LeverEffect effect = levers.size() > i ? levers.get(i) : new SpawnCow();
						effect.apply(blockToAttach.getLocation());
					}
					this.cancel(); // Stop the task after the period ends
					return;
				}
				if (tick % interval == 0) {
					for (int i = 0; i < towers.size(); i++) {
						Location t = towers.get(i);
						double adjust = (Math.abs(t.getX()) + Math.abs(t.getZ())) / (maxRadius + 30);
						if (random.nextDouble() > adjust) {
							t.getBlock().setType(Material.BLACKSTONE);
							t.add(0, 1, 0);
						}
					}
				}
				tick++;
			}
		}.runTaskTimer(PluginMain.getInstance(), 20L, 1L); // Start after 1 second, then run every tick
	}
}
