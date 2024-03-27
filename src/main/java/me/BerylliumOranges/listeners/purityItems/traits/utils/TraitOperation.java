package me.BerylliumOranges.listeners.purityItems.traits.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.traits.ItemTrait;

public class TraitOperation {

	Event event;
	LivingEntity owner;
	List<LivingEntity> targets;
	LinkedHashMap<ItemStack, List<ItemTrait>> ownerItemTraitsMap = new LinkedHashMap<>();
	List<LinkedHashMap<ItemStack, List<ItemTrait>>> targetsItemTraitsMap;
	Map<ItemTrait, Boolean> traitExecutionStatus = new HashMap<>();
	boolean victim = false;

	// Simplified constructor for single target
	public TraitOperation(Event event, LivingEntity owner, LivingEntity target) {
		this(event, owner, target != null ? List.of(target) : Collections.emptyList(), TraitCache.getItemTraitMapFromEntity(owner),
				target != null ? List.of(TraitCache.getItemTraitMapFromEntity(target)) : Collections.emptyList());
	}

	/**
	 * Initializes a TraitOperation with specific traits and items for the owner and
	 * multiple targets.
	 * 
	 * @param event             The event associated with this operation.
	 * @param owner             The LivingEntity (e.g., player or mob) that owns the
	 *                          items.
	 * @param targets           The list of LivingEntity targets involved in the
	 *                          event.
	 * @param ownerItemTraits   A LinkedHashMap linking each ItemStack owned by the
	 *                          'owner' to its associated list of ItemTraits.
	 * @param targetsItemTraits A list of LinkedHashMaps, each linking ItemStacks
	 *                          owned by a target to their associated list of
	 *                          ItemTraits.
	 */
	public TraitOperation(Event event, LivingEntity owner, List<LivingEntity> targets,
			LinkedHashMap<ItemStack, List<ItemTrait>> ownerItemTraits,
			List<LinkedHashMap<ItemStack, List<ItemTrait>>> targetsItemTraitsMap) {
		this.event = event;
		this.owner = owner;
		this.targets = targets;
		this.ownerItemTraitsMap = ownerItemTraits;
		this.targetsItemTraitsMap = targetsItemTraitsMap;
	}

	public void processOperation() {
		// Iterate through the owner's items and their associated lists of traits
		for (Map.Entry<ItemStack, List<ItemTrait>> entry : ownerItemTraitsMap.entrySet()) {
			ItemStack item = entry.getKey();
			List<ItemTrait> traitsList = entry.getValue();

			for (ItemTrait trait : traitsList) {
				// Execute the trait and record whether it was executed successfully
				boolean executedSuccessfully = trait.executeTrait(this, owner, item, false);
				traitExecutionStatus.put(trait, executedSuccessfully);
			}

		}

		int i = -1; // Index starts from -1 because we increment at the start of the loop
		for (LinkedHashMap<ItemStack, List<ItemTrait>> map : targetsItemTraitsMap) {
			i++; // Move to the next target
			for (Map.Entry<ItemStack, List<ItemTrait>> entry : map.entrySet()) {
				ItemStack item = entry.getKey();
				List<ItemTrait> traitsList = entry.getValue();

				for (ItemTrait trait : traitsList) {
					// Execute the trait and record whether it was executed successfully
					boolean executedSuccessfully = trait.executeTrait(this, targets.get(i), item, true);
					traitExecutionStatus.put(trait, executedSuccessfully);
				}
			}
		}
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public LivingEntity getOwner() {
		return owner;
	}

	public void setOwner(LivingEntity owner) {
		this.owner = owner;
	}

	public List<LivingEntity> getTargets() {
		return targets;
	}

	public void setTargets(List<LivingEntity> targets) {
		this.targets = targets;
	}

	public LinkedHashMap<ItemStack, List<ItemTrait>> getOwnerItemTraitsMap() {
		return ownerItemTraitsMap;
	}

	public void setOwnerItemTraitsMap(LinkedHashMap<ItemStack, List<ItemTrait>> ownerItemTraitsMap) {
		this.ownerItemTraitsMap = ownerItemTraitsMap;
	}

	public List<LinkedHashMap<ItemStack, List<ItemTrait>>> getTargetsItemTraitsMap() {
		return targetsItemTraitsMap;
	}

	public void setTargetsItemTraitsMap(List<LinkedHashMap<ItemStack, List<ItemTrait>>> targetsItemTraitsMap) {
		this.targetsItemTraitsMap = targetsItemTraitsMap;
	}

	public Map<ItemTrait, Boolean> getTraitExecutionStatus() {
		return traitExecutionStatus;
	}

	public void setTraitExecutionStatus(Map<ItemTrait, Boolean> traitExecutionStatus) {
		this.traitExecutionStatus = traitExecutionStatus;
	}

	public boolean isVictim() {
		return victim;
	}

	public void setVictim(boolean victim) {
		this.victim = victim;
	}
}
