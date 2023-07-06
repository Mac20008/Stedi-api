//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;
import com.getsimplex.steptimer.utils.Configuration;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;


public class KafkaConsumerUtil {

    //private final static String TOPIC = "customer-risk";
    private static String TOPIC;

    private static String BOOTSTRAP_SERVERS = "localhost:9092";

    static {
        Config config = Configuration.getConfiguration();
        if (System.getenv("KAFKA_BROKER")!=null && !System.getenv("KAFKA_BROKER").isEmpty()){
            BOOTSTRAP_SERVERS = System.getenv("KAFKA_BROKER");
            TOPIC = System.getenv("KAFKA_RISK_TOPIC");
        }
        else {
            BOOTSTRAP_SERVERS= config.getString("kafka.broker");
            TOPIC = Configuration.getConfiguration().getString("kafka.riskTopic");
        }

    }

    public static Consumer<Long, String> createConsumer(){

        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "STEDI");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        final Consumer<Long, String> consumer = new KafkaConsumer<Long, String>(props);

        consumer.subscribe(Collections.singletonList(TOPIC));
        return consumer;
    }
}
