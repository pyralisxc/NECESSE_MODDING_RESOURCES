/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.PlayerSprite;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.light.GameLight;

public class FormPlayerIcon
extends FormButton
implements FormPositionContainer {
    private FormPosition position;
    private final int width;
    private final int height;
    private PlayerMob player;
    private final int spriteX;
    private int spriteY;

    public FormPlayerIcon(int x, int y, int width, int height, PlayerMob player) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.height = height;
        this.player = player;
        this.spriteX = 0;
        this.spriteY = 2;
    }

    public void setPlayer(PlayerMob player) {
        this.player = player;
    }

    public void setRotation(int rotation) {
        this.spriteY = rotation = Math.floorMod(rotation, 4);
    }

    public int getRotation() {
        return this.spriteY;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        PlayerSprite.drawInForms((drawX, drawY) -> {
            GameTexture.overrideBlendQuality = GameTexture.BlendQuality.NEAREST;
            PlayerSprite.getIconDrawOptions(this.width, this.height, this.player, this.spriteX, this.spriteY, new GameLight(150.0f), this::modifyHumanDrawOptions).pos(drawX, drawY).draw();
            GameTexture.overrideBlendQuality = null;
        }, this.getX(), this.getY());
    }

    public void modifyHumanDrawOptions(HumanDrawOptions drawOptions) {
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormPlayerIcon.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}

