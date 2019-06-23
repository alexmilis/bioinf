import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Likelihood {

    private static List<Character> bases = List.of('A', 'T', 'C', 'G');

    private static double[] prior = new double[]{0.25, 0.25, 0.25, 0.25};
    private static double[][] changeProbabilities = new double[][]{{0.8, 0.1, 0.1, 0.1},
                                                                    {0.1, 0.8, 0.1, 0.1},
                                                                    {0.1, 0.1, 0.8, 0.1},
                                                                    {0.1, 0.1, 0.1, 0.8}};

    private static final String STARTTREE = "( 0 : 1.0 , 1 : 1.0 );";

    private static final double THRESHOLD = 0.05;

    private static String first = "resources/first%d.fasta";

    private static List<List<Integer>> matrix;



    public static void main(String[] args) {
//        Path infile = Paths.get(String.format(random, g, h));
        Path infile = Paths.get(String.format(first, 10));

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

                for (int j = 0; j < line.length(); j++){
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

                for (Tree.Node child : tree.getRoot().children){
                    evaluateBranches(child, sites);
                }

                likelihoods.add(getLikelihood(tree.getRoot(), sites));
                trees.add(tree.toString());
            }

            bestTree = trees.get(likelihoods.indexOf(Collections.max(likelihoods)));
            System.out.println(bestTree);
            System.out.println(Collections.max(likelihoods));
        }


        System.out.println(bestTree);

        long time = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Time in millis: %d", time));
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

        double oldlikelihood = getLikelihood(node, k); //TODO PROVJERI JEL RADI

        double p = Math.exp(-node.distance);

        while (prob - oldlikelihood > THRESHOLD){
            oldlikelihood = prob;
            double sum = 0;
            for (int i = 0; i < k; i++){
                fillSites(node, k - 1);

                double sumA = 0, sumB = 0;
                for(int j = 0; j < 4; j++) {
                    sumA += ALikelihood(node, i, j);
                    sumB += BLikelihood(node, i, j);
                }
                sum += (sumB * p) / (sumA * (1 - p) + sumB * p);
            }

            p = sum / k;
            if (p <= 0){
                System.out.println("wtf2");
            }

            for (int i = 0; i < k; i++){
                double sumA = 0, sumB = 0;
                for(int j = 0; j < 4; j++) {
                    sumA += ALikelihood(node, i, j);
                    sumB += BLikelihood(node, i, j);
                }
                prob *= (sumA * p + sumB * (1 - p));
            }
        }

        double length = -Math.log(1 - p);
        double change = Math.abs(length - node.distance);
        if (length < 0){
            System.out.println("wtf");
        }
        node.distance = length;

        return change > THRESHOLD;
    }

    static double ALikelihood(Tree.Node node, int index, int base){
//        Todo ubaci bace sa indexa indeks
        if(node.children.size() == 0){
            if (node.value == -1) return 0;
            if (matrix.get(node.value).get(index) == base) return 1;        //TODO PROVJERI FORMULE
            return 0;
        }

        double current = prior[base];
        for(Tree.Node child : node.children){
            current *= ALikelihood(child, index, base);
        }
        return current;

    }

    static double BLikelihood(Tree.Node node, int index, int base){
        double product = 1;

        if(node.children.size() == 0){
            if (node.value == -1) return 0;
            if (matrix.get(node.value).get(index) == base) return 1;
            return 0;
        }

        for(Tree.Node child : node.children){
            product *= prior[base] * BLikelihood(child, index, base);
        }
        return product;

    }


    static double getLikelihood(Tree.Node root, int sites){
        double product = 1;


        for (int site = 0; site < sites; site++){
            fillSites(root, site);
            double sum = 0;
            for(int i = 0; i < 4; i++){
                sum += prior[i] * likelihood(root, i);
            }
            if (sum != 0) product *= sum;
        }

        return product;
    }

    static void fillSites(Tree.Node root, int site){
        if (root.children.size() == 0){
            root.base = matrix.get(root.value).get(site);
        }
        for (Tree.Node child : root.children){
            fillSites(child, site);
        }
    }

//  todo pretvori baze u brojeve kao indeksi u bases
    private static double likelihood(Tree.Node node, int index){
        double product = 1;

        if(node.children.size() == 0){
            if(index == node.base) return 1;
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
