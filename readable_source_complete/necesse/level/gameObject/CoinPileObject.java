/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.CoinPileObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.CoinItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CoinPileObject
extends GameObject {
    private GameTexture texture;
    private final int maxStackSize = 300;
    private final Point[] stackOffsets = new Point[]{new Point(-10, -14), new Point(-10, -14), new Point(-4, -10), new Point(4, -14), new Point(10, -10), new Point(-10, -6), new Point(-4, -2), new Point(4, -6), new Point(10, -2), new Point(-10, 2), new Point(-6, 6), new Point(4, 2), new Point(10, 6)};

    public CoinPileObject() {
        super(new Rectangle(32, 32));
        this.setItemCategory("misc");
        this.canPlaceOnShore = true;
        this.objectHealth = 10;
        this.toolType = ToolType.ALL;
        this.rarity = Item.Rarity.RARE;
        this.stackSize = 5000;
        this.attackThrough = true;
        this.canPlaceOnProtectedLevels = true;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("item", this.getMultiTile(0).getMasterObject().getStringID());
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage, Attacker attacker) {
        if (!level.objectLayer.isPlayerPlaced(x, y)) {
            super.attackThrough(level, x, y, damage, attacker);
        }
    }

    public void setCoins(int amount, Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof CoinPileObjectEntity) {
            ((CoinPileObjectEntity)objectEntity).coinAmount = amount;
        }
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        if (level.getObjectID(layerID, x, y) == this.getID()) {
            if (this.getCoinAmount(level, x, y) >= 300) {
                return "toomanycoins";
            }
            return null;
        }
        return super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
    }

    @Override
    public void placeObject(Level level, int layerID, int x, int y, int rotation, boolean byPlayer) {
        boolean isAlreadyCoinPile = level.getObjectID(layerID, x, y) == this.getID();
        int coinAmount = isAlreadyCoinPile ? this.getCoinAmount(level, x, y) : 0;
        super.placeObject(level, layerID, x, y, rotation, byPlayer);
        this.setCoins(coinAmount + 1, level, x, y);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        float alpha = 1.0f;
        int coinAmount = this.getCoinAmount(level, tileX, tileY);
        if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
            Rectangle alphaRec = new Rectangle(tileX * 32, tileY * 32 - coinAmount, 32, coinAmount + 4);
            if (perspective.getCollision().intersects(alphaRec)) {
                alpha = 0.2f;
            } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                alpha = 0.2f;
            }
        }
        final SharedTextureDrawOptions drawOptions = new SharedTextureDrawOptions(this.texture);
        for (int i = 0; i < coinAmount; ++i) {
            int whatStack = i % this.stackOffsets.length;
            int coinInStack = i / this.stackOffsets.length;
            Point stackOffset = this.stackOffsets[whatStack];
            drawOptions.addSprite(coinInStack == 0 ? 0 : 1, 0, 32).alpha(alpha).light(light).pos(drawX + stackOffset.x, drawY + stackOffset.y - coinInStack * 4);
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int coinAmount = level.getObjectID(tileX, tileY) != this.getID() ? 1 : this.getCoinAmount(level, tileX, tileY) + 1;
        SharedTextureDrawOptions drawOptions = new SharedTextureDrawOptions(this.texture);
        int whatStack = coinAmount - 1 > 0 ? (coinAmount - 1) % this.stackOffsets.length : 0;
        int coinInStack = coinAmount / this.stackOffsets.length;
        Point stackOffset = this.stackOffsets[whatStack];
        drawOptions.addSprite(coinInStack == 0 ? 0 : 1, 0, 32).light(light).alpha(alpha).pos(drawX + stackOffset.x, drawY + stackOffset.y - coinInStack * 4);
        drawOptions.draw();
    }

    private int getCoinAmount(Level level, int tileX, int tileY) {
        int coinAmount = 1;
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof CoinPileObjectEntity) {
            coinAmount = ((CoinPileObjectEntity)objectEntity).coinAmount;
        }
        return coinAmount;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/coinpile");
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage) {
        super.attackThrough(level, x, y, damage);
        if (damage.damage > 0.0f) {
            this.playDamageSound(level, x, y, true);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new CoinPileObjectEntity(level, x, y);
    }

    @Override
    public Item generateNewObjectItem() {
        return new CoinItem(this);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        int coinAmount = this.getCoinAmount(level, tileX, tileY);
        if (coinAmount == 0) {
            return new LootTable();
        }
        return new LootTable(new LootItem("coin", coinAmount).splitItems(10).preventLootMultiplier());
    }

    @Override
    public void playPlaceSound(int tileX, int tileY) {
        SoundManager.playSound(GameResources.coinsPlace, (SoundEffect)SoundEffect.effect(tileX * 32 + 16, tileY * 32 + 16));
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.coins, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16));
    }
}

