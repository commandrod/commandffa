package me.commandrod.commandffa.game;

import lombok.Getter;
import lombok.Setter;
import me.commandrod.commandffa.Main;
import me.commandrod.commandffa.scoreboardmanager.ScoreboardManager;
import me.commandrod.commandffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter @Setter
public class Game {

    private GameState gameState = GameState.PRE_GAME;
    private boolean PvP;
    private final List<UUID> alivePlayers = new ArrayList<>();
    private final HashMap<Player, Location> deathLocations = new HashMap<>();
    private final HashMap<Player, Integer> kills = new HashMap<>();

    public void countdown(int seconds) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            int countdown = seconds + 1;
            if (countdown >= -1) countdown--;
            for (UUID uuid : getAlivePlayers()) {
                Player players = Bukkit.getPlayer(uuid);
                if (countdown > 0) players.sendTitle(Utils.color("&3Match starts in " + countdown), Utils.color("&bGood luck!"), 5, 20, 5);
                if (countdown == 0){
                    setPvP(true);
                    players.sendTitle(Utils.color("&3Good luck!"), Utils.color("&bTry to be the last one standing!"), 5, 60, 5);
                    Bukkit.getScheduler().cancelTasks(Main.getInstance());
                    if (Utils.getConfigBoolean("settings.scoreboard")) ScoreboardManager.scoreboardRunnable(Main.getGame());
                }
            }
        }, 0, 20);
    }

    public void eliminate(Player player){
        this.getAlivePlayers().remove(player.getUniqueId());
        player.getInventory().clear();
        player.sendTitle(Utils.color("&cYou died!"), "", 5, 40 ,5);
        player.setGameMode(GameMode.SPECTATOR);
        Bukkit.broadcastMessage(Utils.color(Utils.getConfigString("messages.elimination").replace("%player%", player.getName())));
    }

    public void revive(Player player){
        this.getAlivePlayers().add(player.getUniqueId());
        player.setGameMode(GameMode.SURVIVAL);
        if (deathLocations.containsKey(player)){
            player.teleport(deathLocations.get(player));
            deathLocations.remove(player);
        } else {
            player.teleport(Utils.getConfigLocation("center-location"));
        }
        heal(player, true);
        giveKit(player);
    }

    public void stopGame(Player player){
        setGameState(GameState.ENDING);
        setPvP(false);
        for (Player players : Bukkit.getOnlinePlayers()){
            players.sendTitle(Utils.color("&3" + player.getName() + " won the game!"), Utils.color("&bGG!"), 5, 80 ,5);
            heal(players, true);
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            int fireworkCount = 21;
            @Override
            public void run() {
                if (fireworkCount >= 1) {
                    player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK, CreatureSpawnEvent.SpawnReason.COMMAND);
                }
                if (fireworkCount == 0) {
                    for (Player players : Bukkit.getOnlinePlayers()){
                        players.setGameMode(GameMode.SURVIVAL);
                        players.teleport(Utils.getConfigLocation("lobby-location"));
                    }
                    setGameState(GameState.PRE_GAME);
                    getAlivePlayers().clear();
                    getKills().clear();
                    getDeathLocations().clear();
                    Bukkit.getScheduler().cancelTasks(Main.getInstance());
                }
                fireworkCount--;
            }
        }, 0 ,10);
        player.getWorld().getWorldBorder().reset();
    }

    public void heal(Player player, boolean clearInventory) {
        player.setHealth(20);
        player.setFoodLevel(20);
        if (clearInventory) player.getInventory().clear();
    }

    public void giveKit(Player player) {
        player.getInventory().addItem(new ItemStack(Material.DIAMOND_AXE));
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF));
        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    }

    public void setPlayerKills(Player player, int newKills) {
        kills.replace(player, newKills);
    }

    public int getPlayerKills(Player player) { return kills.get(player); }
}