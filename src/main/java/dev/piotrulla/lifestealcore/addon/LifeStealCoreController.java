package dev.piotrulla.lifestealcore.addon;

import com.eternalcode.multification.shared.Formatter;
import dev.norska.lsc.api.HeartConsumeEvent;
import dev.norska.lsc.api.LifestealCoreAPI;
import dev.piotrulla.lifestealcore.addon.config.PluginConfig;
import dev.piotrulla.lifestealcore.addon.shared.Delay;
import dev.piotrulla.lifestealcore.addon.shared.DurationUtil;
import dev.piotrulla.lifestealcore.addon.shared.ItemUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;

public class LifeStealCoreController implements Listener {

    private final LifeStealCoreMultification multification;
    private final LifestealCoreAPI lifestealCoreAPI;
    private final PluginConfig pluginConfig;
    private final MiniMessage miniMessage;
    private final Delay delay;

    public LifeStealCoreController(LifeStealCoreMultification multification, LifestealCoreAPI lifestealCoreAPI, PluginConfig pluginConfig, MiniMessage miniMessage) {
        this.multification = multification;
        this.lifestealCoreAPI = lifestealCoreAPI;
        this.pluginConfig = pluginConfig;
        this.delay = new Delay(pluginConfig.ultraItemCooldown);
        this.miniMessage = miniMessage;
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

    @EventHandler(priority = EventPriority.MONITOR)
    void onTier1(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getType() != this.pluginConfig.itemMaterial) {
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null || !itemMeta.hasDisplayName()) {
            return;
        }

        String displayName = itemStack.getItemMeta().getDisplayName();
        String coloredName = this.miniMessage.serialize(this.miniMessage.deserialize(displayName));

        if (!displayName.equals(coloredName)) {
            return;
        }

        int currentHp = this.getPlayerHp(event.getPlayer());

        if (currentHp >= this.pluginConfig.itemTier1HpMax) {
            this.multification.player(event.getPlayer().getUniqueId(), message -> message.maximumHpReached);
            event.setCancelled(true);
        }
    }


    @EventHandler
    void onUltraItem(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        if (item.isSimilar(this.pluginConfig.ultraItem)) {
            Player player = event.getPlayer();
            int currentHp = this.getPlayerHp(player);

            if (currentHp < this.pluginConfig.minimumHpToUse) {
                this.multification.player(player.getUniqueId(), message -> message.needMinimumHp);
                event.setCancelled(true);
                return;
            }

            if (currentHp >= this.pluginConfig.maxUltraHp) {
                this.multification.player(player.getUniqueId(), message -> message.maximumHpReached);
                event.setCancelled(true);
                return;
            }

            if (this.hasDelay(player)) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);

            this.lifestealCoreAPI.setPlayerMaxHearts(player.getUniqueId(), currentHp + this.pluginConfig.ultraAdditionHp);
            this.lifestealCoreAPI.setPlayerHearts(player.getUniqueId(), this.lifestealCoreAPI.getPlayerMaxHearts(player.getUniqueId()));
            this.multification.player(player.getUniqueId(), message -> message.ultraItemUsed);
            this.delay.markDelay(player.getUniqueId());

            ItemUtil.removeItemFromHand(player);
        }
    }

    int getPlayerHp(Player player) {
        return this.lifestealCoreAPI.getPlayerHearts(player.getUniqueId());
    }

    boolean hasDelay(Player player) {
        if (!this.delay.hasDelay(player.getUniqueId())) {
            return false;
        }

        Duration duration = this.delay.getDurationToExpire(player.getUniqueId());
        this.multification.viewer(player, messagesConfig -> messagesConfig.cooldownMessage, new Formatter().register("{TIME}", DurationUtil.format(duration, true)));
        return true;
    }
}
