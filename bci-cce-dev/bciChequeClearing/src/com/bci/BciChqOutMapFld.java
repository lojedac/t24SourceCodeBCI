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
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bcicceheadercounter.BciCceHeaderCounterRecord;
import com.temenos.t24.api.tables.bcicceheadercounter.BciCceHeaderCounterTable;
import com.temenos.t24.api.tables.bcicceheadercounter.NameClass;
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
public class BciChqOutMapFld extends PaymentLifecycle {

    @Override
    public void updateRequestToExternalCoreSystem(StatusAction arg0, PorTransactionRecord arg1, PaymentContext arg2,
            PorSupplementaryInfoRecord arg3, PorAgreementAndAdviceRecord arg4, PorPostingAndConfirmationRecord arg5,
            PorAuditTrailRecord arg6, PpCompanyPropertiesRecord arg7, CommonData arg8, EbQueriesAnswersRecord arg9,
            Flags arg10, PaymentApplicationUpdate arg11) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        List<FieldNameClass> fldList = null;
        Date date = new Date(this);
        DatesRecord datesRec = date.getDates();
        String today = datesRec.getToday().getValue();

        /*
         * String outwardFile =
         * "/project/BackupData/BACKUP/BCIISB/BNK/UD/CCE/OUTWARDCHQ/sample.txt";
         * List<String> finalList = new ArrayList<String>(); FileWriter myWriter
         * = null; File myObj = new File(outwardFile); try {
         * myObj.createNewFile(); myWriter = new FileWriter(outwardFile);
         */
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            String tellerID = arg1.getTransactionreferenceincoming().getValue();
            String ftNum = arg1.getFtnumber().getValue();

            int valuecnt = 0;
            // myWriter.write("TELLER: " + tellerID +
            // System.getProperty("line.separator"));

            BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(this);

            MapFieldTypeClass bciMapRec = new MapFieldTypeClass();

            BciCceMappingFieldValuesTable bciTable = new BciCceMappingFieldValuesTable(this);

            TellerRecord telRec = new TellerRecord(da.getRecord("TELLER", tellerID));
            BciCceInterfaceParameterRecord bciParamIndRec = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.INDIVIDUAL.CHQ"));
            fldList = bciParamIndRec.getFieldName();
            String typeReg = "";
            String transCode = "";
            String trunCode = "";
            String addRec = "";
            String girSqr = "";
            String lcciCodeOrg = "";
            String docType = "";
            for (FieldNameClass fieldid : fldList) {
                String fieldName = fieldid.getFieldName().getValue();

                if (fieldName.equals("TYPE.REGISTER")) {
                    typeReg = fieldid.getFieldValue().getValue();
                }
                if (fieldName.equals("TRANSACTION.CODE.INDA")) {
                    transCode = fieldid.getFieldValue().getValue();
                }
                if (fieldName.equals("TRUNCATION.INDICATOR")) {
                    trunCode = fieldid.getFieldValue().getValue();
                }
                if (fieldName.equals("ADDITIONAL.RECORDS")) {
                    addRec = fieldid.getFieldValue().getValue();
                }
                if (fieldName.equals("GIRADA.SQUARE")) {
                    girSqr = fieldid.getFieldValue().getValue();
                }
                if (fieldName.equals("L.CCI.CODE.ORIG")) {
                    lcciCodeOrg = fieldid.getFieldValue().getValue();
                }

            }
            // myWriter.write("typeReg: " + typeReg +
            // System.getProperty("line.separator"));
            // trunCode =
            // telRec.getLocalRefField("L.TRUNCATION").getValue();
            String lcciDest = telRec.getLocalRefField("L.CCI.DESTINATION").getValue();

            String netAmt = "";

            String transactionAmt = telRec.getNetAmount().getValue();
            BigDecimal decimal = new BigDecimal(transactionAmt);
            netAmt = String.format("%.02f", decimal);
            int amt = netAmt.indexOf(".");
            netAmt = netAmt.substring(0, amt).concat(netAmt.substring(amt + 1));

            String cheqNum = telRec.getChequeNumber(0).getChequeNumber().getValue();
            // telRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
            String free = "";
            String lcciSameOwn = telRec.getLocalRefField("L.SAME.OWNER").getValue();

