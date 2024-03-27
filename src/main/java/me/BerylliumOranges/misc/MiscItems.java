package me.BerylliumOranges.misc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.purityItems.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.traits.ItemTrait;
import me.BerylliumOranges.listeners.traits.LesserHealPower;
import me.BerylliumOranges.listeners.traits.PlaceholderTrait;
import me.BerylliumOranges.main.DirectoryTools;

public class MiscItems {

	public static ItemStack getFreeChestplate() {
		ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
		List<ItemTrait> traits = new ArrayList<>();
		traits.add(new LesserHealPower());
		ItemBuilder.buildItem(item, traits);
		return item;
	}

	public static ItemStack getSword() {
		ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
		List<ItemTrait> traits = new ArrayList<>();
		traits.add(new PlaceholderTrait());
		ItemBuilder.buildItem(item, traits);
		return item;
	}

	public static ArrayList<ItemStack> loadItems() {
		ArrayList<ItemStack> items = new ArrayList<>();
		{
			ItemStack item = new ItemStack(Material.DIAMOND_AXE);
			List<ItemTrait> traits = new ArrayList<>();
			traits.addAll(List.of(new PlaceholderTrait(), new PlaceholderTrait(), new PlaceholderTrait(), new PlaceholderTrait()));
			ItemBuilder.buildItem(item, traits);
			items.add(item);
		}

		return items;
	}

	public static ArrayList<ItemStack> loadPotions() {
		ArrayList<ItemStack> items = new ArrayList<>();
		ArrayList<Class<? extends ItemTrait>> classes = DirectoryTools.getClasses("me.BerylliumOranges.listeners.purityItems.traits",
				ItemTrait.class);

		for (Class<? extends ItemTrait> clazz : classes) {
			try {
				ItemTrait itemTrait = clazz.getDeclaredConstructor().newInstance();
				items.add(ItemBuilder.buildPotionItem(itemTrait, false));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return items;
	}
}
