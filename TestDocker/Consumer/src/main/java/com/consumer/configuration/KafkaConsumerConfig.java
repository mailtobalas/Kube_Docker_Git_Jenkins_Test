/*
 * Copyright (c) TESCO 2020.
 * All rights reserved.
 * No part of this program may be reproduced, translated or transmitted,
 * in any form or by any means, electronic, mechanical, photocopying,
 * recording or otherwise, or stored in any retrieval system of any nature,
 * without written permission of the copyright holder.
 */
package com.consumer.configuration;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Configuration file to read the kafka configuration for topics. 
 */
@Slf4j
@Configuration
public class KafkaConsumerConfig {

  @Autowired
  KafkaProperties properties;
  


  @Bean
  public KafkaConsumer<String, String> kafkaConsumer() {

    KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(consumerConfigs());

    kafkaConsumer.subscribe(Collections.singletonList(properties.getTopic()));
    
    log.info("[KafkaConsumerConfig][kafkaConsumer] kafka isSeekToPredeterminedOffset : " + properties.isSeekToPredeterminedOffset());
    
    if(properties.isSeekToPredeterminedOffset()) {
    	
    	log.info("[KafkaConsumerConfig][kafkaConsumer] IMMEDIATELY check if seek overrite in processoris set to TRUE.. to overrite updatedAt with createdAt values.");
    	setPredeterminedPartitionOffsetAndSeek(kafkaConsumer);
    }
    
    return kafkaConsumer;
  }

  private Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.getEnableAutoCommit());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getAutoOffsetReset());
    props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, properties.getHeartbeatInterval());
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, properties.getMaxPollRecords());
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, properties.getMaxPollInterval());
    props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, properties.maxPartitionFetchBytes);
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, properties.getSessionTimeout());
    props.put(ConsumerConfig.CLIENT_ID_CONFIG, properties.getClientId());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroupId());

    KafkaProperties.SSLConfig sslConfig = properties.getSslConfig();
    if (sslConfig.getSslEnabled().equals(Boolean.TRUE)) {
      props.put(
          CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, sslConfig.getSecurityProtocolConfig());
      props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslConfig.getTrustStoreLocation());
      props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslConfig.getTrustStorePassword());
      props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, StringUtils.EMPTY);

      props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, sslConfig.getKeyStoreLocation());
      props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, sslConfig.getKeyStorePassword());
      props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, sslConfig.getKeyStorePassword());
    }

    return props;
  }
  
	
  private void setPredeterminedPartitionOffsetAndSeek(KafkaConsumer<String, String> kafkaConsumer) {

	  log.info("[KafkaConsumerConfig][setPredeterminedPartitionOffsetAndSeek] In..");

	  ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(5000));
	  log.info("[KafkaConsumerConfig][setPredeterminedPartitionOffsetAndSeek] calling poll over...");

	  Properties seekPartitionOffsetProperties = new Properties();

	  try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("seekPartitionOffSet.properties");) {

		  seekPartitionOffsetProperties.load(inputStream);

		  log.info("[KafkaConsumerConfig][setPredeterminedPartitionOffsetAndSeek] configured seek partition offsets : " + seekPartitionOffsetProperties);

		  for(Entry<Object, Object> entrySet : seekPartitionOffsetProperties.entrySet()) {

			String partitionNumber = ((String)entrySet.getKey()).trim();
			String partitionOffset = ((String)entrySet.getValue()).trim();

			log.info("[KafkaConsumerConfig][setPredeterminedPartitionOffsetAndSeek] configured partitionNumber : " + partitionNumber + " partitionOffset : " + partitionOffset);

			try {

				TopicPartition currentPartition = new TopicPartition(properties.getTopic(), Integer.parseInt(partitionNumber));
				kafkaConsumer.seek(currentPartition , Integer.parseInt(partitionOffset));

				log.info("[KafkaConsumerConfig][setPredeterminedPartitionOffsetAndSeek] seeking completed for partition number : " + partitionNumber);

			}catch(Exception ex) {
				log.info("[KafkaConsumerConfig][setPredeterminedPartitionOffsetAndSeek] Some problem with partition seek for partition number : + " + partitionNumber + " Exception : " + ex.getMessage());
			}

		  }

	  }
	  catch (Exception e) {
		  log.error("[KafkaConsumerConfig][setPredeterminedPartitionOffsetAndSeek] Some problem : " + e.getMessage());
		  e.printStackTrace();
	  }
  }
}
