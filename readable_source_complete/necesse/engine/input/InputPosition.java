/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input;

import java.awt.Point;
import java.util.function.Function;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.window.GameWindow;

public class InputPosition {
    public final int windowX;
    public final int windowY;
    public final int sceneX;
    public final int sceneY;
    public final int hudX;
    public final int hudY;

    InputPosition(int windowX, int windowY, int sceneX, int sceneY, int hudX, int hudY) {
        this.windowX = windowX;
        this.windowY = windowY;
        this.sceneX = sceneX;
        this.sceneY = sceneY;
        this.hudX = hudX;
        this.hudY = hudY;
    }

    public static InputPosition dummyPos() {
        return new InputPosition(0, 0, 0, 0, 0, 0);
    }

    public static InputPosition fromWindowPos(Input input, int windowX, int windowY) {
        return InputPosition.fromWindowPos(input.window, windowX, windowY);
    }

    public static InputPosition fromWindowPos(GameWindow window, int windowX, int windowY) {
        int sceneX = (int)((float)windowX * (float)window.getSceneWidth() / (float)window.getWidth());
        int sceneY = (int)((float)windowY * (float)window.getSceneHeight() / (float)window.getHeight());
        int hudX = (int)((float)windowX * (float)window.getHudWidth() / (float)window.getWidth());
        int hudY = (int)((float)windowY * (float)window.getHudHeight() / (float)window.getHeight());
        return new InputPosition(windowX, windowY, sceneX, sceneY, hudX, hudY);
    }

    public static InputPosition fromScenePos(Input input, int sceneX, int sceneY) {
        return InputPosition.fromScenePos(input.window, sceneX, sceneY);
    }

    public static InputPosition fromScenePos(GameWindow window, int sceneX, int sceneY) {
        int windowX = (int)((float)sceneX / ((float)window.getSceneWidth() / (float)window.getWidth()));
        int windowY = (int)((float)sceneY / ((float)window.getSceneHeight() / (float)window.getHeight()));
        int hudX = (int)((float)windowX * (float)window.getHudWidth() / (float)window.getWidth());
        int hudY = (int)((float)windowY * (float)window.getHudHeight() / (float)window.getHeight());
        return new InputPosition(windowX, windowY, sceneX, sceneY, hudX, hudY);
    }

    public static InputPosition fromHudPos(Input input, int hudX, int hudY) {
        return InputPosition.fromHudPos(input.window, hudX, hudY);
    }

    public static InputPosition fromHudPos(GameWindow window, int hudX, int hudY) {
        int windowX = (int)((float)hudX / ((float)window.getHudWidth() / (float)window.getWidth()));
        int windowY = (int)((float)hudY / ((float)window.getHudHeight() / (float)window.getHeight()));
        int sceneX = (int)((float)windowX * (float)window.getSceneWidth() / (float)window.getWidth());
        int sceneY = (int)((float)windowY * (float)window.getSceneHeight() / (float)window.getHeight());
        return new InputPosition(windowX, windowY, sceneX, sceneY, hudX, hudY);
    }

    public static enum InputPositionGetter {
        WINDOW(p -> new Point(p.windowX, p.windowY)),
        SCENE(p -> new Point(p.sceneX, p.sceneY)),
        HUD(p -> new Point(p.hudX, p.hudY));

        private final Function<InputPosition, Point> getter;

        private InputPositionGetter(Function<InputPosition, Point> getter) {
            this.getter = getter;
        }

        public Point get(InputPosition position) {
            return this.getter.apply(position);
        }

        public Point get(InputEvent event) {
            return this.get(event.pos);
        }
    }
}

