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

package org.sfl;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.sfl.adb.AdbWrapper;
import org.sfl.adb.Device;
import org.sfl.utils.CsvUtils;
import org.sfl.utils.YoctoDevice;
import org.sfl.scenarios.CalabashScenarios;
import org.sfl.server.NagaViper;

import java.io.IOException;

import static java.lang.System.exit;

public class main {
    public static void main(String[] args) throws InterruptedException, IOException {

        Namespace currentArg = argsParser(args); // Argument parser
        argsCheck(currentArg); // Argument check

        int totalRun = currentArg.get("nrun");
        String argScenarioPath = currentArg.getString("scenariosPath");
        String argApkPath = currentArg.getString("apk");

        int nRun = 0;

        // Just for tests, must be removed or commented before tests bench
        System.exit(0);

        // ADB init
        AdbWrapper adbInstance = new AdbWrapper();

        // Use root communication
        adbInstance.asRoot();
        Thread.sleep(500);

        adbInstance.chargeSwitch(0); // Turn off the charge
        Thread.sleep(500);
        System.out.println("ADB charge disabled !");


        // TODO : Not good, change this !!! (Actually, we just have one device)
        Device device = adbInstance.getDevices().get(0);

        // Yocto-amp init
        YoctoDevice myYocto = new YoctoDevice();

        // Start Naga Viper Server
        NagaViper nagaServer = new NagaViper(myYocto, device);
        nagaServer.start();

        while (nRun < totalRun) {
            // Init the Calabash Scenarios Thread
            Thread scThread = new Thread(new CalabashScenarios(argScenarioPath, argApkPath, nRun));

            try {
                System.out.println("Calabash-Android started ...");

                scThread.start();
                scThread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Csv saving
            CsvUtils.testWriter(argScenarioPath, nagaServer.getLastMeasurementsData(), nRun);
            Thread.sleep(5000);
            System.out.println("Run "+nRun+" end");

            nRun++;
        }

        adbInstance.chargeSwitch(1); // Re-enable the charge
        Thread.sleep(500);
        System.out.println("ADB charge re-enabled !");

        System.out.println("Experiments finished");
        exit(0);
    }

    public static Namespace argsParser(String[] args)
    {
        Namespace ns = null;

        ArgumentParser parser = ArgumentParsers.newArgumentParser("")
                .defaultHelp(true)
                .description("Hot-Pepper (Naga Viper), assessing Android app energy consumption");

        parser.addArgument("-n", "--nrun")
                .type(Integer.class)
                .setDefault(1)
                .help("Define the ");

        parser.addArgument("-a", "--apk")
                .required(true)
                .help("Set the location of your apk subject");

        parser.addArgument("-st", "--scenariosType")
                .setDefault("Calabash")
                .choices("Calabash", "Espresso","Monkey")
                .help("Set the framework test type");

        parser.addArgument("-sp", "--scenariosPath")
                .help("Set the location path of your scenarios");

        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        return ns;
    }

    public static void argsCheck(Namespace ns)
    {
        if(ns.getString("scenariosType").equals("Calabash") && ns.getString("scenariosPath") == null){
            System.err.println("Scenarios type is set to Calabash," +
                    " you must specified a Calabash project path with the -sp command");
            System.exit(1);
        }

        ns.getAttrs().forEach((arg , val) -> System.out.println(arg + " : " + val));
    }
}
