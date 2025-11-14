/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.tutorialPhases;

import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.state.MainGame;
import necesse.gfx.forms.events.FormInputEvent;

public class EndTutorialPhase
extends TutorialPhase {
    public EndTutorialPhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void updateObjective(MainGame mainGame) {
        this.setObjective(mainGame, "tutorialdone", "endtutorial", (FormInputEvent e) -> this.over());
    }
}

