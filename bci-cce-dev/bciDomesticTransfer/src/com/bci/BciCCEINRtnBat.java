package com.bci;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bcicceclearingparam.BciCceClearingParamRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.tables.ebbciccegroundreturn.CodeClass;
import com.temenos.t24.api.tables.ebbciccegroundreturn.EbBciCceGroundReturnRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.records.porpostingreversal.PorPostingReversalRecord;
import com.temenos.t24.api.records.portransaction.PorTransactionRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.ebbcilccelogsinward.EbBciLCceLogsInwardRecord;
import com.temenos.t24.api.tables.ebbcilccelogsinward.EbBciLCceLogsInwardTable;
import com.bci.BciCCEInValidaciones;

/**
 *
 * @author anagha.s
 *         ----------------------------------------------------------------------------------------------------------------
 *         Description : This routine is used to reverse the transaction if the
 *         999 status is not reached during the inward process.
 * 
 *         Developed By : Anagha Shastry
 *
 *         Development Reference :
 *         IDD-G2-013_BCI_Interface_Interbank_Transfers_Outward_Inward
 *
 *         Attached To : BATCH>MNEMONC/BciCCEINRtnBat
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
public class BciCCEINRtnBat extends ServiceLifecycle {

    @Override
    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        Date date = new Date(this);
        DatesRecord datesRec = date.getDates();
        String today = datesRec.getToday().getValue();
        String lastWorkingDay = datesRec.getLastWorkingDay().getValue();
        String transRef = "";
        String returnFile = "";
        String idUnivoco = "";
        String transactionID = "";
        String poOriginal = "";
        String secUnivoc = "";
        String appcode = serviceData.getJobData(0);// Data field
        BciCceClearingParamRecord paramrec = new BciCceClearingParamRecord(
                da.getRecord("EB.BCI.CCE.CLEARING.PARAM", "SYSTEM"));
        String status = "";
        String statusReversal = "";

        EbBciCceGroundReturnRecord ebBciCceGroundReturnRecord = new EbBciCceGroundReturnRecord(this);
        try {
            ebBciCceGroundReturnRecord = new EbBciCceGroundReturnRecord(
                    da.getRecord("EB.BCI.CCE.GROUND.RETURN", "SYSTEM"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        returnFile = serviceData.getJobData(1);
        String fileName = paramrec.getInPath().getValue();
        fileName = fileName + "/" + returnFile;
        System.out.println("Filepath: " + fileName);

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
            System.out.println(msg);
            if (!msg.equals("")) {
                EbBciLCceLogsInwardRecord logInwrdRec = new EbBciLCceLogsInwardRecord(this);
                logInwrdRec.setDate(today);
                logInwrdRec.setHour(hora + ":" + minutos + ":" + segundos);
                logInwrdRec.setFile(returnFile);
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

        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(this);
        Iterator<String> itr = lines.iterator();
        while (itr.hasNext()) {
            MapFieldTypeClass mapclass = new MapFieldTypeClass();
            MapFieldTypeClass mapclass1 = new MapFieldTypeClass();
            MapFieldTypeClass mapclass2 = new MapFieldTypeClass();
            String line1 = itr.next();
            String line = BciCCEInValidaciones.removeSpecialChar(line1);
            int flgadd = 0;
            int flgind = 0;
            // to fetch transaction reference
            String firstIndividual = "";
            String secondIndividual = "";
            String firstAdditional = "";
            String secondAdditional = "";
            String firstAdditionalRet = "";
            String secondAdditionalRet = "";

            if (line.substring(0, 1).equals("6")) {
                firstIndividual = line.substring(0, 99);
                secondIndividual = line.substring(99);
                mapclass.setMapFieldType("INDIVIDUAL");
                mapclass.setMapFieldVal(firstIndividual, 0);
                mapclass.setMapFieldVal(secondIndividual, 1);
                mapRec.setMapFieldType(mapclass, 0);
            }
            // to fetch individual string
            if ((line.substring(0, 1).equals("7")) && (!line.substring(1, 3).equals("99"))) {
                firstAdditional = line.substring(0, 99);
                secondAdditional = line.substring(99);
                mapclass1.setMapFieldType("ADDITIONAL");
                mapclass1.setMapFieldVal(firstAdditional, 0);
                mapclass1.setMapFieldVal(secondAdditional, 1);
                mapRec.setMapFieldType(mapclass1, 1);
                flgadd = 1;
                flgind = 1;
            }
            // to fetch additional string
            if ((line.substring(0, 1).equals("7")) && line.substring(1, 3).equals("99")) {
                // if (line.substring(2, 4).equals("99")) {
                firstAdditionalRet = line.substring(0, 99);
                secondAdditionalRet = line.substring(99);
                mapclass2.setMapFieldType("ADDITIONAL.RETURN");
                mapclass2.setMapFieldVal(firstAdditionalRet, 0);
                mapclass2.setMapFieldVal(secondAdditionalRet, 1);
                mapRec.setMapFieldType(mapclass2, 2);
                flgadd = 1;
                flgind = 1;
                idUnivoco = line.substring(73, 80);
                System.out.println("IdUvivoco: " + idUnivoco);
                idUnivoco = StringUtils.leftPad(idUnivoco, 15, "0");
                BciCceMappingFieldValuesRecord mapVal = new BciCceMappingFieldValuesRecord(this);
                try {
                    mapVal = new BciCceMappingFieldValuesRecord(
                            da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", idUnivoco));
                    transRef = mapVal.getInRef().getValue();
                    status = mapVal.getStatus().getValue();
                    poOriginal = mapVal.getPoId().getValue();
                } catch (Exception e) {
                }

            }
            if (flgadd == 1 && flgind == 1 && status.equals("SENT_OUT")) {
                String versionname = "";
                BciCceInterfaceParameterRecord bciRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.VERSION.NAMES"));
                List<FieldNameClass> flList = bciRec.getFieldName();
                for (FieldNameClass fieldid : flList) {
                    String fieldName = fieldid.getFieldName().getValue();
                    if (fieldName.equals("REVERSAL.TRANSACTION")) {
                        versionname = fieldid.getFieldValue().getValue();
                    }
                }

                TransactionData txnData = new TransactionData();

                String debitAcc = "";
                String creditAcc = "";
                String paymentAmt = "";
                String debitCurr = "";
                String paymentOP = "";

                PorTransactionRecord porTransactionRec = new PorTransactionRecord(this);
                PaymentOrderRecord payOrderRec = new PaymentOrderRecord(this);
                try {
                    porTransactionRec = new PorTransactionRecord(da.getRecord("POR.TRANSACTION", transRef));
                    debitAcc = porTransactionRec.getDebitmainaccount().toString();
                    creditAcc = porTransactionRec.getCreditmainaccount().toString();
                    paymentAmt = porTransactionRec.getTransactionamount().toString();
                    debitCurr = porTransactionRec.getTransactioncurrencycode().toString();
                    paymentOP = porTransactionRec.getIncomingmessagetype().toString();

                } catch (Exception e) {
                }

                // PorPostingReversalRecord ppRec = new
                // PorPostingReversalRecord(this);
                txnData.setFunction("INPUTT");
                txnData.setNumberOfAuthoriser("0");
                txnData.setSourceId("OFS.LOAD");
                txnData.setVersionId(versionname);
                txnData.setCompanyId("PE0010001");
                txnData.setTransactionId("PI".concat(idUnivoco));
                transactionData.add(txnData);

                payOrderRec.setDebitAccount(creditAcc);
                payOrderRec.setBeneficiaryAccountNo(debitAcc);
                payOrderRec.setPaymentAmount(paymentAmt);
                payOrderRec.setPaymentCurrency(debitCurr);
                payOrderRec.setPaymentOrderProduct(paymentOP);
                transactionID = txnData.getTransactionId();

                try {
                    AccountRecord accRec = new AccountRecord(da.getRecord("ACCOUNT", debitAcc));
                    payOrderRec.setOrderingCustomer(accRec.getCustomer().toString());
                } catch (Exception e) {
                }
                records.add(payOrderRec.toStructure());

                System.out.println(payOrderRec);

                String servicename = serviceData.getProcessId();
                // String status = serviceData.getJobData(2);
                mapRec.setPoId(poOriginal);
                mapRec.setInRef(transactionID);
                if (servicename.contains("UNWIND")) {
                    mapRec.setStatus("RETURNED_UNWIND");
                } else if (servicename.contains("PARTIAL")) {
                    mapRec.setStatus("PARTIAL_WITHDRAWAL");
                } else {
                    mapRec.setStatus("RETURNED_OUT");
                }
                // mapRec.setStatus(status);
                mapRec.setDate(today);
                mapRec.setTxnType(appcode);
                // mapRec.setInRef(transRef);
                // mapRec.setPoId(payOrderRec.getT);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                mapRec.setTime(dtf.format(now));
                BciCceMappingFieldValuesTable mapTbl = new BciCceMappingFieldValuesTable(this);
                try {
                    mapTbl.write(idUnivoco, mapRec);

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
        System.out.println("LIST: " + list);
        return list;
    }

}
