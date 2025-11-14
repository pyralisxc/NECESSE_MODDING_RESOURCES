/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import necesse.engine.util.pathfinding.Pathfinding;

public class PathResult<T, C extends Pathfinding<T>> {
    public final C finder;
    public final T start;
    public final T target;
    public final LinkedList<Pathfinding.Node> path;
    public final boolean foundTarget;
    public final ArrayList<Pathfinding.Node> openNodes;
    public final ArrayList<Pathfinding.Node> closedNodes;
    public final HashSet<T> invalidChecked;
    public final int iterations;
    public final int maxIterations;
    public final long nsTaken;

    public PathResult(C finder, T start, T target, LinkedList<Pathfinding.Node> path, boolean foundTarget, ArrayList<Pathfinding.Node> openNodes, ArrayList<Pathfinding.Node> closedNodes, HashSet<T> invalidChecked, int iterations, int maxIterations, long nsTaken) {
        this.finder = finder;
        this.start = start;
        this.target = target;
        this.path = path;
        this.foundTarget = foundTarget;
        this.openNodes = openNodes;
        this.closedNodes = closedNodes;
        this.invalidChecked = invalidChecked;
        this.iterations = iterations;
        this.maxIterations = maxIterations;
        this.nsTaken = nsTaken;
    }

    public Pathfinding.Node getLastPathNode() {
        if (this.path.isEmpty()) {
            return null;
        }
        return this.path.get(this.path.size() - 1);
    }

    public T getLastPathResult() {
        if (this.path.isEmpty()) {
            return null;
        }
        return this.path.get((int)(this.path.size() - 1)).item;
    }
}

