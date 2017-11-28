package win.betty35.myPRL.C.pipeline;

import java.util.ArrayList;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import win.betty35.myPRL.C.bean.Product;
import win.betty35.myPRL.C.dbUtils.Configure;
import win.betty35.myPRL.C.dbUtils.DBLink;
import win.betty35.myPRL.C.tools.commentsGetter.JDCG;
import win.betty35.myPRL.C.tools.commentsGetter.TBCG;
import win.betty35.myPRL.C.tools.commentsGetter.TMCG;

public class ProductPL implements Pipeline
{	
	private String[] keywords=null;
	
	@SuppressWarnings("unchecked")
	public void process(ResultItems re, Task t)
	{
		System.out.println("Saving data...");
		Configure c=new Configure();
		DBLink a=new DBLink(c);
		String keywordsIDs=a.getSearchedWordsIDs(keywords);
		a.close();
		
		ArrayList<Product> al=null;
	
			al=(ArrayList<Product>)re.get("ResultList");
			System.out.println(al.size());
			for(int i=0;i<al.size();i++)
			{
				Product p=al.get(i);
				Long id=p.getId();
				Long shopID=p.getShopId();
				String source=p.getSource();
				System.out.println(source);
				DBLink dbl=new DBLink(c);
				Long key=null;
				if(source.equals("TB")||source.equals("Tmall"))
				key=dbl.insertProduct(id,null, source, p.getTitle(), p.getPrice(), Long.parseLong(p.getSells()), -1, p.getPage(), shopID,p.getFilename());
				else if(source.equals("JD"))
				{	
					System.out.println("Saving JD Products");
					//还有问题
					key=dbl.insertProduct(id, null, source, p.getTitle(), p.getPrice(), -1, Long.parseLong(p.getSells()), p.getPage(), shopID, p.getFilename());
				}
				if(key!=null)
				{
					System.out.println("Getting comments for product: "+i);
					dbl.addLinkToProductsAndKeywords(key, keywordsIDs);
					if(source.equals("TB")) TBCG.getComments(id, shopID,key);
					else if(source.equals("Tmall")) TMCG.getComments(id, shopID,key);
					else if(source.equals("JD")) JDCG.getComments(shopID,key);
					else if(source.equals("Amazon"));
				}
				System.out.println(p.getTitle());
				dbl.close();
				
				try {
					Thread.sleep(10*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		System.out.println("Data saved. Continue...");
	}

	public String[] getKeywords() {
		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

}
