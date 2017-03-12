package fm.pattern.cycle;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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
        String directory = getRootDirectory(System.getProperty("user.dir"));
        String script = directory + instance.getPath() + instance.getStart();

        log.info("Starting " + instance.getName());
        log.info("Using start script: " + script);

        execute(script, directory + instance.getPath());
    }

    public static void stop(Instance instance) {
        String directory = getRootDirectory(System.getProperty("user.dir"));
        String script = directory + instance.getPath() + instance.getStop();

        log.info("Stopping " + instance.getName());
        log.info("Using stop script: " + script);

        execute(script, directory + instance.getPath());
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

    private static void execute(String script, String directory) {
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/sh", script);
            builder.directory(new File(directory));

            Map<String, String> map = builder.environment();
            String path = map.get("PATH");
            String newPath = path += ":/usr/local/bin";
            map.put("PATH", newPath);

            builder.redirectErrorStream(true);

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                log.debug(line);
            }

            process.waitFor();
            int exitValue = process.exitValue();
            process.destroy();

            if (exitValue != 0 && exitValue != 7) {
                throw new InstanceManagementException("The script finished with exit code: " + exitValue);
            }
        }
        catch (Exception e) {
            throw new InstanceManagementException("Failed to execute script: ", e);
        }
    }

    private static String getRootDirectory(String currentDirectory) {
        String userRootDirectory1 = currentDirectory.substring(0, currentDirectory.lastIndexOf("/"));
        String userRootDirectory2 = userRootDirectory1.substring(0, userRootDirectory1.lastIndexOf("/"));
        return userRootDirectory2 + "/";
    }

}
