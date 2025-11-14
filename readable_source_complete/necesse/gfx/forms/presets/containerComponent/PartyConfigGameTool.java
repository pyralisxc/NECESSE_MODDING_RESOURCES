/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.presets.containerComponent.SelectSettlersContainerGameTool;
import necesse.gfx.forms.presets.containerComponent.SelectedSettlersHandler;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementToolHandler;
import necesse.inventory.container.AdventurePartyConfigContainer;

public class PartyConfigGameTool
extends SelectSettlersContainerGameTool {
    public final AdventurePartyConfigContainer container;

    public PartyConfigGameTool(Client client, SelectedSettlersHandler selectedSettlers, AdventurePartyConfigContainer container) {
        super(client, selectedSettlers);
        this.container = container;
    }

    @Override
    public SettlementToolHandler getCurrentToolHandler() {
        return null;
    }

    @Override
    public Stream<Mob> streamAllSettlers(Rectangle selectionBox) {
        return this.client.getLevel().entityManager.mobs.streamInRegionsShape(selectionBox, 1).filter(m -> m instanceof HumanMob).filter(m -> ((HumanMob)m).canBeCommanded(this.client));
    }

    @Override
    public void commandAttack(Mob target) {
        this.container.commandAttackAction.runAndSend(this.selectedSettlers.get(), target);
    }

    @Override
    public void commandGuard(int levelX, int levelY) {
        this.container.commandGuardAction.runAndSend(this.selectedSettlers.get(), levelX, levelY);
    }

    @Override
    public void commandGuard(ArrayList<Point> movePositions) {
        this.container.commandGuardAction.runAndSend(this.selectedSettlers.get(), movePositions);
    }
}

