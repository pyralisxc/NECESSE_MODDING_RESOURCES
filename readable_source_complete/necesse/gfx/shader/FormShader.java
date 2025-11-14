/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;
import necesse.engine.GlobalData;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.Renderer;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderBooleanVariable;

public class FormShader
extends GameShader {
    private FormShaderState currentState = null;

    public FormShader() {
        super("vertForm", "fragForm");
        this.addVariable(new ShaderBooleanVariable("drawOutside"));
    }

    public FormShaderState startState(Point offset, Rectangle drawLimit) {
        this.currentState = new FormShaderState(this.currentState, offset, drawLimit);
        this.currentState.apply();
        return this.currentState;
    }

    @Override
    public void use() {
        super.use();
        GameWindow window = WindowManager.getWindow();
        this.currentState = new FormShaderState(null, new Point(0, 0), new Rectangle(0, 0, window.getHudWidth(), window.getHudHeight()));
        this.currentState.apply();
    }

    public void usePrevState() {
        super.use();
        if (this.currentState != null) {
            this.currentState.apply();
        }
    }

    public Point getCurrentOffset() {
        if (this.currentState == null) {
            return new Point(0, 0);
        }
        return this.currentState.offset;
    }

    public Rectangle getCurrentDrawLimit() {
        if (this.currentState == null) {
            return new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        return this.currentState.drawLimit;
    }

    public InputPosition getCurrentMousePos() {
        if (this.currentState == null) {
            return WindowManager.getWindow().mousePos();
        }
        return this.currentState.mouseEvent.pos;
    }

    public class FormShaderState {
        private final FormShaderState prevState;
        public final Point offset;
        public final Rectangle drawLimit;
        public final InputEvent mouseEvent;

        private FormShaderState(FormShaderState prevState, Point offset, Rectangle drawLimit) {
            this.prevState = prevState;
            if (prevState != null) {
                if (offset == null) {
                    offset = prevState.offset;
                } else {
                    offset.x += prevState.offset.x;
                    offset.y += prevState.offset.y;
                }
            }
            Objects.requireNonNull(offset);
            this.offset = offset = new Point(offset);
            if (drawLimit != null) {
                drawLimit = new Rectangle(drawLimit);
                drawLimit.x += offset.x;
                drawLimit.y += offset.y;
                if (prevState != null) {
                    drawLimit = prevState.drawLimit.intersection(drawLimit);
                }
            } else {
                drawLimit = prevState != null ? new Rectangle(prevState.drawLimit) : new Rectangle(0, 0, WindowManager.getWindow().getHudWidth(), WindowManager.getWindow().getHudHeight());
            }
            this.drawLimit = drawLimit;
            GameWindow window = WindowManager.getWindow();
            int mouseX = Integer.MIN_VALUE;
            if (window.mousePos().hudX >= drawLimit.x && window.mousePos().hudX < drawLimit.x + drawLimit.width) {
                mouseX = window.mousePos().hudX - offset.x;
            }
            int mouseY = Integer.MIN_VALUE;
            if (window.mousePos().hudY >= drawLimit.y && window.mousePos().hudY < drawLimit.y + drawLimit.height) {
                mouseY = window.mousePos().hudY - offset.y;
            }
            this.mouseEvent = InputEvent.MouseMoveEvent(InputPosition.fromHudPos(window.getInput(), mouseX, mouseY), GlobalData.getCurrentGameLoop());
        }

        private void apply() {
            GameFrameBuffer currentBuffer = WindowManager.getWindow().getCurrentBuffer();
            FormShader.this.pass2f("pixelOffset", (float)this.offset.x / (float)currentBuffer.getWidth() * 2.0f, (float)this.offset.y / (float)currentBuffer.getHeight() * 2.0f);
            int y = -this.drawLimit.height + currentBuffer.getHeight() - this.drawLimit.y;
            FormShader.this.pass4f("drawLimit", this.drawLimit.x, y, this.drawLimit.width, this.drawLimit.height);
        }

        public void end() {
            if (this.prevState == null) {
                throw new IllegalStateException("Cannot end Form shader state: Not started yet");
            }
            FormShader.this.currentState = this.prevState;
            FormShader.this.currentState.apply();
        }

        public void drawDebugRects() {
            Renderer.initQuadDraw(this.drawLimit.x + this.drawLimit.width - this.offset.x, this.drawLimit.y + this.drawLimit.height - this.offset.y).color(new Color(0, 0, 255, 100)).draw(0, 0);
        }
    }
}

