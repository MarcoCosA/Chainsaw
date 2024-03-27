package me.BerylliumOranges.main;

import org.bukkit.Bukkit;

import me.BerylliumOranges.customEvents.BossTickEvent;

public class EveryTick {
	public static void tick() {
		Bukkit.getServer().getPluginManager().callEvent(new BossTickEvent());
	}
}
