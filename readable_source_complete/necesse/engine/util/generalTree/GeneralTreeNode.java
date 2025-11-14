/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.generalTree;

import java.util.HashMap;
import java.util.List;

public abstract class GeneralTreeNode {
    public final GeneralTreeNode parent;
    public final GeneralTreeNode leftNeighbour;
    public final int level;
    public final int childIndex;
    public final int nodeSize;
    public int x;
    public int y;
    protected int prelim;
    protected int modifier;

    public GeneralTreeNode(HashMap<Integer, GeneralTreeNode> prevLevelNodes, GeneralTreeNode parent, int childIndex, int size) {
        this.parent = parent;
        this.level = parent == null ? 0 : parent.level + 1;
        this.childIndex = childIndex;
        this.nodeSize = size;
        this.leftNeighbour = prevLevelNodes.getOrDefault(this.level, null);
        prevLevelNodes.put(this.level, this);
    }

    protected void resetPos() {
        this.x = 0;
        this.y = 0;
        this.prelim = 0;
        this.modifier = 0;
        for (GeneralTreeNode generalTreeNode : this.getChildren()) {
            generalTreeNode.resetPos();
        }
    }

    public abstract List<? extends GeneralTreeNode> getChildren();

    public GeneralTreeNode getFirstChild() {
        List<? extends GeneralTreeNode> children = this.getChildren();
        if (children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }

    public GeneralTreeNode getLeftSibling() {
        if (!this.hasLeftSibling()) {
            return null;
        }
        return this.parent.getChildren().get(this.childIndex - 1);
    }

    public boolean hasLeftSibling() {
        return this.childIndex > 0;
    }

    public GeneralTreeNode getRightSibling() {
        if (!this.hasRightSibling()) {
            return null;
        }
        return this.parent.getChildren().get(this.childIndex + 1);
    }

    public boolean hasRightSibling() {
        if (this.parent == null) {
            return false;
        }
        return this.childIndex < this.parent.getChildren().size() - 1;
    }

    public GeneralTreeNode getLeftNeighbour() {
        return this.leftNeighbour;
    }

    public boolean isLeaf() {
        return this.getChildren().isEmpty();
    }
}

