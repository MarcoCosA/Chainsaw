package me.BerylliumOranges.bosses.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.dimensions.surfaceeditors.SinWaveGenerator;
import me.BerylliumOranges.dimensions.surfaceeditors.TopographyGenerator;
import me.BerylliumOranges.main.PluginMain;
import me.BerylliumOranges.misc.LoreFormatter;
import net.md_5.bungee.api.ChatColor;

public class Hazards implements Listener {

	public static final String HAZARD_TAG = "[Hazard]";
	private static FileConfiguration config = null;
	private static File configFile = null;

	public Hazards() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	public static final int DAMAGE_CACTUS = 5;
	public static final int DAMAGE_STAND_ON_GREEN = 30;
	public static final int MOVING_MAP_PERIOD = 5;

	public static final int SPIDER_SPAWN_CHANCE = 10;

	public enum Hazard {
		NO_LOGOUT(ChatColor.DARK_RED + "No Combat Logging", "Players will be killed if they log out in the boss chamber.",
				Material.BARRIER),

		INVENTORY_DELETE(ChatColor.DARK_RED + "Inventory Delete",
				"Players will lose their entire inventory if they die in the boss chamber.", Material.LAVA_BUCKET),

		TIME_LIMIT_FIVE(ChatColor.DARK_RED + "Time Limit: 5 Minutes", "Players must defeat the boss within 5 minutes, or they will die.",
				Material.CLOCK),

		MOVING_MAP(ChatColor.DARK_RED + "Map Moves", "The terrain changes every " + MOVING_MAP_PERIOD + " seconds", Material.RED_CONCRETE),

		STAND_ON_GREEN(ChatColor.DARK_RED + "Stand on Green",
				"Every " + MOVING_MAP_PERIOD + " seconds players will take " + DAMAGE_STAND_ON_GREEN
						+ " damage if the block under them is not green",
				Material.GREEN_CONCRETE),

		NO_BUILDING(ChatColor.DARK_RED + "No Building", "Players cannot place blocks", Material.BRICKS),

		TIME_LIMIT_THREE(ChatColor.DARK_RED + "Time Limit: 3 Minutes", "Players must defeat the boss within 3 minutes, or they will die.",
				Material.CLOCK, true),

		EXPLODE_ON_DEATH(ChatColor.DARK_RED + "Explode on Death", "All entities explode when they die.", Material.TNT),

		SIN_WAVES(ChatColor.DARK_RED + "Sin Waves", "Stand under the sin function that cancels the given one.", Material.STRING),

		SPIDER_SPAWN(ChatColor.DARK_RED + "Spider Spawn",
				"Spiders have a " + SPIDER_SPAWN_CHANCE + "% chance to spawn when coal blocks break.", Material.COAL_BLOCK),

		CACTUS_DAMAGE(ChatColor.DARK_RED + "Cactus Damage Boost", "Cacti deal " + DAMAGE_CACTUS + "x damage.", Material.CACTUS),

		;

		private final String name;
		private final String description;
		private final Material type;
		private final ItemStack item;

		Hazard(String name, String description, Material type) {
			this(name, description, type, false);
		}

		Hazard(String name, String description, Material type, boolean enchanted) {
			this.name = name;
			this.description = description;
			this.type = type;

			item = new ItemStack(type);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			meta.setLore(LoreFormatter.formatLore(ChatColor.WHITE + description));
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			if (enchanted)
				item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public Material getType() {
			return type;
		}

		public ItemStack getItem() {
			return item;
		}
	}

