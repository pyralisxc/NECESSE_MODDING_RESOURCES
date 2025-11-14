/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.SelectedSettlersHandler;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.AdventurePartyConfigContainer;

public class PartyConfigCommandForm
extends FormSwitcher {
    public final Client client;
    public final AdventurePartyConfigContainer container;
    public final SelectedSettlersHandler selectedSettlers;
    protected Runnable inventoryPressed;
    protected Form noneSelectedForm;
    protected Form selectedForm;
    protected int lastPartySize;
    protected FormLocalLabel nonePartySizeLabel;
    protected FormLocalLabel selectedPartySizeLabel;

    public PartyConfigCommandForm(Client client, AdventurePartyConfigContainer container, SelectedSettlersHandler selectedSettlers, Runnable inventoryPressed) {
        this.client = client;
        this.container = container;
        this.selectedSettlers = selectedSettlers;
        this.inventoryPressed = inventoryPressed;
        this.noneSelectedForm = this.addComponent(new Form(408, 200));
        FormFlow noneFlow = new FormFlow(4);
        this.noneSelectedForm.addComponent(noneFlow.nextY(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.noneSelectedForm.getWidth() / 2, 0), 4));
        this.nonePartySizeLabel = this.noneSelectedForm.addComponent(noneFlow.nextY(new FormLocalLabel(new LocalMessage("ui", "adventurepartysize", "size", this.lastPartySize), new FontOptions(12), 0, this.noneSelectedForm.getWidth() / 2, 0), 5));
        this.noneSelectedForm.addComponent(noneFlow.nextY(new FormLocalLabel("ui", "settlementcommandtip", new FontOptions(16), 0, this.noneSelectedForm.getWidth() / 2, this.noneSelectedForm.getWidth() / 2, this.noneSelectedForm.getWidth() - 20), 8));
        this.noneSelectedForm.addComponent(noneFlow.nextY(new FormLocalTextButton("ui", "adventurepartycommandall", 4, 0, this.noneSelectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4)).onClicked(e -> {
            SelectedSettlersHandler selectedSettlersHandler = selectedSettlers;
            synchronized (selectedSettlersHandler) {
                AdventureParty adventureParty = client.adventureParty;
                synchronized (adventureParty) {
                    selectedSettlers.selectSettlers(client.adventureParty.getMobUniqueIDs());
                }
            }
            this.updateSelectedForm();
        });
        if (inventoryPressed != null) {
            this.noneSelectedForm.addComponent(noneFlow.nextY(new FormLocalTextButton("ui", "adventurepartyinventory", 4, 0, this.noneSelectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4)).onClicked(e -> inventoryPressed.run());
        }
        this.noneSelectedForm.setHeight(noneFlow.next());
        this.updatePartySizeLabel();
        this.selectedForm = this.addComponent(new Form(408, 200));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (!this.selectedSettlers.isEmpty() && event.getID() == 256) {
            if (!event.state) {
                SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
                synchronized (selectedSettlersHandler) {
                    this.selectedSettlers.clear();
                    this.updateCurrentForm();
                }
            }
            event.use();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleControllerEvent(event, tickManager, perspective);
        if (!this.selectedSettlers.isEmpty() && event.getState() == ControllerInput.MENU_BACK) {
            if (!event.buttonState) {
                SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
                synchronized (selectedSettlersHandler) {
                    this.selectedSettlers.clear();
                    this.updateCurrentForm();
                }
            }
            event.use();
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateCurrentForm();
        if (this.lastPartySize != this.client.adventureParty.getSize()) {
            this.updatePartySizeLabel();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    public void updatePartySizeLabel() {
        this.lastPartySize = this.client.adventureParty.getSize();
        this.nonePartySizeLabel.setLocalization(new LocalMessage("ui", "adventurepartysize", "size", this.lastPartySize));
        if (this.selectedPartySizeLabel != null) {
            this.selectedPartySizeLabel.setLocalization(new LocalMessage("ui", "adventurepartysize", "size", this.lastPartySize));
        }
    }

    public void updateCurrentForm() {
        if (this.selectedSettlers.isEmpty()) {
            if (!this.isCurrent(this.noneSelectedForm)) {
                this.makeCurrent(this.noneSelectedForm);
            }
        } else if (!this.isCurrent(this.selectedForm)) {
            this.updateSelectedForm();
            this.makeCurrent(this.selectedForm);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateSelectedForm() {
        Mob mob;
        this.selectedForm.clearComponents();
        FormFlow flow = new FormFlow(4);
        this.selectedForm.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.selectedForm.getWidth() / 2, 0), 4));
        this.nonePartySizeLabel = this.selectedForm.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "adventurepartysize", "size", this.lastPartySize), new FontOptions(12), 0, this.selectedForm.getWidth() / 2, 0), 5));
        this.updatePartySizeLabel();
        ArrayList<Mob> mobs = new ArrayList<Mob>(this.selectedSettlers.getSize());
        SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
        synchronized (selectedSettlersHandler) {
            for (int uniqueID : this.selectedSettlers.get()) {
                mobs.add(this.client.getLevel().entityManager.mobs.get(uniqueID, false));
            }
        }
        GameMessage subtitle = mobs.size() == 1 ? ((mob = (Mob)mobs.get(0)) == null ? new LocalMessage("ui", "settlementcommandselected", "count", mobs.size()) : mob.getLocalization()) : new LocalMessage("ui", "settlementcommandselected", "count", mobs.size());
        this.selectedForm.addComponent(flow.nextY(new FormLocalLabel(subtitle, new FontOptions(16), 0, this.selectedForm.getWidth() / 2, this.selectedForm.getWidth() / 2, this.selectedForm.getWidth() - 20), 8));
        this.selectedForm.addComponent(flow.nextY(new FormLocalTextButton("ui", "settlementcommandfollow", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4)).onClicked(e -> {
            SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                this.container.commandFollowMeAction.runAndSend(this.selectedSettlers.get());
            }
        });
        this.selectedForm.addComponent(flow.nextY(new FormLocalTextButton("ui", "settlementcommandclear", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4)).onClicked(e -> {
            SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                this.container.commandDisbandAction.runAndSend(this.selectedSettlers.get());
                this.selectedSettlers.clear();
                this.updateCurrentForm();
            }
        });
        if (this.inventoryPressed != null) {
            this.selectedForm.addComponent(flow.nextY(new FormLocalTextButton("ui", "adventurepartyinventory", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4)).onClicked(e -> this.inventoryPressed.run());
        }
        this.selectedForm.addComponent(flow.nextY(new FormLocalTextButton("ui", "cancelbutton", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4)).onClicked(e -> {
            SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                this.selectedSettlers.clear();
                this.updateCurrentForm();
            }
        });
        this.selectedForm.setHeight(flow.next());
        ContainerComponent.setPosInventory(this.selectedForm);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosInventory(this.noneSelectedForm);
        ContainerComponent.setPosInventory(this.selectedForm);
    }
}

