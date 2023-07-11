package com.bci;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.techmill.integration.operaciones.Anulacion;
import com.techmill.integration.operaciones.anulacion.ResponseAnulacion;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebbcibcrpcredentials.EbBciBcrpCredentialsRecord;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsRecord;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsTable;
import com.temenos.t24.api.tables.ebbcibcrppaymentorderdevolucion.EbBciBcrpPaymentOrderDevolucionRecord;
import com.temenos.t24.api.tables.ebbcibcrppaymentorderdevolucion.EbBciBcrpPaymentOrderDevolucionTable;

/**
 * @author Andrea Vaca H.
 * @mail andrea.vaca@nagarro.com
 *
 *       ----------------------------------------------------------------------------------------------------------------
 *       Description : Rutina para copiar toda la información a una tabla local
 * 
 *       Developed By : Andrea Vaca
 *
 *       Development Reference :
 *
 *       Attached To : EB.API
 *
 *       Attached As : Input Routine
 *
 *       -----------------------------------------------------------------------------------------------------------------
 *       M O D I F I C A T I O N S ***************************
 *       -----------------------------------------------------------------------------------------------------------------
 * 
 *       -----------------------------------------------------------------------------------------------------------------
 *
 *       -----------------------------------------------------------------------------------------------------------------
 */

