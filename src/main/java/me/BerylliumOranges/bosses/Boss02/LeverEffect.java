package me.BerylliumOranges.bosses.Boss02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.main.PluginMain;

public abstract class LeverEffect implements Listener {
	public Location loc;
	public Block block;
	protected Random random = new Random();
	protected boolean isEnabled = false;

	public LeverEffect(Location loc) {
		Bukkit.getPluginManager().registerEvents(this, PluginMain.getInstance());
		this.loc = loc;
		this.block = loc.getBlock();
	}

	public void triggerOn() {
		isEnabled = true;
	}

	public void triggerOff() {
		isEnabled = false;
	}

	@EventHandler
	public void onChange(BlockEvent e) {
		if (e.getBlock().equals(block)) {
			if (isEnabled) {
				triggerOff();
			} else {
				triggerOn();
			}
		}
	}
}

class MakeInvulerable extends LeverEffect {
	Entity ent;

	public MakeInvulerable(Location loc, Entity ent) {
		super(loc);
		this.ent = ent;
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
		ent.setInvulnerable(true);
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
		ent.setInvulnerable(false);
	}
}

//Example
class InstantHarvest extends LeverEffect {
	public InstantHarvest(Location loc) {
		super(loc);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (isEnabled && e.getAction().equals(Action.LEFT_CLICK_BLOCK)
				&& (e.getItem() == null || !e.getItem().getType().toString().toLowerCase().contains("sword"))) {
			e.getClickedBlock().breakNaturally(e.getItem());
		}
	}
}

class NightVisionBoost extends LeverEffect {
	public NightVisionBoost(Location loc) {
		super(loc);
	}

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
}

class ResourceRush extends LeverEffect {
	public ResourceRush(Location loc) {
		super(loc);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (isEnabled) {
			Material type = e.getBlock().getType();
			ItemStack droppedItem = new ItemStack(type, 2); // Example: Double the usual drop
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), droppedItem);
		}
	}
}

class RandomTeleport extends LeverEffect {
	public RandomTeleport(Location loc) {
		super(loc);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (isEnabled && random.nextDouble() < 0.1) { // 10% chance on movement to trigger
			Location loc = e.getPlayer().getWorld().getHighestBlockAt(random.nextInt(256), random.nextInt(256)).getLocation();
			e.getPlayer().teleport(loc);
		}
	}
}

class SuddenStorm extends LeverEffect {
	public SuddenStorm(Location loc) {
		super(loc);
	}

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
}

class MobConfusion extends LeverEffect {
	public MobConfusion(Location loc) {
		super(loc);
	}

	@EventHandler
	public void onMobMove(PlayerMoveEvent e) {
		if (isEnabled) {
			e.getPlayer().getNearbyEntities(10, 10, 10).forEach(entity -> {
				if (entity instanceof Mob) {
					Location newLocation = entity.getLocation().add(random.nextInt(10) - 5, 0, random.nextInt(10) - 5);
					entity.teleport(newLocation);
				}
			});
		}
	}
}

class InvisibilityBurst extends LeverEffect {
	public InvisibilityBurst(Location loc) {
		super(loc);
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
		Bukkit.getOnlinePlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 6000, 0)));
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
		Bukkit.getOnlinePlayers().forEach(player -> player.removePotionEffect(PotionEffectType.INVISIBILITY));
	}
}

class GravityFlip extends LeverEffect implements Listener {
	private Set<UUID> affectedEntities = new HashSet<>(); // Track entities affected by gravity flip

	public GravityFlip(Location loc) {
		super(loc);
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
		// Apply zero gravity to all players and mobs within the world where the effect
		// is triggered
		loc.getWorld().getEntitiesByClasses(Player.class, Mob.class).forEach(entity -> {
			if (entity instanceof LivingEntity) {
				entity.setGravity(false);
				entity.setVelocity(entity.getVelocity().add(new Vector(0, 1, 0))); // Gently push upwards
				affectedEntities.add(entity.getUniqueId()); // Add to set of affected entities
			}
		});
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
		// Reset gravity for all previously affected entities that are still valid
		affectedEntities.forEach(uuid -> {
			Entity entity = Bukkit.getEntity(uuid);
			if (entity instanceof LivingEntity) {
				entity.setGravity(true);
			}
		});
		affectedEntities.clear(); // Clear the set once gravity is reset
	}

	// Handle player quitting
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (affectedEntities.remove(event.getPlayer().getUniqueId())) {
			event.getPlayer().setGravity(true);
		}
	}

	// Handle player changing worlds
	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
		if (affectedEntities.remove(event.getPlayer().getUniqueId())) {
			event.getPlayer().setGravity(true);
		}
	}

	// Handle entity death
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (affectedEntities.remove(event.getEntity().getUniqueId())) {
			event.getEntity().setGravity(true); // Reset gravity on death
		}
	}
}

class Heal extends LeverEffect {
	public Heal(Location loc) {
		super(loc);
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
		loc.getWorld().getEntitiesByClasses(Player.class, Mob.class).forEach(entity -> {
			if (entity instanceof LivingEntity) {
				((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 0));
			}
		});
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
		Bukkit.getWorlds().forEach(world -> world.setStorm(false));
	}
}

