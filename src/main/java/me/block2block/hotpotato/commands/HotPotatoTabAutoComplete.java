package me.block2block.hotpotato.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HotPotatoTabAutoComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("hotpotato") && args.length == 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                List<String> list = new ArrayList<>();

                if ("kit".startsWith(args[0])) {
                    list.add("kit");
                }

                if ("team".startsWith(args[0])) {
                    list.add("team");
                }

                if ("leave".startsWith(args[0])) {
                    list.add("leave");
                }

                if ("queue".startsWith(args[0])) {
                    list.add("queue");
                }

                if (p.hasPermission("hotpotato.game")) {
                    if ("forcestart".startsWith(args[0])) {
                        list.add("forcestart");
                    }

                    if ("end".startsWith(args[0])) {
                        list.add("end");
                    }
                }
                if (p.hasPermission("hotpotato.edit")) {
                    if ("edit".startsWith(args[0])) {
                        list.add("edit");
                    }

                    if ("setup".startsWith(args[0])) {
                        list.add("setup");
                    }

                    if ("setlobby".startsWith(args[0])) {
                        list.add("setlobby");
                    }

                    if ("finish".startsWith(args[0])) {
                        list.add("finish");
                    }

                    if ("addbalance".startsWith(args[0])) {
                        list.add("addbalance");
                    }

                    if ("map".startsWith(args[0])) {
                        list.add("map");
                    }
                }

                Collections.sort(list);

                return list;
            }
        } else if (command.getName().equalsIgnoreCase("parkour") && args.length == 2) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("hotpotato.edit")) {
                    if (args[0].toLowerCase().equals("map")) {
                        List<String> list = new ArrayList<>();
                        if ("list".startsWith(args[1])) {
                            list.add("list");
                        }

                        if ("remove".startsWith(args[1])) {
                            list.add("remove");
                        }

                        Collections.sort(list);

                        return list;
                    } else if (args[0].toLowerCase().equals("addbalance")) {
                        List<String> list = new ArrayList<>();
                        for (Player p2 : Bukkit.getOnlinePlayers()) {
                            if (p2.getName().startsWith(args[1])) {
                                list.add(p2.getName());
                            }
                        }

                        Collections.sort(list);

                        return list;
                    }
                }
            }
        }
        return null;
    }
}
