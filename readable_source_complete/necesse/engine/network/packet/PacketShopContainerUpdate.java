/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.journal.listeners.SettlerRecruitedJournalChallengeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.levelEvent.SmokePuffLevelEvent;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.inventory.container.Container;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementOpenSettlementListEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class PacketShopContainerUpdate
extends Packet {
    public final Type secondType;
    public final Packet content;

    public PacketShopContainerUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.secondType = Type.values()[reader.getNextByteUnsigned()];
        this.content = reader.getNextContentPacket();
    }

    private PacketShopContainerUpdate(Type secondType, Packet content) {
        this.secondType = secondType;
        this.content = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(secondType.ordinal());
        writer.putNextContentPacket(content);
    }

    public static PacketShopContainerUpdate settlementsList(Server server, ServerClient client, HumanMob mob) {
        List<CachedSettlementData> settlements = SettlementsWorldData.getSettlementsData(server).collectCachedSettlements(data -> data.hasAccess(client) && mob.isValidRecruitment((CachedSettlementData)data, client));
        return PacketShopContainerUpdate.settlementsList(mob, settlements);
    }

    public static PacketShopContainerUpdate settlementsList(HumanMob mob, List<CachedSettlementData> settlements) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mob.getUniqueID());
        writer.putNextShortUnsigned(settlements.size());
        for (CachedSettlementData settlement : settlements) {
            writer.putNextInt(settlement.uniqueID);
            writer.putNextContentPacket(settlement.getName().getContentPacket());
        }
        return new PacketShopContainerUpdate(Type.SETTLEMENTS_LIST, content);
    }

    public static PacketShopContainerUpdate recruitSettler(int mobUniqueID, int settlementUniqueID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mobUniqueID);
        writer.putNextInt(settlementUniqueID);
        return new PacketShopContainerUpdate(Type.RECRUIT_SETTLER, content);
    }

    public static PacketShopContainerUpdate recruitResponse(HumanMob mob, GameMessage err) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mob.getUniqueID());
        writer.putNextBoolean(err != null);
        if (err != null) {
            writer.putNextContentPacket(err.getContentPacket());
        }
        return new PacketShopContainerUpdate(Type.RECRUIT_RESPONSE, content);
    }

    public ShopContainer checkContainer(Container container) {
        if (container instanceof ShopContainer) {
            return (ShopContainer)container;
        }
        return null;
    }

    public static void recruitSettler(Server server, ServerClient client, ShopContainer container, Supplier<ServerSettlementData> settlementSupplier) {
        LocalMessage err = null;
        if (!container.canPayForRecruit()) {
            err = new LocalMessage("ui", "settlerrecruitnotpay");
        } else {
            ServerSettlementData serverData = settlementSupplier.get();
            if (serverData == null) {
                err = new LocalMessage("ui", "settlementnotfound");
            } else if (!serverData.networkData.doesClientHaveAccess(client)) {
                err = new LocalMessage("ui", "settlerrecruitnoperm");
            } else {
                Settler settler = container.humanShop.getSettler();
                if (settler == null) {
                    err = new LocalMessage("settlement", "notsettler");
                } else {
                    int totalSettlers = serverData.countTotalSettlers();
                    int maxSettlersPerSettlement = server.world.settings.maxSettlersPerSettlement;
                    if (maxSettlersPerSettlement >= 0 && totalSettlers >= maxSettlersPerSettlement) {
                        err = new LocalMessage("ui", "settlementmaxsettlers", "max", maxSettlersPerSettlement);
                    } else {
                        LevelSettler newSettler = new LevelSettler(serverData, settler, container.humanShop.getUniqueID(), container.humanShop.getSettlerSeed());
                        if (!serverData.canMoveIn(newSettler, -1)) {
                            err = new LocalMessage("ui", "settlementfull", "settlement", serverData.networkData.getSettlementName());
                        } else {
                            Level recruitLevel = container.humanShop.getLevel();
                            boolean isWithinSettlement = container.humanShop.isSettlerWithinSettlementLoadedRegions(serverData.networkData);
                            if (!isWithinSettlement && !container.humanShop.shouldTeleportToLevelOnRecruited(client, serverData, newSettler) && container.humanShop.willJoinAdventureParty(client) == null) {
                                SettlersWorldData settlersData = SettlersWorldData.getSettlersData(server);
                                settlersData.refreshWorldSettler(container.humanShop, true);
                                container.humanShop.commandFollow(client, client.playerMob);
                                container.humanShop.adventureParty.set(client);
                            } else if (!isWithinSettlement) {
                                Point spawnPos = Settler.getNewSettlerSpawnPos(container.humanShop, serverData);
                                if (spawnPos == null) {
                                    spawnPos = SettlersWorldData.getReturnPos(container.humanShop, serverData);
                                }
                                recruitLevel.entityManager.events.add(new SmokePuffLevelEvent(container.humanShop.x, container.humanShop.y, 64, new Color(50, 50, 50)));
                                if (!serverData.getLevel().isSamePlace(recruitLevel)) {
                                    recruitLevel.entityManager.changeMobLevel(container.humanShop, serverData.getLevel(), spawnPos.x, spawnPos.y, true);
                                } else {
                                    container.humanShop.setPos(spawnPos.x, spawnPos.y, true);
                                }
                            }
                            serverData.moveIn(newSettler);
                            container.payForRecruit();
                            container.humanShop.onRecruited(client, serverData, newSettler);
                            serverData.networkData.streamTeamMembers().forEach(teamClient -> JournalChallengeRegistry.handleListeners(teamClient, SettlerRecruitedJournalChallengeListener.class, challenge -> challenge.onSettlerRecruited((ServerClient)teamClient, client, recruitLevel, serverData, newSettler, container.humanShop)));
                            serverData.sendEvent(SettlementSettlersChangedEvent.class);
                            LocalMessage joinedMessage = new LocalMessage("ui", "settlementjoined", "settler", container.humanShop.getLocalization(), "settlement", serverData.networkData.getSettlementName());
                            serverData.networkData.streamTeamMembers().forEach(c -> c.sendChatMessage(joinedMessage));
                        }
                    }
                }
            }
        }
        client.sendPacket(PacketShopContainerUpdate.recruitResponse(container.humanShop, err));
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        ShopContainer container = this.checkContainer(client.getContainer());
        if (container == null) {
            return;
        }
        PacketReader reader = new PacketReader(this.content);
        switch (this.secondType) {
            case RECRUIT_SETTLER: {
                int mobUniqueID = reader.getNextInt();
                int settlementUniqueID = reader.getNextInt();
                LocalMessage err = null;
                if (container.humanShop.getUniqueID() != mobUniqueID) {
                    err = new LocalMessage("ui", "settlerrecruitnotfound");
                } else {
                    SettlementsWorldData worldData = SettlementsWorldData.getSettlementsData(server);
                    CachedSettlementData settlement = worldData.getCachedData(settlementUniqueID);
                    if (settlement == null || !settlement.hasAccess(client) || !container.humanShop.isValidRecruitment(settlement, client)) {
                        err = new LocalMessage("ui", "settlementnoperm");
                    } else {
                        PacketShopContainerUpdate.recruitSettler(server, client, container, () -> worldData.getOrLoadServerData(settlementUniqueID));
                    }
                }
                if (err == null) break;
                client.sendPacket(PacketShopContainerUpdate.recruitResponse(container.humanShop, err));
                break;
            }
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ShopContainer container = this.checkContainer(client.getContainer());
        if (container == null) {
            return;
        }
        PacketReader reader = new PacketReader(this.content);
        switch (this.secondType) {
            case SETTLEMENTS_LIST: {
                int mobUniqueID = reader.getNextInt();
                int count = reader.getNextShortUnsigned();
                ArrayList<SettlementOpenSettlementListEvent.SettlementOption> options = new ArrayList<SettlementOpenSettlementListEvent.SettlementOption>(count);
                for (int i = 0; i < count; ++i) {
                    int settlementUniqueID = reader.getNextInt();
                    GameMessage name = GameMessage.fromContentPacket(reader.getNextContentPacket());
                    options.add(new SettlementOpenSettlementListEvent.SettlementOption(settlementUniqueID, name));
                }
                FormComponent focus = client.getFocusForm();
                if (!(focus instanceof ShopContainerForm)) break;
                ((ShopContainerForm)focus).openSettlementList(mobUniqueID, options);
                break;
            }
            case RECRUIT_RESPONSE: {
                int mobUniqueID = reader.getNextInt();
                GameMessage error = reader.getNextBoolean() ? GameMessage.fromContentPacket(reader.getNextContentPacket()) : null;
                FormComponent focus = client.getFocusForm();
                if (!(focus instanceof ShopContainerForm)) break;
                ((ShopContainerForm)focus).submitRecruitResponse(mobUniqueID, error);
                break;
            }
        }
    }

    private static enum Type {
        SETTLEMENTS_LIST,
        RECRUIT_SETTLER,
        RECRUIT_RESPONSE;

    }
}

