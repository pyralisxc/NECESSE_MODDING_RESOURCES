/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import java.util.function.Consumer;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.save.WorldSave;
import necesse.engine.state.MainGame;
import necesse.engine.state.MainMenu;
import necesse.engine.state.State;
import necesse.engine.world.WorldSettings;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;

public abstract class HostSettingsForm
extends Form {
    private Consumer<InputEvent> goBackInputEvent;
    private Consumer<ControllerEvent> goBackControllerEvent;
    public WorldSave selectedWorldSave;

    public HostSettingsForm(String name, int width, int height) {
        super(name, width, height);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (!event.isUsed() && event.state && event.getID() == 256 && this.goBackInputEvent != null) {
            this.goBackInputEvent.accept(event);
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleControllerEvent(event, tickManager, perspective);
        if (!event.isUsed() && event.buttonState && (event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU) && this.goBackInputEvent != null) {
            this.goBackControllerEvent.accept(event);
        }
    }

    public void setGoBackInputEvent(Consumer<InputEvent> goBackInputEvent) {
        this.goBackInputEvent = goBackInputEvent;
    }

    public void setGoBackControllerEvent(Consumer<ControllerEvent> goBackControllerEvent) {
        this.goBackControllerEvent = goBackControllerEvent;
    }

    protected void host() {
        State state = GlobalData.getCurrentState();
        if (state instanceof MainMenu) {
            this.hostFromMainMenu((MainMenu)state);
        } else {
            this.hostFromLoadedWorld((MainGame)state);
        }
    }

    protected abstract void hostFromLoadedWorld(MainGame var1);

    protected abstract void hostFromMainMenu(MainMenu var1);

    public abstract void reset(WorldSettings var1);
}

