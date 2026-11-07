package com.example.everlastgear;

import net.minecraftforge.fml.common.Mod;

@Mod(EverlastGear.MODID)
public class EverlastGear {
    public static final String MODID = "everlast_gear";

    public EverlastGear() {
        ConfigHandler.loadConfig();
    }
}