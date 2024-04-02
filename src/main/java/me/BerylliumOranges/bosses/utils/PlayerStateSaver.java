package me.BerylliumOranges.bosses.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStateSaver {

	// Map to store player inventories by their UUID
	private static final Map<UUID, ItemStack[]> playerInventories = new HashMap<>();
	// Map to store player experience levels by their UUID
	private static final Map<UUID, Integer> playerExperienceLevels = new HashMap<>();
	// Map to store player total experience by their UUID
	private static final Map<UUID, Integer> playerTotalExperience = new HashMap<>();

	public static boolean playerIsBoss(Player player) {
		UUID playerId = player.getUniqueId();
		if (playerInventories.containsKey(playerId))
			return true;
		return false;
	}

	// Method to save a player's inventory and experience
	public static void savePlayerState(Player player) {
		UUID playerId = player.getUniqueId();
		// Save the player's inventory
		playerInventories.put(playerId, player.getInventory().getContents());
		// Save the player's experience level
		playerExperienceLevels.put(playerId, player.getLevel());
		// Save the player's total experience
		playerTotalExperience.put(playerId, player.getTotalExperience());
	}

	// Method to load a player's saved inventory and experience
	public static void loadPlayerState(Player player) {
		UUID playerId = player.getUniqueId();
		// Load the player's inventory
		ItemStack[] inventoryContents = playerInventories.get(playerId);
		if (inventoryContents != null) {
			player.getInventory().setContents(inventoryContents);
		}
		// Load the player's experience level
		Integer experienceLevel = playerExperienceLevels.get(playerId);
		if (experienceLevel != null) {
			player.setLevel(experienceLevel);
		}
		// Load the player's total experience
		Integer totalExperience = playerTotalExperience.get(playerId);
		if (totalExperience != null) {
			player.setTotalExperience(totalExperience);
		}
	}
}
