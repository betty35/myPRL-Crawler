package win.betty35.myPRL.C.pageParser;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import win.betty35.myPRL.C.bean.Product;
import win.betty35.myPRL.C.bean.TBShopCard;
import win.betty35.myPRL.C.tools.HtmlScript;
import win.betty35.myPRL.C.tools.PicGetter;
import win.betty35.myPRL.C.tools.TBScoreGetter;

public class JDParser implements PageParser
{
	private Page page=null;
	private String url=null;
	private ArrayList<Product> al=new ArrayList<Product>();
	
	public JDParser(Page page)
	{
		this.page=page;
		this.url=page.getUrl().toString();
	}

	public Page process()
	{
		System.out.println("Parsing JD search page....");
		Document d=Jsoup.parse(page.getHtml().toString(),url);
		Elements items=d.getElementsByClass("gl-item");
		int sum=items.size();
		System.out.println(sum);
		
		for(int i=0;i<sum;i++)
		{
			System.out.println("now:"+i);
			Element e1=items.get(i);
			String itemID=e1.attr("data-pid");
			//System.out.println(itemID);
			Element e=e1.getElementsByClass("gl-i-wrap").get(0);
			//System.out.println("gl-i-warp:"+e.toString());
			Element picbox=e.getElementsByClass("p-img").get(0).getElementsByTag("a").get(0).getElementsByTag("img").get(0);
			//System.out.println(picbox.text());
			String ImgURL;
			if(picbox.hasAttr("src"))
			ImgURL=picbox.attr("src");
			else ImgURL=picbox.attr("data-lazy-img");
			System.out.println("图片url:"+"https:"+ImgURL);
			
	        String fileName = ImgURL.substring(ImgURL.indexOf("jsf/")+4);
			//下面没弄完
	        Product p=new Product();
			p.setId(Long.parseLong(itemID));
			p.setFilename(fileName);
			Element title=e.getElementsByClass("p-name").get(0).getElementsByTag("a").get(0);
			p.setTitle(HtmlScript.delHTMLTag(title.text()).replaceAll("\\s+",""));
			System.out.println(title.text());
			String shopid;
			Element shop=e.getElementsByClass("p-shop").get(0);
			if(shop.hasAttr("data-done"))
			{
				shop=shop.getElementsByTag("a").get(0);
				shopid=shop.attr("href");
				shopid=shopid.substring(shopid.indexOf("index-"), shopid.indexOf(".html"));
			}
			else shopid=null;
			if(shopid!=null)
			System.out.println("shopid:"+shopid);
			
			p.setShop(shopid);
			
			Element price_sells=e.getElementsByClass("p-price").get(0).getElementsByTag("strong").get(0).getElementsByTag("i").get(0);
			float price=Float.parseFloat(price_sells.text());
			String sells=e.getElementsByClass("p-commit").get(0).getElementsByTag("strong").get(0).getElementsByTag("a").get(0).text();
			System.out.println("reviews:"+sells);
			p.setPrice(price);
			p.setSells(sells);
			p.setPlace(null);
			p.setSource("JD");
			String page_base=url.substring(url.indexOf("page=")+5).split("&")[0];
			int k=i+1;
			p.setPage(page_base+";"+k);
			al.add(p);
		}
		
		page.putField("ResultList", al);
		
		System.out.println("信息获取完毕");
		Element pages=d.getElementById("J_topPage").getElementsByTag("span").get(0).getElementsByTag("i").get(0);
		System.out.println(pages.text());
		if(!url.contains("page")||url.contains("page=1"))
		{
			String base=url;
			String query=url.substring(url.indexOf("keyword=")+8).split("&")[0];
			if(url.contains("page")) base=url.substring(0, url.indexOf("&page"));
			ArrayList<String> linklist=new ArrayList<String>();
			for(int i=1;i<Integer.parseInt(pages.text());i++)
			{
				String link=base+"&page="+(i*2+1);
				linklist.add(link);
				/*String link2="https://search.jd.com/s_new.php?keyword="+query+"&enc=utf-8&page="+i+"&scrolling=y";
				linklist.add(link2);*/
				if(i>3) break;
			}
		System.out.println("添加新连接完毕");
		page.addTargetRequests(linklist);
		}
		return page;
	}
}
