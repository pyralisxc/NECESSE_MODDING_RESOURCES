/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedItemAttackerPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.hostile.HostileItemAttackerMob;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.particle.Particle;
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
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;
import necesse.level.maps.light.GameLight;

public class PossessedSettlerMob
extends HostileItemAttackerMob {
    protected HumanMob humanMob;
    protected int lookSeed;
    protected HumanLook look = new HumanLook();
    protected InventoryItem weapon;
    protected InventoryItem helmet;
    protected InventoryItem chestplate;
    protected InventoryItem boots;

    public PossessedSettlerMob() {
        super(500);
        this.setSpeed(30.0f);
        this.setFriction(3.0f);
        this.setArmor(40);
        this.lookSeed = GameRandom.globalRandom.nextInt();
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
        this.setupLook();
        if (this.humanMob != null) {
            save.addSafeString("humanStringID", this.humanMob.getStringID());
        }
        save.addInt("lookSeed", this.lookSeed);
        SaveData lookSave = new SaveData("look");
        this.look.addSaveData(lookSave);
        save.addSaveData(lookSave);
        if (this.weapon != null) {
            save.addSaveData(this.weapon.getSaveData("weapon"));
        }
        if (this.helmet != null) {
            save.addSaveData(this.helmet.getSaveData("helmet"));
        }
        if (this.chestplate != null) {
            save.addSaveData(this.chestplate.getSaveData("chestplate"));
        }
        if (this.boots != null) {
            save.addSaveData(this.boots.getSaveData("boots"));
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        String humanMobStringID = save.getSafeString("humanStringID", null, false);
        if (humanMobStringID != null) {
            Mob mob = MobRegistry.getMob(humanMobStringID, this.getLevel());
            this.humanMob = mob instanceof HumanMob ? (HumanMob)mob : null;
        }
        this.lookSeed = save.getInt("lookSeed", this.lookSeed, false);
        LoadData lookSave = save.getFirstLoadDataByName("look");
        if (lookSave != null) {
            this.look = new HumanLook();
            this.look.applyLoadData(lookSave);
            if (this.humanMob != null) {
                this.humanMob.look = this.look;
            }
        }
        this.weapon = InventoryItem.fromLoadData(save.getFirstLoadDataByName("weapon"));
        this.helmet = InventoryItem.fromLoadData(save.getFirstLoadDataByName("helmet"));
        this.chestplate = InventoryItem.fromLoadData(save.getFirstLoadDataByName("chestplate"));
        this.boots = InventoryItem.fromLoadData(save.getFirstLoadDataByName("boots"));
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        this.setupLook();
        if (this.humanMob != null) {
            writer.putNextInt(this.humanMob.getID());
        } else {
            writer.putNextInt(-1);
        }
        writer.putNextInt(this.lookSeed);
        this.look.setupContentPacket(writer, true);
        InventoryItem.addPacketContent(this.weapon, writer);
        InventoryItem.addPacketContent(this.helmet, writer);
        InventoryItem.addPacketContent(this.chestplate, writer);
        InventoryItem.addPacketContent(this.boots, writer);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int humanMobID = reader.getNextInt();
        if (humanMobID != -1) {
            Mob mob = MobRegistry.getMob(humanMobID, this.getLevel());
            this.humanMob = mob instanceof HumanMob ? (HumanMob)mob : null;
        }
        this.lookSeed = reader.getNextInt();
        this.look = new HumanLook();
        this.look.applyContentPacket(reader);
        if (this.humanMob != null) {
            this.humanMob.look = this.look;
        }
        this.weapon = InventoryItem.fromContentPacket(reader);
        this.helmet = InventoryItem.fromContentPacket(reader);
        this.chestplate = InventoryItem.fromContentPacket(reader);
        this.boots = InventoryItem.fromContentPacket(reader);
    }

    @Override
    public void init() {
        super.init();
        this.refreshAI(false);
        this.setupLook();
        if (this.humanMob != null) {
            this.humanMob.setLevel(this.getLevel());
        }
    }

    public void setWeapon(InventoryItem weapon, boolean updateTier) {
        this.weapon = weapon;
        if (this.isInitialized()) {
            this.refreshAI(updateTier);
        } else {
            this.updateItemTier(weapon);
        }
    }

    public void setArmor(String helmetStringID, String chestplateStringID, String bootsStringID, boolean updateTier) {
        InventoryItem helmet = helmetStringID == null ? null : new InventoryItem(helmetStringID);
        InventoryItem chestplate = chestplateStringID == null ? null : new InventoryItem(chestplateStringID);
        InventoryItem boots = bootsStringID == null ? null : new InventoryItem(bootsStringID);
        this.setArmor(helmet, chestplate, boots, updateTier);
    }

    public void setArmor(InventoryItem helmet, InventoryItem chestplate, InventoryItem boots, boolean updateTier) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.boots = boots;
        if (updateTier) {
            this.updateItemTier(helmet);
            this.updateItemTier(chestplate);
            this.updateItemTier(boots);
        }
    }

    public void refreshAI(boolean updateWeaponTier) {
        if (this.weapon == null) {
            this.weapon = (InventoryItem)GameRandom.globalRandom.getOneOf(() -> {
                InventoryItem item = new InventoryItem("chargeshower");
                item.getGndData().setFloat("attackSpeedMod", 3.0f);
                item.getGndData().setFloat("damageMod", 0.1f);
                return item;
            }, () -> {
                InventoryItem item = new InventoryItem("chargebeam");
                item.getGndData().setFloat("damageMod", 0.1f);
                return item;
            });
            updateWeaponTier = true;
        }
        if (updateWeaponTier) {
            this.updateItemTier(this.weapon);
        }
        this.ai = new BehaviourTreeAI<PossessedSettlerMob>(this, new ConfusedItemAttackerPlayerChaserWandererAI(null, 512, this.weapon, 40000));
    }

    public void updateItemTier(InventoryItem item) {
        IncursionData incursionData;
        if (item == null) {
            return;
        }
        item.item.setUpgradeTier(item, 1.0f);
        if (this.getLevel() instanceof IncursionLevel && (incursionData = ((IncursionLevel)this.getLevel()).incursionData) != null) {
            item.item.setUpgradeTier(item, incursionData.getTabletTier());
        }
    }

    protected void setupLook() {
        if (this.look != null && this.humanMob != null) {
            return;
        }
        this.lookSeed = GameRandom.globalRandom.nextInt();
        this.look = new HumanLook();
        List settlers = SettlerRegistry.streamSettlers().filter(settler -> settler.isPartOfCompleteHost).collect(Collectors.toList());
        Settler settler2 = (Settler)GameRandom.globalRandom.getOneOf(settlers);
        SettlerMob mob = settler2.getNewSettlerMob(this.getLevel(), null);
        mob.getMob().setLevel(this.getLevel());
        mob.setSettlerSeed(this.lookSeed, false);
        if (mob instanceof HumanMob) {
            this.humanMob = (HumanMob)mob;
            this.look = new HumanLook(this.humanMob.look);
        } else {
            this.humanMob = null;
            this.look = new HumanLook();
            this.look.randomizeLook(GameRandom.globalRandom, true);
        }
        this.look.setEyeColor(12);
        this.look.setHairColor(2);
        this.look.setSkin(13);
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
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        for (int i = 0; i < 25; ++i) {
            int lifeTime = GameRandom.globalRandom.getIntBetween(500, 2000);
            float lifePerc = (float)lifeTime / 5000.0f;
            float startHeight = 10.0f;
            float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(50, 100) * lifePerc;
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), this.y + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), typeSwitcher.next()).sizeFades(20, 30).movesFriction(GameRandom.globalRandom.getFloatBetween(-50.0f, 50.0f), GameRandom.globalRandom.getFloatBetween(-30.0f, 30.0f), 0.8f).heightMoves(startHeight, height).colorRandom(0.0f, 0.8f, 0.5f, 10.0f, 0.1f, 0.1f).lifeTime(lifeTime);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        boolean inLiquid = this.inLiquid(x, y);
        if (inLiquid) {
            sprite.x = 0;
        }
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y)).getMobSinkingAmount(this);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        HumanDrawOptions humanOptions = new HumanDrawOptions(level, this.look, false).sprite(sprite).mask(swimMask).dir(dir).light(light);
        if (inLiquid) {
            humanOptions.armSprite(2);
            humanOptions.mask(MobRegistry.Textures.boat_mask[sprite.y % 4], 0, -7);
        }
        if (this.humanMob != null) {
            this.humanMob.setDefaultArmor(humanOptions);
        }
        if (this.helmet != null) {
            humanOptions.helmet(this.helmet);
        }
        if (this.chestplate != null) {
            humanOptions.chestplate(this.chestplate);
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
    public ItemAttackSlot getCurrentSelectedAttackSlot() {
        return null;
    }

    @Override
    public boolean hasValidSummonItem(Item item, CheckSlotType slotType) {
        return true;
    }

    @Override
    public float getWeaponSkillPercent(InventoryItem item) {
        return 0.8f;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.concat(super.getDefaultModifiers(), Stream.of(new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(-0.5f)), new ModifierValue<Float>(BuffModifiers.PROJECTILE_VELOCITY, Float.valueOf(-0.25f)), new ModifierValue<Float>(BuffModifiers.THROWING_VELOCITY, Float.valueOf(-0.25f))));
    }
}

