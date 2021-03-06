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

package org.sfl.server;

import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YCurrent;
import org.sfl.adb.Device;
import org.sfl.utils.ConsoleColor;
import org.sfl.utils.YoctoDevice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedHashMap;


public class YMeasurement extends Thread{

    private YoctoDevice yocto;
    private Device currentDevice;
    private Socket calabashClient;
    private NagaViper serverInstance;

    private LinkedHashMap<Long,Double> measurementData;

    public YMeasurement (Socket client, YoctoDevice amp, Device device, NagaViper naga) {
        this.calabashClient = client;
        this.yocto = amp;
        this.currentDevice = device;
        this.serverInstance = naga;
    }

    @Override
    public void run() {
        System.out.println(String.format("Client - Address :%s", calabashClient.getRemoteSocketAddress()));

        // Buffer size 1KB
        byte[] buffer = new byte[1024];
        int read = -1;

        System.out.println("Waiting for orders ...");
        TMeasurement measure = new TMeasurement(yocto);

        try {
            while ((read = calabashClient.getInputStream().read(buffer)) > -1) {
                String signal = clientDataRead(buffer,read);

                if(signal.equals("STARTED"))
                {
                    measure.start();
                    System.out.println("Measurement Started");
                } else if (signal.equals("END"))
                {
                    System.out.print("Measurements "+ ConsoleColor.GREEN+"[Done]\n"+ConsoleColor.RESET);
                    measure.setFinished();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saving Measurements ....");

        // Save the measurement data to the server.
        serverInstance.getMeasurementsData().add(measurementData);
    }

    public String clientDataRead(byte[] buffer, int size)
    {
        byte[] redData = new byte[size];
        System.arraycopy(buffer, 0, redData, 0, size);
        try {
            return new String(redData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(-1);
            return "";
        }
    }

    public LinkedHashMap<Long, Double> getMeasurementData() {return measurementData;}

    /**
     * TODO : Change the measurement way (avoid the inner class)
     * The TMeasurement class allow the async run of the measurement method
     * (implemented on the run's method
     */
    private class TMeasurement extends Thread
    {
        private YoctoDevice yocto;

        private long initTime;
        private boolean isFinished;

        public TMeasurement (YoctoDevice amp) {this.yocto = amp;}

        @Override
        public void run() {
            measurementData = new LinkedHashMap<Long, Double>();
            YCurrent dcCurrent = YCurrent.FindCurrent(yocto.getDcSerial());
            initTime = new Date().getTime();
            for (;;) {
                long tmpTime = new Date().getTime() - initTime;
                try {
                    System.out.println(String.format("Time %s, Value %s", tmpTime, dcCurrent.get_currentValue()));
                    //YAPI.Sleep(100);
                    measurementData.put(tmpTime,dcCurrent.get_currentValue());
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
                if (isFinished) break;
            }
        }

        public void setFinished(){this.isFinished = true;}
    }
}
