/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.sidebar;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;

public class WireEditSidebarForm
extends SidebarForm {
    private static boolean[] isEditingWire = new boolean[4];
    private FormCheckBox[] wires;

    public static boolean isEditing(int wireID) {
        return isEditingWire[wireID];
    }

    public WireEditSidebarForm(InventoryItem item) {
        super("wireeditsidebar", 160, 120, item);
        this.addComponent(new FormLocalLabel("ui", "wireedit", new FontOptions(20), 0, this.getWidth() / 2, 10));
        FormFlow flow = new FormFlow(40);
        this.wires = new FormCheckBox[4];
        for (int i = 0; i < 4; ++i) {
            int wireID = i;
            this.wires[i] = this.addComponent(new FormLocalCheckBox("ui", "wire" + wireID, 10, flow.next(20))).onClicked(e -> {
                WireEditSidebarForm.isEditingWire[wireID] = ((FormCheckBox)e.from).checked;
            });
            this.wires[i].checked = WireEditSidebarForm.isEditing(wireID);
        }
        this.setHeight(flow.next());
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        for (int i = 0; i < this.wires.length; ++i) {
            this.wires[i].checked = WireEditSidebarForm.isEditing(i);
        }
        super.draw(tickManager, perspective, renderBox);
    }

    static {
        WireEditSidebarForm.isEditingWire[0] = true;
    }
}

