package com.bci;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bcicceinchqrtnadjbalance.BciCceInChqRtnAdjBalanceRecord;
import com.temenos.t24.api.tables.bcicceinchqrtnadjbalance.BciCceInChqRtnAdjBalanceTable;
import com.temenos.t24.api.tables.bcicceinchqrtnadjbalance.AdjMapFieldTypeClass;
import com.temenos.t24.api.tables.bcicceinchqrtnbalfavourable.BciCceInChqRtnBalFavourableRecord;
import com.temenos.t24.api.tables.bcicceinchqrtnbalfavourable.BciCceInChqRtnBalFavourableTable;
import com.temenos.t24.api.tables.bcicceinchqrtnbalfavourable.BalMapFieldTypeClass;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;

public class BciCceChqOutOfsRtn extends ServiceLifecycle {

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        List<String> list = null;
        list = da.selectRecords("", "EB.BCI.CCE.CLEARING.PARAM", "", "WITH @ID EQ SYSTEM");
        // System.out.println("list " + list);
        return list;
    }

    @Override
    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {

        Date date = new Date(this);
        DatesRecord datesRec = date.getDates();
        String today = datesRec.getToday().getValue();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String entDebt = "";
        String regCnt = "";
        String accDebt = "";
        String accDebtVal = "";
        String amount = "";
        String accCred = "";
        String versionName = "";
        String reasonRet = "";
        String cheqNum = "";
        String presSqr = "";
        String issSqr = "";
        String typPlace = "";
        String drwDocTyp = "";
        String drwDocNum = "";
        String docType = "";
        String orgTranxRef = "";
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        List<String> recListAdj = da.selectRecords("", "EB.BCI.CCE.IN.CHQ.RTN.ADJ.BALANCE", "",
                "WITH STATUS EQ TO_BE_ADJUST_OUT_R17_PARTIAL OR STATUS EQ TO_BE_ADJUST_OUT_R18_PARTIAL");

        PaymentOrderRecord payRec = new PaymentOrderRecord(this);
        for (String recid : recListAdj) {
            BciCceInChqRtnAdjBalanceRecord bciAdjBalRec = new BciCceInChqRtnAdjBalanceRecord(
                    da.getRecord("EB.BCI.CCE.IN.CHQ.RTN.ADJ.BALANCE", recid));

            String indAdjRec = bciAdjBalRec.getAdjMapFieldType(0).getAdjMapFieldVal(0).getValue();
            String addAdjRec = bciAdjBalRec.getAdjMapFieldType(1).getAdjMapFieldVal(0).getValue();
            String status = bciAdjBalRec.getStatus().getValue();
            if (indAdjRec.substring(0, 1).equals("6")) {
                entDebt = indAdjRec.substring(5, 13);

                accDebtVal = indAdjRec.substring(14, 32);
                System.out.println("accDebtVal: " + accDebtVal);
                accDebt = accDebtVal.substring(6, 16);
                System.out.println("accDebt1: " + accDebt);
                BigInteger acctNo = new BigInteger(accDebt);
                accDebt = String.valueOf(acctNo);

                System.out.println("acctNo: " + acctNo);
                System.out.println("accDebt2: " + accDebt);

                // System.out.println("ACCOUNT NUMBER: " + accDebt);
                amount = indAdjRec.substring(32, 47);
                try {
                    amount = amount.substring(0, 13).concat(".").concat(amount.substring(13, 15));
                } catch (Exception e) {

                }
                System.out.println("amount: " + amount);
                cheqNum = indAdjRec.substring(47, 55);
                presSqr = indAdjRec.substring(56, 59);
                issSqr = indAdjRec.substring(59, 62);
                typPlace = indAdjRec.substring(64, 65);
                drwDocTyp = indAdjRec.substring(65, 66);
                drwDocNum = indAdjRec.substring(66, 78);
                regCnt = indAdjRec.substring(79);

            }

            if (addAdjRec.substring(0, 1).equals("7")) {
                reasonRet = addAdjRec.substring(3, 6);
                orgTranxRef = addAdjRec.substring(6, 21);
            }

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
                if (fieldName.equals("RETURNED.TRANS.CHQ.OUT.ADJ")) {
                    versionName = fieldid.getFieldValue().getValue();
                }
            }

            String DocName = "";
            BciCceInterfaceParameterRecord bciDocRec = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.DOCUMENT.TYPE"));
            List<FieldNameClass> flListDoc = bciDocRec.getFieldName();
            for (FieldNameClass fieldid : flListDoc) {
                docType = fieldid.getFieldValue().getValue();
                // System.out.println("flListDoc: " + docType);
                if (docType.equals(drwDocTyp)) {
                    DocName = fieldid.getFieldName().getValue();
                }
            }

            AdjMapFieldTypeClass adjFldType = new AdjMapFieldTypeClass();
            AdjMapFieldTypeClass adjFldType2 = new AdjMapFieldTypeClass();
            BciCceInChqRtnAdjBalanceTable bciAdjBalTab = new BciCceInChqRtnAdjBalanceTable(this);
            payRec.getLocalRefField("L.CCI.DESTINATION").set(accDebt);
            payRec.getLocalRefField("L.CCI.CODE.ORIG").set(presSqr);
            payRec.getLocalRefField("L.SAME.OWNER").set(typPlace);
            payRec.setPaymentAmount(amount);

            // ppRec.getLocalRefField("L.ISSUER.SQUR").set(issSqr);
            payRec.getLocalRefField("L.CHEQUE.NO").set(cheqNum);
            payRec.getLocalRefField("L.DRAWER.DOC.TYPE").set(DocName);
            payRec.getLocalRefField("L.DRAWER.DOC.NUMBER").set(drwDocNum);
            payRec.getLocalRefField("L.REGIST.CONTR").set(regCnt);
            payRec.getLocalRefField("L.ORGNL.TXN.REF").set(orgTranxRef);
            payRec.getLocalRefField("L.REASON").set(reasonRet);
            payRec.setOrderingReference(regCnt);

            bciAdjBalRec.setDate(today);
            bciAdjBalRec.setTime(dtf.format(now));

            if (status.equals("TO_BE_ADJUST_OUT_R17_PARTIAL")) {
                bciAdjBalRec.setStatus("TO_BE_ADJUST_OUT_R17_OFS");
                // need to check
                payRec.setCreditAccount(accDebt);
                payRec.setDebitAccount(accCred);

            }
            if (status.equals("TO_BE_ADJUST_OUT_R18_PARTIAL")) {
                bciAdjBalRec.setStatus("TO_BE_ADJUST_OUT_R18_OFS");
                // need to check
                payRec.setCreditAccount(accCred);
                payRec.setDebitAccount(accDebt);
            }

            adjFldType.setAdjMapFieldType("INDIVIDUAL-B");
            adjFldType.setAdjMapFieldVal(indAdjRec, 0);
            bciAdjBalRec.setAdjMapFieldType(adjFldType, 0);

            adjFldType2.setAdjMapFieldType("ADDITIONAL-B");
            adjFldType2.setAdjMapFieldVal(addAdjRec, 0);
            bciAdjBalRec.setAdjMapFieldType(adjFldType2, 1);

            TransactionData txnData = new TransactionData();
            txnData.setFunction("INPUTT");
            txnData.setNumberOfAuthoriser("0");
            /// confirm OFS source
            txnData.setSourceId("BCI.CHQ.OFS");
            txnData.setVersionId(versionName);
            // txnData.setCompanyId("PE0010001");
            // txnData.setVersionId("PAYMENT.ORDER,");
            txnData.setTransactionId("/");
            transactionData.add(txnData);
            records.add(payRec.toStructure());

            try {
                bciAdjBalTab.write(regCnt, bciAdjBalRec);
            } catch (Exception e) {

            }
        }

        List<String> recListBal = da.selectRecords("", "EB.BCI.CCE.IN.CHQ.RTN.BAL.FAVOURABLE", "",
                "WITH STATUS EQ TO_BE_ADJUST_OUT_R16_PARTIAL");
        // REMOVED STSTUS EQ TO_BE_RETURNED_ADDITIONAL_A

        for (String recid : recListBal) {
            BciCceInChqRtnBalFavourableRecord bciFavBalRec = new BciCceInChqRtnBalFavourableRecord(
                    da.getRecord("EB.BCI.CCE.IN.CHQ.RTN.BAL.FAVOURABLE", recid));

            String indBalRec = bciFavBalRec.getBalMapFieldType(0).getBalMapFieldVal(0).getValue();
            String addBalRec = bciFavBalRec.getBalMapFieldType(1).getBalMapFieldVal(0).getValue();
            String status = bciFavBalRec.getStatus().getValue();
            if (indBalRec.substring(0, 1).equals("6")) {
                entDebt = indBalRec.substring(5, 13);

                accDebtVal = indBalRec.substring(14, 32);
                accDebt = accDebtVal.substring(6, 16);
                BigInteger acctNo = new BigInteger(accDebt);
                accDebt = String.valueOf(acctNo);

                System.out.println("ACCOUNT NUMBER: " + accDebt);
                amount = indBalRec.substring(32, 47);
                try {
                    amount = amount.substring(0, 13).concat(".").concat(amount.substring(13, 15));
                } catch (Exception e) {

                }

                cheqNum = indBalRec.substring(47, 55);
                presSqr = indBalRec.substring(56, 59);
                issSqr = indBalRec.substring(59, 62);
                typPlace = indBalRec.substring(64, 65);
                drwDocTyp = indBalRec.substring(65, 66);
                drwDocNum = indBalRec.substring(66, 78);
                regCnt = indBalRec.substring(79);
            }

            if (addBalRec.substring(0, 1).equals("7")) {
                reasonRet = addBalRec.substring(3, 6);
                orgTranxRef = addBalRec.substring(6, 21);
            }

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
                if (fieldName.equals("RETURNED.TRANS.CHQ.OUT.BAL")) {
                    versionName = fieldid.getFieldValue().getValue();
                }
            }

            String DocName = "";
            BciCceInterfaceParameterRecord bciDocRec = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.DOCUMENT.TYPE"));
            List<FieldNameClass> flListDoc = bciDocRec.getFieldName();
            for (FieldNameClass fieldid : flListDoc) {
                docType = fieldid.getFieldValue().getValue();
                // System.out.println("flListDoc: " + docType);
                if (docType.equals(drwDocTyp)) {
                    DocName = fieldid.getFieldName().getValue();
                }
            }

            BalMapFieldTypeClass balFldType = new BalMapFieldTypeClass();
            BalMapFieldTypeClass balFldType2 = new BalMapFieldTypeClass();
            BciCceInChqRtnBalFavourableTable bciFavBalTab = new BciCceInChqRtnBalFavourableTable(this);
            payRec.getLocalRefField("L.CCI.DESTINATION").set(accDebt);
            payRec.getLocalRefField("L.CCI.CODE.ORIG").set(presSqr);
            payRec.getLocalRefField("L.SAME.OWNER").set(typPlace);
            payRec.setPaymentAmount(amount);
            payRec.setCreditAccount(accCred);
            payRec.setDebitAccount(accDebt);
            // ppRec.getLocalRefField("L.ISSUER.SQUR").set(issSqr);
            payRec.getLocalRefField("L.CHEQUE.NO").set(cheqNum);
            payRec.getLocalRefField("L.DRAWER.DOC.TYPE").set(DocName);
            payRec.getLocalRefField("L.DRAWER.DOC.NUMBER").set(drwDocNum);
            payRec.getLocalRefField("L.REGIST.CONTR").set(regCnt);
            payRec.getLocalRefField("L.ORGNL.TXN.REF").set(orgTranxRef);
            payRec.getLocalRefField("L.REASON").set(reasonRet);
            payRec.setOrderingReference(regCnt);

            bciFavBalRec.setDate(today);
            bciFavBalRec.setTime(dtf.format(now));
            bciFavBalRec.setStatus("TO_BE_ADJUST_OUT_R16_OFS");

            balFldType.setBalMapFieldType("INDIVIDUAL-C");
            balFldType.setBalMapFieldVal(indBalRec, 0);
            bciFavBalRec.setBalMapFieldType(balFldType, 0);

            balFldType2.setBalMapFieldType("ADDITIONAL-C");
            balFldType2.setBalMapFieldVal(addBalRec, 0);
            bciFavBalRec.setBalMapFieldType(balFldType2, 1);

            TransactionData txnData = new TransactionData();
            txnData.setFunction("INPUTT");
            txnData.setNumberOfAuthoriser("0");
            /// confirm OFS source
            // new OFS source
            txnData.setSourceId("BCI.CHQ.OFS");
            txnData.setVersionId(versionName);
            // txnData.setCompanyId("PE0010001");
            // txnData.setVersionId("PAYMENT.ORDER,");
            txnData.setTransactionId("/");
            transactionData.add(txnData);
            records.add(payRec.toStructure());
            
           
            try {
                bciFavBalTab.write(regCnt, bciFavBalRec);
            } catch (Exception e) {

            }
        }
    }

}
