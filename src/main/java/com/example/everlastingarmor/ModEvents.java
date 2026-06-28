package com.example.everlastingarmor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.*;

@EventBusSubscriber(modid = EverlastingArmor.MODID)
public class ModEvents {

    // 1. 耐久归零不消失，变为破损（通过添加副本）
    @SubscribeEvent
    public static void onDestroyItem(PlayerDestroyItemEvent event) {
        ItemStack stack = event.getOriginal();
        if (isToolOrArmor(stack)) {
            ItemStack brokenStack = stack.copy();
            brokenStack.set(DataComponents.DAMAGE, brokenStack.getMaxDamage()); // 耐久最大（红条）
            brokenStack.set(ModDataComponents.BROKEN.get(), true);              // 破损标记
            // 添加到玩家背包，若满则掉落
            if (!event.getEntity().getInventory().add(brokenStack)) {
                event.getEntity().drop(brokenStack, false);
            }
            // 原物品正常销毁，但副本已补回，效果等同不消失
        }
    }

    // 2. 左键挖掘阻止（所有工具）
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack = event.getEntity().getMainHandItem();
        if (isBroken(stack) && isTool(stack)) {
            event.setCanceled(true);
        }
    }

    // 3. 右键使用阻止（所有工具）
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getEntity().getMainHandItem();
        if (isBroken(stack) && isTool(stack)) {
            event.setCanceled(true);
        }
    }

    // 4. 攻击实体阻止（仅剑/武器）
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        ItemStack stack = event.getEntity().getMainHandItem();
        if (isBroken(stack) && isWeapon(stack)) {
            event.setCanceled(true);
        }
    }

    // 5. 铁砧修复后移除破损标记
    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack result = event.getOutput();
        if (isBroken(result)) {
            int damage = result.getDamageValue();
            if (damage < result.getMaxDamage()) {
                result.remove(ModDataComponents.BROKEN.get());
            }
        }
    }

    // 6. 经验修补修复破损物品
    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();
        int xp = event.getAmount();
        if (xp <= 0) return;

        int xpUsed = 0;
        // 主手
        ItemStack main = player.getMainHandItem();
        xpUsed += repairWithMending(player, main, xp - xpUsed);
        if (xpUsed < xp) {
            ItemStack off = player.getOffhandItem();
            xpUsed += repairWithMending(player, off, xp - xpUsed);
        }
        // 护甲槽
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack armor = player.getItemBySlot(slot);
            xpUsed += repairWithMending(player, armor, xp - xpUsed);
            if (xpUsed >= xp) break;
        }
        if (xpUsed > 0) {
            event.setAmount(xp - xpUsed);
        }
    }

    private static int repairWithMending(Player player, ItemStack stack, int maxXp) {
        if (stack.isEmpty() || !isBroken(stack)) return 0;

        // 获取 Mending 的 Holder
        var mendingHolder = player.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(Enchantments.MENDING)
                .orElse(null);
        if (mendingHolder == null) return 0;

        // 直接使用 ItemStack 的 getEnchantments().getLevel(Holder) 方法
        int level = stack.getEnchantments().getLevel(mendingHolder);
        if (level <= 0) return 0;

        int damage = stack.getDamageValue();
        if (damage <= 0) return 0;
        int repair = Math.min(damage, maxXp * 2);
        int cost = (repair + 1) / 2;
        if (cost <= maxXp) {
            stack.set(DataComponents.DAMAGE, damage - repair);
            if (stack.getDamageValue() < stack.getMaxDamage()) {
                stack.remove(ModDataComponents.BROKEN.get());
            }
            return cost;
        }
        return 0;
    }

    // -------- 辅助方法 --------
    private static boolean isBroken(ItemStack stack) {
        return !stack.isEmpty() && stack.getOrDefault(ModDataComponents.BROKEN.get(), false);
    }

    // 判断是否为工具（镐、斧、铲、锄）—— 不包括剑
    private static boolean isTool(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof DiggerItem || item instanceof HoeItem;
    }

    // 判断是否为武器（剑）
    private static boolean isWeapon(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    // 判断是否为工具或护甲（用于耐久归零时处理）
    private static boolean isToolOrArmor(ItemStack stack) {
        Item item = stack.getItem();
        return isTool(stack) || isWeapon(stack) || item instanceof ArmorItem;
    }
}
