package com.example.everlastgear;

import net.neoforged.fml.common.Mod;

@Mod(EverlastGear.MODID)
public class EverlastGear {
    public static final String MODID = "everlastgear";

    public EverlastGear() {
        ConfigHandler.loadConfig(); // 必须包含这一行
    }
}
