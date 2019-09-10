package wei.web.resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import wei.domain.Order;
import wei.serdes.OrderSerializer;

import java.util.Date;
import java.util.Properties;

@Service
@Slf4j
public class OrderSender {

    @Autowired
    @Qualifier("Tx")
    private KafkaProducer<String, Order> kafkaProducer;

    public void send(Integer[] ids, boolean abortOrder, String abortOrderId) {
        try {
            kafkaProducer.beginTransaction();
            for (int id : ids) {
                Order order = Utils.createOrder(id);
                order.setId(order.getId() + "-" + abortOrderId);
                log.info("About to send order: {}", order.getId());
                kafkaProducer.send(new ProducerRecord<>("order-new", 0, new Date().getTime(), order.getId(), order, null),
                        (metadata, error) -> {
                            if (error == null) {
                                log.info("Order sent successfully: orderId = {}, partition = {}, offset = {}", order.getId(), metadata.partition(), metadata.offset());
                            } else {
                                log.error("Order sent error: ", error);
                            }
                        });
                log.info("Order sent: {}", order.getId());
            }
            if (abortOrder) {
                log.info("Order aborted.");
                kafkaProducer.abortTransaction();
            } else {
                log.info("Order committed.");
                kafkaProducer.commitTransaction();
            }
        } catch (Exception e) {
            log.error("Sending order failed: ", e);
            try {
                kafkaProducer.abortTransaction();
            } catch (Exception ex) {
                log.error("Aborting transaction failed: ", ex);
            }
        }
    }
}