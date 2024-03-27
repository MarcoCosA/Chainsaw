package me.BerylliumOranges.bosses.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.misc.LoreFormatter;
import net.md_5.bungee.api.ChatColor;

public class Hazards {
	public enum Hazard {
		NO_LOGOUT(ChatColor.DARK_RED + "No Combat Logging", "Players will be killed if they log out in the boss chamber.",
				Material.BARRIER),

		INVENTORY_DELETE(ChatColor.DARK_RED + "Inventory Delete",
				"Players will lose their entire inventory if they die in the boss chamber.", Material.LAVA_BUCKET),

		TIME_LIMIT_FIVE(ChatColor.DARK_RED + "Time Limit: 5 Minutes", "Players must defeat the boss within 5 minutes, or they will die.",
				Material.CLOCK),

		TIME_LIMIT_THREE(ChatColor.DARK_RED + "Time Limit: 3 Minutes", "Players must defeat the boss within 3 minutes, or they will die.",
				Material.CLOCK, true),

		EXPLODE_ON_DEATH(ChatColor.DARK_RED + "Explode on Death", "All entities explode when they die.", Material.TNT),
		
		CACTUS_DAMAGE(ChatColor.DARK_RED + "Cactus Damage Boost", "Cacti deal 5x damage.", Material.CACTUS),

		;

		private final String name;
		private final String description;
		private final Material type;
		private final ItemStack item;

		Hazard(String name, String description, Material type) {
			this(name, description, type, false);
		}

		Hazard(String name, String description, Material type, boolean enchanted) {
			this.name = name;
			this.description = description;
			this.type = type;

			item = new ItemStack(type);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			meta.setLore(LoreFormatter.formatLore(ChatColor.WHITE + description));
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			if (enchanted)
				item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);

		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public Material getType() {
			return type;
		}

		public ItemStack getItem() {
			return item;
		}
	}
}