class BlockShuffle extends LeverEffect {
	public BlockShuffle(Location loc) {
		super(loc);
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
		Location loc = this.loc;
		int radius = 7; // Define the radius within which to shuffle blocks
		List<Block> blocks = new ArrayList<>();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				for (int dy = -radius; dy <= radius; dy++) {
					blocks.add(loc.getWorld().getBlockAt(loc.clone().add(dx, dy, dz)));
				}
			}
		}
		Collections.shuffle(blocks, random);
		if (!blocks.isEmpty()) {
			Material temp = blocks.get(0).getType();
			for (int i = 1; i < blocks.size(); i++) {
				Material current = blocks.get(i).getType();
				blocks.get(i).setType(temp);
				temp = current;
			}
			blocks.get(0).setType(temp);
		}
	}
}

class Magnet extends LeverEffect {
	private int radius = 20; // Radius to attract mobs

	public Magnet(Location loc) {
		super(loc);
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
	}

	@EventHandler
	public void onTick(TickEvent e) {
		if (isEnabled) {
			loc.getWorld().getNearbyEntities(loc, radius, radius, radius).forEach(entity -> {
				if (entity instanceof LivingEntity && entity.getLocation().distanceSquared(loc) < radius * radius) {
					Vector dir = loc.subtract(entity.getLocation()).toVector().normalize();
					entity.setVelocity(entity.getVelocity().clone().add(dir.multiply(0.1)));
				}
			});
		}
	}
}

class BlastAway extends LeverEffect {
	private int radius = 20;

	public BlastAway(Location loc) {
		super(loc);
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
		loc.getWorld().getNearbyEntities(loc, radius, radius, radius).forEach(entity -> {
			if (entity instanceof LivingEntity && entity.getLocation().distanceSquared(loc) < radius * radius) {
				Vector dir = loc.subtract(entity.getLocation()).toVector().normalize();
				dir.setY(dir.getY() - 0.1);
				entity.setVelocity(entity.getVelocity().clone().add(dir).multiply(-5));
			}
		});
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
	}
}

class SpawnCow extends LeverEffect {
	Entity ent = null;
	EntityType type = EntityType.COW;

	public SpawnCow(Location loc) {
		super(loc);
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
		ent = loc.getWorld().spawnEntity(loc, type);
	}

	@Override
	public void triggerOff() {
		super.triggerOff();
		if (ent != null)
			ent.remove();
	}
}

class SpawnSilverfish extends SpawnCow {
	EntityType type = EntityType.SILVERFISH;

	public SpawnSilverfish(Location loc) {
		super(loc);
	}
}

class SpawnTnt extends SpawnCow {
	EntityType type = EntityType.PRIMED_TNT;

	public SpawnTnt(Location loc) {
		super(loc);
	}
}

class BonusHealth extends LeverEffect {
	private int radius = 50;

	public BonusHealth(Location loc) {
		super(loc);
	}

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
				liv.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 2));
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
}

class Hole extends LeverEffect {
	public Hole(Location loc) {
		super(loc);
	}

	@Override
	public void triggerOn() {
		super.triggerOn();
		Location loc = this.loc;
		for (int dx = -4; dx <= 4; dx++) {
			for (int dz = -4; dz <= 4; dz++) {
				loc.getWorld().getBlockAt(loc.clone().add(dx, 10, dz)).setType(Material.DAMAGED_ANVIL);
				for (int dy = -7; dy <= 7; dy++) {
					loc.getWorld().getBlockAt(loc.clone().add(dx, dy, dz)).setType(Material.AIR);
				}
			}
		}
	}
}

enum LeverEffectType {
	INSTANT_HARVEST {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new InstantHarvest(loc);
		}
	},
	NIGHT_VISION_BOOST {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new NightVisionBoost(loc);
		}
	},
	RESOURCE_RUSH {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new ResourceRush(loc);
		}
	},
	RANDOM_TELEPORT {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new RandomTeleport(loc);
		}
	},
	SUDDEN_STORM {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new SuddenStorm(loc);
		}
	},
	MOB_CONFUSION {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new MobConfusion(loc);
		}
	},
	INVISIBILITY_BURST {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new InvisibilityBurst(loc);
		}
	},
	GRAVITY_FLIP {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new GravityFlip(loc);
		}
	},
	HEALING_RAIN {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new Heal(loc);
		}
	},
	BLOCK_SHUFFLE {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new BlockShuffle(loc);
		}
	},
	MAGNET {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new Magnet(loc);
		}
	},
	BLAST {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new BlastAway(loc);
		}
	},
	SPAWN_COW {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new SpawnCow(loc);
		}
	},
	SPAWN_SILVERFISH {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new SpawnSilverfish(loc);
		}
	},
	SPAWN_TNT {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new SpawnTnt(loc);
		}
	},
	BONUS_HEALTH {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new BonusHealth(loc);
		}
	},
	SPAWN_HOLE {
		@Override
		public LeverEffect createEffect(Location loc) {
			return new Hole(loc);
		}
	};

	public abstract LeverEffect createEffect(Location loc);
}
