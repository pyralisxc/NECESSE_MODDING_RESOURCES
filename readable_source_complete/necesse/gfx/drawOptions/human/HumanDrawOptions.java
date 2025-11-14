/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.gfx.drawOptions.human;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.HumanTextureFull;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.itemAttack.HumanAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.EdgeMaskSpriteOptions;
import necesse.gfx.shader.ShaderState;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.item.armorItem.HelmetArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import org.lwjgl.opengl.GL11;

public class HumanDrawOptions {
    private final Level level;
    private HumanLook look;
    private boolean onlyHumanLike;
    private boolean drawEyes = true;
    private int eyeTypeOverride = -1;
    private GameTexture headTexture;
    private GameTexture eyelidsTexture;
    private GameTexture bodyTexture;
    private GameTexture leftArmsTexture;
    private GameTexture rightArmsTexture;
    private GameTexture feetTexture;
    private GameTexture hairTexture;
    private GameTexture backHairTexture;
    private GameTexture facialFeatureTexture;
    private GameTexture backFacialFeatureTexture;
    private final List<HumanDrawOptionsGetter> behindOptions;
    private final List<HumanDrawOptionsGetter> topOptions;
    private final List<HumanDrawOptionsGetter> onBodyOptions;
    private boolean mirrorX;
    private boolean mirrorY;
    private ArmorItem.HairDrawMode hairMode;
    private ArmorItem.FacialFeatureDrawMode facialFeatureMode;
    private HumanDrawOptionsGetter hatTexture;
    private GameTexture hairMaskTexture;
    private int hatXOffset;
    private int hatYOffset;
    private InventoryItem helmet;
    private InventoryItem chestplate;
    private InventoryItem boots;
    private GameLight light = new GameLight(150.0f);
    private float alpha = 1.0f;
    private float allAlpha = 1.0f;
    private float rotation;
    private int rotationX;
    private int rotationY;
    private int drawOffsetX;
    private int drawOffsetY;
    private int width = 64;
    private int height = 64;
    private PlayerMob player;
    private int attackCenterX = 32;
    private int attackCenterY = 23;
    private int attackArmPosX = 0;
    private int attackArmPosY = 0;
    private int attackArmRotateX = 10;
    private int attackArmRotateY = 16;
    private int attackArmLength = 14;
    private int attackArmCenterHeight = 4;
    private int attackItemYOffset = 12;
    private float attackArmRotationOffset = 0.0f;
    private HumanAttackDrawOptions attackDrawOptions;
    private InventoryItem attackItem;
    private float attackProgress;
    private float attackDirX;
    private float attackDirY;
    private int spriteX;
    private int spriteY;
    private int spriteRes = 64;
    private int rightArmSpriteX;
    private int leftArmSpriteX;
    private int dir;
    private boolean invis;
    private boolean blinking;
    private MaskShaderOptions mask;
    private boolean forcedBufferDraw;
    private InventoryItem holdItem;

    public HumanDrawOptions(Level level) {
        this.level = level;
        this.behindOptions = new LinkedList<HumanDrawOptionsGetter>();
        this.topOptions = new LinkedList<HumanDrawOptionsGetter>();
        this.onBodyOptions = new LinkedList<HumanDrawOptionsGetter>();
    }

    public HumanDrawOptions(Level level, HumanLook look, boolean onlyHumanlike) {
        this(level);
        this.look = look;
        this.onlyHumanLike = onlyHumanlike;
        GameSkin gameSkin = look.getGameSkin(onlyHumanlike);
        this.headTexture = gameSkin.getHeadTexture();
        this.bodyTexture = gameSkin.getBodyTexture();
        this.leftArmsTexture = gameSkin.getLeftArmsTexture();
        this.rightArmsTexture = gameSkin.getRightArmsTexture();
        this.feetTexture = gameSkin.getFeetTexture();
        this.hairTexture = look.getHairTexture();
        this.backHairTexture = look.getBackHairTexture();
        this.facialFeatureTexture = look.getFacialFeatureTexture();
        this.backFacialFeatureTexture = look.getBackFacialFeatureTexture();
    }

    public HumanDrawOptions(Level level, HumanTexture humanTexture) {
        this(level);
        this.bodyTexture = humanTexture.body;
        this.leftArmsTexture = humanTexture.leftArms;
        this.rightArmsTexture = humanTexture.rightArms;
    }

    public HumanDrawOptions(Level level, HumanTextureFull humanTexture) {
        this(level);
        this.headTexture = humanTexture.head;
        this.eyelidsTexture = humanTexture.eyelids;
        this.bodyTexture = humanTexture.body;
        this.leftArmsTexture = humanTexture.leftArms;
        this.rightArmsTexture = humanTexture.rightArms;
        this.feetTexture = humanTexture.feet;
        this.hairTexture = humanTexture.hair;
        this.backHairTexture = humanTexture.backHair;
    }

    public HumanDrawOptions headTexture(GameTexture headTexture) {
        this.headTexture = headTexture;
        return this;
    }

    public HumanDrawOptions drawEyes(boolean drawEyes) {
        this.drawEyes = drawEyes;
        return this;
    }

    public HumanDrawOptions eyeTypeOverride(int eyeType) {
        this.eyeTypeOverride = eyeType;
        return this;
    }

    public HumanDrawOptions eyelidsTexture(GameTexture eyelidsTexture) {
        this.eyelidsTexture = eyelidsTexture;
        return this;
    }

    public HumanDrawOptions bodyTexture(GameTexture bodyTexture) {
        this.bodyTexture = bodyTexture;
        return this;
    }

    public HumanDrawOptions leftArmsTexture(GameTexture leftArmsTexture) {
        this.leftArmsTexture = leftArmsTexture;
        return this;
    }

    public HumanDrawOptions rightArmsTexture(GameTexture rightArmsTexture) {
        this.rightArmsTexture = rightArmsTexture;
        return this;
    }

    public HumanDrawOptions feetTexture(GameTexture feetTexture) {
        this.feetTexture = feetTexture;
        return this;
    }

