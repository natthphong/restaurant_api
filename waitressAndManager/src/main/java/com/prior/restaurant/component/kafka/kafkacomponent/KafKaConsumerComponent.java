package com.prior.restaurant.component.kafka.kafkacomponent;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prior.restaurant.exception.BaseException;
import com.prior.restaurant.models.MenuModel;
import com.prior.restaurant.service.WaitressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class KafKaConsumerComponent {
    private final  WaitressService waitressService;

    public KafKaConsumerComponent(WaitressService waitressService) {
        this.waitressService = waitressService;
    }

    @KafkaListener(topics = "${kafka.topics.cook}" , groupId = "${kafka.groupId.cook}")
    public void  updateStatus(@Payload String message) {
            //log.info("message {}" , message);
            this.waitressService.updateMenuToServing(message);

    }






}
