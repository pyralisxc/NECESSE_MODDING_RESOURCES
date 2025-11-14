/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.tutorialPhases;

import necesse.engine.GlobalData;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.components.FormContentButton;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.presets.ButtonToolbarForm;
import necesse.level.maps.hudManager.HudDrawElement;

public class AdventureJournalPhase
extends TutorialPhase {
    private boolean journalOpened;
    private HudDrawElement drawElement;
    private LocalMessage openJournal;

    public AdventureJournalPhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void start() {
        super.start();
        this.journalOpened = false;
        this.client.hasNewJournalEntry = true;
    }

    @Override
    public void end() {
        super.end();
        if (this.drawElement != null) {
            this.drawElement.remove();
        }
        this.drawElement = null;
    }

    @Override
    public void updateObjective(MainGame mainGame) {
        if (!this.journalOpened) {
            this.setObjective(mainGame, "journal", "next", (FormInputEvent e) -> this.over());
        } else {
            this.over();
        }
    }

    @Override
    public void drawOverForm(PlayerMob perspective) {
        this.openJournal = new LocalMessage("tutorials", "openjournal");
        ButtonToolbarForm form = ((MainGame)GlobalData.getCurrentState()).formManager.rightQuickbar;
        FormContentButton journalButton = ((MainGame)GlobalData.getCurrentState()).formManager.journalButton;
        if (journalButton != null) {
            FairTypeDrawOptions text = this.getTextDrawOptions(this.getTutorialText(this.openJournal.translate()));
            this.getTextDrawOptions(text, null, form.getX() + journalButton.getX() + 16, form.getY() - 6, true).draw();
        }
    }

    public void journalOpened() {
        this.journalOpened = true;
        if (GlobalData.getCurrentState() instanceof MainGame) {
            this.updateObjective((MainGame)GlobalData.getCurrentState());
        }
    }
}

