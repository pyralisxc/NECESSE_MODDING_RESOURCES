/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.components.chat.ChatMessage;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.HudManager;
import necesse.level.maps.hudManager.floatText.FloatText;

public class ChatBubbleText
extends FloatText {
    static int maxWidth = 200;
    private final FontOptions fontOptions = new FontOptions(16);
    private int x;
    private int y;
    private Mob mob;
    private final String message;
    private FairTypeDrawOptions drawOptions;
    private int width;
    private int height;
    private long removeTime;

    public ChatBubbleText(int x, int y, String message) {
        this.x = x;
        this.y = y;
        this.message = message;
        this.mob = null;
    }

    public ChatBubbleText(Mob mob, String message) {
        this(mob.getX(), mob.getY(), message);
        this.mob = mob;
        this.x = mob.getX();
        this.y = mob.getY() - 50;
    }

    public void setMob(Mob mob) {
        this.mob = mob;
    }

    @Override
    public void init(HudManager manager) {
        super.init(manager);
        this.drawOptions = new FairType().append(this.fontOptions, this.message).applyParsers(ChatMessage.getParsers(this.fontOptions)).getDrawOptions(FairType.TextAlign.LEFT, maxWidth, true, true);
        Rectangle boundingBox = this.drawOptions.getBoundingBox();
        this.width = Math.max(20, boundingBox.x + boundingBox.width);
        this.height = boundingBox.y + boundingBox.height + 30;
        long stayTime = (long)Math.max((boundingBox.width * this.drawOptions.getLineCount() + 200) / 100, 3) * 1000L;
        this.removeTime = this.getTime() + stayTime;
        if (this.mob != null) {
            manager.removeElements(element -> {
                if (element != this && element instanceof ChatBubbleText) {
                    ChatBubbleText other = (ChatBubbleText)element;
                    return other.mob == this.mob;
                }
                return false;
            });
        }
    }

    @Override
    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
        if (this.isRemoved()) {
            return;
        }
        if (!camera.getBounds().intersects(this.getCollision())) {
            return;
        }
        if (this.getTime() >= this.removeTime) {
            this.remove();
            return;
        }
        if (this.mob != null) {
            Point drawPos = this.mob.getDrawPos();
            this.x = drawPos.x;
            this.y = drawPos.y - 50;
        }
        int drawX = camera.getDrawX(this.x) - 32;
        int drawY = camera.getDrawY(this.y);
        int additionX = this.width < 50 ? 50 - this.width : 0;
        final LinkedList<DrawOptions> options = new LinkedList<DrawOptions>();
        Rectangle boundingBox = this.drawOptions.getBoundingBox();
        int textHeight = boundingBox.y + boundingBox.height;
        options.add(Renderer.initQuadDraw(this.width + 4, textHeight + 4).pos(drawX + additionX, drawY - textHeight - 26));
        options.add(Settings.UI.chatbubble.initDraw().sprite(0, 0, 16).pos(drawX - 4 + additionX, drawY - textHeight - 30));
        options.add(Settings.UI.chatbubble.initDraw().sprite(1, 0, 16).pos(drawX + this.width + additionX, drawY - textHeight - 30));
        options.add(Renderer.initQuadDraw(this.width - 4, 2).color(new Color(40, 40, 40)).pos(drawX + 4 + additionX, drawY - textHeight - 30));
        options.add(Renderer.initQuadDraw(this.width - 4, 2).pos(drawX + 4 + additionX, drawY - textHeight - 28));
        options.add(Settings.UI.chatbubble.initDraw().sprite(0, 1, 16).pos(drawX - 4 + additionX, drawY - 26));
        options.add(Settings.UI.chatbubble.initDraw().sprite(1, 1, 16).pos(drawX + this.width + additionX, drawY - 26));
        options.add(Renderer.initQuadDraw(this.width + 4 - 8, 2).color(new Color(40, 40, 40)).pos(drawX + 4 + additionX, drawY - 20));
        options.add(Renderer.initQuadDraw(this.width - 4, 2).pos(drawX + 4 + additionX, drawY - 22));
        options.add(Renderer.initQuadDraw(2, textHeight - 4).color(new Color(40, 40, 40)).pos(drawX - 4 + additionX, drawY - textHeight - 22));
        options.add(Renderer.initQuadDraw(2, textHeight - 4).pos(drawX - 2 + additionX, drawY - textHeight - 22));
        options.add(Renderer.initQuadDraw(2, textHeight - 4).color(new Color(40, 40, 40)).pos(drawX + this.width + 6 + additionX, drawY - textHeight - 22));
        options.add(Renderer.initQuadDraw(2, textHeight - 4).pos(drawX + this.width + 4 + additionX, drawY - textHeight - 22));
        options.add(Settings.UI.chatbubble.initDraw().sprite(1, 0, 32).pos(drawX + 24, drawY - 34));
        options.add(() -> this.drawOptions.draw(drawX + 2 + additionX, drawY - this.height + 4, Color.BLACK));
        list.add(new SortedDrawable(){

            @Override
            public int getPriority() {
                return 1000;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.forEach(DrawOptions::draw);
            }
        });
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public Rectangle getCollision() {
        return new Rectangle(this.x - 38, this.y - this.height - 2, this.width + 16, this.height);
    }
}

