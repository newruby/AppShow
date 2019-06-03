package bupt.edu.dataclean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import bupt.edu.util.JdbcUtil;
/**
 * 计算KPI指标量化值
 * @author lynatteluo
 *
 */
public class KpiHandler {

	Connection conn=null;
	PreparedStatement pst=null;
	ResultSet rs=null;
	/**
	 * 
	 * @param tablename omc原始表格：t_omc	有47个字段
	 * 前四个字段为omcid、cellid、recoreddate、地址
	 * 
	 */
	public void normalize(){
		conn=JdbcUtil.getConnection();
		//采用线性归一化方法
		String sql=null;
		//每列的最大值数组
		Double[] maxs= new Double[43];
		//每列最小值数组
		Double[] mins= new Double[43];
		QueryRunner qr = JdbcUtil.getQueryRunner();
		try {
			sql="select * from t_omc";
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			//获取元数据
			ResultSetMetaData metaData = rs.getMetaData();
			int count=metaData.getColumnCount();//获取总列数
			//System.out.println(count);
			for(int i=5;i<=count;i++){
				String name = metaData.getColumnName(i);//获取每列名称
				//获取列最大值
				sql="select max("+name+") from t_omc";
				Double max = qr.query(sql, new ScalarHandler<Double>());
				//将最大值加入到数组中
				maxs[i-5]=max;
				//获取列最大值
				sql="select min("+name+") from t_omc";
				Double min = qr.query(sql, new ScalarHandler<Double>());
				//将最小值加入到数组中
				mins[i-5]=min;
			}
			//线性归一化到【0.1，0.9】
				while(rs.next()){
					Object[] normavalue = new Object[43];
					for(int i=5;i<=count;i++){
						Double value=(Double) rs.getObject(i);
						double s=maxs[i-5]-mins[i-5];
						if(i==5||i==6||i==7||i==9||i==14||i==15){//
							if(s!=0){
								normavalue[i-5]=(value-mins[i-5])/s;
							}else{
								if(value.intValue()==1){
									normavalue[i-5]=1;
								}else{
									normavalue[i-5]=value;
								}
								
							}
						}else{
							if(s!=0){
								normavalue[i-5]=(maxs[i-5]-value)/s;
							}else{
								if(value.intValue()==0){
									normavalue[i-5]=1;
								}
								
							}
						}
						
					}
					//将t_omc表中数据进行归一化,omcid、cellid、时间
					sql="insert into t_omc_n values("+rs.getInt(1)+",'"+rs.getObject(2)+"','"+rs.getString(3)+"','"+rs.getString(4)+"',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
					qr.update(sql, normavalue);
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("omc表归一化出错了！");
		}finally{
			JdbcUtil.close(conn, pst, rs);
			//System.out.println(4);
		}
	}
	/**
	 * 
	 *归一化后的OMC表名t_omc_n(id和37个归一化后的字段)、五大类KPI量化值表名字t_score
	 *@param tablename:t_omc_n/t_score
	 * @return 返回每个字段的变异系数
	 */
	public List<Double> calAvgSd(String tablename){
		conn=JdbcUtil.getConnection();
		//存放每列的变异系数
		List<Double> list = new LinkedList<Double>();
		//存放每列OMC指标的平均值
		List<Double> listavg = new LinkedList<Double>();
		//存放每列OMC指标的标准差
		List<Double> listsd = new LinkedList<Double>();
		//获取表的列数
		String  sql="select * from "+tablename+";";
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			int count = rs.getMetaData().getColumnCount();//获取表的列数
			if("t_score".equalsIgnoreCase(tablename)){
				count=count-6;
			}
			for(int i=5;i<=count;i++){
				//获取每列名称
				String name = rs.getMetaData().getColumnName(i);
				//求取每列的平均值
				sql="select avg("+name+") from "+tablename+";";
				Double avg = JdbcUtil.getQueryRunner().query(sql, new ScalarHandler<Double>());
				listavg.add(avg);
				//求取每列的标准差
				sql="select std("+name+") from "+tablename+";";
				Double std = JdbcUtil.getQueryRunner().query(sql, new ScalarHandler<Double>());
				listsd.add(std);
			}
			
			//计算每列的变异系数
			for(int i=0;i<listsd.size();i++){
				if(listavg.get(i)>0.0){
					double s=listsd.get(i)/listavg.get(i);
					list.add(s);
				}else{
					list.add(0.0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			JdbcUtil.close(conn,pst,rs);
		}
		return list;
	}
	
	/**
	 * 计算五类指标量化值
	 * @param list OMC每个字段的变异系数
	 * list=calAvgSd("t_omc_n")
	 * 
	 */
	public void calKpiScore(List<Double> list){
		conn=JdbcUtil.getConnection();
		String sql=null;
		sql="select * from t_omc_n";
		QueryRunner qr = JdbcUtil.getQueryRunner();
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			int count = rs.getMetaData().getColumnCount();//获取表的列数
			//将每类指标变异系数归一化
			Double[] acess= new Double[3]; //接入类指标变异系数
			Double[] maintain= new Double[6];//保持类指标变异系数
			Double[] mobile= new Double[2];//移动类指标变异系数
			Double[] capacity = new Double[9];//容量类指标变异系数
			Double[] integrity = new Double[23];//完整类指标变异系数
			
			
			//变异系数归一化
			double asum=0.0,mosum=0.0,mainsum=0.0,insum=0.0,casum=0.0;
			for(int i=0;i<list.size();i++){
				if(i<3){
					acess[i]=list.get(i);
					asum+=list.get(i);
				}else if(i<9){
					maintain[i-3]=list.get(i);
					mainsum+=list.get(i);;
				}else if(i<11){
					mobile[i-9]=list.get(i);
					mosum+=list.get(i);
				}else if(i<20){
					capacity[i-11]=list.get(i);
					casum+=list.get(i);
				}else if(i<43){
					integrity[i-20]=list.get(i);
					insum+=list.get(i);
				}else{
					throw new RuntimeException("OMC字段超过了43个！");
				}
				
			}
			
			//得到归一化后变异系数
			List<Double> listnorm = new LinkedList<Double>();
			for(int i=0;i<list.size();i++){
				if(i<3){
					if(asum>0){
						listnorm.add(list.get(i)/asum);
					}else{
						listnorm.add(0.0);
					}
				}else if(i<9){
					if(mainsum>0){
						listnorm.add(list.get(i)/mainsum);
					}else{
						listnorm.add(0.0);
					}
				}else if(i<11){
					if(mosum>0){
						listnorm.add(list.get(i)/mosum);
					}else{
						listnorm.add(0.0);
					}
					
				}else if(i<20){
					if(casum>0){
						listnorm.add(list.get(i)/casum);
					}else{
						listnorm.add(0.0);
					}
					
				}else if(i<43){
					if(insum>0){
						listnorm.add(list.get(i)/insum);
					}else{
						listnorm.add(0.0);
					}
				}else{
					throw new RuntimeException("OMC字段超过了43个！");
				}
			}
			//统计变异系数中为零的指标
			int c1=0,c2=0,c3=0,c4=0,c5=0;
			List<Integer> list2 = new LinkedList<Integer>();
			for (int j=0;j<listnorm.size();j++) {
				if(!(listnorm.get(j)>0.0)){
					if(j<3){
						c1+=1;
					}else if(j<9){
						c2+=1;
					}else if(j<11){
						c3+=1;
					}else if(j<20){
						c4+=1;
					}else if(j<43){
						c5+=1;
					}
					list2.add(j);
				}
			}
			//计算归一化后OMC数据的五类KPI量化值
			while(rs.next()){
				double acess_n=0.0,mobile_n=0.0,maintian_n=0.0,integrity_n=0.0,capacity_n=0.0;
				double acess_n2=0.0,mobile_n2=0.0,maintian_n2=0.0,integrity_n2=0.0,capacity_n2=0.0;
				Object[] x= new Double[43];
				for(int i=0;i<count-4;i++){
					//获取每列的值
					double value = rs.getDouble(i+5);
					//计算接入类KPI指标值
					if(i<3){
						if(c1!=0){
							if(list2.contains(i)){
								acess_n2+=value*100;
								x[i]=value*100/3;
							}else{
								acess_n+=100*listnorm.get(i)*value;
								x[i]=100*listnorm.get(i)*value*(1-(double)c1/3);
							}
							
						}else{
							acess_n+=100*listnorm.get(i)*value;
							x[i]=100*listnorm.get(i)*value;
						}
						
					}else if(i<9){
						if(c2!=0){
							if(list2.contains(i)){
								maintian_n2+=value*100;
								x[i]=value*100/6;
							}else{
								maintian_n+=100*listnorm.get(i)*value;
								x[i]=100*listnorm.get(i)*value*(1-(double)c2/6);
							}
						
						}else{
							maintian_n+=100*listnorm.get(i)*value;
							x[i]=100*listnorm.get(i)*value;
						}
						
					}else if(i<11){
						if(c3!=0){
							if(list2.contains(i)){
								mobile_n2+=value*100;
								x[i]=value*100/2;
							}else{
								mobile_n+=100*listnorm.get(i)*value;
								x[i]=100*listnorm.get(i)*value*(1-(double)c3/2);
							}
						}else{
							mobile_n+=100*listnorm.get(i)*value;
							x[i]=100*listnorm.get(i)*value;
						}
						
					}else if(i<20){
						if(c4!=0){
							if(list2.contains(i)){
								capacity_n2+=value*100;
								x[i]=value*100/9;
							}else{
								capacity_n+=100*listnorm.get(i)*value;
								x[i]=100*listnorm.get(i)*value*(1-(double)c4/9);
							}
						}else{
							capacity_n+=100*listnorm.get(i)*value;
							x[i]=100*listnorm.get(i)*value;
						}
						
					}else if(i<43){
						if(c5!=0){
							if(list2.contains(i)){
								integrity_n2+=value*100;
								x[i]=value*100/23;
							}else{
								integrity_n+=100*listnorm.get(i)*value;
								x[i]=100*listnorm.get(i)*value*(1-(double)c5/23);
							}
							
						}else{
							integrity_n+=100*listnorm.get(i)*value;
							x[i]=100*listnorm.get(i)*value;
						}
						
					}
				}
				double a=acess_n2/3+acess_n*(1-(double)c1/3);
				double a2=maintian_n2/6+maintian_n*(1-(double)c2/6);
				double a3=mobile_n2/2+mobile_n*(1-(double)c3/2);
				double a4=capacity_n2/9+capacity_n*(1-(double)c4/9);
				double a5=integrity_n2/23+integrity_n*(1-(double)c5/23);
				
				String sql2 = "insert into t_score(scoreid,cellid,omctime,cellidname,access,maintain,"
						+ "mobility,integri,capacity) values("+rs.getInt(1)+",'"+rs.getObject(2)+"','"+rs.getString(3)+"','"+rs.getString(4)+"',?,?,?,?,?);";//往t_score表中插入KPI量化值
				//每条omc记录的KPI量化值
				Object[] kpivalue={a,a2,a3,a4,a5};
				//将量化后的KPI值插入到t_score表中
				qr.update(sql2, kpivalue);
				
				String sql3="insert into t_omc_score values("+rs.getInt(1)+",'"+rs.getObject(2)+"','"+rs.getString(3)+"','"+rs.getString(4)+"',"
						+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				qr.update(sql3, x);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("五类KPI量化有问题！");
		}finally {
			JdbcUtil.close(conn, pst, rs);
			//System.out.println(2);
		}
		
	}
	/**
	 * 计算综合指标的量化值
	 * @param list:未归一化的五类指标的变异系数列表
	 * list=calAvgSd("t_score")
	 */
	public void calAllKpi(List<Double> list){
		conn=JdbcUtil.getConnection();
		//变异系数归一化
		Iterator<Double> it = list.iterator();
		double sum=0.0;
		while(it.hasNext()){
			sum+=it.next();
		}
		String sql="select * from t_score";
		QueryRunner qr = JdbcUtil.getQueryRunner();
		try {
			pst = conn.prepareStatement(sql);
			rs=pst.executeQuery();
			String sql2="update t_score set comprehensive=? where scoreid=?";
			String sql3="update  t_score set access_n=?,maintain_n=?,mobility_n=?,integri_n=?,capacity_n=? where scoreid=?";
			Object[] x=new Double[5];
			while(rs.next()){
				double value=0.0;
				for(int i=0;i<5;i++){
					double colvalue=rs.getDouble(i+5);
					if(list.contains(new Double(0.0))){
						if(i==2){
							value+=colvalue/5;
							x[i]=colvalue/5;
						}else{
							value+=(list.get(i)*colvalue/sum)*(0.8);
							x[i]=(list.get(i)*colvalue/sum)*0.8;
						}
					}else{
						value+=(list.get(i)*colvalue/sum);
						x[i]=list.get(i)*colvalue/sum;
					}
					
					
				}
				qr.update(sql2, value,rs.getInt(1));
				qr.update(sql3, x[0],x[1],x[2],x[3],x[4],rs.getInt(1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("综合指标量化值求解出错！");
		}finally{
			JdbcUtil.close(conn, pst, rs);
			//System.out.println(1);
		}
		
	}
	public static void main(String[] args) {
		
		KpiHandler handler = new KpiHandler();
		//将t_omc中数据归一化
		handler.normalize();
		//计算归一化后t_omc每类指标的变异系数
		List<Double> list_omc_n = handler.calAvgSd("t_omc_n");
		//计算五类指标量化值
		handler.calKpiScore(list_omc_n);
		//计算五类指标变异系数
		List<Double> list_score = handler.calAvgSd("t_score");
		//计算综合指标量化值
		handler.calAllKpi(list_score);
	}
}

