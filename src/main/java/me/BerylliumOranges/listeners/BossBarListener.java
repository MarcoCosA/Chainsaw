package me.BerylliumOranges.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.server.PluginDisableEvent;

import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.main.PluginMain;

public class BossBarListener implements Listener {
	public ArrayList<LivingEntity> bosses;
	public BossBar bar;
	public BarColor color;
	public int tier;
	public ArrayList<BossBarListener> allBossBars = new ArrayList<>();
	private String overrideName = null;

	public BossBarListener(LivingEntity boss, BarColor color, int tier) {
		ArrayList<LivingEntity> bosses = new ArrayList<>();
		bosses.add(boss);
		makeBossBar(bosses, color, tier);
	}

	public BossBarListener(ArrayList<LivingEntity> bosses, BarColor color, int tier) {
		makeBossBar((ArrayList<LivingEntity>) bosses.clone(), color, tier);
	}

	private void makeBossBar(ArrayList<LivingEntity> bosses, BarColor color, int tier) {
		this.bosses = bosses;
		this.tier = tier;
		this.color = color;
		Bukkit.broadcastMessage("Name is " + bosses.get(0).getCustomName());
		String name = bosses.get(0).getCustomName();
		if (name == null)
			name = bosses.get(0).getName();
		bar = Bukkit.createBossBar(name, color, getSegmentation(bosses), BarFlag.PLAY_BOSS_MUSIC);
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
		allBossBars.add(this);
	}

	public boolean isBossDead() {
		for (LivingEntity l : bosses) {
			if (!l.isDead()) {
				return false;
			}

		}
		return true;
	}

	public static double getBossMaxHP(ArrayList<LivingEntity> bosses) {
		double hp = 0;
		for (LivingEntity l : bosses) {
			hp += l.getMaxHealth();
		}
		return hp;
	}

	public static double getBossCurrentHP(ArrayList<LivingEntity> bosses) {
		double hp = 0;
		for (LivingEntity l : bosses) {
			hp += l.getHealth();
		}
		return hp;
	}

	public static BarStyle getSegmentation(ArrayList<LivingEntity> liv) {
		double hp = getBossMaxHP(liv);
		if (hp > 80)
			return BarStyle.SEGMENTED_10;
		if (hp > 110)
			return BarStyle.SEGMENTED_12;
		if (hp > 160)
			return BarStyle.SEGMENTED_20;
		return BarStyle.SEGMENTED_6;
	}

	@EventHandler
	public void onDisable(PluginDisableEvent e) {
		for (BossBarListener b : allBossBars) {
			b.getBar().removeAll();
		}
		allBossBars.clear();
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (overrideName == null && bosses.contains(e.getEntity())) {
			LivingEntity l = (LivingEntity) e.getEntity();
			String name = l.getCustomName();
			if (name != null)
				bar.setTitle(name);
		}
	}

	@EventHandler
	public void onTick(TickEvent e) {
		bar.setProgress(getBossCurrentHP(bosses) / getBossMaxHP(bosses));
		int mod = 22 + (tier * 3) * (tier * 3);
		outerloop: for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			for (BossBarListener b : allBossBars) {
				boolean found = false;
				for (LivingEntity boss : b.getBosses()) {
					if (boss.isInvulnerable()) {
						bar.setColor(BarColor.BLUE);
						bar.setStyle(BarStyle.SOLID);
						found = true;
						break;
					}
				}
				if (!found) {
					bar.setColor(color);
					BarStyle s = getSegmentation(bosses);
					if (!bar.getStyle().equals(s))
						bar.setStyle(s);
				}
				if (b.getBar().getPlayers().contains(p)) {
					if (b.getTier() > this.getTier()) {
						bar.removePlayer(p);
						break outerloop;
					}
				}
			}
			if (isBossDead()) {
				bar.removeAll();
			} else if (p.getWorld().equals(bosses.get(0).getWorld())
					&& p.getLocation().distanceSquared(bosses.get(0).getLocation()) < mod * mod) {
				if (!bar.getPlayers().contains(p)) {
					bar.addPlayer(p);
				}
			} else {
				bar.removePlayer(p);
			}
		}
	}

	public ArrayList<LivingEntity> getBosses() {
		return bosses;
	}

	public void setBosses(ArrayList<LivingEntity> bosses) {
		this.bosses = bosses;
	}

	public BossBar getBar() {
		return bar;
	}

	public void setBar(BossBar bar) {
		this.bar = bar;
	}

	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public String getOverrideName() {
		return overrideName;
	}

	public void setOverrideName(String overrideName) {
		this.overrideName = overrideName;
		bar.setTitle(overrideName);
		bar.setStyle(getSegmentation(bosses));
	}
}
