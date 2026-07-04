package com.example.everlastgear;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = EverlastGear.MODID)
public class EverlastGearHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        lockDurability(player.getMainHandItem());
        lockDurability(player.getOffhandItem());
        for (ItemStack armor : player.getArmorSlots()) {
            lockDurability(armor);
        }
    }

    private static void lockDurability(ItemStack stack) {
        if (stack.isEmpty()) return;
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return;
        int current = maxDamage - stack.getDamageValue();
        if (current <= 1 && stack.getDamageValue() < maxDamage - 1) {
            stack.setDamageValue(maxDamage - 1);
        }
    }

    @SubscribeEvent
    public static void onArmorHurt(ArmorHurtEvent event) {
        for (ArmorHurtEvent.ArmorEntry entry : event.getArmorMap().values()) {
            ItemStack stack = entry.armorItemStack;
            if (stack.getItem() instanceof ArmorItem && isBroken(stack)) {
                entry.newDamage = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
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
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ArmorItem && isBroken(stack)) {
            event.clearModifiers();
        }
    }

    private static boolean isBroken(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // 根据配置判断该物品是否受模组影响
        if (!ConfigHandler.isItemAffected(stack)) return false;
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return false;
        return (maxDamage - stack.getDamageValue()) <= 1;
    }
}
