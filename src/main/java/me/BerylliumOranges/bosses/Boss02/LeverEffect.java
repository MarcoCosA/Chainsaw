package me.BerylliumOranges.bosses.Boss02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public abstract class LeverEffect implements Listener {

	public static ArrayList<Block> invulBlocks = new ArrayList<>();

	public Location loc;
	public Block block;
	public Block blockPlacedOn = null;
	protected Random random = new Random();
	protected boolean isEnabled = false;

	public LeverEffect() {
		Bukkit.getPluginManager().registerEvents(this, PluginMain.getInstance());

	}

	public String getBroadcastMessage(boolean newPowerState) {
		String className = this.getClass().getSimpleName();
		return ChatColor.GRAY + className.replaceAll("(\\p{Lu})", " $1").trim() + (newPowerState ? " ON" : " OFF");
	}

	public void apply(Location loc, Block blockPlacedOn) {
		setLocation(loc);
		this.blockPlacedOn = blockPlacedOn;
		blockPlacedOn.setType(TowerPopulator.TOWER_UNUSED);
		invulBlocks.add(blockPlacedOn);
	}

	public void triggerOn() {
		isEnabled = true;
		playTriggerSound();
		broadcastLeverInformation(true);
		blockPlacedOn.setType(TowerPopulator.TOWER_USED);
	}

	public void triggerOff() {
		isEnabled = false;
		broadcastLeverInformation(false);
	}

	public void broadcastLeverInformation(boolean newPowerState) {
		for (Player p : block.getWorld().getPlayers()) {
			p.sendMessage(getBroadcastMessage(newPowerState));
		}
	}

	public List<Player> getPlayers() {
		return loc.getWorld().getPlayers();
	}

	@EventHandler
	public void onChange(PlayerInteractEvent e) {
		if (block != null && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null
				&& e.getClickedBlock().equals(block)) {
			if (isEnabled) {
				triggerOff();
			} else {
				triggerOn();
			}
		}
	}

	@EventHandler
	public void onLeverBreak(BlockBreakEvent e) {
		if ((e.getBlock().equals(block) || e.getBlock().equals(blockPlacedOn))) {
			Bukkit.broadcastMessage("I see someone trying to break a lever");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		List<Block> blocks = e.blockList();
		for (int i = blocks.size() - 1; i >= 0; i--) {
			if (blocks.get(i).equals(block) || blocks.get(i).equals(blockPlacedOn)) {
				blocks.remove(i);
			}
		}
	}

	public Location getLocation() {
		return loc;
	}

	public void setLocation(Location loc) {
		this.loc = loc;
		if (loc == null)
			this.block = null;
		else
			this.block = loc.getBlock();
	}

	public abstract void playTriggerSound();

	public static Location getNearestBlock(Location loc) {
		World world = loc.getWorld();
		int baseX = loc.getBlockX();
		int baseZ = loc.getBlockZ();

		Block highestBlock = world.getBlockAt(baseX, 0, baseZ); // Initialize with the lowest possible block.

		for (int x = baseX - 1; x <= baseX + 1; x++) {
			for (int z = baseZ - 1; z <= baseZ + 1; z++) {
				// Get the highest block at each (x, z) position that isn't air.
				Block block = world.getHighestBlockAt(x, z);
				if (block.getY() > highestBlock.getY() && block.getType().isSolid()) {
					highestBlock = block;
				}
			}
		}
		;
		return highestBlock.getLocation().add(0.5 + (Math.random() * 0.1) - 0.05, 1, 0.5 + (Math.random() * 0.1) - 0.05);
	}
}

abstract class SingleUseLeverEffect extends LeverEffect {
	@Override
	@EventHandler
	public void onChange(PlayerInteractEvent e) {
		if (block != null && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null
				&& e.getClickedBlock().equals(block) && !isEnabled) {
			triggerOn();
			Block temp = block;
			block.getWorld().spawnParticle(Particle.BLOCK, block.getLocation().clone().add(0.5, 0.5, 0.5), 10, 1, 1, 1, 0,
					Bukkit.createBlockData(Material.STONE));
			block = null;

			new BukkitRunnable() {
				@Override
				public void run() {
					temp.setType(Material.AIR);
				}
			}.runTaskLater(PluginMain.getInstance(), 1L);
		}
	}

	@Override
	public void triggerOff() {
	}

	@Override
	public String getBroadcastMessage(boolean newPowerState) {
		String className = this.getClass().getSimpleName();
		return ChatColor.GRAY + className.replaceAll("(\\p{Lu})", " $1").trim();
	}
}

class MakeInvulerable extends LeverEffect {
	Entity ent;

	public MakeInvulerable(Entity ent) {
		this.ent = ent;
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
		ent.setInvulnerable(false);
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
		ent.setInvulnerable(true);
		loc.getWorld().playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.RECORDS, Float.MAX_VALUE, 2);
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.RECORDS, Float.MAX_VALUE, 0);
	}

	@Override
	public String getBroadcastMessage(boolean newPowerState) {
		return super.getBroadcastMessage(!newPowerState);
	}
}

