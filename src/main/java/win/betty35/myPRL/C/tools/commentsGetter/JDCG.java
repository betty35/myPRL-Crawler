package win.betty35.myPRL.C.tools.commentsGetter;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import win.betty35.myPRL.C.dbUtils.Configure;
import win.betty35.myPRL.C.dbUtils.DBLink;
import win.betty35.myPRL.C.tools.RequestSender;

public class JDCG {
/*
 * JingDong Comments Getter
 * */
	public JDCG(){}
	
	public static String getComments(Long id,Long PID)
	{
		System.out.println("Getting comments for products from JD");
		Configure c=new Configure();
		String result="";
		int i=0;
		int max=0;
		while(true)
		{
		ArrayList<NameValuePair> nvl=new ArrayList<NameValuePair>();
		String url="https://club.jd.com/comment/productPageComments.action";
		nvl.add(new BasicNameValuePair("productId",id.toString()));
		nvl.add(new BasicNameValuePair("score","0"));
		nvl.add(new BasicNameValuePair("sortType","5"));
		nvl.add(new BasicNameValuePair("isShadowSku","0"));
		nvl.add(new BasicNameValuePair("pageSize","10"));
		RequestSender rs=new RequestSender();
		nvl.add(new BasicNameValuePair("page",""+i));
		try {
			String re=rs.get(url, nvl, "https://item.jd.com/"+id+".html#comment");
			//System.out.println(re);
			re.replaceAll("\n", "");
			System.out.println(re);
			JSONObject res=(JSONObject) JSONValue.parseStrict(re);
			JSONArray ar=(JSONArray) res.get("comments");
			//result=result.concat(ar.toJSONString());
			for(int j=0;j<ar.size();j++)
			{
				JSONObject ci=(JSONObject)ar.get(j);
				DBLink dbl=new DBLink(c);
				Long rateId=Long.parseLong(ci.get("id").toString());
				result=ci.get("content").toString();
				dbl.insertComment(PID, result, false,rateId, "JD");
				
				int after=Integer.parseInt(ci.get("afterDays").toString());
				if(after!=0)
				try{
					JSONObject c2=(JSONObject)ci.get("afterUserComment");
					JSONObject c3=(JSONObject)c2.get("hAfterUserComment");
					JSONObject c4=(JSONObject)c3.get("content");
					String reply=c4.toString().concat("◇").concat(""+after);
					dbl.insertComment(PID, reply, true,rateId, "JD");
				}
				catch(Exception ee1)
				{
				}
				dbl.close();
			}
			max=Integer.parseInt(((JSONObject) res.get("maxPage")).toString());
			Long time=Math.round(Math.random()*10)+10;
			Thread.sleep(time*1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			i--;
		}
		finally{rs.close();}
		if(i==max)break;else i++;
		if(i>=10) break;
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
