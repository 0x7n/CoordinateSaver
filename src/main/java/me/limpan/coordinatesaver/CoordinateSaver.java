package me.limpan.coordinatesaver;


import org.bukkit.plugin.java.JavaPlugin;
import me.limpan.coordinatesaver.CoordCommands;
import java.io.File;
import java.util.Objects;

public final class CoordinateSaver extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Starting...");

        registerCommands();
        //CommandHandler cmdHandler = new CommandHandler(dataFolder);

        getLogger().info("Your plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Shutting down...");
    }

    void registerCommands()
    {
        File dataFolder = getDataFolder();

        if(!dataFolder.exists())
        {
            getLogger().info("Error: Data folder does not exist, creating a new one...");
            dataFolder.mkdirs();
        }

        Objects.requireNonNull(getCommand("coords")).setExecutor(new CoordCommands(dataFolder));
        Objects.requireNonNull(getCommand("saveCoords")).setExecutor(new CoordCommands(dataFolder));
    }
}