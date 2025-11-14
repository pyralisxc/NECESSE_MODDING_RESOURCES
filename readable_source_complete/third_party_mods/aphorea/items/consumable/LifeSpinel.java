/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.network.packet.PacketPlayerGeneral
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.GameResources
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.consumable;

import aphorea.items.vanillaitemtypes.AphConsumableItem;
import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class LifeSpinel
extends AphConsumableItem {
    public LifeSpinel() {
        super(50, true);
        this.rarity = Item.Rarity.RARE;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 60000;
    }

    public boolean shouldSendToOtherClients(Level level, int x, int y, PlayerMob player, InventoryItem item, String error, GNDItemMap mapContent) {
        return error == null;
    }

    public void onOtherPlayerPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent) {
        SoundManager.playSound((GameSound)GameResources.crystalHit1, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)player));
    }

    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        player.setMaxHealth(Math.min(320, player.getMaxHealthFlat() + 5));
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtEntireLevelExcept((Packet)new PacketPlayerGeneral(player.getServerClient()), level, player.getServerClient());
        } else if (level.isClient()) {
            SoundManager.playSound((GameSound)GameResources.crystalHit1, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)player));
        }
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        return player.getMaxHealthFlat() >= 320 ? "incorrecthealth" : null;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"consumetip"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"lifespinel"));
        return tooltips;
    }
}

