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

/**
 * TODO: Document me!
 *
 * @author spoorthi.bs
 *
 */
public class BciCceinUnwind extends ServiceLifecycle {


    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);

        BciCceClearingParamRecord paramrec = new BciCceClearingParamRecord(
                da.getRecord("EB.BCI.CCE.CLEARING.PARAM", "SYSTEM"));

        BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(this);
        MapFieldTypeClass mapFldType = new MapFieldTypeClass();
        Date date = new Date(this);
        DatesRecord datesRec = date.getDates();
        String today = datesRec.getToday().getValue();
        String inpath = paramrec.getInPath().getValue();
        String fileData = serviceData.getJobData(0);
        String fileName = inpath + "/" + fileData;

        List<String> lines = Collections.emptyList();
        String transCode = "";
        String regCnt = "";
        String recCnt = "";
        String oeId = "";
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
                bciRec = new BciCceMappingFieldValuesRecord(da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", regCnt));
                BciCceMappingFieldValuesTable bciTab = new BciCceMappingFieldValuesTable(this);
                individualline = line;

                oeId = bciRec.getOeId().getValue();

            }
            if (line.substring(0, 1).equals("7")) {
                recCnt = line.substring(79);
                if (individualline.substring(1, 5).equals("2600") && (individualline.equals(recCnt))) {
                    // Fetch transaction reference

                    PpOrderEntryRecord ppRec = new PpOrderEntryRecord(this);

                    PpOrderEntryRecord ppRecs = new PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", oeId));
                    String retCode = ppRecs.getReturncode().getValue();
                    String retDesc = ppRecs.getReturndescription().getValue();

                    ppRec.setStatus("UNWINDED_CHQOUT");
                    ppRec.setReturncode(retCode);
                    ppRec.setReturndescription(retDesc);
                                                         
                    SynchronousTransactionData txnData = new SynchronousTransactionData();
                    txnData.setFunction("INPUTT");
                    txnData.setNumberOfAuthoriser("0");
                    txnData.setSourceId("BCI.CCE.IN"); // need to change
                    txnData.setVersionId("PP.ORDER.ENTRY,"); // need to be given

                  //  txnData.setCompanyId("PE0010001");
                    txnData.setTransactionId(regCnt);
                    transactionData.add(txnData);
                    records.add(ppRec.toStructure());
                    bciRec.setStatus("UNWINDED_CHQOUT");
                    mapFldType.setMapFieldType("INDIVIDUAL A");
                    mapFldType.setMapFieldVal(individualline, 0);
                    bciRec.setMapFieldType(mapFldType, 0);

                    mapFldType.setMapFieldType("ADDITIONAL-A");
                    mapFldType.setMapFieldVal(line, 0);
                    bciRec.setMapFieldType(mapFldType, 0);
                }

                if (individualline.substring(1, 5).equals("2602")) {
                    String reason = line.substring(3, 6);
                    if (reason.equals("R17")) {
                        BciCceInChqRtnAdjBalanceRecord bciAdjRec = new BciCceInChqRtnAdjBalanceRecord(this);
                        bciAdjRec.setDate(today);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        bciAdjRec.setTime(dtf.format(now));
                        AdjMapFieldTypeClass adjClass = new AdjMapFieldTypeClass();
                        adjClass.setAdjMapFieldType("INDIVIDUAL B");
                        adjClass.setAdjMapFieldVal(individualline, 0);
                        bciAdjRec.setAdjMapFieldType(adjClass, 0);

                        adjClass.setAdjMapFieldType("ADDITIONAL B");
                        adjClass.setAdjMapFieldVal(line, 0);
                        bciAdjRec.setAdjMapFieldType(adjClass, 1);
                        bciAdjRec.setStatus("UNWIND_TO_BE_ADJUST_OUT_R17");
                        bciAdjRec.setDate(today);
                        bciAdjRec.setTime(dtf.format(now));
                    }
                    if (reason.equals("R18")) {
                        BciCceInChqRtnAdjBalanceRecord bciAdjRec = new BciCceInChqRtnAdjBalanceRecord(this);
                        bciAdjRec.setDate(today);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        bciAdjRec.setTime(dtf.format(now));
                        AdjMapFieldTypeClass adjClass = new AdjMapFieldTypeClass();
                        adjClass.setAdjMapFieldType("INDIVIDUAL B");
                        adjClass.setAdjMapFieldVal(individualline, 0);
                        bciAdjRec.setAdjMapFieldType(adjClass, 0);

                        adjClass.setAdjMapFieldType("ADDITIONAL B");
                        adjClass.setAdjMapFieldVal(line, 0);
                        bciAdjRec.setAdjMapFieldType(adjClass, 1);
                        bciAdjRec.setStatus("UNWIND_TO_BE_ADJUST_OUT_R18");
                        bciAdjRec.setDate(today);
                        bciAdjRec.setTime(dtf.format(now));
                    }
                }

                if (individualline.substring(1, 5).equals("2603")) {

                    BciCceInChqRtnBalFavourableRecord bciFavRec = new BciCceInChqRtnBalFavourableRecord(this);
                    bciFavRec.setDate(today);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    bciFavRec.setTime(dtf.format(now));
                    BalMapFieldTypeClass balClass = new BalMapFieldTypeClass();
                    balClass.setBalMapFieldType("INDIVIDUAL C");
                    balClass.setBalMapFieldVal(individualline, 0);
                    bciFavRec.setBalMapFieldType(balClass, 0);

                    balClass.setBalMapFieldType("ADDITIONAL C");
                    balClass.setBalMapFieldVal(line, 0);
                    bciFavRec.setBalMapFieldType(balClass, 1);
                    bciFavRec.setStatus("UNWIND_TO_BE_ADJUST_OUT_R16");
                    bciFavRec.setDate(today);
                    bciFavRec.setTime(dtf.format(now));
                }
                if (individualline.substring(1, 5).equals("2604")) {
                    bciRec.setStatus("UNWIND_TO_BE_RETURNED_COMMISSION_OUT");
                    mapFldType.setMapFieldType("INDIVIDUAL D");
                    mapFldType.setMapFieldVal(individualline, 0);
                    bciRec.setMapFieldType(mapFldType, 0);

                }
            }
        }

    }


}
