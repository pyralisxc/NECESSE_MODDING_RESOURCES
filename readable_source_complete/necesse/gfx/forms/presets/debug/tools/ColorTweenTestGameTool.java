/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.tween.ColorTween;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.presets.ColorSelectorForm;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class ColorTweenTestGameTool
extends MouseDebugGameTool {
    public HudDrawElement hudElement;
    private Color leftColor = Color.red;
    private Color rightColor = Color.blue;

    public ColorTweenTestGameTool(DebugForm parent, String name) {
        super(parent, name);
    }

    @Override
    public void init() {
        final FormManager formManager = this.parent.getManager();
        this.onKeyClick(74, e -> {
            formManager.addComponent(new ColorSelectorForm("Left color", this.leftColor){

                @Override
                public void onApplied(Color color) {
                    if (color != null) {
                        ColorTweenTestGameTool.this.leftColor = color;
                    }
                    formManager.removeComponent(this);
                }

                @Override
                public void onSelected(Color color) {
                    if (color != null) {
                        ColorTweenTestGameTool.this.leftColor = color;
                    }
                }
            });
            return true;
        }, "Change left color");
        this.onKeyClick(75, e -> {
            formManager.addComponent(new ColorSelectorForm("Right color", this.rightColor){

                @Override
                public void onApplied(Color color) {
                    if (color != null) {
                        ColorTweenTestGameTool.this.rightColor = color;
                    }
                    formManager.removeComponent(this);
                }

                @Override
                public void onSelected(Color color) {
                    if (color != null) {
                        ColorTweenTestGameTool.this.rightColor = color;
                    }
                }
            });
            return true;
        }, "Change right color");
        this.setupHudElement();
    }

    @Override
    public void isCancelled() {
        super.isCancelled();
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }

    @Override
    public void isCleared() {
        super.isCleared();
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }

    public void setupHudElement() {
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                list.add(new SortedDrawable(){

                    @Override
                    public int getPriority() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        int x = WindowManager.getWindow().mousePos().sceneX;
                        int y = WindowManager.getWindow().mousePos().sceneY;
                        ColorTween tween = new ColorTween(1.0, ColorTweenTestGameTool.this.leftColor, ColorTweenTestGameTool.this.rightColor);
                        ColorTween alphaTween = new ColorTween(1.0, ColorTweenTestGameTool.this.leftColor, new Color(ColorTweenTestGameTool.this.rightColor.getRed(), ColorTweenTestGameTool.this.rightColor.getGreen(), ColorTweenTestGameTool.this.rightColor.getBlue(), 0));
                        tween.play(0.0, 0.0);
                        alphaTween.play(0.0, 0.0);
                        FontManager.bit.drawString(x, y + 20, ColorTween.InterpolationMode.Linear.name(), new FontOptions(8));
                        tween.setInterpolationMode(ColorTween.InterpolationMode.Linear);
                        alphaTween.setInterpolationMode(ColorTween.InterpolationMode.Linear);
                        ColorTween.drawTweenDebug(tween, x, y + 30, 150, 5);
                        ColorTween.drawTweenDebug(alphaTween, x, y + 30 + 5, 150, 5);
                        FontManager.bit.drawString(x, y + 50, ColorTween.InterpolationMode.Hue.name(), new FontOptions(8));
                        tween.restart(0.0);
                        alphaTween.restart(0.0);
                        tween.setInterpolationMode(ColorTween.InterpolationMode.Hue);
                        alphaTween.setInterpolationMode(ColorTween.InterpolationMode.Hue);
                        ColorTween.drawTweenDebug(tween, x, y + 60, 150, 5);
                        ColorTween.drawTweenDebug(alphaTween, x, y + 60 + 5, 150, 5);
                        FontManager.bit.drawString(x, y + 80, ColorTween.InterpolationMode.HueLong.name(), new FontOptions(8));
                        tween.restart(0.0);
                        alphaTween.restart(0.0);
                        tween.setInterpolationMode(ColorTween.InterpolationMode.HueLong);
                        alphaTween.setInterpolationMode(ColorTween.InterpolationMode.HueLong);
                        ColorTween.drawTweenDebug(tween, x, y + 90, 150, 5);
                        ColorTween.drawTweenDebug(alphaTween, x, y + 90 + 5, 150, 5);
                    }
                });
            }
        };
        this.getLevel().hudManager.addElement(this.hudElement);
    }
}

