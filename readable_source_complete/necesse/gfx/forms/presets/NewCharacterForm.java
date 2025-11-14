/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.platforms.PlatformManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.HumanLook;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.componentPresets.FormNewPlayerPreset;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;

public abstract class NewCharacterForm
extends Form {
    protected FormNewPlayerPreset newPlayer;
    protected FormTextInput nameInput;
    protected FormLocalTextButton createButton;
    protected FormLocalTextButton cancelButton;

    public NewCharacterForm(String name) {
        super(name, 400, 10);
        FormFlow flow = new FormFlow(5);
        this.addComponent(new FormLocalLabel("ui", "createnewcharacter", new FontOptions(20), 0, this.getWidth() / 2, flow.next(25)));
        this.newPlayer = this.addComponent(flow.nextY(new FormNewPlayerPreset(0, 0, this.getWidth() - 10, true, true), 20));
        this.newPlayer.setLook(new HumanLook(GameRandom.globalRandom, true));
        this.addComponent(new FormLocalLabel("ui", "playername", new FontOptions(16), -1, 5, flow.next(18)));
        this.nameInput = this.addComponent(new FormTextInput(4, flow.next(40), FormInputSize.SIZE_32_TO_40, this.getWidth() - 8, GameUtils.getPlayerNameLength().height));
        this.nameInput.placeHolder = new LocalMessage("ui", "playername");
        this.nameInput.setRegexMatchFull(GameUtils.playerNameSymbolsPattern);
        String userName = PlatformManager.getPlatform().getUserName();
        if (userName != null && GameUtils.isValidPlayerName(userName) == null) {
            this.nameInput.setText(userName.trim());
        }
        this.nameInput.onChange(e -> this.updateCreateButton());
        int buttonsY = flow.next(40);
        this.createButton = this.addComponent(new FormLocalTextButton("ui", "charcreate", 4, buttonsY, this.getWidth() / 2 - 6));
        this.createButton.onClicked(e -> this.onCreatePressed(this.getPlayer()));
        this.cancelButton = this.addComponent(new FormLocalTextButton("ui", "connectcancel", this.getWidth() / 2 + 2, buttonsY, this.getWidth() / 2 - 6));
        this.cancelButton.onClicked(e -> this.onCancelPressed());
        this.updateCreateButton();
        this.setHeight(flow.next());
        this.onWindowResized(WindowManager.getWindow());
    }

    public abstract void onCreatePressed(PlayerMob var1);

    public abstract void onCancelPressed();

    public void reset() {
        this.newPlayer.reset();
        this.nameInput.setText("");
        String userName = PlatformManager.getPlatform().getUserName();
        if (userName != null && GameUtils.isValidPlayerName(userName) == null) {
            this.nameInput.setText(userName.trim());
        }
        this.updateCreateButton();
    }

    public void setLook(HumanLook look) {
        this.newPlayer.setLook(look);
    }

    public void updateCreateButton() {
        boolean canCreate = true;
        String name = this.nameInput.getText().trim();
        GameMessage valid = GameUtils.isValidPlayerName(name);
        if (valid != null) {
            canCreate = false;
            this.createButton.setLocalTooltip(valid);
        }
        if (canCreate) {
            this.createButton.setLocalTooltip(null);
        }
        this.createButton.setActive(canCreate);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public PlayerMob getPlayer() {
        PlayerMob player = this.newPlayer.getNewPlayer();
        player.playerName = this.nameInput.getText().trim();
        return player;
    }
}

