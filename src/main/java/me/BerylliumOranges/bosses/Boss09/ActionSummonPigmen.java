package me.BerylliumOranges.bosses.Boss09;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.BerylliumOranges.bosses.actions.BossAction;

public class ActionSummonPigmen extends BossAction {

	private static final double SPACING = 1.5;
	private static final int SQUAD_FORMATION_WIDTH = 3;
	private static final int SQUAD_FORMATION_LENGTH = 4;

	public ActionSummonPigmen(LivingEntity source) {
		super(source, 100, 100, 0, 0); // Cooldown, range, and damage settings as needed
	}

	@Override
	public void execute(LivingEntity target) {
		spawnPigmenSquad(source.getLocation().add(-5, 0, 0)); // First squad
		spawnPigmenSquad(source.getLocation().add(5, 0, 0)); // Second squad
	}

	private void spawnPigmenSquad(Location startLocation) {
		Location center = source.getLocation();
		source.getWorld().playSound(center, Sound.BLOCK_PORTAL_TRIGGER, 1.0F, 1.0F);
		for (int i = 0; i < SQUAD_FORMATION_LENGTH; i++) {
			for (int j = 0; j < SQUAD_FORMATION_WIDTH; j++) {
				Location spawnLocation = startLocation.clone().add(j * SPACING, 0, i * SPACING);
				PigZombie pigman = spawnLocation.getWorld().spawn(spawnLocation, PigZombie.class, zombie -> {
					zombie.setAdult();
					zombie.setAI(false); // Disable AI initially
					equipPigman(zombie);
				});
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						pigman.setAI(true); // Enable AI after 5 ticks
						pigman.setAngry(true); // Make them permanently angry
						Random random = new Random();
						List<Player> players = pigman.getWorld().getPlayers();
						if (!players.isEmpty()) {
							Player randomPlayer = players.get(random.nextInt(players.size()));
							pigman.setTarget(randomPlayer); // Target a random player
						}
					}
				}, 100 // 5 ticks in milliseconds (20 ticks per second)
				);
			}
		}
	}

	private void equipPigman(PigZombie pigman) {
		// Equip with diamond armor and sword
		pigman.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		pigman.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		pigman.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		pigman.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		pigman.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
		pigman.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
	}

	@Override
	public void tick() {
		// Additional behavior management during the tick if necessary
	}
}
