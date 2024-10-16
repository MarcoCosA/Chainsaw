package me.BerylliumOranges.listeners.items.traits.traits;

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
import net.md_5.bungee.api.ChatColor;

public class BasicAttackSpeedTrait extends ItemTraitBasic implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;
	public UUID id = UUID.randomUUID();
	public int attackSpeedPercentage = 12;
	public AttributeModifier mod = new AttributeModifier(id, "Attack Speed", attackSpeedPercentage / 100.0, Operation.ADD_SCALAR);

	@Override
	public String getTraitName() {
		return getTraitColor() + "Basic Attack Speed";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.DARK_GREEN;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Haste I";
	}

	@Override
	public String getToolDescription() {
		return getTraitColor() + "+" + attackSpeedPercentage + ChatColor.WHITE + "% attack speed";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.LUCK;
	}

	@Override
	public PotionEffectType getPotionEffectType() {
		return PotionEffectType.HASTE;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.WEAPON_EXCLUSIVE;
	}

	@EventHandler
	public void onCombine(ItemCombineEvent e) {
		if (e.getItem() != null && e.getItem().hasItemMeta()) {
			ItemMeta meta = e.getItem().getItemMeta();
			boolean hasModifier = meta.hasAttributeModifiers() && meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_SPEED) != null
					&& meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_SPEED).contains(mod);

			if (e.getTraits().contains(this)) {
				if (!hasModifier) {
					meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, mod);
				}
			} else if (hasModifier) {
				meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, mod);
			}
			e.getItem().setItemMeta(meta);
		}
	}
}
