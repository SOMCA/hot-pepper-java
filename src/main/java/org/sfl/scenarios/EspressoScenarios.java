/**
 *  Copyright (C) 2014-2016 Savoir-faire Linux Inc.
 *
 *  Author : Mehdi Ait-Younes (overpex) <overpex@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA.
 */


package org.sfl.scenarios;

import org.sfl.adb.AdbWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EspressoScenarios extends Scenarios implements Runnable {

    private String apkPath;
    private String testPackageName;
    private String outputDir;
    private File logPath;

    private int nRun;
    private LinkedHashMap<String, List<String>> outProcess = new LinkedHashMap<>();


    public EspressoScenarios(String apk, String testName, int run, String output)
    {
        this.apkPath = apk;
        this.testPackageName = testName;
        this.outputDir = output;
        this.nRun = run;

        this.logPath = new File(outputDir+"/Espresso_Log_Test");
        if(!logPath.exists())
        {
            logPath.mkdir();
            System.out.println("Espresso log tests directory created on : " + logPath.getAbsolutePath());
        }

        apkInstallation();
    }

    public void apkInstallation()
    {
        Runtime rt = Runtime.getRuntime();
        try {
            // Stop the test process if it exist
            // adb shell am force-stop com.example.overpex.espressoapp (test name package)
            Process pr = rt.exec(String.format("%sadb shell am force-stop %s", AdbWrapper.sdkPlatformTools, testPackageName));
            pr.waitFor();
            processOutput(pr, "Process stop");

            // push the test apk on the device
            // adb push ../app-debug-androidTest.apk /data/local/tmp/com.example.overpex.espressoapp.test
            pr = rt.exec(String.format("%sadb push %s /data/local/tmp/%s.test", AdbWrapper.sdkPlatformTools, apkPath,testPackageName));
            pr.waitFor();
            processOutput(pr, "APK push");

            // Install the test application
            // adb shell pm install -r "/data/local/tmp/com.example.overpex.espressoapp.test"
            pr = rt.exec(String.format("%sadb shell pm install -r /data/local/tmp/%s.test", AdbWrapper.sdkPlatformTools,testPackageName));
            pr.waitFor();
            processOutput(pr, "APK installation");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void processOutput(Process p, String pName)
    {
        List<String> out = new ArrayList<>();
        String getLine = null;

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            while ((getLine = input.readLine()) != null) {
                out.add(getLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        outProcess.put(pName, out);
    }

    @Override
    public void run() {

        // Run the instrumentation test
        //adb shell am instrument -w -r -e debug false com.example.overpex.espressoapp.test/android.support.test.runner.AndroidJUnitRunner

        apkInstallation();
        System.out.println("APK installation and set end");

        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(String.format("%sadb shell am instrument -w -r -e debug false com.example.overpex.espressoapp.test/android.support.test.runner.AndroidJUnitRunner", AdbWrapper.sdkPlatformTools));

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;
            logScenarios = new ArrayList<String>();
            while((line=input.readLine()) != null) {
                logScenarios.add(line);
            }

            // Add the exit code
            logScenarios.add(String.format("Espresso process exit code : %s", pr.waitFor()));

        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void logGenerator(int run) {
        List<String> toPush = new ArrayList<>();

        Path toSave = Paths.get(logPath.getAbsolutePath()+"/log_run_"+run+".txt");
        Path processToSave = Paths.get(logPath.getAbsolutePath()+"/process_set_"+run+".txt");

        try {
            // Scenario log generation
            Files.write(toSave, logScenarios, Charset.defaultCharset());

            // Process Log generation
            for (Map.Entry<String, List<String>> e : outProcess.entrySet())
            {
                String tmp = e.getKey() + " : \n" + String.join("\n", e.getValue()) + "\n";
                toPush.add(tmp);
            }

            Files.write(processToSave, toPush, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
