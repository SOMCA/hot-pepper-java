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

package org.sfl.utils;

import com.yoctopuce.YoctoAPI.*;

public class YoctoDevice {

    private YCurrent yoctoCurrent;

    private String deviceId;
    private String deviceName;
    private String deviceSerialNum;
    private String dcSerial;

    public YoctoDevice(){
        try {
            // Setup the API to use local VirtualHub
            YAPI.RegisterHub("127.0.0.1");
            System.out.println("Connection with VirtualHub succeed!");
        } catch (YAPI_Exception ex) {
            System.out.println("Connexion to 127.0.0.1 failed : " + ex.getLocalizedMessage());
            System.exit(1);
        }

        initVar();
    }

    /*
    Initialize both the YCurrent and the serial number of the device connected
     */
    private void initVar() {
        yoctoCurrent = YCurrent.FirstCurrent();

        if (yoctoCurrent == null) {
            System.out.println(" YCurrent is null, no module detected : Please check USB cable !");
            System.exit(1);
        }
        try {
            deviceSerialNum = yoctoCurrent.module().get_serialNumber();
            deviceName = yoctoCurrent.module().getLogicalName();
            deviceId = yoctoCurrent.module().getHardwareId();
            dcSerial = deviceSerialNum + ".current1";
            yoctoCurrent.set_reportFrequency("75/s");
            System.out.println("DC sensor of the device : " + dcSerial);
        } catch (YAPI_Exception ex) {
            System.out.println("No serial number : Please check USB cable !");
            System.exit(1);
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public YCurrent getYoctoCurrent() {
        return yoctoCurrent;
    }

    public void setYoctoCurrent(YCurrent yoctoCurrent) {
        this.yoctoCurrent = yoctoCurrent;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSerialNum() {
        return deviceSerialNum;
    }

    public void setDeviceSerialNum(String deviceSerialNum) {
        this.deviceSerialNum = deviceSerialNum;
    }

    public String getDcSerial() {
        return dcSerial;
    }

    public void setDcSerial(String dcSerial) {
        this.dcSerial = dcSerial;
    }

    public String toString(){
        return "Device ID : "+ deviceId +"\n"
                +"Device Name : "+ deviceName +"\n"
                +"Serial Numb : "+ deviceSerialNum +"\n";
    }

}
