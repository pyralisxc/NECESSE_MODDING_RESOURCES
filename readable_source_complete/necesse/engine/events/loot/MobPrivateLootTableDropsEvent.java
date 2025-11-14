/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.loot;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.events.GameEvent;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;

public class MobPrivateLootTableDropsEvent
extends GameEvent {
    public final Mob mob;
    public final ServerClient client;
    public Point dropPos;
    public ArrayList<InventoryItem> drops;

    public MobPrivateLootTableDropsEvent(Mob mob, ServerClient client, Point dropPos, ArrayList<InventoryItem> drops) {
        this.mob = mob;
        this.client = client;
        this.dropPos = dropPos;
        this.drops = drops;
    }
}

