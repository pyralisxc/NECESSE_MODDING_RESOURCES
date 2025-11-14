/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.NetworkClient
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PathDoorOption
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.Blackboard
 *  necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode
 *  necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.friendly.FriendlyMob
 *  necesse.entity.particle.FleshParticle
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.friendly;

import aphorea.mobs.ai.AphRunFromMobsAI;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.FriendlyMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class WildPhosphorSlime
extends FriendlyMob {
    public static GameTexture texture;
    public static GameTexture texture_scared;
    public static LootTable lootTable;
    int dayCount = 0;
    int time;
    int sprite;
    int lightTime;
    static int lightCycle;

    public WildPhosphorSlime() {
        super(1);
        this.setSpeed(30.0f);
        this.setFriction(0.5f);
        this.collision = new Rectangle(-7, -5, 14, 10);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -28 - this.getFlyingHeight(), 32, 34 + this.getFlyingHeight());
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new WildPhosphorSlimeAI());
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void clientTick() {
        super.clientTick();
        ++this.time;
        if (this.time >= (this.isScared(this.getLevel()) ? 2 : 3)) {
            this.time = 0;
            ++this.sprite;
        }
        if (this.lightTime >= lightCycle) {
            this.lightTime = 0;
        }
        float lightVariation = (float)Math.sin(Math.toRadians((float)this.lightTime * 360.0f / (float)lightCycle));
        int lightColorVariation = 64 - (int)(64.0f * lightVariation);
        int lightLevelVariation = (int)(10.0f * lightVariation);
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, new Color(255 - lightColorVariation, 208, lightColorVariation), 1.0f, 120 + lightLevelVariation);
        ++this.lightTime;
    }

    public void serverTick() {
        super.serverTick();
        if (this.isScared(this.getLevel())) {
            if (!this.buffManager.hasBuff("movespeedburst")) {
                this.buffManager.addBuff(new ActiveBuff(BuffRegistry.MOVE_SPEED_BURST, (Mob)this, 3000, (Attacker)this), true);
            }
            if (WildPhosphorSlime.dayInSurface(this.getLevel())) {
                ++this.dayCount;
                if (this.dayCount > 400) {
                    this.getServer().network.sendToClientsAtEntireLevel((Packet)new PhosphorSlimeParticlesPacket(this.x, this.y), this.getLevel());
                    this.remove();
                }
            }
        }
    }

    public PathDoorOption getPathDoorOption() {
        return this.getLevel() != null ? this.getLevel().regionManager.CANNOT_PASS_DOORS_OPTIONS : null;
    }

    public int getFlyingHeight() {
        return 20;
    }

    public boolean canTakeDamage() {
        return false;
    }

    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        return true;
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51 - this.getFlyingHeight();
        Point sprite = new Point(this.sprite % 5, this.getDir());
        drawY += this.getBobbing(x, y);
        TextureDrawOptionsEnd drawOptions = (this.isScared(level) ? texture_scared : texture).initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY += this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount((Mob)this));
        list.add(new MobDrawable((DrawOptions)drawOptions){
            final /* synthetic */ DrawOptions val$drawOptions;
            {
                this.val$drawOptions = drawOptions;
            }

            public void draw(TickManager tickManager) {
                this.val$drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    public boolean isScared(Level level) {
        return WildPhosphorSlime.dayInSurface(level) || level.entityManager.streamAreaMobsAndPlayers(this.x, this.y, 500).anyMatch(m -> m.isHostile && m.getDistance((Mob)this) <= 500.0f);
    }

    public static boolean dayInSurface(Level level) {
        return level.getIslandDimension() == 0 && !level.getWorldEntity().isNight();
    }

    protected void addHoverTooltips(ListGameTooltips tooltips, boolean debug) {
        tooltips.add(Localization.translate((String)"mobtooltip", (String)"usenet"));
    }

    public boolean isHealthBarVisible() {
        return false;
    }

    public void playDeathSound() {
    }

    public void playHitSound() {
    }

    public void spawnDamageText(int damage, int size, boolean isCrit) {
    }

    static {
        lootTable = new LootTable(new LootItemInterface[]{new LootItem("cuberry")});
        lightCycle = 80;
    }

    public static class WildPhosphorSlimeAI
    extends SelectorAINode<WildPhosphorSlime> {
        public AphRunFromMobsAI<WildPhosphorSlime> aphRunFromMobsAI;

        public WildPhosphorSlimeAI() {
            this.addChild((AINode)new EscapeAINode<WildPhosphorSlime>(){

                public boolean shouldEscape(WildPhosphorSlime t, Blackboard<WildPhosphorSlime> blackboard) {
                    return WildPhosphorSlime.dayInSurface(t.getLevel());
                }
            });
            this.aphRunFromMobsAI = new AphRunFromMobsAI(500, m -> m.isHostile);
            this.addChild((AINode)this.aphRunFromMobsAI);
            this.addChild((AINode)new WandererAINode(10000));
        }
    }

    public static class PhosphorSlimeParticlesPacket
    extends Packet {
        public final float x;
        public final float y;

        public PhosphorSlimeParticlesPacket(byte[] data) {
            super(data);
            PacketReader reader = new PacketReader((Packet)this);
            this.x = reader.getNextFloat();
            this.y = reader.getNextFloat();
        }

        public PhosphorSlimeParticlesPacket(float x, float y) {
            this.x = x;
            this.y = y;
            PacketWriter writer = new PacketWriter((Packet)this);
            writer.putNextFloat(x);
            writer.putNextFloat(y);
        }

        public void processClient(NetworkPacket packet, Client client) {
            if (client.getLevel() != null) {
                PhosphorSlimeParticlesPacket.apply(client.getLevel(), this.x, this.y);
            }
        }

        public static void apply(Level level, float x, float y) {
            if (level != null && level.isClient()) {
                for (int i = 0; i < 2; ++i) {
                    level.entityManager.addParticle((Particle)new FleshParticle(level, texture, GameRandom.globalRandom.nextInt(3), 8, 32, x, y, 20.0f, 0.0f, 0.0f), Particle.GType.IMPORTANT_COSMETIC);
                }
            }
        }
    }
}

