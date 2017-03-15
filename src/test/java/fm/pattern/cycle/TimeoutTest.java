package fm.pattern.cycle;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import fm.pattern.cycle.config.CycleConfiguration;

public class TimeoutTest {

    @Test
    public void shouldReturnTheDefaultPollingIntervalIfTheCurrentPollingIntervalIsNull() {
        Timeout timeout = new Timeout();
        Assertions.assertThat(timeout.getPollingInterval()).isEqualTo(CycleConfiguration.DEFAULT_POLLING_INTERVAL_MILLIS);
    }

    @Test
    public void shouldReturnTheDefaultRetryCountIfTheCurrentPollingIntervalIsNull() {
        Timeout timeout = new Timeout();
        Assertions.assertThat(timeout.getRetryCount()).isEqualTo(CycleConfiguration.DEFAULT_RETRY_COUNT);
    }

}
