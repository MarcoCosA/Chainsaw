package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import net.md_5.bungee.api.ChatColor;

public class UniqueDurationIncreaser extends ItemTraitUnique {

	private static final long serialVersionUID = -7709915568319277958L;

	public UniqueDurationIncreaser() {
		super(180);
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Duration Increaser";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.GOLD;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "TBD " + getTraitColor() + "TBD";
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Attacking increases the duration of all other potions on this item by 1 second";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.FIRE_RESISTANCE;
	}

	@Override
	public void handlePotionEffectStart() {
//		getConsumer().addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, potionDuration, 1));
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ANY;
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		if (op.getEvent() instanceof EntityDamageByEntityEvent && !victim) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) op.getEvent();
			if (e.getEntity() instanceof LivingEntity) {
				if (owner instanceof Player) {
					Player player = (Player) e.getDamager();
					if (player.getAttackCooldown() == 1.0) {
						for (ItemTrait t : TraitCache.getTraitsFromItem(item)) {
							if (t != this) {
								t.setPotionDuration(t.getPotionDuration() + 1);
							}
						}
					}
				}
			}
		}
		return false;
	}
}
