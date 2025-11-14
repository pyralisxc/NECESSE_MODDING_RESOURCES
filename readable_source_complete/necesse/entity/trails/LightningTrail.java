/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.trails;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailDrawSection;
import necesse.entity.trails.TrailPointList;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;

public class LightningTrail
extends Trail {
    public LightningTrail(TrailVector vector, Level level, Color color) {
        super(vector, level, color, 250);
        this.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
    }

    public void addNewPoint(float distance, float fluctuation, float height) {
        TrailPointList.TrailPoint origin = this.points.get(0);
        Point2D.Float perp = new Point2D.Float(-origin.vector.dy, origin.vector.dx);
        Point2D.Float newPoint = new Point2D.Float(origin.vector.pos.x + origin.vector.dx * distance - perp.x * fluctuation, origin.vector.pos.y + origin.vector.dy * distance - perp.y * fluctuation);
        this.addNewPoint(new TrailVector(newPoint, origin.vector.dx, origin.vector.dy, this.thickness, height));
    }

    public void addNewPoint(TrailVector vector) {
        this.addPoints(0, vector);
    }

    @Override
    protected DrawOptions getDrawSection(TrailDrawSection s, GameCamera camera) {
        Color col = this.getColor();
        if (col == null) {
            col = Color.WHITE;
        }
        return s.getSpriteTrailsDraw(this.sprite, camera, TrailDrawSection.lightColorSetter(this.level, col));
    }

    @Override
    protected DrawOptions getDrawNextSection(TrailDrawSection s, TrailPointList list, float alpha, GameCamera camera) {
        Color col = this.getColor();
        if (col == null) {
            col = Color.WHITE;
        }
        return TrailDrawSection.getSpriteTrailsDraw(this.sprite, list, 0, list.size() - 1, camera, TrailDrawSection.lightColorSetter(this.level, col));
    }
}

