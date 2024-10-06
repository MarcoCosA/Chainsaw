package me.BerylliumOranges.listeners.items.traits.traits;

import java.awt.Color;
import java.util.UUID;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.customEvents.ItemCombineEvent;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class BasicHealthBoost extends ItemTraitBasic implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;
	public UUID id = UUID.randomUUID();
	public int healthBonus = 2;
	public AttributeModifier mod = new AttributeModifier(id, "Max Health", healthBonus, Operation.ADD_NUMBER);

	public BasicHealthBoost() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Basic Health Boost";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.of(new Color(215, 51, 106));
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Health Boost I " + ChatColor.WHITE
				+ ItemBuilder.getTimeInMinutes(potionDuration);
	}

	@Override
	public String getToolDescription() {
		return getTraitColor() + "+" + healthBonus + ChatColor.WHITE + " max health";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.HEALING;
	}

	@Override
	public PotionEffectType getPotionEffectType() {
		return PotionEffectType.HEALTH_BOOST;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ARMOR_EXCLUSIVE;
	}

	@EventHandler
	public void onCombine(ItemCombineEvent e) {
		if (e.getItem() != null && e.getItem().hasItemMeta()) {
			ItemMeta meta = e.getItem().getItemMeta();
			boolean hasModifier = meta.hasAttributeModifiers() && meta.getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH) != null
					&& meta.getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH).contains(mod);

			if (e.getTraits().contains(this)) {
				if (!hasModifier) {
					meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, mod);
				}
			} else if (hasModifier) {
				meta.removeAttributeModifier(Attribute.GENERIC_MAX_HEALTH, mod);
			}
			e.getItem().setItemMeta(meta);
		}
	}
}
