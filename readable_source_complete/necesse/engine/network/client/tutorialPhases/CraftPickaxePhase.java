/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.tutorialPhases;

import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.state.MainGame;

public class CraftPickaxePhase
extends TutorialPhase {
    public CraftPickaxePhase(ClientTutorial tutorial, Client client) {
        super(tutorial, client);
    }

    @Override
    public void updateObjective(MainGame mainGame) {
        this.setObjective(mainGame, "craftpick");
    }

    @Override
    public void tick() {
        if (this.client.getPlayer().getInv().getAmount(ItemRegistry.getItem("woodpickaxe"), false, false, false, false, "tutorial") > 0) {
            this.over();
        }
    }
}

