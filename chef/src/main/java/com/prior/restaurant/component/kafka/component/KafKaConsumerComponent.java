package com.prior.restaurant.component.kafka.component;


import com.prior.restaurant.service.ChefService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafKaConsumerComponent {
    private final ChefService chefService;

    public KafKaConsumerComponent(ChefService chefService) {
        this.chefService = chefService;
    }

    @KafkaListener(topics = "${kafka.topics.new}" , groupId = "${kafka.groupId.new}")
    public void  updateStatus(@Payload String message) {
            log.info("size {}" , message);
        chefService.updateStatusCooking(message);

    }






}
