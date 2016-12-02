# Hot-pepper-java
An automated test bench for asses the power consumption of Android apps (Java version).

Additional features
-------------------

* Rewrite with a strong typing language, more robust and efficient
* Easy communication with Spoon processors
* ...

Packages
--------

* **hub/** : Virtual Hub platform installer
* **org.somca.adb** : The Android device bridge wrapper (for the communication with the phone device)
* **org.somca.scenarios** : User's scenarios support
* **org.somca.server** : Naga Viper Server
* **org.somca.utils** : Utils for the hole project

Environment
-----------
You need to add your sdk location into the **ANDROID_HOME** var<br />
``` export ANDROID_HOME=.../sdk/android-sdk-linux ```<br /><br />
Calabash-Android for the user's scenarios test<br />
[Calabash-Android](https://github.com/calabash/calabash-android) <br />
Note that if you use Calabash-Android, you will need to add the **INTERNET** permission to your app <br />
```<uses-permission android:name="android.permission.INTERNET" />```<br /><br />
Java version : Java 1.8 (JDK 8)<br /><br />

Virtual Hub Configuration
-------------------------
* As Java dose not allow direct access to the hardware, you'll need to setup a Virtual Hub on the computer where Hot-pepper is used.

## Virtual Hub installation(Windows)

* Unzip the compressed file on **hub/** folder
* Run the executable on **32bits** or **64bits**

## Virtual Hub installation(Linux)

 Unzip the compressed file on **hub/** folder and copy the binary to the directory **/usr/sbin/** and make it executable.<br />
    ``` cp 64bits/VirtualHub /usr/sbin/ ``` and ``` chmod +x /usr/sbin/VirtualHub ```<br /><br />
 Copy the file **startup_script/yVirtualHub** to **/etc/init.d/** and make it executable.<br />
    ``` cp startup_script/yVirtualHub /etc/init.d/ ``` and ``` chmod +x /etc/init.d/yVirtualHub ```<br /><br />
 Set this service to be started at boot **(optional)**<br />
    ``` update-rc.d yVirtualHub defaults ```<br /><br />
 Restart the system.<br />

### Optional
 You need to create a new **udev** rule to run the VirtualHub without root access.<br /><br />
 Copy the **51-yoctopuce_all.rules** from **udev_conf** folder of the archive to **/etc/udev/rules.d/**<br />
 ``` cp udev_conf/1-yoctopuce_all.rules /etc/udev/rules.d/```<br /><br />
 Restart the system.
 
Run Hot-Pepper
--------------
...

Credits
-------

[SOMCA](http://sofa.uqam.ca/somca.php) - Associate research team between :

* [UQÀM](http://www.uqam.ca)
* [Inria](http://www.inria.fr)

License
-------

GNU AFFERO GENERAL PUBLIC LICENSE (Version 3)
