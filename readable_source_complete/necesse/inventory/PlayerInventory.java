/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import java.util.ArrayList;
import necesse.engine.network.packet.PacketPlayerInventoryPart;
import necesse.engine.network.packet.PacketPlayerInventorySlot;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public class PlayerInventory
extends Inventory {
    public final PlayerMob player;
    public boolean sizeCanChange;
    private final boolean canBeUsedForCrafting;
    private final boolean canLock;
    protected int invID;

    public PlayerInventory(PlayerMob player, int size, boolean sizeCanChange, boolean canBeUsedForCrafting, boolean canLock) {
        super(size);
        this.player = player;
        this.sizeCanChange = sizeCanChange;
        this.canBeUsedForCrafting = canBeUsedForCrafting;
        this.canLock = canLock;
    }

    public void setIDAndAddToList(ArrayList<PlayerInventory> list) {
        this.invID = list.size();
        list.add(this);
    }

    @Override
    public void override(Inventory inventory) {
        this.override(inventory, this.sizeCanChange, false);
    }

    @Override
    public boolean canLockItem(int slot) {
        return this.canLock;
    }

    @Override
    public boolean canBeUsedForCrafting() {
        return this.canBeUsedForCrafting;
    }

    @Override
    public void setItem(int slot, InventoryItem item, boolean overrideIsNew) {
        super.setItem(slot, item, overrideIsNew);
        if (item != null && this.player.getLevel() != null && this.player.isServer()) {
            ServerClient client = this.player.getServerClient();
            client.markObtainItem(item.item.getStringID());
        }
    }

    public int getInventoryID() {
        return this.invID;
    }

    public void tickSync() {
        if (this.isFullDirty()) {
            this.player.getLevel().getServer().network.sendToAllClients(new PacketPlayerInventoryPart(this.player.getServerClient(), this));
            this.clean();
        } else {
            for (int i = 0; i < this.getSize(); ++i) {
                if (!this.isDirty(i)) continue;
                this.player.getLevel().getServer().network.sendToAllClients(new PacketPlayerInventorySlot(this.player.getServerClient(), new PlayerInventorySlot(this, i)));
                this.clean(i);
            }
        }
    }
}

