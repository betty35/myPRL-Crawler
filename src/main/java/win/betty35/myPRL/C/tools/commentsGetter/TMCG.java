package win.betty35.myPRL.C.tools.commentsGetter;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import win.betty35.myPRL.C.dbUtils.Configure;
import win.betty35.myPRL.C.dbUtils.DBLink;
import win.betty35.myPRL.C.tools.HtmlScript;
import win.betty35.myPRL.C.tools.RequestSender;

public class TMCG {
/*
 * TMall Comments Getter
 * */
	public TMCG(){}
	
	public static String getComments(Long id,Long shopId,Long PID)
	{
		Configure c=new Configure();
		String result="";
		int i=1;
		int max=0;
		while(true)
		{
		ArrayList<NameValuePair> nvl=new ArrayList<NameValuePair>();
		String url="https://rate.tmall.com/list_detail_rate.htm";
		nvl.add(new BasicNameValuePair("itemId",id.toString()));
		nvl.add(new BasicNameValuePair("sellerId",shopId.toString()));
		nvl.add(new BasicNameValuePair("order","3"));
		//nvl.add(new BasicNameValuePair("append","1"));
		RequestSender rs=new RequestSender();
		nvl.add(new BasicNameValuePair("currentPage",""+i));
		try {
			String re=rs.get(url, nvl, "https://detail.tmall.com/item.htm?id="+id);
			re=re.substring(re.indexOf("rateDetail\":")+12);
			//System.out.println(re);
			JSONObject res=(JSONObject) JSONValue.parseStrict(re);
			JSONArray ar=(JSONArray) res.get("rateList");
			//result=result.concat(ar.toJSONString());
			for(int j=0;j<ar.size();j++)
			{
				//result=result.concat(((JSONObject)ar.get(j)).get("rateContent").toString()).concat("◇");
				DBLink dbl=new DBLink(c);
				JSONObject o=(JSONObject)ar.get(j);
				result=o.get("rateContent").toString();
				result=HtmlScript.delHTMLTag(result);
				Long originalID=Long.parseLong(((JSONObject)ar.get(j)).get("id").toString());
				if(notValid(result))
				dbl.insertComment(PID, result, false, originalID, "Tmall");
				//System.out.println(result);
				JSONObject apc=(o.get("appendComment") instanceof String)?null:(JSONObject)o.get("appendComment");
				//System.out.println("apc:"+apc.toString());
				//System.out.println(result);
				if(apc!=null)
				{
					
					String days=apc.get("days").toString();
					result=apc.get("content").toString();
					result=HtmlScript.delHTMLTag(result);
					if(result.length()>5){
					result=result.concat("※"+days);
					//System.out.println(result);	
					dbl.insertComment(PID, result, true, originalID, "Tmall");
					}
				}
				dbl.close();
			}
			max=Integer.parseInt(((JSONObject)res.get("paginator")).get("lastPage").toString());
			Long time=Math.round(Math.random()*10)+10;
			Thread.sleep(time*1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			i--;
		}
		finally{rs.close();}
		if(i==max)break;else i++;
		if(i>=5) break;
		}
		return result;
	}
	
	
	public static boolean notValid(String re)
	{
		if(re.length()<10)
			return false;
		if(re.equals("此用户没有填写评论!"))
			return false;
		if(re.equals("评价方未及时做出评价,系统默认好评!"))
			return false;
		return true;
	}
}
