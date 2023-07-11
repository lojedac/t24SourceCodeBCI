package com.bci;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.techmill.integration.mensajeria.ConsultarMensajeRecibido;
import com.techmill.integration.mensajeria.consultarMensajesEnviado.Emisor;
import com.techmill.integration.mensajeria.consultarMensajesEnviado.Entidad;
import com.techmill.integration.mensajeria.consultarMensajesEnviado.Receptore;
import com.techmill.integration.mensajeria.consultarMensajesRecibido.MensajeNoFinanciero;
import com.techmill.integration.mensajeria.consultarMensajesRecibido.RootConsultarMensajesRecibido;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.complex.eb.templatehook.TransactionData;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebbcibcrpcredentials.EbBciBcrpCredentialsRecord;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsRecord;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsTable;
import com.temenos.t24.api.tables.ebbcibcrpmsgnofinancial.EbBciBcrpMsgNoFinancialRecord;
import com.temenos.t24.api.tables.ebbcibcrpmsgnofinancial.EbBciBcrpMsgNoFinancialTable;
import com.temenos.t24.api.tables.ebbcibcrpmsgnofinancial.IdEmisorClass;
import com.temenos.t24.api.tables.ebbcibcrprecept.EbBciBcrpReceptRecord;
import com.temenos.t24.api.tables.ebbcibcrprecept.EbBciBcrpReceptTable;
import com.temenos.t24.api.tables.ebbcibcrprecept.IdClass;
import com.temenos.t24.api.tables.ebbcibcrprecept.IdEntityClass;
import com.temenos.t24.api.tables.ebbcinonfinancialmsg.EbBciNonFinancialMsgRecord;

/**
 * @author Diego Gallegos
 *
 */
