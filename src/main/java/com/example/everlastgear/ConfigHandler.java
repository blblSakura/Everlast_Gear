package com.example.everlastgear;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("everlast_gear.json");

    private static ConfigData config = new ConfigData();

    private static class ConfigData {
        String mode = "DENYLIST";
        List<String> items = new ArrayList<>();
    }

    public static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            createDefaultConfig();
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            ConfigData loaded = GSON.fromJson(reader, ConfigData.class);
            if (loaded != null) {
                config = loaded;
            }
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("[Everlast Gear] Failed to load config, using defaults.");
            e.printStackTrace();
            config = new ConfigData();
        }
        if (!"ALLOWLIST".equals(config.mode) && !"DENYLIST".equals(config.mode)) {
            config.mode = "DENYLIST";
        }
        config.items.removeIf(String::isBlank);
    }

    private static void createDefaultConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            // 生成带注释的默认配置文件
            String defaultJson = """
            {
              "mode": "DENYLIST",
              "items": [],
              "_comment": "To use ALLOWLIST, set mode to 'ALLOWLIST' and add item IDs to the list."
            }
            """;
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                writer.write(defaultJson);
            }
            System.out.println("[Everlast Gear] Default config created at " + CONFIG_PATH);
        } catch (IOException e) {
            System.err.println("[Everlast Gear] Could not create default config.");
            e.printStackTrace();
        }
    }

    public static boolean isItemAffected(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = stack.getItem().builtInRegistryHolder().key().location();
        String itemId = id.toString();
        boolean inList = config.items.contains(itemId);
        if ("ALLOWLIST".equals(config.mode)) {
            return inList;
        } else {
            return !inList;
        }
    }

    public static void reload() {
        loadConfig();
    }
}
