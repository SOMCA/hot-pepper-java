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
import java.util.List;

public class EspressoScenarios implements Runnable, SimpleLogger{

    private String apkPath;
    private String testPackageName;
    private String outputDir;
    private File logPath;

    private int nRun;

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
    }

    public void apkInstallation()
    {
        Runtime rt = Runtime.getRuntime();
        try {
            // Stop the test process if it exist
            // adb shell am force-stop com.example.overpex.espressoapp (test name package)
            Process pr = rt.exec(String.format("%sadb shell am force-stop %s", AdbWrapper.sdkPlatformTools, testPackageName));
            System.out.println("Stop the process of the previous test");
            pr.waitFor();

            // push the test apk on the device
            // adb push ../app-debug-androidTest.apk /data/local/tmp/com.example.overpex.espressoapp.test
            pr = rt.exec(String.format("%sadb push %s /data/local/tmp/%s.test", AdbWrapper.sdkPlatformTools, apkPath,testPackageName));
            System.out.println("Pushing the test app on the device ...");
            pr.waitFor();

            // Install the test application
            // adb shell pm install -r "/data/local/tmp/com.example.overpex.espressoapp.test"
            pr = rt.exec(String.format("%sadb shell pm install -r \"/data/local/tmp/%s.test\"", AdbWrapper.sdkPlatformTools,testPackageName));
            System.out.println("Pushing the test app on the device ...");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        // Run the instrumentation test
        //adb shell am instrument -w -r -e debug false com.example.overpex.espressoapp.test/android.support.test.runner.AndroidJUnitRunner

        try {
            Runtime rt = Runtime.getRuntime();
            //Process pr = rt.exec("bundle exec calabash-android run " + appPath);
            Process pr = rt.exec(String.format("%sadb shell am instrument -w -r -e debug false com.example.overpex.espressoapp.test/android.support.test.runner.AndroidJUnitRunner", AdbWrapper.sdkPlatformTools));

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;
            List<String> lines = new ArrayList<String>();
            while((line=input.readLine()) != null) {
                lines.add(line);
            }

            // Add the exit code
            lines.add(String.format("Espresso process exit code : %s", pr.waitFor()));
            logGenerator(lines);

        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void logGenerator(List<String> lines) {
        Path toSave = Paths.get(logPath.getAbsolutePath()+"/log_run_"+nRun+".txt");
        try {
            Files.write(toSave, lines, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Log file generated for the "+nRun+" run");
    }
}