//Example
class InstantHarvest extends LeverEffect {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e) {
		if (isEnabled && e.getAction().equals(Action.LEFT_CLICK_BLOCK)
				&& (e.getItem() == null || !e.getItem().getType().toString().toLowerCase().contains("sword")) && e.getClickedBlock() != null
				&& !e.getClickedBlock().getType().equals(Material.LEVER) && e.getPlayer().getWorld().equals(loc.getWorld())) {

			if (LeverEffect.invulBlocks.contains(e.getClickedBlock())) {
				e.getClickedBlock().getWorld().playSound(loc, Sound.BLOCK_STONE_BREAK, SoundCategory.RECORDS, Float.MAX_VALUE, 1);
				e.getPlayer().spawnParticle(Particle.BLOCK, e.getClickedBlock().getLocation(), 2, 0.5, 0.5, 0.5, 1,
						Bukkit.createBlockData(e.getClickedBlock().getType()));
			} else
				e.getClickedBlock().breakNaturally(e.getItem());
		}
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.BLOCK_METAL_BREAK, SoundCategory.RECORDS, Float.MAX_VALUE, 1);
	}
}

class NightVisionBoost extends LeverEffect {
	@Override
	public void triggerOn() {
		super.triggerOn();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 0));
		}
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ITEM_BOTTLE_FILL, SoundCategory.RECORDS, Float.MAX_VALUE, 1);
	}
}

class TeleportBoss extends SingleUseLeverEffect {

	@Override
	public void triggerOn() {
		super.triggerOn();

		Boss boss = BossUtils.getExistingBoss(BossType.TRAP);
		if (boss != null) {
			LivingEntity b = boss.bosses.get(0);
			if (b != null && b.getWorld().equals(loc.getWorld())) {
				b.teleport(getNearestBlock(loc));
			}
		}
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_SCREAM, SoundCategory.RECORDS, Float.MAX_VALUE, 1);
	}
}

class RandomTeleport extends LeverEffect {
	int interval = 400;
	int ticks = interval - 20;

	@Override
	public void triggerOn() {
		super.triggerOn();
		interval = Math.max(400 / block.getWorld().getPlayers().size(), 80);
		ticks = interval - 20;
	}

