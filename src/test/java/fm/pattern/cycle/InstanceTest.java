package fm.pattern.cycle;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import fm.pattern.cycle.Instance;

public class InstanceTest {

    @Test
    public void shouldBeAbleToCreateAnInstance() {
        Instance instance = new Instance("Service Name", "path", "http://localhost:8080");
        Assertions.assertThat(instance.getName()).isEqualTo("Service Name");
    }

}
