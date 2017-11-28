package win.betty35.myPRL.C;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import win.betty35.myPRL.C.dbUtils.Configure;
import win.betty35.myPRL.C.dbUtils.DBLink;
import win.betty35.myPRL.C.downloader.SeleniumDownloader;
import win.betty35.myPRL.C.pipeline.ProductPL;

/**
 * Hello world!
 *
 */
public class CrawlerApp 
{
	private static LinkedBlockingQueue<String[]> crawList; 
	public static boolean running;
	private static String[] keywords;
	private static boolean inited;
	
	public static void init()
	{
		if(!inited)
		{	
			running=false;
			crawList=new LinkedBlockingQueue<String[]>();
			inited=true;
		}
	}
	
	public static boolean inQueue(String searchedWords)
	{
		if(searchedWords.equals(seeKeywords())) return true;
		String[] sw=searchedWords.split(" ");
		if(crawList.contains(sw)) return true;
		return false;
	}
	
	public static String seeKeywords()
	{
		if(keywords==null)
			return null;
		String re="";
		for(int i=0;i<keywords.length;i++)
		{
			re=re+keywords[i];
			if(i!=(keywords.length-1))
				re=re+" ";
		}
		return re;
	}
	
	public static boolean addToList(String[] searchedWords)
	{
		try {
			crawList.put(searchedWords);
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
    public static void run() throws Exception
    {
    	/*ArrayList<String[]> li=new ArrayList<String[]>();
    	String[] l1={"手机"};
    	String[] l2={"扫地机器人"};
    	String[] l3={"加湿器","迷你"};
    	String[] l4={"台灯","led","充电"};
    	String[] l5={"料理机","多功能"};
    	String[] l6={"料理机","家用"};
    	String[] l7={"榨汁机","家用"};
    	String[] l8={"电饭煲"};
    	li.add(l1);*///li.add(l2);li.add(l3);li.add(l4);li.add(l5);li.add(l6);li.add(l7);li.add(l8);
    	//crawler
    	long startTime=System.currentTimeMillis();
    	keywords=crawList.poll();
    	while(keywords!=null)
    	{
    	//String[] keywords=li.get(m);   	
    	//final String[] keywords=args;
    	
    	Configure c=new Configure();
    	DBLink k=new DBLink(c);
    	boolean crawl=k.needToCrawl(keywords);
    	k.close();
    	System.out.println(crawl);
    	if(crawl){
    		System.out.println("Need to crawl for new information");
    		System.out.println("Configuring spiders...");
        CounterSpider s=new CounterSpider(new MyProcessor());
		s.thread(1)
		//.addPipeline(new MysqlPipeLineForTB())
		.setDownloader(new SeleniumDownloader());
       
        String keyword="";
        for(int i=0;i<keywords.length;i++)
        {
        	keyword=keyword.concat(keywords[i]);
        	if(i!=(keywords.length-1))
        		keyword=keyword.concat("+");
        }
        
        //s.addUrl("https://s.taobao.com/search?q="+URLEncoder.encode(keyword, "UTF-8"));
		
        s.addUrl("https://search.jd.com/Search?keyword="+URLEncoder.encode(keyword, "UTF-8")+"&enc=utf-8");
        ProductPL pl=new ProductPL();
		pl.setKeywords(keywords);
        s.addPipeline(pl);
        System.out.println("Configuration finished. Start crawling...");
        s.run();
        
        keywords=crawList.poll();
    	}
    	
    	
    	
    	long endTime=System.currentTimeMillis();
    	long ms=endTime-startTime;
    	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。  
    	String hms = formatter.format(ms);  
    	System.out.println("运行结束");
    	System.out.println("耗时："+hms);
    	running=false;
    	}
    	
    	
    	
    	
    	
    	/*System.out.println("start");
    	String str = "可以应用到自然语言处理等方面,适用于对分词效果要求高的各种项目." ;
    	 System.out.println(ToAnalysis.parse(str).toString());
    	 System.out.println("end");*/
    	
    	/*String re=TBCG.getComments(520292671302l, 34818355l);
    	System.out.println(NlpAnalysis.parse(re).toString());*/
    	
    	//String re=TMCG.getComments(43784374446l, 2422431610l,1l);
    	//System.out.println(NlpAnalysis.parse(re).toString());
    	
    	/*String re=JDCG.getComments(1300999l);
    	System.out.println(NlpAnalysis.parse(re).toString());*/
    	
    }
}
