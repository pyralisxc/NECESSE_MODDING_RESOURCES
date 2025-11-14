/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.sidebar;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.presets.PresetCopyFilter;

public class PresetFilterSidebarForm
extends SidebarForm {
    private boolean isValid = true;
    private boolean clearOtherWires = false;

    public PresetFilterSidebarForm(String label, PresetCopyFilter filter) {
        super("presetFilter", 200, 200);
        FormFlow flow = new FormFlow(5);
        this.addComponent(new FormLabel(label, new FontOptions(20), -1, 5, flow.next(25)));
        this.addComponent(new FormCheckBox("Tiles", 5, flow.next(20), filter.acceptTiles)).onClicked(e -> {
            filter.acceptTiles = ((FormCheckBox)e.from).checked;
        });
        this.addComponent(new FormCheckBox("Objects", 5, flow.next(20), filter.acceptObjects)).onClicked(e -> {
            filter.acceptObjects = ((FormCheckBox)e.from).checked;
        });
        this.addComponent(new FormCheckBox("Object entities", 5, flow.next(20), filter.acceptObjectEntities)).onClicked(e -> {
            filter.acceptObjectEntities = ((FormCheckBox)e.from).checked;
        });
        this.addComponent(new FormCheckBox("Wires", 5, flow.next(20), filter.acceptWires)).onClicked(e -> {
            filter.acceptWires = ((FormCheckBox)e.from).checked;
        });
        this.addComponent(new FormCheckBox("Clear other wires", 5, flow.next(20), this.clearOtherWires)).onClicked(e -> {
            this.clearOtherWires = ((FormCheckBox)e.from).checked;
        });
        this.setHeight(flow.next() + 5);
    }

    public boolean shouldClearOtherWires() {
        return this.clearOtherWires;
    }

    @Override
    public boolean isValid(Client client) {
        return this.isValid;
    }

    public void invalidate() {
        this.isValid = false;
    }
}

