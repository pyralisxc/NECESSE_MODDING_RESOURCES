/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;

public class PacketPlayerItemMobInteract
extends Packet {
    public final float playerX;
    public final float playerY;
    public final int inventoryID;
    public final int inventorySlot;
    public final int itemID;
    public final int attackX;
    public final int attackY;
    public final int mobUniqueID;
    public final int animAttack;
    public final int seed;
    public final GNDItemMap mapContent;

    public PacketPlayerItemMobInteract(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.playerX = reader.getNextFloat();
        this.playerY = reader.getNextFloat();
        this.inventoryID = reader.getNextShortUnsigned();
        this.inventorySlot = reader.getNextShortUnsigned();
        this.itemID = reader.getNextShortUnsigned();
        this.attackX = reader.getNextInt();
        this.attackY = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
        this.animAttack = reader.getNextShortUnsigned();
        this.seed = reader.getNextShortUnsigned();
        this.mapContent = new GNDItemMap(reader);
    }

    public PacketPlayerItemMobInteract(PlayerMob player, PlayerInventorySlot slot, Item item, int attackX, int attackY, Mob mob, int animAttack, int shortSeed, GNDItemMap mapContent) {
        this.playerX = player.x;
        this.playerY = player.y;
        this.inventoryID = slot.inventoryID;
        this.inventorySlot = slot.slot;
        this.itemID = item.getID();
        this.attackX = attackX;
        this.attackY = attackY;
        this.mobUniqueID = mob.getUniqueID();
        this.animAttack = animAttack;
        this.seed = shortSeed;
        this.mapContent = mapContent;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextFloat(this.playerX);
        writer.putNextFloat(this.playerY);
        writer.putNextShortUnsigned(this.inventoryID);
        writer.putNextShortUnsigned(this.inventorySlot);
        writer.putNextShortUnsigned(this.itemID);
        writer.putNextInt(attackX);
        writer.putNextInt(attackY);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextShortUnsigned(animAttack);
        writer.putNextShortUnsigned(this.seed);
        mapContent.writePacket(writer);
    }

    public PlayerInventorySlot getSlot() {
        return new PlayerInventorySlot(this.inventoryID, this.inventorySlot);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!client.checkHasRequestedSelf() || client.isDead()) {
            return;
        }
        client.checkSpawned();
        Mob mob = client.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
        if (mob != null) {
            client.playerMob.runServerItemMobInteract(this, mob);
        } else {
            client.sendPacket(new PacketRemoveMob(this.mobUniqueID));
        }
        client.refreshAFKTimer();
    }
}

