package com.bci;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.beneficiary.BeneficiaryRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author andreavaca
 *
 */
public class BciBcrpCvmeBeneficiaryDetails extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        String bankCode="";
        String ben ="";
        
        DataAccess daBen = new DataAccess(this);
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);
        
        ben=poRec.getBeneficiaryId().getValue();
        BeneficiaryRecord benRec= new BeneficiaryRecord(daBen.getRecord("BENEFICIARY",ben));
            bankCode=benRec.getLocalRefField("ILBNKB.BANK.CODE").getValue();
            poRec.getLocalRefField("L.CCI.DESTINATION").setValue(bankCode);
            poRec.getLocalRefField("L.CTA.MA.BANK.REC").setValue(bankCode);
        currentRecord.set(poRec.toStructure());
    
    }
}
