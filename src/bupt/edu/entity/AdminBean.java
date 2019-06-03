package bupt.edu.entity;

public class AdminBean {

	private Integer userid;
	private String username;
	private String userworkid;
	private String userphonenum;
	
	public AdminBean(){}
	
	public AdminBean(Integer userid,String username,String userworkid,String userphonenum){
		this.userid=userid;
		this.username=username;
		this.userworkid=userworkid;
		this.userphonenum=userphonenum;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserworkid() {
		return userworkid;
	}

	public void setUserworkid(String userworkid) {
		this.userworkid = userworkid;
	}

	public String getUserphonenum() {
		return userphonenum;
	}

	public void setUserphonenum(String userphonenum) {
		this.userphonenum = userphonenum;
	}
	
	@Override
	public String toString() {
		return this.userid+","+this.username+","+this.userworkid+","+this.userphonenum;
	}
}
