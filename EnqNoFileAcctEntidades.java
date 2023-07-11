package com.bci;

import java.util.ArrayList;
import java.util.List;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebbcibcrpcwaccount.EbBciBcrpCwAccountRecord;

/**
 * TODO: Document me!
 *
 * @author EcuLaptop-12
 *
 */
public class EnqNoFileAcctEntidades extends Enquiry {

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        
        DataAccess daAcct = new DataAccess(this);
        EbBciBcrpCwAccountRecord bciRec = new EbBciBcrpCwAccountRecord(daAcct.getRecord("EB.BCI.BCRP.CW.ACCOUNT", "SYSTEM"));
        String fieldCode ="";
        String fieldName = filterCriteria.get(0).getFieldname();
        String fieldValue = filterCriteria.get(0).getValue();
        String acctIds = "";
        List<String> retId = new ArrayList<>();
        String dbitccyAct="";
        if (fieldName.equals("CODE.ENTITY"))
        {
            if(!fieldValue.equals(""))
                {
                for (int i = 0; i < bciRec.getCodeEntity().size(); i++) {
                    fieldCode = bciRec.getCodeEntity(i).getCodeEntity().getValue();
                    acctIds =bciRec.getCodeEntity(i).getNumAccount().getValue();
                    dbitccyAct=bciRec.getCodeEntity(i).getDivT24().getValue();
                    if (fieldCode.equals(fieldValue))
                    {
                       String ctaCCy=acctIds+"*"+dbitccyAct;
                       retId.add(ctaCCy);
                       //retId.add(acctIds);    
                    }
                } 
            } 
        }
        return retId;
    }
}
