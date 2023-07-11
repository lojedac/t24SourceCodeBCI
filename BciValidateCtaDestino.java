package com.bci;

import java.util.ArrayList;

import java.util.List;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.BciCceInterfaceParameterRecord;
import com.temenos.t24.api.tables.bcicceinterfaceparameter.FieldNameClass;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebbcibcrpcwaccount.EbBciBcrpCwAccountRecord;

/**
 * TODO: Document me!
 *
 * @author EcuLaptop-12
 *
 */
public class BciValidateCtaDestino extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
       
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);
        DataAccess daAcct = new DataAccess(this);
        
        String ctaDestino = "";
        String ccyOperacio="";
        String bankCodeDest="";
        String bankCodeOrig="";
        String fieldCode="";
        String dbitccyAct="";
        String cciDestino="";
        String codeOrigen="";
        String ctaExterna="";
        String cuentaNostro="";
        String fieldName = "";
        String idInterface = "BCI.LBTR.ACCT.NUMBER";
        String ccyTxn="";
        List<FieldNameClass> listFieldName = new ArrayList<>();
        
        cciDestino = poRec.getLocalRefField("L.CCI.DESTINATION").getValue();
        codeOrigen=poRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
        bankCodeDest = cciDestino.substring(0, 3);
        
        if (bankCodeOrig.isEmpty())
        {
            bankCodeOrig="063";
        }else
        {
            bankCodeOrig = codeOrigen.substring(0, 3);
        }
        poRec.getLocalRefField("L.CTA.MA.BANK.REC").setValue(bankCodeDest);
        poRec.getLocalRefField("L.COD.BANK.ORIGEN").setValue(bankCodeOrig);
        ccyOperacio=poRec.getPaymentCurrency().getValue();
         if (ccyOperacio.equals("USD"))
         {
             ccyTxn="03";
         }else
         {
             ccyTxn="00";
         }
        
        try {
            EbBciBcrpCwAccountRecord bciRecDest = new EbBciBcrpCwAccountRecord(daAcct.getRecord("EB.BCI.BCRP.CW.ACCOUNT", bankCodeDest.concat(ccyTxn)));
            if (!bankCodeDest.isEmpty())
            {
                for (int i = 0; i < bciRecDest.getCodeEntity().size(); i++) {
                    fieldCode = bciRecDest.getCodeEntity(i).getCodeEntity().getValue();
                        if (fieldCode.equals(bankCodeDest))
                        {
                            dbitccyAct=bciRecDest.getCodeEntity(i).getDivT24().getValue();
                            if (dbitccyAct.equals(ccyOperacio))
                            {
                                ctaDestino=bciRecDest.getCodeEntity(i).getNumAccount().getValue();
                                poRec.getLocalRefField("L.CTA.NO.CTA.EXT").setValue(ctaDestino);
                                break;
                            }
                        }
                 }
             }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            EbBciBcrpCwAccountRecord bciRecOrig = new EbBciBcrpCwAccountRecord(daAcct.getRecord("EB.BCI.BCRP.CW.ACCOUNT", bankCodeOrig.concat(ccyTxn)));
            if (!bankCodeOrig.isEmpty())
            {
                for (int i = 0; i < bciRecOrig.getCodeEntity().size(); i++) {
                    fieldCode = bciRecOrig.getCodeEntity(i).getCodeEntity().getValue();
                        if (fieldCode.equals(bankCodeOrig))
                        {
                            dbitccyAct=bciRecOrig.getCodeEntity(i).getDivT24().getValue();
                            if (dbitccyAct.equals(ccyOperacio))
                            {
                                ctaExterna=bciRecOrig.getCodeEntity(i).getNumAccount().getValue();
                                poRec.getLocalRefField("L.ACT.EXTERNO").setValue(ctaExterna);
                                break;
                            }
                          }
                    }
        }
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
            try{
            
                BciCceInterfaceParameterRecord recordInterface;
                recordInterface = new BciCceInterfaceParameterRecord(daAcct.getRecord("EB.BCI.CCE.INTERFACE.PARAMETER", idInterface));
                listFieldName = recordInterface.getFieldName();
                for (FieldNameClass fieldNameClass : listFieldName) {
                  fieldName = fieldNameClass.getFieldName().getValue();
                  if (fieldName.equals(ctaExterna)) {
                      cuentaNostro = fieldNameClass.getFieldValue().getValue();
                      poRec.setCreditNostroAccount(cuentaNostro);
                      break;
                   }
                 }
             
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
     
            currentRecord.set(poRec.toStructure());
    }

    @Override
    public TValidationResponse validateField(String application, String recordId, String fieldData, TStructure record) {
        
        String destinationCta="";
        
        PaymentOrderRecord poRec = new PaymentOrderRecord(record);
        
        destinationCta = poRec.getLocalRefField("L.CTA.NO.CTA.EXT").getValue();
        if (destinationCta.isEmpty())
        {
            poRec.getLocalRefField("L.CTA.NO.CTA.EXT").setError("PP-DEST.CTA");
        }
        
        return poRec.getValidationResponse();
    }
    
}
