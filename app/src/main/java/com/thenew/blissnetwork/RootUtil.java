package com.thenew.blissnetwork;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RootUtil {

    // Executes a command and returns the terminal output as a list of strings
    public static List<String> executeWithOutput(String command) {
        List<String> output = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();

            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    // Standard execution for commands that don't need to return text (like connecting)
    public static void execute(String command) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

