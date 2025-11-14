/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.ItemAttackerRaiderMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemHolding;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.mapData.ClientDiscoveredMap;

public class FishianRaiderMob
extends ItemAttackerRaiderMob {
    public FishianLook look;

    public FishianRaiderMob() {
        super(false);
        this.setSpeed(25.0f);
        this.setFriction(3.0f);
        this.setSwimSpeed(1.5f);
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
        return new LootTable(super.getLootTable(), LootItem.between("coin", minCoins, maxCoins).splitItems(4), ChanceLootItem.between(0.2f, "bamboo", 10, 20).splitItems(4), ChanceLootItem.between(0.1f, "frogleg", 2, 4), ChanceLootItem.between(0.02f, "gobfish", 1, 5), ChanceLootItem.between(0.02f, "halffish", 1, 5), ChanceLootItem.between(0.02f, "rockfish", 1, 5), ChanceLootItem.between(0.02f, "furfish", 1, 5), ChanceLootItem.between(0.02f, "icefish", 1, 5), ChanceLootItem.between(0.02f, "swampfish", 1, 5), ChanceLootItem.between(0.01f, "terrorfish", 1, 5));
    }

    @Override
    public void updateAIAndLook() {
        super.updateAIAndLook();
        this.look = FishianLook.values()[GameRandom.globalRandom.nextInt(FishianLook.values().length)];
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameTexture bodyTexture = this.look.getTexture.get().body;
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), bodyTexture, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(FishianRaiderMob.getTileCoordinate(x), FishianRaiderMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        boolean inLiquid = this.inLiquid(x, y);
        if (inLiquid) {
            sprite.x = 0;
        }
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(FishianRaiderMob.getTileCoordinate(x), FishianRaiderMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanOptions = new HumanDrawOptions(level, this.look.getTexture.get()).sprite(sprite).mask(swimMask).dir(dir).light(light);
        if (!this.isAttacking && this.carryingLoot != null) {
            InventoryItem holdItem = new InventoryItem("itemhold");
            ItemHolding.setGNDData(holdItem, this.carryingLoot);
            humanOptions.holdItem(holdItem);
        }
        this.setupAttackDraw(humanOptions);
        final DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
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
        HumanDrawOptions humanOptions = new HumanDrawOptions(this.getLevel(), this.look.getTexture.get()).sprite(sprite).dir(dir).size(32, 32);
        humanOptions.pos(x - 15, y - 26).draw();
    }

    public static enum FishianLook {
        Warrior(() -> MobRegistry.Textures.fishianHookWarrior),
        Healer(() -> MobRegistry.Textures.fishianHealer),
        Shaman(() -> MobRegistry.Textures.fishianShaman);

        public final Supplier<HumanTexture> getTexture;

        private FishianLook(Supplier<HumanTexture> getTexture) {
            this.getTexture = getTexture;
        }
    }
}

