package dev.piotrulla.lifestealcore.addon.shared;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class ItemUtil {

    public static void giveItem(Player player, ItemStack item) {
        PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            return;
        }

        inventory.addItem(item);
    }

    public static void removeItemFromHand(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (itemInMainHand.getAmount() == 1) {
            player.getInventory().setItemInMainHand(null);
            return;
        }

        itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
    }
}
