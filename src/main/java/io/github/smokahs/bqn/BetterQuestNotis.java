package io.github.smokahs.bqn;

import io.github.smokahs.bqn.config.BQNConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(BetterQuestNotis.MOD_ID)
public class BetterQuestNotis {
    public static final String MOD_ID = "bqn";

    public BetterQuestNotis() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BQNConfig.SPEC);
    }
}
