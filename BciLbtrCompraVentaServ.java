package com.bci;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.techmill.integration.cw.CompraVenta;
import com.techmill.integration.cw.compraVenta.DatosCompraVentaME;
import com.techmill.integration.cw.compraVenta.ResponseCompraVenta;
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

/**
 *
 * @author David Barahona
 *
 */
public class BciLbtrCompraVentaServ extends ServiceLifecycle {

    static final String PAYMENTORDER = "PAYMENT.ORDER";
    static final String SOURCEID = "OFS.LOAD";

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        DataAccess da = new DataAccess(this);
        EbBciBcrpCredentialsRecord bciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(this);
        List<String> idsLbtr = new ArrayList<String>();
        try {
            bciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(
                    da.getRecord("EB.BCI.BCRP.CREDENTIALS", "SYSTEM"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        String status = bciBcrpCredentialsRecord.getSidStatus().getValue();
        if (status.equals("NoActivo")) {
            return idsLbtr;
        }

        idsLbtr.addAll(da.selectRecords("", PAYMENTORDER, "$NAU", "WITH PAYMENT.ORDER.PRODUCT EQ LBTRCME "
                + "AND RECORD.STATUS EQ INA2 AND L.ESTADO EQ 3 AND L.NUM.REF.LBTR EQ ''"));
        idsLbtr.addAll(da.selectRecords("", PAYMENTORDER, "$NAU", "WITH PAYMENT.ORDER.PRODUCT EQ LBTRVME "
                + "AND RECORD.STATUS EQ INA2 AND L.ESTADO EQ 3 AND L.NUM.REF.LBTR NE ''"));
        return idsLbtr;
    }

    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        EbBciBcrpLIntegrationLogsRecord integrationLogsRecord = new EbBciBcrpLIntegrationLogsRecord(this);
        EbBciBcrpLIntegrationLogsTable integrationLogsTable = new EbBciBcrpLIntegrationLogsTable(this);
        integrationLogsRecord.setAppName("COMPRAVENTA");
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
        EbBciBcrpCredentialsRecord bciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(this);
        DatosCompraVentaME datosCompraVentaME = new DatosCompraVentaME();
        ResponseCompraVenta responseCompraVenta = new ResponseCompraVenta();
        String poId = "CV-" + id + myHoraId;
        String versionName = "";

        try {
            bciBcrpCredentialsRecord = new EbBciBcrpCredentialsRecord(
                    da.getRecord("EB.BCI.BCRP.CREDENTIALS", "SYSTEM"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        String status = bciBcrpCredentialsRecord.getSidStatus().getValue();
        if (status.equals("NoActivo")) {
            return;
        }

        String sid = bciBcrpCredentialsRecord.getSid().getValue();
        PaymentOrderRecord poRec = new PaymentOrderRecord(da.getRecord("", PAYMENTORDER, "$NAU", id));
        String poProduct = poRec.getPaymentOrderProduct().getValue();

        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
        String codConcepto = poRec.getLocalRefField("L.CODE.CONCEPT").getValue();
        String cuentaDestino = poRec.getLocalRefField("L.CTA.NO.CTA.EXT").getValue();
        String cuentaOrigen = poRec.getLocalRefField("L.ACT.EXTERNO").getValue();
        String instruccionesPago = "";

        List<TField> narratives = poRec.getNarrative();
        for (TField tField : narratives) {
            instruccionesPago = instruccionesPago.concat(" ").concat(tField.getValue()).trim();
        }
        String numRefOrigen = "";
        Date fechaLiquidacion = new Date();
        try {

            String fechaLiquidacionStr = poRec.getLocalRefField("L.FECHA.LIQUIDACION").getValue();
            fechaLiquidacion = formatter1.parse(fechaLiquidacionStr);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        String montoME = "";
        String montoMN = "";
        String tipoCambio = poRec.getLocalRefField("L.TIPO.CAMBIO").getValue();

        datosCompraVentaME.setCodConcepto(codConcepto);
        datosCompraVentaME.setCuentaDestino(cuentaDestino);
        datosCompraVentaME.setCuentaOrigen(cuentaOrigen);
        datosCompraVentaME.setFechaLiquidacion(fechaLiquidacion);
        datosCompraVentaME.setInstruccionesPago(instruccionesPago);

        datosCompraVentaME.setTipoCambio(tipoCambio);
        if (poProduct.toUpperCase().equals("LBTRCME")) {
            versionName = "PAYMENT.ORDER,BCI.COMPRA.VENTA.ME";
            montoMN = poRec.getPaymentAmount().getValue();
            montoME = poRec.getLocalRefField("L.MONTO.ORIGEN").getValue();
            numRefOrigen = id;
        } else if (poProduct.toUpperCase().equals("LBTRVME")) {
            versionName = "PAYMENT.ORDER,BCI.API.VENTA.MONEDA.1.0.0";
            String numRefLBTRCV = poRec.getLocalRefField("L.NUM.REF.LBTR").getValue();
            montoMN = poRec.getLocalRefField("L.MONTO.ORIGEN").getValue();
            montoME = poRec.getPaymentAmount().getValue();
            numRefOrigen = poRec.getLocalRefField("L.NUM.REF.ORIGEN").getValue();
            datosCompraVentaME.setNumRefLBTRCV(numRefLBTRCV);
        }
        datosCompraVentaME.setNumRefOrigen(numRefOrigen);
        datosCompraVentaME.setMontoME(montoME);
        datosCompraVentaME.setMontoMN(montoMN);
        integrationLogsRecord.setOut(datosCompraVentaME.toString());
        String firma = "";
        try {
            CompraVenta compraVenta = new CompraVenta();
            if (poProduct.equalsIgnoreCase("LBTRCME")) {
                responseCompraVenta = compraVenta.instruirCompraVentaME(sid, datosCompraVentaME, firma);
            } else if (poProduct.equalsIgnoreCase("LBTRVME")) {
                responseCompraVenta = compraVenta.confirmarCompraVenta(sid, datosCompraVentaME, firma);
            }
            integrationLogsRecord.setIn(responseCompraVenta.toString());

            if (!responseCompraVenta.isOk()) {
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
            statusLBTR = responseCompraVenta.getResult().getEstado();
            numLBTR = responseCompraVenta.getResult().getNumRefLBTR();
            codError = responseCompraVenta.getResult().getCodError();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        SynchronousTransactionData txnData = new SynchronousTransactionData();
        String numAuth0 = "0";
        String numAuth1 = "1";
        String numAuth2 = "2";

        if (statusLBTR == null && codError != null) {
            String msgError = responseCompraVenta.getResult().getMensajeError();
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
