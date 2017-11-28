package win.betty35.myPRL.C;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import win.betty35.myPRL.C.pageParser.JDParser;
import win.betty35.myPRL.C.pageParser.TaoBaoParser;


public class MyProcessor implements PageProcessor
{
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	public void process(Page page) {
		// TODO Auto-generated method stub
		
		String url=page.getUrl().toString();
		System.out.println("已获取一个页面,处理中:"+url);
		long sleepTime=Math.round(Math.random()*10);
		/*try {
			Thread.sleep(sleepTime*1000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}*/
		
		//String html=page.getHtml().toString();
		//System.out.println(html);
	
		if(url.contains("s.taobao.com"))
			page=new TaoBaoParser(page).process();
		if(url.contains("search.jd.com"))
			page=new JDParser(page).process();
		System.out.println("Finished parsing....");
		sleepTime=Math.round(Math.random()*20);
		try {
			Thread.sleep(sleepTime*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
