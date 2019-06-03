package bupt.edu.service.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import bupt.edu.dao.imp.AppDaoImp;
import bupt.edu.entity.AdminBean;
import bupt.edu.entity.AllCellBean;
import bupt.edu.entity.CellDataBean;
import bupt.edu.entity.OmcBean;
import bupt.edu.entity.UserBean;
import bupt.edu.service.IAppService;
@Service
public class AppServiceImp implements IAppService {
	@Autowired
	private AppDaoImp appDaoImp;
	
	public void setAppDaoImp(AppDaoImp appDaoImp) {
		this.appDaoImp = appDaoImp;
	}
	@Override
	public String findByRSRP_RSRQ_SINR(String type,String cellid,JSONArray dates) {
		return appDaoImp.findByRSRP_RSRQ_SINR(type,cellid,dates).toJSONString();
	}

	@Override
	public String findByCPU_Memory(String type, String cellid,JSONArray dates) {
		return appDaoImp.findByCPU_Memory(type, cellid,dates).toJSONString();
	}

	@Override
	public String findByKPI(String index,JSONArray dates,String cellid) {
		return appDaoImp.findByKPI(index,dates,cellid).toJSONString();
	}

	@Override
	public String findBySingleKPI(String cellid,String index,JSONArray dates) {
		List<Double> list = appDaoImp.findBySingleKPI(cellid,index,dates);
		return JSONArray.parseArray(JSON.toJSONString(list)).toJSONString();
	}

	@Override
	public String findByAppData(String type, String cellid,JSONArray dates) {
		return appDaoImp.findByAppData(type, cellid,dates).toJSONString();
	}

	@Override
	public String findByOmcId(String imsi, Integer omcid,JSONArray dates) {
		List<OmcBean> list = appDaoImp.findByOmcId(imsi, omcid,dates);
		return JSONArray.parseArray(JSON.toJSONString(list)).toJSONString();
	}

	@Override
	public String findByImsi(String imsi,String enodeb,JSONArray dates) {
		List<UserBean> list = appDaoImp.findByImsi(imsi,enodeb,dates);
		return JSONArray.parseArray(JSON.toJSONString(list)).toJSONString();
	}

	@Override
	public String findByCellid(String cellid,JSONArray dates) {
		List<CellDataBean> list = appDaoImp.findByCellid(cellid,dates);
		return JSONArray.parseArray(JSON.toJSONString(list)).toJSONString();
	}

	@Override
	public String findByAllCellid(JSONArray dates) {
		List<AllCellBean> list = appDaoImp.findByAllCellid(dates);
		return JSONArray.parseArray(JSON.toJSONString(list)).toJSONString();
	}

	@Override
	public String findRSRP_Terminal(String type,JSONArray dates,String cellid) {
		
		return appDaoImp.findRSRP_Terminal(type,dates,cellid).toJSONString();
	}

	@Override
	public String findTerminal_Index(String type, String index,JSONArray dates,String cellid) {
		return appDaoImp.findTerminal_Index(type, index,dates,cellid).toJSONString();
	}

	@Override
	public String findRSRP_Index(String type,String index,JSONArray dates,String cellid) {
		return appDaoImp.findRSRP_Index(type,index,dates,cellid).toJSONString();
	}
	@Override
	public String findTimeList() {
		return JSON.toJSONString(appDaoImp.findTimeList());
	}
	@Override
	public String findRate_Network(String type, String cellid,JSONArray dates) {
		return appDaoImp.findRate_Network(type, cellid,dates).toJSONString();
	}
	@Override
	public String findRate_Kpi(String index,JSONArray dates,String cellid) {
		return appDaoImp.findRate_Kpi(index,dates,cellid).toJSONString();
	}
	@Override
	public String findOmcData(Integer omcid) {
		return appDaoImp.findOmcData(omcid).toJSONString();
	}
	@Override
	public String findOmcResult(String index, JSONArray dates, String cellid, String key) {
		return appDaoImp.findOmcResult(index, dates, cellid, key).toJSONString();
	}
	@Override
	public String findOmcResult2(JSONArray dates, String cellid, String key) {
		return appDaoImp.findOmcResult2(dates, cellid, key).toJSONString();
	}
	@Override
	public String findOmcResult3(String index, JSONArray dates, String cellid, String key) {
		return appDaoImp.findOmcResult3(index, dates, cellid, key).toJSONString();
	}
	@Override
	public String login(String name, String userworkid, String userphonenum) {
		boolean flag = appDaoImp.findUser(name, userworkid, userphonenum);
		if(flag){
			return "1";
		}
		return "0";
	}
	@Override
	public String register(String name, String userworkid, String userphonenum) {
		boolean flag = appDaoImp.addUser(name, userworkid, userphonenum);
		if(flag){
			return "1";
		}
		return "0";
	}
	@Override
	public String findAllUser() {
		List<AdminBean> list = appDaoImp.findAllUser();
		return JSON.toJSONString(list);
	}
	@Override
	public String deleteUser(int userid) {
		boolean flag = appDaoImp.deleteUser(userid);
		if(flag){
			return "1";
		}
		return "0";
	}

}
