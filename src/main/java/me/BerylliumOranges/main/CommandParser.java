package me.BerylliumOranges.main;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.Boss11.Screen;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.bosses.utils.DungeonChestGenerator;
import me.BerylliumOranges.dimensions.CustomChunkGenerator;
import me.BerylliumOranges.misc.MiscItems;

public class CommandParser {

	public static boolean findCommand(CommandSender sender, Command cmd, String label, String[] args) {
		{
			if (sender instanceof Player && sender.isOp() && cmd.getName().equalsIgnoreCase("createdimension")) {
				if (args.length < 1)
					return false;
				WorldCreator creator = new WorldCreator(args[0]);
				creator.generator(new CustomChunkGenerator());
				World w = Bukkit.getServer().createWorld(creator);
				sender.sendMessage(ChatColor.GREEN + "Created " + args[0]);
				DungeonChestGenerator.placeChests(w);

				return true;
			}
			if (sender instanceof Player && sender.isOp() && cmd.getName().equalsIgnoreCase("screen")) {
				Player player = (Player) sender;

				new Screen(player.getLocation(), args[0], BlockFace.valueOf(args[1].toUpperCase()));

				return true;
			} else if (sender instanceof Player && sender.isOp() && cmd.getName().equalsIgnoreCase("wall")) {
				Player player = (Player) sender;

				Screen.makeWall(player.getLocation(), args[0], BlockFace.valueOf(args[1].toUpperCase()), Integer.parseInt(args[2]));

				return true;
			}

			if (sender instanceof Player && sender.isOp() && cmd.getName().equalsIgnoreCase("deletedimension")) {
				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "Usage: /deletedimension <worldName>");
					return true;
				}
				String worldName = args[0];
				World world = Bukkit.getServer().getWorld(worldName);
				if (world == null) {
					sender.sendMessage(ChatColor.RED + "The world " + worldName + " does not exist.");
					return true;
				}

				// Ensure no players are in the world
				if (!world.getPlayers().isEmpty()) {
					sender.sendMessage(ChatColor.RED + "Cannot delete " + worldName + " because it contains players.");
					return true;
				}

				File worldContainer = Bukkit.getServer().getWorldContainer();
				File worldFolder = new File(worldContainer, worldName);

				// Attempt to unload the world
				if (Bukkit.getServer().unloadWorld(world, true)) {
					sender.sendMessage(ChatColor.GREEN + "Successfully unloaded " + worldName + ". Please delete the folder manually: "
							+ worldFolder.getPath());
					// Note: At this point, you can attempt to delete the files, but it's risky.
					// Manual deletion is recommended.
				} else {
					sender.sendMessage(ChatColor.RED + "Failed to unload " + worldName + ".");
				}

				return true;
			}

			if (sender instanceof Player && sender.isOp() && cmd.getName().equalsIgnoreCase("listdimensions")) {
				sender.sendMessage(ChatColor.YELLOW + "List of dimensions: ");
				for (World s : Bukkit.getServer().getWorlds()) {
					sender.sendMessage(ChatColor.WHITE + "- " + s.getName());
				}
				return true;
			}

			if (sender instanceof Player && sender.isOp() && cmd.getName().equalsIgnoreCase("spawnboss")) {
				Bukkit.broadcastMessage("I see it " + args.length);
				int num = Integer.parseInt(args[0]);
				Bukkit.broadcastMessage("I see " + num);
				try {
					Bukkit.broadcastMessage("Spawning: " + BossType.values()[num].getName());
					Boss boss = BossType.values()[num].getBossClass().getDeclaredConstructor().newInstance();
				} catch (ReflectiveOperationException roe) {
					roe.printStackTrace();
				}
				return false;

			}
			if (sender instanceof Player && sender.isOp() && cmd.getName().equalsIgnoreCase("goto")) {
				if (args.length < 1) {
					((Player) sender).getWorld().generateTree(((Player) sender).getLocation(), TreeType.CHERRY);
					return false;
				}
				for (World s : Bukkit.getServer().getWorlds()) {
					if (args[0].equalsIgnoreCase(s.getName())) {
						sender.sendMessage(ChatColor.YELLOW + "Teleporting to " + s.getName());
						if (sender instanceof Player) {
							((Player) sender).teleport(new Location(s, 0, s.getHighestBlockYAt(0, 0), 0));
						}
						break;
					}
				}
				return true;
			}
			if (sender instanceof Player && cmd.getName().equalsIgnoreCase("dummy")) {
				return true;
			} else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("potions")) {
//				sendPotions((Player) sender);
				return true;
			}

			if (sender instanceof Player && cmd.getName().equalsIgnoreCase("checkitem")) {
				sender.sendMessage(((Player) sender).getItemInHand().getItemMeta().getLocalizedName());
				return true;
			}

			if (sender instanceof Player && sender.isOp() && cmd.getName().equalsIgnoreCase("getitems")) {
				getItems(sender, args);
				return true;
			}
		}
		return false;
	}

//	public static void sendPotions(Player p) {
//		boolean found = false;
//		for (PotionTraitKey pot : PurityItemAbstract.effectedEntities) {
//			if (pot.getOwner().equals(p)) {
//				if (!found) {
//					p.sendMessage(ChatColor.GREEN + "Active Potions");
//				} else {
//					p.sendMessage("");
//				}
//				p.sendMessage(" -" + pot.getTrait().getName() + " " + ChatColor.WHITE
//						+ ItemBuilder.getTimeInMinutes(pot.getTicksLeft() / 20));
//				for (int i = 0;; i++) {
//					String s = pot.getTrait().getPotionTraitDescription().get(i);
//					if (i == pot.getTrait().getPotionTraitDescription().size() - 1) {
//						p.sendMessage(s.substring(0,
//								s.length() - ItemBuilder.getTimeInMinutes(pot.getTrait().getPotionSeconds()).length()));
//						break;
//					} else
//						p.sendMessage(s);
//				}
//
//				found = true;
//			}
//		}
//		if (!found)
//			p.sendMessage(ChatColor.RED + "No Active Potions");
//	}

	public static boolean getItems(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		Inventory inv = Bukkit.createInventory(p, 54, ChatColor.GREEN + "Plugin Items");

		inv.addItem(MiscItems.getSword());
		inv.addItem(MiscItems.getFreeChestplate());

		for (ItemStack item : MiscItems.loadItems()) {
			inv.addItem(item);
		}

		for (ItemStack item : MiscItems.loadPotions()) {
			inv.addItem(item);
		}

		p.openInventory(inv);
		return true;
	}

	public static boolean GM(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		Player pToSet = null;
		GameMode gm = null;
		if (args.length == 1)
			pToSet = (Player) sender;
		else {
			String name = args[1];
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.getName().contains(name)) {
					pToSet = p;
					if (p.getName().equals(name))
						break;
				}
			}
		}

		if (args[0].contains("0")) {
			gm = GameMode.SURVIVAL;
		} else if (args[0].contains("1")) {
			gm = GameMode.CREATIVE;
		} else if (args[0].contains("2")) {
			gm = GameMode.ADVENTURE;
		} else if (args[0].contains("3")) {
			gm = GameMode.SPECTATOR;
		}
		if (gm == null || pToSet == null)
			return false;
		pToSet.setGameMode(gm);

		return true;
	}
}
