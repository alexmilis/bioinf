import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tree {

    private Node root;
    private int size;

    public Node getRoot() {
        return root;
    }

    public Tree(Node root){
        this.root = root;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        root.print(sb);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");
        return sb.toString();
    }

    public static Tree parse(String line){
        return new Tree(getNode(line, 0));
    }


    public int getChanges(){
        return postorder(root, 0);
    }


    private int postorder(Node root, int changes){
        changes = 0;
        for(Node child : root.children){
            changes += postorder(child, 0);
        }
        changes += fitch(root);
        return changes;
    }

    private int fitch(Node root){
        if(root.children.size() > 0){
            Set<String> intersection = new HashSet<>(root.children.get(0).value);
            intersection.retainAll(root.children.get(1).value);
            if (root.children.size() == 3){
                intersection.retainAll(root.children.get(2).value);
            }
            if (intersection.size() > 0){
                root.value = intersection;
                return 0;
            } else {
                Set<String> union = new HashSet<>(root.children.get(0).value);
                union.addAll(root.children.get(1).value);
                if (root.children.size() == 3){
                    union.addAll(root.children.get(2).value);
                }
                root.value = union;
                return 1;
            }
        }
        return 0;
    }

    private static Node getNode(String line, int index){
        Node root = new Node(index, "");
        StringBuilder current = new StringBuilder();
        for (int i = index; i < line.length(); i++){
            char c = line.charAt(i);
            if (Character.isWhitespace(c)) continue;
            switch (c){
                case ')':
                    if (current.length() > 0) root.children.add(new Node(i, current.toString()));
                    root.index = i;
                    return root;
                case '(':
                    Node child = getNode(line, ++i);
                    i = child.index;
                    root.children.add(child);
                    break;
                case ',':
                    if(current.length() > 0) root.children.add(new Node(i, current.toString()));
                    current = new StringBuilder();
                    break;
                default:
                    current.append(c);
                    break;
            }
        }
        if(root.children.size() == 1) return root.children.get(0);
        return root;
    }


    static class Node {
        List<Node> children;
        Set<String> value;
        int depth;
        int index;

        Node(int index, String value){
            this.children = new ArrayList<>();
            this.index = index;
            this.value = new HashSet<>();
            this.value.add(value);
        }

        void print(StringBuilder sb){
            if (children.size() > 0){
                sb.append("( ");
                for(Node child : children){
                    child.print(sb);
                    sb.append(" , ");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);
                sb.append(" ) ");
            }
            sb.append(value);
        }
    }
}
