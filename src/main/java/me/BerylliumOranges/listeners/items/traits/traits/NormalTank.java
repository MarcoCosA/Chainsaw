package me.BerylliumOranges.listeners.items.traits.traits;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.customEvents.ItemCombineEvent;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class NormalTank extends ItemTrait implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;

	public UUID id = UUID.randomUUID();
	public int resistance = 20;
	public AttributeModifier mod = new AttributeModifier(id, "Knockback Resistance", resistance / 100.0, Operation.ADD_SCALAR);

	@Override
	public String getTraitName() {
		return getTraitColor() + "Normal Tank";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.of(new Color(173, 216, 230));
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Mobs periodically target you " + ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Gain " + getTraitColor() + "+" + resistance + ChatColor.WHITE + "% knockback resistance";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.FIRE_RESISTANCE;
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
						int ticks = 0;
						if (ticks % 50 == 0) {
							List<Entity> nearbyEntities = consumer.getNearbyEntities(40, 40, 40);

							for (Entity entity : nearbyEntities) {
								if (entity.equals(consumer)) {
									continue;
								} else if (entity instanceof Mob && entity.getLocation().distance(consumer.getLocation()) <= 5) {
									Mob mob = (Mob) entity;
									if (mob.getTarget() != null && mob.getTarget().equals(consumer)) {
										if (Math.random() <= 0.15) {
											mob.setTarget(mob);
											Particle.DustOptions orangeDust = new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 85, 0),
													1F);
											mob.getWorld().spawnParticle(Particle.REDSTONE,
													mob.getLocation().clone().add(0, mob.getHeight(), 0), 50, 0.3, 0.3, 0.3, 0, orangeDust);
										}
									}
								}
							}
						}
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
		return ToolOption.ARMOR_EXCLUSIVE;
	}

	@EventHandler
	public void onCombine(ItemCombineEvent e) {
		if (e.getItem() != null && e.getItem().hasItemMeta()) {
			ItemMeta meta = e.getItem().getItemMeta();
			boolean hasModifier = meta.hasAttributeModifiers() && meta.getAttributeModifiers(Attribute.GENERIC_KNOCKBACK_RESISTANCE) != null
					&& meta.getAttributeModifiers(Attribute.GENERIC_KNOCKBACK_RESISTANCE).contains(mod);

			if (e.getTraits().contains(this)) {
				if (!hasModifier) {
					meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, mod);
				}
			} else {
				if (hasModifier) {
					meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, mod);
				}
			}
			e.getItem().setItemMeta(meta);
		}
	}

	// ADD KNOCKBACK RESISTANCE ON EVENTS

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		return false;
	}

	@Override
	public void toolEffect(LivingEntity owner) {
		owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, potionDuration * 5, 0));
	}

}
