/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.SelectedSettlersHandler;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public class SettlementCommandForm<T extends SettlementContainer>
extends FormSwitcher
implements SettlementSubForm {
    public final Client client;
    public final T container;
    public final SettlementContainerForm<T> containerForm;
    protected Form noneSelectedForm;
    protected Form selectedForm;

    public SettlementCommandForm(Client client, T container, SettlementContainerForm<T> containerForm) {
        this.client = client;
        this.container = container;
        this.containerForm = containerForm;
        this.noneSelectedForm = this.addComponent(new Form(300, 200));
        FormFlow noneFlow = new FormFlow(4);
        this.noneSelectedForm.addComponent(noneFlow.nextY(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.noneSelectedForm.getWidth() / 2, 0), 4));
        this.noneSelectedForm.addComponent(noneFlow.nextY(new FormLocalLabel("ui", "settlementcommandtip", new FontOptions(16), 0, this.noneSelectedForm.getWidth() / 2, this.noneSelectedForm.getWidth() / 2, this.noneSelectedForm.getWidth() - 20), 8));
        this.noneSelectedForm.addComponent(noneFlow.nextY(new FormLocalTextButton("ui", "settlementcommandall", 4, 0, this.noneSelectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4)).onClicked(e -> {
            SelectedSettlersHandler selectedSettlersHandler = containerForm.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                List<Integer> newSelected = containerForm.settlers.stream().map(m -> m.mobUniqueID).collect(Collectors.toList());
                containerForm.selectedSettlers.selectSettlers(newSelected);
            }
            this.updateSelectedForm();
        });
        this.noneSelectedForm.addComponent(noneFlow.nextY(new FormLocalTextButton("ui", "settlementcommandclearall", 4, 0, this.noneSelectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4)).onClicked(e -> {
            HashSet<Integer> mobUniqueIDs = new HashSet<Integer>();
            for (SettlementSettlerBasicData settler : containerForm.settlers) {
                mobUniqueIDs.add(settler.mobUniqueID);
            }
            container.commandSettlersClearOrders.runAndSend(mobUniqueIDs);
        });
        this.noneSelectedForm.setHeight(noneFlow.next());
        this.selectedForm = this.addComponent(new Form(300, 200));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (!this.containerForm.selectedSettlers.isEmpty() && event.getID() == 256) {
            if (!event.state) {
                SelectedSettlersHandler selectedSettlersHandler = this.containerForm.selectedSettlers;
                synchronized (selectedSettlersHandler) {
                    this.containerForm.selectedSettlers.clear();
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
        if (!this.containerForm.selectedSettlers.isEmpty() && event.getState() == ControllerInput.MENU_BACK) {
            if (!event.buttonState) {
                SelectedSettlersHandler selectedSettlersHandler = this.containerForm.selectedSettlers;
                synchronized (selectedSettlersHandler) {
                    this.containerForm.selectedSettlers.clear();
                    this.updateCurrentForm();
                }
            }
            event.use();
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateCurrentForm();
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void onSetCurrent(boolean current) {
        if (current) {
            this.updateCurrentForm();
        }
    }

    public void updateCurrentForm() {
        if (this.containerForm.selectedSettlers.isEmpty()) {
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
        GameMessage subtitle;
        this.selectedForm.clearComponents();
        FormFlow flow = new FormFlow(4);
        this.selectedForm.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.selectedForm.getWidth() / 2, 0), 4));
        ArrayList<CommandMob> mobs = new ArrayList<CommandMob>(this.containerForm.selectedSettlers.getSize());
        SelectedSettlersHandler selectedSettlersHandler = this.containerForm.selectedSettlers;
        synchronized (selectedSettlersHandler) {
            for (int uniqueID : this.containerForm.selectedSettlers.get()) {
                Mob mob = this.client.getLevel().entityManager.mobs.get(uniqueID, false);
                if (!(mob instanceof CommandMob)) continue;
                mobs.add((CommandMob)((Object)mob));
            }
        }
        if (mobs.size() == 1) {
            Mob mob = (Mob)mobs.get(0);
            subtitle = mob.getLocalization();
        } else {
            subtitle = new LocalMessage("ui", "settlementcommandselected", "count", mobs.size());
        }
        this.selectedForm.addComponent(flow.nextY(new FormLocalLabel(subtitle, new FontOptions(16), 0, this.selectedForm.getWidth() / 2, this.selectedForm.getWidth() / 2, this.selectedForm.getWidth() - 20), 8));
        boolean allHideInside = mobs.stream().allMatch(CommandMob::getHideOnLowHealth);
        this.selectedForm.addComponent(flow.nextY(new FormLocalCheckBox("ui", "settlementcommandhidelowhealth", 4, 0, allHideInside, this.selectedForm.getWidth() - 8), 4)).onClicked(e -> {
            mobs.forEach(m -> m.setHideOnLowHealth(((FormCheckBox)e.from).checked));
            SelectedSettlersHandler selectedSettlersHandler = this.containerForm.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                ((SettlementContainer)this.container).commandSettlersSetHideOnLowHealth.runAndSend(this.containerForm.selectedSettlers.get(), ((FormCheckBox)e.from).checked);
            }
        });
        this.selectedForm.addComponent(flow.nextY(new FormLocalTextButton("ui", "settlementcommandfollow", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4)).onClicked(e -> {
            SelectedSettlersHandler selectedSettlersHandler = this.containerForm.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                ((SettlementContainer)this.container).commandSettlersFollow.runAndSend(this.containerForm.selectedSettlers.get());
            }
        });
        this.selectedForm.addComponent(flow.nextY(new FormLocalTextButton("ui", "settlementcommandclear", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4)).onClicked(e -> {
            SelectedSettlersHandler selectedSettlersHandler = this.containerForm.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                ((SettlementContainer)this.container).commandSettlersClearOrders.runAndSend(this.containerForm.selectedSettlers.get());
                this.containerForm.selectedSettlers.clear();
                this.updateCurrentForm();
            }
        });
        this.selectedForm.addComponent(flow.nextY(new FormLocalTextButton("ui", "cancelbutton", 4, 0, this.selectedForm.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE), 4)).onClicked(e -> {
            SelectedSettlersHandler selectedSettlersHandler = this.containerForm.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                this.containerForm.selectedSettlers.clear();
                this.updateCurrentForm();
            }
        });
        this.selectedForm.setHeight(flow.next());
        ContainerComponent.setPosInventory(this.selectedForm);
    }

    @Override
    public void onMenuButtonClicked(FormSwitcher switcher) {
        SettlementSubForm.super.onMenuButtonClicked(switcher);
        this.updateCurrentForm();
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosInventory(this.noneSelectedForm);
        ContainerComponent.setPosInventory(this.selectedForm);
    }

    @Override
    public GameMessage getMenuButtonName() {
        return new LocalMessage("ui", "settlementcommand");
    }

    @Override
    public String getTypeString() {
        return "command";
    }
}

