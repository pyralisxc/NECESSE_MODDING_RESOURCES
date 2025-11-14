/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.data;

import java.awt.Point;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.SettlerRegistry;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementSettlerData {
    public final Settler settler;
    public final int mobUniqueID;
    public final Point bedPosition;

    public SettlementSettlerData(LevelSettler settler) {
        this.settler = settler.settler;
        this.mobUniqueID = settler.mobUniqueID;
        SettlementBed bed = settler.getBed();
        this.bedPosition = bed == null ? null : new Point(bed.tileX, bed.tileY);
    }

    public SettlementSettlerData(PacketReader reader) {
        short settlerNetworkID = reader.getNextShort();
        this.settler = SettlerRegistry.getSettler(settlerNetworkID);
        this.mobUniqueID = reader.getNextInt();
        if (reader.getNextBoolean()) {
            int bedTileX = reader.getNextInt();
            int bedTileY = reader.getNextInt();
            this.bedPosition = new Point(bedTileX, bedTileY);
        } else {
            this.bedPosition = null;
        }
    }

    public void writeContentPacket(PacketWriter writer) {
        writer.putNextShort((short)this.settler.getID());
        writer.putNextInt(this.mobUniqueID);
        writer.putNextBoolean(this.bedPosition != null);
        if (this.bedPosition != null) {
            writer.putNextInt(this.bedPosition.x);
            writer.putNextInt(this.bedPosition.y);
        }
    }

    public SettlerMob getSettlerMob(Level level) {
        Mob mob = level.entityManager.mobs.get(this.mobUniqueID, false);
        if (mob instanceof SettlerMob) {
            return (SettlerMob)((Object)mob);
        }
        return null;
    }
}

