/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.GameLog;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.JournalRegistry;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.presets.PresetCopyException;
import necesse.level.maps.presets.PresetCopyFilter;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public class Preset {
    public int width;
    public int height;
    public int[] tiles;
    public int[][] objects;
    public byte[][] objectRotations;
    public boolean clearOtherWires = false;
    public byte[] wires;
    private Boolean hasAnyWireData = null;
    public HashMap<Point, PresetLogicGate> logicGates = new HashMap();
    public HashMap<Point, PresetObjectEntity> objectEntities = new HashMap();
    public ArrayList<ApplyPredicate> applyPredicates = new ArrayList();
    public ArrayList<Consumer<GameBlackboard>> blackboardSetups = new ArrayList();
    public ArrayList<CustomApply> customPreApplies = new ArrayList();
    public ArrayList<CustomApply> customApplies = new ArrayList();
    public ArrayList<TileApplyListener> tileApplyListeners = new ArrayList();
    public ArrayList<ObjectApplyListener> objectApplyListeners = new ArrayList();
    public ArrayList<WireApplyListener> wireApplyListeners = new ArrayList();

    public Preset(int width, int height) {
        this.width = width;
        this.height = height;
        this.clearPreset();
    }

    public static LoadData getLoadDataFromScript(String script) {
        try {
            LoadData loadData = new LoadData(script);
            if (loadData.hasLoadDataByName("width") && loadData.hasLoadDataByName("height")) {
                return loadData;
            }
            throw new LoadDataException("Preset script does not contain width and height data");
        }
        catch (Exception e) {
            try {
                script = new String(GameUtils.decompressData(GameUtils.fromBase64(script)));
                return new LoadData(script);
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    public Preset(String script) {
        this(Preset.getLoadDataFromScript(script));
    }

    public Preset(LoadData save) {
        this(save.getInt("width"), save.getInt("height"));
        this.applySave(0, 0, save);
    }

    public static Preset copyFromLevel(Level level, int x, int y, int width, int height) {
        Preset preset = new Preset(width, height);
        preset.copyFromLevel(level, x, y);
        return preset;
    }

    public void clearPreset() {
        this.tiles = new int[this.width * this.height];
        this.objects = new int[ObjectLayerRegistry.getTotalLayers()][this.width * this.height];
        this.objectRotations = new byte[ObjectLayerRegistry.getTotalLayers()][this.width * this.height];
        this.wires = new byte[this.width * this.height];
        this.hasAnyWireData = null;
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                this.setTile(x, y, -1);
                for (int layer = 0; layer < this.objects.length; ++layer) {
                    this.setObjectLayer(layer, x, y, -1);
                }
            }
        }
    }

    protected Preset newObject(int width, int height) {
        return new Preset(width, height);
    }

    public Preset copy() {
        return this.copy(new PresetCopyFilter());
    }

    public Preset copy(PresetCopyFilter filter) {
        Preset preset = this.newObject(this.width, this.height);
        preset.copyData(this, filter);
        return preset;
    }

    private void copyData(Preset from, PresetCopyFilter filter) {
        if (filter.acceptTiles) {
            this.tiles = (int[])from.tiles.clone();
        }
        if (filter.acceptObjects) {
            this.objects = (int[][])Arrays.stream(from.objects).map(rec$ -> (int[])((int[])rec$).clone()).toArray(x$0 -> new int[x$0][]);
            this.objectRotations = (byte[][])Arrays.stream(from.objectRotations).map(rec$ -> (byte[])((byte[])rec$).clone()).toArray(x$0 -> new byte[x$0][]);
            if (filter.acceptObjectEntities) {
                this.objectEntities.putAll(from.objectEntities);
            }
        }
        if (filter.acceptWires) {
            this.wires = (byte[])from.wires.clone();
            this.hasAnyWireData = from.hasAnyWireData;
            this.logicGates.putAll(from.logicGates);
        }
        this.applyPredicates.addAll(from.applyPredicates);
        this.customPreApplies.addAll(from.customPreApplies);
        this.customApplies.addAll(from.customApplies);
        this.tileApplyListeners.addAll(from.tileApplyListeners);
        this.objectApplyListeners.addAll(from.objectApplyListeners);
        this.wireApplyListeners.addAll(from.wireApplyListeners);
    }

    public final Preset tryMirrorX() {
        try {
            return this.mirrorX();
        }
        catch (PresetMirrorException e) {
            return this.copy();
        }
    }

    public Preset mirrorX() throws PresetMirrorException {
        Preset preset = this.newObject(this.width, this.height);
        preset.mirrorXData(this);
        return preset;
    }

    private void mirrorXData(Preset from) throws PresetMirrorException {
        boolean[] computed = new boolean[this.width * this.height];
        for (int x = 0; x < this.width; ++x) {
            int mirrorX = this.getMirroredX(x);
            for (int y = 0; y < this.height; ++y) {
                Preset.setInt(this.tiles, this.width, mirrorX, y, Preset.getInt(from.tiles, this.width, x, y));
                for (int layer = 0; layer < from.objects.length; ++layer) {
                    int objectID = Preset.getInt(from.objects[layer], this.width, x, y);
                    if (objectID != -1) {
                        GameObject obj = ObjectRegistry.getObject(objectID);
                        if (obj != null) {
                            byte objectRotation = Preset.getByte(from.objectRotations[layer], this.width, x, y);
                            MultiTile multiTile = obj.getMultiTile(objectRotation);
                            Point posOffset = multiTile.getMirrorXPosOffset();
                            if (posOffset == null) {
                                throw new PresetMirrorException(new LocalMessage("ui", "presetmirrorerror", "item", obj.getLocalization()));
                            }
                            Preset.setInt(this.objects[layer], this.width, mirrorX + posOffset.x, y + posOffset.y, objectID);
                            Preset.setByte(this.objectRotations[layer], this.width, mirrorX + posOffset.x, y + posOffset.y, (byte)multiTile.getXMirrorRotation());
                            Preset.setBoolean(computed, this.width, mirrorX, y, true);
                            continue;
                        }
                        Preset.setInt(this.objects[layer], this.width, mirrorX, y, objectID);
                        continue;
                    }
                    if (Preset.getBoolean(computed, this.width, mirrorX, y)) continue;
                    Preset.setInt(this.objects[layer], this.width, mirrorX, y, objectID);
                }
                Preset.setByte(this.wires, this.width, mirrorX, y, Preset.getByte(from.wires, this.width, x, y));
            }
        }
        this.hasAnyWireData = null;
        from.logicGates.forEach((p, lg) -> this.logicGates.put(new Point(this.getMirroredX(p.x), p.y), new PresetLogicGate(lg.logicGateID, lg.data, !lg.mirrorX, lg.mirrorY, lg.rotation)));
        from.objectEntities.forEach((p, lg) -> this.objectEntities.put(new Point(this.getMirroredX(p.x), p.y), new PresetObjectEntity(lg.data, !lg.mirrorX, lg.mirrorY, lg.rotation)));
        for (ApplyPredicate applyPredicate : from.applyPredicates) {
            this.applyPredicates.add(applyPredicate.mirrorX(this.width));
        }
        for (CustomApply customApply : from.customPreApplies) {
            this.customPreApplies.add(customApply.mirrorX(this.width));
        }
        for (CustomApply customApply : from.customApplies) {
            this.customApplies.add(customApply.mirrorX(this.width));
        }
        this.tileApplyListeners.addAll(from.tileApplyListeners);
        this.objectApplyListeners.addAll(from.objectApplyListeners);
        this.wireApplyListeners.addAll(from.wireApplyListeners);
    }

    public final Preset tryMirrorY() {
        try {
            return this.mirrorY();
        }
        catch (PresetMirrorException e) {
            return this.copy();
        }
    }

    public Preset mirrorY() throws PresetMirrorException {
        Preset preset = this.newObject(this.width, this.height);
        preset.mirrorYData(this);
        return preset;
    }

    private void mirrorYData(Preset from) throws PresetMirrorException {
        boolean[] computed = new boolean[this.width * this.height];
        for (int y = 0; y < this.height; ++y) {
            int mirrorY = this.getMirroredY(y);
            for (int x = 0; x < this.width; ++x) {
                Preset.setInt(this.tiles, this.width, x, mirrorY, Preset.getInt(from.tiles, this.width, x, y));
                for (int layer = 0; layer < from.objects.length; ++layer) {
                    int objectID = Preset.getInt(from.objects[layer], this.width, x, y);
                    if (objectID != -1) {
                        GameObject obj = ObjectRegistry.getObject(objectID);
                        if (obj != null) {
                            byte objectRotation = Preset.getByte(from.objectRotations[layer], this.width, x, y);
                            MultiTile multiTile = obj.getMultiTile(objectRotation);
                            Point posOffset = multiTile.getMirrorYPosOffset();
                            if (posOffset == null) {
                                throw new PresetMirrorException(new LocalMessage("ui", "presetmirrorerror", "item", obj.getLocalization()));
                            }
                            Preset.setInt(this.objects[layer], this.width, x + posOffset.x, mirrorY + posOffset.y, objectID);
                            Preset.setByte(this.objectRotations[layer], this.width, x + posOffset.x, mirrorY + posOffset.y, (byte)multiTile.getYMirrorRotation());
                            Preset.setBoolean(computed, this.width, x, mirrorY, true);
                            continue;
                        }
                        Preset.setInt(this.objects[layer], this.width, x, mirrorY, objectID);
                        continue;
                    }
                    if (Preset.getBoolean(computed, this.width, x, mirrorY)) continue;
                    Preset.setInt(this.objects[layer], this.width, x, mirrorY, -1);
                }
                Preset.setByte(this.wires, this.width, x, mirrorY, Preset.getByte(from.wires, this.width, x, y));
            }
        }
        this.hasAnyWireData = null;
        from.logicGates.forEach((p, lg) -> this.logicGates.put(new Point(p.x, this.getMirroredY(p.y)), new PresetLogicGate(lg.logicGateID, lg.data, lg.mirrorX, !lg.mirrorY, lg.rotation)));
        from.objectEntities.forEach((p, lg) -> this.objectEntities.put(new Point(p.x, this.getMirroredY(p.y)), new PresetObjectEntity(lg.data, lg.mirrorX, !lg.mirrorY, lg.rotation)));
        for (ApplyPredicate applyPredicate : from.applyPredicates) {
            this.applyPredicates.add(applyPredicate.mirrorY(this.height));
        }
        for (CustomApply customApply : from.customPreApplies) {
            this.customPreApplies.add(customApply.mirrorY(this.height));
        }
        for (CustomApply customApply : from.customApplies) {
            this.customApplies.add(customApply.mirrorY(this.height));
        }
        this.tileApplyListeners.addAll(from.tileApplyListeners);
        this.objectApplyListeners.addAll(from.objectApplyListeners);
        this.wireApplyListeners.addAll(from.wireApplyListeners);
    }

    public final Preset tryRotate(PresetRotation rotation) {
        try {
            return this.rotate(rotation);
        }
        catch (PresetRotateException e) {
            return this.copy();
        }
    }

    public Preset rotate(PresetRotation rotation) throws PresetRotateException {
        Point dim = PresetUtils.getRotatedPoint(this.width, this.height, 0, 0, rotation);
        Preset preset = this.newObject(Math.abs(dim.x), Math.abs(dim.y));
        preset.rotateData(this, rotation);
        return preset;
    }

    private void rotateData(Preset from, PresetRotation rotation) throws PresetRotateException {
        boolean[] computed = new boolean[this.width * this.height];
        for (int x = 0; x < from.width; ++x) {
            for (int y = 0; y < from.height; ++y) {
                Point rp = PresetUtils.getRotatedPointInSpace(x, y, from.width, from.height, rotation);
                Preset.setInt(this.tiles, this.width, rp.x, rp.y, Preset.getInt(from.tiles, from.width, x, y));
                for (int layer = 0; layer < from.objects.length; ++layer) {
                    int objectID = Preset.getInt(from.objects[layer], from.width, x, y);
                    if (objectID != -1) {
                        GameObject obj = ObjectRegistry.getObject(objectID);
                        if (obj != null) {
                            byte objectRotation = Preset.getByte(from.objectRotations[layer], from.width, x, y);
                            MultiTile multiTile = obj.getMultiTile(objectRotation);
                            Point posOffset = multiTile.getPresetRotationOffset(rotation);
                            if (posOffset == null) {
                                throw new PresetRotateException(new LocalMessage("ui", "presetrotateerror", "item", obj.getLocalization()));
                            }
                            Preset.setInt(this.objects[layer], this.width, rp.x + posOffset.x, rp.y + posOffset.y, objectID);
                            Preset.setByte(this.objectRotations[layer], this.width, rp.x + posOffset.x, rp.y + posOffset.y, (byte)multiTile.getPresetRotation(rotation));
                            Preset.setBoolean(computed, this.width, rp.x, rp.y, true);
                            continue;
                        }
                        Preset.setInt(this.objects[layer], this.width, rp.x, rp.y, objectID);
                        continue;
                    }
                    if (Preset.getBoolean(computed, this.width, rp.x, rp.y)) continue;
                    Preset.setInt(this.objects[layer], this.width, rp.x, rp.y, -1);
                }
                Preset.setByte(this.wires, this.width, rp.x, rp.y, Preset.getByte(from.wires, from.width, x, y));
            }
        }
        this.hasAnyWireData = null;
        from.logicGates.forEach((p, lg) -> this.logicGates.put(PresetUtils.getRotatedPointInSpace(p.x, p.y, from.width, from.height, rotation), new PresetLogicGate(lg.logicGateID, lg.data, false, false, PresetRotation.addRotations(lg.rotation, rotation))));
        from.objectEntities.forEach((p, lg) -> this.objectEntities.put(PresetUtils.getRotatedPointInSpace(p.x, p.y, from.width, from.height, rotation), new PresetObjectEntity(lg.data, false, false, PresetRotation.addRotations(lg.rotation, rotation))));
        for (ApplyPredicate applyPredicate : from.applyPredicates) {
            this.applyPredicates.add(applyPredicate.rotate(rotation, from.width, from.height));
        }
        for (CustomApply customApply : from.customPreApplies) {
            this.customPreApplies.add(customApply.rotate(rotation, from.width, from.height));
        }
        for (CustomApply customApply : from.customApplies) {
            this.customApplies.add(customApply.rotate(rotation, from.width, from.height));
        }
        this.tileApplyListeners.addAll(from.tileApplyListeners);
        this.objectApplyListeners.addAll(from.objectApplyListeners);
        this.wireApplyListeners.addAll(from.wireApplyListeners);
    }

    public void drawPlacePreview(Level level, int levelX, int levelY, PlayerMob perspective, GameCamera camera) {
        int tileY;
        int y;
        int tileX;
        int x;
        for (x = 0; x < this.width; ++x) {
            tileX = levelX + x;
            if (!level.isTileXWithinBounds(tileX)) continue;
            for (y = 0; y < this.height; ++y) {
                int tile2;
                tileY = levelY + y;
                if (!level.isTileYWithinBounds(tileY) || (tile2 = Preset.getInt(this.tiles, this.width, x, y)) == -1) continue;
                TileRegistry.getTile(tile2).drawPreview(level, tileX, tileY, 0.5f, perspective, camera);
            }
        }
        for (x = 0; x < this.width; ++x) {
            tileX = levelX + x;
            if (!level.isTileXWithinBounds(tileX)) continue;
            for (y = 0; y < this.height; ++y) {
                tileY = levelY + y;
                if (!level.isTileYWithinBounds(tileY)) continue;
                for (int layer = 0; layer < this.objects.length; ++layer) {
                    int object = Preset.getInt(this.objects[layer], this.width, x, y);
                    if (object == -1) continue;
                    byte objectRotation = Preset.getByte(this.objectRotations[layer], this.width, x, y);
                    ObjectRegistry.getObject(object).drawPreview(level, tileX, tileY, objectRotation, 0.5f, perspective, camera);
                }
            }
        }
        this.logicGates.forEach((tile, logicGate) -> {
            int tileX = levelX + tile.x;
            int tileY = levelY + tile.y;
            if (level.isTileWithinBounds(tileX, tileY)) {
                LogicGateRegistry.getLogicGate(logicGate.logicGateID).drawPreview(level, tileX, tileY, 0.5f, perspective, camera);
            }
        });
        for (x = 0; x < this.width; ++x) {
            tileX = levelX + x;
            if (!level.isTileXWithinBounds(tileX)) continue;
            for (y = 0; y < this.height; ++y) {
                tileY = levelY + y;
                if (!level.isTileYWithinBounds(tileY)) continue;
                for (int wireID = 0; wireID < 4; ++wireID) {
                    if (!this.hasWire(x, y, wireID)) continue;
                    level.wireManager.drawWirePreset(tileX, tileY, camera, wireID);
                }
            }
        }
    }

    public LinkedList<UndoLogic> applyToLevel(Level level, int levelX, int levelY) {
        return this.applyToLevel(level, levelX, levelY, new GameBlackboard());
    }

    /*
     * WARNING - void declaration
     */
    public LinkedList<UndoLogic> applyToLevel(Level level, int levelX, int levelY, GameBlackboard blackboard) {
        void var6_9;
        for (Consumer<GameBlackboard> consumer : this.blackboardSetups) {
            consumer.accept(blackboard);
        }
        LinkedList<UndoLogic> undoLogics = new LinkedList<UndoLogic>();
        for (CustomApply ca : this.customPreApplies) {
            UndoLogic undoLogic = ca.applyToLevel(level, levelX, levelY, blackboard);
            if (undoLogic == null) continue;
            undoLogics.add(undoLogic);
        }
        boolean bl = false;
        while (var6_9 < this.width) {
            int tileX = levelX + var6_9;
            if (level.isTileXWithinBounds(tileX)) {
                for (int j = 0; j < this.height; ++j) {
                    int tileY = levelY + j;
                    if (!level.isTileYWithinBounds(tileY)) continue;
                    int tile2 = Preset.getInt(this.tiles, this.width, (int)var6_9, j);
                    if (tile2 != -1) {
                        level.setTile(tileX, tileY, tile2);
                        for (TileApplyListener listener : this.tileApplyListeners) {
                            listener.onTileApply(level, tileX, tileY, tile2, blackboard);
                        }
                    }
                    byte wireData = Preset.getByte(this.wires, this.width, (int)var6_9, j);
                    for (int wireID = 0; wireID < 4; ++wireID) {
                        boolean isThere;
                        if (this.clearOtherWires) {
                            if (!GameMath.getBit(wireData, wireID * 2)) continue;
                            isThere = GameMath.getBit(wireData, wireID * 2 + 1);
                            level.wireManager.setWire(tileX, tileY, wireID, isThere);
                            for (WireApplyListener listener : this.wireApplyListeners) {
                                listener.onWireApply(level, tileX, tileY, wireID, isThere, blackboard);
                            }
                            continue;
                        }
                        isThere = GameMath.getBit(wireData, wireID * 2 + 1);
                        if (!isThere) continue;
                        level.wireManager.setWire(tileX, tileY, wireID, isThere);
                        for (WireApplyListener listener : this.wireApplyListeners) {
                            listener.onWireApply(level, tileX, tileY, wireID, isThere, blackboard);
                        }
                    }
                    if (this.clearOtherWires) {
                        level.logicLayer.clearLogicGate(tileX, tileY);
                    }
                    for (int layer = 0; layer < this.objects.length; ++layer) {
                        int object = Preset.getInt(this.objects[layer], this.width, (int)var6_9, j);
                        if (object == -1) continue;
                        byte objectRotation = Preset.getByte(this.objectRotations[layer], this.width, (int)var6_9, j);
                        level.objectLayer.setObject(layer, tileX, tileY, object);
                        level.objectLayer.setObjectRotation(layer, tileX, tileY, objectRotation);
                        for (ObjectApplyListener listener : this.objectApplyListeners) {
                            listener.onObjectApply(level, layer, tileX, tileY, object, objectRotation, blackboard);
                        }
                    }
                }
            }
            ++var6_9;
        }
        this.logicGates.forEach((tile, presetLogicGate) -> {
            int tileX = levelX + tile.x;
            int tileY = levelY + tile.y;
            if (level.isTileWithinBounds(tileX, tileY)) {
                presetLogicGate.applyToLevel(level, tileX, tileY, blackboard);
            }
        });
        this.objectEntities.forEach((tile, presetObjectEntity) -> {
            int tileX = levelX + tile.x;
            int tileY = levelY + tile.y;
            if (level.isTileWithinBounds(tileX, tileY)) {
                presetObjectEntity.applyToLevel(level, tileX, tileY, blackboard);
            }
        });
        for (CustomApply ca : this.customApplies) {
            UndoLogic undoLogic = ca.applyToLevel(level, levelX, levelY, blackboard);
            if (undoLogic == null) continue;
            undoLogics.add(undoLogic);
        }
        return undoLogics;
    }

    public void applyToLevelCentered(Level level, int x, int y) {
        this.applyToLevelCentered(level, x, y, new GameBlackboard());
    }

    public void applyToLevelCentered(Level level, int x, int y, GameBlackboard blackboard) {
        this.applyToLevel(level, x - this.width / 2, y - this.height / 2, blackboard);
    }

    public boolean canApplyToLevel(Level level, int levelX, int levelY) {
        for (ApplyPredicate applyPredicate : this.applyPredicates) {
            if (applyPredicate.canApplyToLevel(level, levelX, levelY)) continue;
            return false;
        }
        return true;
    }

    public boolean canApplyToLevelCentered(Level level, int x, int y) {
        return this.canApplyToLevel(level, x - this.width / 2, y - this.height / 2);
    }

    public Preset subPreset(int x, int y, int width, int height) {
        width = Math.min(this.width - x, width);
        height = Math.min(this.height - y, height);
        Preset sub = new Preset(width, height);
        sub.hasAnyWireData = null;
        for (int subX = 0; subX < width; ++subX) {
            for (int subY = 0; subY < height; ++subY) {
                Preset.setInt(sub.tiles, width, subX, subY, Preset.getInt(this.tiles, this.width, x + subX, y + subY));
                for (int layer = 0; layer < this.objects.length; ++layer) {
                    Preset.setInt(sub.objects[layer], width, subX, subY, Preset.getInt(this.objects[layer], this.width, x + subX, y + subY));
                    Preset.setByte(sub.objectRotations[layer], width, subX, subY, Preset.getByte(this.objectRotations[layer], this.width, x + subX, y + subY));
                }
                Preset.setByte(sub.wires, width, subX, subY, Preset.getByte(this.wires, this.width, x + subX, y + subY));
            }
        }
        return sub;
    }

    public Preset subPresetFull(int x, int y, int width, int height) {
        width = Math.min(this.width - x, width);
        height = Math.min(this.height - y, height);
        Preset sub = new Preset(width, height);
        sub.applyPreset(-x, -y, this);
        return sub;
    }

    public void applyPresetRaw(int x, int y, Preset other) throws PresetCopyException {
        CustomApply copy;
        for (int i = Math.max(x, 0); i < x + other.width; ++i) {
            if (i >= this.width) continue;
            for (int j = Math.max(y, 0); j < y + other.height; ++j) {
                if (j >= this.height) continue;
                this.setTile(i, j, other.getTile(i - x, j - y));
                this.setObject(i, j, other.getObject(i - x, j - y));
                this.setRotation(i, j, other.getObjectRotation(i - x, j - y));
                this.setWireData(i, j, other.getWireData(i - x, j - y));
            }
        }
        for (Map.Entry<Point, PresetLogicGate> entry : other.logicGates.entrySet()) {
            Point otherPos = entry.getKey();
            PresetLogicGate gate = entry.getValue();
            Point newPos = new Point(otherPos.x + x, otherPos.y + y);
            if (newPos.x < 0 || newPos.y < 0 || newPos.x >= this.width || newPos.y >= this.height) continue;
            this.logicGates.put(newPos, gate.copy());
        }
        this.blackboardSetups.addAll(other.blackboardSetups);
        for (ApplyPredicate predicate : other.applyPredicates) {
            this.applyPredicates.add(predicate.copy(x, y, this.width, this.height));
        }
        for (CustomApply apply : other.customPreApplies) {
            copy = apply.copy(x, y, this.width, this.height);
            if (copy == null) continue;
            this.customPreApplies.add(copy);
        }
        for (CustomApply apply : other.customApplies) {
            copy = apply.copy(x, y, this.width, this.height);
            if (copy == null) continue;
            this.customApplies.add(copy);
        }
        this.tileApplyListeners.addAll(other.tileApplyListeners);
        this.objectApplyListeners.addAll(other.objectApplyListeners);
        this.wireApplyListeners.addAll(other.wireApplyListeners);
    }

    public void applyPreset(int x, int y, Preset other) {
        try {
            this.applyPresetRaw(x, y, other);
        }
        catch (PresetCopyException presetCopyException) {
            // empty catch block
        }
    }

    public void applyScript(String script) {
        this.applyScript(0, 0, script);
    }

    public void applyScript(int x, int y, String script) {
        this.applySave(x, y, new LoadData(script));
    }

    public void applySave(int x, int y, LoadData save) {
        LoadData objectEntities;
        Object[] objectsTo;
        Object[] objectsFrom;
        int scriptWidth = save.getInt("width");
        int scriptHeight = save.getInt("height");
        int[] scriptTiles = null;
        if (save.hasLoadDataByName("tiles")) {
            scriptTiles = save.getIntArray("tiles");
        }
        if (scriptTiles != null) {
            Object[] tilesTo;
            Object[] tilesFrom;
            if (save.hasLoadDataByName("tileIDs")) {
                tilesFrom = new String[TileRegistry.getTileStringIDs().length];
                String[] tileIDs = save.getStringArray("tileIDs");
                for (int i = 0; i < tileIDs.length; i += 2) {
                    try {
                        int id = Integer.parseInt(tileIDs[i]);
                        if (id < 0) continue;
                        if (tilesFrom.length <= id) {
                            tilesFrom = (String[])Arrays.copyOf(tilesFrom, id + 1);
                        }
                        tilesFrom[id] = tileIDs[i + 1];
                        continue;
                    }
                    catch (NumberFormatException id) {
                        // empty catch block
                    }
                }
                VersionMigration.convertArray(scriptTiles, (String[])tilesFrom, TileRegistry.getTileStringIDs(), -1, VersionMigration.oldTileStringIDs);
            } else if (save.hasLoadDataByName("tileData") && !Arrays.equals(tilesFrom = save.getStringArray("tileData"), tilesTo = TileRegistry.getTileStringIDs())) {
                VersionMigration.convertArray(scriptTiles, (String[])tilesFrom, (String[])tilesTo, -1, VersionMigration.oldTileStringIDs);
            }
        }
        int[][] scriptObjects = new int[ObjectLayerRegistry.getTotalLayers()][];
        for (int layer = 0; layer < scriptObjects.length; ++layer) {
            String saveStringID;
            String string = saveStringID = layer == 0 ? "objects" : ObjectLayerRegistry.getLayerStringID(layer) + "Objects";
            if (save.hasLoadDataByName(saveStringID)) {
                scriptObjects[layer] = save.getIntArray(saveStringID);
                continue;
            }
            if ((layer <= 0 || !save.hasLoadDataByName(saveStringID + "Clear")) && !save.hasLoadDataByName("objects") || !save.getBoolean(saveStringID + "Clear", true, false)) continue;
            scriptObjects[layer] = new int[this.width * this.height];
        }
        if (save.hasLoadDataByName("objectIDs")) {
            String[] objectsFrom2 = new String[ObjectRegistry.getObjectStringIDs().length];
            String[] objectIDs = save.getStringArray("objectIDs");
            for (int i = 0; i < objectIDs.length; i += 2) {
                try {
                    int id = Integer.parseInt(objectIDs[i]);
                    if (id < 0) continue;
                    if (objectsFrom2.length <= id) {
                        objectsFrom2 = Arrays.copyOf(objectsFrom2, id + 1);
                    }
                    objectsFrom2[id] = objectIDs[i + 1];
                    continue;
                }
                catch (NumberFormatException id) {
                    // empty catch block
                }
            }
            for (int[] layerObjects : scriptObjects) {
                if (layerObjects == null || layerObjects.length == 0) continue;
                VersionMigration.convertArray(layerObjects, objectsFrom2, ObjectRegistry.getObjectStringIDs(), -1, VersionMigration.oldObjectStringIDs);
            }
        } else if (save.hasLoadDataByName("objectData") && !Arrays.equals(objectsFrom = save.getStringArray("objectData"), objectsTo = ObjectRegistry.getObjectStringIDs())) {
            for (int[] layerObjects : scriptObjects) {
                if (layerObjects == null || layerObjects.length == 0) continue;
                VersionMigration.convertArray(layerObjects, (String[])objectsFrom, (String[])objectsTo, -1, VersionMigration.oldObjectStringIDs);
            }
        }
        byte[][] scriptObjectRotations = new byte[ObjectLayerRegistry.getTotalLayers()][];
        for (int layer = 0; layer < scriptObjectRotations.length; ++layer) {
            String saveStringID;
            String string = saveStringID = layer == 0 ? "rotations" : ObjectLayerRegistry.getLayerStringID(layer) + "Rotations";
            if (!save.hasLoadDataByName(saveStringID)) continue;
            scriptObjectRotations[layer] = save.getByteArray(saveStringID);
        }
        this.clearOtherWires = save.getBoolean("clearOtherWires", false, false);
        byte[] scriptWireData = null;
        if (save.hasLoadDataByName("wire")) {
            scriptWireData = save.getByteArray("wire");
        }
        for (int i = Math.max(x, 0); i < x + scriptWidth; ++i) {
            if (i >= this.width) continue;
            for (int j = Math.max(y, 0); j < y + scriptHeight; ++j) {
                int layer;
                if (j >= this.height) continue;
                if (scriptTiles != null) {
                    this.setTile(i, j, Preset.getInt(scriptTiles, scriptWidth, i - x, j - y));
                }
                for (layer = 0; layer < scriptObjects.length; ++layer) {
                    int[] layerObjects;
                    layerObjects = scriptObjects[layer];
                    if (layerObjects == null || layerObjects.length == 0) continue;
                    this.setObjectLayer(layer, i, j, Preset.getInt(layerObjects, scriptWidth, i - x, j - y));
                }
                for (layer = 0; layer < scriptObjectRotations.length; ++layer) {
                    byte[] layerObjectRotations = scriptObjectRotations[layer];
                    if (layerObjectRotations == null || layerObjectRotations.length == 0) continue;
                    this.setRotationLayer(layer, i, j, Preset.getByte(layerObjectRotations, scriptWidth, i - x, j - y));
                }
                if (scriptWireData == null) continue;
                this.setWireData(i, j, Preset.getByte(scriptWireData, scriptWidth, i - x, j - y));
            }
        }
        LoadData logicGates = save.getFirstLoadDataByName("logicGates");
        if (logicGates != null && logicGates.isArray()) {
            for (LoadData data : logicGates.getLoadDataByName("gate")) {
                try {
                    int tileX = data.getInt("tileX");
                    int tileY = data.getInt("tileY");
                    String gateStringID = data.getUnsafeString("stringID");
                    int logicGateID = LogicGateRegistry.getLogicGateID(gateStringID);
                    if (logicGateID == -1) continue;
                    LoadData gateData = data.getFirstLoadDataByName("data");
                    boolean mirrorX = data.getBoolean("mirrorX", false, false);
                    boolean mirrorY = data.getBoolean("mirrorY", false, false);
                    int rotationOffset = data.getInt("rotation", 0, false);
                    this.logicGates.put(new Point(tileX, tileY), new PresetLogicGate(logicGateID, gateData == null ? null : gateData.toSaveData(), mirrorX, mirrorY, PresetRotation.toRotationAngle(rotationOffset)));
                }
                catch (Exception tileX) {}
            }
        }
        if ((objectEntities = save.getFirstLoadDataByName("objectEntities")) != null && objectEntities.isArray()) {
            for (LoadData data : objectEntities.getLoadDataByName("entity")) {
                try {
                    int tileX = data.getInt("tileX");
                    int tileY = data.getInt("tileY");
                    LoadData gateData = data.getFirstLoadDataByName("data");
                    boolean mirrorX = data.getBoolean("mirrorX", false, false);
                    boolean mirrorY = data.getBoolean("mirrorY", false, false);
                    int rotationOffset = data.getInt("rotation", 0, false);
                    this.objectEntities.put(new Point(tileX, tileY), new PresetObjectEntity(gateData == null ? null : gateData.toSaveData(), mirrorX, mirrorY, PresetRotation.toRotationAngle(rotationOffset)));
                }
                catch (Exception exception) {}
            }
        }
    }

    public SaveData getSaveData(String saveName) {
        return this.getSaveData(saveName, new PresetCopyFilter());
    }

    public SaveData getSaveData() {
        return this.getSaveData("PRESET");
    }

    public SaveData getSaveData(String saveName, PresetCopyFilter filter) {
        SaveData save = new SaveData(saveName);
        save.addInt("width", this.width);
        save.addInt("height", this.height);
        if (filter.acceptTiles) {
            HashSet<Integer> tilesUsed = new HashSet<Integer>();
            for (int tile : this.tiles) {
                if (tile < 0) continue;
                tilesUsed.add(tile);
            }
            if (!tilesUsed.isEmpty()) {
                String[] tileIDs = new String[tilesUsed.size() * 2];
                int i = 0;
                Iterator iterator = tilesUsed.iterator();
                while (iterator.hasNext()) {
                    int tile;
                    tile = (Integer)iterator.next();
                    tileIDs[i++] = Integer.toString(tile);
                    tileIDs[i++] = TileRegistry.getTile(tile).getStringID();
                }
                save.addStringArray("tileIDs", tileIDs);
            }
            save.addIntArray("tiles", this.tiles);
        }
        if (filter.acceptObjects) {
            HashSet<Integer> objectsUsed = new HashSet<Integer>();
            boolean addAir = false;
            boolean[] layerHasData = new boolean[this.objects.length];
            boolean[] layerHasObjects = new boolean[this.objects.length];
            for (int layer = 0; layer < this.objects.length; ++layer) {
                for (int object : this.objects[layer]) {
                    if (object < 0) continue;
                    objectsUsed.add(object);
                    addAir = true;
                    layerHasData[layer] = true;
                    if (object <= 0) continue;
                    layerHasObjects[layer] = true;
                }
            }
            if (addAir) {
                objectsUsed.add(0);
            }
            if (!objectsUsed.isEmpty()) {
                String[] objectIDs = new String[objectsUsed.size() * 2];
                int i = 0;
                Iterator iterator = objectsUsed.iterator();
                while (iterator.hasNext()) {
                    int object = (Integer)iterator.next();
                    objectIDs[i++] = Integer.toString(object);
                    objectIDs[i++] = ObjectRegistry.getObject(object).getStringID();
                }
                save.addStringArray("objectIDs", objectIDs);
            }
            save.addIntArray("objects", this.objects[0]);
            save.addByteArray("rotations", this.objectRotations[0]);
            for (int layer = 1; layer < this.objects.length; ++layer) {
                if (!layerHasData[layer]) continue;
                if (layerHasObjects[layer]) {
                    save.addIntArray(ObjectLayerRegistry.getLayerStringID(layer) + "Objects", this.objects[layer]);
                    save.addByteArray(ObjectLayerRegistry.getLayerStringID(layer) + "Rotations", this.objectRotations[layer]);
                    continue;
                }
                save.addBoolean(ObjectLayerRegistry.getLayerStringID(layer) + "ObjectsClear", true);
            }
        }
        if (filter.acceptWires) {
            save.addBoolean("clearOtherWires", this.clearOtherWires);
            if (this.clearOtherWires || this.hasAnyWireData()) {
                save.addByteArray("wire", this.wires);
            }
            if (!this.logicGates.isEmpty()) {
                SaveData logicGatesData = new SaveData("logicGates");
                this.logicGates.forEach((point, gate) -> {
                    SaveData data = new SaveData("gate");
                    data.addInt("tileX", point.x);
                    data.addInt("tileY", point.y);
                    if (gate.mirrorX) {
                        data.addBoolean("mirrorX", true);
                    }
                    if (gate.mirrorY) {
                        data.addBoolean("mirrorY", true);
                    }
                    if (gate.rotation != null) {
                        data.addInt("rotation", gate.rotation.dirOffset);
                    }
                    data.addUnsafeString("stringID", LogicGateRegistry.getLogicGateStringID(gate.logicGateID));
                    if (gate.data != null) {
                        data.addSaveData(gate.data);
                    }
                    logicGatesData.addSaveData(data);
                });
                save.addSaveData(logicGatesData);
            }
        }
        if (filter.acceptObjects && filter.acceptObjectEntities && !this.objectEntities.isEmpty()) {
            SaveData objectEntityData = new SaveData("objectEntities");
            this.objectEntities.forEach((point, objectEntity) -> {
                SaveData data = new SaveData("entity");
                data.addInt("tileX", point.x);
                data.addInt("tileY", point.y);
                if (objectEntity.mirrorX) {
                    data.addBoolean("mirrorX", true);
                }
                if (objectEntity.mirrorY) {
                    data.addBoolean("mirrorY", true);
                }
                if (objectEntity.rotation != null) {
                    data.addInt("rotation", objectEntity.rotation.dirOffset);
                }
                if (objectEntity.data != null) {
                    data.addSaveData(objectEntity.data);
                }
                objectEntityData.addSaveData(data);
            });
            save.addSaveData(objectEntityData);
        }
        return save;
    }

    public SaveData getSaveData(PresetCopyFilter filter) {
        return this.getSaveData("PRESET", filter);
    }

    public String getScript() {
        return this.getSaveData().getScript();
    }

    public String getCompressedBase64Script() {
        SaveData saveData = this.getSaveData();
        try {
            return GameUtils.toBase64(GameUtils.compressData(saveData.getScript(true).getBytes()));
        }
        catch (Exception e) {
            return saveData.getScript(false);
        }
    }

    public void copyFromLevel(Level level, int x, int y) {
        this.copyFromLevel(level, x, y, new PresetCopyFilter());
    }

    public void copyFromLevel(Level level, int x, int y, PresetCopyFilter filter) {
        this.copyFromLevel(level, x, y, 0, 0, this.width, this.height, filter);
    }

    public void copyFromLevel(Level level, int levelTileX, int levelTileY, int presetTileX, int presetTileY, int tileWidth, int tileHeight, PresetCopyFilter filter) {
        for (int tileX = 0; tileX < tileWidth; ++tileX) {
            int currentPresetTileX = presetTileX + tileX;
            if (currentPresetTileX < 0 || currentPresetTileX >= this.width) continue;
            int currentLevelTileX = levelTileX + tileX;
            for (int tileY = 0; tileY < tileHeight; ++tileY) {
                int currentPresetTileY = presetTileY + tileY;
                if (currentPresetTileY < 0 || currentPresetTileY >= this.height) continue;
                int currentLevelTileY = levelTileY + tileY;
                if (filter.acceptTiles) {
                    this.setTile(currentPresetTileX, currentPresetTileY, level.getTileID(currentLevelTileX, currentLevelTileY));
                }
                if (filter.acceptObjects) {
                    for (int layer = 0; layer < ObjectLayerRegistry.getTotalLayers(); ++layer) {
                        this.setObjectLayer(layer, currentPresetTileX, currentPresetTileY, level.getObjectID(layer, currentLevelTileX, currentLevelTileY));
                        this.setRotationLayer(layer, currentPresetTileX, currentPresetTileY, level.getObjectRotation(layer, currentLevelTileX, currentLevelTileY));
                    }
                    if (filter.acceptObjectEntities) {
                        this.setObjectEntity(currentPresetTileX, currentPresetTileY, level.entityManager.getObjectEntity(currentLevelTileX, currentLevelTileY));
                    }
                }
                if (!filter.acceptWires) continue;
                for (int wireID = 0; wireID < 4; ++wireID) {
                    this.putWire(currentPresetTileX, currentPresetTileY, wireID, level.wireManager.hasWire(currentLevelTileX, currentLevelTileY, wireID));
                }
                if (level.logicLayer.hasGate(currentLevelTileX, currentLevelTileY)) {
                    this.setLogicGate(currentPresetTileX, currentPresetTileY, level.logicLayer.getEntity(currentLevelTileX, currentLevelTileY));
                    continue;
                }
                this.setLogicGate(currentPresetTileX, currentPresetTileY, null);
            }
        }
    }

    public static void setInt(int[] buffer, int width, int x, int y, int value) {
        if (x + y * width >= buffer.length) {
            return;
        }
        buffer[x + y * width] = value;
    }

    public static int getInt(int[] buffer, int width, int x, int y) {
        if (x + y * width >= buffer.length) {
            return -1;
        }
        return buffer[x + y * width];
    }

    public static void setByte(byte[] buffer, int width, int x, int y, byte value) {
        if (x + y * width >= buffer.length) {
            return;
        }
        buffer[x + y * width] = value;
    }

    public static byte getByte(byte[] buffer, int width, int x, int y) {
        if (x + y * width >= buffer.length) {
            return -1;
        }
        return buffer[x + y * width];
    }

    public static void setBoolean(boolean[] buffer, int width, int x, int y, boolean value) {
        if (x + y * width >= buffer.length) {
            return;
        }
        buffer[x + y * width] = value;
    }

    public static boolean getBoolean(boolean[] buffer, int width, int x, int y) {
        if (x + y * width >= buffer.length) {
            return false;
        }
        return buffer[x + y * width];
    }

    public void setTile(int x, int y, int tile) {
        Preset.setInt(this.tiles, this.width, x, y, tile);
    }

    public void setObjectLayer(int layerID, int x, int y, int object, int rotation) {
        Preset.setInt(this.objects[layerID], this.width, x, y, object);
        Preset.setByte(this.objectRotations[layerID], this.width, x, y, (byte)rotation);
    }

    public void setObjectLayer(int layerID, int x, int y, int object) {
        this.setObjectLayer(layerID, x, y, object, 0);
    }

    public void setObject(int x, int y, int object, int rotation) {
        this.setObjectLayer(0, x, y, object, rotation);
    }

    public void setObject(int x, int y, int object) {
        this.setObject(x, y, object, 0);
    }

    public void setRotationLayer(int layerID, int x, int y, int rotation) {
        Preset.setByte(this.objectRotations[layerID], this.width, x, y, (byte)rotation);
    }

    public void setRotation(int x, int y, int rotation) {
        this.setRotationLayer(0, x, y, rotation);
    }

    public void setWireData(int x, int y, byte wireData) {
        Preset.setByte(this.wires, this.width, x, y, wireData);
        this.hasAnyWireData = null;
    }

    public void putWire(int x, int y, int wireID, boolean isThere) {
        byte wireData = this.getWireData(x, y);
        wireData = GameMath.setBit(wireData, wireID * 2, true);
        wireData = GameMath.setBit(wireData, wireID * 2 + 1, isThere);
        this.setWireData(x, y, wireData);
    }

    public void clearWire(int x, int y, int wireID) {
        this.setWireData(x, y, GameMath.setBit(this.getWireData(x, y), wireID * 2, false));
    }

    public boolean hasWire(int x, int y, int wireID) {
        byte wireData = this.getWireData(x, y);
        return GameMath.getBit(wireData, wireID * 2) && GameMath.getBit(wireData, wireID * 2 + 1);
    }

    public boolean doesSetWire(int x, int y, int wireID) {
        return GameMath.getBit(this.getWireData(x, y), wireID * 2);
    }

    public boolean hasAnyWireData() {
        if (this.hasAnyWireData == null) {
            this.hasAnyWireData = false;
            for (byte wireData : this.wires) {
                for (int wireID = 0; wireID < 4; ++wireID) {
                    if (!GameMath.getBit(wireData, wireID * 2 + 1)) continue;
                    this.hasAnyWireData = true;
                    break;
                }
                if (this.hasAnyWireData.booleanValue()) break;
            }
        }
        return this.hasAnyWireData;
    }

    public int getTile(int x, int y) {
        return Preset.getInt(this.tiles, this.width, x, y);
    }

    public int getObject(int layerID, int x, int y) {
        return Preset.getInt(this.objects[layerID], this.width, x, y);
    }

    public int getObject(int x, int y) {
        return this.getObject(0, x, y);
    }

    public byte getObjectRotation(int layerID, int x, int y) {
        return Preset.getByte(this.objectRotations[layerID], this.width, x, y);
    }

    public byte getObjectRotation(int x, int y) {
        return this.getObjectRotation(0, x, y);
    }

    public byte getWireData(int x, int y) {
        return Preset.getByte(this.wires, this.width, x, y);
    }

    public void fillTile(int x, int y, int width, int height, int tile) {
        for (int i = x; i < width + x; ++i) {
            for (int j = y; j < height + y; ++j) {
                this.setTile(i, j, tile);
            }
        }
    }

    public void fillObject(int x, int y, int width, int height, int object, int rotation) {
        for (int i = x; i < width + x; ++i) {
            for (int j = y; j < height + y; ++j) {
                this.setObject(i, j, object, rotation);
            }
        }
    }

    public void fillObject(int x, int y, int width, int height, int object) {
        this.fillObject(x, y, width, height, object, 0);
    }

    public void boxObject(int x, int y, int width, int height, int object, int rotation) {
        this.fillObject(x, y, width, 1, object, rotation);
        this.fillObject(x, y, 1, height, object, rotation);
        this.fillObject(x, y + height - 1, width, 1, object, rotation);
        this.fillObject(x + width - 1, y, 1, height, object, rotation);
    }

    public void boxObject(int x, int y, int width, int height, int object) {
        this.boxObject(x, y, width, height, object, 0);
    }

    public void boxTile(int x, int y, int width, int height, int tile) {
        this.fillTile(x, y, width, 1, tile);
        this.fillTile(x, y, 1, height, tile);
        this.fillTile(x, y + height - 1, width, 1, tile);
        this.fillTile(x + width - 1, y, 1, height, tile);
    }

    public void replaceTile(String oldTileID, String newTileID) {
        this.replaceTile(TileRegistry.getTileID(oldTileID), TileRegistry.getTileID(newTileID));
    }

    public void replaceNonEmptyTiles(int oldTile, int newTile) {
        if (oldTile > 0 && newTile >= 0) {
            this.replaceTile(oldTile, newTile);
        }
    }

    public Preset randomlyReplaceObjects(int oldObjectID, Supplier<Integer> getRandomObjectID) {
        return this.randomlyReplaceObjects(0, oldObjectID, getRandomObjectID);
    }

    public Preset randomlyReplaceObjects(int objectLayer, int oldObjectID, Supplier<Integer> getRandomObjectID) {
        if (oldObjectID != -1) {
            for (int i = 0; i < this.objects[objectLayer].length; ++i) {
                int newObject = getRandomObjectID.get();
                if (newObject == -1 || this.objects[objectLayer][i] != oldObjectID) continue;
                this.objects[objectLayer][i] = newObject;
            }
        }
        return this;
    }

    public void replaceTile(int oldTile, int newTile) {
        for (int i = 0; i < this.tiles.length; ++i) {
            if (this.tiles[i] != oldTile) continue;
            this.tiles[i] = newTile;
        }
    }

    public void replaceObjectLayer(int layerID, int oldObject, int newObject, int newRotation) {
        for (int i = 0; i < this.objects[layerID].length; ++i) {
            if (this.objects[layerID][i] != oldObject) continue;
            this.objects[layerID][i] = newObject;
            if (newRotation == -1) continue;
            this.objectRotations[layerID][i] = (byte)newRotation;
        }
    }

    public void replaceObjectAll(int oldObject, int newObject, int newRotation) {
        for (int layer = 0; layer < this.objects.length; ++layer) {
            this.replaceObjectLayer(layer, oldObject, newObject, newRotation);
        }
    }

    public void replaceObject(int objectLayerID, int oldObject, int newObject, int newRotation) {
        this.replaceObjectLayer(objectLayerID, oldObject, newObject, newRotation);
    }

    public void replaceObject(int oldObject, int newObject, int newRotation) {
        this.replaceObjectLayer(0, oldObject, newObject, newRotation);
    }

    public void replaceObject(String oldObjectID, String newObjectID) {
        this.replaceObject(ObjectRegistry.getObjectID(oldObjectID), ObjectRegistry.getObjectID(newObjectID), -1);
    }

    public void replaceObject(int oldObject, int newObject) {
        this.replaceObject(oldObject, newObject, -1);
    }

    public void replaceNonEmptyObjects(int oldObject, int newObject) {
        if (oldObject > 0 && newObject >= 0) {
            this.replaceObject(oldObject, newObject, -1);
        }
    }

    public void replaceNonEmptyObjects(int objectLayerID, int oldObject, int newObject) {
        if (oldObject > 0 && newObject >= 0) {
            this.replaceObject(objectLayerID, oldObject, newObject, -1);
        }
    }

    public void iteratePreset(BiConsumer<Integer, Integer> consumer) {
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                consumer.accept(x, y);
            }
        }
    }

    public void iteratePreset(BiFunction<Integer, Integer, Boolean> filter, BiConsumer<Integer, Integer> consumer) {
        this.iteratePreset((x, y) -> {
            if (((Boolean)filter.apply((Integer)x, (Integer)y)).booleanValue()) {
                consumer.accept((Integer)x, (Integer)y);
            }
        });
    }

    public void setLogicGate(int tileX, int tileY, LogicGateEntity entity) {
        if (tileX < 0 || tileY < 0 || tileX >= this.width || tileY >= this.height) {
            throw new IllegalArgumentException("Cannot add logic gate outside preset bounds");
        }
        if (entity == null) {
            this.logicGates.remove(new Point(tileX, tileY));
        } else {
            SaveData data = new SaveData("data");
            entity.addPresetSaveData(data);
            this.logicGates.put(new Point(tileX, tileY), new PresetLogicGate(entity.logicGateID, data, false, false, null));
        }
    }

    public void setObjectEntity(int tileX, int tileY, ObjectEntity entity) {
        if (tileX < 0 || tileY < 0 || tileX >= this.width || tileY >= this.height) {
            throw new IllegalArgumentException("Cannot add object entity outside preset bounds");
        }
        if (entity == null) {
            this.objectEntities.remove(new Point(tileX, tileY));
        } else {
            SaveData data = new SaveData("data");
            entity.addPresetSaveData(data);
            if (!data.isEmpty()) {
                this.objectEntities.put(new Point(tileX, tileY), new PresetObjectEntity(data, false, false, null));
            }
        }
    }

    public void onTileApply(TileApplyListener listener) {
        Objects.requireNonNull(listener);
        this.tileApplyListeners.add(listener);
    }

    public void onObjectApply(ObjectApplyListener listener) {
        Objects.requireNonNull(listener);
        this.objectApplyListeners.add(listener);
    }

    public void onWireApply(WireApplyListener listener) {
        Objects.requireNonNull(listener);
        this.wireApplyListeners.add(listener);
    }

    public void addCanApplyPredicate(ApplyPredicate apply) {
        Objects.requireNonNull(apply);
        this.applyPredicates.add(apply);
    }

    public void addCanApplyPredicate(int tileX, int tileY, int dir, ApplyPredicateFunction apply, boolean limitWithinBounds) {
        Objects.requireNonNull(apply);
        this.addCanApplyPredicate(new ApplyTilePredicate(tileX, tileY, dir, apply, limitWithinBounds));
    }

    public void addCanApplyPredicate(int tileX, int tileY, int dir, ApplyPredicateFunction apply) {
        this.addCanApplyPredicate(tileX, tileY, dir, apply, false);
    }

    public void addCanApplyAreaPredicate(int startX, int startY, int endX, int endY, int dir, ApplyAreaPredicateFunction apply, boolean limitWithinBounds) {
        Objects.requireNonNull(apply);
        this.addCanApplyPredicate(new ApplyAreaPredicate(startX, startY, endX, endY, dir, apply, limitWithinBounds));
    }

    public void addCanApplyAreaPredicate(int startX, int startY, int endX, int endY, int dir, ApplyAreaPredicateFunction apply) {
        this.addCanApplyAreaPredicate(startX, startY, endX, endY, dir, apply, false);
    }

    public void addCanApplyRectPredicate(int tileX, int tileY, int width, int height, int dir, ApplyAreaPredicateFunction apply, boolean limitWithinBounds) {
        this.addCanApplyAreaPredicate(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply, limitWithinBounds);
    }

    public void addCanApplyRectPredicate(int tileX, int tileY, int width, int height, int dir, ApplyAreaPredicateFunction apply) {
        this.addCanApplyAreaPredicate(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply);
    }

    public void addCanApplyAreaEachPredicate(int startX, int startY, int endX, int endY, int dir, ApplyPredicateFunction apply, boolean limitWithinBounds) {
        Objects.requireNonNull(apply);
        this.addCanApplyPredicate(new ApplyAreaPredicate(startX, startY, endX, endY, dir, apply, limitWithinBounds));
    }

    public void addCanApplyAreaEachPredicate(int startX, int startY, int endX, int endY, int dir, ApplyPredicateFunction apply) {
        this.addCanApplyAreaEachPredicate(startX, startY, endX, endY, dir, apply, false);
    }

    public void addCanApplyRectEachPredicate(int tileX, int tileY, int width, int height, int dir, ApplyPredicateFunction apply, boolean limitWithinBounds) {
        this.addCanApplyAreaEachPredicate(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply, limitWithinBounds);
    }

    public void addCanApplyRectEachPredicate(int tileX, int tileY, int width, int height, int dir, ApplyPredicateFunction apply) {
        this.addCanApplyAreaEachPredicate(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply);
    }

    public void addCustomPreApply(CustomApply apply) {
        Objects.requireNonNull(apply);
        this.customPreApplies.add(apply);
    }

    public void addCustomPreApply(int tileX, int tileY, int dir, CustomApplyFunction apply, boolean limitWithinBounds) {
        Objects.requireNonNull(apply);
        this.addCustomPreApply(new CustomApplyTile(tileX, tileY, dir, apply, limitWithinBounds));
    }

    public void addCustomPreApply(int tileX, int tileY, int dir, CustomApplyFunction apply) {
        this.addCustomPreApply(tileX, tileY, dir, apply, true);
    }

    public void addCustomPreApplyArea(int startX, int startY, int endX, int endY, int dir, CustomApplyAreaFunction apply, boolean limitWithinBounds) {
        Objects.requireNonNull(apply);
        this.addCustomPreApply(new CustomApplyArea(startX, startY, endX, endY, dir, apply, limitWithinBounds));
    }

    public void addCustomPreApplyArea(int startX, int startY, int endX, int endY, int dir, CustomApplyAreaFunction apply) {
        this.addCustomPreApplyArea(startX, startY, endX, endY, dir, apply, true);
    }

    public void addCustomPreApplyRect(int tileX, int tileY, int width, int height, int dir, CustomApplyAreaFunction apply, boolean limitWithinBounds) {
        this.addCustomPreApplyArea(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply, limitWithinBounds);
    }

    public void addCustomPreApplyRect(int tileX, int tileY, int width, int height, int dir, CustomApplyAreaFunction apply) {
        this.addCustomPreApplyArea(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply);
    }

    public void addCustomPreApplyAreaEach(int startX, int startY, int endX, int endY, int dir, CustomApplyFunction apply, boolean limitWithinBounds) {
        Objects.requireNonNull(apply);
        this.addCustomPreApply(new CustomApplyArea(startX, startY, endX, endY, dir, apply, limitWithinBounds));
    }

    public void addCustomPreApplyAreaEach(int startX, int startY, int endX, int endY, int dir, CustomApplyFunction apply) {
        this.addCustomPreApplyAreaEach(startX, startY, endX, endY, dir, apply, true);
    }

    public void addCustomPreApplyRectEach(int tileX, int tileY, int width, int height, int dir, CustomApplyFunction apply, boolean limitWithinBounds) {
        this.addCustomPreApplyAreaEach(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply, limitWithinBounds);
    }

    public void addCustomPreApplyRectEach(int tileX, int tileY, int width, int height, int dir, CustomApplyFunction apply) {
        this.addCustomPreApplyAreaEach(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply);
    }

    public void setupBlackboard(Consumer<GameBlackboard> blackboardConsumer) {
        Objects.requireNonNull(blackboardConsumer);
        this.blackboardSetups.add(blackboardConsumer);
    }

    public void addCustomApply(CustomApply apply) {
        Objects.requireNonNull(apply);
        this.customApplies.add(apply);
    }

    public void addCustomApply(int tileX, int tileY, int dir, CustomApplyFunction apply, boolean limitWithinBounds) {
        Objects.requireNonNull(apply);
        this.addCustomApply(new CustomApplyTile(tileX, tileY, dir, apply, limitWithinBounds));
    }

    public void addCustomApply(int tileX, int tileY, int dir, CustomApplyFunction apply) {
        this.addCustomApply(tileX, tileY, dir, apply, true);
    }

    public void addCustomApplyArea(int startX, int startY, int endX, int endY, int dir, CustomApplyAreaFunction apply, boolean limitWithinBounds) {
        Objects.requireNonNull(apply);
        this.addCustomApply(new CustomApplyArea(startX, startY, endX, endY, dir, apply, limitWithinBounds));
    }

    public void addCustomApplyArea(int startX, int startY, int endX, int endY, int dir, CustomApplyAreaFunction apply) {
        this.addCustomApplyArea(startX, startY, endX, endY, dir, apply, true);
    }

    public void addCustomApplyRect(int tileX, int tileY, int width, int height, int dir, CustomApplyAreaFunction apply, boolean limitWithinBounds) {
        this.addCustomApplyArea(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply, limitWithinBounds);
    }

    public void addCustomApplyRect(int tileX, int tileY, int width, int height, int dir, CustomApplyAreaFunction apply) {
        this.addCustomApplyArea(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply);
    }

    public void addCustomApplyAreaEach(int startX, int startY, int endX, int endY, int dir, CustomApplyFunction apply, boolean limitWithinBounds) {
        Objects.requireNonNull(apply);
        this.addCustomApply(new CustomApplyArea(startX, startY, endX, endY, dir, apply, limitWithinBounds));
    }

    public void addCustomApplyAreaEach(int startX, int startY, int endX, int endY, int dir, CustomApplyFunction apply) {
        this.addCustomApplyAreaEach(startX, startY, endX, endY, dir, apply, true);
    }

    public void addCustomApplyRectEach(int tileX, int tileY, int width, int height, int dir, CustomApplyFunction apply, boolean limitWithinBounds) {
        this.addCustomApplyAreaEach(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply, limitWithinBounds);
    }

    public void addCustomApplyRectEach(int tileX, int tileY, int width, int height, int dir, CustomApplyFunction apply) {
        this.addCustomApplyAreaEach(tileX, tileY, tileX + width - 1, tileY + height - 1, dir, apply);
    }

    public void addMobs(int tileX, int tileY, Consumer<Mob> onAdded, String ... mobIDs) {
        for (String mobID : mobIDs) {
            this.addMob(mobID, tileX, tileY, onAdded, false);
        }
    }

    protected void chanceAddMobs(float chancePercent, int tileX, int tileY, String ... mobIDs) {
        if (GameRandom.globalRandom.nextFloat() > chancePercent) {
            return;
        }
        this.addMobs(tileX, tileY, false, mobIDs);
    }

    public void addMobs(int tileX, int tileY, boolean canDespawn, String ... mobIDs) {
        for (String mobID : mobIDs) {
            this.addMob(mobID, tileX, tileY, canDespawn, false);
        }
    }

    public <T extends Mob> void addMob(Function<Level, T> mobSupplier, int tileX, int tileY, Consumer<T> onAdded, boolean limitWithinBounds) {
        this.addCustomApply(tileX, tileY, 0, (level, levelX, levelY, dir, blackboard) -> {
            Mob mob;
            if (!level.isClient() && (mob = (Mob)mobSupplier.apply(level)) != null) {
                mob.onSpawned(levelX * 32 + 16, levelY * 32 + 16);
                level.entityManager.addMob(mob, levelX * 32 + 16, levelY * 32 + 16);
                onAdded.accept(mob);
                return (level1, presetX, presetY) -> mob.remove();
            }
            return null;
        }, limitWithinBounds);
    }

    public <T extends Mob> void addMob(Function<Level, T> mobSupplier, int tileX, int tileY, Consumer<T> onAdded) {
        this.addMob(mobSupplier, tileX, tileY, onAdded, false);
    }

    public <T extends Mob> void addMob(String mobStringID, int tileX, int tileY, Class<T> mobClass, Consumer<T> onAdded, boolean limitWithinBounds) {
        this.addMob((Level level) -> (Mob)mobClass.cast(MobRegistry.getMob(mobStringID, level)), tileX, tileY, onAdded, limitWithinBounds);
    }

    public <T extends Mob> void addMob(String mobStringID, int tileX, int tileY, Class<T> mobClass, Consumer<T> onAdded) {
        this.addMob((Level level) -> (Mob)mobClass.cast(MobRegistry.getMob(mobStringID, level)), tileX, tileY, onAdded);
    }

    public void addMob(String mobStringID, int tileX, int tileY, Consumer<Mob> onAdded, boolean limitWithinBounds) {
        this.addMob(mobStringID, tileX, tileY, Mob.class, onAdded, limitWithinBounds);
    }

    public void addMob(String mobStringID, int tileX, int tileY, Consumer<Mob> onAdded) {
        this.addMob(mobStringID, tileX, tileY, Mob.class, onAdded);
    }

    public void addMob(String mobStringID, int tileX, int tileY, boolean canDespawn, boolean limitWithinBounds) {
        this.addMob(mobStringID, tileX, tileY, (Mob mob) -> {
            mob.canDespawn = canDespawn;
        }, limitWithinBounds);
    }

    public void addMob(String mobStringID, int tileX, int tileY, boolean canDespawn) {
        this.addMob(mobStringID, tileX, tileY, (Mob mob) -> {
            mob.canDespawn = canDespawn;
        });
    }

    public String getRandomHostileMobNameForBiomeLevelExcept(Biome biome, LevelIdentifier levelIdentifier, GameRandom seededRandom, String ... exceptions) {
        String[] mobStringIDsForBiomeLevel = JournalRegistry.getHostileMobStringIDsForBiomeLevel(biome, levelIdentifier);
        if (mobStringIDsForBiomeLevel != null) {
            String[] streamResult = (String[])Arrays.stream(mobStringIDsForBiomeLevel).filter(stringID -> Arrays.stream(exceptions).noneMatch(stringID::contains)).toArray(String[]::new);
            if (streamResult.length == 0) {
                return "skeleton";
            }
            String chanceResult = seededRandom.getOneOf(streamResult);
            if (chanceResult == null) {
                return "skeleton";
            }
            return chanceResult;
        }
        return "skeleton";
    }

    public String getRandomHostileMobNameForBiomeLevelFromGiven(Biome biome, LevelIdentifier levelIdentifier, GameRandom seededRandom, String ... choices) {
        String[] mobStringIDsForBiomeLevel = JournalRegistry.getHostileMobStringIDsForBiomeLevel(biome, levelIdentifier);
        if (mobStringIDsForBiomeLevel != null) {
            String[] streamResult = (String[])Arrays.stream(mobStringIDsForBiomeLevel).filter(stringID -> Arrays.stream(choices).anyMatch(stringID::contains)).toArray(String[]::new);
            if (streamResult.length == 0) {
                return "skeleton";
            }
            String chanceResult = seededRandom.getOneOf(streamResult);
            if (chanceResult == null) {
                return "skeleton";
            }
            return chanceResult;
        }
        return "skeleton";
    }

    public void addRandomHostileMobForBiomeLevel(Biome biome, LevelIdentifier levelIdentifier, int tileX, int tileY, boolean canDespawn, GameRandom seededRandom, String[] exceptions) {
        String mobStringIDsForBiomeLevel = this.getRandomHostileMobNameForBiomeLevelExcept(biome, levelIdentifier, seededRandom, exceptions);
        this.addMob(mobStringIDsForBiomeLevel, tileX, tileY, canDespawn);
    }

    public void addHuman(String mobStringID, int homeX, int homeY, Consumer<HumanMob> onAdded, GameRandom random) {
        this.addCustomApply(homeX, homeY, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (!level.isClient()) {
                Mob mob = MobRegistry.getMob(mobStringID, level);
                if (!(mob instanceof HumanMob)) {
                    GameLog.err.println("Tried spawning non human or null mob with addHuman() in preset. MobStringID: " + mobStringID);
                    return null;
                }
                HumanMob humanMob = (HumanMob)mob;
                Point spawnLocation = GameUtils.getValidMobLocationAroundObject(random, level, mob, levelX, levelY, true);
                mob.onSpawned(spawnLocation.x, spawnLocation.y);
                level.entityManager.addMob(humanMob, spawnLocation.x, spawnLocation.y);
                humanMob.setHome(levelX, levelY);
                humanMob.canDespawn = false;
                onAdded.accept(humanMob);
                return (level1, presetX, presetY) -> humanMob.remove();
            }
            return null;
        });
    }

    public void addInventory(LootTable lootTable, GameRandom random, int tileX, int tileY, Object ... extra) {
        this.addCustomApply(tileX, tileY, 0, (level, levelX, levelY, dir, blackboard) -> {
            LevelObject masterLevelObject = level.getLevelObject(levelX, levelY).getMasterLevelObject().orElse(null);
            if (masterLevelObject != null) {
                levelX = masterLevelObject.tileX;
                levelY = masterLevelObject.tileY;
            }
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt == null || !objEnt.implementsOEInventory()) {
                    throw new NullPointerException("Could not find an objectEntity (" + objEnt + ") with inventory for loot table at [X" + levelX + ", Y" + levelY + "] in preset " + this.getClass());
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                return null;
            }
            lootTable.applyToLevel(random, level.buffManager.getModifier(LevelModifiers.LOOT).floatValue(), level, levelX, levelY, GameUtils.concat(new Object[]{level}, extra));
            return null;
        }, true);
    }

    public void addSign(Supplier<String> text, int x, int y, int object, int objectRotation) {
        this.setObject(x, y, object, objectRotation);
        this.addCustomApply(x, y, 0, (level, levelX, levelY, dir, blackboard) -> {
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt instanceof SignObjectEntity) {
                    ((SignObjectEntity)objEnt).setText((String)text.get());
                } else if (level.isServer()) {
                    throw new NullPointerException("Could not find a sign objectEntity for preset " + this.getClass() + " at " + levelX + ", " + levelY);
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
            return null;
        }, true);
    }

    public void addSign(Supplier<String> text, int x, int y, int objectRotation) {
        this.setObject(x, y, ObjectRegistry.getObjectID("sign"), objectRotation);
        this.addCustomApply(x, y, 0, (level, levelX, levelY, dir, blackboard) -> {
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt instanceof SignObjectEntity) {
                    ((SignObjectEntity)objEnt).setText((String)text.get());
                } else if (level.isServer()) {
                    throw new NullPointerException("Could not find a sign objectEntity for preset " + this.getClass() + " at " + levelX + ", " + levelY);
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
            return null;
        }, true);
    }

    public void addSign(String text, int x, int y) {
        this.addSign(() -> text, x, y, this.getObjectRotation(x, y));
    }

    public void addSign(Supplier<String> text, int x, int y) {
        this.addSign(text, x, y, this.getObjectRotation(x, y));
    }

    public Point getMirroredPoint(int x, int y, boolean xMirror, boolean yMirror) {
        return PresetUtils.getMirroredPoint(x, y, xMirror, yMirror, this.width, this.height);
    }

    public int getMirroredX(int x) {
        return PresetUtils.getMirroredValue(x, this.width);
    }

    public int getMirroredY(int y) {
        return PresetUtils.getMirroredValue(y, this.height);
    }

    public static interface ApplyPredicate {
        public boolean canApplyToLevel(Level var1, int var2, int var3);

        public ApplyPredicate copy(int var1, int var2, int var3, int var4) throws PresetCopyException;

        public ApplyPredicate mirrorX(int var1) throws PresetMirrorException;

        public ApplyPredicate mirrorY(int var1) throws PresetMirrorException;

        public ApplyPredicate rotate(PresetRotation var1, int var2, int var3) throws PresetRotateException;
    }

    public static interface CustomApply {
        public UndoLogic applyToLevel(Level var1, int var2, int var3, GameBlackboard var4);

        public CustomApply copy(int var1, int var2, int var3, int var4) throws PresetCopyException;

        public CustomApply mirrorX(int var1) throws PresetMirrorException;

        public CustomApply mirrorY(int var1) throws PresetMirrorException;

        public CustomApply rotate(PresetRotation var1, int var2, int var3) throws PresetRotateException;
    }

    @FunctionalInterface
    public static interface UndoLogic {
        public void applyUndo(Level var1, int var2, int var3);
    }

    @FunctionalInterface
    public static interface TileApplyListener {
        public void onTileApply(Level var1, int var2, int var3, int var4, GameBlackboard var5);
    }

    @FunctionalInterface
    public static interface WireApplyListener {
        public void onWireApply(Level var1, int var2, int var3, int var4, boolean var5, GameBlackboard var6);
    }

    @FunctionalInterface
    public static interface ObjectApplyListener {
        public void onObjectApply(Level var1, int var2, int var3, int var4, int var5, int var6, GameBlackboard var7);
    }

    public static class PresetLogicGate {
        public final int logicGateID;
        public final SaveData data;
        public final boolean mirrorX;
        public final boolean mirrorY;
        public final PresetRotation rotation;

        public PresetLogicGate(int logicGateID, SaveData data, boolean mirrorX, boolean mirrorY, PresetRotation rotation) {
            if (data != null && !data.toLoadData().getName().equals("data")) {
                throw new IllegalArgumentException("Data name must be \"data\"");
            }
            this.logicGateID = logicGateID;
            this.data = data;
            this.mirrorX = mirrorX;
            this.mirrorY = mirrorY;
            this.rotation = rotation;
        }

        public PresetLogicGate copy() {
            return new PresetLogicGate(this.logicGateID, this.data, this.mirrorX, this.mirrorY, this.rotation);
        }

        public void applyToLevel(Level level, int tileX, int tileY, GameBlackboard blackboard) {
            level.logicLayer.setLogicGate(tileX, tileY, this.logicGateID, null);
            LogicGateEntity entity = level.logicLayer.getEntity(tileX, tileY);
            if (entity != null) {
                entity.applyPresetLoadData(this.data.toLoadData(), this.mirrorX, this.mirrorY, this.rotation);
            }
        }
    }

    public static class PresetObjectEntity {
        public final SaveData data;
        public final boolean mirrorX;
        public final boolean mirrorY;
        public final PresetRotation rotation;

        public PresetObjectEntity(SaveData data, boolean mirrorX, boolean mirrorY, PresetRotation rotation) {
            if (data != null && !data.toLoadData().getName().equals("data")) {
                throw new IllegalArgumentException("Data name must be \"data\"");
            }
            this.data = data;
            this.mirrorX = mirrorX;
            this.mirrorY = mirrorY;
            this.rotation = rotation;
        }

        public void applyToLevel(Level level, int tileX, int tileY, GameBlackboard blackboard) {
            ObjectEntity oe = level.entityManager.getObjectEntity(tileX, tileY);
            if (oe != null) {
                oe.applyPresetLoadData(this.data.toLoadData(), this.mirrorX, this.mirrorY, this.rotation);
            }
        }
    }

    public static class ApplyTilePredicate
    implements ApplyPredicate {
        public final int tileX;
        public final int tileY;
        public final int dir;
        public final ApplyPredicateFunction applyFunction;
        public final boolean limitWithinBounds;

        public ApplyTilePredicate(int tileX, int tileY, int dir, ApplyPredicateFunction applyFunction, boolean limitWithinBounds) {
            Objects.requireNonNull(applyFunction);
            this.tileX = tileX;
            this.tileY = tileY;
            this.dir = dir;
            this.applyFunction = applyFunction;
            this.limitWithinBounds = limitWithinBounds;
        }

        @Override
        public boolean canApplyToLevel(Level level, int presetX, int presetY) {
            return this.applyFunction.canApplyToLevel(level, presetX + this.tileX, presetY + this.tileY, this.dir);
        }

        @Override
        public ApplyPredicate copy(int xOffset, int yOffset, int maxWidth, int maxHeight) throws PresetCopyException {
            int newX = this.tileX + xOffset;
            if (this.limitWithinBounds && (newX < 0 || newX >= maxWidth)) {
                return null;
            }
            int newY = this.tileY + yOffset;
            if (this.limitWithinBounds && (newY < 0 || newY >= maxHeight)) {
                return null;
            }
            return new ApplyTilePredicate(newX, newY, this.dir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public ApplyPredicate mirrorX(int width) {
            int newDir = this.dir == 1 || this.dir == 3 ? (this.dir + 2) % 4 : this.dir;
            return new ApplyTilePredicate(PresetUtils.getMirroredValue(this.tileX, width), this.tileY, newDir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public ApplyPredicate mirrorY(int height) {
            int newDir = this.dir == 0 || this.dir == 2 ? (this.dir + 2) % 4 : this.dir;
            return new ApplyTilePredicate(this.tileX, PresetUtils.getMirroredValue(this.tileY, height), newDir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public ApplyPredicate rotate(PresetRotation angle, int width, int height) {
            int newDir = (this.dir + (angle == null ? 0 : angle.dirOffset)) % 4;
            Point pos = PresetUtils.getRotatedPointInSpace(this.tileX, this.tileY, width, height, angle);
            return new ApplyTilePredicate(pos.x, pos.y, newDir, this.applyFunction, this.limitWithinBounds);
        }
    }

    @FunctionalInterface
    public static interface ApplyPredicateFunction {
        public boolean canApplyToLevel(Level var1, int var2, int var3, int var4);
    }

    public static class ApplyAreaPredicate
    implements ApplyPredicate {
        public final int startX;
        public final int startY;
        public final int endX;
        public final int endY;
        public final int dir;
        public final ApplyAreaPredicateFunction applyFunction;
        public final boolean limitWithinBounds;

        public ApplyAreaPredicate(int startX, int startY, int endX, int endY, int dir, ApplyAreaPredicateFunction applyFunction, boolean limitWithinBounds) {
            Objects.requireNonNull(applyFunction);
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.dir = dir;
            this.applyFunction = applyFunction;
            this.limitWithinBounds = limitWithinBounds;
        }

        public ApplyAreaPredicate(int startX, int startY, int endX, int endY, int dir, ApplyPredicateFunction applyFunction, boolean limitWithinBounds) {
            this(startX, startY, endX, endY, dir, (Level level, int levelStartX, int levelStartY, int levelEndX, int levelEndY, int levelDir) -> {
                for (int x = levelStartX; x <= levelEndX; ++x) {
                    for (int y = levelStartY; y <= levelEndY; ++y) {
                        if (applyFunction.canApplyToLevel(level, x, y, levelDir)) continue;
                        return false;
                    }
                }
                return true;
            }, limitWithinBounds);
            Objects.requireNonNull(applyFunction);
        }

        @Override
        public boolean canApplyToLevel(Level level, int presetX, int presetY) {
            int startX = presetX + Math.min(this.startX, this.endX);
            int endX = presetX + Math.max(this.startX, this.endX);
            int startY = presetY + Math.min(this.startY, this.endY);
            int endY = presetY + Math.max(this.startY, this.endY);
            return this.applyFunction.canApplyToLevel(level, startX, startY, endX, endY, this.dir);
        }

        @Override
        public ApplyPredicate copy(int xOffset, int yOffset, int maxWidth, int maxHeight) throws PresetCopyException {
            int newStartX = this.startX + xOffset;
            int newStartY = this.startY + yOffset;
            int newEndX = this.endX + xOffset;
            int newEndY = this.endY + yOffset;
            if (this.limitWithinBounds) {
                newStartX = Math.max(newStartX, 0);
                newStartY = Math.max(newStartY, 0);
                newEndX = Math.min(newEndX, maxWidth - 1);
                newEndY = Math.min(newEndY, maxHeight - 1);
                if (newStartX == newEndX) {
                    return null;
                }
                if (newStartY == newEndY) {
                    return null;
                }
            }
            return new ApplyAreaPredicate(newStartX, newStartY, newEndX, newEndY, this.dir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public ApplyPredicate mirrorX(int width) {
            int newDir = this.dir == 1 || this.dir == 3 ? (this.dir + 2) % 4 : this.dir;
            return new ApplyAreaPredicate(PresetUtils.getMirroredValue(this.startX, width), this.startY, PresetUtils.getMirroredValue(this.endX, width), this.endY, newDir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public ApplyPredicate mirrorY(int height) {
            int newDir = this.dir == 0 || this.dir == 2 ? (this.dir + 2) % 4 : this.dir;
            return new ApplyAreaPredicate(this.startX, PresetUtils.getMirroredValue(this.startY, height), this.endX, PresetUtils.getMirroredValue(this.endY, height), newDir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public ApplyPredicate rotate(PresetRotation angle, int width, int height) {
            int newDir = (this.dir + (angle == null ? 0 : angle.dirOffset)) % 4;
            Point pos1 = PresetUtils.getRotatedPointInSpace(this.startX, this.startY, width, height, angle);
            Point pos2 = PresetUtils.getRotatedPointInSpace(this.endX, this.endY, width, height, angle);
            return new ApplyAreaPredicate(pos1.x, pos1.y, pos2.x, pos2.y, newDir, this.applyFunction, this.limitWithinBounds);
        }
    }

    @FunctionalInterface
    public static interface ApplyAreaPredicateFunction {
        public boolean canApplyToLevel(Level var1, int var2, int var3, int var4, int var5, int var6);
    }

    public static class CustomApplyTile
    implements CustomApply {
        public final int tileX;
        public final int tileY;
        public final int dir;
        public final CustomApplyFunction applyFunction;
        public final boolean limitWithinBounds;

        public CustomApplyTile(int tileX, int tileY, int dir, CustomApplyFunction applyFunction, boolean limitWithinBounds) {
            Objects.requireNonNull(applyFunction);
            this.tileX = tileX;
            this.tileY = tileY;
            this.dir = dir;
            this.applyFunction = applyFunction;
            this.limitWithinBounds = limitWithinBounds;
        }

        @Override
        public UndoLogic applyToLevel(Level level, int presetX, int presetY, GameBlackboard blackboard) {
            return this.applyFunction.applyToLevel(level, presetX + this.tileX, presetY + this.tileY, this.dir, blackboard);
        }

        @Override
        public CustomApply copy(int xOffset, int yOffset, int maxWidth, int maxHeight) throws PresetCopyException {
            int newX = this.tileX + xOffset;
            if (this.limitWithinBounds && (newX < 0 || newX >= maxWidth)) {
                return null;
            }
            int newY = this.tileY + yOffset;
            if (this.limitWithinBounds && (newY < 0 || newY >= maxHeight)) {
                return null;
            }
            return new CustomApplyTile(newX, newY, this.dir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public CustomApply mirrorX(int width) throws PresetMirrorException {
            int newDir = this.dir == 1 || this.dir == 3 ? (this.dir + 2) % 4 : this.dir;
            return new CustomApplyTile(PresetUtils.getMirroredValue(this.tileX, width), this.tileY, newDir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public CustomApply mirrorY(int height) throws PresetMirrorException {
            int newDir = this.dir == 0 || this.dir == 2 ? (this.dir + 2) % 4 : this.dir;
            return new CustomApplyTile(this.tileX, PresetUtils.getMirroredValue(this.tileY, height), newDir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public CustomApply rotate(PresetRotation angle, int width, int height) throws PresetRotateException {
            int newDir = (this.dir + (angle == null ? 0 : angle.dirOffset)) % 4;
            Point pos = PresetUtils.getRotatedPointInSpace(this.tileX, this.tileY, width, height, angle);
            return new CustomApplyTile(pos.x, pos.y, newDir, this.applyFunction, this.limitWithinBounds);
        }
    }

    @FunctionalInterface
    public static interface CustomApplyFunction {
        public UndoLogic applyToLevel(Level var1, int var2, int var3, int var4, GameBlackboard var5);
    }

    public static class CustomApplyArea
    implements CustomApply {
        public final int startX;
        public final int startY;
        public final int endX;
        public final int endY;
        public final int dir;
        public final CustomApplyAreaFunction applyFunction;
        public final boolean limitWithinBounds;

        public CustomApplyArea(int startX, int startY, int endX, int endY, int dir, CustomApplyAreaFunction applyFunction, boolean limitWithinBounds) {
            Objects.requireNonNull(applyFunction);
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.dir = dir;
            this.applyFunction = applyFunction;
            this.limitWithinBounds = limitWithinBounds;
        }

        public CustomApplyArea(int startX, int startY, int endX, int endY, int dir, CustomApplyFunction applyFunction, boolean limitWithinBounds) {
            this(startX, startY, endX, endY, dir, (Level level, int levelStartX, int levelStartY, int levelEndX, int levelEndY, int levelDir, GameBlackboard blackboard) -> {
                LinkedList<UndoLogic> undoLogics = new LinkedList<UndoLogic>();
                for (int x = levelStartX; x <= levelEndX; ++x) {
                    for (int y = levelStartY; y <= levelEndY; ++y) {
                        UndoLogic undoLogic = applyFunction.applyToLevel(level, x, y, levelDir, blackboard);
                        if (undoLogic == null) continue;
                        undoLogics.add(undoLogic);
                    }
                }
                return (level1, presetX, presetY) -> undoLogics.forEach(undoLogic -> undoLogic.applyUndo(level, presetX, presetY));
            }, limitWithinBounds);
            Objects.requireNonNull(applyFunction);
        }

        @Override
        public UndoLogic applyToLevel(Level level, int presetX, int presetY, GameBlackboard blackboard) {
            int startX = presetX + Math.min(this.startX, this.endX);
            int endX = presetX + Math.max(this.startX, this.endX);
            int startY = presetY + Math.min(this.startY, this.endY);
            int endY = presetY + Math.max(this.startY, this.endY);
            return this.applyFunction.applyToLevel(level, startX, startY, endX, endY, this.dir, blackboard);
        }

        @Override
        public CustomApply copy(int xOffset, int yOffset, int maxWidth, int maxHeight) throws PresetCopyException {
            int newStartX = this.startX + xOffset;
            int newStartY = this.startY + yOffset;
            int newEndX = this.endX + xOffset;
            int newEndY = this.endY + yOffset;
            if (this.limitWithinBounds) {
                newStartX = Math.max(newStartX, 0);
                newStartY = Math.max(newStartY, 0);
                newEndX = Math.min(newEndX, maxWidth - 1);
                newEndY = Math.min(newEndY, maxHeight - 1);
                if (newStartX == newEndX) {
                    return null;
                }
                if (newStartY == newEndY) {
                    return null;
                }
            }
            return new CustomApplyArea(newStartX, newStartY, newEndX, newEndY, this.dir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public CustomApply mirrorX(int width) throws PresetMirrorException {
            int newDir = this.dir == 1 || this.dir == 3 ? (this.dir + 2) % 4 : this.dir;
            return new CustomApplyArea(PresetUtils.getMirroredValue(this.startX, width), this.startY, PresetUtils.getMirroredValue(this.endX, width), this.endY, newDir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public CustomApply mirrorY(int height) throws PresetMirrorException {
            int newDir = this.dir == 0 || this.dir == 2 ? (this.dir + 2) % 4 : this.dir;
            return new CustomApplyArea(this.startX, PresetUtils.getMirroredValue(this.startY, height), this.endX, PresetUtils.getMirroredValue(this.endY, height), newDir, this.applyFunction, this.limitWithinBounds);
        }

        @Override
        public CustomApply rotate(PresetRotation angle, int width, int height) throws PresetRotateException {
            int newDir = (this.dir + (angle == null ? 0 : angle.dirOffset)) % 4;
            Point pos1 = PresetUtils.getRotatedPointInSpace(this.startX, this.startY, width, height, angle);
            Point pos2 = PresetUtils.getRotatedPointInSpace(this.endX, this.endY, width, height, angle);
            return new CustomApplyArea(pos1.x, pos1.y, pos2.x, pos2.y, newDir, this.applyFunction, this.limitWithinBounds);
        }
    }

    @FunctionalInterface
    public static interface CustomApplyAreaFunction {
        public UndoLogic applyToLevel(Level var1, int var2, int var3, int var4, int var5, int var6, GameBlackboard var7);
    }

    public static class ORApplyPredicate
    implements ApplyPredicate {
        public final ArrayList<ApplyPredicate> predicates;

        public ORApplyPredicate(List<ApplyPredicate> applyPredicates) {
            this.predicates = new ArrayList<ApplyPredicate>(applyPredicates);
        }

        public ORApplyPredicate(ApplyPredicate ... predicates) {
            this(Arrays.asList(predicates));
        }

        @Override
        public boolean canApplyToLevel(Level level, int presetX, int presetY) {
            for (ApplyPredicate predicate : this.predicates) {
                if (!predicate.canApplyToLevel(level, presetX, presetY)) continue;
                return true;
            }
            return false;
        }

        @Override
        public ApplyPredicate copy(int xOffset, int yOffset, int maxWidth, int maxHeight) throws PresetCopyException {
            ArrayList<ApplyPredicate> copyList = new ArrayList<ApplyPredicate>(this.predicates.size());
            for (ApplyPredicate predicate : this.predicates) {
                ApplyPredicate copy = predicate.copy(xOffset, yOffset, maxWidth, maxHeight);
                if (copy == null) continue;
                copyList.add(copy);
            }
            if (copyList.isEmpty()) {
                return null;
            }
            return new ORApplyPredicate(copyList);
        }

        @Override
        public ApplyPredicate mirrorX(int width) throws PresetMirrorException {
            ArrayList<ApplyPredicate> nextPredicates = new ArrayList<ApplyPredicate>(this.predicates.size());
            for (ApplyPredicate predicate : this.predicates) {
                nextPredicates.add(predicate.mirrorX(width));
            }
            return new ORApplyPredicate(nextPredicates);
        }

        @Override
        public ApplyPredicate mirrorY(int height) throws PresetMirrorException {
            ArrayList<ApplyPredicate> nextPredicates = new ArrayList<ApplyPredicate>(this.predicates.size());
            for (ApplyPredicate predicate : this.predicates) {
                nextPredicates.add(predicate.mirrorY(height));
            }
            return new ORApplyPredicate(nextPredicates);
        }

        @Override
        public ApplyPredicate rotate(PresetRotation angle, int width, int height) throws PresetRotateException {
            ArrayList<ApplyPredicate> nextPredicates = new ArrayList<ApplyPredicate>(this.predicates.size());
            for (ApplyPredicate predicate : this.predicates) {
                nextPredicates.add(predicate.rotate(angle, width, height));
            }
            return new ORApplyPredicate(nextPredicates);
        }
    }
}

