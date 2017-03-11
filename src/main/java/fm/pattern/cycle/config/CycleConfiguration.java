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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;

import fm.pattern.cycle.Instance;
import fm.pattern.cycle.Timeout;

@SuppressWarnings("unchecked")
public class CycleConfiguration {

    private static final Log log = LogFactory.getLog(CycleConfiguration.class);
    private static final String DEFAULT_CONFIG_FILENAME = "cycle.yml";

    private static Map<String, Map<String, Object>> model;

    static {
        String configFilename = System.getProperty("cycle.config");

        if (StringUtils.isNotBlank(configFilename)) {
            load(configFilename);
        }
        else {
            load(DEFAULT_CONFIG_FILENAME);
        }
    }

    public static void load(String filename) {
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

    public static Timeout getStartupConfiguration() {
        for (Map.Entry<String, Map<String, Object>> entry : model.entrySet()) {
            String key = entry.getKey();

            if (key.equals("startup")) {
                Map<String, Object> map = entry.getValue();
                Integer pollingInterval = map.containsKey("polling_interval") ? (Integer) map.get("polling_interval") : null;
                Integer retryCount = map.containsKey("retry_count") ? (Integer) map.get("retry_count") : null;
                return new Timeout(pollingInterval, retryCount);
            }

        }

        return null;
    }

    public static List<Instance> instances() {
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
