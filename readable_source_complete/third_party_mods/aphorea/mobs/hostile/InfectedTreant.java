/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.engine.network.NetworkClient
 *  necesse.engine.network.client.Client
 *  necesse.engine.util.GameRandom
 *  necesse.engine.window.WindowManager
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobBeforeHitCalculatedEvent
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.MobWasHitEvent
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.Blackboard
 *  necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode
 *  necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserAI
 *  necesse.entity.mobs.ai.behaviourTree.util.WandererBaseOptions
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.hostile.HostileMob
 *  necesse.gfx.GameResources
 *  necesse.gfx.Renderer
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTexture.GameTextureSection
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.axeToolItem.AxeToolItem
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameObject.TreeObject
 *  necesse.level.maps.Level
 *  necesse.level.maps.TilePosition
 *  necesse.level.maps.biomes.Biome
 *  necesse.level.maps.levelBuffManager.LevelModifiers
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.hostile;

import aphorea.registry.AphTiles;
import aphorea.utils.AphColors;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameRandom;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.WandererBaseOptions;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.axeToolItem.AxeToolItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.TreeObject;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class InfectedTreant
extends HostileMob {
    public static GameTexture texture;
    public static GameTexture texture_shadow;
    public static GameDamage collisionDamage;
    public int doAlpha = 0;
    public int jump = 0;
    public static int weaveTime;
    public static float weaveAmount;
    public static int leavesCenterWidth;
    public static int leavesMinHeight;
    public static int leavesMaxHeight;
    public static String leavesTextureName;
    public static Supplier<GameTextureSection> leavesTexture;
    protected final GameRandom drawRandom;
    public static float jumpHeight;
    public static int jumpDuration;
    public boolean mirrored;
    public int spriteY;
    public static LootTable lootTable;
    public static Map<Integer, Long> playersMessageTime;

    public InfectedTreant() {
        super(100);
        this.setSpeed(60.0f);
        this.setFriction(5.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-16, -22, 32, 32);
        this.selectBox = new Rectangle(-56, -96, 112, 104);
        this.drawRandom = new GameRandom();
        this.mirrored = this.drawRandom.getChance(0.5f);
        this.spriteY = this.drawRandom.getIntBetween(0, 3);
        this.spawnLightThreshold = new ModifierValue(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, (Object)150);
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new InfectedTreantAI(null, 192, collisionDamage, 0, 800000));
        this.jump = 0;
    }

    public void clientTick() {
        float windAmount;
        float windSpeed;
        super.clientTick();
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0f, 0.6f, 100);
        if (Settings.windEffects && (windSpeed = this.getLevel().weatherLayer.getWindSpeed()) > 0.2f && (windAmount = this.getLevel().weatherLayer.getWindAmount(this.x / 32.0f, this.y / 32.0f) * 3.0f) > 0.5f) {
            Point2D.Double windDir = this.getLevel().weatherLayer.getWindDirNormalized();
            for (float buffer = 0.016666668f * windAmount * windSpeed; buffer >= 1.0f || GameRandom.globalRandom.getChance(buffer); buffer -= 1.0f) {
                this.spawnLeafParticles(this.getLevel(), (int)(this.x / 32.0f), (int)(this.y / 32.0f), leavesMinHeight, 1, windDir, windAmount * windSpeed);
            }
        }
        if (this.dx == 0.0f && this.dy == 0.0f) {
            this.jump = 0;
        } else {
            ++this.jump;
            if (this.jump > jumpDuration) {
                this.jump = 0;
            }
        }
        if (this.jump == 0) {
            this.setFriction(20.0f);
        } else {
            this.setFriction(0.1f);
        }
    }

    public void serverTick() {
        super.serverTick();
        if (this.dx == 0.0f && this.dy == 0.0f) {
            this.jump = 0;
        } else {
            ++this.jump;
            if (this.jump > jumpDuration) {
                this.jump = 0;
            }
        }
        if (this.jump == 0) {
            this.setFriction(20.0f);
        } else {
            this.setFriction(0.1f);
        }
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (this.dx == 0.0f && this.dy == 0.0f && this.getHealthPercent() == 1.0f) {
            this.doAlpha = 2;
        }
        return super.onMouseHover(camera, perspective, debug);
    }

    protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
        if (this.getHealthPercent() != 1.0f && this.canTakeDamage() && this.getMaxHealth() > 1) {
            super.addHoverTooltips(tooltips, debug);
        }
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 110;
        drawY -= (int)(Math.sin((double)((float)this.jump / (float)jumpDuration) * Math.PI) * (double)jumpHeight);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount((Mob)this);
        float alpha = 1.0f;
        if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
            Rectangle alphaRec = new Rectangle(x - 48, y - 100, 128, 100);
            if (perspective.getCollision().intersects(alphaRec)) {
                alpha = 0.5f;
            } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                alpha = 0.5f;
            }
        }
        Consumer waveChange = GameResources.waveShader.setupGrassWaveMod(level, x / 32, y / 32, (long)weaveTime, weaveAmount, 2, this.drawRandom, GameObject.getTileSeed((int)(x / 32), (int)(y / 32), (int)0), this.mirrored, 3.0f);
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(0, this.spriteY, 128).light(light).alpha(alpha).addPositionMod(waveChange).mirror(this.mirrored, false).pos(drawX, drawY);
        list.add(new MobDrawable((DrawOptions)drawOptions){
            final /* synthetic */ DrawOptions val$drawOptions;
            {
                this.val$drawOptions = drawOptions;
            }

            public void draw(TickManager tickManager) {
                this.val$drawOptions.draw();
            }
        });
        if (!this.isWaterWalking()) {
            this.addShadowDrawables(tileList, x, y, light, camera, alpha);
        }
    }

    protected void addShadowDrawables(OrderableDrawables list, int x, int y, GameLight light, GameCamera camera, float alpha) {
        TextureDrawOptions shadowOptions;
        if (!((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).booleanValue() && !this.isRiding() && (shadowOptions = this.getShadowDrawOptions(x, y, light, camera, alpha)) != null) {
            list.add(tm -> shadowOptions.draw());
        }
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera, float alpha) {
        GameTexture shadowTexture = texture_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 8 + 4;
        return shadowTexture.initDraw().sprite(0, this.spriteY, shadowTexture.getWidth(), shadowTexture.getHeight() / 4).alpha(alpha).light(light).mirror(this.mirrored, false).pos(drawX, drawY += this.getBobbing(x, y));
    }

    public MobWasHitEvent isHit(MobWasHitEvent event, Attacker attacker) {
        if (!event.wasPrevented && this.isClient()) {
            this.getLevel().makeGrassWeave((int)this.x, (int)this.y, weaveTime, true);
            int leaves = GameRandom.globalRandom.getIntBetween(0, 2);
            this.spawnLeafParticles(this.getLevel(), (int)this.x, (int)this.y, leavesMinHeight, leaves, new Point2D.Double(), 0.0f);
        }
        return super.isHit(event, attacker);
    }

    protected void doBeforeHitCalculatedLogic(MobBeforeHitCalculatedEvent event) {
        boolean prevent = true;
        if (event.attacker != null && event.attacker.getAttackOwner() != null && event.attacker.getAttackOwner().isPlayer) {
            long now;
            long messageTime;
            PlayerMob player = (PlayerMob)event.attacker.getAttackOwner();
            if (player.isAttacking) {
                InventoryItem item = player.attackSlot.getItem(player.getInv());
                boolean bl = prevent = item == null || !(item.item instanceof AxeToolItem);
            }
            if (prevent && player.isServer() && (messageTime = playersMessageTime.getOrDefault(player.getUniqueID(), 0L).longValue()) + 5000L < (now = player.getTime())) {
                playersMessageTime.put(player.getUniqueID(), now);
                player.getServerClient().sendChatMessage((GameMessage)new LocalMessage("message", "treantattackmessage"));
            }
        }
        if (prevent) {
            event.prevent();
            event.playHitSound = false;
            event.showDamageTip = false;
        }
        super.doBeforeHitCalculatedLogic(event);
    }

    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        return attacker.isPlayer && super.canBeTargeted(attacker, attackerClient);
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        int leaves = GameRandom.globalRandom.getIntBetween(15, 20);
        this.spawnLeafParticles(this.getLevel(), (int)this.x, (int)this.y, 20, leaves, new Point2D.Double(), 0.0f);
    }

    public void spawnLeafParticles(Level level, int x, int y, int minStartHeight, int amount, Point2D.Double windDir, float windSpeed) {
        if (leavesTexture != null) {
            TreeObject.spawnLeafParticles((Level)level, (int)x, (int)y, (int)leavesCenterWidth, (int)minStartHeight, (int)leavesMaxHeight, (int)amount, (Point2D.Double)windDir, (float)windSpeed, leavesTexture);
        }
    }

    public boolean shouldDrawOnMap() {
        return true;
    }

    public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
        super.drawOnMap(tickManager, client, x, y, tileScale, drawBounds, isMinimap);
        int width = (int)tileScale;
        int height = (int)tileScale;
        Renderer.initQuadDraw((int)width, (int)height).color(AphColors.infected_dark).draw(x - width / 2, y - height / 2);
    }

    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue(BuffModifiers.SLOW, (Object)Float.valueOf(0.0f)).max((Object)Float.valueOf(0.0f)), new ModifierValue(BuffModifiers.FIRE_DAMAGE, (Object)Float.valueOf(-1.0f)).max((Object)Float.valueOf(-1.0f)), new ModifierValue(BuffModifiers.FROST_DAMAGE, (Object)Float.valueOf(-1.0f)).max((Object)Float.valueOf(-1.0f)), new ModifierValue(BuffModifiers.POISON_DAMAGE, (Object)Float.valueOf(-1.0f)).max((Object)Float.valueOf(-1.0f)));
    }

    static {
        collisionDamage = new GameDamage(60.0f, 40.0f);
        weaveTime = 250;
        weaveAmount = 0.02f;
        leavesCenterWidth = 45;
        leavesMinHeight = 60;
        leavesMaxHeight = 110;
        leavesTextureName = "infectedleaves";
        jumpHeight = 20.0f;
        jumpDuration = 8;
        lootTable = new LootTable(new LootItemInterface[]{LootItem.between((String)"infectedalloy", (int)1, (int)2), LootItem.between((String)"infectedlog", (int)4, (int)5), LootItem.between((String)"infectedsapling", (int)1, (int)2)});
        playersMessageTime = new HashMap<Integer, Long>();
    }

    public static class InfectedTreantAI
    extends SelectorAINode<InfectedTreant> {
        public final EscapeAINode<InfectedTreant> escapeAINode;
        public final CollisionPlayerChaserAI<InfectedTreant> collisionPlayerChaserAI;
        public final WandererAINode<InfectedTreant> wandererAINode;

        public InfectedTreantAI(final Supplier<Boolean> shouldEscape, int searchDistance, GameDamage damage, int knockback, int wanderFrequency) {
            this.escapeAINode = new EscapeAINode<InfectedTreant>(){

                public boolean shouldEscape(InfectedTreant mob, Blackboard<InfectedTreant> blackboard) {
                    if (mob.isHostile && !mob.isSummoned && ((Boolean)mob.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING)).booleanValue()) {
                        return true;
                    }
                    return shouldEscape != null && (Boolean)shouldEscape.get() != false;
                }
            };
            this.addChild((AINode)this.escapeAINode);
            this.collisionPlayerChaserAI = new CollisionPlayerChaserAI<InfectedTreant>(searchDistance, damage, knockback){

                public boolean attackTarget(InfectedTreant mob, Mob target) {
                    return this.attackTarget(mob, target);
                }
            };
            this.addChild((AINode)this.collisionPlayerChaserAI);
            this.wandererAINode = new WandererAINode<InfectedTreant>(wanderFrequency){

                public Point findNewPosition(InfectedTreant mob, int xOffset, int yOffset, WandererBaseOptions<InfectedTreant> baseOptions, BiFunction<TilePosition, Biome, Integer> tilePriority) {
                    Point position;
                    int i = 0;
                    while (((position = super.findNewPosition((Mob)mob, xOffset, yOffset, baseOptions, tilePriority)) == null || mob.getLevel().getTile(position.x, position.y).getID() != AphTiles.INFECTED_GRASS) && ++i < 20) {
                    }
                    return position;
                }
            };
            this.addChild((AINode)this.wandererAINode);
        }

        public boolean attackTarget(InfectedTreant mob, Mob target) {
            return CollisionChaserAINode.simpleAttack((Mob)mob, (Mob)target, (GameDamage)this.collisionPlayerChaserAI.damage, (int)this.collisionPlayerChaserAI.knockback);
        }
    }
}

