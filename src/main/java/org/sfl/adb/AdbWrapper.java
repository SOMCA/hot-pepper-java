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

package org.sfl.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AdbWrapper {

    // Get the current runtime.
    private Runtime rt = Runtime.getRuntime();
    private Process pr;
    public final static String ANDROID_HOME_ENV = System.getenv("ANDROID_HOME");

    // Devices list.
    private List<Device> devicesConnected;
    // The current device used for the adb communication.
    private Device deviceOnUse;
    private List<String> processOutput;
    private static String sdkPlatformTools = ANDROID_HOME_ENV+"/platform-tools/";

    /*
    * By default, when the AdbWrapper object is created the adb server start automatically.
     */
    public AdbWrapper()
    {
        try {
            pr = rt.exec(String.format("%sadb start-servers", sdkPlatformTools));
            List<String> tmp = localOutput(pr);
            if (tmp.isEmpty()) {
                System.out.println("ADB server already started, the following devices are connected :");
            }
            else {
                tmp.forEach(System.out::println);
                System.out.println("ADB server started successfully");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * serverStatus : The method allow user to turn on or off the adb server.
    * int onOff :
    *   1 -> start the adb server
    *   2 -> Shut down the adb server
     */
    public void serverStatus(int onOff){
        try {
            switch (onOff){
                case 1 : pr  = rt.exec("%sadb kill-servers");
                    break;
                case 0 : pr = rt.exec("%sadb kill-servers");
                    break;
                default : System.err.println("Please choose [1] to start adb server or [0] to turn it off.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * start : The method starts the
     */
    public void start(String cmd, String[] listOfArgs) {}

    public void asRoot()
    {
        try {
            pr = rt.exec(String.format("%sadb root", sdkPlatformTools));
            Thread.sleep(100);
            localOutput(pr).forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException i){
            i.printStackTrace();
        }
    }

    public void chargeSwitch(int charge)
    {
        try {
            pr = rt.exec(String.format("%sadb shell echo %s > /sys/class/power_supply/usb/device/charge", sdkPlatformTools, charge));
            Thread.sleep(100);
            localOutput(pr).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException i){
            i.printStackTrace();
        }
    }

    /*
    getDevices : This method return the list of the devices currently connected
     */
    public List<Device> getDevices(){
        devicesConnected = new ArrayList<Device>();
        try {
            // Run the "adb devices" command line to get the current devices connected.
            try {
                pr = rt.exec(String.format("%sadb devices", sdkPlatformTools));
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<String> tmp = localOutput(pr);
            // To remove the empty element of the terminal at the end of list
            tmp.remove(0);

            for (String e : tmp)
            {
                String [] data = e.split("\t");
                if (data.length > 1) devicesConnected.add(new Device(data[1], data[0]));
            }

            return devicesConnected;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    localOutput : This method store the terminal output of any command in an List in order to display it locally.
     */
    public List<String> localOutput(Process p)
    {
        processOutput = new ArrayList<String>();
        String getLine = null;

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            while ((getLine = input.readLine()) != null) processOutput.add(getLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return processOutput;
    }
}
