package com.epam.epmcacm.msademo.resourcesrv.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RMQPublisherService {

    private final String publisherBinding = "output-1";

    @Autowired
    private StreamBridge streamBridge;

    public void publishCreationEvent(String message) {
        streamBridge.send(publisherBinding, message);
    }
}
