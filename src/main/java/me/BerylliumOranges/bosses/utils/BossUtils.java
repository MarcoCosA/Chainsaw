package me.BerylliumOranges.bosses.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.Boss1_Thorns;
import me.BerylliumOranges.bosses.Boss2_Echantment;
import me.BerylliumOranges.bosses.utils.Hazards.Hazard;
import me.BerylliumOranges.main.DirectoryTools;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class BossUtils {
	public static ArrayList<Class<Boss>> bossClasses = BossUtils.loadBossClasses();
	public static ArrayList<Boss> bossInstances = new ArrayList<>();

	protected BossUtils() {
	}

	public enum BossType {
		THORNS(ChatColor.DARK_GREEN + "Thorns Devil", Boss1_Thorns.class, Arrays.asList(Hazard.CACTUS_DAMAGE)),
		ENCHANTMENT(ChatColor.AQUA + "Enchantment Devil", Boss2_Echantment.class, Arrays.asList(Hazard.NO_LOGOUT, Hazard.EXPLODE_ON_DEATH));

		private final String name;
		private final Class<? extends Boss> bossClass;
		private final List<Hazard> hazards;

		BossType(String name, Class<? extends Boss> bossClass, List<Hazard> hazards) {
			this.name = name;
			this.bossClass = bossClass;
			this.hazards = hazards;
		}

		public Class<? extends Boss> getBossClass() {
			return bossClass;
		}

		public List<Hazard> getHazards() {
			return hazards;
		}

		public String getName() {
			return name;
		}

		public static BossType getTypeFromName(String name) {
			for (BossType t : values()) {
				if (t.getName().equals(name))
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
}