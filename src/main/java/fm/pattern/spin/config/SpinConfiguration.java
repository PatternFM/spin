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

package fm.pattern.spin.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import fm.pattern.spin.Instance;
import fm.pattern.spin.StartupConfiguration;

@SuppressWarnings("unchecked")
public final class SpinConfiguration {

    public static final Integer DEFAULT_RETRY_COUNT = 60;
    public static final Integer DEFAULT_POLLING_INTERVAL_MILLIS = 1000;
    public static final String DEFAULT_FILENAME = "spin.yml";
    public static final boolean DEFAULT_CONCURRENT = true;

    private static List<Instance> instances = null;
    private static StartupConfiguration startupConfiguration = null;

    private static final Logger log = LoggerFactory.getLogger(SpinConfiguration.class);

    private SpinConfiguration() {

    }

    static {
        String filename = System.getProperty("spin.config");
        load(StringUtils.isNotBlank(filename) ? filename : DEFAULT_FILENAME);
    }

    static void reset() {
        System.clearProperty("spin.config");
        instances = null;
        startupConfiguration = null;
    }

    public static void load(String filename) {
        InputStream inputStream = SpinConfiguration.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            log.warn("Unable to find " + filename + " on the classpath.");
            return;
        }

        Map<String, Map<String, Object>> model = null;
        try {
            log.info("Using Spin configuration file: " + filename);
            model = (Map<String, Map<String, Object>>) new Yaml().load(inputStream);
        }
        catch (Exception e) {
            throw new SpinConfigurationException("Spin failed to parse file '" + filename + "'", e);
        }

        startupConfiguration = resolveStartupConfiguration(model);
        instances = resolveInstances(model);
    }

    public static List<Instance> getInstances() {
        return instances != null ? instances : new ArrayList<Instance>();
    }

    public static StartupConfiguration getStartupConfiguration() {
        return startupConfiguration != null ? startupConfiguration : new StartupConfiguration(DEFAULT_POLLING_INTERVAL_MILLIS, DEFAULT_RETRY_COUNT, DEFAULT_CONCURRENT);
    }

    private static List<Instance> resolveInstances(Map<String, Map<String, Object>> model) {
        List<Instance> instances = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : model.entrySet()) {
            String name = entry.getKey();
            if (name.equals("startup")) {
                continue;
            }

            Map<String, Object> map = entry.getValue();

            String start = (String) map.get("start");
            if (StringUtils.isEmpty(start)) {
                throw new SpinConfigurationException("Invalid Spin configuration - 'start' is required attribute for " + name);
            }

            String stop = (String) map.get("stop");
            if (StringUtils.isEmpty(stop)) {
                throw new SpinConfigurationException("Invalid Spin configuration - 'stop' is required attribute for " + name);
            }

            String ping = (String) map.get("ping");
            if (StringUtils.isBlank(ping)) {
                throw new SpinConfigurationException("Invalid Spin configuration - 'ping' is a required attribute for " + name);
            }

            Instance instance = new Instance(name, start, stop, ping);

            String path = (String) map.get("path");
            if (StringUtils.isNotBlank(path)) {
                instance.setPath(path);
            }

            Map<String, String> environment = (Map<String, String>) map.get("environment");
            if (environment != null && !environment.isEmpty()) {
                instance.setEnvironment(environment);
            }

            instances.add(instance);
        }

        return instances;
    }

    private static StartupConfiguration resolveStartupConfiguration(Map<String, Map<String, Object>> model) {
        for (Map.Entry<String, Map<String, Object>> entry : model.entrySet()) {
            String key = entry.getKey();

            if (key.equals("startup")) {
                Map<String, Object> map = entry.getValue();
                Integer pollingInterval = map.containsKey("polling_interval") ? (Integer) map.get("polling_interval") : DEFAULT_POLLING_INTERVAL_MILLIS;
                Integer retryCount = map.containsKey("retry_count") ? (Integer) map.get("retry_count") : DEFAULT_RETRY_COUNT;
                Boolean concurrentStartup = map.containsKey("concurrent") ? (Boolean) map.get("concurrent") : DEFAULT_CONCURRENT;
                return new StartupConfiguration(pollingInterval, retryCount, concurrentStartup);
            }

        }

        return new StartupConfiguration(DEFAULT_POLLING_INTERVAL_MILLIS, DEFAULT_RETRY_COUNT, DEFAULT_CONCURRENT);
    }

}
