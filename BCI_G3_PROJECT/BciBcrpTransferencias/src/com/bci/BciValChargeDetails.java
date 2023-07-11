package com.bci;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.eblookup.EbLookupRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author EcuLaptop-12
 *
 */
public class BciValChargeDetails extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        String docTypeBen="";
        String docNumberBen="";
        String docTypeOrd="";
        String docNumberOrd="";
        String docTypeDesc="";
        String recId="CUS.LEGAL.DOC.NAME*";
        DataAccess daEblookup = new DataAccess(this);
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);
        docTypeBen=poRec.getLocalRefField("L.TIPODOC.BENEFICIARIO").getValue();
        EbLookupRecord docTypeRec=new EbLookupRecord(daEblookup.getRecord("EB.LOOKUP", recId.concat(docTypeBen.toString())));
        docTypeDesc=docTypeRec.getDescription().get(0).getValue();
        
        docNumberBen=poRec.getLocalRefField("L.NUM.DOC.BENEFICIARIO").getValue();
        docTypeOrd=poRec.getLocalRefField("L.TIPODOC.ORDENANTE").getValue();
        docNumberOrd=poRec.getLocalRefField("L.NUM.DOC.ORDENANTE").getValue();
        try {
            
            if (docTypeDesc.equals(docTypeOrd) )
            {
                if (docNumberBen.equals(docNumberOrd))
                {
                    poRec.getLocalRefField("L.SAME.OWNER").setValue("YES");
                }
           }else{
               poRec.getLocalRefField("L.SAME.OWNER").setValue("NO");
           }
            
            currentRecord.set(poRec.toStructure());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(exception_var, exception_var);
            System.out.println(e.getMessage());
        }
      }

}
