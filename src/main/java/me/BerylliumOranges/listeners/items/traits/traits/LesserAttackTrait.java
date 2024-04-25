package me.BerylliumOranges.listeners.items.traits.traits;

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

public class LesserAttackTrait extends ItemTrait {

	private static final long serialVersionUID = -7709915568319277958L;
	public int damage = 1;

	@Override
	public String getTraitName() {
		return getTraitColor() + "Basic Attack Damage";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.RED;
	}

	@Override
	public String getPotionDescription() {
		// TODO Auto-generated method stub
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Strength I " + ChatColor.WHITE + ""
				+ ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Deal " + getTraitColor() + "+" + damage + ChatColor.WHITE + " damage on-hit";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.STRENGTH;
	}

	@Override
	public BukkitRunnable potionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				consumer.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, getPotionDuration() * 20, 0));
			}
		};
	}

	@Override
	public int getRarity() {
		return 1;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.WEAPON_EXCLUSIVE;
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof EntityDamageByEntityEvent && !victim) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) op.getEvent();
			e.setDamage(e.getDamage() + damage);
			Bukkit.broadcastMessage("Damaged! " + damage + ", " + e.getDamage());
			return true;
		} else
			Bukkit.broadcastMessage("Ignored");
		return false;
	}

	@Override
	public void toolEffect(LivingEntity center) {
		center.damage(1);
	}
}
