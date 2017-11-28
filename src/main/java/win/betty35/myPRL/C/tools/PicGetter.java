package win.betty35.myPRL.C.tools;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;  
import org.apache.http.HttpResponse;  
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import win.betty35.myPRL.C.dbUtils.Configure;




public class PicGetter {
	 CloseableHttpClient httpClient; 
     String result = null;  
    
	public PicGetter()
     {
    	httpClient= HttpClients.createDefault();  
     }
     
	 
	 public void savepic(String ImgURL,String filename) throws Exception
	 {
	        if(ImgURL == null||filename == null)
	        {
	            throw new Exception();
	        }
	        HttpGet get = new HttpGet(ImgURL);
	        
	        //set referer
	         get.setHeader("Referer", "http://www.taobao.com/");
	         get.setHeader("Connection","keep-alive");
	         get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
	         
	        //String[] strs = ImgURL.split("/");
	        //String fileName = strs[strs.length-1];
	         HttpEntity entity = null;
	         HttpResponse response =httpClient.execute(get);
	         entity = response.getEntity();
	         //System.out.println("saving>>>>.>>>>>>"+fileName);
	         InputStream is = entity.getContent();
	         Configure c=new Configure();
	         ImageSaver.download(is, filename,c.getBasepath()+"/pics");
	         get.releaseConnection();
   
	 }	 
	 
	 
	 
	public void close()
	 {
		 try {
			httpClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
