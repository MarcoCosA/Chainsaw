package me.BerylliumOranges.listeners.items.traits.globallisteners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait.ToolOption;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import me.BerylliumOranges.listeners.items.traits.utils.TraitInventoryHolder;
import me.BerylliumOranges.main.PluginMain;

public class InventoryListener implements Listener {
	public InventoryListener() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getView() == null)
			return;

		ItemStack item = e.getCurrentItem();
		Inventory inv = getTopInventory(e);

		if (inv.getHolder() instanceof TraitInventoryHolder) {
			TraitInventoryHolder holder = (TraitInventoryHolder) inv.getHolder();
			boolean cancelled = true;
			if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
				if (item.getType().equals(Material.POTION) && TraitCache.hasItemId(item)
						&& !item.getItemMeta().getLocalizedName().contains(ItemTrait.LOCKED_INDICATOR)) {

					if ((isArmor(holder.getItem()) || !TraitCache.hasExclusiveTrait(item, ToolOption.ARMOR_EXCLUSIVE)
							&& (!isArmor(holder.getItem()) || !TraitCache.hasExclusiveTrait(item, ToolOption.WEAPON_EXCLUSIVE))))
						cancelled = false;
				}
			}
			e.setCancelled(cancelled);
		}
	}

	@EventHandler
	public void onShift(PlayerToggleSneakEvent e) {
		if (e.isSneaking()) {
			Player p = e.getPlayer();
			ItemStack item = p.getInventory().getItemInMainHand();
			ItemBuilder.transformDefaultItemToTraitItem(item);
			if (item != null && item.hasItemMeta()) {
				if (isArmor(item)) {
					if (TraitCache.hasItemId(item) && !item.getType().equals(Material.POTION)) {
						p.openInventory(TraitCache.generateItemInventory(item, p));
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onShiftAndInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		Action a = e.getAction();
		EquipmentSlot slot = e.getHand();
		ItemBuilder.transformDefaultItemToTraitItem(item);
		if (item != null && item.hasItemMeta()) {
			if ((slot.equals(EquipmentSlot.HAND)
					&& ((a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) && p.isSneaking())
					|| (isArmor(item) && !p.isSneaking()))) {
				if (TraitCache.hasItemId(item) && !item.getType().equals(Material.POTION)) {
					p.openInventory(TraitCache.generateItemInventory(item, p));
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (e.getInventory().getHolder() instanceof TraitInventoryHolder)
			TraitCache.saveItemInventory(getTopInventory(e));
		p.updateInventory();
	}

	public static boolean isArmor(ItemStack item) {
		if (item == null)
			return false;
		String type = item.getType().toString().toLowerCase();
		if (type.contains("helmet"))
			return true;
		if (type.contains("chestplate"))
			return true;
		if (type.contains("leggings"))
			return true;
		if (type.contains("boots"))
			return true;
		return false;
	}

	public static Inventory getTopInventory(InventoryEvent event) {
		try {
			Object view = event.getView();
			Method getTopInventory = view.getClass().getMethod("getTopInventory");
			getTopInventory.setAccessible(true);
			return (Inventory) getTopInventory.invoke(view);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getInventoryTitle(InventoryEvent event) {
		try {
			Object view = event.getView();
			Method getTopInventory = view.getClass().getMethod("getTitle");
			getTopInventory.setAccessible(true);
			return (String) getTopInventory.invoke(view);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
