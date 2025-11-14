/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class CrystalSet
extends PresetSet<CrystalSet> {
    public static final CrystalSet sapphire = (CrystalSet)new CrystalSet("sapphire").cave(BiomeRegistry.FOREST);
    public static final CrystalSet amethyst = (CrystalSet)new CrystalSet("amethyst").cave(BiomeRegistry.DESERT);
    public static final CrystalSet ruby = (CrystalSet)new CrystalSet("ruby").deepCave(BiomeRegistry.FOREST);
    public static final CrystalSet topaz = (CrystalSet)new CrystalSet("topaz").deepCave(BiomeRegistry.PLAINS);
    public static final CrystalSet emerald = (CrystalSet)new CrystalSet("emerald").deepCave(BiomeRegistry.SWAMP);
    public final int cluster;
    public final int clusterR;
    public final int pureCluster;
    public final int pureClusterR;
    public final int clusterSmall;
    public final int crystalGravelTile;

    public CrystalSet(String prefixStringID) {
        this.cluster = ObjectRegistry.getObjectID(prefixStringID + "cluster");
        this.objectArrays = new int[][]{{this.cluster}, {this.clusterR = ObjectRegistry.getObjectID(prefixStringID + "clusterr")}, {this.pureCluster = ObjectRegistry.getObjectID(prefixStringID + "clusterpure")}, {this.pureClusterR = ObjectRegistry.getObjectID(prefixStringID + "clusterpurer")}, {this.clusterSmall = ObjectRegistry.getObjectID(prefixStringID + "clustersmall")}};
        this.crystalGravelTile = TileRegistry.getTileID(prefixStringID + "gravel");
        this.tileArrays = new int[][]{{this.crystalGravelTile}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(CrystalSet.class);
    }
}

