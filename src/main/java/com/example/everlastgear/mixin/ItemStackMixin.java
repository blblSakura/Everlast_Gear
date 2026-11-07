package com.example.everlastgear.mixin;

import com.example.everlastgear.GearProtection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    // Keep persisted damage below maximum, including damage written by other mods.
    @ModifyVariable(method = "setDamageValue", at = @At("HEAD"), argsOnly = true)
    private int everlastGear$keepLastDurability(int damage) {
        ItemStack stack = (ItemStack) (Object) this;
        return GearProtection.isProtected(stack)
                ? Math.min(damage, stack.getMaxDamage() - 1) : damage;
    }

    // Vanilla may decide breakage from its local damage value, so clamping the
    // stored value alone is insufficient. Suppress the break signal as well.
    @Inject(method = "hurt", at = @At("RETURN"), cancellable = true)
    private void everlastGear$preventBreak(int amount, RandomSource random, ServerPlayer player,
                                          CallbackInfoReturnable<Boolean> callback) {
        ItemStack stack = (ItemStack) (Object) this;
        if (callback.getReturnValue() && GearProtection.isProtected(stack)) {
            stack.setDamageValue(stack.getMaxDamage() - 1);
            callback.setReturnValue(false);
        }
    }
}
