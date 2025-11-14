/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public class ModularPreset
extends Preset {
    public final int sectionWidth;
    public final int sectionHeight;
    public final int sectionRes;
    public final int openingSize;
    public final int openingDepth;
    public int closeObject;
    public int closeTile;
    public int openObject;
    public int openTile;
    protected ArrayList<Opening> openings;
    public boolean overlap = false;

    public ModularPreset(int sectionWidth, int sectionHeight, int sectionRes, int openingSize, int openingDepth) {
        super(sectionWidth * sectionRes, sectionHeight * sectionRes);
        this.sectionWidth = sectionWidth;
        this.sectionHeight = sectionHeight;
        this.sectionRes = sectionRes;
        this.openingSize = openingSize;
        this.openingDepth = openingDepth;
        this.openings = new ArrayList();
        this.closeObject = -1;
        this.closeTile = -1;
        this.openObject = -1;
        this.openTile = -1;
    }

    protected void open(int x, int y, int dir) {
        this.openings.add(new Opening(x, y, dir));
    }

    protected ModularPreset newModularObject(int sectionWidth, int sectionHeight, int sectionRes, int openingSize, int openingDepth) {
        return new ModularPreset(sectionWidth, sectionHeight, sectionRes, openingSize, openingDepth);
    }

    @Override
    protected final ModularPreset newObject(int width, int height) {
        ModularPreset preset = this.newModularObject(width / this.sectionRes, height / this.sectionRes, this.sectionRes, this.openingSize, this.openingDepth);
        preset.closeObject = this.closeObject;
        preset.closeTile = this.closeTile;
        preset.openObject = this.openObject;
        preset.openTile = this.openTile;
        preset.overlap = this.overlap;
        return preset;
    }

    @Override
    public ModularPreset copy() {
        ModularPreset preset = (ModularPreset)super.copy();
        preset.openings.addAll(this.openings);
        return preset;
    }

    @Override
    public ModularPreset mirrorX() throws PresetMirrorException {
        ModularPreset preset = (ModularPreset)super.mirrorX();
        for (Opening opening : this.openings) {
            int mirroredX = this.getMirroredX(opening.x);
            int dir = opening.dir;
            if (dir == 1) {
                dir = 3;
            } else if (dir == 3) {
                dir = 1;
            }
            preset.openings.add(new Opening(mirroredX, opening.y, dir));
        }
        return preset;
    }

    @Override
    public ModularPreset mirrorY() throws PresetMirrorException {
        ModularPreset preset = (ModularPreset)super.mirrorY();
        for (Opening opening : this.openings) {
            int mirroredY = this.getMirroredY(opening.y);
            int dir = opening.dir;
            if (dir == 0) {
                dir = 2;
            } else if (dir == 2) {
                dir = 0;
            }
            preset.openings.add(new Opening(opening.x, mirroredY, dir));
        }
        return preset;
    }

    @Override
    public ModularPreset rotate(PresetRotation rotation) throws PresetRotateException {
        ModularPreset preset = (ModularPreset)super.rotate(rotation);
        int rotationOffset = rotation == null ? 0 : rotation.dirOffset;
        for (Opening opening : this.openings) {
            Point rp = PresetUtils.getRotatedPointInSpace(opening.x, opening.y, this.width, this.height, rotation);
            preset.openings.add(new Opening(rp.x, rp.y, (opening.dir + rotationOffset) % 4));
        }
        return preset;
    }

    public void openLevel(Level level, int x, int y, int xOffset, int yOffset, int dir, GameRandom random, int cellRes) {
        this.openLevel(level, x, y, xOffset, yOffset, dir, cellRes);
    }

    public void openLevel(Level level, int x, int y, int xOffset, int yOffset, int dir, int cellRes) {
        this.fillOpening(level, x, y, xOffset, yOffset, dir, this.openObject, this.openTile, cellRes);
    }

    public void closeLevel(Level level, int x, int y, int xOffset, int yOffset, int dir, int cellRes) {
        this.fillOpening(level, x, y, xOffset, yOffset, dir, this.closeObject, this.closeTile, cellRes);
    }

    public void fillOpening(Level level, int x, int y, int xOffset, int yOffset, int dir, int object, int tile, int cellRes) {
        dir = Math.abs(dir) % 4;
        int farEnd = cellRes - this.openingDepth + (this.overlap ? 1 : 0);
        int middle = cellRes / 2 - this.openingSize / 2;
        if (dir == 0) {
            this.fillOpeningReal(level, xOffset + x * cellRes + middle, yOffset + y * cellRes, dir, object, tile);
        } else if (dir == 1) {
            this.fillOpeningReal(level, xOffset + x * cellRes + farEnd, yOffset + y * cellRes + middle, dir, object, tile);
        } else if (dir == 2) {
            this.fillOpeningReal(level, xOffset + x * cellRes + middle, yOffset + y * cellRes + farEnd, dir, object, tile);
        } else if (dir == 3) {
            this.fillOpeningReal(level, xOffset + x * cellRes, yOffset + y * cellRes + middle, dir, object, tile);
        }
    }

    public void fillOpeningReal(Level level, int x, int y, int dir, int object, int tile) {
        if ((dir = Math.abs(dir) % 4) == 0) {
            if (object != -1) {
                this.fillObject(level, x, y, this.openingSize, this.openingDepth, object);
            }
            if (tile != -1) {
                this.fillTile(level, x, y, this.openingSize, this.openingDepth, tile);
            }
        } else if (dir == 1) {
            if (object != -1) {
                this.fillObject(level, x, y, this.openingDepth, this.openingSize, object);
            }
            if (tile != -1) {
                this.fillTile(level, x, y, this.openingDepth, this.openingSize, tile);
            }
        } else if (dir == 2) {
            if (object != -1) {
                this.fillObject(level, x, y, this.openingSize, this.openingDepth, object);
            }
            if (tile != -1) {
                this.fillTile(level, x, y, this.openingSize, this.openingDepth, tile);
            }
        } else if (dir == 3) {
            if (object != -1) {
                this.fillObject(level, x, y, this.openingDepth, this.openingSize, object);
            }
            if (tile != -1) {
                this.fillTile(level, x, y, this.openingDepth, this.openingSize, tile);
            }
        }
    }

    public void fillObject(Level level, int x, int y, int width, int height, int object, int rotation) {
        for (int i = x; i < width + x; ++i) {
            for (int j = y; j < height + y; ++j) {
                level.setObject(i, j, object);
                level.setObjectRotation(i, j, (byte)rotation);
            }
        }
    }

    public void fillObject(Level level, int x, int y, int width, int height, int object) {
        this.fillObject(level, x, y, width, height, object, 0);
    }

    public void fillTile(Level level, int x, int y, int width, int height, int tile) {
        for (int i = x; i < width + x; ++i) {
            for (int j = y; j < height + y; ++j) {
                level.setTile(i, j, tile);
            }
        }
    }

    public boolean isOpen(int x, int y, int dir) {
        for (Opening p : this.openings) {
            if (p.x != x || p.y != y || p.dir != dir) continue;
            return true;
        }
        return false;
    }

    public boolean getSpecificOpenLeft(int y) {
        for (Opening p : this.openings) {
            if (p.x != 0 || p.y != y || p.dir != 3) continue;
            return true;
        }
        return false;
    }

    public int getOpenLeft(int randomInt) {
        for (int i = 0; i < this.openings.size(); ++i) {
            int index = (i + Math.abs(randomInt)) % this.openings.size();
            Opening p = this.openings.get(index);
            if (p.x != 0 || p.dir != 3) continue;
            return p.y;
        }
        return -1;
    }

    public int getOpenLeft() {
        return this.getOpenLeft(0);
    }

    public boolean isOpenLeft() {
        return this.getOpenLeft() != -1;
    }

    public boolean getSpecificOpenRight(int y) {
        for (Opening p : this.openings) {
            if (p.x != this.sectionWidth - 1 || p.y != y || p.dir != 1) continue;
            return true;
        }
        return false;
    }

    public int getOpenRight(int randomInt) {
        for (int i = 0; i < this.openings.size(); ++i) {
            int index = (i + Math.abs(randomInt)) % this.openings.size();
            Opening p = this.openings.get(index);
            if (p.x != this.sectionWidth - 1 || p.dir != 1) continue;
            return p.y;
        }
        return -1;
    }

    public int getOpenRight() {
        return this.getOpenRight(0);
    }

    public boolean isOpenRight() {
        return this.getOpenRight() != -1;
    }

    public boolean getSpecificOpenTop(int x) {
        for (Opening p : this.openings) {
            if (p.x != x || p.y != 0 || p.dir != 0) continue;
            return true;
        }
        return false;
    }

    public int getOpenTop(int randomInt) {
        for (int i = 0; i < this.openings.size(); ++i) {
            int index = (i + Math.abs(randomInt)) % this.openings.size();
            Opening p = this.openings.get(index);
            if (p.y != 0 || p.dir != 0) continue;
            return p.x;
        }
        return -1;
    }

    public int getOpenTop() {
        return this.getOpenTop(0);
    }

    public boolean isOpenTop() {
        return this.getOpenTop() != -1;
    }

    public boolean getSpecificOpenBottom(int x) {
        for (Opening p : this.openings) {
            if (p.x != x || p.y != this.sectionHeight - 1 || p.dir != 2) continue;
            return true;
        }
        return false;
    }

    public int getOpenBottom(int randomInt) {
        for (int i = 0; i < this.openings.size(); ++i) {
            int index = (i + Math.abs(randomInt)) % this.openings.size();
            Opening p = this.openings.get(index);
            if (p.y != this.sectionHeight - 1 || p.dir != 2) continue;
            return p.x;
        }
        return -1;
    }

    public int getOpenBottom() {
        return this.getOpenBottom(0);
    }

    public boolean isOpenBottom() {
        return this.getOpenBottom() != -1;
    }

    public int getRandomOpenDir(int randomInt, int dir) {
        if ((dir = Math.abs(dir) % 4) == 0) {
            return this.getOpenTop(randomInt);
        }
        if (dir == 1) {
            return this.getOpenRight(randomInt);
        }
        if (dir == 2) {
            return this.getOpenBottom(randomInt);
        }
        if (dir == 3) {
            return this.getOpenLeft(randomInt);
        }
        return -1;
    }

    public int getOpenDir(int dir) {
        return this.getRandomOpenDir(0, dir);
    }

    public boolean isOpenDir(int dir) {
        return this.getOpenDir(dir) != -1;
    }

    public boolean canPlace(Level level, int x, int y) {
        return true;
    }

    protected static class Opening
    extends Point {
        public int dir;

        public Opening(int x, int y, int dir) {
            super(x, y);
            this.dir = dir;
        }
    }
}

