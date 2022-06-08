package com.grouptileman;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GroupTilemanAddonTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GroupTilemanAddon.class);
		RuneLite.main(args);
	}
}