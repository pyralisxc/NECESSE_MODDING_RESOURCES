/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.HostSettingsForm;
import necesse.engine.platforms.Platform;
import necesse.engine.save.WorldSave;
import necesse.engine.state.MainMenu;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.presets.WorldSelectForm;

public abstract class HostWorldSelectForm
extends WorldSelectForm {
    private final HostSettingsForm hostSettingsForm = Platform.getNetworkManager().getHostSettingsForm(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.selectForm), null);

    public HostWorldSelectForm(MainMenu mainMenu, GameMessage backButton) {
        super(mainMenu, backButton);
        this.hostSettingsForm.setGoBackInputEvent(event -> {
            if (this.isCurrent(this.hostSettingsForm)) {
                this.makeCurrent(this.selectForm);
                event.use();
            }
        });
        this.hostSettingsForm.setGoBackControllerEvent(event -> {
            if (this.isCurrent(this.hostSettingsForm)) {
                this.makeCurrent(this.selectForm);
                event.use();
            }
        });
        this.addComponent(this.hostSettingsForm);
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    public void onSelected(WorldSave worldSave, boolean fromNewlyCreated) {
        this.makeCurrent(this.hostSettingsForm);
        this.hostSettingsForm.selectedWorldSave = worldSave;
        this.hostSettingsForm.reset(worldSave.worldSettings());
    }
}

