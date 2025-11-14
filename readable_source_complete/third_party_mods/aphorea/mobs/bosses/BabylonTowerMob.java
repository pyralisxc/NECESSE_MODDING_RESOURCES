/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.DifficultyBasedGetter
 *  necesse.engine.achievements.AchievementManager
 *  necesse.engine.eventStatusBars.EventStatusBarData
 *  necesse.engine.eventStatusBars.EventStatusBarData$BarCategory
 *  necesse.engine.eventStatusBars.EventStatusBarManager
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.packet.PacketChatMessage
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.registries.MusicRegistry
 *  necesse.engine.sound.GameMusic
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.SoundManager$MusicPriority
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.gameAreaSearch.GameAreaStream
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.MaxHealthGetter
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.MobHealthScaling
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.AINodeResult
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.Blackboard
 *  necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode
 *  necesse.entity.mobs.ai.behaviourTree.util.AIMover
 *  necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover
 *  necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance
 *  necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff
 *  necesse.entity.mobs.hostile.bosses.BossMob
 *  necesse.entity.objectEntity.ObjectEntity
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.GameTooltips
 *  necesse.gfx.gameTooltips.StringTooltips
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.inventory.lootTable.lootItem.RotationLootItem
 *  necesse.level.gameObject.GameObject
 *  necesse.level.maps.IncursionLevel
 *  necesse.level.maps.Level
 */
package aphorea.mobs.bosses;

import aphorea.levelevents.babylon.BabylonTowerFallingCrystalAttackEvent;
import aphorea.mobs.bosses.minions.HearthCrystalMob;
import aphorea.mobs.bosses.minions.babylon.BabylonHead;
import aphorea.objects.BabylonEntranceObject;
import aphorea.objects.BabylonTowerObject;
import aphorea.packets.AphRemoveObjectEntity;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import necesse.engine.DifficultyBasedGetter;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.eventStatusBars.EventStatusBarData;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.GameMusic;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;

