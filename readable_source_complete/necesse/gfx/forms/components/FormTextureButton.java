/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;

public class FormTextureButton
extends FormButton
implements FormPositionContainer {
    private FormPosition position;
    public Supplier<GameSprite> textureSupplier;
    private final int maxWidth;
    private final int maxHeight;
    public FairType.TextAlign xAlign;
    public FairType.TextAlign yAlign;
    public GameBackground background;

    public FormTextureButton(int x, int y, Supplier<GameSprite> textureSupplier, int maxWidth, int maxHeight, FairType.TextAlign xAlign, FairType.TextAlign yAlign) {
        this.position = new FormFixedPosition(x, y);
        this.textureSupplier = textureSupplier;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.xAlign = xAlign == null ? FairType.TextAlign.LEFT : xAlign;
        this.yAlign = yAlign == null ? FairType.TextAlign.LEFT : yAlign;
    }

    public FormTextureButton(int x, int y, Supplier<GameTexture> textureSupplier, int maxWidth, int maxHeight) {
        this(x, y, () -> new GameSprite((GameTexture)textureSupplier.get()), maxWidth, maxHeight, null, null);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Color drawCol = this.handleClicksIfNoEventHandlers || this.clickedEvents.hasListeners() || this.dragStartedEvents.hasListeners() ? this.getDrawColor() : new Color(1.0f, 1.0f, 1.0f);
        TextureDrawOptionsEnd drawOptions = this.getDrawOptions(false).color(drawCol);
        int drawX = this.getX();
        int drawY = this.getY();
        if (this.xAlign == FairType.TextAlign.CENTER) {
            drawX -= drawOptions.getWidth() / 2;
        } else if (this.xAlign == FairType.TextAlign.RIGHT) {
            drawX -= drawOptions.getWidth();
        }
        if (this.yAlign == FairType.TextAlign.CENTER) {
            drawY -= drawOptions.getHeight() / 2;
        } else if (this.yAlign == FairType.TextAlign.RIGHT) {
            drawX -= drawOptions.getHeight();
        }
        if (this.background != null) {
            this.background.getDrawOptions(drawX, drawY, drawOptions.getWidth(), drawOptions.getHeight()).draw();
        }
        drawOptions.draw(drawX, drawY);
        if (this.background != null) {
            this.background.getEdgeDrawOptions(drawX, drawY, drawOptions.getWidth(), drawOptions.getHeight()).draw();
        }
        if (this.isHovering()) {
            this.addTooltips(perspective);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        Dimension dimensions = this.getActualDimensions();
        TextureDrawOptionsEnd drawOptions = this.getDrawOptions(false);
        int x = this.getX();
        int y = this.getY();
        if (this.xAlign == FairType.TextAlign.CENTER) {
            x -= drawOptions.getWidth() / 2;
        } else if (this.xAlign == FairType.TextAlign.RIGHT) {
            x -= drawOptions.getWidth();
        }
        if (this.yAlign == FairType.TextAlign.CENTER) {
            y -= drawOptions.getHeight() / 2;
        } else if (this.yAlign == FairType.TextAlign.RIGHT) {
            y -= drawOptions.getHeight();
        }
        return FormTextureButton.singleBox(new Rectangle(x, y, dimensions.width, dimensions.height));
    }

    protected void addTooltips(PlayerMob perspective) {
    }

    public GameSprite getTexture() {
        return this.textureSupplier.get();
    }

    public TextureDrawOptionsEnd getDrawOptions(boolean translate) {
        GameSprite texture = this.getTexture();
        TextureDrawOptionsEnd options = texture.initDraw();
        if (this.maxWidth > 0 && options.getWidth() > this.maxWidth) {
            options = options.shrinkWidth(this.maxWidth, translate);
        }
        if (this.maxHeight > 0 && options.getHeight() > this.maxHeight) {
            options = options.shrinkHeight(this.maxHeight, translate);
        }
        return options;
    }

    public Dimension getActualDimensions() {
        TextureDrawOptionsEnd options = this.getDrawOptions(false);
        return new Dimension(options.getWidth(), options.getHeight());
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}