	@EventHandler
	public void onTick(TickEvent e) {
		if (isEnabled) {
			ticks++;
			if (!block.getWorld().getPlayers().isEmpty() && ticks % interval == 0) {
				Player p = block.getWorld().getPlayers().get((int) (Math.random() * block.getWorld().getPlayers().size()));

				p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1);
				p.spawnParticle(Particle.PORTAL, p.getEyeLocation(), 30, 0.5, 0.5, 0.5, 1, null, true);
				new BukkitRunnable() {
					@Override
					public void run() {
						p.teleport(getRandomLocation(p, block.getWorld().getSpawnLocation(), 45));
						p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1);
						p.spawnParticle(Particle.PORTAL, p.getEyeLocation(), 30, 0.5, 0.5, 0.5, 1, null, true);
					}
				}.runTaskLater(PluginMain.getInstance(), 5L);
			}
		}
	}

	private Location getRandomLocation(Player p, Location center, int radius) {
		for (int i = 0; i < 10; i++) {

			int dx = random.nextInt(radius * 2 + 1) - radius;
			int dz = random.nextInt(radius * 2 + 1) - radius;
			Location randomLoc = new Location(center.getWorld(), center.getX() + dx, 0, center.getZ() + dz);
			randomLoc = randomLoc.getWorld().getHighestBlockAt(randomLoc).getLocation();
			if (randomLoc.getBlock() != null && !randomLoc.getBlock().getType().isAir()) {
				randomLoc.setDirection(p.getLocation().getDirection());
				return randomLoc.add(0, 1, 0);
			}
		}
		Bukkit.broadcastMessage("Failed to find suitable location, teleporting to center");
		return center;
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_AMBIENT, SoundCategory.RECORDS, Float.MAX_VALUE, 0);
	}
}

class Storm extends LeverEffect {
	@Override
	public void triggerOn() {
		super.triggerOn();
		loc.getWorld().setStorm(true);
		loc.getWorld().setThundering(true);
		loc.getWorld().setWeatherDuration(6000);
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
		loc.getWorld().setStorm(false);
		loc.getWorld().setThundering(false);
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.RECORDS, Float.MAX_VALUE, 1);
	}
}

class BuffMobs extends SingleUseLeverEffect {
	@Override
	public void triggerOn() {
		super.triggerOn();
		for (Entity ent : loc.getWorld().getEntities()) {
			if (ent instanceof Mob) {
				Mob m = (Mob) ent;
				m.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 6000, 0));
				m.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 0));
			}
		}
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_WITCH_CELEBRATE, Float.MAX_VALUE, 1);
	}
}

class Poison extends LeverEffect {
	@Override
	public void triggerOn() {
		super.triggerOn();
		getPlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 6000, 0)));
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
		getPlayers().forEach(player -> player.removePotionEffect(PotionEffectType.POISON));
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_WITCH_AMBIENT, SoundCategory.RECORDS, Float.MAX_VALUE, 1);
	}
}

class Gravity extends LeverEffect implements Listener {
	private int ticks = 0;

	@Override
	public void triggerOff() {
		super.triggerOff();
		loc.getWorld().playSound(loc, Sound.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.RECORDS, Float.MAX_VALUE, 2);
	}

	@EventHandler
	public void onTick(TickEvent e) {
		if (isEnabled) {
			ticks++;
			if (ticks > 150) {
				ticks = 0;
				triggerOff();
			} else {
				for (Entity ent : loc.getWorld().getEntities()) {
					if (ent instanceof Player) {
						Player p = (Player) ent;
						p.setVelocity(p.getVelocity().add(new Vector(0, 0.086, 0)));
					} else if (ent instanceof LivingEntity) {
						LivingEntity liv = (LivingEntity) ent;
						liv.setVelocity(liv.getVelocity().add(new Vector(0, 0.084, 0)));
					}
				}
			}
		}
	}

	@Override
	public String getBroadcastMessage(boolean poweredStatus) {
		return super.getBroadcastMessage(!poweredStatus);
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.RECORDS, Float.MAX_VALUE, 1);
	}
}

class Heal extends SingleUseLeverEffect {
	@Override
	public void triggerOn() {
		super.triggerOn();
		loc.getWorld().getEntitiesByClasses(Player.class, Mob.class).forEach(entity -> {
			if (entity instanceof LivingEntity) {
				((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 0));
			}
		});
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_SPLASH_POTION_BREAK, SoundCategory.RECORDS, Float.MAX_VALUE, 1);
	}
}

