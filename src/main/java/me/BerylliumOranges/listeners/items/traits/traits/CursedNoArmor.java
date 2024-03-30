package me.BerylliumOranges.listeners.items.traits.traits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class CursedNoArmor extends ItemTrait implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;

	public double maxSpeed = 0.2;

	public CursedNoArmor() {
		curse = true;
		potionDuration = 180;
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Curse of No Armor";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.DARK_RED;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Items periodically drop from your inventory";
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Chance to drop weapon on hotbar change";
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

						if (Math.random() > 0.999 && consumer instanceof Player) { // Check if consumer is a player
							Player player = (Player) consumer;
							ItemStack itemToDrop = player.getInventory().getItem((int) (Math.random() * player.getInventory().getSize()));
							if (itemToDrop != null && itemToDrop.getType() != Material.AIR) {
								player.getWorld().dropItemNaturally(player.getLocation(), itemToDrop);
								player.getInventory().remove(itemToDrop);
								player.getWorld().playSound(player.getLocation(), Sound.ITEM_BUNDLE_DROP_CONTENTS, 0.5F, 1);
							}
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
		return 1;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.WEAPON_EXCLUSIVE;
	}

	@EventHandler
	public void onPlayerMove(PlayerSwapHandItemsEvent e) {
		if (entitiesWithPotion.contains(e.getPlayer()) && TraitCache.getTraitsFromItem(e.getMainHandItem()).contains(this)) {
			e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), e.getMainHandItem());
			e.getPlayer().getInventory().remove(e.getMainHandItem());
			e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ITEM_BUNDLE_DROP_CONTENTS, 0.5F, 1);
		}
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		return false;
	}

	@Override
	public void toolEffect(LivingEntity target) {
		if (target instanceof Player) {
//			
		}
	}

}
