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
package org.apache.camel.quarkus.component.sql.deployment;

import io.quarkus.deployment.GizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.pkg.steps.NativeBuild;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;

import java.sql.Types;

import org.apache.camel.quarkus.component.sql.CamelSqlConfig;

class SqlProcessor {

    private static final String FEATURE = "camel-sql";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerForReflection(BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        reflectiveClass.produce(new ReflectiveClassBuildItem(false, true, Types.class));
    }

    @BuildStep
    void sqlNativeImageResources(BuildProducer<NativeImageResourceBuildItem> nativeImage, CamelSqlConfig config) {
        config.scriptFiles
                .stream()
                .map(scriptFile -> new NativeImageResourceBuildItem(scriptFile.replace("classpath:", "")))
                .forEach(nativeImage::produce);
    }

    @BuildStep(onlyIf = NativeBuild.class)
    void generateKParameterClass(BuildProducer<GeneratedClassBuildItem> generatedClass) {
        // TODO: The native image build fails with a NoClassDefFoundError without this. Possibly similar to https://github.com/oracle/graal/issues/656.
        ClassOutput classOutput = new GizmoAdaptor(generatedClass, false);
        ClassCreator.builder()
                .className("kotlin.reflect.KParameter")
                .classOutput(classOutput)
                .setFinal(true)
                .superClass(Object.class)
                .build()
                .close();
    }
}
