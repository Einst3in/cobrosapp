package com.telenord.cobrosapp.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


public class PRTAndroidPrint {
    public class BTOperator
    {
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
                    iStatusCode = 1;
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
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(PRTAndroidPrint.MY_UUID);
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
        final PRTAndroidPrint this$0;

        public BTOperator()
        {
            super();
            this$0 = PRTAndroidPrint.this;
            mBluetoothAdapter = null;
        }
    }

    public class BTPrinter
    {

        public int MakeFormat(boolean width, boolean height, boolean bold, boolean underline, boolean minifont)
        {
            byte com[] = new byte[1];
            com[0] = 0;
            if(width)
                com[0] |= 32;
            if(height)
                com[0] |= 16;
            if(bold)
                com[0] |= 8;
            if(underline)
                com[0] |= 128;
            if(minifont)
                com[0] |= 1;
            return com[0];
        }

        public byte[] MakeComm(int iType, int para1, int para2, byte iLength[])
        {
            byte mBuff[] = new byte[20];
            int mLen = 0;
            switch(iType)
            {
                default:
                    break;

                case 0: // '\0'
                    mBuff[0] = 27;
                    mBuff[1] = 64;
                    mLen = 2;
                    break;

                case 1: // '\001'
                    mBuff[0] = 27;
                    mBuff[1] = 74;
                    mBuff[2] = (byte)para1;
                    mLen = 3;
                    break;

                case 2: // '\002'
                    mBuff[0] = 29;
                    mBuff[1] = 76;
                    mBuff[2] = (byte)(para1 / 256);
                    mBuff[3] = (byte)(para1 % 256);
                    mLen = 4;
                    break;

                case 3: // '\003'
                    mBuff[0] = 29;
                    mBuff[1] = 87;
                    mBuff[2] = (byte)(para1 / 256);
                    mBuff[3] = (byte)(para1 % 256);
                    mLen = 4;
                    break;

                case 4: // '\004'
                    mBuff[0] = 29;
                    mBuff[1] = 66;
                    mBuff[2] = (byte)para1;
                    mLen = 3;
                    break;

                case 5: // '\005'
                    if(1 == para2)
                    {
                        mBuff[0] = 27;
                        mBuff[1] = 50;
                        mLen = 2;
                    } else
                    {
                        mBuff[0] = 27;
                        mBuff[1] = 51;
                        mBuff[2] = (byte)para1;
                        mLen = 3;
                    }
                    break;

                case 6: // '\006'
                    mBuff[0] = 27;
                    mBuff[1] = 97;
                    mBuff[2] = (byte)para1;
                    mLen = 3;
                    break;

                case 7: // '\007'
                    mBuff[0] = 27;
                    mBuff[1] = 71;
                    mBuff[2] = (byte)para1;
                    mLen = 3;
                    break;

                case 8: // '\b'
                    mBuff[0] = 29;
                    mBuff[1] = 107;
                    mBuff[2] = (byte)para1;
                    mBuff[3] = (byte)para2;
                    mLen = 4;
                    break;

                case 10: // '\n'
                    mBuff[0] = 29;
                    mBuff[1] = 119;
                    mBuff[2] = (byte)para1;
                    mLen = 3;
                    break;

                case 11: // '\013'
                    mBuff[0] = 29;
                    mBuff[1] = 104;
                    mBuff[2] = (byte)para1;
                    mLen = 3;
                    break;

                case 12: // '\f'
                    mBuff[0] = 29;
                    mBuff[1] = 72;
                    mBuff[2] = (byte)para1;
                    mLen = 3;
                    break;

                case 9: // '\t'
                    mBuff[0] = 27;
                    mBuff[1] = 118;
                    mLen = 2;
                    break;

                case 13: // '\r'
                    mBuff[0] = 27;
                    mBuff[1] = 33;
                    mBuff[2] = (byte)para1;
                    mLen = 3;
                    break;
            }
            iLength[0] = (byte)mLen;
            return mBuff;
        }

        public static final int BTP_reset = 0;
        public static final int BTP_feed = 1;
        public static final int BTP_left = 2;
        public static final int BTP_width = 3;
        public static final int BTP_opp = 4;
        public static final int BTP_linesp = 5;
        public static final int BTP_align = 6;
        public static final int BTP_dup = 7;
        public static final int BTP_barcode = 8;
        public static final int BTP_status = 9;
        public static final int BTP_barWidth = 10;
        public static final int BTP_barHeight = 11;
        public static final int BTP_barHRI = 12;
        public static final int BTP_format = 13;
        final PRTAndroidPrint this$0;

        public BTPrinter()
        {
            super();
            this$0 = PRTAndroidPrint.this;
        }
    }

    public boolean Get_BTStatus(){
        bto = new BTOperator();
        return bto.BTO_ReadState();
    }

    public boolean Enable_BT(){
        bto = new BTOperator();
        return bto.BTO_EnableBluetooth();
    }

