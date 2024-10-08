package me.BerylliumOranges.listeners.items.traits.traits;

import java.awt.Color;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import net.md_5.bungee.api.ChatColor;

public class NormalArmorPenetrationTrait extends ItemTrait {

	private static final long serialVersionUID = -7709915568319277958L;
	public int armorPenetrationP = 1;

	@Override
	public String getTraitName() {
		return getTraitColor() + "Normal Armor Penetration";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.of(new Color(183, 110, 121));
	}

	@Override
	public String getPotionDescription() {
		// TODO Auto-generated method stub
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Strength I " + ChatColor.WHITE + ""
				+ ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Deal " + getTraitColor() + "+" + armorPenetrationP + ChatColor.WHITE + " damage on-hit";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.WEAKNESS;
	}

	@Override
	public void handlePotionEffectStart() {
		consumer.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, getPotionDuration() * 20, 0));
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.WEAPON_EXCLUSIVE;
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof EntityDamageByEntityEvent && !victim) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) op.getEvent();
			e.setDamage(e.getDamage() + armorPenetrationP);
			Bukkit.broadcastMessage("Damaged! " + armorPenetrationP + ", " + e.getDamage());
			return true;
		} else
			Bukkit.broadcastMessage("Ignored");
		return false;
	}
}
