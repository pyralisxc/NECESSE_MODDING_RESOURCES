/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.presets.containerComponent.SelectSettlersContainerGameTool;
import necesse.gfx.forms.presets.containerComponent.SelectedSettlersHandler;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementToolHandler;
import necesse.inventory.container.settlement.SettlementContainer;

public class SettlementContainerGameTool
extends SelectSettlersContainerGameTool {
    public final SettlementContainer container;
    public final SettlementContainerForm<?> containerForm;

    public SettlementContainerGameTool(Client client, SelectedSettlersHandler selectedSettlers, SettlementContainer container, SettlementContainerForm<?> containerForm) {
        super(client, selectedSettlers);
        this.container = container;
        this.containerForm = containerForm;
    }

    @Override
    public SettlementToolHandler getCurrentToolHandler() {
        return this.containerForm.getCurrentToolHandler();
    }

    @Override
    public Stream<Mob> streamAllSettlers(Rectangle selectionBox) {
        return this.containerForm.settlers.stream().map(data -> this.level.entityManager.mobs.get(data.mobUniqueID, false));
    }

    @Override
    public void commandAttack(Mob target) {
        this.container.commandSettlersAttack.runAndSend(this.selectedSettlers.get(), target);
    }

    @Override
    public void commandGuard(int levelX, int levelY) {
        this.container.commandSettlersGuard.runAndSend(this.selectedSettlers.get(), levelX, levelY);
    }

    @Override
    public void commandGuard(ArrayList<Point> movePositions) {
        this.container.commandSettlersGuard.runAndSend(this.selectedSettlers.get(), movePositions);
    }
}

