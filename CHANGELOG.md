# Changelog

## 1.3

- Quest names and headers keep their own formatting: `&` codes, `&#RRGGBB` and raw JSON all render as written and still fade
- `titleColor` / `subtitleColor` / `boldTitle` / `underlineTitle` replaced by `titleStyle` and `subtitleStyle`, which take `&` codes
- `stripNameFormatting` removed, no longer needed
- `/bqn` parses its text arguments like a real quest title, so formatting can be previewed

## 1.2

- New `defaultTaskToast` option (general, on by default): single task completions keep FTB Quests' own corner toast, so the big popup is only used for quests, chapters and the whole file
- `useFtbTitles` now defaults to `true`, so the popup reads "Quest completed!"
- `scale` now defaults to `1.0`, Better Questing's original size
- Mod logo added

## 1.1

- `/bqn` now takes a quest name, header and icon, so you can preview any popup

## 1.0

- Initial release: the FTB Quests completion toast is replaced with the Better Questing center screen popup
