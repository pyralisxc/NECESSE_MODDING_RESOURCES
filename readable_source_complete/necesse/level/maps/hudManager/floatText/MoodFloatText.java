/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsStart;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.hudManager.HudManager;
import necesse.level.maps.hudManager.floatText.FloatText;

public class MoodFloatText
extends FloatText {
    private int padding = 2;
    private int maxWidth = 100;
    private final Mob mob;
    private final int stayTime;
    private long removeTime;
    private final List<Moods> moods;
    private MoodDrawOptionsBox draws;

    public MoodFloatText(Mob mob, int stayTime, List<Moods> moods) {
        this.mob = mob;
        this.stayTime = stayTime;
        this.moods = moods;
    }

    public MoodFloatText(Mob mob, int stayTime, Moods ... moods) {
        this(mob, stayTime, Arrays.asList(moods));
    }

    @Override
    public void init(HudManager manager) {
        super.init(manager);
        manager.removeElements(element -> {
            if (element != this && element instanceof MoodFloatText) {
                MoodFloatText other = (MoodFloatText)element;
                return this.mob.getUniqueID() == other.mob.getUniqueID();
            }
            return false;
        });
        this.removeTime = this.getTime() + (long)this.stayTime;
    }

    public MoodFloatText setPadding(int padding) {
        if (this.padding != padding) {
            this.padding = padding;
            this.draws = null;
        }
        return this;
    }

    public MoodFloatText setMaxWidth(int maxWidth) {
        if (this.maxWidth != maxWidth) {
            this.maxWidth = maxWidth;
            this.draws = null;
        }
        return this;
    }

    private MoodDrawOptionsBox getDraws() {
        if (this.draws == null) {
            int totalWidth = 0;
            int totalHeight = 0;
            final ArrayList<MoodLine> lines = new ArrayList<MoodLine>();
            MoodLine currentLine = null;
            for (Moods mood : this.moods) {
                if (currentLine == null) {
                    if (!lines.isEmpty()) {
                        totalHeight += this.padding;
                    }
                    currentLine = new MoodLine();
                    lines.add(0, currentLine);
                } else {
                    currentLine.width += this.padding;
                }
                MoodDrawOptionsBox draw = mood.drawMethod.getMoodDraw();
                Dimension boundingBox = draw.getBoundingBox();
                currentLine.width += boundingBox.width;
                totalWidth = Math.max(totalWidth, currentLine.width);
                if (currentLine.height < boundingBox.height) {
                    totalHeight -= currentLine.height;
                    totalHeight += boundingBox.height;
                    currentLine.height = boundingBox.height;
                }
                currentLine.draws.add(draw);
                if (currentLine.width <= this.maxWidth) continue;
                currentLine = null;
            }
            int yOffset = 0;
            boolean firstLine = true;
            for (MoodLine line : lines) {
                ListIterator<MoodDrawOptionsBox> li = line.draws.listIterator();
                int widthDelta = totalWidth - line.width;
                int xOffset = widthDelta / 2;
                boolean firstDraw = true;
                if (!firstLine) {
                    yOffset += this.padding;
                }
                while (li.hasNext()) {
                    if (!firstDraw) {
                        xOffset += this.padding;
                    }
                    MoodDrawOptionsBox next = li.next();
                    Dimension box = next.getBoundingBox();
                    int heightDelta = line.height - box.height;
                    li.set(next.offset(xOffset, yOffset + heightDelta / 2));
                    xOffset += box.width;
                    firstDraw = false;
                }
                yOffset += line.height;
                firstLine = false;
            }
            final int finalWidth = totalWidth;
            final int finalHeight = totalHeight;
            this.draws = new MoodDrawOptionsBox(){

                @Override
                public Dimension getBoundingBox() {
                    return new Dimension(finalWidth, finalHeight);
                }

                @Override
                public void draw(int drawX, int drawY, float alpha) {
                    for (MoodLine line : lines) {
                        for (MoodDrawOptionsBox draw : line.draws) {
                            draw.draw(drawX, drawY, alpha);
                        }
                    }
                }
            };
        }
        return this.draws;
    }

    @Override
    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
        if (this.isRemoved() || this.mob.removed()) {
            return;
        }
        if (this.getTime() >= this.removeTime) {
            this.remove();
            return;
        }
        if (!camera.getBounds().intersects(this.getCollision())) {
            return;
        }
        final int drawX = camera.getDrawX(this.getX());
        final int drawY = camera.getDrawY(this.getY());
        final float alpha = Math.min(1.0f, GameMath.lerp(GameUtils.getAnimFloatContinuous(this.removeTime - this.getTime(), 1000), 0.2f, 1.4f));
        final MoodDrawOptionsBox draws = this.getDraws();
        list.add(new SortedDrawable(){

            @Override
            public int getPriority() {
                return 0;
            }

            @Override
            public void draw(TickManager tickManager) {
                draws.draw(drawX, drawY, alpha);
            }
        });
    }

    @Override
    public int getX() {
        Rectangle selectBox = this.mob.getSelectBox(this.mob.getDrawX(), this.mob.getDrawY());
        return selectBox.x + selectBox.width / 2 - this.getDraws().getBoundingBox().width / 2;
    }

    @Override
    public int getY() {
        Rectangle selectBox = this.mob.getSelectBox(this.mob.getDrawX(), this.mob.getDrawY());
        return selectBox.y - (this.mob.isHealthBarVisible() ? 10 : 5) - this.getDraws().getBoundingBox().height;
    }

    @Override
    public int getWidth() {
        return this.getDraws().getBoundingBox().width;
    }

    @Override
    public int getHeight() {
        return this.getDraws().getBoundingBox().height;
    }

    private static interface MoodDrawOptionsBox {
        public Dimension getBoundingBox();

        public void draw(int var1, int var2, float var3);

        default public MoodDrawOptionsBox offset(final int xOffset, final int yOffset) {
            final MoodDrawOptionsBox me = this;
            return new MoodDrawOptionsBox(){

                @Override
                public Dimension getBoundingBox() {
                    return me.getBoundingBox();
                }

                @Override
                public void draw(int drawX, int drawY, float alpha) {
                    me.draw(drawX + xOffset, drawY + yOffset, alpha);
                }
            };
        }
    }

    public static enum Moods {
        HUNGRY(100, () -> Settings.UI.settler_mood_hungry, (GameMessage)new LocalMessage("ui", "settlermoodhungry")),
        INVENTORY_FULL(50, () -> Settings.UI.settler_mood_full_inventory, (GameMessage)new LocalMessage("ui", "settlermoodinventoryfull")),
        NO_BED(0, () -> Settings.UI.settler_mood_no_bed, (GameMessage)new LocalMessage("ui", "settlermoodnobed")),
        BED_OUTSIDE(0, () -> Settings.UI.settler_mood_no_bed, (GameMessage)new LocalMessage("ui", "settlermoodbedoutside"));

        public final int priority;
        private final MoodDrawMethod drawMethod;
        public GameMessage moodMessage;

        private Moods(int priority, MoodDrawMethod drawMethod, GameMessage moodMessage) {
            this.priority = priority;
            this.drawMethod = drawMethod;
            this.moodMessage = moodMessage;
        }

        private Moods(int priority, Supplier<GameTexture> textureSupplier, GameMessage moodMessage) {
            this(priority, () -> {
                final GameTexture texture = (GameTexture)textureSupplier.get();
                final TextureDrawOptionsStart options = texture.initDraw();
                return new MoodDrawOptionsBox(){

                    @Override
                    public Dimension getBoundingBox() {
                        return new Dimension(texture.getWidth(), texture.getHeight());
                    }

                    @Override
                    public void draw(int drawX, int drawY, float alpha) {
                        options.alpha(alpha).draw(drawX, drawY);
                    }
                };
            }, moodMessage);
        }
    }

    private static class MoodLine {
        public ArrayList<MoodDrawOptionsBox> draws = new ArrayList();
        public int width;
        public int height;

        private MoodLine() {
        }
    }

    private static interface MoodDrawMethod {
        public MoodDrawOptionsBox getMoodDraw();
    }
}

