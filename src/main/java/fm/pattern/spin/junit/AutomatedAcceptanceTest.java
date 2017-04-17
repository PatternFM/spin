/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fm.pattern.spin.junit;

import java.util.concurrent.Executors;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import fm.pattern.spin.RuntimeEnvironment;
import fm.pattern.spin.config.SpinConfiguration;

@RunWith(AcceptanceTestRunner.class)
public abstract class AutomatedAcceptanceTest {

    private static RuntimeEnvironment environment = new RuntimeEnvironment(Executors.newCachedThreadPool());

    @BeforeClass
    public static void start() {
        environment.start(SpinConfiguration.getInstances(), SpinConfiguration.getStartupConfiguration());
    }

    public static void stop() {
        environment.stop(SpinConfiguration.getInstances());
    }

}
