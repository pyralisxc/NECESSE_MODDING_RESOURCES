/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemIDData;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.inventory.item.Item;

public class GNDItemGameItem
extends GNDItemIDData<Item> {
    public GNDItemGameItem(int id) {
        super(id);
    }

    public GNDItemGameItem(String stringID) {
        super(stringID);
    }

    public GNDItemGameItem(Item item) {
        super(item);
    }

    public GNDItemGameItem(PacketReader reader) {
        super(reader);
    }

    public GNDItemGameItem(LoadData data) {
        super(data);
    }

    @Override
    protected String getStringID(int id) {
        return ItemRegistry.getItemStringID(id);
    }

    @Override
    protected int getID(String stringID) {
        return ItemRegistry.getItemID(stringID);
    }

    public Item getItem() {
        return ItemRegistry.getItem(this.id);
    }

    @Override
    public GNDItem copy() {
        return new GNDItemGameItem(this.id);
    }

    @Override
    protected int getID(Item item) {
        return item.getID();
    }
}

