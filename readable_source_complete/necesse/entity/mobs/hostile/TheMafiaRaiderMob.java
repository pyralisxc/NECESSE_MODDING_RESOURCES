/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.ItemAttackerRaiderMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemHolding;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.mapData.ClientDiscoveredMap;

public class TheMafiaRaiderMob
extends ItemAttackerRaiderMob {
    public HumanLook look;
    public GameRandom random;

    public TheMafiaRaiderMob() {
        super(false);
        this.setSpeed(25.0f);
        this.setFriction(3.0f);
        this.setSwimSpeed(1.2f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
    }

    @Override
    public LootTable getLootTable() {
        int minCoins = this.getMaxHealth() / 30;
        int maxCoins = this.getMaxHealth() / 20;
        return new LootTable(super.getLootTable(), LootItem.between("coin", minCoins, maxCoins).splitItems(4), new ChanceLootItem(0.15f, "healthpotion"), new ConditionLootItemList((random, objects) -> this.getMaxWeaponOrArmorValue() > 1150.0, new ChanceLootItem(0.1f, "greaterhealthpotion")), new ConditionLootItemList((random, objects) -> this.getMaxWeaponOrArmorValue() >= 2000.0, new ChanceLootItem(0.05f, "superiorhealthpotion")), ChanceLootItem.between(0.15f, "voidbullet", 8, 24), ChanceLootItem.between(0.15f, "bouncingbullet", 8, 24), new ChanceLootItem(0.02f, "invisibilitypotion"), new ChanceLootItem(0.01f, "elderhat"), new ChanceLootItem(0.01f, "pirateboots"), new ChanceLootItem(0.01f, "tophat"), new ChanceLootItem(0.01f, "blazer"), new ChanceLootItem(0.01f, "dressshoes"), new ChanceLootItem(0.01f, "sunglasses"), new ChanceLootItem(0.01f, "blackcoffee"));
    }

    @Override
    public void updateAIAndLook() {
        super.updateAIAndLook();
        this.random = new GameRandom(this.getUniqueID());
        this.look = new HumanLook(this.random, true);
        if (this.random.getChance(0.75f)) {
            if (this.random.getChance(0.7f)) {
                this.look.setFacialFeature(2);
            } else {
                this.look.setFacialFeature(0);
            }
            this.look.setHair(this.random.getOneOf(0, 1, 4, 5, 8, 9, 11, 12, 18, 19, 20));
        } else {
            this.look.setFacialFeature(0);
            this.look.setHair(this.random.getOneOf(2, 7, 15, 27, 28, 30, 32));
        }
        this.look.setHairColor(this.random.getOneOf(0, 2));
        String randomHelmet = this.random.getOneOf("elderhat", "tophat", "sunglasses");
        String randomBoots = this.random.getOneOf("dressshoes", "pirateboots");
        this.helmet = new InventoryItem(randomHelmet);
        this.chest = new InventoryItem("blazer");
        this.boots = new InventoryItem(randomBoots);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameSkin gameSkin = this.look.getGameSkin(false);
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), gameSkin.getBodyTexture(), GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(TheMafiaRaiderMob.getTileCoordinate(x), TheMafiaRaiderMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        boolean inLiquid = this.inLiquid(x, y);
        if (inLiquid) {
            sprite.x = 0;
        }
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(TheMafiaRaiderMob.getTileCoordinate(x), TheMafiaRaiderMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanOptions = new HumanDrawOptions(level, this.look, false).sprite(sprite).mask(swimMask).dir(dir).light(light);
        if (inLiquid) {
            humanOptions.armSprite(2);
            humanOptions.mask(MobRegistry.Textures.boat_mask[sprite.y % 4], 0, -7);
        }
        if (this.helmet != null) {
            humanOptions.helmet(this.helmet);
        }
        if (this.chest != null) {
            humanOptions.chestplate(this.chest);
        }
        if (this.boots != null) {
            humanOptions.boots(this.boots);
        }
        if (!this.isAttacking && this.carryingLoot != null) {
            InventoryItem holdItem = new InventoryItem("itemhold");
            ItemHolding.setGNDData(holdItem, this.carryingLoot);
            humanOptions.holdItem(holdItem);
        }
        this.setupAttackDraw(humanOptions);
        final DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
        final TextureDrawOptionsEnd boat = inLiquid ? MobRegistry.Textures.steelBoat.initDraw().sprite(0, sprite.y, 64).light(light).pos(drawX, drawY + 7) : null;
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                if (boat != null) {
                    boat.draw();
                }
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    public boolean shouldDrawOnMap() {
        return true;
    }

    @Override
    public boolean isVisibleOnMap(Client client, ClientDiscoveredMap map) {
        return true;
    }

    @Override
    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-10, -24, 20, 24);
    }

    @Override
    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(this.getDrawX(), this.getDrawY(), dir);
        HumanDrawOptions humanOptions = new HumanDrawOptions(this.getLevel(), this.look, false).helmet(this.helmet).chestplate(this.chest).boots(this.boots).sprite(sprite).dir(dir).size(32, 32);
        humanOptions.pos(x - 15, y - 26).draw();
    }
}

