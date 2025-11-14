/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement;

import necesse.engine.GameAuth;
import necesse.engine.GameLog;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public abstract class SettlementDependantContainer
extends Container {
    protected boolean acceptNoSettlementFound;
    public SettlementDataEvent settlementData;

    public SettlementDependantContainer(NetworkClient client, int uniqueSeed, SettlementDataEvent settlementData, boolean acceptNoSettlementFound) {
        super(client, uniqueSeed);
        this.settlementData = settlementData;
        this.acceptNoSettlementFound = acceptNoSettlementFound;
        this.subscribeEvent(SettlementDataEvent.class, e -> e.settlementUniqueID == this.getSettlementUniqueID(), () -> true);
        this.onEvent(SettlementDataEvent.class, (T e) -> {
            this.settlementData = e;
        });
        this.subscribeEvent(SettlementRemovedEvent.class, e -> e.settlementUniqueID == 0 || e.settlementUniqueID == this.getSettlementUniqueID(), () -> true);
        this.onEvent(SettlementRemovedEvent.class, (T e) -> {
            this.settlementData = null;
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.acceptNoSettlementFound && !this.hasSettlement()) {
            GameLog.warn.println("Could not find settlement for container " + this + " for client " + this.client);
            if (this.client.isClient()) {
                this.client.getClientClient().getClient().closeContainer(true);
            } else {
                this.close();
            }
        }
    }

    public SettlementsWorldData getWorldData() {
        if (this.client.isServer()) {
            return SettlementsWorldData.getSettlementsData(this.client.getServerClient().getServer());
        }
        return SettlementsWorldData.getSettlementsData(this.client.getClientClient().getClient());
    }

    public ServerSettlementData getServerData() {
        if (!this.client.isServer()) {
            throw new IllegalStateException("Cannot get server data client side");
        }
        if (this.settlementData == null) {
            return null;
        }
        return this.getWorldData().getServerData(this.settlementData.settlementUniqueID);
    }

    public int getSettlementUniqueID() {
        return this.settlementData != null ? this.settlementData.settlementUniqueID : 0;
    }

    public boolean hasSettlement() {
        return this.settlementData != null;
    }

    public long getSettlementDisbandTime() {
        return this.settlementData == null ? 0L : this.settlementData.disbandTime;
    }

    public boolean hasSettlementOwner() {
        return this.settlementData != null && this.settlementData.ownerAuth != -1L;
    }

    public boolean isSettlementOwner(ServerClient client) {
        if (this.settlementData == null) {
            return false;
        }
        return this.settlementData.ownerAuth == client.authentication;
    }

    public boolean hasSettlementAccess(ServerClient client) {
        if (this.settlementData == null) {
            return false;
        }
        return !this.settlementData.isPrivate || !this.hasSettlementOwner() || this.isSettlementOwner(client) || this.settlementData.teamID != -1 && this.settlementData.teamID == client.getTeamID();
    }

    public boolean isSettlementOwner(Client client) {
        if (this.settlementData == null) {
            return false;
        }
        return this.settlementData.ownerAuth == GameAuth.getAuthentication();
    }

    public boolean hasSettlementAccess(Client client) {
        if (this.settlementData == null) {
            return false;
        }
        return !this.settlementData.isPrivate || !this.hasSettlementOwner() || this.isSettlementOwner(client) || this.settlementData.teamID != -1 && this.settlementData.teamID == client.getTeam();
    }
}

