package bupt.edu.service;

import com.alibaba.fastjson.JSONArray;


public interface IAppService {
		//获得每天/每小时时间列表
		public String findTimeList();
		//分段统计全网的RSRP分布
		public String findByRSRP_RSRQ_SINR(String type,String cellid,JSONArray dates);
		//分段统计cpu占用率、内存占用率（全网或单基站）
		public String findByCPU_Memory(String type,String cellid,JSONArray dates);
		//分段统计全网五类KPI指标量化值、综合量化值
		public  String findByKPI(String index,JSONArray dates,String cellid);
		//查询每个小区一段时间的的KPI量化值【五个或综合指标】
		public String findBySingleKPI(String cellid,String index,JSONArray dates);
		//查询终端类型、异常业务类型（全网或单基站）
		public String findByAppData(String type,String cellid,JSONArray dates);
		//查询单用户的OMC具体51项指标值
		public String findOmcData(Integer omcid);
		//查询单用户omc指标及网络情况
		public String findByOmcId(String imsi,Integer omcid,JSONArray dates);
		//查询单用户终端上报情况
		public String findByImsi(String imsi,String enodeb,JSONArray dates);
		//查询单基站上报情况
		public String findByCellid(String cellid,JSONArray dates);
		//查询全部上报基站列表
		public String findByAllCellid(JSONArray dates);
		//统计手机品牌、手机CPU使用率、手机内存占用率分别与RSRP关系。
		public String findRSRP_Terminal(String type,JSONArray dates,String cellid);
		//统计手机品牌、手机CPU使用率、手机内存占用率分别与五类KPI指标量化值及KPI综合量化值关系。
		public String findTerminal_Index(String type,String index,JSONArray dates,String cellid);
		//RSRP分别与五类KPI指标量化值及KPI综合量化值关系。
		public String findRSRP_Index(String type,String index,JSONArray dates,String cellid);
		//下载速率与RSRP、RSRQ、SINR关联分析【全网或单小区】
		public String findRate_Network(String type,String cellid,JSONArray dates);
		//下载速率与五类KPI指标、综合KPI指标关系
		public String findRate_Kpi(String index,JSONArray dates,String cellid);
		//统计导致OMC量化值低的OMC指标分布【全网或单小区】[针对五类KPI]
		public String findOmcResult(String index,JSONArray dates,String cellid,String key);
		//统计导致OMC量化值低的OMC指标分布【全网或单小区】[针对综合指标]
		public String findOmcResult2(JSONArray dates,String cellid,String key);
		//统计导致综合指标差的五类指标中的具体KPI
		public String findOmcResult3(String index,JSONArray dates,String cellid,String key);
		//用户登陆验证
		public String login(String name,String userworkid,String userphonenum);
		//用户注册
		public String register(String name,String userworkid,String userphonenum);
		//获取用户列表
		public String findAllUser();
		//用户删除
		public String deleteUser(int userid);
}
