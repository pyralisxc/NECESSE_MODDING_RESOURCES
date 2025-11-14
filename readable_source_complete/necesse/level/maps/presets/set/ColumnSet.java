/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.set.PresetSet;

public class ColumnSet
extends PresetSet<ColumnSet> {
    public static final ColumnSet wood = (ColumnSet)new ColumnSet("woodcolumn").all(BiomeRegistry.FOREST, BiomeRegistry.SWAMP, BiomeRegistry.PLAINS);
    public static final ColumnSet granite = (ColumnSet)new ColumnSet("granitecolumn").all(BiomeRegistry.PLAINS, BiomeRegistry.DESERT);
    public static final ColumnSet basalt = (ColumnSet)new ColumnSet("basaltcolumn").all(BiomeRegistry.PLAINS);
    public static final ColumnSet crypt = (ColumnSet)new ColumnSet("cryptcolumn").incursion(BiomeRegistry.GRAVEYARD);
    public static final ColumnSet obsidian = (ColumnSet)new ColumnSet("obsidiancolumn").deepCave(BiomeRegistry.FOREST);
    public static final ColumnSet spidercastle = (ColumnSet)new ColumnSet("spidercastlecolumn").incursion(BiomeRegistry.SPIDER_CASTLE);
    public static final ColumnSet stone = (ColumnSet)((ColumnSet)((ColumnSet)new ColumnSet("stonecolumn").surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST)).incursion();
    public static final ColumnSet deepstone = (ColumnSet)new ColumnSet("deepstonecolumn").deepCave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS);
    public static final ColumnSet snowstone = (ColumnSet)((ColumnSet)((ColumnSet)new ColumnSet("snowstonecolumn").surface(BiomeRegistry.SNOW)).cave(BiomeRegistry.SNOW)).incursion();
    public static final ColumnSet deepsnowstone = (ColumnSet)new ColumnSet("deepsnowstonecolumn").deepCave(BiomeRegistry.SNOW);
    public static final ColumnSet sandstone = (ColumnSet)((ColumnSet)((ColumnSet)new ColumnSet("sandstonecolumn").surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).incursion();
    public static final ColumnSet deepsandstone = (ColumnSet)new ColumnSet("deepsandstonecolumn").deepCave(BiomeRegistry.DESERT);
    public static final ColumnSet swampstone = (ColumnSet)((ColumnSet)((ColumnSet)new ColumnSet("swampstonecolumn").surface(BiomeRegistry.SWAMP)).cave(BiomeRegistry.SWAMP)).incursion();
    public static final ColumnSet deepswampstone = (ColumnSet)new ColumnSet("deepswampstonecolumn").deepCave(BiomeRegistry.SWAMP);
    public final int column;

    protected ColumnSet(String column) {
        this.column = ObjectRegistry.getObjectID(column);
        this.objectArrays = new int[][]{{this.column}};
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(ColumnSet.class);
    }
}