public class BabylonTowerMob
extends BossMob {
    public static int BOSS_AREA_RADIUS = 1024;
    private static final AphAreaList searchArea = new AphAreaList(new AphArea((float)BOSS_AREA_RADIUS, AphColors.spinel)).setOnlyVision(false);
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(5000, 5500, 6000, 7000, 8000);
    private int aliveTimer;
    public static GameTexture icon;
    public static LootTable lootTable;
    public static LootTable privateLootTable;
    protected MobHealthScaling scaling = new MobHealthScaling((Mob)this);

    public BabylonTowerMob() {
        super(10000);
        this.difficultyChanges.setMaxHealth((DifficultyBasedGetter)MAX_HEALTH);
        this.setArmor(10);
        this.setSpeed(0.0f);
        this.setFriction(1000.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-48, -32, 96, 64);
        this.hitBox = new Rectangle(-48, -32, 96, 64);
        this.selectBox = new Rectangle(-52, -127, 104, 164);
        this.shouldSave = false;
        this.aliveTimer = 20;
        this.isStatic = true;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    public void clientTick() {
        super.clientTick();
        SoundManager.setMusic((GameMusic)MusicRegistry.WrathOfTheEmpress, (SoundManager.MusicPriority)SoundManager.MusicPriority.EVENT, (float)1.5f);
        EventStatusBarManager.registerEventStatusBar((int)this.getUniqueID(), (int)this.getHealthUnlimited(), (int)this.getMaxHealth(), () -> new EventStatusBarData(EventStatusBarData.BarCategory.boss, this.getLocalization()){

            public Color getFillColor() {
                return BabylonTowerMob.this.canTakeDamage() ? super.getFillColor() : new Color(102, 102, 102);
            }
        });
        BossNearbyBuff.applyAround((Mob)this);
        searchArea.executeClient(this.getLevel(), this.x, this.y, 1.0f, 1.0f, 0.0f, 100);
        this.tickAlive();
    }

    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround((Mob)this);
        this.tickAlive();
    }

    public boolean canBeTargetedFromAdjacentTiles() {
        return true;
    }

    private void tickAlive() {
        --this.aliveTimer;
        if (this.aliveTimer <= 0) {
            this.remove();
        }
    }

    public void keepAlive(BabylonTowerObject.BabylonTowerObjectEntity entity) {
        this.aliveTimer = 20;
        this.setPos(entity.getMobX(), entity.getMobY(), true);
    }

    public void playDeathSound() {
    }

    public boolean canBePushed(Mob other) {
        return false;
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    public boolean canTakeDamage() {
        return !this.hearthCrystalClose();
    }

    public boolean countDamageDealt() {
        return true;
    }

    public boolean canPushMob(Mob other) {
        return false;
    }

    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    public void setupHealthPacket(PacketWriter writer, boolean isFull) {
        this.scaling.setupHealthPacket(writer, isFull);
        super.setupHealthPacket(writer, isFull);
    }

    public void applyHealthPacket(PacketReader reader, boolean isFull) {
        this.scaling.applyHealthPacket(reader, isFull);
        super.applyHealthPacket(reader, isFull);
    }

    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);
        if (this.scaling != null) {
            this.scaling.updatedMaxHealth();
        }
    }

    public boolean shouldDrawOnMap() {
        return true;
    }

    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int drawX = x - 16;
        int drawY = y - 16;
        icon.initDraw().sprite(0, 0, 32).size(32, 32).draw(drawX, drawY);
    }

    public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
        return new Rectangle(-16, -16, 32, 32);
    }

    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        ObjectEntity objectEntity;
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> {
            LocalMessage message = new LocalMessage("misc", "bossdefeat", "name", this.getLocalization());
            c.sendPacket((Packet)new PacketChatMessage((GameMessage)message));
        });
        if (!this.isDamagedByPlayers) {
            AchievementManager.checkMeAndThisArmyKill((Level)this.getLevel(), attackers);
        }
        int x = this.getTileX() - 1;
        int y = this.getTileY() - 1;
        GameObject object = this.getLevel().getObject(x, y);
        if (object != null && (objectEntity = object.getCurrentObjectEntity(this.getLevel(), x, y)) instanceof BabylonTowerObject.BabylonTowerObjectEntity) {
            objectEntity.remove();
            this.getServer().network.sendToClientsAtEntireLevel((Packet)new AphRemoveObjectEntity(x, y), this.getLevel());
            boolean openingStaircase = false;
            if (!(this.getLevel() instanceof IncursionLevel)) {
                Point entrancePosition = new Point(x + 1, y + 2);
                if (!this.getLevel().getLevelObject(entrancePosition.x, entrancePosition.y).getMultiTile().getMasterObject().getStringID().equals("babylonentrance")) {
                    this.getLevel().entityManager.events.add((LevelEvent)new BabylonEntranceObject.BabylonEntranceEvent(x + 1, y + 2));
                    openingStaircase = true;
                }
            }
            boolean finalOpeningStaircase = openingStaircase;
            attackers.stream().map(Attacker::getFirstPlayerOwner).filter(Objects::nonNull).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).distinct().forEach(c -> {
                if (finalOpeningStaircase) {
                    c.sendChatMessage((GameMessage)new LocalMessage("misc", "staircaseopening"));
                }
            });
        }
    }

    public int getParts() {
        return (int)Math.ceil(this.getHealthPercent() / 0.2f) - 1;
    }

    public int projectileRate() {
        return this.getParts() + 3;
    }

    public boolean hearthCrystalClose() {
        return this.getLevel() != null && this.getLevel().entityManager.mobs.stream().anyMatch(m -> Objects.equals(m.getStringID(), "hearthcrystal") && m.getDistance((Mob)this) < (float)BOSS_AREA_RADIUS);
    }

    public void init() {
        super.init();
        SoundManager.playSound((GameSound)GameResources.roar, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        this.ai = new BehaviourTreeAI((Mob)this, new BabylonTowerAI(), (AIMover)new FlyingAIMover());
    }

    static {
        lootTable = new LootTable(new LootItemInterface[]{LootItem.between((String)"spinel", (int)8, (int)16)});
        privateLootTable = new LootTable(new LootItemInterface[]{new LootItem("runeofbabylontower"), RotationLootItem.globalLootRotation((LootItemInterface[])new LootItemInterface[]{new LootItem("babylongreatsword"), new LootItem("babyloncandle")})});
    }

    public static class BabylonTowerAI<T extends BabylonTowerMob>
    extends SelectorAINode<T> {
        static GameDamage projectileDamage = new GameDamage(20.0f, 40.0f);
        public ArrayList<BabylonTowerActionAiNode> stages = new ArrayList();
        public int stagesUntilNow = 0;
        public int currentStage = 0;
        public int currentStageTick = 0;
        public int currentStageTickDuration = 120;
        public boolean babylonSummoned = false;
        public boolean alreadyBulkStage = false;
        public Map<String, Object> saveData = new HashMap<String, Object>();
        public static ArrayList<Integer> projectileStages = new ArrayList();
        public static ArrayList<Integer> projectileBulkStages = new ArrayList();
        public static int hearthPilarStage = 3;

        public BabylonTowerAI() {
            this.addChild(new AINode<T>(){

                protected void onRootSet(AINode<T> aiNode, T mob, Blackboard<T> blackboard) {
                }

                public void init(T mob, Blackboard<T> blackboard) {
                }

                public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                    if (!babylonSummoned && mob.getHealthPercent() <= 0.6f) {
                        babylonSummoned = true;
                        this.summonBabylon(mob);
                        float angle = GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2);
                        float distanceFromCenter = 0.25f;
                        int health = 80 + (int)(40L * this.streamPossibleTargets(mob).count());
                        boolean clockWise = GameRandom.globalRandom.getChance(0.5f);
                        this.summonHearthCrystalMoved(mob, distanceFromCenter, angle, health, 0.0f, 0.15f, 0.5f, clockWise);
                        this.summonHearthCrystalMoved(mob, distanceFromCenter, angle + 2.0943952f, health, 0.0f, 0.15f, 0.5f, clockWise);
                        this.summonHearthCrystalMoved(mob, distanceFromCenter, angle + 4.1887903f, health, 0.0f, 0.15f, 0.5f, clockWise);
                    }
                    return AINodeResult.FAILURE;
                }
            });
            this.addChild(new BabylonTowerActionAiNode(){

                @Override
                public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard) {
                    if (time % (50 * ((BabylonTowerMob)((Object)mob)).projectileRate()) == 0) {
                        this.summonRandomCrystal(mob);
                    }
                }

                @Override
                public int getStageDuration(T mob) {
                    return 5000;
                }
            });
            this.addChild(new BabylonTowerActionAiNode(){

                @Override
                public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard) {
                    if (time % (100 * ((BabylonTowerMob)((Object)mob)).projectileRate()) == 0) {
                        this.summonCrystalToAllTargets(mob, GameRandom.globalRandom.getFloatBetween(0.0f, 2.0f), 80);
                    }
                }

                @Override
                public int getStageDuration(T mob) {
                    return 5000;
                }
            });
            this.addChild(new BabylonTowerActionAiNode(){

                @Override
                public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard) {
                    if (time % (200 * ((BabylonTowerMob)((Object)mob)).projectileRate()) == 0) {
                        this.summonCrystalToAllTargets(mob, GameRandom.globalRandom.getFloatBetween(0.0f, 2.0f), 80);
                    }
                    if (time % (300 * ((BabylonTowerMob)((Object)mob)).projectileRate()) == 0) {
                        float angle = GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2);
                        float prediction = GameRandom.globalRandom.getFloatBetween(0.0f, 0.5f);
                        for (int i = 0; i < 12; ++i) {
                            this.summonCrystalAroundAllTargets(mob, prediction, 5, angle + (float)Math.PI * (float)i / 6.0f, 100.0f);
                        }
                    }
                }

                @Override
                public int getStageDuration(T mob) {
                    return 5000;
                }
            });
            this.addChild(new BabylonTowerActionAiNode(){

                @Override
                public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard) {
                    int health = 80 + (int)(40L * this.streamPossibleTargets(mob).count());
                    boolean clockWise = GameRandom.globalRandom.getChance(0.5f);
                    switch (GameRandom.globalRandom.getIntBetween(0, 5)) {
                        case 0: {
                            float angle = GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2);
                            this.summonHearthCrystalMoved(mob, 0.15f, angle, health, 0.0f, 0.15f, 0.5f, clockWise);
                            this.summonHearthCrystalMoved(mob, 0.15f, angle + 2.0943952f, health, 0.0f, 0.15f, 0.5f, clockWise);
                            this.summonHearthCrystalMoved(mob, 0.15f, angle + 4.1887903f, health, 0.0f, 0.15f, 0.5f, clockWise);
                            break;
                        }
                        case 1: {
                            float angle = GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2);
                            this.summonHearthCrystalMoved(mob, 0.15f, angle, health, 0.0f, 0.15f, 0.5f, clockWise);
                            this.summonHearthCrystalMoved(mob, 0.15f, angle + 2.0943952f, health, 2.0943952f, 0.15f, 0.5f, clockWise);
                            this.summonHearthCrystalMoved(mob, 0.15f, angle + 4.1887903f, health, 4.1887903f, 0.15f, 0.5f, clockWise);
                            break;
                        }
                        case 2: {
                            this.summonHearthCrystalCenter(mob, health, 0.0f, 0.15f, 0.5f, clockWise);
                            this.summonHearthCrystalCenter(mob, health, 2.0943952f, 0.15f, 0.5f, clockWise);
                            this.summonHearthCrystalCenter(mob, health, 4.1887903f, 0.15f, 0.5f, clockWise);
                            break;
                        }
                        case 3: {
                            this.summonHearthCrystalCenter(mob, health / 2, 0.0f, 0.25f, 0.45f, !clockWise);
                            this.summonHearthCrystalCenter(mob, health, 0.0f, 0.15f, 0.65f, clockWise);
                            this.summonHearthCrystalCenter(mob, health, (float)Math.PI, 0.15f, 0.65f, clockWise);
                            break;
                        }
                        case 4: {
                            float angle = GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2);
                            float distanceFromCenter = GameRandom.globalRandom.getFloatBetween(0.05f, 0.2f);
                            this.summonHearthCrystalMoved(mob, distanceFromCenter, angle, health, 0.0f, 0.15f, 0.5f, clockWise);
                            this.summonHearthCrystalMoved(mob, distanceFromCenter, angle + 2.0943952f, health, 0.0f, 0.15f, 0.5f, clockWise);
                            this.summonHearthCrystalMoved(mob, distanceFromCenter, angle + 4.1887903f, health, 0.0f, 0.15f, 0.5f, clockWise);
                            break;
                        }
                        case 5: {
                            float angle = GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2);
                            float distanceFromCenter = GameRandom.globalRandom.getFloatBetween(0.05f, 0.2f);
                            this.summonHearthCrystalMoved(mob, distanceFromCenter, angle, health, 0.0f, 0.15f, 0.5f, clockWise);
                            this.summonHearthCrystalMoved(mob, distanceFromCenter, angle + 2.0943952f, health, 2.0943952f, 0.15f, 0.5f, clockWise);
                            this.summonHearthCrystalMoved(mob, distanceFromCenter, angle + 4.1887903f, health, 4.1887903f, 0.15f, 0.5f, clockWise);
                            break;
                        }
                    }
                }

                @Override
                public int getStageDuration(T mob) {
                    return 50;
                }
            });
            this.addChild(new BabylonTowerActionAiNode(){

                @Override
                public void startStage(T mob) {
                    super.startStage(mob);
                    saveData.put("startAngle", Float.valueOf(GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2)));
                    saveData.put("clockWise", GameRandom.globalRandom.getChance(0.5f));
                }

                @Override
                public void doTickAction(T mob, int time, int duration, float progress, Blackboard<T> blackboard) {
                    if (time % (100 * ((BabylonTowerMob)((Object)mob)).projectileRate()) == 0) {
                        this.summonRandomCrystal(mob);
                    }
                    if (time % 100 == 0) {
                        float startAngle = ((Float)saveData.get("startAngle")).floatValue();
                        boolean clockWise = (Boolean)saveData.get("clockWise");
                        float angleProgress = startAngle + progress * (float)Math.PI * 4.0f * (float)(clockWise ? 1 : -1);
                        float distance = (float)BOSS_AREA_RADIUS * (0.05f + progress * 0.9f);
                        for (int i = 0; i < 12; ++i) {
                            float targetX = ((BabylonTowerMob)((Object)mob)).x + distance * (float)Math.cos(angleProgress + (float)Math.PI * (float)i / 6.0f);
                            float targetY = ((BabylonTowerMob)((Object)mob)).y + distance * (float)Math.sin(angleProgress + (float)Math.PI * (float)i / 6.0f);
                            this.summonFallingCrystal(mob, targetX, targetY, 4);
                        }
                    }
                }

                @Override
                public int getStageDuration(T mob) {
                    return 10000;
                }
            });
        }

        public void selectNextStage(T mob) {
            int selected;
            ++this.stagesUntilNow;
            if (!this.alreadyBulkStage && mob.getHealthPercent() < 0.4f) {
                this.stages.get(hearthPilarStage).startStage(mob);
                selected = this.selectStage(projectileBulkStages);
                this.alreadyBulkStage = true;
            } else if (this.stagesUntilNow == 5 || this.stagesUntilNow % 10 == 0) {
                selected = hearthPilarStage;
            } else if (this.stagesUntilNow % 5 == 0) {
                selected = this.selectStage(projectileBulkStages);
                this.alreadyBulkStage = true;
            } else {
                selected = this.selectStage(projectileStages);
            }
            this.currentStage = selected;
            this.currentStageTick = 0;
            this.currentStageTickDuration = this.stages.get(this.currentStage).getStageDuration(mob) / 50;
            this.stages.get(this.currentStage).startStage(mob);
        }

        public int selectStage(ArrayList<Integer> stages) {
            int selected;
            while ((selected = ((Integer)GameRandom.globalRandom.getOneOf(stages)).intValue()) == this.currentStage) {
            }
            return selected;
        }

        public GameAreaStream<Mob> streamPossibleTargets(T mob) {
            return new TargetFinderDistance(BOSS_AREA_RADIUS).streamPlayersInRange(new Point((int)((BabylonTowerMob)((Object)mob)).x, (int)((BabylonTowerMob)((Object)mob)).y), mob).filter(m -> m != null && !m.removed() && (m.isHuman && m.getTeam() != -1 || m.isPlayer)).map(m -> m);
        }

        public void summonCrystalToAllTargets(T mob, float prediction, int imprecision) {
            this.streamPossibleTargets(mob).forEach(target -> this.summonCrystalToTarget(mob, (Mob)target, target.dx * prediction, target.dy * prediction, imprecision));
        }

        public void summonCrystalAroundAllTargets(T mob, float prediction, int imprecision, float angle, float distance) {
            this.streamPossibleTargets(mob).forEach(target -> this.summonCrystalToTarget(mob, (Mob)target, target.dx * prediction + (float)Math.cos(angle) * distance, target.dy * prediction + (float)Math.sin(angle) * distance, imprecision));
        }

        public void summonCrystalToTarget(T mob, Mob target, float extraX, float extraY, int imprecision) {
            this.summonFallingCrystal(mob, target.x + extraX, target.y + extraY, imprecision);
        }

        public void summonRandomCrystal(T mob) {
            this.summonRandomCrystalBetweenDistances(mob, 0.0f, 1.0f);
        }

        public void summonRandomCrystalBetweenDistances(T mob, float minDistance, float maxDistance) {
            this.summonRandomCrystalAtDistance(mob, minDistance + GameRandom.globalRandom.getFloatBetween(0.0f, maxDistance - minDistance));
        }

        public void summonRandomCrystalAtDistance(T mob, float distance) {
            float angle = GameRandom.globalRandom.getFloatBetween(0.0f, (float)Math.PI * 2);
            int targetX = (int)((double)((BabylonTowerMob)((Object)mob)).x + Math.cos(angle) * (double)BOSS_AREA_RADIUS * (double)distance);
            int targetY = (int)((double)((BabylonTowerMob)((Object)mob)).y + Math.sin(angle) * (double)BOSS_AREA_RADIUS * (double)distance);
            this.summonFallingCrystal(mob, targetX, targetY);
        }

        public void summonFallingCrystal(T mob, float targetX, float targetY, int imprecision) {
            this.summonFallingCrystal(mob, GameRandom.globalRandom.getFloatOffset(targetX, (float)imprecision), GameRandom.globalRandom.getFloatOffset(targetY, (float)imprecision));
        }

        public void summonFallingCrystal(T mob, float targetX, float targetY) {
            if (mob.getDistance(targetX, targetY) <= (float)BOSS_AREA_RADIUS) {
                mob.getLevel().entityManager.events.add((LevelEvent)new BabylonTowerFallingCrystalAttackEvent((Mob)mob, (int)targetX, (int)targetY, GameRandom.globalRandom, projectileDamage));
            }
        }

        public void summonHearthCrystalCenter(T mob, int health, float angleOffset, float radius, float constantTime, boolean clockWise) {
            if (mob.isServer()) {
                HearthCrystalMob hearthCrystalMob = (HearthCrystalMob)MobRegistry.getMob((String)"hearthcrystal", (Level)mob.getLevel());
                hearthCrystalMob.setMaxHealth(health);
                hearthCrystalMob.setHealth(health);
                hearthCrystalMob.setCircularMovement(mob.getX(), mob.getY(), angleOffset, (float)BOSS_AREA_RADIUS * radius, constantTime, clockWise);
                mob.getLevel().entityManager.addMob((Mob)hearthCrystalMob, ((BabylonTowerMob)((Object)mob)).x, ((BabylonTowerMob)((Object)mob)).y);
            }
        }

        public void summonHearthCrystalMoved(T mob, float distanceFromCenter, float distanceAngle, int health, float angleOffset, float radius, float constantTime, boolean clockWise) {
            if (mob.isServer()) {
                HearthCrystalMob hearthCrystalMob = (HearthCrystalMob)MobRegistry.getMob((String)"hearthcrystal", (Level)mob.getLevel());
                hearthCrystalMob.setMaxHealth(health);
                hearthCrystalMob.setHealth(health);
                hearthCrystalMob.setCircularMovement(mob.getX() + (int)((double)((float)BOSS_AREA_RADIUS * distanceFromCenter) * Math.cos(distanceAngle)), mob.getY() + (int)((double)((float)BOSS_AREA_RADIUS * distanceFromCenter) * Math.sin(distanceAngle)), angleOffset, (float)BOSS_AREA_RADIUS * radius, constantTime, clockWise);
                mob.getLevel().entityManager.addMob((Mob)hearthCrystalMob, ((BabylonTowerMob)((Object)mob)).x, ((BabylonTowerMob)((Object)mob)).y);
            }
        }

        public void summonBabylon(T mob) {
            if (mob.isServer()) {
                BabylonHead babylon = (BabylonHead)MobRegistry.getMob((String)"babylon", (Level)mob.getLevel());
                mob.getLevel().entityManager.addMob((Mob)babylon, ((BabylonTowerMob)((Object)mob)).x, ((BabylonTowerMob)((Object)mob)).y);
            }
        }

        static {
            Collections.addAll(projectileStages, 0, 1, 2);
            Collections.addAll(projectileBulkStages, 4);
        }

        public abstract class BabylonTowerActionAiNode
        extends AINode<T> {
            public final int stageNumber;

            public BabylonTowerActionAiNode() {
                this.stageNumber = BabylonTowerAI.this.stages.size();
                BabylonTowerAI.this.stages.add(this);
            }

            protected void onRootSet(AINode<T> aiNode, T t, Blackboard<T> blackboard) {
            }

            public void init(T t, Blackboard<T> blackboard) {
            }

            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                if (BabylonTowerAI.this.currentStage != this.stageNumber) {
                    return AINodeResult.FAILURE;
                }
                if (BabylonTowerAI.this.currentStageTickDuration <= BabylonTowerAI.this.currentStageTick) {
                    BabylonTowerAI.this.selectNextStage(mob);
                    return AINodeResult.SUCCESS;
                }
                ++BabylonTowerAI.this.currentStageTick;
                this.doTickAction(mob, BabylonTowerAI.this.currentStageTick * 50, BabylonTowerAI.this.currentStageTickDuration * 50, (float)BabylonTowerAI.this.currentStageTick / (float)BabylonTowerAI.this.currentStageTickDuration, blackboard);
                if (BabylonTowerAI.this.currentStageTickDuration <= BabylonTowerAI.this.currentStageTick) {
                    BabylonTowerAI.this.selectNextStage(mob);
                }
                return AINodeResult.SUCCESS;
            }

            public abstract void doTickAction(T var1, int var2, int var3, float var4, Blackboard<T> var5);

            public abstract int getStageDuration(T var1);

            public void startStage(T mob) {
            }
        }
    }
}

