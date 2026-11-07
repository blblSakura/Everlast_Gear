package com.example.everlastgear;

import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EverlastGear.MODID)
public final class EverlastGearHandler {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (GearProtection.isBroken(event.getPlayer().getMainHandItem())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (GearProtection.isBroken(event.getItemStack())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (GearProtection.isBroken(event.getItemStack())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (GearProtection.isBroken(event.getItemStack())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (GearProtection.isBroken(event.getEntity().getMainHandItem())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        if (event.getItemStack().getItem() instanceof ArmorItem
                && GearProtection.isBroken(event.getItemStack())) event.clearModifiers();
    }
}
