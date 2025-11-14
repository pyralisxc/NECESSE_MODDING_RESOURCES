/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.inventory.container.settlement.events.SettlementSettlerBasicsEvent;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementGroupConfigForm
extends Form {
    public final SettlementContainerForm<?> parent;
    public int maxHeight;
    public int headerHeight;
    public int contentHeight;
    protected FormLocalLabel header;
    protected FormContentIconButton allCheckbox;
    protected FormContentBox content;
    protected boolean allSelected;
    protected HashSet<Integer> selectedSettlers = new HashSet();

    public SettlementGroupConfigForm(int width, int maxHeight, SettlementContainerForm<?> parent) {
        super(width, 300);
        this.maxHeight = maxHeight;
        this.parent = parent;
        this.header = this.addComponent(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.getWidth() / 2, this.headerHeight));
        this.headerHeight += 25;
        this.allCheckbox = this.addComponent(new FormContentIconButton(4, this.headerHeight, FormInputSize.SIZE_20, ButtonColor.BASE, null, new GameMessage[0]));
        this.allCheckbox.onClicked(e -> {
            if (this.allSelected) {
                this.selectedSettlers.clear();
                this.allCheckbox.setIcon(null);
            } else {
                for (SettlementSettlerBasicData data : parent.settlers) {
                    this.selectedSettlers.add(data.mobUniqueID);
                }
                this.allCheckbox.setIcon(this.getInterfaceStyle().button_checked_20);
            }
            this.updateSettlers();
        });
        this.headerHeight += 22;
        this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 0, this.headerHeight, this.getWidth(), true));
        this.content = this.addComponent(new FormContentBox(0, this.headerHeight, this.getWidth(), this.getHeight() - this.headerHeight));
        ((SettlementContainer)parent.getContainer()).onEvent(SettlementSettlerBasicsEvent.class, event -> this.updateSettlers());
    }

    public void updateSettlers() {
        this.content.clearComponents();
        FormFlow settlersFlow = new FormFlow();
        this.addSettlers(settlersFlow, this.parent.settlers);
        this.updateAllSelected();
        if (this.parent.settlers.isEmpty()) {
            this.content.alwaysShowVerticalScrollBar = false;
            settlersFlow.next(16);
            this.content.addComponent(settlersFlow.nextY(new FormLocalLabel("ui", "settlersnosettlers", new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 16));
        } else {
            this.content.alwaysShowVerticalScrollBar = true;
        }
        this.contentHeight = Math.max(settlersFlow.next(), 70);
        this.updateSize();
    }

    public void updateSize() {
        this.setHeight(Math.min(this.maxHeight, this.content.getY() + this.contentHeight + this.headerHeight));
        this.content.setContentBox(new Rectangle(0, 0, this.content.getWidth(), this.contentHeight));
        this.content.setWidth(this.getWidth());
        this.content.setHeight(this.getHeight() - this.content.getY());
        ContainerComponent.setPosInventory(this);
    }

    public void updateAllSelected() {
        this.allSelected = !this.parent.settlers.isEmpty() && this.parent.settlers.stream().allMatch(data -> this.selectedSettlers.contains(data.mobUniqueID));
        this.allCheckbox.setIcon(this.allSelected ? this.getInterfaceStyle().button_checked_20 : null);
    }

    private void addSettlers(FormFlow settlersFlow, List<SettlementSettlerBasicData> list) {
        settlersFlow.next(2);
        for (SettlementSettlerBasicData data : list) {
            SettlerMob mob = data.getSettlerMob(this.parent.getClient().getLevel());
            if (mob != null) {
                int height = 22;
                FormFlow flowX = new FormFlow(4);
                FormContentIconButton checkboxButton = this.content.addComponent(new FormContentIconButton(flowX.next(24), settlersFlow.next() + 1, FormInputSize.SIZE_20, ButtonColor.BASE, null, new GameMessage[0]));
                if (this.selectedSettlers.contains(data.mobUniqueID)) {
                    checkboxButton.setIcon(this.getInterfaceStyle().button_checked_20);
                }
                checkboxButton.onClicked(e -> {
                    if (this.selectedSettlers.contains(data.mobUniqueID)) {
                        this.selectedSettlers.remove(data.mobUniqueID);
                        checkboxButton.setIcon(null);
                    } else {
                        this.selectedSettlers.add(data.mobUniqueID);
                        checkboxButton.setIcon(this.getInterfaceStyle().button_checked_20);
                    }
                    this.updateAllSelected();
                });
                this.content.addComponent(new FormLabel(mob.getSettlerName() + " (" + mob.getSettler().getGenericMobName() + ")", new FontOptions(16), -1, flowX.next(), settlersFlow.next() + 3, this.content.getWidth() - flowX.next() - this.content.getScrollBarWidth()));
                settlersFlow.next(height);
                continue;
            }
            GameLog.warn.println("Could not find settler mob with id " + data.mobUniqueID);
        }
        settlersFlow.next(2);
    }
}

