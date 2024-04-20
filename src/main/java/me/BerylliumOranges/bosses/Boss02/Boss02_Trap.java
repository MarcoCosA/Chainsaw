package me.BerylliumOranges.bosses.Boss02;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spellcaster.Spell;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.actions.AttackBlockWhip;
import me.BerylliumOranges.bosses.utils.BossBarListener;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.dimensions.chunkgenerators.SkyIslandChunkGenerator;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.LesserAttackTrait;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class Boss02_Trap extends Boss {

	public Boss02_Trap() {
		super(BossType.TRAP, new SkyIslandChunkGenerator(Arrays.asList(Material.STONE, Material.STONE, Material.STONE, Material.ANDESITE),
				Arrays.asList(Material.COAL_BLOCK), Biome.FOREST, 40));
		this.islandSize = 40;
	}

	@Override
	public List<ItemStack> getDrops() {
		return Arrays.asList(ItemBuilder.buildPotionItem(new LesserAttackTrait(), false),
				ItemBuilder.buildItem(new ItemStack(Material.DIAMOND_SWORD), Arrays.asList(new LesserAttackTrait())));
	}

	@Override
	public LivingEntity createDefaultBoss(Location loc) {
		Evoker boss = (Evoker) loc.getWorld().spawnEntity(loc, EntityType.EVOKER);
		boss.setCustomName(ChatColor.GRAY + "Dez Moss");
		boss.setSilent(true);
		boss.setCanPickupItems(false);
		boss.setSpell(Spell.BLINDNESS);
		return boss;
	}

	@Override
	public void bossIntro(Location loc) {
		bosses.add(summonBoss(loc.clone().add(0, 10, 0)));
		LivingEntity boss = bosses.get(0);
		boss.setAI(false);
		Location location = boss.getLocation();
		boss.teleport(location);

		new AttackBlockWhip(boss);
		new BukkitRunnable() {
			Block b = boss.getLocation().getBlock();

			@Override
			public void run() {
				if (introAnimationTicks == 60) {
					b = boss.getLocation().clone().add(boss.getLocation().getDirection()).getBlock();
					b.setType(Material.BARRIER);
					b.getRelative(1, 0, 0).setType(Material.LEVER);
					b.getWorld().playSound(b.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 10F);
				}
				if (introAnimationTicks == 100) {
					b.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, b.getLocation(), 1);
					b.setType(Material.AIR);
					b.getRelative(1, 0, 0).setType(Material.AIR);
				}
				if (introAnimationTicks == 110) {
					LeverPopulator p = new LeverPopulator();
					p.placeTowers(location);
				}
				if (introAnimationTicks > 320) {
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
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true));
		boss.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, true));
		boss.setRemoveWhenFarAway(false);
		boss.setArrowsInBody(12);

		ItemStack[] armor = new ItemStack[] { createArmorItem(Material.DIAMOND_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 2),
				createArmorItem(Material.DIAMOND_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 2),
				createArmorItem(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 2), new ItemStack(Material.TRIPWIRE) };

		boss.getEquipment().setItemInMainHand(new ItemStack(Material.TRIPWIRE_HOOK));
		boss.getEquipment().setItemInOffHand(new ItemStack(Material.TRIPWIRE_HOOK));

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
		new BossBarListener(bosses, BarColor.WHITE, 4);
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