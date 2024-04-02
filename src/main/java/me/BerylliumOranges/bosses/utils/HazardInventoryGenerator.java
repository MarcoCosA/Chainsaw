package me.BerylliumOranges.bosses.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import net.md_5.bungee.api.ChatColor;

public class HazardInventoryGenerator implements InventoryHolder {
	BossType bossType;
	Inventory inventory = null;

	public HazardInventoryGenerator(Inventory inventory, BossType bossType) {
		this.inventory = inventory;
		this.bossType = bossType;

		ItemStack placeholder = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		ItemMeta meta = placeholder.getItemMeta();
		meta.setDisplayName(" ");
		placeholder.setItemMeta(meta);

		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, placeholder);
		}

		int index = (9 - bossType.getHazards().size()) / 2;

		for (int i = 0; i < bossType.getHazards().size(); i++) {
			if (bossType.getHazards().size() % 2 == 0 && i % 9 == 4)
				index++;
			inventory.setItem(index + i, bossType.getHazards().get(i).getItem());
		}

		inventory.setItem(26, CREATE_DUNGEON_ITEM);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public static final ItemStack CREATE_DUNGEON_ITEM = getInventoryCreateItem();
	public static final ItemStack ENTER_DUNGEON_ITEM = getInventoryStartItem();

	private static ItemStack getInventoryCreateItem() {
		ItemStack item = new ItemStack(Material.GRAY_WOOL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Prepare Dungeon");
		item.setItemMeta(meta);
		return item;
	}

	private static ItemStack getInventoryStartItem() {
		ItemStack item = new ItemStack(Material.GREEN_WOOL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Enter Dungeon");
		item.setItemMeta(meta);
		return item;
	}
}
