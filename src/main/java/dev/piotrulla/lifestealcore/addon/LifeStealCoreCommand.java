package dev.piotrulla.lifestealcore.addon;

import com.eternalcode.multification.shared.Formatter;
import dev.piotrulla.lifestealcore.addon.config.ConfigService;
import dev.piotrulla.lifestealcore.addon.config.PluginConfig;
import dev.piotrulla.lifestealcore.addon.shared.ItemUtil;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LifeStealCoreCommand implements CommandExecutor {

    private final LifeStealCoreMultification multification;
    private final ConfigService configService;
    private final PluginConfig pluginConfig;
    private final Server server;

    public LifeStealCoreCommand(LifeStealCoreMultification multification, ConfigService configService, PluginConfig pluginConfig, Server server) {
        this.multification = multification;
        this.configService = configService;
        this.pluginConfig = pluginConfig;
        this.server = server;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("stealdev")) {
            return false;
        }

        if (args.length == 0) {
            this.multification.viewer(sender, message -> message.helpList);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "reload" -> handleReload(sender);
            case "give" -> handleGive(sender, args);
            default -> {
                this.multification.viewer(sender, message -> message.helpList);
                yield true;
            }
        };
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("lifestealcoreaddon.reload")) {
            this.multification.viewer(sender, message -> message.noPermission);
            return true;
        }

        this.configService.reload();
        this.multification.viewer(sender, message -> message.configReloaded);

        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lifestealcoreaddon.give")) {
            this.multification.viewer(sender, message -> message.noPermission);
            return true;
        }

        if (args.length < 3) {
            this.multification.viewer(sender, message -> message.helpList);
            return true;
        }

        String nick = args[1];
        String amountStr = args[2];
        int amount;

        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            this.multification.viewer(sender, message -> message.invalidAmount);
            return true;
        }

        Player target = this.server.getPlayer(nick);

        if (target == null) {
            this.multification.viewer(sender, message -> message.playerNotFound);
            return true;
        }

        Formatter formatter = new Formatter()
                .register("{PLAYER}", target.getName())
                .register("{AMOUNT}", amount);

         this.multification.viewer(sender, message -> message.ultraItemAdded, formatter);

        ItemStack item = this.pluginConfig.ultraItem.clone();
        item.setAmount(amount);

        ItemUtil.giveItem(target, item);

        return true;
    }
}
