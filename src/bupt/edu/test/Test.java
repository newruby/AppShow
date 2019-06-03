package bupt.edu.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Test {

	public static void main(String[] args) throws ParseException {
		/*Connection conn = JdbcUtil.getConnection();
		String sql="select * from t_app where record_time between '2016-07-07 00:00:00' and '2016-07-07 01:00:00'";
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				System.out.println("hhh");
			}else {
				System.out.println("hhh2");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss 格林尼治标准时间+0800 yyyy",Locale.ENGLISH);
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str="Thu Aug 24 14:59:22 格林尼治标准时间+0800 2017";
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf2.format(sdf.parse(str)));
	}
}
