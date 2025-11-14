/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;

public class SettlementWorkstationLevelObject
extends LevelObject {
    public SettlementWorkstationLevelObject(Level level, int tileX, int tileY) {
        super(level, tileX, tileY);
        if (!(this.object instanceof SettlementWorkstationObject)) {
            throw new IllegalStateException("Object not workstation");
        }
    }

    public Stream<Recipe> streamSettlementRecipes() {
        return ((SettlementWorkstationObject)((Object)this.object)).streamSettlementRecipes(this.level, this.tileX, this.tileY);
    }

    public boolean canCurrentlyCraft(Recipe recipe) {
        return ((SettlementWorkstationObject)((Object)this.object)).canCurrentlyCraft(this.level, this.tileX, this.tileY, recipe);
    }

    public int getMaxCraftsAtOnce(Recipe recipe) {
        return ((SettlementWorkstationObject)((Object)this.object)).getMaxCraftsAtOnce(this.level, this.tileX, this.tileY, recipe);
    }

    public void tickCrafting(Recipe recipe) {
        ((SettlementWorkstationObject)((Object)this.object)).tickCrafting(this.level, this.tileX, this.tileY, recipe);
    }

    public void onCraftFinished(Recipe recipe) {
        ((SettlementWorkstationObject)((Object)this.object)).onCraftFinished(this.level, this.tileX, this.tileY, recipe);
    }

    public SettlementRequestOptions getFuelRequestOptions() {
        return ((SettlementWorkstationObject)((Object)this.object)).getFuelRequestOptions(this.level, this.tileX, this.tileY);
    }

    public InventoryRange getFuelInventoryRange() {
        return ((SettlementWorkstationObject)((Object)this.object)).getFuelInventoryRange(this.level, this.tileX, this.tileY);
    }

    public boolean isProcessingInventory() {
        return ((SettlementWorkstationObject)((Object)this.object)).isProcessingInventory(this.level, this.tileX, this.tileY);
    }

    public InventoryRange getProcessingInputRange() {
        return ((SettlementWorkstationObject)((Object)this.object)).getProcessingInputRange(this.level, this.tileX, this.tileY);
    }

    public InventoryRange getProcessingOutputRange() {
        return ((SettlementWorkstationObject)((Object)this.object)).getProcessingOutputRange(this.level, this.tileX, this.tileY);
    }

    public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs() {
        return ((SettlementWorkstationObject)((Object)this.object)).getCurrentAndFutureProcessingOutputs(this.level, this.tileX, this.tileY);
    }
}

