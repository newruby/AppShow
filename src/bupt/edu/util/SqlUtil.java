package bupt.edu.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.alibaba.fastjson.JSONObject;

public class SqlUtil {

	public static JSONObject getJSONObject(PreparedStatement pst,Connection conn,ResultSet rs,JSONObject jsobj,String sql,String cellid,String key2){
		try {
			pst=conn.prepareStatement(sql);
			if(cellid !=null){
				pst.setString(1, cellid);
			}
			rs = pst.executeQuery();
			while(rs.next()){
				jsobj.put(key2, rs.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("RSRP、RSRQ、SINR与速率统计出错！");
		}
		return jsobj;
	}
	public static JSONObject getJSONObject2(PreparedStatement pst,Connection conn,ResultSet rs,JSONObject jsobj,String sql,String key2){
		try {
			pst=conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while(rs.next()){
				jsobj.put(key2, rs.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsobj;
		
	}
	public static JSONObject getJSONObject3(PreparedStatement pst,Connection conn,ResultSet rs,String sql,long sum,String cellid,String key){
		JSONObject jsobj = new JSONObject();
		try {
			pst=conn.prepareStatement(sql);
			if(cellid != null){
				pst.setString(1, cellid);
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				jsobj.put(key, (double)rs.getInt(1)/sum);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsobj;
		
	}
}
