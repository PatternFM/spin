package fm.pattern.spin;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class RuntimeEnvironmentTest {

    @Mock
    private Instance instance1;

    @Mock
    private Instance instance2;

    @Mock
    private ExecutorService executor;

    private StartupConfiguration startupConfiguration;
    private List<Instance> instances = new ArrayList<>();

    private RuntimeEnvironment environment;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        instances.add(instance1);
        instances.add(instance2);

        startupConfiguration = new StartupConfiguration();
        startupConfiguration.setPollingInterval(100);
        startupConfiguration.setRetryCount(10);
        startupConfiguration.setConcurrent(true);

        environment = new RuntimeEnvironment(executor);
    }

    @Test(expected = TimeoutException.class)
    public void shouldThrowATimeoutExceptionIfAllInstancesDoNotStartWithinTheAllottedTime() {
        environment.start(instances, startupConfiguration);
    }

    @Test(expected = TimeoutException.class)
    public void shouldThrowATimeoutExceptionIfSomeInstancesDoNotStartWithinTheAllottedTime() {
        Mockito.when(instance1.running()).thenReturn(true);
        environment.start(instances, startupConfiguration);
    }

    @Test
    public void shouldBeAbleToStartAllInstancesConcurrently() {
        Mockito.when(instance1.running()).thenReturn(true);
        Mockito.when(instance2.running()).thenReturn(true);

        environment.start(instances, startupConfiguration);

        verify(executor, Mockito.times(2)).submit((Runnable) Mockito.anyObject());
    }

    @Test
    public void shouldBeAbleToStartAllInstancesSerially() {
        startupConfiguration.setConcurrent(false);

        Mockito.when(instance1.running()).thenReturn(true);
        Mockito.when(instance2.running()).thenReturn(true);

        environment.start(instances, startupConfiguration);

        verify(instance1, Mockito.times(1)).start();
        verify(instance2, Mockito.times(1)).start();
    }

    @Test
    public void shouldBeAbleToStopAllRunningInstances() {
        Mockito.when(instance1.running()).thenReturn(true);
        Mockito.when(instance2.running()).thenReturn(true);

        environment.stop(instances);

        verify(instance1, Mockito.times(1)).stop();
        verify(instance2, Mockito.times(1)).stop();
    }

    @Test
    public void theRuntimeEnviromentShouldBeRunningWhenAllUnderlyingInstancesAreRunning() {
        Mockito.when(instance1.running()).thenReturn(true);
        Mockito.when(instance2.running()).thenReturn(true);

        Assertions.assertThat(environment.running(instances)).isTrue();
    }

    @Test
    public void theRuntimeEnviromentShouldNotBeRunningWhenNotAllUnderlyingInstancesAreRunning() {
        Mockito.when(instance1.running()).thenReturn(true);
        Mockito.when(instance2.running()).thenReturn(false);

        Assertions.assertThat(environment.running(instances)).isFalse();
    }

}
