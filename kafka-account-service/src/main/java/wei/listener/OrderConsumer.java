package wei.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import wei.domain.Order;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class OrderConsumer implements ApplicationContextAware {

    @Autowired
    private KafkaConsumer<String, Order> kafkaConsumer;

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    private ApplicationContext applicationContext;

    private static AtomicBoolean forceAbortTransaction = new AtomicBoolean(false);
    private AtomicBoolean hasSlept = new AtomicBoolean(false);

    @Async
    @EventListener
    public void applicationReady(ApplicationReadyEvent event) {
        kafkaConsumer.subscribe(Collections.singleton("order-new"), new OrderConsumer.OrderConsumerRebalanceListener());
        while (true) {
            log.info("OrderAccount::Before polling records");
            ConsumerRecords<String, Order> records = kafkaConsumer.poll(Duration.ofSeconds(Integer.MAX_VALUE));

            if (records.isEmpty()) continue;

            List<String> orderIds = new ArrayList<>();

            for (ConsumerRecord<String, Order> record : records) {
                Order order = record.value();
                String key = record.key();
                int partition = record.partition();
                long offset = record.offset();
                log.info("OrderAccount::Received order in group 'order-account-consumer-group': orderId = {}, key = {}, partition = {}, offset = {}", order.getId(), key, partition, offset);
                orderIds.add(order.getId());
            }

            Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
            for (TopicPartition partition : records.partitions()) {
                List<ConsumerRecord<String, Order>> partitionedRecords = records.records(partition);
                long offset = partitionedRecords.get(partitionedRecords.size() - 1).offset();
                offsetsToCommit.put(partition, new OffsetAndMetadata(offset + 1));
            }

            try {
                boolean abort = false, exit = false;
                kafkaProducer.beginTransaction();
                for (String orderId : orderIds) {
                    kafkaProducer.send(new ProducerRecord<>("order-id", orderId, orderId));
                    if (orderId.contains("abort")) {
                        abort = true;
                    } else if(orderId.contains("exit")) {
                        exit = true;
                    } else {
                        abort = exit = false;
                        //if(!orderId.endsWith("-") && hasSlept.compareAndSet(false, true)) {
                            int index = orderId.lastIndexOf("-");
                            try {
                                int sleepTime = Integer.parseInt(orderId.substring(index + 1));
                                log.info("OrderAccount::Sleeping...");
                                Thread.sleep(sleepTime);
                            } catch (Exception e) {
                                log.error("OrderAccount::Consumer sleep time failed: {}", orderId);
                            }
                        //}
                    }
                }
                kafkaProducer.sendOffsetsToTransaction(offsetsToCommit, "order-consumer-group");
                if (abort) {
                    log.info("OrderAccount::Order id aborted.");
                    kafkaProducer.abortTransaction();
                } else if(forceAbortTransaction.get()) {
                    log.info("OrderAccount::Order id forced aborted.");
                    kafkaProducer.abortTransaction();
                } else if(exit) {
                    log.info("OrderAccount::Exit application.");
                    break;
                } else {
                    log.info("OrderAccount::Order id committed.");
                    kafkaProducer.commitTransaction();
                }
            } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
                // We can't recover from these exceptions, so our only option is to close the producer and exit.
                log.error("OrderAccount::Sending order id failed fatally: ", e);
                kafkaProducer.close();
                break;
            } catch (KafkaException e) {
                // For all other exceptions, just abort the transaction and try again.
                log.error("OrderAccount::Sending order id failed: ", e);
                kafkaProducer.abortTransaction();
            } catch (Exception e) {
                log.error("OrderAccont::general error: ", e);
                kafkaProducer.abortTransaction();
            }
        }
        log.info("OrderAccount::Exiting application...");
        int errCode = SpringApplication.exit(applicationContext, () -> 0);
        System.exit(errCode);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static class OrderConsumerRebalanceListener implements ConsumerRebalanceListener {

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> collection) {
            //forceAbortTransaction.set(true);
            log.info("OrderAccount::Rebalance::partition revoked.");
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> collection) {
            log.info("OrderAccount::Rebalance::partition assigned.");
        }
    }
}