class BlockShuffle extends SingleUseLeverEffect {

	@Override
	public void triggerOn() {
		super.triggerOn();
		Location loc = this.loc;
		int radius = 7; // Define the radius for the sphere
		List<Block> shufflableBlocks = new ArrayList<>();

		// Collect all blocks that are inside the spherical region and not of type LEVER
		// or GILDED_BLACKSTONE
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					if (dx * dx + dy * dy + dz * dz <= radius * radius) { // Check if inside sphere
						Block block = loc.getWorld().getBlockAt(loc.clone().add(dx, dy, dz));
						if (block.getType() != Material.LEVER && block.getType() != TowerPopulator.TOWER_UNUSED
								&& block.getType() != TowerPopulator.TOWER_USED && block.getType() != Material.WHITE_WOOL) {
							shufflableBlocks.add(block);
						}
					}
				}
			}
		}

		// Shuffle the collected blocks
		Collections.shuffle(shufflableBlocks, random);

		// Perform the shuffle by swapping materials
		if (!shufflableBlocks.isEmpty()) {
			Material firstMaterial = shufflableBlocks.get(0).getType(); // Save the first material
			for (int i = 1; i < shufflableBlocks.size(); i++) {
				Material currentMaterial = shufflableBlocks.get(i).getType();
				shufflableBlocks.get(i).setType(firstMaterial);
				firstMaterial = currentMaterial;
			}
			shufflableBlocks.get(0).setType(firstMaterial); // Complete the loop by setting the last material
		}
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.BLOCK_BONE_BLOCK_PLACE, SoundCategory.RECORDS, 2f, 1);
	}
}

class Artillery extends SingleUseLeverEffect {
	int idleTime = 20;
	int firingTime = 60;

	int firingInterval = 20;
	int ticks = -idleTime;

	ArrayList<Player> targets = new ArrayList<Player>();
	ArrayList<Player> playersToAdd = new ArrayList<Player>();

	@Override
	public void apply(Location loc, Block blockPlacedOn) {
		super.apply(loc, blockPlacedOn);
		blockPlacedOn.setType(Material.LIME_GLAZED_TERRACOTTA);
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
		playersToAdd.add(BossUtils.getNearestPlayer(loc, 10));
	}

	public void findTargets(ArrayList<Player> preselected) {
		List<Player> playersInWorld = new ArrayList<>(loc.getWorld().getPlayers());
		playersInWorld.removeAll(preselected);

		int numToSelect = Math.max(0, ((playersInWorld.size() + 1) / 2) - preselected.size());

		Collections.shuffle(playersInWorld);
		targets = new ArrayList<>(playersInWorld.subList(0, Math.min(numToSelect, playersInWorld.size())));

		targets.addAll(preselected);

		for (Player target : targets) {
			for (Player p : loc.getWorld().getPlayers()) {
				p.sendMessage(ChatColor.GRAY + "[Artillery] Targetting " + target.getName());
			}
		}
		preselected.clear();
	}

