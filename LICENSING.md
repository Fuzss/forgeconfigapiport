# Licensing
As the main purpose of Forge Config Api Port is to make certain Minecraft Forge / NeoForge features available on other mod loaders, it includes a lot of classes from other projects. This file exists to give an overview for copied / adapted classes from those other projects and their respective licenses.

All changes to copied classes introduced by Forge Config API Port have been marked with a comment in the form of `// Forge Config API Port: ...`.

The project as a whole is licensed under the Mozilla Public License 2.0.

## Minecraft Forge / NeoForge
All classes copied from Forge / NeoForge are licensed under Forge's original license which is the GNU Lesser General Public License v2.1.
Those classes are marked with their respective license header:

```
/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
```

The following packages contain classes from Forge / NeoForge:
- `net.minecraftforge.common`
- `net.minecraftforge.fml.config`
- `net.neoforged.fml.config`
- `net.neoforged.neoforge.common`

The package `fuzs.forgeconfigapiport.fabric.impl.network` mostly contains classes heavily based on similar classes from Forge, the whole package therefore also uses Forge's license and includes the respective header.

The full license file for Minecraft Forge is included with every published jar as `LICENSE-FORGE.md`.

## Night Config
The production jar for this project also includes the library behind Forge's config implementation, TheElectronWill's [Night Config]. It is licensed under the GNU Lesser General Public License v3.0.

The full license file for Night Config is included with every published jar as `LICENSE-NIGHT-CONFIG.md`.

[Night Config]: https://github.com/TheElectronWill/night-config
