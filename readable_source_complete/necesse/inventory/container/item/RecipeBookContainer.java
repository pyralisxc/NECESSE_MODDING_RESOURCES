/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.item;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.ContainerSlot;

public class RecipeBookContainer
extends Container {
    public final int INGREDIENT_SLOT;
    public final PlayerTempInventory ingredientInv;
    public final PlayerInventorySlot guideSlot;
    public final EmptyCustomAction clearingIngredientSlot;

    public RecipeBookContainer(NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);
        PacketReader reader = new PacketReader(content);
        int guideInventoryID = reader.getNextInt();
        int guideInventorySlot = reader.getNextInt();
        this.guideSlot = new PlayerInventorySlot(guideInventoryID, guideInventorySlot);
        Packet tempInvContent = reader.getNextContentPacket();
        this.ingredientInv = client.playerMob.getInv().applyTempInventoryPacket(tempInvContent, m -> this.isClosed());
        this.INGREDIENT_SLOT = this.addSlot(new ContainerSlot(this.ingredientInv, 0));
        this.addInventoryQuickTransfer(this.INGREDIENT_SLOT, this.INGREDIENT_SLOT);
        InventoryItem guideItem = this.guideSlot.getItem(client.playerMob.getInv());
        if (guideItem != null && guideItem.item.getStringID().equals("recipebook")) {
            this.lockSlot(this.guideSlot);
        }
        this.clearingIngredientSlot = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ContainerSlot ingredientSlot = RecipeBookContainer.this.getSlot(RecipeBookContainer.this.INGREDIENT_SLOT);
                InventoryItem item = ingredientSlot.getItem();
                if (item != null) {
                    PlayerMob player = this.getContainer().client.playerMob;
                    player.getInv().addItemsDropRemaining(item, "addback", player, false, true);
                    ingredientSlot.setItem(null);
                }
            }
        });
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        InventoryItem guideItem = this.guideSlot.getItem(client.playerMob.getInv());
        return guideItem != null && guideItem.item.getStringID().equals("recipebook");
    }

    public static Packet getContainerContent(ServerClient client, PlayerInventorySlot inventorySlot) {
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        writer.putNextInt(inventorySlot.inventoryID);
        writer.putNextInt(inventorySlot.slot);
        writer.putNextContentPacket(client.playerMob.getInv().getTempInventoryPacket(1));
        return p;
    }
}

