package me.archwizard7.headdatabase;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    PluginDescriptionFile pdf = this.getDescription();
    String pluginName = pdf.getVersion();
    String version = pdf.getVersion();

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("\u001b[92m[" + pluginName + "] Enabled!\u001b[0m");
        Bukkit.getLogger().info("\u001b[92m[" + pluginName + "] Version: " + version + "\u001b[0m");

        //リスナーを登録
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new ClickItem(), this);
        Bukkit.getPluginManager().registerEvents(new Config(), this);
        Bukkit.getPluginManager().registerEvents(new MenuControl(), this);

        //コンフィグを生成
        new Config().initialize();
        new Config().create("database.yml");
        new Config().create("count.yml");

        //データベース更新
        new Config().updateCount();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("\u001b[91m[" + pluginName + "] Disabled!\u001b[0m");
    }

    @Override
    public void onLoad() {
        Bukkit.getLogger().info("\u001b[96m[" + pluginName + "] Loaded!\u001b[0m");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            // "/headdatabase"
            if (command.getName().equalsIgnoreCase("headdatabase")) {
                if (args.length == 0) {
                    new Menu().mainMenu(p);
                    return true;
                } else {
                    if (args[0].equalsIgnoreCase("show")) {
                        if (args.length != 3) {
                            p.sendMessage("§c引数を正しく入力してください");
                            p.sendMessage("§c/hdb show <key> <Page No.>");

                            return true;
                        } else {
                            try {
                                String key = args[1];
                                int page = Integer.parseInt(args[2]);
                                new Menu().showByKey(p, key, page);
                            } catch (NumberFormatException e) {
                                p.sendMessage("§cページ番号は整数である必要があります");
                            }
                        }
                    }

                    if (args[0].equalsIgnoreCase("item")) {
                        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                        assert headMeta != null;
                        headMeta.setDisplayName("§4§lHeadDataBase§8 (Right Click)");
                        headMeta.setOwnerProfile(Bukkit.createPlayerProfile(p.getName()));
                        head.setItemMeta(headMeta);

                        p.getInventory().addItem(head);

                        p.sendMessage("§aメニューアイテムを " + p.getDisplayName() + "§a に与えました");

                        return true;
                    }

                    if (args[0].equalsIgnoreCase("update")) {
                        p.sendMessage("§dデータベースを更新中...");

                        new Config().updateCount();

                        p.sendMessage("§a✔ データベースの更新が完了しました");

                        return true;
                    }
                }
            }
        } else {
            if (command.getName().equalsIgnoreCase("headdatabase")) {
                if (args.length == 0) {
                    return true;
                } else {
                    if (args[0].equalsIgnoreCase("update")) {
                        Bukkit.getLogger().info("Updating database...");

                        new Config().updateCount();

                        Bukkit.getLogger().info("Completed update database!");

                        return true;
                    }
                }
            }
        }

        return true;
    }
}
