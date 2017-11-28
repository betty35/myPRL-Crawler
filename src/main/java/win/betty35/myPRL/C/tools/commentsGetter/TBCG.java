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

public class TBCG {
/*
 * TaoBao Comments Getter
 * */
	public TBCG(){}
	
	public static String getComments(Long id,Long shopId,Long PID)
	{
		Configure c=new Configure();
		String result="";
		int i=1;
		int max=0;
		while(true)
		{
		ArrayList<NameValuePair> nvl=new ArrayList<NameValuePair>();
		String url="https://rate.taobao.com/feedRateList.htm";
		nvl.add(new BasicNameValuePair("auctionNumId",id.toString()));
		nvl.add(new BasicNameValuePair("userNumId",shopId.toString()));
		nvl.add(new BasicNameValuePair("pageSize","20"));
		nvl.add(new BasicNameValuePair("orderType","sort_weight"));
		RequestSender rs=new RequestSender();
		nvl.add(new BasicNameValuePair("currentPageNum",""+i));
		try {
			String re=rs.get(url, nvl, "https://item.taobao.com/item.htm?id="+id);
			re=re.substring(3,re.length()-2);
			//System.out.println(re);
			JSONObject res=(JSONObject) JSONValue.parseStrict(re);
			JSONArray ar=(JSONArray) res.get("comments");
			//result=result.concat(ar.toJSONString());
			for(int j=0;j<ar.size();j++)
			{	DBLink dbl=new DBLink(c);
				JSONObject o=(JSONObject)ar.get(j);
				Long rateId=Long.parseLong(o.get("rateId").toString());
				result=o.get("content").toString();
				result=HtmlScript.delHTMLTag(result);
				if(notValid(result))
				dbl.insertComment(PID, result, false,rateId, "TB");
				JSONObject o2=(JSONObject)o.get("append");
				if(o2!=null)
				{
					result=o2.get("content").toString();
					result=result.concat("※");
					result=result.concat(o2.get("dayAfterConfirm").toString());
					result=HtmlScript.delHTMLTag(result);
					if(result.length()>5)
					dbl.insertComment(PID, result, true, rateId, "TB");
				}
				dbl.close();
			}
			max=Integer.parseInt(res.get("maxPage").toString());
			Long time=Math.round(Math.random()*10)+10;
			Thread.sleep(time*1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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
