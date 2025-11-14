/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketMobBuff;
import necesse.engine.network.packet.PacketPlayerBuff;
import necesse.engine.network.packet.PacketPlayerBuffs;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;

public class PotionConsumableItem
extends ConsumableItem
implements AdventurePartyConsumableItem {
    protected String buffType;
    protected ArrayList<String> overrideBuffs = new ArrayList();
    protected ArrayList<String> overrideThis = new ArrayList();
    protected int playerBuffDuration;
    protected int settlerBuffDuration;
    protected boolean obeysBuffPotionPolicy = true;

    public PotionConsumableItem(int stackSize, String buffType, int playerBuffDurationSeconds, int settlerBuffDurationSeconds) {
        super(stackSize, true);
        this.buffType = buffType;
        this.playerBuffDuration = playerBuffDurationSeconds;
        this.settlerBuffDuration = settlerBuffDurationSeconds;
        this.isPotion = true;
        this.setItemCategory("consumable", "potions");
        this.keyWords.add("potion");
        this.incinerationTimeMillis = 10000;
    }

    public PotionConsumableItem(int stackSize, String buffType, int buffDurationSeconds) {
        this(stackSize, buffType, buffDurationSeconds, buffDurationSeconds);
    }

    public PotionConsumableItem overridePotion(String potionStringID) {
        Item item = ItemRegistry.getItem(potionStringID);
        if (item instanceof PotionConsumableItem) {
            PotionConsumableItem potionItem = (PotionConsumableItem)item;
            this.overrideBuffs.add(potionItem.buffType);
            potionItem.overrideThis.add(this.buffType);
        } else {
            GameLog.warn.println("Could not find override potion with stringID " + potionStringID);
        }
        return this;
    }

    @Override
    public boolean shouldSendToOtherClients(Level level, int x, int y, PlayerMob player, InventoryItem item, String error, GNDItemMap mapContent) {
        return error == null;
    }

    @Override
    public void onOtherPlayerPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent) {
        SoundManager.playSound(GameResources.drink, (SoundEffect)SoundEffect.effect(player));
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (this.buffType != null && this.playerBuffDuration > 0) {
            ServerClient client;
            ActiveBuff ab = new ActiveBuff(this.buffType, (Mob)player, (float)this.playerBuffDuration, null);
            player.addBuff(ab, false);
            if (level.isServer() && (client = player.getServerClient()) != null) {
                level.getServer().network.sendToClientsWithEntity(new PacketPlayerBuff(client.slot, ab), client.playerMob);
            }
        }
        for (String buffStringID : this.overrideBuffs) {
            player.buffManager.removeBuff(buffStringID, level.isServer());
        }
        if (level.isServer()) {
            ServerClient client = player.getServerClient();
            if (client != null) {
                client.newStats.potions_consumed.add(this);
            }
        } else {
            SoundManager.playSound(GameResources.drink, (SoundEffect)SoundEffect.effect(player));
        }
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    @Override
    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, String error) {
        if (level.isServer() && !player.buffManager.hasBuff(this.buffType)) {
            level.getServer().network.sendPacket((Packet)new PacketPlayerBuffs(player.getServerClient()), player.getServerClient());
        }
        return item;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (player.buffManager.hasBuff(this.buffType)) {
            return "buffactive";
        }
        for (String buffStringID : this.overrideThis) {
            if (!player.buffManager.hasBuff(buffStringID)) continue;
            return "overrideactive";
        }
        return null;
    }

    @Override
    public boolean canAndShouldPartyConsume(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item, String purpose) {
        if (mob.buffManager.hasBuff(this.buffType)) {
            return false;
        }
        for (String buffStringID : this.overrideThis) {
            if (!mob.buffManager.hasBuff(buffStringID)) continue;
            return false;
        }
        if (this.obeysBuffPotionPolicy) {
            AdventureParty.BuffPotionPolicy buffPolicy = partyClient.adventureParty.getBuffPotionPolicy();
            switch (buffPolicy) {
                case ALWAYS: {
                    return true;
                }
                case IN_COMBAT: {
                    return mob.isInCombat();
                }
                case SAME_AS_ME: {
                    return partyClient.playerMob.buffManager.hasBuff(this.buffType);
                }
                case ON_HOTKEY: {
                    return purpose.equals("usebuffpotion");
                }
                case NEVER: {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public InventoryItem onPartyConsume(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item, String purpose) {
        if (this.buffType != null && this.settlerBuffDuration > 0) {
            ActiveBuff ab = new ActiveBuff(this.buffType, (Mob)mob, (float)this.settlerBuffDuration, null);
            mob.addBuff(ab, false);
            if (level.isServer()) {
                level.getServer().network.sendToClientsWithEntity(new PacketMobBuff(mob.getUniqueID(), ab, false), mob);
                mob.playConsumeSound.runAndSend(true);
            }
        }
        for (String buffStringID : this.overrideBuffs) {
            mob.buffManager.removeBuff(buffStringID, level.isServer());
        }
        InventoryItem out = item.copy();
        if (this.isSingleUse(null)) {
            item.setAmount(item.getAmount() - 1);
        }
        return out;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "consumetip"));
        return tooltips;
    }

    public GameMessage getDurationMessage() {
        return PotionConsumableItem.getBuffDurationMessage(this.playerBuffDuration);
    }

    @Override
    public ComparableSequence<Integer> getInventoryPriority(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose) {
        if (!this.overrideBuffs.isEmpty() && purpose.equals("usebuffpotion")) {
            return super.getInventoryPriority(level, player, inventory, inventorySlot, item, purpose).beforeBy(-this.overrideBuffs.size());
        }
        return super.getInventoryPriority(level, player, inventory, inventorySlot, item, purpose);
    }

    @Override
    public ItemUsed useBuffPotion(Level level, PlayerMob player, int seed, InventoryItem item) {
        String error = this.canPlace(level, 0, 0, player, null, item, null);
        if (error == null) {
            return new ItemUsed(true, this.onPlace(level, 0, 0, player, seed, item, null));
        }
        return new ItemUsed(false, this.onAttemptPlace(level, 0, 0, player, item, null, error));
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "potion");
    }
}

