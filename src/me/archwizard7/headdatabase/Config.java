package me.archwizard7.headdatabase;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config implements Listener {
    /**
     * @param fileName
     * 拡張子ありのファイル名を指定します
     *
     * @return
     * 指定したファイル名の File
     */
    public File file(String fileName) {
        //プラグインの指定
        Plugin plugin = Bukkit.getPluginManager().getPlugin("HeadDataBase");

        //プラグインフォルダー
        File folder = Objects.requireNonNull(plugin).getDataFolder();

        //プレイヤーのコンフィグファイル
        return new File(folder.getPath() + "/" + fileName);
    }

    /**
     * @param fileName
     * 拡張子ありのファイル名を指定します
     *
     * @return
     * 指定したファイル名の YamlConfiguration
     */
    public FileConfiguration config(String fileName) {
        return YamlConfiguration.loadConfiguration(file(fileName));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        createUserConfig(p);
    }

    public void initialize() {
        //プラグインの指定
        Plugin plugin = Bukkit.getPluginManager().getPlugin("HeadDataBase");

        //プラグインフォルダー
        File folder = Objects.requireNonNull(plugin).getDataFolder();
        if (!folder.exists()) {
            if (!folder.mkdirs())
                Bukkit.getLogger().warning("Folder creation failed. :(");
        }
    }

    public void save(Player p, String[] key, Object[] value) {
        if (key.length != value.length) {
            Bukkit.getLogger().warning("key.length != value.length");
            return;
        }

        //プラグインの指定
        Plugin plugin = Bukkit.getPluginManager().getPlugin("HeadDataBase");

        //プラグインフォルダー
        File folder = Objects.requireNonNull(plugin).getDataFolder();

        //プレイヤーのコンフィグファイル
        File file = new File(folder.getPath() + "/" + p.getUniqueId() + ".yml");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    Bukkit.getLogger().warning("Player's config file creation failed. :(");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //YamlConfiguration
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (int i = 0; i < key.length; i++)
            config.set(key[i], value[i]);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createUserConfig(Player p) {
        String[] key = {"show_unknown", "favorites"};
        Object[] value = {false, new int[0]};

        save(p, key, value);
    }

    public void create(String fileName) {
        //プレイヤーのコンフィグファイル
        File file = file(fileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    Bukkit.getLogger().warning(fileName + ".yml creation failed. :(");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //YamlConfiguration
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCount() {
        FileConfiguration database = config("database.yml");

        List<Integer> allList = new ArrayList<>();
        List<Integer> foundList = new ArrayList<>();

        Map<String, List<Integer>> categories = new HashMap<>();
        Map<String, List<Integer>> tags = new HashMap<>();

        for (int i = 1; ; i++) {
            if (database.get("" + i) != null) {
                allList.add(i);

                if (!Objects.requireNonNull(database.getString(i + ".name")).equals("Not Found")) {
                    foundList.add(i);

                    String category = database.getString(i + ".category");
                    List<Integer> categoryList = categories.get("category." + category);
                    if (categoryList == null) categoryList = new ArrayList<>();
                    categoryList.add(i);
                    categories.put("category." + category, categoryList);

                    List<String> tag = database.getStringList(i + ".tags");
                    for (String s : tag) {
                        List<Integer> tagList = tags.get("tag." + s);
                        if (tagList == null) tagList = new ArrayList<>();
                        tagList.add(i);
                        tags.put("tag." + s, tagList);
                    }
                }
            } else break;
        }

        FileConfiguration count = new YamlConfiguration();
        count.set("all.size", allList.size());
        count.set("all.list", allList);
        count.set("found.size", foundList.size());
        count.set("found.list", foundList);

        Object[] categoriesKey = categories.keySet().toArray();
        Arrays.sort(categoriesKey);
        for (Object o : categoriesKey) {
            String s = o.toString();
            count.set(s + ".size", categories.get(s).size());
            count.set(s + ".list", categories.get(s));
        }

        Object[] tagsKey = tags.keySet().toArray();
        Arrays.sort(tagsKey);
        for (Object o : tagsKey) {
            String s = o.toString();
            count.set(s + ".size", tags.get(s).size());
            count.set(s + ".list", tags.get(s));
        }

        File file = file("count.yml");

        try {
            count.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCount(String key) {
        FileConfiguration config = config("count.yml");
        return config.getInt(key);
    }
}
