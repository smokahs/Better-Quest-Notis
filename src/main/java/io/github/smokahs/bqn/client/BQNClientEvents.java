package io.github.smokahs.bqn.client;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.mojang.brigadier.arguments.StringArgumentType;

import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import io.github.smokahs.bqn.BetterQuestNotis;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterQuestNotis.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BQNClientEvents {
    private BQNClientEvents() {
    }

    /** Don't carry a pending popup across into the next world. */
    @SubscribeEvent
    static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        QuestNotification.reset();
    }

    /** {@code /bqnpreview [quest name]} - fires a popup so the config can be tuned without finishing a quest. */
    @SubscribeEvent
    static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("bqnpreview")
                .executes(ctx -> preview("Test Quest"))
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> preview(StringArgumentType.getString(ctx, "name")))));
    }

    private static int preview(String name) {
        QuestNotification.enqueue(
                Component.translatable("bqn.notice.quest_complete"),
                Component.literal(name),
                ItemIcon.getItemIcon(new ItemStack(Items.DIAMOND)));
        return 1;
    }
}
