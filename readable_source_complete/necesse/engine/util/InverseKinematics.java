/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;

public class InverseKinematics {
    public GameLinkedList<Limb> limbs = new GameLinkedList();

    public InverseKinematics(float startX, float startY, float startAngle, float startLength, float maxLeftAngle, float maxRightAngle) {
        this.limbs.add(new Limb(startX, startY, startAngle, startLength, maxLeftAngle, maxRightAngle));
    }

    public InverseKinematics(float startX, float startY, float startAngle, float startLength) {
        this(startX, startY, startAngle, startLength, 180.0f, 180.0f);
    }

    public static InverseKinematics startFromPoints(float startX, float startY, float endX, float endY, float maxLeftAngle, float maxRightAngle) {
        Point2D.Float dir = new Point2D.Float(endX - startX, endY - startY);
        float length = (float)dir.distance(0.0, 0.0);
        float angle = GameMath.getAngle(dir);
        return new InverseKinematics(startX, startY, angle, length, maxLeftAngle, maxRightAngle);
    }

    public static InverseKinematics startFromPoints(float startX, float startY, float endX, float endY) {
        return InverseKinematics.startFromPoints(startX, startY, endX, endY, 180.0f, 180.0f);
    }

    public void addJointAngle(float angle, float length, float maxLeftAngle, float maxRightAngle) {
        Limb last = this.limbs.getLast();
        this.limbs.add(new Limb(last.outboundX, last.outboundY, angle, length, maxLeftAngle, maxRightAngle));
    }

    public void addJointAngle(float angle, float length) {
        this.addJointAngle(angle, length, 180.0f, 180.0f);
    }

    public void addJointPoint(float nextX, float nextY, float maxLeftAngle, float maxRightAngle) {
        Limb last = this.limbs.getLast();
        Point2D.Float dir = new Point2D.Float(nextX - last.outboundX, nextY - last.outboundY);
        float length = (float)dir.distance(0.0, 0.0);
        float angle = GameMath.getAngle(dir);
        this.limbs.add(new Limb(last.outboundX, last.outboundY, angle, length, maxLeftAngle, maxRightAngle));
    }

    public void addJointPoint(float nextX, float nextY) {
        this.addJointPoint(nextX, nextY, 180.0f, 180.0f);
    }

    public int getTotalJoints() {
        return this.limbs.size();
    }

    public Limb removeLastLimb() {
        if (this.limbs.size() == 1) {
            throw new IllegalStateException("Cannot remove the last joint");
        }
        return this.limbs.removeLast();
    }

    public int apply(float targetX, float targetY, float maxDistChange, float targetMinDistance, int maxIterations) {
        double targetDist;
        double change;
        float invalidDist;
        if (this.limbs.size() == 1) {
            this.apply(targetX, targetY);
            return 1;
        }
        Limb first = this.limbs.getFirst();
        double distToTarget = new Point2D.Float(targetX, targetY).distance(first.inboundX, first.inboundY);
        float totalLength = 0.0f;
        float largestLength = 0.0f;
        for (Limb limb : this.limbs) {
            if (limb.length > largestLength) {
                largestLength = limb.length;
            }
            totalLength += limb.length;
        }
        if (largestLength >= totalLength - largestLength && distToTarget < (double)(invalidDist = largestLength - (totalLength - largestLength))) {
            targetMinDistance = (float)((double)targetMinDistance + ((double)invalidDist - distToTarget));
        }
        if (distToTarget > (double)totalLength) {
            targetMinDistance = (float)((double)targetMinDistance + (distToTarget - (double)totalLength));
        }
        double lastTargetDist = maxDistChange;
        int iterations = 0;
        do {
            ++iterations;
            this.apply(targetX, targetY);
            Limb last = this.limbs.getLast();
            targetDist = new Point2D.Float(last.outboundX, last.outboundY).distance(targetX, targetY);
            change = Math.abs(targetDist - lastTargetDist);
            lastTargetDist = targetDist;
        } while (!(change <= (double)maxDistChange) && iterations < maxIterations && !(targetDist <= (double)targetMinDistance));
        return iterations;
    }

    public void apply(float targetX, float targetY) {
        Limb first = this.limbs.getFirst();
        Point2D.Float rootPos = new Point2D.Float(first.inboundX, first.inboundY);
        Point2D.Float currentGoal = new Point2D.Float(targetX, targetY);
        this.finalToRoot(currentGoal);
        this.rootToFinal(rootPos);
    }

