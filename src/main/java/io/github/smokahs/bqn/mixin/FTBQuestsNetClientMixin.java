package io.github.smokahs.bqn.mixin;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.ftb.mods.ftbquests.client.FTBQuestsNetClient;
import dev.ftb.mods.ftbquests.client.gui.ToastQuestObject;
import dev.ftb.mods.ftbquests.quest.QuestObjectType;
import io.github.smokahs.bqn.client.QuestNotification;
import io.github.smokahs.bqn.config.BQNConfig;

/**
 * FTB Quests turns a completion packet straight into a corner toast. We take that toast and turn it
 * into a Better Questing style popup instead; everything else the method does (refreshing an open
 * quest book) is left alone.
 */
@Mixin(FTBQuestsNetClient.class)
public class FTBQuestsNetClientMixin {
    @Redirect(
            method = "displayCompletionToast(J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
    private static void bqn$replaceCompletionToast(ToastComponent toasts, Toast toast) {
        if (toast instanceof ToastQuestObject questToast && BQNConfig.ENABLED.get()) {
            //  by default tasks keep FTB's small corner toast
            if (BQNConfig.DEFAULT_TASK_TOAST.get() && bqn$isTask(questToast)) {
                toasts.addToast(toast);
                return;
            }
            QuestNotification.enqueue(questToast);
            if (!BQNConfig.KEEP_FTB_TOAST.get()) {
                return;
            }
        }
        toasts.addToast(toast);
    }

    private static boolean bqn$isTask(ToastQuestObject toast) {
        String taskKey = bqn$key(QuestObjectType.TASK.getCompletedMessage());
        return !taskKey.isEmpty() && taskKey.equals(bqn$key(toast.getTitle()));
    }

    private static String bqn$key(Component component) {
        return component.getContents() instanceof TranslatableContents contents ? contents.getKey() : "";
    }
}
