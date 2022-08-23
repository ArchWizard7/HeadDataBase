package me.archwizard7.headdatabase;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClickItem implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (event.getItem() == null) return;
        ItemStack item = event.getItem();

        if (item.getItemMeta() == null) return;
        ItemMeta meta = item.getItemMeta();
        String name = meta.getDisplayName();

        if (item.getType() == Material.PLAYER_HEAD && name.equals("§4§lHeadDataBase§8 (Right Click)")) {
            new Menu().mainMenu(p);
            event.setCancelled(true);
        }
    }
}
