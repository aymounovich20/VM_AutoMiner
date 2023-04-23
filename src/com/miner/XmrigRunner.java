package com.miner;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class XmrigRunner {

    public static void main(String[] args) throws JSchException, IOException, InterruptedException {
    	Scanner sc = new Scanner(System.in);
    	System.out.println("----This is an auto configuration of VM mining---- \n");
    	System.out.println("VM IP address: ");
        String ipAddress = sc.next();
        System.out.println("VM username: ");
        String username = sc.next();
        System.out.println("VM password: ");
        String password = sc.next();
        System.out.println("miner worker name: ");
        String workerName = sc.next();
		String command = "sudo apt-get install screen && wget https://github.com/xmrig/xmrig/releases/download/v6.19.2/xmrig-6.19.2-focal-x64.tar.gz && tar -xvf xmrig-6.19.2-focal-x64.tar.gz && cd xmrig-6.19.2 && screen -dmS xmrig_session ./xmrig -o pool.supportxmr.com:443 -u 8AwA22ufebgYKj33FEanCUbS1NdPkpLGHYrZXm34X1iVQSyKnTmek3bX1sutckKMMwNqq7g8mQbJFP1yY55H4R5MKGsCNpH -k --tls -p "
				+ workerName;

        // Create a JSch session to the remote machine
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, ipAddress, 22);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        System.out.println("Connecting to remote machine...");
        session.connect();
        System.out.println("Connected to remote machine.");

        // Open a shell channel
        ChannelExec shell = (ChannelExec) session.openChannel("exec");
        InputStream shellIn = shell.getInputStream();
        shell.setCommand(command);
        shell.connect();
        System.out.println("Opened shell in remote machine.");

        // Read output from shell
        BufferedReader reader = new BufferedReader(new InputStreamReader(shellIn));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Wait for Xmrig process to complete
        int exitStatus = -1;
        while (true) {
            try {
                Thread.sleep(5000); // Wait for 5 seconds before checking again
                exitStatus = shell.getExitStatus();
                if (exitStatus != -1) {
                    break;
                }
                System.out.println("Xmrig process is still running...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Xmrig process completed with exit status: " + exitStatus);

        // Close channel and session
        shell.disconnect();
        session.disconnect();
        System.out.println("Disconnected from remote machine.");
    }
}
