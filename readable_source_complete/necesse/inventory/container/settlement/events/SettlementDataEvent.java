/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementDataEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int tileX;
    public final int tileY;
    public final int flagTier;
    public final long disbandTime;
    public final boolean isPrivate;
    public final GameMessage settlementName;
    public final long ownerAuth;
    public final String ownerName;
    public final int teamID;
    public final boolean isTeamPublic;

    public SettlementDataEvent(ServerSettlementData data) {
        PlayerTeam team;
        this.settlementUniqueID = data.uniqueID;
        this.tileX = data.networkData.getTileX();
        this.tileY = data.networkData.getTileY();
        this.flagTier = data.networkData.getFlagTier();
        this.disbandTime = data.networkData.getDisbandTime();
        this.isPrivate = data.networkData.isPrivate();
        this.settlementName = data.networkData.getSettlementName();
        this.ownerAuth = data.networkData.getOwnerAuth();
        this.ownerName = data.networkData.getOwnerName();
        this.teamID = data.networkData.getTeamID();
        this.isTeamPublic = this.teamID != -1 && data.isServer() ? (team = data.getLevel().getServer().world.getTeams().getTeam(this.teamID)) != null && team.isPublic() : false;
    }

    public SettlementDataEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.flagTier = reader.getNextShortUnsigned();
        this.disbandTime = reader.getNextLong();
        this.settlementName = GameMessage.fromContentPacket(reader.getNextContentPacket());
        this.isPrivate = reader.getNextBoolean();
        this.ownerAuth = reader.getNextLong();
        this.ownerName = reader.getNextString();
        this.teamID = reader.getNextInt();
        this.isTeamPublic = reader.getNextBoolean();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextShortUnsigned(this.flagTier);
        writer.putNextLong(this.disbandTime);
        writer.putNextContentPacket(this.settlementName.getContentPacket());
        writer.putNextBoolean(this.isPrivate);
        writer.putNextLong(this.ownerAuth);
        writer.putNextString(this.ownerName);
        writer.putNextInt(this.teamID);
        writer.putNextBoolean(this.isTeamPublic);
    }
}

