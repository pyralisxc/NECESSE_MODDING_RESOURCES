/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Rectangle;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.Zoning;
import necesse.engine.util.ZoningChange;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class RestrictZone {
    public final ServerSettlementData data;
    public final int uniqueID;
    public int index;
    public GameMessage name = new LocalMessage("ui", "settlementareadefname", "number", 0);
    public int colorHue = 120;
    private final Zoning zoning;

    public RestrictZone(final ServerSettlementData data, int uniqueID, int index, GameMessage name) {
        this.data = data;
        this.uniqueID = uniqueID;
        this.index = index;
        this.name = name;
        this.zoning = new Zoning(){

            @Override
            public Rectangle getLimits() {
                return data.boundsManager.getTileRectangle();
            }
        };
    }

    public RestrictZone(final ServerSettlementData data, int index, LoadData save, int tileXOffset, int tileYOffset) {
        this.data = data;
        this.index = index;
        try {
            this.uniqueID = save.getInt("uniqueID");
        }
        catch (Exception e) {
            throw new LoadDataException("Could not load restrict zone uniqueID");
        }
        LoadData nameSave = save.getFirstLoadDataByName("name");
        if (nameSave != null) {
            this.name = GameMessage.loadSave(nameSave);
        }
        this.colorHue = save.getInt("color", this.colorHue);
        this.zoning = new Zoning(){

            @Override
            public Rectangle getLimits() {
                return data.boundsManager.getTileRectangle();
            }
        };
        this.zoning.applyZoneSaveData("zone", save, tileXOffset, tileYOffset);
    }

    public void addSaveData(SaveData save) {
        save.addInt("uniqueID", this.uniqueID);
        save.addSaveData(this.name.getSaveData("name"));
        save.addInt("color", this.colorHue);
        this.zoning.addZoneSaveData("zone", save);
    }

    public ZoningChange getFullChange() {
        return ZoningChange.full(this.zoning);
    }

    public boolean isEmpty() {
        return this.zoning.isEmpty();
    }

    public boolean containsTile(int tileX, int tileY) {
        return this.zoning.containsTile(tileX, tileY);
    }

    public boolean applyChange(ZoningChange change) {
        return change.applyTo(this.zoning);
    }

    public boolean expand(Rectangle rectangle) {
        return this.zoning.addRectangle(rectangle);
    }

    public boolean shrink(Rectangle rectangle) {
        return this.zoning.removeRectangle(rectangle);
    }

    public void copyZoneFrom(RestrictZone other) {
        this.zoning.addRectangles(other.zoning.getTileRectangles());
    }

    public boolean limitZoneToBounds(Rectangle tileRectangle) {
        return this.zoning.limitZoneToRectangle(tileRectangle);
    }

    public Iterable<Rectangle> getTileRectangles() {
        return this.zoning.getTileRectangles();
    }
}