public class BciUpdateTransferencias extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        String currFunction = transactionContext.getCurrentFunction().toString();
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);
        EbBciBcrpPaymentOrderDevolucionRecord poRecDev = new EbBciBcrpPaymentOrderDevolucionRecord();
        EbBciBcrpPaymentOrderDevolucionTable poRecTable = new EbBciBcrpPaymentOrderDevolucionTable(this);

        if (!currFunction.equals("DELETE")) {
            return poRec.getValidationResponse();
        }
        String paymentOrder = poRec.getPaymentOrderProduct().getValue();
        String codeConcept = poRec.getLocalRefField("L.CODE.CONCEPT").getValue();
        String debitAcct = poRec.getDebitAccount().getValue();
        String debitCcy = poRec.getDebitCcy().getValue();
        String debitValueDate = poRec.getDebitValueDate().getValue();
        String payCcy = poRec.getPaymentCurrency().getValue();
        String payAmount = poRec.getPaymentAmount().getValue();
        String payExDate = poRec.getPaymentExecutionDate().getValue();
        String benId = poRec.getBeneficiaryId().getValue();
        String cciDesti = poRec.getLocalRefField("L.CCI.DESTINATION").getValue();
        String benName = poRec.getBeneficiaryName().getValue();
        String dirBen = poRec.getLocalRefField("L.DIRECCION.BENEFICIARIO").getValue();
        String tipoDocBen = poRec.getLocalRefField("L.TIPODOC.BENEFICIARIO").getValue();
        String numDocBen = poRec.getLocalRefField("L.NUM.DOC.BENEFICIARIO").getValue();
        String sameOwner = poRec.getLocalRefField("L.SAME.OWNER").getValue();
        String clientId = poRec.getOrderingCustomer().getValue();
        String cciCodeOrig = poRec.getLocalRefField("L.CCI.CODE.ORIG").getValue();
        String ordeCustName = poRec.getOrderingCustName().getValue();
        String ordeCustAdd = poRec.getOrderingPostAddrLine(1).getValue();
        String tipoDocOrde = poRec.getLocalRefField("L.TIPODOC.ORDENANTE").getValue();
        String numDocOrde = poRec.getLocalRefField("L.NUM.DOC.ORDENANTE").getValue();
        String montoOrigen = poRec.getLocalRefField("L.MONTO.ORIGEN").getValue();
        String tipoCambio = poRec.getLocalRefField("L.TIPO.CAMBIO").getValue();
        String creditAcc = poRec.getCreditNostroAccount().getValue();
        String codeBankOrig = poRec.getLocalRefField("L.COD.BANK.ORIGEN").getValue();
        String desActExt = poRec.getLocalRefField("L.DESC.ACT.EXTERNO").getValue();
        String destBankCode = poRec.getLocalRefField("L.CTA.MA.BANK.REC").getValue();
        String ctaNoCtaEx = poRec.getLocalRefField("L.CTA.NO.CTA.EXT").getValue();
        String numRefLbtr = poRec.getLocalRefField("L.NUM.REF.LBTR").getValue();
        String fechaLiquida = poRec.getLocalRefField("L.FECHA.LIQUIDACION").getValue();
        String hourLiquidacion = poRec.getLocalRefField("L.HORA.LIQUIDACION").getValue();
        String lbtrEstado = poRec.getLocalRefField("L.ESTADO").getValue();
        String lbtrCodeError = poRec.getLocalRefField("L.CODE.ERROR").getValue();
        String lbtrMsgError = poRec.getLocalRefField("L.ERROR.MSG").getValue();
        String lbtrEstadoAcred = poRec.getLocalRefField("L.ESTADO.ABONO").getValue();
        String codeSab = poRec.getLocalRefField("L.CODE.SAB").getValue();
        String chargeAcct = poRec.getChargeAccount().getValue();

        poRecDev.setPaymentOrderProduct(paymentOrder);
        poRecDev.setLCodeConcept(codeConcept);
        poRecDev.setDebitAccount(debitAcct);
        poRecDev.setDebitCcy(debitCcy);
        poRecDev.setDebitValueDate(debitValueDate);
        poRecDev.setPaymentCurrency(payCcy);
        poRecDev.setPaymentAmount(payAmount);
        poRecDev.setPaymentExecutionDate(payExDate);
        poRecDev.setBeneficiaryId(benId);
        poRecDev.setCciDestination(cciDesti);
        poRecDev.setBeneficiaryName(benName);
        poRecDev.setLDirBenefic(dirBen);
        poRecDev.setLTipoDocBen(tipoDocBen);
        poRecDev.setLNumDocBenef(numDocBen);
        poRecDev.setSameOwner(sameOwner);
        poRecDev.setOrderingCustomer(clientId);
        poRecDev.setLCciCodeOrig(cciCodeOrig);
        poRecDev.setOrderingCustName(ordeCustName);
        poRecDev.setLTipodocOrdern(tipoDocOrde);
        poRecDev.setLNumDocOrden(numDocOrde);
        poRecDev.setLMontoOrigen(montoOrigen);
        poRecDev.setLTipoCambio(tipoCambio);
        poRecDev.setCreditNosroAccount(creditAcc);
        poRecDev.setLCodBankOrig(codeBankOrig);
        poRecDev.setLDescActExte(desActExt);
        poRecDev.setLCtaMaBankRec(destBankCode);
        poRecDev.setLCtaNoCtaEx(ctaNoCtaEx);
        poRecDev.setLNumRefLbtr(numRefLbtr);
        poRecDev.setLFechaLiquida(fechaLiquida);
        poRecDev.setOrderingPostAddrLine(ordeCustAdd, 1);
        poRecDev.setChargeAccount(chargeAcct);
        poRecDev.setLHoraLiquidacion(hourLiquidacion);
        poRecDev.setLEstado(lbtrEstado);
        poRecDev.setLCodeError(lbtrCodeError);
        poRecDev.setLEstadoAbono(lbtrEstadoAcred);
        poRecDev.setLErrorMsg(lbtrMsgError);
        poRecDev.setLCodeSab(codeSab);

        Anulacion anulacion = new Anulacion();
        DataAccess da = new DataAccess(this);
        EbBciBcrpLIntegrationLogsRecord integrationLogsRecord = new EbBciBcrpLIntegrationLogsRecord(this);
        EbBciBcrpLIntegrationLogsTable integrationLogsTable = new EbBciBcrpLIntegrationLogsTable(this);
        Date date = new Date();
        SimpleDateFormat dateformtfch = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateformthora1 = new SimpleDateFormat("HH:mm:ss");
        String myfecha = dateformtfch.format(date);
        String myHora1 = dateformthora1.format(date);
        integrationLogsRecord.setFecha(myfecha);
        integrationLogsRecord.setHora(myHora1);
        integrationLogsRecord.setAppName("ANULACION");

        EbBciBcrpCredentialsRecord bciCredentRec = new EbBciBcrpCredentialsRecord(
                da.getRecord("EB.BCI.BCRP.CREDENTIALS", "SYSTEM"));
        String sidStatus = bciCredentRec.getSidStatus().getValue();
        if (sidStatus.equals("NoActivo")) {
            poRec.getPaymentOrderProduct().setError("Sesion LBTR no se encuentra Activo");
            return poRec.getValidationResponse();
        }
        String sid = bciCredentRec.getSid().getValue();
        String firma = "6A8E36D0397BAA592EC21F";
        String estado = "";
        String rspNumRefLbtr = "";
        integrationLogsRecord.setOut(numRefLbtr);

        try {
            ResponseAnulacion responseAnulacion = anulacion.anular(sid, numRefLbtr, firma);
            integrationLogsRecord.setIn(responseAnulacion.toString());
            if (responseAnulacion.isOk()) {
                estado = responseAnulacion.getResult().getEstado();
            } else {
                integrationLogsRecord.setFlagErr("YES");
                integrationLogsTable.write("ANULACION-" + currentRecordId, integrationLogsRecord);
                return poRec.getValidationResponse();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        poRecDev.setLEstado(estado);

        try {
            poRecTable.write(currentRecordId, poRecDev);
            integrationLogsTable.write("ANULACION-" + currentRecordId, integrationLogsRecord);
        } catch (T24IOException e) {
            System.out.println(e.getMessage());
        }
        return poRec.getValidationResponse();
    }
}