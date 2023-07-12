package com.bci;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebbcihdrawerdoctype.EbBciHDrawerDocTypeRecord;

/**
 * TODO: Document me!
 *
 * @author Usuario
 *
 */
public class BciCCELegalIdValidate extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        DataAccess da = new DataAccess(this);
        String accountCredit = "";
        String customerId = "";

        boolean flgequal = false;
        List<LegalIdClass> lstCusLegIds = new ArrayList<LegalIdClass>();
        String sameOwner = "";
        String ppoLegalId = "";
        String ppoLegalDoc = "";
        String cusLeglId = "";
        String cusLeglDoc = "";
        BigDecimal ppoLegalDocBD = new BigDecimal(0);

        PpOrderEntryRecord ppOrderEntryRecord = new PpOrderEntryRecord(currentRecord);
        accountCredit = ppOrderEntryRecord.getCreditaccountnumber().getValue();
        sameOwner = ppOrderEntryRecord.getLocalRefField("L.SAME.OWNER").getValue();
        if (sameOwner.equals("NO") || sameOwner.isEmpty()) {
            return ppOrderEntryRecord.getValidationResponse();
        }
        // account = accountCredit.substring(8,18);
        AccountRecord aRecord = new AccountRecord(this);
        CustomerRecord cuRecord = new CustomerRecord(this);
        EbBciHDrawerDocTypeRecord docTypeRecord = new EbBciHDrawerDocTypeRecord(this);
        try {
            aRecord = new AccountRecord(da.getRecord("ACCOUNT", accountCredit));
            customerId = aRecord.getCustomer().getValue();
            cuRecord = new CustomerRecord(da.getRecord("CUSTOMER", customerId));
            ppoLegalId = ppOrderEntryRecord.getLocalRefField("L.DRAWER.DOC.TYPE").getValue();
            docTypeRecord = new EbBciHDrawerDocTypeRecord(da.getRecord("EB.BCI.H.DRAWER.DOC.TYPE", ppoLegalId));
            ppoLegalId = docTypeRecord.getDocTypeDescription().getValue();
            ppoLegalDoc = ppOrderEntryRecord.getLocalRefField("L.DRAWER.DOC.NUMBER").getValue();
            ppoLegalDocBD = new BigDecimal(ppoLegalDoc);
            ppoLegalDoc = ppoLegalDocBD.toString();
            lstCusLegIds = cuRecord.getLegalId();

            for (LegalIdClass cusLegIdClass : lstCusLegIds) {
                cusLeglId = cusLegIdClass.getLegalId().getValue();
                cusLeglDoc = cusLegIdClass.getLegalDocName().getValue();
                if (cusLeglId.equals(ppoLegalDoc)) {
                    if (cusLeglDoc.equals(ppoLegalId)) {
                        flgequal = true;
                    }

                    break;
                }
            }
            if (!flgequal) {

                ppOrderEntryRecord.setNickname("NIKCNAME");
                ppOrderEntryRecord.setReturndescription("CPD10003");
                ppOrderEntryRecord.setOverride("AA-EXP.DAYS.SAME.FOR.CHILD", 0);

                ppOrderEntryRecord.setFunctionalerror("CPD10003", 0);

                ppOrderEntryRecord.getLocalRefField("L.SAME.OWNER").setError("AC-ACCOUNT.CLOSED.STATUS");

                currentRecord.set(ppOrderEntryRecord.toStructure());

            }

        } catch (Exception e) {

        }
        return ppOrderEntryRecord.getValidationResponse();
    }

}