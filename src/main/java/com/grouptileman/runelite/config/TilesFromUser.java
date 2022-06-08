package com.grouptileman.runelite.config;

import lombok.Value;

import java.util.List;
import java.util.TreeMap;

@Value
public class TilesFromUser {
    String playerName;
    TreeMap<String, List<Tile>> regionTiles;
}
