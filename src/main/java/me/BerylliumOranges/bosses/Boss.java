package me.BerylliumOranges.bosses;

import java.io.File;
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
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.bosses.utils.Hazards;
import me.BerylliumOranges.bosses.utils.Hazards.Hazard;
import me.BerylliumOranges.bosses.utils.PlayerStateSaver;
import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.listeners.BossBarListener;
import me.BerylliumOranges.main.PluginMain;
import me.BerylliumOranges.misc.EntityUtils;

public abstract class Boss implements Listener {

	public PlayerStateSaver saved = null;
	public ArrayList<LivingEntity> bosses = new ArrayList<>();
	public ArrayList<Player> participants = new ArrayList<>();

	public int ticksAlive = 0;
	public int maxTicksAlive = 7200; // 6 minutes
	public int introAnimationTicks = 0;
	public int idleCountdown = 0;
	protected int stage = 0;

	public BossType bossType;
	public String name;
	public int islandSize = 30;
	public World world;
	public boolean beingDeleted = false;
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
		Location spawnLocation = new Location(world, 0.5, highestY + 1, 0);

		world.setTime(1000);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				world.setChunkForceLoaded(x, z, true);
			}
		}
		world.setSpawnLocation(spawnLocation);

		bossIntro(spawnLocation);
	}

	public LivingEntity summonBoss(Location loc) {
		LivingEntity boss = null;// BossUtils.getPlayerSubstitute(bossType);
//		if (boss != null) {
//			PlayerStateSaver.savePlayerState((Player) boss);
//		} else
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
			if (dead) {
				doDeathAnimation(e.getEntity());
				for (Entity ent : e.getEntity().getNearbyEntities(200, 200, 200)) {
					if (ent instanceof Mob)
						ent.remove();
					else if (ent instanceof Player) {
						((Player) ent).addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 4, false));
						((Player) ent).playSound(ent, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
					}
				}
			}
		} else if (e.getEntity() instanceof Player && e.getEntity().getWorld().equals(world)) {
			for (Player p : world.getPlayers()) {
				if (!bosses.contains(p)) {
					new BukkitRunnable() {
						@Override
						public void run() {
							for (Player p : world.getPlayers()) {
								if (p.isDead())
									p.spigot().respawn();
							}
							if (!world.getPlayers().isEmpty()) {
								removeWorld(world, false);
								this.cancel();
							}
						}
					}.runTaskTimer(PluginMain.getInstance(), 100, 100);
					break;
				}
			}
		}
	}

	public static void startWorldRemoval(World w, Location death) {
		Bukkit.broadcastMessage("I see delete world start");
		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if (count > 1500) {
					removeWorld(w, true);
					this.cancel();
				}
				if (count == 400) {

					w.playSound(death, Sound.ENTITY_WARDEN_AGITATED, Float.MAX_VALUE, 0f);

					Bukkit.broadcastMessage("Starting removal");
					new BukkitRunnable() {
						int step = 6; // This represents the current radius being filled
						double maxRadius = 60; // Maximum radius of the inner sphere
//PLAY A SOUND

						@Override
						public void run() {
							if (step <= maxRadius) {
								// Inner sphere, add leaves
//								if (step < maxRadius) {
//									createHollowSphere(step, Material.CHERRY_LEAVES); // assuming Material.CHERRY_LEAVES exists
//								}
								createHollowSphere(step - 1, Material.AIR);
								step++;
							} else {
								this.cancel();
							}
						}

						private void createHollowSphere(int radius, Material material) {
							w.playSound(death, Sound.ENTITY_GOAT_RAM_IMPACT, Float.MAX_VALUE, 0f);
							for (int x = -radius; x <= radius; x++) {
								for (int y = -radius; y <= radius; y++) {
									for (int z = -radius; z <= radius; z++) {
										if (x * x + y * y + z * z <= radius * radius
												&& x * x + y * y + z * z >= (radius - 1) * (radius - 1)) {
											Block block = w.getBlockAt(death.clone().add(x, y + 1, z));

											if (material == Material.CHERRY_LEAVES && !block.getType().isAir()) {
												block.setType(Material.CHERRY_LEAVES);
												BlockState state = block.getState();
												Leaves leaves = (Leaves) state.getBlockData();
												leaves.setPersistent(true); // Prevent leaves from decaying
												state.setBlockData(leaves);
												state.update(true, false);
											} else if (material == Material.AIR) {
												block.setType(Material.AIR);
											}
										}
									}
								}
							}
						}
					}.runTaskTimer(PluginMain.getInstance(), 120L, 20L); // Start after 6 seconds, repeat every second

				}
				count++;
			}
		}.runTaskTimer(PluginMain.getInstance(), 40L, 1L); // Starts after 2 seconds, repeats every tick
	}

	public static void removeWorld(World w, boolean punish) {
		for (Player p : w.getPlayers()) {
			Location tp = Bukkit.getServer().getWorlds().get(0).getSpawnLocation().clone();
			if (p.getRespawnLocation() != null && p.getRespawnLocation().getWorld().getEnvironment() == Environment.NORMAL) {
				tp = p.getRespawnLocation();
			}
			if (punish) {
				double radius = 30.0;
				double u = Math.random();
				double v = Math.random();
				double theta = 2 * Math.PI * u;
				double phi = Math.acos(2 * v - 1);
				double x = radius * Math.sin(phi) * Math.cos(theta);
				double y = radius * Math.sin(phi) * Math.sin(theta);
				double z = radius * Math.cos(phi);
				tp.add(x, 200 + y, z);
			}
			p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
			p.teleport(tp);
		}

		File worldContainer = Bukkit.getServer().getWorldContainer();
		File worldFolder = new File(worldContainer, w.getName());

		// Attempt to unload the world
		if (Bukkit.getServer().unloadWorld(w, true)) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully unloaded " + w.getName());

			// Delete the world folder recursively
			if (deleteDirectory(worldFolder)) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully deleted " + worldFolder.getPath());
			} else {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to delete " + worldFolder.getPath());
			}

		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to unload " + w.getName() + ".");
		}
	}

	private static boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}

	public void doDeathAnimation(LivingEntity lastAlive) {
		World world = lastAlive.getWorld();
		Location center = lastAlive.getLocation();
		world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
		world.spawnParticle(Particle.EXPLOSION, center, 20, 0.5, 0.5, 0.5, 0.1);

		List<Hazard> hazards = new ArrayList<>();
		hazards.add(Hazard.IS_BOSS_WORLD);
		Hazards.saveHazards(world, hazards);

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

					world.spawnParticle(Particle.CHERRY_LEAVES, randomLocation, 1);
					if (count == 0) {
						world.playSound(center, Sound.ITEM_GOAT_HORN_SOUND_6, Float.MAX_VALUE, 0f);
						for (World w : Bukkit.getServer().getWorlds()) {
							if (!w.equals(world)) {
								w.playSound(w.getSpawnLocation(), Sound.ITEM_GOAT_HORN_SOUND_6, Float.MAX_VALUE, 0f);
							}
						}
					}
					count++;
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(PluginMain.getInstance(), 40L, 1L);

		int blockRadius = 3;
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
									if (!(block.getState() instanceof Container)
											&& (block.getType().isBlock() && block.getType().isSolid())) {
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

		startWorldRemoval(world, center);

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
