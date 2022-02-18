import java.util.ArrayList;
import java.util.List;

public class EndpointStats {
    private List<EndpointRecord> endpointStats = new ArrayList<>();

    public EndpointStats(EndpointRecord newRecord) {
        this.endpointStats.add(newRecord);
    }
}
