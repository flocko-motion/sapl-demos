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
import io.sapl.grammar.sapl.impl.Val;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

// MQTT imports
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

@NoArgsConstructor
@Service
@Slf4j
@PolicyInformationPoint(name = MqttPolicyInformationPoint.NAME, description = MqttPolicyInformationPoint.DESCRIPTION)
public class MqttPolicyInformationPoint {

    public static final String NAME = "mqtt";

    public static final String DESCRIPTION = "Policy Information Point and attributes for retrieving current date and time information";

    // singleton mqtt client factory
    public MqttPahoClientFactory mqttClientFactory;
    public MqttPahoClientFactory getMqttClientFactory() {
        LOGGER.trace("MQTT: getMqttClientFactory()");
        if(this.mqttClientFactory != null) return this.mqttClientFactory;
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { "tcp://test.mosquitto.org:1883" });
//		options.setUserName("guest");
//		options.setPassword("guest".toCharArray());
        factory.setConnectionOptions(options);
        this.mqttClientFactory = factory;
        return factory;
    }

    @Attribute(docs = "Subscribes to a MQTT channel and emits all incoming messages")
    public Flux<Val> channel(@Number Val value, Map<String, JsonNode> variables) throws AttributeException {
        LOGGER.trace("MQTT: channel(..)");
        try {
            MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                    "myConsumer",
                    getMqttClientFactory(),
                    "/#");
            adapter.setCompletionTimeout(5000);
            adapter.setConverter(new DefaultPahoMessageConverter());
            adapter.setQos(1);

            Publisher<Message<Object>> flow = IntegrationFlows.from(adapter)
                    .toReactivePublisher();
            Flux<Val> flux = Flux.from(flow)
                    .log()
//                    .map(i -> Val.of("todo: convert message object to val"));
                    .map(i -> Val.of("2020-10-07T00:00:59.000Z")); // for testing: send a UTC date and time
            return flux;
        } catch (Exception e) {
            throw new AttributeException("Exception while creating the next ticker value.", e);
        }
    }

    @Attribute(docs = "Emits every x seconds the current UTC date and time as an ISO-8601 string. x is the passed number value.")
    public Flux<Val> ticker(@Number Val value, Map<String, JsonNode> variables) throws AttributeException {
        LOGGER.trace("MQTT: ticker(..)");
        try {
            // gebe einene fixen wert zurück - in der zukunft sollte das der mqtt output sein
            return Flux.interval(Duration.ofSeconds(value.get().asLong())).map(i -> Val.of("2020-10-07T00:00:01.000Z"));
        } catch (Exception e) {
            throw new AttributeException("Exception while creating the next ticker value.", e);
        }
    }


}
