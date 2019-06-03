public class Tree {

    private Node root;

    public Tree(int value){
        this.root = new Node(value);
    }

    class Node {
        private Node left = null;
        private Node rigth = null;
        private int value;

        Node(int value){
            this.value = value;

        }

    }
}
