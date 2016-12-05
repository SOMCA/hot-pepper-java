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

public class CalabashScenarios implements Runnable, SimpleLogger{

    private String scenariosPath;
    private String appPath;
    private File logPath;

    private int nRun;


    public CalabashScenarios(String path, String app, int run){
        this.scenariosPath = path;
        this.appPath = app;
        this.nRun = run;

        this.logPath = new File(scenariosPath+"/logtest");
        if(!logPath.exists())
        {
            logPath.mkdir();
            System.out.println("Calabash log tests directory created on : " + logPath.getAbsolutePath());
        }
    }

    @Override
    public void run() {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("bundle exec calabash-android run " + appPath,
                    null,
                    new File(scenariosPath)); // position on the calabash path

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;
            List<String> lines = new ArrayList<String>();
            while((line=input.readLine()) != null) {
                lines.add(line);
            }

            // Add the exit code
            lines.add(String.format("Calabash process exit code : %s", pr.waitFor()));
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
