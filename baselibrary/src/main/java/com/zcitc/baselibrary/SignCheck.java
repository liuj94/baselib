package com.zcitc.baselibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * 增加签名检验方法
 * private void singCheck() {
 *         //
 *         SignCheck signCheck = new SignCheck(this, "88:E4:FE2:36:BF:C2:65:26:73:D5:B2:71:F7D:32:2A");
 *         // 第2步查看签名指纹的SHA1
 *         if (signCheck.check()) {
 *         // 签名正常
 *         } else {
 *         // 签名异常
 *         signCheck.showCheckErrorTips();
 *         }
 *         }
 *
 */

public class SignCheck {
    private Context context;
    private String cer = null;
    private String realCer = "83:79:97:37:1F:67:6F:67:5A:C4:32:B3:1D:B2:46:9A:4B:83:97:99";
    private static final String TAG = "SignCheck";

    public SignCheck(Context context) {
        this.context = context;
        this.cer = getCertificateSHA1Fingerprint();
    }

    public SignCheck(Context context, String realCer) {
        this.context = context;
        this.realCer = realCer;
        this.cer = getCertificateSHA1Fingerprint();
    }

    public String getRealCer() {
        return realCer;
    }

    /**
     * 设置正确的签名
     *
     * @param realCer
     */
    public void setRealCer(String realCer) {
        this.realCer = realCer;
    }

    /**
     * 获取应用的签名
     *
     * @return
     */
    public String getCertificateSHA1Fingerprint() {
        //获取包管理器
        PackageManager pm = context.getPackageManager();

        //获取当前要获取 SHA1 值的包名，也可以用其他的包名，但需要注意，
        //在用其他包名的前提是，此方法传递的参数 Context 应该是对应包的上下文。
        String packageName = context.getPackageName();

        //返回包括在包中的签名信息
        int flags = PackageManager.GET_SIGNATURES;

        PackageInfo packageInfo = null;

        try {
            //获得包的所有内容信息类
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //签名信息
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();

        //将签名转换为字节数组流
        InputStream input = new ByteArrayInputStream(cert);

        //证书工厂类，这个类实现了出厂合格证算法的功能
        CertificateFactory cf = null;

        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //X509 证书，X.509 是一种非常通用的证书格式
        X509Certificate c = null;

        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String hexString = null;

        try {
            //加密算法的类，这里的参数可以使 MD4,MD5 等加密算法
            MessageDigest md = MessageDigest.getInstance("SHA1");

            //获得公钥
            byte[] publicKey = md.digest(c.getEncoded());

            //字节到十六进制的格式转换
            hexString = byte2HexFormatted(publicKey);

        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    //这里是将获取到得编码进行16 进制转换
    private String byte2HexFormatted(byte[] arr) {

        StringBuilder str = new StringBuilder(arr.length * 2);

        for (int i = 0; i <arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l =h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }

    /**
     * 检测签名是否正确
     * @return true 签名正常 false 签名不正常
     */
    public boolean check() {

        if (this.realCer != null) {
            cer = cer.trim();
            realCer = realCer.trim();
            //Log.e(TAG, "未给定真实的签名 SHA-1 值");
            //Log.e(TAG, "cer="+cer);
            //Log.e(TAG, "realCer="+realCer);

            if (this.cer.equals(this.realCer)) {
                return true;
            }
        }else {
            Log.e(TAG, "未给定真实的签名 SHA-1 值");
        }
        return false;
    }

    public void checkSuccess() {
        new AlertDialog.Builder(context)
                .setTitle("签名校验成功")
                .setMessage("成功！")
                .setPositiveButton("确定", null)
                .show();
    }

    public void showCheckErrorTips() {
        new AlertDialog.Builder(context)
                .setTitle("签名校验失败")
                .setMessage("存在签名异常，请在官方下载最新的APP!")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }

                })
                .show();
    }
    public void showCheckRootTips() {
        new AlertDialog.Builder(context)
                .setTitle("异常提醒")
                .setMessage("设备已被Root")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }

                })
                .show();
    }
}




