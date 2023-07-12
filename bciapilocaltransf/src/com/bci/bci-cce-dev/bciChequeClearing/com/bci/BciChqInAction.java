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

import com.temenos.t24.api.complex.pp.paymentlifecyclehook.CommonData;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.Flags;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.PaymentApplicationUpdate;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.PaymentContext;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.StatusAction;
import com.temenos.t24.api.hook.payments.PaymentLifecycle;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.ebqueriesanswers.EbQueriesAnswersRecord;
import com.temenos.t24.api.records.poragreementandadvice.PorAgreementAndAdviceRecord;
import com.temenos.t24.api.records.poraudittrail.PorAuditTrailRecord;
import com.temenos.t24.api.records.porpostingandconfirmation.PorPostingAndConfirmationRecord;
import com.temenos.t24.api.records.porsupplementaryinfo.PorSupplementaryInfoRecord;
import com.temenos.t24.api.records.portransaction.PorTransactionRecord;
import com.temenos.t24.api.records.ppcompanyproperties.PpCompanyPropertiesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.tables.bcicceheadercounter.BciCceHeaderCounterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterTable;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.t24.api.tables.ebbcihcceparticipantdir.EbBciHCceParticipantDirRecord;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;

/**
 * TODO: Document me!
 *
 * @author spoorthi.bs
 *
 */
public class BciChqInAction extends PaymentLifecycle {

