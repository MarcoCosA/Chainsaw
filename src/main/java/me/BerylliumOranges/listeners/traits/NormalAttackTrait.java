package me.BerylliumOranges.listeners.traits;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.purityItems.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.purityItems.traits.utils.TraitOperation;
import net.md_5.bungee.api.ChatColor;

public class NormalAttackTrait extends ItemTrait {

	private static final long serialVersionUID = -7709915568319277958L;

	public int stack_max = 3;
	public int damage = 5;
	public int hits = 0;

	@Override
	public String getTraitName() {
		return getTraitColor() + "Normal Attack";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.RED;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Strength II " + ChatColor.WHITE + ""
				+ ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Every " + getTraitColor() + stack_max + "" + ChatColor.WHITE + " hits this item deals " + getTraitColor()
				+ "+" + damage + ChatColor.WHITE + " damage";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.STRENGTH;
	}

	int time = 0;

	@Override
	public BukkitRunnable potionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				consumer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, potionDuration, 1));
			}
		};
	}

	@Override
	public int getRarity() {
		return 2;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.WEAPON_EXCLUSIVE;
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof EntityDamageByEntityEvent && !victim) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) op.getEvent();
			boolean valid = false;
			if (e.getEntity() instanceof LivingEntity) {
				if (owner instanceof Player) {
					Player player = (Player) e.getDamager();
					if (player.getAttackCooldown() == 1.0) {
						valid = true;
					}
				}
				if (valid) {
					hits++;
					if (hits >= stack_max) {
						hits = 0;
						e.setDamage(e.getDamage() + damage);
						owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5F, 2F);
					}
				}
			}
		}
		return false;
	}

	@Override
	public void toolEffect(LivingEntity center) {
		center.damage(damage);
	}

}
