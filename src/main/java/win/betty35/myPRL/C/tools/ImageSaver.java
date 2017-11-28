package win.betty35.myPRL.C.tools;
import java.io.ByteArrayInputStream;
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.InputStream;  
import java.io.OutputStream;  

public class ImageSaver 
{
    public static void download(byte[] b,String filename,String savePath) throws Exception 
    {        
        InputStream is = new ByteArrayInputStream(b);
        byte[] bs = new byte[1024];  
        int len;  
       File sf=new File(savePath);  
       if(!sf.exists()){  
           sf.mkdirs();  
       }  
       OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);  
        while ((len = is.read(bs)) != -1) {  
          os.write(bs, 0, len);  
        }  
        os.close();  
        is.close();  
    }   	

    
    public static void download(InputStream a,String filename,String savePath) throws Exception 
    {  
        InputStream is = a;
        byte[] bs = new byte[1024];  
        int len;  
       File sf=new File(savePath);  
       if(!sf.exists()){  
           sf.mkdirs();  
       }  
       OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);  
        while ((len = is.read(bs)) != -1) {  
          os.write(bs, 0, len);  
        }  
        os.close();  
        is.close();  
    }   

}
