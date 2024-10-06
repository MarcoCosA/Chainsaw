package me.BerylliumOranges.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.BasicHealPower;
import me.BerylliumOranges.listeners.items.traits.traits.PlaceholderTrait;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.main.DirectoryTools;

public class MiscItems {

	public static ItemStack getFreeChestplate() {
		ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
		List<ItemTrait> traits = new ArrayList<>();
		traits.add(new BasicHealPower());
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
		ArrayList<Class<? extends ItemTrait>> classes = DirectoryTools.getClasses("me.BerylliumOranges.listeners.items.traits.traits",
				ItemTrait.class);
		classes.sort(Comparator.comparing(Class<? extends ItemTrait>::getName)); // Sort items by name
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
