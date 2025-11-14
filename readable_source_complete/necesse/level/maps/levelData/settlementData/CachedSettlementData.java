/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Rectangle;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBoundsManager;

public class CachedSettlementData {
    public final LevelIdentifier levelIdentifier;
    public final int uniqueID;
    private int tileX;
    private int tileY;
    private int flagTier;
    private GameMessage name;
    private long ownerAuth;
    private int teamID;
    private NetworkSettlementData loadedData;

    public CachedSettlementData(LevelIdentifier levelIdentifier, int uniqueID, int tileX, int tileY, int flagTier, GameMessage name, long ownerAuth, int teamID) {
        this.levelIdentifier = levelIdentifier;
        this.uniqueID = uniqueID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.flagTier = flagTier;
        this.name = name;
        this.ownerAuth = ownerAuth;
        this.teamID = teamID;
    }

    public CachedSettlementData(LevelIdentifier levelIdentifier, int uniqueID, LoadData save) {
        this.levelIdentifier = levelIdentifier;
        this.uniqueID = uniqueID;
        this.loadSaveData(save);
    }

    public void setLoadedData(NetworkSettlementData networkData) {
        if (this.loadedData != null) {
            throw new IllegalStateException("Loaded data already set for settlement " + this.uniqueID);
        }
        this.loadedData = networkData;
    }

    public void addSaveData(SaveData save) {
        save.addInt("tileX", this.tileX);
        save.addInt("tileY", this.tileY);
        save.addInt("flagTier", this.flagTier);
        if (this.name != null) {
            save.addSaveData(this.name.getSaveData("name"));
        }
        save.addLong("ownerAuth", this.ownerAuth);
        save.addInt("teamID", this.teamID);
    }

    public void loadSaveData(LoadData save) {
        this.tileX = save.getInt("tileX", Integer.MIN_VALUE, false);
        if (this.tileX == Integer.MIN_VALUE) {
            throw new LoadDataException("Could not find tileX");
        }
        this.tileY = save.getInt("tileY", Integer.MIN_VALUE, false);
        if (this.tileY == Integer.MIN_VALUE) {
            throw new LoadDataException("Could not find tileY");
        }
        this.flagTier = save.getInt("flagTier", this.flagTier, false);
        LoadData data = save.getFirstLoadDataByName("name");
        this.name = data != null ? GameMessage.loadSave(data) : new StaticMessage("Unknown settlement");
        this.ownerAuth = save.getLong("ownerAuth", -1L, false);
        this.teamID = save.getInt("teamID", -1, false);
    }

    public int getTileX() {
        return this.tileX;
    }

    public int getTileY() {
        return this.tileY;
    }

    public int getFlagTier() {
        return this.flagTier;
    }

    public GameMessage getName() {
        return this.name;
    }

    public long getOwnerAuth() {
        return this.ownerAuth;
    }

    public int getTeamID() {
        return this.teamID;
    }

    public boolean hasAccess(ServerClient client) {
        return this.ownerAuth == client.authentication || client.isSameTeam(this.teamID);
    }

    public void updateOwnerTeamID(int teamID) {
        this.teamID = teamID;
    }

    public boolean isLoaded() {
        return this.loadedData != null;
    }

    public NetworkSettlementData getLoadedData() {
        return this.loadedData;
    }

    public boolean isTileWithinBounds(int tileX, int tileY) {
        return SettlementBoundsManager.isTileWithinBounds(tileX, tileY, this.tileX, this.tileY, this.flagTier);
    }

    public boolean isTileWithinLoadedRegionBounds(int tileX, int tileY) {
        return SettlementBoundsManager.isTileWithinLoadedRegionBounds(tileX, tileY, this.tileX, this.tileY, this.flagTier);
    }

    public Rectangle getRegionRectangle() {
        return SettlementBoundsManager.getRegionRectangleFromTier(this.tileX, this.tileY, this.flagTier);
    }
}

