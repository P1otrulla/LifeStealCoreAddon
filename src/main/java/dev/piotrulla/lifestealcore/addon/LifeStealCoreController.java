package dev.piotrulla.lifestealcore.addon;

import com.eternalcode.multification.shared.Formatter;
import dev.norska.lsc.api.HeartConsumeEvent;
import dev.norska.lsc.api.LifestealCoreAPI;
import dev.piotrulla.lifestealcore.addon.config.PluginConfig;
import dev.piotrulla.lifestealcore.addon.shared.Delay;
import dev.piotrulla.lifestealcore.addon.shared.DurationUtil;
import dev.piotrulla.lifestealcore.addon.shared.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

public class LifeStealCoreController implements Listener {

    private final LifeStealCoreMultification multification;
    private final LifestealCoreAPI lifestealCoreAPI;
    private final PluginConfig pluginConfig;
    private final Delay delay;

    public LifeStealCoreController(LifeStealCoreMultification multification, LifestealCoreAPI lifestealCoreAPI, PluginConfig pluginConfig) {
        this.multification = multification;
        this.lifestealCoreAPI = lifestealCoreAPI;
        this.pluginConfig = pluginConfig;
        this.delay = new Delay(pluginConfig.ultraItemCooldown);
    }

    @EventHandler
    void onHeartEat(HeartConsumeEvent event) {
        Player player = event.getPlayer();
        int hearts = event.getHearts();

        if (hearts >= this.pluginConfig.minimumHpToUse) {
            this.multification.player(player.getUniqueId(), message -> message.maximumHpReachedOnlyUltraUse);
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        if (item.isSimilar(this.pluginConfig.ultraItem)) {
            Player player = event.getPlayer();
            int playerHearts = this.getMaxHpForPlayer(player);

            if (playerHearts < this.pluginConfig.minimumHpToUse) {
                this.multification.player(player.getUniqueId(), message -> message.needMinimumHp);
                event.setCancelled(true);
                return;
            }

            if (playerHearts >= this.pluginConfig.maxUltraHp) {
                this.multification.player(player.getUniqueId(), message -> message.maximumHpReached);
                event.setCancelled(true);
                return;
            }

            if (this.hasDelay(player)) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);


            this.lifestealCoreAPI.setPlayerMaxHearts(player.getUniqueId(), playerHearts + this.pluginConfig.ultraAdditionHp);
            this.multification.player(player.getUniqueId(), message -> message.ultraItemUsed);
            this.delay.markDelay(player.getUniqueId());

            ItemUtil.removeItemFromHand(player);
        }
    }

    int getMaxHpForPlayer(Player player) {
        return this.lifestealCoreAPI.getPlayerMaxHearts(player.getUniqueId());
    }

    boolean hasDelay(Player player) {
        if (!this.delay.hasDelay(player.getUniqueId())) {
            return false;
        }

        Duration duration = this.delay.getDurationToExpire(player.getUniqueId());
        this.multification.viewer(player, messagesConfig -> messagesConfig.cooldownMessage, new Formatter().register("{TIME}", DurationUtil.format(duration)));
        return true;
    }
}
