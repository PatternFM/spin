package fm.pattern.cycle;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public final class InstanceManagementService {

    private static final Logger log = LoggerFactory.getLogger(InstanceManagementService.class);

    private InstanceManagementService() {

    }

    public static void start(Instance instance) {
        String directory = resolveTargetDirectory(instance.getPath());
        String script = directory + instance.getStart();

        log.info("Starting " + instance.getName());
        log.info("Using start script: " + script);

        execute(script, directory, instance.getEnvironment());
    }

    public static void stop(Instance instance) {
        String directory = resolveTargetDirectory(instance.getPath());
        String script = directory + instance.getStop();

        log.info("Stopping " + instance.getName());
        log.info("Using stop script: " + script);

        execute(script, directory, instance.getEnvironment());
    }

    public static boolean isRunning(Instance instance) {
        try {
            HttpResponse<String> response = Unirest.get(instance.getPing()).asString();
            return response.getStatus() == 200;
        }
        catch (UnirestException e) {
            return false;
        }
    }

    private static void execute(String script, String directory, Map<String, String> environment) {
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/sh", script);
            builder.directory(new File(directory));
            builder.redirectErrorStream(true);
            configureEnvironment(builder, environment);

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                log.debug(line);
            }

            Integer exitValue = process.waitFor();
            process.destroy();

            if (exitValue != 0 && exitValue != 7) {
                String message = "Script '" + script + "'completed with exit code: " + exitValue;
                log.error(message);
                throw new InstanceManagementException(message);
            }
        }
        catch (Exception e) {
            log.error("Failed to execute script:", e);
            throw new InstanceManagementException("Failed to execute script: ", e);
        }
    }

    private static String appendTrailingSlashIfNotPresent(String path) {
        return String.valueOf(path.charAt(path.length() - 1)).equals("/") ? path : path + "/";
    }

    private static String resolveTargetDirectory(String relativePath) {
        Path base = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
        Path resolved = base.resolve(relativePath);
        return appendTrailingSlashIfNotPresent(resolved.normalize().toAbsolutePath().toString());
    }

    private static void configureEnvironment(ProcessBuilder builder, Map<String, String> environment) {
        Map<String, String> map = builder.environment();
        map.put("PATH", map.get("PATH") + ":/usr/local/bin");
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

}
