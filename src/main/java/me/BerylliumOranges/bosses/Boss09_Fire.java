package me.BerylliumOranges.bosses;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
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

import me.BerylliumOranges.bosses.actions.AttackCactus;
import me.BerylliumOranges.bosses.actions.AttackMeteorStrike;
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
		super(BossType.FIRE, new SkyIslandChunkGenerator(Arrays.asList(Material.DIRT), Arrays.asList(Material.STONE), Biome.PLAINS, 80));
		this.islandSize = 80;
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
		boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500);
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
		location.setPitch(-90.0F);
		boss.teleport(location);

		new BukkitRunnable() {
			@Override
			public void run() {
				world.spawnParticle(Particle.REDSTONE, boss.getEyeLocation(), 20, 0.5, 0.5, 0.5, 0,
						new DustOptions(org.bukkit.Color.LIME, 1));
				if (introAnimationTicks == 30) {
					SurfacePopulator.placeCacti(world, islandSize);
					new AttackCactus(boss);
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
				if (healthAfterDamage / boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() <= 0.5) {
					event.setCancelled(true);
					transitionToStageTwo(boss);
				}
			} else if (stage == 1) {
				double healthAfterDamage = boss.getHealth() - event.getFinalDamage();
				if (healthAfterDamage <= 0) {
					event.setCancelled(true); // Cancel the death
					transitionToStageThree(boss);
				}
			}
		}
	}

	private void transitionToStageTwo(LivingEntity boss) {
		boss.setHealth(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		bossBar.setOverrideName(ChatColor.RED + ChatColor.stripColor(boss.getCustomName()) + " THE UNDYING");
		boss.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, boss.getLocation(), 10);
		boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
	}

	private void transitionToStageThree(LivingEntity boss) {
		boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
		boss.setHealth(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		bossBar.setOverrideName(ChatColor.BOLD + "" + ChatColor.RED + ChatColor.stripColor(boss.getCustomName()) + " HARBINGER OF DOOM");
		boss.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, boss.getLocation(), 10);
		boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
		new AttackMeteorStrike(boss);
	}

	@Override
	public void equipBoss(LivingEntity boss) {
		boss.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 5, true));
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
		bossBar = new BossBarListener(bosses, BarColor.GREEN, 2);
		if (!(boss instanceof Player))
			bossBar.setOverrideName(bossBar.getBar().getTitle() + ": Rekindled");
	}

	private ItemStack createArmorItem(Material material) {
		ItemStack item = new ItemStack(material);
		item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 6);
		item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
		item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 6);
		ItemMeta meta = item.getItemMeta();
		item.setItemMeta(meta);
		return item;
	}

}
