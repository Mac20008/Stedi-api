//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;

import akka.actor.UntypedActor;
import com.getsimplex.steptimer.model.DeviceMessage;
import com.getsimplex.steptimer.model.StediEvent;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Optional;
import java.util.logging.Logger;

public class KafkaRiskTopicProducerActor extends UntypedActor {
    private final static String TOPIC = "stedi-events";
    private static Logger logger = Logger.getLogger(KafkaRiskTopicProducerActor.class.getName());
    private Optional<Producer<Long,String>> stediEventsTopic = Optional.empty();


    public void onReceive(Object object){

        if (object instanceof StediEvent) {
            StediEvent event = (StediEvent) object;
            if (!stediEventsTopic.isPresent()){
                logger.info("Creating Kafka Producer");
                stediEventsTopic = Optional.of(KafkaProducerUtil.createProducer());
            }
            logger.info("Sending to Kafka Topic: stedi-events");

            ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(TOPIC, System.currentTimeMillis(),event.getMessage());
            stediEventsTopic.get().send(record);
        }

    }

}
