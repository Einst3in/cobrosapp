package com.telenord.cobrosapp.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BTOperator
{
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public boolean BTO_ReadState(){
        boolean bRet = false;
        BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BTAdapter != null)
        {
            if(BTAdapter.isEnabled()) bRet = true;
        } else {
            Log.d("Bluetooth", "Bluetooth Reporta un problema");
        }
        return bRet;
    }

    public boolean BTO_EnableBluetooth()
    {
        boolean bRet = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null)
        {
            if(mBluetoothAdapter.isEnabled())
                return true;
            mBluetoothAdapter.enable();
            if(!mBluetoothAdapter.isEnabled())
            {
                bRet = true;
//                iStatusCode = 1;
                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
            }
        } else
        {
            Log.d("PRTLIB", "BTO_EnableBluetooth --> mBluetoothAdapter is null");
        }
        return bRet;
    }

    public boolean BTO_DisableBluetooth()
    {
        Log.d("PRTLIB", "BTO_DisableBluetooth...");
        boolean bRet = false;
        if(mBluetoothAdapter != null)
        {
            if(mBluetoothAdapter.isEnabled())
                mBluetoothAdapter.disable();
            if(!mBluetoothAdapter.isEnabled())
            {
                bRet = true;
                Log.d("PRTLIB", "BTO_DisableBluetooth --> Close OK");
            }
        } else
        {
            Log.d("PRTLIB", "BTO_DisableBluetooth --> mBluetoothAdapter is null");
        }
        return bRet;
    }

    public Set BTO_GetBondedDevice()
    {
        Log.d("PRTLIB", "BTO_GetBondedDevice...");
        if (!mBluetoothAdapter.isEnabled())
        {
            mBluetoothAdapter.enable();//´ò¿ªÀ¶ÑÀ
            mBluetoothAdapter.startDiscovery();//·¢ÏÖÀ¶ÑÀ

        }
        Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        mBluetoothAdapter.enable();//´ò¿ªÀ¶ÑÀ
        mBluetoothAdapter.startDiscovery();//·¢ÏÖÀ¶ÑÀ

        return mBluetoothAdapter.getBondedDevices();
    }

    public boolean BTO_ConnectDevice(String strMac)
    {
        Log.d("PRTLIB", "BTO_ConnectDevice...");
        boolean bRet = false;
        try
        {
            mmDevice = mBluetoothAdapter.getRemoteDevice(strMac);
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(BTOperator.MY_UUID);
        }
        catch(IOException e)
        {
            Log.d("PRTLIB", (new StringBuilder("BTO_ConnectDevice --> create ")).append(e.getMessage()).toString());
            return bRet;
        }
        mBluetoothAdapter.cancelDiscovery();
        try
        {
            mmSocket.connect();
            bRet = true;
        }
        catch(IOException e)
        {
            Log.d("PRTLIB", (new StringBuilder("BTO_ConnectDevice --> connect ")).append(e.getMessage()).toString());
        }
        return bRet;
    }

    public boolean BTO_CloseDevice()
    {
        Log.d("PRTLIB", "BTO_CloseDevice...");
        boolean bRet = true;
        try
        {
            mmSocket.close();
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder("BTO_ConnectDevice close ")).append(e.getMessage()).toString());
            bRet = false;
        }
        return bRet;
    }

    public boolean BTO_GetIOInterface()
    {
        Log.d("PRTLIB", "BTO_GetIOInterface...");
        try
        {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
        }
        catch(IOException e)
        {
            Log.d("PRTLIB", (new StringBuilder("BTO_GetIOInterface ")).append(e.getMessage()).toString());
            return false;
        }
        return true;
    }

    /*public boolean BTO_Write(byte buffer[], int count)
    {
        Log.d("PRTLIB", "BTO_Write");
       try
        {
            mmOutStream.write(buffer, 0, count);
        }
        catch(IOException e)
        {
            Log.d("PRTLIB", (new StringBuilder("BTO_Write --> error ")).append(e.getMessage()).toString());
            return false;
        }
        return true;
    }*/
    public boolean BTO_Write(byte buffer[], int count)
    {
        Log.d("PRTLIB", "BTO_Write split");
        try
        {

            if(count<=16){
                mmOutStream.write(buffer, 0, count);
            }
            else
            {
                long interval=50;
                int times=(count/16);
                int offset=0;
                byte[] buf2=new byte[16];
                for(int i=0;i<times;i++){
                    for(int j=0;j<16;j++){
                        buf2[j]=buffer[offset+j];
                    }
                    offset=offset+16;
                    mmOutStream.write(buf2, 0, 16);
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if(count>offset){
                    byte[] buf3=new byte[count-offset];
                    int tmp=0;
                    for(int i=offset;i<count;i++){
                        buf3[tmp]=buffer[offset];
                    }
                    mmOutStream.write(buf3, 0, count-offset);
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        catch(IOException e)
        {
            Log.d("PRTLIB", (new StringBuilder("BTO_Write --> error ")).append(e.getMessage()).toString());
            return false;
        }
        return true;
    }


    public boolean BTO_Read(byte buffer[], int length)
    {
        Log.d("PRTLIB", "BTO_Read");
        try
        {
            mmInStream.read(buffer, 0, length);
        }
        catch(IOException e)
        {
            Log.d("PRTLIB", (new StringBuilder("BTO_Read --> error ")).append(e.getMessage()).toString());
            return false;
        }
        return true;
    }

    private BluetoothAdapter mBluetoothAdapter;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
//    final PRTAndroidPrint this$0;

    public BTOperator()
    {
        super();
//        this$0 = PRTAndroidPrint.this;
        mBluetoothAdapter = null;
    }
}