	public static void saveHazards(World world, List<Hazard> hazards) {
		if (config == null) {
			configFile = new File(PluginMain.getInstance().getDataFolder(), "hazards.yml");
			if (!configFile.exists()) {
				configFile.getParentFile().mkdirs();
				try {
					configFile.createNewFile(); // Directly create the file instead
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			config = YamlConfiguration.loadConfiguration(configFile);
		}

		List<String> hazardNames = hazards.stream().map(Enum::name).collect(Collectors.toList());
		config.set("worlds." + world.getName() + ".hazards", hazardNames);
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Hazard> loadHazards(World world) {
		if (config == null) {
			configFile = new File(PluginMain.getInstance().getDataFolder(), "hazards.yml");
			config = YamlConfiguration.loadConfiguration(configFile);
		}
		List<String> hazardNames = config.getStringList("worlds." + world.getName() + ".hazards");
		return hazardNames.stream().map(name -> Hazard.valueOf(name)).collect(Collectors.toList());
	}

	public static boolean hasHazard(World world, Hazard hazard) {
		for (Hazard h : loadHazards(world)) {
			if (h.equals(hazard))
				return true;
		}
		return false;
	}

	int ticks = 0;
	HashMap<World, TopographyGenerator> topGenerators = new HashMap<>();
	HashMap<World, SinWaveGenerator> sinGenerators = new HashMap<>();

	@EventHandler
	public void onTick(TickEvent e) {
		ticks++;
		for (World w : Bukkit.getServer().getWorlds()) {
			if (hasHazard(w, Hazard.STAND_ON_GREEN) && (ticks + 20) % (MOVING_MAP_PERIOD * 20) == 0 && !w.getPlayers().isEmpty()) {
				for (Player p : w.getPlayers()) {
					if (!PlayerStateSaver.playerIsBoss(p)) {
						boolean green = false;
						for (int i = 0; i < 20; i++) {
							Material b = p.getLocation().clone().add(0, -i, 0).getBlock().getType();
							if (b.isSolid()) {
								if (b.toString().toLowerCase().contains("green")) {
									p.playNote(p.getLocation(), Instrument.CHIME, Note.sharp(1, Tone.A));
									green = true;
								}
								break;
							}
						}
						if (!green) {
							p.playNote(p.getLocation(), Instrument.FLUTE, Note.natural(0, Tone.B));
							p.playNote(p.getLocation(), Instrument.FLUTE, Note.natural(0, Tone.C));
							p.damage(DAMAGE_STAND_ON_GREEN);
						}
					}
				}
			}
			if (hasHazard(w, Hazard.MOVING_MAP) && (ticks - 20) % (MOVING_MAP_PERIOD * 20) == 0) {
				if (!topGenerators.containsKey(w))
					topGenerators.put(w, new TopographyGenerator(w, 30));
				topGenerators.get(w).generateRandomTopographyExcludingLast();
			}

			if (hasHazard(w, Hazard.SIN_WAVES) && (ticks - 20) % (15 * 20) == 0) {
				Bukkit.broadcastMessage("generating sin waves");
				if (!sinGenerators.containsKey(w))
					sinGenerators.put(w, new SinWaveGenerator(w, 0.1));
				sinGenerators.get(w).generateWaves();
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (hasHazard(event.getTo().getWorld(), Hazard.MOVING_MAP)) {
			Block block = event.getTo().getBlock();
			if (block.getType().equals(Material.RED_CONCRETE) || block.getType().equals(Material.GREEN_CONCRETE)) {
				event.getPlayer().teleport(event.getTo().add(0, 1, 0));
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (hasHazard(event.getBlock().getWorld(), Hazard.SPIDER_SPAWN)) {
			if (event.getBlock().getType().equals(Material.COAL_BLOCK)) {
				if (Math.random() <= (SPIDER_SPAWN_CHANCE) / 100.0)
					event.getBlock().getWorld().spawn(event.getBlock().getLocation(), Spider.class);
			}
		}
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		World w = e.getPlayer().getWorld();
		if (hasHazard(w, Hazard.NO_LOGOUT))
			e.getPlayer().setHealth(0);
	}

	@EventHandler
	public void onDamage(EntityDamageByBlockEvent e) {
		World w = e.getEntity().getWorld();
		if (e.getDamager() != null && e.getDamager().getType().equals(Material.CACTUS)) {
			if (hasHazard(w, Hazard.CACTUS_DAMAGE))
				e.setDamage(e.getDamage() * DAMAGE_CACTUS);
		}
	}
}
