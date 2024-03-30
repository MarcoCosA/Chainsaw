package me.BerylliumOranges.listeners.items.traits.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class TraitInventoryHolder implements InventoryHolder {
	private final ItemStack item;
	private final Player p;

	public TraitInventoryHolder(ItemStack item, Player p) {
		this.item = item;
		this.p = p;
	}

	@Override
	public Inventory getInventory() {
		return null; // Return the linked inventory here
	}

	public ItemStack getItem() {
		return item;
	}

	public Player getPlayer() {
		return p;
	}

}
