package me.BerylliumOranges.listeners.items.traits.traits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class CursedPanicTrait extends ItemTrait implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;

	public double maxSpeed = 0.2;

	public CursedPanicTrait() {
		curse = true;
		potionDuration = 150;
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Curse of Panic";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.DARK_RED;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Become blind periodically";
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Chance to move randomly after being hit";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.WEAKNESS;
	}

	List<LivingEntity> entitiesWithPotion = new ArrayList<>();

	// Untested
	public BukkitRunnable potionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				entitiesWithPotion.add(consumer);
				new BukkitRunnable() {
					private int ticksElapsed = 0;

					@Override
					public void run() {
						if (ticksElapsed >= potionDuration * 20) {
							this.cancel();
							alertPlayer(consumer, "Potion effect ended.");
							entitiesWithPotion.remove(consumer);
							return;
						}

						if (Math.random() > 0.994) {
							consumer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
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
	public int getRarity() {
		return 2;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ARMOR_EXCLUSIVE;
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof EntityDamageByEntityEvent && victim && Math.random() > 0.9) {
			toolEffect(owner);
			return true;
		} else
			Bukkit.broadcastMessage("Ignored");
		return false;
	}

	@Override
	public void toolEffect(LivingEntity target) {
		new BukkitRunnable() {
			private int ticksElapsed = 0;
			private final int potionDuration = 15;
			private final Random random = new Random();

			@Override
			public void run() {
				if (ticksElapsed >= potionDuration) {
					this.cancel();
					alertPlayer(target, "Dizziness ended.");
					return;
				}

				if (target instanceof LivingEntity) {
					LivingEntity entity = (LivingEntity) target;
					float yaw = random.nextFloat() * 360 - 180;
					float pitch = random.nextFloat() * 360 - 180;
					Location newDirection = entity.getLocation().clone();
					newDirection.setYaw(yaw);
					newDirection.setPitch(pitch);
					entity.teleport(newDirection);
				}

				ticksElapsed++;
			}
		}.runTaskTimer(PluginMain.getInstance(), 0L, 1L);
	}

}
