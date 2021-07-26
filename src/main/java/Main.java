public class Main {

    public static void main(String[] args) {
        int nIndividuals = 20;
        int nGenerations = 1000;
        double crossoverRate = 0.85;
        double mutationRate = 0.03;
        double elitismRate = 0.05;

        int nDatasets = 15;
        int nRuns = 30;

        for (int i = 1; i < nDatasets + 1; i++) {
            Knapsack k = new Knapsack(i);
            double[] best = new double[nRuns];
            for (int j = 0; j < nRuns; j++) {
                GA ga = new GA(k, nIndividuals, nGenerations, crossoverRate, mutationRate, elitismRate);
                GA.Individual b = ga.run();
                best[j] = b.fitness;
            }
            double[] result = meanBestWorst(best);
            System.out.println("Dataset: " + i + " Optimum: " + k.optimum + 
            " Mean: " + result[0] + " Best: " + result[1] + " Worst: " + result[2]);
        }
    }

    static double[] meanBestWorst(double[] x) {
        double best = x[0];
        double worst = x[0];
        double total = 0;
        for (int i = 0; i < x.length; i++) {
            total += x[i];
            if (best < x[i]) {
                best = x[i];
            }
            if (worst > x[i]) {
                worst = x[i];
            }
        }
        return new double[]{total / x.length, best, worst};
    }
}