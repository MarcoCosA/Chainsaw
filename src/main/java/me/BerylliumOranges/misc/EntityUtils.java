package me.BerylliumOranges.misc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.BerylliumOranges.customEvents.TeleportEvent;

public class EntityUtils {

	/**
	 * Retrieves all living entities from all worlds on the server.
	 *
	 * @return A list of all living entities currently loaded on the server.
	 */
	public static List<LivingEntity> getAllLivingEntities() {
		List<LivingEntity> livingEntities = new ArrayList<>();
		// Iterate through all the worlds on the server
		for (World world : Bukkit.getServer().getWorlds()) {
			// Iterate through all the entities in the current world
			for (Entity entity : world.getEntities()) {
				// Check if the entity is a living entity
				if (entity instanceof LivingEntity) {
					// Add the living entity to the list
					livingEntities.add((LivingEntity) entity);
				}
			}
		}
		return livingEntities;
	}

	public static void teleportEntity(Entity entity, Location location) {
		Entity topEntity = findTopEntity(entity); // Find the top entity in the stack
		List<Entity> stack = new ArrayList<>();
		dismountStack(topEntity, stack); // Start dismounting from the top

		// Reverse the list to start teleportation from the bottom
		List<Entity> reversedStack = new ArrayList<>(stack);
		java.util.Collections.reverse(reversedStack);

		for (Entity stackEntity : reversedStack) {
			TeleportEvent teleportEvent = new TeleportEvent(stackEntity, location);
			Bukkit.getPluginManager().callEvent(teleportEvent);
			if (!teleportEvent.isCancelled()) {
				stackEntity.teleport(location);
				Bukkit.broadcastMessage("Teleporting: " + stackEntity.getCustomName());
			}
		}
	}

	private static Entity findTopEntity(Entity entity) {
		// If there's an entity riding this one, keep going up
		while (entity.getPassenger() != null) {
			entity = entity.getPassenger();
		}
		return entity; // This is the top entity in the stack
	}

	private static void dismountStack(Entity entity, List<Entity> stack) {
		// Add the current entity to the stack list
		stack.add(entity);
		Bukkit.broadcastMessage("Processing entity: " + entity.getCustomName());
		if (entity.getVehicle() != null) {
			Entity vehicle = entity.getVehicle();
			entity.leaveVehicle(); // Ensure the current entity is dismounted
			dismountStack(vehicle, stack); // Continue with the vehicle
		}
	}
}
