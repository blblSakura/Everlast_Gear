package com.example.everlastgear;

import net.neoforged.fml.common.Mod;

@Mod(EverlastGear.MODID)
public class EverlastGear {
    public static final String MODID = "everlast_gear";

    public EverlastGear() {
        ConfigHandler.loadConfig();
    }
}