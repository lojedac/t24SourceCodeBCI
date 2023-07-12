package com.bci;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.t24.api.complex.pp.paymentlifecyclehook.CommonData;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.Flags;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.PaymentApplicationUpdate;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.PaymentContext;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.StatusAction;
import com.temenos.t24.api.hook.payments.PaymentLifecycle;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.ebqueriesanswers.EbQueriesAnswersRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.records.poragreementandadvice.PorAgreementAndAdviceRecord;
import com.temenos.t24.api.records.poraudittrail.PorAuditTrailRecord;
import com.temenos.t24.api.records.porpostingandconfirmation.PorPostingAndConfirmationRecord;
import com.temenos.t24.api.records.porsupplementaryinfo.PorSupplementaryInfoRecord;
import com.temenos.t24.api.records.portransaction.PorTransactionRecord;
import com.temenos.t24.api.records.ppcompanyproperties.PpCompanyPropertiesRecord;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bcicceheadercounter.BciCceHeaderCounterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;

/**
 * TODO: Document me!
 *
 * @author spoorthi.bs
 *
 */
public class BciCceTest extends PaymentLifecycle {

    @Override
    public void updateRequestToExternalCoreSystem(StatusAction arg0, PorTransactionRecord arg1, PaymentContext arg2,
            PorSupplementaryInfoRecord arg3, PorAgreementAndAdviceRecord arg4, PorPostingAndConfirmationRecord arg5,
            PorAuditTrailRecord arg6, PpCompanyPropertiesRecord arg7, CommonData arg8, EbQueriesAnswersRecord arg9,
            Flags arg10, PaymentApplicationUpdate arg11) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        

        Date date = new Date(this);
        DatesRecord datesRec = date.getDates();
        String today = datesRec.getToday().getValue();

        String outwardFile = "/project/BackupData/BACKUP/BCIISB/BNK/UD/CCE/OUTWARD/sample2.txt";
        List<String> finalList = new ArrayList<String>();

