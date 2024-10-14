package me.BerylliumOranges.listeners.items.traits.traits;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import net.md_5.bungee.api.ChatColor;

public abstract class ItemTraitSoulUser extends ItemTrait {
	private static final long serialVersionUID = 304743326888941162L;

	protected int soulCost = 0;

	public ItemTraitSoulUser(int initialDuration, int soulCost) {
		super(initialDuration);
		this.soulCost = soulCost;
	}

	@Override
	public String getFullToolDescription() {
		return getToolDescription() + " (costs " + getTraitColor() + soulCost + ChatColor.WHITE + " soul)";
	}

	public boolean handleSoulUseEvent(LivingEntity user, ItemStack consumerItem) {
		for (Entry<ItemStack, List<ItemTrait>> entry : TraitCache.getItemTraitMapFromEntity(user).entrySet()) {
			for (ItemTrait t : entry.getValue()) {
				if (t instanceof ItemTraitSoulHolder) {
					ItemTraitSoulHolder h = (ItemTraitSoulHolder) t;
					if (h.handleSoulsSold(user, entry.getKey(), consumerItem, this, soulCost)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}