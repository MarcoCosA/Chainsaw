package me.BerylliumOranges.bosses.Boss02;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.material.Lever;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.main.PluginMain;

public class TowerNode {
	public static final BlockFace[] FACES = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };
	private ArrayList<TowerNode> children = new ArrayList<>();

	private Location location;
	private TowerNode parent;
	private BlockFace facing;
	private LeverEffect leverEffect;
	private int height = 0;
	int totalTicks = 210;
	final Random random;

	public static ArrayList<TowerNode> existingTowers = new ArrayList<>();

	public final Material towerMaterial;
	public final Material peakMaterial;
	public final Material stairsMaterial;

	public final int maxHeight;
	public final double maxRadius;

	public TowerNode(Location loc, LeverEffect leverEffect, Random random, Material towerMaterial, Material peakMaterial,
			Material stairsMaterial, int maxHeight, double maxRadius) {
		this(null, loc, null, leverEffect, random, towerMaterial, peakMaterial, stairsMaterial, maxHeight, maxRadius);
	}

	public TowerNode(TowerNode parent, Location loc, BlockFace facing) {
		this(parent, loc, facing, null, parent.random, parent.towerMaterial, parent.peakMaterial, parent.stairsMaterial, -1, -1);
	}

	public TowerNode(TowerNode parent, Location loc, BlockFace facing, LeverEffect leverEffect, Random random, Material towerMaterial,
			Material peakMaterial, Material stairsMaterial, int maxHeight, double maxRadius) {
		this.parent = parent;
		this.location = loc;
		this.facing = facing;
		this.leverEffect = leverEffect;
		this.random = random;
		this.towerMaterial = towerMaterial;
		this.peakMaterial = peakMaterial;
		this.stairsMaterial = stairsMaterial;
		this.maxHeight = maxHeight;
		this.maxRadius = maxRadius;
		existingTowers.add(this);

		setFoundation();
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public TowerNode getParent() {
		return parent;
	}

	public void setParent(TowerNode parent) {
		this.parent = parent;
	}

	public BlockFace getFacing() {
		return facing;
	}

	public void setFacing(BlockFace facing) {
		this.facing = facing;
	}

	public LeverEffect getLevereffect() {
		return leverEffect;
	}

	public void setLevereffect(LeverEffect leverEffect) {
		this.leverEffect = leverEffect;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public int incrementHeight() {
		return ++height;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public void addChild(TowerNode child) {
		children.add(child);
	}

	public void buildTower() {
		new BukkitRunnable() {
			private int tick = 0;

			@Override
			public void run() {
				if (tick >= totalTicks) {
					placeLeverAndStairs();
					this.cancel(); // Stop the task after placing levers
					return;
				}
				if (tick % (totalTicks / (maxHeight - 1)) == 0) {
					growTower();
				}
				tick++;
			}
		}.runTaskTimer(PluginMain.getInstance(), 10L, 1L); // Start after 1 second, then run every tick

	}

	private void growTower() {
		if (!isRoot() || random.nextDouble() > calculateAdjustment(location)) {
			location.getBlock().setType(towerMaterial);
			location.add(0, 1, 0);
			height++;

			if (height > 2 && random.nextDouble() < 0.30) {
//				if (facing != null) {
//					createChildTower(facing);
//				} else {
					createChildTower(FACES[random.nextInt(FACES.length)]);
//				}
			}

			for (TowerNode child : children) {
				child.growTower();
			}
		}
	}

	private void createChildTower(BlockFace direction) {
		Location supportingLocation = location.getBlock().getRelative(direction).getLocation();
		supportingLocation.setY(0);
		if (!isLocationOccupied(supportingLocation)) {
			supportingLocation.setY(supportingLocation.getWorld().getHighestBlockYAt(supportingLocation));
			children.add(new TowerNode(this, supportingLocation, direction));
		}
	}

	private void setFoundation() {
		for (int y = 0; y < 30; y++)
			location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - y, location.getBlockZ()).setType(towerMaterial);
	}

	private static boolean isLocationOccupied(Location location) {
		for (TowerNode tower : existingTowers) {
			if (tower.getLocation().getWorld().equals(location.getWorld()) && tower.getLocation().getBlockX() == location.getBlockX()
					&& tower.getLocation().getBlockZ() == location.getBlockZ()) {
				return true;
			}
		}
		return false;
	}

	public void placeLeverAndStairs() {
		if (isRoot()) {
			Location leverLocation = location.clone().add(0, -1, 0);
			BlockFace face = FACES[random.nextInt(FACES.length)];
			Block blockToAttach = leverLocation.getBlock().getRelative(face);
			blockToAttach.setType(Material.LEVER);
			BlockState state = blockToAttach.getState();
			Lever lever = (Lever) state.getData();
			lever.setPowered(false);
			lever.setFacingDirection(face);
			state.setData(lever);
			state.update(true);

			leverEffect.apply(blockToAttach.getLocation(), leverLocation.getBlock());
			location.getBlock().setType(peakMaterial);
		} else {
			Block block = location.getBlock();
			block.setType(stairsMaterial);
			BlockData blockData = block.getBlockData();

			if (blockData instanceof Directional) { // Ensure the block data is directional (like stairs)
				((Directional) blockData).setFacing(facing.getOppositeFace());
				block.setBlockData(blockData); // Apply the block data changes
			}
		}
		for (TowerNode child : children) {
			child.placeLeverAndStairs();
		}
	}

	private double calculateAdjustment(Location location) {
		return ((Math.abs(location.getX()) + Math.abs(location.getZ())) / (maxRadius + 20)) - 0.3;
	}
}
