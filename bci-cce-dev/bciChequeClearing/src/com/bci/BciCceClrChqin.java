package com.bci;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bcicceclearingparam.BciCceClearingParamRecord;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.t24.api.records.acchargerequest.AcChargeRequestRecord;
import com.temenos.t24.api.records.acchargerequest.ChargeCodeClass;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * TODO: Document me!
 *
 * @author spoorthi.bs
 *
 */
public class BciCceClrChqin extends ServiceLifecycle {

    @Override
    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        BciCceClearingParamRecord bciParamRec = new BciCceClearingParamRecord(
                da.getRecord("EB.BCI.CCE.CLEARING.PARAM", "SYSTEM"));
        String inpath = bciParamRec.getChqInPath().getValue();
        // String logPath = bciParamRec.getLogfilePath().getValue();
        Date date = new Date(this);
        DatesRecord datesRec = date.getDates();
        String today = datesRec.getToday().getValue();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String name = serviceData.getProcessId();
        System.out.println(name);
        String fileData = serviceData.getJobData(0);
        String fileName = inpath + "/" + fileData;

        System.out.println(fileData);
        System.out.println(fileName);

        List<String> lines = Collections.emptyList();
        String regCnt = "";
        String accDebt = "";
        String accDebtVal = "";
        String amount = "";
        String accCred = "";
        String versionName = "";
        String codeOrg = "";
        String cheqNum = "";
        String presSqr = "";
        String issSqr = "";
        String typPlace = "";
        String drwDocTyp = "";
        String drwDocNum = "";
        String docType = "";

        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<String> itr = lines.iterator();
        PpOrderEntryRecord ppRec = new PpOrderEntryRecord(this);
        String currValue = "";
        while (itr.hasNext()) {
            String line = itr.next();
            BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(this);
            BciCceMappingFieldValuesTable bciTable = new BciCceMappingFieldValuesTable(this);

            if (line.substring(0, 1).equals("1")) {
                String currVal = line.substring(2, 3);
                if (currVal.equals("1")) {
                    currValue = "PEN";
                }
                if (currVal.equals("2")) {
                    currValue = "USD";
                }
            }
            if (line.substring(0, 1).equals("6")) {
                System.out.println(line);
                accDebtVal = line.substring(14, 32);
                accDebt = accDebtVal.substring(6, 16);
                /*
                 * int acctNo = Integer.parseInt(accDebt); acctNo = acctNo * 1;
                 * accDebt = String.valueOf(acctNo);
                 */

                BigInteger acctNo = new BigInteger(accDebt);
                accDebt = String.valueOf(acctNo);

             //   System.out.println("ACCOUNT NUMBER: " + accDebt);
                amount = line.substring(32, 47);

                // transactionAmount = line.substring(33, 48);
                try {
                    amount = amount.substring(0, 13).concat(".").concat(amount.substring(13, 15));
                    // System.out.println("amount after concat: " +
                    // transactionAmount);
                } catch (Exception e) {
                    // e.printStackTrace();
                }
               // System.out.println("amount: " + amount);
                regCnt = line.substring(79);
              //  System.out.println("regCnt: " + regCnt);
                codeOrg = line.substring(5, 13);
             //   System.out.println("codeOrg: " + codeOrg);
                cheqNum = line.substring(47, 56);
            //    System.out.println("cheqNum: " + cheqNum);
                presSqr = line.substring(56, 59);
            //    System.out.println("presSqr: " + presSqr);
                issSqr = line.substring(59, 62);
             //   System.out.println("issSqr: " + issSqr);
                typPlace = line.substring(64, 65);
             //   System.out.println("typPlace: " + typPlace);
                if (typPlace.equals("M")){
                    typPlace = "Y";
                }
                if (typPlace.equals("O")){
                    typPlace = "N";
                }
                
                
                drwDocTyp = line.substring(65, 66);
             //   System.out.println("drwDocTyp: " + drwDocTyp);
                drwDocNum = line.substring(66, 78);
             //   System.out.println("drwDocNum: " + drwDocNum);

                MapFieldTypeClass mapclass = new MapFieldTypeClass();
                mapclass.setMapFieldType("INDIVIDUAL");
                mapclass.setMapFieldVal(line, 0);
                bciRec.setMapFieldType(mapclass, 0);

                /*
                 * MapFieldTypeClass mapFldClInd = new MapFieldTypeClass();
                 * String first = line.substring(0, 99); String second =
                 * line.substring(99);
                 * mapFldClInd.setMapFieldType("INDIVIDUAL");
                 * mapFldClInd.setMapFieldVal(first, 0);
                 * mapFldClInd.setMapFieldVal(second, 1);
                 * bciRec.setMapFieldType(mapFldClInd, 0);
                 * 
                 * // }
                 * 
                 * 
                 * if (line.substring(0, 1).equals("7")) { MapFieldTypeClass
                 * mapFldClAdd = new MapFieldTypeClass(); String first =
                 * line.substring(0, 99); String second = line.substring(99);
                 * mapFldClAdd.setMapFieldType("ADDITIONAL");
                 * mapFldClAdd.setMapFieldVal(first, 0);
                 * mapFldClAdd.setMapFieldVal(second, 1);
                 * bciRec.setMapFieldType(mapFldClAdd, 0); }
                 */

                BciCceInterfaceParameterRecord accRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "CCE.ACCOUNT.NUMBER"));
                List<FieldNameClass> flLst = accRec.getFieldName();
                for (FieldNameClass fieldid : flLst) {
                    String fieldName = fieldid.getFieldName().getValue();
                    if (fieldName.equals("CREDIT.ACCT.NUMBER")) {
                        accCred = fieldid.getFieldValue().getValue();
                    }
                }
                BciCceInterfaceParameterRecord verRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.VERSION.NAMES"));
                List<FieldNameClass> flList = verRec.getFieldName();
                for (FieldNameClass fieldid : flList) {
                    String fieldName = fieldid.getFieldName().getValue();
                    if (fieldName.equals("PRESENTED.TRANS.CHQ.IN")) {
                        versionName = fieldid.getFieldValue().getValue();
                    }
                }
                String rateCode = "N";
                String DocName = "";
                BciCceInterfaceParameterRecord bciDocRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.DOCUMENT.TYPE"));
                List<FieldNameClass> flListDoc = bciDocRec.getFieldName();
                // System.out.println("flListDoc: " + flListDoc);
                for (FieldNameClass fieldid : flListDoc) {
                    docType = fieldid.getFieldValue().getValue();
                    // System.out.println("flListDoc: " + docType);
                    if (docType.equals(drwDocTyp)) {
                        DocName = fieldid.getFieldName().getValue();
                    }
                }
                System.out.println(accCred);
                ppRec.setDebitaccountnumber(accDebt);
                ppRec.setCreditaccountnumber(accCred);
                ppRec.setTransactioncurrency(currValue);
                ppRec.setTransactionamount(new
                BigDecimal(amount).toPlainString());
                System.out.println("amoutn after bigdecimal: "+amount);
                ppRec.getLocalRefField("L.CCI.CODE.ORIG").set(codeOrg);
                ppRec.setChequenumber(cheqNum);
                ppRec.getLocalRefField("L.PRESNTER.SQUR").set(presSqr);
                ppRec.getLocalRefField("L.CCI.DESTINATION").set(presSqr);
                ppRec.getLocalRefField("L.ISSUER.SQUR").set(issSqr);
                ppRec.getLocalRefField("L.SAME.OWNER").set(typPlace);
                ppRec.getLocalRefField("L.DRAWER.DOC.TYPE").set(DocName);
                ppRec.getLocalRefField("L.DRAWER.DOC.NUMBER").set(drwDocNum);
                ppRec.getLocalRefField("L.REGIST.CONTR").set(regCnt);
                ppRec.setOrderingaccount(accDebt);
                ppRec.setSendersreferencenumber(regCnt);
                ppRec.getLocalRefField("L.RATE.CODE").set(rateCode);
                System.out.println("ppRec:" +ppRec);
                // ppRec.getLocalRefField("L.CCI.CODE.ORIG").set("0990999");
                // ppRec.setChequestatus("CLEARED");
                bciRec.setDate(today);

                bciRec.setTime(dtf.format(now));
                bciRec.setStatus("TO_BE_RETURN_IN_ADDITIONAL_A");

                TransactionData txnData = new TransactionData();
                txnData.setFunction("INPUTT");
                txnData.setNumberOfAuthoriser("0");
                txnData.setSourceId("BCI.CCE.CHQ");
                txnData.setVersionId(versionName);
                // txnData.setCompanyId("PE0010001");
                txnData.setTransactionId("/");
                transactionData.add(txnData);
                records.add(ppRec.toStructure()); 

                try {
                    bciTable.write(regCnt, bciRec);
                } catch (Exception e) {

                }
            }
        }

    }

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        List<String> list = null;
        list = da.selectRecords("", "EB.BCI.CCE.CLEARING.PARAM", "", "WITH @ID EQ SYSTEM");
        System.out.println("list " + list);
        return list;
    }

}
