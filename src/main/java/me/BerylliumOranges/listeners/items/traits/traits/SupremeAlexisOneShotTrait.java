package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import net.md_5.bungee.api.ChatColor;

public class SupremeAlexisOneShotTrait extends ItemTrait {

	private static final long serialVersionUID = -7709915568319277958L;

	public SupremeAlexisOneShotTrait() {
		potionDuration = 3600000; // Set this to the new duration you want
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Supreme Alexis Attack Damage";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.DARK_GRAY;
	}

	@Override
	public String getPotionDescription() {
		// TODO Auto-generated method stub
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Strength C " + ChatColor.WHITE
				+ ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "If you aren't Alexis you instantly die upon use";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.INVISIBILITY;
	}

	@Override
	public BukkitRunnable potionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (consumer instanceof Player) {
					Player p = (Player) consumer;
					if (p.getName().equals("dlyxne")) {
						consumer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, getPotionDuration() * 20, 100));
						consumer.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, getPotionDuration() * 20, 100));
						consumer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, getPotionDuration() * 20, 100));
						consumer.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, getPotionDuration() * 20, 100));
					} else {
						p.setHealth(0);
					}
				}

			}
		};
	}

	@Override
	public int getRarity() {
		return 4;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.WEAPON_EXCLUSIVE;
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof EntityDamageByEntityEvent && !victim) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) op.getEvent();
			if (owner instanceof Player) {
				Player p = (Player) owner;
				if (p.getName().equals("dlyxne")) {
					LivingEntity liv = (LivingEntity) e.getEntity();
					liv.setHealth(0);
				} else {
					p.setHealth(0);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void toolEffect(LivingEntity center) {
		center.damage(1);
	}
}
