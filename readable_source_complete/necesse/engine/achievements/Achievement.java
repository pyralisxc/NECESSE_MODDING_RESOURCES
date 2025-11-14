/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.achievements;

import java.awt.Color;
import java.awt.Rectangle;
import java.time.Instant;
import necesse.engine.Settings;
import necesse.engine.achievements.AchievementProviderInterface;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.drawOptions.DrawOptionsBox;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.StringDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;

public abstract class Achievement {
    public final String stringID;
    private int dataID = -1;
    protected long completedTime;
    protected GameTexture completeTexture;
    protected GameTexture incompleteTexture;
    public final GameMessage name;
    public final GameMessage description;
    private boolean isDirty;

    public void setDataID(int dataID) {
        if (this.dataID != -1) {
            throw new IllegalArgumentException("Cannot set data id twice");
        }
        this.dataID = dataID;
    }

    public int getDataID() {
        return this.dataID;
    }

    public Achievement(String stringID, GameMessage name, GameMessage description) {
        this.stringID = stringID;
        this.name = name;
        this.description = description;
        this.completedTime = -1L;
    }

    public Achievement(String stringID, String nameLocalKey, String descriptionLocalKey) {
        this(stringID, new LocalMessage("achievement", nameLocalKey), new LocalMessage("achievement", descriptionLocalKey));
    }

    public abstract boolean isCompleted();

    public abstract void runStatsUpdate(ServerClient var1);

    public final long getTimeCompleted() {
        return this.completedTime;
    }

    protected void updateTimeCompleted() {
        this.completedTime = this.getNow();
        this.onAchivementUnlocked();
    }

    protected void onAchivementUnlocked() {
    }

    protected long getNow() {
        return Instant.now().toEpochMilli() / 1000L;
    }

    public void loadTextures(Achievement oldAchievement) {
        if (oldAchievement == null) {
            this.completeTexture = this.borderTexture(this.stringID + "_complete");
            this.incompleteTexture = this.borderTexture(this.stringID + "_incomplete");
        } else {
            this.completeTexture = oldAchievement.completeTexture;
            this.incompleteTexture = oldAchievement.incompleteTexture;
        }
    }

    protected GameTexture borderTexture(String path) {
        GameTexture border = GameTexture.fromFile("achievements/border", true);
        GameTexture mask = GameTexture.fromFile("achievements/border_mask", true);
        GameTexture sourceTexture = GameTexture.fromFile("achievements/" + path, true);
        GameTexture texture = new GameTexture(sourceTexture);
        sourceTexture.makeFinal();
        texture.merge(mask, 0, 0, MergeFunction.ALPHA_MASK);
        texture.merge(border, 0, 0, MergeFunction.NORMAL);
        texture.makeFinal();
        return texture;
    }

    public abstract void loadFromPlatform(AchievementProviderInterface var1);

    public void drawIcon(int x, int y) {
        GameTexture texture = this.isCompleted() ? this.completeTexture : this.incompleteTexture;
        if (texture == null) {
            texture = GameResources.error;
        }
        texture.initDraw().size(40, 40).draw(x, y);
    }

    public abstract void drawProgress(int var1, int var2, int var3, boolean var4);

    public abstract void addSaveData(SaveData var1);

    public abstract void applyLoadData(LoadData var1);

    public void addCompletedTimeSave(SaveData save) {
        save.addLong("time", this.completedTime);
    }

    public void applyCompletedTimeSave(LoadData save) {
        this.completedTime = save.getLong("time", -1L);
    }

    public abstract void setupContentPacket(PacketWriter var1);

    public abstract void applyContentPacket(PacketReader var1);

    public final void markDirty() {
        this.isDirty = true;
    }

    public void clean() {
        this.isDirty = false;
    }

    public final boolean isDirty() {
        return this.isDirty;
    }

    public static void drawProgressbar(int drawX, int drawY, int width, int height, float progress) {
        Achievement.drawProgressbarText(drawX, drawY, width, height, progress, null, null);
    }

    public static void drawProgressbar(int drawX, int drawY, int width, int height, float progress, Color inCompleteCol, Color completeCol) {
        Achievement.drawProgressbarText(drawX, drawY, width, height, progress, inCompleteCol, completeCol, null, null);
    }

    public static void drawProgressbarText(int drawX, int drawY, int width, int height, float progress, String text, Color textColor) {
        if (text != null && textColor == null) {
            textColor = progress == 1.0f ? Settings.UI.successTextColor : Settings.UI.errorTextColor;
        }
        Achievement.drawProgressbarText(drawX, drawY, width, height, progress, Settings.UI.progressBarOutline, Settings.UI.progressBarFill, text, textColor == null ? null : new FontOptions(16).color(textColor));
    }

    public static void drawProgressbarText(int drawX, int drawY, int width, int height, float progress, Color inCompleteCol, Color completeCol, String text, FontOptions fontOptions) {
        Achievement.getProgressbarTextDrawBox(drawX, drawY, width, height, progress, inCompleteCol, completeCol, text, fontOptions).draw();
    }

    public static DrawOptionsBox getProgressbarTextDrawBox(final int drawX, final int drawY, final int width, int height, float progress, Color inCompleteCol, Color completeCol, String text, FontOptions fontOptions) {
        final DrawOptionsList drawOptions = new DrawOptionsList();
        progress = Math.min(1.0f, Math.max(0.0f, progress));
        int maxHeight = height;
        int barDrawY = drawY;
        int barWidth = width;
        if (text != null) {
            int sWidth = FontManager.bit.getWidthCeil(text, fontOptions);
            int sHeight = FontManager.bit.getHeightCeil(text, fontOptions);
            barDrawY = drawY + sHeight / 2 - height / 2;
            barWidth = width - sWidth - 20;
            maxHeight = Math.max(maxHeight, sHeight);
            int sDrawX = drawX + width - sWidth - 10;
            drawOptions.add(new StringDrawOptions(fontOptions, text).pos(sDrawX, drawY));
        }
        int completeWidth = (int)((float)barWidth * progress);
        drawOptions.add(Renderer.initQuadDraw(barWidth + 4, height + 4).color(inCompleteCol).pos(drawX - 2, barDrawY - 2));
        drawOptions.add(Renderer.initQuadDraw(completeWidth, height).color(completeCol).pos(drawX, barDrawY));
        final int finalHeight = maxHeight;
        return new DrawOptionsBox(){

            @Override
            public Rectangle getBoundingBox() {
                return new Rectangle(drawX, drawY, width, finalHeight);
            }

            @Override
            public void draw() {
                drawOptions.draw();
            }
        };
    }
}

