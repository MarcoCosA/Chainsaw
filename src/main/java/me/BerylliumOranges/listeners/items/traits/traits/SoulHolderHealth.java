package me.BerylliumOranges.listeners.items.traits.traits;

import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;

import net.md_5.bungee.api.ChatColor;

public class SoulHolderHealth extends ItemTraitSoulHolder {

	private static final long serialVersionUID = -7709915568319277958L;
	private int storePercentage = 20;

	private ArrayList<Double> storedSouls = new ArrayList<Double>();

	public SoulHolderHealth() {
		super(180, 5);
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Soul Holder";
	}

	@Override
	public String getPotionDescription() {
		String val = "(" + ChatColor.RED + Math.floor(getStoredHP() * 100.0) / 100.0 + ChatColor.WHITE + ")";
		return ChatColor.WHITE + "" + getTraitColor() + storePercentage + ChatColor.WHITE + "% of souls' max health " + val
				+ " is added to yours";
	}

	@Override
	public void handlePotionEffectStart() {
		getConsumer().setAbsorptionAmount(getConsumer().getAbsorptionAmount() + getStoredHP());
	}

	@Override
	public void handleStatsCapture(LivingEntity soul) {
		storedSouls.add(soul.getMaxHealth() * storePercentage / 100.0);
	}

	@Override
	public void handleSoulUse() {
		storedSouls.remove(0);
		super.handleSoulUse();
	}

	public double getStoredHP() {
		double num = 0;
		for (Double i : storedSouls) {
			num += i;
		}
		return num;
	}
}
