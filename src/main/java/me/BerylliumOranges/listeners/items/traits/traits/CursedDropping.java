package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import net.md_5.bungee.api.ChatColor;

public class CursedDropping extends ItemTraitCursed implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;

	public double maxSpeed = 0.2;
	public int potionDuration = 60;

	public CursedDropping() {
		super(60);
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Curse of Dropping";
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
	public void handlePotionEffectTick() {
		if (Math.random() > 0.995 && getConsumer() instanceof Player) { // Check if consumer is a player
			Player player = (Player) getConsumer();
			int slot = (int) (Math.random() * player.getInventory().getSize());
			ItemStack item = player.getInventory().getItem(slot);
			if (item != null && item.getType() != Material.AIR) {
				ItemStack itemToDrop = item.clone();
				itemToDrop.setAmount(item.getAmount());
				dropItemFromEntity(player, itemToDrop);
				player.getInventory().setItem(slot, null);
			}
		}
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.WEAPON_EXCLUSIVE;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onHotbarChange(PlayerItemHeldEvent e) {
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItem(e.getNewSlot());
		if (item != null && !item.getType().isAir() && TraitCache.getTraitsFromItem(item).contains(this) && Math.random() > 0.85) {
			ItemStack itemToDrop = item.clone();
			itemToDrop.setAmount(item.getAmount());
			dropItemFromEntity(player, itemToDrop);
			player.getInventory().setItem(e.getNewSlot(), null);
		}
	}

	public static void dropItemFromEntity(LivingEntity ent, ItemStack item) {
		Item i = ent.getWorld().dropItemNaturally(ent.getEyeLocation(), item);
		i.teleport(ent.getEyeLocation().clone().subtract(0, 0.1, 0));
		i.setVelocity(ent.getFacing().getDirection().multiply(0.15));
		ent.getWorld().playSound(ent.getLocation(), Sound.ITEM_BUNDLE_DROP_CONTENTS, 0.5F, 1);
		i.setPickupDelay(40);
	}
}
