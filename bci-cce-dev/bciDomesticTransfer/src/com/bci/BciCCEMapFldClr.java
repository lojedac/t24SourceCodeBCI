package com.bci;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.TField;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.CommonData;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.Flags;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.PaymentApplicationUpdate;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.PaymentContext;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.StatusAction;
import com.temenos.t24.api.hook.payments.PaymentLifecycle;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.records.customer.AddressClass;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.ebqueriesanswers.EbQueriesAnswersRecord;
import com.temenos.t24.api.records.paymentorder.ChargeTypeClass;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.records.poragreementandadvice.PorAgreementAndAdviceRecord;
import com.temenos.t24.api.records.poraudittrail.PorAuditTrailRecord;
import com.temenos.t24.api.records.porpostingandconfirmation.PorPostingAndConfirmationRecord;
import com.temenos.t24.api.records.porsupplementaryinfo.PorSupplementaryInfoRecord;
import com.temenos.t24.api.records.portransaction.PorTransactionRecord;
import com.temenos.t24.api.records.ppcompanyproperties.PpCompanyPropertiesRecord;
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
 *
 * @author anagha.s
 *         ----------------------------------------------------------------------------------------------------------------
 *         Description : This routine is used to update the template when 999
 *         status is reached during inward process.
 * 
 *         Developed By : Anagha Shastry , DB
 *
 *         Development Reference :
 *         IDD-G2-013_BCI_Interface_Interbank_Transfers_Outward_Inward
 *
 *         Attached To : PP.STATUS.ACTION>999*.
 *
 *         Attached As : Api routine attached to PP.STATUS.ACTION.
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
public class BciCCEMapFldClr extends PaymentLifecycle {

