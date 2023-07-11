package com.bci;


import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.eblookup.EbLookupRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author EcuLaptop-12
 *
 */
public class BciValOrderingDetails extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        
        String value = "";
        String preFix="";
        String subFix ="";
        String currency="";
        String acctId = "";
        String cust ="";
        String[] iddocType;
        String docType ="";
        String docNumber="";
        String recordId="CUS.LEGAL.DOC.NAME*";
        DataAccess daAcct = new DataAccess(this);
        DataAccess daCust = new DataAccess(this);
        DataAccess daEblookup = new DataAccess(this);
       
        
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);
        value=poRec.getPaymentOrderProduct().getValue();
        preFix=value.substring(4,5);
                       
        acctId=poRec.getDebitAccount().getValue();
        AccountRecord acctRec= new AccountRecord(daAcct.getRecord("ACCOUNT",acctId));
        currency=acctRec.getCurrency().getValue();
        cust=acctRec.getCustomer().getValue();
               if (!value.equals("LBTRCME") && !value.equals("LBTRCMEC") && !value.equals("LBTRVME") && !value.equals("LBTRVMEC"))
                {
                    try {
                        if (currency.equals("USD"))
                        {
                            subFix=preFix.concat("101");
                            poRec.getLocalRefField("L.CODE.CONCEPT").setValue(subFix);
                            
                        }else{
                            
                            subFix=preFix.concat("170");
                            poRec.getLocalRefField("L.CODE.CONCEPT").setValue(subFix);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        // Uncomment and replace with appropriate logger
                        // LOGGER.error(exception_var, exception_var);
                        System.out.println(e.getMessage());
                    }
                }
        
        try {
            if (!cust.isEmpty()){
                CustomerRecord custRec= new CustomerRecord(daCust.getRecord("CUSTOMER",cust));
            
                docNumber=custRec.getLegalId().get(0).getLegalId().getValue();
                if (!docNumber.isEmpty()){
                    iddocType=((custRec.getLegalIdDocName().get(0).getValue()).split("-", 2));  
                    EbLookupRecord docTypeRec=new EbLookupRecord(daEblookup.getRecord("EB.LOOKUP", recordId.concat(iddocType[1].toString())));
                    docType=docTypeRec.getDescription().get(0).getValue();
                    poRec.getLocalRefField("L.TIPODOC.ORDENANTE").setValue(docType);
                    poRec.getLocalRefField("L.NUM.DOC.ORDENANTE").setValue(docNumber);        
                }
            }
            poRec.setDebitCcy(currency);
            poRec.setPaymentCurrency(currency);
            currentRecord.set(poRec.toStructure());
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(exception_var, exception_var);
            System.out.println(e.getMessage());
        }  
    }
}
  