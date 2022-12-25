package dto;

import java.util.ArrayList;

public class PurchaseMaster {
    
    // Number of columns in PurchaseMaster: 27
    /* pmid, csid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
    supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
    netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */

    private String pmid="";
    private String csid="";
    private String compid="";
    private String invno="";
    private String invdt="";
    private String deliverynote="";
    private String payterm="";
    private String ordno="";
    private String orddt="";
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
    private String advance="";
    private String netamt02="";
    private String isopening="";
    private String amtpaid="";
    private String isactive="";
    private String remarks="";
    private ArrayList<PurchaseSub> psAl;

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getCsid() {
        return csid;
    }

    public void setCsid(String csid) {
        this.csid = csid;
    }

    public String getCompid() {
        return compid;
    }

    public void setCompid(String compid) {
        this.compid = compid;
    }

    public String getInvno() {
        return invno;
    }

    public void setInvno(String invno) {
        this.invno = invno;
    }

    public String getInvdt() {
        return invdt;
    }

    public void setInvdt(String invdt) {
        this.invdt = invdt;
    }

    public String getDeliverynote() {
        return deliverynote;
    }

    public void setDeliverynote(String deliverynote) {
        this.deliverynote = deliverynote;
    }

    public String getPayterm() {
        return payterm;
    }

    public void setPayterm(String payterm) {
        this.payterm = payterm;
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

    public String getIsopening() {
        return isopening;
    }

    public void setIsopening(String isopening) {
        this.isopening = isopening;
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

    public ArrayList<PurchaseSub> getPsAl() {
        return psAl;
    }

    public void setPsAl(ArrayList<PurchaseSub> psAl) {
        this.psAl = psAl;
    }
    
}
