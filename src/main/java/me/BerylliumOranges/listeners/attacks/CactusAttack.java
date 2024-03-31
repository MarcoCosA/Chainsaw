package me.BerylliumOranges.listeners.attacks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
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

public class CactusAttack extends BossAttack {

	private List<FallingBlock> cacti = new ArrayList<>();

	public CactusAttack(LivingEntity source) {
		super(source, 200, 10, 5);
	}

	@Override
	public void playAnimation() {
		source.getWorld().playSound(source.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 0.5F, 1F);
	}

	@Override
	public void execute(LivingEntity target) {
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
		for (FallingBlock b : cacti) {
			for (Entity e : b.getNearbyEntities(1, 1, 1)) {
				if (e instanceof LivingEntity && !source.equals(e)) {
					applyDamage((LivingEntity) e);
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
			Block aboveBlock2 = landedBlock.getRelative(0, 2, 0);
			if (aboveBlock1.isEmpty() && aboveBlock2.isEmpty()) {
				// Grow the cactus to 3 blocks tall
				aboveBlock1.setType(Material.CACTUS);
				aboveBlock2.setType(Material.CACTUS);
			}
		}
	}
}
