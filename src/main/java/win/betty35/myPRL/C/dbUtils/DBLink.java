package win.betty35.myPRL.C.dbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;


public class DBLink 
{
	private IConnectionProvider connectionProvider = null;
	private DbHelper dbh;
	
	public DBLink(Configure con)
	{
		try{
			connectionProvider = (IConnectionProvider) new JdbcProvider("com.mysql.jdbc.Driver",
					"jdbc:mysql://" + con.getDb_ip(), con.getDb_user_name(),
					con.getDb_user_passwd());
			dbh = new DbHelper(connectionProvider,con.getDb_name());
		}catch (Exception e) {
			e.printStackTrace();
		} 
	
	}
	
	public Long insertProduct(Long proID,String vcharID,String source,String title,double price,long sales,long reviews,String page,long shopID,String imgdir) 
	{
		/**
		 * Insert if it is not in the table or update if it is out-dated
		 * Returns PID if anything is inserted or updated
		 */
		
		String sql=null;
		long status=this.checkStatus(proID, vcharID, source);
		
		System.out.println("status:"+status);
		if(status==-2)
		{
			sql="insert into Product (productID,VcharID,Source,Title,Price,Sales,Reviews,Page,ShopID,imgDir) values (?,?,?,?,?,?,?,?,?,?)";
			try {
				Long key=dbh.insertAndReturnLongKey(sql,proID,vcharID,source,title,price,sales,reviews,page,shopID,imgdir);
				return key;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		else if(status!=-1)//then status==PID
		{
			
			sql="insert into PriceUpdate (PID,Price) values (?,?)";
			try {
				Long key=dbh.insertAndReturnLongKey(sql,status,price);
			sql="update Product set lastupdated=? where (PID=?)";
			Timestamp t=new Timestamp(new Date().getTime());
			dbh.updatePrepareSQL(sql, t,status);
				return this.getPID(proID, vcharID, source);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
			
	}
	
	
	
	/*
	 * sql="update productmeta set description=?,attitude=?,delivery=?,sells=? where (id=?)";
				
					dbh.updatePrepareSQL(sql,description,attitude,delivery,sells,id);
	 * 
	 * */
	
	
	public void insertComment(Long PID, String text, boolean additional, Long OriginalID, String source )
	{
		String sql="insert into Comment (PID,text,additional,OriginalID,Source) values (?,?,?,?,?)";
		try {
			if(!this.commentExist(OriginalID, source, additional))
			dbh.insertAndReturnKey(sql, PID,text,additional,OriginalID,source);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public boolean insertKeywords(String[] words)
	{//true for inserted anything
		boolean insertKeyword=false;
		ArrayList<Long> position=new ArrayList<Long>();
		
		//Check if any new keyword should be inserted
		for(int i=0;i<words.length;i++)
		{
			System.out.println(words[i]);
			String sql="select * from Keyword where keyword=?";
			try {
				ResultSet re=dbh.query(sql, words[i]);
				if(!re.next())
				{
					sql="insert into Keyword (Keyword) values (?)";
					position.add(dbh.insertAndReturnLongKey(sql, words[i]));
					insertKeyword=true;
				}
				else
					position.add(re.getLong("KeyID"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		//Check if the keywords-set is already there. If not, insert it
		Collections.sort(position);
		
		String word_set="";
		for(int i=0;i<position.size();i++)
		{
			word_set=word_set.concat(position.get(i).toString());
			if(i!=(position.size()-1))
				word_set=word_set.concat(";");
		}
		
		if(insertKeyword)
		{
			String sql="insert into SearchedWords (WordsIDs) values (?)";
			try {
				dbh.insertAndReturnLongKey(sql, word_set);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		else{
		String sql="select * from SearchedWords where WordsIDs=?";
		try {
			ResultSet re=dbh.query(sql, word_set);
			if(!re.next())
			{
				sql="insert into SearchedWords (WordsIDs) values (?)";
				dbh.insertAndReturnLongKey(sql, word_set);
				return true;
			}
			else return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		}
	}
	
	
	public boolean needToCrawl(String[] search)
	{
		if(this.insertKeywords(search))//if any new word or words-set is inserted
			{System.out.println("New keywords inserted");return true;}//need to crawl for new information
		else
		{
			ArrayList<Long> PIDs=new ArrayList<Long>();
			String w=this.getSearchedWordsIDs(search);
			String sql="select * from Key_Product where keywordsID=?";
			try {
				ResultSet re=dbh.query(sql, w);
				while(re.next())
				{
					PIDs.add(re.getLong("PID"));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(PIDs.isEmpty()) return true;
			for(int i=0;i<PIDs.size();i++)
			{
				sql="select * from Product where PID=?";
				try {
					ResultSet re=dbh.findById(sql, PIDs.get(i));
					re.next();
					Date d=re.getTimestamp("LastUpdated");
					Date now=new Date();
					Calendar calendar = new GregorianCalendar();
				    calendar.setTime(d);
				    calendar.add(Calendar.DAY_OF_MONTH, 14);
				    d=calendar.getTime(); 
					if(d.before(now)) return true; 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;	
		}
	}
	
	public String getSearchedWordsIDs(String[] search)
	{
		ArrayList<Long> li=new ArrayList<Long>();
		String sql="select * from Keyword where keyword=?";
		for(int i=0;i<search.length;i++)
		{
			try {
				ResultSet re=dbh.query(sql, search[i]);
				if(re.next()) li.add(re.getLong("KeyID")); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Collections.sort(li);
		
		String r="";
		for(int i=0;i<li.size();i++)
		{
			r=r.concat(li.get(i).toString());
			if(i!=(li.size()-1)) r=r.concat(";");
		}
		
		return r;
	}
	
	public ArrayList<Long> getPIDsByKeywords(String[] search)
	{
		ArrayList<Long> li=new ArrayList<Long>();
		String sql="select * from Key_Product where keyword=?";
		for(int i=0;i<search.length;i++)
		{
			ArrayList<Long> l=new ArrayList<Long>();
			try {
				ResultSet re=dbh.query(sql, search[i]);
				if(!re.next())
				do
				{
					l.add(re.getLong("PID"));
				}while(re.next());
				
				if(i==0)li=l;
				else 
				{
					li.retainAll(l);
					if(li.isEmpty()) return null;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return li;		
	}
	
	
	public void addLinkToProductsAndKeywords(Long PID,String keywordsIDs)
	{
		String sql="select * from Key_Product where keywordsID=? and PID=?";
		try {
			ResultSet re=dbh.query(sql, keywordsIDs,PID);
			if(!re.next()) 
			{
				sql="insert into Key_Product (KeywordsID,PID) values (?,?)";
				dbh.insertAndReturnLongKey(sql, keywordsIDs,PID);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean commentExist(Long originalID,String source,Boolean additional)
	{
		String sql="select * from Comment where (source=? and additional=? and originalID=?)";
		ResultSet re;
		try {
			re = dbh.query(sql, source,additional,originalID);
			return re.next();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public Long getPID(Long proID,String VcharID,String source)
	{
		String sql=null;
		ResultSet re=null;
		try 
		{
			if(source.equals("Amazon")) 
			{
				sql="select * from Product where VcharID=? and source=?";
					re=dbh.query(sql,VcharID,source);		
			}
			else 
			{
				sql="select * from Product where productid=? and source=?";
				re=dbh.query(sql,proID,source);
			}
		
			boolean has=re.next();
			if(!has) return null;
			else return re.getLong("PID");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public long checkStatus(Long proID,String VcharID,String source) 
	{
		/**
		 * returns:
		 * -2: The product is not in database yet.
		 * -1: The product is in database and newly-updated.
		 * PID: The product is in database but need an price update.
		 */
		String sql=null;
		ResultSet re=null;
		try 
		{
			if(source.equals("Amazon")) 
			{
				sql="select * from Product where VcharID=? and source=?";
				
					re=dbh.query(sql,VcharID,source);
				
			}
			else 
			{
				sql="select * from Product where productid=? and source=?";
				re=dbh.query(sql,proID,source);
			}
		
			boolean has=re.next();
			if(!has) return -2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		Date d=new Date();
		Long pid=0l;
		try {
			d = re.getTimestamp("LastUpdated");
			pid=re.getLong("PID");
		} catch (SQLException e) {
			e.printStackTrace();
			return pid;
		}
		
		Date now=new Date();
		Calendar calendar = new GregorianCalendar();
	    calendar.setTime(d);
	    calendar.add(Calendar.DAY_OF_MONTH, 14);
	    d=calendar.getTime(); 
		if(d.after(now)) return -1;
		else return pid;
	}
	
	/*public ArrayList<String> getRecord(String filename)
	{
		ArrayList<String> list=new ArrayList<String>();
		String sql="select * from productmeta where (filename=?)";
		try {
			ResultSet re=dbh.query(sql, filename);
			while(re.next())
			{
				list.add(re.getString(""));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}*/
	
	public void updateTest(String ip, String place, Boolean ping)
	{
		Timestamp t=new Timestamp(System.currentTimeMillis());
		String sql="update pool set ping=?, time=? where (ip=? and place=?)";
		try {
			dbh.updatePrepareSQL(sql, ping,t,ip,place);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public void delFail(String ip, String place)
	{
		String sql="delete from pool where (ip=? and place=?)";
		try {
			dbh.updatePrepareSQL(sql, ip,place);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	public ArrayList<String> getAllFileNames()
	{
		String sql="select filename from productmeta";
		try {
			
			ArrayList<String> al=new ArrayList<String>();
			ResultSet re=dbh.query(sql);
			while(re.next())
			{
				al.add(re.getString("filename"));
			}
			return al;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/*public void del0pic()
	{
		String sql="delete from productmeta where pic=flase";
		try {
			dbh.updatePrepareSQL(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void updatePic(String filename)
	{
		String sql="update productmeta set pic=true where (filename=?)";
		try {
			dbh.updatePrepareSQL(sql, filename);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	
	public void close()
	{
		dbh.close();
	}

}
