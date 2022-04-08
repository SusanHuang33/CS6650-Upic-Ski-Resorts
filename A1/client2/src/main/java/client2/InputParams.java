package client2;

public class InputParams {
    private static final int MIN_NUMTHREADS = 10;
    private static final int MAX_NUMTHREADS = 1024;
    private static final int MAX_NUMSKIERS = 100000;
    private static final int DEFAULT_NUMLIFTS = 40;
    private static final int MIN_NUMLIFTS = 5;
    private static final int MAX_NUMLIFTS = 60;
    private static final int DEFAULT_NUMRUNS = 10;
    private static final int MAX_NUMRUNS = 20;

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

//    private int validateNumThreads(int inputNumThreads) {
//        if (inputNumThreads == null || inputNumThreads < MIN_NUMTHREADS || inputNumThreads > MAX_NUMTHREADS) {
//
//        }
//    }

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
