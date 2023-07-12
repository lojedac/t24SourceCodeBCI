package com.bci;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;

import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bcicceclearingparam.BciCceClearingParamRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterTable;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.tables.bcicceheadercounter.BciCceHeaderCounterRecord;
import com.temenos.t24.api.tables.bcicceheadercounter.BciCceHeaderCounterTable;
import com.temenos.t24.api.tables.bcicceheadercounter.NameClass;
import com.temenos.t24.api.tables.ebbcicceparticipantsbankname.EbBciCceParticipantsBankNameRecord;
import com.temenos.t24.api.records.teller.TellerRecord;

/**
 * TODO: Document me!
 *
 * @author spoorthi.bs
 *
 */
public class BciChqOutClr extends ServiceLifecycle {

    @Override
    public void processSingleThreaded(ServiceData serviceData) {
        try {

            // TODO Auto-generated method stub
            DataAccess da = new DataAccess(this);
            List<FieldNameClass> fldList = null;
            List<String> fileHeaderPEN = new ArrayList<String>();
            List<String> fileHeaderUSD = new ArrayList<String>();
            List<String> lotHeaderPEN = new ArrayList<String>();
            List<String> lotHeaderUSD = new ArrayList<String>();
            List<String> individualPEN = new ArrayList<String>();
            List<String> individualUSD = new ArrayList<String>();
            List<String> lotEndControlPEN = new ArrayList<String>();
            List<String> lotEndControlUSD = new ArrayList<String>();
            List<String> fileEndControlPEN = new ArrayList<String>();
            List<String> fileEndControlUSD = new ArrayList<String>();
            List<String> finalList = new ArrayList<String>();

            String FileHeaderRec1 = "";
            String FileHeaderRec2 = "";

            String typReg = "";
            String sesType = "";
            String curr = "";
            String appCode = "";
            String codeOrg = "";
            String immDest = "";
            String immOrg = "";
            String immDestName = "";
            String immOrgName = "";
            String free = "";
            String currPen = "";
            String currUsd = "";
            int valuecnt = 0;
            String originEntity = "";

            String typRegLH = "";
            String arcNumLH = "";
            String lotType = "";
            String tellerID = "";
            String lotNum = "";
            long controlTotal = 0;
            long controlTotalPen = 0;
            long controlTotalUsd = 0;
            double amtTotal = 0;

            String typRegEC = "";
            String totRec = "";
            String totOp = "";
            String originEntityEC = "";
            String lotNumEC = "";

            String typRegFC = "";
            String totLot = "";
            String totRecord = "";
            long controlTotalFCUsd = 0;
            long controlTotalFCPen = 0;
            String totOpFC = "";
            int totOps = 0;
            int totSizeUsd = 0;
            int totSizePen = 0;
            int totOpsPen = 0;
            int totOpsUsd = 0;
            int totalLot = 0;
            double amtTotalFC = 0;
            String originEntityFC = "";
            String archiveNumber = "";
            String crtlTotFC = "";
            int usdFlag = 0;
            int penFlag = 0;
            String bnkRef = "";
            BigDecimal Amountsum = new BigDecimal("0.00");
            BigDecimal AmountsumPEN = new BigDecimal("0.00");
            BigDecimal AmountsumUSD = new BigDecimal("0.00");
            BigDecimal SumOfAmt = new BigDecimal("0.00");
            // BigDecimal sumAmountPen = new BigDecimal("0.00");
            // BigDecimal sumAmountUsd = new BigDecimal("0.00");
            Double sumAmountPen = 0.00;
            Double sumAmountUsd = 0.00;

            String amountTotalFC = "";
            String amountTotalUsd = "";
            String amountTotalPen = "";
            String amountTotal = "";
            String endOfContrlUsd = "";
            String endOfContrlPen = "";
            Date date = new Date(this);
            DatesRecord datesRec = date.getDates();
            String today = datesRec.getToday().getValue();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            BciCceMappingFieldValuesTable bciTab = new BciCceMappingFieldValuesTable(this);
            BciCceInterfaceParameterRecord bciParamRec = new BciCceInterfaceParameterRecord(this);

            BciCceClearingParamRecord paramrec = new BciCceClearingParamRecord(
                    da.getRecord("EB.BCI.CCE.CLEARING.PARAM", "SYSTEM"));

            String outPath = paramrec.getChqOutPath().getValue();
            List<String> recList = da.selectRecords("", "EB.BCI.CCE.MAPPING.FIELD.VALUES", "",
                    "WITH STATUS EQ TO_BE_SENT_CHQOUT");

            for (String recid : recList) {
                BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(
                        da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", recid));
                System.out.println("recid" + recid);
                bciParamRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.FILEHEADER.CHQ"));

                // String oeId = bciRec.getOeId().getValue();
                // PpOrderEntryRecord ppOrdEnt = new
                // PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", oeId));
                // check TELLER id

                tellerID = bciRec.getPoId().getValue();
                // System.out.println("TELLER id" + tellerID);
                bnkRef = bciRec.getInRef().getValue();
                String oeID = bciRec.getOeId().getValue();
                TellerRecord telRec = new TellerRecord(da.getRecord("TELLER", tellerID));

                curr = telRec.getCurrency2().getValue();

                // System.out.println("ENTERED LOOP");
                immOrgName = "";
                fldList = bciParamRec.getFieldName();
                for (FieldNameClass fieldid : fldList) {
                    String fieldName = fieldid.getFieldName().getValue();

                    if (fieldName.equals("TYPE.REGISTER")) {
                        typReg = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("SESSION.TYPE.PRESENTED")) {
                        sesType = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("APPLICATION.CODE")) {
                        appCode = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("CODE.ORIGINATOR")) {
                        codeOrg = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("IMMEDIATE.DESTINATION.NAME")) {
                        immDestName = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("CURRENCY.PEN")) {
                        currPen = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("CURRENCY.USD")) {
                        currUsd = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("IMMEDIATE.DESTINATION.CODE.ECTRMP")) {
                        immDest = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("IMMEDIATE.ORIGIN.NAME")) {
                        immOrgName = fieldid.getFieldValue().getValue();
                    }

                }

                String bnkCodeLH = "";
                try {
                    immDestName = immDestName.substring(0, 23);
                    String immOrgVal = codeOrg;
                    // immOrg = "0" + StringUtils.substring(immOrgVal, 0, 3) +
                    // "0" +
                    // StringUtils.substring(immOrgVal, 3, 6);
                    immOrg = immOrgVal;
                    immOrgName = immOrgName.substring(0, 23);
                    // bnkCodeLH = codeOrg.substring(1, 4);
                } catch (Exception e) {

                }

                if (curr.equals("USD")) {
                    curr = currUsd;
                    usdFlag = 1;
                }
                if (curr.equals("PEN")) {
                    curr = currPen;
                    penFlag = 1;
                }

                // EbBciCceParticipantsBankNameRecord bnkNameLH = new
                // EbBciCceParticipantsBankNameRecord(
                // da.getRecord("EB.BCI.CCE.PARTICIPANTS.BANK.NAME",
                // bnkCodeLH));
                // System.out.println("bnkName: " +bnkName);
                // System.out.println("bnkCode: " +bnkCode);
                // immOrgName = bnkNameLH.getBankName().getValue();

                String todayFH = today + "-" + "FILEHEADER-CHQ";

                try {
                    BciCceHeaderCounterRecord archRec = new BciCceHeaderCounterRecord(
                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayFH));
                    archiveNumber = archRec.getName(0).getValue().getValue();

                } catch (Exception e) {

                }
                if (archiveNumber.equals("")) {
                    archiveNumber = "1";
                }

                String FileHeaderRec = typReg.concat(sesType).concat(curr).concat(StringUtils.rightPad(appCode, 3, ""))
                        .concat(StringUtils.leftPad(immDest, 8, "0")).concat(StringUtils.leftPad(immOrg, 8, "0"))
                        .concat(today).concat(StringUtils.leftPad(archiveNumber, 2, "0")).concat(immDestName)
                        .concat(StringUtils.rightPad(immOrgName, 3, "")).concat(StringUtils.rightPad(free, 16, ""));
                // add immediate origin name mapping
                if (curr.equals(currPen)) {
                    int cntPen = fileHeaderPEN.size();
                    if (cntPen == 0) {
                        BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                        NameClass fldclass = new NameClass();
                        fldclass.setName("ARCHNO");
                        fldclass.setName("ARCHNO");
                        valuecnt = valuecnt + 1;
                        String Value = String.valueOf(valuecnt);
                        fldclass.setValue(Value);
                        archwriteRec.setName(fldclass, 0);
                        BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                        try {
                            tblRec.write(todayFH, archwriteRec);
                        } catch (Exception e) {

                        }
                        fileHeaderPEN.add(FileHeaderRec);
                    }

                }
                if (curr.equals(currUsd)) {
                    int cntUsd = fileHeaderUSD.size();
                    if (cntUsd == 0) {
                        BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                        NameClass fldclass = new NameClass();
                        fldclass.setName("ARCHNO");
                        valuecnt = valuecnt + 1;
                        String Value = String.valueOf(valuecnt);
                        fldclass.setValue(Value);
                        archwriteRec.setName(fldclass, 0);
                        BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                        try {
                            tblRec.write(todayFH, archwriteRec);
                        } catch (Exception e) {

                        }
                        fileHeaderUSD.add(FileHeaderRec);
                    }

                }
                System.out.println("Added fileheader: " + fileHeaderPEN);
                bciParamRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.LOTHEADER.CHQ"));
                // CHECK SETTLEMENT DATE
                String settelmentDate = telRec.getExposureDate2().getValue();
                String originEntityLH = "";
                // originEntity = "0" + StringUtils.substring(originEntityVal,
                // 0, 3)
                // + "0" + StringUtils.substring(originEntityVal, 3, 6);
                fldList = bciParamRec.getFieldName();
                for (FieldNameClass fieldid : fldList) {
                    String fieldName = fieldid.getFieldName().getValue();

                    if (fieldName.equals("TYPE.REGISTER")) {
                        typRegLH = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("LOT.TYPE")) {
                        lotType = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("L.CCI.CODE.ORIG")) {
                        originEntityLH = fieldid.getFieldValue().getValue();
                    }

                }
                String LotHeaderRec = typRegLH.concat(StringUtils.leftPad(archiveNumber, 2, "0")).concat(lotType)
                        .concat(StringUtils.leftPad(free, 58, "")).concat(today).concat(settelmentDate)
                        .concat(StringUtils.rightPad(originEntityLH, 8, ""))
                        .concat(StringUtils.leftPad(archiveNumber, 7, "0"));

                if (curr.equals(currPen)) {
                    int lotPen = lotHeaderPEN.size();
                    if (lotPen == 0) {
                        lotHeaderPEN.add(LotHeaderRec);
                    }

                }
                if (curr.equals(currUsd)) {
                    int lotUsd = lotHeaderUSD.size();
                    if (lotUsd == 0) {
                        lotHeaderUSD.add(LotHeaderRec);
                    }

                }
                System.out.println("Added lotheader: " + lotHeaderPEN);

                String indRec = bciRec.getMapFieldType(0).getMapFieldVal(0).getValue();
                // String controlTot = "";
                String controlTotPEN = "";
                String controlTotUSD = "";
                long controlTotLong = 0;
                try {
                    // controlTot =
                    // telRec.getLocalRefField("L.CCI.DESTINATION").getValue();

                    /*
                     * controlTot = indRec.substring(5, 13);
                     * 
                     * controlTotLong = Long.parseLong(controlTot);
                     */
                } catch (Exception e) {

                }

                if (curr.equals(currPen)) {
                    controlTotPEN = indRec.substring(5, 13);
                    controlTotLong = Long.parseLong(controlTotPEN);
                    //change variable
                    controlTotalPen = controlTotalPen + controlTotLong;
                    controlTotal = controlTotalPen;
                    controlTotalFCPen = controlTotalPen;
                    individualPEN.add(indRec);
                }
                if (curr.equals(currUsd)) {
                    controlTotUSD = indRec.substring(5, 13);
                    controlTotLong = Long.parseLong(controlTotUSD);
                    controlTotalUsd = controlTotalUsd + controlTotLong;
                    controlTotal = controlTotalUsd;
                    controlTotalFCUsd = controlTotalUsd;
                    individualUSD.add(indRec);
                }
                ///// System.out.println("controlTotLong: " + controlTotLong);

                // amount
                // Amountsum = new BigDecimal("0.00");

                String amtTot = telRec.getNetAmount().getValue();
                if (curr.equals(currUsd)) {
                    usdFlag = 1;
                    sumAmountUsd = sumAmountUsd + Double.parseDouble(amtTot);
                    SumOfAmt = new BigDecimal(sumAmountUsd);
                    AmountsumUSD = SumOfAmt;
                    Amountsum = AmountsumUSD;
                }
                System.out.println("CUR: " + curr);
                if (curr.equals(currPen)) {
                    penFlag = 1;
                    sumAmountPen = sumAmountPen + Double.parseDouble(amtTot);
                    SumOfAmt = new BigDecimal(sumAmountPen);
                    AmountsumPEN = SumOfAmt;
                    Amountsum = AmountsumPEN;
                    ///// System.out.println("amtTotal: " + amtTot);

                    // String controlTotFC = controlTot;
                    ///// System.out.println("sumAmountPen: " + sumAmountPen);
                    ///// System.out.println("SumOfAmt: " + SumOfAmt);
                    ///// System.out.println("Amountsum: " + Amountsum);
                }

                BciCceMappingFieldValuesRecord bciWriteRec = new BciCceMappingFieldValuesRecord(this);
                MapFieldTypeClass mapFldType = new MapFieldTypeClass();
                System.out.println("Added individual: " + indRec);
                bciWriteRec.setPoId(tellerID);
                bciWriteRec.setInRef(bnkRef);
                bciWriteRec.setOeId(oeID);
                mapFldType.setMapFieldType("INDIVIDUAL");
                mapFldType.setMapFieldVal(indRec, 0);
                bciWriteRec.setMapFieldType(mapFldType, 0);
                bciWriteRec.setStatus("SENT_CHQOUT");
                bciWriteRec.setDate(today);
                bciWriteRec.setTime(dtf.format(now));

                // System.out.println("tEMP RECORD: " + bciWriteRec);
                try {
                    bciTab.write(recid, bciWriteRec);
                } catch (Exception e) {
                    ///// System.out.println("Template: " + bciWriteRec);
                }
                ////////////////////////

                /////////////////////////////
                String crtlTot = Long.toString(controlTotal);

                amountTotal = String.format("%.02f", Amountsum);
                ///// System.out.println("sUM WITH DECIMAL: " + amountTotal);
                int index = amountTotal.indexOf(".");

                // String amountTotal = Double.toString(amtTotal);
                try {
                    if (curr.equals(currPen)) {
                        amountTotal = amountTotal.substring(0, index).concat(amountTotal.substring(index + 1));
                        amountTotalPen = amountTotal;
                    }
                    if (curr.equals(currUsd)) {
                        amountTotal = amountTotal.substring(0, index).concat(amountTotal.substring(index + 1));
                        amountTotalUsd = amountTotal;
                    }
                    ///// System.out.println("amountTotal " + amountTotal);
                } catch (Exception e) {

                }

                // String amountTotalFC = Double.toString(amtTotalFC);

                bciParamRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.ENDCONTROL.CHQ"));
                fldList = bciParamRec.getFieldName();
                for (FieldNameClass fieldid : fldList) {
                    String fieldName = fieldid.getFieldName().getValue();

                    if (fieldName.equals("TYPE.REGISTER")) {
                        typRegEC = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("L.CCI.CODE.ORIG")) {
                        originEntityEC = fieldid.getFieldValue().getValue();
                        // originEntityEC = "0" +
                        // StringUtils.substring(originEntityECVal, 0, 3) + "0"
                        // +
                        // StringUtils.substring(originEntityECVal, 3, 6);
                    }

                }

                /*
                 * int totRecs = fileHeaderPEN.size() + lotHeaderPEN.size() +
                 * individualPEN.size() + lotEndControlPEN.size() +
                 * fileEndControlPEN.size() + fileHeaderUSD.size() +
                 * lotHeaderUSD.size() + individualUSD.size() +
                 * lotEndControlUSD.size() + fileEndControlUSD.size() + 2;
                 */

                int totRecs = 0;
                totOps = 0;
                totalLot = 0;
                if (curr == currPen) {
                    totSizePen = individualPEN.size();
                    totOpsPen = totSizePen;
                    totOp = String.valueOf(totSizePen);
                    totRecs = lotHeaderPEN.size() + individualPEN.size() + 1;
                    totalLot = lotHeaderPEN.size();
                }
                if (curr == currUsd) {
                    totSizeUsd = individualUSD.size();
                    totOpsUsd = totSizeUsd;
                    totOp = String.valueOf(totSizeUsd);
                    
                    totRecs = lotHeaderUSD.size() + individualUSD.size() + 1;
                    totalLot = lotHeaderUSD.size();
                }
                // int totRecs = individualPEN.size() + individualUSD.size() +
                // 2;
                totRec = String.valueOf(totRecs);

                // exclude
                // file
                // header
//                totOp = String.valueOf(totOps);

                String EndCtrlRec = typRegEC.concat(StringUtils.leftPad(totRec, 10, "0"))
                        .concat(StringUtils.leftPad(crtlTot, 15, "0")).concat(StringUtils.leftPad(totOp, 15, "0"))
                        .concat(StringUtils.leftPad(amountTotal, 15, "0")).concat(StringUtils.rightPad(free, 23, ""))
                        .concat(StringUtils.rightPad(originEntityEC, 8, ""))
                        .concat(StringUtils.leftPad(archiveNumber, 7, "0"));

                String endControlStr = EndCtrlRec;
                System.out.println("endControlStr: " + endControlStr);
                // if (!fileHeaderPEN.equals("")) {
                System.out.println("PEN FLAG: " + penFlag);
                if (penFlag == 1) {
                    System.out.println("currPen: " + currPen + "CUR: " + curr);
                    if (curr == currPen) {
                        endOfContrlPen = endControlStr;
                        // lotEndControlPEN.add(EndCtrlRec);
                        System.out.println("PEN FLAG2: " + penFlag);

                    }
                    System.out.println("EndCtrlRec 1: " + EndCtrlRec);

                }
                // if (!fileHeaderUSD.equals("")) {
                System.out.println("USD FLAG: " + usdFlag);
                if (usdFlag == 1) {

                    if (curr == currUsd) {
                        endOfContrlUsd = endControlStr;
                        // lotEndControlUSD.add(EndCtrlRec);
                        System.out.println("USD FLAG2: " + usdFlag);
                    }
                    System.out.println("EndCtrlRec 2: " + EndCtrlRec);
                    System.out.println("endOfContrlUsd: " + endOfContrlUsd);
                }

            }
            // System.out.println("Added lot end: " + lotEndControlPEN);
            bciParamRec = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.FILEENDCONTROL.CHQ"));
            fldList = bciParamRec.getFieldName();
            for (FieldNameClass fieldid : fldList) {
                String fieldName = fieldid.getFieldName().getValue();

                if (fieldName.equals("TYPE.REGISTER")) {
                    typRegFC = fieldid.getFieldValue().getValue();
                }
                if (fieldName.equals("L.CCI.CODE.ORIG")) {
                    originEntityFC = fieldid.getFieldValue().getValue();
                }

            }

            if (!fileHeaderPEN.equals("")) {
                
                int indexFC = amountTotalPen.indexOf(".");

                try {
                    amountTotalPen = amountTotalPen.substring(0, indexFC).concat(amountTotalPen.substring(indexFC + 1));

                } catch (Exception e) {

                }
                totOpFC = String.valueOf(totOpsPen);
                crtlTotFC = Long.toString(controlTotalFCPen);
                totLot = String.valueOf(totalLot);
                int totRecords = fileHeaderPEN.size() + lotHeaderPEN.size() + individualPEN.size()
                        + lotEndControlPEN.size() + fileEndControlPEN.size() + 2;

                totRecord = String.valueOf(totRecords);

                String FileEndCtrlRec = typRegFC.concat(StringUtils.leftPad(totLot, 6, "0"))
                        .concat(StringUtils.leftPad(totRecord, 15, "0")).concat(StringUtils.leftPad(crtlTotFC, 15, "0"))
                        .concat(StringUtils.leftPad(totOpFC, 15, "0"))
                        .concat(StringUtils.leftPad(amountTotalPen, 15, "0")).concat(StringUtils.leftPad(free, 32, ""));
                int fileEndcnt = fileEndControlPEN.size();
                if (fileEndcnt == 0) {
                    fileEndControlPEN.add(FileEndCtrlRec);
                }
            }

            if (!fileHeaderUSD.equals("")) {

                
                int indexFC = amountTotalUsd.indexOf(".");

                try {
                    amountTotalUsd = amountTotalUsd.substring(0, indexFC).concat(amountTotalUsd.substring(indexFC + 1));

                } catch (Exception e) {

                }
               // totOp = String.valueOf(totOpsUsd);
                totOpFC = String.valueOf(totOpsUsd);
                crtlTotFC = Long.toString(controlTotalFCUsd);
                totLot = String.valueOf(totalLot);
                int totRecords = fileHeaderUSD.size() + lotHeaderUSD.size() + individualUSD.size()
                        + lotEndControlUSD.size() + fileEndControlUSD.size() + 2;

                totRecord = String.valueOf(totRecords);

                String FileEndCtrlRec = typRegFC.concat(StringUtils.leftPad(totLot, 6, "0"))
                        .concat(StringUtils.leftPad(totRecord, 15, "0")).concat(StringUtils.leftPad(crtlTotFC, 15, "0"))
                        .concat(StringUtils.leftPad(totOpFC, 15, "0"))
                        .concat(StringUtils.leftPad(amountTotalUsd, 15, "0")).concat(StringUtils.leftPad(free, 32, ""));
                int fileEndcnt = fileEndControlUSD.size();
                if (fileEndcnt == 0) {
                    fileEndControlUSD.add(FileEndCtrlRec);
                }
            }
            System.out.println("Added file end: " + fileEndControlPEN);

            if (penFlag == 1) {
                finalList.addAll(fileHeaderPEN);
                finalList.addAll(lotHeaderPEN);
                finalList.addAll(individualPEN);
                finalList.add(endOfContrlPen);
                finalList.addAll(fileEndControlPEN);
            }

            if (usdFlag == 1) {
                finalList.addAll(fileHeaderUSD);
                finalList.addAll(lotHeaderUSD);
                finalList.addAll(individualUSD);
                finalList.add(endOfContrlUsd);
                finalList.addAll(fileEndControlUSD);
            }
            // System.out.println("fINAL sTRING: " + finalList);
            try {
                immDestName = immDestName.substring(0, 23);
            } catch (Exception e) {

            }
            if (penFlag == 0) {

                String currPenNull = "1";
                totLot = "";
                totRecord = "";
                crtlTotFC = "";
                totOpFC = "";
                amountTotalFC = "";
                FileHeaderRec1 = typReg.concat(sesType).concat(currPenNull).concat(StringUtils.rightPad(appCode, 3, ""))
                        .concat(immDest).concat(StringUtils.leftPad(immOrg, 8, "0")).concat(today)
                        .concat(StringUtils.leftPad(archiveNumber, 2, "0")).concat(immDestName)
                        .concat(StringUtils.rightPad(immOrgName, 3, "")).concat(StringUtils.leftPad(free, 16, ""));
                String FileEndCtrlRec1 = typRegFC.concat(StringUtils.leftPad(totLot, 6, "0"))
                        .concat(StringUtils.leftPad(totRecord, 15, "0")).concat(StringUtils.leftPad(crtlTotFC, 15, "0"))
                        .concat(StringUtils.leftPad(totOpFC, 15, "0"))
                        .concat(StringUtils.leftPad(amountTotalFC, 15, "0")).concat(StringUtils.leftPad(free, 32, ""));

                finalList.add(FileHeaderRec1);
                finalList.add(FileEndCtrlRec1);

            }
            if (usdFlag == 0) {
                totLot = "";
                totRecord = "";
                crtlTotFC = "";
                totOpFC = "";
                amountTotalFC = "";
                String currUsdNull = "2";
                FileHeaderRec2 = typReg.concat(sesType).concat(currUsdNull).concat(StringUtils.rightPad(appCode, 3, ""))
                        .concat(immDest).concat(StringUtils.leftPad(immOrg, 8, "0")).concat(today)
                        .concat(StringUtils.leftPad(archiveNumber, 2, "0")).concat(immDestName)
                        .concat(StringUtils.rightPad(immOrgName, 3, "")).concat(StringUtils.leftPad(free, 16, ""));
                String FileEndCtrlRec2 = typRegFC.concat(StringUtils.leftPad(totLot, 6, "0"))
                        .concat(StringUtils.leftPad(totRecord, 15, "0")).concat(StringUtils.leftPad(crtlTotFC, 15, "0"))
                        .concat(StringUtils.leftPad(totOpFC, 15, "0"))
                        .concat(StringUtils.leftPad(amountTotalFC, 15, "0")).concat(StringUtils.leftPad(free, 32, ""));

                finalList.add(FileHeaderRec2);
                finalList.add(FileEndCtrlRec2);

                // System.out.println("printed USD line");

                // System.out.println(fileEndControlUSD);
            }
            // String session = sessObj.isService();
            String fileName = serviceData.getJobData(0);
            outPath = outPath + "/" + fileName;
            FileWriter myWriter = null;
            File myObj = new File(outPath);
            try {
                myObj.createNewFile();
                myWriter = new FileWriter(outPath);
                for (String s : finalList) {
                    try {
                        System.out.println("Writing file: " + s);
                        myWriter.write(s + System.getProperty("line.separator"));
                    } catch (IOException e) {

                    }
                }
                myWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    myWriter.close();
                } catch (IOException e) {

                }

            }
        } catch (Exception e) {

        }

    }

}
