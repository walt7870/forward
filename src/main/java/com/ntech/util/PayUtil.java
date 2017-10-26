package com.ntech.util;

import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.*;
import com.pingplusplus.model.Charge;
import org.apache.commons.codec.binary.Base64;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PayUtil {
    //设置apiKey
    private static String apiKey ;
    //设置appid
    private  static String appId ;

    // 设置私钥
    private  static String privateKeyPath ;
    //设置签名  用于测试  实际在http请求中获取
    private    static  String signaturePath;
    //设置webhooks的原始数据  用于测试
    private  static  String dataPath;
    //设置公钥
    private  static String   publicPath;

    static {
        apiKey = "sk_test_anj9C08CuPi51C8C4GfLOGyT";
        appId = "app_qrjLSKXLW94KuPuX";
        privateKeyPath=getPath("rsa_private_key.pem");
        signaturePath=getPath("signature.txt");
        dataPath=getPath("webhooks_raw_post_data.json");
        publicPath=getPath("pingpp_public_key.pem");
    }
    private static  String getPath(String fileName){

        return Thread.currentThread().getContextClassLoader().getResource(fileName).getPath();
    }
             //签名验证测试
    public static  void main(String[] args) throws Exception {
         String data=getStringFromFile(dataPath);
         System.out.println("--------post数据--------");
         System.out.println(data);
         String signature=getStringFromFile(signaturePath);
         System.out.println("--------签名数据-----------");
         System.out.println(signature);
         boolean result=verifyData(data,signature,getPublicKey());
         System.out.println(result?"验证成功":"验证失败");
//        System.out.println(getPath("signature.txt"));

    }
    //创建订单
    public static Charge createCharge(int amount, String channel) {
        Pingpp.apiKey = apiKey;
        Pingpp.privateKeyPath =privateKeyPath;
        Pingpp.appId = appId;
        Charge charge = null;
        Map<String, Object> chargeMap = new HashMap<>();
        //设置金额  单位为分
        chargeMap.put("amount", amount );
        chargeMap.put("currency", "cny");
        chargeMap.put("subject", "sdk的使用费");

        chargeMap.put("body", "xxxx");
        //流水号
        String orderNo = String.valueOf(System.currentTimeMillis());
        chargeMap.put("order_no", orderNo);
        //设置支付渠道
        chargeMap.put("channel", channel);
        //设置发起交易的客户端ip
        chargeMap.put("client_ip", "127.0.0.1");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);//15分钟失效
        long timestamp = cal.getTimeInMillis() / 1000L;
        chargeMap.put("time_expire", timestamp);
        Map<String, String> app = new HashMap<>();
        app.put("id", appId);
        chargeMap.put("app", app);
        Map<String, String> extra = new HashMap<>();
        extra.put("success_url", "http://127.0.0.1:8080");
        chargeMap.put("extra", extra);
        try {
            //发起交易请求
            charge = Charge.create(chargeMap);
            // 传到客户端请先转成字符串 .toString(), 调该方法，会自动转成正确的 JSON 字符串
            String chargeString = charge.toString();
            System.out.println(chargeString);
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (ChannelException e) {
            e.printStackTrace();
        } catch (RateLimitException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        }
        return charge;
    }

    /**
     * 查询 Charge
     * <p>
     * 该接口根据 charge Id 查询对应的 charge 。
     * 参考文档：https://www.pingxx.com/api#api-c-inquiry
     * <p>
     * 参考文档： https://www.pingxx.com/api#api-expanding
     *
     * param id
     */
    public static Charge retrieve(String id) {
        Pingpp.apiKey = apiKey;
        Pingpp.privateKeyPath = privateKeyPath;

        Charge charge = null;
        try {
            Map<String, Object> params = new HashMap<>();
            charge = Charge.retrieve(id, params);
            System.out.println(charge);
        } catch (PingppException e) {
            e.printStackTrace();
        }

        return charge;
    }

    /**
     * 撤销 Charge
     *
     * param id
     */
    public static Charge reverse(String id) {
        Pingpp.apiKey = apiKey;
        Pingpp.privateKeyPath = privateKeyPath;

        Charge charge = null;
        try {
            charge = Charge.reverse(id);
            System.out.println(charge);
        } catch (PingppException e) {
            e.printStackTrace();
        }

        return charge;
    }
    //获取公钥
    public static PublicKey getPublicKey() throws Exception {
         //String publicKey=getStringFromFile(publicKeyPath);
        String publicKey=getStringFromFile(publicPath);
        publicKey=publicKey.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
        byte[] keyBytes= Base64.decodeBase64(publicKey);
         KeyFactory keyFactory=KeyFactory.getInstance("RSA");
         return  keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }
    //验证密匙
    public static boolean verifyData(String dataString, String signatureString, PublicKey publicKey)
            throws NoSuchAlgorithmException,InvalidKeyException,SignatureException,UnsupportedEncodingException {
   byte[] signatureBytes = Base64.decodeBase64(signatureString);
   Signature signature = Signature.getInstance("SHA256withRSA");
   signature.initVerify(publicKey);
   signature.update(dataString.getBytes("UTF-8"));
    return signature.verify(signatureBytes);
  }
    //从文件中读取字符
    private  static String getStringFromFile(String filePath) throws Exception {
        FileInputStream in = new FileInputStream(filePath);
        InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
        BufferedReader bf = new BufferedReader(inReader);
        StringBuilder sb = new StringBuilder();
        String line;
        do {
            line = bf.readLine();
            if (line != null){
            if(sb.length()!=0){
            sb.append("\n");
                }
            sb.append(line);
            }
      }while(line!=null);
        return sb.toString();
}
}
