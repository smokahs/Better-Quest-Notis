package io.github.smokahs.bqn.client;

import io.github.smokahs.bqn.BetterQuestNotis;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterQuestNotis.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BQNClientSetup {
    private BQNClientSetup() {
    }

    @SubscribeEvent
    static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("quest_notification", QuestNotification::render);
    }
}
