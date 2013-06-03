package com.confluex.mule.test.event

import groovy.util.logging.Slf4j
import org.mule.api.context.notification.EndpointMessageNotificationListener
import org.mule.context.notification.EndpointMessageNotification
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Adds some simple logging around endpoint notifications to help diagnose activity and performance problems.
 */

class LoggingEndpointListener implements EndpointMessageNotificationListener<EndpointMessageNotification> {
    Logger log = LoggerFactory.getLogger(this.class)
    String endpointName
    Boolean logPayload = false

    @Override
    void onNotification(EndpointMessageNotification notification) {
        if (!endpointName || notification.endpoint == endpointName) {
            log.debug("endpoint={} id={} type={}", notification.endpoint,  notification.source.correlationId, notification.type,)
            //don't invoke payloadAsString
            if (logPayload) {
                log.debug("endpoint={} id={} payload={}", notification.endpoint, notification.source.correlationId, notification.source.payloadAsString)
            }
        }

    }
}
