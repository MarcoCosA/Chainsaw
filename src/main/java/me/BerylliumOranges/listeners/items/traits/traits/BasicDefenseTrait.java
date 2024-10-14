package me.BerylliumOranges.listeners.items.traits.traits;

import java.awt.Color;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import net.md_5.bungee.api.ChatColor;

public class BasicDefenseTrait extends ItemTraitBasic {

	private static final long serialVersionUID = -7709915568319277958L;
	public int damage = 1;

	@Override
	public String getTraitName() {
		return getTraitColor() + "Basic Defense";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.of(new Color(173, 216, 230));
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Resistance I";
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Take " + getTraitColor() + "-" + damage + ChatColor.WHITE + " damage on-hit";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.TURTLE_MASTER;
	}

	@Override
	public PotionEffectType getPotionEffectType() {
		return PotionEffectType.RESISTANCE;
	}

	@Override
	public void handlePotionEffectStart() {
		getConsumer().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, getPotionDuration() * 20, 0));
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ARMOR_EXCLUSIVE;
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof EntityDamageByEntityEvent && victim) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) op.getEvent();
			e.setDamage(e.getDamage() - damage);
			Bukkit.broadcastMessage("Damaged! " + damage + ", " + e.getDamage());
			return true;
		} else
			Bukkit.broadcastMessage("Ignored");
		return false;
	}
}
