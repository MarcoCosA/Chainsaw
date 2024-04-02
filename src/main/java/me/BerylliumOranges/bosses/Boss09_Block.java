package me.BerylliumOranges.bosses;

import java.awt.Color;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.BerylliumOranges.bosses.utils.BossBarListener;
import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.bosses.utils.PlayerStateSaver;
import me.BerylliumOranges.dimensions.chunkgenerators.CubeChunkGenerator;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.LesserAttackTrait;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class Boss09_Block extends Boss {

	public static final List<String> characterNames = Arrays.asList(ChatColor.of(new Color(128, 0, 128)) + "Geometry Gordon", // Purple
			ChatColor.of(new Color(147, 112, 219)) + "Craig", // Medium Purple
			ChatColor.of(new Color(153, 50, 204)) + "Gary", // Dark Orchid
			ChatColor.of(new Color(186, 85, 211)) + "Bubble Bob", // Medium Orchid
			ChatColor.of(new Color(218, 112, 214)) + "Ned the Nook Nabber", // Orchid
			ChatColor.of(new Color(221, 160, 221)) + "Ted the Tweaker", // Plum
			ChatColor.of(new Color(238, 130, 238)) + "Harry the Hexahead", // Violet
			ChatColor.of(new Color(255, 0, 255)) + "Carl the Cloud Conjurer", // Magenta / Fuchsia
			ChatColor.of(new Color(139, 0, 139)) + "Pete Puddle Plodder", // Dark Magenta
			ChatColor.of(new Color(216, 191, 216)) + "Smelly Sammy" // Thistle
	);

	public Boss09_Block() {
		super(BossType.BLOCK,
				new CubeChunkGenerator(Arrays.asList(Material.OBSIDIAN), Arrays.asList(Material.END_STONE), Biome.THE_END, 20));
		this.islandSize = 20;
	}

	@Override
	public List<ItemStack> getDrops() {
		return Arrays.asList(ItemBuilder.buildPotionItem(new LesserAttackTrait(), false),
				ItemBuilder.buildItem(new ItemStack(Material.DIAMOND_SWORD), Arrays.asList(new LesserAttackTrait())));
	}

	@Override
	public LivingEntity summonBoss(Location loc) {
		LivingEntity boss = BossUtils.getPlayerSubstitute(bossType);

		if (boss != null) {
			PlayerStateSaver.savePlayerState((Player) boss);
		} else
			boss = createDefaultBoss(loc);
		bosses.add(boss);
		equipBoss(boss);
		return boss;
	}

	@Override
	public LivingEntity createDefaultBoss(Location loc) {
		Enderman bottom = null;
		for (int i = 0; i < 10; i++) {
			Enderman enderman = (Enderman) loc.getWorld().spawnEntity(loc, EntityType.ENDERMAN);

			enderman.setCanPickupItems(false);
			enderman.setCustomName(characterNames.get(i));
			bosses.add(enderman); // Add each Enderman to the bosses list

			if (bottom != null) {
				bottom.addPassenger(enderman); // Stack the Enderman on the previous one
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
		return bottom;
	}

	@Override
	public void bossIntro(Location loc) {
		bosses.add(summonBoss(loc));

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