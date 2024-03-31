package me.BerylliumOranges.bosses;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.BerylliumOranges.bosses.utils.BossBarListener;
import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.dimensions.BossChunkGenerator;
import me.BerylliumOranges.dimensions.populators.SurfacePopulator;
import me.BerylliumOranges.listeners.attacks.RainbowSheepAttack;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.LesserAttackTrait;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;

public class Boss3_Block extends Boss {

	public Boss3_Block() {
		super(BossType.ENCHANTMENT,
				new BossChunkGenerator(Arrays.asList(Material.OBSIDIAN), Arrays.asList(Material.END_STONE), Biome.THE_END, 20));
		this.islandSize = 20;
	}

	@Override
	public List<ItemStack> getDrops() {
		return Arrays.asList(ItemBuilder.buildPotionItem(new LesserAttackTrait(), false),
				ItemBuilder.buildItem(new ItemStack(Material.DIAMOND_SWORD), Arrays.asList(new LesserAttackTrait())));
	}

	@Override
	public void bossIntro(Location loc) {
		spawnBoss(loc);
	}

	@Override
	public LivingEntity spawnBoss(Location loc) {
		// Spawn a zombie at the provided location
		Enderman bottom = null;
		for (int i = 0; i < 10; i++) {
			Enderman enderman = (Enderman) loc.getWorld().spawnEntity(loc, EntityType.ENDERMAN);
			enderman.setCanPickupItems(false);
			bosses.add(enderman); // Add each Enderman to the bosses list

			if (bottom != null) {
				bottom.setPassenger(enderman); // Stack the Enderman on the previous one
			}
			bottom = enderman; // Update the bottom Enderman to the current one for the next iteration
		}

		try {
			List<Class<? extends ItemTrait>> traitClasses = getBossType().getTraits();

			for (Class<? extends ItemTrait> clazz : traitClasses) {
				ItemTrait trait = clazz.getDeclaredConstructor().newInstance();

				trait.potionRunnable(bottom);
			}
		} catch (ReflectiveOperationException roe) {
			roe.printStackTrace();
		}

		new RainbowSheepAttack(bottom);

		new BossBarListener(bosses, BarColor.PURPLE, 2);

		return bottom;
	}

	private ItemStack createArmorItem(Material material, Enchantment enchantment, int level) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.addEnchant(enchantment, level, true);
			meta.setUnbreakable(true);
			item.setItemMeta(meta);
		}
		return item;
	}

	@Override
	public void despawn() {

	}

	@EventHandler
	public void onTick(TickEvent e) {
		for (LivingEntity b : bosses) {
			Player p = BossUtils.getNearestPlayer(b.getLocation(), 7);
			if (p != null) {
				Mob mob = (Mob) b;

			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByBlockEvent e) {
		if (bosses.contains(e.getEntity()) && (e.getCause().equals(DamageCause.THORNS) || e.getCause().equals(DamageCause.SUFFOCATION))) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEndermanDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (bosses.contains(entity)) {
			bosses.remove(entity); // Remove the deceased Enderman from the list

			if (entity.getVehicle() != null && entity.getVehicle() instanceof Enderman) {
				LivingEntity above = (LivingEntity) entity.getVehicle();
				above.eject(); // Remove the above Enderman from being a passenger

				if (entity.getPassenger() != null && entity.getPassenger() instanceof Enderman) {
					LivingEntity below = (LivingEntity) entity.getPassenger();
					entity.eject(); // Eject the below Enderman
					above.setPassenger(below); // Set the below Enderman as the new passenger of the above Enderman
				}
			}
		}
	}
}