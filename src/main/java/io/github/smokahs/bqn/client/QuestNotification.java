package io.github.smokahs.bqn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.client.gui.ToastQuestObject;
import io.github.smokahs.bqn.config.BQNConfig;
import net.minecraftforge.client.gui.overlay.ForgeGui;

/**
 * Port of Better Questing 1.7.10's {@code betterquesting.client.QuestNotification}: a queue of
 * centre-screen popups, each showing an icon, a bold underlined title and the quest name, drawn
 * over the HUD and faded in and out.
 */
public final class QuestNotification {
    private static final List<Notice> QUEUE = new ArrayList<>();

    /** Glint already blends, and its texture isn't the block atlas - leave these layers alone. */
    private static final Set<RenderType> GLINT_TYPES = Set.of(
            RenderType.glint(),
            RenderType.glintDirect(),
            RenderType.glintTranslucent(),
            RenderType.entityGlint(),
            RenderType.entityGlintDirect(),
            RenderType.armorGlint(),
            RenderType.armorEntityGlint());

    private static boolean fading;
    private static float fadeAlpha = 1F;

    private static String cachedTitleColorKey = "";
    private static int cachedTitleColor = 0xFFFFFF;
    private static String cachedSubtitleColorKey = "";
    private static int cachedSubtitleColor = 0xFFFFFF;

    private QuestNotification() {
    }

    /** Queues a popup built from the FTB Quests toast we intercepted. */
    public static void enqueue(ToastQuestObject toast) {
        enqueue(titleFor(toast.getTitle()), toast.getSubtitle(), toast.getIcon());
    }

    public static void enqueue(Component title, Component subtitle, @Nullable Icon icon) {
        QUEUE.add(new Notice(title, subtitle, icon));
    }

    public static void reset() {
        QUEUE.clear();
    }

    /**
     * Better Questing's own wording, keyed off whichever "x completed!" message FTB Quests picked.
     * Anything unrecognised falls through to FTB's message.
     */
    private static Component titleFor(Component ftbTitle) {
        if (BQNConfig.USE_FTB_TITLES.get()) {
            return ftbTitle;
        }
        String key = ftbTitle.getContents() instanceof TranslatableContents tc ? tc.getKey() : "";
        return switch (key) {
            case "ftbquests.quest.completed" -> Component.translatable("bqn.notice.quest_complete");
            case "ftbquests.chapter.completed" -> Component.translatable("bqn.notice.chapter_complete");
            case "ftbquests.file.completed" -> Component.translatable("bqn.notice.file_complete");
            default -> ftbTitle;
        };
    }

    public static void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        if (QUEUE.isEmpty()) {
            return;
        }

        if (!BQNConfig.ENABLED.get() || QUEUE.size() > BQNConfig.MAX_QUEUED.get()) {
            QUEUE.clear();
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Notice notice = QUEUE.get(0);

        if (!notice.started) {
            // Don't burn a popup while the player is in a menu or paused - hold it until they're looking.
            if (mc.isPaused() || mc.screen != null) {
                return;
            }
            notice.started = true;
            notice.startTime = Util.getMillis();
            playSound(mc);
        }

        float duration = BQNConfig.DURATION.get().floatValue();
        float time = (Util.getMillis() - notice.startTime) / 1000F;
        if (time >= duration) {
            QUEUE.remove(0);
            return;
        }

        // Fade in over the first second, hold, fade out over the second-to-last second.
        float alpha = time <= duration - 2F ? Math.min(1F, time) : Math.max(0F, (duration - 1F) - time);
        alpha = Mth.clamp(alpha, 0.02F, 1F);
        int alphaBits = Mth.ceil(alpha * 255F) << 24;

        // Threshold is on the raw GUI-scaled width, as in Better Questing, so changing `scale`
        // resizes the popup without moving the point where the wide-GUI bump kicks in.
        float scale = (float) (double) BQNConfig.SCALE.get();
        if (BQNConfig.AUTO_SCALE.get() && screenWidth > 600) {
            scale *= 1.5F;
        }

        int width = Mth.ceil(screenWidth / scale);
        int height = Mth.ceil(screenHeight / scale);
        int titleY = Mth.ceil(height * BQNConfig.Y_FRACTION.get());

        Component title = styleTitle(notice.title);
        Component subtitle = notice.subtitle;

        boolean shadow = BQNConfig.TEXT_SHADOW.get();

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.scale(scale, scale, scale);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (BQNConfig.SHOW_ICON.get() && notice.icon != null && !notice.icon.isEmpty()) {
            int iconSize = BQNConfig.ICON_SIZE.get();
            // Texture icons take the shader colour directly; item icons are routed through
            // drawFadedItem by ItemIconMixin, however deeply they're wrapped.
            fading = true;
            fadeAlpha = alpha;
            try {
                RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
                notice.icon.draw(graphics, width / 2 - iconSize / 2, titleY - BQNConfig.ICON_OFFSET.get(), iconSize, iconSize);
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            } finally {
                fading = false;
            }
        }

        int titleColor = alphaBits | titleColor();
        int subtitleColor = alphaBits | subtitleColor();

        graphics.drawString(mc.font, title, width / 2 - mc.font.width(title) / 2, titleY, titleColor, shadow);
        graphics.drawString(mc.font, subtitle, width / 2 - mc.font.width(subtitle) / 2, titleY + 12, subtitleColor, shadow);

        pose.popPose();
        RenderSystem.disableBlend();
    }

