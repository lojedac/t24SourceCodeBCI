package com.bci;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.pporderentry.DebitchargecomponentClass;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
														
import com.temenos.t24.api.records.tsaservice.TsaServiceRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bcicceclearingparam.BciCceClearingParamRecord;
import com.temenos.t24.api.tables.bcicceheadercounter.BciCceHeaderCounterRecord;
import com.temenos.t24.api.tables.bcicceheadercounter.BciCceHeaderCounterTable;
import com.temenos.t24.api.tables.bcicceheadercounter.NameClass;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
/**
 *
 * @author anagha.s
 *         ----------------------------------------------------------------------------------------------------------------
 *         Description : This routine is used to generate the outward Confirmed
 *         file in the specified path.
 * 
 *         Developed By : Anagha Shastry
 *
 *         Development Reference :
 *         IDD-G2-013_BCI_Interface_Interbank_Transfers_Outward_Inward
 *
 *         Attached To : BATCH>BciCCEOutClrConf
 *
 *         Attached As : Batch routine
 *         -----------------------------------------------------------------------------------------------------------------
 *         Input Parameter: ---------------* Argument#1 : N/A Argument#2 : N/A
 *         Argument#3 : N/A -----------------* Output Parameter:
 *         ----------------* Argument#4 : N/A Argument#5 : N/A Argument#6 : N/A
 *         -----------------------------------------------------------------------------------------------------------------
 *         M O D I F I C A T I O N S ***************************
 *         -----------------------------------------------------------------------------------------------------------------
 *         Defect Reference Modified By Date of Change Change Details
 *         (RTC/TUT/PACS) (YYYY-MM-DD)
 *         -----------------------------------------------------------------------------------------------------------------
 *         XXXX <<name of modifier>> <<modification details goes here>>
 *
 *         -----------------------------------------------------------------------------------------------------------------
 */
public class BciCCEOutClrConf extends ServiceLifecycle {

