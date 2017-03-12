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

package fm.pattern.cycle.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import fm.pattern.cycle.Instance;
import fm.pattern.cycle.Timeout;

@SuppressWarnings("unchecked")
public final class CycleConfiguration {

    public static final Integer DEFAULT_RETRY_COUNT = 60;
    public static final Integer DEFAULT_POLLING_INTERVAL_MILLIS = 1000;

    private static final Logger log = LoggerFactory.getLogger(CycleConfiguration.class);
    private static final String default_filename = "cycle.yml";

    private static Map<String, Map<String, Object>> model;

    private CycleConfiguration() {

    }

    static {
        String configFilename = System.getProperty("cycle.config");

        if (StringUtils.isNotBlank(configFilename)) {
            load(configFilename);
        }
        else {
            load(default_filename);
        }
    }

    public static void load(String filename) {
        if (model != null) {
            model.clear();
        }

        InputStream inputStream = CycleConfiguration.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            log.warn("Unable to find " + filename + " on the classpath.");
            return;
        }

        try {
            model = (Map<String, Map<String, Object>>) new Yaml().load(inputStream);
        }
        catch (Exception e) {
            throw new CycleConfigurationException("Failed to parse " + filename + ":", e);
        }
    }

    public static Timeout getTimeout() {
        for (Map.Entry<String, Map<String, Object>> entry : model.entrySet()) {
            String key = entry.getKey();

            if (key.equals("startup")) {
                Map<String, Object> map = entry.getValue();
                Integer pollingInterval = map.containsKey("polling_interval") ? (Integer) map.get("polling_interval") : DEFAULT_POLLING_INTERVAL_MILLIS;
                Integer retryCount = map.containsKey("retry_count") ? (Integer) map.get("retry_count") : DEFAULT_RETRY_COUNT;
                return new Timeout(pollingInterval, retryCount);
            }

        }

        return new Timeout(DEFAULT_POLLING_INTERVAL_MILLIS, DEFAULT_RETRY_COUNT);
    }

    public static List<Instance> getInstances() {
        List<Instance> instances = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : model.entrySet()) {
            String service = entry.getKey();

            if (service.equals("startup")) {
                continue;
            }

            Map<String, Object> map = entry.getValue();
            String path = (String) map.get("path");
            String ping = (String) map.get("ping");

            Instance instance = new Instance(service, path, ping);
            if (map.containsKey("start")) {
                instance.setStart((String) map.get("start"));
            }
            if (map.containsKey("stop")) {
                instance.setStop((String) map.get("stop"));
            }

            instances.add(instance);
        }

        return instances;
    }

}
