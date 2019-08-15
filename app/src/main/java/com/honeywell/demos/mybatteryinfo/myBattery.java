package com.honeywell.demos.mybatteryinfo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.honeywell.osservice.data.OSConstant;
import com.honeywell.osservice.sdk.BatteryManager;
import com.honeywell.osservice.sdk.HonOSException;

public class myBattery {
    private BatteryManager _batteryManager;
    Context _context;
    public static String GET_BATTERY_INFO_MESSAGE="GET_BATTERY_INFO_MESSAGE";
    public static String infoKeys[]=new String[]{
            OSConstant.KEY_RESULT_BATTERY_SERIAL_NUMBER,
            OSConstant.KEY_RESULT_BATTERY_AUTHENTICATION,
            OSConstant.KEY_RESULT_BATTERY_VOLTAGE,
            OSConstant.KEY_RESULT_BATTERY_CURRENT,
            OSConstant.KEY_RESULT_BATTERY_TEMPERATURE,
            OSConstant.KEY_RESULT_BATTERY_STATE_OF_CHARGE,
            OSConstant.KEY_RESULT_BATTERY_REMAINING_CAPACITY,
            OSConstant.KEY_RESULT_BATTERY_FULL_CAPACITY,
            OSConstant.KEY_RESULT_BATTERY_TIME_TO_EMPTY,
            OSConstant.KEY_RESULT_BATTERY_TIME_TO_FULL,
            OSConstant.KEY_RESULT_BATTERY_CYCLE_COUNT,
            OSConstant.KEY_RESULT_BATTERY_AGE_CAPACITY,
            OSConstant.KEY_RESULT_BATTERY_AGE_FORECAST,
            OSConstant.KEY_RESULT_BATTERY_AGE_TIME,
            OSConstant.KEY_RESULT_BATTERY_FULL_CAPACITY_COMPENSATED,
            OSConstant.KEY_RESULT_BATTERY_FULL_CAPACITY_NOT_COMPENSATED,
            OSConstant.KEY_RESULT_BATTERY_MAX_VOLTAGE,
            OSConstant.KEY_RESULT_BATTERY_MIN_VOLTAGE,
            OSConstant.KEY_RESULT_BATTERY_MAX_CURRENT,
            OSConstant.KEY_RESULT_BATTERY_MIN_CURRENT,
            OSConstant.KEY_RESULT_BATTERY_MAX_TEMP,
            OSConstant.KEY_RESULT_BATTERY_MIN_TEMP
    };

    public static String dumpBatteryInfo(Bundle bundle){
        StringBuilder sb = new StringBuilder();
        for (String s:infoKeys) {
            if(bundle.containsKey(s))
                sb.append(s + ": " + bundle.get(s).toString() + "\n");
        }
        return sb.toString();
    }
    public myBattery(Context context){
        _context=context;
        _batteryManager = BatteryManager.getInstance(context);
    }
    // A button onClick method
    public Bundle getBatteryInfo() {
        if (_batteryManager.isReady()) {
            try {
                Bundle batteryInfo = _batteryManager.getBatteryGaugeInfo(null);
                return batteryInfo;
            } catch (HonOSException e) {
                e.printStackTrace();
                Log.e("Demo", e.getErrorCode() + " " + e.getMessage());
            }
        }
        return new Bundle();
    }
}
