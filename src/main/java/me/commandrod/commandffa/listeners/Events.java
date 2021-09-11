package me.commandrod.commandffa.listeners;

import me.commandrod.commandffa.game.Game;
import me.commandrod.commandffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import static me.commandrod.commandffa.Main.plugin;

public class Events implements Listener {

    Game game = new Game();

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (game.isGame() && !p.hasPermission("commandffa.admin")){
            p.sendMessage(Utils.color("&cBlock interactions are disabled mid game!"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (game.isGame() && !p.hasPermission("commandffa.admin")){
            p.sendMessage(Utils.color("&cBlock interactions are disabled mid game!"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (game.isGame()){
            Player p = e.getEntity();
            Player killer = e.getEntity().getKiller();
            game.eliminate(p);
            e.getDrops().clear();
            if (game.getAlivePlayers().size() == 1){
                Bukkit.getScheduler().runTaskLater(plugin(), new Runnable() {
                    @Override
                    public void run() {
                        game.stopGame(killer);
                    }
                }, 60);
            }
        }
    }

    /*@EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (game.isGame()){
            p.setGameMode(GameMode.SPECTATOR);
            p.sendTitle(Utils.color("&cYou died!"), "", 5, 40 ,5);
            p.teleport(Utils.getConfigLocation("center-location"));
        }
    }*/

    @EventHandler
    public void onPunch(EntityDamageByEntityEvent e){
        if (!game.isPvP()) e.setCancelled(true);
    }
}