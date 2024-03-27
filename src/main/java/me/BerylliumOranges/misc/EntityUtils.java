package me.BerylliumOranges.misc;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

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
}
