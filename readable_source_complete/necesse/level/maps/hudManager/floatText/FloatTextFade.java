/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.StringDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.hudManager.HudManager;
import necesse.level.maps.hudManager.floatText.FloatText;

public class FloatTextFade
extends FloatText {
    public int riseTime;
    public int hoverTime;
    public int fadeTime;
    private long spawnTime;
    private String[] lines;
    protected FontOptions fontOptions;
    private int x;
    private int y;
    private int width;
    private int height;
    protected int heightIncrease;
    public boolean avoidOtherText;
    public Predicate<FloatTextFade> avoidOtherTextFilter;
    public int expandTime;

    public FloatTextFade(int x, int y, FontOptions fontOptions) {
        this.fontOptions = fontOptions;
        this.x = x;
        this.y = y;
        this.expandTime = 150;
        this.riseTime = 500;
        this.hoverTime = 0;
        this.fadeTime = 1000;
        this.heightIncrease = 50;
    }

    public FloatTextFade(int x, int y, String text, FontOptions fontOptions) {
        this(x, y, fontOptions);
        this.setText(text);
    }

    @Override
    public void init(HudManager manager) {
        block1: {
            super.init(manager);
            this.resetSpawnTime();
            if (!this.avoidOtherText) break block1;
            while (this.checkCollision(manager.getElements())) {
            }
        }
    }

    public void resetSpawnTime() {
        this.spawnTime = this.getTime();
    }

    public boolean isAlive() {
        return this.getTime() - this.spawnTime <= (long)(this.riseTime + this.hoverTime + this.fadeTime);
    }

    public float getLifeProgress() {
        return (float)(this.getTime() - this.spawnTime) / (float)(this.riseTime + this.hoverTime + this.fadeTime);
    }

    public String[] getLines() {
        return this.lines;
    }

    public String getText() {
        return GameUtils.join(this.lines, "\n");
    }

    public void setText(String text) {
        this.lines = text.split("\\n");
        this.width = 0;
        this.height = 0;
        for (String line : this.lines) {
            this.width = Math.max(this.width, FontManager.bit.getWidthCeil(line, this.fontOptions));
            this.height += FontManager.bit.getHeightCeil(line, this.fontOptions);
        }
    }

    @Override
    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
        if (this.isRemoved()) {
            return;
        }
        if (this.getLevel() == null) {
            return;
        }
        if (!camera.getBounds().intersects(this.getCollision())) {
            return;
        }
        if (!this.isAlive()) {
            this.remove();
            return;
        }
        long timeAlive = this.getTime() - this.spawnTime;
        int drawX = camera.getDrawX(this.getAnchorX());
        int drawY = camera.getDrawY(this.getY() - this.getHeight());
        float alpha = 1.0f;
        if (timeAlive >= (long)(this.riseTime + this.hoverTime)) {
            alpha = Math.abs((float)(timeAlive - (long)(this.riseTime + this.hoverTime)) / (float)this.fadeTime - 1.0f);
        }
        float size = 1.0f;
        if (timeAlive < (long)this.expandTime) {
            size = GameMath.limit((float)timeAlive / (float)this.expandTime, 0.0f, 1.0f);
        }
        FontOptions fontOptions = new FontOptions(this.fontOptions).size((int)((float)this.fontOptions.getSize() * size)).alphaf(alpha);
        final DrawOptionsList drawOptions = new DrawOptionsList();
        for (int i = 0; i < this.lines.length; ++i) {
            String line = this.lines[i];
            drawOptions.add(new StringDrawOptions(fontOptions, line).posCenterX(drawX, drawY));
            if (i >= this.lines.length - 1) continue;
            drawY += FontManager.bit.getHeightCeil(line, fontOptions);
        }
        list.add(new SortedDrawable(){

            @Override
            public int getPriority() {
                return 0;
            }

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    public int getAnchorX() {
        return this.x;
    }

    public int getAnchorY() {
        return this.y;
    }

    @Override
    public int getX() {
        return this.getAnchorX() - this.width / 2;
    }

    @Override
    public int getY() {
        return this.getAnchorY() - this.getCurrentHeightIncrease();
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public void setX(int x) {
        this.x = x + this.width / 2;
    }

    public void setY(int y) {
        this.y = y + this.getCurrentHeightIncrease();
    }

    private int getCurrentHeightIncrease() {
        float lifeProgress = Math.min(1.0f, (float)(this.getTime() - this.spawnTime) / (float)this.riseTime);
        return (int)((float)this.heightIncrease * GameMath.sin(lifeProgress * 90.0f));
    }

    public void setPos(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    @Override
    public Rectangle getCollision() {
        return new Rectangle(this.getAnchorX(), this.getAnchorY() - this.getHeight(), this.getWidth(), this.getHeight());
    }

    public boolean checkCollision(Iterable<HudDrawElement> elements) {
        for (HudDrawElement e : elements) {
            if (e.isRemoved() || e == this || !(e instanceof FloatTextFade)) continue;
            FloatTextFade other = (FloatTextFade)e;
            if (this.avoidOtherTextFilter != null && !this.avoidOtherTextFilter.test(other) || !other.collidesWith(this)) continue;
            int increase = this.getHeight() + 2 - (other.y - this.y);
            increase = Math.max(increase, 2);
            this.setY(this.getY() - increase);
            return true;
        }
        return false;
    }
}

