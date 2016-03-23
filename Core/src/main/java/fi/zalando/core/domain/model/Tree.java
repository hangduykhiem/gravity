package fi.zalando.core.domain.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic Tree structured class
 *
 * @param <T> {@link T} generic type
 */
public class Tree<T> {

    private final Node<T> root;

    /**
     * Constructor that starts the tree using as root the given data
     *
     * @param rootData {@link T} that will be the root of the {@link Tree}
     */
    public Tree(T rootData) {

        root = new Node<>(rootData, null, new ArrayList<>());
    }

    public Node<T> getRoot() {
        return root;
    }

    private static class Node<T> {

        private final T data;
        @Nullable
        private final Node<T> parent;
        private final List<Node<T>> children;

        private Node(T data, @Nullable Node<T> parent, List<Node<T>> children) {

            this.data = data;
            this.parent = parent;
            this.children = children != null ? children : new ArrayList<>();
        }

        public T getData() {
            return data;
        }

        @Nullable
        public Node<T> getParent() {
            return parent;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public void addChildren(Node<T> childrenNode) {

            children.add(childrenNode);
        }
    }
}
