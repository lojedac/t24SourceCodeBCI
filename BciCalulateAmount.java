package com.bci;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;

/**
 * TODO: Document me!
 *
 * @author andreavaca
 *
 */
public class BciCalulateAmount extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        Double montoOrigen;
        Double tipoCambio;
        Double valorCalculado;
        
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);
        montoOrigen= Double.parseDouble(poRec.getLocalRefField("L.MONTO.ORIGEN").getValue());
        tipoCambio=Double.parseDouble(poRec.getLocalRefField("L.TIPO.CAMBIO").getValue());
        valorCalculado=tipoCambio*montoOrigen;
        BigDecimal bdnum = new BigDecimal(valorCalculado).setScale(2, RoundingMode.HALF_UP);
        poRec.setPaymentAmount(bdnum.toString());
        
        currentRecord.set(poRec.toStructure());
    }
    
    
}
