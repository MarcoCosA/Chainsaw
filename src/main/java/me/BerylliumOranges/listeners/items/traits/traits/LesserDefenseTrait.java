package me.BerylliumOranges.listeners.items.traits.traits;

import java.awt.Color;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import net.md_5.bungee.api.ChatColor;

public class LesserDefenseTrait extends ItemTrait {

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
		// TODO Auto-generated method stub
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Resistance I " + ChatColor.WHITE + ""
				+ ItemBuilder.getTimeInMinutes(getPotionDuration());
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
	public BukkitRunnable potionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				consumer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, getPotionDuration() * 20, 0));
			}
		};
	}

	@Override
	public int getRarity() {
		return 1;
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

	@Override
	public void toolEffect(LivingEntity center) {
		center.setAbsorptionAmount(center.getAbsorptionAmount() + 1);
	}

}
