package com.prior.restaurant.component.kafka.kafkacomponent;


import com.prior.restaurant.service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafKaConsumerComponent {

    private final BillService billService;

    public KafKaConsumerComponent(BillService billService) {
        this.billService = billService;
    }


    @KafkaListener(topics = "${kafka.topics.bill}" , groupId = "${kafka.groupId.bill}")
    public void  generateBill(@Payload String message) {
            this.billService.generateBill(message);
    }






}
