package bupt.edu.dao;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
public interface IAppDao {
		//获取天数列表
		public List<String>findTimeList();
		//统计RSRP、RSRQ、SINR分布【全网或单小区】
		public JSONArray findByRSRP_RSRQ_SINR(String type,String cellid,JSONArray dates);
		//分段统计cpu占用率、内存占用率【全网或单小区】
		public JSONArray findByCPU_Memory(String type,String cellid,JSONArray dates);
		//分段统计全网[单小区]五类KPI指标量化值、综合量化值
		public JSONArray findByKPI(String index,JSONArray dates,String cellid);
		//查询每个小区一段时间的的KPI量化值【五个或综合指标】画折线图
		public List<Double>  findBySingleKPI(String cellid,String index,JSONArray dates);
		//查询终端类型、异常业务类型【全网或单小区】
		public JSONObject findByAppData(String type,String cellid,JSONArray dates);
		//查询单用户的OMC具体51项指标值
		public JSONArray findOmcData(Integer omcid);
		//查询单用户omc指标及网络情况
		public <T>List<T> findByOmcId(String imsi,Integer omcid,JSONArray dates);
		//查询单用户终端上报情况
		public <T>List<T> findByImsi(String imsi,String cellid,JSONArray dates);
		//查询单小区上报情况
		public <T>List<T> findByCellid(String cellid,JSONArray dates);
		//查询全部上报小区列表
		public <T>List<T> findByAllCellid(JSONArray dates);
		//统计手机品牌、手机CPU使用率、手机内存占用率分别与RSRP关系。【全网或单小区】
		public JSONObject findRSRP_Terminal(String type,JSONArray dates,String cellid);
		//统计手机品牌、手机CPU使用率、手机内存占用率分别与五类KPI指标量化值及KPI综合量化值关系。【全网或单小区】
		public JSONObject findTerminal_Index(String type,String index,JSONArray dates,String cellid);
		//RSRP/RSRQ/SINR分别与五类KPI指标量化值及KPI综合量化值关系。【全网或单小区】
		public JSONObject findRSRP_Index(String type,String index,JSONArray dates,String cellid);
		//下载速率与RSRP、RSRQ、SINR关联分析【全网或单小区】
		public JSONObject findRate_Network(String type,String cellid,JSONArray dates);
		//下载速率与五类KPI指标、综合KPI指标关系【全网或单小区】
		public JSONObject findRate_Kpi(String index,JSONArray dates,String cellid);
		//统计导致OMC量化值低的OMC指标分布【全网或单小区】[针对五类KPI]
		public JSONArray findOmcResult(String index,JSONArray dates,String cellid,String key);
		//统计导致OMC量化值低的OMC指标分布【全网或单小区】[针对综合指标]
		public JSONArray findOmcResult2(JSONArray dates,String cellid,String key);
		//统计导致综合指标差的五类指标中的具体KPI
		public JSONArray findOmcResult3(String index,JSONArray dates,String cellid,String key);
		//用户登陆验证
		public boolean findUser(String name,String userworkid,String userphonenum);
		//用户注册
		public boolean addUser(String name,String userworkid,String userphonenum);
		//获取用户列表
		public <T>List<T> findAllUser();
		//用户删除
		public boolean deleteUser(int userid);
		
	}
