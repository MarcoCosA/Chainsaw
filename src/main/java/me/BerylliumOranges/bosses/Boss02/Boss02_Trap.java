package me.BerylliumOranges.bosses.Boss02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spellcaster.Spell;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Lever;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.bosses.Boss;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.dimensions.chunkgenerators.SkyIslandChunkGenerator;
import me.BerylliumOranges.listeners.BossBarListener;
import me.BerylliumOranges.listeners.items.traits.traits.BasicAttackTrait;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class Boss02_Trap extends Boss {
	public static final LeverEffectType[] STAGE_1_TOWERS = LeverEffectType.values();

	public Boss02_Trap() {
		super(BossType.TRAP, new SkyIslandChunkGenerator(Arrays.asList(Material.STONE, Material.STONE, Material.STONE, Material.ANDESITE),
				Arrays.asList(Material.COAL_BLOCK), Biome.FOREST, 60));
		this.islandSize = 60;
		world.setTime(24000);
	}

	@Override
	public List<ItemStack> getDrops() {
		return Arrays.asList(ItemBuilder.buildPotionItem(new BasicAttackTrait(), false),
				ItemBuilder.buildItem(new ItemStack(Material.DIAMOND_SWORD), Arrays.asList(new BasicAttackTrait())));
	}

	@Override
	public LivingEntity createDefaultBoss(Location loc) {
		Evoker boss = (Evoker) loc.getWorld().spawnEntity(loc, EntityType.EVOKER);
		boss.setCustomName(ChatColor.GRAY + "TBD");
		boss.setSilent(true);
		boss.setCanPickupItems(false);
		boss.setSpell(Spell.BLINDNESS);
		return boss;
	}

	MakeInvulerable invulLever = null;

	@Override
	public void bossIntro(Location loc) {
		summonBoss(loc.clone().add(0, 10, 0));
		LivingEntity boss = bosses.get(0);
		boss.setAI(false);
		Location location = boss.getLocation();
		boss.teleport(location);

		new BukkitRunnable() {
			Block b = boss.getLocation().getBlock();

			@Override
			public void run() {
				introAnimationTicks++;
				if (introAnimationTicks == 60) {
					b = boss.getLocation().add(0, 1, 1).getBlock();
					b.setType(Material.LEVER);
					b.getRelative(0, 0, 1).setType(TowerPopulator.TOWER_UNUSED);
					b.getWorld().playSound(b.getLocation(), Sound.BLOCK_STONE_PLACE, 10, 1F);
				}
				if (introAnimationTicks == 85) {
					if (b.getType().equals(Material.LEVER)) {
						BlockState state = b.getState();
						Lever lever = (Lever) state.getData();
						lever.setPowered(true);
						state.setData(lever);
						state.update(true);
					}
					invulLever = new MakeInvulerable(boss);
					invulLever.apply(b.getLocation(), b.getRelative(0, 0, 1));
					b.getWorld().playSound(b.getLocation(), Sound.BLOCK_LEVER_CLICK, 10, 1F);
					b.getWorld().spawnParticle(Particle.END_ROD, b.getLocation().clone().add(0.5, 0.5, 0.5), 10);
					invulLever.triggerOff();

				}
				if (introAnimationTicks == 100) {
					b.setType(Material.AIR);
					b.getWorld().spawnParticle(Particle.BLOCK, b.getLocation().clone().add(0.5, 0.5, 1.5), 30,
							Bukkit.createBlockData(b.getRelative(0, 0, 1).getType()));
					b.getRelative(0, 0, 1).setType(Material.AIR);
				}
				if (introAnimationTicks == 110) {
					TowerPopulator p = new TowerPopulator();
					ArrayList<LeverEffect> types = new ArrayList<>();
					for (LeverEffectType type : STAGE_1_TOWERS)
						types.add(type.createEffect());

					Collections.shuffle(types, new Random(1));
					types.add(types.set((int) (Math.random() * types.size()), invulLever));
					p.makeTowers(location, types);
					p.levers = types;
				}
				if (introAnimationTicks == 250) {
					new AttackBlockWhip(boss);
				}
				if (introAnimationTicks > 320) {
					boss.setAI(true);
					Location location = boss.getWorld().getHighestBlockAt(boss.getLocation()).getLocation().add(0, 1, 0);
					location.setPitch(0F);
					boss.teleport(location);
					this.cancel();
					return;
				}
			}
		}.runTaskTimer(PluginMain.getInstance(), 20L, 1L);
	}

	@Override
	public void equipBoss(LivingEntity boss) {
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true));
		boss.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 0, true));
		boss.setRemoveWhenFarAway(false);
		boss.setArrowsInBody(12);

		ItemStack[] armor = new ItemStack[] { createArmorItem(Material.DIAMOND_BOOTS, Enchantment.PROTECTION, 2),
				createArmorItem(Material.DIAMOND_LEGGINGS, Enchantment.PROTECTION, 2),
				createArmorItem(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION, 2), new ItemStack(Material.TRIPWIRE) };

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

	// Stops the Evoker Boss from spawning Vexes
	@EventHandler
	public void onVex(EntitySpellCastEvent e) {
		if (bosses.contains(e.getEntity())) {
			e.setCancelled(true);
		}
	}

}