package me.BerylliumOranges.listeners.items.traits.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import com.google.common.collect.Multimap;

import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait.ToolOption;
import me.BerylliumOranges.misc.LoreFormatter;
import net.md_5.bungee.api.ChatColor;

public class ItemBuilder {
	public static ItemStack buildPotionItem(ItemStack item, ItemTrait trait, boolean locked) {
		String uniqueId = TraitCache.getItemIdFromMetadata(item);
		if (uniqueId == null) {
			uniqueId = UUID.randomUUID().toString();
			TraitCache.setItemId(item, uniqueId);
		}
		TraitCache.addTraitsToItem(item, List.of(trait));

		ItemMeta meta = item.getItemMeta();

		List<String> lore = new ArrayList<>();

		String displayName = trait.getTraitName();

		ToolOption toolType = TraitCache.getToolOption(item);
		lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "When Applied to " + toolType.getDescription());
		for (String d : LoreFormatter.formatLore(trait.getToolDescription())) {
			lore.add("  " + d);
		}
		lore.add("");
		lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "When Consumed");
		for (String d : LoreFormatter.formatLore(trait.getPotionDescription())) {
			lore.add("  " + d);
		}

		if (!locked)
			meta.setDisplayName(displayName + ChatColor.RESET + ChatColor.WHITE + " Potion");
		else {
			meta.setDisplayName(displayName + ChatColor.RESET + ChatColor.WHITE + " Potion " + ItemTrait.LOCKED_INDICATOR);
			meta.setLocalizedName(meta.getLocalizedName() + ItemTrait.LOCKED_INDICATOR);
		}
		meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack buildPotionItem(ItemTrait trait, boolean locked) {
		ItemStack item = new ItemStack(Material.POTION);
		PotionMeta pm = (PotionMeta) item.getItemMeta();
		pm.setBasePotionType(trait.getPotionType());
		item.setItemMeta(pm);

		return buildPotionItem(item, trait, locked);
	}

	public static ItemStack buildItem(ItemStack item, List<ItemTrait> traits) {
		TraitCache.addTraitsToItem(item, traits);
		return updateMeta(item);
	}

	public static ItemStack updateMeta(ItemStack item) {

		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> att = meta.getAttributeModifiers();
		List<ItemTrait> traits = new ArrayList<>();
		traits.addAll(TraitCache.getTraitsFromItem(item));

		for (ItemTrait p : traits) {
			lore.add("");
			lore.add(p.getTraitName());
			for (String s : LoreFormatter.formatLore(p.getToolDescription())) {
				lore.add("  " + s);
			}
		}

		if (att != null) {
			lore.add("");
			boolean found = false;
			for (Map.Entry<Attribute, AttributeModifier> a : att.entries()) {
//				if (!found) {
//					lore.add(ChatColor.GRAY + "When on "
//							+ StringUtils.capitalize(StringUtils.replaceChars(a.getValue().getSlot().toString().toLowerCase(), '_', ' '))
//							+ ":");
//					found = true;
//				}
				String add = ChatColor.BLUE + "+";
				ChatColor attColor = getColorFromAtribute(a.getKey());
				if (a.getValue().getAmount() < 0
						|| (a.getValue().getOperation().equals(Operation.MULTIPLY_SCALAR_1) && a.getValue().getAmount() < 1)) {
					add = ChatColor.RED + "";
				}

				String operation = "";
				if (a.getValue().getOperation().equals(AttributeModifier.Operation.ADD_SCALAR)) {
					operation = "%";
				}

				// Your going to have check to see if MULITPLY_SCALAR is %x or
				// something

				lore.add(add + StringUtils.replaceChars("" + a.getValue().getAmount(), ".0", "") + operation + " " + attColor
						+ StringUtils.capitalize(StringUtils.replaceChars(a.getValue().getName(), '_', ' ')));
			}

			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ChatColor getColorFromAtribute(Attribute a) {
		switch (a) {
		case GENERIC_ATTACK_SPEED:
			return ChatColor.GREEN;
		case GENERIC_MAX_HEALTH:
			return ChatColor.RED;
		default:
			return ChatColor.BLUE;
		}
	}

	public static String colorTextFade(String text, Color[] colors) {
		if (colors.length == 0)
			return text;
		String temp = "";
		for (int i = 0; i < text.length(); i++) {
			int index = ((i * (colors.length - 1)) / text.length());
			Color color1 = colors[index % colors.length];
			Color color2 = colors[(index + 1) % colors.length];
			double charsAllowed;

			if (colors.length == 1)
				charsAllowed = text.length();
			else
				charsAllowed = (text.length() / (colors.length - 1));

			if (text.length() % 2 == 1)
				charsAllowed += 1;

			double am = (i % (charsAllowed)) / (charsAllowed - 1);
			temp += ChatColor.of(new Color((int) ((am * color2.getRed()) + ((1 - am) * color1.getRed())),
					((int) ((am * color2.getGreen()) + ((1 - am) * color1.getGreen()))),
					((int) ((am * color2.getBlue()) + ((1 - am) * color1.getBlue()))))) + "" + text.charAt(i);
		}
		return temp;
	}

	public static String getTimeInMinutes(int seconds) {
		String s = "" + seconds % 60;
		if (seconds % 60 < 10)
			s = "0" + seconds % 60;
		return "(" + (seconds / 60) + ":" + s + ")";
	}
}
