package AI_Assignment;
import java.util.*;

public class Main3 {
        static final int N = 51;

        static final int POPULATION_SIZE = 200;
        static final int GENERATIONS = 1000;
        static final double MUTATION_RATE = 0.05;
        static final int TOURNAMENT_SIZE = 5;

        static final long RANDOM_SEED = 42;
        static final Random random = new Random(RANDOM_SEED);

        // Distance matrix
        static int[][] graph;

        // Store the best solution found
        static int[] bestTour;
        static long bestCost = Long.MAX_VALUE;

        // Generating a complete, undirected weighted graph
        static void generateGraph() {
            graph = new int[N][N];

            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    int distance = random.nextInt(91) + 10;

                    graph[i][j] = distance;
                    graph[j][i] = distance;
                }
            }
        }
        static long calculateCost(int[] tour) {
            long cost = 0;

            for (int i = 0; i < N - 1; i++) {
                cost += graph[tour[i]][tour[i + 1]];
            }

            cost += graph[tour[N - 1]][tour[0]];

            return cost;
        }

        // Create a random permutation of all cities
        static int[] createRandomTour() {
            int[] tour = new int[N];

            for (int i = 0; i < N; i++) {
                tour[i] = i;
            }

            // Fisher-Yates shuffle
            for (int i = N - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);

                int temp = tour[i];
                tour[i] = tour[j];
                tour[j] = temp;
            }

            return tour;
        }

        // Initialize the population
        static int[][] initializePopulation() {
            int[][] population =
                    new int[POPULATION_SIZE][N];

            for (int i = 0; i < POPULATION_SIZE; i++) {
                population[i] = createRandomTour();
            }

            return population;
        }


        static int[] selectParent(int[][] population) {
            int bestIndex = random.nextInt(POPULATION_SIZE);
            long lowestCost =
                    calculateCost(population[bestIndex]);

            for (int i = 1; i < TOURNAMENT_SIZE; i++) {
                int candidateIndex =
                        random.nextInt(POPULATION_SIZE);

                long candidateCost =
                        calculateCost(population[candidateIndex]);

                if (candidateCost < lowestCost) {
                    lowestCost = candidateCost;
                    bestIndex = candidateIndex;
                }
            }

            return population[bestIndex].clone();
        }
        static int[] crossover(int[] parent1, int[] parent2) {
            int[] child = new int[N];
            Arrays.fill(child, -1);

            int left = random.nextInt(N);
            int right = random.nextInt(N);

            if (left > right) {
                int temp = left;
                left = right;
                right = temp;
            }

            boolean[] used = new boolean[N];

            // Copy a segment from parent 1
            for (int i = left; i <= right; i++) {
                child[i] = parent1[i];
                used[parent1[i]] = true;
            }

            // Fill remaining positions in parent 2's order
            int position = (right + 1) % N;

            for (int k = 0; k < N; k++) {
                int city = parent2[(right + 1 + k) % N];

                if (!used[city]) {
                    child[position] = city;
                    used[city] = true;

                    position = (position + 1) % N;
                }
            }

            return child;
        }

        // Swap mutation
        static void mutate(int[] tour) {
            if (random.nextDouble() < MUTATION_RATE) {
                int i = random.nextInt(N);
                int j = random.nextInt(N);

                while (i == j) {
                    j = random.nextInt(N);
                }

                int temp = tour[i];
                tour[i] = tour[j];
                tour[j] = temp;
            }
        }

        // Main Genetic Algorithm
        static void solveTSP() {
            int[][] population = initializePopulation();

            bestTour = population[0].clone();
            bestCost = calculateCost(bestTour);

            System.out.println("Initial best cost: " + bestCost);

            for (int generation = 1;
                 generation <= GENERATIONS;
                 generation++) {

                int[][] newPopulation =
                        new int[POPULATION_SIZE][N];

                // Elitism: preserve the best tour
                int eliteIndex = 0;
                long eliteCost = calculateCost(population[0]);

                for (int i = 1; i < POPULATION_SIZE; i++) {
                    long cost = calculateCost(population[i]);

                    if (cost < eliteCost) {
                        eliteCost = cost;
                        eliteIndex = i;
                    }
                }

                newPopulation[0] =
                        population[eliteIndex].clone();

                int nextIndex = 1;

                // Generate the next population
                while (nextIndex < POPULATION_SIZE) {
                    int[] parent1 = selectParent(population);
                    int[] parent2 = selectParent(population);

                    int[] child1 = crossover(parent1, parent2);
                    mutate(child1);

                    newPopulation[nextIndex] = child1;
                    nextIndex++;

                    if (nextIndex < POPULATION_SIZE) {
                        int[] child2 = crossover(parent2, parent1);
                        mutate(child2);

                        newPopulation[nextIndex] = child2;
                        nextIndex++;
                    }
                }

                population = newPopulation;

                // Update the best solution found so far
                for (int i = 0; i < POPULATION_SIZE; i++) {
                    long cost = calculateCost(population[i]);

                    if (cost < bestCost) {
                        bestCost = cost;
                        bestTour = population[i].clone();
                    }
                }

                if (generation % 100 == 0) {
                    System.out.println(
                            "Generation: " + generation
                                    + ", Best cost: " + bestCost);
                }
            }

            System.out.println("\n===== FINAL RESULT =====");
            System.out.println("Number of cities: " + N);
            System.out.println("Generations: " + GENERATIONS);
            System.out.println("Population size: " + POPULATION_SIZE);
            System.out.println("Best tour cost: " + bestCost);

            System.out.print("Best tour: ");

            for (int city : bestTour) {
                System.out.print(city + " -> ");
            }

            // Return to the starting city
            System.out.println(bestTour[0]);
        }

         public static void main(String[] args) {
            System.out.println(
                    "TSP using Genetic Algorithm");

            System.out.println("Generating graph...");
            generateGraph();
            System.out.println("Graph generated.");

            solveTSP();
        }
    }

