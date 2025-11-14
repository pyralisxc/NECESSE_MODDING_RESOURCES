/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;

public abstract class PresetSet<T extends PresetSet<T>> {
    private static final HashMap<Biome, HashMap<Class<? extends PresetSet<?>>, HashSet<PresetSet<?>>>> incursionSets = new HashMap();
    private static final HashMap<Biome, HashMap<Class<? extends PresetSet<?>>, HashSet<PresetSet<?>>>> surfaceBiomeSets = new HashMap();
    private static final HashMap<Biome, HashMap<Class<? extends PresetSet<?>>, HashSet<PresetSet<?>>>> caveBiomeSets = new HashMap();
    private static final HashMap<Biome, HashMap<Class<? extends PresetSet<?>>, HashSet<PresetSet<?>>>> deepCaveBiomeSets = new HashMap();
    private static final HashMap<Class<? extends PresetSet<?>>, HashSet<PresetSet<?>>> alwaysAllowSets = new HashMap();
    public int[][] objectArrays = null;
    public int[][] tileArrays = null;
    public PresetSet<?>[][] nestedSetArrays = null;

    public <C extends Preset> C replaceWith(T replacementPresetSet, C preset) {
        if (replacementPresetSet == this) {
            return preset;
        }
        PresetSet.replaceIDs(this.objectArrays, ((PresetSet)replacementPresetSet).objectArrays, preset::replaceNonEmptyObjects);
        PresetSet.replaceIDs(this.tileArrays, ((PresetSet)replacementPresetSet).tileArrays, preset::replaceNonEmptyTiles);
        if (this.nestedSetArrays != null && ((PresetSet)replacementPresetSet).nestedSetArrays != null) {
            for (int arrayIndex = 0; arrayIndex < this.nestedSetArrays.length && arrayIndex < ((PresetSet)replacementPresetSet).nestedSetArrays.length; ++arrayIndex) {
                PresetSet<?>[] myIDArray = this.nestedSetArrays[arrayIndex];
                PresetSet<?>[] otherIDArray = ((PresetSet)replacementPresetSet).nestedSetArrays[arrayIndex];
                if (otherIDArray == null || myIDArray == null) continue;
                for (int IDIndex = 0; IDIndex < myIDArray.length && IDIndex < otherIDArray.length; ++IDIndex) {
                    PresetSet<?> mySet = myIDArray[IDIndex];
                    PresetSet<?> otherSet = otherIDArray[IDIndex];
                    if (!otherSet.getClass().isInstance(mySet)) continue;
                    ((PresetSet)mySet.getClass().cast(mySet)).replaceWith(otherSet, preset);
                }
            }
        }
        return preset;
    }

    private static void replaceIDs(int[][] myIDs, int[][] otherIDs, BiConsumer<Integer, Integer> consumer) {
        if (myIDs == null || otherIDs == null) {
            return;
        }
        for (int arrayIndex = 0; arrayIndex < myIDs.length && arrayIndex < otherIDs.length; ++arrayIndex) {
            int[] myIDArray = myIDs[arrayIndex];
            int[] otherIDArray = otherIDs[arrayIndex];
            if (otherIDArray == null || myIDArray == null) continue;
            for (int IDIndex = 0; IDIndex < myIDArray.length && IDIndex < otherIDArray.length; ++IDIndex) {
                int myID = myIDArray[IDIndex];
                int otherID = otherIDArray[IDIndex];
                consumer.accept(myID, otherID);
            }
        }
    }

