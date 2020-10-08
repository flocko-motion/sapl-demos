/*
  Copyright © 2020 Dominic Heutelbeck (dominic@heutelbeck.com)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.demo;

import com.fasterxml.jackson.databind.JsonNode;
import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.AttributeException;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Number;
import io.sapl.api.validation.Text;
import io.sapl.grammar.sapl.impl.Val;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

// MQTT imports
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.stream.CharacterStreamReadingMessageSource;
import org.springframework.messaging.MessageHandler;

// hallo

// @NoArgsConstructor
@Service
@PolicyInformationPoint(name = MqttPolicyInformationPoint.NAME, description = MqttPolicyInformationPoint.DESCRIPTION)
public class MqttPolicyInformationPoint {

    public static final String NAME = "mqtt";

    public static final String DESCRIPTION = "Policy Information Point and attributes for retrieving current date and time information";

    private static final Log LOGGER = LogFactory.getLog(MqttPolicyInformationPoint.class);


    public MqttPolicyInformationPoint() {
        LOGGER.info("\n=mqtt PIP construct !!!!11!!1===");
    }

    public MqttPahoClientFactory mqttClientFactory;
    public MqttPahoClientFactory getMqttClientFactory() {
        if(this.mqttClientFactory != null) return this.mqttClientFactory;
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { "tcp://test.mosquitto.org:1883" });
//		options.setUserName("guest");
//		options.setPassword("guest".toCharArray());
        factory.setConnectionOptions(options);
        LOGGER.info("\n=setup FACTORY===");
        this.mqttClientFactory = factory;
        return factory;
    }
/*
    @Bean
    public IntegrationFlow mqttInFlow() {
        LOGGER.info("\nBEAN - mqttInFlow");
        return IntegrationFlows.from(mqttInbound())
                .transform(p -> p + ", received from MQTT")
                .handle(logger())
                .get();
    }

    @Bean
    public MessageProducerSupport mqttInbound() {
        LOGGER.info("\nBEAN - mqttInbound");
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("siSampleConsumer",
                getMqttClientFactory(), "siSampleTopic");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        return adapter;
    }


    private LoggingHandler logger() {
        LoggingHandler loggingHandler = new LoggingHandler("INFO");
        loggingHandler.setLoggerName("siSample");
        return loggingHandler;
    }
 */


    @Attribute(docs = "Emits every x seconds the current UTC date and time as an ISO-8601 string. x is the passed number value.")
    public Flux<Val> ticker(@Number Val value, Map<String, JsonNode> variables) throws AttributeException {
        try {
            // return Flux.interval(Duration.ofSeconds(value.get().asLong())).map(i -> Val.of(Instant.now().toString()));
            return Flux.interval(Duration.ofSeconds(value.get().asLong())).map(i -> Val.of("2020-10-07T08:34:07.042Z"));
        } catch (Exception e) {
            throw new AttributeException("Exception while creating the next ticker value.", e);
        }
    }

    @Attribute(docs = "Listens to MQTT Broker - TODO: pass server details as parameter.")
    public Flux<Val> channel(@Number Val value, Map<String, JsonNode> variables) throws AttributeException {
        try {
            // return Flux.interval(Duration.ofSeconds(value.get().asLong())).map(i -> Val.of(Instant.now().toString()));
            return Flux.interval(Duration.ofSeconds(value.get().asLong())).map(i -> Val.of("2020-10-07T08:34:07.042Z"));
        } catch (Exception e) {
            throw new AttributeException("Exception while creating the next ticker value.", e);
        }
    }

}
