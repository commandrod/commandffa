package me.commandrod.commandffa.commands;

import me.commandrod.commandffa.utils.MessageUtils;
import me.commandrod.commandffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobby implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setlobby")){
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.NOT_PLAYER);
                return true;
            }
            Player p = (Player) sender;
            if (!p.hasPermission("commandffa.setlobby")) {
                sender.sendMessage(Utils.color(Utils.getConfigString("messages.permission")));
                Utils.fail(sender);
                return true;
            }
            Utils.setConfigLocation("lobby-location", p);
            p.sendMessage(Utils.color("&3Successfully updated the &blobby &3location."));
        }
        return true;
    }
}