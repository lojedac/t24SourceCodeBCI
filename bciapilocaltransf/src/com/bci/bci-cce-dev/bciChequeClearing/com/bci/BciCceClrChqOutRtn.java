package com.bci;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bcicceclearingparam.BciCceClearingParamRecord;
import com.temenos.t24.api.tables.bcicceclearingparam.BciCceClearingParamTable;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.tables.bcicceinchqrtnadjbalance.BciCceInChqRtnAdjBalanceRecord;
import com.temenos.t24.api.tables.bcicceinchqrtnadjbalance.BciCceInChqRtnAdjBalanceTable;
import com.temenos.t24.api.tables.bcicceinchqrtnadjbalance.AdjMapFieldTypeClass;
import com.temenos.t24.api.tables.bcicceinchqrtnbalfavourable.BciCceInChqRtnBalFavourableRecord;
import com.temenos.t24.api.tables.bcicceinchqrtnbalfavourable.BciCceInChqRtnBalFavourableTable;
import com.temenos.t24.api.tables.bcicceinchqrtnbalfavourable.BalMapFieldTypeClass;
import com.temenos.t24.api.tables.ebbcihcceparticipantdir.EbBciHCceParticipantDirRecord;

/**
 * TODO: Document me!
 *
 * @author spoorthi.bs
 *
 */
public class BciCceClrChqOutRtn extends ServiceLifecycle {


