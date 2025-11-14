/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator;

import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.biomeGenerator.GeneratorStack;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.FruitGrowerObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.regionSystem.Region;

public class GeneratorPlaceFactory {
    protected BiomeGeneratorStack stack;
    protected Biome biome;
    protected Region region;
    protected GameRandom random;

    protected GeneratorPlaceFactory(BiomeGeneratorStack stack, Biome biome, Region region, GameRandom random) {
        this.stack = stack;
        this.biome = biome;
        this.region = region;
        this.random = random;
    }

    protected GeneratorPlaceFactory(GeneratorPlaceFactory parent) {
        this(parent.stack, parent.biome, parent.region, parent.random);
    }

    public GeneratorPlaceFactory onVein(String veinUniqueID) {
        final GeneratorStack veinStack = this.stack.uniqueIDBranchingStacks.get(veinUniqueID);
        if (veinStack == null) {
            throw new IllegalArgumentException("No generation vein with unique ID: " + veinUniqueID);
        }
        final GeneratorPlaceFactory me = this;
        return new GeneratorPlaceFactory(this){

            @Override
            protected boolean testTile(int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
                return me.testTile(regionTileX, regionTileY, level, tileX, tileY) && veinStack.get(tileX, tileY) == -1;
            }
        };
    }

    public GeneratorPlaceFactory notOnBeach() {
        return this.test((random, region, regionTileX, regionTileY, level, tileX, tileY) -> !this.stack.isSurfaceOceanOrRiverBeach(tileX, tileY));
    }

    public GeneratorPlaceFactory notOnWater() {
        return this.test((random, region, regionTileX, regionTileY, level, tileX, tileY) -> !this.stack.isSurfaceExpensiveWater(tileX, tileY));
    }

    public GeneratorPlaceFactory onlyOnTile(int tileID) {
        return this.test((random, region, regionTileX, regionTileY, level, tileX, tileY) -> region.tileLayer.getTileIDByRegion(regionTileX, regionTileY) == tileID);
    }

    public GeneratorPlaceFactory testTileID(Predicate<Integer> tileIDPredicate) {
        return this.test((random, region, regionTileX, regionTileY, level, tileX, tileY) -> tileIDPredicate.test(region.tileLayer.getTileIDByRegion(regionTileX, regionTileY)));
    }

    public GeneratorPlaceFactory onlyOnObject(String objectStringID) {
        int objectID = ObjectRegistry.getObjectID(objectStringID);
        return this.test((random, region, regionTileX, regionTileY, level, tileX, tileY) -> region.objectLayer.getObjectIDByRegion(ObjectLayerRegistry.BASE_LAYER, regionTileX, regionTileY) == objectID);
    }

    public GeneratorPlaceFactory onlyOnObject(int objectID) {
        return this.test((random, region, regionTileX, regionTileY, level, tileX, tileY) -> region.objectLayer.getObjectIDByRegion(ObjectLayerRegistry.BASE_LAYER, regionTileX, regionTileY) == objectID);
    }

    public GeneratorPlaceFactory testObjectID(Predicate<Integer> objectIDPredicate) {
        return this.test((random, region, regionTileX, regionTileY, level, tileX, tileY) -> objectIDPredicate.test(region.objectLayer.getObjectIDByRegion(ObjectLayerRegistry.BASE_LAYER, regionTileX, regionTileY)));
    }

    public GeneratorPlaceFactory chance(double chance) {
        return this.test((random, region, regionTileX, regionTileY, level, tileX, tileY) -> this.random.getChance(chance));
    }

    public GeneratorPlaceFactory chancePerRegion(double placesPerRegion) {
        double chance = placesPerRegion / (double)(this.region.tileLayer.region.tileWidth * this.region.tileLayer.region.tileHeight);
        return this.chance(chance);
    }

    public GeneratorPlaceFactory onRandom(Predicate<GameRandom> randomPredicate) {
        return this.test((random, region, regionTileX, regionTileY, level, tileX, tileY) -> randomPredicate.test(this.random));
    }