        // System.out.println(outPath);
        FileWriter myWriter = null;
        File myObj = new File(outwardFile);
        try {
            myObj.createNewFile();
            myWriter = new FileWriter(outwardFile);
            try {

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                BciCceInterfaceParameterRecord bciParamIndRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.INDIVIDUAL.CHQ"));

                BciCceInterfaceParameterRecord bciParamAddRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.ADDITIONAL.CHQ"));

                // myWriter.write("File generated HEADER1" +
                // System.getProperty("line.separator"));
                //

                String oeID = "";
                String poID = arg1.getFilereferenceincoming().getValue();

                myWriter.write("File generated HEADER 2" + poID + System.getProperty("line.separator"));

                String inMsgtype = arg1.getIncomingmessagetype().getValue();
                BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(this);
                BciCceMappingFieldValuesTable bciTable = new BciCceMappingFieldValuesTable(this);

                myWriter.write("File generated HEADER 3" + System.getProperty("line.separator"));
                MapFieldTypeClass bciMapRec = new MapFieldTypeClass();
                MapFieldTypeClass bciMapRec2 = new MapFieldTypeClass();
               // PaymentOrderRecord paymentOrdRec = new PaymentOrderRecord(da.getRecord("PAYMENT.ORDER", poID));

                myWriter.write("File generated HEADER 4" + System.getProperty("line.separator"));
                String typReg = "";
                String transCode = "";
                String entDebCred = "";
                String trunInd = "";
                String accDebt = "";
                String amount = "";
                String chqNum = "";
                String presSqr = "";
                String girSqr = "";
                String free = "";
                String depInd = "";
                String docTyp = "";
                String docNum = "";
                String addRec = "";
                String regCnt = "";

                String typRegAdd = "";
                String addRegCode = "";
                String reasonRet = "";
                String orgTransRec = "";
                String entOrgTrans = "";
                String nameCtaRotated = "";
                String recCnt = "";
                String seqNum = "";
                String dateRegCnt = "";

                String individualRec = "";
                String additionalRec = "";
                myWriter.write("File generated -2" + System.getProperty("line.separator"));

                if (inMsgtype.equals("INWCD")) {

                    oeID = arg3.getOrderEntryId(0).getValue();

                    PpOrderEntryRecord ppRec = new PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", oeID));
                    entDebCred = ppRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
                    accDebt = ppRec.getDebitaccountnumber().getValue();
                    amount = ppRec.getTransactionamount().getValue();
                    chqNum = ppRec.getChequenumber().getValue();
                    presSqr = ppRec.getLocalRefField("L.PRESNTER.SQUR").getValue();
                    girSqr = ppRec.getLocalRefField("L.ISSUER.SQUR").getValue();
                    depInd = ppRec.getLocalRefField("L.TYPE.PLACE").getValue();
                    docTyp = ppRec.getLocalRefField("L.DRAWER.DOC.TYPE").getValue();
                    docNum = ppRec.getLocalRefField("L.DRAWER.DOC.NUMBER").getValue();
                    regCnt = ppRec.getLocalRefField("L.REGIST.CONTR").getValue();
                    // trunInd =
                    // ppRec.getLocalRefField("L.TRUNCATION").getValue();
                    
                    List<FieldNameClass> fldList1 = bciParamIndRec.getFieldName();
                    for (FieldNameClass fieldid : fldList1) {
                        String fieldName = fieldid.getFieldName().getValue();

                        if (fieldName.equals("TYPE.REGISTER")) {
                            typReg = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("TRANSACTION.CODE.INDA")) {
                            transCode = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("TRUNCATION.INDICATOR")) {
                            trunInd = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("ADDITIONAL.RECORDS")) {
                            addRec = fieldid.getFieldValue().getValue();
                        }

                    }

                    individualRec = typReg.concat(transCode).concat(StringUtils.leftPad(entDebCred, 8, "0"))
                            .concat(trunInd).concat(StringUtils.leftPad(accDebt, 18, "0"))
                            .concat(StringUtils.leftPad(amount, 15, "0")).concat(StringUtils.leftPad(chqNum, 9, "0"))
                            .concat(presSqr).concat(girSqr).concat(StringUtils.leftPad(free, 2, "")).concat(depInd)
                            .concat(docTyp).concat(StringUtils.leftPad(docNum, 12, "")).concat(addRec)
                            .concat(StringUtils.leftPad(regCnt, 15, "0"));

                    String todayOutAD = today + "-" + "ADDITIONAL-CHQ";
                    try {
                        BciCceHeaderCounterRecord archRec = new BciCceHeaderCounterRecord(
                                da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayOutAD));
                        seqNum = archRec.getName(0).getValue().getValue();

                    } catch (Exception e) {

                    }
                    if (seqNum.equals("")) {
                        seqNum = "1";
                    }

                    String addReg = "";
                    List<FieldNameClass> fldListAdd = bciParamAddRec.getFieldName();
                    
                    for (FieldNameClass fieldid : fldListAdd) {
                        String fieldName = fieldid.getFieldName().getValue();

                        if (fieldName.equals("TYPE.REGISTER")) {
                            typRegAdd = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("ADDITIONAL.REGISTRATION.CODE.A1")) {
                            addReg = fieldid.getFieldValue().getValue();
                        }

                    }
                    reasonRet = ppRec.getReturncode().getValue();
                    orgTransRec = entDebCred;
                    entOrgTrans = entDebCred;
                    String nameCTArotated = ppRec.getBeneficiaryname().getValue();
                    regCnt = ppRec.getLocalRefField("L.REGIST.CONTR").getValue();

                    String additionalRecStr = typRegAdd.concat(addReg).concat(StringUtils.rightPad(reasonRet, 3, ""))
                            .concat(StringUtils.leftPad(orgTransRec, 15, "0")).concat(StringUtils.leftPad(free, 6, ""))
                            .concat(StringUtils.leftPad(entOrgTrans, 8, "0"))
                            .concat(StringUtils.rightPad(nameCTArotated, 44, ""))
                            .concat(StringUtils.leftPad(regCnt, 15, "0"));

                    bciRec.setDate(today);
                    bciRec.setTime(dtf.format(now));
                    bciMapRec.setMapFieldType("INDIVIDUAL-A");
                    bciMapRec.setMapFieldVal(individualRec, 0);
                    bciRec.setMapFieldType(bciMapRec, 0);

                    bciMapRec.setMapFieldType("ADDITIONAL-A");
                    bciMapRec.setMapFieldVal(additionalRecStr, 0);
                    bciRec.setMapFieldType(bciMapRec, 1);
                    bciRec.setOeId(oeID);
                    bciRec.setStatus("CLEARED_IN");
                    bciRec.setPoId(poID);

                    try {
                        bciTable.write(oeID, bciRec);
                    } catch (Exception e) {

                    }
                    myWriter.write("File generated -3" + inMsgtype + System.getProperty("line.separator"));
                }

                myWriter.write("File generated -3" + inMsgtype + System.getProperty("line.separator"));

             /*   if (inMsgtype.equals("ADJSTMTS")) {

                    entDebCred = paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    // entDebCred = "0" + entDebCredVal.substring(0, 3) + "0" +
                    // entDebCredVal.substring(3, 6);
                    myWriter.write("R17-BE" + inMsgtype + System.getProperty("line.separator"));
                    accDebt = paymentOrdRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
                    amount = paymentOrdRec.getPaymentAmount().getValue();
                    chqNum = paymentOrdRec.getLocalRefField("L.CHEQUE.NO").getValue();
                    // trunInd =
                    // paymentOrdRec.getLocalRefField("L.TRUNCATION").getValue();

                    try{
                        presSqr = accDebt.substring(5, 8);
                        girSqr = entDebCred.substring(5, 8);
                    } catch(Exception e){
                        
                    }
                 
                    

                    depInd = paymentOrdRec.getLocalRefField("L.SAME.OWNER").getValue();
                    docTyp = paymentOrdRec.getLocalRefField("L.DRAWER.DOC.TYPE").getValue();
                    docNum = paymentOrdRec.getLocalRefField("L.DRAWER.DOC.NUMBER").getValue();
                    

                    regCnt = paymentOrdRec.getLocalRefField("L.REGIST.CONTR").getValue();
                    String todayOut = today + "-" + "INDIVIDUAL-CHQ";
                    //dateRegCnt = today.substring(4, 8);
                    
                    List<FieldNameClass> fldList1 = bciParamIndRec.getFieldName();
                    for (FieldNameClass fieldid : fldList1) {
                        String fieldName = fieldid.getFieldName().getValue();

                        if (fieldName.equals("TYPE.REGISTER")) {
                            typReg = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("TRANSACTION.CODE.INDB")) {
                            transCode = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("TRUNCATION.INDICATOR")) {
                            trunInd = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("ADDITIONAL.RECORDS")) {
                            addRec = fieldid.getFieldValue().getValue();
                        }
                    }

                    myWriter.write("TYPE REG" + typReg + System.getProperty("line.separator"));

                    try {
                        BciCceHeaderCounterRecord archRec = new BciCceHeaderCounterRecord(
                                da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayOut));
                        seqNum = archRec.getName(0).getValue().getValue();

                    } catch (Exception e) {

                    }
                    if (seqNum.equals("")) {
                        seqNum = "1";
                    }
                    // regCnt = "0" + regCntVal.substring(0, 3) + "0" +
                    // regCntVal.substring(3, 6) + seqNum;
                    // add default values
                    individualRec = typReg.concat(transCode).concat(StringUtils.leftPad(entDebCred, 8, "0"))
                            .concat(trunInd).concat(StringUtils.leftPad(accDebt, 18, "0"))
                            .concat(StringUtils.leftPad(amount, 15, "0")).concat(StringUtils.leftPad(chqNum, 9, "0"))
                            .concat(presSqr).concat(girSqr).concat(StringUtils.rightPad(free, 2, "")).concat(depInd)
                            .concat(docTyp).concat(StringUtils.rightPad(docNum, 12, "")).concat(addRec)
                            .concat(StringUtils.leftPad(regCnt, 15, "0"));

                    myWriter.write("R17-3" + individualRec + System.getProperty("line.separator"));

                    reasonRet = paymentOrdRec.getLocalRefField("L.REASON").getValue();
                    orgTransRec = paymentOrdRec.getLocalRefField("L.ORGNL.TXN.REF").getValue();

                    entOrgTrans = paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    // entOrgTrans = "0" + entOrgTransVal.substring(0, 3) + "0"
                    // +
                    // entOrgTransVal.substring(3, 6);
                    recCnt = paymentOrdRec.getOrderingReference().getValue();
                    
                    List<FieldNameClass> fldListAdd = bciParamAddRec.getFieldName();
                    for (FieldNameClass fieldid : fldListAdd) {
                        String fieldName = fieldid.getFieldName().getValue();

                        if (fieldName.equals("TYPE.REGISTER")) {
                            typRegAdd = fieldid.getFieldValue().getValue();
                        }
                        // CHECK
                        if (fieldName.equals("ADDITIONAL.REGISTRATION.CODE.A2")) {
                            addRegCode = fieldid.getFieldValue().getValue();
                        }

                    }
                    myWriter.write("TYPE REG" + typRegAdd + System.getProperty("line.separator"));
                    // recCnt = recCntVal.concat(dateRegCnt).concat(seqNum);
                    additionalRec = typRegAdd.concat(addRegCode).concat(StringUtils.rightPad(reasonRet, 3, ""))
                            .concat(StringUtils.leftPad(orgTransRec, 15, "0")).concat(StringUtils.rightPad(free, 6, ""))
                            .concat(StringUtils.leftPad(entOrgTrans, 8, "0"))
                            .concat(StringUtils.rightPad(nameCtaRotated, 44, ""))
                            .concat(StringUtils.leftPad(regCnt, 15, "0"));
                    myWriter.write("R17-4" + additionalRec + System.getProperty("line.separator"));
                    bciRec.setDate(today);
                    bciRec.setTime(dtf.format(now));

                    bciMapRec.setMapFieldType("INDIVIDUAL-B");
                    bciMapRec.setMapFieldVal(individualRec, 0);
                    bciRec.setMapFieldType(bciMapRec, 0);

                    bciMapRec2.setMapFieldType("ADDITIONAL-B");
                    bciMapRec2.setMapFieldVal(additionalRec, 0);
                    bciRec.setMapFieldType(bciMapRec2, 1);

                    bciRec.setOeId(oeID);
                    bciRec.setPoId(poID);
                    if (reasonRet.equals("R17")) {
                        bciRec.setStatus("TO_BE_ADJUST_IN_R17");
                    }

                    if (reasonRet.equals("R18")) {
                        bciRec.setStatus("TO_BE_ADJUST_IN_R18");
                    }
                    try {
                        bciTable.write(poID, bciRec);
                    } catch (Exception e) {

                    }
                   
                }
                if (inMsgtype.equals("FOVRBAL")){

                    String entDebCredVal1 = paymentOrdRec.getLocalRefField("L.REVOLV.ENTITY").getValue();
                    String entDebCredVal2 = paymentOrdRec.getLocalRefField("L.REVOLV.OFFICE").getValue();
                    entDebCred = entDebCredVal1 + entDebCredVal2;

                    accDebt = paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    amount = paymentOrdRec.getPaymentAmount().getValue();
                    chqNum = paymentOrdRec.getLocalRefField("L.CHEQUE.NO").getValue();
                    // trunInd =
                    // paymentOrdRec.getLocalRefField("L.TRUNCATION").getValue();
                    myWriter.write("R17-1" + System.getProperty("line.separator"));
                    presSqr = paymentOrdRec.getLocalRefField("L.COMPENSATE.PLACE").getValue();
                    // presSqr = presSqrVal.substring(5, 8);

                    //String girSqrVal = paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    //remove comment
                    try{
                        girSqr = accDebt.substring(5, 8);
                    } catch(Exception e){
                        
                    }
                   // 

                    String presDate = paymentOrdRec.getPaymentExecutionDate().getValue();
                    String namePlaza = paymentOrdRec.getLocalRefField("L.COMPENSATE.PLACE").getValue() + presDate;
                    /*
                     * depInd =
                     * paymentOrdRec.getLocalRefField("L.SAME.OWNER").getValue(); docTyp
                     * = paymentOrdRec.getLocalRefField("L.DRAWER.DOC.TYPE").getValue();
                     * docNum =
                     * paymentOrdRec.getLocalRefField("L.DRAWER.DOC.NUMBER").getValue();
                    

                   regCnt = paymentOrdRec.getLocalRefField("L.REGIST.CONTR").getValue();
                    String todayOut = today + "-" + "INDIVIDUAL-CHQ";
                    dateRegCnt = today.substring(4, 8);
                    List<FieldNameClass> fldList1 = bciParamIndRec.getFieldName();
                    for (FieldNameClass fieldid : fldList1) {
                        String fieldName = fieldid.getFieldName().getValue();

                        if (fieldName.equals("TYPE.REGISTER")) {
                            typReg = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("TRANSACTION.CODE.INDC")) {
                            transCode = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("TRUNCATION.INDICATOR")) {
                            trunInd = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("ADDITIONAL.RECORDS")) {
                            addRec = fieldid.getFieldValue().getValue();
                        }

                    }
                    myWriter.write("R17-2" + System.getProperty("line.separator"));
                    try {
                        BciCceHeaderCounterRecord archRec = new BciCceHeaderCounterRecord(
                                da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayOut));
                        seqNum = archRec.getName(0).getValue().getValue();

                    } catch (Exception e) {

                    }
                    if (seqNum.equals("")) {
                        seqNum = "1";
                    }
                    //regCnt = "0" + regCntVal.substring(0, 3) + "0" + regCntVal.substring(3, 6) + seqNum;
                    // add default values
                   individualRec = typReg.concat(transCode).concat(StringUtils.leftPad(entDebCred, 8, "0"))
                            .concat(trunInd).concat(StringUtils.leftPad(accDebt, 18, "0"))
                            .concat(StringUtils.leftPad(amount, 15, "0")).concat(StringUtils.leftPad(chqNum, 9, "0"))
                            .concat(presSqr).concat(girSqr).concat(StringUtils.rightPad(namePlaza, 16, "")).concat(addRec)
                            .concat(StringUtils.leftPad(regCnt, 15, "0"));
                   myWriter.write("R17-3" + System.getProperty("line.separator"));
                    reasonRet = paymentOrdRec.getLocalRefField("L.REASON").getValue();
                    orgTransRec = paymentOrdRec.getLocalRefField("L.ORGNL.TXN.REF").getValue();

                    entOrgTrans = paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    //entOrgTrans = "0" + entOrgTransVal.substring(0, 3) + "0" + entOrgTransVal.substring(3, 6);
                    recCnt = paymentOrdRec.getOrderingReference().getValue();

                    // recCnt = recCntVal.concat(dateRegCnt).concat(seqNum);
                    List<FieldNameClass> fldListAdd = bciParamAddRec.getFieldName();
                    for (FieldNameClass fieldid : fldListAdd) {
                        String fieldName = fieldid.getFieldName().getValue();

                        if (fieldName.equals("TYPE.REGISTER")) {
                            typRegAdd = fieldid.getFieldValue().getValue();
                        }
                        // CHECK
                        if (fieldName.equals("ADDITIONAL.REGISTRATION.CODE.A1")) {
                            addRegCode = fieldid.getFieldValue().getValue();
                        }

                    }

                    additionalRec = typRegAdd.concat(addRegCode).concat(StringUtils.rightPad(reasonRet, 3, ""))
                            .concat(StringUtils.leftPad(orgTransRec, 15, "0")).concat(StringUtils.rightPad(free, 6, ""))
                            .concat(StringUtils.leftPad(entOrgTrans, 8, "0"))
                            .concat(StringUtils.rightPad(nameCtaRotated, 44, "")).concat(StringUtils.leftPad(regCnt, 15, "0"));
                    myWriter.write("R17-4" + System.getProperty("line.separator"));
                    bciRec.setDate(today);
                    bciRec.setTime(dtf.format(now));
                    bciMapRec.setMapFieldType("INDIVIDUAL-C");
                    bciMapRec.setMapFieldVal(individualRec, 0);
                    bciRec.setMapFieldType(bciMapRec, 0);

                    bciMapRec2.setMapFieldType("ADDITIONAL-C");
                    bciMapRec2.setMapFieldVal(additionalRec, 0);
                    bciRec.setMapFieldType(bciMapRec2, 1);
                    bciRec.setOeId(oeID);
                    bciRec.setPoId(poID);
                    bciRec.setStatus("TO_BE_BALFAVOR_IN_R16");
                    myWriter.write("R17-5" + System.getProperty("line.separator"));
                    try {
                        bciTable.write(poID, bciRec);
                    } catch (Exception e) {

                    }
                
                } */
                myWriter.write("R17 -6" + System.getProperty("line.separator"));
            } catch (IOException e) {

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
