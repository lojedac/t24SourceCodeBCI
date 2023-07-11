    package com.bci;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.beneficiary.BeneficiaryRecord;
import com.temenos.t24.api.records.eblookup.EbLookupRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author EcuLaptop-12
 *
 */
public class BciValBeneficiaryDetails extends RecordLifecycle {

    @Override
    public TValidationResponse validateField(String application, String recordId, String fieldData, TStructure record) {
        
        String ben ="";
        String cciBen="";
        String docType ="";
        String docNumber="";
        String address="";
        String value ="";
        String preFix="";
        String bankCode="";
        
        DataAccess daBen = new DataAccess(this);
        PaymentOrderRecord poRec = new PaymentOrderRecord(record);
        
        
        value=poRec.getPaymentOrderProduct().getValue();
        preFix=value.substring(4,5);
        ben=poRec.getBeneficiaryId().getValue();
        BeneficiaryRecord benRec= new BeneficiaryRecord(daBen.getRecord("BENEFICIARY",ben));
        
        if (!preFix.equals("C") && !preFix.equals("F") && !preFix.equals("D") && !preFix.equals("E"))
        {
            try {
                
                if (!ben.isEmpty())
                {
                    
                    cciBen=benRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    docType=benRec.getLocalRefField("L.TIPODOC.BENEFICIARIO").getValue();
                    docNumber=benRec.getLocalRefField("L.NUM.DOC.BENEFICIARIO").getValue();
                    address=benRec.getLocalRefField("L.DIRECCION.BENEFICIARIO").getValue();
                    if (cciBen.isEmpty() || docType.isEmpty() || docNumber.isEmpty() || address.isEmpty())
                    {
                        poRec.getLocalRefField("L.CCI.DESTINATION").setError("PP-BENEFICIARY.CCI");
                    }
                }else
                {
                    poRec.getLocalRefField("L.CCI.DESTINATION").setValue(cciBen);
                    poRec.getLocalRefField("L.TIPODOC.BENEFICIARIO").setValue(docType);
                    poRec.getLocalRefField("L.NUM.DOC.BENEFICIARIO").setValue(docNumber);
                    poRec.getLocalRefField("L.DIRECCION.BENEFICIARIO").setValue(address);
                }
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                // Uncomment and replace with appropriate logger
                // LOGGER.error(exception_var, exception_var);
                System.out.println(e.getMessage());
            }
        }else{
            
            bankCode=benRec.getLocalRefField("ILBNKB.BANK.CODE").getValue();
            poRec.getLocalRefField("L.CCI.DESTINATION").setValue(bankCode);
            if (bankCode.isEmpty())
            {
                poRec.getLocalRefField("L.CCI.DESTINATION").setError("PP-BENEFICIARY.CCI");
            }
        }
       return poRec.getValidationResponse();  
    }

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        String ben ="";
        String cciBen="";
        String docType ="";
        String docNumber="";
        String address="";
        String nameBen="";
        String value ="";
        String preFix="";
        String bankCode="";
        DataAccess daBen = new DataAccess(this);
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);
        
        value=poRec.getPaymentOrderProduct().getValue();
        preFix=value.substring(4,5);
        ben=poRec.getBeneficiaryId().getValue();
        BeneficiaryRecord benRec= new BeneficiaryRecord(daBen.getRecord("BENEFICIARY",ben));
        
        if (!preFix.equals("C") && !preFix.equals("F") && !preFix.equals("D") && !preFix.equals("E"))
        {
            try {
                
                if (!ben.isEmpty())
                {
                    
                    cciBen=benRec.getLocalRefField("L.CCI.DESTINATION").getValue();
                    docType=benRec.getLocalRefField("L.TIPODOC.BENEFICIARIO").getValue();
                    docNumber=benRec.getLocalRefField("L.NUM.DOC.BENEFICIARIO").getValue();
                    address=benRec.getLocalRefField("L.DIRECCION.BENEFICIARIO").getValue();
                    nameBen=benRec.getNickname(0).getValue();
                    
                    if (!cciBen.isEmpty())
                    {
                        poRec.getLocalRefField("L.CCI.DESTINATION").setValue(cciBen);
                    }else{
                        poRec.getLocalRefField("L.CTA.MA.BANK.REC").getValue();
                        poRec.getLocalRefField("L.CTA.NO.CTA.EXT").getValue();
                        //cciBencode=codeBank.concat(ccyBank);
                    }
                    poRec.getLocalRefField("L.TIPODOC.BENEFICIARIO").setValue(docType);
                    poRec.getLocalRefField("L.NUM.DOC.BENEFICIARIO").setValue(docNumber);
                    poRec.getLocalRefField("L.DIRECCION.BENEFICIARIO").setValue(address);
                    poRec.setBeneficiaryName(nameBen);
                    currentRecord.set(poRec.toStructure());
                }
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                // Uncomment and replace with appropriate logger
                // LOGGER.error(exception_var, exception_var);
                System.out.println(e.getMessage());
            }
        }else{
            
            bankCode=benRec.getLocalRefField("ILBNKB.BANK.CODE").getValue();
            poRec.getLocalRefField("L.CCI.DESTINATION").setValue(bankCode);
        }
        currentRecord.set(poRec.toStructure());
    }
 }
