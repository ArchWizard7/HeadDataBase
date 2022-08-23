package me.archwizard7.headdatabase;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.*;

public class MenuControl implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player p) {
            FileConfiguration config = new Config().config(p.getUniqueId() + ".yml");

            Inventory inv = event.getClickedInventory();
            String title = event.getView().getTitle();

            if (event.getCurrentItem() == null) return;
            ItemStack item = event.getCurrentItem();

            if (item.getItemMeta() == null) return;
            ItemMeta meta = item.getItemMeta();
            String name = meta.getDisplayName();

            int slot = event.getRawSlot();

            // SEPARATE //

            //メインメニュー
            if (title.equals("§4§lHeadDataBase")) {
                event.setCancelled(true);

                if (name.equals("§d全て参照")) new Menu().showByKey(p, "all", 1);

                if (name.equals("§dお気に入り")) new Menu().showByKey(p, "favorites", 1);

                if (name.equals("§4✘ Close")) p.closeInventory();

                if (name.startsWith("§d不明なヘッドを表示:")) {
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
                    toggleShowUnknown(p);
                    new Menu().mainMenu(p);
                }
            }

            //全て参照
            if (title.endsWith(")")) {
                String key = title.split(" ")[0].replace("§4§l", "").replace("§8", "");

                if (slot >= 0 && slot <= 53) event.setCancelled(true);

                if (Objects.requireNonNull(inv).getItem(51) == null) return;
                ItemStack f = inv.getItem(51);

                if (Objects.requireNonNull(f).getItemMeta() == null) return;
                ItemMeta fMeta = f.getItemMeta();

                String fName = fMeta.getDisplayName();
                int page = Integer.parseInt(fName.split(" ")[1]);

                //ヘッドの範囲内
                if (slot >= 0 && slot <= 44 && item.getType() == Material.PLAYER_HEAD) {
                    ClickType ct = event.getClick();

                    if (ct == ClickType.RIGHT) {
                        String sub = name.split("\\.")[1].replace(")", "");
                        int id = Integer.parseInt(sub);
                        Set<Integer> favorites = new HashSet<>(config.getIntegerList("favorites"));

                        assert meta.getLore() != null;
                        int loreSize = meta.getLore().size();

                        if (meta.getLore().get(loreSize - 1).contains("登録")) {
                            favorites.add(id);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1.0f, 2.0f);
                        } else {
                            favorites.remove(id);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                        }

                        List<Integer> list = new ArrayList<>(favorites);
                        list.sort(Comparator.naturalOrder());
                        config.set("favorites", list);

                        try {
                            config.save(new Config().file(p.getUniqueId() + ".yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        new Menu().showByKey(p, key, page);
                    } else {
                        ItemStack tmp = item.clone();
                        assert tmp.getItemMeta() != null;
                        ItemMeta tmpMeta = tmp.getItemMeta();
                        tmpMeta.setLore(List.of());
                        tmp.setItemMeta(tmpMeta);
                        if (ct == ClickType.MIDDLE) tmp.setAmount(64);

                        event.setCursor(tmp);
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
                    }
                }

                if (name.equals("§3← Back")) new Menu().mainMenu(p);

                if (name.startsWith("§c↩ Page")) new Menu().showByKey(p, key, page - 1);

                if (name.equals("§4✘ Close")) p.closeInventory();

                if (name.startsWith("§aPage")) new Menu().showByKey(p, key, page + 1);

                if (name.startsWith("§d不明なヘッドを表示:")) {
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
                    toggleShowUnknown(p);
                    new Menu().showByKey(p, key, page);
                }
            }
        }
    }

    //染料の切り替え
    public void toggleShowUnknown(Player p) {
        FileConfiguration config = new Config().config(p.getUniqueId() + ".yml");
        boolean b = config.getBoolean("show_unknown");
        String[] key = {"show_unknown"};
        Boolean[] value = {!b};
        new Config().save(p, key, value);
    }
}
