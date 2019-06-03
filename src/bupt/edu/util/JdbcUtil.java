package bupt.edu.util;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.dbutils.QueryRunner;

import com.alibaba.fastjson.JSONArray;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JdbcUtil {
	private static ComboPooledDataSource dataSource =null;
	  // 一次加载连接池
    static {
    	dataSource = new ComboPooledDataSource();
    }
	
	public static QueryRunner getQueryRunner(){
		
		return new QueryRunner(dataSource);
		
	}
	public static Connection getConnection(){
			Connection conn =null;
			try {
				conn=dataSource.getConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return conn;
			
		}
	public static  void close (Connection conn,PreparedStatement pst,ResultSet rs){
		if(rs!=null){
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(pst!=null){
			try {
				pst.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(conn!=null){
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @param dates 要查询的时间段，包括最大和最小时间及标志查询小时级别还是天级别的字段：hour/day
	 * 时间格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getTimeSql(JSONArray dates){
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date max=null,min=null,date=null,date2=null,date3=null;
		Calendar cal=Calendar.getInstance();  
		String str="";
			try {
				if(dates.size()==2){
					date=df.parse(dates.getString(0));
					date2=date;
					if("hour".equalsIgnoreCase(dates.getString(1))){
						cal.setTime(date2);
						cal.add(Calendar.HOUR_OF_DAY, 1);
						date3=cal.getTime();
					}else if("day".equalsIgnoreCase(dates.getString(1))){
						cal.setTime(date2);
						cal.add(Calendar.DAY_OF_YEAR, 1);
						date3=cal.getTime();
					}
					str=" recorddate between '"+df.format(date)+"' and '"+df.format(date3)+"' ";
				}else if(dates.size()==3){
					if(df.parse(dates.getString(0)).getTime()>df.parse(dates.getString(1)).getTime()){
						max=df.parse(dates.getString(0));
						min=df.parse(dates.getString(1));
					}else {
						max=df.parse(dates.getString(1));
						min=df.parse(dates.getString(0));
					}
					str ="  recorddate between '"+df.format(min)+"' and '"+df.format(max)+"' ";
				}else{
					throw new RuntimeException("时间格式不对！");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return str;
	}
	
	public static String getTimeSql2(JSONArray dates){
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String str="";
		String max=null,min=null;
		try {
			if(df.parse(dates.getString(0)).getTime()>=df.parse(dates.getString(1)).getTime()){
				max=dates.getString(0);
				min = dates.getString(1);
			}else if(df.parse(dates.getString(0)).getTime()<df.parse(dates.getString(1)).getTime()){
				max=dates.getString(1);
				min = dates.getString(0);
			}
			str ="recorddate between '"+min+"' and '"+max+"' ";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}
}
	

