/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.loot;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.events.GameEvent;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;

public class MobLootTableDropsEvent
extends GameEvent {
    public final Mob mob;
    public Point dropPos;
    public ArrayList<InventoryItem> drops;

    public MobLootTableDropsEvent(Mob mob, Point dropPos, ArrayList<InventoryItem> drops) {
        this.mob = mob;
        this.dropPos = dropPos;
        this.drops = drops;
    }
}

