package dev.piotrulla.lifestealcore.addon;

import com.eternalcode.example.bukkit.BukkitMultification;
import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.translation.TranslationProvider;
import dev.piotrulla.lifestealcore.addon.config.PluginConfig;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LifeStealCoreMultification extends BukkitMultification<PluginConfig> {

    private final AudienceProvider audienceProvider;
    private final PluginConfig pluginConfig;
    private final MiniMessage miniMessage;

    public LifeStealCoreMultification(AudienceProvider audienceProvider, PluginConfig pluginConfig, MiniMessage miniMessage) {
        this.audienceProvider = audienceProvider;
        this.pluginConfig = pluginConfig;
        this.miniMessage = miniMessage;
    }

    @Override
    protected @NotNull TranslationProvider<PluginConfig> translationProvider() {
        return locale -> this.pluginConfig;
    }

    @Override
    protected @NotNull ComponentSerializer<Component, Component, String> serializer() {
        return this.miniMessage;
    }

    @Override
    protected @NotNull AudienceConverter<CommandSender> audienceConverter() {
        return commandSender -> {
            if (commandSender instanceof Player player) {
                return this.audienceProvider.player(player.getUniqueId());
            }

            return this.audienceProvider.console();
        };
    }
}
