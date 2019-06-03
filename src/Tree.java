import java.util.ArrayList;
import java.util.List;

public class Tree {

    private Root root;

    public Tree(){
        this.root = new Root();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        for (Node child : root.children){
            child.print(sb);
            sb.append(" , ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" )");
        return sb.toString();
    }

    class Node {
        Node left;
        Node rigth;
        int value;
        int index;

        Node(int index){
            this.left = null;
            this.rigth = null;
            this.index = index;
        }

        void print(StringBuilder sb){
            if(left != null && rigth != null) {
                sb.append("( ");
                left.print(sb);
                sb.append(" , ");
                rigth.print(sb);
                sb.append(" ), ");
            }
//            if(rigth != null) rigth.print(sb);
            sb.append(index);
        }
    }

    class Root {
        List<Node> children;

        Root(){
            this.children = new ArrayList<>();
            this.children.add(new Node(0));
            this.children.add(new Node(1));
            this.children.add(new Node(2));
        }
    }
}
