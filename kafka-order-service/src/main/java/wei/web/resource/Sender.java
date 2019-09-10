package wei.web.resource;

/*import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import wei.domain.Order;

import java.util.Collections;
import java.util.Date;

@Transactional(transactionManager = "txManager")
@Service
@Slf4j
public class Sender {

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    public void send(boolean abortOrder, boolean abortOrderId, Integer[] ids) {
        for (int id : ids) {
            Order order = Utils.createOrder(id);
            order.setId(order.getId() + "-" + abortOrderId);
            log.info("About to send order: {}", order.getId());
            ListenableFuture<SendResult<String, Order>> result = kafkaTemplate.send("order-new", order.getId(), order);
            result.addCallback(sendResult -> {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                log.info("Order sent successfully: orderId = {}, partition = {}, offset = {}", order.getId(), metadata.partition(), metadata.offset());
            }, error -> {
                log.error("Order sent error: ", error);
            });
            log.info("Order sent: {}", order.getId());
        }
        if (abortOrder) {
            throw new RuntimeException("Transaction aborted for sending order.");
        }
    }
}*/
