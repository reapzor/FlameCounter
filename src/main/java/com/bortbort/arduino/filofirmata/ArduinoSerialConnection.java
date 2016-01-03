package com.bortbort.arduino.filofirmata;



import jtermios.JTermios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import purejavacomm.*;

import java.io.IOException;
import java.util.TooManyListenersException;

/**
 * Created by chuck on 1/1/2016.
 */
public class ArduinoSerialConnection extends ArduinoConnection implements SerialPortEventListener {
    private static final Logger log = LoggerFactory.getLogger(ArduinoSerialConnection.class);
    PureJavaSerialPort serialPort;
    String comPort;


    public ArduinoSerialConnection() {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier("COM3");
            serialPort = (PureJavaSerialPort) portIdentifier.open("Hi", 2000);
            serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            serialPort.notifyOnBreakInterrupt(true);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN + SerialPort.FLOWCONTROL_XONXOFF_OUT);
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
        this.comPort = "COM3";
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            int avail = serialPort.getInputStream().available();
            int avail2 = avail;
            if (avail2 > 2048) {
                avail2 = 2048;
            }
            byte[] b = new byte[avail2];
            log.info("hi {} -- {} -- {}", avail, serialPortEvent.getEventType(), serialPort.getInputStream().read(b, 0, avail2));
            log.info(bytesToHex(b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void main (String[] args) {
        //JTermios.JTermiosLogging.setLogMask(4);
        ArduinoSerialConnection serialConnection = new ArduinoSerialConnection();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       // log.error(String.valueOf(serialConnection.serialPort.isInternalThreadRunning()));
        //serialConnection.serialPort.close();

       //  serialConnection = new ArduinoSerialConnection();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("CLOSING");

    }

}
