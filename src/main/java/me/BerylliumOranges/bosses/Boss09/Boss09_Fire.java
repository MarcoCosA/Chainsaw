package me.BerylliumOranges.bosses.Boss09;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.utils.BossBarListener;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.dimensions.chunkgenerators.SkyIslandChunkGenerator;
import me.BerylliumOranges.dimensions.surfaceeditors.SurfacePopulator;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.LesserAttackTrait;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class Boss09_Fire extends Boss {

	public Boss09_Fire() {
		super(BossType.FIRE, new SkyIslandChunkGenerator(Arrays.asList(Material.GRASS_BLOCK),
				Arrays.asList(Material.STONE, Material.STONE, Material.STONE, Material.ANDESITE), Biome.PLAINS, 75));
		this.islandSize = 75;
		SurfacePopulator.placeTrees(world, islandSize);
	}

	@Override
	public List<ItemStack> getDrops() {
		return Arrays.asList(ItemBuilder.buildPotionItem(new LesserAttackTrait(), false),
				ItemBuilder.buildItem(new ItemStack(Material.DIAMOND_SWORD), Arrays.asList(new LesserAttackTrait())));
	}

	@Override
	public LivingEntity createDefaultBoss(Location loc) {
		PigZombie boss = (PigZombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIFIED_PIGLIN);
		boss.setCustomName(bossType.getColor() + "Pigzard");
		boss.setAdult();
		boss.setSilent(true);
		boss.setCanPickupItems(false);
		boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(150);
		boss.setHealth(150);
		return boss;
	}

	@Override
	public void bossIntro(Location loc) {
		introAnimationTicks = -100;
		bosses.add(summonBoss(loc.clone().add(0, 10, 0)));
		LivingEntity boss = bosses.get(0);
		boss.setAI(false);
		Location location = boss.getLocation();
		boss.teleport(location);

		new BukkitRunnable() {
			@Override
			public void run() {
				world.spawnParticle(Particle.SOUL_FIRE_FLAME, boss.getEyeLocation(), 1, 0.5, 0.5, 0.5);
				if (introAnimationTicks == 30) {
					new ActionSummonPigmen(boss);
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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBossDamage(EntityDamageEvent event) {
		if (!bosses.isEmpty() && event.getEntity().equals(bosses.get(0))) {
			LivingEntity boss = (LivingEntity) event.getEntity();
			if (stage == 0) {
				double healthAfterDamage = boss.getHealth() - event.getFinalDamage();
				if (healthAfterDamage <= 0) {
					event.setCancelled(true);
					transitionToStageTwo(boss);
					stage++;
				}
			} else if (stage == 1) {
				double healthAfterDamage = boss.getHealth() - event.getFinalDamage();
				if (healthAfterDamage <= 0) {
					event.setCancelled(true); // Cancel the death
					transitionToStageThree(boss);
					stage++;
				}
			}
		}
	}

	private void transitionToStageTwo(LivingEntity boss) {
		boss.setHealth(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		bossBar.setOverrideName(ChatColor.RED + ChatColor.stripColor(boss.getCustomName()) + " the " + ChatColor.BOLD + "Undying");
		boss.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, boss.getLocation(), 10);
		boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
	}

	private void transitionToStageThree(LivingEntity boss) {
		boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
		boss.setHealth(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		bossBar.setOverrideName(
				ChatColor.RED + "" + ChatColor.BOLD + ChatColor.stripColor(boss.getCustomName()).toUpperCase() + " HARBINGER OF DOOM");
		boss.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, boss.getLocation(), 10);
		boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
		new AttackMeteorStrike(boss);
	}

	@Override
	public void equipBoss(LivingEntity boss) {
		boss.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 5, true));
		boss.setRemoveWhenFarAway(false);
		boss.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
		ItemStack[] armor = new ItemStack[] { createArmorItem(Material.NETHERITE_BOOTS), createArmorItem(Material.NETHERITE_LEGGINGS),
				createArmorItem(Material.NETHERITE_CHESTPLATE), new ItemStack(Material.RED_BANNER) };

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
		bossBar = new BossBarListener(bosses, BarColor.GREEN, 4);
		if (!(boss instanceof Player))
			bossBar.setOverrideName(bossBar.getBar().getTitle() + ": Rekindled");
	}

	private ItemStack createArmorItem(Material material) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(Enchantment.PROTECTION, 4);
		item.addUnsafeEnchantment(Enchantment.FIRE_PROTECTION, 6);
		item.addUnsafeEnchantment(Enchantment.BLAST_PROTECTION, 6);
		item.addUnsafeEnchantment(Enchantment.PROJECTILE_PROTECTION, 6);
		ItemMeta meta = item.getItemMeta();
		item.setItemMeta(meta);
		return item;
	}

	public ArrayList<LivingEntity> generals = new ArrayList<>();

	public void createGenerals(Location loc) {
		PigZombie boss1 = (PigZombie) loc.getWorld().spawnEntity(loc.clone().add(30, -3, 30), EntityType.ZOMBIFIED_PIGLIN);
		boss1.setCustomName(bossType.getColor() + "Gen. Zil");
		boss1.setAdult();
		boss1.setSilent(true);
		boss1.setCanPickupItems(false);
		boss1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(75);
		boss1.setHealth(75);

		generals.add(boss1);
	}

}
