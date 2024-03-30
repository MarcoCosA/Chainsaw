package me.BerylliumOranges.listeners.items.traits.traits;

import java.util.UUID;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.customEvents.ItemCombineEvent;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class LesserAttackSpeedTrait extends ItemTrait implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;
	public UUID id = UUID.randomUUID();
	public int attackSpeedPercentage = 12;
	public AttributeModifier mod = new AttributeModifier(id, "Attack Speed", attackSpeedPercentage / 100.0, Operation.ADD_SCALAR);

	public LesserAttackSpeedTrait() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Basic Attack Speed";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.DARK_GREEN;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Gives " + getTraitColor() + "Haste I " + ChatColor.WHITE + "for "
				+ ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	@Override
	public String getToolDescription() {
		return getTraitColor() + "+" + attackSpeedPercentage + ChatColor.WHITE + "% attack speed";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.LUCK;
	}

	@Override
	public BukkitRunnable potionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				consumer.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, getPotionDuration() * 20, 0));
			}
		};
	}

	@Override
	public int getRarity() {
		return 1;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.WEAPON_EXCLUSIVE;
	}

	@EventHandler
	public void onCombine(ItemCombineEvent e) {
		if (e.getItem() != null && e.getItem().hasItemMeta()) {
			ItemMeta meta = e.getItem().getItemMeta();
			boolean hasModifier = meta.hasAttributeModifiers() && meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_SPEED) != null
					&& meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_SPEED).contains(mod);

			if (e.getTraits().contains(this)) {
				if (!hasModifier) {
					meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, mod);
				}
			} else {
				if (hasModifier) {
					meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, mod);
				}
			}
			e.getItem().setItemMeta(meta);
		}
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		return false;
	}

	@Override
	public void toolEffect(LivingEntity center) {
		center.damage(1);
	}
}
