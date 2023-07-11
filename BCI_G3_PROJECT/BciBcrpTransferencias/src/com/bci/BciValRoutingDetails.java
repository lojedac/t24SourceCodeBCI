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
 * @author EcuLaptop-12
 *
 */
public class BciValRoutingDetails extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        String acctId = "";
        String cciCode = "";
        String nomAcct = "";
        String noAcct = "";
        DataAccess da = new DataAccess(this);
        DataAccess daAcct = new DataAccess(this);
        String value = "";
        String preFix = "";
        String bankCode = "";
        String ccy = "";
        String ccyTxn = "";
        String cciDestino = "";
        String fieldCode = "";
        String acctDestino = "";
        String ccyOper="";
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);
        value = poRec.getPaymentOrderProduct().getValue();
        preFix = value.substring(4, 5);
        ccyTxn = poRec.getPaymentCurrency().getValue();
        
        if (ccyTxn.equals("USD"))
        {
            ccyOper="03";
        }else
        {
            ccyOper="00";
        }

        cciDestino = poRec.getLocalRefField("L.CCI.DESTINATION").getValue();
        if (cciDestino.isEmpty()) {
            poRec.getLocalRefField("L.CCI.DESTINATION").setError("PP-BENEFICIARY.CCI");
        } else {

            try {
                cciDestino = cciDestino.substring(0, 3);
                EbBciBcrpCwAccountRecord bciRec = new EbBciBcrpCwAccountRecord(
                        daAcct.getRecord("EB.BCI.BCRP.CW.ACCOUNT", cciDestino.concat(ccyOper)));
                acctId = poRec.getCreditNostroAccount().getValue();
                cciCode = poRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
                for (int i = 0; i < bciRec.getCodeEntity().size(); i++) {
                    fieldCode = bciRec.getCodeEntity(i).getCodeEntity().getValue();

                    if (fieldCode.equals(cciDestino)) {
                        String ccyEnt = "";
                        ccyEnt = bciRec.getCodeEntity(i).getDivT24().getValue();
                        if (ccyEnt.equals(ccyTxn)) {
                            nomAcct = bciRec.getCodeEntity(i).getName().getValue();
                            acctDestino = bciRec.getCodeEntity(i).getNumAccount().getValue();
                            poRec.getLocalRefField("L.CTA.NO.CTA.EXT").setValue(acctDestino);
                            break;
                        }
                    }
                }

                AccountRecord acctRec = new AccountRecord(da.getRecord("ACCOUNT", acctId));
                ccy = acctRec.getCurrency().getValue();

                if (ccy.equals(ccyTxn)) {
                    if (cciCode.isEmpty()) {
                        cciCode = "063";
                    }
                    noAcct = acctRec.getOurExtAcctNo().getValue();
                    poRec.getLocalRefField("L.ACT.EXTERNO").setValue(noAcct);
                    poRec.getLocalRefField("L.COD.BANK.ORIGEN").setValue(cciCode.substring(0, 3));
                    poRec.getLocalRefField("L.CTA.MA.BANK.REC").setValue(cciDestino.substring(0, 3));
                    poRec.getLocalRefField("L.DESC.ACT.EXTERNO").setValue(nomAcct);
                    poRec.getLocalRefField("L.CTA.NO.CTA.EXT").setValue(acctDestino);
                } else {
                    throw new Error("La moneda de la cuenta nostro debe coincidir con la operación");
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                // Uncomment and replace with appropriate logger
                // LOGGER.error(exception_var, exception_var);
                System.out.println(e.getMessage());
            }
        } 
        
        if (preFix.equals("C") || preFix.equals("D") || preFix.equals("E") ) {
            bankCode = poRec.getLocalRefField("L.CCI.DESTINATION").getValue();
            if (bankCode.isEmpty()) {
                poRec.getLocalRefField("L.CCI.DESTINATION").setError("PP-BENEFICIARY.CCI");
            } else {
                poRec.getLocalRefField("L.CTA.MA.BANK.REC").setValue(bankCode);
                EbBciBcrpCwAccountRecord bciRec = new EbBciBcrpCwAccountRecord(
                        daAcct.getRecord("EB.BCI.BCRP.CW.ACCOUNT", bankCode.concat(ccyOper)));
                for (int i = 0; i < bciRec.getCodeEntity().size(); i++) {
                    fieldCode = bciRec.getCodeEntity(i).getCodeEntity().getValue();

                    if (fieldCode.equals(cciDestino)) {
                        String ccyEnt = "";
                        ccyEnt = bciRec.getCodeEntity(i).getDivT24().getValue();
                        if (ccyEnt.equals(ccyTxn)) {
                            nomAcct = bciRec.getCodeEntity(i).getName().getValue();
                            acctDestino = bciRec.getCodeEntity(i).getNumAccount().getValue();
                            poRec.getLocalRefField("L.CTA.NO.CTA.EXT").setValue(acctDestino);
                            break;
                        }
                    }
                }
            }
        }
      currentRecord.set(poRec.toStructure());
    }
}