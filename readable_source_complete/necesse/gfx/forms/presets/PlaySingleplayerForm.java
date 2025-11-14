/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.save.WorldSave;
import necesse.engine.state.MainMenu;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.NewSaveForm;
import necesse.gfx.forms.presets.WorldSelectForm;
import necesse.gfx.gameFont.FontOptions;

public abstract class PlaySingleplayerForm
extends FormSwitcher {
    public final MainMenu mainMenu;
    public Form mainForm;
    public NewSaveForm newWorldForm;
    public WorldSelectForm loadWorldForm;

    public PlaySingleplayerForm(final MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        this.mainForm = this.addComponent(new Form("main", 400, 40));
        int buttonsWidth = this.mainForm.getWidth() - 8;
        FormFlow mainFlow = new FormFlow(10);
        this.mainForm.addComponent(new FormLocalLabel("ui", "playsingleplayer", new FontOptions(20), 0, this.mainForm.getWidth() / 2, mainFlow.next(30)));
        this.mainForm.addComponent(new FormLocalTextButton("ui", "newworld", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> this.makeNewWorldCurrent());
        this.mainForm.addComponent(new FormLocalTextButton("ui", "loadworld", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> this.makeLoadWorldCurrent());
        this.mainForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> this.onBackPressed());
        this.mainForm.setHeight(mainFlow.next());
        this.newWorldForm = this.addComponent(new NewSaveForm(){

            @Override
            public void createPressed(ServerCreationSettings serverCreationSettings) {
                mainMenu.startSingleplayer(null, serverCreationSettings, null);
            }

            @Override
            public void error(String error) {
                mainMenu.addNotice(error);
            }

            @Override
            public void backPressed() {
                PlaySingleplayerForm.this.makeCurrent(PlaySingleplayerForm.this.mainForm);
            }
        });
        this.loadWorldForm = this.addComponent(new WorldSelectForm(mainMenu, new LocalMessage("ui", "backbutton")){

            @Override
            public void onSelected(WorldSave worldSave, boolean fromNewlyCreated) {
                if (worldSave == null) {
                    return;
                }
                ServerCreationSettings serverCreationSettings = new ServerCreationSettings(worldSave.filePath);
                if (worldSave.creationSettings != null) {
                    serverCreationSettings = worldSave.creationSettings;
                }
                this.mainMenu.startSingleplayer(worldSave, serverCreationSettings, MainMenu.ConnectFrom.SinglePlayerLoadWorld);
            }

            @Override
            public void onBackPressed() {
                PlaySingleplayerForm.this.makeCurrent(PlaySingleplayerForm.this.mainForm);
            }
        }, (c, isCurrent) -> {
            if (isCurrent.booleanValue()) {
                this.loadWorldForm.loadWorlds();
            }
        });
    }

    public abstract void onBackPressed();

    public void makeMainMenuCurrent() {
        this.makeCurrent(this.mainForm);
    }

    public void makeNewWorldCurrent() {
        this.makeCurrent(this.newWorldForm);
        this.newWorldForm.onStarted();
    }

    public void makeLoadWorldCurrent() {
        this.makeCurrent(this.loadWorldForm);
    }

    public void submitEscapeEvent(InputEvent event) {
        if (this.isCurrent(this.newWorldForm)) {
            this.newWorldForm.submitEscapeEvent(event);
        } else if (this.isCurrent(this.loadWorldForm)) {
            this.loadWorldForm.submitEscapeEvent(event);
        } else {
            this.onBackPressed();
            event.use();
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.mainForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }
}

