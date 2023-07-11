package com.bci;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;

/**
 * Va en el jar de Bcil3g3Afectacion.jar
 * 
 * @author David Barahona
 *
 */
public class BciBcrpStatusAbonoVal extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);
        String statusLBTR = poRec.getLocalRefField("L.ESTADO").getValue();
        String estadoAbonado = poRec.getLocalRefField("L.ESTADO.ABONO").getValue();
        if (!estadoAbonado.isEmpty() && !statusLBTR.equals("4")) {
            poRec.getLocalRefField("L.ESTADO.ABONO").setError("EB-BCRP.ABONAD.VAL");
        }
        return poRec.getValidationResponse();
    }

}
