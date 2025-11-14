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
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.ItemAttackerRaiderMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemHolding;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.mapData.ClientDiscoveredMap;

public class VampireRaiderMob
extends ItemAttackerRaiderMob {
    public VampireRaiderMob() {
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
        return new LootTable(super.getLootTable(), LootItem.between("coin", minCoins, maxCoins).splitItems(4), new ChanceLootItem(0.15f, "healthregenpotion"), new ConditionLootItemList((random, objects) -> this.getMaxWeaponOrArmorValue() >= 2000.0, new ChanceLootItem(0.1f, "superiorhealthregenpotion")), ChanceLootItem.between(0.1f, "batwing", 1, 3), new ConditionLootItemList((random, objects) -> this.getMaxWeaponOrArmorValue() >= 2000.0, ChanceLootItem.between(0.02f, "phantomdust", 1, 4), new ConditionLootItemList((random, objects) -> this.getMaxWeaponOrArmorValue() >= 2000.0, ChanceLootItem.between(0.01f, "bloodessence", 1, 2))), new ChanceLootItem(0.05f, "recallscroll"), new ChanceLootItem(0.05f, "battlepotion"), new ConditionLootItemList((random, objects) -> this.getMaxWeaponOrArmorValue() >= 2000.0, new ChanceLootItem(0.05f, "greaterbattlepotion")), new ChanceLootItem(0.01f, "bloodplatecowl"), new ChanceLootItem(0.01f, "bloodplatechestplate"), new ChanceLootItem(0.01f, "bloodplateboots"), new ChanceLootItem(0.01f, "regenpendant"), new ConditionLootItemList((random, objects) -> this.getMaxWeaponOrArmorValue() >= 1300.0, new ChanceLootItem(0.01f, "lifependant")), new ConditionLootItemList((random, objects) -> this.getMaxWeaponOrArmorValue() >= 2000.0, new ChanceLootItem(0.005f, "bloodstonering")));
    }

    @Override
    public boolean canAttack() {
        return super.canAttack() && !this.hasCurrentMovement();
    }

    @Override
    public void updateAIAndLook() {
        super.updateAIAndLook();
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameTexture bodyTexture = MobRegistry.Textures.vampire.body;
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), bodyTexture, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(VampireRaiderMob.getTileCoordinate(x), VampireRaiderMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        boolean inLiquid = this.inLiquid(x, y);
        if (inLiquid) {
            sprite.x = 0;
        }
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(VampireRaiderMob.getTileCoordinate(x), VampireRaiderMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanOptions = new HumanDrawOptions(level, MobRegistry.Textures.vampire).sprite(sprite).mask(swimMask).dir(dir).light(light);
        if (inLiquid) {
            humanOptions.armSprite(0);
            humanOptions.mask(MobRegistry.Textures.boat_mask[sprite.y % 4], 0, -7);
        }
        if (!this.isAttacking && this.carryingLoot != null) {
            InventoryItem holdItem = new InventoryItem("itemhold");
            ItemHolding.setGNDData(holdItem, this.carryingLoot);
            humanOptions.holdItem(holdItem);
        }
        if (!this.hasCurrentMovement()) {
            this.setupAttackDraw(humanOptions);
        }
        final DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
        final TextureDrawOptionsEnd boat = inLiquid ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, sprite.y, 64).light(light).pos(drawX, drawY + 7) : null;
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
        HumanDrawOptions humanOptions = new HumanDrawOptions(this.getLevel(), MobRegistry.Textures.vampire).sprite(sprite).dir(dir).size(32, 32);
        humanOptions.pos(x - 15, y - 26).draw();
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("vamp", 3);
    }
}

