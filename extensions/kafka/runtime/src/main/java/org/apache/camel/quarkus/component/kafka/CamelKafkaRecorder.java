/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.component.kafka;

import java.util.Collections;
import java.util.Map;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import org.apache.camel.component.kafka.KafkaClientFactory;

@Recorder
public class CamelKafkaRecorder {

    @SuppressWarnings("unchecked")
    public RuntimeValue<KafkaClientFactory> createKafkaClientFactory() {
        final InstanceHandle<Object> instance = Arc.container().instance("default-kafka-broker");
        Map<String, Object> kafkaConfig;

        if (instance.isAvailable()) {
            kafkaConfig = (Map<String, Object>) instance.get();
        } else {
            kafkaConfig = Collections.emptyMap();
        }

        QuarkusKafkaClientFactory quarkusKafkaClientFactory = new QuarkusKafkaClientFactory(kafkaConfig);
        return new RuntimeValue<>(quarkusKafkaClientFactory);
    }
}
