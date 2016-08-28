package org.somca;

import org.somca.yoctamp.YoctoDevice;

/**
 * Created by overpex on 10/08/16.
 */
public class main {
    public static void main(String[] args)
    {
        YoctoDevice myYocto = new YoctoDevice();
        System.out.println(myYocto.ToString());
        //myYocto.run();
    }
}
