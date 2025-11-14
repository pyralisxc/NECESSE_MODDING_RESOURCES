/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GameLinkedList<T>
implements Collection<T> {
    public final Object lock = new Object();
    private int size;
    private Element first;
    private Element last;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element addFirst(T object) {
        Object object2 = this.lock;
        synchronized (object2) {
            if (this.first == null) {
                Element e;
                this.first = e = new Element(object);
                this.last = e;
                this.size = 1;
                this.onAdded(e);
                return e;
            }
            return this.first.insertBefore(object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element addLast(T object) {
        Object object2 = this.lock;
        synchronized (object2) {
            if (this.last == null) {
                Element e;
                this.first = e = new Element(object);
                this.last = e;
                this.size = 1;
                this.onAdded(e);
                return e;
            }
            return this.last.insertAfter(object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T getFirst() {
        Object object = this.lock;
        synchronized (object) {
            return this.first == null ? null : (T)this.first.object;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element getFirstElement() {
        Object object = this.lock;
        synchronized (object) {
            return this.first;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T getLast() {
        Object object = this.lock;
        synchronized (object) {
            return this.last == null ? null : (T)this.last.object;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element getLastElement() {
        Object object = this.lock;
        synchronized (object) {
            return this.last;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T removeFirst() {
        Object object = this.lock;
        synchronized (object) {
            Element e = this.getFirstElement();
            e.remove();
            return e.object;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T removeLast() {
        Object object = this.lock;
        synchronized (object) {
            Element e = this.getLastElement();
            e.remove();
            return e.object;
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        Object object = this.lock;
        synchronized (object) {
            this.size = 0;
            this.first = null;
            this.last = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean contains(Object o) {
        Object object = this.lock;
        synchronized (object) {
            for (Element current = this.getFirstElement(); current != null; current = current.next()) {
                if (!Objects.equals(current.object, o)) continue;
                return true;
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object[] toArray() {
        Object object = this.lock;
        synchronized (object) {
            Object[] objects = new Object[this.size];
            Element current = this.getFirstElement();
            for (int i = 0; i < this.size; ++i) {
                objects[i] = current.object;
                current = current.next();
            }
            return objects;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T1> T1[] toArray(T1[] a) {
        Object object = this.lock;
        synchronized (object) {
            if (a.length < this.size) {
                return Arrays.copyOf(this.toArray(), this.size, a.getClass());
            }
            Element current = this.getFirstElement();
            for (int i = 0; i < this.size; ++i) {
                a[i] = current == null ? null : current.object;
                Object v0 = a[i];
                if (current == null) continue;
                current = current.next();
            }
            return a;
        }
    }

    @Override
    public boolean add(T t) {
        this.addLast(t);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Object o) {
        Object object = this.lock;
        synchronized (object) {
            for (Element current = this.getFirstElement(); current != null; current = current.next()) {
                if (!Objects.equals(current.object, o)) continue;
                current.remove();
                return true;
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        Object object = this.lock;
        synchronized (object) {
            for (Object e : c) {
                if (this.contains(e)) continue;
                return false;
            }
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        Object object = this.lock;
        synchronized (object) {
            for (T e : c) {
                this.add(e);
            }
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        Object object = this.lock;
        synchronized (object) {
            boolean removed = false;
            for (Object e : c) {
                if (!this.remove(e)) continue;
                removed = true;
            }
            return removed;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        Object object = this.lock;
        synchronized (object) {
            return this.removeIf((Predicate<? super T>)((Predicate<Object>)e -> !c.contains(e)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        Object object = this.lock;
        synchronized (object) {
            Objects.requireNonNull(filter);
            boolean removed = false;
            Element current = this.getFirstElement();
            while (current != null) {
                if (filter.test(current.object)) {
                    Element last = current;
                    current = current.next();
                    last.remove();
                    removed = true;
                    continue;
                }
                current = current.next();
            }
            return removed;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sort(Comparator<? super T> c) {
        Object object = this.lock;
        synchronized (object) {
            Object[] a = this.toArray();
            Arrays.sort(a, c);
            Element current = this.getFirstElement();
            for (Object e : a) {
                current = current.replace(e).next();
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new GameLinkedListIterator<Object>(e -> e.object);
    }

    public Iterator<Element> elementIterator() {
        return new GameLinkedListIterator<Element>(e -> e);
    }

    public ListIterator<T> listIterator() {
        return new GameListIterator<Object>(e -> e.object, true);
    }

    public GameListIterator<Element> elementListIterator() {
        return new GameListIterator<Element>(e -> e, false);
    }

    public Iterable<Element> elements() {
        return new ElementIterable();
    }

    public Stream<Element> streamElements() {
        return StreamSupport.stream(this.elements().spliterator(), false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printDebug() {
        Object object = this.lock;
        synchronized (object) {
            int i = 0;
            System.out.println("Size: " + this.size + ", first: " + this.hexString(this.first) + ", last: " + this.hexString(this.last));
            for (Element element : this.elements()) {
                System.out.println(i + ": " + this.hexString(element) + " (" + element.object + "), prev: " + this.hexString(element.prev) + ", next: " + this.hexString(element.next));
                ++i;
            }
        }
    }

    public String toString() {
        Iterator<T> it = this.iterator();
        if (!it.hasNext()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        while (true) {
            T next;
            sb.append((Object)((next = it.next()) == this ? "(this GameLinkedList)" : next));
            if (!it.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
    }

    private String hexString(Object o) {
        return o == null ? null : Integer.toHexString(o.hashCode());
    }

    public void onAdded(Element element) {
    }

    public void onRemoved(Element element) {
    }

    public class Element {
        private boolean removed;
        private Element prev;
        private Element next;
        public final T object;

        private Element(T object) {
            this.object = object;
        }

        public GameLinkedList<T> getList() {
            return GameLinkedList.this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean hasNext() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                return this.next != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Element next() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                if (this.removed) {
                    throw new IllegalStateException("Cannot perform actions on removed element");
                }
                return this.next;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Element nextWrap() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                if (this.removed) {
                    throw new IllegalStateException("Cannot perform actions on removed element");
                }
                return this.next == null ? GameLinkedList.this.first : this.next;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean hasPrev() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                return this.prev != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Element prev() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                if (this.removed) {
                    throw new IllegalStateException("Cannot perform actions on removed element");
                }
                return this.prev;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Element prevWrap() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                if (this.removed) {
                    throw new IllegalStateException("Cannot perform actions on removed element");
                }
                return this.prev == null ? GameLinkedList.this.last : this.prev;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Element insertAfter(T object) {
            Object object2 = GameLinkedList.this.lock;
            synchronized (object2) {
                if (this.removed) {
                    throw new IllegalStateException("Cannot perform actions on removed element");
                }
                Element e = new Element(object);
                e.prev = this;
                e.next = this.next;
                if (this.next != null) {
                    this.next.prev = e;
                } else {
                    GameLinkedList.this.last = e;
                }
                this.next = e;
                GameLinkedList.this.size++;
                GameLinkedList.this.onAdded(e);
                return e;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Element insertBefore(T object) {
            Object object2 = GameLinkedList.this.lock;
            synchronized (object2) {
                if (this.removed) {
                    throw new IllegalStateException("Cannot perform actions on removed element");
                }
                Element e = new Element(object);
                e.next = this;
                e.prev = this.prev;
                if (this.prev != null) {
                    this.prev.next = e;
                } else {
                    GameLinkedList.this.first = e;
                }
                this.prev = e;
                GameLinkedList.this.size++;
                GameLinkedList.this.onAdded(e);
                return e;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Element replace(T object) {
            Object object2 = GameLinkedList.this.lock;
            synchronized (object2) {
                if (this.removed) {
                    throw new IllegalStateException("Cannot perform actions on removed element");
                }
                Element out = new Element(object);
                out.prev = this.prev;
                out.next = this.next;
                if (this.prev != null) {
                    this.prev.next = out;
                } else {
                    GameLinkedList.this.first = out;
                }
                if (this.next != null) {
                    this.next.prev = out;
                } else {
                    GameLinkedList.this.last = out;
                }
                this.removed = true;
                GameLinkedList.this.onRemoved(this);
                GameLinkedList.this.onAdded(out);
                return out;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void remove() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                if (this.removed) {
                    throw new IllegalStateException("Cannot perform actions on removed element");
                }
                if (this.prev != null) {
                    this.prev.next = this.next;
                } else {
                    GameLinkedList.this.first = this.next;
                }
                if (this.next != null) {
                    this.next.prev = this.prev;
                } else {
                    GameLinkedList.this.last = this.prev;
                }
                GameLinkedList.this.size--;
                this.removed = true;
                GameLinkedList.this.onRemoved(this);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void removePrev() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                if (this.prev != null) {
                    this.prev.remove();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void removePrevWrap() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                Element prev = this.prevWrap();
                if (prev != null) {
                    prev.remove();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void removeNext() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                if (this.next != null) {
                    this.next.remove();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void removeNextWrap() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                Element next = this.nextWrap();
                if (next != null) {
                    next.remove();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean isRemoved() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                return this.removed;
            }
        }
    }

    private class GameLinkedListIterator<E>
    implements Iterator<E> {
        private Element next;
        private Function<Element, E> extractor;

        public GameLinkedListIterator(Function<Element, E> extractor) {
            this.extractor = extractor;
            this.next = GameLinkedList.this.first;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean hasNext() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                return this.next != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E next() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                E out = this.extractor.apply(this.next);
                this.next = this.next.next;
                return out;
            }
        }
    }

    public class GameListIterator<E>
    implements ListIterator<E> {
        private Element current;
        private Element next;
        private Function<Element, E> extractor;
        private boolean isListObjectIterator;

        public GameListIterator(Function<Element, E> extractor, boolean isListObjectIterator) {
            this.extractor = extractor;
            this.isListObjectIterator = isListObjectIterator;
            this.next = GameLinkedList.this.first;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean hasNext() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                return this.next != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E next() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                if (this.next == null) {
                    if (GameLinkedList.this.first == null) {
                        throw new NoSuchElementException("List is empty");
                    }
                    throw new NoSuchElementException("We are at the end of the list");
                }
                E out = this.extractor.apply(this.next);
                this.current = this.next;
                this.next = this.next.next;
                return out;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean hasPrevious() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                return this.next != null && this.next.prev != null || GameLinkedList.this.last != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public E previous() {
            Object object = GameLinkedList.this.lock;
            synchronized (object) {
                if (this.next != null) {
                    if (this.next.prev == null) {
                        throw new NoSuchElementException("We are at the beginning of the list");
                    }
                    E out = this.extractor.apply(this.next.prev);
                    this.current = this.next.prev;
                    this.next = this.next.prev;
                    return out;
                }
                if (GameLinkedList.this.last == null) {
                    throw new NoSuchElementException("List is empty");
                }
                E out = this.extractor.apply(GameLinkedList.this.last);
                this.current = GameLinkedList.this.last;
                this.next = GameLinkedList.this.last;
                return out;
            }
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException("Indexes not supported in GameLinkedLists");
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException("Indexes not supported in GameLinkedLists");
        }

        @Override
        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException("No element has been returned");
            }
            this.current.remove();
            this.current = null;
        }

        @Override
        public void set(E e) {
            if (!this.isListObjectIterator) {
                throw new UnsupportedOperationException("List iterator is not for list objects. Use GameLinkedListListIterator.setObject(T) instead");
            }
            this.setObject(e);
        }

        public void setObject(T object) {
            if (this.current == null) {
                throw new IllegalStateException("No element has been returned");
            }
            this.current.replace(object);
        }

        @Override
        public void add(E e) {
            if (!this.isListObjectIterator) {
                throw new UnsupportedOperationException("List iterator is not for list objects. Use GameLinkedListListIterator.setObject(T) instead");
            }
            this.addObject(e);
        }

        public void addObject(T object) {
            if (this.next == null) {
                GameLinkedList.this.addLast(object);
            } else {
                this.next.insertBefore(object);
            }
        }
    }

    private class ElementIterable
    implements Iterable<Element> {
        private ElementIterable() {
        }

        @Override
        public Iterator<Element> iterator() {
            return new GameLinkedListIterator<Element>(e -> e);
        }
    }
}

