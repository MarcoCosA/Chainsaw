package me.BerylliumOranges.listeners.items.traits.traits;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.customEvents.ItemCombineEvent;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import net.md_5.bungee.api.ChatColor;

public class BasicSpeedTrait extends ItemTraitBasic implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;
	public UUID id = UUID.randomUUID();
	public int speedPercentage = 7;
	public AttributeModifier mod = new AttributeModifier(id, "Speed", speedPercentage / 100.0, Operation.ADD_SCALAR);

	@Override
	public String getTraitName() {
		return getTraitColor() + "Basic Speed";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.BLUE;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Speed I";
	}

	@Override
	public String getToolDescription() {
		return getTraitColor() + "+" + speedPercentage + ChatColor.WHITE + "% movement speed";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.SWIFTNESS;
	}

	@Override
	public PotionEffectType getPotionEffectType() {
		return PotionEffectType.SPEED;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.WEAPON_EXCLUSIVE;
	}

	@EventHandler
	public void onCombine(ItemCombineEvent e) {
		if (e.getItem() != null && e.getItem().hasItemMeta()) {
			ItemMeta meta = e.getItem().getItemMeta();
			boolean hasModifier = meta.hasAttributeModifiers() && meta.getAttributeModifiers(Attribute.GENERIC_MOVEMENT_SPEED) != null
					&& meta.getAttributeModifiers(Attribute.GENERIC_MOVEMENT_SPEED).contains(mod);

			if (e.getTraits().contains(this)) {
				if (!hasModifier) {
					meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mod);
					Bukkit.broadcastMessage("Applied!");
				}
			} else if (hasModifier) {
				meta.removeAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mod);
				Bukkit.broadcastMessage("Removed");
			}
			e.getItem().setItemMeta(meta);
		}
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		return false;
	}

}
