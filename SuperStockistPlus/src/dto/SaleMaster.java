package dto;

import java.util.ArrayList;

public class SaleMaster {
    
    // Number of columns in SaleMaster: 27
    /* salemid, compid, distid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
    supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
    netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */

    private String salemid="";
    private String compid="";
    private String distid="";
    private String saledt="";
    private String ordno="";
    private String orddt="";
    private String deliverynote="";
    private String paymentterm="";
    private String transporter="";
    private String vehicleno="";
    private String supplydt="";
    private String netqty="";
    private String netamt="";
    private String nettaxableamt="";
    private String netcgst="";
    private String netsgst="";
    private String netigst="";
    private String nettotal="";
    private String roundoff="";
    private String netamt01="";
    private String displaynote="";
    private String displayamt="";
    private String advance="";
    private String netamt02="";
    private String amtpaid="";
    private String isactive="";
    private String remarks="";
    private ArrayList<SaleSub> ssAl;

    public String getSalemid() {
        return salemid;
    }

    public void setSalemid(String salemid) {
        this.salemid = salemid;
    }

    public String getCompid() {
        return compid;
    }

    public void setCompid(String compid) {
        this.compid = compid;
    }

    public String getDistid() {
        return distid;
    }

    public void setDistid(String distid) {
        this.distid = distid;
    }

    public String getSaledt() {
        return saledt;
    }

    public void setSaledt(String saledt) {
        this.saledt = saledt;
    }

    public String getOrdno() {
        return ordno;
    }

    public void setOrdno(String ordno) {
        this.ordno = ordno;
    }

    public String getOrddt() {
        return orddt;
    }

    public void setOrddt(String orddt) {
        this.orddt = orddt;
    }

    public String getDeliverynote() {
        return deliverynote;
    }

    public void setDeliverynote(String deliverynote) {
        this.deliverynote = deliverynote;
    }

    public String getPaymentterm() {
        return paymentterm;
    }

    public void setPaymentterm(String paymentterm) {
        this.paymentterm = paymentterm;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }

    public String getVehicleno() {
        return vehicleno;
    }

    public void setVehicleno(String vehicleno) {
        this.vehicleno = vehicleno;
    }

    public String getSupplydt() {
        return supplydt;
    }

    public void setSupplydt(String supplydt) {
        this.supplydt = supplydt;
    }

    public String getNetqty() {
        return netqty;
    }

    public void setNetqty(String netqty) {
        this.netqty = netqty;
    }

    public String getNetamt() {
        return netamt;
    }

    public void setNetamt(String netamt) {
        this.netamt = netamt;
    }

    public String getNettaxableamt() {
        return nettaxableamt;
    }

    public void setNettaxableamt(String nettaxableamt) {
        this.nettaxableamt = nettaxableamt;
    }

    public String getNetcgst() {
        return netcgst;
    }

    public void setNetcgst(String netcgst) {
        this.netcgst = netcgst;
    }

    public String getNetsgst() {
        return netsgst;
    }

    public void setNetsgst(String netsgst) {
        this.netsgst = netsgst;
    }

    public String getNetigst() {
        return netigst;
    }

    public void setNetigst(String netigst) {
        this.netigst = netigst;
    }

    public String getNettotal() {
        return nettotal;
    }

    public void setNettotal(String nettotal) {
        this.nettotal = nettotal;
    }

    public String getRoundoff() {
        return roundoff;
    }

    public void setRoundoff(String roundoff) {
        this.roundoff = roundoff;
    }

    public String getNetamt01() {
        return netamt01;
    }

    public void setNetamt01(String netamt01) {
        this.netamt01 = netamt01;
    }

    public String getDisplaynote() {
        return displaynote;
    }

    public void setDisplaynote(String displaynote) {
        this.displaynote = displaynote;
    }

    public String getDisplayamt() {
        return displayamt;
    }

    public void setDisplayamt(String displayamt) {
        this.displayamt = displayamt;
    }

    public String getAdvance() {
        return advance;
    }

    public void setAdvance(String advance) {
        this.advance = advance;
    }

    public String getNetamt02() {
        return netamt02;
    }

    public void setNetamt02(String netamt02) {
        this.netamt02 = netamt02;
    }

    public String getAmtpaid() {
        return amtpaid;
    }

    public void setAmtpaid(String amtpaid) {
        this.amtpaid = amtpaid;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ArrayList<SaleSub> getSsAl() {
        return ssAl;
    }

    public void setSsAl(ArrayList<SaleSub> ssAl) {
        this.ssAl = ssAl;
    }
    
}
