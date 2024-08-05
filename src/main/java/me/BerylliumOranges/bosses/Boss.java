package me.BerylliumOranges.bosses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.BerylliumOranges.bosses.utils.BossBarListener;
import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.bosses.utils.Hazards;
import me.BerylliumOranges.bosses.utils.Hazards.Hazard;
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
	protected int stage = 0;

	public BossType bossType;
	public String name;
	public int islandSize = 30;
	public World world;
	public BossBarListener bossBar;

	public Boss(BossType type, ChunkGenerator chunkGenerator) {
		this.bossType = type;
		name = type.getName();
		BossUtils.bossInstances.add(this);
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());

		String cleanName = ChatColor.stripColor(name.toLowerCase()).replaceAll("[^a-z0-9/._-]", ""); // Removes any character not allowed
		WorldCreator creator = new WorldCreator(cleanName);

		creator.generator(chunkGenerator);
		world = Bukkit.getServer().createWorld(creator);
		List<Hazard> hazards = new ArrayList<>(bossType.getHazards());
		hazards.add(Hazard.IS_BOSS_WORLD);
		Hazards.saveHazards(world, hazards);

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

	public boolean dead = false;

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if (bosses.contains(e.getEntity())) {
			boolean found = false;
			for (LivingEntity b : bosses) {
				if (!b.equals(e.getEntity()) && !b.isDead()) {
					found = true;
					break;
				}
			}
			dead = !found;
			if (dead)
				doDeathAnimation(e.getEntity());
		}
	}

	public static void startWorldRemoval(World w) {
		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if (count < 200) {

				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(PluginMain.getInstance(), 40L, 1L); // Starts after 2 seconds, repeats every tick
	}

	public void doDeathAnimation(LivingEntity lastAlive) {
		World world = lastAlive.getWorld();
		Location center = lastAlive.getLocation();
		world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
		world.spawnParticle(Particle.EXPLOSION, center, 20, 0.5, 0.5, 0.5, 0.1);
		double radius = 10.0;
		for (Entity entity : world.getNearbyEntities(center, radius, radius, radius)) {
			if (entity instanceof LivingEntity && entity != lastAlive) {
				LivingEntity target = (LivingEntity) entity;
				Location targetLocation = target.getLocation();
				Vector direction = targetLocation.toVector().subtract(center.toVector()).normalize();
				double distance = center.distance(targetLocation);
				double knockbackIntensity = 1.0 - (distance / radius);
				target.setVelocity(direction.multiply(knockbackIntensity * 2));
			}
		}

		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if (count < 200) {
					double radius = 4.0;
					double u = Math.random();
					double v = Math.random();
					double theta = 2 * Math.PI * u;
					double phi = Math.acos(2 * v - 1);
					double x = radius * Math.sin(phi) * Math.cos(theta);
					double y = radius * Math.sin(phi) * Math.sin(theta);
					double z = radius * Math.cos(phi);
					Location randomLocation = center.clone().add(x, y, z);

					// Spawn 1 cherry leaf particle at the generated location
					world.spawnParticle(Particle.CHERRY_LEAVES, randomLocation, 1);
					if (count == 0)
						world.playSound(center, Sound.ITEM_GOAT_HORN_SOUND_6, 1f, 0f);
					if (count == 150)
						world.playSound(center, Sound.ITEM_GOAT_HORN_SOUND_7, 0.3f, 2f);
					count++;
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(PluginMain.getInstance(), 40L, 1L); // Starts after 2 seconds, repeats every tick

		int blockRadius = 5;
		new BukkitRunnable() {
			int step = 0; // This represents the current radius being filled
			double radius = blockRadius; // Maximum radius of the sphere

			@Override
			public void run() {
				if (step <= radius) {
					for (int x = -step; x <= step; x++) {
						for (int z = -step; z <= step; z++) {
							for (int y = -step; y <= step; y++) {
								// Check if the block is within a spherical radius
								if (x * x + y * y + z * z <= step * step) {
									Block block = world.getBlockAt(center.clone().add(x, y, z));
									if (!(block.getState() instanceof Container) && (block.getType().isBlock() && block.getType().isSolid())
											|| step <= 2) {
										block.setType(Material.CHERRY_LEAVES);
										BlockState state = block.getState();
										Leaves leaves = (Leaves) state.getBlockData();
										leaves.setPersistent(true); // Make leaves persistent to prevent decay
										state.setBlockData(leaves);
										state.update(true, false);
									}
								}
							}
						}
					}
					step++;
				} else {
					this.cancel();
					createEndPortal(center, Material.POPPY);
				}
			}
		}.runTaskTimer(PluginMain.getInstance(), 120L, 20L); // Start after 6 seconds, repeat every second

		new BukkitRunnable() {
			@Override
			public void run() {
				world.spawnParticle(Particle.CHERRY_LEAVES, center.getX(), center.getY() + 0.5, center.getZ(), 4, 3, 2, 3);
			}
		}.runTaskTimer(PluginMain.getInstance(), 200L, 7L);

	}

	public static void createEndPortal(Location center, Material middle) {
		World world = center.getWorld();
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {

				for (int i = 0; i < 2; i++) {
					Block blockAbove = world.getBlockAt(center.clone().add(x, i + 1, z));
					blockAbove.setType(Material.AIR);
				}

				Block block = world.getBlockAt(center.clone().add(x, 0, z));
				block.setType(Material.END_PORTAL);
			}
		}

		Block block = world.getBlockAt(center.clone().add(0, 3, 0));
		block.setType(middle);

		int yHeight = 0;
		if (middle.equals(Material.BELL))
			yHeight = -1;
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				for (int y = yHeight; y < 1; y++) {
					if ((Math.abs(x) == 2 || Math.abs(z) == 2) && !(Math.abs(x) == 2 && Math.abs(z) == 2)) {
						Block pillar = world.getBlockAt(center.clone().add(x, y, z));
						pillar.setType(Material.POLISHED_BASALT);
					}
				}
			}
		}
		world.playSound(center, Sound.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f);
	}

	public void despawn() {
		for (int i = bosses.size() - 1; i >= 0; i--) {
			bosses.get(i).remove();
		}
		bosses.clear();
	}

	public int getStage() {
		return stage;
	}
}
