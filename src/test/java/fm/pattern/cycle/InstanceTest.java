package fm.pattern.cycle;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class InstanceTest {

    @Test
    public void shouldBeAbleToUpdateTheInstanceNamePathAndPing() {
        Instance instance = new Instance("name", ".", "http://localhost:8080/v1/ping");
        instance.setName("new_name");
        instance.setPath("new_path");
        instance.setPing("new_ping");

        Assertions.assertThat(instance.getPing()).isEqualTo("new_ping");
        Assertions.assertThat(instance.getPath()).isEqualTo("new_path");
        Assertions.assertThat(instance.getName()).isEqualTo("new_name");
    }

    @Test
    public void shouldBeAbleToStartAnInstance() {
        Instance instance = new Instance("name", ".", "http://localhost:8080/v1/ping");
        instance.setStart("test-start.sh");
        instance.start();
    }

    @Test
    public void shouldBeAbleToStopAnInstance() {
        Instance instance = new Instance("name", ".", "http://localhost:8080/v1/ping");
        instance.setStop("test-stop.sh");
        instance.stop();
    }

}
