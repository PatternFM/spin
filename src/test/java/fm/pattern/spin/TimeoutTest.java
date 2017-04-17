package fm.pattern.spin;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import fm.pattern.spin.StartupConfiguration;
import fm.pattern.spin.config.SpinConfiguration;

public class TimeoutTest {

    @Test
    public void shouldReturnTheDefaultPollingIntervalIfTheCurrentPollingIntervalIsNull() {
        StartupConfiguration timeout = new StartupConfiguration();
        Assertions.assertThat(timeout.getPollingInterval()).isEqualTo(SpinConfiguration.DEFAULT_POLLING_INTERVAL_MILLIS);
    }

    @Test
    public void shouldReturnTheDefaultRetryCountIfTheCurrentPollingIntervalIsNull() {
        StartupConfiguration timeout = new StartupConfiguration();
        Assertions.assertThat(timeout.getRetryCount()).isEqualTo(SpinConfiguration.DEFAULT_RETRY_COUNT);
    }

}
