package com.rwtema.monkmod.abilities;

import com.rwtema.monkmod.MonkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

public class MonkAbilityTameAnimals extends MonkAbility {

	public MonkAbilityTameAnimals() {
		super("tame_animals");
	}


	@SubscribeEvent
	public void onRightClickAnimal(@Nonnull PlayerInteractEvent.EntityInteract event) {
		if (!event.getEntityPlayer().getHeldItem(event.getHand()).isEmpty()) return;
		Entity entity = event.getTarget();
		if (!(entity instanceof EntityAnimal)) return;
		EntityAnimal animal = (EntityAnimal) entity;
		boolean isFleeing = animal.getRevengeTarget() != null;
		if (!isFleeing && animal.isInLove()) {
			return;
		}
		EntityPlayer entityPlayer = event.getEntityPlayer();

		if (!entityPlayer.getHeldItem(event.getHand()).isEmpty()) return;

		if (!MonkManager.getAbilityLevel(entityPlayer, this)) return;


		if (event.getWorld().isRemote) {
//			event.setCanceled(true);
//			event.setCancellationResult(EnumActionResult.SUCCESS);
		} else {

			entityPlayer.sendMessage(new TextComponentTranslation("chat.type.text", entity.getDisplayName(), new TextComponentTranslation("monk.heart")));
			if (isFleeing) {
				animal.setRevengeTarget(null);
				animal.getNavigator().clearPath();
			} else {
				if (animal.getGrowingAge() == 0) {
					animal.setInLove(entityPlayer);
				}
			}
//
//			event.setCanceled(true);
//			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}
}
