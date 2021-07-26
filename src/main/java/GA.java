import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GA {

    Knapsack knapsack;
    int nIndividuals;
    int nGenes;
    int nGenerations;
    double crossoverRate;
    double mutationRate;
    double elitismRate;

    Random random;

    GA(Knapsack knapsack, int nIndividuals, int nGenerations,
    double crossoverRate, double mutationRate, double elitismRate) {
        
        this.knapsack = knapsack;
        this.nIndividuals = nIndividuals;
        this.nGenes = knapsack.weights.length;
        this.nGenerations = nGenerations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismRate = elitismRate;

        random = new Random();

    }

    Individual[] rouletteSelection(Population p) {
        int skip = -1;
        Individual[] selected = new Individual[2];
        // Loop twice to select two individuals.
        for (int i = 0; i < 2; i++) {
            double tf = 0;
            // Calcuate total fitness.
            for (int j = 0; j < nIndividuals; j++) {
                if (j == skip) {
                    continue;
                }
                tf += p.individuals[j].fitness;
            }
            double cf = 0;
            double r = random.nextDouble();
            for (int j = 0; j < nIndividuals; j++) {
                if (j == skip) {
                    continue;
                }
                // Prevent divding by zero.
                if (tf == 0) {
                    // Reduces to random selection.
                    cf += 1.0 / (p.individuals.length - i);
                } else {
                    // Else, select based on cumulative fitness.
                    cf += p.individuals[j].fitness / tf;
                }
                if (r < cf) {
                    selected[i] = new Individual(p.individuals[j]);
                    skip = j;
                    break;
                }
            }
        }
        return selected;
    }

    void singlePointCrossover(Individual[] selected) {
        int crossoverPoint = random.nextInt(this.nGenes - 1) + 1;
        for (int i = 0; i < crossoverPoint; i++) {
            int temp = selected[0].genes[i];
            selected[0].genes[i] = selected[1].genes[i];
            selected[1].genes[i] = temp;
        }
    }

    Population recombination(Population p) {
        double worst = p.individuals[this.nIndividuals - 1].fitness;

        for (int i = 0; i < this.nIndividuals; i++) {
            p.individuals[i].fitness -= worst;
        }

        Population new_p = new Population();

        for (int i = 0; i < this.nIndividuals; i += 2) {
            Individual[] selected = rouletteSelection(p);
            double r = random.nextDouble();
            if (r < crossoverRate) {
                singlePointCrossover(selected);
            }
            for (int j = 0; j < 2; j++) {
                if (this.nIndividuals % 2 == 1 && i == this.nIndividuals - 1 && 
                j == 2) {
                    break;
                }
                new_p.individuals[i + j] = selected[j];
            }
        }
        return new_p;
    }

    void mutation(Population p) {
        for (int i = 0; i < this.nIndividuals; i++) {
            for (int j = 0; j < this.nGenes; j++) {
                double r = random.nextDouble();
                if (r < this.mutationRate) {
                    if (p.individuals[i].genes[j] == 0) {
                        p.individuals[i].genes[j] = 1;
                    } else {
                        p.individuals[i].genes[j] = 0;
                    }
                }
            }
        }
    }

    void elitism(Population p, Population new_p) {
        int nElites = (int) Math.ceil(this.elitismRate * this.nIndividuals);
        for (int i = nElites; i < this.nIndividuals; i++) {
            p.individuals[i] = new_p.individuals[i - nElites];
        }
    }

    void updateFitness(Population p) {
        double tf = 0;
        for (int i = 0; i < this.nIndividuals; i++) {
            p.individuals[i].fitness = knapsack.objective(p.individuals[i].genes);
            tf += p.individuals[i].fitness;
        }
        p.sort();
        p.avgFitness = tf / this.nIndividuals;        
    }

    Individual run() {
        Population p = new Population();
        p.initialize();

        for (int i = 0; i < this.nGenerations; i++) {
            Population new_p = recombination(p);
            mutation(new_p);
            updateFitness(new_p);
            elitism(p, new_p);
            updateFitness(p);
        }
        return p.individuals[0];
    }

    class Individual implements Comparable<Individual> {

        int[] genes;
        double fitness;

        // Generate an new individual.
        Individual() {
            this.genes = new int[nGenes];
            for (int i = 0; i < nGenes; i++) {
                this.genes[i] = random.nextInt(2);
            }
            this.fitness = knapsack.objective(this.genes);
        }

        // Clone another individual.
        Individual(Individual other) {
            this.genes = other.genes.clone();
            this.fitness = other.fitness;
        }

        @Override
        public int compareTo(Individual ind) {
            if (this.fitness < ind.fitness) {
                return -1;
            } else if (ind.fitness < this.fitness) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    class Population {

        Individual[] individuals;
        double avgFitness;

        // Empty population.
        Population() {
            this.individuals = new Individual[nIndividuals];
            this.avgFitness = 0;
        }

        // Clone another population.
        Population(Population other) {
            this.individuals = new Individual[nIndividuals];
            for (int i = 0; i < nIndividuals; i++) {
                this.individuals[i] = new Individual(other.individuals[i]);
            }
            this.avgFitness = other.avgFitness;
        }

        void initialize() {
            double tf = 0;
            for (int i = 0; i < nIndividuals; i++) {
                Individual ind = new Individual();
                this.individuals[i] = ind;
                tf += ind.fitness;
            }
            sort();
            this.avgFitness = tf / nIndividuals;
        }

        void sort() {
            Arrays.sort(this.individuals, Collections.reverseOrder());
        }

    }
    
}
