package me.BerylliumOranges.bosses.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.bosses.utils.Hazards.Hazard;
import me.BerylliumOranges.listeners.items.traits.globallisteners.InventoryListener;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class GlobalBossListener implements Listener {

	public GlobalBossListener() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		String title = InventoryListener.getInventoryTitle(e);
		if (title.contains(HazardsChestGenerator.DUNGEON_TAG)) {
			e.setCancelled(true);

			BossType type = BossType.getTypeFromName(title.split(HazardsChestGenerator.DUNGEON_TAG)[0]);

			if (e.getCurrentItem() != null && type != null && (e.getCurrentItem().equals(HazardInventoryGenerator.ENTER_DUNGEON_ITEM)
					|| e.getCurrentItem().equals(HazardInventoryGenerator.CREATE_DUNGEON_ITEM))) {
				Player player = (Player) e.getWhoClicked();

				player.closeInventory();
				Boss boss = BossUtils.getExistingBoss(type);
				if (boss == null) {
					try {
						player.sendMessage(ChatColor.GRAY + "Preparing " + type.getName() + ChatColor.GRAY + "'s dungeon...");
						boss = type.getBossClass().getDeclaredConstructor().newInstance();

						e.getInventory().setItem(e.getSlot(), HazardInventoryGenerator.ENTER_DUNGEON_ITEM);
						player.sendMessage(ChatColor.GRAY + "Dungeon is ready.");
					} catch (ReflectiveOperationException roe) {
						player.sendMessage(ChatColor.RED + "An error occurred while entering the dungeon.");
						roe.printStackTrace();
						return;
					}
				} else {
					player.sendMessage(ChatColor.GREEN + "Entering " + type.getName() + ChatColor.GREEN + "'s dungeon.");
					World bossWorld = boss.getWorld();
					int highestY = bossWorld.getHighestBlockYAt(0, 0);
					Location teleportLocation = new Location(bossWorld, 0.5, highestY + 1, 0.5);
					player.teleport(teleportLocation);
				}

			}
		}
	}

	@EventHandler
	public void onPortal(EntityPortalEvent e) {
		if (Hazards.hasHazard(e.getEntity().getWorld(), Hazard.IS_BOSS_WORLD)) {
			e.setTo(Bukkit.getWorlds().get(0).getSpawnLocation());
		}
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent e) {
		if (Hazards.hasHazard(e.getPlayer().getWorld(), Hazard.IS_BOSS_WORLD)) {
			e.setTo(e.getPlayer().getRespawnLocation() != null ? e.getPlayer().getRespawnLocation()
					: Bukkit.getWorlds().get(0).getSpawnLocation());
		}
	}
}
