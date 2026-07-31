# Better (FTB) Quest Notifications!

A small FTB Quests addon for Minecraft Forge 1.20.1 that replaces the small corner toast you get on
quest completion with the **Better Questing** popup:

- the quest's icon, centre screen
- **`Quest Complete`** in bold, underlined white text
- the quest name underneath
- the level-up sound

## Requirements

- Minecraft 1.20.1
- Forge 47.x
- FTB Quests 2001.4+ (and FTB Library, which it already needs)

Client side only 

## Config

`config/bqn-client.toml`, all client side:

| Section | Key | Default | Notes |
| --- | --- | --- | --- |
| general | `enabled` | `true` | Master switch |
| general | `keepFtbToast` | `false` | Show FTB's corner toast as well as the popup |
| general | `useFtbTitles` | `false` | Use "Quest completed!" instead of "Quest Complete" |
| general | `maxQueued` | `20` | Drop the queue past this many, so bulk completions don't spam |
| display | `duration` | `6.0` | Seconds per popup |
| display | `yFraction` | `0.25` | Title position as a fraction of screen height |
| display | `autoScale` | `true` | 1.5x when scaled width > 600 |
| display | `scale` | `1.3` | Flat multiplier, applied at every GUI scale. `1.0` is Better Questing's original size |
| icon | `showIcon` / `iconSize` / `iconOffset` | `true` / `16` / `20` | |
| text | `boldTitle` / `underlineTitle` | `true` / `true` | |
| text | `textShadow` | `false` | Better Questing drew flat text |
| text | `stripNameFormatting` | `true` | Off keeps custom quest colours, but coloured text won't fade |
| text | `titleColor` / `subtitleColor` | `FFFFFF` | Hex RGB |
| sound | `playSound` / `sound` / `volume` / `pitch` | `true` / `minecraft:entity.player.levelup` / `1.0` / `1.0` | |

## Previewing

`/bpqnpreview` or alternatively if you want, `/bqnpreview [quest name]`

## Building

```
./gradlew build
```
