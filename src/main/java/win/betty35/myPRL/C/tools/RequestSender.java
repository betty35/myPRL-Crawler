package win.betty35.myPRL.C.tools;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;  
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;



public class RequestSender {
	 private CloseableHttpClient httpClient; 
     
	public RequestSender()
     {
    	httpClient= HttpClients.createDefault();  
     }
     
	 
	 public String get(String url,List<NameValuePair> nvl,String referer) throws Exception
	 {
		 if(nvl!=null)
		 for(int i=0;i<nvl.size();i++)
		 {
			 String name=URLEncoder.encode(nvl.get(i).getName(),"utf-8");
			 String value=URLEncoder.encode(nvl.get(i).getValue(),"utf-8");
			 if(i==0)url=url.concat("?");
			 url=url.concat(name).concat("=").concat(value);
			 if(i!=nvl.size()-1) url=url.concat("&");
		 }
		 System.out.println("getting from url: "+url);
	         HttpGet get = new HttpGet(url);
	         if(referer!=null)get.setHeader("Referer", referer);
	         get.setHeader("Connection","keep-alive");
	         get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
	         
	         HttpEntity entity = null;
	         CloseableHttpResponse response =httpClient.execute(get);
	         entity = response.getEntity();
	         String re=EntityUtils.toString(entity,"utf-8");
	         get.releaseConnection();
	         return re;
	 }	 
	 
	 public String post(String url,List<NameValuePair> nvl,String referer) throws Exception
	 {
		 HttpPost post=new HttpPost(url);
		 if(referer!=null)post.setHeader("Referer",referer);
		 post.setHeader("Connection","keep-alive");
		 post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
		 if(nvl!=null)post.setEntity(new UrlEncodedFormEntity(nvl,"utf-8"));
		 CloseableHttpResponse response =httpClient.execute(post);
		 HttpEntity entity = response.getEntity();
         String re=EntityUtils.toString(entity,"utf-8");
		 post.releaseConnection();
         return re;
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
