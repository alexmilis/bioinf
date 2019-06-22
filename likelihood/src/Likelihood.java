import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Likelihood {

    private static List<Character> bases = List.of('a', 't', 'c', 'g');

    private static double[] prior = new double[]{0.25, 0.25, 0.25, 0.25};
    private static double[][] changeProbabilities = new double[][]{{0.8, 0.1, 0.1, 0.1},
                                                                    {0.1, 0.8, 0.1, 0.1},
                                                                    {0.1, 0.1, 0.8, 0.1},
                                                                    {0.1, 0.1, 0.1, 0.8}};

    private static final String STARTTREE = "( 0 : 1.0 , 1 : 1.0);";

    private static final double THRESHOLD = 0.05;

    private static String first = "resources/first%d.fasta";


    public static void main(String[] args) {
//        Path infile = Paths.get(String.format(random, g, h));
//        TODO ULAZNI FILE

//        Path infile = null;
        Path infile = Paths.get(String.format(first, 5));


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


        int numberOfTrees = matrix.size();
        int sites = matrix.get(0).size();
        String bestTree = STARTTREE;


        evaluateBranches(Tree.parse(bestTree).getRoot(), sites);


        for (int i = 2; i < numberOfTrees; i++) {
            List<String> trees = new ArrayList<>();
            List<Double> likelihoods = new ArrayList<>();

            for (int j = 0; j < i; j++) {
                String str = String.format(" %d : ", j);
                int index = bestTree.indexOf(str) + str.length();
                int index2 = bestTree.indexOf(" ", index);
                String dist = bestTree.substring(index, index2).strip();

                String newtree = bestTree.replaceFirst(String.format(" %d : %s ", j, dist), String.format(" ( %d : 1.0 , %d : 1.0) : %s", j, i, dist));
                trees.add(newtree);

                Tree tree = Tree.parse(newtree);


                evaluateBranches(tree.getRoot(), sites);
                likelihoods.add(getLikelihood(tree.getRoot()));
            }

            bestTree = trees.get(likelihoods.indexOf(Collections.max(likelihoods)));

        }


        System.out.println(bestTree);

        long time = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Time in millis: %d", time));



    }

    static boolean evaluateBranches(Tree.Node node, int k){
        boolean result = true;

        while (result){
            result = branch(node, k);
            for (Tree.Node child : node.children){
                result &= evaluateBranches(child, k);
            }
        }

        return result;
    }

    static boolean branch(Tree.Node node, int k){
        double prob = 1;

        double oldlikelihood = getLikelihood(node); //TODO PROVJERI JEL RADI

        double p = Math.exp(-node.distance);

        while (prob - oldlikelihood > THRESHOLD){
            oldlikelihood = prob;
            double sum = 0;
            for (int i = 0; i < k; i++){
                double A = ALikelihood(node, i);
                double B = BLikelihood(node, i);
                sum += (B * p) / (A * (1 - p) + B * p);
            }

            p = sum / k;

            for (int i = 0; i < k; i++){
                double A = ALikelihood(node, i);
                double B = BLikelihood(node, i);
                prob *= A * p + B * (1 - p);
            }

        }


        double length = -Math.log(1 - p);
        double change = Math.abs(length - node.distance);
        node.distance = length;

        return change > THRESHOLD;
    }

    static double ALikelihood(Tree.Node node, int index){
        double sum = 0;

        if(node.children.size() == 0){
            if(index == bases.indexOf(node.value)) return 1;
            return 0;
        }

        for(int i = 0; i < 4; i++){
            double current = prior[i];
            for(Tree.Node child : node.children){
                current *= likelihood(child, i);
            }
            sum += current;
        }
        return sum;

    }

    static double BLikelihood(Tree.Node node, int index){

        double product = 1;

        if(node.children.size() == 0){
            if(index == bases.indexOf(node.value)) return 1;
            return 0;
        }

        for(Tree.Node child : node.children){
            double sum = 0;
            for(int i = 0; i < 4; i++){
//                iteriranje po indexu ili i?
                sum += prior[i] * likelihood(child, i);
            }
            product *= sum;
        }
        return product;

    }


    static double getLikelihood(Tree.Node root){
        double sum = 0;

        for(int i = 0; i < 4; i++){
            sum += prior[i] * likelihood(root, i);
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

    private static double probability(int i, int j, double distance){
        if(i == j) return Math.exp(-distance);
        return (1 - Math.exp(-distance)) * changeProbabilities[i][j];
    }
}
