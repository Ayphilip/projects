
public class BaseStationPlacementSolution {
    private int[] x;
    private int[] y;

    public BaseStationPlacementSolution(int numBaseStations) {
        x = new int[numBaseStations];
        y = new int[numBaseStations];
    }

    public int getX(int index) {
        return x[index];
    }

    public void setX(int index, int value) {
        x[index] = value;
    }

    public int getY(int index) {
        return y[index];
    }

    public void setY(int index, int value) {
        y[index] = value;
    }

    public void mutate() {
        // ...
    }

    public int getNumBaseStations() {
        return 0;
    }

    public int getY() {
        return 0;
    }
}