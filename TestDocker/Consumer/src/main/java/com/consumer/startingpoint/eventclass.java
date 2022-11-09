package com.consumer.startingpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.consumer.service.consumerclass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class eventclass {

    private static final ExecutorService singleThreadedExecutor = Executors.newSingleThreadExecutor();
@Autowired
consumerclass consumeClass;


    public void Consumer() throws Exception {


       singleThreadedExecutor.submit(() -> consumeClass.ConsumeRecords());


    }
}
