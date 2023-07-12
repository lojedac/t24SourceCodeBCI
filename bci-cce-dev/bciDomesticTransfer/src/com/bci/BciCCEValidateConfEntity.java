package com.bci;


import java.util.ArrayList;
import java.util.List;

import com.temenos.t24.api.tables.ebbcicceparticipantsbankname.EbBciCceParticipantsBankNameRecord;


/**
 * TODO: Document me!
 *
 * @author Diego Maigualca
 *
 */
public class BciCCEValidateConfEntity {

    public static String validConfigTypeTranfer(EbBciCceParticipantsBankNameRecord ebNameRecord, String appcode ,  String typeTranfer) {
        
        String errorBol = "";
        
        String tri = "";
        String trm = "";
        String trt = "";
        try {
           tri  = ebNameRecord.getTurnoEctri().getValue();
        } catch (Exception e) {
            errorBol = "error";
        }
        try {
            trm = ebNameRecord.getTurnoEctrm().getValue();
        } catch (Exception e) {
            errorBol = "error";
        }
        try {
            trt = ebNameRecord.getTurnoEctrt().getValue();
        } catch (Exception e) {
            errorBol = "error";
        }
        
            if (appcode.equals("TRI") && tri.equals("NO")) {
                errorBol = "turno";
            } else if (appcode.equals("TRM") && trm.equals("NO")) {
                errorBol = "turno";
            } else if (appcode.equals("TRT") && trt.equals("NO")) {
                errorBol = "turno";
            } 

        return errorBol;
    }
    
    
    public static String validConfigConfirmados(EbBciCceParticipantsBankNameRecord ebNameRecord, String appcode ,  String typeTranfer) {
        String errorBol = "";


        try {
            String confirma = ebNameRecord.getTransConfirma().getValue();
            if (confirma.equals("NO") || confirma.equals("") || confirma.isEmpty()) {
                errorBol = "NO";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            errorBol = "NO";
        }
        
        return errorBol;
    }
    
    public static List<String> validTurnosConNo(EbBciCceParticipantsBankNameRecord ebNameRecord, String appcode) {
        
        List<String> lstTurno = new ArrayList<>();
        String tri = "";
        String trm = "";
        String trt = "";
        try {
           tri  = ebNameRecord.getTurnoEctri().getValue();
        } catch (Exception e) {
            
        }
        try {
            trm = ebNameRecord.getTurnoEctrm().getValue();
        } catch (Exception e) {
            
        }
        try {
            trt = ebNameRecord.getTurnoEctrt().getValue();
        } catch (Exception e) {
            
        }
           
            if (tri.equals("NO") || tri.equals("")) {
                lstTurno.add(appcode);
            } else if (trm.equals("NO") || trm.equals("")) {
                lstTurno.add(appcode);
            } else if (trt.equals("NO") || trt.equals("")) {
                lstTurno.add(appcode);
            } 
        
        return lstTurno;
        
    }
}
