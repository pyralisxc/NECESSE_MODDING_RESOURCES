/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameMath;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.ui.HUD;
import org.lwjgl.opengl.GL11;

public class FormColorHuePicker
extends FormComponent
implements FormPositionContainer {
    public Color backgroundColor = GameBackground.form.getCenterColor();
    public int circleExtraSize = 10;
    private FormPosition position;
    private final int width;
    private final int height;
    private Point mouseDownPoint;
    private InputEvent mouseDownEvent;
    private boolean isControllerSelected;
    private float lastHue;
    private float currentHue;
    protected FormEventsHandler<FormEvent<FormColorHuePicker>> changedEvents = new FormEventsHandler();

    public FormColorHuePicker(int x, int y, int width, int height, float startHue) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.height = height;
        this.lastHue = startHue;
        this.currentHue = startHue;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        int x;
        if (event.isMouseMoveEvent()) {
            event = InputEvent.MouseMoveEvent(WindowManager.getWindow().mousePos(), tickManager);
            if (this.mouseDownPoint != null) {
                x = GameMath.limit(this.mouseDownPoint.x + event.pos.hudX - this.mouseDownEvent.pos.hudX, 0, this.width);
                this.currentHue = FormColorHuePicker.getHueAt(x, this.width);
                this.queryNewColor();
            }
        }
        if (event.getID() == -100) {
            if (!event.state) {
                if (this.mouseDownPoint != null) {
                    event.use();
                    this.mouseDownPoint = null;
                    if (event.pos.hudX != Integer.MIN_VALUE && event.pos.hudY != Integer.MIN_VALUE) {
                        x = GameMath.limit(event.pos.hudX - this.getX(), 0, this.width);
                        this.currentHue = FormColorHuePicker.getHueAt(x, this.width);
                        this.queryNewColor();
                    }
                }
            } else if (this.isMouseOver(event)) {
                event.use();
                this.mouseDownEvent = InputEvent.MouseMoveEvent(WindowManager.getWindow().mousePos(), tickManager);
                x = GameMath.limit(event.pos.hudX - this.getX(), 0, this.width);
                this.mouseDownPoint = new Point(x, 0);
                this.currentHue = FormColorHuePicker.getHueAt(x, this.width);
                this.queryNewColor();
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.getState() == ControllerInput.MENU_SELECT) {
            if (this.isControllerFocus() && event.buttonState) {
                this.isControllerSelected = true;
                event.use();
            }
        } else if ((event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU) && this.isControllerSelected && event.buttonState) {
            this.isControllerSelected = false;
            event.use();
        }
    }

    @Override
    public void onControllerUnfocused(ControllerFocus current) {
        super.onControllerUnfocused(current);
        this.isControllerSelected = false;
    }

    @Override
    public boolean handleControllerNavigate(int dir, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isControllerSelected) {
            switch (dir) {
                case 1: {
                    if (this.currentHue < 1.0f) {
                        this.currentHue = Math.min(this.currentHue + 0.02f, 1.0f);
                        this.queryNewColor();
                    }
                    event.use();
                    break;
                }
                case 3: {
                    if (this.currentHue > 0.0f) {
                        this.currentHue = Math.max(this.currentHue - 0.02f, 0.0f);
                        this.queryNewColor();
                    }
                    event.use();
                }
            }
            return true;
        }
        return super.handleControllerNavigate(dir, event, tickManager, perspective);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    protected void queryNewColor() {
        if (this.lastHue != this.currentHue) {
            this.lastHue = this.currentHue;
            FormEvent<FormColorHuePicker> event = new FormEvent<FormColorHuePicker>(this);
            this.changedEvents.onEvent(event);
        }
    }

    public FormColorHuePicker onChanged(FormEventListener<FormEvent<FormColorHuePicker>> listener) {
        this.changedEvents.addListener(listener);
        return this;
    }

    public float getSelectedHue() {
        return this.currentHue;
    }

    public void setSelectedHue(float hue) {
        this.currentHue = hue;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        FormColorHuePicker.drawHueBar(this.getX(), this.getY(), this.width, this.height);
        int selectedHueX = this.getX() + (int)(this.currentHue * (float)this.width);
        int selectedHueRadius = this.height / 2 + this.circleExtraSize / 2;
        int selectedHueCenterX = GameMath.limit(selectedHueX, this.getX() + selectedHueRadius - 2, this.getX() + this.width - selectedHueRadius + 2);
        Renderer.drawCircle(selectedHueCenterX, this.getY() + this.height / 2, selectedHueRadius, 30, this.backgroundColor, true);
        Renderer.drawCircle(selectedHueCenterX, this.getY() + this.height / 2, selectedHueRadius - 2, 30, Color.getHSBColor(this.currentHue, 1.0f, 1.0f), true);
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        if (this.isControllerSelected) {
            Rectangle box = current.boundingBox;
            int padding = 5;
            box = new Rectangle(box.x - padding, box.y - padding, box.width + padding * 2, box.height + padding * 2);
            HUD.selectBoundOptions(this.getInterfaceStyle().controllerFocusBoundsHighlightColor, true, box).draw();
        } else {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }

    public static Color getColorAt(int x, int y, int width, int height, float hue) {
        x = GameMath.limit(x, 0, width);
        y = GameMath.limit(y, 0, height);
        float sat = (float)x / (float)width;
        float val = Math.abs((float)y / (float)height - 1.0f);
        return Color.getHSBColor(hue, sat, val);
    }

    public static float getHueAt(int x, int width) {
        x = GameMath.limit(x, 0, width);
        return (float)x / (float)width;
    }

    public static void drawHueBar(int x, int y, int width, int height) {
        GameTexture.unbindTexture();
        GL11.glLoadIdentity();
        GL11.glBegin((int)1);
        for (int i = 0; i <= width; ++i) {
            float currentPerc = (float)i / (float)width;
            Color c = Color.getHSBColor(currentPerc, 1.0f, 1.0f);
            GL11.glColor4f((float)((float)c.getRed() / 255.0f), (float)((float)c.getGreen() / 255.0f), (float)((float)c.getBlue() / 255.0f), (float)1.0f);
            GL11.glVertex2f((float)(x + i), (float)y);
            GL11.glVertex2f((float)(x + i), (float)(y + height));
        }
        GL11.glEnd();
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormColorHuePicker.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public static float[] RGBtoHSL(float r, float g, float b) {
        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));
        float h = 0.0f;
        if (max == min) {
            h = 0.0f;
        } else if (max == r) {
            h = (60.0f * (g - b) / (max - min) + 360.0f) % 360.0f;
        } else if (max == g) {
            h = 60.0f * (b - r) / (max - min) + 120.0f;
        } else if (max == b) {
            h = 60.0f * (r - g) / (max - min) + 240.0f;
        }
        float l = (max + min) / 2.0f;
        float s = max == min ? 0.0f : (l <= 0.5f ? (max - min) / (max + min) : (max - min) / (2.0f - max - min));
        return new float[]{h / 360.0f, s, l};
    }

    public static float[] HSLtoRGB(float h, float s, float l) {
        float q = (double)l < 0.5 ? l * (1.0f + s) : l + s - s * l;
        float p = 2.0f * l - q;
        float r = Math.max(0.0f, FormColorHuePicker.HueToRGB(p, q, h + 0.33333334f));
        float g = Math.max(0.0f, FormColorHuePicker.HueToRGB(p, q, h));
        float b = Math.max(0.0f, FormColorHuePicker.HueToRGB(p, q, h - 0.33333334f));
        r = Math.min(r, 1.0f);
        g = Math.min(g, 1.0f);
        b = Math.min(b, 1.0f);
        return new float[]{r, g, b};
    }

    public static float[] toHSL(Color color) {
        return FormColorHuePicker.RGBtoHSL((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f);
    }

    public static Color fromHSL(float h, float s, float l) {
        return FormColorHuePicker.fromHSL(h, s, l, 1.0f);
    }

    public static Color fromHSL(float h, float s, float l, float alpha) {
        float[] rgb = FormColorHuePicker.HSLtoRGB(h, s, l);
        return new Color(rgb[0], rgb[1], rgb[2], alpha);
    }

    private static float HueToRGB(float p, float q, float h) {
        if (h < 0.0f) {
            h += 1.0f;
        }
        if (h > 1.0f) {
            h -= 1.0f;
        }
        if (6.0f * h < 1.0f) {
            return p + (q - p) * 6.0f * h;
        }
        if (2.0f * h < 1.0f) {
            return q;
        }
        if (3.0f * h < 2.0f) {
            return p + (q - p) * 6.0f * (0.6666667f - h);
        }
        return p;
    }
}

