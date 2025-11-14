/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.eventStages;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.AngleSplitNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.MoveNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmCompletedCounter;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitCounterNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.WaitNightSwarmBatStage;
import necesse.entity.levelEvent.nightSwarmEvent.eventStages.NightSwarmEventStage;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class JailNightSwarmEventStage
extends NightSwarmEventStage {
    @Override
    public void onStarted(NightSwarmLevelEvent event) {
        float midX = event.currentTarget.x;
        float midY = event.currentTarget.y;
        int angleOffset = GameRandom.globalRandom.nextInt(360);
        int edges = 4;
        int minGap = 150;
        int startDistance = 500;
        int edgeLength = 700;
        float anglePerEdge = 360.0f / (float)edges;
        ArrayList<BatLine> batLines = new ArrayList<BatLine>();
        ArrayList<JailLine> jailLines = new ArrayList<JailLine>();
        for (int i = 0; i < edges; ++i) {
            float angle = GameMath.fixAngle((float)i * anglePerEdge + (float)angleOffset);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float lineMidX = midX + dir.x * (float)startDistance;
            float lineMidY = midY + dir.y * (float)startDistance;
            BatLine batLine = new BatLine(lineMidX, lineMidY, -dir.x, -dir.y, edgeLength);
            batLines.add(batLine);
            batLine.addJailLines(jailLines, minGap, startDistance * 2);
        }
        jailLines.sort(Comparator.comparingDouble(l -> GameMath.diagonalMoveDistance(l.startX - midX, l.startY - midY)));
        ArrayList<NightSwarmBatMob> bats = new ArrayList<NightSwarmBatMob>(event.bats.size());
        for (NightSwarmBatMob bat : event.getBats(false)) {
            bats.add(bat);
        }
        ArrayList linesCopy = new ArrayList(jailLines);
        NightSwarmCompletedCounter inPositionCounter = new NightSwarmCompletedCounter();
        while (!linesCopy.isEmpty() && !bats.isEmpty()) {
            JailLine line = (JailLine)linesCopy.remove(0);
            float minDistance = 0.0f;
            int batIndex = -1;
            for (int i = 0; i < bats.size(); ++i) {
                NightSwarmBatMob bat = (NightSwarmBatMob)bats.get(i);
                float distance = (float)GameMath.diagonalMoveDistance(bat.x - line.startX, bat.y - line.startY);
                if (batIndex != -1 && !(distance < minDistance)) continue;
                batIndex = i;
                minDistance = distance;
            }
            if (batIndex == -1) continue;
            NightSwarmBatMob bat = (NightSwarmBatMob)bats.remove(batIndex);
            bat.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(line.startX, line.startY)).addCompletedCounter(inPositionCounter));
            bat.stages.add(new WaitCounterNightSwarmBatStage(false, inPositionCounter));
            bat.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(line.endX, line.endY)));
            bat.stages.add(new AngleSplitNightSwarmBatStage(midX, midY, 4, angleOffset, startDistance));
        }
        for (NightSwarmBatMob bat : bats) {
            BatLine batLine = batLines.stream().min(Comparator.comparingDouble(l -> bat.getDistance(l.midX, l.midY))).orElse(null);
            if (batLine != null && !batLine.jailLines.isEmpty()) {
                JailLine line = GameRandom.globalRandom.getOneOf(batLine.jailLines);
                bat.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(line.startX, line.startY)).addCompletedCounter(inPositionCounter));
                bat.stages.add(new WaitCounterNightSwarmBatStage(false, inPositionCounter));
                bat.stages.add(new WaitNightSwarmBatStage(false, GameRandom.globalRandom.getIntBetween(100, 400)));
                bat.stages.add(new MoveNightSwarmBatStage(new MobMovementLevelPos(line.endX, line.endY)));
                bat.stages.add(new AngleSplitNightSwarmBatStage(midX, midY, 4, angleOffset, startDistance));
                continue;
            }
            bat.stages.add(new AngleSplitNightSwarmBatStage(midX, midY, 4, angleOffset, startDistance));
        }
    }

    @Override
    public void serverTick(NightSwarmLevelEvent event) {
    }

    @Override
    public boolean hasCompleted(NightSwarmLevelEvent event) {
        return true;
    }

    @Override
    public void onCompleted(NightSwarmLevelEvent event) {
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

