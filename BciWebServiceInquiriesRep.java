package com.bci;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.RandomStringUtils;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebbcibcrpconsultasselect.EbBciBcrpConsultasSelectRecord;
import com.temenos.t24.api.tables.ebbcibcrpcredentials.EbBciBcrpCredentialsRecord;

/**
 * @author Diego Maigualca
 *
 */

public class BciWebServiceInquiriesRep extends RecordLifecycle {

    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {
        // Cambia el id de un registro en EB.BCI.BCRP.CONSULTAS.SELECT
        Session ss = new Session(this);
        return ss.getUserId();
    }

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        DataAccess da = new DataAccess(this);
        String sidStatus = "";
        String typeId = "";
        EbBciBcrpConsultasSelectRecord ebBciBcrpConsultasSelectRecord = new EbBciBcrpConsultasSelectRecord(this);
        try {
            EbBciBcrpCredentialsRecord ebBciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(
                    da.getRecord("EB.BCI.BCRP.CREDENTIALS", "SYSTEM"));
            sidStatus = ebBciBcrpCredentialsRecord.getSidStatus().getValue();

            ebBciBcrpConsultasSelectRecord = new EbBciBcrpConsultasSelectRecord(currentRecord);
            ebBciBcrpConsultasSelectRecord.setSidStatus(sidStatus);

        } catch (Exception e) {
            e.getMessage();
        }
        String typeVersion = "";

        try {
            typeVersion = ebBciBcrpConsultasSelectRecord.getType().getValue();
            final String filtro = "WITH DESCRIPTION EQ '" + typeVersion + "'";
            final List<String> typeList = (List<String>) da.selectRecords("", "EB.LOOKUP", "", filtro);
            typeId = typeList.get(0);
        } catch (Exception e) {
            e.getMessage();
        }
        // set the data
        if (typeVersion.equals("") || typeVersion.isEmpty()) {
            ebBciBcrpConsultasSelectRecord.setPeriodo("");
            ebBciBcrpConsultasSelectRecord.setEstado("");
            ebBciBcrpConsultasSelectRecord.setFecha("");
            ebBciBcrpConsultasSelectRecord.setNumCuenta("");
            ebBciBcrpConsultasSelectRecord.setInstitucionId("");
            ebBciBcrpConsultasSelectRecord.setFechaLiquidacion("");
        }

        ebBciBcrpConsultasSelectRecord.setIdEbLookup(typeId);

