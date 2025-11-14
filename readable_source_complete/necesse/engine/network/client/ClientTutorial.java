/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client;

import java.util.ArrayList;
import java.util.UUID;
import necesse.engine.GameCache;
import necesse.engine.GlobalData;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.tutorialPhases.AdventureJournalPhase;
import necesse.engine.network.client.tutorialPhases.ChopTreePhase;
import necesse.engine.network.client.tutorialPhases.CraftPickaxePhase;
import necesse.engine.network.client.tutorialPhases.CraftTorchPhase;
import necesse.engine.network.client.tutorialPhases.EndTutorialPhase;
import necesse.engine.network.client.tutorialPhases.InteractElderPhase;
import necesse.engine.network.client.tutorialPhases.InteractSettlementFlagPhase;
import necesse.engine.network.client.tutorialPhases.MineOrePhase;
import necesse.engine.network.client.tutorialPhases.OreTipPhase;
import necesse.engine.network.client.tutorialPhases.QuestsTipPhase;
import necesse.engine.network.client.tutorialPhases.SettlementTipPhase;
import necesse.engine.network.client.tutorialPhases.TakeQuestFromElderPhase;
import necesse.engine.network.client.tutorialPhases.TutorialPhase;
import necesse.engine.network.client.tutorialPhases.UseLadderPhase;
import necesse.engine.network.client.tutorialPhases.UseWorkstationPhase;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;

public class ClientTutorial {
    private final long worldUniqueID;
    private final Client client;
    private boolean isActive;
    private ArrayList<TutorialPhase> phases;
    private int currentPhase;
    private InteractElderPhase interactElderPhase;
    private TakeQuestFromElderPhase takeQuestFromElderPhase;
    private AdventureJournalPhase adventureJournalPhase;
    private UseWorkstationPhase useWorkstationPhase;

    public ClientTutorial(Client client, long worldUniqueID) {
        this.client = client;
        this.worldUniqueID = worldUniqueID;
        this.setupPhases();
        this.isActive = true;
        this.loadProgress();
    }

    private void setupPhases() {
        this.phases = new ArrayList();
        this.phases.add(new ChopTreePhase(this, this.client));
        this.phases.add(new CraftTorchPhase(this, this.client));
        this.interactElderPhase = new InteractElderPhase(this, this.client);
        this.phases.add(this.interactElderPhase);
        this.useWorkstationPhase = new UseWorkstationPhase(this, this.client);
        this.phases.add(this.useWorkstationPhase);
        this.phases.add(new CraftPickaxePhase(this, this.client));
        this.phases.add(new UseLadderPhase(this, this.client));
        this.phases.add(new MineOrePhase(this, this.client));
        this.phases.add(new OreTipPhase(this, this.client));
        this.adventureJournalPhase = new AdventureJournalPhase(this, this.client);
        this.phases.add(this.adventureJournalPhase);
        this.phases.add(new InteractSettlementFlagPhase(this, this.client));
        this.phases.add(new SettlementTipPhase(this, this.client));
        this.takeQuestFromElderPhase = new TakeQuestFromElderPhase(this, this.client);
        this.phases.add(this.takeQuestFromElderPhase);
        this.phases.add(new QuestsTipPhase(this, this.client));
        this.phases.add(new EndTutorialPhase(this, this.client));
    }

    private String getCachePath() {
        byte[] bytes = (this.worldUniqueID + "Tutorial").getBytes();
        return "/client/" + UUID.nameUUIDFromBytes(bytes);
    }

    private void saveProgress() {
        SaveData save = new SaveData("Tutorial");
        save.addBoolean("isActive", this.isActive);
        save.addInt("currentPhase", this.currentPhase);
        GameCache.cacheSave(save, this.getCachePath());
    }

    private void loadProgress() {
        LoadData save = GameCache.getSave(this.getCachePath());
        if (save == null) {
            return;
        }
        this.isActive = save.getBoolean("isActive", this.isActive);
        this.currentPhase = save.getInt("currentPhase", 0);
    }

    public void updateObjective(MainGame mainGame) {
        if (!this.isActive) {
            return;
        }
        this.getCurrentPhase().updateObjective(mainGame);
    }

    public void start() {
        if (!this.isActive) {
            return;
        }
        this.setPhase(this.currentPhase);
    }

    public void reset() {
        if (this.isActive) {
            this.getCurrentPhase().end();
        }
        this.isActive = true;
        this.setupPhases();
        this.setPhase(0);
        this.saveProgress();
    }

    public void endTutorial() {
        if (this.isActive) {
            this.getCurrentPhase().end();
        }
        this.isActive = false;
        if (GlobalData.getCurrentState() instanceof MainGame) {
            ((MainGame)GlobalData.getCurrentState()).formManager.clearTutorial();
        }
        this.saveProgress();
    }

    public void tick() {
        TutorialPhase currentPhase;
        if (!this.isActive) {
            return;
        }
        if (this.client.worldSettings.creativeMode) {
            this.endTutorial();
        }
        if (!(currentPhase = this.getCurrentPhase()).isOver()) {
            this.getCurrentPhase().tick();
        } else if (!this.nextPhase()) {
            this.endTutorial();
        } else {
            this.tick();
            this.saveProgress();
        }
    }

    public void drawOverForms(PlayerMob perspective) {
        if (!this.isActive) {
            return;
        }
        this.getCurrentPhase().drawOverForm(perspective);
    }

    private TutorialPhase getCurrentPhase() {
        return this.phases.get(this.currentPhase);
    }

    private boolean setPhase(int phase) {
        if (phase < 0 && phase >= this.phases.size()) {
            return false;
        }
        this.getCurrentPhase().end();
        this.currentPhase = phase;
        this.getCurrentPhase().start();
        if (GlobalData.getCurrentState() instanceof MainGame) {
            this.getCurrentPhase().updateObjective((MainGame)GlobalData.getCurrentState());
        }
        return true;
    }

    public boolean nextPhase() {
        if (this.currentPhase + 1 < this.phases.size()) {
            this.setPhase(this.currentPhase + 1);
            return true;
        }
        return false;
    }

    public boolean previousPhase() {
        if (this.currentPhase - 1 > 0) {
            this.setPhase(this.currentPhase - 1);
            return true;
        }
        return false;
    }

    public void elderInteracted() {
        if (!this.isActive) {
            return;
        }
        if (this.getCurrentPhase() == this.interactElderPhase) {
            this.interactElderPhase.elderInteracted();
        }
        if (this.getCurrentPhase() == this.takeQuestFromElderPhase) {
            this.takeQuestFromElderPhase.elderInteracted();
        }
    }

    public void questAccepted() {
        if (!this.isActive) {
            return;
        }
        if (this.getCurrentPhase() == this.takeQuestFromElderPhase) {
            this.takeQuestFromElderPhase.questAccepted();
        }
    }

    public void adventureJournalOpened() {
        if (!this.isActive) {
            return;
        }
        if (this.getCurrentPhase() == this.adventureJournalPhase) {
            this.adventureJournalPhase.journalOpened();
        }
    }

    public void usedWorkstation() {
        if (!this.isActive) {
            return;
        }
        if (this.getCurrentPhase() == this.useWorkstationPhase) {
            this.useWorkstationPhase.usedWorkstation();
        }
    }
}

