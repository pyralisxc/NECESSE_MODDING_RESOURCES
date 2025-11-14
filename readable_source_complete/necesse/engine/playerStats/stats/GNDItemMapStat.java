/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats.stats;

import java.util.ArrayList;
import java.util.HashSet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.registries.GNDRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemMapStat
extends GameStat {
    protected GNDItemMap data;
    protected HashSet<Integer> clearKeys;
    protected HashSet<Integer> dirtyClearKeys;
    protected boolean isImportant;

    public GNDItemMapStat(EmptyStats parent, String stringID, boolean isImportant) {
        super(parent, stringID);
        this.isImportant = isImportant;
        this.clearKeys = new HashSet();
        this.dirtyClearKeys = new HashSet();
    }

    public GNDItemMap getData() {
        if (this.data == null) {
            this.data = new GNDItemMap(){

                @Override
                protected void initIDData() {
                    GNDRegistry.applyIDData(this, GNDItemMap.class);
                }

                @Override
                public void markDirty(int hash) {
                    super.markDirty(hash);
                    if (GNDItemMapStat.this.isImportant) {
                        GNDItemMapStat.this.markImportantDirty();
                    } else {
                        GNDItemMapStat.this.markDirty();
                    }
                }

                @Override
                public void markDirty(String key) {
                    super.markDirty(key);
                    if (GNDItemMapStat.this.isImportant) {
                        GNDItemMapStat.this.markImportantDirty();
                    } else {
                        GNDItemMapStat.this.markDirty();
                    }
                }
            };
        }
        return this.data;
    }

    @Override
    public void clean() {
        super.clean();
        if (this.data != null) {
            this.data.cleanAll();
        }
        this.dirtyClearKeys.clear();
    }

    public void clearKey(String key) {
        int keyHash = GNDItemMap.getKeyHash(key);
        if (this.clearKeys.contains(keyHash)) {
            return;
        }
        this.clearKeys.add(keyHash);
        this.dirtyClearKeys.add(keyHash);
        if (this.isImportant) {
            this.markImportantDirty();
        } else {
            this.markDirty();
        }
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof GNDItemMapStat) {
            GNDItemMapStat other = (GNDItemMapStat)stat;
            if (other.data == null) {
                return;
            }
            this.getData().addAll(other.getData());
            for (int key : other.clearKeys) {
                this.getData().clearItem(key);
            }
        }
    }

    @Override
    public void resetCombine() {
        if (this.data != null) {
            this.data.clearAll();
        }
        this.clearKeys.clear();
        this.dirtyClearKeys.clear();
    }

    protected void updatePlatform() {
        if (!this.parent.controlAchievements) {
            // empty if block
        }
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
    }

    @Override
    public void addSaveData(SaveData save) {
        if (this.data != null && !this.data.isEmpty()) {
            SaveData gndData = new SaveData("gndData");
            this.data.addSaveData(gndData);
            save.addSaveData(gndData);
        }
        if (!this.clearKeys.isEmpty()) {
            save.addIntCollection("clearKeys", this.clearKeys);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        LoadData gndData = save.getFirstLoadDataByName("gndData");
        if (gndData != null) {
            this.getData().applyLoadData(gndData);
        }
        this.clearKeys = new HashSet<Integer>(save.getIntCollection("clearKeys", new ArrayList<Integer>(), false));
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        this.getData().writePacket(writer);
        writer.putNextShortUnsigned(this.clearKeys.size());
        for (int key : this.clearKeys) {
            writer.putNextInt(key);
        }
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.getData().readPacket(reader);
        int clearKeysSize = reader.getNextShortUnsigned();
        for (int i = 0; i < clearKeysSize; ++i) {
            this.getData().clearItem(reader.getNextInt());
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        this.getData().writeDirtyPacket(writer);
        writer.putNextShortUnsigned(this.dirtyClearKeys.size());
        for (int key : this.dirtyClearKeys) {
            writer.putNextInt(key);
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        this.getData().readDirtyPacket(reader);
        int clearKeysSize = reader.getNextShortUnsigned();
        for (int i = 0; i < clearKeysSize; ++i) {
            this.getData().clearItem(reader.getNextInt());
        }
    }
}

