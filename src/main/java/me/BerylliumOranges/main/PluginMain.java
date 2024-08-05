package me.BerylliumOranges.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.bosses.utils.BossesSpawnListener;
import me.BerylliumOranges.bosses.utils.GlobalBossListener;
import me.BerylliumOranges.bosses.utils.Hazards;
import me.BerylliumOranges.bosses.utils.Hazards.Hazard;
import me.BerylliumOranges.bosses.utils.HazardsChestGenerator;
import me.BerylliumOranges.dimensions.CustomChunkGenerator;
import me.BerylliumOranges.listeners.ItemsAndTradesListener;
import me.BerylliumOranges.listeners.items.traits.globallisteners.GlobalTraitListener;
import me.BerylliumOranges.listeners.items.traits.globallisteners.InventoryListener;
import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;

public class PluginMain extends JavaPlugin implements Listener {
	public static BossesSpawnListener pl;
	private static PluginMain instance;

	private static final String ITEM_DATA_KEY = "custom_item_data";
	public static final String DIMENSION_1_NAME = "dimension1";
	private NamespacedKey dataKey;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;
		if (getServer().getWorld(DIMENSION_1_NAME) == null) {
			WorldCreator creator = new WorldCreator(DIMENSION_1_NAME);
			creator.generator(new CustomChunkGenerator());
			World w = Bukkit.getServer().createWorld(creator);
			HazardsChestGenerator.placeChests(w);
			Hazards.saveHazards(w, Arrays.asList(Hazard.IS_BOSS_WORLD));
			w.setSpawnLocation(0, w.getHighestBlockYAt(0, 0), 0);
			Boss.createEndPortal(w.getSpawnLocation().clone().add(-4, 0, 0), Material.BELL);
		}

		// This is just here to ensure the BossAbstract class is loaded first
		BossUtils.loadBossClasses();
		TraitCache.loadTraitsFromFile();

		// load listeners
		new GlobalTraitListener();
		new GlobalBossListener();
		new InventoryListener();
		new ItemsAndTradesListener(); // Testing only
		new Hazards();

		dataKey = new NamespacedKey(this, ITEM_DATA_KEY);
		getServer().getPluginManager().registerEvents(this, this);

		try {

			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				@Override
				public void run() {

					EveryTick.tick();
				}
			}, 0, 0);
		} catch (Exception er) {

		}

	}

	@Override
	public void onDisable() {
		TraitCache.saveTraitsToFile();
		for (Boss b : BossUtils.bossInstances) {
			b.despawn();
		}
	}

	public static PluginMain getInstance() {
		return instance;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandParser.findCommand(sender, cmd, label, args);
	}

	public static int generateRand(int max, int min) {
		Random rand = new Random();
		int n = rand.nextInt(max) + min;
		return n;
	}

	public static Entity[] getNearbyEntities(Location l, double radius) {
		double chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		HashSet<Entity> radiusEntities = new HashSet<>();

		for (double chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
			for (double chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
				int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
				for (Entity e : new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
					if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
						radiusEntities.add(e);
				}
			}
		}

		return radiusEntities.toArray(new Entity[radiusEntities.size()]);
	}

	public static ArrayList<Player> getNearbyPlayers(Location l, double radius) {
		ArrayList<Player> ps = new ArrayList<>();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Location loc = p.getLocation().clone();
			loc.setY(0);
			if (p.getWorld().equals(l.getWorld()) && loc.distanceSquared(l) < radius * radius) {
				ps.add(p);
			}
		}
		return ps;
	}
}
