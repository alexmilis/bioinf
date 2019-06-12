import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Likelihood {

    private static List<Character> bases = List.of('a', 't', 'c', 'g');

    private static double[] prior = new double[]{0.25, 0.25, 0.25, 0.25};
    private static double[][] changeProbabilities = new double[][]{{0.8, 0.1, 0.1, 0.1},
                                                                    {0.1, 0.8, 0.1, 0.1},
                                                                    {0.1, 0.1, 0.8, 0.1},
                                                                    {0.1, 0.1, 0.1, 0.8}};

    private static final String STARTTREE = "(0, 1);";

    private static final double THRESHOLD = 0.05;
    private static final double STEP = 0.1;

    public static void main(String[] args) {
//        Path infile = Paths.get(String.format(random, g, h));
        Path infile;

        List<List<Integer>> matrix = new ArrayList<>();
        Map<Integer, String> names = new HashMap<>();

        System.out.println("Reading file: " + infile);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(infile.toString()));
            String line = reader.readLine();

            int i = 0;
            while (line != null) {
                names.put(i++, line.substring(1));
                line = reader.readLine();
                matrix.add(line.chars().mapToObj(c -> bases.indexOf(c)).collect(Collectors.toList()));
                line = reader.readLine();
            }

            reader.close();
        } catch (Exception ex) {
            System.out.println("Cannot read file");
            System.exit(1);
        }

        long startTime = System.currentTimeMillis();


        List<String> trees = new ArrayList<>();
        int numberOfTrees = matrix.size();
        int sites = matrix.get(0).size();

        trees.add(STARTTREE);

        for (int i = 2; i < numberOfTrees; i++) {
            List<String> current = new ArrayList<>();
            List<Double> likelihoods = new ArrayList<>();
            for (String tree : trees) {
                for (int j = 0; j < i; j++) {
                    String newtree = tree.replaceFirst(String.format(" %d ", j), String.format(" ( %d , %d ) ", j, i));
                    current.add(newtree);
                    likelihoods.add(getLikelihood(Tree.parse(newtree)));
                }
            }
            trees.clear();
            trees.addAll(current);
        }















    }

    static double evaluateBranches(Tree tree){


    }


    static double getLikelihood(Tree tree){
        double sum = 0;

        for(int i = 0; i < 4; i++){
            sum += prior[i] * likelihood(tree.getRoot(), i);
        }

        return sum;
    }



//  todo pretvori baze u brojeve kao indeksi u bases
    private static double likelihood(Tree.Node node, int index){
        double product = 1;

        if(node.children.size() == 0){
            if(index == bases.indexOf(node.value)) return 1;
            return 0;
        }

        for(Tree.Node child : node.children){
            double sum = 0;
            for(int i = 0; i < 4; i++){
//                iteriranje po indexu ili i?
                sum += probability(index, i, child.distance) * likelihood(child, i);
            }
            product *= sum;
        }
        return product;
    }

    private static double probability(int i, int j, int distance){
        if(i == j) return Math.exp(-distance);
        return (1 - Math.exp(-distance)) * changeProbabilities[i][j];
    }
}
