/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.journal.listeners.ObjectDestroyedJournalChallengeListener;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.ObjectDamageResult;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.regionSystem.RegionType;
import necesse.level.maps.regionSystem.SimulatePriorityList;

public class GameObject {
    public final IDData idData = new IDData();
    private final GameRandom tileRandom = new GameRandom();
    protected Rectangle collision;
    protected Rectangle hoverHitbox = new Rectangle(32, 32);
    protected int hoverHitboxSortY = 16;
    public Color mapColor;
    public Color debrisColor;
    public boolean displayMapTooltip = false;
    public int lightLevel;
    public float lightHue;
    public float lightSat;
    public final boolean isDoor;
    public boolean isWall;
    public boolean isRock;
    public boolean isFence;
    public boolean isSwitch;
    public boolean isSwitched;
    public boolean isPressurePlate;
    public boolean isGrass;
    public boolean isSeed;
    public boolean isFlowerpot;
    public boolean isTree;
    public boolean isOre;
    public boolean isSolid;
    public boolean canPlaceOnLiquid;
    public boolean canPlaceOnShore;
    public boolean isLightTransparent;
    public boolean showsWire;
    public boolean attackThrough;
    public boolean overridesInLiquid;
    public boolean isIncursionExtractionObject;
    private GameMessage displayName = new StaticMessage("Unknown");
    public int stackSize = 100;
    public String[] itemCategoryTree = new String[]{"objects"};
    public String[] craftingCategoryTree = new String[]{"objects"};
    public HashSet<String> itemGlobalIngredients = new HashSet();
    public Item.Rarity rarity = Item.Rarity.NORMAL;
    public ToolType toolType = ToolType.PICKAXE;
    public int objectHealth = 100;
    public float toolTier = 0.0f;
    public boolean drawDamage = true;
    public boolean canPlaceOnProtectedLevels = false;
    public boolean shouldReturnOnDeletedLevels = false;
    public HashSet<String> roomProperties = new HashSet();
    public HashSet<String> replaceCategories = new HashSet();
    public HashSet<String> canReplaceCategories = new HashSet();
    public boolean replaceRotations = true;
    protected RegionType regionType;
    protected LinkedHashSet<Integer> validObjectLayers = new LinkedHashSet();

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public GameObject() {
        this(new Rectangle());
    }

    public GameObject(Rectangle collision) {
        if (ObjectRegistry.instance.isClosed()) {
            throw new RegistryClosedException("Cannot construct GameObject objects when object registry is closed, since they are a static registered objects. Use ObjectRegistry.getObject(...) to get objects.");
        }
        this.collision = collision;
        this.isDoor = this instanceof DoorObject;
        if (collision.width > 0 && collision.height > 0) {
            this.isSolid = true;
        }
        this.mapColor = new Color(127, 127, 127);
        this.regionType = this.isSolid ? RegionType.SOLID : RegionType.OPEN;
        this.validObjectLayers.add(0);
    }

    public void onObjectRegistryClosed() {
    }

    public LinkedHashSet<Integer> getValidObjectLayers() {
        return this.validObjectLayers;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        this.addLayerDrawables(list, tileList, level, 0, tileX, tileY, tickManager, camera, perspective);
    }

