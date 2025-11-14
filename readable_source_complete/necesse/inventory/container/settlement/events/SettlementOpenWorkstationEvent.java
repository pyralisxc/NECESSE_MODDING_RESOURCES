/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadDataException;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class SettlementOpenWorkstationEvent
extends ContainerEvent {
    public final int tileX;
    public final int tileY;
    public final ArrayList<SettlementWorkstationRecipe> recipes;

    public SettlementOpenWorkstationEvent(SettlementWorkstation workstation) {
        this.tileX = workstation.tileX;
        this.tileY = workstation.tileY;
        this.recipes = new ArrayList<SettlementWorkstationRecipe>(workstation.recipes);
    }

    public SettlementOpenWorkstationEvent(PacketReader reader) {
        super(reader);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        int recipesCount = reader.getNextShortUnsigned();
        this.recipes = new ArrayList(recipesCount);
        for (int i = 0; i < recipesCount; ++i) {
            try {
                int uniqueID = reader.getNextInt();
                SettlementWorkstationRecipe recipe = new SettlementWorkstationRecipe(uniqueID, reader);
                this.recipes.add(recipe);
                continue;
            }
            catch (LoadDataException loadDataException) {
                // empty catch block
            }
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextShortUnsigned(this.recipes.size());
        for (SettlementWorkstationRecipe recipe : this.recipes) {
            writer.putNextInt(recipe.uniqueID);
            recipe.writePacket(writer);
        }
    }
}

