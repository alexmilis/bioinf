import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Likelihood3 {

    private static List<Character> bases = List.of('A', 'T', 'C', 'G');

    private static double[] prior = new double[]{0.25, 0.25, 0.25, 0.25};
    private static double[][] changeProbabilities = new double[][]{{0.8, 0.1, 0.1, 0.1},
            {0.1, 0.8, 0.1, 0.1},
            {0.1, 0.1, 0.8, 0.1},
            {0.1, 0.1, 0.1, 0.8}};

    private static final String STARTTREE = "( 0 : 1.0 , 1 : 1.0 );";

    private static final double THRESHOLD = 0.05;

    private static String first = "resources/first%d.fasta";
    private static String random = "../resources/random_data/prolazak%d/random%d.fasta";

    private static String resultTreeFile = "results/prolazak%d/tree%d.txt";

    private static List<List<Integer>> matrix;

    public static void main(String[] args) {
        for (int g = 1; g < 11; g++) {
            for (int h = 5; h < 11; h++) {
//                Path infile = Paths.get(String.format("resources/first%d.fasta", h));
                Path infile = Paths.get(String.format(random, g, h));

                matrix = new ArrayList<>();
                Map<Integer, String> names = new HashMap<>();

                System.out.println("Reading file: " + infile);

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(infile.toString()));
                    String line = reader.readLine();

                    int i = 0;
                    while (line != null) {
                        names.put(i++, line.substring(1));
                        line = reader.readLine();
                        List<Integer> row = new ArrayList<>();

                        for (int j = 0; j < line.length(); j++) {
                            row.add(bases.indexOf(line.charAt(j)));
                        }
                        matrix.add(row);
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

                for (int i = 2; i < numberOfTrees; i++) {
                    List<String> trees = new ArrayList<>();
                    List<Double> likelihoods = new ArrayList<>();

                    for (int j = 0; j < i; j++) {
                        String dist = getDistString(bestTree, j);
                        String newtree = bestTree.replaceFirst(String.format(" %d : %s ", j, dist), String.format(" ( %d : 1.0 , %d : 1.0 ) : %s", j, i, dist));
                        Tree tree = Tree.parse(newtree);

                        for (Tree.Node child : tree.getRoot().children) {
                            evaluateBranches(child, sites);
                        }
                        likelihoods.add(getLikelihood(tree.getRoot(), sites));
                        trees.add(tree.toString());
                    }

                    bestTree = trees.get(likelihoods.indexOf(Collections.min(likelihoods)));

                }


                System.out.println(bestTree);

                long time = System.currentTimeMillis() - startTime;
                System.out.println(String.format("Time in millis: %d", time));


                try {
                    String tree = bestTree;
                    for (int k = 0; k < matrix.size(); k++) {
                        tree = tree.replace(String.format(" %d ", k), String.format(" %s ", names.get(k)));
                    }
                    System.out.println(tree);

                    OutputStream out = new BufferedOutputStream(Files.newOutputStream(
                            Paths.get(String.format(resultTreeFile, g, h)), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
                    out.write(tree.getBytes());
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                    System.exit(1);
                }

            }
        }
    }

    private static String getDistString(String bestTree, int j) {
        String str = String.format(" %d : ", j);
        int index = bestTree.indexOf(str) + str.length();
        int index2 = bestTree.indexOf(" ", index);
        return bestTree.substring(index, index2).strip();
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
        double oldlikelihood = getLikelihood(node, k);
        double p = Math.exp(-node.distance);

        while (Math.abs(prob - oldlikelihood)> THRESHOLD){
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
            if (node.value == -1) return 1;
            int base = matrix.get(node.value).get(index);
            if (base == -1) return 1;
            return 1 * prior[base];
        }

        for(int i = 0; i < 4; i++){
            double current = prior[i];
            for(Tree.Node child : node.children){
                current *= ALikelihood(child, index);
            }
            sum += current;
        }
        return sum;
    }

    static double BLikelihood(Tree.Node node, int index){
        double product = 1;

        if(node.children.size() == 0){
            if (node.value == -1) return 0;
            int base = matrix.get(node.value).get(index);
            if (base == -1) return 0;
            return 1;
        }

        for(Tree.Node child : node.children){
            double sum = 0;
            for(int i = 0; i < 4; i++){
                sum += prior[i] * BLikelihood(child, i);
            }
            product *= sum;
        }
        return product;
    }


    static double getLikelihood(Tree.Node root, int sites){
//        double product = 1;
        int zeros = 0;

        for (int site = 0; site < sites; site++){
            fillSites(root, site);
            double sum = 0;
            for(int i = 0; i < 4; i++){
                sum += prior[i] * likelihood(root, i);
            }
            if (sum != 0){
                if (sum < 1){
                    zeros += getZeros(sum);
//                    product *= sum * 10;
                } else {
//                    product *= sum;
                }
            }
        }

        return zeros;
//        return product;
    }

    private static int getZeros(double sum) {
        String number = Double.toString(sum);
        for (int h = 2; h < number.length(); h++){
            if (number.charAt(h) != '0'){
                return h - 1;
            }
        }
        return number.length();
    }

    static void fillSites(Tree.Node root, int site){
        if (root.children.size() == 0){
            root.base = matrix.get(root.value).get(site);
        }
        for (Tree.Node child : root.children){
            fillSites(child, site);
        }
    }


    private static double likelihood(Tree.Node node, int index){
        double product = 1;

        if(node.children.size() == 0){
            if(index == node.base) return 1;
            return 0;
        }

        for(Tree.Node child : node.children){
            double sum = 0;
            for(int i = 0; i < 4; i++){
                sum += probability(index, i, child.distance) * likelihood(child, i);
            }
            if (sum != 0) {
                product *= sum;
            }
        }
        return product;
    }

    private static double probability(int i, int j, double distance){
        if(i == j) return Math.exp(-distance);
        return (1 - Math.exp(-distance)) * changeProbabilities[i][j];
    }
}
