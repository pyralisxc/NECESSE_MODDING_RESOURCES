/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementAssignWorkForm;
import necesse.gfx.forms.presets.containerComponent.settlement.WorkZoneConfigComponent;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementWorkZoneNameEvent;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SettlementBasicZoneConfigForm
extends FormSwitcher
implements WorkZoneConfigComponent {
    public SettlementAssignWorkForm<?> assignWork;
    public SettlementWorkZone zone;
    public Form configForm;
    protected ConfirmationForm deleteConfirm;
    public FormLabelEdit label;
    public FormContentIconButton renameButton;
    public LocalMessage renameTip;

    public SettlementBasicZoneConfigForm(SettlementAssignWorkForm<?> assignWork, SettlementWorkZone zone, Runnable backPressed) {
        this.assignWork = assignWork;
        this.zone = zone;
        this.deleteConfirm = this.addComponent(new ConfirmationForm("delete", 300, 200));
        this.configForm = this.addComponent(new Form(400, 200));
        FormFlow flow = new FormFlow(5);
        FontOptions labelOptions = new FontOptions(20);
        this.label = this.configForm.addComponent(flow.nextY(new FormLabelEdit("", labelOptions, this.getInterfaceStyle().activeTextColor, 5, 5, 10, 50)), -1000);
        this.label.onMouseChangedTyping(e -> this.runLabelUpdate());
        this.label.onSubmit(e -> this.runLabelUpdate());
        this.label.allowItemAppend = true;
        this.label.setParsers(OEInventoryContainerForm.getParsers(labelOptions));
        this.label.setText(zone.getName().translate());
        FormFlow iconFlow = new FormFlow(this.configForm.getWidth() - 4);
        this.configForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.RED, this.getInterfaceStyle().container_storage_remove, new LocalMessage("ui", "settlementdeletezone"))).onClicked(e -> {
            this.deleteConfirm.setupConfirmation(new LocalMessage("ui", "settlementareadeleteconfirm", "zone", zone.getName().translate()), () -> {
                ((SettlementContainer)assignWork.container).deleteWorkZone.runAndSend(zone.getUniqueID());
                backPressed.run();
            }, () -> this.makeCurrent(this.configForm));
            this.makeCurrent(this.deleteConfirm);
        });
        this.renameTip = new LocalMessage("ui", "renamebutton");
        this.renameButton = this.configForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_rename, this.renameTip));
        this.renameButton.onClicked(e -> {
            this.label.setTyping(!this.label.isTyping());
            this.runLabelUpdate();
        });
        this.label.setWidth(iconFlow.next() - 8);
        this.configForm.addComponent(flow.nextY(new FormLocalLabel(zone.getAbstractName(), new FontOptions(12), -1, 5, 0, this.configForm.getWidth() - 10)));
        flow.next(20);
        this.configForm.addComponent(flow.nextY(new FormLocalTextButton("ui", "backbutton", 40, this.configForm.getHeight() / 2 - 12, this.configForm.getWidth() - 80, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked(e -> backPressed.run());
        flow.next(5);
        this.configForm.setHeight(flow.next());
        this.makeCurrent(this.configForm);
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    protected void init() {
        super.init();
        ((Container)((Object)this.assignWork.container)).onEvent(SettlementWorkZoneNameEvent.class, event -> {
            if (event.zoneUniqueID != this.zone.getUniqueID()) {
                return;
            }
            if (!this.label.isTyping()) {
                this.zone.setName(event.name);
                this.label.setText(this.zone.getName().translate());
            }
        });
    }

    public void runLabelUpdate() {
        if (this.label.isTyping()) {
            this.renameButton.setIcon(this.getInterfaceStyle().container_rename_save);
            this.renameTip = new LocalMessage("ui", "savebutton");
        } else {
            if (!this.label.getText().equals(this.zone.getName().translate())) {
                this.zone.setName(new StaticMessage(this.label.getText()));
                ((SettlementContainer)this.assignWork.container).renameWorkZone.runAndSend(this.zone.getUniqueID(), this.zone.getName());
            }
            this.renameButton.setIcon(this.getInterfaceStyle().container_rename);
            this.renameTip = new LocalMessage("ui", "renamebutton");
            this.label.setText(this.zone.getName().translate());
        }
        this.renameButton.setTooltips(this.renameTip);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosInventory(this.configForm);
    }
}

