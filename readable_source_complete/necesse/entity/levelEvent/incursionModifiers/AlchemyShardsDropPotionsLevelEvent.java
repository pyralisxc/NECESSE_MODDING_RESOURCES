/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.util.ArrayList;
import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.engine.incursionPerkTree.AlchemyShardsDropPotionsPerk;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.ObjectLootTableDroppedListenerEntityComponent;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.presets.IncursionLootLists;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class AlchemyShardsDropPotionsLevelEvent
extends LevelEvent
implements ObjectLootTableDroppedListenerEntityComponent {
    public AlchemyShardsDropPotionsLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void onObjectLootTableDropped(ObjectLootTableDropsEvent event) {
        if (event.object.isOre && !event.level.objectLayer.isPlayerPlaced(event.layerID, event.tileX, event.tileY) && GameRandom.globalRandom.getChance(AlchemyShardsDropPotionsPerk.POTION_DROP_CHANCE)) {
            int alchemyShardItemID = ItemRegistry.getItemID("alchemyshard");
            if (event.objectDrops.stream().anyMatch(p -> p.item.getID() == alchemyShardItemID)) {
                ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
                IncursionLootLists.greaterPotions.addItems(items, GameRandom.globalRandom, this.getLevel().buffManager.getModifier(LevelModifiers.LOOT).floatValue(), new Object[0]);
                event.objectDrops.addAll(items);
            }
        }
    }
}

