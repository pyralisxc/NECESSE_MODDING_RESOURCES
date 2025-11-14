/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import java.util.ArrayList;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.PresetSet;

public class CropSet
extends PresetSet<CropSet> {
    public static final CropSet wheat = (CropSet)((CropSet)((CropSet)new CropSet("wheatseed", "wheat").surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST)).deepCave(BiomeRegistry.FOREST);
    public static final CropSet corn = (CropSet)((CropSet)((CropSet)new CropSet("cornseed", "corn").surface(BiomeRegistry.FOREST)).cave(BiomeRegistry.FOREST)).deepCave(BiomeRegistry.FOREST);
    public static final CropSet tomato = (CropSet)((CropSet)((CropSet)new CropSet("tomatoseed", "tomato").surface(BiomeRegistry.FOREST, BiomeRegistry.SNOW)).cave(BiomeRegistry.FOREST, BiomeRegistry.SNOW)).deepCave(BiomeRegistry.FOREST, BiomeRegistry.SNOW);
    public static final CropSet cabbage = (CropSet)((CropSet)((CropSet)new CropSet("cabbageseed", "cabbage").surface(BiomeRegistry.FOREST, BiomeRegistry.SNOW)).cave(BiomeRegistry.FOREST, BiomeRegistry.SNOW)).deepCave(BiomeRegistry.FOREST, BiomeRegistry.SNOW);
    public static final CropSet chilipepper = (CropSet)((CropSet)((CropSet)new CropSet("chilipepperseed", "chilipepper").surface(BiomeRegistry.SNOW, BiomeRegistry.SWAMP)).cave(BiomeRegistry.SNOW, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.SNOW, BiomeRegistry.SWAMP);
    public static final CropSet sugarbeet = (CropSet)((CropSet)((CropSet)new CropSet("sugarbeetseed", "sugarbeet").surface(BiomeRegistry.SNOW, BiomeRegistry.SWAMP)).cave(BiomeRegistry.SNOW, BiomeRegistry.SWAMP)).deepCave(BiomeRegistry.SNOW, BiomeRegistry.SWAMP);
    public static final CropSet beet = new CropSet("beetseed", "beet");
    public static final CropSet eggplant = (CropSet)((CropSet)((CropSet)new CropSet("eggplantseed", "eggplant").surface(BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).cave(BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).deepCave(BiomeRegistry.SWAMP, BiomeRegistry.DESERT);
    public static final CropSet potato = (CropSet)((CropSet)((CropSet)new CropSet("potatoseed", "potato").surface(BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).cave(BiomeRegistry.SWAMP, BiomeRegistry.DESERT)).deepCave(BiomeRegistry.SWAMP, BiomeRegistry.DESERT);
    public static final CropSet rice = (CropSet)((CropSet)((CropSet)new CropSet("riceseed", "riceseed").surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).deepCave(BiomeRegistry.DESERT);
    public static final CropSet carrot = (CropSet)((CropSet)((CropSet)new CropSet("carrotseed", "carrot").surface(BiomeRegistry.DESERT)).cave(BiomeRegistry.DESERT)).deepCave(BiomeRegistry.DESERT);
    public static final CropSet onion = (CropSet)new CropSet("onionseed", "onion").deepCave(BiomeRegistry.FOREST);
    public static final CropSet pumpkin = (CropSet)new CropSet("pumpkinseed", "pumpkin").deepCave(BiomeRegistry.FOREST, BiomeRegistry.SNOW, BiomeRegistry.SWAMP);
    public static final CropSet strawberry = (CropSet)new CropSet("strawberryseed", "strawberry").deepCave(BiomeRegistry.SNOW, BiomeRegistry.SWAMP, BiomeRegistry.DESERT);
    public static final CropSet coffee = (CropSet)new CropSet("coffeebeans", "coffeebeans").deepCave(BiomeRegistry.DESERT);
    public final int[] seedIDs;
    public final int productID;

    public CropSet(String seedPrefixStringID, String productStringID) {
        int[][] nArrayArray = new int[2][];
        this.seedIDs = CropSet.loadSeeds(seedPrefixStringID);
        nArrayArray[0] = this.seedIDs;
        this.productID = ItemRegistry.getItemID(productStringID);
        nArrayArray[1] = new int[]{this.productID};
        this.objectArrays = nArrayArray;
    }

    private static int[] loadSeeds(String seedPrefixStringID) {
        String seedStringID;
        int seedID;
        ArrayList<Integer> seedIDs = new ArrayList<Integer>();
        for (int stage = 0; stage < 1000 && (seedID = ObjectRegistry.getObjectID(seedStringID = seedPrefixStringID + (stage == 0 ? "" : Integer.valueOf(stage)))) != -1; ++stage) {
            seedIDs.add(seedID);
        }
        return seedIDs.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public <C extends Preset> C replaceWith(CropSet replacementPresetSet, C preset) {
        return this.replacePreset(replacementPresetSet, preset, null);
    }

    public <C extends Preset> C replacePreset(CropSet presetSet, C preset, GameRandom stageRandomizer) {
        if (presetSet == this && stageRandomizer == null) {
            return preset;
        }
        if (presetSet.seedIDs.length == 0) {
            return preset;
        }
        if (this.seedIDs.length == 0) {
            return preset;
        }
        if (stageRandomizer != null) {
            int[] objects = preset.objects[0];
            for (int i = 0; i < objects.length; ++i) {
                int selectedSeed = this.seedIDs[stageRandomizer.nextInt(this.seedIDs.length)];
                if (objects[i] != presetSet.seedIDs[0]) continue;
                objects[i] = selectedSeed;
            }
        } else {
            int myMax = this.seedIDs.length;
            int toReplaceMax = presetSet.seedIDs.length;
            for (int i = 0; i < toReplaceMax; ++i) {
                preset.replaceNonEmptyObjects(presetSet.seedIDs[i], this.seedIDs[Math.min(i, myMax - 1)]);
            }
            preset.replaceNonEmptyObjects(presetSet.seedIDs[0], this.seedIDs[0]);
        }
        return preset;
    }

    static {
        PresetDebugPreviewForm.registerPresetSet(CropSet.class);
    }
}

