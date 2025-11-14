/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.platforms.steam.forms;

import necesse.engine.input.InputEvent;
import necesse.engine.network.PlatformSubForm;
import necesse.engine.platforms.steam.forms.FormSteamFriendsJoinList;
import necesse.engine.platforms.steam.forms.FormSteamFriendsList;
import necesse.engine.state.MainMenu;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;

public abstract class JoinSteamFriendForm
extends Form
implements PlatformSubForm {
    public FormSteamFriendsList friendsList;

    public JoinSteamFriendForm(int width, int height, MainMenu mainMenu) {
        super("joinfriend", width, height);
        FormLocalLabel joinLabel = this.addComponent(new FormLocalLabel("ui", "joinfriendhow", new FontOptions(16), 0, this.getWidth() / 2, 10, this.getWidth() - 20));
        int joinLabelHeight = joinLabel.getHeight() + 20;
        this.friendsList = this.addComponent(new FormSteamFriendsJoinList(mainMenu, 0, joinLabelHeight, this.getWidth(), this.getHeight() - 40 - joinLabelHeight));
        this.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.getHeight() - 40, this.getWidth() - 8)).onClicked(e -> this.onBackPressed());
    }

    public abstract void onBackPressed();

    @Override
    public void submitEscapeEvent(InputEvent event) {
        this.onBackPressed();
        event.use();
    }

    @Override
    public void reset() {
        this.friendsList.reset();
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }
}

