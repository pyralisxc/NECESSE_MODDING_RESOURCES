/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.tutorialPhases;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.state.MainGame;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameFont.FontOptions;

public class TutorialPhase {
    public static final Color TUTORIAL_TEXT_COLOR = new Color(200, 50, 50);
    public static final int TUTORIAL_TEXT_MAX_WIDTH = 200;
    public static final FontOptions TUTORIAL_TEXT_OPTIONS = new FontOptions(16).outline();
    public ClientTutorial tutorial;
    public Client client;
    private boolean isOver;

    public TutorialPhase(ClientTutorial tutorial, Client client) {
        this.tutorial = tutorial;
        this.client = client;
        this.isOver = false;
    }

    public void start() {
    }

    public void end() {
    }

    public void updateObjective(MainGame mainGame) {
    }

    public void tick() {
    }

    public void drawOverForm(PlayerMob perspective) {
    }

    public void over() {
        this.isOver = true;
    }

    public boolean isOver() {
        return this.isOver;
    }

    public void setObjective(MainGame mainGame, GameMessage objective) {
        mainGame.formManager.setTutorialContent(objective, null);
    }

    public void setObjective(MainGame mainGame, String objectiveKey) {
        this.setObjective(mainGame, new LocalMessage("tutorials", objectiveKey));
    }

    public void setObjective(MainGame mainGame, GameMessage objective, GameMessage button, FormEventListener<FormInputEvent> listener) {
        mainGame.formManager.setTutorialContent(objective, button, listener);
    }

    public void setObjective(MainGame mainGame, String objectiveKey, String buttonKey, FormEventListener<FormInputEvent> listener) {
        this.setObjective(mainGame, new LocalMessage("tutorials", objectiveKey), new LocalMessage("tutorials", buttonKey), listener);
    }

    public FairTypeDrawOptions getTextDrawOptions(FairType type) {
        return type.getDrawOptions(FairType.TextAlign.CENTER, 200, false, true);
    }

    public FairType getTutorialText(String message) {
        return new FairType().append(TUTORIAL_TEXT_OPTIONS, message);
    }

    public DrawOptions getLevelTextDrawOptions(FairTypeDrawOptions text, int levelX, int levelY, GameCamera camera, PlayerMob perspective, boolean drawArrow) {
        int drawX = camera.getDrawX(levelX);
        int drawY = camera.getDrawY(levelY);
        int distance = -1;
        Rectangle camBounds = camera.getBounds();
        camBounds.x += 25;
        camBounds.y += 25;
        camBounds.width -= 50;
        camBounds.width -= 50;
        if (!camBounds.contains(levelX, levelY)) {
            int screenSize = Math.min(camera.getWidth(), camera.getHeight());
            int widthHalf = camera.getWidth() / 2;
            int heightHalf = camera.getHeight() / 2;
            Point2D.Float dir = GameMath.normalize(levelX - (camera.getX() + widthHalf), levelY - (camera.getY() + heightHalf));
            drawX = widthHalf + (int)(dir.x * (float)screenSize / 3.0f);
            drawY = heightHalf + (int)(dir.y * (float)screenSize / 3.0f);
            distance = (int)perspective.getDistance(levelX, levelY);
        }
        FairTypeDrawOptions subtitle = null;
        if (distance != -1) {
            subtitle = new FairType().append(TUTORIAL_TEXT_OPTIONS, "\n(" + (int)GameMath.pixelsToMeters(distance) + "m)").getDrawOptions(FairType.TextAlign.CENTER, 200, false, true);
        }
        return this.getTextDrawOptions(text, subtitle, drawX, drawY, distance == -1 && drawArrow);
    }

    public DrawOptions getTextDrawOptions(FairTypeDrawOptions text, FairTypeDrawOptions subtitle, int drawX, int drawY, boolean drawArrow) {
        DrawOptionsList list = new DrawOptionsList();
        list.add(() -> {
            int y = drawY - (drawArrow ? 10 : 0);
            if (subtitle != null) {
                subtitle.draw(drawX, y -= subtitle.getBoundingBox().height, TUTORIAL_TEXT_COLOR);
            }
            if (text != null) {
                text.draw(drawX, y -= text.getBoundingBox().height, TUTORIAL_TEXT_COLOR);
            }
        });
        if (drawArrow) {
            list.add(Settings.UI.tutorial_arrow.initDraw().pos(drawX - Settings.UI.tutorial_arrow.getWidth() / 2, drawY - Settings.UI.tutorial_arrow.getHeight()));
        }
        return list;
    }
}

