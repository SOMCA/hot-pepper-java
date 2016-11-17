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

package org.somca.server;

import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YCurrent;
import org.somca.adb.Device;
import org.somca.utils.ConsoleColor;
import org.somca.utils.YoctoDevice;

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

    public LinkedHashMap<Long,Double> measurementData;
    private long initTime;
    private boolean isFinished = false;

    public YMeasurement (Socket client, YoctoDevice amp, Device device, NagaViper naga) {
        this.calabashClient = client;
        this.yocto = amp;
        this.currentDevice = device;
        this.serverInstance = naga;
    }

    @Override
    public void run() {
        System.out.println(String.format("SERVER - Address :%s", calabashClient.getRemoteSocketAddress()));

        // Buffer size 1KB
        byte[] buffer = new byte[1024];
        int read = 0;

        try {
            while ((read = calabashClient.getInputStream().read(buffer)) > 0) {
                String signal = clientDataRead(buffer,read);
                switch (signal){
                    case "START" :
                        System.out.println("Calabash Start");
                        startMeasurements();
                        break;

                    case "END" :
                        setFinished();
                        System.out.print("Measurements "+ ConsoleColor.GREEN+"[Done]\n"+ConsoleColor.RESET);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save the measurement data to the server.
        serverInstance.getMeasurementsData().add(measurementData);
        System.exit(0);
    }

    private void startMeasurements(){
        measurementData = new LinkedHashMap<Long, Double>();
        YCurrent dcCurrent = YCurrent.FindCurrent(yocto.getDcSerial());
        initTime = new Date().getTime();
        for (;;) {
            long tmpTime = new Date().getTime() - initTime;
            try {
                System.out.println(String.format("Time %s, Value %s", tmpTime, dcCurrent.get_currentValue()));
                measurementData.put(tmpTime,dcCurrent.get_currentValue());
            } catch (YAPI_Exception e) {
                e.printStackTrace();
            }
            if (isFinished) break;
            //YAPI.Sleep(100);
        }
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

    public void setFinished(){this.isFinished = true;}

    public LinkedHashMap<Long, Double> getMeasurementData() {
        return measurementData;
    }
}
