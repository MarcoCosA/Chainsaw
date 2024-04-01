package me.BerylliumOranges.bosses.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.main.PluginMain;

public class BossesSpawnListener implements Listener {

	public BossesSpawnListener() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@EventHandler
	public void onReload(PluginDisableEvent e) {
		Bukkit.broadcastMessage("Reloading Server...");
	}

	@EventHandler
	public void onDisable(PluginDisableEvent e) {
		if (Boss.class != null && BossUtils.bossInstances != null) {
			for (Boss boss : BossUtils.bossInstances) {
				boss.despawn();
			}
			BossUtils.bossInstances.clear();
		}
	}
}