	@EventHandler
	public void onTick(TickEvent e) {
		if (isEnabled) {
			ticks++;
			if (!loc.getWorld().getPlayers().isEmpty() && ticks > 0) {

				if (ticks > firingTime) {
					if (targets.isEmpty() || Math.random() < 0.33) {
						findTargets(playersToAdd);
					}
					ticks = -idleTime;
				} else if (ticks % firingInterval == 0) {
					for (Player p : targets) {
						if (p.getWorld().equals(loc.getWorld())) {
							p.getWorld().playSound(p.getLocation().clone().add(0, 0, 24), Sound.ENTITY_GENERIC_EXPLODE, 1, 1.5F);
							SpectralArrow arrow = p.getWorld().spawn(p.getLocation().clone().add(0, 200, 0), SpectralArrow.class);
							arrow.setCustomName(ChatColor.GRAY + "Artillery Shell");
							arrow.setVelocity(new Vector(0, -5, 0));
							new BukkitRunnable() {
								@Override
								public void run() {
//								p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1);
//								p.spawnParticle(Particle.PORTAL, p.getEyeLocation(), 30, 0.5, 0.5, 0.5, 1, null, true);
									arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
									arrow.getWorld().spawnParticle(Particle.EXPLOSION, arrow.getLocation(), 3);
									int effectRadius = 3;
									for (int dx = -effectRadius; dx <= effectRadius; dx++) {
										for (int dy = -effectRadius; dy <= effectRadius; dy++) {
											for (int dz = -effectRadius; dz <= effectRadius; dz++) {
												if (dx * dx + dy * dy + dz * dz <= effectRadius * effectRadius) {
													Location blockLocation = arrow.getLocation().clone().add(dx, dy, dz);
													blockLocation.getBlock().setType(Material.AIR);
												}
											}
										}
									}
									for (Player p : PluginMain.getNearbyPlayers(arrow.getLocation(), 3)) {
										p.damage(10, arrow);
									}
									arrow.remove();
								}
							}.runTaskLater(PluginMain.getInstance(), 80L);
						}
					}
				}
			}
		}
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.BLOCK_DECORATED_POT_BREAK, SoundCategory.RECORDS, Float.MAX_VALUE, 0F);
	}
}

class Magnet extends LeverEffect {
	private int ticks = 0;
	private int radius = 20; // Radius to attract mobs

	@EventHandler
	public void onTick(TickEvent e) {
		if (isEnabled) {
			ticks++;
			loc.getWorld().getNearbyEntities(loc, radius, radius, radius).forEach(entity -> {
				if (entity instanceof LivingEntity && entity.getLocation().distanceSquared(loc) < radius * radius) {
					Vector dir = loc.clone().subtract(entity.getLocation()).toVector().normalize();
					entity.setVelocity(entity.getVelocity().clone().add(dir.multiply(0.25)));
					entity.setFallDistance(0f);
					if (ticks % 40 == 0) {
						loc.getWorld().playSound(loc, Sound.ENTITY_GUARDIAN_ATTACK, SoundCategory.RECORDS, 0.5f, 1);
					}
				}
			});
		}
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_GUARDIAN_AMBIENT, SoundCategory.RECORDS, 2f, 2);
	}
}

class BlastAway extends SingleUseLeverEffect {
	private int radius = 20;

	@Override
	public void triggerOn() {
		super.triggerOn();
		loc.getWorld().getNearbyEntities(loc, radius, radius, radius).forEach(entity -> {
			if (entity instanceof LivingEntity && entity.getLocation().distanceSquared(loc) < radius * radius) {
				Vector dir = loc.clone().subtract(entity.getLocation()).toVector().normalize();
				dir.setY(dir.getY() - 0.1);
				entity.setVelocity(entity.getVelocity().clone().add(dir).multiply(-5));
			}
		});
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.RECORDS, 2f, 1);
	}
}

class SpawnCow extends LeverEffect {
	Entity ent = null;

	private int maxMobTicks = 20;
	private int mobTicks = maxMobTicks;

	@EventHandler
	public void spawnMobTick(TickEvent e) {
		if (isEnabled) {
			mobTicks++;
			if (mobTicks > maxMobTicks) {
				mobTicks = 0;
				// maxMobTicks += 5;
				ent = loc.getWorld().spawnEntity(getNearestBlock(loc).setDirection(
						new Vector((Math.random() * 2) - 1, (Math.random() * 2) - 1, (Math.random() * 2) - 1)), getEntityType());
			}
		}
	}

	public EntityType getEntityType() {
		return EntityType.COW;
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_COW_AMBIENT, SoundCategory.RECORDS, 2f, 1);
	}
}

class SpawnSilverfish extends SpawnCow {
	@Override
	public EntityType getEntityType() {
		return EntityType.SILVERFISH;
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_SILVERFISH_AMBIENT, SoundCategory.RECORDS, 2f, 1);
	}
}

