package me.BerylliumOranges.listeners.items.traits.traits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class CursedFrictionTrait extends ItemTraitCursed implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;

	public double maxSpeed = 0.2;

	public CursedFrictionTrait() {
		potionDuration = 150;
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Curse of Friction";
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Take damage when you move too fast";
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Your velocity is capped";
	}

	List<LivingEntity> entitiesWithPotion = new ArrayList<>();

	// Untested
	public BukkitRunnable potionConsume(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				entitiesWithPotion.add(consumer);
				new BukkitRunnable() {
					private int ticksElapsed = 0;

					@Override
					public void run() {
						if (ticksElapsed >= potionDuration) {
							this.cancel();
							alertPlayer(consumer, "Potion effect ended.");
							entitiesWithPotion.remove(consumer);
							return;
						}

						double speed = consumer.getVelocity().length();
						if (speed > maxSpeed) {
							consumer.setVelocity(consumer.getVelocity().normalize().multiply(maxSpeed));
						}

						ticksElapsed++;
					}

					@Override
					public void cancel() {
						super.cancel();
						alertPlayer(consumer, "Potion effect ended.");
						entitiesWithPotion.remove(consumer);
					}
				}.runTaskTimer(PluginMain.getInstance(), 0L, 1L);

				this.cancel();
			}
		};
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ANY;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
//		Player player = event.getPlayer();
//		// Get the player's current speed
//		double speed = player.getVelocity().length();
//
//		// If the player's speed exceeds the maximum, scale the velocity to cap it at
//		// the maximum speed
//		if (speed > maxSpeed) {
//			// Scale the velocity to match the maximum speed
//			player.setVelocity(player.getVelocity().normalize().multiply(maxSpeed));
//		}
	}

	public void toolEffect(LivingEntity center) {
		new BukkitRunnable() {
			private final long startTime = System.currentTimeMillis(); // Store the start time

			@Override
			public void run() {
				// Check if 10 seconds have passed
				if (System.currentTimeMillis() >= startTime + 10000) {
					this.cancel(); // End the Runnable after 10 seconds
					return;
				}

				double speed = center.getVelocity().length();
				// If the player's speed exceeds the maximum, scale the velocity
				if (speed > maxSpeed) {
					center.setVelocity(center.getVelocity().normalize().multiply(maxSpeed));
				}
			}

		}.runTaskTimer(PluginMain.getInstance(), 0L, 1L);
	}

}
