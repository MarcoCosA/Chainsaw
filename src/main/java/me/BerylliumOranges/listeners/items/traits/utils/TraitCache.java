package me.BerylliumOranges.listeners.items.traits.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.BerylliumOranges.customEvents.ItemCombineEvent;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait.ToolOption;
import me.BerylliumOranges.listeners.items.traits.traits.PlaceholderTrait;
import me.BerylliumOranges.main.PluginMain;

public class TraitCache {
	private static Map<String, List<ItemTrait>> itemTraits = new HashMap<>();

	public static void addTraitsToItem(ItemStack item, List<ItemTrait> traits) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			NamespacedKey key = new NamespacedKey(PluginMain.getInstance(), "trait_item_id");

			String itemId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

			if (itemId == null || itemId.isEmpty()) {
				itemId = UUID.randomUUID().toString();
				meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, itemId);
				item.setItemMeta(meta);
			}

			itemTraits.put(itemId, traits);

			ItemCombineEvent e = new ItemCombineEvent(item, traits);
			Bukkit.getPluginManager().callEvent(e);

			ItemBuilder.updateMeta(item);
		}
	}

	public static List<ItemTrait> getTraitsFromItem(ItemStack item) {
		String itemId = getItemIdFromMetadata(item); // Implement this method to extract the ID
		return itemTraits.getOrDefault(itemId, new ArrayList<>());
	}

	public static LinkedHashMap<ItemStack, List<ItemTrait>> getItemTraitMapFromEntity(LivingEntity liv) {
		LinkedHashMap<ItemStack, List<ItemTrait>> map = new LinkedHashMap<>();

		List<ItemStack> items = new ArrayList<>();
		items.add(liv.getEquipment().getItemInMainHand());
		items.addAll(Arrays.asList(liv.getEquipment().getArmorContents()));

		for (ItemStack item : items) {
			map.put(item, getTraitsFromItem(item));
		}

		return map;
	}

	public static void setItemId(ItemStack item, String id) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			NamespacedKey key = new NamespacedKey(PluginMain.getInstance(), "trait_item_id");
			meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
			item.setItemMeta(meta);
		}
	}

	public static String getItemIdFromMetadata(ItemStack item) {

		if (item != null) {
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				NamespacedKey key = new NamespacedKey(PluginMain.getInstance(), "trait_item_id");
				return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
			}
		}
		return null; // Return null or a default value if the ID is not found
	}

	public static boolean hasTraits(String itemId) {
		return itemTraits.containsKey(itemId) && !itemTraits.get(itemId).isEmpty();
	}

	public static boolean hasExclusiveTrait(ItemStack item, ToolOption op) {
		for (ItemTrait t : TraitCache.getTraitsFromItem(item)) {
			if (t.getToolOption().equals(op))
				return true;
		}
		return false;
	}

	public static ToolOption getToolOption(ItemStack item) {
		boolean weapon = false;
		boolean armor = false;
		for (ItemTrait t : TraitCache.getTraitsFromItem(item)) {
			if (t.getToolOption().equals(ToolOption.WEAPON_EXCLUSIVE))
				weapon = true;
			else if (t.getToolOption().equals(ToolOption.ARMOR_EXCLUSIVE))
				armor = true;
		}
		if (armor && weapon)
			return ToolOption.ANY;
		if (weapon)
			return ToolOption.WEAPON_EXCLUSIVE;
		if (armor)
			return ToolOption.ARMOR_EXCLUSIVE;
		return ToolOption.ANY;
	}

	public static boolean hasItemId(ItemStack item) {
		if (item == null || !item.hasItemMeta()) {
			return false; // No ID can be assigned if there is no item or item meta
		}

		ItemMeta meta = item.getItemMeta();
		NamespacedKey key = new NamespacedKey(PluginMain.getInstance(), "trait_item_id");
		String id = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
		return id != null && !id.isEmpty(); // True if an ID exists and is not empty
	}

	public static Inventory generateItemInventory(ItemStack item, Player p) {
		List<ItemTrait> itemsTraits = getTraitsFromItem(item);
		Inventory inv = Bukkit.createInventory(new TraitInventoryHolder(item, p), ((itemsTraits.size() / 9) + 1) * 9,
				item.getItemMeta().getDisplayName());

		ItemStack placeholder = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		ItemMeta meta = placeholder.getItemMeta();
		meta.setDisplayName(" ");
		meta.setLocalizedName(ItemTrait.LOCKED_INDICATOR);
		placeholder.setItemMeta(meta);

		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, placeholder);
		}

		int index = (9 - itemsTraits.size()) / 2;

		for (int i = 0; i < itemsTraits.size(); i++) {
			if (itemsTraits.size() % 2 == 0 && i % 9 == 4)
				index++;

			if (itemsTraits.get(i) instanceof PlaceholderTrait) {

				inv.setItem(index + i, null);
			} else {
				ItemStack potion = ItemBuilder.buildPotionItem(itemsTraits.get(i), itemsTraits.get(i).isLocked());
				inv.setItem(index + i, potion);
			}
		}
		return inv;

	}

	public static Inventory saveItemInventory(Inventory inv) {
		if (inv.getHolder() instanceof TraitInventoryHolder) {
			TraitInventoryHolder holder = (TraitInventoryHolder) inv.getHolder();
			List<ItemTrait> traits = new ArrayList<>();
			for (ItemStack item : inv.getContents()) {
				if (item != null && item.getType().equals(Material.POTION) && hasItemId(item)) {
					for (ItemTrait traitToAdd : getTraitsFromItem(item)) {
						traits.add(traitToAdd);
					}
				} else if (item == null || (item.hasItemMeta() && item.getType().equals(Material.POTION))) {
					traits.add(new PlaceholderTrait());
				}
			}
			addTraitsToItem(holder.getItem(), traits);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PluginMain.getInstance(), new Runnable() {
				public void run() {
					holder.getPlayer().updateInventory();
				}
			}, 10);
		}
		return inv;
	}

	private static final String TRAITS_FILE_PATH = "plugins/Chainsaw/traits.dat";

	public static void saveTraitsToFile() {
		try {
			// Ensure the directory exists before writing the file
			File file = new File(TRAITS_FILE_PATH);
			File directory = file.getParentFile(); // Get the directory part of the file path
			if (!directory.exists()) {
				directory.mkdirs(); // Make the directory (including any parent directories)
			}

			// Now, safely write the file
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
				Map<String, String> serializedTraits = new HashMap<>();
				for (Map.Entry<String, List<ItemTrait>> entry : itemTraits.entrySet()) {
					List<String> serializedTraitLists = new ArrayList<>();
					for (ItemTrait trait : entry.getValue()) {
						List<String> serializedInnerList = new ArrayList<>();
						serializedInnerList.add(TraitSerializationUtils.serializeTrait(trait));
						serializedTraitLists.add(String.join(", ", serializedInnerList));
					}
					serializedTraits.put(entry.getKey(), String.join("; ", serializedTraitLists));
				}
				oos.writeObject(serializedTraits);
			}
		} catch (IOException e) {
			e.printStackTrace(); // Log the exception
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadTraitsFromFile() {
		File file = new File(TRAITS_FILE_PATH);
		if (file.exists() && file.length() > 0) { // Check that the file is not empty
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
				Map<String, String> serializedTraits = (Map<String, String>) ois.readObject();
				itemTraits.clear(); // Clear existing traits before loading new ones
				for (Map.Entry<String, String> entry : serializedTraits.entrySet()) {
					List<ItemTrait> traitLists = new ArrayList<>();
					String[] serializedLists = entry.getValue().split("; ");
					for (String serializedList : serializedLists) {
						for (String serializedTrait : serializedList.split(", ")) {
							ItemTrait t = TraitSerializationUtils.deserializeTrait(serializedTrait);
							if (t instanceof Listener)
								PluginMain.getInstance().getServer().getPluginManager().registerEvents((Listener) t,
										PluginMain.getInstance());
							traitLists.add(t);

						}
					}
					itemTraits.put(entry.getKey(), traitLists);
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
