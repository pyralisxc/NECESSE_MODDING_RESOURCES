/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.set;

import java.util.ArrayList;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.gfx.forms.presets.PresetDebugPreviewForm;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.set.PresetSet;

public class RockAndOreSet
extends PresetSet<RockAndOreSet> {
    public static final RockAndOreSet forest = (RockAndOreSet)new RockAndOreSet("rock", "ironorerock", "copperorerock", "goldorerock", "surfacerock", "surfacerocksmall", "rocktile").surface(BiomeRegistry.FOREST);
    public static final RockAndOreSet snow;
    public static final RockAndOreSet plains;
    public static final RockAndOreSet swamp;
    public static final RockAndOreSet desert;
    public static final RockAndOreSet forestcave;
    public static final RockAndOreSet snowcave;
    public static final RockAndOreSet plainscave;
    public static final RockAndOreSet swampcave;
    public static final RockAndOreSet desertcave;
    public static final RockAndOreSet deepforestcave;
    public static final RockAndOreSet deepsnowcave;
    public static final RockAndOreSet deepplainscave;
    public static final RockAndOreSet deepswampcave;
    public static final RockAndOreSet deepdesertcave;
    public final int rock;
    public final int ironOre;
    public final int copperOre;
    public final int goldOre;
    public final int surfaceRock;
    public final int surfaceRockR;
    public final int surfaceRockSmall;
    public final int floorTile;

    public RockAndOreSet(String rock, String ironOre, String copperOre, String goldOre, String surfaceRock, String surfaceRockSmall, String floorTile) {
        this.rock = ObjectRegistry.getObjectID(rock);
        this.objectArrays = new int[][]{{this.rock}, {this.ironOre = ObjectRegistry.getObjectID(ironOre)}, {this.copperOre = ObjectRegistry.getObjectID(copperOre)}, {this.goldOre = ObjectRegistry.getObjectID(goldOre)}, {this.surfaceRock = ObjectRegistry.getObjectID(surfaceRock)}, {this.surfaceRockR = this.surfaceRock + 1}, {this.surfaceRockSmall = ObjectRegistry.getObjectID(surfaceRockSmall)}};
        this.floorTile = TileRegistry.getTileID(floorTile);
        this.tileArrays = new int[][]{{this.floorTile}};
    }

    public static String getRandomSignatureOreStringID(Biome biome, LevelIdentifier levelIdentifier, GameRandom seededRandom) {
        if (levelIdentifier.equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
            return null;
        }
        ArrayList<String> possibleOres = new ArrayList<String>();
        possibleOres.add("ironore");
        possibleOres.add("goldore");
        possibleOres.add("copperore");
        if (levelIdentifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
            if (biome.equals(BiomeRegistry.SWAMP)) {
                possibleOres.add("ivyore");
            } else if (biome.equals(BiomeRegistry.DESERT)) {
                possibleOres.add("quartz");
            }
        } else if (levelIdentifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            possibleOres.add("tungstenore");
            possibleOres.add("lifequartz");
            if (biome.equals(BiomeRegistry.SWAMP)) {
                possibleOres.add("myceliumore");
            } else if (biome.equals(BiomeRegistry.SNOW)) {
                possibleOres.add("glacialore");
            } else if (biome.equals(BiomeRegistry.DESERT)) {
                possibleOres.add("ancientfossilore");
            }
        }
        return (String)seededRandom.getOneOf(possibleOres) + RockAndOreSet.getOreSuffix(biome, levelIdentifier);
    }

    public static String getOreSuffix(Biome biome, LevelIdentifier levelIdentifier) {
        if (levelIdentifier.equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
            return null;
        }
        if (levelIdentifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
            if (biome.equals(BiomeRegistry.SWAMP)) {
                return "swamp";
            }
            if (biome.equals(BiomeRegistry.SNOW)) {
                return "snow";
            }
            if (biome.equals(BiomeRegistry.DESERT)) {
                return "sandstone";
            }
            if (biome.equals(BiomeRegistry.PLAINS)) {
                return "graniterock";
            }
            if (biome.equals(BiomeRegistry.FOREST)) {
                return "rock";
            }
        }
        if (biome.equals(BiomeRegistry.SWAMP)) {
            return "deepswamprock";
        }
        if (biome.equals(BiomeRegistry.SNOW)) {
            return "deepsnowrock";
        }
        if (biome.equals(BiomeRegistry.DESERT)) {
            return "deepsandstone";
        }
        if (biome.equals(BiomeRegistry.PLAINS)) {
            return "basaltrock";
        }
        if (biome.equals(BiomeRegistry.FOREST)) {
            return "deeprock";
        }
        return null;
    }

    static {
        forestcave = (RockAndOreSet)new RockAndOreSet("rock", "ironorerock", "copperorerock", "goldorerock", "caverock", "caverocksmall", "rocktile").cave(BiomeRegistry.FOREST);
        deepforestcave = (RockAndOreSet)new RockAndOreSet("deeprock", "ironoredeeprock", "copperoredeeprock", "goldoredeeprock", "deepcaverock", "deepcaverocksmall", "deeprocktile").deepCave(BiomeRegistry.FOREST);
        snow = (RockAndOreSet)new RockAndOreSet("snowrock", "ironoresnow", "copperoresnow", "goldoresnow", "snowsurfacerock", "snowsurfacerocksmall", "snowrocktile").surface(BiomeRegistry.SNOW);
        snowcave = (RockAndOreSet)new RockAndOreSet("snowrock", "ironoresnow", "copperoresnow", "goldoresnow", "snowcaverock", "snowcaverocksmall", "snowrocktile").cave(BiomeRegistry.SNOW);
        deepsnowcave = (RockAndOreSet)new RockAndOreSet("deepsnowrock", "ironoredeepsnowrock", "copperoredeepsnowrock", "goldoredeepsnowrock", "deepsnowcaverock", "deepsnowcaverocksmall", "deepsnowrocktile").deepCave(BiomeRegistry.SNOW);
        plains = (RockAndOreSet)new RockAndOreSet("graniterock", "ironoregraniterock", "copperoregraniterock", "goldoregraniterock", "surfacerock", "surfacerocksmall", "graniterocktile").surface(BiomeRegistry.PLAINS);
        plainscave = (RockAndOreSet)new RockAndOreSet("graniterock", "ironoregraniterock", "copperoregraniterock", "goldoregraniterock", "granitecaverock", "granitecaverocksmall", "graniterocktile").cave(BiomeRegistry.PLAINS);
        deepplainscave = (RockAndOreSet)new RockAndOreSet("basaltrock", "ironorebasaltrock", "copperorebasaltrock", "goldorebasaltrock", "basaltcaverock", "basaltcaverocksmall", "basaltrocktile").deepCave(BiomeRegistry.PLAINS);
        swamp = (RockAndOreSet)new RockAndOreSet("swamprock", "ironoreswamp", "copperoreswamp", "goldoreswamp", "swampsurfacerock", "swampsurfacerocksmall", "swamprocktile").surface(BiomeRegistry.SWAMP);
        swampcave = (RockAndOreSet)new RockAndOreSet("swamprock", "ironoreswamp", "copperoreswamp", "goldoreswamp", "swampcaverock", "swampcaverocksmall", "swamprocktile").cave(BiomeRegistry.SWAMP);
        deepswampcave = (RockAndOreSet)new RockAndOreSet("deepswamprock", "ironoredeepswamprock", "copperoredeepswamprock", "goldoredeepswamprock", "deepswampcaverock", "deepswampcaverocksmall", "deepswamprocktile").deepCave(BiomeRegistry.SWAMP);
        desert = (RockAndOreSet)new RockAndOreSet("sandstonerock", "ironoresandstone", "copperoresandstone", "goldoresandstone", "sandsurfacerock", "sandsurfacerocksmall", "sandstonetile").surface(BiomeRegistry.DESERT);
        desertcave = (RockAndOreSet)new RockAndOreSet("sandstonerock", "ironoresandstone", "copperoresandstone", "goldoresandstone", "sandcaverock", "sandcaverocksmall", "sandstonetile").cave(BiomeRegistry.DESERT);
        deepdesertcave = (RockAndOreSet)new RockAndOreSet("deepsandstonerock", "ironoredeepsandstone", "copperoredeepsandstone", "goldoredeepsandstone", "deepsandcaverock", "deepsandcaverocksmall", "deepsandstonetile").deepCave(BiomeRegistry.DESERT);
        PresetDebugPreviewForm.registerPresetSet(RockAndOreSet.class);
    }
}

