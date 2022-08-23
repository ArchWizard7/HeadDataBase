package me.archwizard7.headdatabase;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Menu {
    String[] empty = new String[0];
    Config c = new Config();
    Head h = new Head();

    public void mainMenu(Player p) {
        FileConfiguration config = c.config(p.getUniqueId() + ".yml");
        boolean showUnknown = config.getBoolean("show_unknown");

        Inventory inv = Bukkit.createInventory(null, 45, "§4§lHeadDataBase");

        //全て参照
        int allCount = c.getCount(showUnknown ? "all" : "found.size");
        String[] allLore = {"§aデータベース上に存在する全ての", "§aヘッドを参照します", "§", "§f利用可能", "§8➠ " + allCount + " 個のヘッド"};
        ItemStack all = h.item(Material.BOOK, 1, "§d全て参照", allLore);
        inv.setItem(10, all);

        int favoriteCount = config.getIntegerList("favorites").size();
        String[] favoriteLore = {"§aお気に入りに登録した", "§aヘッドを参照します", "§", "§f利用可能", "§8➠ " + favoriteCount + " 個のヘッド"};
        ItemStack favorite = h.item(Material.DIAMOND, 1, "§dお気に入り", favoriteLore);
        inv.setItem(12, favorite);

        ItemStack close = h.item(Material.BARRIER, 1, "§4✘ Close", empty);
        inv.setItem(40, close);

        ItemStack search = h.item(Material.COMPASS, 1, "§6絞り込み・検索", empty);
        inv.setItem(43, search);

        ItemStack dye = showUnknown(p);
        inv.setItem(44, dye);

        p.openInventory(inv);
    }

    public void showByKey(Player p, String key, int page) {
        FileConfiguration config = c.config(p.getUniqueId() + ".yml");
        FileConfiguration count = c.config("count.yml");
        boolean showUnknown = config.getBoolean("show_unknown");

        List<Integer> ids = key.equals("favorites") ? config.getIntegerList("favorites") : count.getIntegerList(key + ".list");
        if (key.equals("all") && !showUnknown) ids = count.getIntegerList("found.list");

        int max = ids.size();
        int maxPage;

        maxPage = (max % 45 == 0) ? (max / 45) : (max / 45) + 1;

        if (maxPage < 1) maxPage = 1;
        if (page < 1) page = 1;
        if (page > maxPage) page = maxPage;
        int start = (page - 1) * 45 + 1;

        Inventory inv = Bukkit.createInventory(null, 54, "§4§l" + key + "§8 (" + page + "/" + maxPage + ")");

        //ヘッド
        if (max >= 1) {
            for (int i = 0; i <= 44; i++) {
                if ((start + i - 1) >= max) break;
                inv.setItem(i, h.headFromID(p, ids.get(start + i - 1)));
            }

            /*
            if (key.equals("all")) {
                if (showUnknown) {
                    for (int i = 0; i <= 44; i++) {
                        if ((start + i) > max) break;
                        inv.setItem(i, h.headFromID(p, start + i));
                    }
                } else {
                    for (int i = 0; i <= 44;) {
                        if ((start + found) >= max) break;

                        ItemStack head = h.headFromID(p, ids.get(start + i - 1));

                        if (head.getType() == Material.PLAYER_HEAD) {
                            inv.setItem(i, head);
                            i++;
                        }

                        found++;
                    }
                }
            } else {
                for (int i = 0; i <= 44; i++) {
                    if ((start + i - 1) >= max) break;
                    inv.setItem(i, h.headFromID(p, ids.get(start + i - 1)));
                }
            }
             */
        } else {
            ItemStack none = h.item(Material.RED_STAINED_GLASS_PANE, 1, "§4ここには何もありません... :(", empty);
            inv.setItem(22, none);
        }

        //戻る
        ItemStack back = h.item(Material.ARROW, 1, "§3← Back", empty);
        inv.setItem(45, back);

        //前のページ
        if (page > 1) {
            ItemStack prev = h.item(Material.ARROW, 1, "§c↩ Page " + (page - 1), empty);
            inv.setItem(48, prev);
        }

        ItemStack close = h.item(Material.BARRIER, 1, "§4✘ Close", empty);
        inv.setItem(49, close);

        //次のページ
        if (page < maxPage) {
            ItemStack next = h.item(Material.ARROW, 1, "§aPage " + (page + 1) + " ↪", empty);
            inv.setItem(50, next);
        }

        String[] jumpLore = {"§a指定したページ番号に移動します"};
        ItemStack jump = h.item(Material.FEATHER, 1, "§9Page " + page, jumpLore);
        inv.setItem(51, jump);

        ItemStack search = h.item(Material.COMPASS, 1, "§6絞り込み・検索", empty);
        inv.setItem(52, search);

        ItemStack dye = showUnknown(p);
        inv.setItem(53, dye);

        ItemStack pane = h.item(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1, "§", empty);
        for (int i = 0; i <= 53; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, pane);
        }

        p.openInventory(inv);
    }

    public ItemStack showUnknown(Player p) {
        FileConfiguration config = c.config(p.getUniqueId() + ".yml");

        //隠しを表示するか
        boolean dyeBool = config.getBoolean("show_unknown");
        Material dyeMeta = dyeBool ? Material.LIME_DYE : Material.GRAY_DYE;
        String dyeName = dyeBool ? "§a§lON" : "§c§lOFF";
        String[] dyeLore = {"§6クリックで変更"};

        return h.item(dyeMeta, 1, "§d不明なヘッドを表示: " + dyeName, dyeLore);
    }
}
