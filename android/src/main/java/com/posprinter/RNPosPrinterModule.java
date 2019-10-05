package com.posprinter;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.hoin.usbsdk.UsbController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by froogal on 3/27/18.
 */

public class RNPosPrinterModule extends ReactContextBaseJavaModule {

  private int[][] u_infor;
  private UsbController usbCtrl = null;
  private UsbDevice dev = null;


  public RNPosPrinterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    u_infor = new int[12][2];
    u_infor[0][0] = 0x0D3A;
    u_infor[0][1] = 0x037C;

    u_infor[1][0] = 0x05C6;
    u_infor[1][1] = 0x904C;

    u_infor[2][0] = 0x0483;
    u_infor[2][1] = 0x5740;

    u_infor[3][0] = 0x0493;
    u_infor[3][1] = 0x8760;

    u_infor[4][0] = 0x1FC9;
    u_infor[4][1] = 0x2002;

    u_infor[5][0] = 0x0456;
    u_infor[5][1] = 0x0808;

    u_infor[6][0] = 0x05C6;
    u_infor[6][1] = 0x904C;

    u_infor[7][0] = 0x04b8;
    u_infor[7][1] = 0x0e11;

    u_infor[8][0] = 0x04b8;
    u_infor[8][1] = 0x0e20;

    u_infor[9][0] = 0x1fc9;
    u_infor[9][1] = 0x2003;

    u_infor[10][0] = 0x154f;
    u_infor[10][1] = 0x154f;

    u_infor[11][0] = 0x0525;
    u_infor[11][1] = 0xa700;
  }

  @Override
  public String getName() {
    return "RNPosPrinter";
  }

  @ReactMethod
  public void execute(ReadableArray array, Promise promise) {
    try {
      byte bits[] = new byte[array.size()];
      for (int i =0; i < array.size(); i++) {
         bits[i] = (byte) array.getInt(i);
//        if (array.getType(i).equals(ReadableType.String)) {
//          bits[i] = array.getString(i).getBytes()[0];
//        } else {
//          bits[i] = (byte) array.getInt(i);
//        }
      }
      Activity activity = getCurrentActivity();
      if (activity != null) {
        usbCtrl = new UsbController(activity, mHandler);
      }
      if (usbCtrl != null) {
        usbCtrl.close();
        for (int i = 0; i < u_infor.length; i++) {
          dev = usbCtrl.getDev(u_infor[i][0], u_infor[i][1]);
          if (dev != null)
            break;
        }
        System.out.println("dev toast module" + dev);
        if (dev != null) {
          if (!(usbCtrl.isHasPermission(dev))) {
            Log.d("isHasPermission", usbCtrl.isHasPermission(dev) + "");
            usbCtrl.getPermission(dev);
            promise.reject("No Permission");
          } else {
            usbCtrl.sendByte(bits, dev);
            promise.resolve(null);
          }
        } else {
          promise.reject("No Printers Found");
        }
      }
    } catch (Exception e) {
      promise.reject(e.getMessage(), e.getMessage(), e);
    }
  }

  private final Handler mHandler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      System.out.println("test" + msg);
      switch (msg.what) {
        case UsbController.USB_CONNECTED:
          break;
        default:
          break;
      }
    }
  };
}
