package win.betty35.myPRL.C.pageParser;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.Page;
import win.betty35.myPRL.C.bean.Product;
import win.betty35.myPRL.C.bean.TBShopCard;
import win.betty35.myPRL.C.tools.PicGetter;
import win.betty35.myPRL.C.tools.TBScoreGetter;

public class TaoBaoParser implements PageParser
{
	private Page page=null;
	private ArrayList<Product> al=new ArrayList<Product>();
	private String url=null;
	
	public TaoBaoParser(Page page)
	{
		this.page=page;
		this.url=page.getUrl().toString();
	}

	public Page process()
	{
		System.out.println("Start parsing TaoBao search page...");
		boolean shouldstop=false;
		Document d=Jsoup.parse(page.getHtml().toString(),url);
		Elements items=d.getElementsByClass("J_MouserOnverReq");
		int sum=items.size();
		System.out.println("Number of products in this page:"+sum);
		//PicGetter pg=new PicGetter();
		TBScoreGetter sg=new TBScoreGetter();
		int place=url.indexOf("&s=");
		//calculate page number
		String page_base=null;
		if(place!=-1)
			page_base=url.substring(url.indexOf("&s=")+3).split("&")[0];
		else page_base="1";
		
		int page_b=1;
		if(place==-1) page_b=1;
		else page_b=1+Integer.parseInt(page_base)/44;
		
		//parse products
		for(int i=0;i<sum;i++)
		{
			System.out.println("Parsing "+(i+1)+" product of this page");
			try {
				Thread.sleep(3*1000);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			Element e=items.get(i);
			Element picbox=e.getElementsByClass("pic").get(0);
			String ImgURL=picbox.getElementsByTag("img").get(0).attr("src");
			if(ImgURL==null||ImgURL=="")
				ImgURL=picbox.getElementsByTag("img").get(0).attr("data-ks-lazyload");
			System.out.println("图片url:"+"https:"+ImgURL);
			String[] strs = ImgURL.split("/");
	        String fileName = strs[strs.length-1];
			/*try {
				pg.savepic("http:"+ImgURL, fileName);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("未能成功下载图片");
			}*/
			
	        Product p=new Product();
	        p.setFilename(fileName);
			p.setId(Long.parseLong(picbox.getElementsByTag("a").get(0).attr("data-nid")));
			//p.setFilename(fileName);
			Element title=e.getElementsByClass("row-2").get(0).getElementsByTag("a").get(0);
			p.setTitle(title.text());
			System.out.println(title.text());
			String shopid=e.getElementsByClass("shop").get(0).getElementsByTag("a").get(0).attr("data-userid");
			try {
				TBShopCard sc=sg.getScore(shopid);
				p.setAttitude(sc.getAttitude());
				p.setDelivery(sc.getDelivery());
				p.setDescription(sc.getDescription());
				p.setShop(sc.getShop());
				p.setShopId(sc.getShopId());
				if(sc.getIsTmall())p.setSource("Tmall");else p.setSource("TB");
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("未能成功抓到店铺打分");
			}
			
			Element price_sells=e.getElementsByClass("row-1").get(0);
			double price=Double.parseDouble(price_sells.getElementsByTag("strong").get(0).text());
			String sells=price_sells.getElementsByClass("deal-cnt").get(0).text().split("人")[0];
			p.setPrice(price);
			p.setSells(sells);
			if(Long.parseLong(sells)<=10) shouldstop=true;
			p.setPlace(e.getElementsByClass("location").get(0).text());
			
			int k=i+1;
			p.setPage(page_b+";"+k);
			
			al.add(p);
		}
		sg.close();
		page.putField("ResultList", al);
		Elements links=d.select("a[data-url=pager]");
		ArrayList<String> linklist=new ArrayList<String>();
		if(!shouldstop)
		{
			for(int i=0;i<links.size();i++)
			{
				String link=links.get(i).attr("href").replaceAll("#","");
				if(link.contains("&s="))
				link=link.substring(0, link.indexOf("&s="));
				if(links.get(i).attr("data-value").contains(","))
					continue;
				link=link+"&s="+links.get(i).attr("data-value");
				System.out.println("获取连接： "+link);
				linklist.add(link);
			}
			System.out.println("添加新连接完毕");
			page.addTargetRequests(linklist);
		}
		else System.out.println("销售量过低，不再增加爬取页数");
		return page;
	}
}
