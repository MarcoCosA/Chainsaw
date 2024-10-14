package me.BerylliumOranges.listeners.items.traits.traits;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import net.md_5.bungee.api.ChatColor;

public class UniqueSoulbound extends ItemTraitUnique implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;

	public UniqueSoulbound() {
		super(300);
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Soulbound";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.GRAY;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Keep inventory on death";
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "This item is " + getTraitColor() + "Soulbound";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.INVISIBILITY;
	}

	public HashMap<Player, ItemStack[]> savedItems = new HashMap<Player, ItemStack[]>();

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Bukkit.broadcastMessage("here");
		Player player = event.getEntity();
		ItemStack[] items = player.getInventory().getContents();
		if (consumerUUID != null && consumerUUID.equals(event.getEntity())) {
			event.getDrops().removeAll(Arrays.asList(items));
			savedItems.put(player, items);
			potionEffectTicker.stopTimer();
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
			potionEffectTicker.endPotion();
			savedItems.remove(event.getPlayer());
		}
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ANY;
	}
}
