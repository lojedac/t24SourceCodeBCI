package com.bci;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;

/**
 * TODO: 
 *
 * @author Diego Maigualca
 * @date 08-02-2023
 *
 */
public class BciChangeCurrencyApiPo extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        PaymentOrderRecord poRecord = new PaymentOrderRecord(currentRecord);
        // Inicializar las variables
        String debiAccount = "";
        String currencyAcc = "";
        String idBciCceInt = "CCE.ACCOUNT.NUMBER";
        String currencyPo = "";
        String fieldNameStrPen = "";
        String fieldValueStr = "";
        List<FieldNameClass> fieldNameLst = new ArrayList<FieldNameClass>();

        
        
        DataAccess da = new DataAccess(this);
        debiAccount = poRecord.getDebitAccount().getValue();
        AccountRecord accRec = new AccountRecord(da.getRecord("ACCOUNT", debiAccount));
        currencyAcc = accRec.getCurrency().getValue();
        poRecord.setPaymentCurrency(currencyAcc);
        
        
        currencyPo = currencyAcc;
        BciCceInterfaceParameterRecord cceIntRec = new BciCceInterfaceParameterRecord(da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", idBciCceInt));
        fieldNameLst = cceIntRec.getFieldName();
        
        for (FieldNameClass fieldNameClass : fieldNameLst) {
            fieldNameStrPen = fieldNameClass.getFieldName().getValue();
            if (currencyPo.equals("PEN")) {
                if (fieldNameStrPen.equals("CREDIT.ACCT.NUMBER")) {
                    fieldValueStr = fieldNameClass.getFieldValue().getValue();
                }
            } else if (currencyPo.equals("USD")) {
                if (fieldNameStrPen.equals("CREDIT.ACCT.NUMBER.USD")) {
                    fieldValueStr = fieldNameClass.getFieldValue().getValue();
                }
                
            }
        }
        
        poRecord.setCreditNostroAccount(fieldValueStr);

        currentRecord.set(poRecord.toStructure());
        
    }
    

}
