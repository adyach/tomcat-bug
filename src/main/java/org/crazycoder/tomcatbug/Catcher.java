package org.crazycoder.tomcatbug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class Catcher {

    public static void main(String[] args) {
        Random random = new Random();
        while (true) {
            HttpURLConnection conn = null;
            BufferedReader bufferedReader = null;
            try {
                conn = (HttpURLConnection) new URL("http://localhost:8080/echo/bug").openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setChunkedStreamingMode(-1);
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    System.out.format("Response code is not 200, but %s", responseCode).println();
                    continue;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                long timeout = Math.max(500, random.nextInt(3000));
                long stopAt = System.currentTimeMillis() + timeout;
                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.equals("bug")) {
                        System.out.format("Error! Expected `bug`, but was %s", line).println();
                    }
                    if (stopAt <= System.currentTimeMillis()) {
                        System.out.format("Timeout %s, reconnecting", timeout).println();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
