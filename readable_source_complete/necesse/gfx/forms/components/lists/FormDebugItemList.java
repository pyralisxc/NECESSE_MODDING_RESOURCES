/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.util.List;
import necesse.engine.input.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketSpawnItem;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.lists.FormItemList;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemSearchTester;
import necesse.level.maps.Level;

public class FormDebugItemList
extends FormItemList {
    private String nameFilter = "";
    private boolean showUnobtainable;
    private final Client client;

    public FormDebugItemList(int x, int y, int width, int height, Client client) {
        super(x, y, width, height, FormItemList.UpdateMode.CONCURRENT_CONTINUOUS);
        this.client = client;
        this.reset();
        this.setFilter(this.nameFilter);
    }

    public void showUnobtainable(boolean value) {
        this.showUnobtainable = value;
        this.setFilter(this.nameFilter);
    }

    @Override
    public void addAllItems(List<InventoryItem> list) {
        for (Item item : ItemRegistry.getItems()) {
            if (item == null) continue;
            item.addDefaultItems(list, this.client == null ? null : this.client.getPlayer());
        }
    }

    @Override
    public void onItemClicked(InventoryItem item, InputEvent event) {
        PlayerMob player = this.client.getPlayer();
        item = item.copy(event.getID() == -100 || event.isControllerEvent() ? item.item.getStackSize() : 1);
        boolean inHand = player.isInventoryExtended() && !WindowManager.getWindow().isKeyDown(340);
        this.client.network.sendPacket(new PacketSpawnItem(item, inHand));
        if (inHand) {
            if (player.getDraggingItem() == null) {
                player.setDraggingItem(item);
            } else if (!player.getDraggingItem().combine((Level)player.getLevel(), (PlayerMob)player, (Inventory)player.getInv().drag, (int)0, (InventoryItem)item, (String)"spawnitem", null).success) {
                player.setDraggingItem(null);
            }
            this.playTickSound();
        } else {
            int startAmount = item.getAmount();
            player.getInv().addItem(item, true, "give", null);
            if (item.getAmount() != startAmount) {
                SoundManager.playSound(GameResources.pop, SoundEffect.ui());
            }
        }
    }

    public void setFilter(String filter) {
        this.nameFilter = filter.toLowerCase();
        ItemSearchTester tester = ItemSearchTester.constructSearchTester(this.nameFilter);
        this.setFilter((InventoryItem invItem) -> {
            if (!invItem.item.shouldShowInItemList()) {
                return false;
            }
            if (!this.showUnobtainable && !ItemRegistry.isObtainable(invItem.item.getID())) {
                return false;
            }
            return tester.matches((InventoryItem)invItem, null, new GameBlackboard());
        });
    }
}

