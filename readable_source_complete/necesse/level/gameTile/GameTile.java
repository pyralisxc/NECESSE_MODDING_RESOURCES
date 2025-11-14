/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.events.loot.TileLootTableDropsEvent;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileLightDrawOptions;
import necesse.gfx.drawables.LevelTileLiquidDrawOptions;
import necesse.gfx.drawables.LevelTileTerrainDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.gfx.gameTexture.SharedGameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameTile.LiquidTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SimulatePriorityList;

public class GameTile {
    public static SharedGameTexture tileTextures;
    public static GameTextureSection tileBlankTexture;
    public static GameTextureSection tileShoreTexture;
    private static GameTextureSection tileErrorTexture;
    public static GameTexture generatedTileTexture;
    public Item.Rarity rarity = Item.Rarity.NORMAL;
    public final IDData idData = new IDData();
    public final boolean terrainSplatting;
    public final boolean isLiquid;
    public String[] itemCategoryTree = new String[]{"tiles"};
    public String[] craftingCategoryTree = new String[]{"tiles"};
    public HashSet<String> itemGlobalIngredients = new HashSet();
    public boolean isFloor;
    public boolean isOrganic;
    public boolean smartMinePriority;
    public int lightLevel;
    public float lightHue;
    public float lightSat;
    public Color mapColor = new Color(200, 50, 50);
    public boolean drawDamage = true;
    private GameMessage displayName;
    public boolean canBeMined;
    public float toolTier = 0.0f;
    public int stackSize = 500;
    public boolean overridesCannotPlaceOnShore = false;
    public boolean overridesCannotPlaceOnLiquid = false;
    public int tileHealth;
    public boolean shouldReturnOnDeletedLevels;
    public HashSet<String> roomProperties = new HashSet();

    public static void setupTileTextures() {
        tileTextures = new SharedGameTexture("tilesShared");
        tileBlankTexture = tileTextures.addBlankQuad(32, 32);
        tileShoreTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/shoremask"));
        tileErrorTexture = tileTextures.addTexture(GameResources.error);
    }

    public static void generateTileTextures() {
        generatedTileTexture = tileTextures.generate();
        tileTextures.close();
    }

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public GameTile(boolean isFloor) {
        if (TileRegistry.instance.isClosed()) {
            throw new RegistryClosedException("Cannot construct GameTile objects when tile registry is closed, since they are a static registered objects. Use TileRegistry.getTile(...) to get tiles.");
        }
        this.terrainSplatting = this instanceof TerrainSplatterTile;
        this.isLiquid = this instanceof LiquidTile;
        this.isFloor = isFloor;
        this.smartMinePriority = isFloor;
        int n = this.tileHealth = isFloor ? 50 : 100;
        if (this.isLiquid) {
            this.setItemCategory("tiles", "liquids");
        } else if (isFloor) {
            this.setItemCategory("tiles", "floors");
        } else if (this.terrainSplatting) {
            this.setItemCategory("tiles", "terrain");
        }
    }

    public void onTileRegistryClosed() {
    }

    public LootTable getLootTable(Level level, int tileX, int tileY) {
        if (ItemRegistry.itemExists(this.getStringID())) {
            return new LootTable(new LootItem(this.getStringID()).preventLootMultiplier());
        }
        return new LootTable();
    }

    public void addDrawables(LevelTileTerrainDrawOptions underLiquidList, LevelTileLiquidDrawOptions liquidList, LevelTileTerrainDrawOptions overLiquidList, OrderableDrawables objectTileList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        underLiquidList.add(tileErrorTexture).pos(drawX, drawY);
    }

    public void addBridgeDrawables(LevelTileTerrainDrawOptions sharedList, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
    }

    public void addLightDrawables(LevelTileLightDrawOptions lightList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        lightList.addLight(tickManager, level, tileX, tileY, camera);
    }

    public void drawPreview(Level level, int tileX, int tileY, float alpha, PlayerMob player, GameCamera camera) {
    }

    public GameMessage getNewLocalization() {
        return new LocalMessage("tile", this.getStringID());
    }

    public void updateLocalDisplayName() {
        this.displayName = this.getNewLocalization();
    }

    public GameMessage getLocalization() {
        return this.displayName;
    }

    public String getDisplayName() {
        return this.displayName.translate();
    }

    public GameTile setItemCategory(String ... categoryTree) {
        this.itemCategoryTree = categoryTree;
        return this;
    }

    public GameTile setCraftingCategory(String ... categoryTree) {
        this.craftingCategoryTree = categoryTree;
        return this;
    }

    public GameTile addGlobalIngredient(String ... globalIngredientStringIDs) {
        this.itemGlobalIngredients.addAll(Arrays.asList(globalIngredientStringIDs));
        return this;
    }

