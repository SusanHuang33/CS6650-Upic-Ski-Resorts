public class EndpointRecord {
    private String URL;
    private String operation;
    private Integer mean;
    private Integer max;

    public EndpointRecord(String URL, String operation, Integer mean, Integer max) {
        this.URL = URL;
        this.operation = operation;
        this.mean = mean;
        this.max = max;
    }
}