            String lDrwDocType = telRec.getLocalRefField("L.DRAWER.DOC.TYPE").getValue();
            String lDrwDocNum = telRec.getLocalRefField("L.DRAWER.DOC.NUMBER").getValue();
            String entDebtCred = "";
            String presSqr = "";
            // myWriter.write("Before try catch " +
            // System.getProperty("line.separator"));
            try {
                entDebtCred = "0" + lcciDest.substring(0, 3) + "0" + lcciDest.substring(3, 6);
                presSqr = lcciCodeOrg.substring(5, 8);
                cheqNum = StringUtils.leftPad(cheqNum, 8, "0");
                cheqNum = cheqNum.substring(0, 8) + "0";
                lcciSameOwn = lcciSameOwn.substring(0, 1);
                
               if(lcciSameOwn.equals("Y")) {
                   lcciSameOwn = "M";
               }
               if(lcciSameOwn.equals("N")){
                   lcciSameOwn = "O";
               }
                
                lcciDest = StringUtils.leftPad(lcciDest, 18, "0");
                lcciDest = lcciDest.substring(0, 16) + "00";
            } catch (Exception e) {

            }

            BciCceInterfaceParameterRecord bciDocRec = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.DOCUMENT.TYPE"));
            List<FieldNameClass> flListDoc = bciDocRec.getFieldName();
            for (FieldNameClass fieldid : flListDoc) {
                String fieldName = fieldid.getFieldName().getValue();
                if (fieldName.equals(lDrwDocType)) {
                    docType = fieldid.getFieldValue().getValue();
                }
            }

            String todayIN = "INDIVIDUALHEADER";
            // String dateRegCnt = today.substring(4, 8);
            // myWriter.write("HEADER ID: " +todayIN +
            // System.getProperty("line.separator"));
            String seqNum = "";
            try {
                BciCceHeaderCounterRecord archRec = new BciCceHeaderCounterRecord(
                        da.getRecord("EB.BCI.CCE.HEADER.COUNTER", todayIN));
                seqNum = archRec.getName(0).getValue().getValue();

            } catch (Exception e) {

            }
            // myWriter.write("seqNum: " + seqNum +
            // System.getProperty("line.separator"));
            if (seqNum.equals("")) {
                seqNum = "1";
            }

            String regCntVal = seqNum;
            if (regCntVal.equals("")) {
                regCntVal = "1";
            } else {
                int RegCnt = Integer.parseInt(regCntVal);
                RegCnt = RegCnt + 1;
                regCntVal = String.valueOf(RegCnt);
            }
            //regCntVal = entDebtCred + StringUtils.leftPad(regCntVal, 7, "0");
            regCntVal = StringUtils.leftPad(regCntVal, 15, "0");
            String oeId = arg3.getOrderEntryId(0).getValue();
            // String bnkRef = arg1.getFtnumber().getValue();
            // myWriter.write("ftNum: " +ftNum +
            // System.getProperty("line.separator"));
            String individualRec = typeReg.concat(transCode).concat(StringUtils.leftPad(entDebtCred, 8, "0"))
                    .concat(trunCode).concat(lcciDest).concat(StringUtils.leftPad(netAmt, 15, "0"))
                    .concat(StringUtils.leftPad(cheqNum, 9, "0")).concat(StringUtils.leftPad(presSqr, 3, "0"))
                    .concat(StringUtils.leftPad(girSqr, 3, "0")).concat(StringUtils.rightPad(free, 2, ""))
                    .concat(lcciSameOwn).concat(StringUtils.rightPad(docType, 1, "0"))
                    .concat(StringUtils.rightPad(lDrwDocNum, 12, "0")).concat(StringUtils.rightPad(addRec, 1, "0"))
                    .concat(regCntVal);
            // myWriter.write("individualRec: " + individualRec +
            // System.getProperty("line.separator"));
            bciRec.setDate(today);
            bciRec.setTime(dtf.format(now));
            bciMapRec.setMapFieldType("INDIVIDUAL-A");
            bciMapRec.setMapFieldVal(individualRec, 0);
            bciRec.setMapFieldType(bciMapRec, 0);
            bciRec.setPoId(tellerID);
            bciRec.setOeId(oeId);
            bciRec.setInRef(ftNum);
            bciRec.setStatus("TO_BE_SENT_CHQOUT");
            // bciRec.setPoId(ftNum);
            // myWriter.write("bciRec: " + bciRec +
            // System.getProperty("line.separator"));
            try {
                bciTable.write(regCntVal, bciRec);
            } catch (Exception e) {

            }
            BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
            NameClass fldclass = new NameClass();
            fldclass.setName("ARCHNO");
            String Value = regCntVal;
            fldclass.setValue(Value);
            archwriteRec.setName(fldclass, 0);
            BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
            try {
                tblRec.write(todayIN, archwriteRec);
            } catch (Exception e) {

            }
            // myWriter.write("GENERATING FILE" +
            // System.getProperty("line.separator"));
        } catch (Exception e) {

        }
        /*
         * myWriter.close(); } catch (Exception e) { e.printStackTrace(); }
         * finally { try { myWriter.close(); } catch (IOException e) {
         * 
         * }
         * 
         * }
         */

    }

}