    public HumanDrawOptions hairTexture(GameTexture hairTexture) {
        this.hairTexture = hairTexture;
        return this;
    }

    public HumanDrawOptions backHairTexture(GameTexture backHairTexture) {
        this.backHairTexture = backHairTexture;
        return this;
    }

    public HumanDrawOptions facialFeatureTexture(GameTexture facialFeatureTexture) {
        this.facialFeatureTexture = facialFeatureTexture;
        return this;
    }

    public HumanDrawOptions backFacialFeatureTexture(GameTexture backFacialFeatureTexture) {
        this.backFacialFeatureTexture = backFacialFeatureTexture;
        return this;
    }

    public HumanDrawOptions hatTexture(HumanDrawOptionsGetter drawOptionsGetter, ArmorItem.HairDrawMode mode, int xOffset, int yOffset) {
        this.hatTexture = drawOptionsGetter;
        this.hairMode = mode;
        this.hatXOffset = xOffset;
        this.hatYOffset = yOffset;
        return this;
    }

    public HumanDrawOptions hatTexture(HumanDrawOptionsGetter drawOptionsGetter, ArmorItem.HairDrawMode mode) {
        return this.hatTexture(drawOptionsGetter, mode, 0, 0);
    }

    public HumanDrawOptions hatTexture(GameTexture hatTexture, ArmorItem.HairDrawMode mode, int xOffset, int yOffset) {
        return this.hatTexture((PlayerMob player, int dir, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) -> hatTexture.initDraw().sprite(spriteX, spriteY, spriteRes).light(light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY), mode, xOffset, yOffset);
    }

    public HumanDrawOptions hatTexture(GameTexture hatTexture, ArmorItem.HairDrawMode mode) {
        return this.hatTexture(hatTexture, mode, 0, 0);
    }

    public HumanDrawOptions helmet(InventoryItem helmet) {
        this.helmet = helmet;
        return this;
    }

    public InventoryItem getHelmet() {
        return this.helmet;
    }

    public HumanDrawOptions chestplate(InventoryItem chestplate) {
        this.chestplate = chestplate;
        return this;
    }

    public InventoryItem getChestplate() {
        return this.chestplate;
    }

    public HumanDrawOptions boots(InventoryItem boots) {
        this.boots = boots;
        return this;
    }

    public InventoryItem getBoots() {
        return this.boots;
    }

    public HumanDrawOptions addBehindDraw(HumanDrawOptionsGetter getter) {
        this.behindOptions.add(getter);
        return this;
    }

    public HumanDrawOptions addTopDraw(HumanDrawOptionsGetter getter) {
        this.topOptions.add(getter);
        return this;
    }

    public HumanDrawOptions addOnBodyDraw(HumanDrawOptionsGetter getter) {
        this.onBodyOptions.add(getter);
        return this;
    }

    public HumanDrawOptions mirrorX(boolean mirror) {
        this.mirrorX = mirror;
        return this;
    }

    public HumanDrawOptions mirrorY(boolean mirror) {
        this.mirrorY = mirror;
        return this;
    }

    public HumanDrawOptions alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public HumanDrawOptions allAlpha(float alpha) {
        this.allAlpha = alpha;
        return this;
    }

    public HumanDrawOptions rotate(float angle, int midX, int midY) {
        this.rotation = angle;
        this.rotationX = midX;
        this.rotationY = midY;
        return this;
    }

    public HumanDrawOptions light(GameLight light) {
        this.light = light;
        return this;
    }

    public HumanDrawOptions addDrawOffset(int x, int y) {
        this.drawOffsetX = x;
        this.drawOffsetY = y;
        return this;
    }

    public HumanDrawOptions drawOffset(int x, int y) {
        this.drawOffsetX += x;
        this.drawOffsetY += y;
        return this;
    }

    public HumanDrawOptions size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public HumanDrawOptions player(PlayerMob player) {
        this.player = player;
        return this;
    }

    public HumanDrawOptions attackOffsets(int centerX, int centerY, int armRotateX, int armRotateY, int armLength, int armCenterHeight, int itemYOffset) {
        this.attackCenterX = centerX;
        this.attackCenterY = centerY;
        this.attackArmRotateX = armRotateX;
        this.attackArmRotateY = armRotateY;
        this.attackArmLength = armLength;
        this.attackArmCenterHeight = armCenterHeight;
        this.attackItemYOffset = itemYOffset;
        return this;
    }

    public HumanDrawOptions attackOffsets(int centerX, int centerY, int armLength, int armCenterHeight, int itemYOffset) {
        this.attackCenterX = centerX;
        this.attackCenterY = centerY;
        this.attackArmLength = armLength;
        this.attackArmCenterHeight = armCenterHeight;
        this.attackItemYOffset = itemYOffset;
        return this;
    }

    public HumanDrawOptions attackArmRotatePoint(int x, int y) {
        this.attackArmRotateX = x;
        this.attackArmRotateY = y;
        return this;
    }

    public HumanDrawOptions attackArmPosOffset(int x, int y) {
        this.attackArmPosX = x;
        this.attackArmPosY = y;
        return this;
    }

    public HumanDrawOptions attackArmRotationOffset(float rotation) {
        this.attackArmRotationOffset = rotation;
        return this;
    }

    public HumanDrawOptions itemAttack(InventoryItem item, PlayerMob player, float attackProgress, float attackDirX, float attackDirY) {
        this.attackDrawOptions = null;
        this.attackItem = item;
        this.player = player;
        this.attackProgress = attackProgress;
        this.attackDirX = attackDirX;
        this.attackDirY = attackDirY;
        return this;
    }

    public HumanDrawOptions attackAnim(HumanAttackDrawOptions drawOptions, float attackProgress) {
        this.attackItem = null;
        this.attackDrawOptions = drawOptions;
        this.attackProgress = attackProgress;
        return this;
    }

    public HumanDrawOptions mask(MaskShaderOptions mask) {
        this.mask = mask;
        return this;
    }

