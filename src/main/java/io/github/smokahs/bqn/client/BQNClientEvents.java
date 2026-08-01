package io.github.smokahs.bqn.client;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.mojang.brigadier.arguments.StringArgumentType;

import org.jetbrains.annotations.Nullable;

import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.util.TextUtils;
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

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        QuestNotification.reset();
    }

    /**
     * {@code /bqn "[questname]" "[header]" [icon]}
     * EXAMPLE: /bpn "Kill the enderdragon" "Quest... Failed" minecraft:stick
     * <p>
     * Both text arguments go through FTB Quests' own parser, so {@code &}codes, {@code &#RRGGBB}
     * and raw JSON components behave exactly as they would in a real quest title.
     */
    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandBuildContext buildContext = event.getBuildContext();

        event.getDispatcher().register(Commands.literal("bqn")
                .executes(ctx -> preview(null, null, null))
                .then(Commands.argument("questname", StringArgumentType.string())
                        .executes(ctx -> preview(questName(ctx), null, null))
                        .then(Commands.argument("header", StringArgumentType.string())
                                .executes(ctx -> preview(questName(ctx), header(ctx), null))
                                .then(Commands.argument("icon", ItemArgument.item(buildContext))
                                        .executes(ctx -> preview(questName(ctx), header(ctx),
                                                ItemArgument.getItem(ctx, "icon").createItemStack(1, false)))))));
    }

    private static String questName(com.mojang.brigadier.context.CommandContext<?> ctx) {
        return StringArgumentType.getString(ctx, "questname");
    }

    private static String header(com.mojang.brigadier.context.CommandContext<?> ctx) {
        return StringArgumentType.getString(ctx, "header");
    }

    private static int preview(@Nullable String questName, @Nullable String header, @Nullable ItemStack icon) {
        QuestNotification.enqueue(
                header == null ? Component.translatable("bqn.notice.quest_complete") : TextUtils.parseRawText(header),
                questName == null ? Component.literal("Test Quest") : TextUtils.parseRawText(questName),
                ItemIcon.getItemIcon(icon == null ? new ItemStack(Items.DIAMOND) : icon));
        return 1;
    }
}
