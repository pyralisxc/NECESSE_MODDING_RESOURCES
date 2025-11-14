/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import necesse.engine.GameTileRange;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.GeneratorPlaceFactory;
import necesse.engine.world.biomeGenerator.GeneratorStack;
import necesse.engine.world.biomeGenerator.layers.BiomeRulesGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.BiomeSpreadGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.CaveLavaGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.CaveRockPlaceGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.ChanceVeinRandomSizeGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.ChanceVeinStaticSizeGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.ClearSinglesGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.InitialBiomeGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.MostFrequentCellularAutomatonGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.MultiCellularAutomatonGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.OceanGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.PerlinVeinGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.RandomReplaceGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.RandomReplaceValueGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.RiverGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.ScalingNearestGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.ScalingRandomGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.SimplexVeinGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.SingleCellularAutomatonGeneratorLayer;
import necesse.engine.world.biomeGenerator.layers.SwampLandGeneratorLayer;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.regionSystem.Region;

public class BiomeGeneratorStack
extends GeneratorStack {
    public static Color waterDebugColor = new Color(31, 96, 170);
    public static Color lavaDebugColor = new Color(170, 68, 31);
    protected GeneratorLayer lazyBiomeIDLayer;
    protected OceanGeneratorLayer oceanLayer;
    protected RiverGeneratorLayer riverLayer;
    protected GeneratorStack waterBranch;
    protected GeneratorStack biomeSpreadBranch;
    protected GeneratorStack caveRocksBranch;
    protected GeneratorStack caveRiverBranch;
    protected GeneratorStack caveLavaBranch;
    protected GeneratorStack deepCaveRockBranch;
    protected GeneratorStack deepCaveLavaBranch;
    public HashMap<String, GeneratorStack> uniqueIDBranchingStacks = new HashMap();

    public BiomeGeneratorStack(int worldSeed) {
        super(worldSeed);
        int i;
        this.addLayer(new InitialBiomeGeneratorLayer(10));
        this.addLayer(new MostFrequentCellularAutomatonGeneratorLayer());
        for (i = 0; i < 4; ++i) {
            this.addLayer(new BiomeRulesGeneratorLayer(5 + i));
        }
        this.addLayer(new ClearSinglesGeneratorLayer(3));
        this.addLayer(new ClearSinglesGeneratorLayer(2));
        this.addLayer(new ClearSinglesGeneratorLayer(1));
        this.addLayer(new ScalingRandomGeneratorLayer(20));
        this.addLayer(new ScalingNearestGeneratorLayer(25));
        this.addLayer(new MostFrequentCellularAutomatonGeneratorLayer());
        this.addLayer(new MostFrequentCellularAutomatonGeneratorLayer());
        this.addLayer(new ScalingRandomGeneratorLayer(30));
        this.addLayer(new ScalingNearestGeneratorLayer(35));
        this.addLayer(new MostFrequentCellularAutomatonGeneratorLayer());
        this.addLayer(new MostFrequentCellularAutomatonGeneratorLayer());
        this.addLayer(new ScalingRandomGeneratorLayer(40));
        this.lazyBiomeIDLayer = this.addLayer(new ScalingNearestGeneratorLayer(45));
        this.biomeSpreadBranch = this.addBranchingStack(new GeneratorStack(worldSeed * GameRandom.prime(10)));
        this.biomeSpreadBranch.addLayer(new BiomeSpreadGeneratorLayer(60, new GameTileRange(4, new Point[0])));
        this.waterBranch = this.addBranchingStack(new GeneratorStack(worldSeed * GameRandom.prime(20)));
        this.oceanLayer = this.waterBranch.addLayer(new OceanGeneratorLayer(80, 0.4f, 0.45f, 0.04f));
        this.riverLayer = this.waterBranch.addLayer(new RiverGeneratorLayer(60, 0.3f, 0.04f, 0.05f));
        this.waterBranch.addLayer(new SwampLandGeneratorLayer(90, 3.0f, 0.42f, 0.04f, 4, this::getLazyBiomeID));
        for (i = 0; i < 4; ++i) {
            this.addLayer(new MostFrequentCellularAutomatonGeneratorLayer());
        }
        this.caveRocksBranch = this.addBranchingStack(new GeneratorStack(worldSeed * GameRandom.prime(30)));
        this.caveRocksBranch.addLayer(new CaveRockPlaceGeneratorLayer(120){

            @Override
            protected Biome getBiome(int x, int y) {
                return BiomeGeneratorStack.this.getLazyBiome(x, y);
            }

            @Override
            protected float getRockPlaceChance(Biome biome, int x, int y) {
                return biome.getGenerationCaveRockObjectChance();
            }
        });
        for (i = 0; i < 4; ++i) {
            this.caveRocksBranch.addLayer(new SingleCellularAutomatonGeneratorLayer(4, 3, -1, 0));
        }
        this.caveRiverBranch = this.addBranchingStack(new GeneratorStack(worldSeed * GameRandom.prime(20)));
        this.caveRiverBranch.addLayer(new RiverGeneratorLayer(60, 0.3f, 0.02f, 0.05f));
        this.caveLavaBranch = this.addBranchingStack(new GeneratorStack(worldSeed * GameRandom.prime(20)));
        this.caveLavaBranch.addLayer(new CaveLavaGeneratorLayer(65, 1.5f, 0.335f));
        for (i = 0; i < 2; ++i) {
            this.caveLavaBranch.addLayer(new MostFrequentCellularAutomatonGeneratorLayer());
        }
        this.deepCaveRockBranch = this.addBranchingStack(new GeneratorStack(worldSeed * GameRandom.prime(30)));
        this.deepCaveRockBranch.addLayer(new CaveRockPlaceGeneratorLayer(130){

            @Override
            protected Biome getBiome(int x, int y) {
                return BiomeGeneratorStack.this.getLazyBiome(x, y);
            }

            @Override
            protected float getRockPlaceChance(Biome biome, int x, int y) {
                return biome.getGenerationDeepCaveRockObjectChance();
            }
        });
        for (i = 0; i < 4; ++i) {
            this.deepCaveRockBranch.addLayer(new SingleCellularAutomatonGeneratorLayer(4, 3, -1, 0));
        }
        this.deepCaveLavaBranch = this.addBranchingStack(new GeneratorStack(worldSeed * GameRandom.prime(20)));
        this.deepCaveLavaBranch.addLayer(new CaveLavaGeneratorLayer(90, 4.0f, 0.41f));
        for (i = 0; i < 2; ++i) {
            this.deepCaveLavaBranch.addLayer(new MostFrequentCellularAutomatonGeneratorLayer());
        }
        for (Biome biome : BiomeRegistry.getBiomes()) {
            float weight = biome.getBiomeGenerationWeight();
            if (!(weight > 0.0f)) continue;
            biome.initializeGeneratorStack(this);
        }
    }

    public int getExpensiveBiomeID(int x, int y) {
        return this.get(x, y);
    }

    public Biome getExpensiveBiome(int x, int y) {
        return BiomeRegistry.getBiome(this.getExpensiveBiomeID(x, y));
    }

    public int getSpreadBiomeID(int x, int y) {
        return this.biomeSpreadBranch.get(x, y);
    }

    public Biome getSpreadBiome(int x, int y) {
        return BiomeRegistry.getBiome(this.getSpreadBiomeID(x, y));
    }

    public int getLazyBiomeID(int x, int y) {
        return this.lazyBiomeIDLayer.get(x, y);
    }

    public Biome getLazyBiome(int x, int y) {
        return BiomeRegistry.getBiome(this.getLazyBiomeID(x, y));
    }

    public boolean isSurfaceExpensiveWater(int x, int y) {
        return this.waterBranch.get(x, y) == -1;
    }

    public boolean isSurfaceExpensiveWaterOrBeach(int x, int y) {
        int value = this.waterBranch.get(x, y);
        return value < 0;
    }

    public boolean isSurfaceOcean(int x, int y) {
        return this.oceanLayer.get(x, y) == -1;
    }

    public boolean isSurfaceOceanBeach(int x, int y) {
        return this.oceanLayer.get(x, y) == -2;
    }

    public boolean isSurfaceOceanOrBeach(int x, int y) {
        int value = this.oceanLayer.get(x, y);
        return value < 0;
    }

    public boolean isSurfaceOceanOrRiver(int x, int y) {
        return this.riverLayer.get(x, y) == -1;
    }

    public boolean isSurfaceOceanOrRiverBeach(int x, int y) {
        return this.riverLayer.get(x, y) == -2;
    }

    public boolean isSurfaceOceanOrRiverOrBeach(int x, int y) {
        int value = this.riverLayer.get(x, y);
        return value < 0;
    }

    public boolean isCaveRock(int x, int y) {
        return this.caveRocksBranch.get(x, y) == -1;
    }

    public boolean isCaveRiver(int x, int y) {
        return this.caveRiverBranch.get(x, y) == -1;
    }

    public boolean isCaveLava(int x, int y) {
        return this.caveLavaBranch.get(x, y) == -1;
    }

    public boolean isCaveRiverOrLava(int x, int y) {
        return this.isCaveRiver(x, y) || this.isCaveLava(x, y);
    }

    public boolean isDeepCaveRock(int x, int y) {
        return this.deepCaveRockBranch.get(x, y) == -1;
    }

    public boolean isDeepCaveLava(int x, int y) {
        return this.deepCaveLavaBranch.get(x, y) == -1;
    }

    public GameRandom getNewRegionRandom(Region region) {
        return new GameRandom(this.worldSeed).nextSeeded(region.regionX).nextSeeded(region.regionY);
    }

    public void addRandomVeinsBranch(String veinUniqueID, float veinsPerRegion, int minVeinSize, int maxVeinSize, float placeChance, int smoothIterations, boolean circular) {
        this.addRandomVeinsBranch(veinUniqueID, veinUniqueID.hashCode(), veinsPerRegion, minVeinSize, maxVeinSize, placeChance, smoothIterations, circular);
    }

    public void addRandomVeinsBranch(String veinUniqueID, int seed, float veinsPerRegion, int minVeinSize, int maxVeinSize, float placeChance, int smoothIterations, boolean circular) {
        if (this.uniqueIDBranchingStacks.containsKey(veinUniqueID)) {
            throw new IllegalArgumentException("A generation vein with unique ID already exists: " + veinUniqueID);
        }
        GeneratorStack stack = this.addBranchingStack(new GeneratorStack(this.worldSeed * GameRandom.prime(seed)));
        double veinsPossibleInRegionSize = 16.0f / (float)maxVeinSize;
        double totalVeinsPossibleInRegion = veinsPossibleInRegionSize * veinsPossibleInRegionSize;
        double averageVeinSize = (double)(minVeinSize + maxVeinSize) / 2.0;
        float veinPlaceChance = (float)(1.0 / totalVeinsPossibleInRegion * (double)veinsPerRegion * ((double)maxVeinSize / averageVeinSize));
        if (minVeinSize == maxVeinSize) {
            stack.addLayer(new ChanceVeinStaticSizeGeneratorLayer(seed, minVeinSize, veinPlaceChance, -1, circular).setDebugName(veinUniqueID));
        } else {
            stack.addLayer(new ChanceVeinRandomSizeGeneratorLayer(seed, minVeinSize, maxVeinSize, veinPlaceChance, -1, circular).setDebugName(veinUniqueID));
        }
        if (placeChance < 1.0f) {
            stack.addLayer(new RandomReplaceValueGeneratorLayer(seed * 2, -1, 1.0f - placeChance, 0).setDebugName(veinUniqueID));
        }
        for (int i = 0; i < smoothIterations; ++i) {
            stack.addLayer(new SingleCellularAutomatonGeneratorLayer(4, 3, -1, 0).setDebugName(veinUniqueID));
        }
        this.uniqueIDBranchingStacks.put(veinUniqueID, stack);
    }

    public void addRandomCellularMapVeinsBranch(String veinUniqueID, float placeChance, int smoothIterations) {
        this.addRandomCellularMapVeinsBranch(veinUniqueID, veinUniqueID.hashCode(), placeChance, smoothIterations);
    }

    public void addRandomCellularMapVeinsBranch(String veinUniqueID, int seed, float placeChance, int smoothIterations) {
        if (this.uniqueIDBranchingStacks.containsKey(veinUniqueID)) {
            throw new IllegalArgumentException("A generation vein with unique ID already exists: " + veinUniqueID);
        }
        GeneratorStack stack = this.addBranchingStack(new GeneratorStack(this.worldSeed * GameRandom.prime(seed)));
        stack.addLayer(new RandomReplaceGeneratorLayer(seed, placeChance){

            @Override
            protected boolean shouldReplaceValue(int value) {
                return true;
            }

            @Override
            protected int getPlaceValue(int x, int y) {
                return -1;
            }

            @Override
            protected Color getDebugColor(int value) {
                if (value == -1) {
                    return new Color(255, 0, 255);
                }
                return super.getDebugColor(value);
            }
        }.setDebugName(veinUniqueID));
        for (int i = 0; i < smoothIterations; ++i) {
            stack.addLayer(new SingleCellularAutomatonGeneratorLayer(4, 3, -1, 0).setDebugName(veinUniqueID));
        }
        this.uniqueIDBranchingStacks.put(veinUniqueID, stack);
    }

    public void addRandomSimplexVeinsBranch(String veinUniqueID, float spreadModifier, float minValue, float placeChance, int smoothIterations) {
        this.addRandomSimplexVeinsBranch(veinUniqueID, veinUniqueID.hashCode(), spreadModifier, minValue, placeChance, smoothIterations);
    }

    public void addRandomSimplexVeinsBranch(String veinUniqueID, int seed, float spreadModifier, float minValue, float placeChance, int smoothIterations) {
        if (this.uniqueIDBranchingStacks.containsKey(veinUniqueID)) {
            throw new IllegalArgumentException("A generation vein with unique ID already exists: " + veinUniqueID);
        }
        GeneratorStack stack = this.addBranchingStack(new GeneratorStack(this.worldSeed * GameRandom.prime(seed)));
        stack.addLayer(new SimplexVeinGeneratorLayer(seed, spreadModifier, minValue, placeChance, -1).setDebugName(veinUniqueID));
        for (int i = 0; i < smoothIterations; ++i) {
            stack.addLayer(new MultiCellularAutomatonGeneratorLayer(4, 3, 0).setDebugName(veinUniqueID));
        }
        this.uniqueIDBranchingStacks.put(veinUniqueID, stack);
    }

    public void addRandomPerlinVeinsBranch(String veinUniqueID, float spreadModifier, float minValue, float placeChance, int smoothIterations) {
        this.addRandomPerlinVeinsBranch(veinUniqueID, veinUniqueID.hashCode(), spreadModifier, minValue, placeChance, smoothIterations);
    }

    public void addRandomPerlinVeinsBranch(String veinUniqueID, int seed, float spreadModifier, float minValue, float placeChance, int smoothIterations) {
        if (this.uniqueIDBranchingStacks.containsKey(veinUniqueID)) {
            throw new IllegalArgumentException("A generation vein with unique ID already exists: " + veinUniqueID);
        }
        GeneratorStack stack = this.addBranchingStack(new GeneratorStack(this.worldSeed * GameRandom.prime(seed)));
        stack.addLayer(new PerlinVeinGeneratorLayer(seed, spreadModifier, minValue, placeChance, -1).setDebugName(veinUniqueID));
        for (int i = 0; i < smoothIterations; ++i) {
            stack.addLayer(new MultiCellularAutomatonGeneratorLayer(4, 3, 0).setDebugName(veinUniqueID));
        }
        this.uniqueIDBranchingStacks.put(veinUniqueID, stack);
    }

    public GeneratorPlaceFactory startPlace(Biome biome, Region region, GameRandom random) {
        return new GeneratorPlaceFactory(this, biome, region, random.nextSeeded());
    }

    public GeneratorPlaceFactory startPlaceOnVein(Biome biome, Region region, GameRandom random, String veinUniqueID) {
        return new GeneratorPlaceFactory(this, biome, region, random.nextSeeded(veinUniqueID.hashCode())).onVein(veinUniqueID);
    }
}