    public <C extends Preset> C replaceWithRandomly(GameRandom random, T[] randomSets, C preset) {
        int i;
        if (randomSets == null || randomSets.length == 0) {
            return preset;
        }
        if (this.objectArrays != null) {
            for (i = 0; i < this.objectArrays.length; ++i) {
                int n;
                Object presetSetObjectIDs;
                ArrayList<Integer> objects = new ArrayList<Integer>();
                int[] objectIDs = this.objectArrays[i];
                if (objectIDs == null || objectIDs.length == 0) continue;
                for (T t : randomSets) {
                    if (i >= ((PresetSet)t).objectArrays.length || (presetSetObjectIDs = ((PresetSet)t).objectArrays[i]) == null) continue;
                    int[] nArray = presetSetObjectIDs;
                    n = nArray.length;
                    for (int j = 0; j < n; ++j) {
                        int objectID = nArray[j];
                        objects.add(objectID);
                    }
                }
                for (int n2 : objectIDs) {
                    if (n2 == -1) continue;
                    presetSetObjectIDs = preset.objects;
                    int n3 = ((int[])presetSetObjectIDs).length;
                    for (n = 0; n < n3; ++n) {
                        int presetObjectLayers = presetSetObjectIDs[n];
                        for (int j = 0; j < ((int)presetObjectLayers).length; ++j) {
                            void presetObjectIds = presetObjectLayers[j];
                            if (presetObjectIds != n2) continue;
                            presetObjectLayers[j] = (Integer)random.getOneOf(objects);
                        }
                    }
                }
            }
        }
        if (this.tileArrays != null) {
            for (i = 0; i < this.tileArrays.length; ++i) {
                ArrayList<Integer> tiles = new ArrayList<Integer>();
                int[] tileIDs = this.tileArrays[i];
                if (tileIDs == null || tileIDs.length == 0) continue;
                for (T t : randomSets) {
                    int[] presetSetTileIDs;
                    if (i >= ((PresetSet)t).tileArrays.length || (presetSetTileIDs = ((PresetSet)t).tileArrays[i]) == null) continue;
                    for (int tileID : presetSetTileIDs) {
                        tiles.add(tileID);
                    }
                }
                for (int n : tileIDs) {
                    if (n == -1) continue;
                    for (int j = 0; j < preset.tiles.length; ++j) {
                        int n4 = preset.tiles[j];
                        if (n4 != n) continue;
                        preset.tiles[j] = (Integer)random.getOneOf(tiles);
                    }
                }
            }
        }
        if (this.nestedSetArrays != null) {
            for (i = 0; i < this.nestedSetArrays.length; ++i) {
                ArrayList sets = new ArrayList();
                PresetSet<?>[] nestedSets = this.nestedSetArrays[i];
                if (nestedSets == null || nestedSets.length == 0) continue;
                for (T t : randomSets) {
                    PresetSet<?>[] presetSetNestedSets;
                    if (i >= ((PresetSet)t).nestedSetArrays.length || (presetSetNestedSets = ((PresetSet)t).nestedSetArrays[i]) == null) continue;
                    sets.addAll(Arrays.asList(presetSetNestedSets));
                }
                for (PresetSet<?> presetSet : nestedSets) {
                    if (presetSet == null) continue;
                    for (PresetSet presetSet2 : sets) {
                        if (!presetSet2.getClass().isInstance(presetSet)) continue;
                        ((PresetSet)presetSet.getClass().cast(presetSet)).replaceWith(presetSet2, preset);
                    }
                }
            }
        }
        return preset;
    }

    public int replaceSingleObjectRandomly(GameRandom random, T lastSet, int lastObjectID) {
        if (this.objectArrays != null) {
            for (int i = 0; i < this.objectArrays.length; ++i) {
                int[] lastObjectIDs;
                int[] objectIDs = this.objectArrays[i];
                if (objectIDs == null || objectIDs.length == 0 || i >= ((PresetSet)lastSet).objectArrays.length || (lastObjectIDs = ((PresetSet)lastSet).objectArrays[i]) == null) continue;
                for (int objectID : lastObjectIDs) {
                    if (objectID == -1 || objectID != lastObjectID) continue;
                    return objectIDs[random.nextInt(objectIDs.length)];
                }
            }
        }
        return lastObjectID;
    }

