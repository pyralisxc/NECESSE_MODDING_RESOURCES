/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementRequestInventory;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class SettlementWorkstation {
    public static int maxRecipes = 10;
    public final ServerSettlementData data;
    public final int tileX;
    public final int tileY;
    public ArrayList<SettlementWorkstationRecipe> recipes = new ArrayList();
    public SettlementRequestInventory fuelInventory;
    public SettlementInventory processingInputInventory;
    public SettlementInventory processingOutputInventory;

    public SettlementWorkstation(ServerSettlementData data, int tileX, int tileY) {
        this.data = data;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void addSaveData(SaveData save) {
        save.addPoint("tile", new Point(this.tileX, this.tileY));
        SaveData recipesData = new SaveData("RECIPES");
        for (SettlementWorkstationRecipe recipe : this.recipes) {
            SaveData recipeData = new SaveData("RECIPE");
            recipe.addSaveData(recipeData, true);
            recipesData.addSaveData(recipeData);
        }
        save.addSaveData(recipesData);
    }

    public SettlementWorkstation(ServerSettlementData data, LoadData save, int tileXOffset, int tileYOffset) throws LoadDataException {
        this.data = data;
        Point tile = save.getPoint("tile", null);
        if (tile == null) {
            throw new LoadDataException("Missing position");
        }
        tile.translate(tileXOffset, tileYOffset);
        this.tileX = tile.x;
        this.tileY = tile.y;
        this.recipes = new ArrayList();
        LoadData recipesData = save.getFirstLoadDataByName("RECIPES");
        if (recipesData != null) {
            recipesData.getLoadDataByName("RECIPE").stream().filter(c -> c.isArray()).forEach(recipeData -> {
                try {
                    SettlementWorkstationRecipe recipe = new SettlementWorkstationRecipe((LoadData)recipeData, true);
                    this.recipes.add(recipe);
                }
                catch (LoadDataException e) {
                    System.err.println("Could not load settlement work station recipe at level " + data.getLevel().getIdentifier() + ": " + e.getMessage());
                }
                catch (Exception e) {
                    System.err.println("Unknown error loading settlement work station recipe at level " + data.getLevel().getIdentifier());
                    e.printStackTrace();
                }
            });
        }
    }

    public void updateRecipe(int index, int uniqueID, PacketReader reader) {
        if (index > this.recipes.size()) {
            GameLog.warn.println("Received invalid settlement recipe update index");
            new SettlementWorkstationEvent(this.data, this).applyAndSendToClientsAt(this.data.getLevel());
            return;
        }
        int currentIndex = -1;
        for (int i = 0; i < this.recipes.size(); ++i) {
            if (this.recipes.get((int)i).uniqueID != uniqueID) continue;
            currentIndex = i;
            break;
        }
        if (currentIndex != -1) {
            if (index == currentIndex) {
                this.recipes.get(index).applyPacket(reader);
            } else {
                SettlementWorkstationRecipe last = this.recipes.remove(currentIndex);
                last.applyPacket(reader);
                this.recipes.add(index, last);
            }
            new SettlementWorkstationRecipeUpdateEvent(this.data, this.tileX, this.tileY, index, this.recipes.get(index)).applyAndSendToClientsAt(this.data.getLevel());
        } else if (this.recipes.size() >= maxRecipes) {
            new SettlementWorkstationEvent(this.data, this).applyAndSendToClientsAt(this.data.getLevel());
        } else {
            SettlementWorkstationRecipe recipe = new SettlementWorkstationRecipe(uniqueID, reader);
            this.recipes.add(index, recipe);
            new SettlementWorkstationRecipeUpdateEvent(this.data, this.tileX, this.tileY, index, recipe).applyAndSendToClientsAt(this.data.getLevel());
        }
    }

    public boolean isValid() {
        return this.getWorkstationObject() != null;
    }

    public SettlementRequestInventory getFuelInventory() {
        SettlementRequestOptions fuelRequestOptions;
        SettlementWorkstationLevelObject workstationObject = this.getWorkstationObject();
        if (workstationObject != null && (fuelRequestOptions = workstationObject.getFuelRequestOptions()) != null) {
            if (this.fuelInventory == null) {
                this.fuelInventory = new SettlementRequestInventory(this.data.getLevel(), this.tileX, this.tileY, fuelRequestOptions){

                    @Override
                    public InventoryRange getInventoryRange() {
                        SettlementWorkstationLevelObject workstationObjectInt = SettlementWorkstation.this.getWorkstationObject();
                        if (workstationObjectInt != null) {
                            return workstationObjectInt.getFuelInventoryRange();
                        }
                        return null;
                    }
                };
            }
            this.fuelInventory.filter = new ItemCategoriesFilter(fuelRequestOptions.minAmount, fuelRequestOptions.maxAmount, true);
            if (!this.fuelInventory.isValid()) {
                this.fuelInventory = null;
            }
            return this.fuelInventory;
        }
        this.fuelInventory = null;
        return null;
    }

    public SettlementInventory getProcessingInputInventory() {
        if (this.isProcessingWorkstation()) {
            if (this.processingInputInventory == null) {
                this.processingInputInventory = new SettlementInventory(this.data.getLevel(), this.tileX, this.tileY){

                    @Override
                    public InventoryRange getInventoryRange() {
                        SettlementWorkstationLevelObject workstationObjectInt = SettlementWorkstation.this.getWorkstationObject();
                        if (workstationObjectInt != null) {
                            return workstationObjectInt.getProcessingInputRange();
                        }
                        return null;
                    }
                };
            }
            if (!this.processingInputInventory.isValid()) {
                this.processingInputInventory = null;
            }
            return this.processingInputInventory;
        }
        this.processingInputInventory = null;
        return null;
    }

    public SettlementInventory getProcessingOutputInventory() {
        if (this.isProcessingWorkstation()) {
            if (this.processingOutputInventory == null) {
                this.processingOutputInventory = new SettlementInventory(this.data.getLevel(), this.tileX, this.tileY){

                    @Override
                    public InventoryRange getInventoryRange() {
                        SettlementWorkstationLevelObject workstationObjectInt = SettlementWorkstation.this.getWorkstationObject();
                        if (workstationObjectInt != null) {
                            return workstationObjectInt.getProcessingOutputRange();
                        }
                        return null;
                    }
                };
            }
            this.processingOutputInventory.filter = new ItemCategoriesFilter(0, 0, false);
            if (!this.processingOutputInventory.isValid()) {
                this.processingOutputInventory = null;
            }
            return this.processingOutputInventory;
        }
        this.processingOutputInventory = null;
        return null;
    }

    public boolean isProcessingWorkstation() {
        SettlementWorkstationLevelObject workstationObject = this.getWorkstationObject();
        if (workstationObject != null) {
            return workstationObject.isProcessingInventory();
        }
        return false;
    }

    public SettlementWorkstationLevelObject getWorkstationObject() {
        GameObject gameObject = this.data.getLevel().getObject(this.tileX, this.tileY);
        if (gameObject instanceof SettlementWorkstationObject) {
            return new SettlementWorkstationLevelObject(this.data.getLevel(), this.tileX, this.tileY);
        }
        return null;
    }
}

