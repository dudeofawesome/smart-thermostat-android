package io.orleans.smartthermostat;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.HashMap;
import java.util.Map;

public class IntroActivity extends ActionBarActivity {

    private ViewPager mViewPager;
    public static double target_temperature = 71;
    public static double current_temperature = 72;

    boolean coolOn = false;
    UsbDevice device;
    UsbDeviceConnection connection;

    // A callback for received data must be defined
    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback()
    {
        @Override
        public void onReceivedData(final byte[] arg0)
        {
            // Code here
            System.out.println(new String(arg0));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro_layout);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Set an Adapter on the ViewPager
        mViewPager.setAdapter(new IntroAdapter(getSupportFragmentManager(), getBaseContext()));
        
        // Set a PageTransformer
        mViewPager.setPageTransformer(false, new IntroPageTransformer(getBaseContext()));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Screwit.context = getBaseContext();
        Screwit.tempColor = ContextCompat.getColor(Screwit.context, R.color.neutral);

        // This snippet will open the first usb device connected, excluding usb root hubs
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                if (deviceVID != 0x1d6b || (devicePID != 0x0001 || devicePID != 0x0002 || devicePID != 0x0003)) {
                    // We are supposing here there is only one device connected and it is our serial device
                    connection = usbManager.openDevice(device);
                    keep = false;

                    Screwit.serial = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if(Screwit.serial != null) {
                        if (Screwit.serial.open()) {
                            System.out.println("Connected to serial device");
                            // Devices are opened with default values, Usually 9600,8,1,None,OFF
                            // CDC driver default values 115200,8,1,None,OFF
                            Screwit.serial.setBaudRate(9600);
                            Screwit.serial.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            Screwit.serial.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            Screwit.serial.setParity(UsbSerialInterface.PARITY_NONE);
                            Screwit.serial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            Screwit.serial.read(mCallback);

                            byte[] relay = new byte[1];
                            relay[0] = 0x000031;
                            Screwit.serial.write(relay);

                            relay = new byte[1];
                            relay[0] = 0x000033;
                            Screwit.serial.write(relay);
                        } else {
                            // Serial port could not be opened, maybe an I/O error or it CDC driver was chosen it does not really fit
                        }
                    } else {
                        // No driver for given device, even generic CDC driver could not be loaded
                    }
                } else {
                    connection = null;
                    device = null;
                    System.out.println("No serial device found");
                }

                if(!keep)
                    break;
            }
        }
    }

    public static void sendTemp () {
        if (current_temperature > target_temperature + 5) {
            Screwit.tempColor = ContextCompat.getColor(Screwit.context, R.color.cooling);
            byte[] relay = new byte[1];
            relay[0] = 0x000032;
            Screwit.serial.write(relay);

        } else  if (current_temperature < target_temperature - 5) {
            Screwit.tempColor = ContextCompat.getColor(Screwit.context, R.color.heating);

            byte[] relay = new byte[1];
            relay[0] = 0x000034;
            Screwit.serial.write(relay);
        } else {
            Screwit.tempColor = ContextCompat.getColor(Screwit.context, R.color.neutral);

            byte[] relay = new byte[1];
            relay[0] = 0x000031;
            Screwit.serial.write(relay);

            relay = new byte[1];
            relay[0] = 0x000033;
            Screwit.serial.write(relay);
        }
    }
}