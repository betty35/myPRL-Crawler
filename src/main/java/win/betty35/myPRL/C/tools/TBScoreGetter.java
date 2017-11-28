package win.betty35.myPRL.C.tools;

import java.io.IOException;

import org.apache.http.HttpEntity;  
import org.apache.http.HttpResponse;  
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import win.betty35.myPRL.C.bean.TBShopCard;



public class TBScoreGetter {
	 CloseableHttpClient httpClient; 
     String result = null;  
    
	public TBScoreGetter()
     {
    	httpClient= HttpClients.createDefault();  
     }
     
	 
	 public TBShopCard getScore(String sid) throws Exception
	 {
	        TBShopCard sc=new TBShopCard();
	        HttpGet get = new HttpGet("http://s.taobao.com/api?sid="+sid+"&callback=shopcard&app=api&m=get_shop_card");
	        System.out.println("获取店铺信息："+"http://s.taobao.com/api?sid="+sid+"&callback=shopcard&app=api&m=get_shop_card");
	        //set referer
	         get.setHeader("Referer", "http://www.taobao.com/");
	         get.setHeader("Connection","keep-alive");
	         get.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
	         HttpEntity entity = null;
	         HttpResponse response =httpClient.execute(get);
	         entity = response.getEntity();
	         String re=EntityUtils.toString(entity);
	         re=re.substring(11,re.length()-2);
	        //System.out.println(re);
	         JSONObject res=(JSONObject) JSONValue.parseStrict(re);
	         get.releaseConnection();	
	        // System.out.println(res);
	     
	         sc.setDescription(Float.parseFloat(res.get("matchDescription").toString()));
	         sc.setAttitude(Float.parseFloat(res.get("serviceAttitude").toString()));
	         sc.setDelivery(Float.parseFloat(res.get("deliverySpeed").toString()));
	         sc.setShop(res.get("title").toString());
	         sc.setShopId(Long.parseLong(res.get("userId").toString()));
	         sc.setIsTmall((res.get("isTmall").toString().equals("1")?true:false));
	         return sc;
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
