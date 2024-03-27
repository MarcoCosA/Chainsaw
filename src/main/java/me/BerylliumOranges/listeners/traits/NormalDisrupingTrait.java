package me.BerylliumOranges.listeners.traits;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.customEvents.BossTickEvent;
import me.BerylliumOranges.listeners.purityItems.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.purityItems.traits.utils.TraitCache;
import me.BerylliumOranges.listeners.purityItems.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class NormalDisrupingTrait extends ItemTrait implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;
	public int stacks_max = 4;
	public int hits = 0;

	public NormalDisrupingTrait() {
		potionDuration = 120;
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Normal Disrupting";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.of(new Color(255, 255, 51));
	}

	@Override
	public String getPotionDescription() {
		// TODO Auto-generated method stub
		return ChatColor.WHITE + "Two random curse traits are temporarily removed from your current items "
				+ ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	@Override
	public String getToolDescription() {
		return "" + getTraitColor() + stacks_max + ChatColor.WHITE + " consecutive hits temporarily disables one of their armor traits";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.FIRE_RESISTANCE;
	}

	@Override
	public BukkitRunnable potionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				int totalTraitsRemoved = 0;
				ItemStack[] items = consumer.getEquipment().getArmorContents();
				for (ItemStack item : items) {
					if (item != null) {
						List<ItemTrait> traits = TraitCache.getTraitsFromItem(item);
						Collections.shuffle(traits);

						// Check and remove up to two cursed traits if they exist
						List<ItemTrait> cursedTraitsToRemove = traits.stream().filter(ItemTrait::isCurse).limit(2)
								.collect(Collectors.toList());

						if (!cursedTraitsToRemove.isEmpty()) {
							traits.removeAll(cursedTraitsToRemove);
							TraitCache.addTraitsToItem(item, traits);
							totalTraitsRemoved += cursedTraitsToRemove.size();

							for (ItemTrait traitRemoved : cursedTraitsToRemove) {
								Bukkit.getScheduler().runTaskLater(PluginMain.getInstance(), () -> {
									List<ItemTrait> newTraits = TraitCache.getTraitsFromItem(item);
									newTraits.add(traitRemoved);
									TraitCache.addTraitsToItem(item, newTraits);
								}, 20L * potionDuration);
							}
						}
					}
					if (totalTraitsRemoved >= 2) {
						break;
					}
				}

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

	int ticks = 0;
	public LivingEntity enemy = null;

	@EventHandler
	public void onTick(BossTickEvent e) {
		if (ticks <= 0) {
			hits = 0;
			enemy = null;
		} else {
			ticks--;
		}
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof EntityDamageByEntityEvent && !victim) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) op.getEvent();
			boolean reset = true;
			if (e.getEntity() != null && e.getEntity().equals(enemy)) {
				if (++hits >= stacks_max) {
					toolEffect(enemy);
				} else {
					ticks = 100;
					reset = false;
				}
			}
			if (reset) {
				hits = 0;
				ticks = 0;
				enemy = null;
			}
			return true;
		}
		return false;
	}

	@Override
	public void toolEffect(LivingEntity center) {
		ItemStack[] items = center.getEquipment().getArmorContents();
		for (ItemStack item : items) {
			if (item != null) {
				List<ItemTrait> traits = TraitCache.getTraitsFromItem(item);
				if (traits.isEmpty()) {

					ItemTrait traitRemoved = traits.remove(0);
					TraitCache.addTraitsToItem(item, traits);

					Bukkit.getScheduler().runTaskLater(PluginMain.getInstance(), new Runnable() {
						@Override
						public void run() {
							List<ItemTrait> newTraits = TraitCache.getTraitsFromItem(item);
							newTraits.add(traitRemoved);
							TraitCache.addTraitsToItem(item, newTraits);
						}
					}, 200L);
				}
			}
		}
	}
}
