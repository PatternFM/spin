package fm.pattern.spin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import fm.pattern.spin.config.SpinConfigurationException;

public final class InstanceManagementService {

    private static final Logger log = LoggerFactory.getLogger(InstanceManagementService.class);

    private InstanceManagementService() {

    }

    public static void start(Instance instance) {
        String startScript = instance.getStart();

        String relativePath = null;
        String filename = null;

        if (startScript.lastIndexOf(File.separator) == -1) {
            relativePath = "";
            filename = instance.getStart();
        }
        else {
            relativePath = startScript.substring(0, startScript.lastIndexOf(File.separator) + 1);
            filename = startScript.substring(startScript.lastIndexOf(File.separator) + 1, instance.getStart().length());
        }

        String directory = resolveTargetDirectory(relativePath);
        String script = directory + filename;

        if (!Files.exists(Paths.get(directory))) {
            throw new SpinConfigurationException("Unable to find start script '" + script + "' - the specified directory does not exist.");
        }
        if (!Files.exists(Paths.get(script))) {
            throw new SpinConfigurationException("Unable to find start script '" + script + "' - the directory is valid, but the filename '" + filename + "' could not be found.");
        }

        log.info("Starting " + instance.getName() + " using script: " + script);
        execute(script, directory, instance);
    }

    public static void stop(Instance instance) {
        String stopScript = instance.getStop();

        String relativePath = null;
        String filename = null;

        if (stopScript.lastIndexOf(File.separator) == -1) {
            relativePath = "";
            filename = instance.getStop();
        }
        else {
            relativePath = stopScript.substring(0, stopScript.lastIndexOf(File.separator) + 1);
            filename = instance.getStop().substring(stopScript.lastIndexOf(File.separator) + 1, stopScript.length());
        }

        String directory = resolveTargetDirectory(relativePath);
        String script = directory + filename;

        if (!Files.exists(Paths.get(directory))) {
            throw new SpinConfigurationException("Unable to find stop script '" + script + "' - the specified directory does not exist.");
        }
        if (!Files.exists(Paths.get(script))) {
            throw new SpinConfigurationException("Unable to find stop script '" + script + "' - the directory is valid, but the filename '" + filename + "' could not be found.");
        }

        log.info("Stopping " + instance.getName() + " using script: " + script);
        execute(script, directory, instance);
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

    private static void execute(String script, String directory, Instance instance) {
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/sh", script);
            builder.directory(new File(directory));
            builder.redirectErrorStream(true);

            configureEnvironment(builder, instance);

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                log.debug(line);
            }

            Integer exitValue = process.waitFor();

            process.destroy();
            reader.close();

            if (exitValue != 0 && exitValue != 7) {
                String message = "Script '" + script + "' completed with exit code: " + exitValue;
                log.error(message);
                throw new ScriptExecutionException(message);
            }
        }
        catch (Exception e) {
            log.error("Failed to execute script:", e);
            throw new ScriptExecutionException("Failed to execute script: ", e);
        }
    }

    private static String appendTrailingSlashIfNotPresent(String path) {
        return String.valueOf(path.charAt(path.length() - 1)).equals(File.separator) ? path : path + File.separator;
    }

    private static String resolveTargetDirectory(String relativePath) {
        Path base = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
        Path resolved = base.resolve(relativePath);
        return appendTrailingSlashIfNotPresent(resolved.normalize().toAbsolutePath().toString());
    }

    private static void configureEnvironment(ProcessBuilder builder, Instance instance) {
        Map<String, String> map = builder.environment();

        if (StringUtils.isNotBlank(instance.getPath())) {
            map.put("PATH", map.get("PATH") + instance.getPath());
        }

        for (Map.Entry<String, String> entry : instance.getEnvironment().entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

}
