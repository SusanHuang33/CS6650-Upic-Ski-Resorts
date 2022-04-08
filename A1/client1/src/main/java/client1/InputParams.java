package client1;

public class InputParams {
    private int numThreads;
    private int numSkiers;
    private int numLifts;
    private int numRuns;
    private String serverUrl;

    public InputParams(int numThreads, int numSkiers, int numLifts, int numRuns, String server) {
        this.numThreads = numThreads;
        this.numSkiers = numSkiers;
        this.numLifts = numLifts;
        this.numRuns = numRuns;
        this.serverUrl = server;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public int getNumSkiers() {
        return numSkiers;
    }

    public int getNumLifts() {
        return numLifts;
    }

    public int getNumRuns() {
        return numRuns;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    @Override
    public String toString() {
        return "Input:" +
                "\nThe maximum number of threads to run (numThreads - min 10, max 1024): " + numThreads +
                "\nThe number of skier to generate lift rides for (numSkiers - max 100000): " + numSkiers +
                "\nThe number of ski lifts (numLifts - range 5-60, default 40):" + numLifts +
                "\nThe mean numbers of ski lifts each skier rides each day (numRuns - default 10, max 20):" + numRuns +
                "\nThe IP/port address of the server: " + serverUrl + "\n";
    }
}
