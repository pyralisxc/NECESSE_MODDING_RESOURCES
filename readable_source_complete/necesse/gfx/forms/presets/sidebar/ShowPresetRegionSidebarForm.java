/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.sidebar;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.HUD;

public class ShowPresetRegionSidebarForm
extends SidebarForm {
    public static String searchFilter = "";
    public FormTextInput searchInput;

    public ShowPresetRegionSidebarForm() {
        super("presetRegion", 200, 200);
        FormFlow flow = new FormFlow(5);
        this.addComponent(new FormLabel("Preset region", new FontOptions(20), -1, 5, flow.next(25)));
        this.addComponent(new FormLabel("Search:", new FontOptions(16), -1, 5, flow.next(20)));
        this.searchInput = this.addComponent(new FormTextInput(5, flow.next(24), FormInputSize.SIZE_24, this.getWidth() - 10, 100));
        this.searchInput.setText(searchFilter);
        this.searchInput.onChange(e -> {
            searchFilter = this.searchInput.getText();
        });
        this.setHeight(flow.next() + 5);
    }

    @Override
    public boolean isValid(Client client) {
        return HUD.showWorldPresetRegionBounds;
    }

    @Override
    public void onRemoved(Client client) {
        super.onRemoved(client);
        this.searchInput.setTyping(false);
    }
}

