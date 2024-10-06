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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.BerylliumOranges.customEvents.ItemCombineEvent;
import me.BerylliumOranges.customEvents.KnockbackEvent;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class SupremeRepulsionTrait extends ItemTrait implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;

	public int stack_max = 4;
	public int damage = 1;
	public int hits = 0;

	@Override
	public String getTraitName() {
		return getTraitColor() + "Supreme Repulsion";
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

	// Untested
	public BukkitRunnable potionConsume(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.broadcastMessage("ADDED: " + consumer);
				entitiesWithPotion.add(consumer);
				new BukkitRunnable() {
					private int ticksElapsed = 0;

					@Override
					public void run() {
						Bukkit.broadcastMessage("RUNNING: " + consumer);
						if (ticksElapsed >= potionDuration * 20) {
							this.cancel();
							alertPlayer(consumer, "Potion effect ended.");
							entitiesWithPotion.remove(consumer);
							return;
						}

						if (Math.random() > 0.994) {
							for (Entity ent : consumer.getNearbyEntities(5, 5, 5)) {
								Bukkit.broadcastMessage("HERE: " + consumer);
								if (ent.getLocation().distanceSquared(consumer.getLocation()) < 25) {
									Bukkit.broadcastMessage("RUNNINGLAUNCBHING " + consumer);
									ent.setVelocity(ent.getVelocity().clone().add(ent.getLocation().subtract(consumer.getLocation())
											.toVector().normalize().multiply(1.5).add(new Vector(0, 0.5, 0))));
								}
							}
						}
						ticksElapsed++;
					}

					@Override
					public void cancel() {
						super.cancel();
						alertPlayer(consumer, "Potion effect ended.");
						entitiesWithPotion.remove(consumer);
					}
				}.runTaskTimer(PluginMain.getInstance(), 0L, 1L);

				this.cancel();
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
