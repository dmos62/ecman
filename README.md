## Introduction

Ecman is a simple game where the goal is to navigate through the maze using Emacs keybindings.
Each level will get more difficult and requires player to use more effective keybindings. For example, instead of running forward by pressing down ``C-f`` player will learn to jump around with ``M-f`` etc. New combinations and keybindings can be unlocked by picking up points or badges on the way.The score for each level will be calculated based on number of keystrokes player used, points collected etc.

## Instructions

Run `boot dev` inside the cloned ecman project and navigate to `localhost:3000`. Open javascript console for game notifications and instructions:

  > Level 1 and 3 keybinds: left: S-a, S-s. right: S-d, S-f.
  > Level 2 keybinds: h, l, gj, gk, $, ^.
  > Note: q resets combo. If commands aren't working you probably should reset the combo, though it resets itself if running combo is too long.
  > Note: r restarts game.

## Notes

At the moment you can move left and right, and the levels are very basic and only vary in their length (they are corridors). However, movement logic allows for movement in arbitrary directions, orthogonally or diagonally (with minimal changes). Shortcomings are the level data structure, because you have to "draw" levels manually in a vector also specifying tile coordinates manually as well. I ran out of time before I could improve it.

A notable feature is key combos backed by pattern matching, which allows sophisticated keybindings/commands. At the moment there are two types of keybindings/movement commands, a single step, and max steps in a given direction. It would be easy to implement something like vim's multiplier commands (e.g. 2l), by creating a modified :move-max handler that has a limit (whereas :move-max loops until it can't advance anymore).

PS: `js-println` is used instead of `println` because of reasons I don't understand println would sporadically stop printing to browser's console.