    public void addLayerDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int layerID, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    public void drawMultiTilePreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        this.getMultiTile(rotation).streamObjects(tileX, tileY).forEach(e -> ((GameObject)e.value).drawPreview(level, e.tileX, e.tileY, rotation, alpha, player, camera));
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
    }

    public void drawFailedPreview(Level level, int tileX, int tileY, int rotation, float alpha, String error, PlayerMob player, GameCamera camera) {
    }

    public GameMessage getNewLocalization() {
        MultiTile multiTile = this.getMultiTile(0);
        if (multiTile.isMaster) {
            return new LocalMessage("object", this.getStringID());
        }
        return multiTile.getMasterObject().getNewLocalization();
    }

    public void updateLocalDisplayName() {
        this.displayName = this.getNewLocalization();
    }

    public final GameMessage getLocalization() {
        return this.displayName;
    }

    public final String getDisplayName() {
        return this.displayName.translate();
    }

    public GameObject setItemCategory(String ... categoryTree) {
        this.itemCategoryTree = categoryTree;
        return this;
    }

    public GameObject setCraftingCategory(String ... categoryTree) {
        this.craftingCategoryTree = categoryTree;
        return this;
    }

    public GameObject addGlobalIngredient(String ... globalIngredientStringIDs) {
        this.itemGlobalIngredients.addAll(Arrays.asList(globalIngredientStringIDs));
        return this;
    }

    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        return new Rectangle(x * 32 + this.collision.x, y * 32 + this.collision.y, this.collision.width, this.collision.height);
    }

    public List<Rectangle> getCollisions(Level level, int x, int y, int rotation) {
        LinkedList<Rectangle> list = new LinkedList<Rectangle>();
        list.add(this.getCollision(level, x, y, rotation));
        return list;
    }

    protected ObjectHoverHitbox getHoverHitbox(Level level, int layerID, int tileX, int tileY) {
        return new ObjectHoverHitbox(layerID, tileX, tileY, this.hoverHitbox.x, this.hoverHitbox.y, this.hoverHitbox.width, this.hoverHitbox.height, this.hoverHitboxSortY);
    }

    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int layerID, int tileX, int tileY) {
        LinkedList<ObjectHoverHitbox> list = new LinkedList<ObjectHoverHitbox>();
        list.add(this.getHoverHitbox(level, layerID, tileX, tileY));
        return list;
    }

    public int getHitboxLayerPriority(Level level, int layerID, int tileX, int tileY) {
        return layerID;
    }

    public List<Rectangle> getProjectileCollisions(Level level, int x, int y, int rotation) {
        return this.getCollisions(level, x, y, rotation);
    }

    public List<Rectangle> getAttackThroughCollisions(Level level, int x, int y) {
        LinkedList<Rectangle> out = new LinkedList<Rectangle>();
        out.add(new Rectangle(x * 32, y * 32, 32, 32));
        return out;
    }

    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        return this.lightLevel;
    }

    public GameLight getLight(Level level, int layerID, int tileX, int tileY) {
        return level.lightManager.newLight(this.lightHue, this.lightSat, (float)this.getLightLevel(level, layerID, tileX, tileY));
    }

    public boolean allowsAmbientLightPassThrough(Level level, int tileX, int tileY) {
        return false;
    }

    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (ItemRegistry.itemExists(this.getStringID()) && ItemRegistry.isObtainable(ItemRegistry.getItemID(this.getStringID()))) {
            return new LootTable(new LootItem(this.getStringID()).preventLootMultiplier());
        }
        return new LootTable();
    }

    public boolean canPlaceOn(Level level, int layerID, int x, int y, GameObject other) {
        return true;
    }

    public boolean canBePlacedOn(Level level, int layerID, int x, int y, GameObject newObject, boolean ignoreOtherLayers) {
        if (this.isGrass && !newObject.isGrass) {
            return true;
        }
        if (layerID != 0 || ignoreOtherLayers) {
            return false;
        }
        LinkedHashSet<Integer> invalidLayers = newObject.getValidObjectLayers();
        int moveToLayer = -1;
        Iterator iterator = this.getValidObjectLayers().iterator();
        while (iterator.hasNext()) {
            int layer = (Integer)iterator.next();
            if (layer == layerID || invalidLayers != null && invalidLayers.contains(layer)) continue;
            moveToLayer = layer;
            break;
        }
        return moveToLayer != -1;
    }

    public void onPlacedOn(Level level, int layerID, int x, int y, GameObject newObject) {
        if (this.isGrass) {
            return;
        }
        LinkedHashSet<Integer> invalidLayers = newObject.getValidObjectLayers();
        int moveToLayer = -1;
        Iterator iterator = this.getValidObjectLayers().iterator();
        while (iterator.hasNext()) {
            int layer = (Integer)iterator.next();
            if (layer == layerID || invalidLayers != null && invalidLayers.contains(layer)) continue;
            moveToLayer = layer;
            break;
        }
        if (moveToLayer != -1) {
            this.moveMasterToLayer(level, layerID, moveToLayer, x, y);
        }
    }

    public boolean isTilePlaceOccupied(Level level, int layerID, int x, int y, boolean ignoreOtherLayers) {
        GameObject object = level.getObject(layerID, x, y);
        if (!object.canBePlacedOn(level, layerID, x, y, this, ignoreOtherLayers) || !this.canPlaceOn(level, layerID, x, y, object)) {
            return true;
        }
        if (!ignoreOtherLayers) {
            if (layerID != 0) {
                GameObject baseObject = level.getObject(0, x, y);
                if (baseObject.getID() == 0) {
                    return false;
                }
                return baseObject.getValidObjectLayers().contains(layerID);
            }
            Iterator iterator = this.getValidObjectLayers().iterator();
            while (iterator.hasNext()) {
                GameObject layerObject;
                int layer = (Integer)iterator.next();
                if (layer == layerID || (layerObject = level.getObject(layer, x, y)).canBePlacedOn(level, layer, x, y, this, ignoreOtherLayers) && this.canPlaceOn(level, layerID, x, y, layerObject)) continue;
                return true;
            }
        }
        return false;
    }

    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        boolean anyLiquidNotPlaceable;
        if (level.getObjectID(layerID, x, y) == this.getID()) {
            return "sameobject";
        }
        if (this.isTilePlaceOccupied(level, layerID, x, y, ignoreOtherLayers)) {
            return "occupied";
        }
        if (!this.canPlaceOnLiquid && level.isLiquidTile(x, y) && !level.getTile((int)x, (int)y).overridesCannotPlaceOnLiquid) {
            return "liquid";
        }
        if (!this.canPlaceOnShore && level.isShore(x, y) && !level.getTile((int)x, (int)y).overridesCannotPlaceOnShore && (anyLiquidNotPlaceable = Arrays.stream(Level.adjacentGetters).anyMatch(p -> {
            GameTile tile = level.getTile(x + p.x, y + p.y);
            return tile.isLiquid && !tile.overridesCannotPlaceOnLiquid;
        }))) {
            return "shore";
        }
        MultiTile multiTile = this.getMultiTile(rotation);
        if (multiTile.isMaster) {
            return multiTile.streamOtherObjects(x, y).map(e -> ((GameObject)e.value).canPlace(level, layerID, e.tileX, e.tileY, rotation, byPlayer, ignoreOtherLayers)).reduce(null, (prev, e) -> prev != null ? prev : e);
        }
        return null;
    }

    public final String canPlace(Level level, int x, int y, int rotation, boolean byPlayer) {
        return this.canPlace(level, 0, x, y, rotation, byPlayer, false);
    }

    public void attemptPlace(Level level, int x, int y, PlayerMob player, String error) {
    }

    public boolean checkPlaceCollision(Level level, int x, int y, int rotation, boolean checkClients) {
        for (Rectangle r : this.getCollisions(level, x, y, rotation)) {
            if (!level.entityCollides(r, checkClients)) continue;
            return true;
        }
        MultiTile multiTile = this.getMultiTile(rotation);
        if (multiTile.isMaster) {
            return multiTile.streamOtherObjects(x, y).anyMatch(e -> ((GameObject)e.value).checkPlaceCollision(level, e.tileX, e.tileY, rotation, checkClients));
        }
        return false;
    }

    public boolean isValid(Level level, int layerID, int x, int y) {
        boolean anyLiquidNotPlaceable;
        if (this.toolType == ToolType.UNBREAKABLE) {
            return true;
        }
        if (!this.canPlaceOnLiquid && level.isLiquidTile(x, y) && !level.getTile((int)x, (int)y).overridesCannotPlaceOnLiquid) {
            return false;
        }
        if (!this.canPlaceOnShore && level.isShore(x, y) && !level.getTile((int)x, (int)y).overridesCannotPlaceOnShore && (anyLiquidNotPlaceable = Arrays.stream(Level.adjacentGetters).anyMatch(p -> {
            if (!level.regionManager.isTileLoaded(x + p.x, y + p.y)) {
                return false;
            }
            GameTile tile = level.getTile(x + p.x, y + p.y);
            return tile.isLiquid && !tile.overridesCannotPlaceOnLiquid;
        }))) {
            return false;
        }
        return this.getMultiTile(level.getObjectRotation(layerID, x, y)).streamOtherIDs(x, y).allMatch(e -> {
            if (!level.regionManager.isTileLoaded(e.tileX, e.tileY)) {
                return true;
            }
            return level.getObjectID(layerID, e.tileX, e.tileY) == ((Integer)e.value).intValue();
        });
    }

    public void checkAround(Level level, int x, int y) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i == 0 && j == 0) continue;
                for (int layerID : ObjectLayerRegistry.getLayerIDs()) {
                    GameObject object = level.getObject(layerID, x + i, y + j);
                    if (object.getID() == 0) continue;
                    object.checkIsValid(level, layerID, x + i, y + j);
                }
            }
        }
    }

    public void checkIsValid(Level level, int layerID, int tileX, int tileY) {
        if (level.isClient()) {
            return;
        }
        if (!this.isValid(level, layerID, tileX, tileY)) {
            level.entityManager.doObjectDamageOverride(layerID, tileX, tileY, this.objectHealth);
        }
    }

    public boolean shouldSnapSmartMining(Level level, int x, int y) {
        return this.isSolid(level, x, y);
    }

    public boolean shouldSnapControllerMining(Level level, int x, int y) {
        return true;
    }

    public boolean isSolid(Level level, int x, int y) {
        return this.isSolid;
    }

    public boolean isLightTransparent(Level level, int x, int y) {
        return this.isLightTransparent;
    }

    public int getLightLevelMod(Level level, int x, int y) {
        if (this.isSolid(level, x, y) && !this.isLightTransparent(level, x, y)) {
            return 40;
        }
        return 10;
    }

    public boolean shouldGenerateDamageOverlayTextures() {
        if (!this.drawDamage) {
            return false;
        }
        if (this.objectHealth <= 10) {
            return false;
        }
        return this.toolType != ToolType.NONE && this.toolType != ToolType.UNBREAKABLE;
    }

    public GameObject setDebrisColor(Color color) {
        this.debrisColor = color;
        return this;
    }

    public Color getDebrisColor(Level level, int tileX, int tileY) {
        if (this.debrisColor != null) {
            return this.debrisColor;
        }
        Color mapColor = this.getMapColor(level, tileX, tileY);
        if (mapColor != null) {
            return mapColor;
        }
        return new Color(127, 127, 127);
    }

    public Color getMapColor(Level level, int tileX, int tileY) {
        return this.mapColor;
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return false;
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return null;
    }

    public void onMouseHover(Level level, int x, int y, GameCamera camera, PlayerMob perspective, boolean debug) {
        ObjectEntity ent = level.entityManager.getObjectEntity(x, y);
        if (ent != null) {
            ent.onMouseHover(perspective, debug);
        }
    }

    public int getInteractRange(Level level, int tileX, int tileY) {
        return 100;
    }

    public boolean isInInteractRange(Level level, int tileX, int tileY, PlayerMob perspective) {
        return this.isInInteractRange(level, tileX, tileY, perspective, this.getInteractRange(level, tileX, tileY));
    }

    public boolean isInInteractRange(Level level, int tileX, int tileY, PlayerMob perspective, int range) {
        Point centerPos = this.getMultiTile(level.getObjectRotation(tileX, tileY)).getCenterLevelPos(tileX, tileY);
        return perspective.getDistance(centerPos.x, centerPos.y) < (float)range;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (!level.isClient()) {
            return;
        }
        if (this instanceof OEUsers) {
            return;
        }
        this.playInteractSound(level, player, x, y, false, false);
    }

    public void playInteractSound(Level level, Mob interactingMob, int tileX, int tileY, boolean isFirstOrLast, boolean close) {
        SoundSettings sound;
        if (!this.shouldPlayInteractSound(level, tileX, tileY)) {
            return;
        }
        if (!this.interactSoundIsGlobal()) {
            if (!interactingMob.isPlayer) {
                return;
            }
            if (level.getClient().getPlayer() != interactingMob) {
                return;
            }
        }
        if (this.interactSoundIsFirstAndLastOnly() && !isFirstOrLast) {
            return;
        }
        SoundSettings soundSettings = sound = close ? this.getInteractSoundClose() : this.getInteractSoundOpen();
        if (sound == null) {
            return;
        }
        sound.setPitchVarianceIfNotSet(0.03f);
        Point soundPos = new Point(tileX * 32 + 16, tileY * 32 + 16);
        if (this.isMultiTile()) {
            soundPos = this.getMultiTile(level.getObjectRotation(tileX, tileY)).getCenterLevelPos(tileX, tileY);
        }
        SoundManager.playSound(sound, soundPos.x, soundPos.y);
    }

    protected boolean shouldPlayInteractSound(Level level, int tileX, int tileY) {
        return false;
    }

    protected boolean interactSoundIsGlobal() {
        return false;
    }

    protected boolean interactSoundIsFirstAndLastOnly() {
        return false;
    }

    protected SoundSettings getInteractSoundOpen() {
        return SoundSettingsRegistry.defaultOpen;
    }

    protected SoundSettings getInteractSoundClose() {
        return null;
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return null;
    }

    public ObjectEntity getCurrentObjectEntity(Level level, int tileX, int tileY) {
        return level.entityManager.getObjectEntity(tileX, tileY);
    }

    public <T extends ObjectEntity> T getCurrentObjectEntity(Level level, int tileX, int tileY, Class<T> expectedClass) {
        return level.entityManager.getObjectEntity(tileX, tileY, expectedClass);
    }

    public final ObjectItem getObjectItem() {
        GameObject masterObject;
        MultiTile multiTile = this.getMultiTile(0);
        if (!multiTile.isMaster && (masterObject = multiTile.getMasterObject()) != null) {
            return masterObject.getObjectItem();
        }
        return (ObjectItem)ItemRegistry.getItem(this.getStringID());
    }

    public Item generateNewObjectItem() {
        return new ObjectItem(this);
    }

    public boolean shouldShowInItemList() {
        return this.isMultiTileMaster();
    }

    public final void moveMasterToLayer(Level level, int fromLayerID, int toLayerID, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(fromLayerID, tileX, tileY);
        MultiTile multiTile = this.getMultiTile(rotation);
        if (!multiTile.isMaster) {
            LevelObject masterObject = multiTile.getMasterLevelObject(level, fromLayerID, tileX, tileY).orElse(null);
            if (masterObject != null) {
                masterObject.object.moveToLayer(level, fromLayerID, toLayerID, masterObject.tileX, masterObject.tileY);
            } else {
                this.moveToLayer(level, fromLayerID, toLayerID, tileX, tileY);
            }
        } else {
            this.moveToLayer(level, fromLayerID, toLayerID, tileX, tileY);
        }
    }

    public void moveToLayer(Level level, int fromLayerID, int toLayerID, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(fromLayerID, tileX, tileY);
        boolean isPlayerPlaced = level.objectLayer.isPlayerPlaced(fromLayerID, tileX, tileY);
        level.objectLayer.setObject(toLayerID, tileX, tileY, this.getID());
        level.objectLayer.setObjectRotation(toLayerID, tileX, tileY, rotation);
        level.objectLayer.setIsPlayerPlaced(toLayerID, tileX, tileY, isPlayerPlaced);
        level.objectLayer.setObject(fromLayerID, tileX, tileY, 0);
        level.objectLayer.setObjectRotation(fromLayerID, tileX, tileY, 0);
        level.objectLayer.setIsPlayerPlaced(fromLayerID, tileX, tileY, false);
        MultiTile multiTile = this.getMultiTile(rotation);
        if (multiTile.isMaster) {
            multiTile.streamOtherObjects(tileX, tileY).filter(e -> ((GameObject)e.value).getID() == level.getObjectID(fromLayerID, e.tileX, e.tileY)).forEach(e -> ((GameObject)e.value).moveToLayer(level, fromLayerID, toLayerID, e.tileX, e.tileY));
        }
    }

    public void placeObject(Level level, int layerID, int x, int y, int rotation, boolean byPlayer) {
        ObjectEntity objectEntity;
        MultiTile multiTile = this.getMultiTile(rotation);
        if (multiTile.isMaster) {
            Rectangle tileRectangle = multiTile.getTileRectangle(x, y);
            level.regionManager.ensureTilesAreLoaded(tileRectangle.x, tileRectangle.y, tileRectangle.x + tileRectangle.width - 1, tileRectangle.y + tileRectangle.height - 1);
        }
        level.objectLayer.getObject(layerID, x, y).onPlacedOn(level, layerID, x, y, this);
        level.objectLayer.setObject(layerID, x, y, this.getID());
        level.objectLayer.setObjectRotation(layerID, x, y, (byte)rotation);
        level.objectLayer.setIsPlayerPlaced(layerID, x, y, byPlayer);
        DamagedObjectEntity damagedObjectEntity = level.entityManager.getDamagedObjectEntity(x, y);
        if (damagedObjectEntity != null) {
            damagedObjectEntity.objectDamage[layerID] = 0;
        }
        if (layerID == 0 && level.isServer() && (objectEntity = this.getNewObjectEntity(level, x, y)) != null) {
            level.entityManager.objectEntities.add(objectEntity);
        }
        if (multiTile.isMaster) {
            multiTile.streamOtherObjects(x, y).forEach(e -> ((GameObject)e.value).placeObject(level, layerID, e.tileX, e.tileY, rotation, byPlayer));
        }
    }

    public final void placeObject(Level level, int x, int y, int rotation, boolean byPlayer) {
        this.placeObject(level, 0, x, y, rotation, byPlayer);
    }

    public final boolean placeObjectOnFirstValidLayer(Level level, int x, int y, int rotation, boolean byPlayer, boolean allowReplace) {
        int layerID;
        LinkedHashSet<Integer> validLayers = this.getValidObjectLayers();
        Iterator iterator = validLayers.iterator();
        while (iterator.hasNext()) {
            layerID = (Integer)iterator.next();
            if (this.canPlace(level, layerID, x, y, rotation, byPlayer, false) != null) continue;
            this.placeObject(level, layerID, x, y, rotation, byPlayer);
            return true;
        }
        if (allowReplace) {
            iterator = validLayers.iterator();
            while (iterator.hasNext()) {
                layerID = (Integer)iterator.next();
                if (level.getObjectID(layerID, x, y) == 0 || !this.canReplace(level, layerID, x, y, rotation)) continue;
                this.placeObject(level, layerID, x, y, rotation, byPlayer);
                return true;
            }
        }
        return false;
    }

    public void playPlaceSound(int tileX, int tileY) {
        SoundManager.playSound(GameResources.tap, (SoundEffect)SoundEffect.effect(tileX * 32 + 16, tileY * 32 + 16));
    }

    public boolean canReplaceRotation(Level level, int layerID, int tileX, int tileY, int currentRotation, int newRotation) {
        if (!this.replaceRotations) {
            return false;
        }
        return currentRotation != newRotation;
    }

    public boolean canReplace(Level level, int layerID, int tileX, int tileY, int rotation) {
        GameObject currentObject = level.getObject(layerID, tileX, tileY);
        byte currentRotation = level.getObjectRotation(layerID, tileX, tileY);
        if (currentObject.getID() == this.getID() && !this.canReplaceRotation(level, layerID, tileX, tileY, currentRotation, rotation)) {
            return false;
        }
        Iterator<Object> iterator = this.getValidObjectLayers().iterator();
        while (iterator.hasNext()) {
            int otherLayerID = (Integer)iterator.next();
            if (otherLayerID == layerID || level.getObjectID(otherLayerID, tileX, tileY) != this.getID()) continue;
            return false;
        }
        if (currentObject.getMultiTile(currentRotation).streamOtherIDs(tileX, tileY).filter(e -> ((Integer)e.value).intValue() == this.getID()).filter(e -> level.getObjectID(layerID, e.tileX, e.tileY) == ((Integer)e.value).intValue()).anyMatch(e -> {
            byte tileRotation = level.getObjectRotation(layerID, e.tileX, e.tileY);
            return this.canReplaceRotation(level, layerID, e.tileX, e.tileY, tileRotation, rotation);
        })) {
            return false;
        }
        if (currentObject.getID() == 0) {
            return true;
        }
        for (String canReplaceCategory : this.canReplaceCategories) {
            if (!currentObject.replaceCategories.contains(canReplaceCategory)) continue;
            return true;
        }
        return false;
    }

    public MultiTile getMultiTile(int rotation) {
        return new MultiTile(0, 0, 1, 1, rotation, true, this.getID());
    }

    public final MultiTile getMultiTile(Level level, int layerID, int x, int y) {
        return this.getMultiTile(level.getObjectRotation(layerID, x, y));
    }

    @Deprecated
    public final MultiTile getMultiTile(Level level, int x, int y) {
        return this.getMultiTile(level, 0, x, y);
    }

    public final boolean isMultiTileMaster() {
        return this.getMultiTile((int)0).isMaster;
    }

    public boolean isMultiTile() {
        MultiTile multiTile = this.getMultiTile(0);
        return multiTile.width > 1 || multiTile.height > 1;
    }

    public void loadTextures() {
    }

    public GameTexture generateItemTexture() {
        return GameTexture.fromFile("items/" + this.getStringID());
    }

    public void tick(Mob mob, Level level, int x, int y) {
    }

    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
    }

    public void tick(Level level, int x, int y) {
    }

    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
    }

    public void tickValid(Level level, int layerID, int tileX, int tileY, boolean checkMultiTile, boolean underGeneration) {
        if (!this.isValid(level, layerID, tileX, tileY)) {
            if (underGeneration) {
                level.objectLayer.setObject(layerID, tileX, tileY, 0);
            } else {
                level.entityManager.doObjectDamageOverride(layerID, tileX, tileY, this.objectHealth);
            }
            byte rotation = level.objectLayer.getObjectRotation(layerID, tileX, tileY);
            if (checkMultiTile) {
                this.getMultiTile(rotation).streamOtherObjects(tileX, tileY).filter(e -> ((GameObject)e.value).getID() == level.objectLayer.getObjectID(layerID, e.tileX, e.tileY)).forEach(o -> ((GameObject)o.value).tickValid(level, layerID, o.tileX, o.tileY, false, underGeneration));
            }
        }
    }

    public boolean overridesInLiquid(Level level, int tileX, int tileY, int levelX, int levelY) {
        return this.overridesInLiquid;
    }

    public boolean isWireActive(Level level, int x, int y, int wireID) {
        return false;
    }

    public List<LevelJob> getLevelJobs(Level level, int tileX, int tileY) {
        return Collections.emptyList();
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", "placetip"));
        return tooltips;
    }

    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
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

    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        PlayerMob playerAttacker;
        byte rotation = level.getObjectRotation(layerID, x, y);
        PlayerMob playerMob = playerAttacker = attacker == null ? null : attacker.getFirstPlayerOwner();
        if (!(itemsDropped == null || playerAttacker != null && playerAttacker.hasGodMode())) {
            ArrayList<InventoryItem> objectDrops = this.getObjectDroppedItems(level, layerID, x, y, "onDestroyed");
            ArrayList<InventoryItem> entityDrops = this.getEntityDroppedItems(level, layerID, x, y, "onDestroyed");
            ObjectLootTableDropsEvent dropsEvent = new ObjectLootTableDropsEvent(this, level, layerID, x, y, new Point(x * 32 + 16, y * 32 + 16), objectDrops, entityDrops);
            level.onObjectLootTableDropped(dropsEvent);
            if (dropsEvent.dropPos != null) {
                ItemPickupEntity droppedItem;
                if (dropsEvent.objectDrops != null) {
                    for (InventoryItem item : dropsEvent.objectDrops) {
                        droppedItem = item.getPickupEntity(level, dropsEvent.dropPos.x, dropsEvent.dropPos.y);
                        level.entityManager.pickups.add(droppedItem);
                        itemsDropped.add(droppedItem);
                    }
                }
                if (dropsEvent.entityDrops != null) {
                    for (InventoryItem item : dropsEvent.entityDrops) {
                        droppedItem = item.getPickupEntity(level, dropsEvent.dropPos.x, dropsEvent.dropPos.y);
                        level.entityManager.pickups.add(droppedItem);
                        itemsDropped.add(droppedItem);
                    }
                }
            }
        }
        if (client != null) {
            JournalChallengeRegistry.handleListeners(client, ObjectDestroyedJournalChallengeListener.class, challenge -> challenge.onObjectDestroyed(this, level, layerID, x, y, rotation, attacker, client));
            client.newStats.objects_mined.increment(1);
        }
        if (!level.isServer()) {
            this.spawnDestroyedParticles(level, x, y);
        }
        if (layerID == 0) {
            ObjectEntity objectEntity = level.entityManager.getObjectEntity(x, y);
            level.setObject(x, y, 0);
            if (objectEntity != null) {
                objectEntity.onObjectDestroyed(this, client, itemsDropped);
                objectEntity.remove();
            }
            MultiTile multiTile = this.getMultiTile(rotation);
            Iterable multiIterable = () -> multiTile.streamObjects(x, y).iterator();
            block2: for (MultiTile.CoordinateValue e : multiIterable) {
                for (int layer = 1; layer < ObjectLayerRegistry.getTotalLayers(); ++layer) {
                    boolean isPlayerPlaced;
                    byte objectRotation;
                    String canPlace;
                    LinkedHashSet<Integer> layers;
                    GameObject object = level.getObject(layer, e.tileX, e.tileY);
                    if (object.getID() == 0 || !object.isMultiTileMaster() || !(layers = object.getValidObjectLayers()).contains(layerID) || (canPlace = object.canPlace(level, layerID, e.tileX, e.tileY, objectRotation = level.getObjectRotation(layer, e.tileX, e.tileY), isPlayerPlaced = level.objectLayer.isPlayerPlaced(layer, e.tileX, e.tileY), true)) != null) continue;
                    object.moveMasterToLayer(level, layer, layerID, e.tileX, e.tileY);
                    continue block2;
                }
            }
        } else {
            level.objectLayer.setObject(layerID, x, y, 0);
        }
    }

    public boolean shouldReturnOnDeletedLevels(Level level, int layerID, int tileX, int tileY) {
        return this.shouldReturnOnDeletedLevels;
    }

    public ArrayList<InventoryItem> getObjectDroppedItems(Level level, int layerID, int tileX, int tileY, String purpose) {
        LootTable lootTable = this.getLootTable(level, layerID, tileX, tileY);
        return lootTable.getNewList(GameRandom.globalRandom, level.buffManager.getModifier(LevelModifiers.LOOT).floatValue(), level, layerID, tileX, tileY, purpose);
    }

    public ArrayList<InventoryItem> getEntityDroppedItems(Level level, int layerID, int tileX, int tileY, String purpose) {
        ObjectEntity objectEntity;
        ArrayList<InventoryItem> drops = new ArrayList<InventoryItem>();
        if (layerID == 0 && (objectEntity = level.entityManager.getObjectEntity(tileX, tileY)) != null) {
            drops.addAll(objectEntity.getDroppedItems());
        }
        return drops;
    }

    public ArrayList<InventoryItem> getCombinedDroppedItems(Level level, int layerID, int tileX, int tileY, String purpose) {
        ArrayList<InventoryItem> drops = new ArrayList<InventoryItem>();
        drops.addAll(this.getObjectDroppedItems(level, layerID, tileX, tileY, purpose));
        drops.addAll(this.getEntityDroppedItems(level, layerID, tileX, tileY, purpose));
        return drops;
    }

    public boolean onDamaged(Level level, int layerID, int x, int y, int damage, Attacker attacker, ServerClient client, boolean showEffect, int mouseX, int mouseY) {
        if (showEffect && !level.isServer()) {
            this.spawnDebrisParticles(level, x, y, damage > 0, mouseX, mouseY);
            this.playDamageSound(level, x, y, damage > 0);
        }
        return true;
    }

    public void spawnDestroyedParticles(Level level, int tileX, int tileY) {
        Color color = this.getDebrisColor(level, tileX, tileY);
        if (color == null) {
            return;
        }
        for (int i = 0; i < 5; ++i) {
            float posX = (float)(tileX * 32) + GameRandom.globalRandom.getFloatOffset(16.0f, 5.0f);
            float posY = (float)(tileY * 32) + GameRandom.globalRandom.getFloatOffset(24.0f, 5.0f);
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
            level.entityManager.addParticle(posX, posY, Particle.GType.COSMETIC).sprite(GameResources.debrisParticles.sprite(GameRandom.globalRandom.nextInt(6), 0, 20)).color(this.getDebrisColor(level, tileX, tileY)).fadesAlphaTime(0, timeToFadeOut).sizeFadesInAndOut(10, 15, 0, 0).height(heightMover).onMoveTick((delta, lifeTime, timeAlive, lifePercent) -> {
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

    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.tap, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16).pitch(damageDone ? 1.0f : 2.0f));
    }

    public GameMessage preventsLadderPlacement(Level level, int tileX, int tileY) {
        if (this.toolType == ToolType.UNBREAKABLE) {
            return new LocalMessage("misc", "blockingexit");
        }
        return null;
    }

    public boolean isClearedOnLadderPlacement(Level level, int tileX, int tileY) {
        return this.isGrass || this.isRock;
    }

    public boolean pathCollidesIfOpen(Level level, int tileX, int tileY, CollisionFilter collisionFilter, Rectangle mobCollision) {
        return false;
    }

    public boolean pathCollidesIfBreakDown(Level level, int tileX, int tileY, CollisionFilter collisionFilter, Rectangle mobCollision) {
        return false;
    }

    public void onPathOpened(Level level, int tileX, int tileY, Attacker attacker) {
        level.entityManager.doObjectDamage(0, tileX, tileY, this.objectHealth, this.toolTier, attacker, null, true, tileX * 32 + 16, tileY * 32 + 16);
    }

    public boolean onPathBreakDown(Level level, int tileX, int tileY, int damage, Attacker attacker, int hitX, int hitY) {
        ObjectDamageResult result = level.entityManager.doObjectDamage(0, tileX, tileY, damage, this.toolTier, attacker, null, true, hitX, hitY);
        return result != null && result.destroyed;
    }

    public double getPathCost(Level level, int x, int y) {
        return 0.0;
    }

    public double getBreakDownPathCost(Level level, int x, int y) {
        return (float)this.objectHealth * 2.5f;
    }

    public int getPathBreakDamage(Level level, int x, int y) {
        return 10;
    }

    public void doExplosionDamage(Level level, int layerID, int tileX, int tileY, int damage, float toolTier, Attacker attacker, ServerClient client) {
        level.entityManager.doObjectDamage(layerID, tileX, tileY, damage, toolTier, attacker, client);
    }

    public void attackThrough(Level level, int x, int y, GameDamage damage, Attacker attacker) {
        ObjectDamageResult result;
        PlayerMob player;
        Mob attackOwner;
        if (damage.damage <= 0.0f) {
            level.getServer().network.sendToClientsWithTile(new PacketHitObject(level, x, y, this, damage), level, x, y);
            return;
        }
        ServerClient client = null;
        Mob mob = attackOwner = attacker == null ? null : attacker.getAttackOwner();
        if (attackOwner != null && attackOwner.isPlayer && (player = (PlayerMob)attackOwner).isServerClient()) {
            client = player.getServerClient();
        }
        if ((result = level.entityManager.doObjectDamage(0, x, y, this.objectHealth, this.toolTier, attacker, client)) != null && result.addedDamage > 0) {
            level.getServer().network.sendToClientsWithTile(new PacketHitObject(level, x, y, this, damage), level, x, y);
        }
    }

    public void attackThrough(Level level, int x, int y, GameDamage damage) {
    }

    public static long getTileSeed(int tileX, int tileY, int primeIndex) {
        return GameTile.getTileSeed(tileX, tileY, primeIndex);
    }

    public static long getTileSeed(int tileX, int tileY) {
        return GameTile.getTileSeed(tileX, tileY);
    }

    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        Point offset = offsetMultiTile ? this.getPlaceOffset(playerDir) : null;
        int tileX = GameMath.getTileCoordinate(levelX + (offset == null ? 0 : offset.x));
        int tileY = GameMath.getTileCoordinate(levelY + (offset == null ? 0 : offset.y));
        return new ArrayList<ObjectPlaceOption>(Collections.singleton(new ObjectPlaceOption(tileX, tileY, this, playerDir, false)));
    }

    public Point getPlaceOffset(int rotation) {
        MultiTile multiTile = this.getMultiTile(rotation);
        return new Point(-multiTile.getCenterXOffset() * 32 / 2, -multiTile.getCenterYOffset() * 32 / 2);
    }

    public GameTooltips getMapTooltips(Level level, int x, int y) {
        MultiTile multiTile = this.getMultiTile(level.getObjectRotation(x, y));
        if (multiTile.isMaster) {
            ObjectEntity objectEntity = level.entityManager.getObjectEntity(x, y);
            if (this.displayMapTooltip) {
                GameTooltips mapTooltips;
                if (objectEntity != null && (mapTooltips = objectEntity.getMapTooltips()) != null) {
                    return mapTooltips;
                }
                return new StringTooltips(this.getDisplayName());
            }
            return null;
        }
        return multiTile.getMasterLevelObject(level, 0, x, y).map(LevelObject::getMapTooltips).orElseGet(StringTooltips::new);
    }

    public RegionType getRegionType() {
        return this.regionType;
    }

    public boolean stopsTerrainSplatting() {
        return false;
    }

    public boolean drawsFullTile() {
        return false;
    }
}

