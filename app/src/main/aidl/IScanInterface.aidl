// IScanInterface.aidl
package com.sunmi.scanner;

// Declare any non-default types here with import statements

interface IScanInterface {
 /**
     * 触发开始与停止扫码
     * key.getAction()==KeyEvent.ACTION_UP 触发开始扫码
     * key.getAction()==KeyEvent.ACTION_DWON 触发停止扫码
     */
    void sendKeyEvent(in KeyEvent key);
    /**
     * 触发开始扫码
     */
    void scan();
    /**
     * 触发停止扫码
     */
    void stop();
    /**
     * 获取扫码头类型
     * 100-->NONE
     * 101-->P2Lite
     * 102-->l2-newland
     * 103-->l2-zabra
     */
    int getScannerModel();
}
/*
2.  Connect Service（AIDL）

2.1.AIDL

AIDL is the abbreviation of Android Interface Definition language. It is a description language of
Android's internal process communication interface. Through it, we can define the communication
 interface between processes.

2.2.Use AIDL

Establishing a connection can be divided into the following 5 steps:

Add the AIDL file included in the resource file to your project.

Implement ServiceConnection in the code class that controls scan code.

Call ApplicationContext.bindService() and pass it in the ServiceConnection implementation.
Note: bindservice is a non-blocking call, meaning that the call is not completed immediately after
 the call is completed. ServiceConnected must prevail.

In the ServiceConnection.onServiceConnected() implementation, you receive an IBinder instance
(the invoked Service). Call IScanInterface.Stub.asInterface(service) to convert
 the argument to IScanInterface type.

Now you can call the methods defined in the IScanInterface interface.

Binding service example：

private static ServiceConnection conn = new ServiceConnection()
{@Overridepublic void onServiceConnected(ComponentName name, IBinder service)
 {scanInterface = IScanInterface.Stub.asInterface(service);
 Log.i("setting", "Scanner Service Connected!");}
 @Overridepublic void onServiceDisconnected(ComponentName name)
 {Log.e("setting", "Scanner Service Disconnected!");

scanInterface = null;

}

};

public void bindScannerService() {

Intent intent = new Intent();

intent.setPackage("com.sunmi.scanner");

intent.setAction("com.sunmi.scanner.IScanInterface");

bindService(intent, conn, Service.BIND_AUTO_CREATE);

}

2.3.   AIDL Interface

No.

Function

1

void sendKeyEvent(KeyEvent key)

Custom the trigger key

2

void scan()

start scan

3

void stop()

stop scan

4

int getScannerModel()

Get scanner type

     Custom the trigger key

**function：**void sendKeyEvent(KeyEvent key)

parameter：

key ：KeyEvent

action=KeyEvent.ACTION_UP：start scan

action=KeyEvent.ACTION_DOWN：stop scan

Example：

| @Override
**public boolean dispatchKeyEvent(KeyEvent event) {
// Example: Use the X key value as the trigger sweep key.
if (event.getKeyCode() == x) {
scanInterface.sendKeyEvent(event);
}
return super.dispatchKeyEvent(event);

}**

Start scan
function：void scan( )
Note：Need to work with the stop() method to start identifying scan codes.

Example：

scanInterface.scan();

Stop scan
function：void stop( )
Note：Need to work with the scan() method to stop identifying scan codes.
Example：

scanInterface.stop();

Get scan type
function：int getScannerModel( )
Return：Type:
100: NONE
101: super_n1365_y1825
102: newland-2096
103: zebra-4710
104: honeywell-3601
105: honeywell-6603
106: zebra-4750
107: zebra-1350
108: honeywell-6703
109: honeywell-3603
110: newland-cm47
111: newland-3108
112: zebra_965
113: sm_ss_1100
114: newland-cm30
115: honeywell-4603
116: zebra_4770
117: newland_2596

118: sm_ss_1103
119: sm_ss_1101
120: honeywell-5703
121: sm_ss_1100_2
122: sm_ss_1104

Example：

scanInterface.getScannerModel();
*/