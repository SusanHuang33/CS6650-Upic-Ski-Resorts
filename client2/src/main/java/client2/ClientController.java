package client2;

import java.io.IOException;
import java.util.Scanner;

public class ClientController {

  public static void main(String[] args) throws InterruptedException, IOException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("1. Enter the maximum number of threads to run (numThreads - min 10, max 1024): ");
    int numThreads = scanner.nextInt();

    System.out.print("2. Enter the number of skier to generate lift rides for (numSkiers - max 100000): ");
    int numSkiers = scanner.nextInt();

    System.out.print("3. Enter the number of ski lifts (numLifts - range 5-60, default 40): ");
    int numLifts = scanner.nextInt();

    System.out.print("4. Enter the mean numbers of ski lifts each skier rides each day (numRuns - default 10, max 20): ");
    int numRuns = scanner.nextInt();

    System.out.print("5. Enter the IP/port address of the server "
            + "(default \"http://localhost:8080/A1_war_exploded/\"): ");
    String url = scanner.next();
    scanner.close();

    System.out.println();
    InputParams inputParams = new InputParams(numThreads, numSkiers, numLifts, numRuns, url);
    System.out.println(inputParams);

    System.out.println();
    SkiersClient clientPart2 = new SkiersClient(inputParams);
    clientPart2.PostNewLiftRide();

  }
}
