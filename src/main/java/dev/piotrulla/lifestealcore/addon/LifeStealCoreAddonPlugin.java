package dev.piotrulla.lifestealcore.addon;

import com.eternalcode.commons.adventure.AdventureLegacyColorPostProcessor;
import dev.norska.lsc.LifestealCore;
import dev.norska.lsc.api.LifestealCoreAPI;
import dev.piotrulla.lifestealcore.addon.config.ConfigService;
import dev.piotrulla.lifestealcore.addon.config.PluginConfig;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LifeStealCoreAddonPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigService configService = new ConfigService();
        PluginConfig pluginConfig = configService.create(PluginConfig.class, new File(this.getDataFolder(), "config.yml"));

        Server server = this.getServer();

        if (!server.getPluginManager().isPluginEnabled("LifeStealCore")) {
            server.getPluginManager().disablePlugin(this);
            this.getLogger().warning("LifeStealCore is not enabled, disabling addon.");
            return;
        }

        LifestealCoreAPI lifestealCoreAPI = LifestealCore.getInstance().getAPI();

        AudienceProvider audienceProvider = BukkitAudiences.create(this);
        MiniMessage miniMessage = MiniMessage.builder()
                .postProcessor(new AdventureLegacyColorPostProcessor())
                .build();

        LifeStealCoreMultification multification = new LifeStealCoreMultification(audienceProvider, pluginConfig, miniMessage);

        this.getCommand("stealdev").setExecutor(new LifeStealCoreCommand(multification, configService, pluginConfig, server));
        server.getPluginManager().registerEvents(new LifeStealCoreController(multification, lifestealCoreAPI, pluginConfig), this);
    }
}
