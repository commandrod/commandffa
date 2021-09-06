package me.commandrod.commandffa.commands;

import me.commandrod.commandffa.game.Game;
import me.commandrod.commandffa.utils.Messages;
import me.commandrod.commandffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.commandrod.commandffa.Main.plugin;
import static me.commandrod.commandffa.game.Game.*;

public class Start implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("start")){
            if (sender instanceof Player){
                Player p = (Player) sender;
                if (p.hasPermission("commandffa.start")){
                    Location centerLoc = Utils.getConfigLocation("center-location");
                    double size = plugin().getConfig().getDouble("border.distance");
                    long seconds = plugin().getConfig().getLong("border.seconds");
                    int warningTime = plugin().getConfig().getInt("border.warning");
                    WorldBorder border = centerLoc.getWorld().getWorldBorder();
                    border.setSize(size, seconds);
                    border.setWarningTime(warningTime);
                    border.setCenter(centerLoc);
                    for (Player players : Bukkit.getOnlinePlayers()){
                        Game.heal(players, true);
                        Game.giveKit(players);
                        getAlivePlayers().add(players.getUniqueId());
                        players.setGameMode(GameMode.SURVIVAL);
                        players.teleport(centerLoc);
                    }
                    setGame(true);
                    countdown(plugin().getConfig().getInt("settings.countdown"));
                } else {
                    sender.sendMessage(Utils.color(Messages.PERMISSION));
                    Utils.fail(sender);
                }
            } else {
                sender.sendMessage(Utils.color("&a[CommandFFA] Only players may execute this command!"));
            }
        }
        return true;
    }
}