    public boolean canReplaceObject(int objectID) {
        if (objectID <= 0) {
            return false;
        }
        if (this.objectArrays != null) {
            for (int[] objectArray : this.objectArrays) {
                if (objectArray == null) continue;
                for (int object : objectArray) {
                    if (object != objectID) continue;
                    return true;
                }
            }
        }
        if (this.nestedSetArrays != null) {
            for (PresetSet<?>[] nestedSetArray : this.nestedSetArrays) {
                for (int nestedSet : (int[])nestedSetArray) {
                    if (!nestedSet.canReplaceObject(objectID)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canReplaceTile(int tileID) {
        if (tileID <= 0) {
            return false;
        }
        if (this.tileArrays != null) {
            for (int[] tileArray : this.tileArrays) {
                if (tileArray == null) continue;
                for (int tile : tileArray) {
                    if (tile != tileID) continue;
                    return true;
                }
            }
        }
        if (this.nestedSetArrays != null) {
            for (PresetSet<?>[] nestedSetArray : this.nestedSetArrays) {
                for (int nestedSet : (int[])nestedSetArray) {
                    if (!nestedSet.canReplaceTile(tileID)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    protected T incursion(Biome ... biomes) {
        for (Biome biome : biomes) {
            incursionSets.computeIfAbsent(biome, b -> new HashMap()).computeIfAbsent(this.getClass(), c -> new HashSet()).add(this);
        }
        return (T)this;
    }

    protected T incursion() {
        return this.incursion(BiomeRegistry.PLAINS, BiomeRegistry.SWAMP, BiomeRegistry.SNOW, BiomeRegistry.DESERT, BiomeRegistry.FOREST, BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION, BiomeRegistry.SNOW_DEEP_CAVE_INCURSION, BiomeRegistry.DESERT_DEEP_CAVE_INCURSION, BiomeRegistry.SLIME_CAVE, BiomeRegistry.GRAVEYARD, BiomeRegistry.SPIDER_CASTLE, BiomeRegistry.SUN_ARENA, BiomeRegistry.MOON_ARENA, BiomeRegistry.CRYSTAL_HOLLOW);
    }

    protected T surface(Biome ... biomes) {
        for (Biome biome : biomes) {
            surfaceBiomeSets.computeIfAbsent(biome, b -> new HashMap()).computeIfAbsent(this.getClass(), c -> new HashSet()).add(this);
        }
        return (T)this;
    }

    protected T surface() {
        return this.surface(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SWAMP, BiomeRegistry.SNOW, BiomeRegistry.DESERT);
    }

    protected T cave(Biome ... biomes) {
        for (Biome biome : biomes) {
            caveBiomeSets.computeIfAbsent(biome, b -> new HashMap()).computeIfAbsent(this.getClass(), c -> new HashSet()).add(this);
        }
        return (T)this;
    }

    protected T cave() {
        return this.cave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SWAMP, BiomeRegistry.SNOW, BiomeRegistry.DESERT);
    }

    protected T deepCave(Biome ... biomes) {
        for (Biome biome : biomes) {
            deepCaveBiomeSets.computeIfAbsent(biome, b -> new HashMap()).computeIfAbsent(this.getClass(), c -> new HashSet()).add(this);
        }
        return (T)this;
    }

    protected T deepCave() {
        return this.deepCave(BiomeRegistry.FOREST, BiomeRegistry.PLAINS, BiomeRegistry.SWAMP, BiomeRegistry.SNOW, BiomeRegistry.DESERT);
    }

    protected T all() {
        alwaysAllowSets.computeIfAbsent(this.getClass(), c -> new HashSet()).add(this);
        return (T)this;
    }

    protected T all(Biome ... biomes) {
        return ((PresetSet)((PresetSet)((PresetSet)this.surface(biomes)).cave(biomes)).deepCave(biomes)).incursion(biomes);
    }

    public static <C extends PresetSet<?>> C[] getIncursionSets(Class<C> presetType, Biome biome) {
        return PresetSet.getSets(presetType, incursionSets, (Biome)biome);
    }

    public static <C extends PresetSet<?>> C[] getSurfaceSets(Class<C> presetType, Biome biome) {
        return PresetSet.getSets(presetType, surfaceBiomeSets, (Biome)biome);
    }

    public static <C extends PresetSet<?>> C[] getCaveSets(Class<C> presetType, Biome biome) {
        return PresetSet.getSets(presetType, caveBiomeSets, (Biome)biome);
    }

    public static <C extends PresetSet<?>> C[] getDeepCaveSets(Class<C> presetType, Biome biome) {
        return PresetSet.getSets(presetType, deepCaveBiomeSets, (Biome)biome);
    }

    private static <C extends PresetSet<?>> C[] getSets(Class<C> presetType, HashMap<Biome, HashMap<Class<? extends PresetSet<?>>, HashSet<PresetSet<?>>>> presetSetList, Biome biome) {
        HashSet<PresetSet<?>> sets;
        ArrayList output = new ArrayList();
        HashMap<Class<PresetSet<?>>, HashSet<PresetSet<?>>> biomeSet = presetSetList.get(biome);
        HashSet<PresetSet<?>> alwaysSets = alwaysAllowSets.get(presetType);
        if (alwaysSets != null) {
            output.addAll(alwaysSets);
        }
        if (biomeSet != null && (sets = biomeSet.get(presetType)) != null) {
            output.addAll(sets);
        }
        if (!output.isEmpty()) {
            return output.toArray((PresetSet[])Array.newInstance(presetType, output.size()));
        }
        return (PresetSet[])Array.newInstance(presetType, 0);
    }

    @SafeVarargs
    public static <C extends PresetSet<C>> C[] getReducedSetForIncursionBiome(C[] set, Biome biome, C ... alwaysInclude) {
        return PresetSet.getReducedSetForBiome(set, (Biome)biome, incursionSets, (boolean)true, alwaysInclude);
    }

    @SafeVarargs
    public static <C extends PresetSet<C>> C[] getReducedSetForIncursionBiome(C[] set, Biome biome, boolean returnValidSetOnNoMatch, C ... alwaysInclude) {
        return PresetSet.getReducedSetForBiome(set, (Biome)biome, incursionSets, (boolean)returnValidSetOnNoMatch, alwaysInclude);
    }

    @SafeVarargs
    public static <C extends PresetSet<C>> C[] getReducedSetForBiome(C[] set, Biome biome, LevelIdentifier identifier, C ... alwaysInclude) {
        return PresetSet.getReducedSetForBiome(set, (Biome)biome, (LevelIdentifier)identifier, (boolean)true, alwaysInclude);
    }

    @SafeVarargs
    public static <C extends PresetSet<C>> C[] getReducedSetForBiome(C[] set, Biome biome, LevelIdentifier identifier, boolean returnRandomValidSetOnNoMatch, C ... alwaysInclude) {
        HashMap<Biome, HashMap<Class<? extends PresetSet<?>>, HashSet<PresetSet<?>>>> biomeSets = surfaceBiomeSets;
        if (identifier == LevelIdentifier.CAVE_IDENTIFIER) {
            biomeSets = caveBiomeSets;
        } else if (identifier == LevelIdentifier.DEEP_CAVE_IDENTIFIER) {
            biomeSets = deepCaveBiomeSets;
        }
        return PresetSet.getReducedSetForBiome(set, (Biome)biome, biomeSets, (boolean)returnRandomValidSetOnNoMatch, alwaysInclude);
    }

    @SafeVarargs
    private static <C extends PresetSet<C>> C[] getReducedSetForBiome(C[] set, Biome biome, HashMap<Biome, HashMap<Class<? extends PresetSet<?>>, HashSet<PresetSet<?>>>> presetSetList, boolean returnValidSetOnNoMatch, C ... alwaysInclude) {
        HashSet<PresetSet<?>> presetSets;
        ArrayList<C> reducedSet = new ArrayList<C>(set.length);
        Class<?> setClass = set.getClass().getComponentType();
        HashSet<PresetSet<?>> alwaysSets = alwaysAllowSets.get(setClass);
        HashMap<Class<PresetSet<?>>, HashSet<PresetSet<?>>> biomeSet = presetSetList.get(biome);
        if (biomeSet != null) {
            presetSets = biomeSet.get(setClass);
            if (presetSets != null) {
                for (C presetSet : set) {
                    if (!presetSets.contains(presetSet)) continue;
                    reducedSet.add(presetSet);
                }
            }
            if (alwaysSets != null) {
                for (C presetSet : set) {
                    if (!alwaysSets.contains(presetSet)) continue;
                    reducedSet.add(presetSet);
                }
            }
        }
        Collections.addAll(reducedSet, alwaysInclude);
        if (reducedSet.isEmpty() && returnValidSetOnNoMatch) {
            if (biomeSet != null && (presetSets = biomeSet.get(setClass)) != null) {
                return presetSets.toArray((PresetSet[])Array.newInstance(set.getClass().getComponentType(), presetSets.size()));
            }
            return set;
        }
        return reducedSet.toArray((PresetSet[])Array.newInstance(set.getClass().getComponentType(), reducedSet.size()));
    }
}

