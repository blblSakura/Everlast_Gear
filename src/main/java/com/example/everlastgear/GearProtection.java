package com.example.everlastgear;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class GearProtection {
    private GearProtection() { }

    public static boolean isProtected(ItemStack stack) {
        return !stack.isEmpty() && stack.isDamageableItem()
                && ConfigHandler.isItemAffected(stack)
                && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BINDING_CURSE, stack) == 0;
    }

    public static boolean isBroken(ItemStack stack) {
        return isProtected(stack) && stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }
}
