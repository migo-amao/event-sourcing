package wei.web.resource;

import wei.domain.Order;
import wei.domain.ProductLine;

import java.util.Collections;
import java.util.Date;

public class Utils {

    public static ProductLine createProductLine(int id, int qty) {
        return new ProductLine("prod-id-" + id, qty);
    }

    public static Order createOrder(int id) {
        return new Order("order-id-" + id, new Date().getTime(), Collections.singletonList(createProductLine(id, id + 1)));
    }
}
