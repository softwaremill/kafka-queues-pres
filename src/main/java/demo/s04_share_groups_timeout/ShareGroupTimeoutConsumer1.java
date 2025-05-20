package demo.s04_share_groups_timeout;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaShareConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class ShareGroupTimeoutConsumer1 {
    private static final Logger logger = LoggerFactory.getLogger(ShareGroupTimeoutConsumer1.class);

    public static void main(String[] args) throws InterruptedException {
        var props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group3");

        try (var consumer = new KafkaShareConsumer<String, String>(props)) {
            consumer.subscribe(List.of("topic3"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    logger.info("Received: " + record.value());
                }

                if (!records.isEmpty()) {
                    logger.info("And now sleeping, before commiting ...");
                    Thread.sleep(60 * 1000);
                }
            }
        }
    }
}
