package com.bci;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import com.techmill.integration.mensajeria.RecibirMensaje;
import com.techmill.integration.mensajeria.recibirMensaje.ResponseRecibirMensajes;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.complex.eb.templatehook.TransactionData;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebbcibcrpcredentials.EbBciBcrpCredentialsRecord;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsRecord;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsTable;
import com.temenos.t24.api.tables.ebbcinonfinancialmsg.EbBciNonFinancialMsgRecord;
import com.temenos.t24.api.tables.ebbcinonfinancialmsg.EbBciNonFinancialMsgTable;

/**
 * @author Diego Gallegos
 *
 */
public class BciNonFinancialMessage extends RecordLifecycle {
    public static final String SYSTEM = "SYSTEM";

    @Override
    public void updateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext,
            List<TransactionData> transactionData, List<TStructure> currentRecords) {
       
        DataAccess da = new DataAccess(this);
        EbBciBcrpLIntegrationLogsRecord integrationLogsRecord = new EbBciBcrpLIntegrationLogsRecord(this);
        EbBciNonFinancialMsgTable ebBciNonFinancialMsgTable = new EbBciNonFinancialMsgTable(this);
        EbBciBcrpLIntegrationLogsTable integrationLogsTable = new EbBciBcrpLIntegrationLogsTable(this);
        EbBciNonFinancialMsgRecord ebBciNonFinancialMsgRecord = new EbBciNonFinancialMsgRecord(currentRecord);
        EbBciBcrpCredentialsRecord credentialsRecord = new EbBciBcrpCredentialsRecord(
                da.getRecord("EB.BCI.BCRP.CREDENTIALS", SYSTEM));
        RecibirMensaje recibirMensaje = new RecibirMensaje();

        String sid = "";
        String mensaje = "";
        SimpleDateFormat dateformtfch = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateformthora = new SimpleDateFormat("HHmmss");
        SimpleDateFormat dateformthora1 = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String myfecha = dateformtfch.format(date);
        String myHora = dateformthora.format(date);
        String myHora1 = dateformthora1.format(date);
        Session ss = new Session(this);
        String user = ss.getUserId();

        String mydate = myfecha + "-" + myHora;
        integrationLogsRecord.setFecha(myfecha);
        integrationLogsRecord.setHora(myHora1);
        String id = "MNF-" + user + "-" + mydate;
        sid = credentialsRecord.getSid().getValue();
        mensaje = ebBciNonFinancialMsgRecord.getMensaje().getValue();

        try {
            integrationLogsRecord.setAppName("NON.FINANCIAL.MSG");
            integrationLogsRecord.setOut("sid=" + sid + "Mensaje=" + mensaje);
            integrationLogsRecord.setTxId(SYSTEM);
            ResponseRecibirMensajes responseRecibirMensajes = recibirMensaje.recibirMensajes(sid, mensaje);
            if (!responseRecibirMensajes.isOk()) {
                integrationLogsRecord.setFlagErr("YES");

            }
            integrationLogsRecord.setIn(responseRecibirMensajes.getDescription());
        } catch (IOException e) {
            integrationLogsRecord.setFlagErr("YES");
        }
        try {
            integrationLogsTable.write(id, integrationLogsRecord);
            ebBciNonFinancialMsgTable.write(id, ebBciNonFinancialMsgRecord);
        } catch (Exception e) {
            e.getMessage();

        }

    }

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        String sidStatus = "";

        EbBciNonFinancialMsgRecord ebBciNonFinancialMsgRecord = new EbBciNonFinancialMsgRecord(currentRecord);
        try {

            sidStatus = ebBciNonFinancialMsgRecord.getSidStatus().getValue();
            String rdm = RandomStringUtils.randomAlphanumeric(7);
            ebBciNonFinancialMsgRecord.setReservado1(rdm);
            currentRecord.set(ebBciNonFinancialMsgRecord.toStructure());
            if (!sidStatus.equals("Activo")) {
                ebBciNonFinancialMsgRecord.getSidStatus().setError("Su cuenta no está activa, por favor inicie sesión");

            }

        } catch (Exception e) {
            e.getMessage();

        }

        return ebBciNonFinancialMsgRecord.getValidationResponse();
    }

    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {

        Session ss = new Session(this);
        return ss.getUserId();
    }

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        com.temenos.t24.api.system.Date date = new com.temenos.t24.api.system.Date(this);
        DataAccess da = new DataAccess(this);
        EbBciNonFinancialMsgRecord ebBciNonFinancialMsgRecord = new EbBciNonFinancialMsgRecord(currentRecord);
        DatesRecord datesRecord = date.getDates();
        String sidStatus = "";
        String fecha = "";
        try {
            EbBciBcrpCredentialsRecord ebBciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(
                    da.getRecord("EB.BCI.BCRP.CREDENTIALS", SYSTEM));
            fecha = ebBciNonFinancialMsgRecord.getFecha().getValue();
            if (fecha.isEmpty() || fecha.equals("")) {

                ebBciNonFinancialMsgRecord.setFecha(datesRecord.getToday().getValue());
                ebBciNonFinancialMsgRecord.setMensaje("");

            }
            sidStatus = ebBciBcrpCredentialsRecord.getSidStatus().getValue();
            ebBciNonFinancialMsgRecord.getSidStatus().setValue(sidStatus);

        } catch (Exception e) {
            e.getMessage();

        }
        try {
            currentRecord.set(ebBciNonFinancialMsgRecord.toStructure());
        } catch (Exception e) {
            e.getMessage();

        }

    }

}
