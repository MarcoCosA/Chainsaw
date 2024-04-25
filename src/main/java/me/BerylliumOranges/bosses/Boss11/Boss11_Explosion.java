package me.BerylliumOranges.bosses.Boss11;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Illager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.actions.AttackGravityTeleport;
import me.BerylliumOranges.bosses.utils.BossBarListener;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.dimensions.chunkgenerators.SkyIslandChunkGenerator;
import me.BerylliumOranges.dimensions.surfaceeditors.SurfacePopulator;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.LesserAttackTrait;
import me.BerylliumOranges.listeners.items.traits.traits.NormalArmorPenetrationTrait;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class Boss11_Explosion extends Boss {

	public Boss11_Explosion() {
		super(BossType.EXPLOSION,
				new SkyIslandChunkGenerator(Arrays.asList(Material.DIRT), Arrays.asList(Material.STONE), Biome.PLAINS, 55));
		this.islandSize = 55;
		SurfacePopulator.placeTrees(world, islandSize);
	}

	@Override
	public List<ItemStack> getDrops() {
		return Arrays.asList(ItemBuilder.buildPotionItem(new LesserAttackTrait(), false),
				ItemBuilder.buildItem(new ItemStack(Material.DIAMOND_SWORD), Arrays.asList(new LesserAttackTrait())));
	}

	@Override
	public LivingEntity createDefaultBoss(Location loc) {
		Illager boss = (Illager) loc.getWorld().spawnEntity(loc, EntityType.VINDICATOR);
		boss.setCustomName(ChatColor.AQUA + "Killager");
		boss.setSilent(true);
		boss.setCanPickupItems(false);
		return boss;
	}

	@Override
	public void bossIntro(Location loc) {

	}

	@Override
	public void equipBoss(LivingEntity boss) {
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false));
		boss.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 2, false));

		boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
		boss.setHealth(200);

		ItemStack[] armor = new ItemStack[] { createArmorItem(Material.DIAMOND_BOOTS), createArmorItem(Material.DIAMOND_LEGGINGS),
				createArmorItem(Material.DIAMOND_CHESTPLATE), createArmorItem(Material.DIAMOND_HELMET) };

		EntityEquipment equipment = boss.getEquipment();
		if (equipment != null) {
			equipment.setArmorContents(armor);
			equipment.setHelmetDropChance(0f);
			equipment.setChestplateDropChance(0f);
			equipment.setLeggingsDropChance(0f);
			equipment.setBootsDropChance(0f);

			ItemStack sword = new ItemStack(Material.IRON_AXE);
			sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 3);
			equipment.setItemInMainHand(
					ItemBuilder.buildItem(new ItemStack(Material.IRON_SWORD), Arrays.asList(new NormalArmorPenetrationTrait())));
		}

		bosses.add(boss);

		try {
			List<Class<? extends ItemTrait>> traitClasses = getBossType().getTraits();
			for (Class<? extends ItemTrait> clazz : traitClasses) {
				ItemTrait trait = clazz.getDeclaredConstructor().newInstance();
				trait.potionRunnable(boss);
			}
		} catch (ReflectiveOperationException roe) {
			roe.printStackTrace();
		}
		new AttackGravityTeleport(boss);

		new BossBarListener(bosses, BarColor.RED, 3);
	}

	private ItemStack createArmorItem(Material material) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.addEnchant(Enchantment.PROTECTION, 4, true);
			meta.addEnchant(Enchantment.BLAST_PROTECTION, 4, true);
			meta.addEnchant(Enchantment.FIRE_PROTECTION, 4, true);
			meta.setUnbreakable(true);
			item.setItemMeta(meta);
		}
		return item;
	}

	@Override
	public void despawn() {

	}
}