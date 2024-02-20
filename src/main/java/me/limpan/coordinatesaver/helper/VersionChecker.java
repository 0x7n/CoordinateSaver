package me.limpan.coordinatesaver.helper;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class VersionChecker {

    private static final int PROJECT_ID = 971659;
    private static final String CURSEFORGE_API_URL = "https://api.cfwidget.com/971659";
    private final JavaPlugin plugin;

    public VersionChecker(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    public void checkForUpdates()
    {
        try{
            URL url = new URL(CURSEFORGE_API_URL);
            InputStream inputStream = url.openStream();
            Scanner scanner = new Scanner(inputStream);

            if(scanner.hasNext())
            {
                String response = scanner.next();
            }
            scanner.close();
        }
        catch (IOException e)
        {
            plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
        }
    }

}
