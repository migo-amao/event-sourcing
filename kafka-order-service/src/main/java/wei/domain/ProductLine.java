package wei.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductLine {
    private String productId;
    private int quantity;
}