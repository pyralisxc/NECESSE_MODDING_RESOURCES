/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.jobCondition;

import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.jobCondition.JobCondition;
import necesse.level.maps.levelData.settlementData.jobCondition.JobConditionUpdatePacketSender;

public class DoForeverJobCondition
extends JobCondition {
    @Override
    public boolean isConditionMet(EntityJobWorker settlerMob, ServerSettlementData serverData) {
        return true;
    }

    @Override
    public GameMessage getSelectedMessage() {
        return new LocalMessage("ui", "conditiondoforever");
    }

    @Override
    public Form getConfigurationForm(Client client, int minWidth, JobConditionUpdatePacketSender updatePacketSender, ArrayList<Runnable> updateListeners, Runnable refreshForm) {
        Form form = new Form(Math.max(minWidth, 300), 100);
        FormFlow flow = new FormFlow(4);
        form.addComponent(flow.nextY(new FormLocalLabel(this.getSelectedMessage(), new FontOptions(20), 0, form.getWidth() / 2, 4, form.getWidth() - 10), 4));
        form.addComponent(flow.nextY(new FormLocalLabel("ui", "conditiondoforevertip", new FontOptions(16), 0, form.getWidth() / 2, 4, form.getWidth() - 10), 4));
        form.setHeight(flow.next());
        return form;
    }
}

