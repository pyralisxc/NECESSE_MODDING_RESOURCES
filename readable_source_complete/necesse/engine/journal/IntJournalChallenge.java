/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.Settings;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.util.GameMath;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public abstract class IntJournalChallenge
extends JournalChallenge {
    protected final int min;
    protected final int max;

    public IntJournalChallenge(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public IntJournalChallenge(int max) {
        this(0, max);
    }

    protected abstract int getProgress(PlayerStats var1);

    protected float getProgressFloat(PlayerStats stats) {
        return GameMath.limit((float)this.getProgress(stats) / (float)this.max, 0.0f, 1.0f);
    }

    protected boolean isProgressDone(PlayerStats stats) {
        return this.getProgress(stats) >= this.max;
    }

    @Override
    public void addJournalFormContent(Client client, FormContentBox contentBox, FormFlow flow) {
        int maxWidth = contentBox.getWidth() - 20 - contentBox.getScrollBarWidth();
        boolean completed = this.isCompleted(client);
        FormContentIconButton isChallengeCompleteIcon = completed ? new FormContentIconButton(maxWidth - 20, flow.next(), FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.button_checked_20, new LocalMessage("achievement", "complete")) : new FormContentIconButton(maxWidth - 20, flow.next(), FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.button_escaped_20, new LocalMessage("achievement", "incomplete"));
        contentBox.addComponent(isChallengeCompleteIcon);
        FontOptions subtitleOptions = new FontOptions(16).color(completed ? Settings.UI.successTextColor : Settings.UI.errorTextColor);
        int absMax = this.max - this.min;
        int complete = completed ? absMax : Math.max(0, this.getProgress(client.characterStats) - this.min);
        GameMessageBuilder builder = new GameMessageBuilder();
        if (!completed) {
            builder.append(complete + "/" + absMax + " ");
            contentBox.addComponent(new FormLocalLabel(builder, subtitleOptions, 1, maxWidth - 24, flow.next() + 3, maxWidth));
        }
        this.addChallengeLabelInJournal(client, contentBox, flow, maxWidth - 50, completed);
        flow.next(6);
    }
}

