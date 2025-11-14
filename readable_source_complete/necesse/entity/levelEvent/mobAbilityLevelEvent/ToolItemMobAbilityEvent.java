/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.hudManager.HudDrawElement;

public class ToolItemMobAbilityEvent
extends MobAbilityLevelEvent {
    public int seed;
    public long endTime;
    public int hitCooldown;
    public InventoryItem item;
    public int aimX;
    public int aimY;
    private boolean mobHitsIsPlayer;
    private HashMap<Integer, Long> mobHits;
    private HudDrawElement debugDrawElement;
    public float lastHitboxProgress;
    public int totalHits;

    public ToolItemMobAbilityEvent() {
    }

    public ToolItemMobAbilityEvent(AttackAnimMob mob, int seed, InventoryItem toolItem, int aimX, int aimY, int duration, int hitCooldown, HashMap<Integer, Long> mobHits) {
        super(mob, new GameRandom(seed));
        this.seed = seed;
        if (!(toolItem.item instanceof ToolItem)) {
            throw new IllegalArgumentException("toolItem parameter must be a ToolItem instance");
        }
        this.item = toolItem;
        this.aimX = aimX;
        this.aimY = aimY;
        this.hitCooldown = hitCooldown;
        this.endTime = mob.getTime() + (long)duration;
        if (mobHits == null) {
            if (mob.isPlayer) {
                this.mobHitsIsPlayer = true;
                this.mobHits = ((PlayerMob)mob).toolHits;
            } else {
                this.mobHits = new HashMap();
            }
        } else {
            this.mobHits = mobHits;
        }
    }

    public ToolItemMobAbilityEvent(AttackAnimMob mob, int seed, InventoryItem toolItem, int aimX, int aimY, int duration, int hitCooldown) {
        this(mob, seed, toolItem, aimX, aimY, duration, hitCooldown, null);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.seed);
        InventoryItem.addPacketContent(this.item, writer);
        writer.putNextInt(this.hitCooldown);
        writer.putNextInt(this.aimX);
        writer.putNextInt(this.aimY);
        writer.putNextInt((int)(this.endTime - this.getTime()));
        writer.putNextBoolean(this.mobHitsIsPlayer);
        if (!this.mobHitsIsPlayer) {
            writer.putNextShortUnsigned(this.mobHits.size());
            for (Map.Entry<Integer, Long> entry : this.mobHits.entrySet()) {
                writer.putNextInt(entry.getKey());
                writer.putNextLong(entry.getValue());
            }
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.seed = reader.getNextInt();
        this.item = InventoryItem.fromContentPacket(reader);
        this.hitCooldown = reader.getNextInt();
        this.aimX = reader.getNextInt();
        this.aimY = reader.getNextInt();
        this.endTime = this.getTime() + (long)reader.getNextInt();
        this.mobHitsIsPlayer = reader.getNextBoolean();
        if (!this.mobHitsIsPlayer) {
            int size = reader.getNextShortUnsigned();
            this.mobHits = new HashMap(size);
            for (int i = 0; i < size; ++i) {
                int uniqueID = reader.getNextInt();
                long time = reader.getNextLong();
                this.mobHits.put(uniqueID, time);
            }
        }
    }

    @Override
    public void init() {
        super.init();
        if (this.item == null || !(this.item.item instanceof ToolItem)) {
            if (this.item != null) {
                GameLog.warn.println("Started invalid ToolItemMobAbilityEvent with non ToolItem: " + this.item);
            }
            this.over();
            return;
        }
        if (!(this.owner instanceof AttackAnimMob)) {
            if (this.owner != null) {
                GameLog.warn.println("Started invalid ToolItemMobAbilityEvent with non AttackAnimMob owner: " + this.owner);
            }
            this.over();
            return;
        }
        if (this.mobHitsIsPlayer && this.owner.isPlayer) {
            this.mobHits = ((PlayerMob)this.owner).toolHits;
        } else if (this.mobHits == null) {
            GameLog.warn.println("Started invalid ToolItemMobAbilityEvent with player mobHits, but the owner was not a player: " + this.owner);
            this.over();
            return;
        }
        this.hitsObjects = this.owner.isPlayer;
        if (GlobalData.debugActive()) {
            this.debugDrawElement = new HudDrawElement(){

                @Override
                public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                    final DrawOptionsList drawOptions = new DrawOptionsList();
                    ArrayList<Shape> hitBoxes = ((ToolItem)ToolItemMobAbilityEvent.this.item.item).getHitboxes(ToolItemMobAbilityEvent.this.item, (AttackAnimMob)ToolItemMobAbilityEvent.this.owner, ToolItemMobAbilityEvent.this.aimX, ToolItemMobAbilityEvent.this.aimY, ToolItemMobAbilityEvent.this, true);
                    for (Shape hitBox : hitBoxes) {
                        drawOptions.add(() -> Renderer.drawShape(hitBox, camera, true, 1.0f, 0.0f, 0.0f, 0.5f));
                    }
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return Integer.MAX_VALUE;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            drawOptions.draw();
                        }
                    });
                }
            };
            this.level.hudManager.addElement(this.debugDrawElement);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.globalTick();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.globalTick();
    }

    protected void globalTick() {
        if (this.isOver()) {
            return;
        }
        if (this.endTime <= this.getTime()) {
            this.over();
            return;
        }
        ToolItem toolItem = (ToolItem)this.item.item;
        ArrayList<Shape> hitBoxes = toolItem.getHitboxes(this.item, (AttackAnimMob)this.owner, this.aimX, this.aimY, this, false);
        this.handleHits(hitBoxes, (Mob target) -> this.canHit((Mob)target, 0), null);
    }

    @Override
    protected boolean anyHitboxIntersects(Iterable<Shape> hitBoxes, Mob target) {
        boolean hitDetected = super.anyHitboxIntersects(hitBoxes, target);
        if (hitDetected) {
            if (target.canHitThroughCollision()) {
                return true;
            }
            CollisionFilter collisionFilter = this.owner.modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), target);
            return !this.level.collides(new Line2D.Float(this.owner.x, this.owner.y, target.x, target.y), collisionFilter);
        }
        return false;
    }

    public boolean canHit(Mob target, int tolerance) {
        if (!((ToolItem)this.item.item).canHitMob(target, this)) {
            return false;
        }
        if (!this.mobHits.containsKey(target.getHitCooldownUniqueID())) {
            return true;
        }
        return this.mobHits.get(target.getHitCooldownUniqueID()) + (long)this.hitCooldown - (long)tolerance < this.getTime();
    }

    protected void startCooldown(Mob target) {
        this.mobHits.put(target.getHitCooldownUniqueID(), target.getTime());
        target.startHitCooldown();
    }

    @Override
    public void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        this.startCooldown(target);
        ++this.totalHits;
    }

    @Override
    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        super.serverHit(target, content, clientSubmitted);
        this.startCooldown(target);
        ((ToolItem)this.item.item).hitMob(this.item, this, this.level, target, this.owner);
        ++this.totalHits;
    }

    @Override
    public boolean canHitObjectFilter(LevelObject levelObject) {
        return ((ToolItem)this.item.item).canHitObject(levelObject);
    }

    @Override
    public boolean canHit(LevelObjectHit hit) {
        boolean superCanHit = super.canHit(hit);
        if (superCanHit) {
            CollisionFilter collisionFilter = this.owner.modifyChasingCollisionFilter(new CollisionFilter().projectileCollision().addFilter(tp -> !tp.object().object.attackThrough), null);
            return !this.level.collides(new Line2D.Float(this.owner.x, this.owner.y, hit.tileX * 32 + 16, hit.tileY * 32 + 16), collisionFilter);
        }
        return false;
    }

    @Override
    public void hit(LevelObjectHit hit) {
        super.hit(hit);
        ((ToolItem)this.item.item).hitObject(this.item, hit.getLevelObject(), this.owner);
    }

    @Override
    public void over() {
        super.over();
        if (this.debugDrawElement != null) {
            this.debugDrawElement.remove();
        }
    }

    @Override
    public void onDispose() {
        super.onDispose();
        if (this.debugDrawElement != null) {
            this.debugDrawElement.remove();
        }
    }
}