    public HumanDrawOptions mask(GameTexture mask, int xOffset, int yOffset) {
        this.mask = new MaskShaderOptions(mask, 0, 0, xOffset, yOffset);
        return this;
    }

    public HumanDrawOptions mask(GameTexture mask) {
        return this.mask(mask, 0, 0);
    }

    public HumanDrawOptions forceBufferDraw() {
        this.forcedBufferDraw = true;
        return this;
    }

    public HumanDrawOptions invis(boolean invis) {
        this.invis = invis;
        return this;
    }

    public HumanDrawOptions blinking(boolean blinking) {
        this.blinking = blinking;
        return this;
    }

    public HumanDrawOptions sprite(int spriteX, int spriteY, int spriteRes) {
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        this.rightArmSpriteX = spriteX;
        this.leftArmSpriteX = spriteX;
        this.spriteRes = spriteRes;
        return this;
    }

    public HumanDrawOptions sprite(int spriteX, int spriteY) {
        return this.sprite(spriteX, spriteY, this.spriteRes);
    }

    public HumanDrawOptions armSprite(int spriteX) {
        this.rightArmSpriteX = spriteX;
        this.leftArmSpriteX = spriteX;
        return this;
    }

    public HumanDrawOptions rightArmSprite(int spriteX) {
        this.rightArmSpriteX = spriteX;
        return this;
    }

    public HumanDrawOptions leftArmSprite(int spriteX) {
        this.leftArmSpriteX = spriteX;
        return this;
    }

    public HumanDrawOptions holdItem(InventoryItem item) {
        if (this.dir == 3) {
            this.leftArmSprite(1);
        } else {
            this.rightArmSprite(1);
        }
        this.holdItem = item;
        return this;
    }

    public boolean hasHoldItem() {
        return this.holdItem != null;
    }

    public HumanDrawOptions sprite(Point sprite, int spriteRes) {
        return this.sprite(sprite.x, sprite.y, spriteRes);
    }

    public HumanDrawOptions sprite(Point sprite) {
        return this.sprite(sprite.x, sprite.y);
    }

    public HumanDrawOptions dir(int dir) {
        this.dir = dir;
        return this;
    }

    public boolean isAttacking() {
        return this.attackDrawOptions != null || this.attackItem != null;
    }

    public float getAttackProgress() {
        return this.attackProgress;
    }

