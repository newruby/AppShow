package bupt.edu.dataclean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import bupt.edu.util.JdbcUtil;

/**
 * 缺失值和异常值清洗
 * 数据入库
 * @author lynatteluo
 *
 */
public class CleanData {

	
	/**	
	 * 先计算t_app中的发送/接收速率、内存占用率，及与omc关联
	 * 注意：缺失值处理和分母零值处理
	 * @param filepath：appdata路径
	 * @param filepath2:输出路径
	 * @param map：omc信息
	 */
	public  static void calApp(String filepath, String filepath2,Map<Integer,List<String>> map){
		File[] list = new File(filepath).listFiles();
		int i=1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement pst=null;
		String sql="insert into t_app values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try {
			pst=conn.prepareStatement(sql);
			for (File file : list) {
				//FileWriter fw =null;
				if(file.getName().endsWith(".csv")||file.getName().endsWith(".CSV")){
					//指定以GBK格式读文件
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
				//	String filename=file.getName();
				//	fw = new FileWriter(filepath2+filename.substring(0,filename.length()-4),true);
					String line=br.readLine();//标题栏跳过
					while((line=br.readLine())!=null){
						//StringBuilder sb = new StringBuilder();
						int omcid=0;
						String[] datas = line.split(",");
						//判断数据中是否有空格
						for (String string : datas) {
							if("".equals(string)){
								continue;
							}
						}
						//判断网络类型是否为wifi
						if("wifi".equalsIgnoreCase(datas[16])){
							continue;
						}
						//时间格式转化
						String d="";
						if(datas[22].length()<18){
							d=sdf.format(sdf2.parse(datas[22]));
						}else{
							if(datas[22].contains("-")){
								d=sdf.format(sdf3.parse(datas[22]));
							}else{
								d=datas[22];
							}
							
						}
						//获取appdata对应的omcid
						Set<Entry<Integer, List<String>>> entrySet = map.entrySet();
						for (Entry<Integer, List<String>> entry : entrySet) {
							List<String> value = entry.getValue();
							// 判断cellid是否相同
						//	String cellid=datas[19]+datas[11];
						//	if(value.get(0).equals(cellid)){//cellid相同
								// 判断时间是否在范围内
							long diff=sdf.parse(value.get(1)).getTime()-sdf.parse(d).getTime();
								if(diff<=900000&&diff>=0){
									omcid=entry.getKey();
									break;
								}
								
						//	}
						}
						String str=datas[19]+datas[11];
						//主键、imsi、时间、cellid
						//sb.append(i+","+datas[1]+","+date+","+str+",");
						pst.setInt(1, i);
						//判断imsi是否为科学计数表达
						String imsi="";
						if(datas[1].contains("E")){
							imsi=new BigDecimal(datas[1]).toPlainString();
						}else{
							imsi=datas[1];
						}
						pst.setString(2, imsi);
						pst.setString(3, d);
						pst.setString(4, str);
						//curTxBytes、rsrp、deviceBrand、memUsed、memTotal、、、、、、
						pst.setDouble(5, Double.parseDouble(datas[2]));
						pst.setDouble(6, Double.parseDouble(datas[3]));
						
						for(int j=5;j<32;j++){
							if(j>=25){
								//sb.append(datas[j-2]+",");
								pst.setString(j, datas[j-2]);
							}else if(j<25){
								//sb.append(datas[j-3]+",");
								if(j==5||j==6||j==8||j==9||j==11||j==12){
									pst.setDouble(j, Double.parseDouble(datas[j-3]));
								}else if(j==21){
									if("".equals(datas[j-3])||"null".equals(datas[j-3])){
										pst.setDouble(j, 0);
									}else{
										pst.setDouble(j, Double.parseDouble(datas[j-3])/100);
									}
									
								}else{
									pst.setString(j, datas[j-3]);
								}
							}
							
						}
						double a1=Double.parseDouble(datas[2])*8/1000;//sendrate
						double a2=Double.parseDouble(datas[14])*8/1000;//receiverate
						if("0".equals(datas[6])){
							continue;
						}
						double a3=Double.parseDouble(datas[5])/Double.parseDouble(datas[6]);//memoryrate
						//omcid
						pst.setDouble(32,Double.parseDouble(datas[30]));
						pst.setDouble(33,a1);
						pst.setDouble(34,a2);
						pst.setDouble(35,a3);
						pst.setInt(36,omcid);
					//	sb.append(a1+","+a2+","+a3+","+omcid);
					//	fw.write(sb.toString()+"\r\n");
					//	fw.flush();
						i++;
						pst.executeUpdate();
					}
					br.close();
				}else{
					System.out.println("文件格式不正确！");
				}
			//	fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			JdbcUtil.close(conn, pst, null);
		}
	
	}
	/**
	 * 将omc中数据计算成对应的KPI指标输出
	 * 注意：缺失值补充和分母零值处理
	 * @param filepath：omc输入数据路径
	 * @param filepath2：输出路径
	 */
	public static Map<Integer,List<String>>  calOmc(String filepath,String filepath2){
		File[] list = new File(filepath).listFiles();
		Map<Integer,List<String>> map =new LinkedHashMap<Integer,List<String>>();
		String sql="insert into t_omc values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
		Connection conn = JdbcUtil.getConnection();
		PreparedStatement pst=null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
			int i=1;
			for (File file2 : list) {
				//FileWriter fw=null;
				pst=conn.prepareStatement(sql);
				if(file2.getName().endsWith("csv")||file2.getName().endsWith("CSV")){
					//指定以GBK格式读文件
			        BufferedReader br =  new BufferedReader(new InputStreamReader(new FileInputStream(file2), "GBK"));
			        String line=br.readLine();
				//	String filename=file2.getName();
				//	fw = new FileWriter(filepath2+filename.substring(0,filename.length()-4),true);
					while((line=br.readLine())!=null){
					//	StringBuilder sb = new StringBuilder();
						String[] datas = line.split(",");
						//缺失值补0
						for(int j=0;j<54;j++){
							if("".equals(datas[j])){
								datas[j]=0+"";
							}
						}
						//将omc信息放入到map中
						List<String> list2 =new LinkedList<String>();
						String enodeb=Integer.parseInt(datas[2].substring(0, datas[2].length()-2),16)+"";
						String cellid_n=Integer.parseInt(datas[2].substring(datas[2].length()-2),16)+"";
						String cellid=enodeb+cellid_n;
						list2.add(cellid);
						String date="";
						if(datas[0].length()<18){
							date=sdf.format(sdf2.parse(datas[0]));
						}else{
							date=datas[0];
						}
						list2.add(date);
						map.put(i, list2);
						//计算OMC的KPI指标、将cellid、时间放入
						pst.setInt(1, i);
						pst.setString(2, cellid);//cellid 十六进制要转成十进制
						pst.setString(3, datas[0]);
						pst.setString(4, datas[1]);
						double a1=0.0,a2=0.0,a3=0.0;
						if(!"0".equals(datas[4])){
							a1=Double.parseDouble(datas[3])/Double.parseDouble(datas[4]);
						}else{
							a1=1;
						}
						if(!"0".equals(datas[13])){
							a2=Double.parseDouble(datas[12])/Double.parseDouble(datas[13]);
						}else{
							a2=1;
						}
						if((Integer.parseInt(datas[8])+Integer.parseInt(datas[4]))!=0){
							a3=Double.parseDouble(datas[8])/(Double.parseDouble(datas[8])+Double.parseDouble(datas[4]));
						}else{
							a3=0;
						}
						//sb.append(a1+","+a2+","+a1*a2+","+a3+",");//RRC连接建立成功率、E-RAB建立成功率、无线接通率、RRC连接重建比率
						pst.setDouble(5, a1);
						pst.setDouble(6, a2);
						pst.setDouble(7, a1*a2);
						pst.setDouble(8, a3);
						//初始上下文建立成功次数、遗留上下文个数、切出失败的E-RAB数、遗留E-RAB个数、切换入E-RAB数
						for(int j=9;j<14;j++){
							//sb.append(datas[j+8]+",");
							pst.setDouble(j, Double.parseDouble(datas[j+7]));
						}
						double a4=0.0,a5=0.0,a6=0.0,a7=0.0,a8=0.0,a9=0.0,a10=0.0,a11=0.0,a12=0.0,a13=0.0,a14=0.0,a15=0.0,a16=0.0,a17=0.0,a18=0.0,a19=0.0;
						if(!"0".equals(datas[22])){
							a4=Double.parseDouble(datas[21])/(Double.parseDouble(datas[22]));//LTE到2G切换成功率
						}else{
							a4=1;
						}
						if(!"0".equals(datas[24])){
							a5=Double.parseDouble(datas[23])/(Double.parseDouble(datas[24]));//LTE到3G切换成功率
						}else{
							a5=1;
						}
						//上行业务信道占用PRB平均数、下行业务信道占用PRB平均数、RRC连接平均数、RRC连接最大数、平均E-RAB数
						//sb.append(a4+","+a5+","+datas[30]+","+datas[31]+","+datas[5]+","+datas[6]+","+datas[14]+",");
						pst.setDouble(14, a4);
						pst.setDouble(15, a5);
						pst.setDouble(16, Double.parseDouble(datas[30]));
						pst.setDouble(17, Double.parseDouble(datas[31]));
						pst.setDouble(18, Double.parseDouble(datas[5]));
						pst.setDouble(19, Double.parseDouble(datas[6]));
						pst.setDouble(20, Double.parseDouble(datas[14]));
						if(!"0".equals(datas[33])){
							a6=Double.parseDouble(datas[32])/Double.parseDouble(datas[33]);//eNodeB寻呼拥塞率
						}
						//sb.append(a6+","+datas[53]+","+datas[9]+","+datas[10]+",");//PDCCH信道CCE占用率、有效RRC连接平均数、有效RRC连接最大数
						pst.setDouble(21, a6);
						pst.setDouble(22, Double.parseDouble(datas[53]));
						pst.setDouble(23, Double.parseDouble(datas[9]));
						pst.setDouble(24, Double.parseDouble(datas[10]));
						if(!"0".equals(datas[26])){
							a7=Double.parseDouble(datas[25])/Double.parseDouble(datas[26]);//小区用户面上行丢包率
						}
						if(!"0".equals(datas[28])){
							a8=Double.parseDouble(datas[27])/Double.parseDouble(datas[28]);//小区用户面下行丢包率
							a9=Double.parseDouble(datas[29])/Double.parseDouble(datas[28]);//小区用户面下行弃包率
						}
						//sb.append(a7+","+a8+","+a9+","+datas[7]+","+datas[11]+",");//RRC连接平均建立时长、E-RAB平均建立时长
						pst.setDouble(25, a7);
						pst.setDouble(26, a8);
						pst.setDouble(27, a9);
						pst.setDouble(28, Double.parseDouble(datas[7]));
						pst.setDouble(29, Double.parseDouble(datas[11]));
						if(!"0".equals(datas[35])){
							a10=Double.parseDouble(datas[34])/Double.parseDouble(datas[35]);//MAC层上行误块率
							a12=(Double.parseDouble(datas[38])-Double.parseDouble(datas[35]))/Double.parseDouble(datas[35]);//上行初始HARQ重传比率
						}
						if(!"0".equals(datas[37])){
							a11=Double.parseDouble(datas[36])/Double.parseDouble(datas[37]);//MAC层下行误块率
							a13= (Double.parseDouble(datas[15])-Double.parseDouble(datas[37]))/Double.parseDouble(datas[37]);//下行初始HARQ重传比率
						}
						
						//sb.append(a10+","+a11+","+a12+","+a13+",");
						pst.setDouble(30, a10);
						pst.setDouble(31, a11);
						pst.setDouble(32, a12);
						pst.setDouble(33, a13);
						int sum=0;
						for(int j=45;j<53;j++){
							sum+=Integer.parseInt(datas[j]);
						}
						//TM1占比、TM2占比、TM3占比、TM4占比、TM5占比、TM6占比、TM7占比、TM8占比
						if(sum!=0){
							for(int j=34;j<42;j++){
								//sb.append(Double.parseDouble(datas[j+12])/sum+",");
								pst.setDouble(j, Double.parseDouble(datas[j+11])/sum);
							}
						}else{
							for(int j=34;j<42;j++){
								// TM总次数为0时，填0
								//sb.append(0+",");
								pst.setDouble(j, 0);
							}
						}
						
						if(!"0".equals(datas[35])){
							a14=Double.parseDouble(datas[39])/Double.parseDouble(datas[35]);//上行QPSK编码比例
							a16=Double.parseDouble(datas[40])/Double.parseDouble(datas[35]);//上行MCS 16QAM编码比例
							a17=Double.parseDouble(datas[41])/Double.parseDouble(datas[35]);//上行MCS 64QAM编码比例
						}
						if(!"0".equals(datas[37])){
							a15=Double.parseDouble(datas[42])/Double.parseDouble(datas[37]);//下行 QPSK编码比例
							a18=Double.parseDouble(datas[43])/Double.parseDouble(datas[37]);//下行MCS 16QAM编码比例
							a19=Double.parseDouble(datas[44])/Double.parseDouble(datas[37]);//下行MCS 64QAM编码比例
						}
						pst.setDouble(42, a14);
						pst.setDouble(43, a15);
						pst.setDouble(44, a16);
						pst.setDouble(45, a17);
						pst.setDouble(46, a18);
						pst.setDouble(47, a19);
						//sb.append(a14+","+a15+","+a16+","+a17+","+a18+","+a19);
						i++;
						//fw.write(sb.toString()+"\r\n");
						//fw.flush();
						pst.executeUpdate();
					}
					br.close();
				}
				//fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			JdbcUtil.close(conn, pst, null);
		}
		return map;
	}
	/**	
	 * 先计算t_app中的发送/接收速率、内存占用率，及与omc关联
	 * 注意：缺失值处理和分母零值处理
	 * @param filepath：appdata路径
	 * @param filepath2:输出路径
	 * @param map：omc信息
	 */
	public  static void calApp2(String filepath, String filepath2,Map<Integer,List<String>> map){
		File[] list = new File(filepath).listFiles();
		int i=1;
		try {
			FileWriter fw = new FileWriter(filepath2,true);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (File file : list) {
				if(file.getName().endsWith(".csv")||file.getName().endsWith(".CSV")){
					//指定以GBK格式读文件
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
					String line=br.readLine();//标题栏跳过
					while((line=br.readLine())!=null){
						StringBuilder sb = new StringBuilder();
						int omcid=0;
						String[] datas = line.split(",");
						//判断数据中是否有空格
						for (String string : datas) {
							if("".equals(string)){
								continue;
							}
						}
						//判断网络类型是否为wifi
						if("wifi".equalsIgnoreCase(datas[16])||"0".equals(datas[19])||"-1".equals(datas[11])){
							continue;
						}
						//时间格式转化
						String d="";
						if(datas[22].length()<18){
							d=sdf.format(sdf2.parse(datas[22]));
						}else{
							if(datas[22].contains("-")){
								d=sdf.format(sdf3.parse(datas[22]));
							}else{
								d=datas[22];
							}
							
						}
						//获取appdata对应的omcid
						Set<Entry<Integer, List<String>>> entrySet = map.entrySet();
						for (Entry<Integer, List<String>> entry : entrySet) {
							List<String> value = entry.getValue();
							// 判断cellid是否相同
						//	String cellid=datas[19]+datas[11];
						//	if(value.get(0).equals(cellid)){//cellid相同
								// 判断时间是否在范围内
							long diff=sdf.parse(value.get(1)).getTime()-sdf.parse(d).getTime();
								if(diff<=900000&&diff>=0){
									omcid=entry.getKey();
									break;
								}
						//	}
						}
						String str=datas[19]+datas[11];
						//主键、imsi、时间、cellid
						//判断imsi是否为科学计数表达
						String imsi="";
						if(datas[1].contains("E")){
							imsi=new BigDecimal(datas[1]).toPlainString();
						}else{
							imsi=datas[1];
						}
						sb.append(i+","+imsi+","+d+","+str+",");
						//curTxBytes、rsrp、deviceBrand、memUsed、memTotal、、、、、、
						
						for(int j=5;j<33;j++){
							if(j>=25){
								sb.append(datas[j-2]+",");
							}else if(j<25){
								if(j==21){
									if("".equals(datas[j-3])||"null".equalsIgnoreCase(datas[j-3])){
										sb.append(datas[j-3]+",");
									}else{
										sb.append(Double.parseDouble(datas[j-3])/100+",");
									}
									
								}else{
									sb.append(datas[j-3]+",");
								}
							}
						}
						double a1=Double.parseDouble(datas[2])*8/1000;//sendrate
						double a2=Double.parseDouble(datas[14])*8/1000;//receiverate
						if("0".equals(datas[6])){
							continue;
						}
						double a3=Double.parseDouble(datas[5])/Double.parseDouble(datas[6]);//memoryrate
						//omcid
						sb.append(a1+","+a2+","+a3+","+omcid);
						fw.write(sb.toString()+"\r\n");
						fw.flush();
						i++;
					}
					br.close();
				}else{
					System.out.println("文件格式不正确！");
				}
				
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	/**
	 * 将omc中数据计算成对应的KPI指标输出
	 * 注意：缺失值补充和分母零值处理
	 * @param filepath：omc输入数据路径
	 * @param filepath2：输出路径
	 */
	public static Map<Integer,List<String>>  calOmc2(String filepath,String filepath2){
		File[] list = new File(filepath).listFiles();
		Map<Integer,List<String>> map =new LinkedHashMap<Integer,List<String>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
			int i=1;
			FileWriter fw = new FileWriter(filepath2,true);
			for (File file2 : list) {
				
				if(file2.getName().endsWith("csv")||file2.getName().endsWith("CSV")){
					//指定以GBK格式读文件
			        BufferedReader br =  new BufferedReader(new InputStreamReader(new FileInputStream(file2), "GBK"));
			        String line=br.readLine();
					while((line=br.readLine())!=null){
						StringBuilder sb = new StringBuilder();
						String[] datas = line.split(",");
						//缺失值补0
						for(int j=0;j<54;j++){
							if("".equals(datas[j])){
								datas[j]=0+"";
							}
						}
						//将omc信息放入到map中
						List<String> list2 =new LinkedList<String>();
						String enodeb=Integer.parseInt(datas[2].substring(0, datas[2].length()-2),16)+"";
						String cellid_n=Integer.parseInt(datas[2].substring(datas[2].length()-2),16)+"";
						String cellid=enodeb+cellid_n;
						list2.add(cellid);
						String date="";
						if(datas[0].length()<18){
							date=sdf.format(sdf2.parse(datas[0]));
						}else{
							date=datas[0];
						}
						list2.add(date);
						map.put(i, list2);
						//omcid、cellid、时间、地址
						sb.append(i+","+cellid+","+date+","+datas[1]+",");
						double a1=0.0,a2=0.0,a3=0.0;
						if(!"0".equals(datas[4])){
							a1=Double.parseDouble(datas[3])/Double.parseDouble(datas[4]);
						}else{
							a1=1;
						}
						if(!"0".equals(datas[13])){
							a2=Double.parseDouble(datas[12])/Double.parseDouble(datas[13]);
						}else{
							a2=1;
						}
						if((Integer.parseInt(datas[8])+Integer.parseInt(datas[4]))!=0){
							a3=Double.parseDouble(datas[8])/(Double.parseDouble(datas[8])+Double.parseDouble(datas[4]));
						}else{
							a3=0;
						}
						sb.append(a1+","+a2+","+a1*a2+","+a3+",");//RRC连接建立成功率、E-RAB建立成功率、无线接通率、RRC连接重建比率
						//初始上下文建立成功次数、遗留上下文个数、切出失败的E-RAB数、遗留E-RAB个数、切换入E-RAB数
						for(int j=8;j<13;j++){
							sb.append(datas[j+8]+",");
						}
						double a4=0.0,a5=0.0,a6=0.0,a7=0.0,a8=0.0,a9=0.0,a10=0.0,a11=0.0,a12=0.0,a13=0.0,a14=0.0,a15=0.0,a16=0.0,a17=0.0,a18=0.0,a19=0.0;
						if(!"0".equals(datas[22])){
							a4=Double.parseDouble(datas[21])/(Double.parseDouble(datas[22]));//LTE到2G切换成功率
						}else{
							a4=1;
						}
						if(!"0".equals(datas[24])){
							a5=Double.parseDouble(datas[23])/(Double.parseDouble(datas[24]));//LTE到3G切换成功率
						}else{
							a5=1;
						}
						//上行业务信道占用PRB平均数、下行业务信道占用PRB平均数、RRC连接平均数、RRC连接最大数、平均E-RAB数
						sb.append(a4+","+a5+","+datas[30]+","+datas[31]+","+datas[5]+","+datas[6]+","+datas[14]+",");
						if(!"0".equals(datas[33])){
							a6=Double.parseDouble(datas[32])/Double.parseDouble(datas[33]);//eNodeB寻呼拥塞率
						}
						sb.append(a6+","+datas[53]+","+datas[9]+","+datas[10]+",");//PDCCH信道CCE占用率、有效RRC连接平均数、有效RRC连接最大数
						if(!"0".equals(datas[26])){
							a7=Double.parseDouble(datas[25])/Double.parseDouble(datas[26]);//小区用户面上行丢包率
						}
						if(!"0".equals(datas[28])){
							a8=Double.parseDouble(datas[27])/Double.parseDouble(datas[28]);//小区用户面下行丢包率
							a9=Double.parseDouble(datas[29])/Double.parseDouble(datas[28]);//小区用户面下行弃包率
						}
						sb.append(a7+","+a8+","+a9+","+datas[7]+","+datas[11]+",");//RRC连接平均建立时长、E-RAB平均建立时长
						if(!"0".equals(datas[35])){
							a10=Double.parseDouble(datas[34])/Double.parseDouble(datas[35]);//MAC层上行误块率
							a12=(Double.parseDouble(datas[38])-Double.parseDouble(datas[35]))/Double.parseDouble(datas[35]);//上行初始HARQ重传比率
						}
						if(!"0".equals(datas[37])){
							a11=Double.parseDouble(datas[36])/Double.parseDouble(datas[37]);//MAC层下行误块率
							a13= (Double.parseDouble(datas[15])-Double.parseDouble(datas[37]))/Double.parseDouble(datas[37]);//下行初始HARQ重传比率
						}
						
						sb.append(a10+","+a11+","+a12+","+a13+",");
						int sum=0;
						for(int j=45;j<53;j++){
							sum+=Integer.parseInt(datas[j]);
						}
						//TM1占比、TM2占比、TM3占比、TM4占比、TM5占比、TM6占比、TM7占比、TM8占比
						if(sum!=0){
							for(int j=33;j<41;j++){
								sb.append(Double.parseDouble(datas[j+12])/sum+",");
								//pst.setDouble(j, Double.parseDouble(datas[j+12])/sum);
							}
						}else{
							for(int j=33;j<41;j++){
								// TM总次数为0时，填0
								sb.append(0+",");
							}
						}
						
						if(!"0".equals(datas[35])){
							a14=Double.parseDouble(datas[39])/Double.parseDouble(datas[35]);//上行QPSK编码比例
							a16=Double.parseDouble(datas[40])/Double.parseDouble(datas[35]);//上行MCS 16QAM编码比例
							a17=Double.parseDouble(datas[41])/Double.parseDouble(datas[35]);//上行MCS 64QAM编码比例
						}
						if(!"0".equals(datas[37])){
							a15=Double.parseDouble(datas[42])/Double.parseDouble(datas[37]);//下行 QPSK编码比例
							a18=Double.parseDouble(datas[43])/Double.parseDouble(datas[37]);//下行MCS 16QAM编码比例
							a19=Double.parseDouble(datas[44])/Double.parseDouble(datas[37]);//下行MCS 64QAM编码比例
						}
						sb.append(a14+","+a15+","+a16+","+a17+","+a18+","+a19);
						i++;
						fw.write(sb.toString()+"\r\n");
						fw.flush();
					}
					br.close();
				}
				
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static void dataToDataBase(String file,String file2){
		Connection conn = JdbcUtil.getConnection();
		String sql="LOAD DATA local INFILE '"+file+"' INTO TABLE "+"t_app"+" FIELDS TERMINATED BY ',';  ";
		String sql2="LOAD DATA local INFILE '"+file2+"' INTO TABLE "+"t_omc"+" FIELDS TERMINATED BY ',';  ";
		PreparedStatement pst=null;
		try {
			pst = conn.prepareStatement(sql);
			pst.executeUpdate();
			pst.close();
			pst=conn.prepareStatement(sql2);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtil.close(conn, pst, null);
		}
	}
		public static void main(String[] args) {
			Map<Integer, List<String>> map = calOmc2("F:/研一下/广西/data/omc_test/", "F:/研一下/广西/data/test/omc");
			calApp2("F:/研一下/广西/data/app_test/", "F:/研一下/广西/data/test/app", map);
			dataToDataBase("F:/研一下/广西/data/test/app", "F:/研一下/广西/data/test/omc");
		}
	}

