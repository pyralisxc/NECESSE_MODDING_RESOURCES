/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.networkField.IntNetworkField;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.SlotPriority;
import necesse.inventory.container.mob.ShopContainerPartyUpdateEvent;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;

public class AdventurePartyHumanHandler {
    private final HumanMob mob;
    private final IntNetworkField slot;
    private ServerClient client;
    private long nextConsumeTime;
    private long playerDiedBuffer = -1L;

    public AdventurePartyHumanHandler(final HumanMob mob) {
        this.mob = mob;
        this.slot = mob.registerNetworkField(new IntNetworkField(-1){

            @Override
            public void onChanged(Integer value) {
                super.onChanged(value);
                if (mob.isServer()) {
                    ShopContainerPartyUpdateEvent.sendAndApplyUpdate(mob);
                }
            }
        });
    }

    public void addSaveData(String name, SaveData save) {
        if (this.client != null) {
            save.addLong(name, this.client.authentication);
        }
    }

    public void applyLoadData(String name, LoadData save) {
        long adventurePartyAuth = save.getLong(name, -1L, false);
        if (adventurePartyAuth != -1L) {
            this.mob.runOnNextServerTick.add(() -> {
                ServerClient client;
                if (this.mob.isServer() && (client = this.mob.getLevel().getServer().getClientByAuth(adventurePartyAuth)) != null) {
                    this.set(client);
                }
            });
        }
    }

    public void serverTick() {
        if (!this.mob.isServer()) {
            return;
        }
        if (this.mob.isSettler() && !this.mob.isSettlerWithinSettlementLoadedRegions()) {
            SettlersWorldData settlersData = SettlersWorldData.getSettlersData(this.mob.getLevel().getServer());
            settlersData.refreshWorldSettler(this.mob, this.isInAdventureParty());
        }
        if (!this.mob.isSettler() && this.isInAdventureParty()) {
            this.set(null);
            return;
        }
        ServerClient serverClient = this.getServerClient();
        if (serverClient != null) {
            if (serverClient.isDisposed()) {
                this.set(null);
                this.mob.commandGuard(null, this.mob.getX(), this.mob.getY());
            } else if (this.playerDiedBuffer >= 0L) {
                this.playerDiedBuffer += 50L;
                if (!serverClient.isDead() || this.playerDiedBuffer >= 5000L) {
                    this.set(null);
                    if (!SettlersWorldData.getSettlersData(this.mob.getLevel().getServer()).returnToSettlement(this.mob, false)) {
                        this.mob.commandGuard(null, this.mob.getX(), this.mob.getY());
                    }
                    this.playerDiedBuffer = -1L;
                    return;
                }
            } else if (serverClient.isDead()) {
                this.playerDiedBuffer = 0L;
            }
        }
        if (serverClient != null) {
            PlayerMob player = this.getPlayerMob();
            if (player != null && !this.mob.isSamePlace(player) && this.mob.commandFollowMob == player) {
                if (this.mob.hungerLevel <= 0.0f) {
                    LocalMessage message = new LocalMessage("ui", "adventurepartyleftnofood", "name", this.mob.getLocalization());
                    serverClient.sendChatMessage(message);
                    this.set(null);
                    if (!SettlersWorldData.getSettlersData(this.mob.getLevel().getServer()).returnToSettlement(this.mob, false)) {
                        this.mob.commandGuard(null, this.mob.getX(), this.mob.getY());
                    }
                } else {
                    this.mob.getLevel().entityManager.changeMobLevel(this.mob, player.getLevel(), player.getX(), player.getY(), true);
                    this.mob.commandFollow(serverClient, player);
                    this.mob.ai.blackboard.mover.stopMoving(this.mob);
                }
                return;
            }
            if (this.nextConsumeTime <= this.mob.getTime()) {
                InventoryItem usedItem = this.tryConsumeItem("tick");
                if (usedItem == null) {
                    this.nextConsumeTime = this.mob.getTime() + (long)GameRandom.globalRandom.getIntBetween(3, 5) * 1000L;
                    if (this.mob.hungerLevel <= 0.0f && !this.mob.isBeingInteractedWith() && !this.mob.isSettlerWithinSettlement()) {
                        LocalMessage message = new LocalMessage("ui", "adventurepartyleftnofood", "name", this.mob.getLocalization());
                        serverClient.sendChatMessage(message);
                        this.set(null);
                        if (!SettlersWorldData.getSettlersData(this.mob.getLevel().getServer()).returnToSettlement(this.mob, false)) {
                            this.mob.commandGuard(null, this.mob.getX(), this.mob.getY());
                        }
                    }
                } else {
                    this.nextConsumeTime = this.mob.getTime() + 1000L;
                }
            }
        }
    }

