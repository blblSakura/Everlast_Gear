package com.example.everlastgear.tests;

import com.example.everlastgear.ConfigHandler;
import com.example.everlastgear.EverlastGear;
import com.example.everlastgear.EverlastGearHandler;
import com.example.everlastgear.GearProtection;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Files;

@GameTestHolder(EverlastGear.MODID)
@PrefixGameTestTemplate(false)
@Mod("everlast_gear_tests")
public final class GearGameTests {
    @GameTest(template = "empty", timeoutTicks = 100)
    public static void equipmentProtection(GameTestHelper helper) throws Exception {
        var configPath = FMLPaths.CONFIGDIR.get().resolve("everlast_gear.json");
        String savedConfig = Files.readString(configPath);
        try {
            Files.writeString(configPath, "{\"mode\":\"DENYLIST\",\"items\":[]}");
            ConfigHandler.reload();
            var zombie = new Zombie(EntityType.ZOMBIE, helper.getLevel());
            var sword = new ItemStack(Items.DIAMOND_SWORD);
            sword.setDamageValue(sword.getMaxDamage() - 2);
            sword.hurtAndBreak(100, zombie, entity -> {});
            helper.assertTrue(sword.getCount() == 1, "Overdamage destroyed a protected tool");
            helper.assertTrue(sword.getDamageValue() == sword.getMaxDamage() - 1,
                    "Protected tool did not stop at one durability");
            helper.assertTrue(GearProtection.isBroken(sword), "Exhausted tool is still usable");

            var player = FakePlayerFactory.getMinecraft(helper.getLevel());
            player.setItemSlot(EquipmentSlot.MAINHAND, sword);
            var attack = new AttackEntityEvent(player, zombie);
            EverlastGearHandler.onAttackEntity(attack);
            helper.assertTrue(attack.isCanceled(), "Broken weapon can attack");
            var click = new PlayerInteractEvent.RightClickItem(player, InteractionHand.MAIN_HAND);
            EverlastGearHandler.onRightClickItem(click);
            helper.assertTrue(click.isCanceled(), "Broken tool can right click");

            var chest = new ItemStack(Items.DIAMOND_CHESTPLATE);
            chest.setDamageValue(chest.getMaxDamage() - 1);
            chest.hurtAndBreak(100, zombie, entity -> {});
            helper.assertTrue(chest.getCount() == 1, "Broken armor was destroyed");
            helper.assertTrue(chest.getAttributeModifiers(EquipmentSlot.CHEST).isEmpty(),
                    "Broken armor retains attributes");
            chest.setDamageValue(chest.getMaxDamage() - 20);
            helper.assertTrue(!chest.getAttributeModifiers(EquipmentSlot.CHEST).isEmpty(),
                    "Repaired armor did not regain attributes");

            var menu = new AnvilMenu(0, player.getInventory(),
                    ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(net.minecraft.core.BlockPos.ZERO)));
            menu.getSlot(0).set(sword.copy());
            menu.getSlot(1).set(new ItemStack(Items.DIAMOND));
            menu.createResult();
            var repaired = menu.getSlot(2).getItem();
            helper.assertTrue(!repaired.isEmpty() && !GearProtection.isBroken(repaired),
                    "Anvil material repair failed");

            sword.enchant(Enchantments.MENDING, 1);
            player.takeXpDelay = 0;
            new ExperienceOrb(helper.getLevel(), player.getX(), player.getY(), player.getZ(), 5)
                    .playerTouch(player);
            helper.assertTrue(!GearProtection.isBroken(sword), "Mending did not restore usability");

            var cursed = new ItemStack(Items.DIAMOND_CHESTPLATE);
            cursed.enchant(Enchantments.BINDING_CURSE, 1);
            helper.assertTrue(!GearProtection.isProtected(cursed), "Binding Curse exception ignored");
            cursed.setDamageValue(cursed.getMaxDamage() - 1);
            cursed.hurtAndBreak(10, zombie, entity -> {});
            helper.assertTrue(cursed.isEmpty(), "Excluded armor did not break normally");

            Files.writeString(configPath, "{\"mode\":\"DENYLIST\",\"items\":[\"minecraft:diamond_sword\"]}");
            ConfigHandler.reload();
            var excluded = new ItemStack(Items.DIAMOND_SWORD);
            helper.assertTrue(!GearProtection.isProtected(excluded), "Denylist ignored");
            excluded.hurtAndBreak(excluded.getMaxDamage(), zombie, entity -> {});
            helper.assertTrue(excluded.isEmpty(), "Denylisted tool did not break normally");
            Files.writeString(configPath, "{\"mode\":\"ALLOWLIST\",\"items\":[\"minecraft:diamond_sword\"]}");
            ConfigHandler.reload();
            helper.assertTrue(GearProtection.isProtected(new ItemStack(Items.DIAMOND_SWORD))
                    && !GearProtection.isProtected(new ItemStack(Items.IRON_SWORD)), "Allowlist ignored");
            helper.assertTrue(!GearProtection.isProtected(ItemStack.EMPTY)
                    && !GearProtection.isProtected(new ItemStack(Items.STONE)), "Invalid item protected");
        } finally {
            Files.writeString(configPath, savedConfig);
            ConfigHandler.reload();
        }
        helper.succeed();
    }
}
