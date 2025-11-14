/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class PathSet
extends PresetSet<PathSet> {
    public static final PathSet wood = (PathSet)((PathSet)new PathSet(TileRegistry.woodPathID).surface(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).cave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT);
    public static final PathSet dryad;
    public static final PathSet stone;
    public static final PathSet sandStone;
    public static final PathSet swampStone;
    public static final PathSet snowStone;
    public static final PathSet granite;
    public static final PathSet basalt;
    public static final PathSet lava;
    public static final PathSet crypt;
    public static final PathSet dawn;
    public static final PathSet moon;
    public static final PathSet darkMoon;
    public static final PathSet darkFullMoon;
    public final int tile;

    protected PathSet(int tile) {
        this.tile = tile;
        this.tileArrays = new int[][]{{this.tile}};
    }

    static {
        stone = (PathSet)((PathSet)new PathSet(TileRegistry.stonePathID).surface(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).cave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT);
        snowStone = (PathSet)((PathSet)((PathSet)new PathSet(TileRegistry.snowStonePathID).surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW)).deepCave(BiomeRegistry.SNOW);
        granite = (PathSet)((PathSet)new PathSet(TileRegistry.granitePathID).surface(BiomeRegistry.PLAINS)).cave(BiomeRegistry.PLAINS);
        sandStone = (PathSet)((PathSet)((PathSet)new PathSet(TileRegistry.sandstonePathID).surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).deepCave(BiomeRegistry.DESERT);
        swampStone = (PathSet)((PathSet)((PathSet)new PathSet(TileRegistry.swampStonePathID).surface(BiomeRegistry.SWAMP)).cave(BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.SWAMP);
        dryad = (PathSet)((PathSet)((PathSet)new PathSet(TileRegistry.dryadPathID).cave(BiomeRegistry.PLAINS)).deepCave(BiomeRegistry.PLAINS)).incursion();
        basalt = (PathSet)((PathSet)new PathSet(TileRegistry.basaltPathID).deepCave(BiomeRegistry.PLAINS)).incursion();
        crypt = (PathSet)new PathSet(TileRegistry.getTileID("cryptpath")).incursion();
        lava = new PathSet(TileRegistry.getTileID("lavapath"));
        dawn = new PathSet(TileRegistry.getTileID("dawnpath"));
        moon = new PathSet(TileRegistry.getTileID("moonpath"));
        darkMoon = new PathSet(TileRegistry.getTileID("darkmoonpath"));
        darkFullMoon = new PathSet(TileRegistry.getTileID("darkfullmoonpath"));
        PresetDebugPreviewForm.registerPresetSet(PathSet.class);
    }
}

