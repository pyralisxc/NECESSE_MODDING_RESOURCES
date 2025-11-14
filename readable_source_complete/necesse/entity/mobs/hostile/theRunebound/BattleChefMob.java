/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.theRunebound;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.WandererBaseOptions;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
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
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class BattleChefMob
extends HostileItemAttackerMob {
    public static LootTable lootTable = new LootTable(new LootItemMultiplierIgnored(new ChanceLootItem(0.02f, "rollingpin", new GNDItemMap().setInt("upgradeLevel", 100))), new LootItemMultiplierIgnored(new ChanceLootItem(0.02f, "butcherscleaver", new GNDItemMap().setInt("upgradeLevel", 100))), new LootItemMultiplierIgnored(new ChanceLootItem(0.005f, "chefsspecial", new GNDItemMap().setInt("upgradeLevel", 100))), ChanceLootItem.between(0.5f, "coin", 200, 600), ChanceLootItem.between(0.25f, "altardust", 5, 30));
    public int lookSeed;
    public HumanLook look = new HumanLook();
    public InventoryItem helmet;
    public InventoryItem chest;
    public InventoryItem boots;
    public Point baseTile;
    public boolean spawnedFromBossBringsInvadersPerk = false;
    private String mobName = "";

    public BattleChefMob() {
        super(900);
        this.setSpeed(50.0f);
        this.setFriction(3.0f);
        this.setArmor(30);
        this.getLookSeed();
        this.attackCooldown = 1000;
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
        save.addPoint("baseTile", this.baseTile);
        save.addBoolean("spawnedFromBossBringsInvadersPerk", this.spawnedFromBossBringsInvadersPerk);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.lookSeed = save.getInt("lookSeed", this.lookSeed);
        this.baseTile = save.getPoint("baseTile", new Point(this.getTileX(), this.getTileY()), false);
        this.spawnedFromBossBringsInvadersPerk = save.getBoolean("spawnedFromBossBringsInvadersPerk", this.spawnedFromBossBringsInvadersPerk);
        this.getLookSeed();
        this.updateLook();
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lookSeed = reader.getNextInt();
        this.mobName = reader.getNextString();
        this.updateLook();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.lookSeed);
        writer.putNextString(this.mobName);
    }

    @Override
    public ItemAttackSlot getCurrentSelectedAttackSlot() {
        return null;
    }

    @Override
    public boolean hasValidSummonItem(Item item, CheckSlotType slotType) {
        return false;
    }

    @Override
    public void init() {
        super.init();
        if (this.baseTile == null) {
            this.baseTile = new Point(this.getX() / 32, this.getY() / 32);
        }
        this.updateLook();
        this.ai = new BehaviourTreeAI<BattleChefMob>(this, new BattleChefAI(512));
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
        if (this.spawnedFromBossBringsInvadersPerk) {
            return lootTable;
        }
        return new LootTable(new LootItemMultiplierIgnored(RotationLootItem.privateLootRotation(new LootItem("battlechefhat", 1, new GNDItemMap().setInt("upgradeLevel", 100)), new LootItem("battlechefchestplate", 1, new GNDItemMap().setInt("upgradeLevel", 100)), new LootItem("battlechefboots", 1, new GNDItemMap().setInt("upgradeLevel", 100)))), lootTable);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("battlechef", 3);
    }

    public void getBattleChefGear() {
        this.helmet = new InventoryItem("battlechefhat");
        this.chest = new InventoryItem("battlechefchestplate");
        this.boots = new InventoryItem("battlechefboots");
    }

    public void updateLook() {
        GameRandom random = new GameRandom(this.lookSeed);
        this.look = new HumanLook(random, true);
        HumanGender gender = random.getOneOfWeighted(HumanGender.class, new Object[]{40, HumanGender.MALE, 40, HumanGender.FEMALE, 20, HumanGender.NEUTRAL});
        this.look.setHair(GameHair.getRandomHairBasedOnGender(random, gender));
        if (gender == HumanGender.MALE) {
            this.look.setFacialFeature(GameHair.getRandomFacialFeature(random));
        }
        this.look.setHairColor(GameHair.getRandomHairColorAboveColorWeight(random, GameHair.UNCOMMON_HAIR_COLOR_WEIGHT));
        this.mobName = HumanMob.getRandomName(random, gender);
        this.getBattleChefGear();
    }

    public void getLookSeed() {
        if (this.lookSeed == 0) {
            this.lookSeed = GameRandom.globalRandom.nextInt();
        }
    }

    @Override
    public GameMessage getLocalization() {
        return new LocalMessage("mob", "battlechef", "randomname", this.mobName);
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
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        boolean inLiquid = this.inLiquid(x, y);
        if (inLiquid) {
            sprite.x = 0;
        }
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
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

    public static class BattleChefAI<T extends BattleChefMob>
    extends ItemAttackerPlayerChaserWandererAI<T> {
        boolean hasTalked = false;

        public BattleChefAI(int searchDistance) {
            super(null, searchDistance, BattleChefAI.getAIWeapons(), 40000);
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob target = blackboard.getObject(Mob.class, "currentTarget");
            if (target != null && !this.hasTalked) {
                this.hasTalked = true;
                this.sendRandomAttackMessage(mob);
            }
            return super.tick(mob, blackboard);
        }

        public static InventoryItem getAIWeapons() {
            InventoryItem inventoryItem = new InventoryItem("chefsspecial");
            inventoryItem.getGndData().setFloat("damage", 55.0f);
            return inventoryItem;
        }

        @Override
        public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            super.onRootSet(root, mob, blackboard);
            blackboard.put("baseOptions", new WandererBaseOptions<T>(){

                @Override
                public Point getBaseTile(T mob) {
                    return ((BattleChefMob)mob).baseTile;
                }
            });
        }

        private void sendRandomAttackMessage(T mob) {
            if (!((Entity)mob).isServer()) {
                return;
            }
            ((Entity)mob).getLevel().getServer().network.sendToClientsWithEntity(new PacketMobChat(((Entity)mob).getUniqueID(), "mobmsg", this.getRandomAttackKey()), (RegionPositionGetter)mob);
        }

        private String getRandomAttackKey() {
            int nextInt = GameRandom.globalRandom.nextInt(4) + 1;
            return "battlechefangry" + nextInt;
        }
    }
}

