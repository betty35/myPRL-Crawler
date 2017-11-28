package win.betty35.myPRL.C;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class CounterSpider extends Spider{

	protected int finished=0;
	
	public CounterSpider(PageProcessor pageProcessor) {
		super(pageProcessor);
		finished=0;
		// TODO Auto-generated constructor stub
	}
	
@Override
	protected void onSuccess(Request request) {
	System.out.println("On success!");
		 finished++;
	     System.out.println("finished:"+finished+" pages now...");
	     if(finished>=3) {System.out.println("Stopping....");this.stop();}
	     
		super.onSuccess(request);
      
    }

}