    public DrawOptions pos(int drawX, int drawY) {
        DrawOptions attackOptions;
        DrawOptions chestFrontArmorOption;
        DrawOptions chestBackArmorOption;
        int attackYOffset;
        int attackXOffset;
        EdgeMaskSpriteOptions maskOptions;
        MaskShaderOptions hairMaskOptions;
        if (this.mask != null) {
            this.mask.useShader(false);
        }
        drawX += this.drawOffsetX;
        drawY += this.drawOffsetY;
        DrawOptionsList behind = new DrawOptionsList();
        DrawOptionsList headOptions = new DrawOptionsList();
        DrawOptionsList eyelidsOptions = new DrawOptionsList();
        DrawOptionsList headBackArmorOptions = new DrawOptionsList();
        DrawOptionsList headArmorOptions = new DrawOptionsList();
        DrawOptionsList headFrontArmorOptions = new DrawOptionsList();
        if (this.hatTexture != null) {
            if (this.hairMode != ArmorItem.HairDrawMode.NO_HEAD) {
                hairMaskOptions = this.mask;
                if (this.hairMaskTexture != null) {
                    maskOptions = new EdgeMaskSpriteOptions(new GameSprite(this.hairMaskTexture, this.spriteX, this.spriteY, this.spriteRes, this.width, this.height), 0, 0);
                    hairMaskOptions = this.mask == null ? new MaskShaderOptions(0, 0).addMask(maskOptions) : this.mask.copyAndAddMask(maskOptions);
                }
                if (this.facialFeatureMode == ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE) {
                    if (this.facialFeatureTexture != null && !this.invis) {
                        headArmorOptions.add(this.facialFeatureTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                    }
                    if (this.backFacialFeatureTexture != null && !this.invis) {
                        behind.add(this.backFacialFeatureTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                    }
                }
                if (this.hairMode == ArmorItem.HairDrawMode.OVER_HAIR) {
                    if (this.hairTexture != null && !this.invis) {
                        headArmorOptions.add(this.hairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(hairMaskOptions).pos(drawX, drawY));
                    }
                    if (this.backHairTexture != null && !this.invis) {
                        behind.add(this.backHairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(hairMaskOptions).pos(drawX, drawY));
                    }
                }
                if (this.headTexture != null && !this.invis) {
                    headOptions.add(this.headTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                }
                if (this.eyelidsTexture != null && this.blinking && !this.invis) {
                    eyelidsOptions.add(this.eyelidsTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                } else if (this.look != null && !this.invis && this.drawEyes) {
                    headOptions.add(HumanLook.getEyesDrawOptions(this.eyeTypeOverride == -1 ? this.look.getEyeType() : this.eyeTypeOverride, this.look.getEyeColor(), this.look.getSkin(), this.onlyHumanLike, this.blinking, drawX, drawY, this.spriteX, this.spriteY, this.width, this.height, this.mirrorX, this.mirrorY, this.alpha, this.light, this.mask));
                }
                headArmorOptions.add(this.hatTexture.getDrawOptions(this.player, this.dir, this.spriteX, this.spriteY, this.spriteRes, drawX + this.hatXOffset, drawY + this.hatYOffset, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
                if (this.facialFeatureMode == ArmorItem.FacialFeatureDrawMode.UNDER_FACIAL_FEATURE) {
                    if (this.facialFeatureTexture != null && !this.invis) {
                        headArmorOptions.add(this.facialFeatureTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                    }
                    if (this.backFacialFeatureTexture != null && !this.invis) {
                        behind.add(this.backFacialFeatureTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                    }
                }
                if (this.hairMode == ArmorItem.HairDrawMode.UNDER_HAIR) {
                    if (this.hairTexture != null && !this.invis) {
                        headArmorOptions.add(this.hairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(hairMaskOptions).pos(drawX, drawY));
                    }
                    if (this.backHairTexture != null && !this.invis) {
                        behind.add(this.backHairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(hairMaskOptions).pos(drawX, drawY));
                    }
                }
            }
        } else if (this.helmet != null && this.helmet.item.isArmorItem()) {
            DrawOptions headFrontDrawOption;
            ((ArmorItem)this.helmet.item).addExtraDrawOptions(this, this.helmet);
            ArmorItem.HairDrawMode headDrawOptions = ((ArmorItem)this.helmet.item).hairDrawOptions;
            ArmorItem.FacialFeatureDrawMode facialFeatureDrawOptions = ((ArmorItem)this.helmet.item).facialFeatureDrawOptions;
            if (headDrawOptions != ArmorItem.HairDrawMode.NO_HEAD) {
                GameTexture hairMask = this.hairMaskTexture == null ? (this.helmet.item instanceof HelmetArmorItem ? ((HelmetArmorItem)this.helmet.item).hairMaskTexture : null) : this.hairMaskTexture;
                MaskShaderOptions hairMaskOptions2 = this.mask;
                if (hairMask != null) {
                    EdgeMaskSpriteOptions maskOptions2 = new EdgeMaskSpriteOptions(new GameSprite(hairMask, this.spriteX, this.spriteY, this.spriteRes, this.width, this.height), 0, 0);
                    hairMaskOptions2 = this.mask == null ? new MaskShaderOptions(0, 0).addMask(maskOptions2) : this.mask.copyAndAddMask(maskOptions2);
                }
                if (facialFeatureDrawOptions == ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE) {
                    if (this.facialFeatureTexture != null && !this.invis) {
                        headArmorOptions.add(this.facialFeatureTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                    }
                    if (this.backFacialFeatureTexture != null && !this.invis) {
                        behind.add(this.backFacialFeatureTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                    }
                }
                if (headDrawOptions == ArmorItem.HairDrawMode.OVER_HAIR) {
                    if (this.hairTexture != null && !this.invis) {
                        headArmorOptions.add(this.hairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(hairMaskOptions2).pos(drawX, drawY));
                    }
                    if (this.backHairTexture != null && !this.invis) {
                        behind.add(this.backHairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(hairMaskOptions2).pos(drawX, drawY));
                    }
                }
                if (this.headTexture != null && !this.invis) {
                    headOptions.add(this.headTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                }
                if (this.eyelidsTexture != null && this.blinking && !this.invis) {
                    eyelidsOptions.add(this.eyelidsTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                } else if (this.look != null && !this.invis && this.drawEyes) {
                    headOptions.add(HumanLook.getEyesDrawOptions(this.eyeTypeOverride == -1 ? this.look.getEyeType() : this.eyeTypeOverride, this.look.getEyeColor(), this.look.getSkin(), this.onlyHumanLike, this.blinking, drawX, drawY, this.spriteX, this.spriteY, this.width, this.height, this.mirrorX, this.mirrorY, this.alpha, this.light, this.mask));
                }
                headArmorOptions.add(((ArmorItem)this.helmet.item).getArmorDrawOptions(this.helmet, this.level, this.player, this.helmet, this.chestplate, this.boots, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
                if (facialFeatureDrawOptions == ArmorItem.FacialFeatureDrawMode.UNDER_FACIAL_FEATURE) {
                    if (this.facialFeatureTexture != null && !this.invis) {
                        headArmorOptions.add(this.facialFeatureTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                    }
                    if (this.backFacialFeatureTexture != null && !this.invis) {
                        behind.add(this.backFacialFeatureTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                    }
                }
                if (headDrawOptions == ArmorItem.HairDrawMode.UNDER_HAIR) {
                    if (this.hairTexture != null && !this.invis) {
                        headArmorOptions.add(this.hairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(hairMaskOptions2).pos(drawX, drawY));
                    }
                    if (this.backHairTexture != null && !this.invis) {
                        behind.add(this.backHairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(hairMaskOptions2).pos(drawX, drawY));
                    }
                }
            } else {
                headArmorOptions.add(((ArmorItem)this.helmet.item).getArmorDrawOptions(this.helmet, this.level, this.player, this.helmet, this.chestplate, this.boots, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
            }
            DrawOptions headBackArmorOption = ((ArmorItem)this.helmet.item).getBackArmorDrawOptions(this.helmet, this.player, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask);
            if (headBackArmorOption != null) {
                headBackArmorOptions.add(headBackArmorOption);
            }
            if ((headFrontDrawOption = ((ArmorItem)this.helmet.item).getFrontArmorDrawOptions(this.helmet, this.player, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask)) != null) {
                headFrontArmorOptions.add(headFrontDrawOption);
            }
        } else {
            hairMaskOptions = this.mask;
            if (this.hairMaskTexture != null) {
                maskOptions = new EdgeMaskSpriteOptions(new GameSprite(this.hairMaskTexture, this.spriteX, this.spriteY, this.spriteRes, this.width, this.height), 0, 0);
                hairMaskOptions = this.mask == null ? new MaskShaderOptions(0, 0).addMask(maskOptions) : this.mask.copyAndAddMask(maskOptions);
            }
            if (this.backFacialFeatureTexture != null && !this.invis) {
                behind.add(this.backFacialFeatureTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
            }
            if (this.backHairTexture != null && !this.invis) {
                behind.add(this.backHairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(hairMaskOptions).pos(drawX, drawY));
            }
            if (this.headTexture != null && !this.invis) {
                headOptions.add(this.headTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
            }
            if (this.eyelidsTexture != null && this.blinking && !this.invis) {
                eyelidsOptions.add(this.eyelidsTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
            } else if (this.look != null && !this.invis && this.drawEyes) {
                headOptions.add(HumanLook.getEyesDrawOptions(this.eyeTypeOverride == -1 ? this.look.getEyeType() : this.eyeTypeOverride, this.look.getEyeColor(), this.look.getSkin(), this.onlyHumanLike, this.blinking, drawX, drawY, this.spriteX, this.spriteY, this.width, this.height, this.mirrorX, this.mirrorY, this.alpha, this.light, this.mask));
            }
            if (this.facialFeatureTexture != null && !this.invis) {
                headArmorOptions.add(this.facialFeatureTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
            }
            if (this.hairTexture != null && !this.invis) {
                headArmorOptions.add(this.hairTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(hairMaskOptions).pos(drawX, drawY));
            }
        }
        DrawOptionsList chestOptions = new DrawOptionsList();
        DrawOptionsList chestBackArmorOptions = new DrawOptionsList();
        DrawOptionsList chestArmorOptions = new DrawOptionsList();
        DrawOptionsList chestFrontArmorOptions = new DrawOptionsList();
        DrawOptionsList leftArmsOptions = new DrawOptionsList();
        DrawOptionsList rightArmsOptions = new DrawOptionsList();
        DrawOptionsList leftArmsFrontOptions = new DrawOptionsList();
        DrawOptionsList rightArmsFrontOptions = new DrawOptionsList();
        DrawOptionsList holdItemOptions = new DrawOptionsList();
        boolean holdItemInFrontOfArms = false;
        boolean addLeftArm = true;
        boolean addRightArm = true;
        if (this.attackDrawOptions != null) {
            attackXOffset = 0;
            int n = attackYOffset = HumanDrawOptions.isSpriteXOffset(this.spriteX) ? -2 : 0;
            if (this.mask != null) {
                attackXOffset += this.mask.drawXOffset;
                attackYOffset += this.mask.drawYOffset;
            }
            if (this.chestplate != null && this.chestplate.item.isArmorItem()) {
                ((ArmorItem)this.chestplate.item).addExtraDrawOptions(this, this.chestplate);
                if (((ArmorItem)this.chestplate.item).drawBodyPart(this.chestplate, this.player) && this.bodyTexture != null && !this.invis) {
                    chestOptions.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                }
                chestArmorOptions.add(((ArmorItem)this.chestplate.item).getArmorDrawOptions(this.chestplate, this.level, this.player, this.helmet, this.chestplate, this.boots, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
                chestBackArmorOption = ((ArmorItem)this.chestplate.item).getBackArmorDrawOptions(this.chestplate, this.player, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask);
                if (chestBackArmorOption != null) {
                    chestBackArmorOptions.add(chestBackArmorOption);
                }
                if ((chestFrontArmorOption = ((ArmorItem)this.chestplate.item).getFrontArmorDrawOptions(this.chestplate, this.player, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask)) != null) {
                    chestFrontArmorOptions.add(chestFrontArmorOption);
                }
            } else if (this.bodyTexture != null && !this.invis) {
                chestOptions.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
            }
            attackOptions = this.attackDrawOptions.light(this.light).setOffsets(this.attackCenterX, this.attackCenterY, this.attackArmPosX, this.attackArmPosY, this.attackArmRotationOffset, this.attackArmRotateX, this.attackArmRotateY, this.attackArmLength, this.attackArmCenterHeight, this.attackItemYOffset).pos(drawX + attackXOffset, drawY + attackYOffset);
            if (this.dir != 3) {
                rightArmsOptions.add(attackOptions);
                addRightArm = false;
            } else {
                leftArmsOptions.add(attackOptions);
                addLeftArm = false;
            }
        } else if (this.attackItem != null) {
            attackXOffset = 0;
            int n = attackYOffset = HumanDrawOptions.isSpriteXOffset(this.spriteX) ? -2 : 0;
            if (this.mask != null) {
                attackXOffset += this.mask.drawXOffset;
                attackYOffset += this.mask.drawYOffset;
            }
            if (this.chestplate != null && this.chestplate.item.isArmorItem()) {
                ((ArmorItem)this.chestplate.item).addExtraDrawOptions(this, this.chestplate);
                if (((ArmorItem)this.chestplate.item).drawBodyPart(this.chestplate, this.player) && this.bodyTexture != null && !this.invis) {
                    chestOptions.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                }
                chestArmorOptions.add(((ArmorItem)this.chestplate.item).getArmorDrawOptions(this.chestplate, this.level, this.player, this.helmet, this.chestplate, this.boots, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
                chestBackArmorOption = ((ArmorItem)this.chestplate.item).getBackArmorDrawOptions(this.chestplate, this.player, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask);
                if (chestBackArmorOption != null) {
                    chestBackArmorOptions.add(chestBackArmorOption);
                }
                if ((chestFrontArmorOption = ((ArmorItem)this.chestplate.item).getFrontArmorDrawOptions(this.chestplate, this.player, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask)) != null) {
                    chestFrontArmorOptions.add(chestFrontArmorOption);
                }
            } else if (this.bodyTexture != null && !this.invis) {
                chestOptions.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
            }
            attackOptions = this.attackItem.getAttackDrawOptions(this.level, this.player, this.helmet, this.chestplate, this.boots, this.dir, this.attackDirX, this.attackDirY, this.invis || this.bodyTexture == null ? null : new GameSprite(this.bodyTexture, 0, 8, this.spriteRes / 2), this.attackProgress).light(this.light).setOffsets(this.attackCenterX, this.attackCenterY, this.attackArmPosX, this.attackArmPosY, this.attackArmRotationOffset, this.attackArmRotateX, this.attackArmRotateY, this.attackArmLength, this.attackArmCenterHeight, this.attackItemYOffset).pos(drawX + attackXOffset, drawY + attackYOffset);
            if (this.dir != 3) {
                rightArmsOptions.add(attackOptions);
                addRightArm = false;
            } else {
                leftArmsOptions.add(attackOptions);
                addLeftArm = false;
            }
        }
        if (addLeftArm || addRightArm) {
            boolean leftArmOffset = HumanDrawOptions.isSpriteXOffset(this.leftArmSpriteX);
            boolean rightArmOffset = HumanDrawOptions.isSpriteXOffset(this.rightArmSpriteX);
            boolean isSpriteOffset = HumanDrawOptions.isSpriteXOffset(this.spriteX);
            int leftArmXOffset = 0;
            int leftArmYOffset = 0;
            int rightArmXOffset = 0;
            int rightArmYOffset = 0;
            if (leftArmOffset != isSpriteOffset) {
                int n = leftArmYOffset = isSpriteOffset ? 0 : 2;
            }
            if (rightArmOffset != isSpriteOffset) {
                int n = rightArmYOffset = isSpriteOffset ? 0 : 2;
            }
            if (this.chestplate != null && this.chestplate.item.isArmorItem()) {
                DrawOptions chestFrontArmorOption2;
                ((ArmorItem)this.chestplate.item).addExtraDrawOptions(this, this.chestplate);
                if (((ArmorItem)this.chestplate.item).drawBodyPart(this.chestplate, this.player)) {
                    if (this.bodyTexture != null && !this.invis) {
                        chestOptions.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                    }
                    if (addLeftArm && this.leftArmsTexture != null && !this.invis) {
                        leftArmsOptions.add(this.leftArmsTexture.initDraw().sprite(this.leftArmSpriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX + leftArmXOffset, drawY + leftArmYOffset));
                    }
                    if (addRightArm && this.rightArmsTexture != null && !this.invis) {
                        rightArmsOptions.add(this.rightArmsTexture.initDraw().sprite(this.rightArmSpriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX + rightArmXOffset, drawY + rightArmYOffset));
                    }
                }
                chestArmorOptions.add(((ArmorItem)this.chestplate.item).getArmorDrawOptions(this.chestplate, this.level, this.player, this.helmet, this.chestplate, this.boots, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
                DrawOptions chestBackArmorOption2 = ((ArmorItem)this.chestplate.item).getBackArmorDrawOptions(this.chestplate, this.player, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask);
                if (chestBackArmorOption2 != null) {
                    chestBackArmorOptions.add(chestBackArmorOption2);
                }
                if ((chestFrontArmorOption2 = ((ArmorItem)this.chestplate.item).getFrontArmorDrawOptions(this.chestplate, this.player, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask)) != null) {
                    chestFrontArmorOptions.add(chestFrontArmorOption2);
                }
                if (this.chestplate.item instanceof ChestArmorItem) {
                    if (addLeftArm) {
                        leftArmsOptions.add(((ChestArmorItem)this.chestplate.item).getArmorLeftArmsDrawOptions(this.chestplate, this.level, this.player, this.helmet, this.chestplate, this.boots, this.leftArmSpriteX, this.spriteY, this.spriteRes, drawX + leftArmXOffset, drawY + leftArmYOffset, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
                        DrawOptions chestFrontArmorLeftArmsOption = ((ChestArmorItem)this.chestplate.item).getFrontArmorLeftArmsDrawOptions(this.chestplate, this.player, this.leftArmSpriteX, this.spriteY, this.spriteRes, drawX + leftArmXOffset, drawY + leftArmYOffset, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask);
                        if (chestFrontArmorLeftArmsOption != null) {
                            leftArmsFrontOptions.add(chestFrontArmorLeftArmsOption);
                        }
                    }
                    if (addRightArm) {
                        rightArmsOptions.add(((ChestArmorItem)this.chestplate.item).getArmorRightArmsDrawOptions(this.chestplate, this.level, this.player, this.helmet, this.chestplate, this.boots, this.rightArmSpriteX, this.spriteY, this.spriteRes, drawX + rightArmXOffset, drawY + rightArmYOffset, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
                        DrawOptions chestFrontArmorRightArmsOption = ((ChestArmorItem)this.chestplate.item).getFrontArmorRightArmsDrawOptions(this.chestplate, this.player, this.rightArmSpriteX, this.spriteY, this.spriteRes, drawX + rightArmXOffset, drawY + rightArmYOffset, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask);
                        if (chestFrontArmorRightArmsOption != null) {
                            rightArmsFrontOptions.add(chestFrontArmorRightArmsOption);
                        }
                    }
                }
            } else {
                if (this.bodyTexture != null && !this.invis) {
                    chestOptions.add(this.bodyTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
                }
                if (addLeftArm && this.leftArmsTexture != null && !this.invis) {
                    leftArmsOptions.add(this.leftArmsTexture.initDraw().sprite(this.leftArmSpriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX + leftArmXOffset, drawY + leftArmYOffset));
                }
                if (addRightArm && this.rightArmsTexture != null && !this.invis) {
                    rightArmsOptions.add(this.rightArmsTexture.initDraw().sprite(this.rightArmSpriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX + rightArmXOffset, drawY + rightArmYOffset));
                }
            }
            if (this.holdItem != null && addLeftArm && addRightArm) {
                holdItemOptions.add(this.holdItem.item.getHoldItemDrawOptions(this.holdItem, this.player, this.spriteX, this.spriteY, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
                holdItemInFrontOfArms = this.holdItem.item.holdItemInFrontOfArms(this.holdItem, this.player, this.spriteX, this.spriteY, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask);
            }
        }
        DrawOptionsList feetOptions = new DrawOptionsList();
        DrawOptionsList feetBackArmorOptions = new DrawOptionsList();
        DrawOptionsList feetArmorOptions = new DrawOptionsList();
        DrawOptionsList feetFrontArmorOptions = new DrawOptionsList();
        if (this.boots != null && this.boots.item.isArmorItem()) {
            DrawOptions feetFrontArmorOption;
            ((ArmorItem)this.boots.item).addExtraDrawOptions(this, this.boots);
            if (((ArmorItem)this.boots.item).drawBodyPart(this.boots, this.player) && this.feetTexture != null && !this.invis) {
                feetOptions.add(this.feetTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
            }
            feetArmorOptions.add(((ArmorItem)this.boots.item).getArmorDrawOptions(this.boots, this.level, this.player, this.helmet, this.chestplate, this.boots, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
            DrawOptions feetBackArmorOption = ((ArmorItem)this.boots.item).getBackArmorDrawOptions(this.boots, this.player, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask);
            if (feetBackArmorOption != null) {
                feetBackArmorOptions.add(feetBackArmorOption);
            }
            if ((feetFrontArmorOption = ((ArmorItem)this.boots.item).getFrontArmorDrawOptions(this.boots, this.player, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask)) != null) {
                feetFrontArmorOptions.add(feetFrontArmorOption);
            }
        } else if (this.feetTexture != null && !this.invis) {
            feetOptions.add(this.feetTexture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).size(this.width, this.height).mirror(this.mirrorX, this.mirrorY).light(this.light).alpha(this.alpha).addMaskShader(this.mask).pos(drawX, drawY));
        }
        for (HumanDrawOptionsGetter behindOption : this.behindOptions) {
            behind.add(behindOption.getDrawOptions(this.player, this.dir, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
        }
        DrawOptionsList onBody = new DrawOptionsList();
        for (HumanDrawOptionsGetter onBodyOption : this.onBodyOptions) {
            onBody.add(onBodyOption.getDrawOptions(this.player, this.dir, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
        }
        DrawOptionsList top = new DrawOptionsList();
        for (HumanDrawOptionsGetter topOption : this.topOptions) {
            top.add(topOption.getDrawOptions(this.player, this.dir, this.spriteX, this.spriteY, this.spriteRes, drawX, drawY, this.width, this.height, this.mirrorX, this.mirrorY, this.light, this.alpha, this.mask));
        }
        return HumanDrawOptions.getArmorDrawOptions(this.isAttacking(), this.dir, behind, headOptions, eyelidsOptions, headBackArmorOptions, headArmorOptions, headFrontArmorOptions, chestOptions, chestBackArmorOptions, chestArmorOptions, chestFrontArmorOptions, feetOptions, feetBackArmorOptions, feetArmorOptions, feetFrontArmorOptions, leftArmsOptions, leftArmsFrontOptions, rightArmsOptions, rightArmsFrontOptions, holdItemOptions.isEmpty() ? null : holdItemOptions, holdItemInFrontOfArms, onBody, top, this.allAlpha, this.rotation, drawX + this.rotationX, drawY + this.rotationY, this.mask, this.forcedBufferDraw);
    }

    public static boolean isSpriteXOffset(int spriteX) {
        return spriteX == 1 || spriteX == 3;
    }

    public void draw(int drawX, int drawY) {
        this.pos(drawX, drawY).draw();
    }

    public static void addArmorDrawOptions(DrawOptionsList list, boolean isAttacking, int mobDir, DrawOptions behind, DrawOptions head, DrawOptions eyelids, DrawOptions headBackArmor, DrawOptions headArmor, DrawOptions headFrontArmor, DrawOptions chest, DrawOptions chestBackArmor, DrawOptions chestArmor, DrawOptions chestFrontArmor, DrawOptions feet, DrawOptions feetBackArmor, DrawOptions feetArmor, DrawOptions feetFrontArmor, DrawOptions leftArms, DrawOptions frontLeftArms, DrawOptions rightArms, DrawOptions frontRightArms, DrawOptions holdItem, boolean holdItemInFrontOfArms, DrawOptions onBody, DrawOptions top, ShaderState shader) {
        if (shader != null) {
            list.add(shader::use);
        }
        if (behind != null) {
            list.add(behind);
        }
        if (mobDir == 0) {
            if (holdItem != null && holdItemInFrontOfArms) {
                list.add(holdItem);
            }
            if (feetBackArmor != null) {
                list.add(feetBackArmor);
            }
            if (chestBackArmor != null) {
                list.add(chestBackArmor);
            }
            if (headBackArmor != null) {
                list.add(headBackArmor);
            }
            if (isAttacking) {
                if (shader != null) {
                    list.add(shader::stop);
                }
                if (rightArms != null) {
                    list.add(rightArms);
                }
                if (head != null) {
                    list.add(head);
                }
                if (shader != null) {
                    list.add(shader::use);
                }
            } else if (rightArms != null) {
                list.add(rightArms);
            }
            if (leftArms != null) {
                list.add(leftArms);
            }
            if (holdItem != null && !holdItemInFrontOfArms) {
                list.add(holdItem);
            }
            if (feet != null) {
                list.add(feet);
            }
            if (chest != null) {
                list.add(chest);
            }
            if (head != null) {
                list.add(head);
            }
            if (eyelids != null) {
                list.add(eyelids);
            }
            if (onBody != null) {
                list.add(onBody);
            }
            if (feetArmor != null) {
                list.add(feetArmor);
            }
            if (chestArmor != null) {
                list.add(chestArmor);
            }
            if (headArmor != null) {
                list.add(headArmor);
            }
            if (frontLeftArms != null) {
                list.add(frontLeftArms);
            }
            if (frontRightArms != null) {
                list.add(frontRightArms);
            }
            if (feetFrontArmor != null) {
                list.add(feetFrontArmor);
            }
            if (chestFrontArmor != null) {
                list.add(chestFrontArmor);
            }
            if (headFrontArmor != null) {
                list.add(headFrontArmor);
            }
        } else {
            if (feetBackArmor != null) {
                list.add(feetBackArmor);
            }
            if (chestBackArmor != null) {
                list.add(chestBackArmor);
            }
            if (headBackArmor != null) {
                list.add(headBackArmor);
            }
            if (feet != null) {
                list.add(feet);
            }
            if (mobDir == 1 && leftArms != null) {
                list.add(leftArms);
                if (frontLeftArms != null) {
                    list.add(frontLeftArms);
                }
            } else if (mobDir == 3 && rightArms != null) {
                list.add(rightArms);
                if (frontRightArms != null) {
                    list.add(frontRightArms);
                }
            }
            if (chest != null) {
                list.add(chest);
            }
            if (onBody != null) {
                list.add(onBody);
            }
            if (feetArmor != null) {
                list.add(feetArmor);
            }
            if (feetFrontArmor != null) {
                list.add(feetFrontArmor);
            }
            if (holdItem != null) {
                if (chestArmor != null) {
                    list.add(chestArmor);
                }
                if (head != null) {
                    list.add(head);
                }
                if (eyelids != null) {
                    list.add(eyelids);
                }
                if (headArmor != null) {
                    list.add(headArmor);
                }
                if (!isAttacking) {
                    if (!holdItemInFrontOfArms) {
                        list.add(holdItem);
                    }
                    if (mobDir == 1 && rightArms != null) {
                        list.add(rightArms);
                    } else if (mobDir == 3 && leftArms != null) {
                        list.add(leftArms);
                    } else if (mobDir == 2) {
                        if (leftArms != null) {
                            list.add(leftArms);
                        }
                        if (rightArms != null) {
                            list.add(rightArms);
                        }
                    }
                    if (holdItemInFrontOfArms) {
                        list.add(holdItem);
                    }
                } else if (mobDir == 2 && leftArms != null) {
                    list.add(leftArms);
                }
                if (frontLeftArms != null) {
                    list.add(frontLeftArms);
                }
                if (frontRightArms != null) {
                    list.add(frontRightArms);
                }
                if (feetFrontArmor != null) {
                    list.add(feetFrontArmor);
                }
                if (chestFrontArmor != null) {
                    list.add(chestFrontArmor);
                }
                if (headFrontArmor != null) {
                    list.add(headFrontArmor);
                }
            } else {
                if (chestArmor != null) {
                    list.add(chestArmor);
                }
                if (chestFrontArmor != null) {
                    list.add(chestFrontArmor);
                }
                if (!isAttacking) {
                    if (mobDir == 1 && rightArms != null) {
                        list.add(rightArms);
                        if (frontRightArms != null) {
                            list.add(frontRightArms);
                        }
                    } else if (mobDir == 3 && leftArms != null) {
                        list.add(leftArms);
                        if (frontLeftArms != null) {
                            list.add(frontLeftArms);
                        }
                    } else if (mobDir == 2) {
                        if (leftArms != null) {
                            list.add(leftArms);
                        }
                        if (frontLeftArms != null) {
                            list.add(frontLeftArms);
                        }
                        if (rightArms != null) {
                            list.add(rightArms);
                        }
                        if (frontRightArms != null) {
                            list.add(frontRightArms);
                        }
                    }
                } else if (mobDir == 2) {
                    if (leftArms != null) {
                        list.add(leftArms);
                    }
                    if (frontLeftArms != null) {
                        list.add(frontLeftArms);
                    }
                }
                if (head != null) {
                    list.add(head);
                }
                if (eyelids != null) {
                    list.add(eyelids);
                }
                if (headArmor != null) {
                    list.add(headArmor);
                }
                if (headFrontArmor != null) {
                    list.add(headFrontArmor);
                }
            }
            if (isAttacking) {
                if (mobDir == 1 && rightArms != null) {
                    if (shader != null) {
                        list.add(shader::stop);
                    }
                    list.add(rightArms);
                    if (frontRightArms != null) {
                        list.add(frontRightArms);
                    }
                    if (shader != null) {
                        list.add(shader::use);
                    }
                } else if (mobDir == 3 && leftArms != null) {
                    if (shader != null) {
                        list.add(shader::stop);
                    }
                    list.add(leftArms);
                    if (shader != null) {
                        list.add(shader::use);
                    }
                } else if (mobDir == 2) {
                    if (shader != null) {
                        list.add(shader::stop);
                    }
                    if (rightArms != null) {
                        list.add(rightArms);
                    }
                    if (frontRightArms != null) {
                        list.add(frontRightArms);
                    }
                    if (shader != null) {
                        list.add(shader::use);
                    }
                }
            }
        }
        if (top != null) {
            list.add(top);
        }
        if (shader != null) {
            list.add(shader::stop);
        }
    }

    public static DrawOptionsList getArmorDrawOptions(boolean isAttacking, int mobDir, DrawOptions behind, DrawOptions head, DrawOptions eyelids, DrawOptions headBackArmor, DrawOptions headArmor, DrawOptions headFrontArmor, DrawOptions chest, DrawOptions chestBackArmor, DrawOptions chestArmor, DrawOptions chestFrontArmor, DrawOptions feet, DrawOptions feetBackArmor, DrawOptions feetArmor, DrawOptions feetFrontArmor, DrawOptions leftArms, DrawOptions frontLeftArms, DrawOptions rightArms, DrawOptions frontRightArms, DrawOptions holdItem, boolean holdItemInFrontOfArms, DrawOptions onBody, DrawOptions top, final float alpha, final float angle, final int rotationMidX, final int rotationMidY, ShaderState shader, final boolean forcedBufferDraw) {
        DrawOptionsList draws = new DrawOptionsList(){

            @Override
            public void draw() {
                if (alpha == 1.0f && angle == 0.0f && !forcedBufferDraw) {
                    super.draw();
                } else {
                    WindowManager.getWindow().applyDraw(() -> super.draw(), () -> {
                        GL11.glTranslatef((float)rotationMidX, (float)rotationMidY, (float)0.0f);
                        GL11.glRotatef((float)angle, (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glTranslatef((float)(-rotationMidX), (float)(-rotationMidY), (float)0.0f);
                        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
                    }, null);
                    GL11.glLoadIdentity();
                }
            }
        };
        HumanDrawOptions.addArmorDrawOptions(draws, isAttacking, mobDir, behind, head, eyelids, headBackArmor, headArmor, headFrontArmor, chest, chestBackArmor, chestArmor, chestFrontArmor, feet, feetBackArmor, feetArmor, feetFrontArmor, leftArms, frontLeftArms, rightArms, frontRightArms, holdItem, holdItemInFrontOfArms, onBody, top, shader);
        return draws;
    }

    @FunctionalInterface
    public static interface HumanDrawOptionsGetter {
        public DrawOptions getDrawOptions(PlayerMob var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10, boolean var11, GameLight var12, float var13, MaskShaderOptions var14);
    }
}

