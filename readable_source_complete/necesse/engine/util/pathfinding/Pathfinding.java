/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.HashProxyLinkedList;
import necesse.engine.util.pathfinding.PathResult;

public abstract class Pathfinding<T> {
    public boolean useBestOfConnected = false;
    public boolean biDirectional = false;

    public <C extends Pathfinding<T>> Process<C> getProcess(T from, T target, int maxIterations) {
        return new Process(this, this, from, target, maxIterations);
    }

    public <C extends Pathfinding<T>> PathResult<T, C> findPath(T from, T target, int maxIterations) {
        Process<C> process = this.getProcess(from, target, maxIterations);
        return process.findPath();
    }

    protected abstract boolean isAtTarget(T var1, T var2);

    protected boolean handleNewNode(T node, Node current, HashProxyLinkedList<Node, T> openNodes, HashProxyLinkedList<Node, T> closedNodes) {
        Node openNode;
        double newPathCost = current.pathCost + this.getNodePathCost(node, current.item);
        boolean foundInNodes = false;
        Node closedNode = closedNodes.getObject(node);
        if (closedNode != null) {
            if (closedNode.pathCost > newPathCost) {
                closedNode.setCameFrom(current, newPathCost);
            }
            foundInNodes = true;
        }
        if ((openNode = openNodes.getObject(node)) != null) {
            if (openNode.pathCost > newPathCost) {
                openNode.setCameFrom(current, newPathCost);
            }
            foundInNodes = true;
        }
        return foundInNodes;
    }

    protected abstract void handleConnectedNodes(Node var1, HashProxyLinkedList<Node, T> var2, HashProxyLinkedList<Node, T> var3, HashSet<T> var4, Function<T, Boolean> var5, BiConsumer<Node, Node> var6, Runnable var7);

    public LinkedList<Node> constructPath(Node node) {
        LinkedList<Node> list = new LinkedList<Node>();
        list.add(node);
        while (node.cameFrom != null) {
            list.addFirst(node.cameFrom);
            node = node.cameFrom;
        }
        return list;
    }

    protected Node removeAndReturnNextNode(HashProxyLinkedList<Node, T> openNodes) {
        GameLinkedList.Element best = openNodes.streamElements().min(Comparator.comparingDouble(e -> this.getNodeComparable((Node)e.object))).orElse(null);
        if (best == null) {
            return null;
        }
        best.remove();
        return (Node)best.object;
    }

    protected double getNodeComparable(Node node) {
        return node.heuristicCost + node.pathCost;
    }

    protected abstract double getNodeHeuristicCost(T var1, T var2);

    protected abstract double getNodeCost(T var1);

    protected abstract double getNodePathCost(T var1, T var2);

    public class Process<C extends Pathfinding<T>> {
        public final C finder;
        public final T from;
        public final T target;
        public final int maxIterations;
        private final HashProxyLinkedList<Node, T> openNodes = new HashProxyLinkedList<Node, T>(n -> n.item){

            @Override
            public void onAdded(GameLinkedList.Element element) {
                super.onAdded(element);
                if (((Node)element.object).reverseDirection) {
                    Process.this.openBiDirectionalNodes.add((Node)element.object);
                }
            }

            @Override
            public void onRemoved(GameLinkedList.Element element) {
                super.onRemoved(element);
                if (!((Node)element.object).reverseDirection || !Process.this.openBiDirectionalNodes.remove(((Node)element.object).item)) {
                    // empty if block
                }
            }
        };
        private final HashProxyLinkedList<Node, T> openBiDirectionalNodes = new HashProxyLinkedList<Node, Object>(n -> n.item);
        private final HashProxyLinkedList<Node, T> closedNodes = new HashProxyLinkedList<Node, Object>(n -> n.item);
        private final HashSet<T> invalidChecked = new HashSet();
        private int iterations;
        private long time;
        private PathResult<T, C> result;
        public final Object nodeLock = new Object();
        final /* synthetic */ Pathfinding this$0;

