package wei.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wei.domain.Order;
import wei.serdes.OrderSerializer;

import java.util.Properties;
import java.util.UUID;

@Configuration
public class KafkaConfig {

    private static final String UNIQUE_TOKEN = UUID.randomUUID().toString();
    private static final String ID_PREFIX = "order.producer.";
    private static final String TX_ID_TOKEN = "tx.";

    @Bean("Tx")
    public KafkaProducer<String, Order> txTafkaProducer() {
        Properties producerProperties = producerConfiguration();
        producerProperties.put(ProducerConfig.CLIENT_ID_CONFIG, ID_PREFIX + TX_ID_TOKEN + UNIQUE_TOKEN);
        producerProperties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, ID_PREFIX + TX_ID_TOKEN + UNIQUE_TOKEN);
        KafkaProducer<String, Order> kafkaProducer = new KafkaProducer<String, Order>(producerProperties);
        kafkaProducer.initTransactions();
        return kafkaProducer;
    }

    @Bean("Non-TX")
    public KafkaProducer<String, Order> kafkaProducer() {
        Properties producerProperties = producerConfiguration();
        producerProperties.put(ProducerConfig.CLIENT_ID_CONFIG, ID_PREFIX + UNIQUE_TOKEN);
        return new KafkaProducer<String, Order>(producerProperties);
    }

    private Properties producerConfiguration() {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, OrderSerializer.class);
        producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProperties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);
        producerProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        producerProperties.put(ProducerConfig.LINGER_MS_CONFIG, 4000);
        return producerProperties;
    }
}
