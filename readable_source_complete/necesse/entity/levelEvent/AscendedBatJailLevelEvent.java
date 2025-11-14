/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.ListIterator;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.AscendedBatMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class AscendedBatJailLevelEvent
extends LevelEvent {
    protected Mob owner;
    protected float targetX;
    protected float targetY;
    protected int edges;
    protected int minGap;
    protected int startDistance;
    protected int edgeLength;
    protected int msToWaitBeforeMoving;
    protected ArrayList<AscendedBatMob> batMobs = new ArrayList();
    protected int waitMoveTimer;

    public AscendedBatJailLevelEvent() {
        this.shouldSave = false;
    }

    public AscendedBatJailLevelEvent(Mob owner, float targetX, float targetY, int edges, int minGap, int startDistance, int edgeLength, int msToWaitBeforeMoving) {
        this();
        this.owner = owner;
        this.targetX = targetX;
        this.targetY = targetY;
        this.edges = edges;
        this.minGap = minGap;
        this.startDistance = startDistance;
        this.edgeLength = edgeLength;
        this.msToWaitBeforeMoving = msToWaitBeforeMoving;
    }

    public AscendedBatJailLevelEvent(Mob owner, float targetX, float targetY) {
        this(owner, targetX, targetY, 4, 175, 900, 900, 3000);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            this.over();
            return;
        }
        float midX = this.targetX;
        float midY = this.targetY;
        int angleOffset = GameRandom.globalRandom.nextInt(360);
        float anglePerEdge = 360.0f / (float)this.edges;
        ArrayList<JailLine> jailLines = new ArrayList<JailLine>();
        for (int i = 0; i < this.edges; ++i) {
            float angle = GameMath.fixAngle((float)i * anglePerEdge + (float)angleOffset);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float lineMidX = midX + dir.x * (float)this.startDistance;
            float lineMidY = midY + dir.y * (float)this.startDistance;
            BatLine batLine = new BatLine(lineMidX, lineMidY, -dir.x, -dir.y, this.edgeLength);
            batLine.addJailLines(jailLines, this.minGap, this.startDistance * 2);
        }
        for (JailLine line : jailLines) {
            AscendedBatMob batMob = new AscendedBatMob();
            if (this.owner != null) {
                batMob.master.uniqueID = this.owner.getUniqueID();
            }
            batMob.setLevel(this.getLevel());
            batMob.onSpawned((int)line.startX, (int)line.startY);
            batMob.targetX = line.endX;
            batMob.targetY = line.endY;
            Point2D.Float dir = GameMath.normalize(batMob.targetX - batMob.x, batMob.targetY - batMob.y);
            batMob.setFacingDir(dir.x, dir.y);
            this.level.entityManager.mobs.add(batMob);
            this.batMobs.add(batMob);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.waitMoveTimer != Integer.MIN_VALUE) {
            this.waitMoveTimer += 50;
            if (this.waitMoveTimer >= this.msToWaitBeforeMoving) {
                for (AscendedBatMob batMob : this.batMobs) {
                    batMob.setMovement(new MobMovementLevelPos(batMob.targetX, batMob.targetY));
                }
                this.waitMoveTimer = Integer.MIN_VALUE;
            }
            for (AscendedBatMob batMob : this.batMobs) {
                batMob.keepAliveBuffer = 0;
            }
        } else {
            ListIterator<AscendedBatMob> li = this.batMobs.listIterator();
            while (li.hasNext()) {
                AscendedBatMob next = li.next();
                if (next.hasArrivedAtTarget()) {
                    li.remove();
                    next.remove();
                    continue;
                }
                next.keepAliveBuffer = 0;
            }
            if (this.batMobs.isEmpty()) {
                this.over();
            }
        }
    }

    @Override
    public void over() {
        super.over();
        this.batMobs.forEach(Mob::remove);
    }

    private static class BatLine {
        public float midX;
        public float midY;
        public float x1;
        public float y1;
        public float x2;
        public float y2;
        public float length;
        public float lineDirX;
        public float lineDirY;
        public float facingX;
        public float facingY;
        public ArrayList<JailLine> jailLines = new ArrayList();

        public BatLine(float midX, float midY, float facingDirX, float facingDirY, int stretch) {
            this.midX = midX;
            this.midY = midY;
            this.facingX = facingDirX;
            this.facingY = facingDirY;
            Point2D.Float perp = GameMath.getPerpendicularDir(facingDirX, facingDirY);
            this.lineDirX = perp.x;
            this.lineDirY = perp.y;
            this.x1 = midX + this.lineDirX * (float)stretch;
            this.y1 = midY + this.lineDirY * (float)stretch;
            this.x2 = midX - this.lineDirX * (float)stretch;
            this.y2 = midY - this.lineDirY * (float)stretch;
            this.length = stretch * 2;
        }

        public void addJailLines(ArrayList<JailLine> lines, int gap, int lineLength) {
            int points = Math.max((int)(this.length / (float)gap), 1);
            int lengthWithPoints = (points - 1) * gap;
            float offset = (this.length - (float)lengthWithPoints) / 2.0f;
            this.jailLines = new ArrayList(points);
            for (int i = 0; i < points; ++i) {
                float dist = offset + (float)(i * gap);
                float startX = this.x1 - this.lineDirX * dist;
                float startY = this.y1 - this.lineDirY * dist;
                float endX = startX + this.facingX * (float)lineLength;
                float endY = startY + this.facingY * (float)lineLength;
                this.jailLines.add(new JailLine(startX, startY, endX, endY));
            }
            lines.addAll(this.jailLines);
        }
    }

    private static class JailLine {
        public float startX;
        public float startY;
        public float endX;
        public float endY;

        public JailLine(float startX, float startY, float endX, float endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }
}

