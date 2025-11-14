/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.buildmode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ShapeCalculator {
    public static List<Point> calculatePositions(int centerX, int centerY, int selectedShape, boolean isHollow, int playerDir, int lineLength, int squareSize, int circleRadius, int spacing, int objectWidth, int objectHeight) {
        ArrayList<Point> positions = new ArrayList<Point>();
        int effectiveSpacingX = spacing + objectWidth - 1;
        int effectiveSpacingY = spacing + objectHeight - 1;
        switch (selectedShape) {
            case 0: {
                positions.add(new Point(centerX, centerY));
                break;
            }
            case 1: {
                block45: for (int i = 0; i < lineLength; ++i) {
                    int offset = switch (playerDir) {
                        case 0, 2 -> (i - lineLength / 2) * (spacing + objectHeight - 1);
                        default -> (i - lineLength / 2) * (spacing + objectWidth - 1);
                    };
                    switch (playerDir) {
                        case 0: {
                            positions.add(new Point(centerX, centerY - offset));
                            continue block45;
                        }
                        case 1: {
                            positions.add(new Point(centerX + offset, centerY));
                            continue block45;
                        }
                        case 2: {
                            positions.add(new Point(centerX, centerY + offset));
                            continue block45;
                        }
                        case 3: {
                            positions.add(new Point(centerX - offset, centerY));
                        }
                    }
                }
                break;
            }
            case 2: {
                for (int i = 0; i < lineLength; ++i) {
                    int offsetX = (i - lineLength / 2) * (spacing + objectWidth - 1);
                    int offsetY = (i - lineLength / 2) * (spacing + objectHeight - 1);
                    positions.add(new Point(centerX + offsetX, centerY));
                    if (i == lineLength / 2) continue;
                    positions.add(new Point(centerX, centerY + offsetY));
                }
                break;
            }
            case 3: {
                block47: for (int i = 0; i < lineLength; ++i) {
                    int offsetX = i * (spacing + objectWidth - 1);
                    int offsetY = i * (spacing + objectHeight - 1);
                    switch (playerDir) {
                        case 0: {
                            positions.add(new Point(centerX, centerY - offsetY));
                            if (i == 0) continue block47;
                            positions.add(new Point(centerX + offsetX, centerY));
                            continue block47;
                        }
                        case 1: {
                            positions.add(new Point(centerX + offsetX, centerY));
                            if (i == 0) continue block47;
                            positions.add(new Point(centerX, centerY + offsetY));
                            continue block47;
                        }
                        case 2: {
                            positions.add(new Point(centerX, centerY + offsetY));
                            if (i == 0) continue block47;
                            positions.add(new Point(centerX - offsetX, centerY));
                            continue block47;
                        }
                        case 3: {
                            positions.add(new Point(centerX - offsetX, centerY));
                            if (i == 0) continue block47;
                            positions.add(new Point(centerX, centerY - offsetY));
                        }
                    }
                }
                break;
            }
            case 4: {
                int halfLen = lineLength / 2;
                switch (playerDir) {
                    case 0: {
                        int i;
                        for (i = 0; i < lineLength; ++i) {
                            int offset = (i - halfLen) * (spacing + objectWidth - 1);
                            positions.add(new Point(centerX + offset, centerY));
                        }
                        for (i = 1; i < lineLength; ++i) {
                            positions.add(new Point(centerX, centerY - i * (spacing + objectHeight - 1)));
                        }
                        break;
                    }
                    case 1: {
                        int i;
                        for (i = 0; i < lineLength; ++i) {
                            int offset = (i - halfLen) * (spacing + objectHeight - 1);
                            positions.add(new Point(centerX, centerY + offset));
                        }
                        for (i = 1; i < lineLength; ++i) {
                            positions.add(new Point(centerX + i * (spacing + objectWidth - 1), centerY));
                        }
                        break;
                    }
                    case 2: {
                        int i;
                        for (i = 0; i < lineLength; ++i) {
                            int offset = (i - halfLen) * (spacing + objectWidth - 1);
                            positions.add(new Point(centerX + offset, centerY));
                        }
                        for (i = 1; i < lineLength; ++i) {
                            positions.add(new Point(centerX, centerY + i * (spacing + objectHeight - 1)));
                        }
                        break;
                    }
                    case 3: {
                        int i;
                        for (i = 0; i < lineLength; ++i) {
                            int offset = (i - halfLen) * (spacing + objectHeight - 1);
                            positions.add(new Point(centerX, centerY + offset));
                        }
                        for (i = 1; i < lineLength; ++i) {
                            positions.add(new Point(centerX - i * (spacing + objectWidth - 1), centerY));
                        }
                        break;
                    }
                }
                break;
            }
            case 5: {
                int startSquareX = centerX - squareSize * effectiveSpacingX / 2;
                int startSquareY = centerY - squareSize * effectiveSpacingY / 2;
                for (int x = 0; x < squareSize; ++x) {
                    for (int y = 0; y < squareSize; ++y) {
                        boolean isEdge;
                        boolean bl = isEdge = x == 0 || x == squareSize - 1 || y == 0 || y == squareSize - 1;
                        if (isHollow && !isEdge) continue;
                        positions.add(new Point(startSquareX + x * effectiveSpacingX, startSquareY + y * effectiveSpacingY));
                    }
                }
                break;
            }
            case 6: {
                int radius = circleRadius;
                double edgeThickness = 0.5;
                for (int x = -radius; x <= radius; ++x) {
                    for (int y = -radius; y <= radius; ++y) {
                        boolean onEdge;
                        double distance = Math.sqrt(x * x + y * y);
                        boolean inCircle = distance <= (double)radius;
                        boolean bl = onEdge = Math.abs(distance - (double)radius) <= edgeThickness;
                        if (!(!isHollow && inCircle || isHollow && onEdge)) continue;
                        positions.add(new Point(centerX + x * effectiveSpacingX, centerY + y * effectiveSpacingY));
                    }
                }
                break;
            }
            case 7: {
                int diamondSize = squareSize;
                for (int x = -diamondSize; x <= diamondSize; ++x) {
                    for (int y = -diamondSize; y <= diamondSize; ++y) {
                        boolean onEdge;
                        int manhattanDist = Math.abs(x) + Math.abs(y);
                        boolean inDiamond = manhattanDist <= diamondSize;
                        boolean bl = onEdge = manhattanDist == diamondSize;
                        if (!(!isHollow && inDiamond || isHollow && onEdge)) continue;
                        positions.add(new Point(centerX + x * effectiveSpacingX, centerY + y * effectiveSpacingY));
                    }
                }
                break;
            }
            case 8: {
                int halfRadius = circleRadius;
                for (int x = -halfRadius; x <= halfRadius; ++x) {
                    for (int y = -halfRadius; y <= halfRadius; ++y) {
                        boolean shouldPlace;
                        double dist = Math.sqrt(x * x + y * y);
                        boolean inCircle = dist <= (double)halfRadius;
                        boolean onCurvedEdge = dist >= (double)halfRadius - 0.5 && dist <= (double)halfRadius + 0.5;
                        boolean inHalf = false;
                        boolean onBackWall = false;
                        switch (playerDir) {
                            case 0: {
                                inHalf = y <= 0;
                                onBackWall = y == 0 && Math.abs(x) <= halfRadius;
                                break;
                            }
                            case 1: {
                                inHalf = x >= 0;
                                onBackWall = x == 0 && Math.abs(y) <= halfRadius;
                                break;
                            }
                            case 2: {
                                inHalf = y >= 0;
                                onBackWall = y == 0 && Math.abs(x) <= halfRadius;
                                break;
                            }
                            case 3: {
                                inHalf = x <= 0;
                                boolean bl = onBackWall = x == 0 && Math.abs(y) <= halfRadius;
                            }
                        }
                        if (isHollow) {
                            shouldPlace = inHalf && onCurvedEdge || onBackWall;
                        } else {
                            boolean bl = shouldPlace = inHalf && inCircle;
                        }
                        if (!shouldPlace) continue;
                        positions.add(new Point(centerX + x * effectiveSpacingX, centerY + y * effectiveSpacingY));
                    }
                }
                break;
            }
            case 9: {
                int triSize = circleRadius;
                for (int x = -triSize; x <= triSize; ++x) {
                    for (int y = -triSize; y <= triSize; ++y) {
                        boolean inTriangle = false;
                        boolean onEdge = false;
                        switch (playerDir) {
                            case 0: {
                                inTriangle = y <= 0 && y >= -triSize && Math.abs(x) <= -y;
                                onEdge = y == 0 && Math.abs(x) == 0 || y < 0 && Math.abs(x) == -y || y == -triSize && Math.abs(x) <= triSize;
                                break;
                            }
                            case 1: {
                                inTriangle = x >= 0 && x <= triSize && Math.abs(y) <= x;
                                onEdge = x == 0 && Math.abs(y) == 0 || x > 0 && Math.abs(y) == x || x == triSize && Math.abs(y) <= triSize;
                                break;
                            }
                            case 2: {
                                inTriangle = y >= 0 && y <= triSize && Math.abs(x) <= y;
                                onEdge = y == 0 && Math.abs(x) == 0 || y > 0 && Math.abs(x) == y || y == triSize && Math.abs(x) <= triSize;
                                break;
                            }
                            case 3: {
                                inTriangle = x <= 0 && x >= -triSize && Math.abs(y) <= -x;
                                boolean bl = onEdge = x == 0 && Math.abs(y) == 0 || x < 0 && Math.abs(y) == -x || x == -triSize && Math.abs(y) <= triSize;
                            }
                        }
                        if (!(!isHollow && inTriangle || isHollow && onEdge)) continue;
                        positions.add(new Point(centerX + x * effectiveSpacingX, centerY + y * effectiveSpacingY));
                    }
                }
                break;
            }
        }
        return positions;
    }

    public static List<Point> calculatePositions(int centerX, int centerY, int selectedShape, boolean isHollow, int playerDir, int lineLength, int squareSize, int circleRadius) {
        return ShapeCalculator.calculatePositions(centerX, centerY, selectedShape, isHollow, playerDir, lineLength, squareSize, circleRadius, 1, 1, 1);
    }
}

