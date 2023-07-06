//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;
import com.getsimplex.steptimer.utils.Configuration;
import com.typesafe.config.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;


public class KafkaProducerUtil {

    private static String BOOTSTRAP_SERVERS = "localhost:9092";

    static {
        Config config = Configuration.getConfiguration();
        if (System.getenv("KAFKA_BROKER")!=null && !System.getenv("KAFKA_BROKER").isEmpty()){
            BOOTSTRAP_SERVERS = System.getenv("KAFKA_BROKER");
        }
        else {
            BOOTSTRAP_SERVERS= config.getString("kafka.broker");
        }

    }

    public static Producer<Long, String>    createProducer(){

        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "STEDI");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        final Producer<Long, String> producer = new KafkaProducer<Long, String>(props);

        return producer;
    }
}