    @Override
    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
        // TODO Auto-generated method stub

        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);

        BciCceClearingParamRecord paramrec = new BciCceClearingParamRecord(
                da.getRecord("EB.BCI.CCE.CLEARING.PARAM", "SYSTEM"));

       // BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(this);
        MapFieldTypeClass mapFldType = new MapFieldTypeClass();
        MapFieldTypeClass mapFldType2 = new MapFieldTypeClass();
        Date date = new Date(this);
        DatesRecord datesRec = date.getDates();
        String today = datesRec.getToday().getValue();
        String inpath = paramrec.getChqInPath().getValue();
        String fileData = serviceData.getJobData(0);
        String fileName = inpath + "/" + fileData;

        System.out.println("FILENAME: " + fileName);
        List<String> lines = Collections.emptyList();
        String transCode = "";
        String regCnt = "";
        String recCnt = "";
        String oeId = "";
        String bnkRef = "";
        String tellerId = "";
        String individualline = "";

        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<String> itr = lines.iterator();
        while (itr.hasNext()) {
            String line = itr.next();
            if (line.substring(0, 1).equals("6")) {
                transCode = line.substring(1, 5);
                regCnt = line.substring(79);
                System.out.println("regCnt: " + regCnt);               
                individualline = line;
            }
            if (line.substring(0, 1).equals("7")) {
               // recCnt = line.substring(79);
                //System.out.println("REC CNT: " + recCnt);
                String retCode = line.substring(1, 3);
                String retDesc = line.substring(3, 6);
                System.out.println("ENTERED ADD LOOP");
                // if (individualline.substring(1, 5).equals("2700") &&
                // (individualline.equals(recCnt))) {
                System.out.println("individualline: "+individualline);
                if (individualline.substring(1, 5).equals("2600")) {                    
                    BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(this);
                    bciRec = new BciCceMappingFieldValuesRecord(da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", regCnt));
                    

                    oeId = bciRec.getOeId().getValue();
                    bnkRef = bciRec.getInRef().getValue();
                    tellerId = bciRec.getPoId().getValue();
                    System.out.println("oeId: " + oeId);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    // Fetch transaction reference
                   // System.out.println("ENTERED 2700 LOOP");
                    PpOrderEntryRecord ppRec = new PpOrderEntryRecord(this);
                   // System.out.println("FILENAME: " + fileName);
                    ppRec = new PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", oeId));
                 //   System.out.println("oeId in add-A: " + oeId);
                    // retCode = ppRecs.getReturncode().getValue();
                    // String retDesc =
                    // ppRecs.getReturndescription().getValue();
                    //BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(this);
                    BciCceMappingFieldValuesTable bciTab = new BciCceMappingFieldValuesTable(this);
                    ppRec.setStatus("RETURNED");
                    ppRec.setReturncode(retCode);
                    ppRec.setReturndescription(retDesc);

                    TransactionData txnData = new TransactionData();
                    txnData.setFunction("INPUTT");
                    txnData.setNumberOfAuthoriser("0");
                    txnData.setSourceId("OFS.LOAD"); // need to change
                    txnData.setVersionId("PP.ORDER.ENTRY,"); // need to be given
                    txnData.setCompanyId("PE0010001");
                    txnData.setTransactionId("");
                    transactionData.add(txnData);
                    records.add(ppRec.toStructure());
                                       
                    System.out.println("ppRec: " + ppRec);
                    bciRec.setStatus("RETURNED_CHQOUT");

                    String servicename = serviceData.getProcessId();
                    if (servicename.contains("UNWIND")) {
                        bciRec.setStatus("CHEQUE_UNWIND");
                    }
                    mapFldType.setMapFieldType("INDIVIDUAL A");
                    mapFldType.setMapFieldVal(individualline, 0);
                    bciRec.setMapFieldType(mapFldType, 0);

                    mapFldType2.setMapFieldType("ADDITIONAL-A");
                    mapFldType2.setMapFieldVal(line, 0);
                    bciRec.setMapFieldType(mapFldType2, 1);

                    bciRec.setOeId(oeId);
                    bciRec.setInRef(bnkRef);
                    bciRec.setPoId(tellerId);
                    bciRec.setDate(today);
                    bciRec.setTime(dtf.format(now));

                    try {
                        System.out.println("regCnt: " + regCnt);
                        bciTab.write(regCnt, bciRec);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Writing R20 rec: " + bciRec);
                }

                if (individualline.substring(1, 5).equals("2602")) {
                    String reason = line.substring(3, 6);
                    if (reason.equals("R17")) {
                        BciCceInChqRtnAdjBalanceRecord bciAdjRec = new BciCceInChqRtnAdjBalanceRecord(this);
                        System.out.println("R17 temp: " + bciAdjRec);
                        bciAdjRec.setDate(today);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        bciAdjRec.setTime(dtf.format(now));
                        AdjMapFieldTypeClass adjClass = new AdjMapFieldTypeClass();
                        AdjMapFieldTypeClass adjClass2 = new AdjMapFieldTypeClass();
                        adjClass.setAdjMapFieldType("INDIVIDUAL B");
                        adjClass.setAdjMapFieldVal(individualline, 0);
                        bciAdjRec.setAdjMapFieldType(adjClass, 0);

                        adjClass2.setAdjMapFieldType("ADDITIONAL B");
                        adjClass2.setAdjMapFieldVal(line, 0);
                        bciAdjRec.setAdjMapFieldType(adjClass2, 1);
                        bciAdjRec.setStatus("TO_BE_ADJUST_OUT_R17_PARTIAL");
                        bciAdjRec.setDate(today);
                        bciAdjRec.setTime(dtf.format(now));
                        BciCceInChqRtnAdjBalanceTable bciAdjTab = new BciCceInChqRtnAdjBalanceTable(this);
                        try {

                            bciAdjTab.write(regCnt, bciAdjRec);
                        } catch (Exception e) {

                        }
                        System.out.println("Writing R17 rec: " + bciAdjRec);
                    }
                    if (reason.equals("R18")) {
                        BciCceInChqRtnAdjBalanceRecord bciAdjRec = new BciCceInChqRtnAdjBalanceRecord(this);
                        System.out.println("R18 temp: " + bciAdjRec);
                        bciAdjRec.setDate(today);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        bciAdjRec.setTime(dtf.format(now));
                        AdjMapFieldTypeClass adjClass = new AdjMapFieldTypeClass();
                        AdjMapFieldTypeClass adjClass2 = new AdjMapFieldTypeClass();
                        adjClass.setAdjMapFieldType("INDIVIDUAL B");
                        adjClass.setAdjMapFieldVal(individualline, 0);
                        bciAdjRec.setAdjMapFieldType(adjClass, 0);

                        adjClass2.setAdjMapFieldType("ADDITIONAL B");
                        adjClass2.setAdjMapFieldVal(line, 0);
                        bciAdjRec.setAdjMapFieldType(adjClass2, 1);
                        bciAdjRec.setStatus("TO_BE_ADJUST_OUT_R18_PARTIAL");
                        bciAdjRec.setDate(today);
                        bciAdjRec.setTime(dtf.format(now));
                        BciCceInChqRtnAdjBalanceTable bciAdjTab = new BciCceInChqRtnAdjBalanceTable(this);
                        try {
                            bciAdjTab.write(regCnt, bciAdjRec);
                        } catch (Exception e) {

                        }
                        System.out.println("Writing R18 rec: " + bciAdjRec);
                    }
                }

                if (individualline.substring(1, 5).equals("2603")) {

                    BciCceInChqRtnBalFavourableRecord bciFavRec = new BciCceInChqRtnBalFavourableRecord(this);
                    bciFavRec.setDate(today);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    bciFavRec.setTime(dtf.format(now));
                    BalMapFieldTypeClass balClass = new BalMapFieldTypeClass();
                    BalMapFieldTypeClass balClass2 = new BalMapFieldTypeClass();
                    balClass.setBalMapFieldType("INDIVIDUAL C");
                    balClass.setBalMapFieldVal(individualline, 0);
                    bciFavRec.setBalMapFieldType(balClass, 0);

                    balClass2.setBalMapFieldType("ADDITIONAL C");
                    balClass2.setBalMapFieldVal(line, 0);
                    bciFavRec.setBalMapFieldType(balClass2, 1);
                    bciFavRec.setStatus("TO_BE_ADJUST_OUT_R16_PARTIAL");
                    bciFavRec.setDate(today);
                    bciFavRec.setTime(dtf.format(now));
                    BciCceInChqRtnBalFavourableTable bciFavTab = new BciCceInChqRtnBalFavourableTable(this);
                    try {
                        bciFavTab.write(regCnt, bciFavRec);
                    } catch (Exception e) {

                    }
                    System.out.println("Writing R16 rec: " + bciFavRec);
                }
              /*  if (individualline.substring(1, 5).equals("2604")) {
                    BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(this);
                    bciRec.setStatus("TO_BE_CLEARED_COMMISSION");
                    mapFldType.setMapFieldType("INDIVIDUAL D");
                    mapFldType.setMapFieldVal(individualline, 0);
                    bciRec.setMapFieldType(mapFldType, 0);

                } */
                /*
                 * String servicename = serviceData.getProcessId(); //String
                 * status = serviceData.getJobData(2); if
                 * (servicename.contains("UNWIND")){
                 * mapRec.setStatus("RETURNED_UNWIND"); }
                 */
            }
        }

    
    }

}
