/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.tutorialPhases;

import necesse.engine.input.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.state.MainGame;

public class UseWorkstationPhase
extends TutorialPhase {
    public UseWorkstationPhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void updateObjective(MainGame mainGame) {
        LocalMessage msg = new LocalMessage("tutorials", "craftwork");
        msg.addReplacement("key1", "[input=" + Control.MOUSE1.id + "]");
        msg.addReplacement("key2", "[input=" + Control.MOUSE2.id + "]");
        this.setObjective(mainGame, msg);
    }

    public void usedWorkstation() {
        this.over();
    }
}

