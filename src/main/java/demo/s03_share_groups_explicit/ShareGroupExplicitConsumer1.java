package demo.s03_share_groups_explicit;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class ShareGroupExplicitConsumer1 {
    public static void main(String[] args) {
        var props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group3");

        var r = new Random();

        try (var consumer = new KafkaShareConsumer<String, String>(props)) {
            consumer.subscribe(List.of("topic3"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    var ackType = switch (r.nextInt(3)) {
                        case 0 -> AcknowledgeType.ACCEPT;
                        case 1 -> AcknowledgeType.RELEASE;
                        default -> AcknowledgeType.REJECT;
                    };

                    System.out.println("Received: " + record.value() + ", ackType: " + ackType);
                    consumer.acknowledge(record, ackType);
                }

                if (!records.isEmpty()) {
                    System.out.println("---");
                }

                consumer.commitSync();
            }
        }
    }
}
