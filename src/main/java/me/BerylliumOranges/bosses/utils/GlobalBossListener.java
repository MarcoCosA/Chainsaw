package me.BerylliumOranges.bosses.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class GlobalBossListener implements Listener {

	public GlobalBossListener() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getView().getTitle().contains(HazardsChestGenerator.DUNGEON_TAG)) {
			e.setCancelled(true);

			BossType type = BossType.getTypeFromName(e.getView().getTitle().split(HazardsChestGenerator.DUNGEON_TAG)[0]);

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

}
