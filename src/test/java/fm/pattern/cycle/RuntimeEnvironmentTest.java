package fm.pattern.cycle;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

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

    private Timeout timeout;

    private List<Instance> instances = new ArrayList<>();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        this.instances.add(instance1);
        this.instances.add(instance2);
        this.timeout = new Timeout(100, 10);
    }

    @Test(expected = TimeoutException.class)
    public void shouldThrowATimeoutExceptionIfAllInstancesDoNotStartWithinTheAllottedTime() {
        RuntimeEnvironment.start(instances, timeout);
    }

    @Test(expected = TimeoutException.class)
    public void shouldThrowATimeoutExceptionIfSomeInstancesDoNotStartWithinTheAllottedTime() {
        Mockito.when(instance1.running()).thenReturn(true);
        RuntimeEnvironment.start(instances, timeout);
    }

    @Test
    public void shouldBeAbleToStartAllInstancesSuccessfully() {
        Mockito.when(instance1.running()).thenReturn(true);
        Mockito.when(instance2.running()).thenReturn(true);

        RuntimeEnvironment.start(instances, timeout);

        verify(instance1, Mockito.times(1)).start();
        verify(instance2, Mockito.times(1)).start();
    }

    @Test
    public void shouldBeAbleToStopAllRunningInstances() {
        Mockito.when(instance1.running()).thenReturn(true);
        Mockito.when(instance2.running()).thenReturn(true);

        RuntimeEnvironment.stop(instances);

        verify(instance1, Mockito.times(1)).stop();
        verify(instance2, Mockito.times(1)).stop();
    }

    @Test
    public void theRuntimeEnviromentShouldBeRunningWhenAllUnderlyingInstancesAreRunning() {
        Mockito.when(instance1.running()).thenReturn(true);
        Mockito.when(instance2.running()).thenReturn(true);

        Assertions.assertThat(RuntimeEnvironment.running(instances)).isTrue();
    }

    @Test
    public void theRuntimeEnviromentShouldNotBeRunningWhenNotAllUnderlyingInstancesAreRunning() {
        Mockito.when(instance1.running()).thenReturn(true);
        Mockito.when(instance2.running()).thenReturn(false);

        Assertions.assertThat(RuntimeEnvironment.running(instances)).isFalse();
    }

}
