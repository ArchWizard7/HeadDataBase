package me.archwizard7.headdatabase;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Head {
    String[] empty = new String[0];

    public ItemStack item(Material material, int count, String name, String[] lore) {
        ItemStack i = new ItemStack(material, count);
        assert i.getItemMeta() != null;
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) meta.setLore(Arrays.asList(lore));
        i.setItemMeta(meta);

        return i;
    }

    //IDからヘッドを取得
    public ItemStack headFromID(Player p, int id) {
        FileConfiguration config = new Config().config("database.yml");
        FileConfiguration playerConfig = new Config().config(p.getUniqueId() + ".yml");

        List<Integer> favorites = playerConfig.getIntegerList("favorites");

        if (config.get("" + id) != null) {
            if (config.getString(id + ".name") == null)
                return item(Material.BARRIER, 1, "§4Not Found§8 (No." + id + ")", empty);

            if (Objects.requireNonNull(config.getString(id + ".name")).equals("Not Found"))
                return item(Material.BARRIER, 1, "§4Not Found§8 (No." + id + ")", empty);

            boolean contains = favorites.contains(id);
            String register = contains ? "から§c§l解除" : "に§a§l登録";

            String uuid = config.getString(id + ".uuid");
            String url = config.getString(id + ".url");

            List<String> lore = new ArrayList<>();
            lore.add("§aカテゴリ");
            lore.add("§f・" + config.getString(id + ".category"));

            List<String> tags = config.getStringList(id + ".tags");
            lore.add("§");
            lore.add("§aタグ");
            for (String s : tags) lore.add("§f・" + s);
            lore.add("§");
            lore.add("§6左クリック");
            lore.add("§fヘッドを取り出す");
            lore.add("§");
            lore.add("§6右クリック");
            lore.add("§fお気に入り" + register + "§fする");

            return headFromURL("§e" + config.getString(id + ".name") + "§8 (No." + id + ")", lore.toArray(empty), uuid, url);
        } else {
            return item(Material.BARRIER, 1, "§4Not Found§8 (No." + id + ")", empty);
        }
    }

    //URLからヘッドを取得
    public ItemStack headFromURL(String name, String[] lore, String uuid, String suffixURL) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        assert headMeta != null;
        PlayerProfile headPP = Bukkit.createPlayerProfile(UUID.fromString(uuid));
        PlayerTextures headPT = headPP.getTextures();

        try {
            URL url = new URL("https://textures.minecraft.net/texture/" + suffixURL);
            headPT.setSkin(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        headPP.setTextures(headPT);

        headMeta.setDisplayName(name);
        headMeta.setOwnerProfile(headPP);
        if (lore.length > 0) headMeta.setLore(Arrays.asList(lore));

        head.setItemMeta(headMeta);

        return head;
    }
}
