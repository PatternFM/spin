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

package fm.pattern.cycle;

import fm.pattern.cycle.config.CycleConfiguration;

public final class RuntimeEnvironment {

    private RuntimeEnvironment() {

    }

    public static void start() {
        CycleConfiguration.getInstances().forEach(instance -> instance.start());

        Integer count = 0;

        while (!running()) {
            if (count > CycleConfiguration.getTimeout().getRetryCount()) {
                throw new TimeoutException("Timeout occurred while waiting for the runtime environment to boot. Timeout configuration: " + CycleConfiguration.getTimeout());
            }
            pause(CycleConfiguration.getTimeout().getPollingInterval());
            count++;
        }
    }

    public static void stop() {
        CycleConfiguration.getInstances().forEach(instance -> instance.stop());
    }

    public static boolean running() {
        return CycleConfiguration.getInstances().stream().filter(instance -> !instance.running()).count() == 0;
    }

    private static void pause(Integer milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
