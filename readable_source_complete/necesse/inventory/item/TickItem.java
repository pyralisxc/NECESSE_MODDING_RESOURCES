/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.util.function.Consumer;
import necesse.engine.GameState;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.TileEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public interface TickItem {
    public void tick(Inventory var1, int var2, InventoryItem var3, GameClock var4, GameState var5, Entity var6, TileEntity var7, WorldSettings var8, Consumer<InventoryItem> var9);
}

