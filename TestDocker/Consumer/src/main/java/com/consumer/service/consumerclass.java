package com.consumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;

@Service
public class consumerclass {

    @Autowired
    KafkaConsumer<String, String> kafkaConsumer;

    public void ConsumeRecords()
    {
        ConsumerRecords<String, String> records = kafkaConsumer.poll(50000);

        if (records.count() > 1)
        {
            for (ConsumerRecord record: records) {

                System.out.println(record.value().toString());
            }
        }
        kafkaConsumer.commitSync();
        PrintOffset();

    }

    public void PrintOffset()
    {
        List<PartitionInfo> partitions = kafkaConsumer.partitionsFor("consumerinvoice");



        for (PartitionInfo partition:partitions)
        {
            TopicPartition topicPartition = new TopicPartition("consumerinvoice", partition.partition());
            System.out.println( kafkaConsumer.position(topicPartition));
            /*TopicPartition topicPartition = new TopicPartition("consumerinvoice", 1);
            System.out.println( kafkaConsumer.position(topicPartition));

            topicPartition = new TopicPartition("consumerinvoice", 2);
            System.out.println( kafkaConsumer.position(topicPartition));

            topicPartition = new TopicPartition("consumerinvoice", 0);
            System.out.println( kafkaConsumer.position(topicPartition));*/
        }




    }
}