    public Color getMapColor(Level level, int tileX, int tileY) {
        return this.mapColor;
    }

    public Color getDebrisColor(Level level, int tileX, int tileY) {
        return this.getMapColor(level, tileX, tileY);
    }

    public final void loadTileTextures() {
        this.loadTextures();
    }

    public GameTexture generateItemTexture() {
        GameTexture itemMask = GameTexture.fromFile("tiles/itemmask", true);
        GameTexture generatedTexture = new GameTexture(GameResources.error);
        generatedTexture.merge(itemMask, 0, 0, MergeFunction.MULTIPLY);
        generatedTexture.makeFinal();
        return generatedTexture;
    }

    protected void loadTextures() {
    }

    public static long getTileSeed(int tileX, int tileY, int primeIndex) {
        return ((long)tileX * 1289969L + (long)tileY * 888161L) * (long)GameRandom.prime(Math.abs(primeIndex));
    }

    public static long getTileSeed(int tileX, int tileY) {
        return GameTile.getTileSeed(tileX, tileY, 0);
    }

    public TileItem getTileItem() {
        return (TileItem)ItemRegistry.getItem(this.getStringID());
    }

    public TileItem generateNewTileItem() {
        return new TileItem(this);
    }

    public boolean canBePlacedOn(Level level, int tileX, int tileY, GameTile placing) {
        return !this.isLiquid && !this.isFloor;
    }

    public String canPlace(Level level, int x, int y, boolean byPlayer) {
        GameTile lastTile = level.getTile(x, y);
        if (lastTile == this) {
            return "sametile";
        }
        if (!level.getWorldSettings().creativeMode && !lastTile.canBePlacedOn(level, x, y, this)) {
            return "wrongtile";
        }
        return null;
    }

    public boolean isValid(Level level, int x, int y) {
        return true;
    }

