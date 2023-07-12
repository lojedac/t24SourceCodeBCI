package com.bci;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.messagehook.MessageContext;
import com.temenos.t24.api.hook.system.MessageLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.ofsrequestdetail.OfsRequestDetailRecord;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;

/**
 *
 * @author anagha.s
 *         ----------------------------------------------------------------------------------------------------------------
 *         Description : This routine is used to update status to the template
 *         when OFS is posted.
 * 
 *         Developed By : Anagha Shastry
 *
 *         Development Reference :
 *         IDD-G2-013_BCI_Interface_Interbank_Transfers_Outward_Inward
 *
 *         Attached To : OFS.SOURCE>BciCCEofsUpd
 *
 *         Attached As : MSG.POST.RTN
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
public class BciCCEofsUpd extends MessageLifecycle {

    @Override
    public void postProcess(OfsRequestDetailRecord requestDetailRecord, MessageContext messageContext) {
        // TODO Auto-generated method stub
        Date dat = new Date(this);
        String currency = "";
        DatesRecord datesRec = dat.getDates();
        String today = datesRec.getToday().getValue();
        DataAccess da = new DataAccess(this);
        String msgIn = requestDetailRecord.getMsgIn().getValue();
        String msgOut = requestDetailRecord.getMsgOut().getValue();
        String transactionNumber = requestDetailRecord.getTransReference().getValue();
        String appName = requestDetailRecord.getApplication().getValue();

        int pos = msgIn.indexOf("SendersReferenceNumber");
        int len = pos + 27;
        int endlen = pos + 27 + 7;
        String id = msgIn.substring(len, endlen);
        int idlen = id.indexOf(",");

        String status = requestDetailRecord.getStatus().getValue();
        int outlen = StringUtils.ordinalIndexOf(msgOut, "/", 2);
        status = msgOut.substring(outlen + 1, outlen + 2);
        if (appName.equals("PP.ORDER.ENTRY")) {

            if (status.equals("1")) {
                BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(
                        da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", id));
                BciCceMappingFieldValuesRecord mapRecwrite = new BciCceMappingFieldValuesRecord(this);
                List<MapFieldTypeClass> fieldListmap = mapRec.getMapFieldType();
                MapFieldTypeClass mapclass = new MapFieldTypeClass();
                MapFieldTypeClass mapclass1 = new MapFieldTypeClass();
                MapFieldTypeClass mapclass2 = new MapFieldTypeClass();
                for (MapFieldTypeClass mapId : fieldListmap) {
                    String type = mapId.getMapFieldType().getValue();

                    if (type.equals("INDIVIDUAL")) {

                        mapclass.setMapFieldType(type);
                        String individualRegistrationfisrt = mapId.getMapFieldVal().get(0).getValue();
                        String individualRegistrationsec = mapId.getMapFieldVal().get(1).getValue();
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
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                mapRecwrite.setTxnType(mapRec.getTxnType().getValue());
                mapRecwrite.setTime(dtf.format(now));
                mapRecwrite.setDate(today);
                // mapRecwrite.setTime(value);
                mapRecwrite.setStatus("TO_BE_CONFIRMED_IN_PARTIAL");

                mapRecwrite.setOeId(transactionNumber);
                BciCceMappingFieldValuesTable mapTbl = new BciCceMappingFieldValuesTable(this);
                try {
                    mapTbl.write(id, mapRecwrite);
                } catch (T24IOException e) {

                }
            } else {
                PpOrderEntryRecord payRec = new PpOrderEntryRecord(
                        da.getRecord("", "PP.ORDER.ENTRY", "$NAU", transactionNumber));
                String recid = payRec.getSendersreferencenumber().getValue();
                BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(
                        da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", recid));
                BciCceMappingFieldValuesRecord mapRecwrite = new BciCceMappingFieldValuesRecord(this);
                List<MapFieldTypeClass> fieldListmap = mapRec.getMapFieldType();
                MapFieldTypeClass mapclass = new MapFieldTypeClass();
                MapFieldTypeClass mapclass1 = new MapFieldTypeClass();
                for (MapFieldTypeClass mapId : fieldListmap) {
                    String type = mapId.getMapFieldType().getValue();

                    if (type.equals("INDIVIDUAL")) {
                        mapclass.setMapFieldType(type);
                        String individualRegistrationfisrt = mapId.getMapFieldVal().get(0).getValue();
                        String individualRegistrationsec = mapId.getMapFieldVal().get(1).getValue();
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
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                mapRecwrite.setTxnType(mapRec.getTxnType().getValue());
                mapRecwrite.setTime(dtf.format(now));
                mapRecwrite.setDate(today);
                // mapRecwrite.setTime(value);
                mapRecwrite.setStatus("TO_BE_RETURNED_IN_OFS");
                mapRecwrite.setOeId(transactionNumber);
                BciCceMappingFieldValuesTable mapTbl = new BciCceMappingFieldValuesTable(this);
                try {
                    mapTbl.write(recid, mapRecwrite);
                } catch (T24IOException e) {

                }
            }
        }
        /*
         * if (appName.equals("POR.POSTING.REVERSAL")){
         * 
         * if (status.equals("1")) { BciCceMappingFieldValuesRecord mapRec = new
         * BciCceMappingFieldValuesRecord(
         * da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", id));
         * BciCceMappingFieldValuesRecord mapRecwrite = new
         * BciCceMappingFieldValuesRecord(this); List<MapFieldTypeClass>
         * fieldListmap = mapRec.getMapFieldType(); MapFieldTypeClass mapclass =
         * new MapFieldTypeClass(); MapFieldTypeClass mapclass1 = new
         * MapFieldTypeClass(); for (MapFieldTypeClass mapId : fieldListmap) {
         * String type = mapId.getMapFieldType().getValue();
         * 
         * if (type.equals("INDIVIDUAL")) { mapclass.setMapFieldType(type);
         * String individualRegistrationfisrt =
         * mapId.getMapFieldVal().get(0).getValue(); String
         * individualRegistrationsec = mapId.getMapFieldVal().get(1).getValue();
         * mapclass.setMapFieldVal(individualRegistrationfisrt, 0);
         * mapclass.setMapFieldVal(individualRegistrationsec, 1);
         * 
         * mapRecwrite.setMapFieldType(mapclass, 0); } if
         * (type.equals("ADDITIONAL")) { mapclass1.setMapFieldType(type); String
         * additionalRegistrationfirst =
         * mapId.getMapFieldVal().get(0).getValue(); String
         * additionalRegistrationSec = mapId.getMapFieldVal().get(1).getValue();
         * mapclass1.setMapFieldVal(additionalRegistrationfirst, 0);
         * mapclass1.setMapFieldVal(additionalRegistrationSec, 1);
         * 
         * mapRecwrite.setMapFieldType(mapclass1, 1); } } DateTimeFormatter dtf
         * = DateTimeFormatter.ofPattern("HH:mm:ss"); LocalDateTime now =
         * LocalDateTime.now();
         * mapRecwrite.setTxnType(mapRec.getTxnType().getValue());
         * mapRecwrite.setTime(dtf.format(now)); mapRecwrite.setDate(today); //
         * mapRecwrite.setTime(value); mapRecwrite.setStatus("RETURNED_OUT");
         * mapRecwrite.setOeId(transactionNumber); BciCceMappingFieldValuesTable
         * mapTbl = new BciCceMappingFieldValuesTable(this); try {
         * mapTbl.write(id, mapRecwrite); } catch (T24IOException e) {
         * 
         * } } else { PpOrderEntryRecord payRec = new PpOrderEntryRecord(
         * da.getRecord("", "PP.ORDER.ENTRY", "$NAU", transactionNumber));
         * String recid = payRec.getSendersreferencenumber().getValue();
         * BciCceMappingFieldValuesRecord mapRec = new
         * BciCceMappingFieldValuesRecord(
         * da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", recid));
         * BciCceMappingFieldValuesRecord mapRecwrite = new
         * BciCceMappingFieldValuesRecord(this); List<MapFieldTypeClass>
         * fieldListmap = mapRec.getMapFieldType(); MapFieldTypeClass mapclass =
         * new MapFieldTypeClass(); MapFieldTypeClass mapclass1 = new
         * MapFieldTypeClass(); for (MapFieldTypeClass mapId : fieldListmap) {
         * String type = mapId.getMapFieldType().getValue();
         * 
         * if (type.equals("INDIVIDUAL")) { mapclass.setMapFieldType(type);
         * String individualRegistrationfisrt =
         * mapId.getMapFieldVal().get(0).getValue(); String
         * individualRegistrationsec = mapId.getMapFieldVal().get(1).getValue();
         * mapclass.setMapFieldVal(individualRegistrationfisrt, 0);
         * mapclass.setMapFieldVal(individualRegistrationsec, 1);
         * 
         * mapRecwrite.setMapFieldType(mapclass, 0); } if
         * (type.equals("ADDITIONAL")) { mapclass1.setMapFieldType(type); String
         * additionalRegistrationfirst =
         * mapId.getMapFieldVal().get(0).getValue(); String
         * additionalRegistrationSec = mapId.getMapFieldVal().get(1).getValue();
         * mapclass1.setMapFieldVal(additionalRegistrationfirst, 0);
         * mapclass1.setMapFieldVal(additionalRegistrationSec, 1);
         * 
         * mapRecwrite.setMapFieldType(mapclass1, 1); } } DateTimeFormatter dtf
         * = DateTimeFormatter.ofPattern("HH:mm:ss"); LocalDateTime now =
         * LocalDateTime.now();
         * mapRecwrite.setTxnType(mapRec.getTxnType().getValue());
         * mapRecwrite.setTime(dtf.format(now)); mapRecwrite.setDate(today); //
         * mapRecwrite.setTime(value);
         * mapRecwrite.setStatus("RETURNED_OUT_FAILED");
         * mapRecwrite.setOeId(transactionNumber); BciCceMappingFieldValuesTable
         * mapTbl = new BciCceMappingFieldValuesTable(this); try {
         * mapTbl.write(recid, mapRecwrite); } catch (T24IOException e) {
         * 
         * } }
         * 
         * }
         */
    }

}
