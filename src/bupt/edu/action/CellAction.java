package bupt.edu.action;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import bupt.edu.service.imp.AppServiceImp;

@Controller
@RequestMapping(value="/Cell")
public class CellAction {
	@Resource(name="appServiceImp")
	private AppServiceImp appServiceImp;
	public void setAppServiceImp(AppServiceImp appServiceImp) {
		this.appServiceImp = appServiceImp;
	}

	/**
	 * 查询单小区上报数据
	 * @param cellid
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@RequestMapping(value="/findCellData")
	public @ResponseBody String findCellData(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String cellid=jsobj.getString("cellid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findByCellid(cellid,arr);
		
	}
	
	/**
	 * 查询全部上报小区列表
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@RequestMapping(value="/findAllCell")
	public @ResponseBody String findAllCell(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findByAllCellid(arr);
	}
	
	/**
	 * 查询每个小区一段时间的的KPI量化值【五个或综合指标】
	 * @param cellid 要查的小区
	 * @param index:access、maintain、mobility、integri、capacity、 comprehensive
	 * @param dates:最大、最小时间[要查询的时间段]
	 */
	@RequestMapping(value="/findCellKpi")
	public @ResponseBody String findCellKpi(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String cellid=jsobj.getString("cellid");
		String index=jsobj.getString("index");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findBySingleKPI(cellid,index,arr);
	}
	
	/**
	 * 获取已有数据的日期的列表
	 * 
	 */
	@RequestMapping(value="/findTimeList")
	public @ResponseBody String findTimeList(){
		return appServiceImp.findTimeList();
	}
	/**
	 * 统计导致OMC量化值低的OMC指标分布【全网或单小区】[针对五类KPI]
	 * @param index:access、maintain、mobility、integri、capacity
	 * @param dates:最小/最大时间
	 * @param cellid:为null时，统计全网的
	 * @param key:量化值的分值范围：10-20....
	 * 
	 */
	@RequestMapping(value="/find_omcresult")
	public @ResponseBody String find_omcresult(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String key = jsobj.getString("key");
		JSONArray dates = jsobj.getJSONArray("dates");
		String cellid = jsobj.getString("cellid");
		String index = jsobj.getString("index");
		return appServiceImp.findOmcResult(index, dates, cellid, key);
	}
	/**
	 * 统计导致OMC量化值低的OMC指标分布
	 * @param dates:最小/最大时间
	 * @param cellid:为null时，统计全网的
	 * @param key:量化值的分值范围：10-20....
	 *
	 */
	@RequestMapping(value="/find_omcresult2")
	public @ResponseBody String find_omcresult2(HttpServletRequest re,@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		JSONArray dates = jsobj.getJSONArray("dates");
		String cellid = jsobj.getString("cellid");
		String key = jsobj.getString("key");
		//将综合指标查询条件放入到session中
		HttpSession session = re.getSession();
		session.setAttribute("key", key);
		session.setAttribute("dates", dates);
		session.setAttribute("cellid", cellid);
		return appServiceImp.findOmcResult2(dates, cellid, key);
	}
	/**
	 * 统计导致综合指标差的五类指标中的具体KPI
	 * @param index:access、maintain、mobility、integri、capacity
	 * @param dates:最小/最大时间
	 * @param cellid:为null时，统计全网的
	 * @param key:量化值的分值范围：10-20....
	 */
	@RequestMapping(value="/find_omcresult3")
	public @ResponseBody String find_omcresult3(HttpServletRequest re,@RequestBody String json){
		HttpSession session = re.getSession(false);
		JSONArray dates = (JSONArray) session.getAttribute("dates");
		String cellid = (String) session.getAttribute("cellid");
		String key = (String) session.getAttribute("key");
		JSONObject jsobj = JSONObject.parseObject(json);
		String index = jsobj.getString("index");
		return appServiceImp.findOmcResult3(index, dates, cellid, key);
	}
}