        currentRecord.set(ebBciBcrpConsultasSelectRecord.toStructure());
    }

    @Override
    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {
        DataAccess da = new DataAccess(this);
        String typeId = "";

        EbBciBcrpConsultasSelectRecord ebBciBcrpConsultasSelectRecord = new EbBciBcrpConsultasSelectRecord(
                currentRecord);
        try {
            String typeVersion = ebBciBcrpConsultasSelectRecord.getType().getValue();
            final String filtro = "WITH DESCRIPTION EQ '" + typeVersion + "'";
            final List<String> typeList = (List<String>) da.selectRecords("", "EB.LOOKUP", "", filtro);
            typeId = typeList.get(0);
        } catch (Exception e) {
            e.getMessage();
        }
        // set the data

        ebBciBcrpConsultasSelectRecord.setPeriodo("");
        ebBciBcrpConsultasSelectRecord.setEstado("");
        ebBciBcrpConsultasSelectRecord.setFecha("");
        ebBciBcrpConsultasSelectRecord.setNumCuenta("");
        ebBciBcrpConsultasSelectRecord.setInstitucionId("");
        ebBciBcrpConsultasSelectRecord.setFechaLiquidacion("");
        // change the select type

        ebBciBcrpConsultasSelectRecord.setIdEbLookup(typeId);
        currentRecord.set(ebBciBcrpConsultasSelectRecord.toStructure());

    }

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        DataAccess da = new DataAccess(this);
        String sid = "";
        String sidStatus = "";
        String fechas = "";
        EbBciBcrpConsultasSelectRecord ebBciBcrpConsultasSelectRecord = null;
        TField sidStatusFieldMine = null;

        String nameVersion = "EB.BCI.BCRP.CONSULTAS";
        String nameEBLookUp = nameVersion + "*";
        String nameUnidoVersion = nameEBLookUp + nameVersion;
        String cobroTarifa = nameUnidoVersion + ",FEE.COLLECTION";
        String ctasCtesEntidad = nameUnidoVersion + ",ACCT.CTE.ENTITY";
        String movimientosCtaCte = nameUnidoVersion + ",ACCT.CURRENT.MOVES";
        String negCompraVenta = nameUnidoVersion + ",BUY.SELL.ME";
        String operacionOtorgada = nameUnidoVersion + ",OPERATIONS.GRANTED";
        String operacionEntregada = nameUnidoVersion + ",OPERATIONS.RECEIVED";
        String saldoCtaCte = nameUnidoVersion + ",ACCT.CURRENT.BALANCES";
        String tipoCambio = nameUnidoVersion + ",CHANGE.TYPE";
        String typeId = "";
        // Mensaje de error
        String msgError = "no puede ser nulo para esta consulta";

        try {
            EbBciBcrpCredentialsRecord ebBciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(
                    da.getRecord("EB.BCI.BCRP.CREDENTIALS", "SYSTEM"));
            sid = ebBciBcrpCredentialsRecord.getSid().getValue();
            sidStatus = ebBciBcrpCredentialsRecord.getSidStatus().getValue();
            ebBciBcrpConsultasSelectRecord = new EbBciBcrpConsultasSelectRecord(currentRecord);
            ebBciBcrpConsultasSelectRecord.setSid(sid);
            ebBciBcrpConsultasSelectRecord.setSidStatus(sidStatus);
            sidStatusFieldMine = ebBciBcrpConsultasSelectRecord.getSidStatus();

            /* Lógica para comprobar mandatory fields */

            TField periodoVersionFld = new TField();
            TField fechaVersionFld = new TField();
            TField fechaLiquidacionFld = new TField();
            TField estadoVersionFld = new TField();
            TField numCuentaVersionFld = new TField();
            TField institucionIdFld = new TField();
            try {

                // Obtenemos los TFIleds de los campos que pueden ser
                // mandatorios
                periodoVersionFld = ebBciBcrpConsultasSelectRecord.getPeriodo();
                fechaVersionFld = ebBciBcrpConsultasSelectRecord.getFecha();
                estadoVersionFld = ebBciBcrpConsultasSelectRecord.getEstado();
                fechaLiquidacionFld = ebBciBcrpConsultasSelectRecord.getFechaLiquidacion();
                numCuentaVersionFld = ebBciBcrpConsultasSelectRecord.getNumCuenta();
                institucionIdFld = ebBciBcrpConsultasSelectRecord.getInstitucionId();

                // Obtenemos el valor del combobox
                String typeVersion = ebBciBcrpConsultasSelectRecord.getType().getValue();

                // Obtenemos el valor de los Tfields
                String periodoVersion = periodoVersionFld.getValue();
                String fechaVersion = fechaVersionFld.getValue();
                String fechaLiquidacionVersion = fechaLiquidacionFld.getValue();
                String estadoVersion = estadoVersionFld.getValue();
                String numCuentaVersion = numCuentaVersionFld.getValue();
                String institucionVersion = institucionIdFld.getValue();
                try {
                    final String filtro = "WITH DESCRIPTION EQ '" + typeVersion + "'";
                    final List<String> typeList = (List<String>) da.selectRecords("", "EB.LOOKUP", "", filtro);
                    typeId = typeList.get(0);

                } catch (Exception e) {

                }

                // Mensaje de validacion para las fechas
                String msgFechaInvalid = "El formato de la fecha no es correcto";

                boolean validateFecha = false;

                // Validacion de la fecha
                fechas = fechaVersion;
                validateFecha = validateFecha(fechas);
                if (validateFecha) {
                    fechaVersionFld.setError(msgFechaInvalid);
                }

                // Validacion de la fecha de liquidacion
                fechas = fechaLiquidacionVersion;
                validateFecha = validateFecha(fechas);
                if (validateFecha) {
                    fechaLiquidacionFld.setError(msgFechaInvalid);
                }

                // Validacion de la periodo
                fechas = periodoVersion;
                validateFecha = validateFecha(fechas);
                if (validateFecha) {
                    periodoVersionFld.setError(msgFechaInvalid);
                }

                if (typeId.equals("") || typeId.isEmpty()) {
                    ebBciBcrpConsultasSelectRecord.getType().setError("AC-INP.MISS");
                }
                if (typeId.equals(cobroTarifa) && (periodoVersion.equals(""))) {
                    periodoVersionFld.setError(msgError);
                } else if ((typeId.equals(movimientosCtaCte)
                        || typeId.equals(saldoCtaCte) || typeId.equals(tipoCambio))) {
                    if (fechaVersion.equals("")) {
                        fechaVersionFld.setError(msgError);
                    }
                    if (numCuentaVersion.equals("")) {
                        numCuentaVersionFld.setError(msgError);
                    }
                } else if ((typeId.equals(operacionOtorgada) || typeId.equals(operacionEntregada))) {
                    if (fechaLiquidacionVersion.equals("")) {
                        fechaLiquidacionFld.setError(msgError);
                    }
                } else if (typeId.equals(ctasCtesEntidad) && institucionVersion.equals("")) {
                    institucionIdFld.setError(msgError);
                }
                if (typeId.equals(tipoCambio)) {
                    if (estadoVersion.isEmpty()) {
                        estadoVersionFld.setError(msgError);
                    }
                }
                if(typeId.equals(negCompraVenta)) {
                    if (fechaVersion.equals("")) {
                        fechaVersionFld.setError(msgError);
                    }
                }
            } catch (Exception ex) {
                ex.getMessage();
            }

            String rdm = RandomStringUtils.randomAlphanumeric(3);
            ebBciBcrpConsultasSelectRecord.setSid(rdm);
            currentRecord.set(ebBciBcrpConsultasSelectRecord.toStructure());

            if (!sidStatus.equals("Activo")) {
                throw new Exception("Su cuenta no está activa, por favor inicie sesión");
            }

        } catch (Exception e) {
            sidStatusFieldMine.setError(e.getMessage());
        }
        return ebBciBcrpConsultasSelectRecord.getValidationResponse();
    }

    public static boolean validateFecha(String fecha) {
        boolean isIncorrect = false;
        Date date1 = new Date();
        if (!fecha.equals("")) {
            try {
                date1 = new SimpleDateFormat("yyyyMMdd").parse(fecha);

            } catch (Exception ex) {
                try {
                    date1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(fecha);
                } catch (Exception e) {
                    isIncorrect = true;
                }
            }
        }

        System.out.println(date1);
        return isIncorrect;
    }
}
