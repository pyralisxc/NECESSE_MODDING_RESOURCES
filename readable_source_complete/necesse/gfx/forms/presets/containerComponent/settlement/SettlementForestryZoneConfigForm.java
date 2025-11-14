/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.util.LinkedList;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
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
import necesse.inventory.container.settlement.events.SettlementForestryZoneUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneNameEvent;
import necesse.level.gameObject.ForestrySaplingObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.settlementData.zones.SettlementForestryZone;

public class SettlementForestryZoneConfigForm
extends FormSwitcher
implements WorkZoneConfigComponent {
    public SettlementAssignWorkForm<?> assignWork;
    public SettlementForestryZone zone;
    public Form configForm;
    protected ConfirmationForm deleteConfirm;
    public FormLabelEdit label;
    public FormContentIconButton renameButton;
    public LocalMessage renameTip;
    public FormCheckBox choppingAllowedCheckbox;
    public FormCheckBox replantChoppedDownTreesCheckbox;
    public FormDropdownSelectionButton<Integer> autoPlantSaplingDropdown;

    public SettlementForestryZoneConfigForm(SettlementAssignWorkForm<?> assignWork, SettlementForestryZone zone, Runnable backPressed) {
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
        flow.next(10);
        this.choppingAllowedCheckbox = this.configForm.addComponent(flow.nextY(new FormLocalCheckBox("ui", "forestryzoneallowchopping", 5, 0, true, this.configForm.getWidth() - 10)));
        this.choppingAllowedCheckbox.onClicked(e -> {
            zone.setChoppingAllowed(((FormCheckBox)e.from).checked);
            ((SettlementContainer)assignWork.container).forestryZoneConfig.runAndSendSetAllowChopping(zone.getUniqueID(), ((FormCheckBox)e.from).checked);
        });
        flow.next(10);
        this.replantChoppedDownTreesCheckbox = this.configForm.addComponent(flow.nextY(new FormLocalCheckBox("ui", "forestryzonereplant", 5, 0, true, this.configForm.getWidth() - 10)));
        this.replantChoppedDownTreesCheckbox.onClicked(e -> {
            zone.setReplantChoppedDownTrees(((FormCheckBox)e.from).checked);
            ((SettlementContainer)assignWork.container).forestryZoneConfig.runAndSendSetReplantTrees(zone.getUniqueID(), ((FormCheckBox)e.from).checked);
        });
        LinkedList<GameObject> saplingObjects = new LinkedList<GameObject>();
        for (GameObject object : ObjectRegistry.getObjects()) {
            if (!(object instanceof ForestrySaplingObject) || ((ForestrySaplingObject)((Object)object)).getForestryResultObjectStringID() == null) continue;
            saplingObjects.add(object);
        }
        flow.next(5);
        this.configForm.addComponent(flow.nextY(new FormLocalLabel("ui", "forestryzoneplantcustom", new FontOptions(16), -1, 5, 0, this.configForm.getWidth() - 10)));
        flow.next(5);
        this.autoPlantSaplingDropdown = this.configForm.addComponent(flow.nextY(new FormDropdownSelectionButton(40, 0, FormInputSize.SIZE_24, ButtonColor.BASE, this.configForm.getWidth() - 80)));
        for (GameObject saplingObject : saplingObjects) {
            this.autoPlantSaplingDropdown.options.add(saplingObject.getID(), saplingObject.getLocalization());
        }
        this.autoPlantSaplingDropdown.onSelected(e -> {
            zone.setAutoPlantSaplingID((Integer)e.value);
            ((SettlementContainer)assignWork.container).forestryZoneConfig.runAndSendSetAutoPlantSapling(zone.getUniqueID(), (Integer)e.value);
        });
        this.updateConfigForm();
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
        ((Container)((Object)this.assignWork.container)).onEvent(SettlementForestryZoneUpdateEvent.class, event -> {
            if (event.zoneUniqueID != this.zone.getUniqueID()) {
                return;
            }
            this.zone.setChoppingAllowed(event.choppingAllowed);
            this.zone.setReplantChoppedDownTrees(event.replantChoppedDownTrees);
            this.zone.setAutoPlantSaplingID(event.autoPlantSaplingID);
            this.updateConfigForm();
        }, () -> !this.isDisposed());
    }

    public void updateConfigForm() {
        this.choppingAllowedCheckbox.checked = this.zone.isChoppingAllowed();
        this.replantChoppedDownTreesCheckbox.checked = this.zone.replantChoppedDownTrees();
        int autoPlantSaplingID = this.zone.getAutoPlantSaplingID();
        if (autoPlantSaplingID != -1) {
            GameObject object = ObjectRegistry.getObject(autoPlantSaplingID);
            this.autoPlantSaplingDropdown.setSelected(autoPlantSaplingID, object.getLocalization());
        } else {
            this.autoPlantSaplingDropdown.setSelected(-1, new LocalMessage("ui", "selectbutton"));
        }
        this.autoPlantSaplingDropdown.setActive(!this.zone.replantChoppedDownTrees());
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

