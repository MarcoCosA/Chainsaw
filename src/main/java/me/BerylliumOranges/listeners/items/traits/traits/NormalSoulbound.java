package me.BerylliumOranges.listeners.items.traits.traits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class NormalSoulbound extends ItemTrait implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;
	public int potionDuration = 60;

	public NormalSoulbound() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Normal Soulbound";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.GRAY;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Keep inventory on death " + ChatColor.WHITE + ItemBuilder.getTimeInMinutes(potionDuration);
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "This item is " + getTraitColor() + "Soulbound";
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
				if (!consumer.isDead()) {
					entitiesWithPotion.remove(consumer);
					alertPlayer(consumer, "Potion ended");
				}
			}
		};
	}

	public HashMap<Player, ItemStack[]> savedItems = new HashMap<Player, ItemStack[]>();

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Bukkit.broadcastMessage("here");
		Player player = event.getEntity();
		ItemStack[] items = player.getInventory().getContents();
		if (entitiesWithPotion.contains(event.getEntity())) {
			event.getDrops().removeAll(Arrays.asList(items));
			savedItems.put(player, items);
			entitiesWithPotion.remove(event.getEntity());
		} else {
			for (ItemStack item : items) {
				if (item != null && (TraitCache.hasItemId(item))) {
					List<ItemTrait> traits = TraitCache.getTraitsFromItem(item);
					if (traits.contains(this)) {
						event.getDrops().remove(item);
						savedItems.put(player, new ItemStack[] { item });
					}
				}
			}
		}
	}

	@EventHandler()
	public void onRespawn(PlayerRespawnEvent event) {
		if (savedItems != null && savedItems.containsKey(event.getPlayer())) {
			if (!event.getPlayer().getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY))
				for (ItemStack stack : savedItems.get(event.getPlayer())) {
					if (stack != null) {
						event.getPlayer().getInventory().addItem(stack);
					}
				}
			savedItems.remove(event.getPlayer());
		}
	}

	@Override
	public int getRarity() {
		return 2;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ANY;
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		return false;
	}

	@Override
	public void toolEffect(LivingEntity center) {
		center.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, getPotionDuration() * 5, 0));
	}
}
