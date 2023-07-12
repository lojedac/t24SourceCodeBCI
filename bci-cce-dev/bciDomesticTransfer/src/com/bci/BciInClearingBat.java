package com.bci;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.eberror.EbErrorRecord;
import com.temenos.t24.api.records.pporderentry.DebitchargecomponentClass;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.bcicceclearingparam.BciCceClearingParamRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.tables.ebbciccegroundreturn.EbBciCceGroundReturnRecord;
import com.temenos.t24.api.tables.ebbcihdrawerdoctype.EbBciHDrawerDocTypeRecord;
import com.temenos.t24.api.tables.ebbcilccelogsinward.EbBciLCceLogsInwardRecord;
import com.temenos.t24.api.tables.ebbcilccelogsinward.EbBciLCceLogsInwardTable;

/**
 *
 * @author anagha.s
 *         ----------------------------------------------------------------------------------------------------------------
 *         Description : This routine is used to create the PP.ORDER.ENTRY
 *         transaction when the inward file is place in specified folder.
 * 
 *         Developed By : Anagha Shastry
 *
 *         Development Reference :
 *         IDD-G2-013_BCI_Interface_Interbank_Transfers_Outward_Inward
 *
 *         Attached To : BATCH>BciInClearingBat
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
public class BciInClearingBat extends ServiceLifecycle {

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        List<String> list = null;
        list = da.selectRecords("", "EB.BCI.CCE.CLEARING.PARAM", "", "WITH @ID EQ SYSTEM");
        return list;
    }

    @Override
    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
        // TODO Auto-generated method stub

        DataAccess da = new DataAccess(this);
        Date dat = new Date(this);
        DatesRecord datesRec = dat.getDates();
        String today = datesRec.getToday().getValue();
        String lastWorkingDay = datesRec.getLastWorkingDay().getValue();
        Session ss = new Session(this);
        String company = ss.getCompanyId();
        String presentFile = "";
        String docType = "";
        String rateCode = "";
        String appCrit = "";
        String ofsRecId = today + "-" + company;
        String appcode = serviceData.getJobData(0);// Data field
        BciCceClearingParamRecord paramrec = new BciCceClearingParamRecord(
                da.getRecord("EB.BCI.CCE.CLEARING.PARAM", "SYSTEM"));
        BciCceMappingFieldValuesRecord mapRecwrite = new BciCceMappingFieldValuesRecord(this);

        EbBciCceGroundReturnRecord ebBciCceGroundReturnRecord = new EbBciCceGroundReturnRecord(this);
        try {
            ebBciCceGroundReturnRecord = new EbBciCceGroundReturnRecord(
                    da.getRecord("EB.BCI.CCE.GROUND.RETURN", "SYSTEM"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        presentFile = serviceData.getJobData(1);
        String fileName = paramrec.getInPath().getValue();
        fileName = fileName + "/" + presentFile;

        // Validaciones de los archivos Inward
        List<List<String>> listaArchivos = BciCCEInValidaciones.listaArchivos(fileName);
        Calendar calendario = Calendar.getInstance();
        String hora, minutos, segundos, msegundo;
        hora = StringUtils.leftPad(String.valueOf(calendario.get(Calendar.HOUR_OF_DAY)), 2, "0");
        minutos = StringUtils.leftPad(String.valueOf(calendario.get(Calendar.MINUTE)), 2, "0");
        segundos = StringUtils.leftPad(String.valueOf(calendario.get(Calendar.SECOND)), 2, "0");
        msegundo = StringUtils.leftPad(String.valueOf(calendario.get(Calendar.MILLISECOND)), 3, "0");
        String idLogInward = today + "-" + hora + "" + minutos + "" + segundos + "." + msegundo;
        for (List<String> list : listaArchivos) {

            String msg = BciCCEInValidaciones.validaciones(list, today, lastWorkingDay, ebBciCceGroundReturnRecord, da);
            if (!msg.equals("")) {
                EbBciLCceLogsInwardRecord logInwrdRec = new EbBciLCceLogsInwardRecord(this);
                logInwrdRec.setDate(today);
                logInwrdRec.setHour(hora + ":" + minutos + ":" + segundos);
                logInwrdRec.setFile(presentFile);
                logInwrdRec.setDescription(msg);
                try {
                    EbBciLCceLogsInwardTable logInwrdtable = new EbBciLCceLogsInwardTable(this);
                    logInwrdtable.write(idLogInward, logInwrdRec);
                } catch (Exception e) {
                    return;
                }
                return;
            }
        }
        // System.out.println(fileName);
        String entityAccredit = "";
        String accountCredited = "";
        String cciDest = "";
        String transactionAmount = "";
        String senderRef = "";
        String benName = "";
        String transRef = "";
        String benDoc = "";
        String idAccount = "";
        String benAddress = "";
        String creditCardNo = "";
        String originCustAddr = "";
        String cciOriginClient = "";
        String benDocNo = "";
        String coin = "";
        String senderName = "";
        String commissionAmt = "";
        String commisionSign = "";
        String telephoneNum = "";
        String transfertype = "";
        String relRef = "";
        int regCountVal = 0;
        String regCnt = "";
        String uniSeq = "";
        String confirmAbono = "";
        List<String> lines = Collections.emptyList();

        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<String> itr = lines.iterator();
        while (itr.hasNext()) {
            MapFieldTypeClass mapclass = new MapFieldTypeClass();
            MapFieldTypeClass mapclass1 = new MapFieldTypeClass();
            String line1 = itr.next();
            String line = BciCCEInValidaciones.removeSpecialChar(line1);

            int flgind = 0;
            int flgadd = 0;
            
            if (line.substring(0, 1).equals("1")) {
                coin = line.substring(2, 3);
                if (coin.equals("1")) {
                    coin = "PEN";
                }
                if (coin.equals("2")) {
                    coin = "USD";
                }

            }
            if (line.substring(0, 1).equals("5")) {
                transfertype = line.substring(66, 69);

            }
            if (line.substring(0, 1).equals("6")) {
                try {
                    idAccount = line.substring(21, 31);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                entityAccredit = line.substring(3, 11);
                rateCode = line.substring(11, 12);
                appCrit = line.substring(12, 13);
                System.out.println("appCrit: " + appCrit);
                accountCredited = line.substring(13, 33);
                System.out.println("accountCredited: " + accountCredited);
                transactionAmount = line.substring(33, 48);
                try {
                    transactionAmount = transactionAmount.substring(1, 13).concat(".")
                            .concat(transactionAmount.substring(13, 15));
                    System.out.println("amount after concat: " + transactionAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                commisionSign = line.substring(48, 49);
                commissionAmt = line.substring(49, 64);
                // BigInteger commAmt = new BigInteger(commissionAmt);
                // commissionAmt = String.valueOf(commAmt);
                try {
                    commissionAmt = commissionAmt.substring(1, 13).concat(".").concat(commissionAmt.substring(13, 15));
                    System.out.println("amount after concat: " + commissionAmt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                senderName = line.substring(64, 108);
                transRef = line.substring(152, 177);

                transRef = transRef.trim();
                /////
                relRef = line.substring(152, 177);
                /////
                benName = line.substring(108, 152);
                regCountVal = line.length();
                regCnt = line.substring(regCountVal - 15, regCountVal);
                uniSeq = line.substring(regCountVal - 23, regCountVal - 16);
                transRef = uniSeq;
                String first = line.substring(0, 99);
                String second = line.substring(99);
                mapclass.setMapFieldType("INDIVIDUAL");
                mapclass.setMapFieldVal(first, 0);
                mapclass.setMapFieldVal(second, 1);
                mapRecwrite.setMapFieldType(mapclass, 0);
            }
            if (line.substring(0, 1).equals("7")) {
                benDoc = line.substring(3, 4);
                benDocNo = line.substring(4, 16);
                benAddress = line.substring(16, 74);
                telephoneNum = line.substring(74, 84);
                creditCardNo = line.substring(84, 104);
                originCustAddr = line.substring(104, 162);
                cciOriginClient = line.substring(162, 182);
                // cciOriginClient = cciOriginClient.substring(0, 8);
                String first = line.substring(0, 99);
                String second = line.substring(99);
                mapclass1.setMapFieldType("ADDITIONAL");
                mapclass1.setMapFieldVal(first, 0);
                mapclass1.setMapFieldVal(second, 1);
                mapRecwrite.setMapFieldType(mapclass1, 1);
                flgadd = 1;
                flgind = 1;
                // System.out.println("mapRecwrite: " + mapRecwrite);
            }
            
            if (line.substring(0, 3).equals("705")) {
                confirmAbono = line.substring(182, 183);
            }
            // check
            String DocName = "";
            BciCceInterfaceParameterRecord bciDocRec = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.DOCUMENT.TYPE"));
            List<FieldNameClass> flListDoc = bciDocRec.getFieldName();
            // System.out.println("flListDoc: " + flListDoc);
            for (FieldNameClass fieldid : flListDoc) {
                docType = fieldid.getFieldValue().getValue();
                // System.out.println("flListDoc: " + docType);
                if (docType.equals(benDoc)) {
                    DocName = fieldid.getFieldName().getValue();
                }
            }
            // System.out.println("doc type: " + benDoc);
            // System.out.println("doc type number: " + DocName);
            if (flgadd == 1 && flgind == 1) {
                PpOrderEntryRecord ppRec = new PpOrderEntryRecord(this);

                ppRec.setTransactioncurrency(coin);
                ppRec.setCreditaccountcurrency(coin);
                ppRec.setRelatedreference(relRef);
                String sameOwnerStr = relRef.substring(24, 25);
                String rSameOwner = (sameOwnerStr.equals("M")) ? "YES" : "NO";
                ppRec.getLocalRefField("L.SAME.OWNER").set(rSameOwner);
                ppRec.setAdditionaltext(rSameOwner);
                ppRec.getLocalRefField("L.CR.ENTITY.COD").set(entityAccredit);
                ppRec.setCreditaccountnumber(accountCredited);
                System.out.println("amount before bigdec: " + transactionAmount);
                ppRec.setTransactionamount(new BigDecimal(transactionAmount).toPlainString());
                System.out.println("amount after bigdec: " + transactionAmount);
                ppRec.setSendersreferencenumber(transRef);
                ppRec.setBeneficiaryname(benName);
                ppRec.getLocalRefField("L.RATE.CODE").set(rateCode);
                ppRec.getLocalRefField("L.APPL.CRITERIA").set(appCrit);
                System.out.println("added appCrit: " + appCrit);
                ppRec.getLocalRefField("L.DRAWER.DOC.TYPE").set(DocName);
                ppRec.getLocalRefField("L.DRAWER.DOC.NUMBER").set(benDocNo);
                ppRec.setBeneficiaryaddress1(benAddress);
                ppRec.getLocalRefField("L.CRDT.CARD.NUMBER").set(creditCardNo);
                ppRec.getLocalRefField("L.SENDER.ADDRESS").set(originCustAddr);
                ppRec.getLocalRefField("L.SENDER.NAME").set(senderName);
                ppRec.getLocalRefField("L.TELEPHONE.NO").set(telephoneNum);
                ppRec.getLocalRefField("L.CCI.CODE.ORIG").set(cciOriginClient);
                ppRec.getLocalRefField("L.CCI.DESTINATION").set(cciDest);
                ppRec.getLocalRefField("L.TRANSFER.TYPE").set(transfertype);
                ppRec.getLocalRefField("L.REGIST.CONTR").set(regCnt);
                ppRec.getLocalRefField("L.PAYMENT.CONFIRM").set(confirmAbono);
                
                String debitAcctNum = "";
                BciCceInterfaceParameterRecord accRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "CCE.ACCOUNT.NUMBER"));
                List<FieldNameClass> flLst = accRec.getFieldName();
                for (FieldNameClass fieldid : flLst) {
                    String fieldName = fieldid.getFieldName().getValue();
                    if (coin.equals("PEN")) {
                        if (fieldName.equals("DEBIT.ACCT.NUMBER")) {
                            debitAcctNum = fieldid.getFieldValue().getValue();
                        }
                    }
                    if (coin.equals("USD")) {
                        if (fieldName.equals("DEBIT.ACCT.NUMBER.USD")) {
                            debitAcctNum = fieldid.getFieldValue().getValue();
                        }
                    }

                }

                String debitChargeType = "";
                BciCceInterfaceParameterRecord chargeRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "CCE.DEBIT.CHARGE.TYPE"));
                List<FieldNameClass> flLst2 = chargeRec.getFieldName();
                for (FieldNameClass fieldid : flLst2) {
                    String fieldName = fieldid.getFieldName().getValue();
                    if (fieldName.equals("DEBIT.CHARGE.TYPE")) {
                        debitChargeType = fieldid.getFieldValue().getValue();
                    }
                }

                DebitchargecomponentClass debitCharge = new DebitchargecomponentClass();
                ppRec.setDebitchargeimposedflag("Y");
                debitCharge.setDebitchargecomponent(debitChargeType);
                debitCharge.setDebitchargecurrency(coin);
                // ppRec.setTransactionamount(new
                // BigDecimal(transactionAmount).toPlainString());
                debitCharge.setDebitchargeamount(new BigDecimal(commissionAmt).toPlainString());
                ppRec.addDebitchargecomponent(debitCharge);

                ppRec.setDebitaccountnumber(debitAcctNum);
                
                
                String accountCredit = "";
                String customerId = "";
                
                boolean flgequal = false;
                List<LegalIdClass> lstCusLegIds = new ArrayList<LegalIdClass>();
                String sameOwner = "";
                String ppoLegalId = "";
                String ppoLegalDoc = "";
                
                String cusLeglId = "";
                String cusLeglDoc = "";
                BigDecimal ppoLegalDocBD = new BigDecimal(0);
                
                
                
                accountCredit = ppRec.getCreditaccountnumber().getValue();
                sameOwner = ppRec.getLocalRefField("L.SAME.OWNER").getValue();
                if (sameOwner.equals("YES")) {
                    
                    
                    accountCredit = accountCredit.substring(8,18);
                    AccountRecord aRecord = new AccountRecord(this);
                    CustomerRecord cuRecord = new CustomerRecord(this);
                    EbBciHDrawerDocTypeRecord docTypeRecord = new EbBciHDrawerDocTypeRecord(this);
                    try {
                        aRecord = new AccountRecord(da.getRecord("ACCOUNT", accountCredit));
                        customerId = aRecord.getCustomer().getValue();
                        cuRecord = new CustomerRecord(da.getRecord("CUSTOMER", customerId));
                        ppoLegalId = ppRec.getLocalRefField("L.DRAWER.DOC.TYPE").getValue();  
                        docTypeRecord = new EbBciHDrawerDocTypeRecord(da.getRecord("EB.BCI.H.DRAWER.DOC.TYPE", ppoLegalId));
                        ppoLegalId = docTypeRecord.getDocTypeDescription().getValue();
                        ppoLegalDoc = ppRec.getLocalRefField("L.DRAWER.DOC.NUMBER").getValue();
                        ppoLegalDocBD = new BigDecimal(ppoLegalDoc);
                        ppoLegalDoc = ppoLegalDocBD.toString();
                        lstCusLegIds = cuRecord.getLegalId();
                        
                        for (LegalIdClass cusLegIdClass : lstCusLegIds) {
                            cusLeglId = cusLegIdClass.getLegalId().getValue();
                            cusLeglDoc = cusLegIdClass.getLegalDocName().getValue();
                            if (cusLeglId.equals(ppoLegalDoc)) {
                                if (cusLeglDoc.equals(ppoLegalId)) {
                                    flgequal = true;
                                }
                                
                                break;
                            }
                        }
                        if (!flgequal) {
                            
                            EbErrorRecord ebErrorRecord = new EbErrorRecord(da.getRecord("EB.ERROR", "EB-BCI-DNI-NOT-EQ"));
                            String descError = ebErrorRecord.getErrorMsg().get(0).getErrorMsg(0).getValue();
                            
                            ppRec.setReturndescription(descError);
                            ppRec.setOverride("AA-EXP.DAYS.SAME.FOR.CHILD", 0);
                            ppRec.setFunctionalerror("CPD10003", 0);
                            
                            ppRec.getLocalRefField("L.SAME.OWNER").setError("AC-ACCOUNT.CLOSED.STATUS");

                        }
                        
                        
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    
                    
                    
                }
                

                
                // System.out.println(ppRec);
                String versionname = "";
                BciCceInterfaceParameterRecord bciRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.VERSION.NAMES"));
                List<FieldNameClass> flList = bciRec.getFieldName();
                for (FieldNameClass fieldid : flList) {
                    String fieldName = fieldid.getFieldName().getValue();
                    if (fieldName.equals("PRESENTED.TRANSACTION")) {
                        versionname = fieldid.getFieldValue().getValue();
                    }
                }

                mapRecwrite.setInRef(transRef);
                // mapRecwrite.setOeId();
                mapRecwrite.setDate(today);
                mapRecwrite.setTxnType(appcode);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                mapRecwrite.setTime(dtf.format(now));
                mapRecwrite.setTxnType(serviceData.getJobData(0));
                mapRecwrite.setStatus("TO_BE_RETURNED_IN");
                BciCceMappingFieldValuesTable bciTbl = new BciCceMappingFieldValuesTable(this);
                try {
                    bciTbl.write(transRef, mapRecwrite);
                } catch (Exception e) {

                }
                // SynchronousTransactionData txnData = new
                // SynchronousTransactionData();
                
                
                
                
                
            
                
                
                TransactionData txnData = new TransactionData();
                txnData.setFunction("I");
                txnData.setNumberOfAuthoriser("0");
                txnData.setSourceId("BCI.CCE.IN");
                txnData.setVersionId(versionname);
                txnData.setCompanyId("PE0010001");
                txnData.setTransactionId("");
                transactionData.add(txnData);
                records.add(ppRec.toStructure());
                System.out.println("ppRec: " + ppRec);

            }
        }
    }
}
