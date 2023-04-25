public class FitnessEvaluator {
    private double[][] coverageMap; // A 2D array representing the coverage of the 5G network
    private double[][] capacityMap; // A 2D array representing the capacity of the 5G network
    private double alpha; // A weighting factor for the coverage objective
    private double beta; // A weighting factor for the capacity objective

    public FitnessEvaluator(double[][] coverageMap, double[][] capacityMap, double alpha, double beta) {
        this.coverageMap = coverageMap;
        this.capacityMap = capacityMap;
        this.alpha = alpha;
        this.beta = beta;
    }

    public double[] evaluate(BaseStationPlacementSolution solution) {
        double[] objectives = new double[2];

        // Evaluate the coverage objective
        double coverage = 0.0;
        for (int i = 0; i < solution.getNumBaseStations(); i++) {
            int x = solution.getX(i);
            int y = solution.getY(i);

            coverage += coverageMap[x][y];
        }
        objectives[0] = coverage / solution.getNumBaseStations();

        // Evaluate the capacity objective
        double capacity = 0.0;
        for (int i = 0; i < solution.getNumBaseStations(); i++) {
            int x = solution.getX(i);
            int y = solution.getY(i);

            capacity += capacityMap[x][y];
        }
        objectives[1] = capacity / solution.getNumBaseStations();

        // Combine the objectives using a weighted sum approach
        double fitness = alpha * objectives[0] + beta * objectives[1];

        return new double[] { fitness, objectives[0], objectives[1] };
    }
}