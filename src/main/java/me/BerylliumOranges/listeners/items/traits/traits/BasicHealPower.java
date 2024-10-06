package me.BerylliumOranges.listeners.items.traits.traits;

import java.awt.Color;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.listeners.items.traits.dummyevents.DummyHealEvent;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import net.md_5.bungee.api.ChatColor;

public class BasicHealPower extends ItemTraitBasic {
	private static final long serialVersionUID = 8917561592248776081L;
	public int healPowerPercentage = 10;

	@Override
	public String getTraitName() {
		return getTraitColor() + "Basic Heal Power";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.of(new Color(255, 192, 203));
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Regeneration I " + ChatColor.WHITE + "for "
				+ ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	@Override
	public String getToolDescription() {
		return getTraitColor() + "+" + healPowerPercentage + ChatColor.WHITE + "% healing from all sources";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.REGENERATION;
	}

	@Override
	public PotionEffectType getPotionEffectType() {
		return PotionEffectType.REGENERATION;
	}
	
	@Override
	public ToolOption getToolOption() {
		return ToolOption.ANY;
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof DummyHealEvent) {
			DummyHealEvent e = (DummyHealEvent) op.getEvent();
			e.getHealEvent().setAmount(e.getHealEvent().getAmount() * (1 + healPowerPercentage / 100.0));
			return true;
		}
		return false;
	}
}
