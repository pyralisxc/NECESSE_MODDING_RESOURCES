/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import necesse.engine.gameTool.GameToolManager;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.PartyConfigCommandForm;
import necesse.gfx.forms.presets.containerComponent.PartyConfigForm;
import necesse.gfx.forms.presets.containerComponent.PartyConfigGameTool;
import necesse.gfx.forms.presets.containerComponent.SelectSettlersContainerGameTool;
import necesse.gfx.forms.presets.containerComponent.SelectedSettlersHandler;
import necesse.inventory.container.AdventurePartyConfigContainer;

public class PartyConfigContainerForm
extends ContainerFormSwitcher<AdventurePartyConfigContainer> {
    public static boolean startInCommand = true;
    public PartyConfigForm partyConfigForm;
    public PartyConfigCommandForm commandForm;
    public final SelectedSettlersHandler selectedSettlers;
    public SelectSettlersContainerGameTool tool;

    public PartyConfigContainerForm(Client client, AdventurePartyConfigContainer container) {
        super(client, container);
        this.selectedSettlers = new SelectedSettlersHandler(client){

            @Override
            public void updateSelectedSettlers(boolean switchToCommandForm) {
                PartyConfigContainerForm.this.updateSelectedSettlers(switchToCommandForm);
            }
        };
        this.partyConfigForm = this.addComponent(new PartyConfigForm(client, container, container.PARTY_SLOTS_START, container.PARTY_SLOTS_END, 408, () -> {
            this.commandForm.updateCurrentForm();
            this.makeCurrent(this.commandForm);
            SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                AdventureParty adventureParty = client.adventureParty;
                synchronized (adventureParty) {
                    this.selectedSettlers.selectSettlers(client.adventureParty.getMobUniqueIDs());
                }
            }
            this.commandForm.updateSelectedForm();
        }, null, () -> ContainerComponent.setPosFocus(this.partyConfigForm)));
        this.commandForm = this.addComponent(new PartyConfigCommandForm(client, container, this.selectedSettlers, () -> this.makeCurrent(this.partyConfigForm)), (form, isCurrent) -> {
            if (!isCurrent.booleanValue()) {
                if (this.tool != null) {
                    GameToolManager.clearGameTool(this.tool);
                }
                this.tool = null;
            } else {
                if (this.tool != null) {
                    GameToolManager.clearGameTool(this.tool);
                }
                this.tool = new PartyConfigGameTool(client, this.selectedSettlers, container);
                GameToolManager.setGameTool(this.tool);
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.selectedSettlers.init();
        this.client.adventureParty.updateMobsFromLevel(this.client.getLevel());
        this.selectedSettlers.cleanUp(uniqueID -> {
            if (this.client.adventureParty.contains((int)uniqueID)) {
                return true;
            }
            Mob mob = this.client.getLevel().entityManager.mobs.get((int)uniqueID, false);
            return mob instanceof HumanMob && ((HumanMob)mob).canBeCommanded(this.client);
        });
        this.commandForm.updateSelectedForm();
        this.commandForm.updateCurrentForm();
        boolean shouldBeInventoryForm = this.selectedSettlers.isEmpty() && this.client.adventureParty.isEmpty() && this.client.getPlayer().getInv().hasPartyItems();
        this.makeCurrent(startInCommand && !shouldBeInventoryForm ? this.commandForm : this.partyConfigForm);
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    public void updateSelectedSettlers(boolean switchToCommandForm) {
        if (!this.selectedSettlers.isEmpty()) {
            if (switchToCommandForm && !this.isCurrent(this.commandForm) || this.getCurrent() == null) {
                this.commandForm.updateCurrentForm();
                this.makeCurrent(this.commandForm);
            }
            this.commandForm.updateSelectedForm();
        } else if (this.isCurrent(this.commandForm)) {
            this.commandForm.updateCurrentForm();
        }
    }

    @Override
    public boolean shouldShowInventory() {
        return !this.isCurrent(this.commandForm);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.partyConfigForm);
    }

    @Override
    public void dispose() {
        startInCommand = this.isCurrent(this.commandForm);
        super.dispose();
        this.selectedSettlers.dispose();
        if (this.tool != null) {
            GameToolManager.clearGameTool(this.tool);
        }
    }
}

