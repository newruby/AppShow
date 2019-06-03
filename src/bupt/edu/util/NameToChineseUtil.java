package bupt.edu.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class NameToChineseUtil {

	
	public static  Map<String,String> namesToChinese(){
		Map<String,String> map =new LinkedHashMap<String,String>();
	    map.put("rrc_connect_success_rate","RRC连接建立成功率") ; 
	    map.put("erab_connect_success_rate","E-RAB建立成功率") ;
	    map.put("wireless_connection_rate","无线接通率") ;               
	    map.put("rrc_link_reconstruction_ratio","RRC连接重建比率") ;  
	    map.put("initial_context_establishes_success_nums","初始上下文建立成功次数") ;
	    map.put("legacy_context_nums","遗留上下文个数") ;
		map.put("failed_cutout_erab_nums","切出失败的E-RAB数") ;
		map.put("left_erab_nums","遗留E-RAB个数") ;
		map.put("switchto_erab_nums","切换入E-RAB数") ;		         	                  
	    map.put("lte_2g_handover_success_rate","LTE到2G切换成功率") ;           
	    map.put("lte_3g_handover_success_rate","LTE到3G切换成功率") ;         
	    map.put("uplink_business_information_PRB_occupancy_nums","上行业务信道占用PRB平均数") ;
	    map.put("downward_business_information_PRB_occupancy_nums","下行业务信道占用PRB平均数") ;
	    map.put("rrc_connection_average_num","RRC连接平均数");
	    map.put("rrc_connection_maxnum","RRC连接最大数") ;
	    map.put("average_erab","平均E-RAB数") ;
	    map.put("enodeb_paging_rate","eNodeB寻呼拥塞率") ;
	    map.put("pdcch_channel_cce_occupancy_rate","PDCCH信道CCE占用率");
	    map.put("effective_rrc_connection_average","有效RRC连接平均数") ;
	    map.put("effective_rrc_connection_maxnum","有效RRC连接平均数") ;	 
	    map.put("upward_packet_loss_on_community","小区用户面上行丢包率") ;
		map.put("downward_packet_loss_on_community","小区用户面下行丢包率") ;
	    map.put("down_abandon_package_rate","小区用户面下行弃包率") ;                                
	    map.put("rrc_average_build_time","RRC连接平均建立时长") ;                 
	    map.put("erab_average_build_time","E-RAB平均建立时长") ;                 
	    map.put("mac_up_block_error_rate","MAC层上行误块率") ;                
	    map.put("mac_down_block_error_rate","MAC层下行误块率");             
	    map.put("upstream_initial_harq_retransmission_ratio","上行初始HARQ重传比率") ;
	    map.put("down_initial_harq_retransmission_ratio","下行初始HARQ重传比率");
	    map.put("tm1_ratio","TM1占比") ;
	    map.put("tm2_ratio","TM2占比");
	    map.put("tm3_ratio","TM3占比"); 
	    map.put("tm4_ratio","TM4占比");
	    map.put("tm5_ratio","TM5占比");
	    map.put("tm6_ratio","TM6占比");
	    map.put("tm7_ratio","TM7占比"); 
	    map.put("tm8_ratio","TM8占比");
	    map.put("upward_MCS_QPSK_coding_ratio","上行MCS QPSK编码比例") ;
	    map.put("downlink_MCS_QPSK_ratio","下行MCS QPSK编码比例") ;
		map.put("upward_MCS_16QAM_coding_ratio","上行MCS 16QAM编码比例") ;
		map.put("upward_MCS_64QAM_coding_ratio", "上行MCS 64QAM编码比例");
		map.put("downlink_MCS_16QAM_coding_ratio","下行MCS 16QAM编码比例") ;
		map.put("downlink_MCS_64QAM_coding_ratio","下行MCS 64QAM编码比例");
		map.put("access","接入类" );
	    map.put("maintain","保持类" );
	    map.put("mobility","移动类");
	    map.put("integri","完整类") ;
	    map.put("capacity","容量类") ;
	    map.put("comprehensive","综合类");
		return map;
		
	}
}
