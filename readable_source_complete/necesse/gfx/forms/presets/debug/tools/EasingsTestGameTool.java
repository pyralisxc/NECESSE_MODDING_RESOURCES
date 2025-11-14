/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.util.ObjectValue;
import necesse.engine.util.tween.EaseFunction;
import necesse.engine.util.tween.Easings;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class EasingsTestGameTool
extends MouseDebugGameTool {
    private static final ArrayList<ObjectValue<EaseFunction, String>> easings = new ArrayList<ObjectValue>(Arrays.asList(new ObjectValue<EaseFunction, String>(Easings.Linear, "Linear"), new ObjectValue<EaseFunction, String>(Easings.SineIn, "SineIn"), new ObjectValue<EaseFunction, String>(Easings.SineOut, "SineOut"), new ObjectValue<EaseFunction, String>(Easings.SineInOut, "SineInOut"), new ObjectValue<EaseFunction, String>(Easings.QuadIn, "QuadIn"), new ObjectValue<EaseFunction, String>(Easings.QuadOut, "QuadOut"), new ObjectValue<EaseFunction, String>(Easings.QuadInOut, "QuadInOut"), new ObjectValue<EaseFunction, String>(Easings.CubicIn, "CubicIn"), new ObjectValue<EaseFunction, String>(Easings.CubicOut, "CubicOut"), new ObjectValue<EaseFunction, String>(Easings.CubicInOut, "CubicInOut"), new ObjectValue<EaseFunction, String>(Easings.QuartIn, "QuartIn"), new ObjectValue<EaseFunction, String>(Easings.QuartOut, "QuartOut"), new ObjectValue<EaseFunction, String>(Easings.QuartInOut, "QuartInOut"), new ObjectValue<EaseFunction, String>(Easings.QuintIn, "QuintIn"), new ObjectValue<EaseFunction, String>(Easings.QuintOut, "QuintOut"), new ObjectValue<EaseFunction, String>(Easings.QuintInOut, "QuintInOut"), new ObjectValue<EaseFunction, String>(Easings.ExpoIn, "ExpoIn"), new ObjectValue<EaseFunction, String>(Easings.ExpoOut, "ExpoOut"), new ObjectValue<EaseFunction, String>(Easings.ExpoInOut, "ExpoInOut"), new ObjectValue<EaseFunction, String>(Easings.CircIn, "CircIn"), new ObjectValue<EaseFunction, String>(Easings.CircOut, "CircOut"), new ObjectValue<EaseFunction, String>(Easings.CircInOut, "CircInOut"), new ObjectValue<EaseFunction, String>(Easings.BackIn, "BackIn"), new ObjectValue<EaseFunction, String>(Easings.BackOut, "BackOut"), new ObjectValue<EaseFunction, String>(Easings.BackInOut, "BackInOut"), new ObjectValue<EaseFunction, String>(Easings.ElasticIn, "ElasticIn"), new ObjectValue<EaseFunction, String>(Easings.ElasticOut, "ElasticOut"), new ObjectValue<EaseFunction, String>(Easings.ElasticInOut, "ElasticInOut"), new ObjectValue<EaseFunction, String>(Easings.BounceIn, "BounceIn"), new ObjectValue<EaseFunction, String>(Easings.BounceOut, "BounceOut"), new ObjectValue<EaseFunction, String>(Easings.BounceInOut, "BounceInOut")));
    public HudDrawElement hudElement;
    private ObjectValue<EaseFunction, String> easeFunction = easings.get(0);
    private long speed = 1000L;
    private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
    private double time = 0.0;
    private int easingIndex = 0;

    public EasingsTestGameTool(DebugForm parent, String name) {
        super(parent, name);
    }

    @Override
    public void init() {
        this.onLeftClick(e -> {
            --this.easingIndex;
            if (this.easingIndex < 0) {
                this.easingIndex = easings.size() - 1;
            }
            this.easeFunction = easings.get(this.easingIndex);
            return true;
        }, "Previous easing");
        this.onRightClick(e -> {
            ++this.easingIndex;
            if (this.easingIndex >= easings.size()) {
                this.easingIndex = 0;
            }
            this.easeFunction = easings.get(this.easingIndex);
            return true;
        }, "Next easing");
        this.onScroll(e -> {
            this.wheelBuffer.add((InputEvent)e);
            this.wheelBuffer.useScrollY(isPositive -> {
                int change = isPositive ? 1 : -1;
                double percent = this.time % (double)this.speed / (double)this.speed;
                this.speed = Math.max(100L, this.speed + (long)(change * 100));
                this.time = percent * (double)this.speed;
            });
            return true;
        }, "Change simulation speed");
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
                        EasingsTestGameTool.this.time += tickManager.getDelta();
                        Easings.drawEasing((EaseFunction)((EasingsTestGameTool)EasingsTestGameTool.this).easeFunction.object, EasingsTestGameTool.this.time % (double)EasingsTestGameTool.this.speed / (double)EasingsTestGameTool.this.speed, 100, WindowManager.getWindow().mousePos().sceneX - 110, WindowManager.getWindow().mousePos().sceneY - 100, 100.0, Color.red);
                        FontManager.bit.drawString(WindowManager.getWindow().mousePos().sceneX, WindowManager.getWindow().mousePos().sceneY + 10, (String)((EasingsTestGameTool)EasingsTestGameTool.this).easeFunction.value, new FontOptions(10));
                        FontManager.bit.drawString(WindowManager.getWindow().mousePos().sceneX, WindowManager.getWindow().mousePos().sceneY + 20, EasingsTestGameTool.this.speed + " ms", new FontOptions(10));
                    }
                });
            }
        };
        this.getLevel().hudManager.addElement(this.hudElement);
    }
}