        /*
         * WARNING - Possible parameter corruption
         */
        public Process(C finder, T from, T target, int maxIterations) {
            this.this$0 = (Pathfinding)this$0;
            this.finder = finder;
            this.from = from;
            this.target = target;
            this.maxIterations = maxIterations;
            long time = System.nanoTime();
            if (from == null || target == null) {
                this.result = new PathResult(finder, from, target, new LinkedList<Node>(), false, new ArrayList<Node>(this.openNodes), new ArrayList<Node>(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - time);
            } else {
                Node firstPathNode = (Pathfinding)this$0.new Node(from, target, null, false);
                this.openNodes.add(firstPathNode);
                if (this$0.biDirectional) {
                    this.openNodes.add((Pathfinding)this$0.new Node(target, from, null, true));
                }
                if (this$0.isAtTarget(from, target)) {
                    this.result = new PathResult(finder, from, target, this$0.constructPath(firstPathNode), true, new ArrayList<Node>(this.openNodes), new ArrayList<Node>(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - time);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean iterate(int iterations) {
            if (this.result != null) {
                return true;
            }
            Object object = this.nodeLock;
            synchronized (object) {
                AtomicBoolean interrupted = new AtomicBoolean();
                long time = System.nanoTime();
                boolean done = false;
                for (int i = 0; i < iterations; ++i) {
                    if (this.openNodes.isEmpty() || this.iterations >= this.maxIterations || this.result != null || this.this$0.biDirectional && this.openBiDirectionalNodes.isEmpty()) {
                        done = true;
                        break;
                    }
                    ++this.iterations;
                    Node current = this.this$0.removeAndReturnNextNode(this.openNodes);
                    this.closedNodes.add(current);
                    LinkedList resultNodes = new LinkedList();
                    this.this$0.handleConnectedNodes(current, this.openNodes, this.closedNodes, this.invalidChecked, connected -> {
                        this.invalidChecked.remove(connected);
                        if (this.this$0.handleNewNode(connected, current, this.openNodes, this.closedNodes)) {
                            return false;
                        }
                        Node connectedNode = current.reverseDirection ? this.this$0.new Node(connected, this.from, current, true) : this.this$0.new Node(connected, this.target, current, false);
                        current.goesTo.add(connectedNode);
                        if (this.this$0.isAtTarget(connectedNode.item, this.target)) {
                            resultNodes.add(connectedNode);
                            return !this.this$0.useBestOfConnected;
                        }
                        this.openNodes.add(connectedNode);
                        return false;
                    }, (n1, n2) -> {
                        if (n1.reverseDirection != n2.reverseDirection) {
                            Node next;
                            Node last = n1.reverseDirection ? n2 : n1;
                            Node node = next = n1.reverseDirection ? n1 : n2;
                            while (next != null) {
                                Node temp = next.cameFrom;
                                next.cameFrom = last;
                                last = next;
                                next = temp;
                            }
                            this.result = new PathResult(this.finder, this.from, this.target, this.this$0.constructPath(last), true, new ArrayList<Node>(this.openNodes), new ArrayList<Node>(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - time);
                        }
                    }, () -> interrupted.set(true));
                    if (!resultNodes.isEmpty()) {
                        Node best = resultNodes.stream().min(Comparator.comparingDouble(this.this$0::getNodeComparable)).orElse(null);
                        this.result = new PathResult(this.finder, this.from, this.target, this.this$0.constructPath(best), true, new ArrayList<Node>(this.openNodes), new ArrayList<Node>(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - time);
                        break;
                    }
                    if (interrupted.get()) break;
                }
                if (this.result == null && (done || iterations >= this.maxIterations || interrupted.get())) {
                    Node best = Stream.concat(this.openNodes.stream(), this.closedNodes.stream()).min(Comparator.comparingDouble(n -> n.heuristicCost)).orElse(null);
                    this.result = best == null ? new PathResult(this.finder, this.from, this.target, new LinkedList<Node>(), false, new ArrayList<Node>(this.openNodes), new ArrayList<Node>(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - time) : new PathResult(this.finder, this.from, this.target, this.this$0.constructPath(best), false, new ArrayList<Node>(this.openNodes), new ArrayList<Node>(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - time);
                }
                this.time += System.nanoTime() - time;
                return this.result != null;
            }
        }

        public PathResult<T, C> findPath() {
            this.iterate(this.maxIterations);
            return this.result;
        }

        public boolean isDone() {
            return this.result != null;
        }

        public PathResult<T, C> getResult() {
            return this.result;
        }

        public Iterable<Node> getOpenNodes() {
            return this.openNodes;
        }

        public Iterable<Node> getClosedNodes() {
            return this.closedNodes;
        }

        public Iterable<T> getInvalidChecked() {
            return this.invalidChecked;
        }

        public Stream<Node> streamOpenNodes() {
            return this.openNodes.stream();
        }

        public Stream<Node> streamClosedNodes() {
            return this.closedNodes.stream();
        }

        public Stream<T> streamInvalidChecked() {
            return this.invalidChecked.stream();
        }

        public int getIterations() {
            return this.iterations;
        }

        public long getTime() {
            return this.time;
        }
    }

    public class Node {
        public final T item;
        public final boolean reverseDirection;
        public Node cameFrom;
        public LinkedList<Node> goesTo;
        public double heuristicCost;
        public double nodeCost;
        public double pathCost;
        public int pathCount;

        public Node(T item, T target, Node cameFrom, boolean reverseDirection) {
            this.item = item;
            this.reverseDirection = reverseDirection;
            this.setCameFrom(cameFrom);
            this.goesTo = new LinkedList();
            this.heuristicCost = Pathfinding.this.getNodeHeuristicCost(item, target);
        }

        public void setCameFrom(Node cameFrom, Double newPathCost) {
            this.cameFrom = cameFrom;
            if (cameFrom != null) {
                this.pathCost = newPathCost != null ? newPathCost : Pathfinding.this.getNodePathCost(this.item, cameFrom.item) + cameFrom.pathCost;
                this.pathCount = cameFrom.pathCount + 1;
                this.nodeCost = Pathfinding.this.getNodeCost(this.item) + cameFrom.nodeCost;
            } else {
                this.pathCost = 0.0;
                this.pathCount = 0;
                this.nodeCost = Pathfinding.this.getNodeCost(this.item);
            }
        }

        public void setCameFrom(Node cameFrom) {
            this.setCameFrom(cameFrom, null);
        }
    }
}

