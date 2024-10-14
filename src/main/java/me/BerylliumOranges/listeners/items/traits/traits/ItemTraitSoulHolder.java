package me.BerylliumOranges.listeners.items.traits.traits;

import java.awt.Color;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.listeners.items.traits.dummyevents.SoulConsumeEvent;
import net.md_5.bungee.api.ChatColor;

public abstract class ItemTraitSoulHolder extends ItemTrait {
	private static final long serialVersionUID = -1446446382414741314L;

	protected int maxSouls = 0;
	protected int souls = 0;

	public ItemTraitSoulHolder(int initialDuration, int maxSouls) {
		super(initialDuration);
		this.maxSouls = maxSouls;
	}

	@Override
	public String getToolDescription() {
		return "";
	}

	@Override
	public String getFullToolDescription() {
		return getToolDescription() + ChatColor.RESET + getTraitColor() + " " + souls + ChatColor.WHITE + "/" + maxSouls + " stored";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.of(new Color(130, 10, 90));
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ANY;
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.INVISIBILITY;
	}

	public boolean handleSoulAbsorption(LivingEntity soul) {
		if (souls < maxSouls) {
			souls++;
			handleStatsCapture(soul);
			return true;
		}
		return false;
	}

	public void handleSoulUse() {
		souls -= 1;
	}

	public abstract void handleStatsCapture(LivingEntity soul);

	public boolean handleSoulsSold(LivingEntity user, ItemStack soulHolderItem, ItemStack consumerItem, ItemTrait consumer, int numSouls) {
		SoulConsumeEvent event = new SoulConsumeEvent(user, consumerItem, consumer, soulHolderItem, this, numSouls);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled())
			return false;
		numSouls = event.getSoulsToTake();
		if (souls >= numSouls) {
			for (int i = 0; i < numSouls; i++) {
				handleSoulUse();
			}
			return true;
		}
		return false;
	}

	public int getMaxSouls() {
		return maxSouls;
	}

	public int getSouls() {
		return souls;
	}
}
