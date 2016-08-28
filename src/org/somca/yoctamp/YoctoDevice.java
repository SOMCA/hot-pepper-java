package org.somca.yoctamp;

import com.yoctopuce.YoctoAPI.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by overpex on 10/08/16.
 */
public class YoctoDevice {

    private YCurrent yoctoCurrent;
    private String deviceId;
    private String deviceName;
    private String deviceSerialNum;
    private String dcSerial;

    private List<Double> data;
    private boolean isFinished = false;

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
    private void initVar()
    {
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
            System.out.println("DC sensor of the device : " + dcSerial);
        } catch (YAPI_Exception ex) {
            System.out.println("No serial number : Please check USB cable !");
            System.exit(1);
        }
    }

    /*
    Run the measures
     */
    public void run()
    {
        data = new ArrayList<Double>();
        YCurrent dcCurrent = YCurrent.FindCurrent(dcSerial);
        while (!isFinished)
        {
            try {
                data.add(dcCurrent.get_currentValue());
                System.out.println(dcCurrent.get_currentValue());
                //YAPI.Sleep(500);
            } catch (YAPI_Exception ex) {
                System.err.println("Error during measurement : " + ex.getLocalizedMessage());
                System.exit(1);
            }
        }
        System.out.println("Measurements "+"\u001B[32m[Done]");
    }

    /*
    Set the is finished var to true in order to end the measurements.
     */
    public void setFinished(){this.isFinished = true;}

    public String ToString(){
        return "Device ID : "+ deviceId +"\n"
                +"Device Name : "+ deviceName +"\n"
                +"Serial Numb : "+ deviceSerialNum +"\n";
    }


}
