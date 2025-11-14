/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.itemAttack;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Consumer;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.ArrayDrawOptions;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.HumanAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.level.maps.light.GameLight;

public class ItemAttackDrawOptions
implements HumanAttackDrawOptions {
    private final LinkedList<AttackItemSprite> itemSprites = new LinkedList();
    private boolean itemBeforeHand = true;
    private GameSprite armSprite = null;
    private GameSprite armArmorSprite = null;
    private int centerX = 32;
    private int centerY = 23;
    private int armPosX = 0;
    private int armPosY = 0;
    private int armRotateX = 10;
    private int armRotateY = 16;
    private int armLength = 14;
    private int armCenterHeight = 4;
    private int itemYOffset = 12;
    private float armRotationOffset = 0.0f;
    private float addedArmRotationOffset = 0.0f;
    private int addedArmPosX = 0;
    private int addedArmPosY = 0;
    private int addedDrawX = 0;
    private int addedDrawY = 0;
    private Color armorColor = Color.WHITE;
    public final int dir;
    private float rotation = 0.0f;
    private Color shade = Color.WHITE;
    private GameLight light = new GameLight(150.0f);

    private ItemAttackDrawOptions(int dir) {
        this.dir = dir;
    }

    public static ItemAttackDrawOptions start(int dir) {
        return new ItemAttackDrawOptions(dir);
    }

    public AttackItemSprite itemSprite(GameSprite sprite) {
        AttackItemSprite attackItemSprite = new AttackItemSprite(sprite);
        if (sprite != null) {
            this.itemSprites.add(attackItemSprite);
        }
        return attackItemSprite;
    }

    public AttackItemSprite itemSprite(GameTexture texture, int spriteX, int spriteY, int spriteRes) {
        return this.itemSprite(new GameSprite(texture, spriteX, spriteY, spriteRes));
    }

    public ItemAttackDrawOptions itemAfterHand() {
        this.itemBeforeHand = false;
        return this;
    }

    public ItemAttackDrawOptions itemBeforeHand() {
        this.itemBeforeHand = true;
        return this;
    }

    public ItemAttackDrawOptions forEachItemSprite(Consumer<AttackItemSprite> action) {
        this.itemSprites.forEach(action);
        return this;
    }

    public ItemAttackDrawOptions armSprite(GameSprite sprite) {
        this.armSprite = sprite;
        return this;
    }

    public ItemAttackDrawOptions armSprite(GameTexture texture, int spriteX, int spriteY, int spriteRes) {
        return this.armSprite(new GameSprite(texture, spriteX, spriteY, spriteRes));
    }

    public ItemAttackDrawOptions armorSpriteAndColor(InventoryItem armorItem, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        if (armorItem != null && armorItem.item.isArmorItem() && armorItem.item instanceof ChestArmorItem) {
            this.armorSprite(((ChestArmorItem)armorItem.item).getAttackArmSprite(armorItem, player == null ? null : player.getLevel(), player, headItem, chestItem, feetItem));
            this.armorColor(armorItem.item.getDrawColor(armorItem, player));
        }
        return this;
    }

    public ItemAttackDrawOptions armorSprite(GameSprite sprite) {
        this.armArmorSprite = sprite;
        return this;
    }

    public ItemAttackDrawOptions armorSprite(GameTexture texture, int spriteX, int spriteY, int spriteRes) {
        return this.armorSprite(new GameSprite(texture, spriteX, spriteY, spriteRes));
    }

    public ItemAttackDrawOptions addedArmRotationOffset(float offset) {
        this.addedArmRotationOffset = offset;
        return this;
    }

    public ItemAttackDrawOptions addedArmPosOffset(int x, int y) {
        this.addedArmPosX = x;
        this.addedArmPosY = y;
        return this;
    }

    public ItemAttackDrawOptions armorColor(Color color) {
        this.armorColor = color;
        return this;
    }

    public ItemAttackDrawOptions rotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    public ItemAttackDrawOptions pointRotation(float attackDirX, float attackDirY, float angleOffset) {
        if (this.dir == 0) {
            return this.rotation(GameMath.getAngle(new Point2D.Float(attackDirX, attackDirY)) + angleOffset + 45.0f);
        }
        if (this.dir == 1) {
            return this.rotation((float)(attackDirX == 0.0f ? (double)(attackDirY < 0.0f ? -90 : 90) : Math.toDegrees(Math.atan(attackDirY / attackDirX))) + (float)(attackDirX < 0.0f ? 180 : 0) + angleOffset);
        }
        if (this.dir == 2) {
            return this.rotation(GameMath.getAngle(new Point2D.Float(attackDirX, attackDirY)) + angleOffset + 45.0f + 180.0f);
        }
        if (this.dir == 3) {
            return this.rotation((float)(attackDirX == 0.0f ? (double)(attackDirY < 0.0f ? -90 : 90) : -Math.toDegrees(Math.atan(attackDirY / attackDirX))) + (float)(attackDirX > 0.0f ? 180 : 0) + angleOffset);
        }
        return this;
    }

    public ItemAttackDrawOptions pointRotation(float attackDirX, float attackDirY) {
        return this.pointRotation(attackDirX, attackDirY, 0.0f);
    }

    public ItemAttackDrawOptions swingRotation(float attackProgress, float angle, float angleOffset) {
        return this.rotation(ItemAttackDrawOptions.getSwingRotation(attackProgress, angle, angleOffset) - 90.0f);
    }

    public static float getSwingRotation(float attackProgress, float angle, float angleOffset) {
        return angleOffset + attackProgress * angle;
    }

    public ItemAttackDrawOptions swingRotation(float attackProgress) {
        return this.swingRotation(attackProgress, 150.0f, 0.0f);
    }

    public ItemAttackDrawOptions swingRotationInv(float attackProgress, float angle, float angleOffset) {
        return this.rotation(ItemAttackDrawOptions.getSwingRotationInv(attackProgress, angle, angleOffset) - 90.0f);
    }

    public static float getSwingRotationInv(float attackProgress, float angle, float angleOffset) {
        return angleOffset + Math.abs(attackProgress * angle - angle);
    }

    public ItemAttackDrawOptions swingRotationInv(float attackProgress) {
        return this.swingRotationInv(attackProgress, 150.0f, 0.0f);
    }

    public ItemAttackDrawOptions shade(Color color) {
        this.shade = color;
        return this;
    }

    @Override
    public ItemAttackDrawOptions light(GameLight light) {
        this.light = light;
        return this;
    }

    public ItemAttackDrawOptions setOffsets(int centerX, int centerY, int armRotateX, int armRotateY, int armLength, int armCenterHeight, int itemYOffset) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.armRotateX = armRotateX;
        this.armRotateY = armRotateY;
        this.armLength = armLength;
        this.armCenterHeight = armCenterHeight;
        this.itemYOffset = itemYOffset;
        return this;
    }

    @Override
    public ItemAttackDrawOptions setOffsets(int centerX, int centerY, int armPosX, int armPosY, float armRotationOffset, int armRotateX, int armRotateY, int armLength, int armCenterHeight, int itemYOffset) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.armPosX = armPosX;
        this.armPosY = armPosY;
        this.armRotationOffset = armRotationOffset;
        this.armRotateX = armRotateX;
        this.armRotateY = armRotateY;
        this.armLength = armLength;
        this.armCenterHeight = armCenterHeight;
        this.itemYOffset = itemYOffset;
        return this;
    }

    public ItemAttackDrawOptions drawOffset(int drawXOffset, int drawYOffset) {
        this.addedDrawX = drawXOffset;
        this.addedDrawY = drawYOffset;
        return this;
    }

    public ItemAttackDrawOptions thrustOffsets(float attackDirX, float attackDirY, float attackProgress) {
        int xOffset = (int)((double)attackDirX * Math.sin(attackProgress * 4.0f) * 15.0) - (int)(attackDirX * 5.0f);
        int yOffset = (int)((double)attackDirY * Math.sin(attackProgress * 4.0f) * 15.0) - (int)(attackDirY * 5.0f);
        for (AttackItemSprite itemSprite : this.itemSprites) {
            itemSprite.rotationOffset += 45.0f;
            if (this.dir != 2) continue;
            itemSprite.rotateX += 5;
            itemSprite.rotateY -= 4;
        }
        return this.drawOffset(xOffset, yOffset);
    }

    @Override
    public DrawOptions pos(int drawX, int drawY) {
        int armPosX = this.armPosX + this.addedArmPosX;
        int armPosY = this.armPosY + this.addedArmPosY;
        float armRotationOffset = this.armRotationOffset + this.addedArmRotationOffset;
        drawX += this.addedDrawX;
        drawY += this.addedDrawY;
        ArrayList<DrawOptions> options = new ArrayList<DrawOptions>();
        if (this.dir == 0) {
            TextureDrawOptionsEnd chestOptions;
            TextureDrawOptionsEnd handOptions = this.armSprite == null ? null : this.armSprite.initDraw().colorMult(this.shade).light(this.light).mirrorX().rotate(this.rotation + armRotationOffset - 45.0f, this.armRotateX - armPosX, this.armRotateY - armPosY).pos(drawX + armPosX + this.centerX - 2, drawY + armPosY + this.centerY + 5);
            TextureDrawOptionsEnd textureDrawOptionsEnd = chestOptions = this.armArmorSprite == null ? null : this.armArmorSprite.initDraw().color(this.armorColor).colorMult(this.shade).light(this.light).mirrorX().rotate(this.rotation + armRotationOffset - 45.0f, this.armRotateX - armPosX, this.armRotateY - armPosY).pos(drawX + armPosX + this.centerX - 2, drawY + armPosY + this.centerY + 5);
            if (!this.itemBeforeHand) {
                if (handOptions != null) {
                    options.add(handOptions);
                }
                if (chestOptions != null) {
                    options.add(chestOptions);
                }
            }
            for (AttackItemSprite item : this.itemSprites) {
                if (item.rawCoords) {
                    options.add(item.sprite.initDraw().color(item.color).colorMult(this.shade).light(item.getLight(this.light)).mirrorX().rotate(this.rotation + item.rotationOffset - 45.0f, item.rotateX, -item.rotateY + item.height).size(item.width, item.height).pos(drawX - item.rotateX + this.centerX + 6, drawY + item.rotateY - item.height + this.centerY + 22));
                    continue;
                }
                options.add(item.sprite.initDraw().color(item.color).colorMult(this.shade).light(item.getLight(this.light)).mirrorX().rotate(this.rotation + item.rotationOffset - 45.0f, -14 + item.rotateX, 3 - item.rotateY + item.height).size(item.width, item.height).pos(drawX - item.rotateX + this.centerX + 20, drawY + item.rotateY - item.height + this.centerY + 17));
            }
            if (this.itemBeforeHand) {
                if (handOptions != null) {
                    options.add(handOptions);
                }
                if (chestOptions != null) {
                    options.add(chestOptions);
                }
            }
        } else if (this.dir == 1) {
            TextureDrawOptionsEnd chestOptions;
            TextureDrawOptionsEnd handOptions = this.armSprite == null ? null : this.armSprite.initDraw().colorMult(this.shade).light(this.light).mirrorX().rotate(this.rotation + armRotationOffset, this.armRotateX - armPosX, this.armRotateY - armPosY).pos(drawX + this.centerX - this.armRotateX + armPosX - 4, drawY + this.centerY + armPosY + 5);
            TextureDrawOptionsEnd textureDrawOptionsEnd = chestOptions = this.armArmorSprite == null ? null : this.armArmorSprite.initDraw().color(this.armorColor).colorMult(this.shade).light(this.light).mirrorX().rotate(this.rotation + armRotationOffset, this.armRotateX - armPosX, this.armRotateY - armPosY).pos(drawX + this.centerX - this.armRotateX + armPosX - 4, drawY + this.centerY + armPosY + 5);
            if (!this.itemBeforeHand) {
                if (handOptions != null) {
                    options.add(handOptions);
                }
                if (chestOptions != null) {
                    options.add(chestOptions);
                }
            }
            for (AttackItemSprite item : this.itemSprites) {
                if (item.rawCoords) {
                    options.add(item.sprite.initDraw().color(item.color).colorMult(this.shade).light(item.getLight(this.light)).mirrorX().rotate(this.rotation + item.rotationOffset, item.rotateX, -item.rotateY + item.height).size(item.width, item.height).pos(drawX + this.centerX - item.rotateX - 4, drawY + this.centerY + this.itemYOffset + this.armCenterHeight + item.rotateY - item.height + 5));
                    continue;
                }
                options.add(item.sprite.initDraw().color(item.color).colorMult(this.shade).light(item.getLight(this.light)).mirrorX().rotate(this.rotation + item.rotationOffset, -this.armLength + item.rotateX, this.armCenterHeight - item.rotateY + item.height).size(item.width, item.height).pos(drawX + this.centerX + this.armLength - item.rotateX - 4, drawY + this.centerY + this.itemYOffset + item.rotateY - item.height + 5));
            }
            if (this.itemBeforeHand) {
                if (handOptions != null) {
                    options.add(handOptions);
                }
                if (chestOptions != null) {
                    options.add(chestOptions);
                }
            }
        } else if (this.dir == 2) {
            TextureDrawOptionsEnd chestOptions;
            TextureDrawOptionsEnd handOptions = this.armSprite == null ? null : this.armSprite.initDraw().colorMult(this.shade).light(this.light).rotate(this.rotation + armRotationOffset + 180.0f - 60.0f, this.armRotateX - armPosX, this.armRotateY + armPosY + 1).mirrorX().pos(drawX + armPosX + this.centerX - 18, drawY - armPosY + this.centerY + 1);
            TextureDrawOptionsEnd textureDrawOptionsEnd = chestOptions = this.armArmorSprite == null ? null : this.armArmorSprite.initDraw().color(this.armorColor).colorMult(this.shade).light(this.light).rotate(this.rotation + armRotationOffset + 180.0f - 60.0f, this.armRotateX + armPosX, this.armRotateY + armPosY + 1).mirrorX().pos(drawX + armPosX + this.centerX - 18, drawY - armPosY + this.centerY + 1);
            if (!this.itemBeforeHand) {
                if (handOptions != null) {
                    options.add(handOptions);
                }
                if (chestOptions != null) {
                    options.add(chestOptions);
                }
            }
            for (AttackItemSprite item : this.itemSprites) {
                if (item.rawCoords) {
                    options.add(item.sprite.initDraw().color(item.color).colorMult(this.shade).light(item.getLight(this.light)).mirrorX().rotate(this.rotation + item.rotationOffset + 180.0f - 60.0f, item.rotateX, -item.rotateY + item.height + 1).size(item.width, item.height).pos(drawX - item.rotateX + this.centerX - 10, drawY + item.rotateY - item.height + this.centerY + 17));
                    continue;
                }
                options.add(item.sprite.initDraw().color(item.color).colorMult(this.shade).light(item.getLight(this.light)).mirrorX().rotate(this.rotation + item.rotationOffset + 180.0f - 60.0f, item.rotateX - 12, -item.rotateY + item.height + 4).addRotation(15.0f, 0, item.height).size(item.width, item.height).pos(drawX - item.rotateX + this.centerX + 4, drawY + item.rotateY - item.height + this.centerY + 14));
            }
            if (this.itemBeforeHand) {
                if (handOptions != null) {
                    options.add(handOptions);
                }
                if (chestOptions != null) {
                    options.add(chestOptions);
                }
            }
        } else if (this.dir == 3) {
            TextureDrawOptionsEnd chestOptions;
            TextureDrawOptionsEnd handOptions;
            TextureDrawOptionsEnd textureDrawOptionsEnd = handOptions = this.armSprite == null ? null : this.armSprite.initDraw().colorMult(this.shade).light(this.light).rotate(-this.rotation - armRotationOffset, -this.armRotateX + this.armSprite.spriteWidth + armPosX, this.armRotateY - armPosY).pos(drawX + this.centerX - this.armSprite.width + this.armRotateX - armPosX + 4, drawY + this.centerY + armPosY + 5);
            TextureDrawOptionsEnd textureDrawOptionsEnd2 = this.armArmorSprite == null ? null : (chestOptions = this.armArmorSprite.initDraw().color(this.armorColor).colorMult(this.shade).light(this.light).rotate(-this.rotation - armRotationOffset, -this.armRotateX + this.armArmorSprite.spriteHeight + armPosX, this.armRotateY - armPosY).pos(drawX + this.centerX - (this.armSprite == null ? 0 : this.armSprite.width) + this.armRotateX - armPosX + 4, drawY + this.centerY + armPosY + 5));
            if (!this.itemBeforeHand) {
                if (handOptions != null) {
                    options.add(handOptions);
                }
                if (chestOptions != null) {
                    options.add(chestOptions);
                }
            }
            for (AttackItemSprite item : this.itemSprites) {
                if (item.rawCoords) {
                    options.add(item.sprite.initDraw().color(item.color).colorMult(this.shade).light(item.getLight(this.light)).rotate(-this.rotation - item.rotationOffset, -item.rotateX + item.width, -item.rotateY + item.height).size(item.width, item.height).pos(drawX + this.centerX + item.rotateX - item.width + 4, drawY + this.centerY + this.itemYOffset + this.armCenterHeight + item.rotateY - item.height + 5));
                    continue;
                }
                options.add(item.sprite.initDraw().color(item.color).colorMult(this.shade).light(item.getLight(this.light)).rotate(-this.rotation - item.rotationOffset, this.armLength - item.rotateX + item.width, this.armCenterHeight - item.rotateY + item.height).size(item.width, item.height).pos(drawX + this.centerX - this.armLength + item.rotateX - item.width + 4, drawY + this.centerY + this.itemYOffset + item.rotateY - item.height + 5));
            }
            if (this.itemBeforeHand) {
                if (handOptions != null) {
                    options.add(handOptions);
                }
                if (chestOptions != null) {
                    options.add(chestOptions);
                }
            }
        }
        return new ArrayDrawOptions(options);
    }

    public class AttackItemSprite {
        private final GameSprite sprite;
        private int width;
        private int height;
        private int rotateX;
        private int rotateY;
        private boolean rawCoords;
        private float rotationOffset;
        private Color color;
        private int minDrawLightLevel;

        public AttackItemSprite(GameSprite sprite) {
            this.sprite = sprite;
            if (sprite != null) {
                this.width = sprite.width;
                this.height = sprite.height;
            }
            this.rotateX = 0;
            this.rotateY = 0;
            this.rotationOffset = 0.0f;
            this.color = new Color(1.0f, 1.0f, 1.0f);
        }

        public AttackItemSprite itemSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public AttackItemSprite itemRotatePoint(int x, int y) {
            this.rotateX = x;
            this.rotateY = y;
            return this;
        }

        public AttackItemSprite itemRawCoords() {
            this.rawCoords = true;
            return this;
        }

        public AttackItemSprite itemRotateOffset(float offset) {
            this.rotationOffset = offset;
            return this;
        }

        public AttackItemSprite itemRotateOffsetAdd(float offset) {
            this.rotationOffset += offset;
            return this;
        }

        public AttackItemSprite itemColor(Color color) {
            this.color = color;
            return this;
        }

        public AttackItemSprite itemMinDrawLight(int level) {
            this.minDrawLightLevel = level;
            return this;
        }

        public ItemAttackDrawOptions itemEnd() {
            return ItemAttackDrawOptions.this;
        }

        private GameLight getLight(GameLight defaultLight) {
            if (this.minDrawLightLevel > 0) {
                return defaultLight.minLevelCopy(this.minDrawLightLevel);
            }
            return defaultLight;
        }
    }
}

