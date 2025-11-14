/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketMobAbilityLevelEventHit;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.PointSetAbstract;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelObjectHit;

public class MobAbilityLevelEvent
extends LevelEvent
implements Attacker {
    public boolean allowNullOwner = false;
    public Mob owner;
    public int ownerID;
    public boolean clientHandlesHit;
    public NetworkClient handlingClient;
    public boolean hitsObjects;
    private HashMap<Point, Long> objectHits;

    public MobAbilityLevelEvent() {
    }

    public MobAbilityLevelEvent(Mob owner, GameRandom uniqueIDRandom) {
        this.setupOwnerAndUniqueID(owner, uniqueIDRandom);
    }

    protected void setupOwnerAndUniqueID(Mob owner, GameRandom uniqueIDRandom) {
        this.owner = owner;
        if (owner != null) {
            this.level = owner.getLevel();
            this.ownerID = owner.getUniqueID();
        } else {
            this.ownerID = -1;
        }
        this.resetUniqueID(uniqueIDRandom);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.ownerID = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.ownerID);
    }

    @Override
    public void init() {
        super.init();
        this.objectHits = new HashMap();
        this.owner = GameUtils.getLevelMob(this.ownerID, this.level);
        if (this.owner == null) {
            if (!this.allowNullOwner) {
                GameLog.warn.println("Could not find owner for level event " + this.getClass().getSimpleName() + " server level: " + this.isServer());
                this.over();
            }
        } else {
            this.hitsObjects = this.owner.isPlayer;
            if (this.isServer()) {
                if (!Settings.strictServerAuthority) {
                    if (this.owner.isPlayer) {
                        this.handlingClient = ((PlayerMob)this.owner).getServerClient();
                    }
                    this.clientHandlesHit = true;
                }
            } else if (this.isClient()) {
                ClientClient client = this.getClient().getClient();
                if (!this.getClient().hasStrictServerAuthority() && client != null) {
                    if (this.owner == client.playerMob) {
                        this.handlingClient = client;
                    }
                    this.clientHandlesHit = true;
                }
            }
        }
    }

    protected Stream<Mob> streamTargets(Shape hitbox) {
        Stream<Mob> targets = this.allowNullOwner && this.owner == null ? Stream.concat(this.level.entityManager.mobs.streamInRegionsShape(hitbox, 1), this.level.entityManager.players.streamInRegionsShape(hitbox, 1)) : GameUtils.streamTargets(this.owner, this.level, hitbox);
        return targets;
    }

    protected Rectangle getHitboxesBounds(Iterable<Shape> hitboxes) {
        Rectangle bounds = null;
        for (Shape hitbox : hitboxes) {
            Rectangle currentBounds = hitbox.getBounds();
            if (bounds == null) {
                bounds = currentBounds;
                continue;
            }
            bounds = bounds.union(currentBounds);
        }
        return bounds == null ? new Rectangle() : bounds;
    }

    protected boolean anyHitboxIntersects(Iterable<Shape> hitBoxes, Mob target) {
        Rectangle targetHitbox = target.getHitBox();
        for (Shape hitBox : hitBoxes) {
            if (!hitBox.intersects(targetHitbox)) continue;
            return true;
        }
        return false;
    }

    protected void handleHits(Iterable<Shape> hitboxes, Predicate<Mob> canHit, Function<Mob, Packet> attackContentSupplier) {
        block7: {
            block5: {
                ClientClient client;
                block6: {
                    if (!this.isClient()) break block5;
                    if (this.handlingClient == null) break block6;
                    this.streamTargets(this.getHitboxesBounds(hitboxes)).filter(m -> !m.isPlayer || m == this.owner).filter(canHit).filter(m -> this.anyHitboxIntersects(hitboxes, (Mob)m)).forEach(m -> this.clientHit((Mob)m, attackContentSupplier == null ? null : (Packet)attackContentSupplier.apply((Mob)m)));
                    break block7;
                }
                if (!this.clientHandlesHit || (client = this.level.getClient().getClient()) == null || !client.hasSpawned() || client.isDead() || !this.canHitLocalClient(client) || !canHit.test(client.playerMob) || !this.anyHitboxIntersects(hitboxes, client.playerMob)) break block7;
                this.clientHit(client.playerMob, attackContentSupplier == null ? null : attackContentSupplier.apply(client.playerMob));
                break block7;
            }
            if (this.isServer()) {
                if (this.handlingClient == null) {
                    this.streamTargets(this.getHitboxesBounds(hitboxes)).filter(m -> !this.clientHandlesHit || !m.isPlayer).filter(canHit).filter(m -> this.anyHitboxIntersects(hitboxes, (Mob)m)).forEach(m -> this.serverHit((Mob)m, attackContentSupplier == null ? null : (Packet)attackContentSupplier.apply((Mob)m), false));
                }
                if (this.hitsObjects) {
                    for (Shape hitbox : hitboxes) {
                        ArrayList<LevelObjectHit> hits = this.level.getCollisions(hitbox, new CollisionFilter().attackThroughCollision(tp -> this.canHitObjectFilter(tp.object())));
                        for (LevelObjectHit hit : hits) {
                            if (hit.invalidPos() || !this.canHit(hit)) continue;
                            this.hit(hit);
                        }
                    }
                }
            }
        }
    }

    protected void handleHits(Shape hitbox, Predicate<Mob> canHit, Function<Mob, Packet> attackContentSupplier) {
        this.handleHits(Collections.singleton(hitbox), canHit, attackContentSupplier);
    }

    protected boolean canHitLocalClient(ClientClient me) {
        if (this.allowNullOwner && this.owner == null) {
            return me.playerMob.canTakeDamage() && me.playerMob.buffManager.getModifier(BuffModifiers.UNTARGETABLE) == false;
        }
        NetworkClient attackerClient = GameUtils.getAttackerClient(this.owner);
        return (attackerClient == null || me.pvpEnabled() && attackerClient.pvpEnabled()) && me.playerMob.canBeTargeted(this.owner, attackerClient);
    }

    public boolean canHitObjectFilter(LevelObject levelObject) {
        return levelObject.object.attackThrough;
    }

    public boolean canHit(LevelObjectHit hit) {
        if (!this.objectHits.containsKey(hit.getPoint())) {
            return true;
        }
        return this.objectHits.get(hit.getPoint()) + (long)this.getHitCooldown(hit) < this.level.getTime();
    }

    public int getHitCooldown(LevelObjectHit hit) {
        return 500;
    }

    public void hit(LevelObjectHit hit) {
        this.objectHits.put(hit.getPoint(), this.level.getTime());
    }

    public void clientHit(Mob target, Packet content) {
        this.level.getClient().network.sendPacket(new PacketMobAbilityLevelEventHit(this, target, content));
    }

    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
    }

    @Override
    public GameMessage getAttackerName() {
        if (this.owner != null) {
            return this.owner.getAttackerName();
        }
        return new StaticMessage("MOB_ABILITY_EVENT{" + this.getStringID() + "}");
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        if (this.owner != null) {
            return this.owner.getDeathMessages();
        }
        return null;
    }

    @Override
    public Mob getFirstAttackOwner() {
        return this.owner;
    }

    @Override
    public PointSetAbstract<?> getRegionPositions() {
        if (this.owner != null && this.owner.getLevel() != null) {
            return this.owner.getRegionPositions();
        }
        return super.getRegionPositions();
    }

    @Override
    public Point getSaveToRegionPos() {
        if (this.owner != null) {
            return new Point(this.level.regionManager.getRegionCoordByTile(this.owner.getTileX()), this.level.regionManager.getRegionCoordByTile(this.owner.getTileY()));
        }
        return super.getSaveToRegionPos();
    }
}

