/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

import java.awt.Point;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.ReturnedObjects;
import necesse.engine.world.World;
import necesse.engine.world.WorldFile;
import necesse.entity.manager.WorldLevelUnloadedEntityComponent;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class LevelManager {
    private final World world;
    private final HashMap<LevelIdentifier, Level> loadedLevels;

    public LevelManager(World world) {
        this.world = world;
        this.loadedLevels = new HashMap();
    }

    public void serverTick() {
        for (Level level : this.loadedLevels.values().toArray(new Level[0])) {
            if (!this.loadedLevels.containsKey(level.getIdentifier())) continue;
            level.serverTick();
            LinkedList<Region> regionsToUnload = level.regionManager.tickUnloadRegions(Math.max(Settings.unloadLevelsCooldown, 2) + 1);
            for (Region region : regionsToUnload) {
                level.regionManager.unloadRegion(region);
            }
        }
    }

    public void frameTick(TickManager tickManager) {
        for (Level level : this.loadedLevels.values().toArray(new Level[0])) {
            if (!this.loadedLevels.containsKey(level.getIdentifier())) continue;
            level.frameTick(tickManager);
        }
    }

    public Level getLevel(LevelIdentifier identifier) {
        return this.loadedLevels.get(identifier);
    }

    public void overwriteLevel(Level level) {
        LevelIdentifier identifier = level.getIdentifier();
        Level existingLevel = this.getLevel(identifier);
        if (existingLevel == level) {
            return;
        }
        this.unloadLevel(level);
        if (!level.isLoadingComplete()) {
            level.onLoadingComplete();
        }
        this.loadedLevels.put(identifier, level);
        this.world.saveLevel(level);
    }

    public void deleteLevel(LevelIdentifier identifier, ReturnedObjects returnedObjects) {
        this.deleteLevel(identifier, returnedObjects, new HashSet<LevelIdentifier>());
    }

    private void deleteLevel(LevelIdentifier identifier, ReturnedObjects returnedObjects, HashSet<LevelIdentifier> deleted) {
        Level fallbackLevel;
        if (deleted.contains(identifier)) {
            return;
        }
        deleted.add(identifier);
        if (this.world.server != null) {
            this.world.server.streamClients().filter(c -> c.getLevelIdentifier().equals(identifier)).forEach(c -> c.changeToFallbackLevel(identifier, true));
        }
        Level loadedLevel = this.getLevel(identifier);
        LevelIdentifier fallbackIdentifier = null;
        Point fallbackTilePos = null;
        if (loadedLevel == null) {
            loadedLevel = this.world.loadLevel(identifier);
        }
        if (loadedLevel != null && returnedObjects == null) {
            returnedObjects = new ReturnedObjects();
            if (loadedLevel.fallbackIdentifier == null) {
                returnedObjects = null;
            } else {
                fallbackIdentifier = loadedLevel.fallbackIdentifier;
                fallbackTilePos = loadedLevel.fallbackTilePos;
                if (fallbackTilePos == null) {
                    fallbackTilePos = new Point(loadedLevel.tileWidth / 2, loadedLevel.tileHeight / 2);
                }
            }
        }
        if (loadedLevel != null) {
            if (returnedObjects != null) {
                loadedLevel.returnedItemsManager.addAllReturnedItems(returnedObjects);
            }
            for (LevelIdentifier childLevel : loadedLevel.childLevels) {
                this.deleteLevel(childLevel, returnedObjects, deleted);
            }
        } else {
            LoadData levelSave = this.world.loadLevelScript(identifier);
            if (levelSave != null) {
                HashSet<LevelIdentifier> childLevels = LevelSave.getChildLevels(levelSave);
                for (LevelIdentifier childLevel : childLevels) {
                    this.deleteLevel(childLevel, returnedObjects, deleted);
                }
            }
        }
        if (returnedObjects != null && fallbackIdentifier != null && (fallbackLevel = this.world.getLevel(fallbackIdentifier)) != null) {
            returnedObjects.returnObjectsToTile(fallbackLevel, fallbackTilePos.x, fallbackTilePos.y);
        }
        this.unloadLevel(identifier);
        WorldFile levelFile = this.world.fileSystem.getLevelFile(identifier);
        WorldFile regionsFolder = this.world.fileSystem.getLevelRegionsFolder(identifier);
        try {
            levelFile.delete();
            regionsFolder.delete(true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean loadLevel(WorldFile file, LevelIdentifier identifier) {
        Level level = this.world.loadLevel(file);
        if (level == null) {
            return false;
        }
        if (!level.getIdentifier().equals(identifier)) {
            LevelIdentifier before = level.getIdentifier();
            level.overwriteIdentifier(identifier);
            System.out.println("Fixed level identifier from " + before + " to " + identifier);
        }
        this.loadedLevels.put(identifier, level);
        return true;
    }

    public boolean loadLevel(LevelIdentifier identifier) {
        return this.isLoaded(identifier) || this.loadLevel(this.world.fileSystem.getLevelFile(identifier), identifier);
    }

    public void unloadLevel(LevelIdentifier identifier) {
        Level level = this.loadedLevels.get(identifier);
        if (level != null) {
            level.onUnloading();
            level.dispose();
            this.loadedLevels.remove(identifier);
            this.world.worldEntity.dataComponentManager.streamAll(WorldLevelUnloadedEntityComponent.class).forEach(component -> component.onLevelUnloaded(identifier));
        }
    }

    public void unloadLevel(Level level) {
        this.unloadLevel(level.getIdentifier());
    }

    public int getLoadedLevelsNum() {
        return this.loadedLevels.size();
    }

    public Collection<Level> getLoadedLevels() {
        return this.loadedLevels.values();
    }

    public boolean isLoaded(LevelIdentifier identifier) {
        return this.loadedLevels.containsKey(identifier);
    }

    public void dispose() {
        this.loadedLevels.values().forEach(Level::dispose);
    }
}

