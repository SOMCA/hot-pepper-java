/**
 * Hot-Pepper - Energy Measurements
 *     Copyright (C)  2016   Université du Québec à Montréal (UQAM) -  INRIA  - University of Lille
 *
 *     Authors: Mehdi Ait younes (overpex) <overpex@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.somca.scenarios;

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

public class CalabashScenarios implements Runnable{

    private String scenariosPath;
    private String appPath;
    private String logPath;


    public CalabashScenarios(String path, String app){
        this.scenariosPath = path;
        this.appPath = app;
        this.logPath = scenariosPath+"/logtest";
    }

    // Write log file
    private void logger(List<String> lines) {
        Path toSave = Paths.get(logPath+"/test.txt");
        try {
            Files.write(toSave, lines, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Log file generated");

    }

    @Override
    public void run() {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("bundle exec calabash-android run " + appPath,
                    null,
                    new File(scenariosPath));

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;
            List<String> lines = new ArrayList<String>();
            while((line=input.readLine()) != null) {
                lines.add(line);
            }

            // Add the code
            lines.add(String.format("Calabash process exit code : %s", pr.waitFor()));
            logger(lines);

        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