    @Override
    public void updateRequestToExternalCoreSystem(StatusAction arg0, PorTransactionRecord arg1, PaymentContext arg2,
            PorSupplementaryInfoRecord arg3, PorAgreementAndAdviceRecord arg4, PorPostingAndConfirmationRecord arg5,
            PorAuditTrailRecord arg6, PpCompanyPropertiesRecord arg7, CommonData arg8, EbQueriesAnswersRecord arg9,
            Flags arg10, PaymentApplicationUpdate arg11) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);

  // String outwardFile = "/project/BCI/bnk/UD/CCEFile/sample3.txt";
        List<String> finalList = new ArrayList<String>();
  // FileWriter myWriter = null;
   //File myObj = new File(outwardFile);
        try {
   //myObj.createNewFile();
  // myWriter = new FileWriter(outwardFile);
            try {
                // myWriter.write("Triggered routine" +
                // System.getProperty("line.separator"));
                Date date = new Date(this);
                DatesRecord datesRec = date.getDates();
                String today = datesRec.getToday().getValue();

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

                String inMsgtype = arg1.getIncomingmessagetype().getValue();
                BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(this);
                BciCceMappingFieldValuesTable bciTable = new BciCceMappingFieldValuesTable(this);

                MapFieldTypeClass bciMapRec = new MapFieldTypeClass();
                MapFieldTypeClass bciMapRec2 = new MapFieldTypeClass();

                String typReg = "";
                String transCode = "";
                String entDebCred = "";
                String trunInd = "";
                String accDebt = "";
                String accDebtComm = "";
                String amount = "";
                String chqNum = "";
                String chqAmt = "";
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

                if (inMsgtype.equals("INWCD")) {

                    oeID = arg3.getOrderEntryId(0).getValue();
                    PpOrderEntryRecord ppRec = new PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", oeID));
                    regCnt = ppRec.getLocalRefField("L.REGIST.CONTR").getValue();
                    // myWriter.write("oeID" + oeID +
                    // System.getProperty("line.separator"));
                    // myWriter.write("regCnt" + regCnt +
                    // System.getProperty("line.separator"));
                    // myWriter.write("LINE 3"
                    // +System.getProperty("line.separator"));

                    BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(
                            da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", regCnt));

                    List<MapFieldTypeClass> fieldList = mapRec.getMapFieldType();
                    for (MapFieldTypeClass mapId : fieldList) {
                        String type = mapId.getMapFieldType().getValue();
                        // myWriter.write("LINE 1" +
                        // System.getProperty("line.separator"));
                        if (type.equals("INDIVIDUAL")) {
                            // myWriter.write("LINE 2" +
                            // System.getProperty("line.separator"));
                            String individualRegistrationfisrt = mapId.getMapFieldVal().get(0).getValue();

                            individualRec = individualRegistrationfisrt;
                            // myWriter.write("LINE 3" +
                            // System.getProperty("line.separator"));
                        }
                    }
                    // myWriter.write("LINE 4" +
                    // System.getProperty("line.separator"));

                    String indDRecStr = "";

                    List<FieldNameClass> fldList1 = bciParamIndRec.getFieldName();
                    for (FieldNameClass fieldid : fldList1) {
                        String fieldName = fieldid.getFieldName().getValue();

                        if (fieldName.equals("TYPE.REGISTER")) {
                            typReg = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("TRANSACTION.CODE.INDD")) {
                            transCode = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("TRUNCATION.INDICATOR")) {
                            trunInd = fieldid.getFieldValue().getValue();
                        }
                        if (fieldName.equals("ADDITIONAL.RECORDS.B")) {
                            addRec = fieldid.getFieldValue().getValue();
                        }

                    }
                    // myWriter.write("COMPLETED READING TEMPLATE" +
                    // System.getProperty("line.separator"));
                    accDebtComm = ppRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    accDebtComm = StringUtils.leftPad(accDebtComm, 18, "0");
                    accDebtComm = accDebtComm.substring(0, 16) + "00";
                    // myWriter.write("accDebt" + accDebt +
                    // System.getProperty("line.separator"));
                    entDebCred = ppRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
                    // myWriter.write("entDebCred" + entDebCred +
                    // System.getProperty("line.separator"));
                    chqNum = ppRec.getChequenumber().getValue();
                    chqNum = StringUtils.leftPad(chqNum, 8, "0");
                    chqNum = chqNum.substring(0, 8) + "0";

                    presSqr = ppRec.getLocalRefField("L.PRESNTER.SQUR").getValue();
                    // myWriter.write("presSqr" + presSqr +
                    // System.getProperty("line.separator"));
                    girSqr = ppRec.getLocalRefField("L.ISSUER.SQUR").getValue();
                    amount = ppRec.getLocalRefField("L.CCI.COMM.AMT").getValue();
                    // myWriter.write("amount" + amount +
                    // System.getProperty("line.separator"));

                    BigDecimal decAmt = new BigDecimal(amount);
                    amount = String.format("%.02f", decAmt);
                    int amtDec = amount.indexOf(".");
                    amount = amount.substring(0, amtDec).concat(amount.substring(amtDec + 1));

                    chqAmt = ppRec.getTransactionamount().getValue();
                    // amount = paymentOrdRec.getPaymentAmount().getValue();
                    BigDecimal decimal = new BigDecimal(chqAmt);
                    chqAmt = String.format("%.02f", decimal);
                    int amt = chqAmt.indexOf(".");
                    chqAmt = chqAmt.substring(0, amt).concat(chqAmt.substring(amt + 1));
                    // myWriter.write("chqAmt" + chqAmt +
                    // System.getProperty("line.separator"));

                    // myWriter.write("LINE 6" +
                    // System.getProperty("line.separator"));
                    indDRecStr = typReg.concat(transCode).concat(StringUtils.leftPad(entDebCred, 8, "0"))
                            .concat(trunInd).concat(StringUtils.leftPad(accDebtComm, 18, "0"))
                            .concat(StringUtils.leftPad(amount, 15, "0")).concat(StringUtils.leftPad(chqNum, 9, "0"))
                            .concat(StringUtils.leftPad(presSqr, 3, "0")).concat(StringUtils.leftPad(girSqr, 3, "0"))
                            .concat(StringUtils.leftPad(chqAmt, 16, "0")).concat(addRec)
                            .concat(StringUtils.leftPad(regCnt, 15, "0"));

                    bciRec.setDate(today);
                    bciRec.setTime(dtf.format(now));

                    bciMapRec.setMapFieldType("INDIVIDUAL-A");
                    bciMapRec.setMapFieldVal(individualRec, 0);
                    bciRec.setMapFieldType(bciMapRec, 0);

                    // myWriter.write("LINE 7" +
                    // System.getProperty("line.separator"));
                    bciMapRec2.setMapFieldType("INDIVIDUAL-D");
                    bciMapRec2.setMapFieldVal(indDRecStr, 0);
                    bciRec.setMapFieldType(bciMapRec2, 1);

                    bciRec.setOeId(oeID);
                    bciRec.setStatus("CLEARED_IN");
                    // bciRec.setPoId(poID);
                    // myWriter.write("LINE 8" +
                    // System.getProperty("line.separator"));
                    try {
                        bciTable.write(regCnt, bciRec);
                    } catch (Exception e) {

                    }

                }

                if (inMsgtype.equals("ADJSTMTS")) {

                    String addRegCodeB = "";
                    PaymentOrderRecord paymentOrdRec = new PaymentOrderRecord(da.getRecord("PAYMENT.ORDER", poID));
                    entDebCred = paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    // ADD oeID
                    accDebt = paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    try {
                        entDebCred = StringUtils.leftPad(entDebCred, 6, "0");
                        entDebCred = "0" + entDebCred.substring(0, 3) + "0" + entDebCred.substring(3, 6);
                        accDebt = StringUtils.leftPad(accDebt, 18, "0");
                        accDebt = accDebt.substring(0, 16) + "00";
                    } catch (Exception e) {

                    }
                    amount = paymentOrdRec.getPaymentAmount().getValue();
                    // String transactionAmt =
                    // telRec.getAmountLocal2().getValue();
                    BigDecimal decimal = new BigDecimal(amount);
                    amount = String.format("%.02f", decimal);
                    int amt = amount.indexOf(".");
                    amount = amount.substring(0, amt).concat(amount.substring(amt + 1));
                    chqNum = paymentOrdRec.getLocalRefField("L.CHEQUE.NO").getValue();
                    chqNum = StringUtils.leftPad(chqNum, 8, "0");
                    chqNum = chqNum.substring(0, 8) + "0";
                    // trunInd =
                    // paymentOrdRec.getLocalRefField("L.TRUNCATION").getValue();
                    orgTransRec = paymentOrdRec.getLocalRefField("L.ORGNL.TXN.REF").getValue();
                    try {
                        PpOrderEntryRecord ppRec = new PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", orgTransRec));
                        regCnt = ppRec.getLocalRefField("L.REGIST.CONTR").getValue();
                    } catch (Exception e) {

                    }

                    // girSqr = entDebCred.substring(5, 8);

                    depInd = paymentOrdRec.getLocalRefField("L.SAME.OWNER").getValue();
                    docTyp = paymentOrdRec.getLocalRefField("L.DRAWER.DOC.TYPE").getValue();
                    docNum = paymentOrdRec.getLocalRefField("L.DRAWER.DOC.NUMBER").getValue();
                    presSqr = paymentOrdRec.getLocalRefField("L.COMPENSATE.PLACE").getValue();

                    try {
                        depInd = depInd.substring(0, 1);
                        docNum = StringUtils.leftPad(docNum, 12, "");
                        docNum = docNum.substring(0, 12);
                        presSqr = presSqr.substring(0, 3);
                    } catch (Exception e) {

                    }

                    BciCceInterfaceParameterRecord bciDocRec = new BciCceInterfaceParameterRecord(
                            da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.DOCUMENT.TYPE"));
                    List<FieldNameClass> flListDoc = bciDocRec.getFieldName();
                    for (FieldNameClass fieldid : flListDoc) {
                        String fieldName = fieldid.getFieldName().getValue();
                        if (fieldName.equals(docTyp)) {
                            docTyp = fieldid.getFieldValue().getValue();
                        }
                    }
                    // regCnt =
                    // paymentOrdRec.getLocalRefField("L.REGIST.CONTR").getValue();
                    String todayOut = today + "-" + "INDIVIDUAL-CHQ";
                    dateRegCnt = today.substring(4, 8);

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
                            .concat(StringUtils.leftPad(presSqr, 3, "0")).concat(StringUtils.leftPad(girSqr, 3, "0"))
                            .concat(StringUtils.rightPad(free, 2, "")).concat(StringUtils.rightPad(depInd, 1, ""))
                            .concat(StringUtils.rightPad(docTyp, 1, "0")).concat(StringUtils.rightPad(docNum, 12, ""))
                            .concat(addRec).concat(StringUtils.leftPad(regCnt, 15, "0"));

                    reasonRet = paymentOrdRec.getLocalRefField("L.REASON").getValue();

                    entOrgTrans = paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    try {
                        // orgTransRec = "0" + orgTransRec.substring(0, 3) + "0"
                        // + orgTransRec.substring(3, 6);
                        entOrgTrans = "0" + entOrgTrans.substring(0, 3) + "0" + entOrgTrans.substring(3, 6);
                        entOrgTrans = entOrgTrans.substring(0, 8);
                    } catch (Exception e) {

                    }

                    nameCtaRotated = paymentOrdRec.getBeneficiaryName().getValue();
                    recCnt = paymentOrdRec.getOrderingReference().getValue();

                    List<FieldNameClass> fldListAdd = bciParamAddRec.getFieldName();
                    for (FieldNameClass fieldid : fldListAdd) {
                        String fieldName = fieldid.getFieldName().getValue();

                        if (fieldName.equals("TYPE.REGISTER")) {
                            typRegAdd = fieldid.getFieldValue().getValue();
                        }
                        // CHECK
                        if (fieldName.equals("ADDITIONAL.REGISTRATION.CODE.A2")) {
                            addRegCodeB = fieldid.getFieldValue().getValue();
                        }

                    }

                    // recCnt = recCntVal.concat(dateRegCnt).concat(seqNum);
                    additionalRec = typRegAdd.concat(addRegCodeB).concat(StringUtils.rightPad(reasonRet, 3, ""))
                            .concat(StringUtils.leftPad(orgTransRec, 15, "0")).concat(StringUtils.rightPad(free, 6, ""))
                            .concat(StringUtils.leftPad(entOrgTrans, 8, "0"))
                            .concat(StringUtils.rightPad(nameCtaRotated, 44, ""))
                            .concat(StringUtils.leftPad(regCnt, 15, "0"));

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
                if (inMsgtype.equals("FOVRBAL")) {
                    PaymentOrderRecord paymentOrdRec = new PaymentOrderRecord(da.getRecord("PAYMENT.ORDER", poID));
                    String entDebCredVal1 = paymentOrdRec.getLocalRefField("L.REVOLV.ENTITY").getValue();
                    entDebCredVal1 = StringUtils.leftPad(entDebCredVal1, 3, "0");
                    entDebCredVal1 = entDebCredVal1.substring(0, 3);
                    String entDebCredVal2 = paymentOrdRec.getLocalRefField("L.REVOLV.OFFICE").getValue();
                    entDebCredVal2 = StringUtils.leftPad(entDebCredVal2, 3, "0");
                    entDebCredVal2 = entDebCredVal2.substring(0, 3);
                    
                    entDebCred = entDebCredVal1.concat(entDebCredVal2);
                    String PlazaName = entDebCredVal1 + entDebCredVal2;

                    accDebt = paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    amount = paymentOrdRec.getPaymentAmount().getValue();
                    BigDecimal decimal = new BigDecimal(amount);
                    amount = String.format("%.02f", decimal);
                    int amt = amount.indexOf(".");
                    amount = amount.substring(0, amt).concat(amount.substring(amt + 1));
                    chqNum = paymentOrdRec.getLocalRefField("L.CHEQUE.NO").getValue();
                    try {
                        entDebCred = StringUtils.leftPad(entDebCred, 6, "0");
                        entDebCred = "0" + entDebCred.substring(0, 3) + "0" + entDebCred.substring(3, 6);
                        accDebt = StringUtils.leftPad(accDebt, 18, "0");
                        accDebt = accDebt.substring(0, 16) + "00";
                    } catch (Exception e) {

                    }
                    chqNum = StringUtils.leftPad(chqNum, 8, "0");
                    chqNum = chqNum.substring(0, 8) + "0";
                    // trunInd =
                    // paymentOrdRec.getLocalRefField("L.TRUNCATION").getValue();

                    // presSqr =
                    // paymentOrdRec.getLocalRefField("L.COMPENSATE.PLACE").getValue();
                    presSqr = paymentOrdRec.getLocalRefField("L.REVOLV.OFFICE").getValue();

                    presSqr = StringUtils.leftPad(presSqr, 3, "0");
                    presSqr = presSqr.substring(0, 3);

                    // String girSqrVal =
                    // paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    // remove comment
                    // girSqr = accDebt.substring(5, 8);

                    String presDate = paymentOrdRec.getPaymentExecutionDate().getValue();

                    String namePlaza = paymentOrdRec.getLocalRefField("L.COMPENSATE.PLACE").getValue();
                    String compensatePlaza = PlazaName + namePlaza;

                    EbBciHCceParticipantDirRecord ParDirRec = new EbBciHCceParticipantDirRecord(
                            da.getRecord("EB.BCI.H.CCE.PARTICIPANT.DIR", compensatePlaza));
                    String compPlazaName = ParDirRec.getSquareName().getValue();
                    compPlazaName = StringUtils.rightPad(compPlazaName, 8, "");
                    compPlazaName = compPlazaName.substring(0, 8);
                    compPlazaName = compPlazaName.concat(presDate);
                    //compPlazaName = StringUtils.rightPad(compPlazaName, 16, "");
                  //  compPlazaName = compPlazaName.substring(0, 16);
                    
                   // myWriter.write("PlazaName" +PlazaName + System.getProperty("line.separator"));
                  //  myWriter.write("compensatePlaza" +compensatePlaza +  System.getProperty("line.separator"));
                 //   myWriter.write("compPlazaName" +compPlazaName +  System.getProperty("line.separator"));
                    orgTransRec = paymentOrdRec.getLocalRefField("L.ORGNL.TXN.REF").getValue();
                    /*
                     * depInd =
                     * paymentOrdRec.getLocalRefField("L.SAME.OWNER").getValue()
                     * ; docTyp =
                     * paymentOrdRec.getLocalRefField("L.DRAWER.DOC.TYPE").
                     * getValue( ); docNum =
                     * paymentOrdRec.getLocalRefField("L.DRAWER.DOC.NUMBER").
                     * getValue();
                     */
                    try {
                        PpOrderEntryRecord ppRec = new PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", orgTransRec));
                        regCnt = ppRec.getLocalRefField("L.REGIST.CONTR").getValue();

                    } catch (Exception e) {

                    }
                    // regCnt =
                    // paymentOrdRec.getLocalRefField("L.REGIST.CONTR").getValue();
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
                            .concat(StringUtils.leftPad(presSqr, 3, "0")).concat(StringUtils.leftPad(girSqr, 3, "0"))
                            .concat(StringUtils.rightPad(compPlazaName, 16, "")).concat(addRec)
                            .concat(StringUtils.leftPad(regCnt, 15, "0"));

                    reasonRet = paymentOrdRec.getLocalRefField("L.REASON").getValue();
                    orgTransRec = paymentOrdRec.getLocalRefField("L.ORGNL.TXN.REF").getValue();
                    entOrgTrans = paymentOrdRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    try {
                        // orgTransRec = "0" + orgTransRec.substring(0, 3) + "0"
                        // + orgTransRec.substring(3, 6);
                        entOrgTrans = "0" + entOrgTrans.substring(0, 3) + "0" + entOrgTrans.substring(3, 6);
                        entOrgTrans = entOrgTrans.substring(0, 8);
                    } catch (Exception e) {

                    }

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
                        if (fieldName.equals("NAME.CTA.GIRADA")) {
                            nameCtaRotated = fieldid.getFieldValue().getValue();
                        }
                    }

                    additionalRec = typRegAdd.concat(addRegCode).concat(StringUtils.rightPad(reasonRet, 3, ""))
                            .concat(StringUtils.leftPad(orgTransRec, 15, "0")).concat(StringUtils.rightPad(free, 6, ""))
                            .concat(StringUtils.leftPad(entOrgTrans, 8, "0"))
                            .concat(StringUtils.rightPad(nameCtaRotated, 44, ""))
                            .concat(StringUtils.leftPad(regCnt, 15, "0"));

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

                    try {
                        bciTable.write(poID, bciRec);
                    } catch (Exception e) {

                    }

                }

            }

            catch (Exception e) {

            }
//myWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
//myWriter.close();
            } catch (Exception e) {

            }
            ////////////
        }

    }
    ///////
}
