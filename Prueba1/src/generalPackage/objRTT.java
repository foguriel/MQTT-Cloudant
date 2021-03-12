package generalPackage;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class objRTT {

	private Instant inicio;
	private Instant fin;
	private Integer opID;
	private TemperatureItem t = new TemperatureItem(0);
	private Long NR_RTT;
	private Long CP_RTT;
	
	public Instant getInicio() {
		return this.inicio;
	}
	
	public void setInicio(Instant ini) {
		this.inicio = ini;
	}
	
	public Instant getFin() {
		return this.fin;
	}
	
	public void setFin(Instant fi) {
		this.fin = fi;
	}
	
	public int getOpID() {
		return this.opID;
	}
	
	public void setOpID(int o) {
		this.opID = o;
	}
	
	public TemperatureItem getT() {
		return this.t;
	}
	
	public void setT(TemperatureItem te) {
		this.t = te;
	}
	
	public Long getNR_RTT() {
		return this.NR_RTT;
	}
	
	public void setNR_RTT(Long nr) {
		this.NR_RTT = nr;
	}
	
	public Long getCP_RTT() {
		return this.CP_RTT;
	}
	
	public void setCP_RTT(Long cp) {
		this.CP_RTT = cp;
	}
	
	public String toString() {
        return "ID " + this.getOpID() + " / NR RTT " + this.getNR_RTT() + " ms / CP RTT " + this.getCP_RTT() + " ms";
	}
	
}
