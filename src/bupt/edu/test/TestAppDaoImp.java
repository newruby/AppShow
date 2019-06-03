package bupt.edu.test;

import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import bupt.edu.dao.imp.AppDaoImp;
import bupt.edu.entity.AdminBean;
import bupt.edu.entity.AllCellBean;
import bupt.edu.entity.CellDataBean;
import bupt.edu.entity.OmcBean;
import bupt.edu.entity.UserBean;

public class TestAppDaoImp {

	@Test
	public void test(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 14:00:00");
		arr.add("2017/08/27 15:00:00");
		JSONObject object = dao.findByAppData("appName",null,arr);
		System.out.println(object.toString());
	}
	@Test
	public void test2(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 20:05:00");
		arr.add("2017/08/27 20:05:00");
		List<OmcBean> list = dao.findByOmcId("460026000000000", 82, arr);
		System.out.println(list);
	}
	@Test
	public void test3(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		//arr.add("2016-07-06 01:00:00");
		arr.add("2017/08/27 00:00:00");
		arr.add("2017/08/28 00:00:00");
		List<UserBean> list = dao.findByImsi("460025767707060", "49309725", arr);
		System.out.println(list.toString());
	}
	
	@Test
	public void test4(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 00:00:00");
		arr.add("2017/08/28 00:00:00");
		List<CellDataBean> list = dao.findByCellid( "49309725", arr);
		System.out.println(list.toString());
	}
	
	@Test
	public void test5(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 00:00:00");
		arr.add("2017/08/27 10:00:00");
		List<AllCellBean> list = dao.findByAllCellid(arr);
		System.out.println(list.toString());
	}
	
	@Test
	public void test6(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 09:00:00");
		arr.add("2017/08/27 10:00:00");
		String arrs= dao.findByRSRP_RSRQ_SINR("sinr","49309725",arr).toJSONString();
		System.out.println(arrs);
	}
	
	@Test
	public void test7(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 09:00:00");
		arr.add("2017/08/27 15:00:00");
		JSONArray arrs= dao.findByCPU_Memory("cpurate",null,arr);
		System.out.println(arrs.toString());
	}
	
	
	@Test
	public void test8(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 00:00:00");
		arr.add("2017/08/28 00:00:00");
		JSONArray arrs= dao.findByKPI("comprehensive",arr,null);
		System.out.println(arrs.toString());
	}
	
	@Test
	public void test9(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 09:00:00");
		arr.add("2017/08/27 15:00:00");
		List<Double> arrs= dao.findBySingleKPI("4170411","access",arr);
		System.out.println(arrs.toString());
	}
	
	@Test
	public void test10(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 09:00:00");
		arr.add("2017/08/27 15:00:00");
		JSONObject arrs= dao.findRSRP_Terminal("deviceBrand",arr,null);
		System.out.println(arrs.toString());
	}
	
	@Test
	public void test11(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 09:00:00");
		arr.add("2017/08/27 15:00:00");
		JSONObject arrs= dao.findTerminal_Index("cpurate","access",arr,null);
		System.out.println(arrs.toString());
	}
	
	@Test
	public void test12(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 00:00:00");
		arr.add("2017/08/28 00:00:00");
		JSONObject arrs= dao.findRSRP_Index("rsrq","maintain",arr,null);
		System.out.println(arrs.toString());
	}
	
	@Test
	public void test13(){
		AppDaoImp dao = new AppDaoImp();
		List<String> arrs= dao.findTimeList();
		System.out.println(arrs.toString());
	}
	
	
	@Test
	public void test14(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2016-07-06 01:00:00");
		arr.add("2016-07-06 00:00:00");
		JSONObject arrs= dao.findRate_Network("sinr","91309",arr);
		System.out.println(arrs.toString());
	}
	

	@Test
	public void test15(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2016-07-06 01:00:00");
		arr.add("2016-07-06 00:00:00");
		JSONObject arrs= dao.findRate_Kpi("maintain",arr,"49309725");
		System.out.println(arrs.toString());
	}
	@Test
	public void test16(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 00:00:00");
		arr.add("2017/08/28 00:00:00");
		JSONArray a = dao.findOmcData(1);
		System.out.println(a.toJSONString());
	}
	@Test
	public void test17(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 00:00:00");
		arr.add("2017/08/28 00:00:00");
		JSONArray result = dao.findOmcResult("maintain", arr, null, "70-80");
		System.out.println(result.toJSONString());
	}
	@Test
	public void test18(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 00:00:00");
		arr.add("2017/08/28 00:00:00");
		JSONArray result = dao.findOmcResult2( arr, null, "70-80");
		System.out.println(result.toJSONString());
	}
	
	@Test
	public void test19(){
		AppDaoImp dao = new AppDaoImp();
		JSONArray arr = new JSONArray();
		arr.add("2017/08/27 00:00:00");
		arr.add("2017/08/28 00:00:00");
		JSONArray result = dao.findOmcResult3("access", arr, null, "70-80");
		System.out.println(result.toJSONString());
	}
	@Test
	public void test20(){
		AppDaoImp dao = new AppDaoImp();
		boolean flag = dao.addUser("张三", "123", "13200000000");
		System.out.println(flag);
	}
	
	@Test
	public void test21(){
		AppDaoImp dao = new AppDaoImp();
		boolean flag = dao.findUser("张三", "123", "13200000000");
		System.out.println(flag);
	}
	
	@Test
	public void test22(){
		AppDaoImp dao = new AppDaoImp();
		List<AdminBean> list = dao.findAllUser();
		System.out.println(list);
	}
	@Test
	public void test23(){
		AppDaoImp dao = new AppDaoImp();
		boolean flag = dao.deleteUser(2);
		System.out.println(flag);
	}
}
