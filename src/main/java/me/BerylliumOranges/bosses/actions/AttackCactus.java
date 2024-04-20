package me.BerylliumOranges.bosses.actions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import me.BerylliumOranges.dimensions.surfaceeditors.SurfacePopulator;

public class AttackCactus extends BossAction {

	private List<FallingBlock> cacti = new ArrayList<>();

	public AttackCactus(LivingEntity source) {
		super(source, 200, 10, 1);
	}

	@Override
	public void playAnimation() {
	}

	@Override
	public void execute(LivingEntity target) {
		source.getWorld().playSound(source.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 0.5F, 1F);

		launchCactusCluster(target, source);
	}

	private void launchCactusCluster(LivingEntity target, LivingEntity source) {
		Location sourceLocation = source.getLocation().add(0, 1, 0); // Adjust the launch height as needed
		Vector direction = target.getLocation().toVector().subtract(sourceLocation.toVector()).normalize();

		int numberOfCacti = 5; // Number of cacti to launch
		for (int i = 0; i < numberOfCacti; i++) {
			launchCactus(sourceLocation, direction, i);
		}
	}

	private void launchCactus(Location location, Vector direction, int index) {
		FallingBlock cactus = location.getWorld().spawnFallingBlock(location, Material.CACTUS.createBlockData());

		Vector randomizedDirection = direction.clone()
				.add(new Vector((Math.random() - 0.5) * 0.2, Math.random() * 0.5, (Math.random() - 0.5) * 0.2));
		cactus.setVelocity(randomizedDirection.multiply(1.5));
		cactus.setDropItem(false);

		location.getWorld().playSound(location, Sound.BLOCK_SWEET_BERRY_BUSH_PLACE, 0.5F, 0.5F);
		cacti.add(cactus);
	}

	@Override
	public void tick() {
		for (int i = cacti.size() - 1; i >= 0; i--) {
			FallingBlock b = cacti.get(i);
			if (!b.isValid()) {
				cacti.remove(i);
				b.remove();
			} else {
				for (Entity e : b.getNearbyEntities(1, 1, 1)) {
					if (e instanceof LivingEntity && !source.equals(e)) {
						applyDamage((LivingEntity) e);
					}
				}
			}
		}
	}

	@EventHandler
	public void onFallingBlockLand(EntityChangeBlockEvent event) {
		int index = cacti.indexOf(event.getEntity());
		if (index >= 0) {
			Block landedBlock = event.getBlock();
			Block aboveBlock1 = landedBlock.getRelative(0, 1, 0);
			if (SurfacePopulator.canPlaceCactus(aboveBlock1)) {
				aboveBlock1.setType(Material.CACTUS);
				aboveBlock1.getRelative(0, 1, 0).setType(Material.CACTUS);
			}
		}
	}
}
