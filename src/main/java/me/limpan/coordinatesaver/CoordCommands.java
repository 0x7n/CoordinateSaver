package me.limpan.coordinatesaver;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.limpan.coordinatesaver.classes.Coordinate;
import me.limpan.coordinatesaver.classes.Visibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CoordCommands implements CommandExecutor, TabCompleter {

    private final List<Coordinate>  privateCoordinates = new ArrayList<>();
    private final List<Coordinate> globalCoordinates = new ArrayList<>();
    private final File dataFile;
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<List<Coordinate>>(){}.getType();

    public CoordCommands(File dataFolder){
        this.dataFile = new File(dataFolder, "coordinates.json");
        loadDataFromFile();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players"));
            return true;
        }

        Player p = (Player) sender;

        if(command.getName().equalsIgnoreCase("coords"))
        {
            //p.sendMessage(Component.text(args[0]));
            //p.sendMessage(Component.text(args[1]));
            //p.sendMessage(Component.text(args[2]));

            if(args[0].equalsIgnoreCase(Visibility.GLOBAL.name()))
                sendCoordinates(p, globalCoordinates, Visibility.GLOBAL);
            else if(args[0].equalsIgnoreCase(Visibility.PRIVATE.name()))
                sendCoordinates(p, privateCoordinates, Visibility.PRIVATE);
            else if(args[0].equalsIgnoreCase("save") && args[1].equalsIgnoreCase(Visibility.GLOBAL.name()))
                saveCoordinates(p,globalCoordinates,args[2], Visibility.GLOBAL);
            else if(args[0].equalsIgnoreCase("save") && args[1].equalsIgnoreCase(Visibility.PRIVATE.name()))
                saveCoordinates(p,privateCoordinates,args[2], Visibility.PRIVATE);

        }

        /*
                if(command.getName().equalsIgnoreCase("saveCoords"))
        {
            if(args[0].equalsIgnoreCase(Visibility.GLOBAL.name()))
            {
                saveCoordinates(p,globalCoordinates,args[1], Visibility.GLOBAL);
            }
            else if(args[0].equalsIgnoreCase(Visibility.PRIVATE.name()))
            {
                saveCoordinates(p,privateCoordinates,args[1], Visibility.PRIVATE);
            }
        }
         */

        return true;
    }

    private void saveCoordinates(Player p, List<Coordinate> coordList, String title, Visibility visibility)
    {
        if(title.isEmpty())
        {
            p.sendMessage(Component.text("Wrong input, usage: /coords save [private | global] <title>").color(NamedTextColor.YELLOW));
            return;
        }

        Integer x = (int) p.getX();
        Integer y = (int) p.getY();
        Integer z = (int) p.getZ();
        Coordinate coord = new Coordinate(x, y, z, title, visibility, p.getWorld().getEnvironment(), p.getUniqueId().toString());
        coordList.add(coord);
        saveDataToFile();
        p.sendMessage(Component.text("Saved coordinates as " + visibility.name().toLowerCase() + " with the title: " + title).color(TextColor.color(200,200,0)));

    }

    private void sendCoordinates(Player player, List<Coordinate> coordList, Visibility visibility)
    {
        privateCoordinates.clear();
        globalCoordinates.clear();
        loadDataFromFile();
        if (coordList.isEmpty())
        {
            player.sendMessage(Component.text(visibility.name()).color(NamedTextColor.YELLOW)
                    .append(Component.text("No coordinates saved.").color(NamedTextColor.RED)));
            return;
        }

        player.sendMessage(Component.text( visibility.name() + " COORDINATES").color(NamedTextColor.YELLOW));
        for (Coordinate coordinate : coordList) {
            if (visibility == Visibility.PRIVATE && !coordinate.getOwnerUUID().equals(player.getUniqueId().toString())) {
                continue; // Skip coordinates that don't belong to the player
            }

            NamedTextColor dimensionColor = coordinate.getEnvironment().equals(World.Environment.NORMAL) ? NamedTextColor.GREEN :
                    coordinate.getEnvironment().equals(World.Environment.NETHER) ? NamedTextColor.RED :
                            NamedTextColor.DARK_PURPLE;
            Component message = Component.text(coordinate.getName() + ": ")
                    .append(Component.text(coordinate.getX() + ", " + coordinate.getY() + ", " + coordinate.getZ() + " in "))
                    .append(Component.text(getHumanNameForEnvironment(coordinate.getEnvironment())).color(dimensionColor));
            player.sendMessage(message);
        }
    }

    private void loadDataFromFile() {
        if (!dataFile.exists()) {
            Bukkit.getLogger().info("Data file not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            List<Coordinate> data = gson.fromJson(reader, listType);
            if (data != null) {
                for (Coordinate coordinate : data) {
                    if (coordinate.getVisibility() == Visibility.PRIVATE) {
                        privateCoordinates.add(coordinate);
                    } else {
                        globalCoordinates.add(coordinate);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDataToFile() {
        List<Coordinate> data = new ArrayList<>();
        data.addAll(privateCoordinates);
        data.addAll(globalCoordinates);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            gson.toJson(data, listType, writer);
        } catch (IOException e) {
            Bukkit.getLogger().info("Error saving data to file.");
            e.printStackTrace();
        }
    }

    private static String getHumanNameForEnvironment(World.Environment environment) {
        return switch (environment) {
            case NORMAL -> "World";
            case NETHER -> "Nether";
            case THE_END -> "The end";
            case CUSTOM -> "Custom";
        };
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add(Visibility.GLOBAL.name().toLowerCase());
            completions.add(Visibility.PRIVATE.name().toLowerCase());
            completions.add("save");
        } else if (args.length == 2) {
            if ("save".equalsIgnoreCase(args[0])) {
                completions.add(Visibility.GLOBAL.name().toLowerCase());
                completions.add(Visibility.PRIVATE.name().toLowerCase());
            }
        }

        return completions;
    }
}
