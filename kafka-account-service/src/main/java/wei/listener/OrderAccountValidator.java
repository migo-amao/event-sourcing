package wei.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
/*
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import wei.domain.Order;

@Service
@Slf4j
public class OrderAccountValidator {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(id="order-new-gropu", topics = "order-new")
    public void listen(Order order,
                       @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received order in group 'order-account-consumer-group': orderId = {}, key = {}, partition = {}, offset = {}", order.getId(), key, partition, offset);

        log.info("Processing the order ...");

        log.info("Send the result to next pipeline");

        ListenableFuture<SendResult<String, String>> result = kafkaTemplate.send("order-id", order.getId(), order.getId());
        result.addCallback(sendResult -> {
            RecordMetadata metadata = sendResult.getRecordMetadata();
            log.info("Order id sent successfully: orderId = {}, partition = {}, offset = {}", order.getId(), metadata.partition(), metadata.offset());
        }, error -> {
            log.error("Order id sent error: ", error);
        });
        log.info("Order id sent: {}", order.getId());

        if(order.getId().contains("true")) {
            throw new RuntimeException("Error occurred after sending the order id = " + order.getId());
        }
    }

    @KafkaListener(id = "order-id-group", topics = "order-id")
    public void orderidlistener(String orderId,
                                @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received order id in group 'order-id-group': orderId = {}, key = {}, partition = {}, offset = {}", orderId, key, partition, offset);
    }

}*/