    public GeneratorPlaceFactory test(final RegionCanPlaceFunction canPlaceFunction) {
        final GeneratorPlaceFactory me = this;
        return new GeneratorPlaceFactory(this){

            @Override
            protected boolean testTile(int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
                return me.testTile(regionTileX, regionTileY, level, tileX, tileY) && canPlaceFunction.canPlace(this.random, this.region, regionTileX, regionTileY, level, tileX, tileY);
            }
        };
    }

    public void placeTile(int tileID) {
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> region.tileLayer.setTileByRegion(regionTileX, regionTileY, tileID));
    }

    public void placeObject(String objectStringID) {
        GameObject object = ObjectRegistry.getObject(objectStringID);
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> {
            if (object.canPlace(level, tileX, tileY, 0, false) == null) {
                object.placeObject(level, tileX, tileY, 0, false);
            }
        });
    }

    public void placeObjectForced(String objectStringID) {
        GameObject object = ObjectRegistry.getObject(objectStringID);
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> object.placeObject(level, tileX, tileY, 0, false));
    }

    public void placeObjectRandomRotation(String objectStringID) {
        GameObject object = ObjectRegistry.getObject(objectStringID);
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> {
            if (object.canPlace(level, tileX, tileY, this.random.nextInt(4), false) == null) {
                object.placeObject(level, tileX, tileY, this.random.nextInt(4), false);
            }
        });
    }

    public void placeObjectForcedRandomRotation(String objectStringID) {
        GameObject object = ObjectRegistry.getObject(objectStringID);
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> object.placeObject(level, tileX, tileY, this.random.nextInt(4), false));
    }

    public <T extends ObjectEntity> void placeObjectEntity(String objectStringID, Class<T> objectEntityClass, BiConsumer<GameRandom, T> handler) {
        GameObject object = ObjectRegistry.getObject(objectStringID);
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> {
            if (object.canPlace(level, tileX, tileY, 0, false) == null) {
                object.placeObject(level, tileX, tileY, 0, false);
                handler.accept(this.random, level.entityManager.getObjectEntity(tileX, tileY, objectEntityClass));
            }
        });
    }

    public <T extends ObjectEntity> void placeObjectEntityForced(String objectStringID, Class<T> objectEntityClass, BiConsumer<GameRandom, T> handler) {
        GameObject object = ObjectRegistry.getObject(objectStringID);
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> {
            object.placeObject(level, tileX, tileY, 0, false);
            handler.accept(this.random, level.entityManager.getObjectEntity(tileX, tileY, objectEntityClass));
        });
    }

    public <T extends ObjectEntity> void placeObjectEntityRandomRotation(String objectStringID, Class<T> objectEntityClass, BiConsumer<GameRandom, T> handler) {
        GameObject object = ObjectRegistry.getObject(objectStringID);
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> {
            if (object.canPlace(level, tileX, tileY, this.random.nextInt(4), false) == null) {
                object.placeObject(level, tileX, tileY, this.random.nextInt(4), false);
                handler.accept(this.random, level.entityManager.getObjectEntity(tileX, tileY, objectEntityClass));
            }
        });
    }

    public <T extends ObjectEntity> void placeObjectEntityForcedRandomRotation(String objectStringID, Class<T> objectEntityClass, BiConsumer<GameRandom, T> handler) {
        GameObject object = ObjectRegistry.getObject(objectStringID);
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> {
            object.placeObject(level, tileX, tileY, this.random.nextInt(4), false);
            handler.accept(this.random, level.entityManager.getObjectEntity(tileX, tileY, objectEntityClass));
        });
    }

    public void placeObjectFruitGrower(String objectStringID) {
        this.placeObjectEntity(objectStringID, FruitGrowerObjectEntity.class, (random, entity) -> entity.setRandomStage((GameRandom)random));
    }

    public <T extends Mob> void placeMob(BiFunction<Level, GameRandom, T> mobConstructor, BiConsumer<GameRandom, T> handler) {
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> {
            int randomY;
            int posY;
            int randomX;
            int posX;
            Mob mob = (Mob)mobConstructor.apply(level, random);
            if (!mob.collidesWith(level, posX = tileX * 32 + 16 + (randomX = random.getIntBetween(-5, 5)), posY = tileY * 32 + 16 + (randomY = random.getIntBetween(-5, 5)))) {
                mob.onSpawned(posX, posY);
                level.entityManager.mobs.addHidden(mob);
                if (handler != null) {
                    handler.accept(random, mob);
                }
            }
        });
    }

    public <T extends Mob> void placeMob(String mobStringID, Class<T> mobClass, BiConsumer<GameRandom, T> handler) {
        this.placeMob((level, random) -> (Mob)mobClass.cast(MobRegistry.getMob(mobStringID, level)), handler);
    }

    public <T extends Mob> void placeMob(String mobStringID) {
        this.placeMob(mobStringID, Mob.class, null);
    }

    public <T extends Mob> void placeMob(TicketSystemList<String> mobStringIDs) {
        this.placeMob((level, random) -> MobRegistry.getMob((String)mobStringIDs.getRandomObject((Random)random), level), null);
    }

    private boolean isCrate(int object, int ... crateObjects) {
        for (int crateObject : crateObjects) {
            if (object != crateObject) continue;
            return true;
        }
        return false;
    }

    public void placeCrates(String ... crateObjectStringIDs) {
        int[] crateObjects = new int[crateObjectStringIDs.length];
        for (int i = 0; i < crateObjectStringIDs.length; ++i) {
            crateObjects[i] = ObjectRegistry.getObjectID(crateObjectStringIDs[i]);
        }
        this.customPlace((random, region, regionTileX, regionTileY, level, tileX, tileY) -> {
            if (!(level.getObjectID(tileX - 1, tileY) != 0 && !this.isCrate(level.getObjectID(tileX - 1, tileY), crateObjects) || level.getObjectID(tileX + 1, tileY) != 0 && !this.isCrate(level.getObjectID(tileX + 1, tileY), crateObjects) || level.getObjectID(tileX, tileY - 1) != 0 && !this.isCrate(level.getObjectID(tileX, tileY - 1), crateObjects) || level.getObjectID(tileX, tileY + 1) != 0 && !this.isCrate(level.getObjectID(tileX, tileY + 1), crateObjects))) {
                return;
            }
            int crateID = crateObjects[random.nextInt(crateObjects.length)];
            GameObject object = ObjectRegistry.getObject(crateID);
            if (object.canPlace(level, tileX, tileY, 0, false) != null) {
                return;
            }
            object.placeObject(level, tileX, tileY, 0, false);
        });
    }

    public void customPlace(final RegionPlaceFunction placeFunction) {
        final GeneratorPlaceFactory me = this;
        new GeneratorPlaceFactory(this){

            @Override
            protected boolean testTile(int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
                return me.testTile(regionTileX, regionTileY, level, tileX, tileY);
            }

            @Override
            protected void runPlace(int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
                placeFunction.place(this.random, this.region, regionTileX, regionTileY, level, tileX, tileY);
            }
        }.run();
    }

    protected void runPlace(int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
    }

    protected boolean testTile(int regionTileX, int regionTileY, Level level, int tileX, int tileY) {
        return true;
    }

    protected void run() {
        for (int regionTileX = 0; regionTileX < this.region.tileLayer.region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < this.region.tileLayer.region.tileHeight; ++regionTileY) {
                int tileX = regionTileX + this.region.tileXOffset;
                int tileY = regionTileY + this.region.tileYOffset;
                if (this.biome != null && this.biome.getID() != this.stack.getSpreadBiomeID(tileX, tileY) || !this.testTile(regionTileX, regionTileY, this.region.manager.level, tileX, tileY)) continue;
                this.runPlace(regionTileX, regionTileY, this.region.manager.level, tileX, tileY);
            }
        }
    }

    @FunctionalInterface
    public static interface RegionCanPlaceFunction {
        public boolean canPlace(GameRandom var1, Region var2, int var3, int var4, Level var5, int var6, int var7);
    }

    @FunctionalInterface
    public static interface RegionPlaceFunction {
        public void place(GameRandom var1, Region var2, int var3, int var4, Level var5, int var6, int var7);
    }
}

