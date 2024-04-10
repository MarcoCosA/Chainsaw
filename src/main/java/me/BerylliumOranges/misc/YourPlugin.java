package me.BerylliumOranges.misc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.main.PluginMain;

import java.io.File;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.nio.file.Paths;

public class YourPlugin {
	private long lastModifiedTime = 0;

	public void onEnable() {
		// Initialize lastModifiedTime with the current last modified time of the plugin
		// file
		updateLastModifiedTime();

		// Schedule the check for updates task to run every 5 minutes (6000 ticks)
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					File file = new File(YourPlugin.this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
					BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

					long currentModifiedTime = attrs.lastModifiedTime().toMillis();
					if (currentModifiedTime > lastModifiedTime) {
						// File has been updated
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "reload");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.runTaskTimer(PluginMain.getInstance(), 0, 6000L); // 5 minutes
	}

	private void updateLastModifiedTime() {
		try {
			File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			BasicFileAttributes attrs = Files.readAttributes(Paths.get(file.getPath()), BasicFileAttributes.class);
			lastModifiedTime = attrs.lastModifiedTime().toMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
