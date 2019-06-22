import java.util.ArrayList;
import java.util.List;

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
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");
        return sb.toString();
    }

    public static Tree parse(String line){
        return new Tree(getNode(line, 0));
    }

    private static Node getNode(String line, int index){
        Node root = new Node(index, -1);
        StringBuilder current = new StringBuilder();

        Node lastNode = null;
        boolean stateD = false;
        boolean stateE = false;
        String name = null;
        double dist = 0;

        for (int i = index; i < line.length(); i++){
            char c = line.charAt(i);
            if (Character.isWhitespace(c)) continue;

            if(stateD){
                if (Character.isDigit(c) || c == '.'){
                    current.append(c);
                    continue;
                } else {
                    dist = Double.parseDouble(current.toString());
                    stateD = false;
                    if (stateE){
                        lastNode.distance = dist;
                        stateE = false;
                        current = new StringBuilder();
                    }
                }
            }

            switch (c){
                case ')':
                    if (current.length() > 0){
                        lastNode = new Node(i, Integer.parseInt(name), dist);
                        root.children.add(lastNode);
                    }
                    root.index = i;
                    return root;
                case '(':
                    Node child = getNode(line, ++i);
                    lastNode = child;
                    i = child.index;
                    root.children.add(child);
                    stateE = true;
                    break;
                case ',':
                    if(current.length() > 0){
                        lastNode = new Node(i, Integer.parseInt(name), dist);
                        root.children.add(lastNode);
                    }
                    current = new StringBuilder();
                    break;
                case ':':
                    name = current.toString();
                    current = new StringBuilder();
                    stateD = true;
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
        int value;
        double distance;
        int index;

        Node(int index, int value){
            this(index, value, 0);
        }

        Node(int index, int value, double distance){
            this.children = new ArrayList<>();
            this.index = index;
            this.distance = distance;
            this.value = value;
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
            sb.append(value).append(" : ").append(distance);
        }
    }
}
