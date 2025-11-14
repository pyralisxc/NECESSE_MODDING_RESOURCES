/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.JournalChallenge;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.ui.ButtonColor;

public class SimpleJournalChallenge
extends JournalChallenge {
    @Override
    public void addJournalFormContent(Client client, FormContentBox contentBox, FormFlow flow) {
        int maxWidth = contentBox.getWidth() - 20 - contentBox.getScrollBarWidth();
        boolean completed = this.isCompleted(client);
        FormContentIconButton isChallengeCompleteIcon = completed ? new FormContentIconButton(maxWidth - 20, flow.next(), FormInputSize.SIZE_24, ButtonColor.BASE, contentBox.getInterfaceStyle().button_checked_20, new LocalMessage("achievement", "complete")) : new FormContentIconButton(maxWidth - 20, flow.next(), FormInputSize.SIZE_24, ButtonColor.BASE, contentBox.getInterfaceStyle().button_escaped_20, new LocalMessage("achievement", "incomplete"));
        contentBox.addComponent(isChallengeCompleteIcon);
        this.addChallengeLabelInJournal(client, contentBox, flow, maxWidth - 50, completed);
        flow.next(6);
    }
}

