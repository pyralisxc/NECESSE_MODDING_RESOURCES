/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import necesse.engine.GlobalData;
import necesse.engine.state.MainGame;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.inventory.container.Container;

public interface ContainerComponent<T extends Container> {
    public void setHidden(boolean var1);

    public T getContainer();

    public boolean shouldOpenInventory();

    default public boolean shouldCloseInventory() {
        return false;
    }

    default public boolean shouldShowInventory() {
        return this.shouldOpenInventory();
    }

    default public boolean shouldShowToolbar() {
        return true;
    }

    default public boolean inventoryHotkeyClosesContainer() {
        return true;
    }

    public static void setPosInventory(Form form) {
        MainGameFormManager formManager = ContainerComponent.getFormManager();
        if (formManager != null) {
            form.setPosition(new FormRelativePosition((FormPositionContainer)formManager.toolbar, (formManager.toolbar.getWidth() - form.getWidth()) / 2, -form.getHeight() - form.getInterfaceStyle().formSpacing));
        } else {
            GameWindow window = WindowManager.getWindow();
            form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() - 75 - form.getHeight() / 2);
        }
    }

    public static void setPosFocus(Form form, int xOffset) {
        MainGameFormManager formManager = ContainerComponent.getFormManager();
        if (formManager != null) {
            form.setPosition(new FormRelativePosition((FormPositionContainer)formManager.inventory, (formManager.inventory.getWidth() - form.getWidth()) / 2 + xOffset, -form.getHeight() - form.getInterfaceStyle().formSpacing));
        }
    }

    public static void setPosFocus(Form form) {
        ContainerComponent.setPosFocus(form, 0);
    }

    public static MainGameFormManager getFormManager() {
        if (GlobalData.getCurrentState() instanceof MainGame) {
            return ((MainGame)GlobalData.getCurrentState()).formManager;
        }
        return null;
    }

    public static void setPosMiddle(Form form) {
        GameWindow window = WindowManager.getWindow();
        form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    default public void onWindowResized(GameWindow window) {
    }

    default public void onContainerClosed() {
    }
}