public class BciNonFinInMsgInquiries extends RecordLifecycle {
    public static final String DATEFORMAT = "yyyyMMdd";
    public static final String DATEERROCANTAFTERTODAY = "AC-CANT.AFTER.TODAYS.DATE";
    public static final String DATEERROINICANTAFTERFIN = "AC-DT.GT.STDATE";
    @Override
    public void updateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext,
            List<TransactionData> transactionData, List<TStructure> currentRecords) {

        DataAccess da = new DataAccess(this);
        EbBciBcrpMsgNoFinancialRecord ebBciMsgNoFinancialRecord = new EbBciBcrpMsgNoFinancialRecord(this);
        EbBciBcrpMsgNoFinancialTable enBciMsgNoFinancialTable = new EbBciBcrpMsgNoFinancialTable(this);
        EbBciBcrpReceptRecord ebBciMnfReceptRecord = new EbBciBcrpReceptRecord(this);
        EbBciBcrpReceptTable bciBcrpReceptTable = new EbBciBcrpReceptTable(this);
        EbBciNonFinancialMsgRecord ebBciNonFinancialMsgRecord = new EbBciNonFinancialMsgRecord(currentRecord);
        EbBciBcrpCredentialsRecord credentialsRecord = new EbBciBcrpCredentialsRecord(
                da.getRecord("EB.BCI.BCRP.CREDENTIALS", "SYSTEM"));
        EbBciBcrpLIntegrationLogsRecord integrationLogsRecord = new EbBciBcrpLIntegrationLogsRecord(this);
        EbBciBcrpLIntegrationLogsTable integrationLogsTable = new EbBciBcrpLIntegrationLogsTable(this);

        SimpleDateFormat dateformtfch = new SimpleDateFormat(DATEFORMAT);
        SimpleDateFormat dateformthora = new SimpleDateFormat("HHmmss");
        SimpleDateFormat dateformthora1 = new SimpleDateFormat("HH:mm:ss");
        String fechaInicio = "";
        String fechaFinal = "";
        String sid = "";
        String idMNF = "";
        Date date = new Date();
        String myfecha = dateformtfch.format(date);
        String myHora = dateformthora.format(date);
        String myHora1 = dateformthora1.format(date);
        String mydate = myfecha + "-" + myHora;
        integrationLogsRecord.setFecha(myfecha);
        integrationLogsRecord.setHora(myHora1);
        String idLogs = "MNF-" + mydate;

        int cont = 0;
        int contadorPrincipal = 0;

        IdClass idClassReceptore = new IdClass();

        Receptore receptor = new Receptore();
        ArrayList<MensajeNoFinanciero> listMensaje = new ArrayList<>();
        ConsultarMensajeRecibido consultarMsgIn = new ConsultarMensajeRecibido();
        RootConsultarMensajesRecibido roConsutarMsgRecibido;

        fechaInicio = ebBciNonFinancialMsgRecord.getFechaIni().getValue();
        fechaFinal = ebBciNonFinancialMsgRecord.getFechaFin().getValue();
        sid = credentialsRecord.getSid().getValue();
        try {
            integrationLogsRecord.setAppName("NON.FINANCIAL.MSG.");
            integrationLogsRecord.setOut("sid=" + sid + "FechaInicio=" + fechaInicio + "FechaFinal=" + fechaFinal);
            integrationLogsRecord.setTxId("SYSTEM");
            roConsutarMsgRecibido = consultarMsgIn.processRequest(fechaInicio, fechaFinal, sid);
            integrationLogsRecord.setIn(roConsutarMsgRecibido.toString());

            if (roConsutarMsgRecibido.isOk()) {
                listMensaje = roConsutarMsgRecibido.getResult().getMensajeNoFinanciero();

                for (MensajeNoFinanciero mensajeNoFinanciero : listMensaje) {
                    idMNF = mensajeNoFinanciero.getId();
                    cont = 0;

                    receptor = mensajeNoFinanciero.getReceptores();
                    idClassReceptore = new IdClass();
                    idClassReceptore.setId(receptor.getId());
                    idClassReceptore.setRead(receptor.getLeido());
                    idClassReceptore.setIdMsg(receptor.getMensaje().getId());

                    Entidad entity = new Entidad();
                    entity = receptor.getEntidad();
                    IdEntityClass entityClass = new IdEntityClass();
                    entityClass.setIdEntity(entity.getId());
                    entityClass.setDescriptionEntity(entity.getDescripcion());
                    entityClass.setShortNameEntity(entity.getNombreCorto());

                    idClassReceptore.setIdEntity(entityClass, 0);

                    ebBciMnfReceptRecord.setId(idClassReceptore, cont);
                    bciBcrpReceptTable.write(idMNF, ebBciMnfReceptRecord);

                    com.temenos.t24.api.tables.ebbcibcrpmsgnofinancial.IdClass idClass = new com.temenos.t24.api.tables.ebbcibcrpmsgnofinancial.IdClass();

                    Emisor emisor = mensajeNoFinanciero.getEmisor();
                    IdEmisorClass emisorClass = new IdEmisorClass();
                    emisorClass.setIdEmisor(emisor.getId());
                    emisorClass.setDescriptionEmisor(emisor.getDescripcion());
                    emisorClass.setShortNameEmisor(emisor.getNombreCorto());

                    idClass.setIdEmisor(emisorClass, 0);
                    try {
                        String fecha = dateformtfch.format(mensajeNoFinanciero.getFecha());
                        idClass.setDate(fecha);

                    } catch (Exception e) {
                    }
                    idClass.setTextMsg(mensajeNoFinanciero.getTextoMensaje());
                    idClass.setId(idMNF);
                    idClass.setIdReceptor(idMNF, 0);

                    ebBciMsgNoFinancialRecord.setId(idClass, contadorPrincipal);
                    contadorPrincipal++;

                }
                enBciMsgNoFinancialTable.write("RECIBIDOS", ebBciMsgNoFinancialRecord);
                integrationLogsTable.write(idLogs, integrationLogsRecord);
            } else {
                integrationLogsRecord.setFlagErr("YES");
            }

        } catch (IOException e) {
            integrationLogsRecord.setFlagErr("YES");
            e.getMessage();
        }

    }

    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {

        Session ss = new Session(this);
        return ss.getUserId();
    }

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        EbBciNonFinancialMsgRecord ebBciNonFinancialMsgRecord = new EbBciNonFinancialMsgRecord(currentRecord);
        EbBciBcrpLIntegrationLogsRecord integrationLogsRecord = new EbBciBcrpLIntegrationLogsRecord(this);

        String fechaInicio = "";
        String fechaFinal = "";
        Date date = new Date();

        fechaInicio = ebBciNonFinancialMsgRecord.getFechaIni().getValue();
        fechaFinal = ebBciNonFinancialMsgRecord.getFechaFin().getValue();

        try {

            Date fechaInicioDate = new SimpleDateFormat(DATEFORMAT).parse(fechaInicio);
            Date fechaFinDate = new SimpleDateFormat(DATEFORMAT).parse(fechaFinal);

            if (fechaInicioDate.compareTo(date) >= 0) {
                ebBciNonFinancialMsgRecord.getFechaIni().setError(DATEERROCANTAFTERTODAY);
            }
            if (fechaFinDate.compareTo(date) >= 0) {
                ebBciNonFinancialMsgRecord.getFechaFin().setError(DATEERROCANTAFTERTODAY);
            }
            if (fechaInicioDate.compareTo(fechaFinDate) > 0) {
                ebBciNonFinancialMsgRecord.getFechaIni().setError(DATEERROINICANTAFTERFIN);
            }

        } catch (Exception e) {

            try {
                Date fechaInicioDate = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(fechaInicio);
                Date fechaFinDate = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(fechaFinal);
                if (fechaInicioDate.compareTo(date) >= 0) {
                    ebBciNonFinancialMsgRecord.getFechaIni().setError(DATEERROCANTAFTERTODAY);
                }
                if (fechaFinDate.compareTo(date) >= 0) {
                    ebBciNonFinancialMsgRecord.getFechaFin().setError(DATEERROCANTAFTERTODAY);
                }
                if (fechaInicioDate.compareTo(fechaFinDate) > 0) {
                    ebBciNonFinancialMsgRecord.getFechaIni().setError(DATEERROINICANTAFTERFIN);
                }
            } catch (Exception e2) {
                integrationLogsRecord.setFlagErr("YES");
                e2.getMessage();
            }

        }
        return ebBciNonFinancialMsgRecord.getValidationResponse();
    }

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        EbBciNonFinancialMsgRecord ebBciNonFinancialMsgRecord = new EbBciNonFinancialMsgRecord(currentRecord);

        ebBciNonFinancialMsgRecord.setMensaje("");
        currentRecord.set(ebBciNonFinancialMsgRecord.toStructure());
    }

}
