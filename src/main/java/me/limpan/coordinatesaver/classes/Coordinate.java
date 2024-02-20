package me.limpan.coordinatesaver.classes;

import org.bukkit.World;

public class Coordinate {

    private final Integer x,y,z;
    private final String name;
    private final Visibility visibility;
    private final World.Environment environment;
    private final String ownerUUID;

    public Coordinate(Integer x, Integer y, Integer z, String name, Visibility visibility, World.Environment environment, String ownerUUID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
        this.visibility = visibility;
        this.environment = environment;
        this.ownerUUID = ownerUUID;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getZ() {
        return z;
    }

    public String getName() {
        return name;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }
}
