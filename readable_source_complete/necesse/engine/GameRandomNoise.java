/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import necesse.engine.util.GameMath;
import necesse.gfx.drawables.LinesDrawOptionsList;
import necesse.gfx.drawables.QuadDrawOptionsList;

public class GameRandomNoise {
    private static final Vector3[] vectors = new Vector3[]{new Vector3(1, 1, 0), new Vector3(-1, 1, 0), new Vector3(1, -1, 0), new Vector3(-1, -1, 0), new Vector3(1, 0, 1), new Vector3(-1, 0, 1), new Vector3(1, 0, -1), new Vector3(-1, 0, -1), new Vector3(0, 1, 1), new Vector3(0, -1, 1), new Vector3(0, 1, -1), new Vector3(0, -1, -1)};
    private static final int[] permutations = new int[]{151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};
    private static final int permutationLengthBits = GameRandomNoise.getBitLength(permutations.length);
    private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
    private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
    private static final double F3 = 0.3333333333333333;
    private static final double G3 = 0.16666666666666666;
    private final int[] seededPermutations = new int[permutations.length * 2];
    private final Vector3[] vectorPermutations = new Vector3[permutations.length * 2];

    private static int getBitLength(int value) {
        double bits = Math.log(value) / Math.log(2.0);
        if ((double)((int)bits) != bits) {
            throw new IllegalArgumentException("Value length must be a power of 2");
        }
        return (int)bits;
    }

    public GameRandomNoise(int seed) {
        this.seed(seed);
    }

    public void seed(int seed) {
        if (seed < permutations.length) {
            seed |= seed << permutationLengthBits;
        }
        for (int i = 0; i < permutations.length; ++i) {
            int v = (i & 1) == 1 ? permutations[i] ^ seed & permutations.length - 1 : permutations[i] ^ seed >> permutationLengthBits & permutations.length - 1;
            int n = v;
            this.seededPermutations[i + GameRandomNoise.permutations.length] = n;
            this.seededPermutations[i] = n;
            Vector3 vector3 = vectors[v % 12];
            this.vectorPermutations[i + GameRandomNoise.permutations.length] = vector3;
            this.vectorPermutations[i] = vector3;
        }
    }

