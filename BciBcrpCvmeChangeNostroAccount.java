package com.bci;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;

/**
 *
 * @author haroldtabarez
 *
 */
public class BciBcrpCvmeChangeNostroAccount extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        DataAccess da = new DataAccess(this);
        String idInterface = "BCI.LBTR.ACCT.NUMBER";
        String idInterfaceCurr = "BCI.LBTR.INTERNAL.ACCT";
        String fieldName = "";
        String numCuentaDestino = "";
        String cuentaInterna = "";
        String currencyUSD = "USD";
        List<FieldNameClass> listFieldName = new ArrayList<>();
        PpOrderEntryRecord ppReg = new PpOrderEntryRecord(currentRecord);
        try {
            BciCceInterfaceParameterRecord recordInterface;
            numCuentaDestino = ppReg.getLocalRefField("L.CTA.NO.CTA.EXT").getValue();
            recordInterface = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", idInterface));
            listFieldName = recordInterface.getFieldName();
            for (FieldNameClass fieldNameClass : listFieldName) {
                fieldName = fieldNameClass.getFieldName().getValue();
                if (fieldName.equals(numCuentaDestino)) {
                    cuentaInterna = fieldNameClass.getFieldValue().getValue();
                    ppReg.setCreditaccountnumber(cuentaInterna);
                }
            }
            recordInterface = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", idInterfaceCurr));
            listFieldName = recordInterface.getFieldName();
            for (FieldNameClass fieldNameClass : listFieldName) {
                fieldName = fieldNameClass.getFieldName().getValue();
                if (fieldName.equals(currencyUSD)) {
                    cuentaInterna = fieldNameClass.getFieldValue().getValue();
                    ppReg.setDebitaccountnumber(cuentaInterna);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        currentRecord.set(ppReg.toStructure());
    }

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        String montoOrigen = "";
        PpOrderEntryRecord ppReg = new PpOrderEntryRecord(currentRecord);
        BciCceInterfaceParameterRecord recordInterface;
        montoOrigen = ppReg.getLocalRefField("L.MONTO.ORIGEN").getValue();
        try {
            if (!validarNumeroDecimal(montoOrigen)) {
                ppReg.getLocalRefField("L.MONTO.ORIGEN").setError("EB-INP.NOT.NUMERIC");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ppReg.getValidationResponse();
    }

    public static boolean validarNumeroDecimal(String numero) {

        String regex = "^\\d+(\\.\\d+)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(numero);
        if (matcher.matches()) {
            double valor = Double.parseDouble(numero);
            if (valor > 0) {
                return true;
            }
        }
        return false;
    }
}