package com.bci;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
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
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;

/**
 * TODO: Document me!
 *
 * @author spoorthi.bs
 *
 */
public class BciChqInRtn extends ServiceLifecycle {

    @Override
    public void processSingleThreaded(ServiceData serviceData) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        List<FieldNameClass> fldList = null;

        List<String> fileHeaderPEN = new ArrayList<String>();
        List<String> fileHeaderUSD = new ArrayList<String>();
        List<String> lotHeaderPEN = new ArrayList<String>();
        List<String> lotHeaderUSD = new ArrayList<String>();
        List<String> individualPEN = new ArrayList<String>();
        List<String> individualUSD = new ArrayList<String>();
        List<String> additionalPEN = new ArrayList<String>();
        List<String> indAddPEN = new ArrayList<String>();
        List<String> indAddUSD = new ArrayList<String>();
        List<String> individualDPEN = new ArrayList<String>();
        List<String> individualDUSD = new ArrayList<String>();
        List<String> additionalUSD = new ArrayList<String>();
        List<String> lotEndControlPEN = new ArrayList<String>();
        List<String> lotEndControlUSD = new ArrayList<String>();
        List<String> fileEndControlPEN = new ArrayList<String>();
        List<String> fileEndControlUSD = new ArrayList<String>();
        List<String> finalList = new ArrayList<String>();
        List<String> finalListNuLL = new ArrayList<String>();

        String typReg = "";
        String sesType = "";
        String curr = "";
        String currUsd = "";
        String currPen = "";
        String appCode = "";
        String codeOrg = "";
        String immDest = "";
        String immOrg = "";
        String immDestName = "";
        String immOrgName = "";
        String originName = "";
        String free = "";
        String endOfContrlUsd = "";
        String endOfContrlPen = "";
        int valuecnt = 0;
        int indDflagPen = 0;
        int indDflagUsd = 0;
        String controlTot = "";
        String controlTotFC = "";
        String indRec = "";
        String additionalRecStr = "";
        String individualDRec = "";
        BigDecimal Amountsum = new BigDecimal("0.00");
        BigDecimal SumOfAmt = new BigDecimal("0.00");
        BigDecimal AmountsumPEN = new BigDecimal("0.00");
        BigDecimal AmountsumUSD = new BigDecimal("0.00");
        // BigDecimal sumAmountPen = new BigDecimal("0.00");
        // BigDecimal sumAmountUsd = new BigDecimal("0.00");
        String amountTotalFC = "";
        String amountTotalUsd = "";
        String amountTotalPen = "";
        String amountTotal = "";

        Double sumAmountPen = 0.0;
        Double sumAmountUsd = 0.0;
        String typRegLH = "";
        String arcNumLH = "";
        String lotType = "";
        // String tellerID = "";
        String lotNum = "";
        long controlTotal = 0;
        double amtTotal = 0;

        String typRegEC = "";
        String totRec = "";
        String totOp = "";
        String originEntityEC = "";
        String lotNumEC = "";

        long controlTotalPen = 0;
        long controlTotalUsd = 0;
        long controlTotalFCUsd = 0;
        long controlTotalFCPen = 0;

