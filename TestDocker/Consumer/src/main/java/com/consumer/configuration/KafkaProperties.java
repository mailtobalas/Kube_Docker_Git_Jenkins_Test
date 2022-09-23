/*
 * Copyright (c) TESCO 2020.
 * All rights reserved.
 * No part of this program may be reproduced, translated or transmitted,
 * in any form or by any means, electronic, mechanical, photocopying,
 * recording or otherwise, or stored in any retrieval system of any nature,
 * without written permission of the copyright holder.
 */
package com.consumer.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration file to read the kafka configuration for topics. 
 *
 */
@Data
@ConfigurationProperties(prefix = "consumer.kafka")
@Component
public class KafkaProperties {
  String topic;
  String bootstrapServers;
  String groupId;
  String autoOffsetReset;
  String keyDeserializer;
  String valueDeserializer;
  String maxPollRecords;
  String maxPartitionFetchBytes;
  String heartbeatInterval;
  String maxPollInterval;
  String sessionTimeout;
  String requestTimeout;
  String retries;
  String maxBlock;
  String maxInFlight;
  String clientId;
  String enableAutoCommit;
  String acks;
  String timeout;
  Integer commitSize;
  SSLConfig sslConfig;
  boolean seekToPredeterminedOffset;

 @Data
  public static class SSLConfig {
    private Boolean sslEnabled;

    private String securityProtocolConfig = "SSL";

    private String trustStoreLocation;

    private String trustStorePassword;

    private String keyStoreLocation;

    private String keyStorePassword;

    private String sslKeyPassword;
  }

}
