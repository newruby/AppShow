package bupt.edu.entity;

public class AllCellBean {

	private String enodbId;
	private String cellId_n;
	private Integer resport_nums;
	
	public AllCellBean(){}

	public String getEnodbId() {
		return enodbId;
	}

	public void setEnodbId(String enodbId) {
		this.enodbId = enodbId;
	}

	public String getCellId_n() {
		return cellId_n;
	}

	public void setCellId_n(String cellId_n) {
		this.cellId_n = cellId_n;
	}


	public Integer getResport_nums() {
		return resport_nums;
	}

	public void setResport_nums(Integer resport_nums) {
		this.resport_nums = resport_nums;
	}

	@Override
	public String toString() {
		return this.enodbId+"\t"+this.cellId_n+"\t"+this.resport_nums;
	}
}
