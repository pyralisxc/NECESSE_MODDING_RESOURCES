/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import java.util.ArrayList;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.TeleportResult;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.customAction.IntBooleanCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.object.HomestoneUpdateEvent;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.Waystone;

public class HomestoneContainer
extends SettlementDependantContainer {
    public final LevelObject levelObject;
    public int maxWaystones;
    public ArrayList<Waystone> waystones;
    public IntCustomAction useWaystone;
    public IntBooleanCustomAction moveWaystoneUp;
    public IntBooleanCustomAction moveWaystoneDown;
    public WaystoneRenameCustomAction renameWaystone;

    public HomestoneContainer(final NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, LevelObject levelObject, Packet content) {
        super(client, uniqueSeed, settlement, false);
        this.levelObject = levelObject;
        this.update(content);
        this.subscribeEvent(HomestoneUpdateEvent.class, e -> true, () -> true);
        this.onEvent(HomestoneUpdateEvent.class, (T e) -> this.update(e.content));
        this.useWaystone = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int index) {
                if (client.isServer()) {
                    ServerClient serverClient = client.getServerClient();
                    Server server = serverClient.getServer();
                    Level level = serverClient.getLevel();
                    ServerSettlementData settlement = HomestoneContainer.this.getServerData();
                    if (settlement != null) {
                        ArrayList<Waystone> waystones = settlement.getWaystones();
                        if (index >= 0 && index < waystones.size()) {
                            Waystone waystone = waystones.get(index);
                            level.entityManager.events.addHidden(new TeleportEvent(serverClient, 0, waystone.destination, 0.0f, null, newLevel -> {
                                if (waystone.checkIsValid(server, settlement.uniqueID)) {
                                    serverClient.closeContainer(true);
                                    serverClient.newStats.homestones_used.increment(1);
                                    return new TeleportResult(true, waystone.findTeleportLocation(server, client2.playerMob));
                                }
                                waystones.remove(waystone);
                                serverClient.sendChatMessage(new LocalMessage("ui", "waystoneinvalid"));
                                settlement.sendEvent(HomestoneUpdateEvent.class);
                                return new TeleportResult(false, null);
                            }));
                        } else {
                            new HomestoneUpdateEvent(settlement).applyAndSendToClient(serverClient);
                        }
                    }
                }
            }
        });
        this.moveWaystoneUp = this.registerAction(new IntBooleanCustomAction(){

            @Override
            protected void run(int index, boolean shiftDown) {
                if (index <= 0 || index > HomestoneContainer.this.waystones.size() - 1) {
                    return;
                }
                Waystone old = HomestoneContainer.this.waystones.remove(index);
                if (shiftDown) {
                    HomestoneContainer.this.waystones.add(0, old);
                } else {
                    HomestoneContainer.this.waystones.add(index - 1, old);
                }
                if (client.isServer()) {
                    ServerClient serverClient = client.getServerClient();
                    Level level = serverClient.getLevel();
                    ServerSettlementData settlement = HomestoneContainer.this.getServerData();
                    if (settlement != null) {
                        ArrayList<Waystone> waystones = settlement.getWaystones();
                        if (index > waystones.size() - 1) {
                            return;
                        }
                        Waystone oldServer = waystones.remove(index);
                        if (shiftDown) {
                            waystones.add(0, oldServer);
                        } else {
                            waystones.add(index - 1, oldServer);
                        }
                        settlement.sendEvent(HomestoneUpdateEvent.class);
                    }
                }
            }
        });
        this.moveWaystoneDown = this.registerAction(new IntBooleanCustomAction(){

            @Override
            protected void run(int index, boolean shiftDown) {
                ServerSettlementData settlement;
                if (index < 0 || index > HomestoneContainer.this.waystones.size() - 2) {
                    return;
                }
                Waystone old = HomestoneContainer.this.waystones.remove(index);
                if (shiftDown) {
                    HomestoneContainer.this.waystones.add(old);
                } else {
                    HomestoneContainer.this.waystones.add(index + 1, old);
                }
                if (client.isServer() && (settlement = HomestoneContainer.this.getServerData()) != null) {
                    ArrayList<Waystone> waystones = settlement.getWaystones();
                    if (index > waystones.size() - 2) {
                        return;
                    }
                    Waystone oldServer = waystones.remove(index);
                    if (shiftDown) {
                        waystones.add(oldServer);
                    } else {
                        waystones.add(index + 1, oldServer);
                    }
                    settlement.sendEvent(HomestoneUpdateEvent.class);
                }
            }
        });
        this.renameWaystone = this.registerAction(new WaystoneRenameCustomAction());
    }

    public void update(Packet content) {
        PacketReader reader = new PacketReader(content);
        this.maxWaystones = reader.getNextShortUnsigned();
        int count = reader.getNextShortUnsigned();
        this.waystones = new ArrayList(count);
        for (int i = 0; i < count; ++i) {
            this.waystones.add(new Waystone(reader));
        }
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        return !this.levelObject.hasChanged() && this.levelObject.isInInteractRange(client.playerMob);
    }

    public static Packet getContainerContent(ServerSettlementData settlement) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextShortUnsigned(settlement.getMaxWaystones());
        ArrayList<Waystone> waystones = settlement.getWaystones();
        writer.putNextShortUnsigned(waystones.size());
        for (Waystone waystone : waystones) {
            waystone.writePacket(writer);
        }
        return packet;
    }

    public class WaystoneRenameCustomAction
    extends ContainerCustomAction {
        protected WaystoneRenameCustomAction() {
        }

        public void runAndSend(int index, String name) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(index);
            writer.putNextString(name);
            this.runAndSendAction(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int index = reader.getNextInt();
            String name = reader.getNextString();
            if (HomestoneContainer.this.client.isServer()) {
                ServerClient serverClient = HomestoneContainer.this.client.getServerClient();
                ServerSettlementData settlement = HomestoneContainer.this.getServerData();
                if (settlement != null) {
                    ArrayList<Waystone> waystones = settlement.getWaystones();
                    if (index >= 0 && index < waystones.size()) {
                        Waystone waystone = waystones.get(index);
                        waystone.name = name;
                        settlement.sendEvent(HomestoneUpdateEvent.class);
                    } else {
                        new HomestoneUpdateEvent(settlement).applyAndSendToClient(serverClient);
                    }
                }
            }
        }
    }
}

