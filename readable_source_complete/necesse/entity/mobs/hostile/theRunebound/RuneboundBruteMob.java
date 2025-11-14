/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.theRunebound;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedItemAttackerPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileItemAttackerMob;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameHair;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanGender;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.cosmetics.misc.ShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.ShoesArmorItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RuneboundBruteMob
extends HostileItemAttackerMob {
    public static LootTable lootTable = new LootTable(ChanceLootItem.between(0.05f, "runestone", 1, 2), ChanceLootItem.between(0.2f, "clothscraps", 1, 3), ChanceLootItem.between(0.2f, "coin", 2, 16).splitItems(2), new ChanceLootItem(0.05f, "healthpotion"), new ChanceLootItem(0.1f, "steak"));
    public int lookSeed;
    public HumanLook look = new HumanLook();
    public InventoryItem helmet;
    public InventoryItem chest;
    public InventoryItem boots;
    public float facingBuffer;
    public boolean shouldResetFacingPos;

    public RuneboundBruteMob() {
        super(200);
        this.setSpeed(20.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.5f);
        this.setArmor(12);
        this.getLookSeed();
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.swimMaskMove = 16;
        this.swimMaskOffset = -2;
        this.swimSinkOffset = -4;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("lookSeed", this.lookSeed);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.lookSeed = save.getInt("lookSeed", this.lookSeed);
        this.getLookSeed();
        this.updateLook();
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lookSeed = reader.getNextInt();
        this.updateLook();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.lookSeed);
    }

    @Override
    public void init() {
        super.init();
        this.updateLook();
        this.ai = new BehaviourTreeAI<RuneboundBruteMob>(this, new ConfusedItemAttackerPlayerChaserWandererAI(null, 512, RuneboundBruteMob.getAIBattleAxe(), 40000));
    }

    public static InventoryItem getAIBattleAxe() {
        InventoryItem inventoryItem = new InventoryItem("brutesbattleaxe");
        inventoryItem.getGndData().setFloat("damage", 30.0f);
        return inventoryItem;
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            if (this.buffManager.getModifier(BuffModifiers.CAN_BREAK_OBJECTS).booleanValue()) {
                return this.getLevel().regionManager.CAN_BREAK_OBJECTS_OPTIONS;
            }
            return this.getLevel().regionManager.CAN_OPEN_DOORS_OPTIONS;
        }
        return null;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("runebound", 3);
    }

    public void getSeededRandomRuneboundBruteGear() {
        GameRandom random = new GameRandom(this.lookSeed);
        String helmetID = random.getOneOfWeighted(String.class, 40, "runeboundhornhelmet", 40, "runeboundhelmet", 20, "runeboundhood");
        String chestID = random.getOneOf("runeboundbackbones", "runeboundleatherchest");
        this.helmet = new InventoryItem(helmetID);
        this.chest = new InventoryItem(chestID);
        this.boots = new InventoryItem("runeboundboots");
    }

    public void updateLook() {
        GameRandom random = new GameRandom(this.lookSeed);
        HumanGender gender = random.getOneOfWeighted(HumanGender.class, new Object[]{60, HumanGender.MALE, 30, HumanGender.FEMALE, 10, HumanGender.NEUTRAL});
        this.look.setSkin(10);
        this.look.setEyeType(random.getOneOf(0, 2, 4));
        this.look.setEyeColor(random.getIntBetween(0, 10));
        this.look.setHair(GameHair.getRandomHairBasedOnGender(random, gender));
        if (gender == HumanGender.MALE) {
            this.look.setFacialFeature(random.getOneOf(1, 3, 4, 6, 7));
        }
        this.look.setHairColor(random.getOneOf(6, 7, 8, 9));
        this.getSeededRandomRuneboundBruteGear();
    }

    public void getLookSeed() {
        if (this.lookSeed == 0) {
            this.lookSeed = GameRandom.globalRandom.nextInt();
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameSkin gameSkin = this.look.getGameSkin(true);
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), gameSkin.getBodyTexture(), GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(RuneboundBruteMob.getTileCoordinate(x), RuneboundBruteMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        boolean inLiquid = this.inLiquid(x, y);
        if (inLiquid) {
            sprite.x = 0;
        }
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(RuneboundBruteMob.getTileCoordinate(x), RuneboundBruteMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanOptions = new HumanDrawOptions(level, this.look, false).sprite(sprite).mask(swimMask).dir(dir).light(light);
        if (inLiquid) {
            humanOptions.armSprite(2);
            humanOptions.mask(MobRegistry.Textures.runeboundboat_mask[sprite.y % 4], 0, -7);
        }
        if (this.helmet != null) {
            humanOptions.helmet(this.helmet);
        }
        if (this.chest != null) {
            humanOptions.chestplate(this.chest);
        } else {
            humanOptions.chestplate(ShirtArmorItem.addColorData(new InventoryItem("shirt"), this.look.getShirtColor()));
        }
        if (this.boots != null) {
            humanOptions.boots(this.boots);
        } else {
            humanOptions.boots(ShoesArmorItem.addColorData(new InventoryItem("shoes"), this.look.getShoesColor()));
        }
        this.setupAttackDraw(humanOptions);
        final DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
        final TextureDrawOptionsEnd boat = inLiquid ? MobRegistry.Textures.runeboundBoat.initDraw().sprite(0, sprite.y, 64).light(light).pos(drawX, drawY + 7) : null;
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
    public void setFacingDir(float deltaX, float deltaY) {
        if (!this.isAttacking && this.facingBuffer <= (float)this.getTime()) {
            super.setFacingDir(deltaX, deltaY);
            this.shouldResetFacingPos = true;
        } else if (this.isAttacking) {
            this.facingBuffer = this.getTime() + 500L;
            if (this.shouldResetFacingPos) {
                super.setFacingDir(deltaX, deltaY);
                this.shouldResetFacingPos = false;
            }
        }
    }

    @Override
    public float getAttackingMovementModifier() {
        return 0.0f;
    }

    @Override
    public ItemAttackSlot getCurrentSelectedAttackSlot() {
        return null;
    }

    @Override
    public boolean hasValidSummonItem(Item item, CheckSlotType slotType) {
        return true;
    }
}

