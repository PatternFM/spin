package fm.pattern.spin;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import fm.pattern.spin.Timeout;
import fm.pattern.spin.config.SpinConfiguration;

public class TimeoutTest {

    @Test
    public void shouldReturnTheDefaultPollingIntervalIfTheCurrentPollingIntervalIsNull() {
        Timeout timeout = new Timeout();
        Assertions.assertThat(timeout.getPollingInterval()).isEqualTo(SpinConfiguration.DEFAULT_POLLING_INTERVAL_MILLIS);
    }

    @Test
    public void shouldReturnTheDefaultRetryCountIfTheCurrentPollingIntervalIsNull() {
        Timeout timeout = new Timeout();
        Assertions.assertThat(timeout.getRetryCount()).isEqualTo(SpinConfiguration.DEFAULT_RETRY_COUNT);
    }

}
