package com.rwtema.monkmod.abilities;

import com.google.common.collect.Multimap;
import com.rwtema.monkmod.api.MonkWear;
import com.rwtema.monkmod.factory.Factory;
import com.rwtema.monkmod.factory.IFactoryMade;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MonkAbility implements IFactoryMade {
	public static final EntityEquipmentSlot[] HELD_SLOTS = {EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND};
	public static final EntityEquipmentSlot[] ARMOR_SLOTS = {EntityEquipmentSlot.FEET, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS};
	public static Set<String> DAMAGE_MODIFIERS = Stream.of(SharedMonsterAttributes.ATTACK_DAMAGE, SharedMonsterAttributes.ATTACK_SPEED).map(IAttribute::getName).collect(Collectors.toSet());
	public static Set<String> ARMOR_MODIFIERS = Stream.of(SharedMonsterAttributes.ARMOR_TOUGHNESS, SharedMonsterAttributes.ARMOR).map(IAttribute::getName).collect(Collectors.toSet());
	public final String name;

	public MonkAbility(String name) {
		this.name = name;
		if (Factory.shouldRegister)
			MinecraftForge.EVENT_BUS.register(this);
	}

	public static boolean isUnarmed(EntityPlayer player) {
		return checkForContraband(player, HELD_SLOTS, DAMAGE_MODIFIERS);
	}

	private static boolean checkForContraband(EntityPlayer player, EntityEquipmentSlot[] slots, Set<String> illegalModifiers) {
		for (EntityEquipmentSlot slot : slots) {
			ItemStack itemStack = player.getItemStackFromSlot(slot);
			if (itemStack.isEmpty()) continue;
			if (itemStack.hasCapability(MonkWear.MONK_SAFE_CAPABILITY, null)) {
				MonkWear capability = itemStack.getCapability(MonkWear.MONK_SAFE_CAPABILITY, null);
				assert capability != null;
				if (!capability.canMonkWear(player)) {
					return false;
				}
			}
			Item item = itemStack.getItem();
			Multimap<String, AttributeModifier> itemAttributeModifiers = item.getAttributeModifiers(slot, itemStack);
			if (!itemAttributeModifiers.isEmpty() && illegalModifiers.stream().anyMatch(itemAttributeModifiers::containsKey)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isUnarmored(EntityPlayer player) {
		return checkForContraband(player, ARMOR_SLOTS, ARMOR_MODIFIERS);
	}

	public void tickServer(EntityPlayerMP player) {

	}

	public String getUnlocalized() {
		return "monk.advancements.ability." + name;
	}

	@Override
	public String getKey() {
		return name;
	}

	public ITextComponent getTextComponent() {
		TextComponentTranslation iTextComponents = new TextComponentTranslation(getUnlocalized());
		iTextComponents.getStyle().setColor(TextFormatting.AQUA);
		ITextComponent textComponent = new TextComponentString(": ").appendSibling(new TextComponentTranslation(getUnlocalized() + ".desc", (Object[]) args()));
		textComponent.getStyle().setColor(TextFormatting.RESET);
		return iTextComponents.appendSibling(textComponent);
	}

	protected String[] args() {
		return new String[0];
	}
}
