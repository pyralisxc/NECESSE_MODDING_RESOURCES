/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldData.incursions;

import java.util.Objects;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.incursion.IncursionData;

public class OpenIncursion {
    public final LevelIdentifier incursionLevelIdentifier;
    public final IncursionData incursionData;
    public boolean canComplete;

    public OpenIncursion(LevelIdentifier incursionLevelIdentifier, IncursionData incursionData) {
        Objects.requireNonNull(incursionLevelIdentifier);
        Objects.requireNonNull(incursionData);
        this.incursionLevelIdentifier = incursionLevelIdentifier;
        this.incursionData = incursionData;
    }

    public OpenIncursion(LoadData save) {
        String incursionLevelIdentifierSave = save.getUnsafeString("incursionLevel", null, false);
        if (incursionLevelIdentifierSave == null) {
            throw new LoadDataException("Open incursion did not have level identifier");
        }
        this.incursionLevelIdentifier = new LevelIdentifier(incursionLevelIdentifierSave);
        LoadData incursionSave = save.getFirstLoadDataByName("incursionData");
        this.incursionData = IncursionData.fromLoadData(incursionSave);
        this.canComplete = save.getBoolean("canComplete", this.canComplete, false);
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("incursionLevel", this.incursionLevelIdentifier.stringID);
        SaveData incursionDataSave = new SaveData("incursionData");
        this.incursionData.addSaveData(incursionDataSave);
        save.addSaveData(incursionDataSave);
        save.addBoolean("canComplete", this.canComplete);
    }

    public OpenIncursion(PacketReader reader) {
        this.incursionLevelIdentifier = new LevelIdentifier(reader);
        this.incursionData = IncursionData.fromPacket(reader);
        this.canComplete = reader.getNextBoolean();
    }

    public void writePacket(PacketWriter writer) {
        this.incursionLevelIdentifier.writePacket(writer);
        IncursionData.writePacket(this.incursionData, writer);
        writer.putNextBoolean(this.canComplete);
    }
}

