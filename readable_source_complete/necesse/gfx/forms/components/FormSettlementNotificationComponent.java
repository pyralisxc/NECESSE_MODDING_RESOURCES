/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationManager;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationSeverity;

public class FormSettlementNotificationComponent
extends FormButton
implements FormPositionContainer {
    private FormPosition position;
    private final SettlementNotificationManager.ActiveNotification notification;
    public final SettlementNotificationSeverity severity;
    private boolean stopBounce;

    public FormSettlementNotificationComponent(int x, int y, SettlementNotificationManager.ActiveNotification notification, SettlementNotificationSeverity severity) {
        this.position = new FormFixedPosition(x, y);
        this.notification = notification;
        this.severity = severity;
    }

    public GameTexture getBaseTexture() {
        return this.notification.getHighestSeverity().baseTexture.get();
    }

    public GameTexture getShadowTexture() {
        return this.notification.getHighestSeverity().shadowTexture.get();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Color drawCol = this.getDrawColor();
        boolean useDownTexture = this.isButtonDown();
        int drawY = this.getY();
        if (useDownTexture) {
            drawY += 2;
        }
        int bounce = 0;
        if (this.isHovering()) {
            this.stopBounce = true;
        } else if (this.severity != SettlementNotificationSeverity.NOTE) {
            bounce = GameUtils.getBounceAnim(perspective == null ? System.currentTimeMillis() : perspective.getLocalTime(), 30, 2000, 4000, 1700, -1);
            if (bounce == -1) {
                bounce = 0;
                this.stopBounce = false;
            }
            if (this.stopBounce) {
                bounce = 0;
            }
        }
        GameTexture shadowTexture = this.getShadowTexture();
        GameTexture texture = this.getBaseTexture();
        int shadowXOffset = (texture.getWidth() - shadowTexture.getWidth()) / 2;
        int shadowYOffset = (texture.getHeight() - shadowTexture.getHeight()) / 2;
        shadowTexture.initDraw().draw(this.getX() + shadowXOffset + bounce, drawY + shadowYOffset);
        texture.initDraw().color(drawCol).draw(this.getX() + bounce, drawY);
        if (this.isHovering()) {
            this.addTooltips(perspective);
        }
    }

    protected void addTooltips(PlayerMob perspective) {
        GameTooltips tooltip = this.notification.notification.getTooltip(this.notification);
        if (tooltip != null) {
            GameTooltipManager.addTooltip(tooltip, TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        GameTexture texture = this.getBaseTexture();
        return FormSettlementNotificationComponent.singleBox(new Rectangle(this.getX(), this.getY(), texture.getWidth(), texture.getHeight()));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public boolean isButtonDown() {
        return this.isDown() && this.isHovering();
    }
}

