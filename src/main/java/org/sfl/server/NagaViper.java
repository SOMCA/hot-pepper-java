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

import org.sfl.adb.Device;
import org.sfl.utils.YoctoDevice;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class NagaViper extends Thread{

    //TODO : Change this in ...
    private List<LinkedHashMap<Long,Double>> measurementsData = new ArrayList<>();

    private final static int DEFAULT_PORT = 3000;
    private final static int DEFAULT_TIMEOUT = (int) 7.2e+6; // Time in milliseconds (two hours)
    private static boolean SERVER_STATE = false;
    private ServerSocket listenSocket;

    private Device device;
    private YoctoDevice yoctoDevice;

    public NagaViper(int port, int timeOut, YoctoDevice y, Device d) throws IOException {
        listenSocket = new ServerSocket(port);
        listenSocket.setSoTimeout(timeOut);
        SERVER_STATE = true;
        initComponents(y, d);
    }

    public NagaViper(YoctoDevice y, Device d) throws IOException {
        listenSocket = new ServerSocket(DEFAULT_PORT);
        listenSocket.setSoTimeout(DEFAULT_TIMEOUT);
        SERVER_STATE = true;
        initComponents(y, d);
    }

    private void initComponents(YoctoDevice y, Device d){
        this.yoctoDevice = y;
        this.device = d;
    }

    /**
     * A multi-threaded server
     * Each Thread is instantiated with a client socket
     * The measurement part is done by the Measurement class which extends Thread
     * This allow multiple scenarios connection to the server.
     */
    @Override
    public void run() {
        System.out.println("NAGA VIPER Server : Running ...");
        while (SERVER_STATE) {
            try {
                // Waiting for a client connection with the server.
                Thread measurementRun = new YMeasurement(listenSocket.accept(), yoctoDevice, device, this);
                measurementRun.start();

            }catch (SocketTimeoutException t)
            {
                SERVER_STATE = false;
                System.out.print("Time out");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<LinkedHashMap<Long, Double>> getMeasurementsData() {
        return measurementsData;
    }

    public LinkedHashMap<Long, Double> getLastMeasurementsData() {
        return measurementsData.get(measurementsData.size() - 1);
    }

    public void setMeasurementsData(List<LinkedHashMap<Long, Double>> measurementsData) {
        this.measurementsData = measurementsData;
    }
}