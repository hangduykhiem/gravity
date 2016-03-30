package fi.zalando.core.domain.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import fi.zalando.core.utils.EqualUtils;

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

        root = new Node<>(rootData, new ArrayList<>());
    }

    /**
     * Constructor that start the tree with a pre-made root
     *
     * @param root {@link Node<T>} pre-made root
     */
    public Tree(Node<T> root){
        this.root = root;
    }

    public Node<T> getRoot() {
        return root;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tree<?> tree = (Tree<?>) o;
        return EqualUtils.areEqual(root, tree.root);
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }

    /**
     * Class that holds the nodes of the trees.
     *
     * @param <T> {@link T} generic type
     */
    public static class Node<T> {

        private final T data;
        @Nullable
        // Transient needed in case it is serialised as string,
        // serializer does not end in an endless loop
        private transient Node<T> parent;
        private final List<Node<T>> children;

        /**
         * Constructor
         *
         * @param data     {@link T} with the data
         * @param children {@link List} with all the {@link Node} children
         */
        public Node(T data, @Nullable List<Node<T>> children) {

            this.data = data;
            this.children = children != null ? children : new ArrayList<>();
        }

        public void addChildren(Node<T> childrenNode) {

            // Set current node as parent of the new node before adding to the children list
            childrenNode.setParent(this);
            children.add(childrenNode);
        }

        public T getData() {
            return data;
        }

        @Nullable
        public Node<T> getParent() {
            return parent;
        }

        public void setParent(@Nullable Node<T> parent) {
            this.parent = parent;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public boolean isALeaf() {
            return children.isEmpty();
        }

        // NOTE: Don't use parent for equality/hashcode comparison, or the tree will end in an
        // endless loop!

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Node<?> node = (Node<?>) o;
            return EqualUtils.areEqual(data, node.data) &&
//                    EqualUtils.areEqual(parent, node.parent) &&
                    EqualUtils.areEqual(children, node.children);
        }

        @Override
        public int hashCode() {
            int result = data.hashCode();
//            result = 31 * result + (parent != null ? parent.hashCode() : 0);
            result = 31 * result + children.hashCode();
            return result;
        }
    }
}
