package com.bci;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.messagehook.MessageContext;
import com.temenos.t24.api.hook.system.MessageLifecycle;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.records.ofsrequestdetail.OfsRequestDetailRecord;
import com.temenos.t24.api.records.pporderentry.PpOrderEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesRecord;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.BciCceMappingFieldValuesTable;
import com.temenos.t24.api.tables.bciccemappingfieldvalues.MapFieldTypeClass;

/**
 * TODO: Document me!
 *
 * @author spoorthi.bs
 *
 */
public class BciChqinOfsUpdate extends MessageLifecycle {

    @Override
    public void postProcess(OfsRequestDetailRecord requestDetailRecord, MessageContext messageContext) {
        // TODO Auto-generated method stub
        Date dat = new Date(this);
        DatesRecord datesRec = dat.getDates();
        String today = datesRec.getToday().getValue();
        DataAccess da = new DataAccess(this);
        String msgIn = requestDetailRecord.getMsgIn().getValue();
        String msgOut = requestDetailRecord.getMsgOut().getValue();
        String transactionNumber = requestDetailRecord.getTransReference().getValue();
        
        int pos = msgIn.indexOf("SendersReferenceNumber");
        int len = pos + 27;
        int endlen = pos + 27 + 15;
        String id = msgIn.substring(len, endlen);
        int idlen = id.indexOf(",");
        
              
        String status = requestDetailRecord.getStatus().getValue();
        int outlen = StringUtils.ordinalIndexOf(msgOut, "/", 2);
        status = msgOut.substring(outlen + 1, outlen + 2);
        if (status.equals("1")) {
            
            int bnkPos = msgOut.indexOf("TransactionReferenceNumber");
            int bnkLen = bnkPos + 31;
            int bnkEndLen = bnkPos + 31 + 16;
            String bnkid = msgIn.substring(bnkLen, bnkEndLen);
            int bnkIdLen = bnkid.indexOf(",");
            
            BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(
                    da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", id));
            BciCceMappingFieldValuesRecord mapRecwrite = new BciCceMappingFieldValuesRecord(this);
            List<MapFieldTypeClass> fieldListmap = mapRec.getMapFieldType();
            MapFieldTypeClass mapclass = new MapFieldTypeClass();
            for (MapFieldTypeClass mapId : fieldListmap) {
                String type = mapId.getMapFieldType().getValue();

                if (type.equals("INDIVIDUAL")) {
                    mapclass.setMapFieldType(type);
                    String individualRegistrationfisrt = mapId.getMapFieldVal().get(0).getValue();
                    mapclass.setMapFieldVal(individualRegistrationfisrt, 0);
                                       
                }
                /*
                 * if (type.equals("ADDITIONAL")) {
                 * mapclass1.setMapFieldType(type); String
                 * additionalRegistrationfirst =
                 * mapId.getMapFieldVal().get(0).getValue(); String
                 * additionalRegistrationSec =
                 * mapId.getMapFieldVal().get(1).getValue();
                 * mapclass1.setMapFieldVal(additionalRegistrationfirst, 0);
                 * mapclass1.setMapFieldVal(additionalRegistrationSec, 1);
                 * 
                 * mapRecwrite.setMapFieldType(mapclass1, 1); }
                 */
            }
            mapRecwrite.setMapFieldType(mapclass, 0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            mapRecwrite.setTime(dtf.format(now));
            mapRecwrite.setDate(today);
            mapRecwrite.setInRef(bnkid);
            // mapRecwrite.setTime(value);
            mapRecwrite.setStatus("TO_BE_CLEARED_IN_PARTIAL");
            mapRecwrite.setOeId(transactionNumber);
            BciCceMappingFieldValuesTable mapTbl = new BciCceMappingFieldValuesTable(this);
            try {
                mapTbl.write(id, mapRecwrite);
            } catch (T24IOException e) {

            }

        }
        else{
            PpOrderEntryRecord payRec = new PpOrderEntryRecord(
                    da.getRecord("", "PP.ORDER.ENTRY", "$NAU", transactionNumber));
            String recid = payRec.getSendersreferencenumber().getValue();
            BciCceMappingFieldValuesRecord mapRec = new BciCceMappingFieldValuesRecord(
                    da.getRecord("EB.BCI.CCE.MAPPING.FIELD.VALUES", recid));
            BciCceMappingFieldValuesRecord mapRecwrite = new BciCceMappingFieldValuesRecord(this);
            List<MapFieldTypeClass> fieldListmap = mapRec.getMapFieldType();
            MapFieldTypeClass mapclass = new MapFieldTypeClass();
            for (MapFieldTypeClass mapId : fieldListmap) {
                String type = mapId.getMapFieldType().getValue();

                if (type.equals("INDIVIDUAL")) {
                    mapclass.setMapFieldType(type);
                    String individualRegistrationfisrt = mapId.getMapFieldVal().get(0).getValue();
                    mapclass.setMapFieldVal(individualRegistrationfisrt, 0);
                                       
                }
                /*
                 * if (type.equals("ADDITIONAL")) {
                 * mapclass1.setMapFieldType(type); String
                 * additionalRegistrationfirst =
                 * mapId.getMapFieldVal().get(0).getValue(); String
                 * additionalRegistrationSec =
                 * mapId.getMapFieldVal().get(1).getValue();
                 * mapclass1.setMapFieldVal(additionalRegistrationfirst, 0);
                 * mapclass1.setMapFieldVal(additionalRegistrationSec, 1);
                 * 
                 * mapRecwrite.setMapFieldType(mapclass1, 1); }*/
                 
            }
            mapRecwrite.setMapFieldType(mapclass, 0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            mapRecwrite.setTime(dtf.format(now));
            mapRecwrite.setDate(today);
            //mapRecwrite.setInRef(bnkid);
            // mapRecwrite.setTime(value);
            mapRecwrite.setStatus("TO_BE_RETURN_IN_ADDITIONAL_OFS");
            mapRecwrite.setOeId(transactionNumber);
            BciCceMappingFieldValuesTable mapTbl = new BciCceMappingFieldValuesTable(this);
            try {
                mapTbl.write(recid, mapRecwrite);
            } catch (T24IOException e) {

            }
        }

    }

}