class SpawnTnt extends SpawnCow {
	@Override
	public EntityType getEntityType() {
		return EntityType.TNT;
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ENTITY_TNT_PRIMED, SoundCategory.RECORDS, 2f, 1);
	}
}

class BonusHealth extends LeverEffect {
	private int radius = 50;

	List<LivingEntity> affected = new ArrayList<LivingEntity>();

	@Override
	public void triggerOn() {
		super.triggerOn();
		loc.getWorld().getNearbyEntities(loc, radius, radius, radius).forEach(entity -> {
			if (entity instanceof LivingEntity && entity.getLocation().distanceSquared(loc) < radius * radius) {
				LivingEntity liv = (LivingEntity) entity;
				if (liv.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1200, 1))) {
					affected.add(liv);
				}
				liv.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 2));
			}
		});
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
		for (LivingEntity liv : affected) {
			liv.removePotionEffect(PotionEffectType.HEALTH_BOOST);
		}
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.ITEM_BOTTLE_FILL, SoundCategory.RECORDS, Float.MAX_VALUE, 1);
	}
}

class SpawnHole extends SingleUseLeverEffect {

	@Override
	public void triggerOn() {
		super.triggerOn();
		Location loc = this.loc;
		int radius = 4; // Define the radius for the circle
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if (dx * dx + dz * dz <= radius * radius) { // Check if the coordinates are within a circle
					// Calculate the depth based on the distance from the center
					int distanceSquared = dx * dx + dz * dz;
					int depth = (int) (25 * (1 - Math.sqrt(distanceSquared) / radius)); // Parabolic depth modifier

					// Set the top block to DAMAGED_ANVIL
					loc.getWorld().getBlockAt(loc.clone().add(dx, 30, dz)).setType(Material.DAMAGED_ANVIL);
					// Clear blocks vertically, depth varies with distance to center
					for (int dy = -depth; dy <= 7; dy++) {
						loc.getWorld().getBlockAt(loc.clone().add(dx, dy, dz)).setType(Material.AIR);
					}
				}
			}
		}
	}

	@Override
	public void playTriggerSound() {
		loc.getWorld().playSound(loc, Sound.BLOCK_ANVIL_PLACE, SoundCategory.RECORDS, 2f, 1);
	}
}

enum LeverEffectType {
	INSTANT_HARVEST {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new InstantHarvest();
			return e;
		}
	},
	NIGHT_VISION_BOOST {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new NightVisionBoost();
			return e;
		}
	},
	TELEPORT_BOSS {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new TeleportBoss();
			return e;
		}
	},
	RANDOM_TELEPORT {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new RandomTeleport();
			return e;
		}
	},
	STORM {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new Storm();
			return e;
		}
	},
	BUFF_MOBS {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new BuffMobs();
			return e;
		}
	},
	POINSON {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new Poison();
			return e;
		}
	},
	GRAVITY {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new Gravity();
			return e;
		}
	},
	HEAL {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new Heal();
			return e;
		}
	},
	BLOCK_SHUFFLE {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new BlockShuffle();
			return e;
		}
	},
	ARTILLERY {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new Artillery();
			return e;
		}
	},
	MAGNET {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new Magnet();
			return e;
		}
	},
	BLAST {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new BlastAway();
			return e;
		}
	},
	SPAWN_COW {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new SpawnCow();
			return e;
		}
	},
	SPAWN_SILVERFISH {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new SpawnSilverfish();
			return e;
		}
	},
	SPAWN_TNT {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new SpawnTnt();
			return e;
		}
	},
	BONUS_HEALTH {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new BonusHealth();
			return e;
		}
	},
	SPAWN_HOLE {
		@Override
		public LeverEffect createEffect() {
			LeverEffect e = new SpawnHole();
			return e;
		}
	};

	public abstract LeverEffect createEffect();
}
