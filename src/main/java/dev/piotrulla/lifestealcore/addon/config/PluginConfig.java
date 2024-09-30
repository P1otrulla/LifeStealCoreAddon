package dev.piotrulla.lifestealcore.addon.config;

import com.eternalcode.multification.notice.Notice;
import dev.piotrulla.lifestealcore.addon.shared.ItemBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

public class PluginConfig extends OkaeriConfig {

    @Comment({ "Lista uprawnien:", "lifestealcoreaddon.give", "lifestealcoreaddon.reload", "" })
    @Comment("Minimalna ilość życia do użycia itemu")
    public int minimumHpToUse = 40;

    @Comment
    @Comment("Maksymalna ilość życia do osiągnięcia używając itemów z LifeStealCore")
    public int maxHpUsingLifeStealCore = 30;

    @Comment
    @Comment("Maksymalna ilość życia do osiągnięcia")
    public int maxUltraHp = 50;

    @Comment
    @Comment("Ilość życia dodawana przez item")
    public int ultraAdditionHp = 1;

    @Comment
    @Comment("Item dodający życie")
    public ItemStack ultraItem = new ItemBuilder(Material.NETHER_STAR)
            .setName("&cUltra item")
            .setLore("&7Dodaje &c10 &7serduszek")
            .setFlag(ItemFlag.HIDE_ENCHANTS)
            .addEnchant(Enchantment.ARROW_DAMAGE, 1)
            .getItem();

    @Comment
    @Comment("Cooldown itemu, możliwości użycia: 1s, 60s, 1m, 1h, 1d, 1h10m itp.")
    public Duration ultraItemCooldown = Duration.ofSeconds(60);
    public Notice cooldownMessage = Notice.chat("&cMusisz poczekać jeszcze &c{TIME} &caby użyć tego itemu!");

    @Comment
    public Notice helpList = Notice.chat("&6Lista poprawnych użyć komendy:",
            "&7- &c/stealdev reload &7- przeładowanie konfiguracji",
            "&7- &c/stealdev give <nick> <ilość> &7- dodanie życia graczowi"
    );

    @Comment
    public Notice noPermission = Notice.chat("&cNie masz uprawnień do wykonania tej komendy!");
    public Notice invalidAmount = Notice.chat("&cIlość musi być dodatnią liczbą!");
    public Notice playerNotFound = Notice.chat("&cGracz nie został znaleziony!");

    @Comment
    public Notice maximumHpReachedOnlyUltraUse = Notice.chat("&cOsiąnięto maksymalną ilość życia! Możesz ją zwiększyć używając &cUltra itemu!");
    public Notice maximumHpReached = Notice.chat("&cOsiąnięto maksymalną ilość życia!");
    public Notice needMinimumHp = Notice.chat("&cMusisz mieć przynajmniej 40 serduszek aby użyć tego itemu!");

    @Comment
    public Notice ultraItemUsed = Notice.chat("&aDodano &c10 &aserduszek!");

    @Comment
    public Notice configReloaded = Notice.chat("&aPrzeładowano konfigurację!");

    @Comment
    public Notice ultraItemAdded = Notice.chat("&aGracz &c{PLAYER} &adostał &c{AMOUNT}x &aultra itemu!");
}