    /** True only while this class is drawing a popup icon, so the ItemIcon mixin stays inert otherwise. */
    public static boolean isFading() {
        return fading;
    }

    public static float fadeAlpha() {
        return fadeAlpha;
    }

    /**
     * Items normally draw on a solid or cutout render type, which has blending switched off - the
     * shader colour's alpha is written but never blended, so the icon would sit there at full
     * opacity while the text fades. Redirecting every non-glint layer onto the translucent item
     * sheet gets the alpha honoured. Otherwise this is what {@code GuiGraphics#renderItem} does,
     * boxed the same way {@code ItemIcon#draw} boxes it.
     */
    public static void drawFadedItem(GuiGraphics graphics, ItemStack stack, int x, int y, int w, int h, float alpha) {
        if (stack.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        BakedModel model = itemRenderer.getModel(stack, mc.level, mc.player, 0);

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + w / 2F, y + h / 2F, 100F);
        pose.mulPoseMatrix(new Matrix4f().scaling(1F, -1F, 1F));
        float size = Math.min(w, h);
        pose.scale(size, size, size);

        boolean flat = !model.usesBlockLight();
        if (flat) {
            Lighting.setupForFlatItems();
        }

        MultiBufferSource.BufferSource buffers = graphics.bufferSource();
        RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
        itemRenderer.render(stack, ItemDisplayContext.GUI, false, pose,
                type -> buffers.getBuffer(GLINT_TYPES.contains(type) ? type : Sheets.translucentItemSheet()),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model);
        buffers.endBatch();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        if (flat) {
            Lighting.setupFor3DItems();
        }
        pose.popPose();
    }

    private static Component styleTitle(Component title) {
        // copy, don't flatten - getString() would throw away the colours the pack author wrote
        MutableComponent styled = title.copy();
        if (BQNConfig.BOLD_TITLE.get()) {
            styled.withStyle(ChatFormatting.BOLD);
        }
        if (BQNConfig.UNDERLINE_TITLE.get()) {
            styled.withStyle(ChatFormatting.UNDERLINE);
        }
        return styled;
    }

    private static void playSound(Minecraft mc) {
        if (!BQNConfig.PLAY_SOUND.get()) {
            return;
        }
        ResourceLocation id = ResourceLocation.tryParse(BQNConfig.SOUND.get());
        if (id == null) {
            return;
        }
        SoundEvent event = BuiltInRegistries.SOUND_EVENT.getOptional(id)
                .orElseGet(() -> SoundEvent.createVariableRangeEvent(id));
        mc.getSoundManager().play(SimpleSoundInstance.forUI(event,
                BQNConfig.SOUND_PITCH.get().floatValue(),
                BQNConfig.SOUND_VOLUME.get().floatValue()));
    }

    private static int titleColor() {
        String raw = BQNConfig.TITLE_COLOR.get();
        if (!raw.equals(cachedTitleColorKey)) {
            cachedTitleColorKey = raw;
            cachedTitleColor = parseColor(raw);
        }
        return cachedTitleColor;
    }

    private static int subtitleColor() {
        String raw = BQNConfig.SUBTITLE_COLOR.get();
        if (!raw.equals(cachedSubtitleColorKey)) {
            cachedSubtitleColorKey = raw;
            cachedSubtitleColor = parseColor(raw);
        }
        return cachedSubtitleColor;
    }

    private static int parseColor(String raw) {
        try {
            return Integer.parseInt(raw.trim().replace("#", ""), 16) & 0xFFFFFF;
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    private static final class Notice {
        private final Component title;
        private final Component subtitle;
        @Nullable
        private final Icon icon;
        private boolean started;
        private long startTime;

        private Notice(Component title, Component subtitle, @Nullable Icon icon) {
            this.title = title;
            this.subtitle = subtitle;
            this.icon = icon;
        }
    }
}
