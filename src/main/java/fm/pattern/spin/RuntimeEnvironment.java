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

package fm.pattern.spin;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import fm.pattern.spin.config.SpinConfiguration;

public final class RuntimeEnvironment {

    private static ExecutorService executor = Executors.newCachedThreadPool();

    private RuntimeEnvironment() {

    }

    public static void start(List<Instance> instances, StartupConfiguration timeout) {
        instances.forEach(instance -> executor.submit(() -> {
            instance.start();
        }));

        Integer count = 0;

        while (!running(instances)) {
            if (count > timeout.getRetryCount()) {
                String names = instances.stream().map(i -> i.getName()).collect(Collectors.joining(","));
                throw new TimeoutException("Timeout occurred while waiting for " + names + " to start up. Timeout configuration: " + SpinConfiguration.getStartupConfiguration());
            }
            pause(timeout.getPollingInterval());
            count++;
        }
    }

    public static void stop(List<Instance> instances) {
        instances.forEach(instance -> instance.stop());
    }

    public static boolean running(List<Instance> instances) {
        return instances.stream().filter(instance -> !instance.running()).count() == 0;
    }

    private static void pause(Integer milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
