package com.miner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Shell {
    private final Process process;
    private final BufferedReader inputStream;

    public Shell(Process process) {
        this.process = process;
        this.inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    public void execute(String command) throws IOException {
        process.getOutputStream().write((command + "\n").getBytes());
        process.getOutputStream().flush();
    }

    public void waitComplete() throws InterruptedException {
        process.waitFor();
        synchronized (this) {
            notify();
        }
    }

    public String readOutput() throws IOException {
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = inputStream.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}
