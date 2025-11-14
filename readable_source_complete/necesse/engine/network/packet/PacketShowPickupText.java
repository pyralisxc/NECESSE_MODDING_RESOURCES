/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.hudManager.floatText.ItemPickupText;

public class PacketShowPickupText
extends Packet {
    public final Item item;
    public final int amount;
    public final boolean playPickupSound;

    public PacketShowPickupText(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.item = ItemRegistry.getItem(reader.getNextShortUnsigned());
        this.amount = reader.getNextInt();
        this.playPickupSound = reader.getNextBoolean();
    }

    public PacketShowPickupText(Item item, int amount, boolean playPickupSound) {
        this.item = item;
        this.amount = amount;
        this.playPickupSound = playPickupSound;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(item.getID());
        writer.putNextInt(amount);
        writer.putNextBoolean(playPickupSound);
    }

    public PacketShowPickupText(InventoryItem item, boolean playPickupSound) {
        this(item.item, item.getAmount(), playPickupSound);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer();
        if (player == null || player.getLevel() == null) {
            return;
        }
        if (!Settings.showPickupText) {
            return;
        }
        player.getLevel().hudManager.addElement(new ItemPickupText(player, new InventoryItem(this.item, this.amount)));
        if (this.playPickupSound) {
            SoundManager.playSound(GameResources.pop, (SoundEffect)SoundEffect.effect(player));
        }
    }
}

