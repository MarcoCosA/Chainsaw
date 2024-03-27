package me.BerylliumOranges.bosses;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.main.PluginMain;

public abstract class Boss implements Listener {
	public ArrayList<LivingEntity> bosses = new ArrayList<>();
	public ArrayList<Player> participants = new ArrayList<>();

	public int ticksAlive = 0;
	public int maxTicksAlive = 7200; // 6 minutes

	public BossType bossType;
	public String name;
	public int islandSize = 30;
	public World world;

	public Boss(BossType type, ChunkGenerator chunkGenerator) {
		this.bossType = type;
		name = type.getName();
		BossUtils.bossInstances.add(this);
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());

		String cleanName = name.replaceAll("[^a-z0-9/._-]", ""); // Removes any character not allowed
		WorldCreator creator = new WorldCreator(cleanName);

		creator.generator(chunkGenerator);
		world = Bukkit.getServer().createWorld(creator);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Created " + name);
	}

	public abstract List<ItemStack> getDrops();

	public abstract void startFight(Location loc);

	public abstract LivingEntity spawnBoss(Location loc);

	public abstract void despawn();

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

}
