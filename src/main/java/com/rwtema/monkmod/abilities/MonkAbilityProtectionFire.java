package com.rwtema.monkmod.abilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.Locale;

public class MonkAbilityProtectionFire extends MonkAbilityProtection {

	private final float amount;

	public MonkAbilityProtectionFire(float amount) {
		super("fire_resistance");
		this.amount = amount;
	}

	@Override
	public float getAbsorbtion(DamageSource source, EntityPlayer player) {


		return amount;
	}

	@Override
	public boolean canHandle(EntityPlayer player, @Nonnull DamageSource source) {
		return source.isFireDamage();
	}

	@Nonnull
	@Override
	protected String[] args() {
		return new String[]{NumberFormat.getPercentInstance(Locale.UK).format(1 - amount)};
	}
}