    public PRTAndroidPrint()
    {
        iStatusCode = 0;
        iPrintCode = 0;
    }

    public int PRTGetCurrentStatus()
    {
        return iPrintCode;
    }

    public boolean PRTInitLib()
    {
        Log.d("PRTLIB", "LnitLib...");
        boolean bStart = false;
        if(iStatusCode == 0)
        {
            bto = new BTOperator();
            btp = new BTPrinter();
            bStart = bto.BTO_EnableBluetooth();
            if(bStart)
            {
                Log.d("PRTLIB", "LnitLib --> Status OK, the bluetooth is started");
                iStatusCode = 1;
                try
                {
                    Thread.sleep(1500L);
                }
                catch(InterruptedException e1)
                {
                    e1.printStackTrace();
                }
            }
        } else
        {
            iPrintCode = 5;
        }
        return bStart;
    }

    public ArrayList PRTGetBondedDevices()
    {
        Log.d("PRTLIB", "GetBondedDevices...");
        ArrayList btList = null;
        if(iStatusCode == 1)
        {
            Set pairedDevices = bto.BTO_GetBondedDevice();
            if(pairedDevices.size() > 0)
            {
                btList = new ArrayList();
                for(Iterator iterator = pairedDevices.iterator(); iterator.hasNext();)
                {
                    BluetoothDevice device = (BluetoothDevice)iterator.next();
                    String strMac = device.getAddress().substring(0, 9);
                    //if(strMac.equals("00:02:5B:B2:"))
                    // String strName = device.getName();
                    if(strMac.equals("00:02:5B:"))//µØÖ·  ÐòºÅ
                    // if(strName.equals("MPT-II"))
                    {
                        btList.add(device.getName());
                        btList.add(device.getAddress());
                    }
                }
                iStatusCode = 2;
            } else
            {
                Log.d("PRTLIB", "GetBondedDevices --> no pairedDevices");
            }
        } else
        {
            iPrintCode = 5;
        }
        return btList;
    }

    public boolean PRTConnectDevices(ArrayList btList, int iIndex)
    {

        Log.d("PRTLIB", "ConnectDevice...");
        if(iStatusCode == 2)
        {
            String strMac = (String)btList.get(iIndex);
            if(bto.BTO_ConnectDevice(strMac))
            {
                if(bto.BTO_GetIOInterface())
                {
                    Log.d("PRTLIB", "ConnectDevice --> GetIOInterface succeed");
                    iStatusCode = 3;
                    return true;
                }
            }
        }else
        {
            iPrintCode = 5;
        }

        return false;
    }

