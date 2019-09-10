package wei.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String id;
    private long timeStamp;
    private List<ProductLine> productLines;
}