package wei.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import wei.domain.Order;

import java.util.Map;

public class OrderDeserializer implements Deserializer<Order> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public Order deserialize(String topic, byte[] bytOrder) {
        ObjectMapper mapper = new ObjectMapper();
        Order order = null;
        try {
            order = mapper.readValue(bytOrder, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    @Override
    public void close() {

    }
}
