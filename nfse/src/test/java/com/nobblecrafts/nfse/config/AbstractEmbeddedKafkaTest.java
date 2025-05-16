package com.nobblecrafts.nfse.config;

import com.nobblecrafts.nfse.config.data.KafkaTestConfigData;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {"test-topic"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:0",
                "controller.quorum.voters.file.write=true",
                "auto.create.topics.enable=false"
        }
)
@ContextConfiguration
public abstract class AbstractEmbeddedKafkaTest<V extends SpecificRecordBase> {

    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    protected KafkaTestConfigData config;
    protected Consumer<String, V> consumer;

    @BeforeEach
    void setUp() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroup");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://localhost");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("test-topic"));
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close(Duration.ofSeconds(1));
        }
    }
}
