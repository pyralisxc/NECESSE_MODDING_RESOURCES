/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class BiomeGeneratorStackLevel
extends Level {
    protected int seed = 0;
    protected BiomeGeneratorStack generatorStack;

    public BiomeGeneratorStackLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public BiomeGeneratorStackLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity, int seed) {
        super(identifier, width, height, worldEntity);
        this.seed = seed;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (this.seed != 0) {
            save.addInt("seed", this.seed);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.seed = save.getInt("seed", 0, false);
    }

    @Override
    public boolean isOneWorldLevel() {
        return true;
    }

    @Override
    public void generateRegion(Region region) {
        if (this.generatorStack == null) {
            this.generatorStack = this.seed != 0 ? new BiomeGeneratorStack(this.seed) : this.getWorldEntity().getGeneratorStack();
        }
    }
}

