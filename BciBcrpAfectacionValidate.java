package com.bci;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.complex.pp.componentapihook.Account;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;

public class BciBcrpAfectacionValidate extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        DataAccess da = new DataAccess(this);
        PpOrderEntryRecord ppoRec = new PpOrderEntryRecord(currentRecord);
        BciCceInterfaceParameterRecord bciParamRec = new BciCceInterfaceParameterRecord(this);
        String cuentaDestino = ppoRec.getDebitaccountnumber().getValue().trim();
        String incomingMsgType = ppoRec.getIncomingmessagetype().getValue();
        String accNumber = "";

        List<FieldNameClass> listFieldName = new ArrayList<FieldNameClass>();
        try {
            bciParamRec = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.LBTR.ACCT.NUMBER"));
            listFieldName = bciParamRec.getFieldName();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        boolean isAccount = false;
        for (FieldNameClass fieldNameClass : listFieldName) {
            String accExterna = fieldNameClass.getFieldName().getValue().trim();
            if (cuentaDestino.equals(accExterna)) {
                String accInterna = fieldNameClass.getFieldValue().getValue().trim();
                ppoRec.setDebitaccountnumber(accInterna);
                isAccount = true;
                break;
            }
        }
        if (!isAccount) {
            ppoRec.getDebitaccountnumber().setError("Cuenta no configurada en los Parametros!");
        }

        if (!incomingMsgType.equals("LBTRB") && !incomingMsgType.equals("LBTRA")) {
            try {
                bciParamRec = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", "BCI.LBTR.INTERNAL.ACCT"));
                listFieldName = bciParamRec.getFieldName();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            String codMoneda = ppoRec.getTransactioncurrency().getValue().toUpperCase();
            for (FieldNameClass fieldNameClass : listFieldName) {
                String codMonedaParam = fieldNameClass.getFieldName().getValue().toUpperCase();
                if (codMonedaParam.equals(codMoneda)) {
                    accNumber = fieldNameClass.getFieldValue().getValue();
                    ppoRec.setOrderingaccount(accNumber);
                    ppoRec.setCreditaccountnumber(accNumber);
                }
            }
        } else if (incomingMsgType.equals("LBTRB") || incomingMsgType.equals("LBTRA")) {
            String cciBeneficiary = ppoRec.getLocalRefField("L.CCI.DESTINATION").getValue();
            if (cciBeneficiary.length() != 20) {
                ppoRec.getLocalRefField("L.CCI.DESTINATION").setError("Invalid CCI.DESTINATION");
            }
            accNumber = cciBeneficiary.substring(8, 18);
            try {
                Account accRec = new Account(da.getRecord("ACCOUNT", accNumber));
                ppoRec.setOrderingaccount(accNumber);
                ppoRec.setCreditaccountnumber(accNumber);
            } catch (Exception e) {

                ppoRec.getLocalRefField("L.CCI.DESTINATION").setError("PI-INVALID.BEN.ID");
            }
        }
        currentRecord.set(ppoRec.toStructure());
    }

}
