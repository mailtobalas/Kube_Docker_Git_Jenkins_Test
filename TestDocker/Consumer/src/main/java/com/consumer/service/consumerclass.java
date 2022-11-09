package com.consumer.service;

//import com.consumer.configuration.KubeCache;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class consumerclass {

    @Autowired
    KafkaConsumer<String, String> kafkaConsumer;
    //@Autowired
   // KubeCache kubecache;

   // @EventListener(ApplicationReadyEvent.class)
   @Scheduled(cron ="*/10 * * * * *")
    public void ConsumeRecords()
    {

      //String str =  kubecache.getAccessTokenFromCache("Reproces");


        Properties seekToPartitionoffset =  LoadSeekToPartionInfomration();

        List partitionNo = new ArrayList<String>();
        boolean seekToUpdatFileflg=false;

        if(seekToPartitionoffset.size()==0) {
            System.out.println("Recovery Completed!.."); return;}

        ConsumerRecords<String, String> records = kafkaConsumer.poll(2000);


int rec=records.count();
        System.out.println("----------");
        for (ConsumerRecord record: records) {
                String seekoffset =  seekToPartitionoffset.getProperty(String.valueOf(record.partition()))==null ? "-1" :seekToPartitionoffset.getProperty(String.valueOf(record.partition()));
         //  System.out.println(record.partition() + "  " + record.offset()  + " "+  record.value().toString());

                if(record.offset() <= Long.parseLong(seekoffset))
                {
                System.out.println(record.partition() + "  " + record.offset()  + " "+  record.value().toString());

                   if (record.offset() == Long.parseLong(seekoffset)){
                       partitionNo.add(record.partition()); }
                }
            }


        kafkaConsumer.commitSync();


        for (Object partition:partitionNo)
        {
            seekToPartitionoffset.remove(partition.toString());
            seekToUpdatFileflg=true;
        }
        if(seekToUpdatFileflg==true) {
            UpdateOffsetValuesInPropertyfile(seekToPartitionoffset);}

      //  kubecache.addAccessTokenToCache("Reproces","Testing");

    }

    private void UpdateOffsetValuesInPropertyfile(Properties seekToPartitionoffset) {


        String configPath="";
        try {
            URL resource = ClassLoader.getSystemResource("seekToPartitionOffSet.properties");
             configPath = URLDecoder.decode(resource.getFile(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

         try ( OutputStream output
                      = new FileOutputStream(configPath))
        {
            seekToPartitionoffset.store(output, null);
            output.close();


        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    private Properties LoadSeekToPartionInfomration() {

        Properties seekPartitionOffsetProperties = new Properties();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("seekToPartitionOffSet.properties");) {

            seekPartitionOffsetProperties.load(inputStream);

        }
        catch (Exception e)
        {

        }
        return seekPartitionOffsetProperties;

    }

    public void PrintOffset()
    {
        List<PartitionInfo> partitions = kafkaConsumer.partitionsFor("coninvoices1");



        for (PartitionInfo partition:partitions)
        {
            TopicPartition topicPartition = new TopicPartition("coninvoices1", partition.partition());
            System.out.println( kafkaConsumer.position(topicPartition));
            /*TopicPartition topicPartition = new TopicPartition("coninvoices", 1);
            System.out.println( kafkaConsumer.position(topicPartition));

            topicPartition = new TopicPartition("coninvoices", 2);
            System.out.println( kafkaConsumer.position(topicPartition));

            topicPartition = new TopicPartition("coninvoices", 0);
            System.out.println( kafkaConsumer.position(topicPartition));*/
        }




    }
}