    protected Point2D.Float finalToRoot(Point2D.Float currentGoal) {
        GameLinkedList.Element lastElement = null;
        for (GameLinkedList.Element currentElement = this.limbs.getLastElement(); currentElement != null; currentElement = currentElement.prev()) {
            Limb currentLimb = (Limb)currentElement.object;
            currentLimb.angle = GameMath.getAngle(new Point2D.Float(currentGoal.x - currentLimb.inboundX, currentGoal.y - currentLimb.inboundY));
            if (lastElement != null && (currentLimb.maxLeftAngle < 180.0f || currentLimb.maxRightAngle < 180.0f)) {
                float lastAngle = ((Limb)lastElement.object).angle;
                float angleDiff = GameMath.getAngleDifference(lastAngle, currentLimb.angle);
                if (angleDiff < -currentLimb.maxLeftAngle) {
                    currentLimb.angle -= -angleDiff - currentLimb.maxLeftAngle;
                } else if (angleDiff > currentLimb.maxRightAngle) {
                    currentLimb.angle += angleDiff - currentLimb.maxRightAngle;
                }
            }
            currentLimb.outboundX = currentGoal.x;
            currentLimb.outboundY = currentGoal.y;
            currentLimb.fixInboundPos();
            currentGoal = new Point2D.Float(currentLimb.inboundX, currentLimb.inboundY);
            lastElement = currentElement;
        }
        return currentGoal;
    }

    protected void rootToFinal(Point2D.Float rootPos) {
        Point2D.Float currentInbound = new Point2D.Float(rootPos.x, rootPos.y);
        for (GameLinkedList.Element currentElement = this.limbs.getFirstElement(); currentElement != null; currentElement = currentElement.next()) {
            Limb currentLimb = (Limb)currentElement.object;
            currentLimb.inboundX = currentInbound.x;
            currentLimb.inboundY = currentInbound.y;
            currentLimb.fixOutboundPos();
            currentInbound = new Point2D.Float(currentLimb.outboundX, currentLimb.outboundY);
        }
    }

    public static void apply(GameLinkedList.Element limb, boolean useInbound, float targetX, float targetY, boolean root) {
        GameLinkedList.Element first = limb;
        GameLinkedList.Element last = limb;
        while (first.hasPrev()) {
            first = first.prev();
        }
        while (last.hasNext()) {
            last = last.next();
        }
        if (limb == first && useInbound) {
            Point2D.Float rootPos = new Point2D.Float(((Limb)last.object).outboundX, ((Limb)last.object).outboundY);
            InverseKinematics.finalToRoot(limb, true, -1, new Point2D.Float(targetX, targetY));
            if (root) {
                InverseKinematics.rootToFinal(last, null, true, -1, rootPos);
            }
        } else if (limb == last && !useInbound) {
            Point2D.Float rootPos = new Point2D.Float(((Limb)first.object).inboundX, ((Limb)first.object).inboundY);
            InverseKinematics.finalToRoot(limb, false, 1, new Point2D.Float(targetX, targetY));
            if (root) {
                InverseKinematics.rootToFinal(first, null, false, 1, rootPos);
            }
        } else {
            Point2D.Float lastRoot = new Point2D.Float(((Limb)last.object).outboundX, ((Limb)last.object).outboundY);
            Point2D.Float firstRoot = new Point2D.Float(((Limb)first.object).inboundX, ((Limb)first.object).inboundY);
            if (useInbound) {
                InverseKinematics.finalToRoot(limb.prev(), false, 1, new Point2D.Float(targetX, targetY));
                InverseKinematics.finalToRoot(limb, true, -1, new Point2D.Float(targetX, targetY));
                if (root) {
                    InverseKinematics.rootToFinal(first, null, false, 1, firstRoot);
                }
            } else {
                InverseKinematics.finalToRoot(limb, false, 1, new Point2D.Float(targetX, targetY));
                InverseKinematics.finalToRoot(limb.next(), true, -1, new Point2D.Float(targetX, targetY));
                if (root) {
                    InverseKinematics.rootToFinal(last, null, true, -1, lastRoot);
                }
            }
        }
    }

    protected static void finalToRoot(GameLinkedList.Element currentElement, boolean useInbound, int dir, Point2D.Float currentGoal) {
        while (currentElement != null) {
            Limb currentLimb = (Limb)currentElement.object;
            if (useInbound) {
                currentLimb.angle = GameMath.getAngle(new Point2D.Float(currentLimb.outboundX - currentGoal.x, currentLimb.outboundY - currentGoal.y));
                currentLimb.inboundX = currentGoal.x;
                currentLimb.inboundY = currentGoal.y;
                currentLimb.fixOutboundPos();
                currentGoal = new Point2D.Float(currentLimb.outboundX, currentLimb.outboundY);
            } else {
                currentLimb.angle = GameMath.getAngle(new Point2D.Float(currentGoal.x - currentLimb.inboundX, currentGoal.y - currentLimb.inboundY));
                currentLimb.outboundX = currentGoal.x;
                currentLimb.outboundY = currentGoal.y;
                currentLimb.fixInboundPos();
                currentGoal = new Point2D.Float(currentLimb.inboundX, currentLimb.inboundY);
            }
            if (dir < 0) {
                currentElement = currentElement.next();
                continue;
            }
            currentElement = currentElement.prev();
        }
    }

