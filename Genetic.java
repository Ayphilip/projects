
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Genetic {
    private double crossoverRate; // The probability of performing crossover
    private double mutationRate; // The probability of performing mutation
    private int tournamentSize; // The size of the tournament for selection
    private FitnessEvaluator evaluator; // The fitness evaluator
    private Random random; // The random number generator

    public Genetic(double crossoverRate, double mutationRate, int tournamentSize, FitnessEvaluator evaluator) {
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.tournamentSize = tournamentSize;
        this.evaluator = evaluator;
        this.random = new Random();
    }

    public static void main(String[] args) {
        double[][] coverageMap = { { 0.8, 0.7, 0.4, 0.1 }, { 0.7, 0.9, 0.6, 0.2 }, { 0.5, 0.6, 0.8, 0.3 },
                { 0.3, 0.4, 0.7, 0.5 } };
        double[][] capacityMap = { { 30.0, 40.0, 50.0, 60.0 }, { 40.0, 50.0, 60.0, 70.0 }, { 50.0, 60.0, 70.0, 80.0 },
                { 60.0, 70.0, 80.0, 90.0 } };
        int numBaseStations = 4;

        // Initialize the genetic algorithm parameters
        int populationSize = 50;
        double crossoverRate = 0.8;
        double mutationRate = 0.2;
        int tournamentSize = 5;
        double alpha = 0.5;
        double beta = 0.5;

        // Create the initial population
        List<BaseStationPlacementSolution> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            BaseStationPlacementSolution solution = new BaseStationPlacementSolution(numBaseStations);
            for (int j = 0; j < numBaseStations; j++) {
                int x = ThreadLocalRandom.current().nextInt(coverageMap.length);
                int y = ThreadLocalRandom.current().nextInt(coverageMap[0].length);
                solution.setX(j, x);
                solution.setY(j, y);
            }
            population.add(solution);
        }

        // Create the fitness evaluator and genetic operators
        FitnessEvaluator evaluator = new FitnessEvaluator(coverageMap, capacityMap, alpha, beta);
        Genetic genetic = new Genetic(crossoverRate, mutationRate, tournamentSize, evaluator);

        // Run the genetic algorithm
        int numIterations = 100;
        for (int i = 1; i <= numIterations; i++) {
            // Evaluate the fitness of the population
            List<double[]> fitnessValues = new ArrayList<>();
            for (BaseStationPlacementSolution solution : population) {
                double[] fitness = evaluator.evaluate(solution);
                fitnessValues.add(fitness);
            }

            // Select the parents for reproduction
            List<BaseStationPlacementSolution> parents = new ArrayList<>();
            for (int j = 0; j < populationSize / 2; j++) {
                BaseStationPlacementSolution parent1 = genetic.select(population);
                BaseStationPlacementSolution parent2 = genetic.select(population);
                parents.add(parent1);
                parents.add(parent2);
            }

            // Create the offspring by crossover and mutation
            List<BaseStationPlacementSolution> offspring = new ArrayList<>();
            for (int j = 0; j < populationSize / 2; j++) {
                BaseStationPlacementSolution parent1 = parents.get(j * 2);
                BaseStationPlacementSolution parent2 = parents.get(j * 2 + 1);
                BaseStationPlacementSolution child = genetic.crossover(parent1, parent2);
                genetic.mutate(child);
                offspring.add(child);
            }
            List<BaseStationPlacementSolution> combinedPopulation = new ArrayList<>(population);
            combinedPopulation.addAll(offspring);

            // Evaluate the fitness of the combined population
            List<double[]> combinedFitnessValues = new ArrayList<>();
            for (BaseStationPlacementSolution solution : combinedPopulation) {
                double[] fitness = evaluator.evaluate(solution);
                combinedFitnessValues.add(fitness);
            }

            // Select the survivors
            population = genetic.selectSurvivors(combinedPopulation, combinedFitnessValues, populationSize);
        }

        // Print the best solution found
        BaseStationPlacementSolution bestSolution = Collections.max(population,
                Comparator.comparingDouble(s -> evaluator.evaluate(s)[0]));

        System.out.println("Best solution found: " + bestSolution);
        System.out.println("Fitness: " + Arrays.toString(evaluator.evaluate(bestSolution)));

    }

    private List<BaseStationPlacementSolution> selectSurvivors(List<BaseStationPlacementSolution> combinedPopulation,
            List<double[]> combinedFitnessValues, int populationSize) {
        List<BaseStationPlacementSolution> survivors = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            List<BaseStationPlacementSolution> tournament = new ArrayList<>();
            List<double[]> tournamentFitnessValues = new ArrayList<>();
            for (int j = 0; j < tournamentSize; j++) {
                int index = ThreadLocalRandom.current().nextInt(combinedPopulation.size());
                tournament.add(combinedPopulation.get(index));
                tournamentFitnessValues.add(combinedFitnessValues.get(index));
            }
            BaseStationPlacementSolution winner = tournament.get(0);
            double[] winnerFitness = tournamentFitnessValues.get(0);
            for (int j = 1; j < tournamentSize; j++) {
                double[] fitness = tournamentFitnessValues.get(j);
                if (fitness[0] > winnerFitness[0]) {
                    winner = tournament.get(j);
                    winnerFitness = fitness;
                }
            }
            survivors.add(winner);
        }
        return survivors;
    }

    public BaseStationPlacementSolution crossover(BaseStationPlacementSolution parent1,
            BaseStationPlacementSolution parent2) {
        BaseStationPlacementSolution child = new BaseStationPlacementSolution(parent1.getNumBaseStations());

        // Perform uniform crossover
        for (int i = 0; i < parent1.getNumBaseStations(); i++) {
            if (random.nextDouble() < crossoverRate) {
                child.setX(i, parent2.getX(i));
                child.setY(i, parent2.getY(i));
            } else {
                child.setX(i, parent1.getX(i));
                child.setY(i, parent1.getY(i));
            }
        }

        return child;
    }

    public void mutate(BaseStationPlacementSolution solution) {
        // Perform random mutation
        for (int i = 0; i < solution.getNumBaseStations(); i++) {
            if (random.nextDouble() < mutationRate) {
                int x = random.nextInt(solution.getX(i));
                int y = random.nextInt(solution.getY(i));
                solution.setX(i, x);
                solution.setY(i, y);
            }
        }
    }

    public BaseStationPlacementSolution select(List<BaseStationPlacementSolution> population) {
        // Perform tournament selection
        BaseStationPlacementSolution best = null;
        for (int i = 0; i < tournamentSize; i++) {
            BaseStationPlacementSolution candidate = population.get(random.nextInt(population.size()));
            if (best == null || evaluator.evaluate(candidate)[0] > evaluator.evaluate(best)[0]) {
                best = candidate;
            }
        }
        return best;
    }

}