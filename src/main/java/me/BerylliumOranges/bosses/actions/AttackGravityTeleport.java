package me.BerylliumOranges.bosses.actions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import me.BerylliumOranges.bosses.utils.BossUtils;

public class AttackGravityTeleport extends BossAction {

	private int phase = 0;
	private int teleportTickCounter = 0;
	private int pauseTickCounter = 0;
	private final int teleportDuration = 20;
	private final int pauseDuration = 10;
	private int repeat = 3;

	public AttackGravityTeleport(LivingEntity source) {
		super(source, 400, 50, 40);
	}

	@Override
	public void tick() {
		super.tick(); // Call the superclass method to maintain the basic ticking functionality.
		switch (phase) {
		case 0:
			// Step 1: Turn off gravity
			source.setGravity(false);
			// Step 2: Teleport a few blocks in the air
			Location newLocation = source.getLocation().add(0, 5, 0);
			source.teleport(newLocation);
			phase++;
			break;
		case 1:
			// Step 3: Look at the nearest player and teleport closer, generating explosions
			if (teleportTickCounter < teleportDuration) {
				LivingEntity target = BossUtils.getNearestEnemy(source, 100); // Assuming 100 blocks is a reasonable search radius.
				if (target != null) {
					Vector direction = target.getLocation().toVector().subtract(source.getLocation().toVector()).normalize();
					Location nextLocation = source.getLocation().add(direction);
					source.teleport(nextLocation);
					World world = source.getWorld();
					Location behind = nextLocation.subtract(direction);
					world.createExplosion(behind, 4F, false, false, source); // Explosion power set to 4F, no fire, no block damage, source
																				// is the entity causing the explosion.
				}
				teleportTickCounter++;
			} else {
				teleportTickCounter = 0;
				phase++;
			}
			break;
		case 2:
			// Step 4: Pause for 10 ticks
			if (pauseTickCounter < pauseDuration) {
				pauseTickCounter++;
			} else {
				pauseTickCounter = 0;
				if (--repeat > 0) {
					phase = 1; // Repeat step 3
				} else {
					phase++;
				}
			}
			break;
		case 3:
			// Step 5: Turn on gravity and teleport to the highest block at its position
			source.setGravity(true);
			Location highestBlock = source.getWorld().getHighestBlockAt(source.getLocation()).getLocation();
			source.teleport(highestBlock.add(0, 1, 0)); // Add 1 to Y to ensure it's on top of the highest block
			phase = 0; // Reset phase to allow the attack to be executed again in the future
			break;
		}
	}

	@Override
	public void execute(LivingEntity target) {
		// This method can be used to apply any direct effects to the target, if needed
	}
}
