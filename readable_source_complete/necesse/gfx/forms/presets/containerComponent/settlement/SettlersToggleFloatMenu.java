/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public abstract class SettlersToggleFloatMenu
extends FormFloatMenu {
    protected Level level;
    protected ArrayList<? extends SettlementSettlerData> settlers;
    protected HashSet<Integer> toggledSettlers;
    protected FormContentBox content;
    protected FormCheckBox allSelected;

    public SettlersToggleFloatMenu(FormComponent parent, int width, int height, Level level, ArrayList<? extends SettlementSettlerData> settlers, boolean allSettlersToggled, HashSet<Integer> toggledSettlers) {
        super(parent);
        this.level = level;
        this.settlers = settlers;
        this.toggledSettlers = toggledSettlers;
        Form form = new Form(width, height);
        FormFlow flow = new FormFlow(4);
        FormTextInput searchBox = form.addComponent(flow.nextY(new FormTextInput(4, 0, FormInputSize.SIZE_24, form.getWidth() - 8, -1), 4));
        searchBox.placeHolder = new LocalMessage("ui", "searchtip");
        searchBox.onChange(e -> this.updateSettlersContent(((FormTextInput)e.from).getText()));
        this.allSelected = form.addComponent(flow.nextY(new FormLocalCheckBox("ui", "settlerselecteveryone", 4, 0, allSettlersToggled).useButtonTexture(), 4)).onClicked(e -> {
            this.updateSettlersContent(searchBox.getText());
            if (((FormCheckBox)e.from).checked) {
                toggledSettlers.clear();
            }
            this.onAllSettlersToggled(((FormCheckBox)e.from).checked);
        });
        int contentY = flow.next();
        this.content = form.addComponent(new FormContentBox(0, contentY, form.getWidth(), form.getHeight() - contentY));
        this.updateSettlersContent("");
        this.setForm(form);
    }

    protected void updateSettlersContent(String search) {
        this.content.clearComponents();
        FormFlow contentFlow = new FormFlow(5);
        boolean addedAny = false;
        for (SettlementSettlerData settlementSettlerData : this.settlers) {
            SettlerMob mob = settlementSettlerData.getSettlerMob(this.level);
            GameMessage displayName = new LocalMessage("ui", "settlernameunknown");
            if (mob instanceof HumanMob) {
                displayName = ((HumanMob)mob).getLocalization();
            }
            if (!search.isEmpty() && !settlementSettlerData.settler.getGenericMobName().toLowerCase().contains(search.toLowerCase()) && !displayName.translate().toLowerCase().contains(search.toLowerCase())) continue;
            FormLocalCheckBox checkbox = this.content.addComponent(contentFlow.nextY(new FormLocalCheckBox(displayName, 4, 0, this.allSelected.checked || this.toggledSettlers.contains(settlementSettlerData.mobUniqueID)).useButtonTexture(), 4));
            checkbox.setupDragToOtherCheckboxes("toggleSettlerFloatMenu");
            checkbox.setActive(!this.allSelected.checked);
            checkbox.onClicked(e -> this.onSettlerToggled(settler.mobUniqueID, ((FormCheckBox)e.from).checked));
            addedAny = true;
        }
        if (!addedAny) {
            this.content.addComponent(contentFlow.nextY(new FormLocalLabel("ui", this.settlers.isEmpty() ? "settlersnonavailable" : "settlersnosearch", new FontOptions(16), 0, this.content.getWidth() / 2, 0, this.content.getWidth() - 20), 4));
        }
        this.content.setContentBox(new Rectangle(this.content.getWidth(), contentFlow.next()));
    }

    public abstract void onAllSettlersToggled(boolean var1);

    public abstract void onSettlerToggled(int var1, boolean var2);
}

