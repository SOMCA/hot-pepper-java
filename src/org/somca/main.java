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

package org.somca;

import org.somca.adb.AdbWrapper;
import org.somca.adb.Device;
import org.somca.utils.CsvUtils;
import org.somca.utils.YoctoDevice;
import org.somca.scenarios.CalabashScenarios;
import org.somca.server.NagaViper;

import java.io.IOException;

import static java.lang.System.exit;

public class main {
    public static void main(String[] args) throws InterruptedException, IOException {

        int nRun = 1;

        String argScenarioPath = "$SCENARIOS_PATH";
        String argApkPath = "$APK_PATH";

        // ADB init
        AdbWrapper adbInstance = new AdbWrapper();

        // Use root communication
        adbInstance.asRoot();
        Thread.sleep(500);

        // Turn charge off
        adbInstance.chargeSwitch(0);
        Thread.sleep(500);
        System.out.println("ADB charge disabled !");

        // TODO : Not good, change this !!! (Actually, we just have one device)
        Device device = adbInstance.getDevices().get(0);

        // Yocto-amp init
        YoctoDevice myYocto = new YoctoDevice();

        // Start Naga Viper Server
        NagaViper nagaServer = new NagaViper(myYocto, device);
        nagaServer.start();

        while (nRun != 0) {
            // Init the Calabash Scenarios Thread
            Thread scThread = new Thread(new CalabashScenarios(argScenarioPath, argApkPath));

            try {
                // Calabash-Android
                scThread.start();

                scThread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Run end");
            //System.out.println("Measurement saving on ");
            Thread.sleep(8000);

            // Csv saving
            CsvUtils.testWriter(argScenarioPath, nagaServer.getLastMeasurementsData(), nRun);

            nRun--;
        }

        adbInstance.chargeSwitch(1);
        Thread.sleep(500);
        System.out.println("ADB charge re-enabled !");

        System.out.println("Experiments finished");
        exit(0);

    }
}
