package com.bci;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;

/**
 * @author haroldtabarez
 *
 */
public class BciBcrpChangeNostroAcct extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        String numDestinAcct = "";
        String idInterface = "BCI.LBTR.ACCT.NUMBER";
        String fieldName = "";
        String cuentaInterna = "";
        String poProd = "";
        String idInterfaceInternalAcct = "BCI.LBTR.INTERNAL.ACCT";
        String currencyPO = "";
        List<FieldNameClass> listFieldName = new ArrayList<>();
        DataAccess da = new DataAccess(this);
        PaymentOrderRecord poReg = new PaymentOrderRecord(currentRecord);
        currencyPO = poReg.getPaymentCurrency().getValue();
        poProd = poReg.getPaymentOrderProduct().getValue();
        try {
            BciCceInterfaceParameterRecord recordInterface;
            if (!poProd.equals("LBTRA") && !poProd.equals("LBTRB")) {
                recordInterface = new BciCceInterfaceParameterRecord(
                        da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", idInterfaceInternalAcct));
                listFieldName = recordInterface.getFieldName();
                for (FieldNameClass fieldNameClass : listFieldName) {
                    fieldName = fieldNameClass.getFieldName().getValue();
                    if (fieldName.equals(currencyPO)) {
                        cuentaInterna = fieldNameClass.getFieldValue().getValue();
                        poReg.setDebitAccount(cuentaInterna);
                        poReg.setOrderingCustomer("999999");
                        break;
                    }
                }
            }
            numDestinAcct = poReg.getLocalRefField("L.CTA.NO.CTA.EXT").getValue();
            recordInterface = new BciCceInterfaceParameterRecord(
                    da.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", idInterface));
            listFieldName = recordInterface.getFieldName();
            for (FieldNameClass fieldNameClass1 : listFieldName) {
                fieldName = fieldNameClass1.getFieldName().getValue();
                if (fieldName.equals(numDestinAcct)) {
                    cuentaInterna = fieldNameClass1.getFieldValue().getValue();
                    poReg.setCreditNostroAccount(cuentaInterna);
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        currentRecord.set(poReg.toStructure());
    }
}
