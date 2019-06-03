package bupt.edu.dao.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import bupt.edu.dao.IAppDao;
import bupt.edu.entity.AdminBean;
import bupt.edu.entity.AllCellBean;
import bupt.edu.entity.CellDataBean;
import bupt.edu.entity.OmcBean;
import bupt.edu.entity.UserBean;
import bupt.edu.util.JdbcUtil;
import bupt.edu.util.NameToChineseUtil;
import bupt.edu.util.SqlUtil;
@Component
public class AppDaoImp implements IAppDao {

	Connection conn=null;
	PreparedStatement pst=null;
	ResultSet rs =null;
	Map<String,Double> mapthresold = new LinkedHashMap<String, Double>();
	Map<String,String> mapnames=NameToChineseUtil.namesToChinese();
	/**
	 * 查询终端类型、异常业务类型【全网或单小区】
	 * 只能输入：deviceBrand或appName！
	 * @param dates:最大、最小时间
	 * @param type:deviceBrand、appName
	 * @param cellid:查询全网时为null
	 */
	@SuppressWarnings("deprecation")
	@Override
	public JSONObject findByAppData(String type, String cellid,JSONArray dates) {
		conn =JdbcUtil.getConnection();
		long sum1 = 0;
		String sql = "", sql2 = "",str="";
		JSONObject obj = new JSONObject();
		try {
			//str=JdbcUtil.getTimeSql(dates);
			str=JdbcUtil.getTimeSql2(dates);
			if (cellid != null) {
				// 查询指定小区下有多少终端数
				sql = "select count(appid) from t_app where cellid=? and " +str;
				sum1 = JdbcUtil.getQueryRunner().query(sql, cellid, new ScalarHandler<Long>());
				// 查询不同终端类型、异常业务类型下指定小区的分布情况
				sql2 = "select  "+type+",count(appid) from t_app where cellid=? and "+str+" group by  "+type;
				pst = conn.prepareStatement(sql2);
				pst.setString(1, cellid);
			} else {
				// 查询全网终端类型、异常情况分步
				sql = "select count(appid) from t_app where "+str;
				sum1 = JdbcUtil.getQueryRunner().query(sql, new ScalarHandler<Long>());
				sql2 = "select "+type+",count(appid) from t_app where "+str+" group by "+type+";";
				pst = conn.prepareStatement(sql2);
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				String tervalue = rs.getString(1);
				double nums = rs.getDouble(2);
				double ratio = nums / sum1;
				obj.put(tervalue, ratio);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("终端统计出错！");
		} finally {
			JdbcUtil.close(conn, pst, rs);
		}
		return obj;
	}
	/**
	 * 查询单用户omc指标及网络rsrp、rsrq、sinr情况
	 * @param imsi:用户标识号
	 * @param mocid 关联上的t_omc的主键
	 * @param dates:最大、最小时间
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<OmcBean> findByOmcId(String imsi, Integer omcid,JSONArray dates) {
		//String str=JdbcUtil.getTimeSql(dates);
		String str=JdbcUtil.getTimeSql2(dates);
		String sql = "select s.scoreid,a.sendrate,a.receiverate, a.rsrp,a.rsrq,a.sinr,s.access,s.maintain,s.mobility ,"
				+ "s.integri,s.capacity,s.comprehensive  from t_app as a left join t_score as s on a.omcid=s.scoreid "
				+ "where a.imsi=? and a.omcid=? and "+str;
		Object[] paras = new Object[]{imsi, omcid };
		List<OmcBean> list = null;
		try {
			list = JdbcUtil.getQueryRunner().query(sql, new BeanListHandler<OmcBean>(OmcBean.class), paras);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("网络指标查询出错！");
		}
		return list;
	}
	/**
	 * 查询单用户在指定小区的终端上报情况
	 * @param imsi:用户标识
	 * @param cellid 
	 * @param dates:最大、最小时间
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserBean> findByImsi(String imsi,String cellid,JSONArray dates) {
		//String str=JdbcUtil.getTimeSql(dates);
		String str=JdbcUtil.getTimeSql2(dates);
		String sql = "select appid,a.recorddate,androidVersion,appName,cpuRate,memoryrate,networkType,longitude,latitude,"
				+ "omcid from t_app as a where imsi=? and cellid=? and "+str;
		List<UserBean> list = null;
		try {
			Object[] values=new Object[]{imsi,cellid};
			list = JdbcUtil.getQueryRunner().query(sql, new BeanListHandler<UserBean>(UserBean.class), values);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("单用户查询出错！");
		}
		return list;
	}
	/**
	 * 查询单小区上报数据
	 * @param cellid
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CellDataBean> findByCellid(String cellid,JSONArray dates) {
		//String str=JdbcUtil.getTimeSql(dates);
		String str=JdbcUtil.getTimeSql2(dates);
		String sql = "select imsi,deviceBrand,deviceModel,count(1) as recordnums from t_app where cellid=? and "
				+ str+" group by imsi,deviceBrand,deviceModel ";
		List<CellDataBean> list = null;
		try {
			//list = JdbcUtil.getQueryRunner().query(sql, cellid,new BeanListHandler<CellDataBean>(CellDataBean.class));
			list = JdbcUtil.getQueryRunner().query(sql,new BeanListHandler<CellDataBean>(CellDataBean.class),cellid);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("小区列表信息查询出错！");
		}
		return list;
	}
	/**
	 * 查询全部上报小区列表
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllCellBean> findByAllCellid(JSONArray dates) {
		// 查询每个小区的上报数量
		//String str=JdbcUtil.getTimeSql(dates);
		String str=JdbcUtil.getTimeSql2(dates);
		String sql = "select enodbId,cellId_n,count(1) as resport_nums from t_app where "+str+" group by enodbId,cellId_n";
		List<AllCellBean> list = null;
		try {
			list = JdbcUtil.getQueryRunner().query(sql, new BeanListHandler<AllCellBean>(AllCellBean.class));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("小区列表查询出错！");
		}
		return list;
	}

	/**
	 * 统计RSRP、RSRQ、SINR分布【全网或单小区】
	 * @param type:RSRP、RSRQ、SINR
	 * @param cellid : 查询全网时为null
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@Override
	public JSONArray findByRSRP_RSRQ_SINR(String type,String cellid,JSONArray dates) {
		conn = JdbcUtil.getConnection();
		String sql="",key="",sql2="";
		int start=0;
		JSONArray arr = new JSONArray();
		String str="  and cellid =? ";
		long sum=0;
		//String str2=JdbcUtil.getTimeSql(dates);
		String str2=JdbcUtil.getTimeSql2(dates);
		try {
			if(cellid==null){
				//统计全网总的异常事件数
				sql2 ="select count(appid) from t_app where "+str2;
				sum=JdbcUtil.getQueryRunner().query(sql2, new ScalarHandler<Long>());
				str="";
			}else{
				sql2 = "select count(1) from t_app where  "+str2+str;
				sum=JdbcUtil.getQueryRunner().query(sql2, new ScalarHandler<Long>(),cellid);
			}
			if("rsrp".equalsIgnoreCase(type)){
				int range=10;
				for(int i=0;i<6;i++){
					
					start=-105+10*(i-1);
					if(i==0){
						sql="select count(appid) from t_app where rsrp<-105"+str+" and "+str2;
						key="rsrp<-105dBm";
					}else if(i==5){
						sql="select count(appid) from t_app where rsrp>=-65"+str+" and "+str2;
						key="rsrp>=65dBm";
					}else{
						sql="select count(appid) from t_app where rsrp>= "+start+" and rsrp< "+(start+range)+str+" and "+str2;
						key=start+"dBm=<rsrp<"+(start+range)+"dBm";
					}
					JSONObject obj =SqlUtil.getJSONObject3(pst, conn, rs, sql, sum, cellid, key);
					arr.add(obj);
				}
		}else if("rsrq".equalsIgnoreCase(type)){
			for(int j=0;j<5;j++){
				if(j==0){
					sql="select count(appid) from t_app where "+type+" >=-19.5 and "+type+"<-15   "+str+" and "+str2;
					key="-19.5db<=rsrq<-15db";
				}else{
					int start2=-15+3*(j-1);
					int range2=3;
					if(j==4){
						sql="select count(appid) from t_app where "+type+" >= "+start2+" and "+type+"<="+(start2+range2)
								+str+" and "+str2;
						key=start2+"db=<rsrq<="+(start2+range2)+"db";
					}else{
						sql="select count(appid) from t_app where "+type+" >= "+start2+" and "+type+"<"+(start2+range2)
								+str+" and "+str2;
						key=start2+"db=<rsrq<"+(start2+range2)+"db";
					}
				}
				JSONObject obj=SqlUtil.getJSONObject3(pst, conn, rs, sql, sum, cellid, key);
				arr.add(obj);
			}
		}else if("sinr".equalsIgnoreCase(type)){
			for(int j=0;j<5;j++){
				if(j==0){
					sql="select count(appid) from t_app where "+type+">=25 "+str+" and "+str2;
					key="sinr>=25dB";
				}else if(j==1){
					sql="select count(appid) from t_app where "+type+"<25 and "+type+">=16 "+str+" and "+str2;
					key="16dB<=sinr<25dB";
				}else if(j==2){
					sql="select count(appid) from t_app where "+type+"<16 and "+type+">=11 "+str+" and "+str2;
					key="11dB<=sinr<16dB";
				}else if(j==3){
					sql="select count(appid) from t_app where "+type+"<11 and "+type+">=3 "+str+" and "+str2;
					key="3dB<=sinr<11dB";
				}else{
					sql="select count(appid) from t_app where "+type+"<3  "+str+" and "+str2;
					key="sinr<3dB";
				}
				JSONObject obj=SqlUtil.getJSONObject3(pst, conn, rs, sql, sum, cellid, key);
				arr.add(obj);
			}
		}else{
			throw new RuntimeException("请输入：rsrp、rsrq、sinr");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("rsrp、rsrq、sinr统计出错");
			}finally{
				JdbcUtil.close(conn, pst, rs);
			}
		return arr;
	}

	/**
	 * 分段统计cpu占用率、内存占用率（全网或单小区）
	 * 
	 * @param type:cpuRate、memoryrate
	 * @param cellid：小区id，查询全网时为null
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@Override
	public JSONArray findByCPU_Memory(String type, String cellid,JSONArray dates) {
		conn = JdbcUtil.getConnection();
		String sql = "",sql2="";
		long sum=0;
		double start=0.0,range=0.1;
		String key="",str=" and cellid=? ";
		//String str2=JdbcUtil.getTimeSql(dates);
		String str2=JdbcUtil.getTimeSql2(dates);
		JSONArray arr = new JSONArray();
		DecimalFormat  df = new DecimalFormat("#0.0");   
		try {
			if (cellid == null) {
				str=" ";//全网统计
				sql2="select count(1) from t_app where "+str2;
				sum=JdbcUtil.getQueryRunner().query(sql2, new ScalarHandler<Long>());
			}else{
				//统计每个小区总的异常事件数
				sql2 = "select count(appid) from t_app where cellid=? and " +str2;
				sum=JdbcUtil.getQueryRunner().query(sql2,new ScalarHandler<Long>(), cellid);
			}
			for(int i=0;i<10;i++){
				start=(double)i/10;
				key=df.format(start)+"-"+df.format((start+range));
				if(i==9){
					sql="select count(appid) from t_app where "+type+" >= "+start+" and "+type
							+" <= "+(start+range)+str+" and "+str2;
					JSONObject obj=SqlUtil.getJSONObject3(pst, conn, rs, sql, sum, cellid, key);
					arr.add(obj);
				}else{
					sql="select count(appid) from t_app where "+type+" >= "+start+" and "+type
							+" < "+(start+range)+str+" and "+str2;
					JSONObject obj=SqlUtil.getJSONObject3(pst, conn, rs, sql, sum, cellid, key);
					arr.add(obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("cpu占用率/内存占用率统计出错!");
		} 
		return arr;
	}

	/**
	 * 查询全网的KPI量化值分布情况
	 * @param index:access、maintain、mobility、integri、capacity、 comprehensive
	 * @param dates:最大、最小时间[要查询的时间段]
	 * @param cellid:为null时查询全网的
	 */
	@Override
	public JSONArray findByKPI(String index,JSONArray dates,String cellid) {
		conn = JdbcUtil.getConnection();
		String sql ="", sql2 = "",key="",str3="";
		int start=0,range=10;
		//String str=JdbcUtil.getTimeSql(dates);
		String str=JdbcUtil.getTimeSql2(dates);
		str=str.replace("recorddate", "omctime");
		JSONArray arr = new JSONArray();
		if(cellid!=null){
			str3=" and cellid=?";
		}
		sql2 = " select count(1) from t_score where "+str+str3;
		long sum2=0;
		try {
			//查询全网omc记录数
			if(cellid==null){
				sum2 = JdbcUtil.getQueryRunner().query(sql2, new ScalarHandler<Long>());
			}else{
				sum2=JdbcUtil.getQueryRunner().query(sql2,new ScalarHandler<Long>(),cellid);
			}
			
			for(int i=0;i<10;i++){
				start=10*i;
				key=start+"-"+(start+range);
				if(i==9){
					sql="select count(scoreid) from t_score where "+index+" >= "+start+" and "
							+index+" <= "+(start+range)+" and "+str+str3;
				}else{
					sql="select count(scoreid) from t_score where "+index+" >= "+start+" and "
							+index+" < "+(start+range)+" and "+str+str3;
				}
				
				pst = conn.prepareStatement(sql);
				if(cellid!=null){
					pst.setString(1, cellid);
				}
				rs = pst.executeQuery();
				while (rs.next()) {
					JSONObject obj = new JSONObject();
					obj.put(key,(double)rs.getInt(1)/sum2);
					arr.add(obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("KPI量化值统计出错");
		} finally {
			JdbcUtil.close(conn, pst, rs);
		}
		return arr;
	}

	/**
	 * 查询每个小区一段时间的的KPI量化值【五个或综合指标】
	 * @param cellid 要查的小区
	 * @param index:access、maintain、mobility、integri、capacity、 comprehensive
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@Override
	public List<Double> findBySingleKPI(String cellid,String index,JSONArray dates) {
		conn=JdbcUtil.getConnection();
		//String str = JdbcUtil.getTimeSql(dates);
		String str=JdbcUtil.getTimeSql2(dates);
		str=str.replace("recorddate", "omctime");
		String sql = "select "+index+" from t_score where cellid = ?  and "+str;
		List<Double> list = new LinkedList<Double>();
		try {
			pst=conn.prepareStatement(sql);
			pst.setString(1, cellid);
			rs=pst.executeQuery();
			while(rs.next()){
				list.add(rs.getDouble(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("查询单小区KPI量化值出错");
		}finally{
			JdbcUtil.close(conn, pst, rs);
		}
		return list;
	}

	/**
	 * 	统计手机品牌与手机CPU使用率、手机内存占用率分别与RSRP关系。
	 * @param type:deviceBrand、cpuRate、memoryrate
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@Override
	public JSONObject findRSRP_Terminal(String type,JSONArray dates,String cellid) {
		conn = JdbcUtil.getConnection();
		String sql="",sql2="",str3="";
		//String str = JdbcUtil.getTimeSql(dates);
		String str=JdbcUtil.getTimeSql2(dates);
		List<String> listtype = new LinkedList<String>();
		JSONObject result=new JSONObject();
		if(cellid!=null){
			str3=" and cellid= ?";
		}
		try {
			if("deviceBrand".equals(type)){
			//找出所有终端类型
			sql="select "+type+" from t_app where  "+str+str3+"  group by "+type+";";
			pst=conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while(rs.next()){
				listtype.add(rs.getString(1));
			}
				//分组统计同一终端类型下不同段RSRP的分布
				int start=0,range=10;
				String key="";
				for(int i=0;i<listtype.size();i++){
					JSONArray row = new JSONArray();
					for(int j=0;j<6;j++){
						start=-105+10*(j-1);
						JSONObject json = new JSONObject();
						if(j==0){
							sql2="select count(appid) from t_app where "+type+" =? and rsrp<-105 and " +str+str3;
							key="rsrp<-105dBm";
						}else if(j==5){
							sql2="select count(appid) from t_app where "+type+" =? and rsrp>=-65 and "+str+str3;
							key="rsr>=-65dBm";
						}else{
							sql2="select count(appid) from t_app where "+type+" =? and rsrp>= "+start+" and rsrp< "+(start+range)+" and "+str+str3;
							key=start+"dBm=<rsrp<"+(start+range)+"dBm";
						}
						pst=conn.prepareStatement(sql2);
						pst.setString(1, listtype.get(i));
						rs = pst.executeQuery();
						while(rs.next()){
							json.put(key, rs.getInt(1));
						}
						row.add(json);
					}
					result.put(listtype.get(i), row);
				}
			}else if("cpuRate".equals(type)||"memoryrate".equals(type)){
				for(int i=0;i<10;i++){
					double startIndex1 = ((double)i*10)/100;
					double range1 = 0.1; 
					String key1 = (int)(startIndex1*100)+"%-"+(int)((startIndex1+range1)*100)+"%";
					String key2="";
					JSONArray jsonArray = new JSONArray();
					String label=" < ";
				for(int j=0;j<6;j++){
					JSONObject jsobj = new JSONObject();
					if(i==9){
						label=" <= ";
					}
					if(j==0){
						sql = "select count(appid) from t_app where "+type+" >= "+startIndex1+
								" and "+type+label+(startIndex1+range1)+" and rsrp<-105 and "+str+str3;
						key2 = "rsrp<-105dBm";
					}else if(j==5){
						sql = "select count(appid) from t_app where  "+type+" >= "+startIndex1+
								" and "+type+label+(startIndex1+range1)+" and rsrp>=-65 and "+str+str3;
						key2 = "rsrp>=-65dBm";
					}else{
						int startIndex2 = -105+(j-1)*10;
						int range2 = 10; 
						sql = "select count(appid) from t_app where  "+type+" >= "+startIndex1+
								" and "+type+label+(startIndex1+range1)+" and rsrp>= "+
								startIndex2+" and rsrp < "+(startIndex2+range2)+" and "+str+str3;
						key2=startIndex2+"dBm=<rsrp<"+(startIndex2+range2)+"dBm";
					}
					
					jsobj=SqlUtil.getJSONObject(pst, conn, rs, jsobj, sql,cellid, key2);
					jsonArray.add(jsobj);
					}
				result.put(key1, jsonArray);				
				}
			} else{
				throw new RuntimeException("输入了类型有误！只能输入：deviceBrand、cpuRate、memoryrate!");
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("RSRP与手机品牌与、手机CPU使用率、手机内存占用率统计出错！");
		}finally{
			JdbcUtil.close(conn, pst, rs);
		}
		return result;
	}
		
	/**
	 * 统计手机品牌、手机CPU使用率、手机内存占用率分别与五类KPI指标量化值及KPI综合量化值关系。
	 * @param type:deviceBrand、cpuRate、memoryrate
	 * @param index:access、maintain、mobility、integri、capacity、comprehensive
	 * @param dates:最大、最小时间[要查询的时间段]
	 * @param cellid:为null时查询全网的
	 */

	@Override
	public JSONObject findTerminal_Index(String type,String index,JSONArray dates,String cellid) {
		conn = JdbcUtil.getConnection();
		JSONObject result = new JSONObject();
		String key1="",key2="",str3="";
		String sql ="";
		//String str = JdbcUtil.getTimeSql(dates);
		String str=JdbcUtil.getTimeSql2(dates);
		if(cellid!=null){
			str3=" and a.cellid='"+cellid+"' ";
		}
		try {
			if("deviceBrand".equalsIgnoreCase(type)){
				sql="select a.deviceBrand from t_app as a left join t_score as s on a.omcid=s.scoreid"
						+ " where  "+str+str3+" group by a.deviceBrand";
				pst=conn.prepareStatement(sql);
				rs = pst.executeQuery();
				LinkedList<String> list = new LinkedList<String>();
				while(rs.next()){
					//获得所有终端类型
					list.add(rs.getString(1));
				}
				for(int j=0;j<list.size();j++){
					key1=list.get(j);
					JSONArray array = new JSONArray();
					String label=" < ";
					for(int i=0;i<10;i++){
						JSONObject jsobj = new JSONObject();
						int startindex=i*10;
						int range =10;
						key2=startindex+"-"+(startindex+range);
						if(i==9){
							label=" <= ";
						}
						sql="select count(a.appid) from t_app as a left join t_score as s on a.omcid=s.scoreid"
								+ " where "+index+" >="+startindex+" and "+index+label+(startindex+range)+" and deviceBrand ='"+list.get(j)+"' and "+str+str3;
						jsobj=SqlUtil.getJSONObject2(pst, conn, rs, jsobj, sql, key2);
						array.add(jsobj);
						}
					result.put(key1, array);
					}
				
			}else if("cpuRate".equalsIgnoreCase(type)||"memoryrate".equalsIgnoreCase(type)){
				String label="<";
				for(int i=0;i<10;i++){
					int startindex=i*10;
					int range =10;
					key1=startindex+"-"+(startindex+range);
					JSONArray array = new JSONArray();
					for(int j=0;j<10;j++){
						String label2="<";
						double startindex2=(double)j*10/100;
						double range2 =0.1;
						JSONObject jsobj = new JSONObject();
						key2=(int)(startindex2*100)+"%-"+(int)((startindex2+range2)*100)+"%";
						if(i==9){
							label="<=";
						}
						if(j==9){
							label2="<=";
						}
						sql="select count(a.appid) from t_app as a left join t_score as s on a.omcid=s.scoreid"
								+ " where "+index+" >= "+startindex+" and "+index+label+(startindex+range)+" and "+type+
								" >= "+startindex2+" and "+type+label2+(startindex2+range2) +" and "+str+str3;
						
						jsobj=SqlUtil.getJSONObject2(pst, conn, rs, jsobj, sql, key2);
						array.add(jsobj);
					}
					result.put(key1, array);
				}
			}else{
				throw new RuntimeException("只能输入：deviceBrand、cpuRate、memoryrate！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("终端与KPI统计出错！");
		}finally{
			JdbcUtil.close(conn, pst, rs);
		}
		return result;
	}
	/**
	 * RSRP\RSRQ\SINR分别与五类KPI指标量化值及KPI综合量化值关系。
	 * @param type:RSRP\RSRQ\SINR
	 * @param index:access、maintain、mobility、integri、capacity、comprehensive
	 * @param dates:最大、最小时间[要查询的时间段]
	 * @param cellid:为null时查询全网
	 */
	@Override
	public JSONObject findRSRP_Index(String type,String index,JSONArray dates,String cellid) {
		conn = JdbcUtil.getConnection();
		String sql ="";
		int startindex=0,range=0;
		JSONObject result = new JSONObject();
		String key1="",key2="";
		//String str=JdbcUtil.getTimeSql(dates);
		String str=JdbcUtil.getTimeSql2(dates);
		String str2="";
		if(cellid!=null){
			str2=" and a.cellid=? ";
		}
		try {
			if("rsrp".equalsIgnoreCase(type)){
				for(int i=0;i<10;i++){
					startindex =i*10;
					range=10;
					JSONArray arr = new JSONArray();
					key1=""+startindex+"-"+(startindex+range);
					String label=" < ";
					for(int j=0;j<6;j++){
						JSONObject jsobj = new JSONObject();
						if(i==9){
							label=" <= ";
						}
						if(j==0){
							sql="select count(a.appid) from t_app as a left join t_score as s on a.omcid=s.scoreid"
									+ " where s."+index+" > "+startindex+" and s."+index+label+(startindex+range)+" and rsrp<-105 and "+str+str2;
							key2="rsrp<-105dBm";
						}else if(j==5){
							sql="select count(a.appid) from t_app as a left join t_score as s on a.omcid=s.scoreid"
									+ " where s."+index+" > "+startindex+" and s."+index+label+(startindex+range)+" and rsrp>=-65 and "+str+str2;
							key2="rsrp>=-65dBm";
						}else{
							int startindex2=-105+(j-1)*10;
							int range2=10;
							sql="select count(a.appid) from t_app as a left join t_score as s on a.omcid=s.scoreid"
							+ " where s."+index+" >= "+startindex+" and s."+index+label+(startindex+range)+" and rsrp> "+startindex2+" and rsrp< "+(startindex2+range2)+" and "+str+str2;
							key2=startindex2+"dBm<=rsrp<"+(startindex2+range2)+"dBm";
						}
						jsobj=SqlUtil.getJSONObject(pst, conn, rs, jsobj, sql, cellid, key2);
						arr.add(jsobj);
					}
					result.put(key1, arr);
				}
			}else if("rsrq".equalsIgnoreCase(type)){
				String label=" < ";
				for(int i=0;i<10;i++){
					startindex=i*10;
					range=10;
					JSONArray arr = new JSONArray();
					key1=""+startindex+"-"+(startindex+range);
					for(int j=0;j<5;j++){
						JSONObject jsobj = new JSONObject();
						if(i==9){
							label=" <= ";
						}
						if(j==0){
							sql="select count(appid) from t_app as a left join t_score as s on a.omcid=s.scoreid where "+type+">=-19.5 and "+type+"<-15 and "+index+" >= "+startindex+" and "+index
									+label +(startindex+range)+" and "+str+str2;
							key2="-19.5db<=rsrq<-15db";
						}else{
							int start2=-15+3*(j-1);
							int range2=3;
							sql="select count(appid) from t_app as a left join t_score as s on a.omcid=s.scoreid where "+type+">"+start2+" and "+type+"<"+(start2+range2)
									+" and "+index+" >="+startindex+" and "+index+label+(startindex+range)+" and "+str+str2;
							key2=start2+"db<=rsrq<"+(start2+range2)+"db";
						}
						jsobj=SqlUtil.getJSONObject(pst, conn, rs, jsobj, sql, cellid, key2);
						arr.add(jsobj);
					}
					result.put(key1, arr);
				}
			}else if("sinr".equalsIgnoreCase(type)){
				String label=" < ";
				for(int i=0;i<10;i++){
					startindex=i*10;
					JSONArray arr = new JSONArray();
					key1=""+startindex+"-"+(startindex+range);
						for(int j=0;j<5;j++){
							if(i==9){
								label=" <= ";
							}
							JSONObject jsobj = new JSONObject();
							if(j==0){
								sql="select count(appid) from t_app as a left join t_score as s on a.omcid=s.scoreid where "+type+">=25 and "+index+" >= "+startindex+" and "+index+label +(startindex+range)+" and "+str+str2;
								key2="sinr>=25dB";
							}else if(j==1){
								sql="select count(appid) from t_app as a left join t_score as s on a.omcid=s.scoreid where "+type+"<25 and "+type+">=16 and "+index+" >= "+startindex+" and "+index+label +(startindex+range)+" and "+str+str2;
								key2="16dB<=sinr<25dB";
							}else if(j==2){
								sql="select count(appid) from t_app as a left join t_score as s on a.omcid=s.scoreid where "+type+"<16 and "+type+">=11 and "+index+" >= "+startindex+" and "+index+label +(startindex+range)+" and "+str+str2;
								key2="11dB<=sinr<16dB";
							}else if(j==3){
								sql="select count(appid) from t_app as a left join t_score as s on a.omcid=s.scoreid where "+type+"<11 and "+type+">=3 and"+index+" >= "+startindex+" and "+index+label +(startindex+range)+" and "+str+str2;
								key2="3dB<=sinr<11dB";
							}else{
								sql="select count(appid) from t_app as a left join t_score as s on a.omcid=s.scoreid where "+type+"<3 and "+index+" >= "+startindex+" and "+index+label +(startindex+range)+" and "+str+str2;
								key2="sinr<3dB";
							}
							jsobj=SqlUtil.getJSONObject(pst, conn, rs, jsobj, sql, cellid, key2);
							arr.add(jsobj);
						}
						result.put(key1, arr);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("RSRP与KPI统计出错！");
		}finally{
			JdbcUtil.close(conn, pst, rs);
		}
		return result;
	}
	/**
	 * 获取按天/按小时的列表
	 * 
	 */
	@Override
	public List<String> findTimeList() {
		conn = JdbcUtil.getConnection();
		String sql ="select date_format(recorddate,'%Y-%m-%e') as a from t_app group by a;";
		List<String> date =new LinkedList<String>();
		try {
			pst=conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while(rs.next()){
				String value=rs.getString(1);
				if(!"0000-00-0".equals(value)){
					date.add(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("获取时间列表出错！");
		}finally{
			JdbcUtil.close(conn, pst, rs);
		}
		return date;
	}
	
	/**
	 * 下载速率与RSRP、RSRQ、SINR关联分析[单小区或全网]
	 * @param type:RSRP、RSRQ、SINR
	 * @param cellid:查询全网时为null
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@Override
	public JSONObject findRate_Network(String type,String cellid,JSONArray dates) {
		conn = JdbcUtil.getConnection();
		String sql="";
		int range=50;
		int start=0;
		String str="";
		String str2=" and cellid=? ";
		String key1="",key2="";
	//	String str3=JdbcUtil.getTimeSql(dates);
		String str3=JdbcUtil.getTimeSql2(dates);
		JSONObject obj = new JSONObject();
		if(cellid==null){
			str2=" ";
		}
		if("rsrp".equalsIgnoreCase(type)){
			for(int i=0;i<6;i++){
				start=i*50;
				str=" and receiverate < "+(start+range);
				JSONArray arr = new JSONArray();
				if(i!=5){
					key1=start+"kbps-"+(start+range)+"kbps";
				}else{
					key1=">="+start+"kbps";
				}
				for(int j=0;j<6;j++){
					JSONObject jsobj = new JSONObject();
					if(i==5){
						str=" ";
					}
					if(j==0){
						sql="select count(appid) from t_app where "+type+">=-65 and receiverate >= "+start+str+str2+" and "+str3;
						key2="rsrp>-65dBm";
					}else if(j==5){
						sql="select count(appid) from t_app where "+type+"<-105 and receiverate >= "+start+str+str2+" and "+str3;
						key2="rsrp<-105dBm";
					}else{
						int start2=-105+10*(j-1);
						int range2=10;
						sql="select count(appid) from t_app where "+type+">="+start2+" and "+type+" < "+(start2+range2)
								+" and receiverate >= "+start+str+str2+" and "+str3;
						key2=start2+"dBm<=rsrp<"+(start2+range2)+"dBm";
					}
					jsobj=SqlUtil.getJSONObject(pst, conn, rs, jsobj, sql, cellid, key2);
					arr.add(jsobj);
			}
				obj.put(key1, arr);
		}
		}else if("rsrq".equalsIgnoreCase(type)){
			for(int i=0;i<6;i++){
				start=i*50;
				str=" and receiverate < "+(start+range);
				JSONArray arr = new JSONArray();
				if(i!=5){
					key1=start+"kbps-"+(start+range)+"kbps";
				}else{
					key1=">"+start+"kbps";
				}
				for(int j=0;j<5;j++){
					JSONObject jsobj = new JSONObject();
					if(i==5){
						str=" ";
					}
					if(j==0){
						sql="select count(appid) from t_app where "+type+">=-19.5 and "+type+"<-15 and receiverate >="+start+str+str2+" and "+str3;
						key2="-19.5db<=rsrq<-15db";
					}else{
						int start2=-15+3*(j-1);
						int range2=3;
						sql="select count(appid) from t_app where "+type+">"+start2+" and "+type+"<"+(start2+range2)
								+" and receiverate >="+start+str+str2+" and "+str3;
						key2=start2+"db<=rsrq<"+(start2+range2)+"db";
					}
					jsobj=SqlUtil.getJSONObject(pst, conn, rs, jsobj, sql, cellid, key2);
					arr.add(jsobj);
				}
				obj.put(key1, arr);
			}
		}else if("sinr".equalsIgnoreCase(type)){
			for(int i=0;i<6;i++){
				start=i*50;
				str=" and receiverate < "+(start+range);
				if(i!=5){
					key1=start+"kbps-"+(start+range)+"kbps";
				}else{
					key1=">"+start+"kbps";
				}
				JSONArray arr = new JSONArray();
					for(int j=0;j<5;j++){
						JSONObject jsobj = new JSONObject();
						if(i==5){ 
							str=" ";
						}
						if(j==0){
							sql="select count(appid) from t_app where "+type+">=25 and receiverate >="+start+str+str2+" and "+str3;
							key2="sinr>=25dB";
						}else if(j==1){
							sql="select count(appid) from t_app where "+type+"<25 and "+type+">=16 and receiverate >="+start+str+str2+" and "+str3;
							key2="16dB<=sinr<25dB";
						}else if(j==2){
							sql="select count(appid) from t_app where "+type+"<16 and "+type+">=11 and receiverate >="+start+str+str2+" and "+str3;
							key2="11dB<=sinr<16dB";
						}else if(j==3){
							sql="select count(appid) from t_app where "+type+"<11 and "+type+">=3 and receiverate >="+start+str+str2+" and "+str3;
							key2="3dB<=sinr<11dB";
						}else{
							sql="select count(appid) from t_app where "+type+"<3 and receiverate >="+start+str+str2+" and "+str3;
							key2="sinr<3dB";
						}
						jsobj=SqlUtil.getJSONObject(pst, conn, rs, jsobj, sql, cellid, key2);
						arr.add(jsobj);
					}
					obj.put(key1, arr);
			}
		}else{
			throw new RuntimeException("只能输入RSRP、RSRQ、SINR！");
		}
		JdbcUtil.close(conn, pst, rs);
		return obj;
	}
	/**
	 * 下载速率与五类KPI指标、综合KPI指标关系
	 * @param index:access、maintain、mobility、integri、capacity、comprehensive
	 * @param dates:最大、最小时间[要查询的时间段]
	 * @param cellid:为null时查询全网
	 */
	@Override
	public JSONObject findRate_Kpi(String index,JSONArray dates,String cellid) {
		conn = JdbcUtil.getConnection();
		String sql="",key1="",key2="",str3="";
		JSONObject obj = new JSONObject();
		int start=0,start2=0;
		int range =50,range2=10;
		//String str2=JdbcUtil.getTimeSql(dates);
		String str2=JdbcUtil.getTimeSql2(dates);
		if(cellid!=null){
			str3=" and cellid= '"+cellid+"' ";	
		}
		for(int i=0;i<6;i++){
			String label=" < ";
			start=50*i;
			String str=" and receiverate < "+(start+range);
			JSONArray arr = new JSONArray();
			if(i==5){
				key1=">"+start+"kbps";
			}else{
				key1=start+"kbps-"+(start+range)+"kbps";
			}
			for(int j=0;j<10;j++){
				JSONObject jsobj = new JSONObject();
				if(j==9){
					label=" <= ";
				}
				start2=j*10;
				if(i==5){
					str=" ";
				}
				sql="select count(appid) from t_app left join t_score on omcid=scoreid where "+index+">= "+start2+" and "+index+label
						+(start2+range2)+" and receiverate >= "+start+str+" and "+str2+str3;
				key2=start2+"-"+(start2+range2);
			jsobj = SqlUtil.getJSONObject(pst, conn, rs, jsobj, sql, cellid, key2);
			arr.add(jsobj);
			}
			obj.put(key1, arr);
		}
		return obj;
	}
	/**
	 * 查询单用户的OMC具体51项指标值
	 * @param omcid:要查询的omcid
	 * 
	 */
	@Override
	public JSONArray findOmcData(Integer omcid) {
		String sql="select * from t_omc where id =?";
		conn=JdbcUtil.getConnection();
		JSONArray arr = new JSONArray();
		
		try {
			pst=conn.prepareStatement(sql);
			pst.setInt(1, 1);
			rs=pst.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			String[] names= new String[43];
			for(int i=0;i<43;i++){
				names[i]=metaData.getColumnName(i+5);
			}
			sql="select ";
			for(int i=0;i<names.length-1;i++){
				sql+=names[i]+",";
			}
			sql+=sql=names[names.length-1]+" from t_omc where id=?;";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, omcid);
			rs = pst.executeQuery();
			while(rs.next()){
				for(int i=0;i<names.length;i++){
					JSONObject obj = new JSONObject();
					if(mapnames.containsKey(names[i].trim())){
						obj.put(mapnames.get(names[i]), rs.getDouble(i+1));
					}else{
						System.out.println(names[i]);
						throw new RuntimeException("该名称没有对应的中文名称！");
					}
					arr.add(obj);
				}
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally{
			JdbcUtil.close(conn, pst, rs);
		}
		
		return arr;
	}
	/**
	 * 统计导致OMC量化值低的OMC指标分布
	 * @param index:access、maintain、mobility、integri、capacity
	 * @param dates:最小/最大时间
	 * @param cellid:为null时，统计全网的
	 * @param key:量化值的分值范围：10-20....
	 */
	@Override
	public JSONArray findOmcResult(String index, JSONArray dates, String cellid,String key) {
		conn = JdbcUtil.getConnection();
		String str=JdbcUtil.getTimeSql2(dates);
		str=str.replace("recorddate", "omctime");
		String sql="",str3="",sql2="";
		if(cellid!=null){
			str3=" and cellid=?";
		}
		sql="select * from t_omc_score where id=1;";
		PreparedStatement pst=null;
		PreparedStatement pst2=null;
		ResultSet rs=null,rs2=null;
		JSONArray arr = new JSONArray();
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			//获取元数据
			ResultSetMetaData metaData = rs.getMetaData();
			String[] access2= new String[3];
			String[] maintain2=new String[2];
			String[] mobility2= new String[2];
			String[] integri2=new String[2];
			String[] capacity2= new String[9];
			//将各类指标的名字放入对应数组中
			int j=0,k2=0;
			for(int i=0;i<43;i++){
				if(i<3){
					access2[i]=metaData.getColumnName(i+5);
				}else if(i<9){
					if(i==3||i==4){
						maintain2[i-3]=metaData.getColumnName(i+5);
					}
				}else if(i<11){
					mobility2[i-9]=metaData.getColumnName(i+5);
				}else if(i<20){
					if(i==16||i==22){
						integri2[j++]=metaData.getColumnName(i+5);
					}
				}else if(i<43){
					if(i==20||i==21||i==22||i==23||i==24||i==25||i==26||i==27||i==28){
						capacity2[k2++]=metaData.getColumnName(i+5);
					}
					
				}else{
					throw new RuntimeException("OMC指标个数超过43！");
				}
			}
			String str2=" and "+index+" between "+key.replace("-", " and ");
			sql="select count(scoreid) from t_score where "+str+str3+" "+str2;
			//查询该条件下的总数
			long sum=0l;
			//统计低于平均值的omc指标数
			Integer[] r = {0,0,0,0,0,0,0,0,0};
			String[] y=null;
			if("access".equalsIgnoreCase(index)){
				y=access2;
			}else if("maintain".equalsIgnoreCase(index)){
				y=maintain2;
			}else if("mobility".equalsIgnoreCase(index)){
				y=mobility2;
			}else if("integri".equalsIgnoreCase(index)){
				y=integri2;
			}else if("capacity".equalsIgnoreCase(index)){
				y=capacity2;
			}else{
				throw new RuntimeException("指标名称输入错误！");
			}
			sql2="select ";
			for(int i=0;i<y.length-1;i++){
				sql2+=y[i]+",";
			}
			sql2+=y[y.length-1]+" from t_omc_score as t1 left join t_score as t2 on t1.id=t2.scoreid where t2."+str+str3+" "+str2;
			pst2=conn.prepareStatement(sql2);
			if(cellid!=null){
				sum=JdbcUtil.getQueryRunner().query(sql,new ScalarHandler<Long>(), cellid);
				pst.setString(1, cellid);
			}else{
				sum=JdbcUtil.getQueryRunner().query(sql, new ScalarHandler<Long>());
			}
			rs2 = pst2.executeQuery();
			List<Double> list2= new LinkedList<Double>();
			//获取每个omc的kpi的阈值
			for(int i=0;i<y.length;i++){
				if(!mapthresold.containsKey(y[i])){
					list2.add(getThreshold(y[i], cellid,"t_omc_score"));
				}else{
					list2.add(mapthresold.get(y[i]));
				}
				
			}
			if(index.equalsIgnoreCase("maintain")||index.equalsIgnoreCase("mobility")||index.equalsIgnoreCase("integri")){
				while(rs2.next()){
					for(int i=0;i<y.length;i++){
						if(rs2.getDouble(i+1)<list2.get(i)){
							r[i]+=1;
						}
					}
				}
				
			}else if(index.equalsIgnoreCase("access")){
				while(rs2.next()){
					for(int i=0;i<y.length;i++){
						if(rs2.getDouble(i+1)<list2.get(i)){
							r[i]+=1;
						}
					}
				}
			}else if(index.equalsIgnoreCase("capacity")){
				while(rs2.next()){
					for(int i=0;i<y.length;i++){
						if(rs2.getDouble(i+1)<list2.get(i)){
							r[i]+=1;
						}
					}
				}
			}
			
			for(int i=0;i<y.length;i++){
				JSONObject obj = new JSONObject();
				if(mapnames.containsKey(y[i].trim())){
					obj.put(mapnames.get(y[i]),(double) r[i]/sum);
				}else{
					throw new RuntimeException("改名称没有对应的中文名称！");
				}
				arr.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtil.close(conn, pst, rs);
			JdbcUtil.close(conn, pst2, rs2);
		}
		return arr;
	}
	/**
	 * 统计导致OMC量化值低的OMC指标分布
	 * @param dates:最小/最大时间
	 * @param cellid:为null时，统计全网的
	 * @param key:量化值的分值范围：10-20....
	 */
	@Override
	public JSONArray findOmcResult2(JSONArray dates, String cellid, String key) {
		conn = JdbcUtil.getConnection();
		String str=JdbcUtil.getTimeSql2(dates);
		str=str.replace("recorddate", "omctime");
		String sql="",str3="";
		if(cellid!=null){
			str3=" and cellid=?";
		}
		PreparedStatement pst=null,pst2=null;
		ResultSet rs=null,rs2=null;
		JSONArray arr = new JSONArray();
		try {
			sql="select * from t_score where scoreid=1;";
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			//获取元数据
			ResultSetMetaData metaData = rs.getMetaData();
			String[] name= new String[5];
			String[] name2= new String[5];
			//将各类指标的名字放入对应数组中
			for(int i=0;i<5;i++){
				name[i]=metaData.getColumnName(i+5);
				name2[i]=metaData.getColumnName(i+11);
			}
			String str2="and comprehensive"+" between "+key.replace("-", " and ");
			sql="select count(scoreid) from t_score where "+str+str3+" "+str2;
			//查询该条件下指标的分值及对应的id
			String sql2="select  ";
			for(int i=0;i<name2.length-1;i++){
				sql2+=name2[i]+",";
			}
			sql2+=name2[name2.length-1]+" from t_score where "+str+str3+" "+str2;
			//查询该条件下的总数
			long sum=0l;
			List<Double> list = new LinkedList<Double>();
			//确定各类指标分值阈值
			for(int i=0;i<name2.length;i++){
				if(!mapthresold.containsKey(name2[i])){
					list.add(getThreshold(name2[i], cellid,"t_score"));
				}else{
					list.add(mapthresold.get(name2[i]));
				}
			}
			pst2=conn.prepareStatement(sql2);
			if(cellid!=null){
				sum=JdbcUtil.getQueryRunner().query(sql,new ScalarHandler<Long>(), cellid);
				pst2.setString(1, cellid);
			}else{
				sum=JdbcUtil.getQueryRunner().query(sql, new ScalarHandler<Long>());
			}
			rs2=pst2.executeQuery();
			//统计低于平均值的omc指标数
			Integer[] r = {0,0,0,0,0};
			while(rs2.next()){
				for(int i=0;i<name2.length;i++){
					if(rs2.getDouble(i+1)<list.get(i)){
						r[i]+=1;
					}
				}
			}
			
			for(int i=0;i<name2.length;i++){
				JSONObject obj = new JSONObject();
				if(mapnames.containsKey(name[i].trim())){
					obj.put(mapnames.get(name[i]),(double) r[i]/sum);
				}else{
					throw new RuntimeException("改名称没有对应的中文名称！");
				}
				arr.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtil.close(conn, pst, rs);
			JdbcUtil.close(conn, pst2, rs2);
		}
		return arr;
	}
	/**
	 * 统计导致综合指标差的五类指标中的具体KPI
	 * @param index:access、maintain、mobility、integri、capacity
	 * @param dates:最小/最大时间
	 * @param cellid:为null时，统计全网的
	 * @param key:量化值的分值范围：10-20....
	 */
	@Override
	public JSONArray findOmcResult3(String index, JSONArray dates, String cellid, String key) {
		conn = JdbcUtil.getConnection();
		String str=JdbcUtil.getTimeSql2(dates);
		str=str.replace("recorddate", "omctime");
		String sql="",str3="",sql3="";
		if(cellid!=null){
			str3=" and cellid=?";
		}
		PreparedStatement pst=null,pst2=null,pst3=null;
		ResultSet rs=null,rs2=null,rs3=null;
		JSONArray arr = new JSONArray();
		try {
			String index_n=index+"_n";
			String str2="and comprehensive"+" between "+key.replace("-", " and ");
			//查询该条件下指标的分值及对应的贡献分值
			String sql2="select scoreid,"+index_n+" from t_score where "+str+" "+str3+" "+str2;
			double index_th=0.0;
			//确定各类指标分值阈值
			if(!mapthresold.containsKey(index_n)){
				index_th=getThreshold(index_n, cellid,"t_score");
			}else{
				index_th=mapthresold.get(index_n);
			}
			pst2=conn.prepareStatement(sql2);
			if(cellid!=null){
				pst2.setString(1, cellid);
			}else{
			}
			rs2=pst2.executeQuery();
			
			//得到对应指标下的OMC的具体kpi指标
			sql="select * from t_omc_score where id=1;";
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			//获取元数据
			ResultSetMetaData metaData = rs.getMetaData();
			String[] access2= new String[3];
			String[] maintain2=new String[2];
			String[] mobility2= new String[2];
			String[] integri2=new String[2];
			String[] capacity2= new String[9];
			//将各类指标的名字放入对应数组中
			int j=0,k2=0;
			for(int i=0;i<43;i++){
				if(i<3){
					access2[i]=metaData.getColumnName(i+5);
				}else if(i<9){
					if(i==3||i==4){
						maintain2[i-3]=metaData.getColumnName(i+5);
					}
				}else if(i<11){
					mobility2[i-9]=metaData.getColumnName(i+5);
				}else if(i<20){
					if(i==16||i==22){
						integri2[j++]=metaData.getColumnName(i+5);
					}
				}else if(i<43){
					if(i==20||i==21||i==22||i==23||i==24||i==25||i==26||i==27||i==28){
						capacity2[k2++]=metaData.getColumnName(i+5);
					}
					
				}else{
					JdbcUtil.close(conn, pst, rs);
					JdbcUtil.close(conn, pst2, rs2);
					throw new RuntimeException("OMC指标个数超过43！");
				}
			}
			//统计低于平均值的omc指标数
			Integer[] r = {0,0,0,0,0,0,0,0,0};
			String[] y=null;
			if("access".equalsIgnoreCase(index)){
				y=access2;
			}else if("maintain".equalsIgnoreCase(index)){
				y=maintain2;
			}else if("mobility".equalsIgnoreCase(index)){
				y=mobility2;
			}else if("integri".equalsIgnoreCase(index)){
				y=integri2;
			}else if("capacity".equalsIgnoreCase(index)){
				y=capacity2;
			}else{
				throw new RuntimeException("指标名称输入错误！");
			}
			sql3="select ";
			List<Double> list2 = new LinkedList<Double>();
			for(int i=0;i<y.length-1;i++){
				sql3+=y[i]+",";
				if(!mapthresold.containsKey(y[i])){
					list2.add(getThreshold(y[i], cellid, "t_omc_score"));
				}else{
					list2.add(mapthresold.get(y[i]));
				}
				
			}
			if(!mapthresold.containsKey(y[y.length-1])){
				list2.add(getThreshold(y[y.length-1], cellid, "t_omc_score"));
			}else{
				list2.add(mapthresold.get(y[y.length-1]));
			}
			sql3+=y[y.length-1]+" from t_omc_score as t1 left join t_score as t2 on t1.id=t2.scoreid where t1.id=?";
			
			List<Integer> list = new LinkedList<Integer>();
			//统计低于阈值的omc记录的id
			while(rs2.next()){
				int id = rs2.getInt(1);
				double index_s = rs2.getDouble(2);
				if(index_s<index_th){
					list.add(id);
					Connection conn2 = JdbcUtil.getConnection();
					pst3= conn2.prepareStatement(sql3);
					pst3.setInt(1, id);
					rs3 = pst3.executeQuery();
					while(rs3.next()){
						for(int i=0;i<y.length;i++){
							System.out.println(rs3.getDouble(i+1));
							if(rs3.getDouble(i+1)<list2.get(i)){
								r[i]+=1;
							}
						}
					}
					JdbcUtil.close(conn2, pst3, rs3);
				}
			}
			System.out.println(list);
			System.out.println(list2);
			for(int i=0;i<y.length;i++){
				JSONObject obj = new JSONObject();
				if(mapnames.containsKey(y[i].trim())){
					obj.put(mapnames.get(y[i]),(double) r[i]/list.size());
				}else{
					JdbcUtil.close(conn, pst, rs);
					JdbcUtil.close(conn, pst2, rs2);
					throw new RuntimeException("改名称没有对应的中文名称！");
				}
				
				arr.add(obj);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			JdbcUtil.close(conn, pst, rs);
			JdbcUtil.close(conn, pst2, rs2);
			JdbcUtil.close(conn, pst3, rs3);
		}
			
		return arr;
		
	}
private Double getThreshold(String index,String cellid,String tablename){
	String sql="select "+index+" from "+tablename+" ;";
	Connection conn = JdbcUtil.getConnection();
	PreparedStatement pst=null;
	ResultSet rs=null;
	long sum=0l;
	double result=0.0;
	Map<Double,Integer> map =new TreeMap<Double,Integer>(new Comparator<Double>() {
		//降序排列
		@Override
		public int compare(Double o1, Double o2) {
			return o1.compareTo(o2);
		}
	});
	try {
		//统计总数
		String id="";
		if("t_score".equalsIgnoreCase(tablename)){
			id="scoreid";
		}else if("t_omc_score".equalsIgnoreCase(tablename)){
			id="id";
		}
		String sql2="select count("+id+") from "+tablename+" ;";
		if(cellid!=null){
			sql=sql+" where cellid=?";
			pst = conn.prepareStatement(sql);
			pst.setString(1, cellid);
			sql2=sql2+" where cellid=?";
			sum = JdbcUtil.getQueryRunner().query(sql2, new ScalarHandler<Long>(),cellid);
		}else{
			pst = conn.prepareStatement(sql);
			sum = JdbcUtil.getQueryRunner().query(sql2, new ScalarHandler<Long>());
		}
		rs = pst.executeQuery();
		while(rs.next()){
			double d = rs.getDouble(1);
			if(map.containsKey(d)){
				map.put(d, map.get(d)+1);
			}else{
				map.put(d, 1);
			}
		}
		
		//找出该指标后30%对应的分值
		Set<Entry<Double, Integer>> set = map.entrySet();
		double s=0.0;
		for (Entry<Double, Integer> entry : set) {
			s+=entry.getValue();
			if(s/sum>0.3){
				result= entry.getKey();
				break;
			}
		}
		mapthresold.put(index, result);
	} catch (SQLException e) {
		e.printStackTrace();
	}finally{
		JdbcUtil.close(conn, pst, rs);
	}
	return result;
	
}
/**
 * 用户登陆验证
 * @param name:员工姓名
 * @param userworkid:员工工号
 * @param userphonenum:员工手机号
 */
@Override
public boolean findUser(String name, String userworkid, String userphonenum) {
	conn=JdbcUtil.getConnection();
	String sql=" select userid from t_user where username=? and userworkid=? and userphonenum=?;";
	boolean flag=false;
	try {
		pst=conn.prepareStatement(sql);
		pst.setString(1, name);
		pst.setString(2, userworkid);
		pst.setString(3, userphonenum);
		rs = pst.executeQuery();
		if(rs.next()){
			flag=true;
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}finally{
		JdbcUtil.close(conn, pst, rs);
	}
	
	return flag;
}
/**
 * 用户注册
 * @param name:员工姓名
 * @param userworkid:员工工号
 * @param userphonenum:员工手机号
 */
@Override
public boolean addUser(String name, String userworkid, String userphonenum) {
	conn=JdbcUtil.getConnection();
	String sql=" select userid from t_user where username=? and userworkid=? and userphonenum=?;";
	boolean flag=false;
	try {
		pst=conn.prepareStatement(sql);
		pst.setString(1, name);
		pst.setString(2, userworkid);
		pst.setString(3, userphonenum);
		rs = pst.executeQuery();
		if(rs.next()){
			return false;
		}
		//该用户不存在时允许注册
		sql="insert into t_user(username,userworkid,userphonenum) values(?,?,?)";
		pst=conn.prepareStatement(sql);
		pst.setString(1, name);
		pst.setString(2, userworkid);
		pst.setString(3, userphonenum);
		Integer result=pst.executeUpdate();
		if(result.intValue()!=0&&result!=null){
			flag=true;
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}finally{
		JdbcUtil.close(conn, pst, rs);
	}
	
	return flag;
}
/**
 * 删除管理员
 * @param userid:管理员账号id
 */
@Override
public boolean deleteUser(int userid) {
	String sql="delete from t_user where userid=?";
	Connection conn=JdbcUtil.getConnection();
	PreparedStatement pst=null;
	boolean flag=false;
	try {
		 pst = conn.prepareStatement(sql);
		 pst.setInt(1, userid);
		 Integer result = pst.executeUpdate();
		 if(result.intValue()!=0&&result!=null){
			 flag=true;
		 }
	} catch (SQLException e) {
		e.printStackTrace();
	}finally{
		JdbcUtil.close(conn, pst, rs);
	}
			
	return flag;
}
/**
 * 查询数据库中所有管理员用户
 * 
 */
@SuppressWarnings("unchecked")
@Override
public  List<AdminBean> findAllUser() {
	String sql="select * from t_user";
	List<AdminBean> list=null;
	try {
		list = JdbcUtil.getQueryRunner().query(sql, new BeanListHandler<AdminBean>(AdminBean.class));
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return list;
}
}

