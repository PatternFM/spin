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

import java.util.HashMap;
import java.util.Map;

public class Instance {

    private String name;
    private String start;
    private String stop;
    private String ping;
    private String path;

    private Map<String, String> environment = new HashMap<>();

    private boolean shouldTerminate = false;

    public Instance(String name, String start, String stop, String ping) {
        this.name = name;
        this.start = start;
        this.stop = stop;
        this.ping = ping;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPing() {
        return ping;
    }

    public void setPing(String ping) {
        this.ping = ping;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean running() {
        return InstanceManagementService.isRunning(this);
    }

    public void start() {
        if (!running()) {
            shouldTerminate = true;
            InstanceManagementService.start(this);
        }
    }

    public void stop() {
        if (running() && shouldTerminate) {
            InstanceManagementService.stop(this);
        }
    }

}
