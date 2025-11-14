/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class TreeSet
extends PresetSet<TreeSet> {
    public static final TreeSet oak = (TreeSet)((TreeSet)((TreeSet)new TreeSet("oak").surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST)).deepCave(BiomeRegistry.FOREST);
    public static final TreeSet spruce = (TreeSet)((TreeSet)((TreeSet)new TreeSet("spruce").surface(BiomeRegistry.FOREST, BiomeRegistry.SNOW)).cave(BiomeRegistry.FOREST, BiomeRegistry.SNOW)).deepCave(BiomeRegistry.FOREST, BiomeRegistry.SNOW);
    public static final TreeSet pine = (TreeSet)((TreeSet)((TreeSet)new TreeSet("pine").surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW)).deepCave(BiomeRegistry.SNOW);
    public static final TreeSet palm = (TreeSet)((TreeSet)((TreeSet)new TreeSet("palm").surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).deepCave(BiomeRegistry.DESERT);
    public static final TreeSet willow = (TreeSet)((TreeSet)((TreeSet)new TreeSet("willow").surface(BiomeRegistry.SWAMP)).cave(BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.SWAMP);
    public static final TreeSet maple = (TreeSet)((TreeSet)((TreeSet)new TreeSet("maple").surface(BiomeRegistry.PLAINS)).cave(BiomeRegistry.PLAINS)).deepCave(BiomeRegistry.PLAINS);
    public static final TreeSet birch = (TreeSet)((TreeSet)((TreeSet)new TreeSet("birch").surface(BiomeRegistry.PLAINS)).cave(BiomeRegistry.PLAINS)).deepCave(BiomeRegistry.PLAINS);
    public static final TreeSet cactus = (TreeSet)((TreeSet)((TreeSet)new TreeSet("cactus").surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).deepCave(BiomeRegistry.DESERT);
    public static final TreeSet deadwood;
    public static final TreeSet dryad;
    public final int tree;
    public final int treeStump;
    public final int sapling;

    public TreeSet(String prefixStringID) {
        this.tree = ObjectRegistry.getObjectID(prefixStringID + "tree");
        this.objectArrays = new int[][]{{this.tree}, {this.treeStump = ObjectRegistry.getObjectID(prefixStringID + "treestump")}, {this.sapling = ObjectRegistry.getObjectID(prefixStringID + "sapling")}};
    }

    static {
        dryad = (TreeSet)new TreeSet("dryad").deepCave(BiomeRegistry.PLAINS);
        deadwood = new TreeSet("deadwood");
        PresetDebugPreviewForm.registerPresetSet(TreeSet.class);
    }
}

