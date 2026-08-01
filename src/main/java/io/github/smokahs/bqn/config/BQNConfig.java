package io.github.smokahs.bqn.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Client config. Defaults reproduce Better Questing 1.7.10's quest completion notice
 * (6 second lifetime, 1 second fade in / out, underlined bold title, {@code random.levelup}).
 */
public final class BQNConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.BooleanValue KEEP_FTB_TOAST;
    public static final ForgeConfigSpec.BooleanValue DEFAULT_TASK_TOAST;
    public static final ForgeConfigSpec.BooleanValue USE_FTB_TITLES;
    public static final ForgeConfigSpec.IntValue MAX_QUEUED;

    public static final ForgeConfigSpec.DoubleValue DURATION;
    public static final ForgeConfigSpec.DoubleValue Y_FRACTION;
    public static final ForgeConfigSpec.BooleanValue AUTO_SCALE;
    public static final ForgeConfigSpec.DoubleValue SCALE;

    public static final ForgeConfigSpec.BooleanValue SHOW_ICON;
    public static final ForgeConfigSpec.IntValue ICON_SIZE;
    public static final ForgeConfigSpec.IntValue ICON_OFFSET;

    public static final ForgeConfigSpec.BooleanValue BOLD_TITLE;
    public static final ForgeConfigSpec.BooleanValue UNDERLINE_TITLE;
    public static final ForgeConfigSpec.BooleanValue TEXT_SHADOW;
    public static final ForgeConfigSpec.ConfigValue<String> TITLE_COLOR;
    public static final ForgeConfigSpec.ConfigValue<String> SUBTITLE_COLOR;

    public static final ForgeConfigSpec.BooleanValue PLAY_SOUND;
    public static final ForgeConfigSpec.ConfigValue<String> SOUND;
    public static final ForgeConfigSpec.DoubleValue SOUND_VOLUME;
    public static final ForgeConfigSpec.DoubleValue SOUND_PITCH;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Better Quest Notis - all settings are client side").push("general");

        ENABLED = builder
                .comment("Show the Better Questing style popup when a quest is completed.")
                .define("enabled", true);
        KEEP_FTB_TOAST = builder
                .comment("Also show FTB Quests' own corner toast. Off by default - the popup replaces it.")
                .define("keepFtbToast", false);
        DEFAULT_TASK_TOAST = builder
                .comment("Uses FTB Quests default Task completion corner toast.")
                .define("defaultTaskToast", true);
        USE_FTB_TITLES = builder
                .comment("Use FTB Quests' wording (\"Quest completed!\") instead of Better Questing's (\"Quest Complete\").")
                .define("useFtbTitles", true);
        MAX_QUEUED = builder
                .comment("Drop the whole queue if more than this many popups pile up, to avoid spam after a bulk completion.")
                .defineInRange("maxQueued", 20, 1, 256);

        builder.pop().comment("Timing and placement").push("display");

        DURATION = builder
                .comment("Total lifetime of one popup, in seconds. Includes text and quest icon fade")
                .defineInRange("duration", 6.0D, 3.0D, 60.0D);
        Y_FRACTION = builder
                .comment("Vertical position of the title.")
                .defineInRange("yFraction", 0.25D, 0.0D, 1.0D);
        AUTO_SCALE = builder
                .comment("Render at 1.5x on wide GUIs (scaled width > 600), like Better Questing did.")
                .define("autoScale", true);
        SCALE = builder
                .comment("Flat size multiplier, applied at every GUI scale and on top of autoScale.")
                .defineInRange("scale", 1.0D, 0.25D, 4.0D);

        builder.pop().comment("Icon").push("icon");

        SHOW_ICON = builder
                .comment("Draw the quest's icon above the text.")
                .define("showIcon", true);
        ICON_SIZE = builder
                .comment("Icon size in (scaled) pixels.")
                .defineInRange("iconSize", 16, 4, 64);
        ICON_OFFSET = builder
                .comment("Pixels between the top of the title and the top of the icon.")
                .defineInRange("iconOffset", 20, 0, 128);

        builder.pop().comment("Text").push("text");

        BOLD_TITLE = builder.define("boldTitle", true);
        UNDERLINE_TITLE = builder.define("underlineTitle", true);
        TEXT_SHADOW = builder
                .comment("Better Questing drew the text without a drop shadow.")
                .define("textShadow", false);
        TITLE_COLOR = builder
                .comment("Hex RGB of the \"Quest Complete\" line.",
                        "Only used for text that has no colour of its own.")
                .define("titleColor", "FFFFFF");
        SUBTITLE_COLOR = builder
                .comment("Hex RGB of the quest name line.",
                        "Only used for text that has no colour of its own - a coloured quest keeps its own colours.")
                .define("subtitleColor", "FFFFFF");

        builder.pop().comment("Sound").push("sound");

        PLAY_SOUND = builder.define("playSound", true);
        SOUND = builder
                .comment("Sound event id played when a popup appears. Better Questing used random.levelup.")
                .define("sound", "minecraft:entity.player.levelup");
        SOUND_VOLUME = builder.defineInRange("volume", 1.0D, 0.0D, 1.0D);
        SOUND_PITCH = builder.defineInRange("pitch", 1.0D, 0.5D, 2.0D);

        builder.pop();

        SPEC = builder.build();
    }

    private BQNConfig() {
    }
}
