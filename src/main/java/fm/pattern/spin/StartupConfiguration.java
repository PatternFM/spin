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

import fm.pattern.minimal.JSON;
import fm.pattern.spin.config.SpinConfiguration;

public class StartupConfiguration {

    private Integer pollingInterval;
    private Integer retryCount;
    private boolean concurrent;

    public StartupConfiguration() {

    }

    public StartupConfiguration(Integer pollingInterval, Integer retryCount, boolean concurrent) {
        this.pollingInterval = pollingInterval;
        this.retryCount = retryCount;
        this.concurrent = concurrent;
    }

    public Integer getPollingInterval() {
        return pollingInterval == null ? SpinConfiguration.DEFAULT_POLLING_INTERVAL_MILLIS : pollingInterval;
    }

    public void setPollingInterval(Integer pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public Integer getRetryCount() {
        return retryCount == null ? SpinConfiguration.DEFAULT_RETRY_COUNT : retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    @Override
    public String toString() {
        return JSON.stringify(this);
    }

}
