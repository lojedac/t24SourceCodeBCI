package com.bci;

import java.util.List;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;

/**
 * TODO: Document me!
 *
 * @author Diego Maigualca
 *
 */
public class BciWebServiceInquiryEnq extends Enquiry {

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub
        FilterCriteria filterCriteria2 = new FilterCriteria();        
        filterCriteria2.setFieldname("VIRTUAL.TABLE");
        filterCriteria2.setOperand("LK");
        filterCriteria2.setValue("EB.BCI.BCRP.CONSULTAS");
        filterCriteria.add(filterCriteria2);
        return filterCriteria;
    }
    
}