    @Override
    public void processSingleThreaded(ServiceData serviceData) {
        // TODO Auto-generated method stub
        try {
            DataAccess da = new DataAccess(this);
            Date dat = new Date(this);
            DatesRecord datesRec = dat.getDates();
            String today = datesRec.getToday().getValue();
            String nextWorkingDay = datesRec.getNextWorkingDay().getValue();
            String serverName = serviceData.getProcessId();
            TsaServiceRecord tsaRec = new TsaServiceRecord(da.getRecord("TSA.SERVICE", serverName));
												  
            // System.out.println("tsaRec " + tsaRec);
            String serverDate = tsaRec.getDate(0).getStarted().getValue();
            // System.out.println("date: " + serverDate);
            // Date date = Calendar.getInstance().getTime();
            serverDate = serverDate.substring(0, 10);

            serverDate = serverDate.replace("/", "");
            System.out.println("serverDate: " + serverDate);

            String date = serverDate.substring(4, 8).concat(serverDate.substring(2, 4))
                    .concat(serverDate.substring(0, 2));

            System.out.println("date: " + date);
            System.out.println("today: " + today);

            if (today.equals(date)) {

                String lastWorkingday = "";
                //String previousDay = datesRec.getLastWorkingDay().getValue();
                String id = "";
                int countPen = 0;
                int countUsd = 0;
                int valueLHcnt = 0;
                int totalOperationsFCPen = 0;
                int totalOperationsFCUsd = 0;
                String returnedfile = "";

                String controlTotals = "";
                int sumCtrlTotsPen220 = 0;
                int sumCtrlTotsPen221 = 0;
                int sumCtrlTotsPen222 = 0;
                int sumCtrlTotsPen223 = 0;
                int sumCtrlTotsPen224 = 0;
                int sumCtrlTotsUsd220 = 0;
                int sumCtrlTotsUsd221 = 0;
                int sumCtrlTotsUsd222 = 0;
                int sumCtrlTotsUsd223 = 0;
                int sumCtrlTotsUsd224 = 0;

                int controlTotalFCusd = 0;
                int controlTotalFCpen = 0;
                BigDecimal sumOfAmountslc = new BigDecimal("0.00");
                BigDecimal sumOfCommlc = new BigDecimal("0.00");
                BigDecimal Amountsum = new BigDecimal("0.00");
                BigDecimal Commsum = new BigDecimal("0.00");
                BigDecimal sumAmountPen220 = new BigDecimal("0.00");
                BigDecimal sumAmountPen221 = new BigDecimal("0.00");
                BigDecimal sumAmountPen222 = new BigDecimal("0.00");
                BigDecimal sumAmountPen223 = new BigDecimal("0.00");
                BigDecimal sumAmountPen224 = new BigDecimal("0.00");
                BigDecimal sumAmountUsd220 = new BigDecimal("0.00");
                BigDecimal sumAmountUsd221 = new BigDecimal("0.00");
                BigDecimal sumAmountUsd222 = new BigDecimal("0.00");
                BigDecimal sumAmountUsd223 = new BigDecimal("0.00");
                BigDecimal sumAmountUsd224 = new BigDecimal("0.00");
                BigDecimal sumCommPen220 = new BigDecimal("0.00");
                BigDecimal sumCommPen221 = new BigDecimal("0.00");
                BigDecimal sumCommPen222 = new BigDecimal("0.00");
                BigDecimal sumCommPen223 = new BigDecimal("0.00");
                BigDecimal sumCommPen224 = new BigDecimal("0.00");
                BigDecimal sumCommUsd220 = new BigDecimal("0.00");
                BigDecimal sumCommUsd221 = new BigDecimal("0.00");
                BigDecimal sumCommUsd222 = new BigDecimal("0.00");
                BigDecimal sumCommUsd223 = new BigDecimal("0.00");
                BigDecimal sumCommUsd224 = new BigDecimal("0.00");
                Double sumOfAmountsfcusd = 0.00;
                Double sumOfAmountsfcpen = 0.00;
                Double sumOfCommisionsECusd = 0.00;
                Double sumOfCommisionsECpen = 0.00;
                String sumAmountsusd = "";
                String commisionAmountusd = "";
                String sumAmountspen = "";
                String commisionAmountpen = "";
                String appcode = "";
                String endOfContrl220Pen = "";
                String endOfContrl221Pen = "";
                String endOfContrl222Pen = "";
                String endOfContrl223Pen = "";
                String endOfContrl224Pen = "";
                String endOfContrl220Usd = "";
                String endOfContrl221Usd = "";
                String endOfContrl222Usd = "";
                String endOfContrl223Usd = "";
                String endOfContrl224Usd = "";
                String sumOfAmounts = "";
                // String transfConfirma = "";
                // Double sumOfAmountslc = 0.00;
                // Double sumOfCommlc = 0.00;
                // String controlTots = "";
                int flagNewFH = 0;
                int valuecnt = 0;
                int finalcnt = 0;
                BciCceClearingParamRecord paramrec = new BciCceClearingParamRecord(
                        da.getRecord("EB.BCI.CCE.CLEARING.PARAM", "SYSTEM"));
                String outPath = paramrec.getOutPath().getValue();

                appcode = serviceData.getJobData(0);

                lastWorkingday = today;

                List<String> recList = da.selectRecords("", "EB.BCI.CCE.MAPPING.FIELD.VALUES", "",
                        "WITH STATUS EQ TO_BE_CONFIRMED_IN AND DATE EQ " + lastWorkingday + " AND TXN.TYPE EQ "
                                + appcode);

                System.out.println("APPCODE: " + appcode);
                System.out.println("recList: " + recList);
                // List<String> recList = da.selectRecords("",
                // "EB.BCI.CCE.MAPPING.FIELD.VALUES", "", "WITH STATUS EQ
                // TO_BE_CONFIRMED_IN");
                List<String> finalCCEStr = new ArrayList<String>();
                List<String> finalCCEStrNull = new ArrayList<String>();
                List<String> fileHeaderPEN = new ArrayList<String>();
                List<String> fileHeaderUSD = new ArrayList<String>();
                List<String> lotHeader220 = new ArrayList<String>();
                List<String> lotHeader221 = new ArrayList<String>();
                List<String> lotHeader222 = new ArrayList<String>();
                List<String> lotHeader223 = new ArrayList<String>();
                List<String> lotHeader224 = new ArrayList<String>();
                List<String> lotHeader220usd = new ArrayList<String>();
                List<String> lotHeader221usd = new ArrayList<String>();
                List<String> lotHeader222usd = new ArrayList<String>();
                List<String> lotHeader223usd = new ArrayList<String>();
                List<String> lotHeader224usd = new ArrayList<String>();
                List<String> individualAdd220usd = new ArrayList<String>();
                List<String> individualAdd221usd = new ArrayList<String>();
                List<String> individualAdd222usd = new ArrayList<String>();
                List<String> individualAdd223usd = new ArrayList<String>();
                List<String> individualAdd224usd = new ArrayList<String>();
                List<String> individualAdd220Pen = new ArrayList<String>();
                List<String> individualAdd221Pen = new ArrayList<String>();
                List<String> individualAdd222Pen = new ArrayList<String>();
                List<String> individualAdd223Pen = new ArrayList<String>();
                List<String> individualAdd224Pen = new ArrayList<String>();
                /*List<String> lotendHeader220usd = new ArrayList<String>();
                List<String> lotendHeader221usd = new ArrayList<String>();
                List<String> lotendHeader222usd = new ArrayList<String>();
                List<String> lotendHeader223usd = new ArrayList<String>();
                List<String> lotendHeader224usd = new ArrayList<String>();
                List<String> lotendHeader220Pen = new ArrayList<String>();
                */
                List<String> lotendHeader221Pen = new ArrayList<String>();
                List<String> lotendHeader222Pen = new ArrayList<String>();
                List<String> lotendHeader223Pen = new ArrayList<String>();
                List<String> lotendHeader224Pen = new ArrayList<String>();

                List<String> fileendUsd = new ArrayList<String>();
                List<String> fileendPen = new ArrayList<String>();

                // **********************fileHeader*******************************//
                System.out.println("entered fileheader");
                List<FieldNameClass> fldList = null;
                String typReg = "";
                String sesType = "";
                String currency = "";
                int lotend220FlagUsd = 0;
                int lotend221FlagUsd = 0;
                int lotend222FlagUsd = 0;
                int lotend223FlagUsd = 0;
                int lotend224FlagUsd = 0;

                int lotend220FlagPen = 0;
                int lotend221FlagPen = 0;
                int lotend222FlagPen = 0;
                int lotend223FlagPen = 0;
                int lotend224FlagPen = 0;
                int totalOperationsFC = 0;

                String cpFHtoLh = "";
                String copyFileHeader = "";

                int countNullStrFH = -1;
                int countNullStrLH = -1;

                String todayFH = today + "-" + "FILEHEADER";
                String archiveNumber = "";
                String archiveLotHeader = "";
                BciCceHeaderCounterRecord archRec = new BciCceHeaderCounterRecord(this);
                BciCceHeaderCounterRecord archRecLot = new BciCceHeaderCounterRecord(this);
                try {
																					  
                    archRec = new BciCceHeaderCounterRecord(da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayFH));
                    System.out.println("archRec");
                    if (appcode.equals("TRI")) {
                        archiveNumber = archRec.getName(0).getValue().getValue();
                    } else if (appcode.equals("TRM")) {
                        archiveNumber = archRec.getName(1).getValue().getValue();
                    } else if (appcode.equals("TRT")) {
                        archiveNumber = archRec.getName(2).getValue().getValue();
                    }

                } catch (Exception e) {

                }

                try {
																						 
                    archRecLot = new BciCceHeaderCounterRecord(da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayFH));
                    // System.out.println("archRec");
                    if (appcode.equals("TRI")) {
                        archiveLotHeader = archRecLot.getName(0).getValue().getValue();
                    } else if (appcode.equals("TRM")) {
                        archiveLotHeader = archRecLot.getName(1).getValue().getValue();
                    } else if (appcode.equals("TRT")) {
                        archiveLotHeader = archRecLot.getName(2).getValue().getValue();
                    }

                } catch (Exception e) {

                }

                try {
                    countNullStrFH = Integer.parseInt(archiveNumber);
                    countNullStrLH = Integer.parseInt(archiveLotHeader);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    // Uncomment and replace with appropriate logger
                    // LOGGER.error(exception_var, exception_var);
                }

                if (archiveNumber.equals("") || countNullStrFH == 0) {
                    archiveNumber = "1";
                    flagNewFH = 1;
                }

                if (archiveLotHeader.equals("") || countNullStrLH == 0) {
                    archiveLotHeader = "0";
                }
                valuecnt = Integer.parseInt(archiveNumber);
                int valuecntOrign = valuecnt;

                cpFHtoLh = archiveLotHeader;

                System.out.println("valuecnt: " + valuecnt);
                for (String recid : recList) {
                    appcode = serviceData.getJobData(0);

                    BciCceMappingFieldValuesRecord bciFldRec = new BciCceMappingFieldValuesRecord(
                            da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", recid));

                    /*
                     * try { String individualRegistration = "";
                     * 
                     * List<MapFieldTypeClass> fieldList =
                     * bciFldRec.getMapFieldType(); String type =
                     * fieldList.get(0).getMapFieldType().getValue(); if
                     * (type.equals("INDIVIDUAL")) { String
                     * individualRegistrationfisrt =
                     * fieldList.get(0).getMapFieldVal().get(0).getValue();
                     * String individualRegistrationsec =
                     * fieldList.get(0).getMapFieldVal().get(1).getValue();
                     * individualRegistration =
                     * individualRegistrationfisrt.concat(
                     * individualRegistrationsec);
                     * 
                     * }
                     * 
                     * String entidad =
                     * individualRegistration.substring(186,189);
                     * EbBciCceParticipantsBankNameRecord bnkRecord = new
                     * EbBciCceParticipantsBankNameRecord(da.getRecord(
                     * "EB.BCI.CCE.PARTICIPANTS.BANK.NAME", entidad));
                     * transfConfirma = bnkRecord.getTransConfirma().getValue();
                     * 
                     * if (transfConfirma.equals("NO") ||
                     * transfConfirma.equals("")){ continue; }
                     * 
                     * } catch (Exception e) { continue; }
                     */

                    System.out.println("0");
                    String oeid = bciFldRec.getOeId().getValue();
                    PpOrderEntryRecord payRec = new PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", oeid));
                    BciCceInterfaceParameterRecord bciParamRec = new BciCceInterfaceParameterRecord(
                            da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.FILEHEADER"));

                    String transferConcept = "";
                    String transferType = "";
                    fldList = bciParamRec.getFieldName();
                    String immDest = "";
                    String immDestName = "";
                    String immOrigin = "";
                    String originName = "";
                    String currPen = "";
                    String currUsd = "";
                    String originEntity = "";
                    System.out.println("1");
                    for (FieldNameClass fieldid : fldList) {
                        String fieldName = fieldid.getFieldName().getValue();
                        if (fieldName.equals("TYPE.REGISTER")) {
                            typReg = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("SESSION.TYPE.CONFIRMED")) {
                            sesType = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("IMMEDIATE.DESTINATION.CODE.ECTRMA")) {
                            immDest = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("IMMEDIATE.DESTINATION.NAME")) {
                            immDestName = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("L.CCI.CODE.ORIG")) {
                            immOrigin = fieldid.getFieldValue().getValue();
                            // immOrigin = "0" + immOrigin.substring(0, 3) + "0"
                            // + immOrigin.substring(3, 6);
                        }
                        if (fieldName.equals("IMMEDIATE.ORIGIN.NAME")) {
                            originName = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("CURR.PEN")) {
                            currPen = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("CURR.USD")) {
                            currUsd = fieldid.getFieldValue().getValue();
                        }
                    }
                    try {
                        immOrigin = immOrigin.substring(0, 8);
                        originName = originName.substring(0, 23);
                    } catch (Exception e) {

                    }
                    id = recid;
                    System.out.println("2");
                    todayFH = today + "-" + "FILEHEADER";
                    archiveNumber = "";
                    try {
																						  
                        archRec = new BciCceHeaderCounterRecord(da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayFH));
                        if (appcode.equals("TRI")) {
                            archiveNumber = archRec.getName(0).getValue().getValue();
                        } else if (appcode.equals("TRM")) {
                            archiveNumber = archRec.getName(1).getValue().getValue();
                        } else if (appcode.equals("TRT")) {
                            archiveNumber = archRec.getName(2).getValue().getValue();
                        }
                        System.out.println("archiveNumber: " + archiveNumber);
                    } catch (Exception e) {

                    }
                    System.out.println("2.5");
                    if (archiveNumber.equals("")) {
                        valuecnt = 1;
                    } else {
                        valuecnt = Integer.parseInt(archiveNumber);
                        valuecnt = valuecnt + 1;
                    }
                    System.out.println("3");
                    String coin = payRec.getDebitaccountcurrency().getValue();
                    if (coin.equals("PEN")) {
                        currency = currPen;
                    }
                    if (coin.equals("USD")) {
                        currency = currUsd;
                    }

                    appcode = serviceData.getJobData(0);// Data field value
                    try {
                        immDestName = immDestName.substring(0, 8);
                        originName = originName.substring(0, 23);
                    } catch (Exception e) {

                    }
                    /*
                     * try { immOrigin =
                     * payRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
                     * immOrigin = immOrigin.substring(0, 7); } catch (Exception
                     * e) {
                     * 
                     * }
                     */
                    System.out.println("4");
                    archiveNumber = String.valueOf(valuecnt);
                    archiveNumber = StringUtils.leftPad(archiveNumber, 2, "0");
                    archiveNumber = archiveNumber.substring(archiveNumber.length() - 2, archiveNumber.length());

                    String dateOfPresentation = payRec.getDebitvaluedate().getValue();
                    String sysId = payRec.getTransactionreferencenumber().getValue();

                    String fileHeader = typReg.concat(sesType).concat(currency).concat(appcode).concat(immDest)
                            .concat(StringUtils.rightPad(immOrigin, 8, ""))
                            .concat(StringUtils.rightPad(dateOfPresentation, 8, ""))
                            .concat(StringUtils.leftPad(archiveNumber, 2, "0"))
                            .concat(StringUtils.rightPad(immDestName, 23, ""))
                            .concat(StringUtils.rightPad(originName, 23, ""));
                    System.out.println("entererd file header");
                    int lFh = 100;
                    lFh = fileHeader.length();
                    lFh = lFh + 122;
                    String fileHeaderFinal = String.format("%" + -lFh + "s", fileHeader);

                    if (coin.equals("PEN")) {
                        countPen = fileHeaderPEN.size();
                        if (countPen == 0) {
                            BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                            try {
                                archwriteRec = new BciCceHeaderCounterRecord(
                                        da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayFH));
                            } catch (Exception ex) {

                            }
                            NameClass fldclass = new NameClass();
                            fldclass.setName(appcode);
                            // aluecnt = valuecnt + 1;
                            String Value = String.valueOf(valuecnt);
                            fldclass.setValue(Value);
                            if (appcode.equals("TRI")) {
                                archwriteRec.setName(fldclass, 0);
                            } else if (appcode.equals("TRM")) {
                                archwriteRec.setName(fldclass, 1);
                            } else if (appcode.equals("TRT")) {
                                archwriteRec.setName(fldclass, 2);
                            }
                            BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                            try {
                                tblRec.write(todayFH, archwriteRec);
                            } catch (Exception e) {

                            }
                            fileHeaderPEN.add(fileHeaderFinal);
                        }
                    }
                    if (coin.equals("USD")) {
                        countUsd = fileHeaderUSD.size();
                        if (countUsd == 0) {
                            BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                            try {
                                archwriteRec = new BciCceHeaderCounterRecord(
                                        da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayFH));
                            } catch (Exception ex) {

                            }
                            NameClass fldclass = new NameClass();
                            fldclass.setName(appcode);
                            // valuecnt = valuecnt + 1;
                            String Value = String.valueOf(valuecnt);
                            fldclass.setValue(Value);
                            if (appcode.equals("TRI")) {
                                archwriteRec.setName(fldclass, 0);
                            } else if (appcode.equals("TRM")) {
                                archwriteRec.setName(fldclass, 1);
                            } else if (appcode.equals("TRT")) {
                                archwriteRec.setName(fldclass, 2);
                            }
                            BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                            try {
                                tblRec.write(todayFH, archwriteRec);
                            } catch (Exception e) {

                            }
                            fileHeaderUSD.add(fileHeaderFinal);
                        }
                    }

                    // ****************Lot Header************************//
                    System.out.println("entered lotheader");
                    String typRegLH = "";
                    String lotType = "";
                    String issueCompany = "";
                    BciCceInterfaceParameterRecord bciParamReclotHeader = new BciCceInterfaceParameterRecord(
                            da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.LOTHEADER"));
                    List<FieldNameClass> fldList1 = bciParamReclotHeader.getFieldName();
                    for (FieldNameClass fieldid : fldList1) {
                        String fieldName = fieldid.getFieldName().getValue();
                        if (fieldName.equals("TYPE.REGISTER")) {
                            typRegLH = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("LOT.TYPE.CONFIRM")) {
                            lotType = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("ISSUING.COMPANY")) {
                            issueCompany = fieldid.getFieldValue().getValue();
                        }

                    }

                    int headerCnt = 0;
                    int headerCntPEN = lotHeader220.size() + lotHeader221.size() + lotHeader222.size()
                            + lotHeader223.size() + lotHeader224.size() + 1;
                    int headerCntUSD = lotHeader220usd.size() + lotHeader221usd.size() + lotHeader222usd.size()
                            + lotHeader223usd.size() + lotHeader224usd.size() + 1;
                    if (coin.equals("USD")) {
                        headerCnt = headerCntUSD;
                    }
                    if (coin.equals("PEN")) {
                        headerCnt = headerCntPEN;
                    }
                    String archiveNumberLH = "";
                    int valuecntLH = 0;
                    String lotHeadId = today + "-" + "LOTHEADER";
                    try {
                        BciCceHeaderCounterRecord headCntrRec = new BciCceHeaderCounterRecord(
                                da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));
                        archiveNumberLH = headCntrRec.getName(0).getValue().getValue();
                    } catch (Exception e) {

                    }
                    if (archiveNumberLH.equals("")) {
                        archiveNumberLH = "1";
                    } else {
                        int archIntTmp = Integer.parseInt(archiveNumberLH);
                        int archivTmp = archIntTmp + 1;
                        archiveNumberLH = Integer.toString(archivTmp);
                    }

                    valuecntLH = Integer.parseInt(archiveNumberLH);

											 
                    String datePresentation = "";
                    String settlementDate = "";
                    // transferType =
                    // payRec.getPaymentCategorypurpPrty().getValue();
																																							 

                    try {
                        transferConcept = "Domestic transfer inward";
                        // transferType =
                        // payRec.getPaymentCategorypurpPrty().getValue();
                        datePresentation = payRec.getDebitvaluedate().getValue();
                        settlementDate = payRec.getProcessingdate().getValue();
                    } catch (Exception e) {

                    }

                    originEntity = payRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
                    try {
                        /*
                         * String company = payRec.getCoCode(); CompanyRecord
                         * companyRec = new
                         * CompanyRecord(da.getRecord("COMPANY", company));
                         * String entidadCode =
                         * companyRec.getLocalRefField("L.CR.ENTITY.COD").
                         * getValue(); String officeCode =
                         * companyRec.getLocalRefField("L.OFFICE.CODE").getValue
                         * (); originEntity = StringUtils.leftPad(entidadCode,
                         * 4, "0").concat(StringUtils.leftPad(officeCode, 4,
                         * "0"));
                         */
                        originEntity = payRec.getLocalRefField("L.CR.ENTITY.COD").getValue();
                        // originEntity = "0" + originEntity.substring(0, 3) +
                        // "0" + originEntity.substring(3, 6);
                        transferConcept = transferConcept.substring(0, 15);

                        transferType = payRec.getLocalRefField("L.TRANSFER.TYPE").getValue();

                    } catch (Exception e) {

                    }
                    String lotNumber = Integer.toString(headerCnt); // mapping
                                                                    // required

                    if (coin.equals("USD")) {
                        int lotHeaferFH = 0;
                        lotHeaferFH = Integer.parseInt(cpFHtoLh) + 2;
                        copyFileHeader = Integer.toString(lotHeaferFH);
                    } else {
                        int lotHeaferFH = 0;
                        lotHeaferFH = Integer.parseInt(cpFHtoLh) + 1;
                        copyFileHeader = Integer.toString(lotHeaferFH);
                    }

                    if (appcode.equals("TRT")) {
                        settlementDate = nextWorkingDay;
                        System.out.println("settlementDate: " + settlementDate);
                    }

                    String lotHeaderStr = typRegLH.concat(StringUtils.leftPad(copyFileHeader, 2, "0")).concat(lotType)
                            .concat(StringUtils.rightPad(issueCompany, 46, ""))
                            .concat(StringUtils.rightPad(transferConcept, 15, "")).concat(transferType)
                            .concat(StringUtils.rightPad(datePresentation, 8, ""))
                            .concat(StringUtils.rightPad(settlementDate, 8, ""))
                            .concat(StringUtils.rightPad(originEntity, 8, ""))
                            .concat(StringUtils.leftPad(archiveNumberLH, 7, "0"));

                    int L = 100;
                    L = lotHeaderStr.length();
                    L = L + 100;
                    String lotheaderstr = String.format("%" + -L + "s", lotHeaderStr);
                    if (coin.equals("PEN")) {
                        if (transferType.equals("220")) {
                            int cnt = lotHeader220.size();
                            if (cnt == 0) {
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));

                                } catch (Exception ex) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                // valuecntLH = valuecntLH + 1;
                                String Value = String.valueOf(valuecntLH);
                                fldclass.setValue(Value);
                                archwriteRec.setName(fldclass, 0);

                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(lotHeadId, archwriteRec);
                                } catch (Exception e) {

                                }
                                lotHeader220.add(lotheaderstr);
                            }
                        }
                        if (transferType.equals("221")) {
                            int cnt = lotHeader221.size();
                            if (cnt == 0) {
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));

                                } catch (Exception ex) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                // valuecntLH = valuecntLH + 1;
                                String Value = String.valueOf(valuecntLH);
                                fldclass.setValue(Value);
                                archwriteRec.setName(fldclass, 0);

                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(lotHeadId, archwriteRec);
                                } catch (Exception e) {

                                }
                                lotHeader221.add(lotheaderstr);
                            }
                        }
                        if (transferType.equals("222")) {
                            int cnt = lotHeader222.size();
                            if (cnt == 0) {
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));

                                } catch (Exception ex) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                // valuecntLH = valuecntLH + 1;
                                String Value = String.valueOf(valuecntLH);
                                fldclass.setValue(Value);
                                archwriteRec.setName(fldclass, 0);

                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(lotHeadId, archwriteRec);
                                } catch (Exception e) {

                                }
                                lotHeader222.add(lotheaderstr);
                            }
                        }
                        if (transferType.equals("223")) {
                            int cnt = lotHeader223.size();
                            if (cnt == 0) {
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));

                                } catch (Exception ex) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                // valuecntLH = valuecntLH + 1;
                                String Value = String.valueOf(valuecntLH);
                                fldclass.setValue(Value);
                                archwriteRec.setName(fldclass, 0);

                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(lotHeadId, archwriteRec);
                                } catch (Exception e) {

                                }
                                lotHeader223.add(lotheaderstr);
                            }
                        }
                        if (transferType.equals("224")) {
                            int cnt = lotHeader224.size();
                            if (cnt == 0) {
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));

                                } catch (Exception ex) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                // valuecntLH = valuecntLH + 1;
                                String Value = String.valueOf(valuecntLH);
                                fldclass.setValue(Value);
                                archwriteRec.setName(fldclass, 0);
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(lotHeadId, archwriteRec);
                                } catch (Exception e) {

                                }
                                lotHeader224.add(lotheaderstr);
                            }
                        }
                    }
                    if (coin.equals("USD")) {
                        if (transferType.equals("220")) {
                            int cnt = lotHeader220usd.size();
                            if (cnt == 0) {
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));

                                } catch (Exception ex) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                // valuecntLH = valuecntLH + 1;
                                String Value = String.valueOf(valuecntLH);
                                fldclass.setValue(Value);
                                archwriteRec.setName(fldclass, 0);
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(lotHeadId, archwriteRec);
                                } catch (Exception e) {

                                }
                                lotHeader220usd.add(lotheaderstr);
                            }
                        }
                        if (transferType.equals("221")) {
                            int cnt = lotHeader221usd.size();
                            if (cnt == 0) {
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));

                                } catch (Exception ex) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                // valuecntLH = valuecntLH + 1;
                                String Value = String.valueOf(valuecntLH);
                                fldclass.setValue(Value);
                                archwriteRec.setName(fldclass, 0);
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(lotHeadId, archwriteRec);
                                } catch (Exception e) {

                                }
                                lotHeader221usd.add(lotheaderstr);
                            }
                        }
                        if (transferType.equals("222")) {
                            int cnt = lotHeader222usd.size();
                            if (cnt == 0) {
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));

                                } catch (Exception ex) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                // valuecntLH = valuecntLH + 1;
                                String Value = String.valueOf(valuecntLH);
                                fldclass.setValue(Value);
                                archwriteRec.setName(fldclass, 0);
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(lotHeadId, archwriteRec);
                                } catch (Exception e) {

                                }
                                lotHeader222usd.add(lotheaderstr);
                            }
                        }
                        if (transferType.equals("223")) {
                            int cnt = lotHeader223usd.size();
                            if (cnt == 0) {
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));

                                } catch (Exception ex) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                // valuecntLH = valuecntLH + 1;
                                String Value = String.valueOf(valuecntLH);
                                fldclass.setValue(Value);
                                archwriteRec.setName(fldclass, 0);

                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(lotHeadId, archwriteRec);
                                } catch (Exception e) {

                                }
                                lotHeader223usd.add(lotheaderstr);
                            }
                        }
                        if (transferType.equals("224")) {
                            int cnt = lotHeader224usd.size();
                            if (cnt == 0) {
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", lotHeadId));

                                } catch (Exception ex) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                // valuecntLH = valuecntLH + 1;
                                String Value = String.valueOf(valuecntLH);
                                fldclass.setValue(Value);
                                archwriteRec.setName(fldclass, 0);

                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(lotHeadId, archwriteRec);
                                } catch (Exception e) {

                                }
                                lotHeader224usd.add(lotheaderstr);
                            }
                        }
                    }
                    // **************************individual and
                    // additional************************//
                    System.out.println("entered individualadditional");
                    String individualRegistration = "";
                    String additionalRegistration = "";
                    String additionalReturnRegistration = "";
                    String typeRegAdd = "";
                    String addReg = "";
                    String reasonRet = "";
                    String ordTrnsRecCnt = "";
                    // String entOrgTrans = "";
                    String addInfo = "";
                    String uniSeq = "";
                    String freeAdd = "";
                    String indTransRecCnt = "";
                    String docType = "";
                    String docNumber = "";
                    String sameOwner = "";
                    String docTypeValue = "";

                    BciCceInterfaceParameterRecord bciParamRecAdd = new BciCceInterfaceParameterRecord(
                            da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.ADDITIONAL"));
                    List<FieldNameClass> fldListAdd = bciParamRecAdd.getFieldName();
                    for (FieldNameClass fieldid : fldListAdd) {
                        String fieldName = fieldid.getFieldName().getValue();
                        if (fieldName.equals("TYPE.REGISTER")) {
                            typeRegAdd = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("ADDITIONAL.REGISTRATION.TRF")) {
                            addReg = fieldid.getFieldValue().getValue();
                        }

                    }
                    BciCceInterfaceParameterRecord bciParamIndRec = new BciCceInterfaceParameterRecord(
                            da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.INDIVIDUAL"));
                    fldList = bciParamIndRec.getFieldName();
                    String sessType = "";

                    for (FieldNameClass fieldid : fldList) {
                        String fieldName = fieldid.getFieldName().getValue();
                        if (fieldName.equals("TRANSACTION.CODE>CONFIRMATION")) {
                            sessType = fieldid.getFieldValue().getValue();
                        }
                    }
                    // reasonRet = payRec.getReturncode().getValue();
                    reasonRet = "D99";
                    ordTrnsRecCnt = payRec.getLocalRefField("L.REGIST.CONTR").getValue();
                    // entOrgTrans = payRec.getLocalRefField("L.CR.ENTITY.COD").getValue();

                    addInfo = payRec.getReturndescription().getValue();
                    
                    try {
                     
                        uniSeq = recid;
                    } catch (Exception e) {

                    }
                    indTransRecCnt = ordTrnsRecCnt;

                    String ordTrns = payRec.getLocalRefField("L.CR.ENTITY.COD").getValue().substring(0, 8);

                    additionalReturnRegistration = typeRegAdd.concat(addReg)
                            .concat(StringUtils.rightPad(reasonRet, 3, ""))
                            .concat(StringUtils.leftPad(ordTrnsRecCnt, 15, "0"))
                            .concat(StringUtils.leftPad(ordTrns, 8, "0")).concat(StringUtils.rightPad(addInfo, 44, ""))
                            .concat(StringUtils.leftPad(uniSeq, 7, "0")).concat(StringUtils.rightPad(freeAdd, 105, ""))
                            .concat(StringUtils.leftPad(indTransRecCnt, 15, "0"));

                    BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(
                            da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", id));

                    List<MapFieldTypeClass> fieldList = mapRec.getMapFieldType();
                    for (MapFieldTypeClass mapId : fieldList) {
                        String type = mapId.getMapFieldType().getValue();
                        if (type.equals("INDIVIDUAL")) {
                            String individualRegistrationfisrt = mapId.getMapFieldVal().get(0).getValue();
                            String individualRegistrationsec = mapId.getMapFieldVal().get(1).getValue();
                            individualRegistration = individualRegistrationfisrt.concat(individualRegistrationsec);

                        }
                        if (type.equals("ADDITIONAL")) {
                            String additionalRegistrationfirst = mapId.getMapFieldVal().get(0).getValue();
                            String additionalRegistrationSec = mapId.getMapFieldVal().get(1).getValue();
                            additionalRegistration = additionalRegistrationfirst.concat(additionalRegistrationSec);
                        }
                    }
                    individualRegistration = individualRegistration.substring(0, 1) + sessType
                            + individualRegistration.substring(3);

                    try {
                        sameOwner = payRec.getLocalRefField("L.SAME.OWNER").getValue();
                        if (transferType.equals("220")) {
                            if (sameOwner.equals("YES")) {
                                docType = payRec.getLocalRefField("L.DRAWER.DOC.TYPE").getValue();
                                docNumber = payRec.getLocalRefField("L.DRAWER.DOC.NUMBER").getValue();
                                if (docType.equals("PA")) {
                                    docTypeValue = "4";
                                } else if (docType.equals("CE")) {
                                    docTypeValue = "5";
                                } else if (docType.equals("DN")) {
                                    docTypeValue = "2";
                                } else if (docType.equals("LM")) {
                                    docTypeValue = "3";
                                } else if (docType.equals("RU")) {
                                    docTypeValue = "6";
                                } else {
                                    docTypeValue = "1";
                                }
                            }
                            System.out.println(docNumber);
                            System.out.println(docTypeValue);

                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    /*
                     * String campoReferencia =
                     * payRec.getRelatedreference().getValue(); if
                     * (campoReferencia.length() != 25) { campoReferencia =
                     * StringUtils.rightPad(campoReferencia, 25, " "); }
                     * StringBuilder strbline1 = new
                     * StringBuilder(individualRegistration); StringBuilder
                     * linemod1 = strbline1.replace(152, 177, campoReferencia);
                     * individualRegistration = linemod1.toString();
                     */

                    String originEntityLC1 = payRec.getLocalRefField("L.CR.ENTITY.COD").getValue();
                    StringBuilder strbline = new StringBuilder(individualRegistration);
                    StringBuilder linemod = strbline.replace(185, 193, originEntityLC1).replace(3, 11, originEntityLC1);
                    individualRegistration = linemod.toString();

                    ordTrnsRecCnt = payRec.getLocalRefField("L.REGIST.CONTR").getValue();
                    strbline = new StringBuilder(individualRegistration);
                    linemod = strbline.replace(3, 11, ordTrnsRecCnt.substring(0, 8));
                    individualRegistration = linemod.toString();

                    strbline = new StringBuilder(additionalRegistration);
                    linemod = strbline.replace(185, 193, originEntityLC1);
                    additionalRegistration = linemod.toString();

                    strbline = new StringBuilder(additionalReturnRegistration);
                    linemod = strbline.replace(185, 193, originEntityLC1);
                    additionalReturnRegistration = linemod.toString();

                    if (coin.equals("USD")) {

                        if (transferType.equals("220")) {
                            individualAdd220usd.add(individualRegistration);
                            individualAdd220usd.add(additionalRegistration);
                            individualAdd220usd.add(additionalReturnRegistration);
                        }
                        if (transferType.equals("221")) {
                            individualAdd221usd.add(individualRegistration);
                            individualAdd221usd.add(additionalRegistration);
                            individualAdd221usd.add(additionalReturnRegistration);
                        }
                        if (transferType.equals("222")) {
                            individualAdd222usd.add(individualRegistration);
                            individualAdd222usd.add(additionalRegistration);
                            individualAdd222usd.add(additionalReturnRegistration);
                        }
                        if (transferType.equals("223")) {
                            individualAdd223usd.add(individualRegistration);
                            individualAdd223usd.add(additionalRegistration);
                            individualAdd223usd.add(additionalReturnRegistration);
                        }
                        if (transferType.equals("224")) {
                            individualAdd224usd.add(individualRegistration);
                            individualAdd224usd.add(additionalRegistration);
                            individualAdd224usd.add(additionalReturnRegistration);
                        }
                    }
                    if (coin.equals("PEN")) {
                        List<String> indaddPen = new ArrayList<String>();
                        indaddPen.add(individualRegistration);
                        indaddPen.add(additionalRegistration);
                        if (transferType.equals("220")) {
                            individualAdd220Pen.add(individualRegistration);
                            individualAdd220Pen.add(additionalRegistration);
                            individualAdd220Pen.add(additionalReturnRegistration);
                        }
                        if (transferType.equals("221")) {
                            individualAdd221Pen.add(individualRegistration);
                            individualAdd221Pen.add(additionalRegistration);
                            individualAdd221Pen.add(additionalReturnRegistration);
                        }
                        if (transferType.equals("222")) {
                            individualAdd222Pen.add(individualRegistration);
                            individualAdd222Pen.add(additionalRegistration);
                            individualAdd222Pen.add(additionalReturnRegistration);
                        }
                        if (transferType.equals("223")) {
                            individualAdd223Pen.add(individualRegistration);
                            individualAdd223Pen.add(additionalRegistration);
                            individualAdd223Pen.add(additionalReturnRegistration);
                        }
                        if (transferType.equals("224")) {
                            individualAdd224Pen.add(individualRegistration);
                            individualAdd224Pen.add(additionalRegistration);
                            individualAdd224Pen.add(additionalReturnRegistration);
                        }
                    }

                    // *********************end of lot
                    // control*******************//
                    System.out.println("entered endofcontrol");
                    String typeRegLC = "";
                    int totalOperation = 0;
                    BciCceInterfaceParameterRecord bciParamRecEndCtrl = new BciCceInterfaceParameterRecord(
                            da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.ENDCONTROL"));
                    List<FieldNameClass> fldList2 = bciParamRecEndCtrl.getFieldName();
                    for (FieldNameClass fieldid : fldList2) {
                        String fieldName = fieldid.getFieldName().getValue();
                        if (fieldName.equals("TYPE.REGISTER")) {
                            typeRegLC = fieldid.getFieldValue().getValue();
                        }

                    }
                    int totRec = 0;
                    Amountsum = new BigDecimal("0.00");
                    Commsum = new BigDecimal("0.00");
                    String POAmt = payRec.getTransactionamount().getValue();
                    // sumOfAmountslc =
                    // Double.parseDouble(payRec.getPaymentAmount().getValue());

                    sumOfAmountslc = new BigDecimal(POAmt);
                    // sumOfAmounts = String.format("%.02f", sumOfAmountslc);

                    String sumOfCommisions = "";
                    List<DebitchargecomponentClass> commList = payRec.getDebitchargecomponent();
                    System.out.println("sumOfCommisions: " + sumOfCommisions);
                    try {
                        // sumOfAmounts = String.format("%.02f", Decimal);
                        /*
                         * int amt = sumOfAmounts.indexOf("."); sumOfAmounts =
                         * sumOfAmounts.substring(0,
                         * amt).concat(sumOfAmounts.substring(amt + 1));
                         */
                        if (!commList.isEmpty()) {
                            // sumOfCommlc =
                            // Double.parseDouble(commList.get(1).getChargeAmount().getValue());
                            String POCommAmt = commList.get(1).getDebitchargeamount().getValue();
                            sumOfCommlc = new BigDecimal(POCommAmt);
                            // sumOfCommisions = String.format("%.02f",
                            // sumOfCommlc);

                            /*
                             * int commAmt = sumOfCommisions.indexOf(".");
                             * sumOfCommisions = sumOfCommisions.substring(0,
                             * commAmt)
                             * .concat(sumOfCommisions.substring(commAmt + 1));
                             */
                        }
                    } catch (Exception e) {

                    }

                    String controlTotString = payRec.getLocalRefField("L.REGIST.CONTR").getValue();
                    int controlTots = Integer.parseInt(controlTotString.substring(0, 8));
                    if (coin.equals("PEN")) {
                        totalOperation = 0;
                        if (transferType.equals("220")) {
                            totRec = individualAdd220Pen.size() + 2;
                            totalOperation = individualAdd220Pen.size() / 3;
                            sumAmountPen220 = sumAmountPen220.add(sumOfAmountslc);
                            Amountsum = sumAmountPen220;
                            sumCommPen220 = sumCommPen220.add(sumOfCommlc);
                            Commsum = sumCommPen220;
                            sumCtrlTotsPen220 = sumCtrlTotsPen220 + controlTots;
                            controlTotals = String.valueOf(sumCtrlTotsPen220);
                        }
                        if (transferType.equals("221")) {
                            totRec = individualAdd221Pen.size() + 2;
                            totalOperation = individualAdd221Pen.size() / 3;
                            sumAmountPen221 = sumAmountPen221.add(sumOfAmountslc);
                            Amountsum = sumAmountPen221;
                            sumCommPen221 = sumCommPen221.add(sumOfCommlc);
                            Commsum = sumCommPen221;
                            sumCtrlTotsPen221 = sumCtrlTotsPen221 + controlTots;
                            controlTotals = String.valueOf(sumCtrlTotsPen221);
                        }
                        if (transferType.equals("222")) {
                            totRec = individualAdd222Pen.size() + 2;
                            totalOperation = individualAdd222Pen.size() / 3;
                            sumAmountPen222 = sumAmountPen222.add(sumOfAmountslc);
                            Amountsum = sumAmountPen222;
                            sumCommPen222 = sumCommPen222.add(sumOfCommlc);
                            Commsum = sumCommPen222;
                            sumCtrlTotsPen222 = sumCtrlTotsPen222 + controlTots;
                            controlTotals = String.valueOf(sumCtrlTotsPen222);
                        }
                        if (transferType.equals("223")) {
                            totRec = individualAdd223Pen.size() + 2;
                            totalOperation = individualAdd223Pen.size() / 3;
                            sumAmountPen223 = sumAmountPen223.add(sumOfAmountslc);
                            Amountsum = sumAmountPen223;
                            sumCommPen223 = sumCommPen223.add(sumOfCommlc);
                            Commsum = sumCommPen223;
                            sumCtrlTotsPen223 = sumCtrlTotsPen223 + controlTots;
                            controlTotals = String.valueOf(sumCtrlTotsPen223);
                        }
                        if (transferType.equals("224")) {
                            totRec = individualAdd224Pen.size() + 2;
                            totalOperation = individualAdd224Pen.size() / 3;
                            sumAmountPen224 = sumAmountPen224.add(sumOfAmountslc);
                            Amountsum = sumAmountPen224;
                            sumCommPen224 = sumCommPen224.add(sumOfCommlc);
                            Commsum = sumCommPen224;
                            sumCtrlTotsPen224 = sumCtrlTotsPen224 + controlTots;
                            controlTotals = String.valueOf(sumCtrlTotsPen224);
                        }

                        controlTotalFCpen = sumCtrlTotsPen220 + sumCtrlTotsPen221 + sumCtrlTotsPen222
                                + sumCtrlTotsPen223 + sumCtrlTotsPen224;
                        totalOperationsFCPen = ((individualAdd220Pen.size()) / 3) + ((individualAdd221Pen.size()) / 3)
                                + ((individualAdd222Pen.size()) / 3) + ((individualAdd223Pen.size()) / 3)
                                + ((individualAdd224Pen.size()) / 3);

                    }

                    if (coin.equals("USD")) {
                        totalOperation = 0;
                        if (transferType.equals("220")) {
                            totRec = individualAdd220usd.size() + 2;
                            totalOperation = individualAdd220usd.size() / 3;
                            sumAmountUsd220 = sumAmountUsd220.add(sumOfAmountslc);
                            Amountsum = sumAmountUsd220;
                            sumCommUsd220 = sumCommUsd220.add(sumOfCommlc);
                            Commsum = sumCommUsd220;
                            sumCtrlTotsUsd220 = sumCtrlTotsUsd220 + controlTots;
                            controlTotals = String.valueOf(sumCtrlTotsUsd220);
                        }
                        if (transferType.equals("221")) {
                            totRec = individualAdd221usd.size() + 2;
                            totalOperation = individualAdd221usd.size() / 3;
                            sumAmountUsd221 = sumAmountUsd221.add(sumOfAmountslc);
                            Amountsum = sumAmountUsd221;
                            sumCommUsd221 = sumCommUsd221.add(sumOfCommlc);
                            Commsum = sumCommUsd221;
                            sumCtrlTotsUsd221 = sumCtrlTotsUsd221 + controlTots;
                            controlTotals = String.valueOf(sumCtrlTotsUsd221);
                        }
                        if (transferType.equals("222")) {
                            totRec = individualAdd222usd.size() + 2;
                            totalOperation = individualAdd222usd.size() / 3;
                            sumAmountUsd222 = sumAmountUsd222.add(sumOfAmountslc);
                            Amountsum = sumAmountUsd222;
                            sumCommUsd222 = sumCommUsd222.add(sumOfCommlc);
                            Commsum = sumCommUsd222;
                            sumCtrlTotsUsd222 = sumCtrlTotsUsd222 + controlTots;
                            controlTotals = String.valueOf(sumCtrlTotsUsd222);
                        }
                        if (transferType.equals("223")) {
                            totRec = individualAdd223usd.size() + 2;
                            totalOperation = individualAdd223usd.size() / 3;
                            sumAmountUsd223 = sumAmountUsd223.add(sumOfAmountslc);
                            Amountsum = sumAmountUsd223;
                            sumCommUsd223 = sumCommUsd223.add(sumOfCommlc);
                            Commsum = sumCommUsd223;
                            sumCtrlTotsUsd223 = sumCtrlTotsUsd223 + controlTots;
                            controlTotals = String.valueOf(sumCtrlTotsUsd223);
                        }
                        if (transferType.equals("224")) {
                            totRec = individualAdd224usd.size() + 2;
                            totalOperation = individualAdd224usd.size() / 3;
                            sumAmountUsd224 = sumAmountUsd224.add(sumOfAmountslc);
                            Amountsum = sumAmountUsd224;
                            sumCommUsd224 = sumCommUsd224.add(sumOfCommlc);
                            Commsum = sumCommUsd224;
                            sumCtrlTotsUsd224 = sumCtrlTotsUsd224 + controlTots;
                            controlTotals = String.valueOf(sumCtrlTotsUsd224);

                        }
                        controlTotalFCusd = sumCtrlTotsUsd220 + sumCtrlTotsUsd221 + sumCtrlTotsUsd222
                                + sumCtrlTotsUsd223 + sumCtrlTotsUsd224;
                        totalOperationsFCUsd = ((individualAdd220usd.size()) / 3) + ((individualAdd221usd.size()) / 3)
                                + (individualAdd222usd.size() / 3) + (individualAdd223usd.size() / 3)
                                + (individualAdd224usd.size() / 3);
                    }

                    String totRecs = Integer.toString(totRec); // need mapping

                    // String controlTotals =
                    // payRec.getLocalRefField("L.CR.ENTITY.COD").getValue();
                    /*
                     * try{ controlTotals = "0" + controlTotals.substring(0, 3)
                     * + "0" + controlTotals.substring(3, 6); } catch(Exception
                     * e){
                     * 
                     * } sumOfAmountslc =
                     * Double.parseDouble(payRec.getTransactionamount().getValue
                     * ()) + sumOfAmountslc; BigDecimal Decimal = new
                     * BigDecimal(sumOfAmountslc); String sumOfCommisions = "";
                     * List<DebitchargecomponentClass> commList =
                     * payRec.getDebitchargecomponent();
                     * System.out.println("sumOfCommisions: " +
                     * sumOfCommisions); try { sumOfAmounts =
                     * String.format("%.02f", Decimal); int amt =
                     * sumOfAmounts.indexOf("."); sumOfAmounts =
                     * sumOfAmounts.substring(0,
                     * amt).concat(sumOfAmounts.substring(amt + 1)); if
                     * (!commList.isEmpty()) { sumOfCommlc =
                     * Double.parseDouble(commList.get(1).getDebitchargeamount()
                     * . getValue()) + sumOfCommlc; BigDecimal comDec = new
                     * BigDecimal(sumOfCommlc); sumOfCommisions =
                     * String.format("%.02f", comDec); int commAmt =
                     * sumOfCommisions.indexOf("."); sumOfCommisions =
                     * sumOfCommisions.substring(0, commAmt)
                     * .concat(sumOfCommisions.substring(commAmt + 1)); } }
                     * catch (Exception e) {
                     * 
                     * }
                     */

                    /*
                     * String company = payRec.getCoCode(); CompanyRecord
                     * companyRec = new CompanyRecord(da.getRecord("COMPANY",
                     * company)); String entidadCode =
                     * companyRec.getLocalRefField("L.CR.ENTITY.COD").getValue()
                     * ; String officeCode =
                     * companyRec.getLocalRefField("L.OFFICE.CODE").getValue();
                     * String originEntityLC = StringUtils.leftPad(entidadCode,
                     * 4, "0").concat(StringUtils.leftPad(officeCode, 4, "0"));
                     */
                    String originEntityLC = payRec.getLocalRefField("L.CR.ENTITY.COD").getValue();
                    String free = "";
                    try {
                        // originEntityLC = "0" + originEntityLC.substring(0, 3)
                        // + "0" + originEntityLC.substring(3, 6);

                        controlTotals = controlTotals.substring(0, 8);
                    } catch (Exception e) {

                    }
                    String todayLH = today + "-" + "ENDLOTHEADER";
                    String lotNumberLC = "";
                    try {
																						  
                        archRec = new BciCceHeaderCounterRecord(da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                        // lotNumberLC =
                        // archRec.getFieldName(0).getFieldValue().getValue();
                        if (appcode.equals("TRI")) {
                            lotNumberLC = archRec.getName(0).getValue().getValue();
                        } else if (appcode.equals("TRM")) {
                            lotNumberLC = archRec.getName(1).getValue().getValue();
                        } else if (appcode.equals("TRT")) {
                            lotNumberLC = archRec.getName(2).getValue().getValue();
                        }

                    } catch (Exception e) {

                    }
                    if (lotNumberLC.equals("")) {
                        lotNumberLC = "1";
                    }
                    sumOfAmounts = String.format("%.02f", Amountsum);
                    sumOfCommisions = String.format("%.02f", Commsum);
                    int index = sumOfAmounts.indexOf(".");
                    sumOfAmounts = sumOfAmounts.substring(0, index).concat(sumOfAmounts.substring(index + 1));
                    int commindex = sumOfCommisions.indexOf(".");
                    sumOfCommisions = sumOfCommisions.substring(0, commindex)
                            .concat(sumOfCommisions.substring(commindex + 1));
                    String totalOperations = Integer.toString(totalOperation);
                    String endOfcontrol = typeRegLC.concat(StringUtils.leftPad(totRecs, 10, "0"))
                            .concat(StringUtils.leftPad(controlTotals, 15, "0"))
                            .concat(StringUtils.leftPad(totalOperations, 15, "0"))
                            .concat(StringUtils.leftPad(sumOfAmounts, 15, "0"))
                            .concat(StringUtils.leftPad(sumOfCommisions, 15, "0"))
                            .concat(StringUtils.rightPad(free, 23, ""))
                            .concat(StringUtils.leftPad(originEntityLC, 8, "0"))
                            .concat(StringUtils.leftPad(lotNumber, 7, "0")).concat(StringUtils.rightPad(free, 91, ""));

                    /*
                     * int Lec = 100; Lec = endOfcontrol.length(); Lec = Lec +
                     * 23; String endofctrl = String.format("%" + -Lec + "s",
                     * endOfcontrol); String endofctrlstr =
                     * endofctrl.concat(originEntityLC).concat(StringUtils.
                     * leftPad( lotNumberLC, 7, "0")); int len = 100; len =
                     * endOfcontrol.length(); len = len + 91; String finalStr =
                     * String.format("%" + -len + "s", endofctrlstr);
                     */
                    String endofcontrolStr = endOfcontrol;
                    totalOperationsFC = totalOperationsFC + totalOperation;
                    if (coin.equals("USD")) {

                        if (transferType.equals("220")) {
                            endOfContrl220Usd = endofcontrolStr;
                            if (lotend220FlagUsd == 0) {
                                // lotendHeader220usd.add(endofcontrolStr);

                                lotend220FlagUsd = 1;
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);

                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                                } catch (Exception e) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                valueLHcnt = valueLHcnt + 1;
                                String Value = String.valueOf(valueLHcnt);
                                fldclass.setValue(Value);
                                if (appcode.equals("TRI")) {
                                    archwriteRec.setName(fldclass, 0);
                                } else if (appcode.equals("TRM")) {
                                    archwriteRec.setName(fldclass, 1);
                                } else if (appcode.equals("TRT")) {
                                    archwriteRec.setName(fldclass, 2);
                                }
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(todayLH, archwriteRec);
                                } catch (Exception e) {

                                }
                            }
                        }

                        if (transferType.equals("221")) {

                            endOfContrl221Usd = endofcontrolStr;
                            if (lotend221FlagUsd == 0) {
                                // lotendHeader221usd.add(endofcontrolStr);

                                lotend221FlagUsd = 1;
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);

                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                                } catch (Exception e) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                valueLHcnt = valueLHcnt + 1;
                                String Value = String.valueOf(valueLHcnt);
                                fldclass.setValue(Value);
                                if (appcode.equals("TRI")) {
                                    archwriteRec.setName(fldclass, 0);
                                } else if (appcode.equals("TRM")) {
                                    archwriteRec.setName(fldclass, 1);
                                } else if (appcode.equals("TRT")) {
                                    archwriteRec.setName(fldclass, 2);
                                }
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(todayLH, archwriteRec);
                                } catch (Exception e) {

                                }
                            }
                        }
                        if (transferType.equals("222")) {

                            endOfContrl222Usd = endofcontrolStr;
                            if (lotend222FlagUsd == 0) {
                                // lotendHeader222usd.add(endofcontrolStr);

                                lotend222FlagUsd = 1;
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);

                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                                } catch (Exception e) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                valueLHcnt = valueLHcnt + 1;
                                String Value = String.valueOf(valueLHcnt);
                                fldclass.setValue(Value);
                                if (appcode.equals("TRI")) {
                                    archwriteRec.setName(fldclass, 0);
                                } else if (appcode.equals("TRM")) {
                                    archwriteRec.setName(fldclass, 1);
                                } else if (appcode.equals("TRT")) {
                                    archwriteRec.setName(fldclass, 2);
                                }
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(todayLH, archwriteRec);
                                } catch (Exception e) {

                                }
                            }
                        }
                        if (transferType.equals("223")) {

                            endOfContrl223Usd = endofcontrolStr;
                            if (lotend223FlagUsd == 0) {
                                // lotendHeader223usd.add(endofcontrolStr);

                                lotend223FlagUsd = 1;
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);

                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                                } catch (Exception e) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                valueLHcnt = valueLHcnt + 1;
                                String Value = String.valueOf(valueLHcnt);
                                fldclass.setValue(Value);
                                if (appcode.equals("TRI")) {
                                    archwriteRec.setName(fldclass, 0);
                                } else if (appcode.equals("TRM")) {
                                    archwriteRec.setName(fldclass, 1);
                                } else if (appcode.equals("TRT")) {
                                    archwriteRec.setName(fldclass, 2);
                                }
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(todayLH, archwriteRec);
                                } catch (Exception e) {

                                }
                            }
                        }
                        if (transferType.equals("224")) {

                            endOfContrl224Usd = endofcontrolStr;
                            if (lotend224FlagUsd == 0) {

                                lotend224FlagUsd = 1;
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                                } catch (Exception e) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                valueLHcnt = valueLHcnt + 1;
                                String Value = String.valueOf(valueLHcnt);
                                fldclass.setValue(Value);
                                if (appcode.equals("TRI")) {
                                    archwriteRec.setName(fldclass, 0);
                                } else if (appcode.equals("TRM")) {
                                    archwriteRec.setName(fldclass, 1);
                                } else if (appcode.equals("TRT")) {
                                    archwriteRec.setName(fldclass, 2);
                                }
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(todayLH, archwriteRec);
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                    if (coin.equals("PEN")) {

                        if (transferType.equals("220")) {
                            endOfContrl220Pen = endofcontrolStr;
                            if (lotend220FlagPen == 0) {
                                // lotendHeader220Pen.add(endofcontrolStr);
                                lotend220FlagPen = 1;
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                                } catch (Exception e) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                valueLHcnt = valueLHcnt + 1;
                                String Value = String.valueOf(valueLHcnt);
                                fldclass.setValue(Value);
                                if (appcode.equals("TRI")) {
                                    archwriteRec.setName(fldclass, 0);
                                } else if (appcode.equals("TRM")) {
                                    archwriteRec.setName(fldclass, 1);
                                } else if (appcode.equals("TRT")) {
                                    archwriteRec.setName(fldclass, 2);
                                }
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(todayLH, archwriteRec);
                                } catch (Exception e) {

                                }
                            }
                        }
                        if (transferType.equals("221")) {
                            endOfContrl221Pen = endofcontrolStr;
                            if (lotend221FlagPen == 0) {
                                lotendHeader221Pen.add(endofcontrolStr);
                                lotend221FlagPen = 1;
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                                } catch (Exception e) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                valueLHcnt = valueLHcnt + 1;
                                String Value = String.valueOf(valueLHcnt);
                                fldclass.setValue(Value);
                                if (appcode.equals("TRI")) {
                                    archwriteRec.setName(fldclass, 0);
                                } else if (appcode.equals("TRM")) {
                                    archwriteRec.setName(fldclass, 1);
                                } else if (appcode.equals("TRT")) {
                                    archwriteRec.setName(fldclass, 2);
                                }
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(todayLH, archwriteRec);
                                } catch (Exception e) {

                                }
                            }
                        }
                        if (transferType.equals("222")) {
                            endOfContrl222Pen = endofcontrolStr;
                            if (lotend222FlagPen == 0) {
                                lotendHeader222Pen.add(endofcontrolStr);
                                lotend222FlagPen = 1;
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                                } catch (Exception e) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                valueLHcnt = valueLHcnt + 1;
                                String Value = String.valueOf(valueLHcnt);
                                fldclass.setValue(Value);
                                if (appcode.equals("TRI")) {
                                    archwriteRec.setName(fldclass, 0);
                                } else if (appcode.equals("TRM")) {
                                    archwriteRec.setName(fldclass, 1);
                                } else if (appcode.equals("TRT")) {
                                    archwriteRec.setName(fldclass, 2);
                                }
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(todayLH, archwriteRec);
                                } catch (Exception e) {

                                }
                            }
                        }
                        if (transferType.equals("223")) {
                            endOfContrl223Pen = endofcontrolStr;
                            if (lotend223FlagPen == 0) {
                                lotendHeader223Pen.add(endofcontrolStr);
                                lotend223FlagPen = 1;
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                                } catch (Exception e) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                valueLHcnt = valueLHcnt + 1;
                                String Value = String.valueOf(valueLHcnt);
                                fldclass.setValue(Value);
                                if (appcode.equals("TRI")) {
                                    archwriteRec.setName(fldclass, 0);
                                } else if (appcode.equals("TRM")) {
                                    archwriteRec.setName(fldclass, 1);
                                } else if (appcode.equals("TRT")) {
                                    archwriteRec.setName(fldclass, 2);
                                }
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(todayLH, archwriteRec);
                                } catch (Exception e) {

                                }
                            }
                        }
                        if (transferType.equals("224")) {
                            endOfContrl224Pen = endofcontrolStr;
                            if (lotend224FlagPen == 0) {
                                lotendHeader224Pen.add(endofcontrolStr);
                                lotend224FlagPen = 1;
                                BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                                try {
                                    archwriteRec = new BciCceHeaderCounterRecord(
                                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayLH));
                                } catch (Exception e) {

                                }
                                NameClass fldclass = new NameClass();
                                fldclass.setName(appcode);
                                valueLHcnt = valueLHcnt + 1;
                                String Value = String.valueOf(valueLHcnt);
                                fldclass.setValue(Value);
                                if (appcode.equals("TRI")) {
                                    archwriteRec.setName(fldclass, 0);
                                } else if (appcode.equals("TRM")) {
                                    archwriteRec.setName(fldclass, 1);
                                } else if (appcode.equals("TRT")) {
                                    archwriteRec.setName(fldclass, 2);
                                }
                                BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                                try {
                                    tblRec.write(todayLH, archwriteRec);
                                } catch (Exception e) {

                                }
                            }
                        }

                    }
                    // ************** file end control************************//

                    BciCceMappingFieldValuesRecord mapRecwrite = new BciCceMappingFieldValuesRecord(this);
                    // String prvdate = bciFldRec.getDate().getValue();
                    // String prvStatus = bciFldRec.getStatus().getValue();
                    String oeId = bciFldRec.getOeId().getValue();
                    List<MapFieldTypeClass> prvfieldListmap = bciFldRec.getMapFieldType();
                    MapFieldTypeClass prvmapclass = new MapFieldTypeClass();
                    MapFieldTypeClass prvmapclass1 = new MapFieldTypeClass();
                    for (MapFieldTypeClass mapId : prvfieldListmap) {
                        String type = mapId.getMapFieldType().getValue();

                        if (type.equals("INDIVIDUAL")) {
                            prvmapclass.setMapFieldType(type);
                            String individualRegistrationfisrt = mapId.getMapFieldVal().get(0).getValue();
                            String individualRegistrationsec = mapId.getMapFieldVal().get(1).getValue();

                            prvmapclass.setMapFieldVal(individualRegistrationfisrt, 0);
                            prvmapclass.setMapFieldVal(individualRegistrationsec, 1);

                            mapRecwrite.setMapFieldType(prvmapclass, 0);
                        }
                        if (type.equals("ADDITIONAL")) {
                            prvmapclass1.setMapFieldType(type);
                            String additionalRegistrationfirst = mapId.getMapFieldVal().get(0).getValue();
                            String additionalRegistrationSec = mapId.getMapFieldVal().get(1).getValue();

                            prvmapclass1.setMapFieldVal(additionalRegistrationfirst, 0);
                            prvmapclass1.setMapFieldVal(additionalRegistrationSec, 1);

                            mapRecwrite.setMapFieldType(prvmapclass1, 1);
                        }
                    }

                    // ************************UPDATE PREVIOUS
                    // RECORD********************
                    mapRecwrite.setOeId(oeId);
                    mapRecwrite.setDate(today);
                    mapRecwrite.setInRef(sysId);
                    mapRecwrite.setTxnType(appcode);
                    mapRecwrite.setStatus("CONFIRMED_IN");
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    mapRecwrite.setTime(dtf.format(now));
                    BciCceMappingFieldValuesTable mapTbl = new BciCceMappingFieldValuesTable(this);
                    // controlTots =
                    // payRec.getLocalRefField("L.CR.ENTITY.COD").getValue();
                    /*
                     * try{ controlTots = "0" + controlTots.substring(0, 3) +
                     * "0" + controlTots.substring(3, 6); } catch(Exception e){
                     * 
                     * }
                     */
                    try {
                        mapTbl.write(recid, mapRecwrite);
                    } catch (T24IOException e) {

                    }
                    if (coin.equals("USD")) {
                        sumOfAmountsfcusd = Double.parseDouble(payRec.getTransactionamount().getValue())
                                + sumOfAmountsfcusd;
                        BigDecimal Decimalfc = new BigDecimal(sumOfAmountsfcusd);

                        sumAmountsusd = String.format("%.02f", Decimalfc);
                        int amt = sumAmountsusd.indexOf(".");
                        sumAmountsusd = sumAmountsusd.substring(0, amt).concat(sumAmountsusd.substring(amt + 1));
                        try {

                            List<DebitchargecomponentClass> commListfc = payRec.getDebitchargecomponent();
                            if (!commListfc.isEmpty()) {
                                sumOfCommisionsECusd = Double.parseDouble(
                                        commListfc.get(1).getDebitchargeamount().getValue()) + sumOfCommisionsECusd;
                                BigDecimal comDec = new BigDecimal(sumOfCommisionsECusd);
                                commisionAmountusd = String.format("%.02f", comDec);
                                int commAmt = commisionAmountusd.indexOf(".");
                                commisionAmountusd = commisionAmountusd.substring(0, commAmt)
                                        .concat(commisionAmountusd.substring(commAmt + 1));
                            }

                        } catch (Exception e) {

                        }

                    }
                    System.out.println("initial amount variable: " + sumOfAmountsfcpen);
                    if (coin.equals("PEN")) {
                        try {
                            sumOfAmountsfcpen = Double.parseDouble(payRec.getTransactionamount().getValue())
                                    + sumOfAmountsfcpen;
                            // System.out.println("amount from pp: " +
                            // transactionAmt);
                            BigDecimal Decimalfc = new BigDecimal(sumOfAmountsfcpen);
                            sumAmountspen = String.format("%.02f", Decimalfc);
                            int amtFc = sumAmountspen.indexOf(".");
                            sumAmountspen = sumAmountspen.substring(0, amtFc)
                                    .concat(sumAmountspen.substring(amtFc + 1));

                            List<DebitchargecomponentClass> commListfc = payRec.getDebitchargecomponent();
                            if (!commListfc.isEmpty()) {
                                sumOfCommisionsECpen = Double.parseDouble(
                                        commListfc.get(1).getDebitchargeamount().getValue()) + sumOfCommisionsECpen;
                                BigDecimal comDec = new BigDecimal(sumOfCommisionsECpen);
                                commisionAmountpen = String.format("%.02f", comDec);
                                int commAmt = commisionAmountpen.indexOf(".");
                                commisionAmountpen = commisionAmountpen.substring(0, commAmt)
                                        .concat(commisionAmountpen.substring(commAmt + 1));
                            }
                            // commisionAmountpen =
                            // commisionAmountpen.toString();
                        } catch (Exception e) {

                        }
                    }

                }

                
                String typeRegEC = "";
                BciCceInterfaceParameterRecord bciParamRecfc = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.FILECONTROL"));
                List<FieldNameClass> fldList3 = bciParamRecfc.getFieldName();
                for (FieldNameClass fieldid : fldList3) {
                    String fieldName = fieldid.getFieldName().getValue();
                    if (fieldName.equals("TYPE.REGISTER")) {
                        typeRegEC = fieldid.getFieldValue().getValue();
                    }

                }
                if (!fileHeaderUSD.equals("")) {
                    int totalLot = lotHeader220usd.size() + lotHeader221usd.size() + lotHeader222usd.size()
                            + lotHeader223usd.size() + lotHeader224usd.size();
                    String totalLots = Integer.toString(totalLot);
                    int individualCnt = individualAdd220usd.size() + individualAdd221usd.size()
                            + individualAdd222usd.size() + individualAdd223usd.size() + individualAdd224usd.size();// need
                    int totCnt = (totalLot * 2) + (individualCnt) + 2;
                    String totalRecs = Integer.toString(totCnt);
                    String ControlTots = String.valueOf(controlTotalFCusd);
                    String totOperation = "";
                    totOperation = Integer.toString(totalOperationsFCUsd);
                    /*
                     * try { int sumlen = sumAmountsusd.indexOf(".");
                     * sumAmountsusd = sumAmountsusd.substring(0,
                     * sumlen).concat(sumAmountsusd.substring(sumlen + 1)); int
                     * comlen = commisionAmountusd.indexOf(".");
                     * commisionAmountusd = commisionAmountusd.substring(0,
                     * comlen) .concat(commisionAmountusd.substring(comlen +
                     * 1)); } catch (Exception e) {
                     * 
                     * }
                     */
                    String fileControlStr = typeRegEC.concat(StringUtils.leftPad(totalLots, 6, "0"))
                            .concat(StringUtils.leftPad(totalRecs, 10, "0"))
                            .concat(StringUtils.leftPad(ControlTots, 15, "0"))
                            .concat(StringUtils.leftPad(totOperation, 15, "0"))
                            .concat(StringUtils.leftPad(sumAmountsusd, 15, "0"))
                            .concat(StringUtils.leftPad(commisionAmountusd, 15, "0"));
                    int Lfc = 100;
                    Lfc = fileControlStr.length();
                    Lfc = Lfc + 123;
                    

                    fileendUsd.add(fileControlStr);
                }
                System.out.println("AMOUNT 1 : " + sumAmountspen);
                if (!fileHeaderPEN.equals("")) {
                    int totalLot = lotHeader220.size() + lotHeader221.size() + lotHeader222.size() + lotHeader223.size()
                            + lotHeader224.size();
                    String totalLots = Integer.toString(totalLot);
                    int individualCnt = individualAdd220Pen.size() + individualAdd221Pen.size()
                            + individualAdd222Pen.size() + individualAdd223Pen.size() + individualAdd224Pen.size();// need
                                                                                                                   // mapping
                    int totCnt = (totalLot * 2) + (individualCnt) + 2;
                    String totalRecs = Integer.toString(totCnt);
                    String ControlTots = String.valueOf(controlTotalFCpen);
                    String totOperation = "";
                    totOperation = Integer.toString(totalOperationsFCPen);
                    String free = "";

                    String fileControlStr = typeRegEC.concat(StringUtils.leftPad(totalLots, 6, "0"))
                            .concat(StringUtils.leftPad(totalRecs, 10, "0"))
                            .concat(StringUtils.leftPad(ControlTots, 15, "0"))
                            .concat(StringUtils.leftPad(totOperation, 15, "0"))
                            .concat(StringUtils.leftPad(sumAmountspen, 15, "0"))
                            .concat(StringUtils.leftPad(commisionAmountpen, 15, "0"))
                            .concat(StringUtils.rightPad(free, 123, ""));

                    System.out.println("AMOUNT 2 : " + sumAmountspen);


                    fileendPen.add(fileControlStr);

                }

                // Modificaciones para individualcount

                int individualCntUSD = individualAdd220usd.size() + individualAdd221usd.size()
                        + individualAdd222usd.size() + individualAdd223usd.size() + individualAdd224usd.size();
                int individualCntPEN = individualAdd220Pen.size() + individualAdd221Pen.size()
                        + individualAdd222Pen.size() + individualAdd223Pen.size() + individualAdd224Pen.size();
                String indHeaderCntDate = today + "-" + "INDIVIDUALCNT";
                String StrIndivCount = "";
                Integer IntIndivcount = 0;
                BciCceHeaderCounterRecord archRecIndCount = new BciCceHeaderCounterRecord(this);
                try {
                    archRecIndCount = new BciCceHeaderCounterRecord(
                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", indHeaderCntDate));
                    if (appcode.equals("TRI")) {
                        StrIndivCount = archRecIndCount.getName(0).getValue().getValue();
                    } else if (appcode.equals("TRM")) {
                        StrIndivCount = archRecIndCount.getName(1).getValue().getValue();
                    } else if (appcode.equals("TRT")) {
                        StrIndivCount = archRecIndCount.getName(2).getValue().getValue();
                    }
                } catch (Exception e) {
                }

                if (individualCntUSD > 0 || individualCntPEN > 0) {
                    if (StrIndivCount.equals("") || StrIndivCount.equals("0")) {
                        IntIndivcount = 1;
                    } else {
                        IntIndivcount = Integer.parseInt(StrIndivCount);
                    }

                    for (int indice = 0; indice < individualAdd220Pen.size(); indice++) {
                        StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                        if (individualAdd220Pen.get(indice).substring(0, 1).equals("6")) {
                            String secuencia = StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd220Pen.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd220Pen.remove(indice);
                            individualAdd220Pen.add(indice, linemod.toString());
                            IntIndivcount += 1;

                            StringBuilder strbline2 = new StringBuilder(individualAdd220Pen.get(indice));
                            StringBuilder linemod2 = strbline2.replace(49, 64, "000000000000000");
                            individualAdd220Pen.remove(indice);
                            individualAdd220Pen.add(indice, linemod2.toString());
                        }
                        if (individualAdd220Pen.get(indice).substring(0, 1).equals("7")) {
                            Integer temp = IntIndivcount - 1;
                            String secuencia = StringUtils.leftPad(temp.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd220Pen.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd220Pen.remove(indice);
                            individualAdd220Pen.add(indice, linemod.toString());
                        }
                    }
                    for (int indice = 0; indice < individualAdd221Pen.size(); indice++) {
                        StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                        if (individualAdd221Pen.get(indice).substring(0, 1).equals("6")) {
                            String secuencia = StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd221Pen.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd221Pen.remove(indice);
                            individualAdd221Pen.add(indice, linemod.toString());
                            IntIndivcount += 1;

                            StringBuilder strbline2 = new StringBuilder(individualAdd221Pen.get(indice));
                            StringBuilder linemod2 = strbline2.replace(49, 64, "000000000000000");
                            individualAdd221Pen.remove(indice);
                            individualAdd221Pen.add(indice, linemod2.toString());
                        }
                        if (individualAdd221Pen.get(indice).substring(0, 1).equals("7")) {
                            Integer temp = IntIndivcount - 1;
                            String secuencia = StringUtils.leftPad(temp.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd221Pen.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd221Pen.remove(indice);
                            individualAdd221Pen.add(indice, linemod.toString());
                        }
                    }
                    for (int indice = 0; indice < individualAdd222Pen.size(); indice++) {
                        StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                        if (individualAdd222Pen.get(indice).substring(0, 1).equals("6")) {
                            String secuencia = StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd222Pen.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd222Pen.remove(indice);
                            individualAdd222Pen.add(indice, linemod.toString());
                            IntIndivcount += 1;

                            StringBuilder strbline2 = new StringBuilder(individualAdd222Pen.get(indice));
                            StringBuilder linemod2 = strbline2.replace(49, 64, "000000000000000");
                            individualAdd222Pen.remove(indice);
                            individualAdd222Pen.add(indice, linemod2.toString());
                        }
                        if (individualAdd222Pen.get(indice).substring(0, 1).equals("7")) {
                            Integer temp = IntIndivcount - 1;
                            String secuencia = StringUtils.leftPad(temp.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd222Pen.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd222Pen.remove(indice);
                            individualAdd222Pen.add(indice, linemod.toString());
                        }
                    }
                    for (int indice = 0; indice < individualAdd223Pen.size(); indice++) {
                        StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                        if (individualAdd223Pen.get(indice).substring(0, 1).equals("6")) {
                            String secuencia = StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd223Pen.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd223Pen.remove(indice);
                            individualAdd223Pen.add(indice, linemod.toString());
                            IntIndivcount += 1;

                            StringBuilder strbline2 = new StringBuilder(individualAdd223Pen.get(indice));
                            StringBuilder linemod2 = strbline2.replace(49, 64, "000000000000000");
                            individualAdd223Pen.remove(indice);
                            individualAdd223Pen.add(indice, linemod2.toString());
                        }
                        if (individualAdd223Pen.get(indice).substring(0, 1).equals("7")) {
                            Integer temp = IntIndivcount - 1;
                            String secuencia = StringUtils.leftPad(temp.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd223Pen.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd223Pen.remove(indice);
                            individualAdd223Pen.add(indice, linemod.toString());
                        }
                    }
                    for (int indice = 0; indice < individualAdd224Pen.size(); indice++) {
                        StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                        if (individualAdd224Pen.get(indice).substring(0, 1).equals("6")) {
                            String secuencia = StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd224Pen.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd224Pen.remove(indice);
                            individualAdd224Pen.add(indice, linemod.toString());
                            IntIndivcount += 1;

                            StringBuilder strbline2 = new StringBuilder(individualAdd224Pen.get(indice));
                            StringBuilder linemod2 = strbline2.replace(49, 64, "000000000000000");
                            individualAdd224Pen.remove(indice);
                            individualAdd224Pen.add(indice, linemod2.toString());
                        }
                        if (individualAdd224Pen.get(indice).substring(0, 1).equals("7")) {
                            Integer temp = IntIndivcount - 1;
                            String secuencia = StringUtils.leftPad(temp.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd224Pen.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd224Pen.remove(indice);
                            individualAdd224Pen.add(indice, linemod.toString());
                        }
                    }

                    for (int indice = 0; indice < individualAdd220usd.size(); indice++) {
                        StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                        if (individualAdd220usd.get(indice).substring(0, 1).equals("6")) {
                            String secuencia = StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd220usd.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd220usd.remove(indice);
                            individualAdd220usd.add(indice, linemod.toString());
                            IntIndivcount += 1;

                            StringBuilder strbline2 = new StringBuilder(individualAdd220usd.get(indice));
                            StringBuilder linemod2 = strbline2.replace(49, 64, "000000000000000");
                            individualAdd220usd.remove(indice);
                            individualAdd220usd.add(indice, linemod2.toString());
                        }
                        if (individualAdd220usd.get(indice).substring(0, 1).equals("7")) {
                            Integer temp = IntIndivcount - 1;
                            String secuencia = StringUtils.leftPad(temp.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd220usd.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd220usd.remove(indice);
                            individualAdd220usd.add(indice, linemod.toString());
                        }
                    }

                    for (int indice = 0; indice < individualAdd221usd.size(); indice++) {
                        StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                        if (individualAdd221usd.get(indice).substring(0, 1).equals("6")) {
                            String secuencia = StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd221usd.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd221usd.remove(indice);
                            individualAdd221usd.add(indice, linemod.toString());
                            IntIndivcount += 1;

                            StringBuilder strbline2 = new StringBuilder(individualAdd221usd.get(indice));
                            StringBuilder linemod2 = strbline2.replace(49, 64, "000000000000000");
                            individualAdd221usd.remove(indice);
                            individualAdd221usd.add(indice, linemod2.toString());
                        }
                        if (individualAdd221usd.get(indice).substring(0, 1).equals("7")) {
                            Integer temp = IntIndivcount - 1;
                            String secuencia = StringUtils.leftPad(temp.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd221usd.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd221usd.remove(indice);
                            individualAdd221usd.add(indice, linemod.toString());
                        }
                    }

                    for (int indice = 0; indice < individualAdd222usd.size(); indice++) {
                        StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                        if (individualAdd222usd.get(indice).substring(0, 1).equals("6")) {
                            String secuencia = StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd222usd.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd222usd.remove(indice);
                            individualAdd222usd.add(indice, linemod.toString());
                            IntIndivcount += 1;

                            StringBuilder strbline2 = new StringBuilder(individualAdd222usd.get(indice));
                            StringBuilder linemod2 = strbline2.replace(49, 64, "000000000000000");
                            individualAdd222usd.remove(indice);
                            individualAdd222usd.add(indice, linemod2.toString());
                        }
                        if (individualAdd222usd.get(indice).substring(0, 1).equals("7")) {
                            Integer temp = IntIndivcount - 1;
                            String secuencia = StringUtils.leftPad(temp.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd222usd.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd222usd.remove(indice);
                            individualAdd222usd.add(indice, linemod.toString());
                        }
                    }

                    for (int indice = 0; indice < individualAdd223usd.size(); indice++) {
                        StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                        if (individualAdd223usd.get(indice).substring(0, 1).equals("6")) {
                            String secuencia = StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd223usd.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd223usd.remove(indice);
                            individualAdd223usd.add(indice, linemod.toString());
                            IntIndivcount += 1;

                            StringBuilder strbline2 = new StringBuilder(individualAdd223usd.get(indice));
                            StringBuilder linemod2 = strbline2.replace(49, 64, "000000000000000");
                            individualAdd223usd.remove(indice);
                            individualAdd223usd.add(indice, linemod2.toString());
                        }
                        if (individualAdd223usd.get(indice).substring(0, 1).equals("7")) {
                            Integer temp = IntIndivcount - 1;
                            String secuencia = StringUtils.leftPad(temp.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd223usd.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd223usd.remove(indice);
                            individualAdd223usd.add(indice, linemod.toString());
                        }
                    }

                    for (int indice = 0; indice < individualAdd224usd.size(); indice++) {
                        StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                        if (individualAdd224usd.get(indice).substring(0, 1).equals("6")) {
                            String secuencia = StringUtils.leftPad(IntIndivcount.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd224usd.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd224usd.remove(indice);
                            individualAdd224usd.add(indice, linemod.toString());
                            IntIndivcount += 1;

                            StringBuilder strbline2 = new StringBuilder(individualAdd224usd.get(indice));
                            StringBuilder linemod2 = strbline2.replace(49, 64, "000000000000000");
                            individualAdd224usd.remove(indice);
                            individualAdd224usd.add(indice, linemod2.toString());
                        }
                        if (individualAdd224usd.get(indice).substring(0, 1).equals("7")) {
                            Integer temp = IntIndivcount - 1;
                            String secuencia = StringUtils.leftPad(temp.toString(), 7, "0");
                            StringBuilder strbline = new StringBuilder(individualAdd224usd.get(indice));
                            StringBuilder linemod = strbline.replace(193, 200, secuencia);
                            individualAdd224usd.remove(indice);
                            individualAdd224usd.add(indice, linemod.toString());
                        }
                    }

                    NameClass fldclassIndCount = new NameClass();
                    fldclassIndCount.setName(appcode);
                    fldclassIndCount.setValue(IntIndivcount.toString());
                    if (appcode.equals("TRI")) {
                        archRecIndCount.setName(fldclassIndCount, 0);
                    } else if (appcode.equals("TRM")) {
                        archRecIndCount.setName(fldclassIndCount, 1);
                    } else if (appcode.equals("TRT")) {
                        archRecIndCount.setName(fldclassIndCount, 2);
                    }
                    BciCceHeaderCounterTable tblRecCount = new BciCceHeaderCounterTable(this);
                    try {
                        tblRecCount.write(indHeaderCntDate, archRecIndCount);
                    } catch (Exception e) {

                    }
                }

                // Modificaciones para LOTHEADERCOUNT

                String indHeaderLotCntDate = today + "-" + "LOTHEADERCNT";
                String StrHedLotCount = "";
                Integer IntHedLotCount = 0;
                BciCceHeaderCounterRecord archRecLotHedCount = new BciCceHeaderCounterRecord(this);
                try {
                    archRecLotHedCount = new BciCceHeaderCounterRecord(
                            da.getRecord("EB.BCI.CCE.HEADER.COUNTER", indHeaderLotCntDate));
                    StrHedLotCount = archRecLotHedCount.getName(0).getValue().getValue();
                } catch (Exception e) {
                }

                if (StrHedLotCount.equals("") || StrHedLotCount.equals("0")) {
                    IntHedLotCount = 1;
                } else {
                    IntHedLotCount = Integer.parseInt(StrHedLotCount);
                }

                for (int indice = 0; indice < lotHeader220.size(); indice++) {
                    StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                    if (lotHeader220.get(indice).substring(0, 1).equals("5")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(lotHeader220.get(indice));
                        StringBuilder linemod = strbline.replace(93, 100, secuencia);
                        lotHeader220.remove(indice);
                        lotHeader220.add(indice, linemod.toString());

                    }
                    if (endOfContrl220Pen.substring(0, 1).equals("8")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(endOfContrl220Pen);
                        StringBuilder linemod = strbline.replace(102, 109, secuencia);
                        endOfContrl220Pen = linemod.toString();
                        IntHedLotCount += 1;
                    }

                }
                for (int indice = 0; indice < lotHeader221.size(); indice++) {
                    StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                    if (lotHeader221.get(indice).substring(0, 1).equals("5")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(lotHeader221.get(indice));
                        StringBuilder linemod = strbline.replace(93, 100, secuencia);
                        lotHeader221.remove(indice);
                        lotHeader221.add(indice, linemod.toString());
                    }
                    if (endOfContrl221Pen.substring(0, 1).equals("8")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(endOfContrl221Pen);
                        StringBuilder linemod = strbline.replace(102, 109, secuencia);
                        endOfContrl221Pen = linemod.toString();
                        IntHedLotCount += 1;
                    }
                }
                for (int indice = 0; indice < lotHeader222.size(); indice++) {
                    StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                    if (lotHeader222.get(indice).substring(0, 1).equals("5")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(lotHeader222.get(indice));
                        StringBuilder linemod = strbline.replace(93, 100, secuencia);
                        lotHeader222.remove(indice);
                        lotHeader222.add(indice, linemod.toString());
                    }
                    if (endOfContrl222Pen.substring(0, 1).equals("8")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(endOfContrl222Pen);
                        StringBuilder linemod = strbline.replace(102, 109, secuencia);
                        endOfContrl222Pen = linemod.toString();
                        IntHedLotCount += 1;
                    }
                }
                for (int indice = 0; indice < lotHeader223.size(); indice++) {
                    StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                    if (lotHeader223.get(indice).substring(0, 1).equals("5")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(lotHeader223.get(indice));
                        StringBuilder linemod = strbline.replace(93, 100, secuencia);
                        lotHeader223.remove(indice);
                        lotHeader223.add(indice, linemod.toString());
                    }
                    if (endOfContrl223Pen.substring(0, 1).equals("8")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(endOfContrl223Pen);
                        StringBuilder linemod = strbline.replace(102, 109, secuencia);
                        endOfContrl223Pen = linemod.toString();
                        IntHedLotCount += 1;
                    }
                }
                for (int indice = 0; indice < lotHeader224.size(); indice++) {
                    StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                    if (lotHeader224.get(indice).substring(0, 1).equals("5")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(lotHeader224.get(indice));
                        StringBuilder linemod = strbline.replace(93, 100, secuencia);
                        lotHeader224.remove(indice);
                        lotHeader224.add(indice, linemod.toString());
                    }
                    if (endOfContrl224Pen.substring(0, 1).equals("8")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(endOfContrl224Pen);
                        StringBuilder linemod = strbline.replace(102, 109, secuencia);
                        endOfContrl224Pen = linemod.toString();
                        IntHedLotCount += 1;
                    }
                }

                for (int indice = 0; indice < lotHeader220usd.size(); indice++) {
                    StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                    if (lotHeader220usd.get(indice).substring(0, 1).equals("5")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(lotHeader220usd.get(indice));
                        StringBuilder linemod = strbline.replace(93, 100, secuencia);
                        lotHeader220usd.remove(indice);
                        lotHeader220usd.add(indice, linemod.toString());
                    }
                    if (endOfContrl220Usd.substring(0, 1).equals("8")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(endOfContrl220Usd);
                        StringBuilder linemod = strbline.replace(102, 109, secuencia);
                        endOfContrl220Usd = linemod.toString();
                        IntHedLotCount += 1;
                    }
                }

                for (int indice = 0; indice < lotHeader221usd.size(); indice++) {
                    StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                    if (lotHeader221usd.get(indice).substring(0, 1).equals("5")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(lotHeader221usd.get(indice));
                        StringBuilder linemod = strbline.replace(93, 100, secuencia);
                        lotHeader221usd.remove(indice);
                        lotHeader221usd.add(indice, linemod.toString());
                    }
                    if (endOfContrl221Usd.substring(0, 1).equals("8")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(endOfContrl221Usd);
                        StringBuilder linemod = strbline.replace(102, 109, secuencia);
                        endOfContrl221Usd = linemod.toString();
                        IntHedLotCount += 1;
                    }
                }

                for (int indice = 0; indice < lotHeader222usd.size(); indice++) {
                    StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                    if (lotHeader222usd.get(indice).substring(0, 1).equals("5")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(lotHeader222usd.get(indice));
                        StringBuilder linemod = strbline.replace(93, 100, secuencia);
                        lotHeader222usd.remove(indice);
                        lotHeader222usd.add(indice, linemod.toString());
                    }
                    if (endOfContrl222Usd.substring(0, 1).equals("8")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(endOfContrl222Usd);
                        StringBuilder linemod = strbline.replace(102, 109, secuencia);
                        endOfContrl222Usd = linemod.toString();
                        IntHedLotCount += 1;
                    }
                }

                for (int indice = 0; indice < lotHeader223usd.size(); indice++) {
                    StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                    if (lotHeader223usd.get(indice).substring(0, 1).equals("5")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(lotHeader223usd.get(indice));
                        StringBuilder linemod = strbline.replace(93, 100, secuencia);
                        lotHeader223usd.remove(indice);
                        lotHeader223usd.add(indice, linemod.toString());
                    }
                    if (endOfContrl223Usd.substring(0, 1).equals("8")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(endOfContrl223Usd);
                        StringBuilder linemod = strbline.replace(102, 109, secuencia);
                        endOfContrl223Usd = linemod.toString();
                        IntHedLotCount += 1;
                    }
                }

                for (int indice = 0; indice < lotHeader224usd.size(); indice++) {
                    StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                    if (lotHeader224usd.get(indice).substring(0, 1).equals("5")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(lotHeader224usd.get(indice));
                        StringBuilder linemod = strbline.replace(93, 100, secuencia);
                        lotHeader224usd.remove(indice);
                        lotHeader224usd.add(indice, linemod.toString());
                    }
                    if (endOfContrl224Usd.substring(0, 1).equals("8")) {
                        String secuencia = StringUtils.leftPad(IntHedLotCount.toString(), 7, "0");
                        StringBuilder strbline = new StringBuilder(endOfContrl224Usd);
                        StringBuilder linemod = strbline.replace(102, 109, secuencia);
                        endOfContrl224Usd = linemod.toString();
                        IntHedLotCount += 1;
                    }
                }

                NameClass fldclassIndCount = new NameClass();
                fldclassIndCount.setName(appcode);
                fldclassIndCount.setValue(IntHedLotCount.toString());
                archRecLotHedCount.setName(fldclassIndCount, 0);

                BciCceHeaderCounterTable tblRecCount = new BciCceHeaderCounterTable(this);
                try {
                    tblRecCount.write(indHeaderLotCntDate, archRecLotHedCount);
                } catch (Exception e) {

                }

                int fileHeaderUSDcnt = fileHeaderUSD.size();

                int fileHeaderPENcnt = fileHeaderPEN.size();
                
                // int lotEndSize2 = lotEndSize - 1;


                try {
                    if (fileHeaderUSD.size() > 0 && fileHeaderPEN.size() > 0) {
                        String strcountUSD = fileHeaderUSD.get(0).substring(30, 32);
                        String strcountPEN = fileHeaderPEN.get(0).substring(30, 32);
                        Integer intcountUSD = Integer.parseInt(strcountUSD);
                        Integer intcountPEN = Integer.parseInt(strcountPEN);
                        System.out.println(intcountUSD);
                        System.out.println(intcountPEN);
                        if (intcountUSD < intcountPEN) {
                            Integer temp = 0;
                            temp = intcountPEN;
                            intcountPEN = intcountUSD;
                            intcountUSD = temp;
                        }
                        System.out.println(intcountUSD);
                        System.out.println(intcountPEN);
                        StringBuilder strbline = new StringBuilder(fileHeaderUSD.get(0));
                        StringBuilder linemod = strbline.replace(30, 32,
                                StringUtils.leftPad(intcountUSD.toString(), 2, "0"));
                        fileHeaderUSD.remove(0);
                        fileHeaderUSD.add(0, linemod.toString());
                        strbline = new StringBuilder(fileHeaderPEN.get(0));
                        linemod = strbline.replace(30, 32, StringUtils.leftPad(intcountPEN.toString(), 2, "0"));
                        fileHeaderPEN.remove(0);
                        fileHeaderPEN.add(0, linemod.toString());
                    }
                } catch (Exception e) {

                }

                if (fileHeaderPENcnt != 0) {
                    finalCCEStr.addAll(fileHeaderPEN);
                    finalCCEStr.addAll(lotHeader220);
                    finalCCEStr.addAll(individualAdd220Pen);
                    finalCCEStr.add(endOfContrl220Pen);
                    finalCCEStr.addAll(lotHeader221);
                    finalCCEStr.addAll(individualAdd221Pen);
                    finalCCEStr.add(endOfContrl221Pen);
                    finalCCEStr.addAll(lotHeader222);
                    finalCCEStr.addAll(individualAdd222Pen);
                    finalCCEStr.add(endOfContrl222Pen);
                    finalCCEStr.addAll(lotHeader223);
                    finalCCEStr.addAll(individualAdd223Pen);
                    finalCCEStr.add(endOfContrl223Pen);
                    finalCCEStr.addAll(lotHeader224);
                    finalCCEStr.addAll(individualAdd224Pen);
                    finalCCEStr.add(endOfContrl224Pen);
                    finalCCEStr.removeAll(Collections.singletonList(""));
                    finalCCEStr.addAll(fileendPen);

                    /*
                     * lotEnd.addAll(lotendHeader221Pen.subList(lotend220-1,
                     * lotend220)); System.out.println("value is "+lotEnd);
                     */
                }

                // int fileHeaderUSDcnt = fileHeaderUSD.size();
                // int fileHeaderPENcnt = fileHeaderPEN.size();

                if ((fileHeaderUSDcnt == 0) || (fileHeaderPENcnt == 0)) {
                    // fileheader
                    String immDestname = "";
                    String immdest = "";
                    String immOrigin = "";
                    String originName = "";
                    BciCceInterfaceParameterRecord bciParamRec = new BciCceInterfaceParameterRecord(
                            da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.FILEHEADER"));
                    fldList = bciParamRec.getFieldName();
                    for (FieldNameClass fieldid : fldList) {
                        String fieldName = fieldid.getFieldName().getValue();
                        if (fieldName.equals("TYPE.REGISTER")) {
                            typReg = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("SESSION.TYPE.CONFIRMED")) {
                            sesType = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("IMMEDIATE.DESTINATION.CODE.ECTRMA")) {
                            immdest = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("IMMEDIATE.DESTINATION.NAME")) {
                            immDestname = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("L.CCI.CODE.ORIG")) {
                            immOrigin = fieldid.getFieldValue().getValue();
                            // immOrigin = "0" + immOrigin.substring(0, 3) + "0"
                            // + immOrigin.substring(3, 6);
                        }
                        if (fieldName.equals("IMMEDIATE.ORIGIN.NAME")) {
                            originName = fieldid.getFieldValue().getValue();
                        }
                    }
                    try {
                        immOrigin = immOrigin.substring(0, 8);
                        originName = originName.substring(0, 23);
                    } catch (Exception e) {

                    }

                    String coin = "1";
                    // appcode = serviceData.getJobData(0);

                    String dateofPresent = today;
                    // String archNum = "";
                    BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
                    try {
                        archwriteRec = new BciCceHeaderCounterRecord(
                                da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayFH));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        // Uncomment and replace with appropriate logger
                        // LOGGER.error(exception_var, exception_var);
                    }
                    NameClass fldclass = new NameClass();
                    fldclass.setName(appcode);
                    String Value = "";
                    String finalcount = "";
                    if (valuecnt == 1) {
                        valuecnt = 1;
                        finalcnt = valuecnt + 1;
                    }

                    if (valuecnt > 1) {
                        if ((fileHeaderPENcnt == 0) && (fileHeaderUSDcnt == 0)) {
                            valuecnt = valuecnt + 1;
                            finalcnt = valuecnt + 1;
                            System.out.println("valuecnt 2 = " + valuecnt);
                            System.out.println("finalcnt: " + finalcnt);

                        }

                        System.out.println("fileHeaderUSDcnt: " + fileHeaderUSDcnt);
                        if ((fileHeaderUSDcnt == 0) && (fileHeaderPENcnt > 0)) {

                            // finalcnt = valuecnt + 1;
                            int archiveNumberTmp = valuecntOrign + 1;
                            if (archiveNumberTmp == valuecnt) {
                                if (flagNewFH != 1) {
                                    valuecnt = valuecnt + 1;
                                }
                            }
                            finalcnt = valuecnt;
                            System.out.println("valuecnt 2 = " + valuecnt);
                            System.out.println("finalcnt: " + finalcnt);
                        }

                        if ((fileHeaderPENcnt == 0) && (fileHeaderUSDcnt > 0)) {
                            System.out.println("valuecnt 2 = " + valuecnt);
                            // valuecnt = valuecnt + 1;
                            int archiveNumberTmp = valuecntOrign + 1;
                            if (archiveNumberTmp == valuecnt) {
                                if (flagNewFH != 1) {
                                    valuecnt = valuecnt + 1;
                                }
                            }
                            finalcnt = valuecnt;

                        }
                    }
                    finalcount = String.valueOf(finalcnt);
                    Value = String.valueOf(finalcount);
                    fldclass.setValue(Value);
                    if (appcode.equals("TRI")) {
                        archwriteRec.setName(fldclass, 0);
                    } else if (appcode.equals("TRM")) {
                        archwriteRec.setName(fldclass, 1);
                    } else if (appcode.equals("TRT")) {
                        archwriteRec.setName(fldclass, 2);
                    }
                    BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
                    try {
                        tblRec.write(todayFH, archwriteRec);
                    } catch (Exception e) {

                    }
                    String free = "";
                    archiveNumber = String.valueOf(valuecnt);

                    String fileHeaderFinalPen = typReg.concat(sesType).concat(coin).concat(appcode)
                            .concat(StringUtils.leftPad(immdest, 8, "0")).concat(StringUtils.leftPad(immOrigin, 8, "0"))
                            .concat(StringUtils.rightPad(dateofPresent, 8, ""))
                            .concat(StringUtils.leftPad(archiveNumber, 2, "0"))
                            .concat(StringUtils.rightPad(immDestname, 23, ""))
                            .concat(StringUtils.rightPad(originName, 23, ""))
                            .concat(StringUtils.rightPad(free, 122, ""));

                    try {
                        if (fileHeaderUSD.size() > 0 && fileHeaderPEN.size() == 0) {
                            String strcountUSD = fileHeaderUSD.get(0).substring(30, 32);
                            String strcountPEN = fileHeaderFinalPen.substring(30, 32);
                            Integer intcountUSD = Integer.parseInt(strcountUSD);
                            Integer intcountPEN = Integer.parseInt(strcountPEN);
                            System.out.println(intcountUSD);
                            System.out.println(intcountPEN);
                            if (intcountUSD < intcountPEN) {
                                Integer temp = 0;
                                temp = intcountPEN;
                                intcountPEN = intcountUSD;
                                intcountUSD = temp;
                            }
                            System.out.println(intcountUSD);
                            System.out.println(intcountPEN);
                            StringBuilder strbline = new StringBuilder(fileHeaderUSD.get(0));
                            StringBuilder linemod = strbline.replace(30, 32,
                                    StringUtils.leftPad(intcountUSD.toString(), 2, "0"));
                            fileHeaderUSD.remove(0);
                            fileHeaderUSD.add(0, linemod.toString());
                            strbline = new StringBuilder(fileHeaderFinalPen);
                            linemod = strbline.replace(30, 32, StringUtils.leftPad(intcountPEN.toString(), 2, "0"));
                            fileHeaderFinalPen = linemod.toString();
                        }
                    } catch (Exception e) {
                        System.out.println();

                    }

                    finalCCEStrNull.add(fileHeaderFinalPen);

                    String totalLots = "";
                    String totalRecs = "2";
                    String controlTots = "";
                    try {
                        if (coin.equals("USD")) {
                            controlTots = String.valueOf(controlTotalFCusd);
                        } else if (coin.equals("PEN")) {
                            controlTots = String.valueOf(controlTotalFCpen);
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    String totOperation = "";
                    sumAmountsusd = "";
                    commisionAmountusd = "";
                    String sumAmountsNull = "";
                    String fileControlStrPen = typeRegEC.concat(StringUtils.leftPad(totalLots, 6, "0"))
                            .concat(StringUtils.leftPad(totalRecs, 10, "0"))
                            .concat(StringUtils.leftPad(controlTots, 15, "0"))
                            .concat(StringUtils.leftPad(totOperation, 15, "0"))
                            .concat(StringUtils.leftPad(sumAmountsNull, 15, "0"))
                            .concat(StringUtils.leftPad(commisionAmountusd, 15, "0"))
                            .concat(StringUtils.rightPad(free, 123, ""));

					
                    finalCCEStrNull.add(fileControlStrPen);

                    if (fileHeaderPENcnt == 0) {
                        finalCCEStr.addAll(finalCCEStrNull);
                    }
                    finalcount = StringUtils.leftPad(finalcount, 2, "0");
                    finalcount = finalcount.substring(finalcount.length() - 2, finalcount.length());
                    if (fileHeaderUSDcnt == 0) {
                        System.out.println("finalcount NEW: " + finalcount);
                        String fileHeaderFinalUSD = fileHeaderFinalPen.substring(0, 2).concat("2")
                                .concat(fileHeaderFinalPen.substring(3, 30)).concat(finalcount)
                                .concat(fileHeaderFinalPen.substring(32));

                        finalCCEStr.add(fileHeaderFinalUSD);
                        finalCCEStr.add(fileControlStrPen);
                        System.out.println(finalCCEStr);
                    }
                    BciCceHeaderCounterRecord archNowriteRec = new BciCceHeaderCounterRecord(this);
                    try {
                        archNowriteRec = new BciCceHeaderCounterRecord(
                                da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayFH));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        // Uncomment and replace with appropriate logger
                        // LOGGER.error(exception_var, exception_var);
                    }
                    NameClass fldclass2 = new NameClass();
                    fldclass2.setName(appcode);
                    // valuecnt = valuecnt + 1;
                    if (fileHeaderPENcnt == 0) {
                        Value = String.valueOf(valuecnt);
                    }
                    if (fileHeaderUSDcnt == 0) {
                        Value = finalcount;
                    }
                    fldclass2.setValue(Value);
                    // archNowriteRec.setName(fldclass2, 0);
                    if (appcode.equals("TRI")) {
                        archNowriteRec.setName(fldclass2, 0);
                    } else if (appcode.equals("TRM")) {
                        archNowriteRec.setName(fldclass2, 1);
                    } else if (appcode.equals("TRT")) {
                        archNowriteRec.setName(fldclass2, 2);
                    }
                    BciCceHeaderCounterTable tblNumRec = new BciCceHeaderCounterTable(this);
                    try {
                        tblNumRec.write(todayFH, archNowriteRec);
                    } catch (Exception e) {

                    }

                }

                if (fileHeaderUSDcnt != 0) {
                    finalCCEStr.addAll(fileHeaderUSD);
                    finalCCEStr.addAll(lotHeader220usd);
                    finalCCEStr.addAll(individualAdd220usd);
                    finalCCEStr.add(endOfContrl220Usd);
                    finalCCEStr.addAll(lotHeader221usd);
                    finalCCEStr.addAll(individualAdd221usd);
                    finalCCEStr.add(endOfContrl221Usd);
                    finalCCEStr.addAll(lotHeader222usd);
                    finalCCEStr.addAll(individualAdd222usd);
                    finalCCEStr.add(endOfContrl222Usd);
                    finalCCEStr.addAll(lotHeader223usd);
                    finalCCEStr.addAll(individualAdd223usd);
                    finalCCEStr.add(endOfContrl223Usd);
                    finalCCEStr.addAll(lotHeader224usd);
                    finalCCEStr.addAll(individualAdd224usd);
                    finalCCEStr.add(endOfContrl224Usd);
                    finalCCEStr.removeAll(Collections.singletonList(""));
                    finalCCEStr.addAll(fileendUsd);
                }
                /*
                 * BciCceInterfaceParameterRecord bciParamRec = new
                 * BciCceInterfaceParameterRecord(
                 * da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER",
                 * "BCI.CCE.FILENAMES")); System.out.print(bciParamRec);
                 * List<FieldNameClass> fieldList = bciParamRec.getFieldName();
                 * for (FieldNameClass fieldid : fieldList) { String fieldName =
                 * fieldid.getFieldName().getValue(); if
                 * (fieldName.equals("OUTWARD.CONFIRM")) { returnedfile =
                 * fieldid.getFieldValue().getValue(); } }
                 */
                returnedfile = serviceData.getJobData(1);
                outPath = outPath + "/" + returnedfile;
                FileWriter myWriter = null;
                File myObj = new File(outPath);
                try {
                    myObj.createNewFile();
                    myWriter = new FileWriter(outPath);
                    for (String s : finalCCEStr) {
                        String s1 = StringUtils.rightPad(s, 200, " ");
                        s = BciCCEInValidaciones.removeSpecialChar(s1);
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
        } catch (Exception e) {

        }
    }

}
