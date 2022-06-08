package com.grouptileman.runelite.share;

/*
 * Copyright (c) 2021, Adam <Adam@sigterm.info>
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

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.*;
import javax.inject.Inject;

import com.grouptileman.GroupTilemanAddon;
import com.grouptileman.runelite.config.Tile;
import com.grouptileman.runelite.config.TilesFromUser;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;

import static net.runelite.api.widgets.WidgetInfo.MINIMAP_WORLDMAP_OPTIONS;

import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.menus.WidgetMenuOption;

@Slf4j
public
class GroundMarkerSharingManager {
    private static final WidgetMenuOption EXPORT_MARKERS_OPTION = new WidgetMenuOption("Export", "Tileman Markers", MINIMAP_WORLDMAP_OPTIONS);
    private static final WidgetMenuOption IMPORT_MARKERS_OPTION = new WidgetMenuOption("Import", "Group Tileman Markers", MINIMAP_WORLDMAP_OPTIONS);

    private final GroupTilemanAddon plugin;
    private final Client client;
    private final MenuManager menuManager;
    private final ChatMessageManager chatMessageManager;
    private final ChatboxPanelManager chatboxPanelManager;
    private final Gson gson;

    @Inject
    private ConfigManager configManager;

    @Inject
    private GroundMarkerSharingManager(GroupTilemanAddon plugin, Client client, MenuManager menuManager,
                                       ChatMessageManager chatMessageManager, ChatboxPanelManager chatboxPanelManager, Gson gson) {
        this.plugin = plugin;
        this.client = client;
        this.menuManager = menuManager;
        this.chatMessageManager = chatMessageManager;
        this.chatboxPanelManager = chatboxPanelManager;
        this.gson = gson;
    }

    public void addImportExportMenuOptions() {
        menuManager.addManagedCustomMenu(EXPORT_MARKERS_OPTION, this::exportTilesFromPlayer);
        menuManager.addManagedCustomMenu(IMPORT_MARKERS_OPTION, this::importTilesFromPlayer);
    }

    public void removeMenuOptions() {
        menuManager.removeManagedCustomMenu(EXPORT_MARKERS_OPTION);
        menuManager.removeManagedCustomMenu(IMPORT_MARKERS_OPTION);
    }

    private void exportTilesFromPlayer(MenuEntry menuEntry) {
        List<String> keys = configManager.getConfigurationKeys(GroupTilemanAddon.TILEMAN_CONFIG_GROUP);

        TreeMap<String, List<Tile>> regionTiles = new TreeMap<>();

        for (String key : keys) {
            if (key.startsWith(GroupTilemanAddon.TILEMAN_CONFIG_GROUP + "." + GroupTilemanAddon.REGION_PREFIX)) {
                key = key.replace(GroupTilemanAddon.TILEMAN_CONFIG_GROUP + ".","");
                regionTiles.put(key, gson.fromJson(configManager.getConfiguration(GroupTilemanAddon.TILEMAN_CONFIG_GROUP, key), new TypeToken<List<Tile>>() {
                }.getType()));
            }
        }

        final String exportDump = gson.toJson(new TilesFromUser(plugin.getPlayerName(), regionTiles));

        log.debug("Exported ground markers: {}", exportDump);

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(exportDump), null);
    }

    private void importTilesFromPlayer(MenuEntry menuEntry) {
        final String clipboardText;
        try {
            clipboardText = Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.stringFlavor)
                    .toString();
        } catch (IOException | UnsupportedFlavorException ex) {
            sendChatMessage("Unable to read system clipboard.");
            log.warn("error reading clipboard", ex);
            return;
        }

        log.debug("Clipboard contents: {}", clipboardText);
        if (Strings.isNullOrEmpty(clipboardText)) {
            sendChatMessage("You do not have any ground markers copied in your clipboard.");
            return;
        }
        try {
            TilesFromUser tilesFromUser = gson.fromJson(clipboardText, TilesFromUser.class);

            for (String region : tilesFromUser.getRegionTiles().keySet() ) {
                configManager.setConfiguration(GroupTilemanAddon.CONFIG_GROUP, tilesFromUser.getPlayerName() + "-" + region, gson.toJson(tilesFromUser.getRegionTiles().get(region)));
            }
        } catch (JsonSyntaxException e) {
            log.debug("Malformed JSON for clipboard import", e);
            sendChatMessage("You do not have any ground markers copied in your clipboard.");
        }
    }

    private void sendChatMessage(final String message) {
        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(message)
                .build());
    }
}