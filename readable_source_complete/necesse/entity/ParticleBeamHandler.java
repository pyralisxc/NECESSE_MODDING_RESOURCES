/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.function.Supplier;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class ParticleBeamHandler {
    public ParticleTypeSwitcher pTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC);
    public int startThickness = 10;
    public int endThickness = 0;
    public int particleSizeMin = 16;
    public int particleSizeMax = 24;
    public float particleThicknessMod = 0.5f;
    public Supplier<Color> trailColor = () -> Color.WHITE;
    public Supplier<Color> particleColor = () -> Color.WHITE;
    public float particleSpeed = 50.0f;
    public float distPerParticle = 14.0f;
    public int endParticleSizeMin = 10;
    public int endParticleSizeMax = 18;
    public float height = 18.0f;
    private final Level level;
    private final Trail trail;
    private final GameLinkedList<GameLinkedList<BeamParticle>> particles;

    public ParticleBeamHandler(Level level) {
        this.level = level;
        this.trail = new Trail(new TrailVector(0.0f, 0.0f, 1.0f, 0.0f, this.startThickness, this.height), level, Color.WHITE, 10000){

            @Override
            public Color getColor() {
                return ParticleBeamHandler.this.trailColor.get();
            }
        };
        level.entityManager.addTrail(this.trail);
        this.particles = new GameLinkedList();
        this.particles.addLast(new GameLinkedList());
    }

    public ParticleBeamHandler sprite(GameSprite sprite) {
        this.trail.sprite = sprite;
        return this;
    }

    public ParticleBeamHandler particleColor(Supplier<Color> color) {
        this.particleColor = color;
        return this;
    }

    public ParticleBeamHandler particleColor(Color color) {
        return this.particleColor(() -> color);
    }

    public ParticleBeamHandler particleSize(int min, int max) {
        this.particleSizeMin = min;
        this.particleSizeMax = max;
        return this;
    }

    public ParticleBeamHandler trailColor(Supplier<Color> color) {
        this.trailColor = color;
        return this;
    }

    public ParticleBeamHandler trailColor(Color color) {
        return this.trailColor(() -> color);
    }

    public ParticleBeamHandler color(Supplier<Color> color) {
        this.particleColor(color);
        this.trailColor(color);
        return this;
    }

    public ParticleBeamHandler color(Color color) {
        this.particleColor(color);
        this.trailColor(color);
        return this;
    }

    public ParticleBeamHandler thickness(int startThickness, int endThickness) {
        this.startThickness = startThickness;
        this.endThickness = endThickness;
        return this;
    }

    public ParticleBeamHandler particleThicknessMod(float modifier) {
        this.particleThicknessMod = modifier;
        return this;
    }

    public ParticleBeamHandler speed(float speed) {
        this.particleSpeed = speed;
        return this;
    }

    public ParticleBeamHandler distPerParticle(float dist) {
        this.distPerParticle = dist;
        return this;
    }

    public ParticleBeamHandler endParticleSize(int min, int max) {
        this.endParticleSizeMin = min;
        this.endParticleSizeMax = max;
        return this;
    }

    public ParticleBeamHandler height(float height) {
        this.height = height;
        return this;
    }

    public ParticleBeamHandler drawOnTop() {
        this.trail.drawOnTop = true;
        return this;
    }

    public ParticleBeamHandler drawOnTop(int drawOrder) {
        this.trail.drawOnTop = true;
        this.trail.drawOnTopOrder = drawOrder;
        return this;
    }

    public void update(RayLinkedList<LevelObjectHit> rays, float delta) {
        GameLinkedList.Element current = rays.getFirstElement();
        GameLinkedList.Element currentParticles = this.particles.getFirstElement();
        double totalDist = rays.totalDist;
        double currentDist = 0.0;
        boolean isFirst = true;
        while (true) {
            int thickness1 = this.endThickness + (int)((double)(this.startThickness - this.endThickness) * Math.abs(currentDist / totalDist - 1.0));
            Ray currentRay = (Ray)current.object;
            TrailVector vector1 = new TrailVector((float)currentRay.getX1(), (float)currentRay.getY1(), (float)(currentRay.getX2() - currentRay.getX1()), (float)(currentRay.getY2() - currentRay.getY1()), thickness1, this.height);
            if (isFirst) {
                this.trail.reset(vector1);
                isFirst = false;
            } else {
                this.trail.addPoint(vector1, true, 0);
            }
            int thickness2 = this.endThickness + (int)((double)(this.startThickness - this.endThickness) * Math.abs((currentDist += currentRay.dist) / totalDist - 1.0));
            TrailVector vector2 = new TrailVector((float)currentRay.getX2(), (float)currentRay.getY2(), (float)(currentRay.getX2() - currentRay.getX1()), (float)(currentRay.getY2() - currentRay.getY1()), thickness2, this.height);
            this.trail.addPoint(vector2, true, 0);
            this.refreshParticles(current, currentParticles, delta);
            if (this.level.tickManager().isGameTick()) {
                this.spawnRayParticles(current, (GameLinkedList)currentParticles.object, thickness1, thickness2);
            }
            if (((Ray)current.object).targetHit != null && this.level.tickManager().isGameTick()) {
                this.spawnCollisionEndParticles(currentRay);
            }
            if (!current.hasNext()) {
                if (!this.level.tickManager().isGameTick()) break;
                this.spawnEndParticles(currentRay);
                break;
            }
            current = current.next();
            if (!currentParticles.hasNext()) {
                currentParticles = currentParticles.insertAfter(new GameLinkedList());
                continue;
            }
            currentParticles = currentParticles.next();
        }
        while (currentParticles.hasNext()) {
            GameLinkedList.Element nextParticles = currentParticles.next();
            for (BeamParticle p : (GameLinkedList)nextParticles.object) {
                p.option.remove();
            }
            currentParticles.remove();
            currentParticles = nextParticles;
        }
    }

    protected void spawnCollisionEndParticles(Ray<LevelObjectHit> ray) {
        if (this.particleColor != null && this.endParticleSizeMax > 0) {
            for (int i = 0; i < 3; ++i) {
                Color color = this.particleColor.get();
                this.level.lightManager.refreshParticleLightFloat((float)ray.getX2(), (float)ray.getY2(), color, 1.0f);
                ParticleOption option = this.trail.drawOnTop ? this.level.entityManager.addTopParticle((float)ray.getX2() + GameRandom.globalRandom.floatGaussian() * 4.0f, (float)ray.getY2() + GameRandom.globalRandom.floatGaussian() * 4.0f, this.pTypeSwitcher.next(), this.trail.drawOnTopOrder + 1) : this.level.entityManager.addParticle((float)ray.getX2() + GameRandom.globalRandom.floatGaussian() * 4.0f, (float)ray.getY2() + GameRandom.globalRandom.floatGaussian() * 4.0f, this.pTypeSwitcher.next());
                option.movesConstant(GameRandom.globalRandom.floatGaussian() * 8.0f, GameRandom.globalRandom.floatGaussian() * 8.0f).sizeFades(this.endParticleSizeMin, this.endParticleSizeMax).lifeTime(750).color(color).height(this.height);
            }
        }
    }

    protected void spawnEndParticles(Ray<LevelObjectHit> ray) {
        if (this.particleColor != null && this.endParticleSizeMax > 0) {
            for (int i = 0; i < 2; ++i) {
                ParticleOption option = this.trail.drawOnTop ? this.level.entityManager.addTopParticle((float)ray.getX2() + GameRandom.globalRandom.floatGaussian() * 4.0f, (float)ray.getY2() + GameRandom.globalRandom.floatGaussian() * 4.0f, this.pTypeSwitcher.next(), this.trail.drawOnTopOrder + 1) : this.level.entityManager.addParticle((float)ray.getX2() + GameRandom.globalRandom.floatGaussian() * 4.0f, (float)ray.getY2() + GameRandom.globalRandom.floatGaussian() * 4.0f, this.pTypeSwitcher.next());
                option.movesConstant(GameRandom.globalRandom.floatGaussian() * 15.0f, GameRandom.globalRandom.floatGaussian() * 15.0f).sizeFades(this.endParticleSizeMin, this.endParticleSizeMax).lifeTime(500).color(this.particleColor.get()).height(this.height);
            }
        }
    }

    protected void refreshParticles(GameLinkedList.Element rayElement, GameLinkedList.Element pElements, float delta) {
        for (BeamParticle particle : (GameLinkedList)pElements.object) {
            particle.increaseDist(rayElement, delta);
        }
        Iterator<GameLinkedList.Element> iterator = ((GameLinkedList)pElements.object).elementIterator();
        while (iterator.hasNext()) {
            GameLinkedList.Element element = iterator.next();
            BeamParticle particle = (BeamParticle)element.object;
            if (element.isRemoved() || particle.option.isRemoved() || Math.abs(particle.dist) > ((Ray)rayElement.object).dist) {
                particle.option.remove();
                element.remove();
                continue;
            }
            particle.updatePos(element, rayElement, pElements);
        }
    }

    protected void spawnRayParticles(GameLinkedList.Element rayElement, GameLinkedList<BeamParticle> particles, int thickness1, int thickness2) {
        if (this.particleColor != null) {
            Ray ray = (Ray)rayElement.object;
            double dist = ray.getP1().distance(ray.getP2());
            for (double i = 0.0; i < dist - (double)this.distPerParticle; i += (double)this.distPerParticle) {
                double dx = ray.getX2() - ray.getX1();
                double dy = ray.getY2() - ray.getY1();
                double mod = GameRandom.globalRandom.getDoubleBetween(i / dist, (i + (double)this.distPerParticle) / dist);
                double thickness = (double)(thickness1 - thickness2) * Math.abs(mod - 1.0);
                float x = (float)(ray.getX1() + dx * mod);
                float y = (float)(ray.getY1() + dy * mod);
                Color color = this.particleColor.get();
                this.level.lightManager.refreshParticleLightFloat(x, y, color, 1.0f);
                Point2D.Double dir = GameMath.normalize(dx, dy);
                Point2D.Double perp = GameMath.getPerpendicularDir(dir.x, dir.y);
                double perpRandom = (double)GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f) * thickness / 2.0 * (double)this.particleThicknessMod;
                float xRandom = (float)(perp.x * perpRandom);
                float yRandom = (float)(perp.y * perpRandom);
                ParticleOption option = this.trail.drawOnTop ? this.level.entityManager.addTopParticle(x + xRandom, y + yRandom, this.pTypeSwitcher.next(), this.trail.drawOnTopOrder + 1) : this.level.entityManager.addParticle(x + xRandom, y + yRandom, this.pTypeSwitcher.next());
                option.movesConstant(GameRandom.globalRandom.floatGaussian() * 5.0f, GameRandom.globalRandom.floatGaussian() * 5.0f).sizeFades(this.particleSizeMin, this.particleSizeMax).lifeTime(250).color(color).height(this.height);
                float distSpeed = GameRandom.globalRandom.getOneOf(Float.valueOf(1.0f), Float.valueOf(-1.0f)).floatValue() * this.particleSpeed;
                particles.addLast(new BeamParticle(mod, distSpeed, option, xRandom, yRandom));
            }
        }
    }

    public void dispose() {
        this.trail.remove();
    }

    private static class BeamParticle {
        public double dist;
        public float speed;
        public float lastX;
        public float lastY;
        public float movedX;
        public float movedY;
        public ParticleOption option;

        public BeamParticle(double dist, float distSpeed, ParticleOption option, float xRandom, float yRandom) {
            this.dist = dist;
            this.speed = distSpeed;
            this.option = option;
            this.movedX = xRandom;
            this.movedY = yRandom;
            Point2D.Float pos = option.getPos();
            this.lastX = pos.x;
            this.lastY = pos.y;
        }

        public void increaseDist(GameLinkedList.Element rayElement, float delta) {
            this.dist += (double)(this.speed * delta / 250.0f) / ((Ray)rayElement.object).dist;
        }

        private void updatePos(GameLinkedList.Element thisElement, GameLinkedList.Element rayElement, GameLinkedList.Element pElements) {
            Ray ray = (Ray)rayElement.object;
            GameLinkedList.Element nextRay = null;
            GameLinkedList.Element nextParticles = null;
            int sig = 0;
            if (this.dist < 0.0) {
                sig = -1;
                nextRay = rayElement.prevWrap();
                nextParticles = pElements.prevWrap();
            } else if (this.dist > 1.0) {
                sig = 1;
                nextRay = rayElement.nextWrap();
                nextParticles = pElements.nextWrap();
            }
            if (sig != 0) {
                if (nextRay == null) {
                    this.option.remove();
                } else {
                    this.dist -= (double)sig;
                    thisElement.remove();
                    GameLinkedList.Element newElement = ((GameLinkedList)nextParticles.object).addLast((BeamParticle)thisElement.object);
                    this.updatePos(newElement, nextRay, nextParticles);
                    return;
                }
            }
            double dx = ray.getX2() - ray.getX1();
            double dy = ray.getY2() - ray.getY1();
            Point2D.Float currentPos = this.option.getPos();
            this.movedX += currentPos.x - this.lastX;
            this.movedY += currentPos.y - this.lastY;
            this.lastX = (float)(ray.getX1() + dx * this.dist + (double)this.movedX);
            this.lastY = (float)(ray.getY1() + dy * this.dist + (double)this.movedY);
            this.option.changePos(this.lastX, this.lastY);
        }
    }
}

