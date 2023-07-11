package com.bci;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebbcibcrpcwaccount.EbBciBcrpCwAccountRecord;

/**
 * TODO: Document me!
 *
 * @author andreavaca
 *
 */
public class BciBcrpCvmeRoutingDetails extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        String cciCode = "";
        String nomAcct = "";
        DataAccess daAcct = new DataAccess(this);
        String acctDestino = "";
        String fieldCode = "";
        String ccyOperacion="";
        
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);

        cciCode = poRec.getLocalRefField("L.CCI.DESTINATION").getValue();
        ccyOperacion=poRec.getPaymentCurrency().getValue();
        
        if (ccyOperacion.equals("USD"))
        {
            ccyOperacion="03";
        }else
        {
            ccyOperacion="00";
        }
        
        try {
            
            EbBciBcrpCwAccountRecord bciRec = new EbBciBcrpCwAccountRecord(
                    daAcct.getRecord("EB.BCI.BCRP.CW.ACCOUNT", cciCode.concat(ccyOperacion)));

            for (int i = 0; i < bciRec.getCodeEntity().size(); i++) {
                fieldCode = bciRec.getCodeEntity(i).getCodeEntity().getValue();

                if (fieldCode.equals(cciCode)) {
                        nomAcct = bciRec.getCodeEntity(i).getName().getValue();
                        acctDestino = bciRec.getCodeEntity(i).getNumAccount().getValue();
                        poRec.getLocalRefField("L.DESC.ACT.EXTERNO").setValue(nomAcct);
                        poRec.getLocalRefField("L.CTA.NO.CTA.EXT").setValue(acctDestino);
                        break;
                  
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(exception_var, exception_var);
        }

        currentRecord.set(poRec.toStructure());
    }
}
