package com.prior.restaurant.component.kafka.kafkacomponent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProducerComponent {
    private final  KafkaTemplate<String,String> kafkaTemplate;


    public KafkaProducerComponent(@Qualifier("newKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendData(String message , String topic ){
        this.kafkaTemplate.send(topic , message)
                .whenComplete(((result, throwable) -> {
                    if (throwable == null){
                        log.info("Kafka send  to {} done {}" ,
                                result.getRecordMetadata().topic(),
                                result.getProducerRecord().value()
                                );
                    }else{
                        log.info("kafka send exception {}" ,throwable.getMessage());
                    }
                }));
    }
}
