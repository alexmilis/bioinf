import java.util.List;

public class Likelihood {

    private static List<String> bases = List.of("a", "t", "c", "g");

    static double[] prior;

    static double getLikelihood(Tree tree, double[] prob){
        double sum = 0;
        prior = prob;

        for(double pi : prior){
            sum += pi * likelihood(tree.getRoot());
        }

        return sum;
    }

    private static double likelihood(Tree.Node node){
        double sum = 0;
        for(Tree.Node child : node.children){
            sum += change(node.value, child.value, child.distance);
        }
    }

    private static double change(int i, int j, int distance){
        if(i == j) return Math.exp(-distance);
        return (1 - Math.exp(-distance)) * prior[bases.indexOf(j)];
    }
}
