package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class ItemTraitBasic extends ItemTrait {
	private static final long serialVersionUID = -4291429898837986560L;
	private int amplifier = 0;

	public abstract PotionEffectType getPotionEffectType();

	@Override
	public void handlePotionEffectStart() {
		consumer.addPotionEffect(new PotionEffect(getPotionEffectType(), getPotionDuration() * 20, amplifier));
	}

	public int getAmplifier() {
		return amplifier;
	}

	public void setAmplifier(int amplifier) {
		this.amplifier = amplifier;
	}
}
