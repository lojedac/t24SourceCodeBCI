package com.bci;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.CommonData;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.Flags;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.PaymentApplicationUpdate;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.PaymentContext;
import com.temenos.t24.api.complex.pp.paymentlifecyclehook.StatusAction;
import com.temenos.t24.api.hook.payments.PaymentLifecycle;
import com.temenos.t24.api.records.ebqueriesanswers.EbQueriesAnswersRecord;
import com.temenos.t24.api.records.poragreementandadvice.PorAgreementAndAdviceRecord;
import com.temenos.t24.api.records.poraudittrail.PorAuditTrailRecord;
import com.temenos.t24.api.records.porpostingandconfirmation.PorPostingAndConfirmationRecord;
import com.temenos.t24.api.records.porsupplementaryinfo.PorSupplementaryInfoRecord;
import com.temenos.t24.api.records.portransaction.PorTransactionRecord;
import com.temenos.t24.api.records.ppcompanyproperties.PpCompanyPropertiesRecord;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;

/**
 *
 * @author anagha.s
 *         ----------------------------------------------------------------------------------------------------------------
 *         Description : This routine is used to update the template when 999
 *         status is reached during inward process.
 * 
 *         Developed By : Anagha Shastry
 *
 *         Development Reference :
 *         IDD-G2-013_BCI_Interface_Interbank_Transfers_Outward_Inward
 *
 *         Attached To : PP.STATUS.ACTION>999*.
 *
 *         Attached As : Api routine attached to PP.STATUS.ACTION.
 *         -----------------------------------------------------------------------------------------------------------------
 *         Input Parameter: ---------------* Argument#1 : N/A Argument#2 : N/A
 *         Argument#3 : N/A -----------------* Output Parameter:
 *         ----------------* Argument#4 : N/A Argument#5 : N/A Argument#6 : N/A
 *         -----------------------------------------------------------------------------------------------------------------
 *         M O D I F I C A T I O N S ***************************
 *         -----------------------------------------------------------------------------------------------------------------
 *         Defect Reference Modified By Date of Change Change Details
 *         (RTC/TUT/PACS) (YYYY-MM-DD)
 *         -----------------------------------------------------------------------------------------------------------------
 *         XXXX <<name of modifier>> <<modification details goes here>>
 *
 *         -----------------------------------------------------------------------------------------------------------------
 */
public class BciCCEInUpdSts extends PaymentLifecycle {

    @Override
    public void updateRequestToExternalCoreSystem(StatusAction arg0, PorTransactionRecord arg1, PaymentContext arg2,
            PorSupplementaryInfoRecord arg3, PorAgreementAndAdviceRecord arg4, PorPostingAndConfirmationRecord arg5,
            PorAuditTrailRecord arg6, PpCompanyPropertiesRecord arg7, CommonData arg8, EbQueriesAnswersRecord arg9,
            Flags arg10, PaymentApplicationUpdate arg11) {
        // TODO Auto-generated method stub
        String transtype = arg1.getIncomingmessagetype().getValue();
        String source = arg1.getSourceproduct().getValue();

        if (transtype.equals("CCETRANS") && source.equals("CCE")) {
            try {
                DataAccess da = new DataAccess(this);
                String oeId = arg3.getOrderEntryId(0).getValue();
                PpOrderEntryRecord pporderRec = new PpOrderEntryRecord(da.getRecord("PP.ORDER.ENTRY", oeId));
                String bnkRef = pporderRec.getTransactionreferencenumber().getValue();
                // String id =
                // pporderRec.getSendersreferencenumber().getValue();
                String id = arg1.getSendersreferenceincoming().getValue();
                String paymentOrderId = arg1.getFilereferenceincoming().getValue();
                String regCnt = pporderRec.getLocalRefField("L.REGIST.CONTR").getValue();
                BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(
                        da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", id));
                BciCceMappingFieldValuesRecord mapRecwrite = new BciCceMappingFieldValuesRecord(this);
                String date = mapRec.getDate().getValue();
                String transactionNumber = mapRec.getOeId().getValue();
                List<MapFieldTypeClass> fieldListmap = mapRec.getMapFieldType();
                MapFieldTypeClass mapclass = new MapFieldTypeClass();
                MapFieldTypeClass mapclass1 = new MapFieldTypeClass();
                for (MapFieldTypeClass mapId : fieldListmap) {
                    String type = mapId.getMapFieldType().getValue();

                    if (type.equals("INDIVIDUAL")) {
                        mapclass.setMapFieldType(type);
                        String individualRegistrationfisrt = mapId.getMapFieldVal().get(0).getValue();
                        String individualRegistrationsec = mapId.getMapFieldVal().get(1).getValue();
                        // String tranfir = individualRegistrationsec.substring(0, 53);
                        //individualRegistrationsec = tranfir.concat(StringUtils.rightPad(bnkRef, 17, "0")).concat(id)
                        //        .concat(individualRegistrationsec.substring(77));
                        mapclass.setMapFieldVal(individualRegistrationfisrt, 0);
                        mapclass.setMapFieldVal(individualRegistrationsec, 1);
                        mapRecwrite.setMapFieldType(mapclass, 0);
                    }
                    if (type.equals("ADDITIONAL")) {
                        mapclass1.setMapFieldType(type);
                        String additionalRegistrationfirst = mapId.getMapFieldVal().get(0).getValue();
                        String additionalRegistrationSec = mapId.getMapFieldVal().get(1).getValue();
                        mapclass1.setMapFieldVal(additionalRegistrationfirst, 0);
                        mapclass1.setMapFieldVal(additionalRegistrationSec, 1);
                        mapRecwrite.setMapFieldType(mapclass1, 1);
                    }
                }

                mapRecwrite.setPoId(paymentOrderId);
                mapRecwrite.setDate(date);
                mapRecwrite.setStatus("TO_BE_CONFIRMED_IN");
                mapRecwrite.setOeId(oeId);
                mapRecwrite.setTxnType(mapRec.getTxnType().getValue());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                mapRecwrite.setTime(dtf.format(now));
                BciCceMappingFieldValuesTable mapTbl = new BciCceMappingFieldValuesTable(this);
                try {
                    mapTbl.write(id, mapRecwrite);
                } catch (T24IOException e) {

                }
            } catch (Exception e) {

            }
        }
    }

}
