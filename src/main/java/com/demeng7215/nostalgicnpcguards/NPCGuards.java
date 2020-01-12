package com.demeng7215.nostalgicnpcguards;

import com.demeng7215.demlib.DemLib;
import com.demeng7215.demlib.api.BlacklistSystem;
import com.demeng7215.demlib.api.Common;
import com.demeng7215.demlib.api.Registerer;
import com.demeng7215.demlib.api.files.CustomConfig;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.nostalgicnpcguards.commands.NPCGuardCmd;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class NPCGuards extends JavaPlugin {

	public CustomConfig settingsFile;

	@Override
	public void onEnable() {

		DemLib.setPlugin(this, "5CRW3V3VRyqTDTLr");
		MessageUtils.setPrefix("&7[&6Nostalgic-NPCGuards&7] &r");

		try {
			if (BlacklistSystem.checkBlacklist()) {
				MessageUtils.error(null, 0, "Plugin has been forcibly disabled.", true);
				return;
			}
		} catch (final IOException ex) {
			MessageUtils.error(ex, 2, "Failed to connect to auth server.", false);
		}

		getLogger().info("Loading configuration files...");

		try {
			this.settingsFile = new CustomConfig("settings.yml");
		} catch (final Exception ex) {
			MessageUtils.error(ex, 1, "Failed to load settings.yml.", true);
			return;
		}

		MessageUtils.setPrefix(getSettings().getString("prefix"));

		getLogger().info("Registering commands...");
		Registerer.registerCommand(new NPCGuardCmd(this));

		getLogger().info("Hooking into Citizens...");
		if (CitizensAPI.getPlugin() == null) {
			MessageUtils.error(null, 3,
					"Failed to hook into Citizens API.", true);
			return;
		}

		MessageUtils.console("&aNostalgic-NPCGuards v" + Common.getVersion() +
				" has been successfully enabled.");
	}

	@Override
	public void onDisable() {
		MessageUtils.console("&cNostalgic-NPCGuards v" + Common.getVersion() +
				" has been successfully disabled.");
	}

	public FileConfiguration getSettings() {
		return this.settingsFile.getConfig();
	}
}
