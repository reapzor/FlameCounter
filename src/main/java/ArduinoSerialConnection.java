import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;
import java.util.logging.Logger;

/**
 * Created by sqa on 12/24/2015.
 */
public class ArduinoSerialConnection implements SerialPortEventListener {
    private static final Logger log = Logger.getLogger( ArduinoSerialConnection.class.getName() );
    private String devicePortString;
    private SerialPort devicePort;
    private Integer baudRate;
    private InputStream input;
    private OutputStream output;
    private static final Integer serialConnectionTimeout = 3000;
    private Boolean connected = false;

    private static final Integer[] BaudRates = new Integer[]{
            300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 38400, 57600, 115200
    };


    public ArduinoSerialConnection(String devicePort, Integer baudRate) {
        this.devicePortString = devicePort;
        if (!validBaudRate(baudRate)) {
            log.severe(String.format("Invalid Baud Rate Given: %s. Defaulting to 9600.", baudRate));
            log.severe(String.format("Valid Baud Rates: %S", BaudRates));
            baudRate = 9600;
        }
        this.baudRate = baudRate;
    }

    public Boolean connect() {
        if (connected) {
            return true;
        }

        Boolean connectFailed = true;

        try {
            CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(devicePortString);
            SerialPort serialPort = (SerialPort) commPortIdentifier.open(ArduinoSerialConnection.class.getName(),
                    serialConnectionTimeout);

            serialPort.setSerialPortParams(baudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            InputStream is = serialPort.getInputStream();
            OutputStream os = serialPort.getOutputStream();

            devicePort = serialPort;
            input = is;
            output = os;

            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

            connectFailed = false;

        } catch (NoSuchPortException e) {
            log.severe(String.format("Unable to connect to Arduino. No Such COM port: %s", devicePortString));
        } catch (PortInUseException e) {
            log.severe(String.format("Unable to connect to Arduino. COM port already in use: %s", devicePortString));
        } catch (UnsupportedCommOperationException e) {
            log.severe(String.format("Unable to connect to Arduino. Connected COM port is not a serial port, or something.? %s, something: %s",
                    devicePortString, e.getMessage()));
            e.printStackTrace();
        } catch (IOException e) {
            log.severe(String.format("Unable to connect to Arduino. Cannot open IO on connected COM port %s",
                    devicePortString));
        } catch (TooManyListenersException e) {
            log.severe(String.format("Unable to connect to Arduino. Programming error? Tried to listen to port twice+.!? %s",
                    devicePortString));
            e.printStackTrace();
        }

        if (connectFailed) {
            if (devicePort != null) {
                devicePort.removeEventListener();
                devicePort.close();
            }
            devicePort = null;
            input = null;
            output = null;
            connected = false;
        }
        else {
            connected = true;
        }

        return connected;
    }


    public void disconnect() {
        if (!connected) {
            return;
        }

        devicePort.removeEventListener();
        devicePort.close();
        devicePort = null;
        input = null;
        output = null;
        connected = false;;
    }

    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {

        }
    }

    public static Boolean validBaudRate(Integer baudRate) {
        for (Integer validBaud : BaudRates) {
            if (baudRate.equals(validBaud)) {
                return true;
            }
        }
        return false;
    }

}