    public boolean PRTConnectDevice(ArrayList btList, int iIndex)
    {
        Log.d("PRTLIB", "ConnectDevice...");
        if(iStatusCode == 2)
        {
            String strMac = (String)btList.get(iIndex + 1);
            Log.d("PRTLIB", (new StringBuilder("ConnectDevice --> ConnectDevice ")).append(strMac).toString());
            if(bto.BTO_ConnectDevice(strMac))
            {
                Log.d("PRTLIB", "ConnectDevice --> ConnectDevice succeed");
                if(bto.BTO_GetIOInterface())
                {
                    Log.d("PRTLIB", "ConnectDevice --> GetIOInterface succeed");
                    iStatusCode = 3;
                    return true;
                }
            }
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    public boolean PRTCloseConnect()
    {
        Log.d("PRTLIB", "CloseConnect...");
        boolean bRet = false;
        if(3 == iStatusCode)
        {
            bRet = bto.BTO_CloseDevice();
            if(bRet)
            {
                Log.d("PRTLIB", "CloseConnect succeed");
                iStatusCode = 1;
            }
        } else
        {
            iPrintCode = 5;
        }
        return bRet;
    }

    public boolean PRTFreeLib(boolean bDisable)
    {
        Log.d("PRTLIB", "FreeLib...");
        if(bDisable && bto.BTO_DisableBluetooth())
        {
            Log.d("PRTLIB", "FreeLib --> BTO_DisableBluetooth succeed");
            iStatusCode = 0;
            return true;
        } else
        {
            return false;
        }
    }

    public boolean PRTSendString(String str)
            throws UnsupportedEncodingException
    {
        Log.d("PRTLIB", "SendString...");
        if(3 == iStatusCode)
        {
            byte msg[] = str.getBytes("gb2312");
            if(bto.BTO_Write(msg, msg.length))
                return true;
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    public boolean PRTSendBuffer(byte data[], int iLeng)
    {
        Log.d("PRTLIB", "SendBuffer...");
        if(3 == iStatusCode)
        {
            if(bto.BTO_Write(data, iLeng))
                return true;
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    public boolean PRTReset()
    {
        Log.d("PRTLIB", "Reset...");
        if(3 == iStatusCode)
        {
            byte bLen[] = new byte[1];
            byte data[] = btp.MakeComm(0, 0, 0, bLen);
            if(bto.BTO_Write(data, bLen[0]))
                return true;
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    public boolean PRTFeedLines(int iNum)
    {
        Log.d("PRTLIB", "FeedLines...");
        if(3 == iStatusCode)
        {
            byte bLen[] = new byte[1];
            byte data[] = btp.MakeComm(1, iNum, 0, bLen);
            if(bto.BTO_Write(data, bLen[0]))
                return true;
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    public boolean PRTSetPrintPosition(int iLeftMargin, int iWidth)
    {
        Log.d("PRTLIB", "SetPrintPosition...");
        if(3 == iStatusCode)
        {
            byte bLen1[] = new byte[1];
            byte data1[] = btp.MakeComm(2, iLeftMargin, 0, bLen1);
            if(!bto.BTO_Write(data1, bLen1[0]))
                return false;
            byte bLen2[] = new byte[1];
            byte data2[] = btp.MakeComm(3, iWidth, 0, bLen2);
            if(bto.BTO_Write(data2, bLen2[0]))
                return true;
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    public boolean PRTOppositeColor(boolean bOpposite)
    {
        Log.d("PRTLIB", "OppositeColor...");
        if(3 == iStatusCode)
        {
            byte bLen[] = new byte[1];
            int iVal = 0;
            if(bOpposite)
                iVal = 1;
            byte data[] = btp.MakeComm(4, iVal, 0, bLen);
            if(bto.BTO_Write(data, bLen[0]))
                return true;
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    public boolean PRTSetLineSpace(int iLines)
    {
        Log.d("PRTLIB", "SetLineSpace...");
        if(3 == iStatusCode)
        {
            byte bLen[] = new byte[1];
            if(-1 == iLines)
            {
                byte data[] = btp.MakeComm(5, 0, 1, bLen);
                if(bto.BTO_Write(data, bLen[0]))
                    return true;
            } else
            {
                byte data[] = btp.MakeComm(5, iLines, 0, bLen);
                if(bto.BTO_Write(data, bLen[0]))
                    return true;
            }
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    public boolean PRTAlignType(int alignType)
    {
        Log.d("PRTLIB", "AlignType...");
        if(3 == iStatusCode)
        {
            byte bLen[] = new byte[1];
            byte data[] = btp.MakeComm(6, alignType, 0, bLen);
            if(bto.BTO_Write(data, bLen[0]))
                return true;
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    public boolean PRTFormatString(boolean width, boolean height, boolean bold, boolean underline, boolean minifont)
    {
        Log.d("PRTLIB", "FormatString...");
        if(3 == iStatusCode)
        {
            if(!width && !height && !bold && !underline && !minifont)
                return true;
            byte bLen[] = new byte[1];
            int datacom = btp.MakeFormat(width, height, bold, underline, minifont);
            byte data[] = btp.MakeComm(13, datacom, 0, bLen);
            if(bto.BTO_Write(data, bLen[0]))
                return true;
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    public boolean PRTDuplePrint(boolean bdup)
    {
        Log.d("PRTLIB", "DuplePrint...");
        if(3 == iStatusCode)
        {
            byte bLen[] = new byte[1];
            int iVal = 0;
            if(bdup)
                iVal = 1;
            byte data[] = btp.MakeComm(7, iVal, 0, bLen);
            if(bto.BTO_Write(data, bLen[0]))
                return true;
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

	   /* public boolean PRTPrintBarcode(int iType, int iWidth, int iHeight, int hri, String strData)
	    {
	        Log.d("PRTLIB", "PrintBarcode...");
	        if(3 == iStatusCode)
	        {
	        	byte zl[] = new byte[3];
	        	zl[0] = 0x1d;
	        	zl[1] = 0x6b;

	    	    public static final int BC_UPCA = 65;
	    	    public static final int BC_UPCB = 66;
	    	    public static final int BC_EAN13 = 67;
	    	    public static final int BC_EAN8 = 68;
	    	    public static final int BC_CODE39 = 69;
	    	    public static final int BC_ITF = 70;
	    	    public static final int BC_CODEBAR = 71;
	    	    public static final int BC_CODE93 = 72;
	    	    public static final int BC_CODE128 = 73;

	        	if(BC_UPCA)
	        	System.out.println();
	            return true;
	        }
	        else
	        {
	            iPrintCode = 5;
	            return false;
	        }
	    }*/

    public void WriteFileSdcard(String fileName,String message){
        try
        {
            //FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
            FileOutputStream fout = new FileOutputStream(fileName);
            byte [] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Ð´ÎÄ¼þµ½sd¿¨ÉÏ
     *
     * @param context
     */
    public void writeFileToSD(String context)
    {

        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return;
        }
        try {
            String pathName = "/sdcard/";
            String fileName = "log.txt";
            File path = new File(pathName);
            File file = new File(pathName + fileName);
            if (!path.exists()) {
                Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(context.getBytes());
            raf.close();
            //×¢ÊÍµÄÒ²ÊÇÐ´ÎÄ¼þ..µ«ÊÇÃ¿´ÎÐ´Èë¶¼»á°ÑÖ®Ç°µÄ¸²¸Ç..
	      /*String pathName = "/sdcard/";
	      String fileName = "log.txt";
	      File path = new File(pathName);
	      File file = new File(pathName + fileName);
	      if (!path.exists()) {
	       Log.d("TestFile", "Create the path:" + pathName);
	       path.mkdir();
	      }
	      if (!file.exists()) {
	       Log.d("TestFile", "Create the file:" + fileName);
	       file.createNewFile();
	      }
	      FileOutputStream stream = new FileOutputStream(file);
	      String s = context;
	      byte[] buf = s.getBytes();
	      stream.write(buf);
	      stream.close();*/

        } catch (Exception e) {
            Log.e("TestFile", "Error on writeFilToSD.");
        }
    }

    public boolean PRTPrintBarcode(int iType, int iWidth, int iHeight, int hri, String strData)
    {
        Log.d("PRTLIB", "PrintBarcode...");
        if(3 == iStatusCode)
        {
            if(iWidth <= 0 || iWidth > 4)
                iWidth = 2;
            byte bLen1[] = new byte[1];
            byte data1[] = btp.MakeComm(10, iWidth, 0, bLen1);
            if(!bto.BTO_Write(data1, bLen1[0]))
                return false;
            System.out.println((new StringBuilder("BTP_barWidth ")).append(data1.length).toString());
            for(int i = 0; i < 3; i++)
            {
                int hex = data1[i] & 255;
                System.out.print((new StringBuilder(String.valueOf(Integer.toHexString(hex)))).append(" ").toString());
                //writeFileToSD((new StringBuilder(String.valueOf(Integer.toHexString(hex)))).append(" ").toString());
            }
            System.out.println();
            if(iHeight <= 0)
                iHeight = 36;
            byte bLen2[] = new byte[1];
            byte data2[] = btp.MakeComm(11, iHeight, 0, bLen2);
            if(!bto.BTO_Write(data2, bLen2[0]))
                return false;
            System.out.println((new StringBuilder("BTP_barHeight ")).append(data2.length).toString());
            for(int i = 0; i < 3; i++)
            {
                int hex = data2[i] & 255;
                System.out.print((new StringBuilder(String.valueOf(Integer.toHexString(hex)))).append(" ").toString());
            }
            System.out.println();
            if(hri < 0 || hri > 2)
                hri = 2;
            byte bLen3[] = new byte[1];
            byte data3[] = btp.MakeComm(12, hri, 0, bLen3);
            if(!bto.BTO_Write(data3, bLen3[0]))
                return false;
            System.out.println((new StringBuilder("BTP_barHRI ")).append(data3.length).toString());
            for(int i = 0; i < 3; i++)
            {
                int hex = data3[i] & 255;
                System.out.print((new StringBuilder(String.valueOf(Integer.toHexString(hex)))).append(" ").toString());
            }
            System.out.println();

            byte bLen4[] = new byte[1];

            byte data4[] = btp.MakeComm(8, iType, strData.length(), bLen4);
            if(!bto.BTO_Write(data4, bLen4[0]))
                return false;

            System.out.println((new StringBuilder("BTP_barCode ")).append(data4.length).toString());

            for(int i = 0; i < 3; i++)
            {
                int hex = data4[i] & 255;
                System.out.print((new StringBuilder(String.valueOf(Integer.toHexString(hex)))).append(" ").toString());
                //writeFileToSD((new StringBuilder(String.valueOf(Integer.toHexString(hex)))).append(" ").toString());
            }
            System.out.println();
            byte bLen5[] = strData.getBytes();
            if(!bto.BTO_Write(bLen5, bLen5.length))
                return false;
            System.out.println((new StringBuilder("BTP_barSTR ")).append(bLen5.length).toString());
            for(int i = 0; i < bLen5.length; i++)
            {
                int hex = bLen5[i] & 255;
                System.out.print((new StringBuilder(String.valueOf(Integer.toHexString(hex)))).append(" ").toString());

            }
            System.out.println();
            return true;
        } else
        {
            iPrintCode = 5;
            return false;
        }
    }

    /* public boolean PRTPrintBarcode(int iType, int iWidth, int iHeight, int hri, String strData)
     {
         Log.d("PRTLIB", "PrintBarcode...");
         if(3 == iStatusCode)
         {
             byte[] zl = new byte[3];
             zl[0] = 0x1b;
             zl[1] = 0x57;
             zl[2] = 0x01;
             if(!bto.BTO_Write(zl, zl.length))
                 return false;
             short zx = (short)Math.Ceiling(x * 8);
             short zy = (short)Math.Ceiling(y * 8);
             Byte[] zl1 = BitConverter.GetBytes(zx);
             this.PRTMpt.WriteToPort(zl1, 0, zl1.Length);
             Byte[] zl2 = BitConverter.GetBytes(zy);
             this.PRTMpt.WriteToPort(zl2, 0, zl2.Length);
             Byte[] zl3 = new Byte[2]{ 0x1d, 0x77};
             this.PRTMpt.WriteToPort(zl3, 0, zl3.Length);
             Byte[] zl4 = new Byte[1]{(Byte)LineWidth};
             this.PRTMpt.WriteToPort(zl4, 0, zl4.Length);
             Byte[] zl5 = new Byte[2] {0x1d, 0x68};
             this.PRTMpt.WriteToPort(zl5, 0, zl5.Length);
             byte zHeight = (byte)Math.Ceiling(Height * 8);
             //Byte[] zl6 = BitConverter.GetBytes(zHeight);
             Byte[] zl6 = new Byte[1];
             zl6[0] = zHeight;
             this.PRTMpt.WriteToPort(zl6, 0, zl6.Length);
             if (Rotate == 0)
             {
                 Byte[] zl7 = new Byte[3]{0x1B,0x54,0x00};
                 this.PRTMpt.WriteToPort(zl7, 0, zl7.Length);
             }
             else if (Rotate == 90)
             {
                 Byte[] zl7 = new Byte[3]{ 0x1B, 0x54, 0x01};
                 this.PRTMpt.WriteToPort(zl7, 0, zl7.Length);
             }
             else if (Rotate == 180)
             {
                 Byte[] zl7 = new Byte[3]{ 0x1B, 0x54,0x02};
                 this.PRTMpt.WriteToPort(zl7, 0, zl7.Length);
             }
             else if (Rotate == 270)
             {
                 Byte[] zl7 = new Byte[3]{ 0x1B, 0x54, 0x03};
                 this.PRTMpt.WriteToPort(zl7, 0, zl7.Length);
             }
             if (type == BARCODE_TYPE.BARCODE_CODE128)
             {
                 Byte[] zl8 = new Byte[3] { 0x1d,0x6b,0x49 };
                 this.PRTMpt.WriteToPort(zl8, 0, zl8.Length);
                 Byte[] Len = new Byte[1];
                 Len[0] = (Byte)(pData.Length + 2);
                 this.PRTMpt.WriteToPort(Len, 0, Len.Length);
                 Byte[] zl9 = new Byte[2];
                 switch(code128_ASC)
                 {
                     case CODE128_ASCII.CODEA:
                         zl9[0] = 0x7B;
                         zl9[1] = 0x41;
                         break;
                     case CODE128_ASCII.CODEB:
                         zl9[0] = 0x7B;
                         zl9[1] = 0x42;
                         break;
                     case CODE128_ASCII.CODEC:
                         zl9[0] = 0x7B;
                         zl9[1] = 0x43;
                         break;
                     case CODE128_ASCII.SHIFT:
                         zl9[0] = 0x7B;
                         zl9[1] = 0x53;
                         break;
                     case CODE128_ASCII.FNC1:
                         zl9[0] = 0x7B;
                         zl9[1] = 0x31;
                         break;
                     case CODE128_ASCII.FNC2:
                         zl9[0] = 0x7B;
                         zl9[1] = 0x32;
                         break;
                     case CODE128_ASCII.FNC3:
                         zl9[0] = 0x7B;
                         zl9[1] = 0x33;
                         break;
                     case CODE128_ASCII.FNC4:
                         zl9[0] = 0x7B;
                         zl9[1] = 0x34;
                         break;
                     default:
                         zl9[0] = 0x7B;
                         zl9[1] = 0x41;
                         break;
                 }
                 this.PRTMpt.WriteToPort(zl9, 0, zl9.Length);
                 byte[] TextBuf = Encoding.ASCII.GetBytes(pData);
                 this.PRTMpt.WriteToPort(TextBuf, 0, TextBuf.Length);
             }
             else if (type == BARCODE_TYPE.BARCODE_CODE39)
             {
                 Byte[] zl8 = new Byte[3] {0x1d, 0x6b, 0x45 };
                 this.PRTMpt.WriteToPort(zl8, 0, zl8.Length);
                 Byte[] Len = new Byte[1];
                 Len[0] = (Byte)pData.Length;
                 this.PRTMpt.WriteToPort(Len, 0, Len.Length);
                 byte[] TextBuf = Encoding.ASCII.GetBytes(pData);
                 this.PRTMpt.WriteToPort(TextBuf, 0, TextBuf.Length);
             }
             else if (type == BARCODE_TYPE.BARCODE_CODE93)
             {
                 Byte[] zl8 = new Byte[3] { 0x1d, 0x6b, 0x48 };
                 this.PRTMpt.WriteToPort(zl8, 0, zl8.Length);
                 Byte[] Len = new Byte[1];
                 Len[0] = (Byte)pData.Length;
                 this.PRTMpt.WriteToPort(Len, 0, Len.Length);
                 byte[] TextBuf = Encoding.ASCII.GetBytes(pData);
                 this.PRTMpt.WriteToPort(TextBuf, 0, TextBuf.Length);
             }
             System.out.println();
             return true;
         }
         else
         {
             iPrintCode = 5;
             return false;
         }
     }*/
    public boolean PRTPrintBarcode2(double x, double y, int Ver, int Size, int Rotate, String pData) throws UnsupportedEncodingException
    {
        // 0, 0, 0,0,0, "12345678"


        try
        {

            byte[] zl = new byte[3];
            zl[0] = 0x1b;
            zl[1] = 0x57;
            zl[2] = 0x01;
            if(!bto.BTO_Write(zl, zl.length))
                return false;
            short zx = (short)Math.ceil(x * 8);
            short zy = (short)Math.ceil(y * 8);

            byte[] zl1 = new byte[2];
            zl1[0] = (byte)(zx & 0xff);
            zl1[1] = (byte)((zx >> 8) & 0xff);
            if(!bto.BTO_Write(zl1, zl1.length))
                return false;
            byte[] zl2 = new byte[2];
            zl2[0] = (byte)(zy & 0xff);
            zl2[1] = (byte)((zy >> 8) & 0xff);
            if(!bto.BTO_Write(zl2, zl2.length))
                return false;
            if (Rotate == 0)
            {
                byte[] zl3 = new byte[3];
                zl3[0] = 0x1B;
                zl3[1] = 0x54;
                zl3[2] = 0x00;
                if(!bto.BTO_Write(zl3, zl3.length))
                    return false;
            }
            else if (Rotate == 90)
            {
                byte[] zl3 = new byte[3];
                zl3[0] = 0x1B;
                zl3[1] = 0x54;
                zl3[2] = 0x01;
                if(!bto.BTO_Write(zl3, zl3.length))
                    return false;
            }
            else if (Rotate == 180)
            {
                byte[] zl3 = new byte[3];
                zl3[0] = 0x1B;
                zl3[1] = 0x54;
                zl3[2] = 0x02;
                if(!bto.BTO_Write(zl3, zl3.length))
                    return false;
            }
            else if (Rotate == 270)
            {
                byte[] zl3 = new byte[3];
                zl3[0] = 0x1B;
                zl3[1] = 0x54;
                zl3[2] = 0x03;
                if(!bto.BTO_Write(zl3, zl3.length))
                    return false;
            }
            byte[] zl4 = new byte[5];
            zl4[0] = 0x1d;
            zl4[1] = 0x6b;
            zl4[2] = 0x20;
            zl4[3] = 0x01;
            zl4[4] = 0x01;
            if(!bto.BTO_Write(zl4, zl4.length))
                return false;
            //  byte [] TextBuf = pData.getBytes("ISO-8859-5");
            byte [] TextBuf = pData.getBytes();
            if(!bto.BTO_Write(TextBuf, TextBuf.length))
                return false;
            byte[] zl5 = new byte[1];
            zl5[0] = 0x00;
            //zl5[1] = 0x0a;
            if(!bto.BTO_Write(zl5, zl5.length))
                return false;
            return true;
        }

        catch(Exception e)
        {
            return false;
        }
    }
    public int PRTQueryPrinterStatus()
    {
        Log.d("PRTLIB", "QueryPrinterStatus...");
        byte buffer[] = new byte[1];
        int iPrintStatus = 5;
        if(3 == iStatusCode)
        {
            byte bLen[] = new byte[1];
            byte data[] = btp.MakeComm(9, 0, 0, bLen);
            if(bto.BTO_Write(data, bLen[0]) && bto.BTO_Read(buffer, buffer.length))
                switch(buffer[0])
                {
                    case 0: // '\0'
                        Log.d("PRTLIB", "QueryPrinterStatus --> PS_NORMAL");
                        iPrintStatus = 0;
                        break;

                    case 1: // '\001'
                        Log.d("PRTLIB", "QueryPrinterStatus --> PS_PRAPEROUT");
                        iPrintStatus = 3;
                        break;
                }
        } else
        {
            iPrintCode = 5;
        }
        return iPrintStatus;
    }

    private byte[] PRTReadBmpPrintData( Bitmap b, int startline ){
        int nWidth;
        int nHeight;
        int nRealLines;
        int nSize;
        byte[] bData;
        nWidth = b.getWidth();
        nHeight = b.getHeight();
        if(startline>=nHeight)
        {
            return null;
        }
        nRealLines = nHeight - startline;
        if(nRealLines>8)
        {
            nRealLines = 8;
        }
        nSize = nWidth+6;
        bData = new byte[nSize];
        for(int i=0;i<nSize;i++)
        {
            bData[i] = 0x00;
        }
        bData[0] = 0x1B;
        bData[1] = 0x2A;
        bData[2] = 0x01;
        bData[3] = (byte)(nWidth%256);
        bData[4] = (byte)(nWidth/256);

        int nTempValue;
        int nTempData;
        int nPixColor;
        int nDataIndex;
        int nBitIndex;

        nDataIndex = 5;
        for(int w=0;w<nWidth;w++)
        {
            nBitIndex = 0;
            for(int h=startline;h<startline+nRealLines;h++)
            {
                nPixColor = b.getPixel(w, h);
                nPixColor = nPixColor & 0x00111111;
                nBitIndex++;
                if(nPixColor==0)
                {
                    nTempValue = 1;
                    nTempValue = nTempValue << (8-nBitIndex);
                    nTempData = bData[nDataIndex];
                    nTempData = nTempData | nTempValue;
                    bData[nDataIndex] = (byte)nTempData;
                }
            }
            nDataIndex++;
        }
        bData[nDataIndex] = 0x0a;

        return bData;
    }

    public boolean PRTPrintBitmap(String filename,int nsleep)
    {
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        Bitmap b = BitmapFactory.decodeFile(filename,bfoOptions);
        if (b != null) {
            int nStartLine;
            int nHeight;
            byte bData[] = new byte[10];
            //get height of bitmap
            nHeight = b.getHeight();
            //set the printer line space
            bData[0] = 0x1B;
            bData[1] = 0x33;
            bData[2] = 0x00;
            bto.BTO_Write(bData, 3);

            nStartLine = 0;
            while(nStartLine<nHeight)
            {
                byte bBmpData[];
                bBmpData = PRTReadBmpPrintData(b,nStartLine);
                if (bBmpData==null)
                {
                    break;
                }else
                {
                    bto.BTO_Write(bBmpData, bBmpData.length);
                    nStartLine = nStartLine + 8;
                    try
                    {
                        Thread.sleep(nsleep);
                    }catch(InterruptedException e)
                    {
                    }
                }
            }
            //reset the printer line space
            bData[0] = 0x1B;
            bData[1] = 0x32;
            bto.BTO_Write(bData, 2);
            return true;
        }else
        {
            return false;
        }
    }
    //! ÔÚË®Æ½ÉÏ´òÓ¡NÌõÏß¶Î
    public boolean PRTPrintHLines( int nCount, int nDatas[])
    {
        int nLength;
        int nIndex;
        int nTemp;
        byte bPrintData[];

        if((nCount<0)||(nCount>=8))
        {
            return false;
        }

        nLength = 3 + nCount*4;
        bPrintData = new byte[nLength];
        bPrintData[0] = 0x1D;
        bPrintData[1] = 0x27;
        bPrintData[2] = (byte)nCount;
        nIndex = 3;
        for(int i=0;i<(nCount*2);i++)
        {
            nTemp = nDatas[i];
            nTemp = nTemp % 256;
            bPrintData[nIndex] = (byte)nTemp;
            nIndex++;
            nTemp = nDatas[i];
            nTemp = nTemp / 256;
            bPrintData[nIndex] = (byte)nTemp;
            nIndex++;
        }

        if( bto.BTO_Write(bPrintData,nLength) == false )
        {
            return false;
        }

        return true;
    }
    //! ´òÓ¡ÇúÏßÉÏµÄÎÄ×Ö
    public boolean PRTPrintCurveText(int nFlag, int nPos, String sText)
    {
        int nTemp;
        int nIndex;
        int nLength;
        boolean bRet;
        byte bTextData[];
        byte bPrintData[];

        bRet = true;
        try {
            bTextData  = sText.getBytes("gb2312");
            nLength = bTextData.length + 5 + 1;
            bPrintData = new byte[nLength];
            bPrintData[0] = 0x1D;
            bPrintData[1] = 0x22;
            bPrintData[2] = (byte)nFlag;
            nTemp = nPos % 256;
            bPrintData[3] = (byte)nTemp;
            nTemp = nPos / 256;
            bPrintData[4] = (byte)nTemp;
            nIndex = 5;
            for(int i=0; i<bTextData.length; i++)
            {
                bPrintData[nIndex] = bTextData[i];
                nIndex++;
            }
            bPrintData[nLength-1] = 0x00;

            if( bto.BTO_Write(bPrintData,nLength) == false )
            {
                bRet = false;
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            bRet = false;
        }
        return bRet;
    }
//	    //! ½øÈë±³¾°´òÓ¡Ä£Ê½
//	    public boolean PRTEnterBKPrintMode( )
//	    {
//	    	byte bData[] = new byte[2];
//	    	bData[0] = 0x1B;
//	    	bData[1] = 0x36;
//
//	    	if( bto.BTO_Write(bData,2) == false )
//	    	{
//	    		return false;
//	    	}
//
//	    	return true;
//	    }

    //! ½øÈë±³¾°Íø¸ñ´òÓ¡Ä£Ê½£¬²¢¿ØÖÆ±³¾°Íø¸ñµÄ´òÓ¡·¶Î§
    public boolean PRTEnterBKPrintMode(int begin,int end)
            throws UnsupportedEncodingException
    {
        Log.i("PRTLIB", "EnterBKPrintMode...");
        if(3 == iStatusCode)
        {
            byte bData[] = new byte[6];
            bData[0] = 0x1B;
            bData[1] = 0x36;
            bData[2] = (byte) (begin & 0xff) ;
            bData[3] = (byte) ( (begin & 0x100) >>> 8 );
            bData[4] =  (byte) ( end & 0xff);
            bData[5] =  (byte) ( (end & 0x100) >>> 8 );

            Log.i("PRTLIB","PrintMode: begin:" + String.format("%04d", begin) + "   end:"+ String.format("%04d", end));
            Log.i("PRTLIB","PrintModeCMD:" + String.format("%02d", bData[0])
                            + "  " + String.format("%02d", bData[0])
                            + "  " + String.format("%02d", bData[1])
                            + "  " + String.format("%02d", bData[2])
                            + "  " + String.format("%02d", bData[3])
                            + "  " + String.format("%02d", bData[4])
                            + "  " + String.format("%02d", bData[5])
            );
            if( bto.BTO_Write(bData,6))
            {
                Log.i("PRTLIB", "EnterBKPrintMode successful...");
                return true;
            }
        } else
        {
            iPrintCode = 5;
        }
        return false;
    }

    //! ÍË³ö±³¾°´òÓ¡Ä£Ê½
    public boolean PRTExitBKPrintMode( )
    {
        byte bData[] = new byte[2];
        bData[0] = 0x1B;
        bData[1] = 0x37;

        if( bto.BTO_Write(bData,2) == false )
        {
            return false;
        }

        return true;
    }

    public boolean PRTPrintLocalFile(String filePath, String codeType)
    {
        Log.d("PRTLIB", "PrintLocalFile...");
        boolean bRet = true;
        if(3 == iStatusCode)
            try
            {
                StringBuffer sBuffer = new StringBuffer();
                FileInputStream fInputStream = new FileInputStream(filePath);
                InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, codeType);
                BufferedReader in = new BufferedReader(inputStreamReader);
                if(!(new File(filePath)).exists())
                {
                    Log.d("PRTLIB", "PrintLocalFile --> Local file is invalid");
                    bRet = false;
                } else
                {
                    for(; in.ready(); sBuffer.append((new StringBuilder(String.valueOf(in.readLine()))).append("\n").toString()));
                    Log.d("PRTLIB", "PrintLocalFile --> Read file succeed");
                    byte bSend[] = sBuffer.toString().getBytes("gb2312");
                    bto.BTO_Write(bSend, bSend.length);
                }
                in.close();
            }
            catch(Exception e)
            {
                Log.d("PRTLIB", "PrintLocalFile --> Operate failed");
                e.printStackTrace();
                bRet = false;
            }
        else
            iPrintCode = 5;
        return bRet;
    }

    public String PRTQuerySDKVersion()
    {
        Log.d("PRTLIB", "QuerySDKVersion...");
        return "M.1.0.1";
    }

    private static final boolean Dbg = false;
    private static final String TAG = "PRTLIB";
    private static final String pMACAddr = "00:02:5B:B2:";
    private static final int ST_OK = 0;
    private static final int ST_ENABLE = 1;
    private static final int ST_BOND = 2;
    private static final int ST_CONNECT = 3;
    private BTOperator bto;
    private BTPrinter btp;
    public static final int AT_LEFT = 0;
    public static final int AT_CENTER = 1;
    public static final int AT_RIGHT = 2;
    public static final int BC_UPCA = 65;
    public static final int BC_UPCB = 66;
    public static final int BC_EAN13 = 67;
    public static final int BC_EAN8 = 68;
    public static final int BC_CODE39 = 69;
    public static final int BC_ITF = 70;
    public static final int BC_CODEBAR = 71;
    public static final int BC_CODE93 = 72;
    public static final int BC_CODE128 = 73;
    public static final int BC_DEFAULT = 0;
    public static final int BC_HRINONE = 0;
    public static final int BC_HRIUNDER = 1;
    public static final int BC_HRIBELOW = 2;
    public static final String TC_GB2312 = "gb2312";
    public static final String TC_UTF8 = "utf-8";
    public static final int PS_NORMAL = 0;
    public static final int PS_PRAPEROUT = 3;
    public static final int PS_ERR = 5;
    private int iStatusCode;
    private int iPrintCode;
    private static final String SDK_VERSION = "PRT.1.0.1";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
}