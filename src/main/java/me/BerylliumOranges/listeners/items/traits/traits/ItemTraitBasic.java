package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class ItemTraitBasic extends ItemTrait {
	private static final long serialVersionUID = -4291429898837986560L;
	private int amplifier = 0;
	public int percentageBonus = 10;
	public int flatBonus = 1;

	public abstract PotionEffectType getPotionEffectType();

	public ItemTraitBasic() {
		super(180);
	}

	@Override
	public void handlePotionEffectStart() {
		getConsumer().addPotionEffect(new PotionEffect(getPotionEffectType(), getPotionDuration() * 20, amplifier));
	}

	public int getAmplifier() {
		return amplifier;
	}

	public void setAmplifier(int amplifier) {
		this.amplifier = amplifier;
	}

	public int getPercentageBonus() {
		return percentageBonus;
	}

	public void setPercentageBonus(int percentageBonus) {
		this.percentageBonus = percentageBonus;
	}

	public int getFlatBonus() {
		return flatBonus;
	}

	public void setFlatBonus(int flatBonus) {
		this.flatBonus = flatBonus;
	}
}
