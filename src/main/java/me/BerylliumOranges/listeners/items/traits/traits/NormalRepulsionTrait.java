package me.BerylliumOranges.listeners.items.traits.traits;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.BerylliumOranges.customEvents.ItemCombineEvent;
import me.BerylliumOranges.customEvents.KnockbackEvent;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class NormalRepulsionTrait extends ItemTrait implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;

	public int stack_max = 4;
	public int damage = 1;
	public int hits = 0;

	@Override
	public String getTraitName() {
		return getTraitColor() + "Normal Repulsion";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.of(new Color(173, 216, 230));
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Gives " + getTraitColor() + "increased knockback " + ChatColor.WHITE + ""
				+ ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Every " + getTraitColor() + stack_max + "" + ChatColor.WHITE
				+ " hits with this item launches nearby entities away";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.INVISIBILITY;
	}

	List<LivingEntity> entitiesWithPotion = new ArrayList<>();

	@Override
	public BukkitRunnable potionConsume(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				entitiesWithPotion.add(consumer);
				Bukkit.getScheduler().runTaskLater(PluginMain.getInstance(), new Runnable() {
					@Override
					public void run() {
						cancel();
					}

				}, 20L * potionDuration);
			}

			@Override
			public void cancel() {
				alertPlayer(consumer, "Potion ended");
				entitiesWithPotion.remove(consumer);
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

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (entitiesWithPotion.contains(e.getDamager())) {
			Vector direction = e.getEntity().getLocation().toVector().subtract(e.getDamager().getLocation().toVector()).normalize();
			e.getEntity().setVelocity(e.getEntity().getVelocity().add(direction.add(new Vector(0, 0.1, 0)).multiply(0.6)));
		}
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof EntityDamageByEntityEvent && !victim) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) op.getEvent();
			if (owner instanceof Player) {
				Player player = (Player) e.getDamager();
				if (player.getAttackCooldown() == 1.0) {
					checkStacksForToolEffect(owner);
				}
			} else {
				checkStacksForToolEffect(owner);
			}
		}
		return false;
	}

	private void checkStacksForToolEffect(LivingEntity owner) {
		hits++;
		if (hits >= stack_max) {
			toolEffect(owner);
			hits = 0;
		}
	}

	@Override
	public void toolEffect(LivingEntity center) {
		List<Entity> nearbyEntities = center.getNearbyEntities(5, 5, 5);
		center.getWorld().playSound(center.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 0.5F, 2F);

		for (Entity entity : nearbyEntities) {
			if (entity.equals(center)) {
				continue;
			}
			if (entity.getLocation().distance(center.getLocation()) <= 5) {
				Vector direction = entity.getLocation().toVector().subtract(center.getLocation().toVector()).normalize();

				direction = direction.add(new Vector(0, 0.35, 0)).multiply(1.6);

				if (entity instanceof LivingEntity) {
					KnockbackEvent e = new KnockbackEvent(center, (LivingEntity) entity, direction);
					Bukkit.getPluginManager().callEvent(e);
				}

				entity.setVelocity(entity.getVelocity().add(direction));
			}
		}
	}

}
