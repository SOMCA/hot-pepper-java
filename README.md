# Hot-pepper-java
This is a java version of Hot-pepper framework.

Additional features
-------------------

* Rewrite with a static language, more robust and efficient
* Easy communication with Spoon processors
* ...

Packages
--------

* **hub/** : 
* **org.somca.yoctamp** : 

Configuration
-------------

* Yon need to get the yoctoAPI.jar from [yoctopuce](http://www.yoctopuce.com/EN/products/usb-environmental-sensors/yocto-voc)
* As Java dose not allow direct access to the hardware, you'll need to setup a Virtual Hub on the computer where Hot-pepper is used.

## Virtual Hub installation(Windows)

* Unzip the compressed file on **hub/** folder
* Run the executable on **32bits** or **64bits**

## Virtual Hub installation(Linux)

* Unzip the compressed file on **hub/** folder and copy the binary to the directory **/usr/sbin/** and make it executable.<br />
    ``` cp 64bits/VirtualHub /usr/sbin/ ```<br />
    ``` chmod +x /usr/sbin/VirtualHub ```<br />
* Copy the file **startup_script/yVirtualHub** to **/etc/init.d/** and make it executable.<br />
    ``` cp startup_script/yVirtualHub /etc/init.d/ ```<br />
    ``` chmod +x /etc/init.d/yVirtualHub ```<br />
* Set this service to be started at boot **(optional)**<br />
    ``` update-rc.d yVirtualHub defaults ```
* Restart the system.

### Optional
* You need to create a new **udev** rule to run the VirtualHub without root access.
* Copy the **51-yoctopuce_all.rules** from **udev_conf** folder of the archive to **/etc/udev/rules.d/**<br />
 ``` cp udev_conf/1-yoctopuce_all.rules /etc/udev/rules.d/```
* Restart the system.

Credits
-------

[SOMCA](http://sofa.uqam.ca/somca.php) - Associate research team between :

* [UQÀM](http://www.uqam.ca)
* [Inria](http://www.inria.fr)

License
-------

GNU AFFERO GENERAL PUBLIC LICENSE (Version 3)
