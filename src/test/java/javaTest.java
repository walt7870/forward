import com.ntech.forward.HttpUploadFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class javaTest {
    public static void main(String[] args) {
        Map<String,String> header = new HashMap<String, String>();
        Map<String,String> param = new HashMap<String, String>();
        Map<String,Object> file = new HashMap<String, Object>();
        String reply="";
        header.put("Method","POST");
        header.put("API","/n-tech/v0/detect");
        File file1 = new File("/home/testPic/timg.jpeg");
        try {
            InputStream inputStream = new FileInputStream(file1);
            //inputStream.available是图片的length，最好用良田的方法来获取这个length
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            file.put("photo",buffer);

            reply = HttpUploadFile.getInstance().httpURLConnectionSDK(header,param,file,"isFile");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(reply);
    }
}