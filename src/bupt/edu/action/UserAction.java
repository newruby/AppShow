package bupt.edu.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import bupt.edu.service.imp.AppServiceImp;

@Controller
@RequestMapping(value="/user")
public class UserAction {
	

	@Autowired private AppServiceImp appServiceImp;
	
	/**
	 * 查询单用户在指定小区的终端上报情况
	 * @param imsi:用户标识
	 * @param cellid 
	 * @param dates:最大、最小时间
	 */
	@RequestMapping(value="/findUserData")
	public @ResponseBody String findUserData(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String imsi=jsobj.getString("imsi");
		String cellid=jsobj.getString("cellid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findByImsi(imsi,cellid,arr);
	}
	
	/**
	 * 查询单用户omc指标及网络rsrp、rsrq、sinr情况
	 * @param imsi:用户标识号
	 * @param mocid 关联上的t_omc的主键
	 * @param dates:最大、最小时间
	 */
	@RequestMapping(value="/findOmc_Kpi")
	public @ResponseBody String findOmc_Kpi(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String imsi=jsobj.getString("imsi");
		int omcid=jsobj.getInteger("omcid");
		JSONArray arr = jsobj.getJSONArray("dates");
		return appServiceImp.findByOmcId(imsi, omcid,arr);
	}
	
	/**
	 * 查询单用户的OMC具体51项指标值
	 * @param omcid:要查询的omcid
	 */
	@RequestMapping(value="/findomcdata")
	public @ResponseBody String findomcdata(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		Integer omcid = jsobj.getInteger("omcid");
		return appServiceImp.findOmcData(omcid);
	}
	
	/**
	 * 用户登陆验证
	 * @param name:员工姓名
	 * @param userworkid:员工工号
	 * @param userphonenum:员工手机号
	 */
	@RequestMapping(value="/login")
	public @ResponseBody String login(HttpServletRequest rqs,@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String username=jsobj.getString("username");
		String userworkid=jsobj.getString("userworkid");
		String userphonenum=jsobj.getString("userphonenum");
		String result = appServiceImp.login(username, userworkid, userphonenum);
		if("1".equalsIgnoreCase(result)){
			//将登陆成功的用户信息放入session中
			HttpSession session = rqs.getSession();
			session.setAttribute("username", username);
		}
		return result;
		
	}
	/**
	 * 用户注册
	 * @param name:员工姓名
	 * @param userworkid:员工工号
	 * @param userphonenum:员工手机号
	 */
	@RequestMapping(value="/register")
	public @ResponseBody String register(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		String username=jsobj.getString("username");
		String userworkid=jsobj.getString("userworkid");
		String userphonenum=jsobj.getString("userphonenum");
		String result = appServiceImp.register(username, userworkid, userphonenum);
		return result;
		
	}
	
	/**
	 * 查询数据库中所有管理员用户
	 * 
	 */
	@RequestMapping(value="/findAllAdmin")
	public @ResponseBody String findAllAdmin(){
		return appServiceImp.findAllUser();
		
	}
	/**
	 * 删除管理员
	 * @param userid:管理员账号id
	 */
	@RequestMapping(value="/deleteAdmin")
	public @ResponseBody String deleteAdmin(@RequestBody String json){
		JSONObject jsobj = JSONObject.parseObject(json);
		Integer id = jsobj.getInteger("userid");
		return appServiceImp.deleteUser(id);
		
	}
	
	/**
	 * 管理员账号退出
	 * 
	 */
	@RequestMapping(value="/exit")
	public @ResponseBody String exit(HttpServletRequest rs){
		HttpSession session = rs.getSession(false);
		if(session != null){
			session.removeAttribute("username");
		}else{
			throw new RuntimeException("找不到对象");
		}
		return appServiceImp.findAllUser();
		
	}
}