        String typRegFC = "";
        String totLot = "";
        String totRecord = "";
        int controlTotalFC = 0;
        // String totOpFC = "";
        double amtTotalFC = 0;
        String originEntityFC = "";
        String archiveNumber = "";
        String FileHeaderRec1 = "";
        String FileHeaderRec2 = "";
        int usdFlag = 0;
        int penFlag = 0;
        int totOpFCcnt = 0;
        int totLotCnt = 0;
        String totOpFC = "";
        int totOps = 0;
        int totSizeUsd = 0;
        int totSizePen = 0;
        int totOpsPen = 0;
        int totOpsUsd = 0;
        int totalLot = 0;
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
                "WITH STATUS EQ TO_BE_ADJUST_IN_R17 OR STATUS EQ TO_BE_ADJUST_IN_R18 OR STATUS EQ TO_BE_BALFAVOR_IN_R16 OR STATUS EQ TO_BE_CLEARED_IN_PARTIAL OR STATUS EQ TO_BE_RETURN_IN_ADDITIONAL_OFS OR STATUS EQ CLEARED_IN AND DATE EQ "
                        + today);
        // REMOVED STSTUS EQ TO_BE_RETURNED_ADDITIONAL_A
        System.out.println("before loop");
        for (String recid : recList) {
            System.out.println("entered loop");
            if (!recid.equals("")) {
                System.out.println("recid: " + recid);
                BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(
                        da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", recid));
                bciParamRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.FILEHEADER.CHQ"));
                System.out.println("recid: " + recid);
                String oeId = bciRec.getOeId().getValue();
                String status = bciRec.getStatus().getValue();
                System.out.println("status: " + status);
                String poID = bciRec.getPoId().getValue();

                String lcciCodeOrg = "";
                String settelmentDate = "";
                String reasonRet = "";
                String lcciDest = "";
                String amtTot = "";
                String nameCTArotated = "";
                String amtTotFC = "";
                String regCnt = "";

                if (!oeId.equals("")) {
                    PpOrderEntryRecord ppOrdEnt = new PpOrderEntryRecord(this);
                    try {
                        if (status.equals("TO_BE_CLEARED_IN_PARTIAL")) {
                            ppOrdEnt = new PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", oeId));
                            curr = ppOrdEnt.getTransactioncurrency().getValue();

                        }

                        if (status.equals("TO_BE_RETURN_IN_ADDITIONAL_OFS")) {
                            PpOrderEntryRecord ppOrdEntNAU = new PpOrderEntryRecord(
                                    da.getRecord("", "PP.ORDER.ENTRY", "$NAU", oeId));
                            ppOrdEnt = ppOrdEntNAU;
                            curr = ppOrdEnt.getTransactioncurrency().getValue();
                        }

                        if (status.equals("CLEARED_IN")) {
                            ppOrdEnt = new PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", oeId));
                            curr = ppOrdEnt.getTransactioncurrency().getValue();
                        }
                    } catch (Exception e) {

                    }

                    if (curr.equals("USD") && (!status.equals("CLEARED_IN"))) {
                        indDflagUsd = 1;
                    }
                    if (curr.equals("PEN") && (!status.equals("CLEARED_IN"))) {
                        indDflagPen = 1;
                    }
                    settelmentDate = ppOrdEnt.getExposuredate().getValue();
                    lcciCodeOrg = ppOrdEnt.getLocalRefField("L.CCI.CODE.ORIG").getValue();
                    lcciDest = ppOrdEnt.getLocalRefField("L.CCI.DESTINATION").getValue();
                    reasonRet = ppOrdEnt.getReturncode().getValue();

                    amtTot = ppOrdEnt.getTransactionamount().getValue();
                    nameCTArotated = ppOrdEnt.getBeneficiaryname().getValue();

                    // amtTotFC = ppOrdEnt.getTransactionamount().getValue();
                    // amtTotFC = amtTot;
                    regCnt = ppOrdEnt.getLocalRefField("L.REGIST.CONTR").getValue();
                } else {
                    PaymentOrderRecord payRec = new PaymentOrderRecord(da.getRecord("PAYMENT.ORDER", poID));
                    // hardcoded
                    curr = payRec.getPaymentCurrency().getValue();
                    settelmentDate = payRec.getPaymentExecutionDate().getValue();
                    lcciCodeOrg = payRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
                    reasonRet = payRec.getLocalRefField("L.REASON").getValue();
                    amtTot = payRec.getPaymentAmount().getValue();
                    // amtTotFC = amtTot;
                    lcciDest = payRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    nameCTArotated = payRec.getBeneficiaryName().getValue();
                    regCnt = payRec.getLocalRefField("L.REGIST.CONTR").getValue();
                }

                // SumOfAmt = new BigDecimal(amtTot);
                if (curr.equals("USD")) {
                    usdFlag = 1;
                    sumAmountUsd = sumAmountUsd + Double.parseDouble(amtTot);
                    SumOfAmt = new BigDecimal(sumAmountUsd);
                    AmountsumUSD = SumOfAmt;
                    Amountsum = AmountsumUSD;
                }

                if (curr.equals("PEN")) {
                    penFlag = 1;
                    sumAmountPen = sumAmountPen + Double.parseDouble(amtTot);
                    SumOfAmt = new BigDecimal(sumAmountPen);
                    AmountsumPEN = SumOfAmt;
                    Amountsum = AmountsumPEN;
                }

                System.out.println("CUR: " + curr);

                System.out.println("amOUNT FETCHED: " + amtTot);
                System.out.println("sUM: " + sumAmountPen);
                System.out.println("sUM: " + Amountsum);
                // System.out.println("curr: " + curr);

                fldList = bciParamRec.getFieldName();
                for (FieldNameClass fieldid : fldList) {
                    String fieldName = fieldid.getFieldName().getValue();

                    if (fieldName.equals("TYPE.REGISTER")) {
                        typReg = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("SESSION.TYPE.RETURNS")) {
                        sesType = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("APPLICATION.CODE")) {
                        appCode = fieldid.getFieldValue().getValue();
                    }
                    if (fieldName.equals("IMMEDIATE.DESTINATION.CODE.ECCRECE")) {
                        immDest = fieldid.getFieldValue().getValue();
                        // codeOrg = "0"+ codeOrg.substring(0, 3) + "0"+
                        // codeOrg.substring(3, 6);
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
                    if (fieldName.equals("IMMEDIATE.ORIGIN.NAME")) {
                        originName = fieldid.getFieldValue().getValue();
                    }
                }
                // System.out.println("parameter fetched");
                if (curr.equals("USD")) {
                    curr = currUsd;
                }
                if (curr.equals("PEN")) {
                    curr = currPen;
                }
                try {
                    immDestName = immDestName.substring(0, 23);
                    originName = originName.substring(0, 23);
                    // immOrg = lcciCodeOrg.
                    codeOrg = lcciCodeOrg;
                    System.out.println("code originator: " + codeOrg);
                    // codeOrg = "0"+ codeOrg.substring(0, 3) + "0"+
                    // codeOrg.substring(3, 6);
                } catch (Exception e) {

                }
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
                        .concat(StringUtils.leftPad(immDest, 8, "0")).concat(StringUtils.leftPad(codeOrg, 8, "0"))
                        .concat(today).concat(StringUtils.leftPad(archiveNumber, 2, "0")).concat(immDestName)
                        .concat(StringUtils.rightPad(originName, 23, "")).concat(StringUtils.leftPad(free, 16, ""));
                // System.out.println(FileHeaderRec);
                if (curr.equals(currPen)) {
                    int cntPen = fileHeaderPEN.size();
                    if (cntPen == 0) {
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

                        fileHeaderPEN.add(FileHeaderRec);
                    }

                }
                // System.out.println("fileHeaderPEN: " + fileHeaderPEN);
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
                System.out.println("ADDED FILE HEADER" + FileHeaderRec);
                // null file
                bciParamRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.LOTHEADER.CHQ"));
                // System.out.println("lot header: " +bciParamRec);
                // String originEntity = "0" + lcciCodeOrg.substring(0, 3) + "0"
                // + lcciCodeOrg.substring(3, 6);
                fldList = bciParamRec.getFieldName();
                for (FieldNameClass fieldid : fldList) {
                    String fieldName = fieldid.getFieldName().getValue();

                    if (fieldName.equals("TYPE.REGISTER")) {
                        typRegLH = fieldid.getFieldValue().getValue();
                    }
                    // System.out.println("ADDED TYPE REGISTER" +typRegLH);
                    if (fieldName.equals("LOT.TYPE.REJECTED")) {
                        lotType = fieldid.getFieldValue().getValue();
                    }
                    System.out.println("ADDED LOT TYPE" + lotType);
                }

                /*
                 * System.out.println("typRegLH"+typRegLH);
                 * System.out.println("archiveNumber: " +archiveNumber);
                 * System.out.println("lotType: " +lotType);
                 * System.out.println("today: " +today);
                 * System.out.println("settelmentDate: " +settelmentDate);
                 * System.out.println("lcciCodeOrg: " +lcciCodeOrg);
                 * System.out.println("CURRENCY: " +curr);
                 */

                try {
                    lcciCodeOrg = lcciCodeOrg.substring(0, 8);
                } catch (Exception e) {

                }

                String LotHeaderRec = typRegLH.concat(StringUtils.leftPad(archiveNumber, 2, "0")).concat(lotType)
                        .concat(StringUtils.rightPad(free, 58, "")).concat(today).concat(settelmentDate)
                        .concat(StringUtils.leftPad(lcciCodeOrg, 8, "0"))
                        .concat(StringUtils.leftPad(archiveNumber, 7, "0"));

                System.out.println("tHE Lot Header is " + LotHeaderRec);
                // System.out.println(LotHeaderRec);
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
                indRec = bciRec.getMapFieldType(0).getMapFieldVal(0).getValue();
                String controlTotPEN = "";
                String controlTotUSD = "";
                long controlTotLong = 0;

                if (status.equals("CLEARED_IN")) {

                    individualDRec = bciRec.getMapFieldType(1).getMapFieldVal(0).getValue();
                    if (curr.equals(currPen)) {
                        indDflagPen = 1;

                        controlTotUSD = indRec.substring(5, 13);
                        controlTotLong = Long.parseLong(controlTotUSD);
                        controlTotalUsd = controlTotalUsd + controlTotLong;
                        controlTotal = controlTotalUsd;
                        controlTotalFCUsd = controlTotalUsd;

                        individualDPEN.add(individualDRec);
                    }
                    if (curr.equals(currUsd)) {
                        indDflagUsd = 1;
                        controlTotPEN = indRec.substring(5, 13);
                        controlTotLong = Long.parseLong(controlTotPEN);
                        // change variable
                        controlTotalPen = controlTotalPen + controlTotLong;
                        controlTotal = controlTotalPen;
                        controlTotalFCPen = controlTotalPen;
                        individualDUSD.add(individualDRec);
                    }
                } else {
                    indRec = indRec.substring(0, 1).concat("26").concat(indRec.substring(3));
                    System.out.println("ADDED LOT HEADER" + LotHeaderRec);
                    System.out.println("checking");

                    System.out.println("indRec" + indRec);

                    // System.out.println("indRec" + indRec);

                    // System.out.println("individualDRec: " + individualDRec);
                    // System.out.println("ADDED indRec" + indRec);
                    // String additionalRecStr =
                    // bciRec.getMapFieldType(1).getMapFieldVal(0).getValue();
                    controlTot = lcciDest;
                    // System.out.println("ADDED controlTot" + controlTot);
                    // controlTotal = Integer.parseInt(controlTot);
                    // System.out.println("controlTot" + controlTot);
                    if (curr.equals(currPen)) {
                        controlTotPEN = indRec.substring(5, 13);
                        controlTotLong = Long.parseLong(controlTotPEN);
                        // change variable
                        controlTotalPen = controlTotalPen + controlTotLong;
                        controlTotal = controlTotalPen;
                        controlTotalFCPen = controlTotalPen;

                        indAddPEN.add(indRec);
                    }
                    if (curr.equals(currUsd)) {
                        controlTotUSD = indRec.substring(5, 13);
                        controlTotLong = Long.parseLong(controlTotUSD);
                        controlTotalUsd = controlTotalUsd + controlTotLong;
                        controlTotal = controlTotalUsd;
                        controlTotalFCUsd = controlTotalUsd;
                        indAddUSD.add(indRec);
                    }

                    String typRegAdd = "";
                    String addReg = "";
                    BciCceInterfaceParameterRecord bciParamRecAdd = new BciCceInterfaceParameterRecord(
                            da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.ADDITIONAL.CHQ"));
                    fldList = bciParamRecAdd.getFieldName();
                    for (FieldNameClass fieldid : fldList) {
                        String fieldName = fieldid.getFieldName().getValue();

                        if (fieldName.equals("TYPE.REGISTER")) {
                            typRegAdd = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("ADDITIONAL.REGISTRATION.CODE.A1")) {
                            addReg = fieldid.getFieldValue().getValue();
                        }

                    }

                    String orgTrans = lcciCodeOrg;

                    try {
                        orgTrans = "0" + orgTrans.substring(0, 3) + "0" + orgTrans.substring(3, 6);
                    } catch (Exception e) {

                    }
                    String entOrgTrans = lcciCodeOrg;
                    // System.out.println("orgTrans" + orgTrans);
                    // System.out.println("entOrgTrans" + entOrgTrans);
                    if ((status.equals("TO_BE_CLEARED_IN_PARTIAL"))
                            || (status.equals("TO_BE_RETURN_IN_ADDITIONAL_OFS"))) {
                        additionalRecStr = typRegAdd.concat(addReg).concat(StringUtils.rightPad(reasonRet, 3, ""))
                                .concat(StringUtils.leftPad(orgTrans, 15, "0")).concat(StringUtils.leftPad(free, 6, ""))
                                .concat(StringUtils.leftPad(entOrgTrans, 8, "0"))
                                .concat(StringUtils.rightPad(nameCTArotated, 44, ""))
                                .concat(StringUtils.leftPad(regCnt, 15, "0"));

                    }
                    else{
                        additionalRecStr = bciRec.getMapFieldType(1).getMapFieldVal(0).getValue();
                    }
                
                    if (curr.equals(currPen)) {

                        indAddPEN.add(additionalRecStr);
                    }
                    if (curr.equals(currUsd)) {
                        indAddUSD.add(additionalRecStr);
                    }

                    // System.out.println("STATUS: " + status);
                }

                System.out.println("ENTERED RECORD" + individualDPEN);
                // amtTotal = Double.parseDouble(amtTot) + amtTotal;
                originEntityEC = lcciCodeOrg;
                controlTotFC = lcciDest;
                // controlTotalFC = Integer.parseInt(controlTotFC) +
                // controlTotalFC;
                // System.out.println("ENTERED amount" + controlTotalFC);

                // amtTotalFC = Double.parseDouble(amtTotFC) + amtTotalFC;
                // System.out.println("ENTERED RECORD" +amtTotalFC);
                BciCceMappingFieldValuesRecord bciWriteRec = new BciCceMappingFieldValuesRecord(this);
                MapFieldTypeClass mapFldType = new MapFieldTypeClass();
                MapFieldTypeClass mapFldType2 = new MapFieldTypeClass();
                // System.out.println("ENTERED status1" +status);
                if ((status.equals("TO_BE_CLEARED_IN_PARTIAL")) || (status.equals("TO_BE_RETURN_IN_ADDITIONAL_OFS"))) {
                    status = "RETURN_IN_ADDITIONAL_A";
                    mapFldType2.setMapFieldType("ADDITIONAL-A");
                    mapFldType2.setMapFieldVal(additionalRecStr, 0);
                }
                if (status.equals("TO_BE_RETURN_IN_ADDITIONAL_A")) {
                    status = "RETURN_IN_ADDITIONAL_A";
                    mapFldType2.setMapFieldType("ADDITIONAL-A");
                    mapFldType2.setMapFieldVal(additionalRecStr, 0);
                }
                if (status.equals("TO_BE_ADJUST_IN_R17")) {
                    status = "ADJUST_IN_R17";
                    mapFldType2.setMapFieldType("ADDITIONAL-B");
                    mapFldType2.setMapFieldVal(additionalRecStr, 0);
                }
                if (status.equals("TO_BE_ADJUST_IN_R18")) {
                    status = "ADJUST_IN_R18";
                    mapFldType2.setMapFieldType("ADDITIONAL-B");
                    mapFldType2.setMapFieldVal(additionalRecStr, 0);
                }
                if (status.equals("TO_BE_BALFAVOR_IN_R16")) {
                    status = "BALFAVOR_IN_R16";
                    mapFldType2.setMapFieldType("ADDITIONAL-C");
                    mapFldType2.setMapFieldVal(additionalRecStr, 0);
                }
                if (status.equals("CLEARED_IN")) {
                    status = "CLEARED_IN_COMMISSION";
                    mapFldType2.setMapFieldType("INDIVIDUAL-D");
                    mapFldType2.setMapFieldVal(individualDRec, 0);
                }
                // System.out.println("ENTERED status2" + status);
                // System.out.println("Temp record: " + bciWriteRec);
                bciWriteRec.setOeId(oeId);
                bciWriteRec.setPoId(poID);
                mapFldType.setMapFieldType("INDIVIDUAL");
                mapFldType.setMapFieldVal(indRec, 0);
                bciWriteRec.setMapFieldType(mapFldType, 0);

                bciWriteRec.setMapFieldType(mapFldType2, 1);

                bciWriteRec.setDate(today);
                bciWriteRec.setTime(dtf.format(now));
                bciWriteRec.setStatus(status);
                System.out.println("Temp record: " + bciWriteRec);
                System.out.println("added mapFldType2:" + mapFldType2);
                try {
                    bciTab.write(recid, bciWriteRec);
                } catch (Exception e) {

                }
                System.out.println("indAddPEN before loop:" + indAddPEN);
                ///////////////////////
                // }
                // End of for loop-----

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
                System.out.println("indAddPEN after loop:" + indAddPEN);
                // String crtlTot = Integer.toString(controlTotal);
                /*
                 * String crtlTot = controlTot; String amountTotal =
                 * String.format("%.02f", Amountsum); //
                 * System.out.println("sUM WITH DECIMAL: " + amountTotal); int
                 * index = amountTotal.indexOf("."); amountTotal =
                 * amountTotal.substring(0,
                 * index).concat(amountTotal.substring(index + 1));
                 */
                // System.out.println("sUM AT THE END: " + amountTotal);
                // String amountTotal = new
                // BigDecimal(amtTotal).toPlainString();
                // decimal for amt

                // System.out.println(crtlTotFC);
                // System.out.println(amountTotalFC);
                bciParamRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.ENDCONTROL.CHQ"));

                fldList = bciParamRec.getFieldName();
                // System.out.println(fldList);
                for (FieldNameClass fieldid : fldList) {
                    String fieldName = fieldid.getFieldName().getValue();

                    if (fieldName.equals("TYPE.REGISTER")) {
                        typRegEC = fieldid.getFieldValue().getValue();
                    }
                    /*
                     * if (fieldName.equals("L.CCI.CODE.ORIGINATOR")) {
                     * originEntityEC = fieldid.getFieldValue().getValue(); }
                     */
                    // System.out.println(typRegEC);
                    // System.out.println(originEntityEC);
                }
                int totRecs = 0;
                totOps = 0;
                totalLot = 0;
                if (curr == currPen) {
                    totSizePen = (indAddPEN.size() / 2) + individualDPEN.size();
                    totOpsPen = totSizePen;
                    totOp = String.valueOf(totSizePen);
                    totRecs = lotHeaderPEN.size() + +(indAddPEN.size() / 2) + individualDPEN.size() + 1;
                    totalLot = lotHeaderPEN.size();
                }
                if (curr == currUsd) {
                    totSizeUsd = (indAddUSD.size() / 2) + individualDUSD.size();
                    totOpsUsd = totSizeUsd;
                    totOp = String.valueOf(totSizeUsd);

                    totRecs = lotHeaderUSD.size() + (indAddUSD.size() / 2) + individualDUSD.size() + 1;
                    totalLot = lotHeaderUSD.size();
                }
                // int totRecs = individualPEN.size() + individualUSD.size() +
                // 2;
                totRec = String.valueOf(totRecs);

                /*
                 * int totRecs = fileHeaderPEN.size() + lotHeaderPEN.size() +
                 * indAddPEN.size() + lotEndControlPEN.size() +
                 * fileEndControlPEN.size() + fileHeaderUSD.size() +
                 * lotHeaderUSD.size() + indAddUSD.size() +
                 * lotEndControlUSD.size() + fileEndControlUSD.size() + 2;
                 * totRec = String.valueOf(totRecs);
                 * 
                 * int totOps = fileHeaderPEN.size() + lotHeaderPEN.size() +
                 * indAddPEN.size() + lotEndControlPEN.size() +
                 * fileEndControlPEN.size() + fileHeaderUSD.size() +
                 * lotHeaderUSD.size() + indAddUSD.size() +
                 * lotEndControlUSD.size() + fileEndControlUSD.size() + 1;
                 */

                // totOp = String.valueOf(totOps);
                try {
                    originEntityEC = StringUtils.leftPad(originEntityEC, 8, "0");
                    originEntityEC = originEntityEC.substring(0, 8);
                } catch (Exception e) {

                }
                String EndCtrlRec = typRegEC.concat(StringUtils.leftPad(totRec, 10, "0"))
                        .concat(StringUtils.leftPad(crtlTot, 15, "0")).concat(StringUtils.leftPad(totOp, 15, "0"))
                        .concat(StringUtils.leftPad(amountTotal, 15, "0")).concat(StringUtils.leftPad(free, 23, ""))
                        .concat(StringUtils.leftPad(originEntityEC, 8, "0"))
                        .concat(StringUtils.leftPad(archiveNumber, 7, "0"));
                // System.out.println(EndCtrlRec);
                /*
                 * if (!fileHeaderPEN.equals("")) { int lotEndcnt =
                 * lotEndControlPEN.size(); // changed // if (lotEndcnt == 0) {
                 * if (curr == currPen) { endOfContrlPen = EndCtrlRec;
                 * totOpFCcnt = indAddPEN.size(); totLotCnt =
                 * lotHeaderPEN.size(); // lotEndControlPEN.add(EndCtrlRec); } }
                 * if (!fileHeaderUSD.equals("")) { int lotEndcnt =
                 * lotEndControlUSD.size(); // if (lotEndcnt == 0) if (curr ==
                 * currUsd) { endOfContrlUsd = EndCtrlRec; totOpFCcnt =
                 * indAddUSD.size(); totLotCnt = lotHeaderUSD.size(); //
                 * lotEndControlUSD.add(EndCtrlRec); } }
                 */
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
        }
        // ********file header

        bciParamRec = new BciCceInterfaceParameterRecord(
                da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.FILEHEADER.CHQ"));
        fldList = bciParamRec.getFieldName();
        for (FieldNameClass fieldid : fldList) {
            String fieldName = fieldid.getFieldName().getValue();

            if (fieldName.equals("TYPE.REGISTER")) {
                typReg = fieldid.getFieldValue().getValue();
            }
            if (fieldName.equals("SESSION.TYPE.RETURNS")) {
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

        }

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

        // System.out.println(EndCtrlRec);
        bciParamRec = new BciCceInterfaceParameterRecord(
                da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.FILEENDCONTROL.CHQ"));

        // String crtlTotFC = Long.toString(controlTotalFC);
        String crtlTotFC = controlTotFC;
        /*
         * String amountTotalFC = String.format("%.02f", Amountsum); int indexFC
         * = amountTotalFC.indexOf("."); amountTotalFC =
         * amountTotalFC.substring(0,
         * indexFC).concat(amountTotalFC.substring(indexFC + 1));
         */

        // String amountTotalFC = new BigDecimal(amtTotalFC).toPlainString();
        fldList = bciParamRec.getFieldName();
        for (FieldNameClass fieldid : fldList) {
            String fieldName = fieldid.getFieldName().getValue();
            // System.out.println(fieldName);
            if (fieldName.equals("TYPE.REGISTER")) {
                typRegFC = fieldid.getFieldValue().getValue();
            }
            if (fieldName.equals("L.CCI.CODE.ORIGINATOR")) {
                originEntityFC = fieldid.getFieldValue().getValue();
            }
            // System.out.println(typRegFC);
            // System.out.println(originEntityFC);
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
            int totRecords = fileHeaderPEN.size() + lotHeaderPEN.size() + (indAddPEN.size() / 2) + individualDPEN.size()
                    + lotEndControlPEN.size() + fileEndControlPEN.size() + 2;

            totRecord = String.valueOf(totRecords);

            String FileEndCtrlRec = typRegFC.concat(StringUtils.leftPad(totLot, 6, "0"))
                    .concat(StringUtils.leftPad(totRecord, 15, "0")).concat(StringUtils.leftPad(crtlTotFC, 15, "0"))
                    .concat(StringUtils.leftPad(totOpFC, 15, "0")).concat(StringUtils.leftPad(amountTotalPen, 15, "0"))
                    .concat(StringUtils.leftPad(free, 32, ""));
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
            int totRecords = fileHeaderUSD.size() + lotHeaderUSD.size() + (indAddUSD.size() / 2) + individualDUSD.size()
                    + lotEndControlUSD.size() + fileEndControlUSD.size() + 2;

            totRecord = String.valueOf(totRecords);

            String FileEndCtrlRec = typRegFC.concat(StringUtils.leftPad(totLot, 6, "0"))
                    .concat(StringUtils.leftPad(totRecord, 15, "0")).concat(StringUtils.leftPad(crtlTotFC, 15, "0"))
                    .concat(StringUtils.leftPad(totOpFC, 15, "0")).concat(StringUtils.leftPad(amountTotalUsd, 15, "0"))
                    .concat(StringUtils.leftPad(free, 32, ""));
            int fileEndcnt = fileEndControlUSD.size();
            if (fileEndcnt == 0) {
                fileEndControlUSD.add(FileEndCtrlRec);
            }
        }

        /*
         * 
         * int totRecords = fileHeaderPEN.size() + lotHeaderPEN.size() +
         * individualPEN.size() + lotEndControlPEN.size() +
         * fileEndControlPEN.size() + fileHeaderUSD.size() + lotHeaderUSD.size()
         * + individualUSD.size() + lotEndControlUSD.size() +
         * fileEndControlUSD.size() + 1; //
         * System.out.println("AMOUNT IN FILE CONTROL: " + amountTotalFC);
         * totRecord = String.valueOf(totRecords); totOpFC =
         * String.valueOf(totOpFCcnt); totLot = String.valueOf(totLotCnt);
         * 
         * String FileEndCtrlRec = ""; FileEndCtrlRec =
         * typRegFC.concat(StringUtils.leftPad(totLot, 6, "0"))
         * .concat(StringUtils.leftPad(totRecord, 15,
         * "0")).concat(StringUtils.leftPad(crtlTotFC, 15, "0"))
         * .concat(StringUtils.leftPad(totOpFC, 15,
         * "0")).concat(StringUtils.leftPad(amountTotalFC, 15, "0"))
         * .concat(StringUtils.leftPad(free, 32, ""));
         */
        // System.out.println("FILE CONTROL: " + FileEndCtrlRec);
        /*
         * if (curr.equals("USD")) { fileEndControlUSD.add(FileEndCtrlRec); } if
         * (curr.equals("PEN")) { fileEndControlPEN.add(FileEndCtrlRec); }
         */
        // System.out.println(FileEndCtrlRec);
        // System.out.println("FILE END: " + fileEndControlPEN);
        // System.out.println(FileEndCtrlRec);
        // if (!fileHeaderPEN.equals("")) {
        int fileEndcnt = fileEndControlPEN.size();

        // }
        int indDPenCnt = individualDPEN.size();
        int indDUsdCnt = individualDUSD.size();
        System.out.println("added indAddPEN D:" + individualDPEN);
        if ((penFlag == 1) && (indDflagPen == 1)) {
            finalList.addAll(fileHeaderPEN);
            finalList.addAll(lotHeaderPEN);
            finalList.addAll(indAddPEN);
            if (indDPenCnt > 0) {
                finalList.addAll(individualDPEN);
            }
            finalList.add(endOfContrlPen);
            finalList.addAll(fileEndControlPEN);
        }

        if ((usdFlag == 1) && (indDflagUsd == 1)) {
            finalList.addAll(fileHeaderUSD);
            finalList.addAll(lotHeaderUSD);
            finalList.addAll(indAddUSD);
            if (indDUsdCnt > 0) {
                finalList.addAll(individualDUSD);
            }
            finalList.add(endOfContrlUsd);
            finalList.addAll(fileEndControlUSD);
        }

        if ((penFlag == 1) && (indDflagPen == 0)) {
            finalList.addAll(fileHeaderPEN);
            finalList.addAll(lotHeaderPEN);
            finalList.addAll(indAddPEN);
            finalList.add(endOfContrlPen);
            finalList.addAll(fileEndControlPEN);
        }
        if ((usdFlag == 1) && (indDflagUsd == 0)) {
            finalList.addAll(fileHeaderUSD);
            finalList.addAll(lotHeaderUSD);
            finalList.addAll(indAddUSD);
            finalList.add(endOfContrlUsd);
            finalList.addAll(fileEndControlUSD);
        }

        System.out.println("FINAL ARRAY IS: " + finalList);
        try {
            immDestName = immDestName.substring(0, 23);

        } catch (Exception e) {

        }
        if (penFlag == 0) {

            String currPenNull = "1";
            FileHeaderRec1 = typReg.concat(sesType).concat(currPenNull).concat(StringUtils.rightPad(appCode, 3, ""))
                    .concat(immDest).concat(StringUtils.leftPad(immOrg, 8, "0")).concat(today)
                    .concat(StringUtils.leftPad(archiveNumber, 2, "0")).concat(immDestName)
                    .concat(StringUtils.leftPad(free, 16, ""));
            String FileEndCtrlRec1 = typRegFC.concat(StringUtils.leftPad(totLot, 6, "0"))
                    .concat(StringUtils.leftPad(totRecord, 15, "0")).concat(StringUtils.leftPad(crtlTotFC, 15, "0"))
                    .concat(StringUtils.leftPad(totOpFC, 15, "0")).concat(StringUtils.leftPad(amountTotalFC, 15, "0"))
                    .concat(StringUtils.leftPad(free, 32, ""));

            finalList.add(FileHeaderRec1);
            finalList.add(FileEndCtrlRec1);
            // System.out.println("printed PEN line");
            // System.out.println(fileEndControlPEN);
        }
        // }
        // if (!fileHeaderUSD.equals("")) {
        // int fileEndcnt = fileEndControlUSD.size();
        if (usdFlag == 0) {

            String currUsdNull = "2";
            FileHeaderRec2 = typReg.concat(sesType).concat(currUsdNull).concat(StringUtils.rightPad(appCode, 3, ""))
                    .concat(immDest).concat(StringUtils.leftPad(immOrg, 8, "0")).concat(today)
                    .concat(StringUtils.leftPad(archiveNumber, 2, "0")).concat(immDestName)
                    .concat(StringUtils.leftPad(free, 16, ""));
            String FileEndCtrlRec2 = typRegFC.concat(StringUtils.leftPad(totLot, 6, "0"))
                    .concat(StringUtils.leftPad(totRecord, 15, "0")).concat(StringUtils.leftPad(crtlTotFC, 15, "0"))
                    .concat(StringUtils.leftPad(totOpFC, 15, "0")).concat(StringUtils.leftPad(amountTotalFC, 15, "0"))
                    .concat(StringUtils.leftPad(free, 32, ""));

            finalList.add(FileHeaderRec2);
            finalList.add(FileEndCtrlRec2);

            // System.out.println("printed USD line");

            // System.out.println(fileEndControlUSD);
        }
        // System.out.println(finalList);
        String fileName = serviceData.getJobData(0);
        outPath = outPath + "/" + fileName;
        // System.out.println(outPath);
        FileWriter myWriter = null;
        File myObj = new File(outPath);
        try {
            myObj.createNewFile();
            myWriter = new FileWriter(outPath);
            for (String s : finalList) {
                try {
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

    }

}
