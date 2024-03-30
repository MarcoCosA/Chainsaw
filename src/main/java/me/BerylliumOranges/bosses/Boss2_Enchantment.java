package me.BerylliumOranges.bosses;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.bosses.utils.BossUtils.BossType;
import me.BerylliumOranges.dimensions.BossChunkGenerator;
import me.BerylliumOranges.dimensions.populators.SurfacePopulator;

public class Boss2_Enchantment extends Boss {

	public Boss2_Enchantment() {
		super(BossType.ENCHANTMENT,
				new BossChunkGenerator(Arrays.asList(Material.RED_SAND), Arrays.asList(Material.SANDSTONE), Biome.DESERT, 35));
		this.islandSize = 35;
		SurfacePopulator.placeCacti(world, islandSize);
	}

	@Override
	public List<ItemStack> getDrops() {
		return null;
	}

	@Override
	public void bossIntro(Location loc) {

	}

	@Override
	public LivingEntity spawnBoss(Location loc) {
		return null;
	}

	@Override
	public void despawn() {

	}

}