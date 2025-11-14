/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.data;

import java.awt.Rectangle;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.Zoning;
import necesse.engine.util.ZoningChange;
import necesse.level.maps.levelData.settlementData.RestrictZone;

public class SettlementRestrictZoneData {
    public final int uniqueID;
    public int index;
    public int colorHue;
    public GameMessage name;
    private final ZoningChange fullZonePacket;
    private Zoning zoning;

    public SettlementRestrictZoneData(RestrictZone zone) {
        this.uniqueID = zone.uniqueID;
        this.index = zone.index;
        this.colorHue = zone.colorHue;
        this.name = zone.name;
        this.fullZonePacket = zone.getFullChange();
    }

    public SettlementRestrictZoneData(PacketReader reader) {
        this.uniqueID = reader.getNextInt();
        this.index = reader.getNextShortUnsigned();
        this.colorHue = reader.getNextMaxValue(360);
        this.name = GameMessage.fromPacket(reader);
        this.fullZonePacket = ZoningChange.fromPacket(reader);
    }

    public void writeContentPacket(PacketWriter writer) {
        writer.putNextInt(this.uniqueID);
        writer.putNextShortUnsigned(this.index);
        writer.putNextMaxValue(this.colorHue, 360);
        this.name.writePacket(writer);
        this.fullZonePacket.write(writer);
    }

    public Zoning getZoning(final Supplier<Rectangle> settlementTileRectangleGetter) {
        if (this.zoning == null) {
            this.zoning = new Zoning(){

                @Override
                public Rectangle getLimits() {
                    return (Rectangle)settlementTileRectangleGetter.get();
                }
            };
            this.fullZonePacket.applyTo(this.zoning);
        }
        return this.zoning;
    }
}

