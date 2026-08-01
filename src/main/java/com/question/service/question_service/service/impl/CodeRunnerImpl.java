package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.response.CodeRunnerResponse;
import com.question.service.question_service.service.CodeRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CodeRunnerImpl implements CodeRunner {


    @Override
    public CodeRunnerResponse javaCodeRun(String code, String input, int timeoutSeconds) throws IOException {
        // 1. Create temp directory
        Path jobDir = Files.createTempDirectory("job-");
        Path codeFile = jobDir.resolve("Main.java");
        Path inputFile = jobDir.resolve("input.txt");

        try {
            // 2. Write code and input to files
            Files.writeString(codeFile, code);
            Files.writeString(inputFile, input != null ? input : null);

            // 3. Build docker command
            List<String> command = List.of(
                    "docker", "run", "--rm",
                    "--memory=256m",          // RAM limit
                    "--cpus=0.5",             // CPU limit
                    "--network=none",         // no internet
                    "-v", jobDir.toAbsolutePath() + ":/app",  // mount code
                    "eclipse-temurin:21-jdk-alpine",        // Java image
                    "sh", "-c",
                    "cd /app && javac Main.java 2>&1 && java -cp /app Main < /app/input.txt"
            );

            // 4. Run docker process
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 5. Capture output
            String output;
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }
            System.out.println("Output : "+output);
            // 6. Wait with timeout
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            CodeRunnerResponse response = new CodeRunnerResponse();

            if (!finished) {
                process.destroyForcibly();
                return response;
            }

            int exitCode = process.exitValue();

            return response;


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 7. Cleanup temp files
            Files.walk(jobDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try { Files.delete(path); } catch (Exception ignored) {}
                    });
        }
    }

    @Override
    public CodeRunnerResponse pythonCodeRun(String code, String input, int timeoutSeconds) {
        return null;
    }

    @Override
    public CodeRunnerResponse javaScriptCodeRun(String code, String input, int timeoutSeconds) {
        return null;
    }

    @Override
    public CodeRunnerResponse typeScriptCodeRun(String code, String input, int timeoutSeconds) {
        return null;
    }

    @Override
    public CodeRunnerResponse cCodeRun(String code, String input, int timeoutSeconds) {
        return null;
    }

    @Override
    public CodeRunnerResponse cppCodeRun(String code, String input, int timeoutSeconds) {
        return null;
    }
}
