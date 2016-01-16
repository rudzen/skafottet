package com.undyingideas.thor.skafottet.broadcastrecievers;

import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created on 16-01-2016, 07:28.
 * Project : skafottet<br>
 * Generic battery status data slave class.<br>
 * Combines observer and DTO.
 * For use with {@link BatteryLevelReciever}.<br>
 * @author rudz
 */
public class BatteryLevelRecieverData implements Runnable {

    /* store the destination interface as a WEAK reference, this allows the system to GC the subject
       if needed. Since it's guarded nothing will happend if the target was destroyed. */
    private final WeakReference<BatteryLevelRecieveDataInterface> batteryLevelRecieveDataInterfaceWeakReference;

    /* the TAG so we know who we are */
    private final static String TAG = "BatteryLevelRecData";

    /* the battery information as DTO */
    private BatteryDTO data;

    /* to let the class add itself back to the InternetReciever stack */
    private boolean keepInReciever;

    /**
     * This interface has to be implemented by the target, this ensures that we can send data to it.<br>
     * This is a combination of controlling the runnable interface + the needs for the {@link BatteryLevelReciever}.<br>
     * In essence, it "replaces" the Runnable interface with a more controlled one.<br>
     * @author rudz
     */
    public interface BatteryLevelRecieveDataInterface {
        void onBatteryStatusChanged(BatteryDTO batteryInformation);
    }

    /**
     * Required the interface implementation.
     * @param batteryLevelRecieveDataInterface The object which implements the interface.
     */
    public BatteryLevelRecieverData(final BatteryLevelRecieveDataInterface batteryLevelRecieveDataInterface) {
        this(batteryLevelRecieveDataInterface, true);
    }

    /**
     * Allows defining manually if this class should be kept by the Internet Reciever.
     * @param batteryLevelRecieveDataInterface The object which implements the interface.
     * @param keepInReciever If true, this observer class will not be removed when executed.
     */
    public BatteryLevelRecieverData(final BatteryLevelRecieveDataInterface batteryLevelRecieveDataInterface, final boolean keepInReciever) {
        batteryLevelRecieveDataInterfaceWeakReference = new WeakReference<>(batteryLevelRecieveDataInterface);
        this.keepInReciever = keepInReciever;
    }

    /**
     * The runnable interface is started here, this will make sure there still is a reference to the master class.
     * If the references is obtained, it will send the data back to it through its own interface.
     */
    @Override
    public void run() {
        Log.d(TAG, "Observer runnable started.");
        final BatteryLevelRecieveDataInterface internetRecieverInterface = batteryLevelRecieveDataInterfaceWeakReference.get();
        if (internetRecieverInterface != null) {
            internetRecieverInterface.onBatteryStatusChanged(data);
        }
    }

    /**
     * Setter for connection state, this is invokes through the reciever.
     * @param newData The connection state of the system
     */
    public void setData(final BatteryDTO newData) {
        data = newData;
    }

    /**
     * Sets the keepInReciever feature
     * @param newValue If true, it will not be removed from the internet reciever.
     */
    public void setKeepInReciever(final boolean newValue) {
        keepInReciever = newValue;
    }

    public boolean isKeepInReciever() { return keepInReciever; }
}
