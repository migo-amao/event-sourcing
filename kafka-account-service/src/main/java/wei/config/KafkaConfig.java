package wei.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import wei.domain.Order;
import wei.listener.OrderConsumer;
import wei.serdes.OrderDeserializer;

import java.util.*;

@Configuration
@EnableAsync
public class KafkaConfig {

    @Bean
    public KafkaConsumer<String, Order> kafkaConsumer() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "order-consumer-group");
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                OrderDeserializer.class);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "order.consumer.id." + UUID.randomUUID().toString());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 5_000);
        KafkaConsumer<String, Order> kafkaConsumer = new KafkaConsumer<String, Order>(props);
        return kafkaConsumer;
    }

    @Bean("Transactional")
    public KafkaProducer<String, String> kafkaProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProperties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);
        producerProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        producerProperties.put(ProducerConfig.LINGER_MS_CONFIG, 4000);
        producerProperties.put(ProducerConfig.CLIENT_ID_CONFIG, "order.id.producer." + UUID.randomUUID().toString());
        producerProperties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "tx.order.id.producer." + UUID.randomUUID().toString());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(producerProperties);
        kafkaProducer.initTransactions();
        return kafkaProducer;
    }
}