    public void checkAround(Level level, int x, int y) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i == 0 && j == 0) continue;
                level.getTile(x + i, y + j).checkIsValid(level, x + i, y + j);
            }
        }
    }

    public void checkIsValid(Level level, int tileX, int tileY) {
        if (level.isClient()) {
            return;
        }
        if (!this.isValid(level, tileX, tileY)) {
            level.entityManager.doTileDamageOverride(tileX, tileY, this.tileHealth);
        }
    }

    public void attemptPlace(Level level, int x, int y, PlayerMob player, String error) {
    }

    public void placeTile(Level level, int x, int y, boolean byPlayer) {
        level.setTile(x, y, this.getID());
        DamagedObjectEntity damagedObjectEntity = level.entityManager.getDamagedObjectEntity(x, y);
        if (damagedObjectEntity != null) {
            damagedObjectEntity.tileDamage = 0;
        }
    }

    public void playPlaceSound(int tileX, int tileY) {
        if (this.isLiquid) {
            SoundManager.playSound(GameResources.watersplash, (SoundEffect)SoundEffect.effect(tileX * 32 + 16, tileY * 32 + 16));
        } else {
            SoundManager.playSound(GameResources.tap, (SoundEffect)SoundEffect.effect(tileX * 32 + 16, tileY * 32 + 16));
        }
    }

    public boolean canReplace(Level level, int tileX, int tileY) {
        if (level.getTileID(tileX, tileY) == this.getID()) {
            return false;
        }
        return !this.isLiquid && !level.getTile((int)tileX, (int)tileY).isLiquid;
    }

    public ArrayList<InventoryItem> getDroppedItems(Level level, int x, int y) {
        return this.getLootTable(level, x, y).getNewList(GameRandom.globalRandom, level.buffManager.getModifier(LevelModifiers.LOOT).floatValue(), new Object[0]);
    }

    public void tick(Mob mob, Level level, int x, int y) {
    }

    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
    }

    public void tick(Level level, int x, int y) {
    }

    public void tickEffect(Level level, int x, int y) {
    }

    public List<LevelJob> getLevelJobs(Level level, int tileX, int tileY) {
        return Collections.emptyList();
    }

    public void tickValid(Level level, int x, int y, boolean underGeneration) {
    }

    public int getDestroyedTile() {
        return TileRegistry.dirtID;
    }

    public int getLightLevel() {
        return this.lightLevel;
    }

    public GameLight getLight(Level level) {
        return level.lightManager.newLight(this.lightHue, this.lightSat, (float)this.getLightLevel());
    }

    public float getItemSinkingRate(float currentSinking) {
        return 0.0f;
    }

    public float getItemMaxSinking() {
        return 0.0f;
    }

    public boolean inLiquid(Level level, int tileX, int tileY, int levelX, int levelY) {
        return this.isLiquid;
    }

    public int getMobSinkingAmount(Mob mob) {
        if (mob.inLiquid() && mob.isRiding()) {
            return 10;
        }
        return 0;
    }

    public float getLiquidMobHeightPercent(Level level, int tileX, int tileY, Mob perspective, int height) {
        return Math.min((float)height * level.getLiquidMobSinkRate() / -10.0f, 1.0f);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", "tiletip"));
        return tooltips;
    }

    public double spreadToDirtChance() {
        return 0.0;
    }

    public double getPathCost(Level level, int tileX, int tileY, Mob mob) {
        return 0.0;
    }

    public ModifierValue<Float> getSpeedModifier(Mob mob) {
        return new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.0f));
    }

    public ModifierValue<Float> getSlowModifier(Mob mob) {
        return new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f));
    }

    public ModifierValue<Float> getFrictionModifier(Mob mob) {
        return new ModifierValue<Float>(BuffModifiers.FRICTION, Float.valueOf(0.0f));
    }

    public void onDestroyed(Level level, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (itemsDropped != null) {
            TileLootTableDropsEvent dropsEvent = new TileLootTableDropsEvent(this, level, x, y, new Point(x * 32 + 16, y * 32 + 16), this.getDroppedItems(level, x, y));
            level.onTileLootTableDropped(dropsEvent);
            if (dropsEvent.dropPos != null && dropsEvent.drops != null) {
                for (InventoryItem item : dropsEvent.drops) {
                    ItemPickupEntity itemDropped = item.getPickupEntity(level, dropsEvent.dropPos.x, dropsEvent.dropPos.y);
                    level.entityManager.pickups.add(itemDropped);
                    itemsDropped.add(itemDropped);
                }
            }
        }
        if (client != null) {
            client.newStats.tiles_mined.increment(1);
        }
        if (!level.isServer()) {
            this.spawnDestroyedParticles(level, x, y);
        }
        level.setTile(x, y, this.getDestroyedTile());
    }

    public boolean shouldReturnOnDeletedLevels(Level level, int tileX, int tileY) {
        return this.shouldReturnOnDeletedLevels;
    }

    public boolean onDamaged(Level level, int x, int y, int damage, Attacker attacker, ServerClient client, boolean showEffect, int mouseX, int mouseY) {
        if (showEffect && !level.isServer()) {
            this.spawnDebrisParticles(level, x, y, damage > 0, mouseX, mouseY);
            this.playDamageSound(level, x, y, damage > 0);
        }
        return true;
    }

    public void spawnDestroyedParticles(Level level, int x, int y) {
        Color color = this.getDebrisColor(level, x, y);
        if (color == null) {
            return;
        }
        for (int i = 0; i < 5; ++i) {
            float posX = (float)(x * 32) + GameRandom.globalRandom.getFloatOffset(16.0f, 5.0f);
            float posY = (float)(y * 32) + GameRandom.globalRandom.getFloatOffset(24.0f, 5.0f);
            float startHeight = GameRandom.globalRandom.getFloatBetween(5.0f, 10.0f);
            float startHeightSpeed = GameRandom.globalRandom.getFloatBetween(0.0f, 80.0f);
            final float endHeight = GameRandom.globalRandom.getFloatBetween(-5.0f, 0.0f);
            float gravity = GameRandom.globalRandom.getFloatBetween(10.0f, 20.0f);
            boolean mirrorX = GameRandom.globalRandom.nextBoolean();
            boolean mirrorY = GameRandom.globalRandom.nextBoolean();
            float rotation = GameRandom.globalRandom.getFloatBetween(0.5f, 2.0f);
            float moveX = GameRandom.globalRandom.floatGaussian() * 30.0f;
            float moveY = GameRandom.globalRandom.floatGaussian() * 20.0f;
            int timeToLive = GameRandom.globalRandom.getIntBetween(1500, 2500);
            int timeToFadeOut = GameRandom.globalRandom.getIntBetween(500, 1500);
            int totalTime = timeToLive + timeToFadeOut;
            final ParticleOption.HeightMover heightMover = new ParticleOption.HeightMover(startHeight, startHeightSpeed, gravity, 2.0f, endHeight, 0.0f);
            AtomicReference<Float> airTime = new AtomicReference<Float>(Float.valueOf(0.0f));
            level.entityManager.addParticle(posX, posY, Particle.GType.COSMETIC).sprite(GameResources.debrisParticles.sprite(GameRandom.globalRandom.nextInt(6), 0, 20)).color(this.getDebrisColor(level, x, y)).fadesAlphaTime(0, timeToFadeOut).sizeFadesInAndOut(10, 15, 0, 0).height(heightMover).onMoveTick((delta, lifeTime, timeAlive, lifePercent) -> {
                if (heightMover.currentHeight > endHeight) {
                    airTime.set(Float.valueOf(((Float)airTime.get()).floatValue() + delta));
                }
            }).modify((options, lifeTime, timeAlive, lifePercent) -> {
                float angle = ((Float)airTime.get()).floatValue() * rotation;
                options.rotate(angle);
            }).moves(new ParticleOption.FrictionMover(moveX, moveY, 2.0f){

                @Override
                public void tick(Point2D.Float pos, float delta, int lifeTime, int timeAlive, float lifePercent) {
                    if (heightMover.currentHeight > endHeight) {
                        super.tick(pos, delta, lifeTime, timeAlive, lifePercent);
                    }
                }
            }).modify((options, lifeTime, timeAlive, lifePercent) -> options.mirror(mirrorX, mirrorY)).lifeTime(totalTime);
        }
    }

    public void spawnDebrisParticles(Level level, int x, int y, boolean damageDone, int mouseX, int mouseY) {
        if (!damageDone) {
            return;
        }
        Color color = this.getDebrisColor(level, x, y);
        if (color == null) {
            return;
        }
        for (int i = 0; i < 3; ++i) {
            float startHeight = GameRandom.globalRandom.getFloatBetween(5.0f, 10.0f);
            float startHeightSpeed = GameRandom.globalRandom.getFloatBetween(0.0f, 80.0f);
            final float endHeight = GameRandom.globalRandom.getFloatBetween(-5.0f, 0.0f);
            float gravity = GameRandom.globalRandom.getFloatBetween(10.0f, 20.0f);
            boolean mirrorX = GameRandom.globalRandom.nextBoolean();
            boolean mirrorY = GameRandom.globalRandom.nextBoolean();
            float rotation = GameRandom.globalRandom.getFloatBetween(0.5f, 2.0f);
            float moveX = GameRandom.globalRandom.floatGaussian() * 30.0f;
            float moveY = GameRandom.globalRandom.floatGaussian() * 20.0f;
            int timeToLive = GameRandom.globalRandom.getIntBetween(500, 1500);
            int timeToFadeOut = GameRandom.globalRandom.getIntBetween(500, 1500);
            int totalTime = timeToLive + timeToFadeOut;
            final ParticleOption.HeightMover heightMover = new ParticleOption.HeightMover(startHeight, startHeightSpeed, gravity, 2.0f, endHeight, 0.0f);
            AtomicReference<Float> airTime = new AtomicReference<Float>(Float.valueOf(0.0f));
            level.entityManager.addParticle(mouseX, (float)mouseY + startHeight, Particle.GType.COSMETIC).sprite(GameResources.debrisParticles.sprite(GameRandom.globalRandom.nextInt(6), 0, 20)).color(this.getDebrisColor(level, x, y)).fadesAlphaTime(0, timeToFadeOut).sizeFadesInAndOut(10, 15, 0, 0).height(heightMover).onMoveTick((delta, lifeTime, timeAlive, lifePercent) -> {
                if (heightMover.currentHeight > endHeight) {
                    airTime.set(Float.valueOf(((Float)airTime.get()).floatValue() + delta));
                }
            }).modify((options, lifeTime, timeAlive, lifePercent) -> {
                float angle = ((Float)airTime.get()).floatValue() * rotation;
                options.rotate(angle);
            }).moves(new ParticleOption.FrictionMover(moveX, moveY, 2.0f){

                @Override
                public void tick(Point2D.Float pos, float delta, int lifeTime, int timeAlive, float lifePercent) {
                    if (heightMover.currentHeight > endHeight) {
                        super.tick(pos, delta, lifeTime, timeAlive, lifePercent);
                    }
                }
            }).modify((options, lifeTime, timeAlive, lifePercent) -> options.mirror(mirrorX, mirrorY)).lifeTime(totalTime);
        }
    }

    public void doExplosionDamage(Level level, int x, int y, int damage, float toolTier, Attacker attacker, ServerClient client) {
        level.entityManager.doTileDamage(x, y, damage, toolTier, attacker, client);
    }

    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.tap, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16).pitch(damageDone ? 1.0f : 2.0f));
    }

    public GameMessage preventsLadderPlacement(Level level, int tileX, int tileY) {
        if (!level.isCave && (this.isLiquid || level.isShore(tileX, tileY))) {
            return new LocalMessage("misc", "blockingliquid");
        }
        return null;
    }

    public GameTooltips getMapTooltips(Level level, int x, int y) {
        return null;
    }

    public int getLiquidBobbing(Level level, int tileX, int tileY) {
        return 0;
    }

    public MobSpawnTable getMobSpawnTable(TilePosition pos, MobSpawnTable defaultTable) {
        return defaultTable;
    }

    public int getMobSpawnPositionTickets(Level level, int tileX, int tileY) {
        return 100;
    }
}

