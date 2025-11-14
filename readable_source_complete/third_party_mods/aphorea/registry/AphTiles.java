/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.TileRegistry
 *  necesse.level.gameTile.CrystalGravelTile
 *  necesse.level.gameTile.GameTile
 */
package aphorea.registry;

import aphorea.tiles.GelTile;
import aphorea.tiles.InfectedGrassTile;
import aphorea.tiles.InfectedWaterTile;
import aphorea.utils.AphColors;
import necesse.engine.registries.TileRegistry;
import necesse.level.gameTile.CrystalGravelTile;
import necesse.level.gameTile.GameTile;

public class AphTiles {
    public static int INFECTED_GRASS;
    public static int INFECTED_WATER;
    public static int SPINEL_GRAVEL;

    public static void registerCore() {
        TileRegistry.registerTile((String)"geltile", (GameTile)new GelTile("geltile", AphColors.gel), (float)-1.0f, (boolean)true);
        SPINEL_GRAVEL = TileRegistry.registerTile((String)"spinelgravel", (GameTile)new CrystalGravelTile("spinelgravel", AphColors.spinel_dark), (float)10.0f, (boolean)true);
        AphTiles.grass();
        AphTiles.liquids();
    }

    public static void grass() {
        INFECTED_GRASS = TileRegistry.registerTile((String)"infectedgrasstile", (GameTile)new InfectedGrassTile(), (float)0.0f, (boolean)false);
    }

    public static void liquids() {
        INFECTED_WATER = TileRegistry.registerTile((String)"infectedwatertile", (GameTile)new InfectedWaterTile(), (float)20.0f, (boolean)true);
    }
}

