/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.levelEvent.SmokePuffLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementMoveErrorEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class MoveSettlerSettlementAction
extends ContainerCustomAction {
    public final SettlementContainer container;

    public MoveSettlerSettlementAction(SettlementContainer container) {
        this.container = container;
    }

    public void runAndSend(int mobUniqueID, int settlementUniqueID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mobUniqueID);
        writer.putNextInt(settlementUniqueID);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        ServerSettlementData serverData;
        int mobUniqueID = reader.getNextInt();
        int settlementUniqueID = reader.getNextInt();
        if (this.container.client.isServer() && (serverData = this.container.getServerData()) != null) {
            ServerClient client = this.container.client.getServerClient();
            Server server = client.getServer();
            LocalMessage err = null;
            LevelSettler settler = serverData.getSettler(mobUniqueID);
            if (settler == null) {
                err = new LocalMessage("settlement", "notsettler");
            } else {
                SettlerMob settlerMob = settler.getMob();
                if (settlerMob == null) {
                    err = new LocalMessage("settlement", "notsettler");
                } else if (!settler.canMoveOut()) {
                    err = new LocalMessage("ui", "settlementnoperm");
                } else {
                    SettlementsWorldData worldData = SettlementsWorldData.getSettlementsData(server);
                    CachedSettlementData settlement = worldData.getCachedData(settlementUniqueID);
                    if (settlement == null || !SettlementContainer.hasAccess(serverData.networkData, settlement, client)) {
                        err = new LocalMessage("ui", "settlementnoperm");
                    } else {
                        Level otherLevel = server.world.getLevel(settlement.levelIdentifier);
                        ServerSettlementData otherData = worldData.getOrLoadServerData(settlementUniqueID);
                        if (otherData == null) {
                            err = new LocalMessage("ui", "settlementnotfound");
                        } else {
                            int totalSettlers = otherData.countTotalSettlers();
                            int maxSettlersPerSettlement = server.world.settings.maxSettlersPerSettlement;
                            if (maxSettlersPerSettlement >= 0 && totalSettlers >= maxSettlersPerSettlement) {
                                err = new LocalMessage("ui", "settlementmaxsettlers", "max", maxSettlersPerSettlement);
                            } else {
                                LevelSettler newSettler = new LevelSettler(otherData, settler.settler, settler.mobUniqueID, settler.settlerSeed);
                                if (!otherData.canMoveIn(newSettler, -1)) {
                                    err = new LocalMessage("ui", "settlementfull", "settlement", otherData.networkData.getSettlementName());
                                } else {
                                    serverData.removeSettler(mobUniqueID, null);
                                    Mob mob = settlerMob.getMob();
                                    Point spawnPos = Settler.getNewSettlerSpawnPos(mob, serverData);
                                    if (spawnPos == null) {
                                        spawnPos = SettlersWorldData.getReturnPos(mob, serverData);
                                    }
                                    serverData.getLevel().entityManager.events.add(new SmokePuffLevelEvent(mob.x, mob.y, 64, new Color(50, 50, 50)));
                                    if (!serverData.getLevel().isSamePlace(otherLevel)) {
                                        mob.setPos(spawnPos.x, spawnPos.y, true);
                                    } else {
                                        mob.getLevel().entityManager.changeMobLevel(mob, serverData.getLevel(), spawnPos.x, spawnPos.y, true);
                                    }
                                    otherData.moveIn(newSettler);
                                    serverData.sendEvent(SettlementSettlersChangedEvent.class);
                                    otherData.sendEvent(SettlementSettlersChangedEvent.class);
                                }
                            }
                        }
                    }
                }
            }
            if (err != null) {
                Packet content = new Packet();
                PacketWriter writer = new PacketWriter(content);
                writer.putNextContentPacket(err.getContentPacket());
                new SettlementMoveErrorEvent(err).applyAndSendToClient(client);
            }
        }
    }
}

