package me.BerylliumOranges.bosses;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.dimensions.BossChunkGenerator;
import me.BerylliumOranges.dimensions.populators.SurfacePopulator;
import me.BerylliumOranges.listeners.attacks.CactusAttack;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.LesserAttackTrait;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;

public class Boss1_Thorns extends Boss {

	public Boss1_Thorns() {
		super(BossType.THORNS, new BossChunkGenerator(Arrays.asList(Material.SAND), Arrays.asList(Material.SANDSTONE), Biome.DESERT, 35));
		this.islandSize = 35;
		SurfacePopulator.placeCacti(world, islandSize);
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
		Zombie zombie = (Zombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);

		// Ensure the zombie is an adult, is silent, and does not drop items
		zombie.setAdult();
		zombie.setSilent(true);
		zombie.setCanPickupItems(false);

		zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false));
		zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false));

		ItemStack[] armor = new ItemStack[] { createArmorItem(Material.DIAMOND_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 2),
				createArmorItem(Material.DIAMOND_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 2),
				createArmorItem(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 2), new ItemStack(Material.CACTUS) };

		// Set the zombie's armor
		EntityEquipment equipment = zombie.getEquipment();
		if (equipment != null) {
			equipment.setArmorContents(armor);
			equipment.setHelmetDropChance(0f);
			equipment.setChestplateDropChance(0f);
			equipment.setLeggingsDropChance(0f);
			equipment.setBootsDropChance(0f);
		}

		bosses.add(zombie);

		try {
			List<Class<? extends ItemTrait>> traitClasses = getBossType().getTraits();

			for (Class<? extends ItemTrait> clazz : traitClasses) {
				ItemTrait trait = clazz.getDeclaredConstructor().newInstance();

				trait.potionRunnable(zombie);
			}
		} catch (ReflectiveOperationException roe) {
			roe.printStackTrace();
		}

		new CactusAttack(zombie);

		return zombie;
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

}
