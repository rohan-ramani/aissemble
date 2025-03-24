package com.boozallen.aissemble.alerting.core;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AlertSerializer for use in messaging
 *
 */

public class AlertSerializer implements Serializer<Alert> {
    private static final Logger logger = LoggerFactory.getLogger(AlertSerializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Alert alert) {
        try {
            if (alert == null){
                logger.warn("No Alert received for serialization");
                return null;
            }

            return objectMapper.writeValueAsBytes(alert);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Alert to byte[]");
        }
    }
}
