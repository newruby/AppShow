package bupt.edu.action;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import bupt.edu.service.imp.AppServiceImp;


@Controller
@RequestMapping(value="/allCell")
public class AllCellAction {
	@Resource(name="appServiceImp")
	private AppServiceImp appServiceImp;
	public void setAppServiceImp(AppServiceImp appServiceImp) {
		this.appServiceImp = appServiceImp;
	}
	
	/**
	 * 统计RSRP、RSRQ、SINR分布【全网或单小区】
	 * @param type:RSRP、RSRQ、SINR
	 * @param cellid : 查询全网时为null
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@RequestMapping(value="/countRsrp")
	public @ResponseBody String countRsrp(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String type=jsobj.getString("type");
		String cellid=jsobj.getString("cellid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findByRSRP_RSRQ_SINR(type,cellid,arr);
	}
	
	/**
	 * 分段统计cpu占用率、内存占用率（全网或单小区）
	 * 
	 * @param type:cpuRate、memoryrate
	 * @param cellid：小区id，查询全网时为null
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@RequestMapping(value="/countCpu_Memory")
	public @ResponseBody String countCpu_Memory(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String type=jsobj.getString("type");
		String cellid=jsobj.getString("cellid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findByCPU_Memory(type, cellid,arr);
	}
	/**
	 * 查询终端类型、异常业务类型【全网或单小区】
	 * 只能输入：deviceBrand或appName！
	 * @param dates:最大、最小时间
	 * @param type:deviceBrand、appName
	 * @param cellid:查询全网时为null
	 */
	@RequestMapping(value="/countApp")
	public @ResponseBody String countApp(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String type=jsobj.getString("type");
		String cellid=jsobj.getString("cellid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findByAppData(type, cellid,arr);
	}
	
	/**
	 * 查询全网的KPI量化值分布情况
	 * @param index:access、maintain、mobility、integri、capacity、 comprehensive
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@RequestMapping(value="/countKpi")
	public @ResponseBody String countKpi(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String index=jsobj.getString("index");
		String cellid=jsobj.getString("cellid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findByKPI(index,arr,cellid);
	}
	

	/**
	 * 	统计手机品牌与手机CPU使用率、手机内存占用率分别与RSRP关系。
	 * @param type:deviceBrand、cpuRate、memoryrate
	 * @param dates:最大、最小时间[要查询的时间段]
	 * @param cellid:为null时查询全网
	 */
	@RequestMapping(value="/countRSRP_Terminal")
	public @ResponseBody String countRSRP_App(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String type=jsobj.getString("type");
		String cellid=jsobj.getString("cellid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findRSRP_Terminal(type,arr,cellid);
	}
	
	/**
	 * 统计手机品牌、手机CPU使用率、手机内存占用率分别与五类KPI指标量化值及KPI综合量化值关系。
	 * @param type:deviceBrand、cpuRate、memoryrate
	 * @param index:acess、maintain、mobility、integri、capacity、comprehensive
	 * @param dates:最大、最小时间[要查询的时间段]
	 * @param cellid:为null时查询全网
	 */
	@RequestMapping(value="/countIndex_App")
	public @ResponseBody String countIndex_App(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String type=jsobj.getString("type");
		String index=jsobj.getString("index");
		String cellid=jsobj.getString("cellid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findTerminal_Index(type,index,arr,cellid);
	}
	

	/**
	 * RSRP分别与五类KPI指标量化值及KPI综合量化值关系。
	 * @param index:acess、maintain、mobility、integri、capacity、comprehensive
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@RequestMapping(value="/countRSRP_Index")
	public @ResponseBody String countRSRP_Index(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String type=jsobj.getString("type");
		String index=jsobj.getString("index");
		JSONArray arr = jsobj.getJSONArray("dates");
		String cellid=jsobj.getString("cellid");
		return appServiceImp.findRSRP_Index(type,index,arr,cellid);
	}
	
	//下载速率与RSRP、RSRQ、SINR关联分析【全网或单小区】
	@RequestMapping(value="/countRate_Network")
	public @ResponseBody String countRate_Network(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String type=jsobj.getString("type");
		String cellid=jsobj.getString("cellid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findRate_Network(type, cellid,arr);
	}
	
	//下载速率与五类KPI指标、综合KPI指标关系
	/**
	 * 
	 * @param json
	 *  @param cellid:为null时查询全网
	 * @return
	 */
	@RequestMapping(value="/countRate_Kpi")
	public @ResponseBody String countRate_Kpi(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String index=jsobj.getString("index");
		String cellid=jsobj.getString("cellid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findRate_Kpi(index,arr,cellid);
	}
}