    protected static void rootToFinal(GameLinkedList.Element currentElement, GameLinkedList.Element endElement, boolean useInbound, int dir, Point2D.Float rootPos) {
        Point2D.Float currentPos = new Point2D.Float(rootPos.x, rootPos.y);
        while (currentElement != null) {
            if (currentElement == endElement) {
                return;
            }
            Limb currentLimb = (Limb)currentElement.object;
            if (useInbound) {
                currentLimb.outboundX = currentPos.x;
                currentLimb.outboundY = currentPos.y;
                currentLimb.fixInboundPos();
                currentPos = new Point2D.Float(currentLimb.inboundX, currentLimb.inboundY);
            } else {
                currentLimb.inboundX = currentPos.x;
                currentLimb.inboundY = currentPos.y;
                currentLimb.fixOutboundPos();
                currentPos = new Point2D.Float(currentLimb.outboundX, currentLimb.outboundY);
            }
            if (dir < 0) {
                currentElement = currentElement.prev();
                continue;
            }
            currentElement = currentElement.next();
        }
    }

    public void drawDebug(GameCamera camera, Color color, Color limitsColor) {
        this.drawDebug(camera, 0, 0, color, limitsColor);
    }

    public void drawDebug(GameCamera camera, int offsetX, int offsetY, Color color, Color limitsColor) {
        for (Limb limb : this.limbs) {
            Point2D.Float dir;
            Renderer.drawLine(offsetX + camera.getDrawX(limb.inboundX), offsetY + camera.getDrawY(limb.inboundY), offsetX + camera.getDrawX(limb.outboundX), offsetY + camera.getDrawY(limb.outboundY), color);
            float limitsLength = Math.min(50.0f, limb.length / 2.0f);
            if (limb.maxLeftAngle < 180.0f) {
                dir = GameMath.getAngleDir(limb.angle - limb.maxLeftAngle);
                Renderer.drawLine(offsetX + camera.getDrawX(limb.outboundX), offsetY + camera.getDrawY(limb.outboundY), offsetX + camera.getDrawX(limb.outboundX + dir.x * limitsLength), offsetY + camera.getDrawY(limb.outboundY + dir.y * limitsLength), limitsColor);
            }
            if (limb.maxRightAngle < 180.0f) {
                dir = GameMath.getAngleDir(limb.angle + limb.maxRightAngle);
                Renderer.drawLine(offsetX + camera.getDrawX(limb.outboundX), offsetY + camera.getDrawY(limb.outboundY), offsetX + camera.getDrawX(limb.outboundX + dir.x * limitsLength), offsetY + camera.getDrawY(limb.outboundY + dir.y * limitsLength), limitsColor);
            }
            Renderer.drawCircle(offsetX + camera.getDrawX(limb.inboundX), offsetY + camera.getDrawY(limb.inboundY), 4, 12, color, false);
            Renderer.drawCircle(offsetX + camera.getDrawX(limb.outboundX), offsetY + camera.getDrawY(limb.outboundY), 4, 12, color, false);
        }
    }

    public static class Limb {
        public float inboundX;
        public float inboundY;
        public float outboundX;
        public float outboundY;
        public float angle;
        public float length;
        public float maxLeftAngle;
        public float maxRightAngle;

        public Limb(float x, float y, float angle, float length, float maxLeftAngle, float maxRightAngle) {
            this.inboundX = x;
            this.inboundY = y;
            this.angle = angle;
            this.length = length;
            this.maxLeftAngle = maxLeftAngle;
            this.maxRightAngle = maxRightAngle;
            this.fixOutboundPos();
        }

        public void fixInboundPos() {
            Point2D.Float inboundDir = GameMath.getAngleDir(this.angle + 180.0f);
            this.inboundX = this.outboundX + inboundDir.x * this.length;
            this.inboundY = this.outboundY + inboundDir.y * this.length;
        }

        public void fixOutboundPos() {
            Point2D.Float outboundDir = GameMath.getAngleDir(this.angle);
            this.outboundX = this.inboundX + outboundDir.x * this.length;
            this.outboundY = this.inboundY + outboundDir.y * this.length;
        }
    }
}

