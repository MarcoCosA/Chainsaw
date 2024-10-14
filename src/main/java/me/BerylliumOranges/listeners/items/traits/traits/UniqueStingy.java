package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.listeners.items.traits.dummyevents.SoulConsumeEvent;
import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import net.md_5.bungee.api.ChatColor;

public class UniqueStingy extends ItemTraitUnique {

	private static final long serialVersionUID = -7709915568319277958L;
	private int freeSoulPeriodItem = 5;
	private int soulsUsedItem = 0;

	private int freeSoulPeriodPotion = 2;
	private int soulsUsedPotion = 0;

	public UniqueStingy() {
		super(60);
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Stingy";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.BLUE;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Every other soul is free";
	}

	@Override
	public String getToolDescription() {
		String val = "(" + (soulsUsedItem == freeSoulPeriodItem ? ChatColor.GOLD : getTraitColor()) + soulsUsedItem + ChatColor.WHITE + ")";
		return ChatColor.WHITE + "Every " + getTraitColor() + freeSoulPeriodItem + ChatColor.WHITE + "th soul " + val
				+ " is free if its from a soul holder on this item";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.INVISIBILITY;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ANY;
	}

	@EventHandler
	public void onSoulConsume(SoulConsumeEvent e) {
		if (getConsumer().equals(e.getUser()) && isPotionActive()) {
			soulsUsedPotion += e.getSoulsToTake();
			e.setSoulsToTake(e.getSoulsToTake() - soulsUsedPotion / freeSoulPeriodPotion);
			soulsUsedPotion = soulsUsedPotion % freeSoulPeriodPotion;
		}

		for (ItemTrait t : TraitCache.getTraitsFromItem(e.getHolderItem())) {
			if (t.equals(this)) {
				soulsUsedItem += e.getSoulsToTake();
				e.setSoulsToTake(e.getSoulsToTake() - soulsUsedItem / freeSoulPeriodItem);
				soulsUsedItem = soulsUsedItem % freeSoulPeriodItem;
			}
		}
	}
}
