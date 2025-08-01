package com.zkteco.biometric;

import edu.practice.sica.entity.AttendanceRecords;
import edu.practice.sica.entity.Fingerprint;
import edu.practice.sica.entity.enums.RecordType;
import edu.practice.sica.service.AttendanceRecordsService;
import edu.practice.sica.service.FingerprintService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class SicaEnrollmentTool extends JFrame{
    private final FingerprintService fingerprintService;
    private final AttendanceRecordsService attendanceRecordsService;
    JButton btnOpen = null;
    JButton btnEnroll = null;
    JButton btnVerify = null;
    JButton btnIdentify = null;
    JButton btnRegImg = null;
    JButton btnIdentImg = null;
    JButton btnClose = null;
    JButton btnImg = null;


    private JTextArea textArea;

    int fpWidth = 0;
    int fpHeight = 0;
    private byte[] lastRegTemp = new byte[2048];
    private int cbRegTemp = 0;
    private byte[][] regtemparray = new byte[3][2048];
    private boolean bRegister = false;
    private boolean bIdentify = true;
    private int iFid = 1;

    private int nFakeFunOn = 1;
    static final int enroll_cnt = 3;
    private int enroll_idx = 0;

    private byte[] imgbuf = null;
    private byte[] template = new byte[2048];
    private int[] templateLen = new int[1];


    private boolean mbStop = true;
    private long mhDevice = 0;
    private long mhDB = 0;
    private WorkThread workThread = null;

    public SicaEnrollmentTool(FingerprintService fingerprintService, AttendanceRecordsService attendanceRecordsService) {
        this.fingerprintService = fingerprintService;
        this.attendanceRecordsService = attendanceRecordsService;
        launchFrame();
    }

    public void launchFrame(){
        this.setLayout (null);
        btnOpen = new JButton("Open");
        this.add(btnOpen);
        int nRsize = 20;
        btnOpen.setBounds(30, 10 + nRsize, 100, 30);

        btnEnroll = new JButton("Enroll");
        this.add(btnEnroll);
        btnEnroll.setBounds(30, 60 + nRsize, 100, 30);

        btnVerify = new JButton("Verify");
        this.add(btnVerify);
        btnVerify.setBounds(30, 110 + nRsize, 100, 30);

        btnIdentify = new JButton("Identify");
        this.add(btnIdentify);
        btnIdentify.setBounds(30, 160 + nRsize, 100, 30);

        btnRegImg = new JButton("Register By Image");
        this.add(btnRegImg);
        btnRegImg.setBounds(15, 210 + nRsize, 120, 30);

        btnIdentImg = new JButton("Verify By Image");
        this.add(btnIdentImg);
        btnIdentImg.setBounds(15, 260 + nRsize, 120, 30);


        btnClose = new JButton("Close");
        this.add(btnClose);
        btnClose.setBounds(30, 310 + nRsize, 100, 30);


        btnImg = new JButton();
        btnImg.setBounds(150, 5, 256, 300);
        btnImg.setDefaultCapable(false);
        this.add(btnImg);

        textArea = new JTextArea();
        this.add(textArea);
        textArea.setBounds(10, 440, 480, 100);


        this.setSize(520, 580);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setTitle("SICA ENROLLMENT");
        this.setResizable(false);

        btnOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (0 != mhDevice)
                {
                    textArea.setText("Please close device first!");
                    return;
                }
                int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
                cbRegTemp = 0;
                bRegister = false;
                bIdentify = false;
                iFid = 1;
                enroll_idx = 0;
                if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init())
                {
                    textArea.setText("Init failed!");
                    return;
                }
                ret = FingerprintSensorEx.GetDeviceCount();
                if (ret < 0)
                {
                    textArea.setText("No devices connected!");
                    FreeSensor();
                    return;
                }
                if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0)))
                {
                    textArea.setText("Open device fail, ret = " + ret + "!");
                    FreeSensor();
                    return;
                }
                if (0 == (mhDB = FingerprintSensorEx.DBInit()))
                {
                    textArea.setText("Init DB fail, ret = " + ret + "!");
                    FreeSensor();
                    return;
                }

                byte[] paramValue = new byte[4];
                int[] size = new int[1];

                size[0] = 4;
                FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
                fpWidth = byteArrayToInt(paramValue);
                size[0] = 4;
                FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
                fpHeight = byteArrayToInt(paramValue);
                imgbuf = new byte[fpWidth*fpHeight];
                btnImg.resize(fpWidth, fpHeight);
                mbStop = false;
                workThread = new WorkThread();
                workThread.start();
                textArea.setText("Open succ!");
            }
        });



        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FreeSensor();
                textArea.setText("Close succ!");
            }
        });

        btnEnroll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(0 == mhDevice)
                {
                    textArea.setText("Please Open device first!");
                    return;
                }
                if(!bRegister)
                {
                    enroll_idx = 0;
                    bRegister = true;
                    textArea.setText("Please your finger 3 times!");
                }
            }
        });

        btnVerify.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(0 == mhDevice)
                {
                    textArea.setText("Please Open device first!");
                    return;
                }
                if(bRegister)
                {
                    enroll_idx = 0;
                    bRegister = false;
                }
                if(bIdentify)
                {
                    bIdentify = false;
                }
            }
        });

        btnIdentify.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(0 == mhDevice)
                {
                    textArea.setText("Please Open device first!");
                    return;
                }
                if(bRegister)
                {
                    enroll_idx = 0;
                    bRegister = false;
                }
                if(!bIdentify)
                {
                    bIdentify = true;
                }
            }
        });


        btnRegImg.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(0 == mhDB)
                {
                    textArea.setText("Please open device first!");
                }
                String path = "d:\\test\\fingerprint.bmp";
                byte[] fpTemplate = new byte[2048];
                int[] sizeFPTemp = new int[1];
                sizeFPTemp[0] = 2048;
                int ret = FingerprintSensorEx.ExtractFromImage( mhDB, path, 500, fpTemplate, sizeFPTemp);
                if (0 == ret)
                {
                    ret = FingerprintSensorEx.DBAdd( mhDB, iFid, fpTemplate);
                    if (0 == ret)
                    {
                        iFid++;
                        cbRegTemp = sizeFPTemp[0];
                        System.arraycopy(fpTemplate, 0, lastRegTemp, 0, cbRegTemp);
                        textArea.setText("enroll succ");
                    }
                    else
                    {
                        textArea.setText("DBAdd fail, ret=" + ret);
                    }
                }
                else
                {
                    textArea.setText("ExtractFromImage fail, ret=" + ret);
                }
            }
        });


        btnIdentImg.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(0 ==  mhDB)
                {
                    textArea.setText("Please open device first!");
                }
                String path = "d:\\test\\fingerprint.bmp";
                byte[] fpTemplate = new byte[2048];
                int[] sizeFPTemp = new int[1];
                sizeFPTemp[0] = 2048;
                int ret = FingerprintSensorEx.ExtractFromImage(mhDB, path, 500, fpTemplate, sizeFPTemp);
                if (0 == ret)
                {
                    if (bIdentify)
                    {
                        int[] fid = new int[1];
                        int[] score = new int [1];
                        ret = FingerprintSensorEx.DBIdentify(mhDB, fpTemplate, fid, score);
                        if (ret == 0)
                        {
                            textArea.setText("Identify succ, fid=" + fid[0] + ",score=" + score[0]);
                        }
                        else
                        {
                            textArea.setText("Identify fail, errcode=" + ret);
                        }

                    }
                    else
                    {
                        if(cbRegTemp <= 0)
                        {
                            textArea.setText("Please register first!");
                        }
                        else
                        {
                            ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, fpTemplate);
                            if(ret > 0)
                            {
                                textArea.setText("Verify succ, score=" + ret);
                            }
                            else
                            {
                                textArea.setText("Verify fail, ret=" + ret);
                            }
                        }
                    }
                }
                else
                {
                    textArea.setText("ExtractFromImage fail, ret=" + ret);
                }
            }
        });


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                FreeSensor();
            }
        });
    }

    private void FreeSensor()
    {
        mbStop = true;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (0 != mhDB)
        {
            FingerprintSensorEx.DBFree(mhDB);
            mhDB = 0;
        }
        if (0 != mhDevice)
        {
            FingerprintSensorEx.CloseDevice(mhDevice);
            mhDevice = 0;
        }
        FingerprintSensorEx.Terminate();
    }

    public static void writeBitmap(byte[] imageBuf, int nWidth, int nHeight,
                                   String path) throws IOException {
        java.io.FileOutputStream fos = new java.io.FileOutputStream(path);
        java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

        int w = (((nWidth+3)/4)*4);
        int bfType = 0x424d;
        int bfSize = 54 + 1024 + w * nHeight;
        int bfReserved1 = 0;
        int bfReserved2 = 0;
        int bfOffBits = 54 + 1024;

        dos.writeShort(bfType);
        dos.write(changeByte(bfSize), 0, 4);
        dos.write(changeByte(bfReserved1), 0, 2);
        dos.write(changeByte(bfReserved2), 0, 2);
        dos.write(changeByte(bfOffBits), 0, 4);

        int biSize = 40;
        int biWidth = nWidth;
        int biHeight = nHeight;
        int biPlanes = 1;
        int biBitcount = 8;
        int biCompression = 0;
        int biSizeImage = w * nHeight;
        int biXPelsPerMeter = 0;
        int biYPelsPerMeter = 0;
        int biClrUsed = 0;
        int biClrImportant = 0;

        dos.write(changeByte(biSize), 0, 4);
        dos.write(changeByte(biWidth), 0, 4);
        dos.write(changeByte(biHeight), 0, 4);
        dos.write(changeByte(biPlanes), 0, 2);
        dos.write(changeByte(biBitcount), 0, 2);
        dos.write(changeByte(biCompression), 0, 4);
        dos.write(changeByte(biSizeImage), 0, 4);
        dos.write(changeByte(biXPelsPerMeter), 0, 4);
        dos.write(changeByte(biYPelsPerMeter), 0, 4);
        dos.write(changeByte(biClrUsed), 0, 4);
        dos.write(changeByte(biClrImportant), 0, 4);

        for (int i = 0; i < 256; i++) {
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(0);
        }

        byte[] filter = null;
        if (w > nWidth)
        {
            filter = new byte[w-nWidth];
        }

        for(int i=0;i<nHeight;i++)
        {
            dos.write(imageBuf, (nHeight-1-i)*nWidth, nWidth);
            if (w > nWidth)
                dos.write(filter, 0, w-nWidth);
        }
        dos.flush();
        dos.close();
        fos.close();
    }

    public static byte[] changeByte(int data) {
        return intToByteArray(data);
    }

    public static byte[] intToByteArray (final int number) {
        byte[] abyte = new byte[4];
        abyte[0] = (byte) (0xff & number);
        abyte[1] = (byte) ((0xff00 & number) >> 8);
        abyte[2] = (byte) ((0xff0000 & number) >> 16);
        abyte[3] = (byte) ((0xff000000 & number) >> 24);
        return abyte;
    }

    public static int byteArrayToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }

    private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            int ret = 0;
            while (!mbStop) {
                templateLen[0] = 2048;
                if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen)))
                {
                    if (nFakeFunOn == 1)
                    {
                        byte[] paramValue = new byte[4];
                        int[] size = new int[1];
                        size[0] = 4;
                        int nFakeStatus = 0;
                        ret = FingerprintSensorEx.GetParameters(mhDevice, 2004, paramValue, size);
                        nFakeStatus = byteArrayToInt(paramValue);
                        System.out.println("ret = "+ ret +",nFakeStatus=" + nFakeStatus);
                        if (0 == ret && (byte)(nFakeStatus & 31) != 31)
                        {
                            textArea.setText("Is a fake-finer?");
                            return;
                        }
                    }
                    OnCatpureOK(imgbuf);
                    OnExtractOK(template, templateLen[0]);
                    String strBase64 = FingerprintSensorEx.BlobToBase64(template, templateLen[0]);
                    System.out.println("strBase64=" + strBase64);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        private void runOnUiThread(Runnable runnable) {

        }
    }

    private void OnCatpureOK(byte[] imgBuf)
    {
        try {
            writeBitmap(imgBuf, fpWidth, fpHeight, "fingerprint.bmp");
            btnImg.setIcon(new ImageIcon(ImageIO.read(new File("fingerprint.bmp"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void OnExtractOK(byte[] template, int len) {
        if (bRegister) {
            int[] fid = new int[1];
            int[] score = new int[1];
            int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
            if (ret == 0) {
                textArea.setText("the finger already enroll by " + fid[0] + ",cancel enroll");
                bRegister = false;
                enroll_idx = 0;
                return;
            }
            if (enroll_idx > 0 && FingerprintSensorEx.DBMatch(mhDB, regtemparray[enroll_idx - 1], template) <= 0) {
                textArea.setText("please press the same finger 3 times for the enrollment");
                return;
            }
            System.arraycopy(template, 0, regtemparray[enroll_idx], 0, 2048);
            enroll_idx++;
            if (enroll_idx == 3) {
                int[] _retLen = new int[1];
                _retLen[0] = 2048;
                byte[] regTemp = new byte[_retLen[0]];

                if (0 == (ret = FingerprintSensorEx.DBMerge(mhDB, regtemparray[0], regtemparray[1], regtemparray[2], regTemp, _retLen))) {
                    textArea.setText("Local enrollment successful.\nSaving to database...");
                    String studentIdStr = "1";
                    try {
                        int studentId = Integer.parseInt(studentIdStr);
                        Fingerprint fingerprint = new Fingerprint();
                        fingerprint.setStudentId(studentId);
                        fingerprint.setFingerprintData(regTemp);
                        fingerprint.setRegistrationDate(LocalDateTime.now());
                        fingerprint.setFinger("thumb");
                        fingerprintService.createFingerprint(fingerprint);
                        textArea.setText("Fingerprint SAVED successfully to database for student ID: " + studentId);
                    } catch (Exception e) {
                        System.out.println("ERROR saving to database: " + e.getMessage());
                        textArea.setText("ERROR saving to database: " + e.getMessage());
                    }
                } else {
                    textArea.setText("Enrollment failed, error merging templates. Code: " + ret);
                }
                bRegister = false;
            } else {
                textArea.setText("You need to press the " + (3 - enroll_idx) + " times fingerprint");
            }
        } else {
            if (bIdentify) {
                textArea.setText("Identifying against database...");
                Optional<Fingerprint> matchResult = fingerprintService.identify(template, mhDB);

                if (matchResult.isPresent()) {
                    Fingerprint matchedFingerprint = matchResult.get();
                    textArea.setText("Identify SUCCESS!\nMatch found for Student ID: " + matchedFingerprint.getStudentId());
                    AttendanceRecords attendanceRecord = new AttendanceRecords();
                    attendanceRecord.setRecordType(RecordType.ENTRY);
                    attendanceRecord.setStatus("SUCCESS");
                    attendanceRecord.setPersonId(4L);
                    attendanceRecord.setRecordTimestamp(LocalDateTime.now());
                    attendanceRecord.setDeviceId("DEV001");
                    attendanceRecord.setGate("MAIN_GATE");

                    attendanceRecordsService.create(attendanceRecord);
                } else {
                    textArea.setText("Identify FAIL. No match found in the database.");
                }
            } else {
                if (cbRegTemp <= 0) {
                    textArea.setText("Please register first for 1:1 verification!");
                } else {
                    int ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, template);
                    if (ret > 0) {
                        textArea.setText("Verify succ, score=" + ret);
                    } else {
                        textArea.setText("Verify fail, ret=" + ret);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
    }
}
