package dev.piotrulla.lifestealcore.addon.shared;

import com.eternalcode.commons.adventure.AdventureLegacyColorPreProcessor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;

public final class ItemBuilder {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .preProcessor(new AdventureLegacyColorPreProcessor())
            .build();

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public void refreshMeta() {
        this.itemStack.setItemMeta(this.itemMeta);
    }

    public ItemBuilder setName(String name) {
        this.itemMeta.setDisplayName(MINI_MESSAGE.serialize(MINI_MESSAGE.deserialize(name)));
        this.refreshMeta();

        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.itemMeta.setLore(Arrays.stream(lore).map(line -> MINI_MESSAGE.serialize(MINI_MESSAGE.deserialize(line))).toList());
        this.refreshMeta();

        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        this.itemMeta.addEnchant(enchant, level, true);
        this.refreshMeta();

        return this;
    }

    public ItemBuilder setFlag(ItemFlag flag) {
        this.itemMeta.addItemFlags(flag);
        this.refreshMeta();

        return this;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }

    public ItemMeta getMeta() {
        return this.itemMeta;
    }
}