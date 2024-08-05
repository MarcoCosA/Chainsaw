package me.BerylliumOranges.bosses.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.Boss01.Boss01_Thorns;
import me.BerylliumOranges.bosses.Boss02.Boss02_Trap;
import me.BerylliumOranges.bosses.Boss04.Boss04_Block;
import me.BerylliumOranges.bosses.Boss09.Boss09_Fire;
import me.BerylliumOranges.bosses.Boss11.Boss11_Explosion;
import me.BerylliumOranges.bosses.utils.Hazards.Hazard;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.NormalArmorPenetrationTrait;
import me.BerylliumOranges.listeners.items.traits.traits.NormalRepulsionTrait;
import me.BerylliumOranges.listeners.items.traits.traits.SupremeRepulsionTrait;
import me.BerylliumOranges.main.DirectoryTools;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class BossUtils {
	public static ArrayList<Class<Boss>> bossClasses = BossUtils.loadBossClasses();
	public static ArrayList<Boss> bossInstances = new ArrayList<>();

	public static HashMap<BossType, ArrayList<Player>> bossTypeAndPlayerOwner = new HashMap<>();

	protected BossUtils() {
	}

	public enum BossType {
		THORNS(ChatColor.DARK_GREEN, "Thorns Devil", Boss01_Thorns.class, Arrays.asList(Hazard.CACTUS_DAMAGE),
				Arrays.asList(SupremeRepulsionTrait.class)),

		TRAP(ChatColor.GRAY, "Trap Devil", Boss02_Trap.class, Arrays.asList(Hazard.NO_LOGOUT, Hazard.SPIDER_SPAWN),
				Arrays.asList(NormalRepulsionTrait.class)),

		BLOCK(ChatColor.AQUA, "Block Devil", Boss04_Block.class,
				Arrays.asList(Hazard.NO_LOGOUT, Hazard.NO_BUILDING, Hazard.STAND_ON_GREEN, Hazard.MOVING_MAP),
				Arrays.asList(NormalRepulsionTrait.class)),

		FIRE(ChatColor.of(new Color(170, 66, 3)), "Fire Devil", Boss09_Fire.class, Arrays.asList(Hazard.NO_LOGOUT),
				Arrays.asList(NormalArmorPenetrationTrait.class)),

		EXPLOSION(ChatColor.RED, "Explosion Devil", Boss11_Explosion.class, Arrays.asList(Hazard.NO_LOGOUT, Hazard.EXPLODE_ON_DEATH),
				Arrays.asList(NormalRepulsionTrait.class));

		private final String name;
		private final ChatColor color;
		private final Class<? extends Boss> bossClass;
		private final List<Hazard> hazards;
		private final List<Class<? extends ItemTrait>> traits;

		BossType(ChatColor color, String name, Class<? extends Boss> bossClass, List<Hazard> hazards,
				List<Class<? extends ItemTrait>> traits) {
			this.name = color + name;
			this.color = color;
			this.bossClass = bossClass;
			this.hazards = hazards;
			this.traits = traits;
		}

		public Class<? extends Boss> getBossClass() {
			return bossClass;
		}

		public List<Hazard> getHazards() {
			return hazards;
		}

		public List<Class<? extends ItemTrait>> getTraits() {
			return traits;
		}

		public String getName() {
			return name;
		}

		public ChatColor getColor() {
			return color;
		}
		
		public static BossType getTypeFromName(String name) {
			for (BossType t : values()) {
				if (ChatColor.stripColor(t.getName()).equals(ChatColor.stripColor(name)))
					return t;
			}
			return null;
		}
	}

	public static Boss getExistingBoss(BossType t) {
		for (Boss b : bossInstances) {
			if (b.getBossType().equals(t)) {
				return b;
			}
		}
		return null;
	}

	public static ArrayList<Class<Boss>> loadBossClasses() {
		ArrayList<Class<Boss>> instances = new ArrayList<>();
		for (Class<?> clazz : DirectoryTools.getClasses("me.BerylliumOranges.bosses")) {
			if (!clazz.equals(Boss.class) && Boss.class.isAssignableFrom(clazz)) {
				try {
					instances.add((Class<Boss>) clazz);
				} catch (Exception er) {
					er.printStackTrace();
				}
			}
		}
		return instances;
	}

	public static Player getRandomNearbyPlayer(Location l) {
		return getRandomNearbyPlayer(l, 30);
	}

	public static Player getRandomNearbyPlayer(Location l, int radius) {
		ArrayList<Player> ps = PluginMain.getNearbyPlayers(l, radius);
		if (ps.isEmpty())
			return null;
		return ps.get((int) (Math.random() * ps.size()));
	}

	public static LivingEntity getNearestEnemy(LivingEntity source, int radius) {
		LivingEntity closest = null;
		double min = Double.MAX_VALUE;
		Location l = source.getEyeLocation();
		if (source instanceof Mob) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.getWorld().equals(l.getWorld())) {
					Location loc = p.getLocation().clone();
					loc.setY(0);
					double d = loc.distanceSquared(l);
					if (min > d) {
						min = d;
						closest = p;
					}
				}
			}
		} else {
			for (Entity p : l.getWorld().getEntities()) {
				if ((p instanceof Mob || p instanceof Player) && p.getWorld().equals(l.getWorld())) {
					Location loc = p.getLocation().clone();
					loc.setY(0);
					double d = loc.distanceSquared(l);
					if (min > d) {
						min = d;
						closest = (LivingEntity) p;
					}
				}
			}
		}
		if (min > radius * radius) {
			return null;
		}
		return closest;
	}

	/**
	 * Use 'getNearestEnemy()' when possible
	 * 
	 * @param l
	 * @param radius
	 * @return
	 */
	public static Player getNearestPlayer(Location l, int radius) {
		Player closest = null;
		double min = Double.MAX_VALUE;
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (p.getWorld().equals(l.getWorld())) {
				double d = p.getLocation().distanceSquared(l);
				if (min > d) {
					min = d;
					closest = p;
				}
			}
		}
		if (min > radius * radius) {
			return null;
		}
		return closest;
	}

	public static void distrbuteDrops(List<ItemStack> drops, ArrayList<Player> participants, int xp) {
		if (participants.isEmpty())
			return;
		double multiplier = Math.pow(participants.size(), -0.2);
		for (Player p : participants) {
			for (ItemStack item : drops) {
				double temp = multiplier;

				ItemStack copy = item.clone();
				copy.setAmount((int) ((1 + copy.getAmount()) * (Math.random() * temp)));

				p.getInventory().addItem(copy);
			}
			p.giveExp(xp);
		}
	}

	public static Player getPlayerSubstitute(BossType bossType) {
		ArrayList<Player> players = BossUtils.bossTypeAndPlayerOwner.get(bossType);
		if (players != null && !players.isEmpty()) {

			for (Player p : players) {
				if (p.isOnline()) {
					return p;
				}
			}
		}
		return null;
	}
}