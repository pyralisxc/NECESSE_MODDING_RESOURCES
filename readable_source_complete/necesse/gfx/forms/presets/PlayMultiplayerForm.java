/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkManager;
import necesse.engine.network.PlatformSubForm;
import necesse.engine.platforms.Platform;
import necesse.engine.state.MainMenu;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.HostWorldSelectForm;
import necesse.gfx.forms.presets.RentServerForm;
import necesse.gfx.gameFont.FontOptions;

public abstract class PlayMultiplayerForm
extends FormSwitcher {
    public final MainMenu mainMenu;
    public Form mainForm;
    public HostWorldSelectForm hostWorldSelectForm;
    public PlatformSubForm joinFriendForm;
    public PlatformSubForm joinServerForm;
    public RentServerForm rentServerForm;

    public PlayMultiplayerForm(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        this.mainForm = this.addComponent(new Form("main", 400, 40));
        int buttonsWidth = this.mainForm.getWidth() - 8;
        FormFlow mainFlow = new FormFlow(10);
        this.mainForm.addComponent(new FormLocalLabel("ui", "playmultiplayer", new FontOptions(20), 0, this.mainForm.getWidth() / 2, mainFlow.next(30)));
        NetworkManager networkManager = Platform.getNetworkManager();
        if (networkManager.allowsHosting()) {
            this.mainForm.addComponent(new FormLocalTextButton("ui", "hostworld", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> this.makeHostWorldCurrent());
        }
        if (networkManager.allowsFriendJoining()) {
            this.mainForm.addComponent(new FormLocalTextButton("ui", "joinfriend", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> this.makeJoinFriendCurrent());
        }
        if (networkManager.allowsServerJoining()) {
            this.mainForm.addComponent(new FormLocalTextButton("ui", "joinaserver", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> this.makeJoinServerCurrent());
            this.mainForm.addComponent(new FormLocalTextButton("ui", "rentserver", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> this.makeRentServerCurrent());
        }
        this.mainForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, mainFlow.next(40), buttonsWidth)).onClicked(e -> this.onBackPressed());
        this.mainForm.setHeight(mainFlow.next());
        if (networkManager.allowsHosting()) {
            this.hostWorldSelectForm = this.addComponent(new HostWorldSelectForm(mainMenu, new LocalMessage("ui", "backbutton")){

                @Override
                public void onBackPressed() {
                    PlayMultiplayerForm.this.makeMainMenuCurrent();
                }
            }, (c, isCurrent) -> {
                if (isCurrent.booleanValue()) {
                    this.hostWorldSelectForm.loadWorlds();
                }
            });
        }
        if (networkManager.allowsFriendJoining()) {
            this.joinFriendForm = networkManager.getJoinFriendForm(mainMenu, this::makeMainMenuCurrent);
            this.addComponent((FormComponent)((Object)this.joinFriendForm));
        }
        if (networkManager.allowsServerJoining()) {
            this.joinServerForm = networkManager.getJoinServerForm(this, mainMenu, this::makeMainMenuCurrent);
            this.addComponent((FormComponent)((Object)this.joinServerForm));
            this.rentServerForm = this.addComponent(new RentServerForm(){

                @Override
                public void onBackPressed() {
                    PlayMultiplayerForm.this.makeMainMenuCurrent();
                }
            });
        }
        this.makeCurrent(this.mainForm);
    }

    public abstract void onBackPressed();

    public void makeMainMenuCurrent() {
        this.makeCurrent(this.mainForm);
    }

    public void makeHostWorldCurrent() {
        this.makeCurrent(this.hostWorldSelectForm);
    }

    public void makeJoinFriendCurrent() {
        this.makeCurrent((FormComponent)((Object)this.joinFriendForm));
        this.joinServerForm.reset();
    }

    public void makeJoinServerCurrent() {
        this.joinServerForm.reset();
        this.makeCurrent((FormComponent)((Object)this.joinServerForm));
    }

    public void makeRentServerCurrent() {
        this.makeCurrent(this.rentServerForm);
        this.rentServerForm.makeCurrent(this.rentServerForm.infoForm);
    }

    public void submitEscapeEvent(InputEvent event) {
        if (this.isCurrent(this.hostWorldSelectForm)) {
            this.hostWorldSelectForm.submitEscapeEvent(event);
        } else if (this.isCurrent((FormComponent)((Object)this.joinFriendForm))) {
            this.joinFriendForm.submitEscapeEvent(event);
        } else if (this.isCurrent((FormComponent)((Object)this.joinServerForm))) {
            this.joinServerForm.submitEscapeEvent(event);
        } else if (this.isCurrent(this.rentServerForm)) {
            this.rentServerForm.submitEscapeEvent(event);
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

