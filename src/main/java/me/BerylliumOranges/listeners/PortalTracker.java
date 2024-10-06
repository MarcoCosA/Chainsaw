package me.BerylliumOranges.listeners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.PortalCreateEvent;

import me.BerylliumOranges.bosses.utils.Hazards;
import me.BerylliumOranges.bosses.utils.Hazards.Hazard;
import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.main.PluginMain;

public class PortalTracker implements Listener {
	private static List<String> portalBlockCoords = new ArrayList<>();
	private File dataFile;

	public PortalTracker() {
		dataFile = new File(PluginMain.getInstance().getDataFolder(), "portalBlocks.dat");
		loadPortalBlocks();
		Bukkit.getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	public void disable() {
		savePortalBlocks();
	}

	@EventHandler
	public void onPortalLight(PortalCreateEvent e) {
		if (e.getWorld().getEnvironment() == World.Environment.NORMAL) {
			e.getBlocks().stream().filter(b -> b.getType() == Material.NETHER_PORTAL)
					.forEach(b -> portalBlockCoords.add(b.getX() + "," + b.getY() + "," + b.getZ()));
		}
	}

	@EventHandler
	public void onTick(TickEvent e) {
		World w = Bukkit.getServer().getWorlds().get(0);
		if (w.getTime() >= 22420 && w.getTime() <= 23420) {
			for (int i = portalBlockCoords.size() - 1; i >= 0; i--) {
				String[] parts = portalBlockCoords.get(i).split(",");
				Block b = w.getBlockAt(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
				if (!b.getType().equals(Material.NETHER_PORTAL)) {
					portalBlockCoords.remove(i);
					continue;
				} else if (Math.random() > 0.92) {
					w.spawnParticle(Particle.CHERRY_LEAVES, b.getLocation().add(0.25, 1.25, 0.25), 1, 0.5, 0.5, 0.5, 0.1);
				}
			}
		}
	}

	private void savePortalBlocks() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
			oos.writeObject(new ArrayList<>(portalBlockCoords));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadPortalBlocks() {
		if (!dataFile.exists())
			return;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
			portalBlockCoords = (List<String>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPortal(EntityPortalEnterEvent e) {
	}

	@EventHandler
	public void onPortal(EntityPortalEvent e) {
		if (Hazards.hasHazard(e.getEntity().getWorld(), Hazard.IS_BOSS_WORLD)) {
			e.setTo(Bukkit.getWorlds().get(0).getSpawnLocation());
		}
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent e) {
		if (Hazards.hasHazard(e.getFrom().getWorld(), Hazard.IS_BOSS_WORLD)) {
			e.setTo(e.getPlayer().getRespawnLocation() != null ? e.getPlayer().getRespawnLocation()
					: Bukkit.getWorlds().get(0).getSpawnLocation());
		} else if (e.getCause() == TeleportCause.NETHER_PORTAL) {
			if (e.getFrom().getWorld().getEnvironment() == World.Environment.NORMAL) {
				long time = e.getFrom().getWorld().getTime();
				if (time > 22500L && time < 23500L) {
					e.setCancelled(true);
					e.getPlayer().teleport(PluginMain.dimension1.getSpawnLocation().add(0, 1, 0));
				}
			}
		}
	}
}