    public double simplex2(double xin, double yin) {
        double n2;
        double n1;
        double n0;
        int j1;
        int i1;
        double y0;
        int j;
        double t;
        double s = (xin + yin) * F2;
        int i = (int)Math.floor(xin + s);
        double x0 = xin - (double)i + (t = (double)(i + (j = (int)Math.floor(yin + s))) * G2);
        if (x0 > (y0 = yin - (double)j + t)) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }
        double x1 = x0 - (double)i1 + G2;
        double y1 = y0 - (double)j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;
        Vector3 gi0 = this.vectorPermutations[(i &= permutations.length - 1) + this.seededPermutations[j &= permutations.length - 1]];
        Vector3 gi1 = this.vectorPermutations[i + i1 + this.seededPermutations[j + j1]];
        Vector3 gi2 = this.vectorPermutations[i + 1 + this.seededPermutations[j + 1]];
        double t0 = 0.5 - x0 * x0 - y0 * y0;
        if (t0 < 0.0) {
            n0 = 0.0;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * gi0.dot2(x0, y0);
        }
        double t1 = 0.5 - x1 * x1 - y1 * y1;
        if (t1 < 0.0) {
            n1 = 0.0;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * gi1.dot2(x1, y1);
        }
        double t2 = 0.5 - x2 * x2 - y2 * y2;
        if (t2 < 0.0) {
            n2 = 0.0;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * gi2.dot2(x2, y2);
        }
        return 70.0 * (n0 + n1 + n2);
    }

    public double simplex3(double xin, double yin, double zin) {
        double n3;
        double n2;
        double n1;
        double n0;
        int k2;
        int j2;
        int i2;
        int k1;
        int j1;
        int i1;
        double s = (xin + yin + zin) * 0.3333333333333333;
        int i = (int)Math.floor(xin + s);
        int j = (int)Math.floor(yin + s);
        int k = (int)Math.floor(zin + s);
        double t = (double)(i + j + k) * 0.16666666666666666;
        double x0 = xin - (double)i + t;
        double y0 = yin - (double)j + t;
        double z0 = zin - (double)k + t;
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else if (y0 < z0) {
            i1 = 0;
            j1 = 0;
            k1 = 1;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else if (x0 < z0) {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 1;
            j2 = 1;
            k2 = 0;
        }
        double x1 = x0 - (double)i1 + 0.16666666666666666;
        double y1 = y0 - (double)j1 + 0.16666666666666666;
        double z1 = z0 - (double)k1 + 0.16666666666666666;
        double x2 = x0 - (double)i2 + 0.3333333333333333;
        double y2 = y0 - (double)j2 + 0.3333333333333333;
        double z2 = z0 - (double)k2 + 0.3333333333333333;
        double x3 = x0 - 1.0 + 0.5;
        double y3 = y0 - 1.0 + 0.5;
        double z3 = z0 - 1.0 + 0.5;
        Vector3 gi0 = this.vectorPermutations[(i &= permutations.length - 1) + this.seededPermutations[(j &= permutations.length - 1) + this.seededPermutations[k &= permutations.length - 1]]];
        Vector3 gi1 = this.vectorPermutations[i + i1 + this.seededPermutations[j + j1 + this.seededPermutations[k + k1]]];
        Vector3 gi2 = this.vectorPermutations[i + i2 + this.seededPermutations[j + j2 + this.seededPermutations[k + k2]]];
        Vector3 gi3 = this.vectorPermutations[i + 1 + this.seededPermutations[j + 1 + this.seededPermutations[k + 1]]];
        double t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
        if (t0 < 0.0) {
            n0 = 0.0;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * gi0.dot3(x0, y0, z0);
        }
        double t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
        if (t1 < 0.0) {
            n1 = 0.0;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * gi1.dot3(x1, y1, z1);
        }
        double t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
        if (t2 < 0.0) {
            n2 = 0.0;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * gi2.dot3(x2, y2, z2);
        }
        double t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
        if (t3 < 0.0) {
            n3 = 0.0;
        } else {
            t3 *= t3;
            n3 = t3 * t3 * gi3.dot3(x3, y3, z3);
        }
        return 32.0 * (n0 + n1 + n2 + n3);
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
    }

    private double lerp(double a, double b, double t) {
        return (1.0 - t) * a + t * b;
    }

    public double perlin1(double x) {
        int cellX = (int)Math.floor(x);
        x -= (double)cellX;
        double n0 = (double)this.vectorPermutations[this.seededPermutations[cellX &= permutations.length - 1]].x * x;
        double n1 = (double)this.vectorPermutations[this.seededPermutations[cellX + 1]].x * (x - 1.0);
        double u = this.fade(x);
        return this.lerp(n0, n1, u);
    }

    private double vector1(int hash) {
        return (hash & 1) == 0 ? 1.0 : -1.0;
    }

    public double perlin1New(double x) {
        return this.perlin2(x, x);
    }

    public double perlin2(double x, double y) {
        int cellX = (int)Math.floor(x);
        int cellY = (int)Math.floor(y);
        x -= (double)cellX;
        y -= (double)cellY;
        double n00 = this.vectorPermutations[(cellX &= permutations.length - 1) + this.seededPermutations[cellY &= permutations.length - 1]].dot2(x, y);
        double n01 = this.vectorPermutations[cellX + this.seededPermutations[cellY + 1]].dot2(x, y - 1.0);
        double n10 = this.vectorPermutations[cellX + 1 + this.seededPermutations[cellY]].dot2(x - 1.0, y);
        double n11 = this.vectorPermutations[cellX + 1 + this.seededPermutations[cellY + 1]].dot2(x - 1.0, y - 1.0);
        double u = this.fade(x);
        return this.lerp(this.lerp(n00, n10, u), this.lerp(n01, n11, u), this.fade(y));
    }

    public double perlin3(double x, double y, double z) {
        int cellX = (int)Math.floor(x);
        int cellY = (int)Math.floor(y);
        int cellZ = (int)Math.floor(z);
        x -= (double)cellX;
        y -= (double)cellY;
        z -= (double)cellZ;
        double n000 = this.vectorPermutations[(cellX &= permutations.length - 1) + this.seededPermutations[(cellY &= permutations.length - 1) + this.seededPermutations[cellZ &= permutations.length - 1]]].dot3(x, y, z);
        double n001 = this.vectorPermutations[cellX + this.seededPermutations[cellY + this.seededPermutations[cellZ + 1]]].dot3(x, y, z - 1.0);
        double n010 = this.vectorPermutations[cellX + this.seededPermutations[cellY + 1 + this.seededPermutations[cellZ]]].dot3(x, y - 1.0, z);
        double n011 = this.vectorPermutations[cellX + this.seededPermutations[cellY + 1 + this.seededPermutations[cellZ + 1]]].dot3(x, y - 1.0, z - 1.0);
        double n100 = this.vectorPermutations[cellX + 1 + this.seededPermutations[cellY + this.seededPermutations[cellZ]]].dot3(x - 1.0, y, z);
        double n101 = this.vectorPermutations[cellX + 1 + this.seededPermutations[cellY + this.seededPermutations[cellZ + 1]]].dot3(x - 1.0, y, z - 1.0);
        double n110 = this.vectorPermutations[cellX + 1 + this.seededPermutations[cellY + 1 + this.seededPermutations[cellZ]]].dot3(x - 1.0, y - 1.0, z);
        double n111 = this.vectorPermutations[cellX + 1 + this.seededPermutations[cellY + 1 + this.seededPermutations[cellZ + 1]]].dot3(x - 1.0, y - 1.0, z - 1.0);
        double u = this.fade(x);
        double v = this.fade(y);
        double w = this.fade(z);
        return this.lerp(this.lerp(this.lerp(n000, n100, u), this.lerp(n001, n101, u), w), this.lerp(this.lerp(n010, n110, u), this.lerp(n011, n111, u), w), v);
    }

    public double simplex2Fractal(double x, double y, int octaves, double persistence) {
        double total = 0.0;
        double frequency = 1.0;
        double amplitude = 1.0;
        double maxAmplitude = 0.0;
        for (int i = 0; i < octaves; ++i) {
            total += this.simplex2(x * frequency, y * frequency) * amplitude;
            maxAmplitude += amplitude;
            amplitude *= persistence;
            frequency *= 2.0;
        }
        return total / maxAmplitude;
    }

    public double simplex3Fractal(double x, double y, double z, int octaves, double persistence) {
        double total = 0.0;
        double frequency = 1.0;
        double amplitude = 1.0;
        double maxAmplitude = 0.0;
        for (int i = 0; i < octaves; ++i) {
            total += this.simplex3(x * frequency, y * frequency, z * frequency) * amplitude;
            maxAmplitude += amplitude;
            amplitude *= persistence;
            frequency *= 2.0;
        }
        return total / maxAmplitude;
    }

    public double perlin1Fractal(double x, int octaves, double persistence) {
        double total = 0.0;
        double frequency = 1.0;
        double amplitude = 1.0;
        double maxAmplitude = 0.0;
        for (int i = 0; i < octaves; ++i) {
            total += this.perlin1(x * frequency) * amplitude;
            maxAmplitude += amplitude;
            amplitude *= persistence;
            frequency *= 2.0;
        }
        return total / maxAmplitude;
    }

    public double perlin1NewFractal(double x, int octaves, double persistence) {
        double total = 0.0;
        double frequency = 1.0;
        double amplitude = 1.0;
        double maxAmplitude = 0.0;
        for (int i = 0; i < octaves; ++i) {
            total += this.perlin1New(x * frequency) * amplitude;
            maxAmplitude += amplitude;
            amplitude *= persistence;
            frequency *= 2.0;
        }
        return total / maxAmplitude;
    }

    public double perlin2Fractal(double x, double y, int octaves, double persistence) {
        double total = 0.0;
        double frequency = 1.0;
        double amplitude = 1.0;
        double maxAmplitude = 0.0;
        for (int i = 0; i < octaves; ++i) {
            total += this.perlin2(x * frequency, y * frequency) * amplitude;
            maxAmplitude += amplitude;
            amplitude *= persistence;
            frequency *= 2.0;
        }
        return total / maxAmplitude;
    }

    public double perlin3Fractal(double x, double y, double z, int octaves, double persistence) {
        double total = 0.0;
        double frequency = 1.0;
        double amplitude = 1.0;
        double maxAmplitude = 0.0;
        for (int i = 0; i < octaves; ++i) {
            total += this.perlin3(x * frequency, y * frequency, z * frequency) * amplitude;
            maxAmplitude += amplitude;
            amplitude *= persistence;
            frequency *= 2.0;
        }
        return total / maxAmplitude;
    }

    public double perlin1Derivative(double x) {
        int cellX = (int)Math.floor(x);
        x -= (double)cellX;
        double n00 = this.vectorPermutations[this.seededPermutations[cellX &= permutations.length - 1]].x;
        double n10 = this.vectorPermutations[this.seededPermutations[cellX + 1]].x;
        double derivative = -36.0 * n00 * x * x * x * x * x + 75.0 * n00 * x * x * x * x - 40.0 * n00 * x * x * x + n00 + 36.0 * n10 * x * x * x * x * x - 105.0 * n10 * x * x * x * x + 100.0 * n10 * x * x * x - 30.0 * n10 * x * x;
        return -derivative;
    }

    public static LinesDrawOptionsList get1DebugDraw(int drawX, int drawY, int width, int height, double startX, double endX, double currentX, Debug1Draw ... noiseGetters) {
        int i;
        LinesDrawOptionsList list = new LinesDrawOptionsList();
        list.add(drawX, drawY, drawX + width, drawY, 1.0f, 1.0f, 1.0f, 0.5f);
        list.add(drawX + width, drawY, drawX + width, drawY + height, 1.0f, 1.0f, 1.0f, 0.5f);
        list.add(drawX + width, drawY + height, drawX, drawY + height, 1.0f, 1.0f, 1.0f, 0.5f);
        list.add(drawX, drawY + height, drawX, drawY, 1.0f, 1.0f, 1.0f, 0.5f);
        int halfHeight = height / 2;
        int midDrawY = drawY + halfHeight;
        list.add(drawX, midDrawY, drawX + width, midDrawY, 1.0f, 1.0f, 1.0f, 0.5f);
        double deltaX = endX - startX;
        double xProgressPerPixel = deltaX / (double)width;
        double[] last = new double[noiseGetters.length];
        for (i = 0; i < noiseGetters.length; ++i) {
            last[i] = noiseGetters[i].get(startX);
        }
        for (i = 1; i < width; ++i) {
            for (int j = 0; j < noiseGetters.length; ++j) {
                double x = startX + xProgressPerPixel * (double)i;
                Debug1Draw noiseGetter = noiseGetters[j];
                double next = noiseGetter.get(x);
                list.add(drawX + i - 1, (float)midDrawY - (float)(last[j] * (double)halfHeight), drawX + i, (float)midDrawY - (float)(next * (double)halfHeight), noiseGetter.red, noiseGetter.green, noiseGetter.blue, noiseGetter.alpha);
                last[j] = next;
            }
        }
        double currentXDelta = currentX - startX;
        int currentLineX = (int)(currentXDelta / xProgressPerPixel);
        if (currentLineX < width) {
            list.add(drawX + currentLineX, drawY, drawX + currentLineX, drawY + height, 0.0f, 1.0f, 1.0f, 0.5f);
        }
        return list;
    }

    public static LinesDrawOptionsList get1DebugDraw(int drawX, int drawY, int width, int height, double startX, double endX, double currentX, final DoubleUnaryOperator noiseGetter) {
        return GameRandomNoise.get1DebugDraw(drawX, drawY, width, height, startX, endX, currentX, new Debug1Draw(1.0f, 0.0f, 0.0f, 1.0f){

            @Override
            public double get(double x) {
                return noiseGetter.applyAsDouble(x);
            }
        });
    }

    public LinesDrawOptionsList getPerlin1DebugDraw(int drawX, int drawY, int width, int height, double startX, double endX, double currentX) {
        return GameRandomNoise.get1DebugDraw(drawX, drawY, width, height, startX, endX, currentX, this::perlin1);
    }

    public LinesDrawOptionsList getFadeCurve1DebugDraw(int drawX, int drawY, int width, int height, double startX, double endX, double currentX) {
        return GameRandomNoise.get1DebugDraw(drawX, drawY, width, height, startX, endX, currentX, this::perlin1Derivative);
    }

    public static QuadDrawOptionsList get2DebugDraw(int drawX, int drawY, int width, int height, ToDoubleFunction<Point> noiseGetter) {
        QuadDrawOptionsList list = new QuadDrawOptionsList();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                float value = (float)noiseGetter.applyAsDouble(new Point(x, y));
                float percent = GameMath.map(value, -1.0f, 1.0f, 0.0f, 1.0f);
                list.add(drawX + x, drawY + y, 1, 1, percent, percent, percent, 1.0f);
            }
        }
        return list;
    }

    public static QuadDrawOptionsList get2DebugDrawFull(int drawX, int drawY, int width, int height, ToDoubleFunction<Point> noiseGetter) {
        QuadDrawOptionsList list = new QuadDrawOptionsList();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                float value = (float)noiseGetter.applyAsDouble(new Point(x, y));
                if (value < 0.0f) {
                    list.add(drawX + x, drawY + y, 1, 1, -value, 0.0f, 0.0f, 1.0f);
                    continue;
                }
                list.add(drawX + x, drawY + y, 1, 1, 0.0f, value, 0.0f, 1.0f);
            }
        }
        return list;
    }

    public QuadDrawOptionsList getPerlin2DebugDraw(int drawX, int drawY, int width, int height, Function<Point, Point2D.Double> coordGetter) {
        return GameRandomNoise.get2DebugDraw(drawX, drawY, width, height, p -> {
            Point2D.Double coord = (Point2D.Double)coordGetter.apply((Point)p);
            return this.perlin2(coord.x, coord.y);
        });
    }

    private static class Vector3 {
        private final int x;
        private final int y;
        private final int z;

        public Vector3(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double dot2(double x, double y) {
            return (double)this.x * x + (double)this.y * y;
        }

        public double dot3(double x, double y, double z) {
            return (double)this.x * x + (double)this.y * y + (double)this.z * z;
        }
    }

    public static abstract class Debug1Draw {
        public float red;
        public float green;
        public float blue;
        public float alpha;

        public Debug1Draw(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public abstract double get(double var1);
    }
}

