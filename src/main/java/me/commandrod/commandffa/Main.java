package me.commandrod.commandffa;

import me.commandrod.commandffa.commands.*;
import me.commandrod.commandffa.listeners.Events;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    public static Main plugin() { return instance; }

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        this.getCommand("admin").setExecutor(new Admin());
        this.getCommand("setborder").setExecutor(new SetBorder());
        this.getCommand("setlobby").setExecutor(new SetLobby());
        this.getCommand("setcenter").setExecutor(new SetCenter());
        this.getCommand("start").setExecutor(new Start());
        this.saveConfig();
    }

    @Override
    public void onDisable() {
        instance = null;
        this.saveConfig();
    }
}