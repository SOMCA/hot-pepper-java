/**
 * Hot-Pepper - Energy Measurements
 *     Copyright (C)  2016   Université du Québec à Montréal (UQAM) -  INRIA  - University of Lille
 *
 *     Authors: Mehdi Ait younes <overpex@gmail.com>
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

package org.somca;

import com.yoctopuce.YoctoAPI.YAPI_Exception;
import org.somca.adb.AdbWrapper;
import org.somca.api.CsvUtils;
import org.somca.api.YoctoDevice;
import org.somca.scenarios.CalabashScenarios;
import static java.lang.System.exit;

public class main {
    public static void main(String[] args) throws InterruptedException {

        int nRun = 1;

        String argScenarioPath = "$SCENARIOS_PATH";
        String argApkPath = "$APK_PATH";

        // ADB init ...
        AdbWrapper adbInstance = new AdbWrapper();

        // Use root communication
        adbInstance.asRoot();
        Thread.sleep(500);

        // Turn charge off
        adbInstance.chargeSwitch(0);
        Thread.sleep(500);
        System.out.println("ADB charge disabled !");

        // Server Running ...

        while (nRun != 0) {
            YoctoDevice myYocto = new YoctoDevice();
            Thread scThread = new Thread(new CalabashScenarios(argScenarioPath, argApkPath, myYocto));

            // Run Calabash Thread and Yocto measurements
            try {
                // Calabash-Android
                scThread.start();

                // Measurement
                myYocto.run();

                scThread.join();
            } catch (YAPI_Exception e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // The Yocto measurements is stopped on the CalabashScenarios class
            System.out.println("Run end");

            // Csv saving ...
            CsvUtils.testWriter(argScenarioPath,myYocto.getMeasurementData());

            nRun--;
        }

        adbInstance.chargeSwitch(0);
        Thread.sleep(500);
        System.out.println("ADB charge disabled !");

        System.out.println("Experiments finished");
        exit(0);

    }
}
