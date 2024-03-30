package me.BerylliumOranges.listeners.items.traits.traits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.customEvents.ItemCombineEvent;
import me.BerylliumOranges.customEvents.KnockbackEvent;
import me.BerylliumOranges.customEvents.TraitAppliedEvent;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import me.BerylliumOranges.misc.EntityUtils;
import net.md_5.bungee.api.ChatColor;

public class CursedAttractionTrait extends ItemTrait implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;

	public int stack_max = 4;
	public int damage = 1;
	public int hits = 0;

	public CursedAttractionTrait() {
		curse = true;
		potionDuration = 150;
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Curse of Attraction";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.DARK_RED;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Mobs are " + getTraitColor() + "pulled " + ChatColor.WHITE + "towards you and " + getTraitColor()
				+ "touching " + ChatColor.WHITE + "mobs damages you " + ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Mobs are slightly " + getTraitColor() + "pulled " + ChatColor.WHITE + "towards you";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.WEAKNESS;
	}

	List<LivingEntity> entitiesWithPotion = new ArrayList<>();

	@Override
	public BukkitRunnable potionRunnable(LivingEntity consumer) {
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
		return ToolOption.ANY;
	}

	@EventHandler
	public void onHit(TickEvent e) {
		for (LivingEntity consumer : entitiesWithPotion) {
			boolean isPlayer = consumer instanceof Player;
			for (Entity ent : consumer.getWorld().getNearbyEntities(consumer.getEyeLocation(), 10, 10, 10)) {
				if (ent.getLocation().distance(consumer.getLocation()) < 10) {
					if (ent instanceof Mob && isPlayer) {
						Vector direction = ent.getLocation().toVector().subtract(consumer.getLocation().toVector()).normalize();
						ent.setVelocity(ent.getVelocity().add(direction.multiply(-0.01)));
					} else if (ent instanceof Player && !isPlayer) {
						Vector direction = ent.getLocation().toVector().subtract(consumer.getLocation().toVector()).normalize();
						ent.setVelocity(ent.getVelocity().add(direction.multiply(-0.01)));
					}
				}
			}
		}
	}

	@EventHandler
	public void onCombine(ItemCombineEvent e) {
		if (e.getItem() != null && e.getItem().hasItemMeta()) {
//			ItemMeta meta = e.getItem().getItemMeta();
//			boolean hasModifier = meta.hasAttributeModifiers() && meta.getAttributeModifiers(Attribute.GENERIC_MOVEMENT_SPEED) != null
//					&& meta.getAttributeModifiers(Attribute.GENERIC_MOVEMENT_SPEED).contains(mod);
//
//			if (e.getTraits().contains(this)) {
//				if (!hasModifier) {
////					meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mod);
//					Bukkit.broadcastMessage("Applied!");
//				}
//			} else {
//				if (hasModifier) {
////					meta.removeAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, mod);
//					Bukkit.broadcastMessage("Removed");
//				}
//			}
//			e.getItem().setItemMeta(meta);
		}
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof TraitAppliedEvent && !victim) {
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
