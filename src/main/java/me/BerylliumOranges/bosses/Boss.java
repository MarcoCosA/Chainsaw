package me.BerylliumOranges.bosses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.bosses.utils.Hazards;
import me.BerylliumOranges.bosses.utils.PlayerStateSaver;
import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.main.PluginMain;
import me.BerylliumOranges.misc.EntityUtils;

public abstract class Boss implements Listener {

	public PlayerStateSaver saved = null;
	public ArrayList<LivingEntity> bosses = new ArrayList<>();
	public ArrayList<Player> participants = new ArrayList<>();

	public int ticksAlive = 0;
	public int maxTicksAlive = 7200; // 6 minutes
	public int introAnimationTicks = 0;

	public BossType bossType;
	public String name;
	public int islandSize = 30;
	public World world;

	public Boss(BossType type, ChunkGenerator chunkGenerator) {
		this.bossType = type;
		name = type.getName();
		BossUtils.bossInstances.add(this);
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());

		String cleanName = ChatColor.stripColor(name.toLowerCase()).replaceAll("[^a-z0-9/._-]", ""); // Removes any character not allowed
		WorldCreator creator = new WorldCreator(cleanName);

		creator.generator(chunkGenerator);
		world = Bukkit.getServer().createWorld(creator);
		Hazards.saveHazards(world, bossType.getHazards());

		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Created " + name);

		int highestY = world.getHighestBlockYAt(0, 0);
		Location spawnLocation = new Location(world, 0.5, highestY + 1, 10.5);

		world.setTime(1000);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		bossIntro(spawnLocation);
	}

	public LivingEntity summonBoss(Location loc) {
		LivingEntity boss = BossUtils.getPlayerSubstitute(bossType);
		if (boss != null) {
			PlayerStateSaver.savePlayerState((Player) boss);
		} else
			boss = createDefaultBoss(loc);
		bosses.add(boss);
		equipBoss(boss);
		return boss;
	}

	public abstract List<ItemStack> getDrops();

	public abstract void bossIntro(Location loc);

	public abstract void equipBoss(LivingEntity boss);

	public abstract LivingEntity createDefaultBoss(Location loc);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public World getWorld() {
		return world;
	}

	public int getMaxTicksAlive() {
		return maxTicksAlive;
	}

	public void setMaxTicksAlive(int maxTicksAlive) {
		this.maxTicksAlive = maxTicksAlive;
	}

	public BossType getBossType() {
		return bossType;
	}

	public void setBossType(BossType bossType) {
		this.bossType = bossType;
	}

	public int getIslandSize() {
		return islandSize;
	}

	public void setIslandSize(int islandSize) {
		this.islandSize = islandSize;
	}

	@EventHandler
	public void onFall(TickEvent e) {
		for (LivingEntity boss : bosses) {
			if (boss.getFallDistance() > 30) {
				if (!boss.getWorld().getPlayers().isEmpty()) {
					if (!boss.getWorld().getPlayers().isEmpty()) {
						// Correct way to select a random player

						if (Math.random() > 0.9)
							EntityUtils.teleportEntity(boss, new Location(boss.getWorld(), 0, 90, 0));
						else {
							Player randomPlayer = boss.getWorld().getPlayers()
									.get(new Random().nextInt(boss.getWorld().getPlayers().size()));

							EntityUtils.teleportEntity(boss, randomPlayer.getLocation());
						}
						boss.setFallDistance(0);
					}
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (bosses.contains(e.getEntity()) && (e.getCause().equals(DamageCause.VOID) || e.getCause().equals(DamageCause.FALL)
				|| e.getCause().equals(DamageCause.SUFFOCATION) || e.getCause().equals(DamageCause.THORNS))) {
			e.setCancelled(true);
			if (e.getCause().equals(DamageCause.VOID)) {
				EntityUtils.teleportEntity(e.getEntity(), new Location(e.getEntity().getWorld(), 0, 90, 0));
			}
		}
	}

	public void despawn() {
		for (int i = bosses.size() - 1; i >= 0; i--) {
			bosses.get(i).remove();
		}
		bosses.clear();
	}

}
