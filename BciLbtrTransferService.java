package com.bci;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.techmill.integration.transferencias.InstruirTransferencia;
import com.techmill.integration.transferencias.InstruirTransferenciaCavali;
import com.techmill.integration.transferencias.instruirTransferencia.DatosCliente;
import com.techmill.integration.transferencias.instruirTransferencia.DatosTransferencia;
import com.techmill.integration.transferencias.instruirTransferencia.ResponseTransferencia;
import com.techmill.integration.transferencias.instruirTransferenciaCavali.DatosCavali;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebbcibcrpcredentials.EbBciBcrpCredentialsRecord;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsRecord;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsTable;
import com.temenos.t24.api.tables.ebbcibcrpparam.EbBciBcrpParamRecord;
import com.temenos.t24.api.tables.ebbcibcrpparam.PaymentOrderProductClass;


/**
 *
 * @author David Barahona
 * 
 *
 */
public class BciLbtrTransferService extends ServiceLifecycle {

    static final String SYSTEM = "SYSTEM";
    static final String SOURCEID = "OFS.LOAD";

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        DataAccess da = new DataAccess(this);
        EbBciBcrpCredentialsRecord bciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(this);
        List<String> idsLbtr = new ArrayList<>();
        try {
            bciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(da.getRecord("EB.BCI.BCRP.CREDENTIALS", SYSTEM));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        String status = bciBcrpCredentialsRecord.getSidStatus().getValue();
        if (status.equals("NoActivo")) {
            return idsLbtr;
        }
        EbBciBcrpParamRecord bciBcrpParamRecord = new EbBciBcrpParamRecord(this);
        try {
            bciBcrpParamRecord = new EbBciBcrpParamRecord(da.getRecord("EB.BCI.BCRP.PARAM", SYSTEM));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        List<PaymentOrderProductClass> listProd = bciBcrpParamRecord.getPaymentOrderProduct();
        for (PaymentOrderProductClass paymentOrderProductClass : listProd) {
            String product = paymentOrderProductClass.getPaymentOrderProduct().getValue();
            idsLbtr.addAll(da.selectRecords("", "PAYMENT.ORDER", "$NAU", "WITH PAYMENT.ORDER.PRODUCT EQ " + product
                    + " AND RECORD.STATUS EQ INA2 " + "AND L.ESTADO EQ 3 AND L.NUM.REF.LBTR EQ ''"));
        }
        idsLbtr.addAll(da.selectRecords("", "PAYMENT.ORDER", "",
                "WITH PAYMENT.ORDER.PRODUCT EQ LBTRB AND PAYMENT.STATUS.ADD.INFO EQ 999 "
                        + "AND BULK.REFERENCE NE '' AND L.NUM.REF.LBTR EQ '' AND L.ESTADO EQ '' AND L.CODE.ERROR EQ ''"));
        return idsLbtr;
    }

    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        EbBciBcrpLIntegrationLogsRecord integrationLogsRecord = new EbBciBcrpLIntegrationLogsRecord(this);
        EbBciBcrpLIntegrationLogsTable integrationLogsTable = new EbBciBcrpLIntegrationLogsTable(this);
        integrationLogsRecord.setAppName("PAYMENT.ORDER");
        Date date = new Date();
        SimpleDateFormat dateformtfch = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateformthora1 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateformthora = new SimpleDateFormat("HHmmss");
        String myfecha = dateformtfch.format(date);
        String myHora1 = dateformthora1.format(date);
        String myHoraId = dateformthora.format(date);
        integrationLogsRecord.setFecha(myfecha);
        integrationLogsRecord.setHora(myHora1);
        integrationLogsRecord.setTxId(id);
        DataAccess da = new DataAccess(this);
        EbBciBcrpParamRecord bciBcrpParamRecord = new EbBciBcrpParamRecord(this);
        EbBciBcrpCredentialsRecord bciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(this);
        DatosCavali datosCavali = new DatosCavali();

        try {
            bciBcrpParamRecord = new EbBciBcrpParamRecord(da.getRecord("EB.BCI.BCRP.PARAM", SYSTEM));
            bciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(da.getRecord("EB.BCI.BCRP.CREDENTIALS", SYSTEM));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        String status = bciBcrpCredentialsRecord.getSidStatus().getValue();
        if (status.equals("NoActivo")) {
            return;
        }
        String sid = bciBcrpCredentialsRecord.getSid().getValue();
        List<PaymentOrderProductClass> listProd = bciBcrpParamRecord.getPaymentOrderProduct();
        PaymentOrderRecord poRec = new PaymentOrderRecord();
        try {
            poRec = new PaymentOrderRecord(da.getRecord("", "PAYMENT.ORDER", "$NAU", id));
        } catch (Exception e) {
            try {
                poRec = new PaymentOrderRecord(da.getRecord("", "PAYMENT.ORDER", "", id));
            } catch (Exception e2) {
                System.err.println(e2.getMessage());
            }
        }
        DatosTransferencia datosTransferencia = new DatosTransferencia();
        ResponseTransferencia responseTransferencia = new ResponseTransferencia();
        String poProduct = poRec.getPaymentOrderProduct().getValue();
        DatosCliente datosCliente = new DatosCliente();
        String dataClient = "";

        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");

        // Datos de cliente
        if (poProduct.toUpperCase().equals("LBTRA") || poProduct.toUpperCase().equals("LBTRB")) {
            List<String> listdataClient = new ArrayList<String>();
            String cciOrdenate = poRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
            String nombreOrdenante = poRec.getOrderingCustName().getValue();
            String direccionOrdenante = "";
            List<TField> AddrLine = poRec.getOrderingPostAddrLine();
            for (TField tField : AddrLine) {
                direccionOrdenante = direccionOrdenante.concat(" ").concat(tField.getValue());
            }
            String tipoDocOrdenante = poRec.getLocalRefField("L.TIPODOC.ORDENANTE").getValue();
            String numDocOrdenante = poRec.getLocalRefField("L.NUM.DOC.ORDENANTE").getValue();
            String cciBeneficiario = poRec.getLocalRefField("L.CCI.DESTINATION").getValue();
            String nombreBeneficiario = poRec.getBeneficiaryName().getValue();
            String direccionBeneficiario = poRec.getLocalRefField("L.DIRECCION.BENEFICIARIO").getValue();
            String tipoDocBeneficiario = poRec.getLocalRefField("L.TIPODOC.BENEFICIARIO").getValue();
            String numDocBeneficiario = poRec.getLocalRefField("L.NUM.DOC.BENEFICIARIO").getValue();
            String indicadorItf = poRec.getLocalRefField("L.SAME.OWNER").getValue();
            String observaciones = "";
            List<TField> listObservaciones = poRec.getAdditionalInfo();
            for (TField tField : listObservaciones) {
                observaciones = observaciones.concat(tField.getValue().concat(" "));
            }

            listdataClient.add(cciOrdenate);
            listdataClient.add(nombreOrdenante);
            listdataClient.add(direccionOrdenante);
            listdataClient.add(tipoDocOrdenante);
            listdataClient.add(numDocOrdenante);
            listdataClient.add(cciBeneficiario);
            listdataClient.add(nombreBeneficiario);
            listdataClient.add(direccionBeneficiario);
            listdataClient.add(tipoDocBeneficiario);
            listdataClient.add(numDocBeneficiario);
            listdataClient.add(indicadorItf);
            listdataClient.add(observaciones.trim());

            for (String data : listdataClient) {
                dataClient = dataClient.concat(data).concat("|");
            }
        }

        datosCliente.setDataCliente(dataClient);
        datosCliente.setFirmaDataCliente("");
        datosCliente.setKsimDataCliente("");

        // Datos de Transferencia
        String codConcepto = poRec.getLocalRefField("L.CODE.CONCEPT").getValue();
        String cuentaDestino = poRec.getLocalRefField("L.CTA.NO.CTA.EXT").getValue();
        String cuentaOrigen = poRec.getLocalRefField("L.ACT.EXTERNO").getValue();

        String instruccionesPago = "";
        String versionName = "";
        for (PaymentOrderProductClass paymentOrderProductClass : listProd) {
            String poProductParam = paymentOrderProductClass.getPaymentOrderProduct().getValue();
            if (poProductParam.equals(poProduct)) {
                versionName = paymentOrderProductClass.getVesionName().getValue();
                break;
            }
        }
        if (versionName.isEmpty()) {
            System.err.println("Error al obtener nombre de Version");
            return;
        }

        List<TField> narratives = poRec.getNarrative();
        for (TField tField : narratives) {
            instruccionesPago = instruccionesPago.concat(" ").concat(tField.getValue()).trim();
        }

        String montoOperacion = poRec.getPaymentAmount().getValue();
        String numRefOrigen = id;
        String prioridad = "a";
        Date fechaLiquidacion = new Date();
        Date fechaRefLBTREnlace = new Date();
        try {

            String fechaLiquidacionStr = poRec.getLocalRefField("L.FECHA.LIQUIDACION").getValue();
            fechaLiquidacion = formatter1.parse(fechaLiquidacionStr);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }

        datosTransferencia.setCodConcepto(codConcepto);
        datosTransferencia.setCuentaDestino(cuentaDestino);
        datosTransferencia.setCuentaOrigen(cuentaOrigen);
        datosTransferencia.setDatosCliente(datosCliente);
        datosTransferencia.setFechaLiquidacion(fechaLiquidacion);
        datosTransferencia.setInstruccionesPago(instruccionesPago);
        // datosTransferencia.setModalidad(modalidad);
        datosTransferencia.setMontoOperacion(montoOperacion);
        datosTransferencia.setNumRefOrigen(numRefOrigen);
        if (poProduct.toUpperCase().equals("LBTRF")) {
            try {
                String numRefEnlaceOperacion = poRec.getLocalRefField("L.NUM.REF.ORIGEN").getValue();
                String fechaRefLBTREnlaceStr = poRec.getPaymentExecutionDate().getValue();
                fechaRefLBTREnlace = formatter1.parse(fechaRefLBTREnlaceStr);
                datosTransferencia.setNumRefEnlaceOperacion(numRefEnlaceOperacion);
                datosTransferencia.setFechaRefLBTREnlace(fechaRefLBTREnlace);
            } catch (ParseException e) {
                System.err.println(e.getMessage());
            }
        }
        datosTransferencia.setPrioridad(prioridad);
        String dataTrasfer = datosTransferencia.toString();

        // Datos para Cavali
        if (poProduct.toUpperCase().equals("LBTRCAVRF") || poProduct.toUpperCase().equals("LBTRCAVRV")) {
            String codigoSAB = poRec.getLocalRefField("L.CODE.SAB").getValue();
            String cuentaInterbancariaSAB = poRec.getLocalRefField("L.CUENTA.INTERBANCARIA.SAB").getValue();
            String fechaNegociacionCavaliStr = poRec.getLocalRefField("L.FECHA.NEGOCIACION.CAVALI").getValue();
            String numRefCavali = poRec.getLocalRefField("L.NUM.REF.CAVALI").getValue();
            String tipoParticipanteCavali = poRec.getLocalRefField("L.TIPO.PARTICIPANTE.CAVALI").getValue();

            datosCavali.setCodigoSAB(codigoSAB);
            datosCavali.setCuentaInterbancariaSAB(cuentaInterbancariaSAB);
            try {
                Date fechaNegociacionCavali = formatter1.parse(fechaNegociacionCavaliStr);
                datosCavali.setFechaNegociacionCavali(fechaNegociacionCavali);
            } catch (Exception e1) {
                System.err.println(e1.getMessage());
            }
            datosCavali.setNumRefCavali(numRefCavali);
            datosCavali.setTipoParticipanteCavali(tipoParticipanteCavali);
            dataTrasfer = dataTrasfer + datosCavali.toString();
        }
        integrationLogsRecord.setOut(dataTrasfer);
        String firma = "";
        try {
            if (poProduct.equalsIgnoreCase("LBTRCAVRF") || poProduct.equalsIgnoreCase("LBTRCAVRV")) {
                InstruirTransferenciaCavali instruirTransferenciaCav = new InstruirTransferenciaCavali();
                responseTransferencia = instruirTransferenciaCav.instruirTransferenciaCavali(sid, datosTransferencia,
                        datosCavali, firma);
            } else {
                InstruirTransferencia instruirTransferencia = new InstruirTransferencia();
                responseTransferencia = instruirTransferencia.instruirTransferencia(sid, datosTransferencia, firma);
            }
            integrationLogsRecord.setIn(responseTransferencia.toString());
            String poId = "PO-" + id + myHoraId;
            if (!responseTransferencia.isOk()) {
                integrationLogsRecord.setFlagErr("YES");
            }
            integrationLogsTable.write(poId, integrationLogsRecord);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        String statusLBTR = null;
        String numLBTR = null;
        String codError = null;
        try {
            statusLBTR = responseTransferencia.getResult().getEstado();
            numLBTR = responseTransferencia.getResult().getNumRefLBTR();
            codError = responseTransferencia.getResult().getCodError();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        SynchronousTransactionData txnData = new SynchronousTransactionData();
        String numAuth0 = "0";
        String numAuth1 = "1";
        String numAuth2 = "2";
        String poStatus = poRec.getPaymentStatusAddInfo().getValue();

        if (poStatus.equals("999")) {
            if (statusLBTR == null && codError != null) {
                String msgError = responseTransferencia.getResult().getMensajeError();
                String limitMsgError = msgError.substring(0, Math.min(msgError.length(), 69));
                poRec.getLocalRefField("L.CODE.ERROR").setValue(codError);
                poRec.getLocalRefField("L.ERROR.MSG").setValue(limitMsgError);
            } else if (statusLBTR.equals("4")) {
                poRec.getLocalRefField("L.ESTADO").setValue(statusLBTR);
                poRec.getLocalRefField("L.NUM.REF.LBTR").setValue(numLBTR);
            } else if (statusLBTR.equals("3")) {
                poRec.getLocalRefField("L.ESTADO").setValue(statusLBTR);
                poRec.getLocalRefField("L.NUM.REF.LBTR").setValue(numLBTR);
            }
            da.updateLocalfields("PAYMENT.ORDER", id, poRec.toStructure());
            return;
        }

        if (statusLBTR == null && codError != null) {
            String msgError = responseTransferencia.getResult().getMensajeError();
            String limitMsgError = msgError.substring(0, Math.min(msgError.length(), 69));
            poRec.getLocalRefField("L.CODE.ERROR").setValue(codError);
            poRec.getLocalRefField("L.ERROR.MSG").setValue(limitMsgError);
            txnData.setVersionId(versionName);
            txnData.setFunction("I");
            txnData.setSourceId(SOURCEID);
            txnData.setNumberOfAuthoriser(numAuth2);
            txnData.setTransactionId(id);

        } else if (statusLBTR.equals("4")) {
            poRec.getLocalRefField("L.ESTADO").setValue(statusLBTR);
            poRec.getLocalRefField("L.NUM.REF.LBTR").setValue(numLBTR);
            txnData.setVersionId(versionName);
            txnData.setFunction("I");
            txnData.setSourceId(SOURCEID);
            txnData.setNumberOfAuthoriser(numAuth0);
            txnData.setTransactionId(id);

        } else if (statusLBTR.equals("3")) {
            poRec.getLocalRefField("L.NUM.REF.LBTR").setValue(numLBTR);
            txnData.setVersionId(versionName);
            txnData.setFunction("I");
            txnData.setSourceId(SOURCEID);
            txnData.setNumberOfAuthoriser(numAuth1);
            txnData.setTransactionId(id);
        }

        transactionData.add(txnData);
        records.add(poRec.toStructure());
    }
}