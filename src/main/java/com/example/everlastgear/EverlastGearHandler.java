package com.example.everlastgear;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = EverlastGear.MODID)
public class EverlastGearHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        lockDurability(player.getMainHandItem());
        lockDurability(player.getOffhandItem());
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                lockDurability(player.getItemBySlot(slot));
            }
        }
    }

    private static void lockDurability(ItemStack stack) {
        if (stack.isEmpty()) return;
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return;
        int current = maxDamage - stack.getDamageValue();
        if (isBroken(stack) && current <= 1 && stack.getDamageValue() < maxDamage - 1) {
            stack.setDamageValue(maxDamage - 1);
        }
    }

    @SubscribeEvent
    public static void onArmorHurt(ArmorHurtEvent event) {
        for (ArmorHurtEvent.ArmorEntry entry : event.getArmorMap().values()) {
            ItemStack stack = entry.armorItemStack;
            if (isArmor(stack) && isBroken(stack)) {
                entry.newDamage = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BreakBlockEvent event) {
        Player player = event.getPlayer();
        if (isBroken(player.getMainHandItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (isBroken(event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (isBroken(event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (isBroken(player.getMainHandItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (isArmor(stack) && isBroken(stack)) {
            event.clearModifiers();
        }
    }

    private static boolean isArmor(ItemStack stack) {
        var equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
    }

    private static boolean isBroken(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!ConfigHandler.isItemAffected(stack)) return false;

        // 绑定诅咒检测：有绑定诅咒则不受保护
        for (var entry : stack.getEnchantments().entrySet()) {
            if (entry.getKey().is(Enchantments.BINDING_CURSE)) {
                return false;
            }
        }

        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return false;
        return (maxDamage - stack.getDamageValue()) <= 2;
    }
}