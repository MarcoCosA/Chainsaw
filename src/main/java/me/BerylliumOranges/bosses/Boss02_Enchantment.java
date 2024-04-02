package me.BerylliumOranges.bosses;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.bosses.utils.BossBarListener;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.dimensions.chunkgenerators.SkyIslandChunkGenerator;
import me.BerylliumOranges.dimensions.surfaceeditors.SurfacePopulator;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.LesserAttackTrait;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class Boss02_Enchantment extends Boss {

	public Boss02_Enchantment() {
		super(BossType.ENCHANTMENT,
				new SkyIslandChunkGenerator(Arrays.asList(Material.RED_SAND), Arrays.asList(Material.SANDSTONE), Biome.DESERT, 35));
		this.islandSize = 35;
		SurfacePopulator.placeCacti(world, islandSize);
	}

	@Override
	public List<ItemStack> getDrops() {
		return Arrays.asList(ItemBuilder.buildPotionItem(new LesserAttackTrait(), false),
				ItemBuilder.buildItem(new ItemStack(Material.DIAMOND_SWORD), Arrays.asList(new LesserAttackTrait())));
	}

	@Override
	public LivingEntity createDefaultBoss(Location loc) {
		Zombie boss = (Zombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
		boss.setCustomName(ChatColor.GREEN + "Cac King");
		boss.setAdult();
		boss.setSilent(true);
		boss.setCanPickupItems(false);
		return boss;
	}

	@Override
	public void bossIntro(Location loc) {
		bosses.add(summonBoss(loc.clone().add(0, 10, 0)));
		LivingEntity boss = bosses.get(0);
		boss.setAI(false);
		Location location = boss.getLocation();
		location.setPitch(-90.0F);
		boss.teleport(location);

		new BukkitRunnable() {
			@Override
			public void run() {
				world.spawnParticle(Particle.REDSTONE, boss.getEyeLocation(), 20, 0.5, 0.5, 0.5, 0, new DustOptions(Color.LIME, 1));
				if (introAnimationTicks == 30) {
				}
				if (introAnimationTicks > 150) {
					boss.setAI(true);
					Location location = boss.getWorld().getHighestBlockAt(boss.getLocation()).getLocation().add(0, 1, 0);
					location.setPitch(0F);
					boss.teleport(location);
					this.cancel();
					return;
				}
				introAnimationTicks++;
			}

		}.runTaskTimer(PluginMain.getInstance(), 20L, 1L);
	}

	@Override
	public void equipBoss(LivingEntity boss) {
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false));
		boss.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false));
		boss.setRemoveWhenFarAway(false);
		boss.setArrowsInBody(12);

		ItemStack[] armor = new ItemStack[] { createArmorItem(Material.DIAMOND_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 2),
				createArmorItem(Material.DIAMOND_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 2),
				createArmorItem(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 2), new ItemStack(Material.CACTUS) };

		// Set the zombie's armor
		EntityEquipment equipment = boss.getEquipment();
		if (equipment != null) {
			equipment.setArmorContents(armor);
			equipment.setHelmetDropChance(0f);
			equipment.setChestplateDropChance(0f);
			equipment.setLeggingsDropChance(0f);
			equipment.setBootsDropChance(0f);
		}

		try {
			List<Class<? extends ItemTrait>> traitClasses = getBossType().getTraits();

			for (Class<? extends ItemTrait> clazz : traitClasses) {
				ItemTrait trait = clazz.getDeclaredConstructor().newInstance();

				trait.potionRunnable(boss);
			}
		} catch (ReflectiveOperationException roe) {
			roe.printStackTrace();
		}
		new BossBarListener(bosses, BarColor.GREEN, 2);
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
}