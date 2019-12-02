package me.block2block.hotpotato.managers;

import me.block2block.hotpotato.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class HotPotatoExpansion extends PlaceholderExpansion {

    private Main plugin;

    public HotPotatoExpansion(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public String getIdentifier() {
        return "hotpotato";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist(){
        return true;
    }

    public boolean canRegister(){
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        if(identifier.equals("players")){
            return CacheManager.getGames().get(CacheManager.getPlayers().get(player.getUniqueId()).getGameID()).getPlayers().size() + "";
        }

        if(identifier.equals("max")){
            return CacheManager.getGames().get(CacheManager.getPlayers().get(player.getUniqueId()).getGameID()).getMAX() + "";
        }

        if(identifier.equals("mapname")){
            return CacheManager.getGames().get(CacheManager.getPlayers().get(player.getUniqueId()).getGameID()).getMap().getName() + "";
        }
        if(identifier.equals("mapauthor")){
            return CacheManager.getGames().get(CacheManager.getPlayers().get(player.getUniqueId()).getGameID()).getMap().getAuthor() + "";
        }

        if(identifier.equals("kit")){
            return CacheManager.getPlayers().get(player.getUniqueId()).getKit().name();
        }

        if(identifier.equals("bluelives")){
            return CacheManager.getGames().get(CacheManager.getPlayers().get(player.getUniqueId()).getGameID()).getLivesBlue() + "";
        }
        if(identifier.equals("redlives")){
            return CacheManager.getGames().get(CacheManager.getPlayers().get(player.getUniqueId()).getGameID()).getLivesRed() + "";
        }

        if(identifier.equals("team")){
            return plugin.getConfig().getString("Messages.Scoreboard.Teams." + ((CacheManager.getPlayers().get(player.getUniqueId()).isRed())?"Red":"Blue") + "-Format");
        }

        if(identifier.equals("gameid")){
            return CacheManager.getPlayers().get(player.getUniqueId()).getGameID() + "";
        }



        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }


}
