package me.BerylliumOranges.bosses;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.bosses.utils.BossUtils;
import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.customEvents.BossTickEvent;
import me.BerylliumOranges.dimensions.BossChunkGenerator;
import me.BerylliumOranges.dimensions.populators.SurfacePopulator;
import me.BerylliumOranges.listeners.purityItems.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.traits.LesserAttackTrait;

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
	public void startFight(Location loc) {

	}

	@Override
	public LivingEntity spawnBoss(Location loc) {
		return null;
	}

	@Override
	public void despawn() {

	}

	@EventHandler
	public void onTick(BossTickEvent e) {
		for (LivingEntity b : bosses) {
			Player p = BossUtils.getNearestPlayer(b.getLocation(), 7);
			if (p != null) {
				Mob mob = (Mob) b;

			}
		}
	}

}