    public InventoryItem tryConsumeItem(String purpose) {
        if (this.client == null) {
            return null;
        }
        PlayerMob player = this.getPlayerMob();
        PlayerInventory inventory = player.getInv().party;
        for (SlotPriority slotPriority : AdventurePartyConsumableItem.getPartyPriorityList(this.mob.getLevel(), this.mob, this.client, inventory, purpose)) {
            InventoryItem invItem = inventory.getItem(slotPriority.slot);
            AdventurePartyConsumableItem partyItem = (AdventurePartyConsumableItem)((Object)invItem.item);
            InventoryItem itemConsumed = partyItem.onPartyConsume(this.mob.getLevel(), this.mob, this.client, invItem, purpose);
            if (itemConsumed == null) continue;
            this.mob.showPickupAnimation(this.mob.getDir() == 3 ? this.mob.getX() - 10 : this.mob.getX() + 10, this.mob.getY(), itemConsumed.item, 250);
            if (invItem.getAmount() <= 0) {
                inventory.clearSlot(slotPriority.slot);
            }
            inventory.markDirty(slotPriority.slot);
            return itemConsumed;
        }
        return null;
    }

    public void onSecondWindAttempt(MobBeforeHitCalculatedEvent event) {
        InventoryItem consumedItem = this.tryConsumeItem("secondwind");
        if (consumedItem != null && ((AdventurePartyConsumableItem)((Object)consumedItem.item)).shouldPreventHit(this.mob.getLevel(), this.mob, this.client, consumedItem)) {
            event.prevent();
        }
    }

    public void set(ServerClient client) {
        ServerClient oldClient = this.client;
        this.client = client;
        this.slot.set(client == null ? -1 : client.slot);
        if (oldClient != null && oldClient != client) {
            oldClient.adventureParty.serverRemove(this.mob, false, false);
        }
        if (client != null) {
            client.adventureParty.serverAdd(this.mob);
            this.mob.commandFollow(client, client.playerMob);
        }
        if (oldClient != client) {
            this.playerDiedBuffer = -1L;
        }
    }

    public void clear(boolean returnToSettlement) {
        this.set(null);
        if (returnToSettlement) {
            if (!this.mob.isSettlerOnCurrentLevel()) {
                this.mob.commandGuard(null, this.mob.getX(), this.mob.getY());
            } else if (!this.mob.isSettlerWithinSettlementLoadedRegions()) {
                SettlersWorldData settlersData = SettlersWorldData.getSettlersData(this.mob.getServer());
                settlersData.refreshWorldSettler(this.mob, true);
            }
        }
    }

    public ServerClient getServerClient() {
        return this.client;
    }

    public int getSlot() {
        return (Integer)this.slot.get();
    }

    public boolean isInAdventureParty() {
        return this.getSlot() != -1;
    }

    public ClientClient getClientClient() {
        if (this.mob.isClient() && this.getSlot() != -1) {
            return this.mob.getLevel().getClient().getClient(this.getSlot());
        }
        return null;
    }

    public boolean isFollowingMe() {
        if (this.mob.isClient()) {
            return this.getSlot() == this.mob.getLevel().getClient().getSlot();
        }
        return false;
    }

    public PlayerMob getPlayerMob() {
        ClientClient client;
        if (this.client != null) {
            return this.client.playerMob;
        }
        if (this.mob.isClient() && (client = this.getClientClient()) != null) {
            return client.playerMob;
        }
        return null;
    }
}

