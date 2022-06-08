package com.grouptileman.runelite.config;

/*
 * Copyright (c) 2018, TheLonelyDev <https://github.com/TheLonelyDev>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2020, ConorLeckey <https://github.com/ConorLeckey>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import net.runelite.client.config.*;

import java.awt.*;

import static com.grouptileman.runelite.config.TilemanModeConfig.CONFIG_GROUP;


@ConfigGroup(CONFIG_GROUP)
public interface TilemanModeConfig extends Config {
    public static final String CONFIG_GROUP = "groupTilemanAddon";

    @ConfigSection(
            name = "Settings",
            description = "Settings'",
            position = 1
    )
    String settingsSection = "settings";

    @ConfigItem(
            keyName = "drawOnMinimap",
            name = "Draw tiles on minimap",
            section = settingsSection,
            description = "Configures whether marked tiles should be drawn on minimap",
            position = 2
    )
    default boolean drawTilesOnMinimap() {
        return false;
    }

    @ConfigItem(
            keyName = "drawTilesOnWorldMap",
            name = "Draw tiles on world map",
            section = settingsSection,
            description = "Configures whether marked tiles should be drawn on world map",
            position = 3
    )
    default boolean drawTilesOnWorldMap() {
        return false;
    }

    @ConfigItem(
            keyName = "groupPlayerNames",
            name = "Group player names",
            section = settingsSection,
            description = "Configures your group allies. This should be a comma separated list",
            position = 4
    )
    default String groupPlayerNames() {
        return "";
    }

    @Alpha
    @ConfigItem(
            keyName = "markerColor",
            name = "Tile Color",
            section = settingsSection,
            description = "Configures the color of the tiles",
            position = 5
    )
    default Color markerColor() {
        return Color.GREEN;
    }

}