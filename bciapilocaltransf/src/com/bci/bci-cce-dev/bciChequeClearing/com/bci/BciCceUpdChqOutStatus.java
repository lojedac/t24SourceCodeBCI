package com.bci;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.tables.bcicceclearingparam.BciCceClearingParamRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.records.acchargerequest.AcChargeRequestRecord;
import com.temenos.t24.api.records.acchargerequest.ChargeCodeClass;

/**
 * TODO: Document me!
 *
 * @author spoorthi.bs
 *
 */
public class BciCceUpdChqOutStatus extends ServiceLifecycle {

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub

        DataAccess da = new DataAccess(this);
        List<String> recList = null;
        recList = da.selectRecords("", "EB.BCI.CCE.CLEARING.PARAM", "", "WITH @ID EQ SYSTEM");

        return recList;
    }

    @Override
    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
        // TODO Auto-generated method stub
        DataAccess da = new DataAccess(this);
        // List<String> recList = da.selectRecords("",
        // "EB.BCI.CCE.MAPPING.FIELD.VALUES", "", "WITH STATUS EQ SENT_CHQOUT");
        // TODO Auto-generated method stub
        // for (String recid : recList) {
        // id = recid;
        BciCceClearingParamRecord paramrec = new BciCceClearingParamRecord(
                da.getRecord("EB.BCI.CCE.CLEARING.PARAM", "SYSTEM"));

        String inpath = paramrec.getChqInPath().getValue();
        String fileData = serviceData.getJobData(0);
        String fileName = inpath + "/" + fileData;

        List<String> lines = Collections.emptyList();
        String transCode = "";
        String regCnt = "";
        String accDebt = "";
        String accDebtVal = "";
        String amount = "";
        String cheqNum = "";
        String versionName = "";
        String tellerId = "";
        String individualline = "";
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<String> itr = lines.iterator();
        while (itr.hasNext()) {
            String line = itr.next();
            System.out.println("fileName: " + fileName);
            System.out.println("line: " + line);
            if (line.substring(1, 5).equals("2604")) {

                System.out.println("id: " + id);
                Date date = new Date(this);
                DatesRecord datesRec = date.getDates();
                String today = datesRec.getToday().getValue();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                regCnt = line.substring(79);
                id = regCnt;
                MapFieldTypeClass mapFldType = new MapFieldTypeClass();
                MapFieldTypeClass mapFldType2 = new MapFieldTypeClass();

                BciCceMappingFieldValuesRecord bciRec = new BciCceMappingFieldValuesRecord(
                        da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", id));
                BciCceMappingFieldValuesTable bciTable = new BciCceMappingFieldValuesTable(this);
                System.out.println("template found");
                // bciRec.setStatus("CLEARED_CHQOUT");

                String indType = bciRec.getMapFieldType(0).getMapFieldType().getValue();
                String indVal = bciRec.getMapFieldType(0).getMapFieldVal(0).getValue();
                String oeId = bciRec.getOeId().getValue();
                String bnkRef = bciRec.getInRef().getValue();
                // PpOrderEntryRecord ppRec = new
                // PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", oeId));
                PpOrderEntryRecord ppRec = new PpOrderEntryRecord(this);
                System.out.println("record found: " + oeId);

                ppRec.setChequestatus("CLEARED");

                TransactionData txnData = new TransactionData();
                txnData.setFunction("INPUTT");
                txnData.setNumberOfAuthoriser("0");
                txnData.setSourceId("OFS.LOAD");
                txnData.setVersionId("PP.ORDER.ENTRY,CLEARED"); // need to be
                txnData.setCompanyId("PE0010001");
                txnData.setTransactionId(oeId);
                transactionData.add(txnData);
                records.add(ppRec.toStructure());

                mapFldType.setMapFieldType(indType);
                mapFldType.setMapFieldVal(indVal, 0);
                bciRec.setMapFieldType(mapFldType, 0);

                bciRec.setDate(today);
                bciRec.setTime(dtf.format(now));
                bciRec.setOeId(oeId);
                bciRec.setInRef(bnkRef);

                try {
                    bciTable.write(id, bciRec);
                } catch (Exception e) {

                }
                System.out.println("first part success");
                ////////////////////////////

                accDebtVal = line.substring(14, 32);

                accDebt = accDebtVal.substring(6, 16);

                BigInteger acctNo = new BigInteger(accDebt);
                accDebt = String.valueOf(acctNo);

                System.out.println("ACCOUNT NUMBER: " + accDebt);
                amount = line.substring(32, 47);

                try {
                    amount = amount.substring(0, 13).concat(".").concat(amount.substring(13, 15));

                } catch (Exception e) {

                }
                cheqNum = line.substring(47, 56);

                BciCceInterfaceParameterRecord verRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.CCE.VERSION.NAMES"));
                List<FieldNameClass> flList = verRec.getFieldName();
                for (FieldNameClass fieldid : flList) {
                    String fieldName = fieldid.getFieldName().getValue();
                    if (fieldName.equals("CHQ.OUT.AC.CHARGE.REQ")) {
                        versionName = fieldid.getFieldValue().getValue();
                    }
                }

                AcChargeRequestRecord acChrgRqst = new AcChargeRequestRecord(this);
                acChrgRqst.setDebitAccount(accDebt);
                ChargeCodeClass chgCode = new ChargeCodeClass();
                chgCode.setChargeAmount(new BigDecimal(amount).toPlainString());
                acChrgRqst.setChargeCode(chgCode, 0);

                acChrgRqst.setExtraDetails(cheqNum, 0);
                acChrgRqst.setRelatedRef(regCnt);
                acChrgRqst.setTxnReference(bnkRef);
                acChrgRqst.setChargeDate(today);
                acChrgRqst.setStatus("PAID");

                TransactionData txnData2 = new TransactionData();
                txnData2.setFunction("INPUTT");
                txnData2.setNumberOfAuthoriser("0");
                txnData2.setSourceId("BCI.CHQ.UPD");
                txnData2.setVersionId(versionName); //
                txnData2.setCompanyId("PE0010001");
                txnData2.setTransactionId("/");
                transactionData.add(txnData2);
                records.add(acChrgRqst.toStructure());

                System.out.println("txnData2: " + txnData2);
                System.out.println("acChrgRqst: " + acChrgRqst);

                // BciCceMappingFieldValuesRecord bciMapRec = new
                // BciCceMappingFieldValuesRecord(this);
                bciRec.setStatus("CLEARED_COMMISSION");
                mapFldType2.setMapFieldType("INDIVIDUAL D");
                mapFldType2.setMapFieldVal(line, 0);
                bciRec.setMapFieldType(mapFldType2, 1);

                bciRec.setDate(today);
                bciRec.setTime(dtf.format(now));
                bciRec.setOeId(oeId);
                bciRec.setInRef(bnkRef);

                try {
                    bciTable.write(id, bciRec);
                } catch (Exception e) {

                }

            }
        }

        // }
    }

}
