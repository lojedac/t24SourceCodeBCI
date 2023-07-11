package com.bci;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.beneficiary.BeneficiaryRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;

/**
 * TODO: Document me!
 *
 * @author Diego Gallegos
 *
 */
public class BciBcrpCvmeSetIDCciBeneficiary extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        String cciBeneficiary = "";
        String cciBeneficiaryPO = "";
        DataAccess daObj = new DataAccess(this);
        PaymentOrderRecord paymentOrderRecordObj = new PaymentOrderRecord(currentRecord);

        try {
            cciBeneficiaryPO = paymentOrderRecordObj.getLocalRefField("L.CCI.DESTINATION").getValue();

            Session sessObj = new Session(this);

            String mnemonic = sessObj.getCompanyRecord().getMnemonic().getValue();
            List<String> beneficiaryList = (List<String>) daObj.selectRecords(mnemonic, "BENEFICIARY", "",
                    " WITH CCI.DESTINATION EQ '" + cciBeneficiaryPO + "'");

            String idBen = beneficiaryList.get(0);
            BeneficiaryRecord beneficiaryRecordObj = new BeneficiaryRecord(daObj.getRecord("BENEFICIARY", idBen));
            cciBeneficiary = beneficiaryRecordObj.getLocalRefField("L.CCI.DESTINATION").getValue();
            if (cciBeneficiary.equals(cciBeneficiaryPO)) {
                paymentOrderRecordObj.setBeneficiaryId(idBen);

            }

        } catch (Exception e) {
            System.out.println(e);
        }
        currentRecord.set(paymentOrderRecordObj.toStructure());
    }

}
