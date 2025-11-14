/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.fishingRodItem;

import java.awt.Point;
import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public class FishingRodItem
extends PlaceableItem {
    public final int poleWidth;
    public final int poleHeight;
    public final int fishingPower;
    public final int hookSpeed;
    public final int lineLength;
    public final int reelWindow;
    public final int precision;
    public final int lineCount;
    public GameTexture particlesTexture;
    public GameTexture attackTexture;

    @Deprecated
    public FishingRodItem(int fishingPower, Item.Rarity rarity) {
        this(fishingPower, 37, 30, rarity);
    }

    public FishingRodItem(int fishingPower, int poleWidth, int poleHeight, Item.Rarity rarity) {
        this(fishingPower, poleWidth, poleHeight, 90, 200, 1, 30, 45, rarity);
    }

    public FishingRodItem(int fishingPower, int poleWidth, int poleHeight, int hookSpeed, int lineLength, int lineCount, int reelWindow, int precision, Item.Rarity rarity) {
        super(1, false);
        this.setItemCategory("equipment", "tools", "fishingrods");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "tools");
        this.keyWords.add("fishingrod");
        this.fishingPower = fishingPower;
        this.poleWidth = poleWidth;
        this.poleHeight = poleHeight;
        this.attackAnimTime.setBaseValue(300);
        this.hookSpeed = hookSpeed;
        this.lineLength = lineLength;
        this.lineCount = lineCount;
        this.reelWindow = Math.max(10, reelWindow);
        this.precision = Math.max(2, precision);
        this.attackXOffset = 6;
        this.attackYOffset = 6;
        this.rarity = rarity;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.attackTexture = GameTexture.fromFile("player/weapons/" + this.getStringID());
        this.particlesTexture = GameTexture.fromFile("particles/" + this.getStringID());
    }

    @Override
    public Point getControllerAttackLevelPos(Level level, float aimDirX, float aimDirY, PlayerMob player, InventoryItem item) {
        return new Point((int)(player.x + aimDirX * (float)this.lineLength), (int)(player.y + aimDirY * (float)this.lineLength));
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        return new GameSprite(this.attackTexture);
    }

    public GameSprite getHookProjectileSprite() {
        return new GameSprite(this.particlesTexture, 2, 0, 32);
    }

    public GameSprite getHookShadowSprite() {
        return new GameSprite(this.particlesTexture, 3, 0, 32);
    }

    public GameSprite getHookParticleSprite() {
        return new GameSprite(this.particlesTexture, 1, 0, 32);
    }

    public GameSprite getTrailSprite() {
        return new GameSprite(this.particlesTexture, 0, 0, 32);
    }

    public int getTipX(Mob mob) {
        int dir = mob.getDir();
        if (dir == 0) {
            return mob.getDrawX() + 16;
        }
        if (dir == 1) {
            return mob.getDrawX() + this.poleWidth + 10;
        }
        if (dir == 2) {
            return mob.getDrawX() - 15;
        }
        return mob.getDrawX() - this.poleWidth - 10;
    }

    public int getTipY(Mob mob) {
        return mob.getDrawY();
    }

    public int getTipHeight(Mob mob) {
        int dir = mob.getDir();
        int out = dir == 0 ? this.poleWidth + 32 : (dir == 1 ? this.poleHeight + 15 : (dir == 2 ? -this.poleWidth - 15 : this.poleHeight + 15));
        Level level = mob.getLevel();
        if (level != null) {
            Mob mount = null;
            if (mob.isRiding()) {
                mount = mob.getMount();
            }
            out -= mob.getBobbing();
            out -= level.getTile(mob.getTileX(), mob.getTileY()).getMobSinkingAmount(mob);
            if (mount != null) {
                Point spriteOffset = mount.getSpriteOffset(mount.getAnimSprite());
                out -= spriteOffset.y;
            }
        }
        return out;
    }

    public Point getTipPos(Mob mob) {
        return new Point(this.getTipX(mob), this.getTipY(mob));
    }

    @Override
    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        super.draw(item, perspective, x, y, inInventory);
        if (inInventory) {
            int baitAmount = this.getAvailableBait(perspective);
            if (baitAmount > 999) {
                baitAmount = 999;
            }
            String amountString = String.valueOf(baitAmount);
            int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
            FontManager.bit.drawString(x + 28 - width, y + 16, amountString, tipFontOptions);
        }
    }

    protected int getAvailableBait(PlayerMob player) {
        if (player == null) {
            return 0;
        }
        return player.getInv().main.getAmount(player.getLevel(), player, Item.Type.BAIT, "fishingbait");
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        return null;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer()) {
            BaitItem bait = (BaitItem)player.getInv().main.removeItem(level, player, Item.Type.BAIT, "fishingbait");
            FishingEvent event = new FishingEvent(player, x, y, this, bait);
            level.entityManager.events.add(event);
            this.playSwingSound(player);
        }
        return item;
    }

    protected void playSwingSound(PrimitiveSoundEmitter emitter) {
        SoundManager.playSound(GameResources.fishingRodSwing, (SoundEffect)SoundEffect.effect(emitter).volume(0.8f).pitch(GameRandom.globalRandom.getFloatBetween(0.95f, 1.05f)));
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "fishingrodtip"));
        tooltips.add(Localization.translate("itemtooltip", "fishingpower", "value", this.fishingPower + "%"));
        if (this.lineCount != 1) {
            tooltips.add(Localization.translate("itemtooltip", "fishinglines", "value", this.lineCount > 0 ? "+" + (this.lineCount - 1) : Integer.valueOf(this.lineCount)));
        }
        int baitAmount = this.getAvailableBait(perspective);
        tooltips.add(Localization.translate("itemtooltip", "baitamounttip", "value", (Object)baitAmount));
        return tooltips;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress, 110.0f, -20.0f);
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    @Override
    public float getSinkingRate(ItemPickupEntity entity, float currentSinking) {
        return super.getSinkingRate(entity, currentSinking) / 5.0f;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "fishingrod");
    }
}

