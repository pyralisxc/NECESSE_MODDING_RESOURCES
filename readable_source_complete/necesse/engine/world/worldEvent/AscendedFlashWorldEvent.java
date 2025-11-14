/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldEvent;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.PausableSound;
import necesse.engine.util.tween.Easings;
import necesse.engine.util.tween.FloatTween;
import necesse.engine.util.tween.Playable;
import necesse.engine.util.tween.PlayableSequence;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.worldEvent.WorldEvent;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class AscendedFlashWorldEvent
extends WorldEvent {
    public float levelX;
    public float levelY;
    public long startTime;
    protected PlayableSequence sequence = new PlayableSequence();
    protected FloatTween sizeTween = new FloatTween(0.0f);
    protected FloatTween darknessTween = new FloatTween(0.0f);
    protected PausableSound sound;

    public AscendedFlashWorldEvent() {
        this.shouldSave = false;
    }

    public AscendedFlashWorldEvent(float levelX, float levelY, PausableSound sound) {
        this();
        this.levelX = levelX;
        this.levelY = levelY;
        this.sound = sound;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.levelX);
        writer.putNextFloat(this.levelY);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.levelX = reader.getNextFloat();
        this.levelY = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();
        if (this.isServer()) {
            this.over();
        }
        this.startTime = this.getLocalTime();
        this.sequence.addAfterPrevious((Playable<?>)this.sizeTween.newTween(1000.0, Float.valueOf(500.0f)).setEase(Easings.BackOut)).addAfterPrevious((Playable<?>)this.sizeTween.newTween(1500.0, Float.valueOf(2000.0f)).setEase(Easings.CubicIn)).addAtTheSameTime(this.darknessTween.newTween(1500.0, Float.valueOf(1.0f))).addAfterPrevious(this.sizeTween.newTween(1000.0, Float.valueOf(2000.0f))).addAtTheSameTime(this.darknessTween.newTween(1000.0, Float.valueOf(1.0f))).addAfterPrevious((Playable<?>)this.sizeTween.newTween(2000.0, Float.valueOf(0.0f)).setEase(Easings.CubicInOut)).addAtTheSameTime(this.darknessTween.newTween(2000.0, Float.valueOf(0.0f)));
        this.sequence.onComplete(this::over);
        this.sequence.play(this.startTime, this.startTime);
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.sequence.update(this.getLocalTime());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.sound != null) {
            this.sound = this.sound.gameTick();
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> sortedDrawables, OrderableDrawables tileDrawables, OrderableDrawables topDrawables, LevelDrawUtils.DrawArea drawArea, Level level, TickManager tickManager, GameCamera camera) {
        int size = (int)((Float)this.sizeTween.getValue()).floatValue();
        float darkness = ((Float)this.darknessTween.getValue()).floatValue();
        int drawX = camera.getDrawX(this.levelX);
        int drawY = camera.getDrawY(this.levelY);
        if (darkness > 0.0f) {
            GameWindow window = WindowManager.getWindow();
            final TextureDrawOptionsEnd drawOptions = Renderer.initQuadDraw(window.getSceneWidth(), window.getSceneHeight()).color(0.0f, 0.0f, 0.0f, darkness).pos(0, 0);
            topDrawables.add(2147483547, new Drawable(){

                @Override
                public void draw(TickManager tickManager) {
                    drawOptions.draw();
                }
            });
        }
        if (size > 0) {
            final TextureDrawOptionsEnd drawOptions = GameResources.flash.initDraw().size(size, size).posMiddle(drawX, drawY);
            topDrawables.add(Integer.MAX_VALUE, new Drawable(){

                @Override
                public void draw(TickManager tickManager) {
                    drawOptions.draw();
                }
            });
        }
    }
}