    @Override
    public void updateRequestToExternalCoreSystem(StatusAction arg0, PorTransactionRecord arg1, PaymentContext arg2,
            PorSupplementaryInfoRecord arg3, PorAgreementAndAdviceRecord arg4, PorPostingAndConfirmationRecord arg5,
            PorAuditTrailRecord arg6, PpCompanyPropertiesRecord arg7, CommonData arg8, EbQueriesAnswersRecord arg9,
            Flags arg10, PaymentApplicationUpdate arg11) {
        String transtype = arg1.getIncomingmessagetype().getValue();
        String source = arg1.getSourceproduct().getValue();
        if (transtype.equals("CCETRANS") && source.equals("POA")) {
            /*
             * FileWriter myWriter = null; String outwardFile =
             * "/project/BCI/bnk/UD/COMMT24/CCEFile/sample.txt"; File myObj =
             * new File(outwardFile); try { myObj.createNewFile(); myWriter =
             * new FileWriter(outwardFile);
             */
            Date date = new Date(this);
            DatesRecord datesRec = date.getDates();
            String today = datesRec.getToday().getValue();
            String typeReg = "";
            String transactionCode = "";
            String typeRegadd = "";
            // int valuecnt = 0;
            List<FieldNameClass> fieldList = null;
            DataAccess da = new DataAccess(this);
            String paymentOrderId = arg1.getFilereferenceincoming().getValue();
            PaymentOrderRecord paymentRec = new PaymentOrderRecord(this);
            try {
                paymentRec = new PaymentOrderRecord(da.getRecord("BNK", "PAYMENT.ORDER", "", paymentOrderId));
            } catch (Exception e) {

            }
            // *******individual registration**********//
            BciCceInterfaceParameterRecord bciParamRec = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.INDIVIDUAL"));
            fieldList = bciParamRec.getFieldName();
            String rateCode = "";
            String additionalrecs = "";
            String aaplicationCriteria = "";
            for (FieldNameClass fieldid : fieldList) {
                String fieldName = fieldid.getFieldName().getValue();
                if (fieldName.equals("TYPE.REGISTER")) {
                    typeReg = fieldid.getFieldValue().getValue();
                }
                if (fieldName.equals("TRANSACTION.CODE>PRESENTED")) {
                    transactionCode = fieldid.getFieldValue().getValue();
                }
                /*
                 * if (fieldName.equals("RATE.CODE")) { rateCode =
                 * fieldid.getFieldValue().getValue(); }
                 */
                if (fieldName.equals("ADDITIONAL.RECORDS")) {
                    additionalrecs = fieldid.getFieldValue().getValue();
                }
                /*
                 * if (fieldName.equals("APPLICATION.CRITERIA")) {
                 * aaplicationCriteria = fieldid.getFieldValue().getValue(); }
                 */
            }
            String entityAccridit = "";
            String accountToCredit = "";
            String commissionAmt = "";
            String regCount = "";
            String regCountDate = "";
            String regCountDate1 = "";
            String sameOwn = "";
            String transferType = "";
            try {
                rateCode = paymentRec.getLocalRefField("L.RATE.CODE").getValue();
                aaplicationCriteria = paymentRec.getLocalRefField("L.APPL.CRITERIA").getValue();
                entityAccridit = paymentRec.getLocalRefField("L.CR.ENTITY.COD").getValue();
                entityAccridit = entityAccridit.substring(0, 8);
                accountToCredit = paymentRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                sameOwn = paymentRec.getLocalRefField("L.SAME.OWNER").getValue();
                transferType = paymentRec.getLocalRefField("L.TRANSFER.TYPE").getValue();
                // UPDATE
                // regCount =
                // paymentRec.getLocalRefField("L.CR.ENTITY.COD").getValue();
            } catch (Exception e) {

            }
            String amount = "";
            String transactionAmt = paymentRec.getPaymentAmount().getValue();
            BigDecimal decimal = new BigDecimal(transactionAmt);
            amount = String.format("%.02f", decimal);

            try {
                int amt = amount.indexOf(".");
                amount = amount.substring(0, amt).concat(amount.substring(amt + 1));

            } catch (Exception e) {

            }
            List<ChargeTypeClass> commList = paymentRec.getChargeType();
            try {
                if (!commList.isEmpty()) {
                    // int commCnt = commList.size();
                    String commAmt = commList.get(1).getChargeAmount().getValue();
                    BigDecimal commission = new BigDecimal(commAmt);
                    commissionAmt = String.format("%.02f", commission);
                    int comAmt = commissionAmt.indexOf(".");
                    commissionAmt = commissionAmt.substring(0, comAmt).concat(commissionAmt.substring(comAmt + 1));
                }
            } catch (Exception e) {

            }
            String commissionSign = paymentRec.getPaymentCategorypurpPrty().getValue();
            if (commissionSign.equals("225")) {
                commissionSign = "-";
            } else {
                commissionSign = "+";
            }

            String originatingClient = paymentRec.getOrderingCustName().getValue();
            if (originatingClient.length() > 44) {
                originatingClient = originatingClient.substring(0, 44);
            }
            String beneficiaryname = paymentRec.getBeneficiaryName().getValue();
            if (beneficiaryname.length() > 44) {
                beneficiaryname = beneficiaryname.substring(0, 44);
            }

            String monDate = today.substring(0, 6);
            String individualHeader = "INDIVIDUALHEADER";
            String indHeaderDate = today + "-" + individualHeader;
            String RegCounter = "";
            String RegCounterDate = "";
            int regLeng = 0;

            try {
                BciCceHeaderCounterRecord archRec = new BciCceHeaderCounterRecord(
                        da.getRecord("EB.BCI.CCE.HEADER.COUNTER", individualHeader));
                RegCounter = archRec.getName(0).getValue().getValue();

            } catch (Exception e) {

            }

            if (RegCounter.equals("")) {
                RegCounter = "1";
            } else {
                int RegCnt = Integer.parseInt(RegCounter);
                RegCnt = RegCnt + 1;
                RegCounter = String.valueOf(RegCnt);

            }

            try {
                RegCounter = StringUtils.leftPad(RegCounter, 7, "0");
            } catch (Exception e) {

            }
            BciCceHeaderCounterRecord archwriteUni = new BciCceHeaderCounterRecord(this);
            NameClass fldclassUni = new NameClass();
            fldclassUni.setName("TRM");
            String ValueUni = RegCounter;
            fldclassUni.setValue(ValueUni);
            archwriteUni.setName(fldclassUni, 0);
            BciCceHeaderCounterTable tblRecUni = new BciCceHeaderCounterTable(this);
            try {
                tblRecUni.write(individualHeader, archwriteUni);
            } catch (Exception e) {
                e.getMessage();
            }

            try {
                BciCceHeaderCounterRecord archRecDate = new BciCceHeaderCounterRecord(
                        da.getRecord("EB.BCI.CCE.HEADER.COUNTER", indHeaderDate));
                RegCounterDate = archRecDate.getName(0).getValue().getValue();
                // myWriter.write("archRecDate: " + archRecDate +
                // System.getProperty("archRecDate"));
            } catch (Exception e) {

            }
            // myWriter.write("RegCounterDate: " + RegCounterDate +
            // System.getProperty("archRecDate"));

            String idUnivoco = "";
            if (RegCounterDate.equals("")) {
                RegCounterDate = "1";
            } else {
                int RegCntDate = Integer.parseInt(RegCounterDate);
                RegCntDate = RegCntDate + 1;
                RegCounterDate = String.valueOf(RegCntDate);
            }

            try {
                String company = paymentRec.getCoCode();
                CompanyRecord companyRec = new CompanyRecord(da.getRecord("COMPANY", company));
                String entidadCode = companyRec.getLocalRefField("L.ENTITY.CODE").getValue();
                String officeCode = companyRec.getLocalRefField("L.OFFICE.CODE").getValue();
                String originEntity = StringUtils.leftPad(entidadCode, 4, "0")
                        .concat(StringUtils.leftPad(officeCode, 4, "0"));
                regCount = entityAccridit.concat(RegCounter.substring(0, 7));
                RegCounterDate = StringUtils.leftPad(RegCounterDate, 7, "0");
                regCountDate = entityAccridit.concat(RegCounterDate.substring(0, 7));
                regCountDate1 = originEntity.concat(RegCounterDate.substring(0, 7));
            } catch (Exception e) {

            }
            BciCceHeaderCounterRecord archwriteRec = new BciCceHeaderCounterRecord(this);
            // myWriter.write("indHeaderDate: " + indHeaderDate +
            // System.getProperty("indHeaderDate"));
            NameClass fldclass = new NameClass();
            fldclass.setName("TRM");
            String Value = RegCounterDate;
            fldclass.setValue(Value);
            archwriteRec.setName(fldclass, 0);
            BciCceHeaderCounterTable tblRec = new BciCceHeaderCounterTable(this);
            // myWriter.write("tblRec: " + tblRec +
            // System.getProperty("tblRec"));
            try {
                tblRec.write(indHeaderDate, archwriteRec);
                // myWriter.write(System.getProperty("WRITTEN HEADER COUNTS"));
            } catch (Exception e) {
                e.getMessage();
            }

            String creditCode = "";
            String noBenDoc = "";
            String benDoc = "";
            String cciOrginClient = "";
            String docType = "";
            String free = "";
            String payMonth = "";
            String payYear = "";
            String payDate = "";
            String grossAmt = "";
            try {
                // cciOrginClient =
                // paymentRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
                benDoc = paymentRec.getLocalRefField("L.DRAWER.DOC.TYPE").getValue();
                noBenDoc = paymentRec.getLocalRefField("L.DRAWER.DOC.NUMBER").getValue();
                cciOrginClient = paymentRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
                String grossAmt1 = "";
                grossAmt1 = paymentRec.getLocalRefField("L.GROSS.SAL.AMT").getValue();
                BigDecimal bdnum = new BigDecimal(grossAmt1).setScale(2, RoundingMode.HALF_UP);
                grossAmt = String.valueOf(bdnum).replace(".", "");

                /*
                 * try { cciOrginClient = "0" + cciOrginClient.substring(0, 3) +
                 * "0" + cciOrginClient.substring(3, 6); } catch (Exception e) {
                 * 
                 * }
                 */
                creditCode = paymentRec.getLocalRefField("L.CRDT.CARD.NUMBER").getValue();
                payDate = paymentRec.getPaymentExecutionDate().getValue();
                /*
                 * payMonth = payDate.substring(4, 6); payYear =
                 * payDate.substring(0, 4);
                 */
                payMonth = payDate;
                payYear = payDate;

            } catch (Exception e) {

            }
            BciCceInterfaceParameterRecord bciDocRec = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.DOCUMENT.TYPE"));
            List<FieldNameClass> flListDoc = bciDocRec.getFieldName();
            for (FieldNameClass fieldid : flListDoc) {
                String fieldName = fieldid.getFieldName().getValue();
                if (fieldName.equals(benDoc)) {
                    docType = fieldid.getFieldValue().getValue();
                }
            }
            String transferRef = RegCounter;
            String inRef = paymentRec.getPaymentSystemId().getValue();
            if (transferType.equals("220")) {
                if (sameOwn.equals("YES")) {
                    noBenDoc = StringUtils.leftPad(noBenDoc, 12, "0");
                    transferRef = StringUtils.leftPad(transferRef, 11, "0");
                    transferRef = docType.substring(0, 1).concat(noBenDoc.substring(0, 12))
                            .concat(transferRef.substring(0, 11)).concat("M");
                } else {
                    transferRef = StringUtils.leftPad(transferRef, 24, "0");
                    transferRef = transferRef.substring(0, 24).concat("O");
                }
            }

            String subTransferType = paymentRec.getLocalRefField("L.SUBTRASF.TYPE").getValue();
            if (transferType.equals("221")) {
                transferRef = StringUtils.leftPad(transferRef, 24, "0");
                transferRef = transferRef.substring(0, 24).concat(subTransferType);
            }
            if (transferType.equals("222")) {
                transferRef = StringUtils.leftPad(transferRef, 24, "0");
                transferRef = transferRef.substring(0, 24).concat(StringUtils.rightPad(free, 1, ""));
            }
            String anioMes = paymentRec.getLocalRefField("L.ANIOMES.CTS").getValue();
            if (transferType.equals("223")) {
                transferRef = StringUtils.leftPad(transferRef, 24, "0");
                transferRef = anioMes.substring(4, 6).concat(anioMes.substring(0, 4))
                        .concat(transferRef.substring(21, 24)).concat(StringUtils.leftPad(grossAmt, 15, "0"))
                        .concat(StringUtils.rightPad(free, 1, ""));
            }

            // System.out.println("transref= "");
            // int univocaltransferseqVal = regCount.length() - 9;
            String univocaltransferseq = regCount.substring(8);
            idUnivoco = StringUtils.leftPad(univocaltransferseq, 15, "0");
            String individualStr = typeReg.concat(transactionCode).concat(StringUtils.leftPad(entityAccridit, 8, "0"))
                    .concat(StringUtils.rightPad(rateCode, 1, ""))
                    .concat(StringUtils.rightPad(aaplicationCriteria, 1, ""))
                    .concat(StringUtils.leftPad(accountToCredit, 20, "0")).concat(StringUtils.leftPad(amount, 15, "0"))
                    .concat(StringUtils.rightPad(commissionSign, 1, ""))
                    .concat(StringUtils.leftPad(commissionAmt, 15, "0"))
                    .concat(StringUtils.rightPad(originatingClient, 44, ""))
                    .concat(StringUtils.rightPad(beneficiaryname, 44, ""))
                    .concat(StringUtils.leftPad(transferRef, 25, "0"))
                    .concat(StringUtils.leftPad(univocaltransferseq, 7, "0"))
                    .concat(StringUtils.leftPad(additionalrecs, 1, "0")).concat(regCountDate1);

            // myWriter.write("RegCounterDate: " + RegCounterDate +
            // System.getProperty("line.separator"));
            // myWriter.write("transferRef: " + transferRef +
            // System.getProperty("line.separator"));
            // myWriter.write("univocaltransferseq: " + univocaltransferseq +
            // System.getProperty("line.separator"));
            // myWriter.write("regCountDate: " + regCountDate +
            // System.getProperty("line.separator"));

            // *******additional registration**********//
            String paymentConf = "";
            String addCode = "";

            bciParamRec = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.ADDITIONAL"));
            // myWriter.write("bciParamRec: " + bciParamRec +
            // System.getProperty("line.separator"));
            fieldList = bciParamRec.getFieldName();
            for (FieldNameClass fieldid : fieldList) {
                String fieldName = fieldid.getFieldName().getValue();
                if (fieldName.equals("TYPE.REGISTER")) {
                    typeRegadd = fieldid.getFieldValue().getValue();
                }
                if (fieldName.equals("PAYMENT.CONFIRMATION")) {
                    paymentConf = fieldid.getFieldValue().getValue();
                }
                if (fieldName.equals("ADDITIONAL.REGISTRATION")) {
                    addCode = fieldid.getFieldValue().getValue();
                }
                /*
                 * if (fieldName.equals("L.CCI.CODE.ORIG")) { cciOrginClient =
                 * fieldid.getFieldValue().getValue(); }
                 */

            }

            // String benPrvnc = paymentRec.getBeneficiaryBrPrvnc().getValue();
            // String benCity = paymentRec.getBeneficiaryBrCity().getValue();
            // String benAddress = benPrvnc.concat(benCity);
            // Changed next 3 lines for issue 206-G
            String benAddressLine1 = "";
            String benAddress = "";

            List<TField> strAdd = paymentRec.getBenPostAddrLine();

            for (TField strAdd1 : strAdd) {
                benAddressLine1 = benAddressLine1.concat(strAdd1.getValue());
                benAddress = benAddressLine1;
                if (benAddress.length() > 58) {
                    benAddress = benAddress.substring(0, 58);
                }
            }

            // String benAddress = benAddressLine1.concat(benAddressLine2);

            String benTelephone = paymentRec.getBeneficiaryContMob().getValue();
            if (benTelephone.length() > 10) {
                benTelephone = benTelephone.substring(0, 10);
            }

            String originatingClientadd = "";
            String cusId = paymentRec.getOrderingCustomer().getValue();
            // myWriter.write("cusId" + cusId +
            // System.getProperty("line.separator"));
            String address = "";
            String postCode = "";
            String townName = "";
            CustomerRecord cusRec = new CustomerRecord(da.getRecord("BNK", "CUSTOMER", "", cusId));
            List<AddressClass> addressList = cusRec.getAddress();
            List<TField> postCodeList = cusRec.getPostCode();
            List<TField> townNameList = cusRec.getTownCountry();
            // myWriter.write("addressList" + addressList +
            // System.getProperty("line.separator"));
            if (!addressList.isEmpty()) {
                int addressCnt = addressList.size();
                for (int i = 0; i < addressCnt; i++) {
                    String add = cusRec.getAddress().get(i).get(0).getValue();

                    String ad = add.concat(",");
                    address = address.concat(ad);
                }
                // myWriter.write("address" + address +
                // System.getProperty("line.separator"));
                address = address.substring(0, address.length() - 1);
            }
            if (!postCodeList.isEmpty()) {
                int postCnt = postCodeList.size();
                for (int i = 0; i < postCnt; i++) {
                    postCode = cusRec.getPostCode().get(i).getValue();
                }
                // myWriter.write("postCode" + postCode +
                // System.getProperty("line.separator"));

            }
            if (!townNameList.isEmpty()) {
                int townCnt = townNameList.size();
                for (int i = 0; i < townCnt; i++) {
                    townName = cusRec.getTownCountry().get(i).getValue();
                }
                // myWriter.write("townName" +townName +
                // System.getProperty("line.separator"));

            }
            // myWriter.write("address2" + address +
            // System.getProperty("line.separator"));
            originatingClientadd = originatingClientadd.concat(address).concat(" ").concat(townName).concat(" ")
                    .concat(postCode);
            if (originatingClientadd.length() > 58) {
                originatingClientadd = originatingClientadd.substring(0, 58);
            }

            // String free = "";
            String addStr = typeRegadd.concat(addCode).concat(docType).concat(StringUtils.leftPad(noBenDoc, 12, "0"))
                    .concat(StringUtils.rightPad(benAddress, 58, "")).concat(StringUtils.leftPad(benTelephone, 10, "0"))
                    .concat(StringUtils.leftPad(creditCode, 20, "0"))
                    .concat(StringUtils.rightPad(originatingClientadd, 58, ""))
                    .concat(StringUtils.leftPad(cciOrginClient, 20, "0")).concat(paymentConf)
                    .concat(StringUtils.rightPad(free, 2, "")).concat(regCountDate1);

            String finaddStr = addStr;
            // myWriter.write("addStr: " + addStr +
            // System.getProperty("line.separator"));
            BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(this);
            mapRec.setStatus("TO_BE_SENT_OUT");
            mapRec.setPoId(paymentOrderId);
            mapRec.setInRef(inRef);
            mapRec.setDate(today);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            mapRec.setTime(dtf.format(now));
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    MapFieldTypeClass mapClass = new MapFieldTypeClass();
                    mapClass.setMapFieldType("INDIVIDUAL");
                    mapClass.setMapFieldVal(individualStr.substring(0, 100), 0);
                    mapClass.setMapFieldVal(individualStr.substring(100), 1);
                    mapRec.setMapFieldType(mapClass, i);
                }
                if (i == 1) {
                    MapFieldTypeClass mapClass1 = new MapFieldTypeClass();
                    mapClass1.setMapFieldType("ADDITIONAL");
                    mapClass1.setMapFieldVal(finaddStr.substring(0, 100), 0);
                    mapClass1.setMapFieldVal(finaddStr.substring(100), 1);
                    mapRec.setMapFieldType(mapClass1, i);
                }
            }

            BciCceMappingFieldValuesTable mapTbl = new BciCceMappingFieldValuesTable(this);
            try {
                mapTbl.write(idUnivoco, mapRec);
                // mapTbl.write(transferRef, mapRec);
                // myWriter.write("regCountDate: " + regCountDate +
                // System.getProperty("line.separator"));
            } catch (Exception e) {

            }
            // myWriter.write("mapTbl: " + mapTbl +
            // System.getProperty("line.separator"));
            // myWriter.write("mapRec: " + mapRec +
            // System.getProperty("line.separator"));
            // myWriter.close();
        }

        /*
         * catch (Exception e) { e.printStackTrace(); } finally { try {
         * 
         * myWriter.close(); } catch (IOException e) {
         * 
         * } } }
         */
    }
}
