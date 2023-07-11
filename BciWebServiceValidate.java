package com.bci;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.RandomStringUtils;

import com.techmill.integration.cw.AgentesBolsa;
import com.techmill.integration.cw.Conceptos;
import com.techmill.integration.cw.Cuentas;
import com.techmill.integration.cw.Entidades;
import com.techmill.integration.cw.FacilidadesDisponibles;
import com.techmill.integration.cw.Facturacion;
import com.techmill.integration.cw.Monedas;
import com.techmill.integration.cw.Movimientos;
import com.techmill.integration.cw.OperacionesCvme;
import com.techmill.integration.cw.OperacionesOtorgadas;
import com.techmill.integration.cw.OperacionesRecibidas;
import com.techmill.integration.cw.Saldos;
import com.techmill.integration.cw.Tarifas;
import com.techmill.integration.cw.TipoDeCambio;
import com.techmill.integration.cw.agentesBolsa.Agente;
import com.techmill.integration.cw.agentesBolsa.RootAgentesBolsa;
import com.techmill.integration.cw.conceptos.Concepto;
import com.techmill.integration.cw.conceptos.RootConceptos;
import com.techmill.integration.cw.cuentas.Cuenta;
import com.techmill.integration.cw.cuentas.RootCuentas;
import com.techmill.integration.cw.entidades.Entidad;
import com.techmill.integration.cw.entidades.RootEntidades;
import com.techmill.integration.cw.facilidadesDisponibles.FacilidadDisponible;
import com.techmill.integration.cw.facilidadesDisponibles.RootFacilidadesDisponibles;
import com.techmill.integration.cw.facturacion.Detalle;
import com.techmill.integration.cw.facturacion.RootFacturacion;
import com.techmill.integration.cw.moneda.Moneda;
import com.techmill.integration.cw.moneda.RootMonedas;
import com.techmill.integration.cw.movimientos.Movimiento;
import com.techmill.integration.cw.movimientos.RootMovimientos;
import com.techmill.integration.cw.operacionesCvme.OperacionCvme;
import com.techmill.integration.cw.operacionesCvme.RootOperacionesCvme;
import com.techmill.integration.cw.operacionesOtorgadas.OperacionOtorgada;
import com.techmill.integration.cw.operacionesOtorgadas.RootOperacionesOtorgadas;
import com.techmill.integration.cw.operacionesRecibidas.Operaciones;
import com.techmill.integration.cw.operacionesRecibidas.RootOperacionesRecibidas;
import com.techmill.integration.cw.saldos.Result;
import com.techmill.integration.cw.saldos.RootSaldos;
import com.techmill.integration.cw.tarifas.RootTarifas;
import com.techmill.integration.cw.tarifas.Tarifa;
import com.techmill.integration.cw.tipoDeCambio.RootTipodeCambio;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.complex.eb.templatehook.TransactionData;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebbcibcrpconsultasselect.EbBciBcrpConsultasSelectRecord;
import com.temenos.t24.api.tables.ebbcibcrpcredentials.EbBciBcrpCredentialsRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwaccount.CodeEntityClass;
import com.temenos.t24.api.tables.ebbcibcrpcwaccount.EbBciBcrpCwAccountRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwaccount.EbBciBcrpCwAccountTable;
import com.temenos.t24.api.tables.ebbcibcrpcwassignedoperations.CodeArrivalBankClass;
import com.temenos.t24.api.tables.ebbcibcrpcwassignedoperations.EbBciBcrpCwAssignedOperationsRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwassignedoperations.EbBciBcrpCwAssignedOperationsTable;
import com.temenos.t24.api.tables.ebbcibcrpcwassignedoperations.OrderCciClass;
import com.temenos.t24.api.tables.ebbcibcrpcwavailablefacilities.CodeFacilityClass;
import com.temenos.t24.api.tables.ebbcibcrpcwavailablefacilities.EbBciBcrpCwAvailableFacilitiesRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwavailablefacilities.EbBciBcrpCwAvailableFacilitiesTable;
import com.temenos.t24.api.tables.ebbcibcrpcwbalance.EbBciBcrpCwBalanceRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwbalance.EbBciBcrpCwBalanceTable;
import com.temenos.t24.api.tables.ebbcibcrpcwchangetype.EbBciBcrpCwChangeTypeRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwchangetype.EbBciBcrpCwChangeTypeTable;
import com.temenos.t24.api.tables.ebbcibcrpcwcompraventa.EbBciBcrpCwCompraVentaRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwcompraventa.EbBciBcrpCwCompraVentaTable;
import com.temenos.t24.api.tables.ebbcibcrpcwconcept.CodeConceptClass;
import com.temenos.t24.api.tables.ebbcibcrpcwconcept.EbBciBcrpCwConceptRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwconcept.EbBciBcrpCwConceptTable;
import com.temenos.t24.api.tables.ebbcibcrpcwcurrency.CodeCurrencyClass;
import com.temenos.t24.api.tables.ebbcibcrpcwcurrency.EbBciBcrpCwCurrencyRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwcurrency.EbBciBcrpCwCurrencyTable;
import com.temenos.t24.api.tables.ebbcibcrpcwentity.EbBciBcrpCwEntityRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwentity.EbBciBcrpCwEntityTable;
import com.temenos.t24.api.tables.ebbcibcrpcwentity.EntityCodeClass;
import com.temenos.t24.api.tables.ebbcibcrpcwfare.CodeFareClass;
import com.temenos.t24.api.tables.ebbcibcrpcwfare.EbBciBcrpCwFareRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwfare.EbBciBcrpCwFareTable;
import com.temenos.t24.api.tables.ebbcibcrpcwmovements.CodeEntryClass;
import com.temenos.t24.api.tables.ebbcibcrpcwmovements.EbBciBcrpCwMovementsRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwmovements.EbBciBcrpCwMovementsTable;
import com.temenos.t24.api.tables.ebbcibcrpcwreceivedoperations.EbBciBcrpCwReceivedOperationsRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwreceivedoperations.EbBciBcrpCwReceivedOperationsTable;
import com.temenos.t24.api.tables.ebbcibcrpcwstockbrokers.EbBciBcrpCwStockbrokersRecord;
import com.temenos.t24.api.tables.ebbcibcrpcwstockbrokers.EbBciBcrpCwStockbrokersTable;
import com.temenos.t24.api.tables.ebbcibcrpcwstockbrokers.SabCodeClass;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsRecord;
import com.temenos.t24.api.tables.ebbcibcrplintegrationlogs.EbBciBcrpLIntegrationLogsTable;
import com.temenos.t24.api.tables.ebbcibcrpparam.EbBciBcrpParamRecord;
import com.temenos.t24.api.tables.ebbcibcrpparam.PaymentOrderProductClass;
import com.temenos.t24.api.tables.ebbciccebcrpcwfeecollection.ConceptCodeClass;
import com.temenos.t24.api.tables.ebbciccebcrpcwfeecollection.EbBciCceBcrpCwFeeCollectionRecord;
import com.temenos.t24.api.tables.ebbciccebcrpcwfeecollection.EbBciCceBcrpCwFeeCollectionTable;

/**
 * TODO: Document me!
 *
 * @author Diego Maigualca
 *
 */
public class BciWebServiceValidate extends RecordLifecycle {

    @Override
    public void updateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext,
            List<TransactionData> transactionData, List<TStructure> currentRecords) {

        String nameConsult = "";
        String institucionId = "";
        String numAccount = "";
        String dateWc = "";
        String sid = "";

        String nameWCLog = "";
        String userId = "";

        try {
            Session ss = new Session(this);
            userId = ss.getUserId();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        EbBciBcrpLIntegrationLogsRecord integrationLogsRecord = new EbBciBcrpLIntegrationLogsRecord(this);
        EbBciBcrpLIntegrationLogsTable integrationLogsTable = new EbBciBcrpLIntegrationLogsTable(this);

        SimpleDateFormat dateformtfch = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateformthora = new SimpleDateFormat("HHmmss");
        SimpleDateFormat dateformthora1 = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String myfecha = dateformtfch.format(date);
        String myHora = dateformthora.format(date);
        String myHora1 = dateformthora1.format(date);

        String mydate = myfecha + "-" + myHora;
        integrationLogsRecord.setFecha(myfecha);
        integrationLogsRecord.setHora(myHora1);

        final EbBciBcrpCwConceptTable ebBciBcrpCwConceptTable = new EbBciBcrpCwConceptTable(this);
        // final EbBciBcrpC ebConsultasTable = new
        // EbBciBcrpCwStockbrokersTable(this);
        final EbBciBcrpConsultasSelectRecord ebBciBcrpConsultasSelectRecord = new EbBciBcrpConsultasSelectRecord(
                currentRecord);
        nameConsult = ebBciBcrpConsultasSelectRecord.getType().getValue();
        institucionId = ebBciBcrpConsultasSelectRecord.getInstitucionId().getValue();

        DataAccess da = new DataAccess(this);

        EbBciBcrpCredentialsRecord credentialsRecord = new EbBciBcrpCredentialsRecord(
                da.getRecord("EB.BCI.BCRP.CREDENTIALS", "SYSTEM"));
        sid = credentialsRecord.getSid().getValue();

        if (!sid.equals("")) {
            EbBciBcrpCwConceptRecord ebCwConceptRecord = new EbBciBcrpCwConceptRecord(this);

            try {
                String name = NamesWCEnum.STOCKBROKERS.getText();
                System.out.println(name);
                if (nameConsult.equals(NamesWCEnum.STOCKBROKERS.getText())) {
                    nameWCLog = "WCSTOCKBROKERS";
                    ArrayList<Agente> listAgente = new ArrayList<Agente>();
                    EbBciBcrpCwStockbrokersRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwStockbrokersRecord(this);
                    final EbBciBcrpCwStockbrokersTable ebConsultasTable = new EbBciBcrpCwStockbrokersTable(this);
                    int cont = 0;
                    RootAgentesBolsa roBolsa = new RootAgentesBolsa();
                    final AgentesBolsa agBolsaInteg = new AgentesBolsa();

                    try {

                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord.setOut("sid=" + sid);
                        integrationLogsRecord.setTxId("SYSTEM");

                        roBolsa = agBolsaInteg.processRequest("sid=" + sid);
                        integrationLogsRecord.setIn(roBolsa.toString());

                        if (roBolsa.isOk()) {
                            listAgente = (ArrayList<Agente>) roBolsa.getResult().getAgentes();
                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                    try {
                        for (final Agente agente : listAgente) {
                            final SabCodeClass saClass = new SabCodeClass();
                            saClass.setSabCode(agente.getCodigoSAB());
                            saClass.setBusinessName(agente.getRazonSocial());
                            ebBciBcrpConsultasRecord.setSabCode(saClass, cont);

                            ebConsultasTable.write(agente.getCodigoSAB(), ebBciBcrpConsultasRecord);

                        }

                    } catch (Exception e) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e.getMessage());
                    }

                } else if (nameConsult.equals(NamesWCEnum.CONCEPTS.getText())) {
                    nameWCLog = "WCCONCEPTS";
                    int cont = 0;
                    RootConceptos conceptos = new RootConceptos();
                    final Conceptos conceptosInteg = new Conceptos();
                    ArrayList<Concepto> listConcept = new ArrayList<Concepto>();
                    try {
                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord.setOut("sid=" + sid);
                        integrationLogsRecord.setTxId("SYSTEM");

                        conceptos = conceptosInteg.processRequest(sid);
                        integrationLogsRecord.setIn(conceptos.toString());
                        if (conceptos.isOk()) {

                            listConcept = (ArrayList<Concepto>) conceptos.getResult().getConceptos();
                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                    try {

                        for (final Concepto conceptoStr : listConcept) {
                            final CodeConceptClass codeConceptClass = new CodeConceptClass();
                            codeConceptClass.setCodeConcept(conceptoStr.getCodConcepto());
                            codeConceptClass.setCodeCurrency(conceptoStr.getCodMoneda());
                            codeConceptClass.setDescription(conceptoStr.getDesConcepto());
                            codeConceptClass.setStatus(conceptoStr.getEstado());
                            codeConceptClass.setMinMount(conceptoStr.getMontoMinimo());
                            codeConceptClass.setOrigin(conceptoStr.getOrigen());
                            codeConceptClass.setService(conceptoStr.getServicio());

                            ebCwConceptRecord.setCodeConcept(codeConceptClass, cont);

                            ebBciBcrpCwConceptTable.write(conceptoStr.getCodConcepto(), ebCwConceptRecord);
                        }
                    } catch (Exception e) {

                    }

                } else if (nameConsult.equals(NamesWCEnum.ENTITIES.getText())) {
                    nameWCLog = "WCENTITIES";

                    ArrayList<Entidad> listEntidad = new ArrayList<Entidad>();

                    EbBciBcrpCwEntityRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwEntityRecord(this);
                    final EbBciBcrpCwEntityTable ebConsultasTable = new EbBciBcrpCwEntityTable(this);
                    int cont = 0;
                    RootEntidades roEntidades = new RootEntidades();
                    final Entidades EntidadesInteg = new Entidades();

                    try {

                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord.setOut("sid=" + sid);
                        integrationLogsRecord.setTxId("SYSTEM");

                        roEntidades = EntidadesInteg.processRequest(sid);
                        integrationLogsRecord.setIn(roEntidades.toString());

                        if (roEntidades.isOk()) {
                            listEntidad = roEntidades.getResult().getEntidades();
                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                    try {

                        for (final Entidad entidad : listEntidad) {
                            final EntityCodeClass entityCodeClass = new EntityCodeClass();
                            entityCodeClass.setEntityCode((CharSequence) entidad.getCodEntidad());
                            entityCodeClass.setShortName((CharSequence) entidad.getNomCortoEntidad());
                            entityCodeClass.setName(entidad.getNomLargoEntidad());
                            ebBciBcrpConsultasRecord.setEntityCode(entityCodeClass, cont);
                            ebConsultasTable.write(entidad.getCodEntidad(), ebBciBcrpConsultasRecord);
                        }
                    } catch (Exception e) {

                    }

                } else if (nameConsult.equals(NamesWCEnum.CURRENCIES.getText())) {
                    nameWCLog = "WCCURRENCIES";

                    ArrayList<Moneda> listMoneda = new ArrayList<Moneda>();

                    EbBciBcrpCwCurrencyRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwCurrencyRecord(this);
                    final EbBciBcrpCwCurrencyTable ebConsultasTable = new EbBciBcrpCwCurrencyTable(this);
                    int cont = 0;
                    RootMonedas roMonedas = new RootMonedas();
                    final Monedas MonedasInteg = new Monedas();

                    try {

                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord.setOut("sid=" + sid);
                        integrationLogsRecord.setTxId("SYSTEM");
                        roMonedas = MonedasInteg.processRequest(sid);
                        if (roMonedas.isOk()) {
                            listMoneda = roMonedas.getResult().getMonedas();
                        }
                    } catch (Exception e2) {
                        System.out.println(e2.getMessage());
                    }

                    try {

                        for (final Moneda moneda : listMoneda) {
                            final CodeCurrencyClass currencyCodeClass = new CodeCurrencyClass();
                            currencyCodeClass.setCodeCurrency((CharSequence) moneda.getCodMoneda());
                            currencyCodeClass.setDescription((CharSequence) moneda.getDesMoneda());
                            currencyCodeClass.setSymbol(moneda.getSimbolo());
                            ebBciBcrpConsultasRecord.setCodeCurrency(currencyCodeClass, cont);

                            ebConsultasTable.write(moneda.getCodMoneda(), ebBciBcrpConsultasRecord);

                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                } else if (nameConsult.equals(NamesWCEnum.CTASCTES.getText())) {
                    nameWCLog = "WCCTASCTES";
                    ArrayList<Cuenta> listCuenta = new ArrayList<Cuenta>();

                    EbBciBcrpCwAccountRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwAccountRecord(this);
                    final EbBciBcrpCwAccountTable ebConsultasTable = new EbBciBcrpCwAccountTable(this);
                    int cont = 0;
                    RootCuentas roAccount = new RootCuentas();
                    final Cuentas CuentasInteg = new Cuentas();

                    try {
                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord.setOut("sid=" + sid + "intitutionId:" + institucionId);
                        integrationLogsRecord.setTxId("SYSTEM");

                        roAccount = CuentasInteg.processRequest(sid, institucionId);
                        integrationLogsRecord.setIn(roAccount.toString());

                        if (roAccount.isOk()) {
                            listCuenta = roAccount.getResult().getCuentas();
                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }
                    try {

                        for (final Cuenta cuenta : listCuenta) {
                            final CodeEntityClass accountCodeClass = new CodeEntityClass();

                            accountCodeClass.setCodeEntity(cuenta.getCodEntidad());
                            accountCodeClass.setCodeCurrency(cuenta.getCodMoneda());
                            accountCodeClass.setName(cuenta.getNombreCuenta());
                            accountCodeClass.setNumAccount(cuenta.getNumCuenta());
                            accountCodeClass.setDivT24(cuenta.getCodDivisa());
                            ebBciBcrpConsultasRecord.setCodeEntity(accountCodeClass, cont);

                            ebConsultasTable.write(cuenta.getCodEntidad() + cuenta.getCodMoneda(), ebBciBcrpConsultasRecord);

                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                } else if (nameConsult.equals(NamesWCEnum.SALES.getText())) {
                    nameWCLog = "WCSALES";

                    Result saldosResult = new Result();
                    EbBciBcrpCwBalanceRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwBalanceRecord(this);
                    final EbBciBcrpCwBalanceTable ebConsultasTable = new EbBciBcrpCwBalanceTable(this);
                    int cont = 0;
                    String fecha = "";
                    RootSaldos roSaldos = new RootSaldos();
                    final Saldos saldosInteg = new Saldos();

                    numAccount = ebBciBcrpConsultasSelectRecord.getNumCuenta().getValue();
                    dateWc = ebBciBcrpConsultasSelectRecord.getFecha().getValue();
                    try {
                        Date date1 = new SimpleDateFormat("yyyyMMdd").parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    } catch (Exception e) {
                        Date date1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    }

                    try {

                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord
                                .setOut("sid:" + sid.concat("numAccount: " + numAccount.concat("fecha: " + fecha)));
                        integrationLogsRecord.setTxId("SYSTEM");

                        roSaldos = saldosInteg.processRequest(sid, numAccount, fecha);
                        integrationLogsRecord.setIn(roSaldos.toString());
                        if (roSaldos.isOk()) {
                            com.temenos.t24.api.tables.ebbcibcrpcwbalance.CodeEntityClass entityCodeClass = new com.temenos.t24.api.tables.ebbcibcrpcwbalance.CodeEntityClass();
                            try {
                                saldosResult = roSaldos.getResult();
                            } catch (Exception e) {

                            }

                            entityCodeClass.setCodeEntity(saldosResult.getCodEntidad());
                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                                String dateTipCam = simpleDateFormat.format(saldosResult.getFechaSaldo());
                                entityCodeClass.setBalanceDate(dateTipCam);

                            } catch (Exception e) {

                            }
                            entityCodeClass.setAcctNumber(saldosResult.getNumCuenta());
                            entityCodeClass.setActualBalance(saldosResult.getSaldoActual());
                            entityCodeClass.setInitialBalance(saldosResult.getSaldoInicial());
                            entityCodeClass.setTotalEntry(saldosResult.getTotalAbonos());
                            entityCodeClass.setTotalCharge(saldosResult.getTotalCargos());
                            ebBciBcrpConsultasRecord.setCodeEntity(entityCodeClass, cont);
                            try {
                                ebConsultasTable.write(saldosResult.getCodEntidad(), ebBciBcrpConsultasRecord);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                } else if (nameConsult.equals(NamesWCEnum.FCOLLECTION.getText())) {

                    nameWCLog = "WCFCOLLECTION";
                    EbBciCceBcrpCwFeeCollectionRecord ebBciBcrpConsultasRecord = new EbBciCceBcrpCwFeeCollectionRecord(
                            this);
                    final EbBciCceBcrpCwFeeCollectionTable ebConsultasTable = new EbBciCceBcrpCwFeeCollectionTable(
                            this);
                    int cont = 0;
                    String fecha = "";

                    String periodo = "";
                    com.techmill.integration.cw.facturacion.Result result = new com.techmill.integration.cw.facturacion.Result();
                    ArrayList<Detalle> lstDetalles = new ArrayList<>();
                    RootFacturacion roFacturacion = new RootFacturacion();
                    final Facturacion tarifasInteg = new Facturacion();

                    numAccount = ebBciBcrpConsultasSelectRecord.getNumCuenta().getValue();
                    dateWc = ebBciBcrpConsultasSelectRecord.getFecha().getValue();
                    periodo = ebBciBcrpConsultasSelectRecord.getPeriodo().getValue();

                    try {
                        Date date1 = new SimpleDateFormat("yyyyMMdd").parse(periodo);
                        DateFormat df = new SimpleDateFormat("yyyyMMdd");
                        fecha = df.format(date1);
                    } catch (Exception e) {
                        Date date1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(periodo);
                        DateFormat df = new SimpleDateFormat("yyyyMMdd");
                        fecha = df.format(date1);
                    }

                    try {
                        periodo = fecha;
                        periodo = periodo.substring(0, 6);
                    } catch (Exception e) {

                    }

                    try {
                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord.setOut("sid: " + sid.concat("periodo: " + periodo));
                        integrationLogsRecord.setTxId("SYSTEM");

                        roFacturacion = tarifasInteg.processRequest(sid, periodo);
                        integrationLogsRecord.setIn(roFacturacion.toString());
                        if (roFacturacion.isOk()) {
                            ConceptCodeClass codeClass = new ConceptCodeClass();

                            try {
                                result = roFacturacion.getResult();

                                ebBciBcrpConsultasRecord.setEntityCode(result.getCodEntidad());
                                ebBciBcrpConsultasRecord.setPeriod(result.getPeriodo());
                                ebBciBcrpConsultasRecord.setTotalPeriod(result.getTotalPeriodo());
                                ebBciBcrpConsultasRecord.setTotalFixed(result.getTotalFijo());
                                ebBciBcrpConsultasRecord.setTotalVariablle(result.getTotalVariable());

                                lstDetalles = result.getDetalles();
                            } catch (Exception e) {

                            }

                            try {

                                for (Detalle detalle : lstDetalles) {
                                    codeClass = new ConceptCodeClass();
                                    codeClass.setConceptCode(detalle.getCodConcepto());
                                    codeClass.setCodFare(detalle.getCodTarifa());

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                                    String dateTipCam = simpleDateFormat.format(detalle.getFecha());
                                    codeClass.setDate(dateTipCam);

                                    codeClass.setMountRate(detalle.getMontoTarifa());
                                    codeClass.setNumberOperation(detalle.getNumOperaciones());
                                    codeClass.setTypeCollection(detalle.getTipoCobro());
                                    codeClass.setTotalConceptRate(detalle.getTotalConceptoTarifa());

                                }
                            } catch (Exception e) {

                            }

                            ebBciBcrpConsultasRecord.setConceptCode(codeClass, cont);
                            try {
                                if (!(result == null)) {
                                    ebConsultasTable.write(result.getCodEntidad(), ebBciBcrpConsultasRecord);
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                } else if (nameConsult.equals(NamesWCEnum.TARIFARIO.getText())) {
                    nameWCLog = "WCTARIFARIO";

                    EbBciBcrpCwFareRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwFareRecord(this);
                    final EbBciBcrpCwFareTable ebConsultasTable = new EbBciBcrpCwFareTable(this);
                    int cont = 0;

                    com.techmill.integration.cw.tarifas.Result result = new com.techmill.integration.cw.tarifas.Result();
                    ArrayList<Tarifa> lstTarifa = new ArrayList<>();
                    RootTarifas rootIntegration = new RootTarifas();
                    final Tarifas classInteg = new Tarifas();

                    try {
                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord.setOut("sid=" + sid);
                        integrationLogsRecord.setTxId("SYSTEM");

                        rootIntegration = classInteg.processRequest(sid);
                        integrationLogsRecord.setIn(rootIntegration.toString());

                        if (rootIntegration.isOk()) {

                            result = rootIntegration.getResult();
                            try {
                                lstTarifa = result.getTarifas();

                            } catch (Exception e) {

                            }
                            try {

                                for (Tarifa tarifa : lstTarifa) {
                                    final CodeFareClass codeFare = new CodeFareClass();
                                    codeFare.setCodeFare(tarifa.getCodTarifa());
                                    codeFare.setFixedCost(tarifa.getCostoFijo());
                                    codeFare.setFinalHour(tarifa.getHoraFinal());
                                    codeFare.setStartHour(tarifa.getHoraInicio());
                                    codeFare.setEntryIndex(tarifa.getIndAbono());
                                    codeFare.setChargeIndex(tarifa.getIndCargo());
                                    codeFare.setAppliedFare(tarifa.getTarifaAplicada());

                                    ebBciBcrpConsultasRecord.setCodeFare(codeFare, cont);

                                    ebConsultasTable.write(tarifa.getCodTarifa(), ebBciBcrpConsultasRecord);

                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                } else if (nameConsult.equals(NamesWCEnum.FDISPONIBLES.getText())) {
                    nameWCLog = "WCFDISPONIBLES";

                    EbBciBcrpCwAvailableFacilitiesRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwAvailableFacilitiesRecord(
                            this);
                    final EbBciBcrpCwAvailableFacilitiesTable ebConsultasTable = new EbBciBcrpCwAvailableFacilitiesTable(
                            this);
                    int cont = 0;

                    com.techmill.integration.cw.facilidadesDisponibles.Result result = new com.techmill.integration.cw.facilidadesDisponibles.Result();
                    ArrayList<FacilidadDisponible> lstFDisponibles = new ArrayList<>();
                    RootFacilidadesDisponibles rootIntegration = new RootFacilidadesDisponibles();
                    final FacilidadesDisponibles classInteg = new FacilidadesDisponibles();

                    try {
                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord.setOut("sid=" + sid);
                        integrationLogsRecord.setTxId("SYSTEM");

                        rootIntegration = classInteg.processRequest(sid);
                        integrationLogsRecord.setIn(rootIntegration.toString());

                        if (rootIntegration.isOk()) {

                            try {
                                result = rootIntegration.getResult();
                                lstFDisponibles = result.getFacilidades();
                            } catch (Exception e) {

                            }
                            try {

                                for (FacilidadDisponible facilidadDisponible : lstFDisponibles) {
                                    final CodeFacilityClass codeFacility = new CodeFacilityClass();
                                    codeFacility.setCodeFacility(facilidadDisponible.getCodFacilidad());
                                    codeFacility.setCodeCurrency(facilidadDisponible.getCodMoneda());
                                    codeFacility.setDescription(facilidadDisponible.getDesFacilidad());

                                    ebBciBcrpConsultasRecord.setCodeFacility(codeFacility, cont);

                                    ebConsultasTable.write(facilidadDisponible.getCodFacilidad(), ebBciBcrpConsultasRecord);

                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                } else if (nameConsult.equals(NamesWCEnum.MCTASCTES.getText())) {
                    nameWCLog = "WCMCTASCTES";

                    EbBciBcrpCwMovementsRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwMovementsRecord(this);
                    final EbBciBcrpCwMovementsTable ebConsultasTable = new EbBciBcrpCwMovementsTable(this);
                    int cont = 0;
                    String fecha = "";

                    numAccount = ebBciBcrpConsultasSelectRecord.getNumCuenta().getValue();
                    dateWc = ebBciBcrpConsultasSelectRecord.getFecha().getValue();
                    try {
                        Date date1 = new SimpleDateFormat("yyyyMMdd").parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    } catch (Exception e) {
                        Date date1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    }

                    com.techmill.integration.cw.movimientos.Result result = new com.techmill.integration.cw.movimientos.Result();
                    ArrayList<Movimiento> lstMovimientos = new ArrayList<>();
                    RootMovimientos rootIntegration = new RootMovimientos();
                    final Movimientos classInteg = new Movimientos();

                    try {
                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord
                                .setOut("fecha:" + fecha.concat(" " + "sid=" + sid.concat("numAccount" + numAccount)));
                        integrationLogsRecord.setTxId("SYSTEM");

                        rootIntegration = classInteg.processRequest(fecha, sid, numAccount);
                        integrationLogsRecord.setIn(rootIntegration.toString());
                        if (rootIntegration.isOk()) {

                            try {
                                result = rootIntegration.getResult();
                                lstMovimientos = result.getMovimientos();
                            } catch (Exception e) {

                            }
                            try {

                                for (Movimiento movimiento : lstMovimientos) {
                                    final CodeEntryClass codeEntryClass = new CodeEntryClass();
                                    codeEntryClass.setCodeEntry(movimiento.getCodCargoAbono());
                                    codeEntryClass.setCodeConcept(movimiento.getCodConcepto());
                                    codeEntryClass.setCodeEntity(movimiento.getCodEntidad());
                                    codeEntryClass.setCodeEntityCp(movimiento.getCodEntidadCP());
                                    codeEntryClass.setCodeCurrency(movimiento.getCodMoneda());

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                                    String dateTipCam = simpleDateFormat.format(movimiento.getFechaLiquidacion());
                                    codeEntryClass.setSettlementDate(dateTipCam);

                                    codeEntryClass.setPaymentInstructions(movimiento.getInstruccionesPago());
                                    codeEntryClass.setOperationAmount(movimiento.getMontoOperacion());
                                    codeEntryClass.setAcctNumber(movimiento.getNumCuenta());
                                    codeEntryClass.setRefLbtrNum(movimiento.getNumRefLBTR());
                                    codeEntryClass.setChangeType(movimiento.getTipoCambio());

                                    ebBciBcrpConsultasRecord.setCodeEntry(codeEntryClass, cont);

                                    try {
                                        ebConsultasTable.write(movimiento.getNumRefLBTR(), ebBciBcrpConsultasRecord);
                                    } catch (Exception e) {

                                        System.out.println(e.getMessage());
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                } else if (nameConsult.equals("Operaciones Otorgadas")) {
                    nameWCLog = "WCOPOTORGADAS";

                    EbBciBcrpCwAssignedOperationsRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwAssignedOperationsRecord(
                            this);
                    final EbBciBcrpCwAssignedOperationsTable ebConsultasTable = new EbBciBcrpCwAssignedOperationsTable(
                            this);
                    int cont = 0;
                    String fecha = "";
                    String datosCliente = "";

                    dateWc = ebBciBcrpConsultasSelectRecord.getFechaLiquidacion().getValue();
                    try {
                        Date date1 = new SimpleDateFormat("yyyyMMdd").parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    } catch (Exception e) {
                        Date date1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    }

                    com.techmill.integration.cw.operacionesOtorgadas.Result result = new com.techmill.integration.cw.operacionesOtorgadas.Result();
                    ArrayList<OperacionOtorgada> lstOperacionesOtorgadas = new ArrayList<>();
                    RootOperacionesOtorgadas rootIntegration = new RootOperacionesOtorgadas();
                    final OperacionesOtorgadas classInteg = new OperacionesOtorgadas();

                    try {
                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord.setOut("fecha" + fecha.concat(" " + "Sid=" + sid));
                        integrationLogsRecord.setTxId("SYSTEM");

                        rootIntegration = classInteg.processRequest(fecha, sid);
                        integrationLogsRecord.setIn(rootIntegration.toString());
                        if (rootIntegration.isOk()) {

                            try {
                                result = rootIntegration.getResult();
                                lstOperacionesOtorgadas = result.getOperaciones();
                            } catch (Exception e) {

                            }
                            try {

                                for (OperacionOtorgada operacion : lstOperacionesOtorgadas) {
                                    final CodeArrivalBankClass arrivalBankClass = new CodeArrivalBankClass();
                                    arrivalBankClass.setCodeArrivalBank(operacion.getCodBancoDestino());
                                    arrivalBankClass.setCodeOriginBank(operacion.getCodBancoOrigen());
                                    arrivalBankClass.setCodeConcept(operacion.getCodConcepto());
                                    arrivalBankClass.setCodeCurrency(operacion.getCodMoneda());
                                    arrivalBankClass.setAcctArrival(operacion.getCuentaDestino());
                                    arrivalBankClass.setAcctOrigin(operacion.getCuentaOrigen());

                                    datosCliente = operacion.getDatosCliente();
                                    

                                    arrivalBankClass.setSettlementState(operacion.getEstadoLiquidacion());
                                    try {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                                        String dateTipCam = simpleDateFormat.format(operacion.getFechaLiquidacion());
                                        arrivalBankClass.setSettlementDate(dateTipCam);

                                    } catch (Exception e) {

                                    }
                                    arrivalBankClass.setSettlementHour(operacion.getHoraLiquidacion());
                                    arrivalBankClass.setPaymentInstructions(operacion.getInstruccionesPago());
                                    arrivalBankClass.setOperationAmount(operacion.getMontoOperacion());
                                    arrivalBankClass.setRefLbtrNum(operacion.getNumRefLBTR());
                                    arrivalBankClass.setSettlementState(operacion.getEstadoLiquidacion());

                                    try {
                                        String numLbtrPo = "";
                                        String estadoPo = "";
                                        String estadoInte = "";
                                        String versionName = "";
                                        String poProduct = "";
                                        List<PaymentOrderProductClass> listProd = new ArrayList<>();
                                        List<String> lstPo = new ArrayList<>();
                                        TransactionData txnData = new TransactionData();

                                        PaymentOrderRecord poRecord = new PaymentOrderRecord(this);

                                        EbBciBcrpParamRecord bciBcrpParamRecord = new EbBciBcrpParamRecord(this);

                                        try {
                                            bciBcrpParamRecord = new EbBciBcrpParamRecord(
                                                    da.getRecord("EB.BCI.BCRP.PARAM", "SYSTEM"));
                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());
                                        }

                                        listProd = bciBcrpParamRecord.getPaymentOrderProduct();

                                        numLbtrPo = operacion.getNumRefLBTR();
                                        try {
                                            lstPo = da.selectRecords("", "PAYMENT.ORDER", "$NAU",
                                                    "WITH L.NUM.REF.LBTR EQ" + numLbtrPo);
                                        } catch (Exception e) {
                                            System.out.println(e);
                                        }

                                        for (String poId : lstPo) {
                                            poRecord = new PaymentOrderRecord(da.getRecord("PAYMENT.ORDER", poId));

                                            for (PaymentOrderProductClass paymentOrderProductClass : listProd) {
                                                String poProductParam = paymentOrderProductClass
                                                        .getPaymentOrderProduct().getValue();
                                                poProduct = poRecord.getPaymentOrderProduct().getValue();
                                                if (poProductParam.equals(poProduct)) {
                                                    versionName = paymentOrderProductClass.getVesionName().getValue();
                                                    break;
                                                }
                                            }

                                            try {
                                                estadoPo = poRecord.getLocalRefField("L.ESTADO").getValue();
                                                estadoInte = operacion.getEstadoLiquidacion();
                                                if (estadoInte.equals("4") && estadoPo.equals("3")) {
                                                    poRecord.getLocalRefField("L.ESTADO").setValue(estadoInte);
                                                    txnData.setVersionId(versionName);
                                                    txnData.setFunction("I");
                                                    txnData.setTransactionId(poId);
                                                    txnData.setNumberOfAuthoriser("0");
                                                }

                                                transactionData.add(txnData);
                                            } catch (Exception ex) {
                                                System.out.println(ex);
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }

                                    arrivalBankClass.setRefNumOrigin(operacion.getNumRefOrigen());
                                    arrivalBankClass.setPriority(operacion.getPrioridad());
                                    arrivalBankClass.setChangeType(operacion.getTipoCambio());
                                    
                                    
                                    try {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                                        String dateTipCam = simpleDateFormat.format(operacion.getFechaNegociacionCavali());
                                        arrivalBankClass.setCavNegDate(dateTipCam);

                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                    
                                    arrivalBankClass.setCavRefNumber(operacion.getNumRefCavali());
                                    arrivalBankClass.setSabCode(operacion.getCodigoSAB());
                                    arrivalBankClass.setCavParticipantType(operacion.getTipoParticipanteCavali());
                                    arrivalBankClass.setSabAcctInterbnk(operacion.getCuentaInterbancariaSAB());
                                    arrivalBankClass.setConfirmEntry(operacion.getConfirmaAbono());
                                    
                                    try {
                                        String informacion = datosCliente;
                                        String[] partDataClient = informacion.split("\\|");
                                        OrderCciClass orderClass = new OrderCciClass();
                                        orderClass.setOrderCci(partDataClient[0]);
                                        orderClass.setOrderName(partDataClient[1]);
                                        orderClass.setOrderAddress(partDataClient[2]);
                                        orderClass.setOrderDocType(partDataClient[3]);
                                        orderClass.setOrderNumDoc(partDataClient[4]);
                                        orderClass.setBenefCci(partDataClient[5]);
                                        orderClass.setBenefName(partDataClient[6]);
                                        orderClass.setBenefAddress(partDataClient[7]);
                                        orderClass.setBenefTypeDoc(partDataClient[8]);
                                        orderClass.setBenefNumDoc(partDataClient[9]);
                                        orderClass.setIndicatorItf(partDataClient[10]);
                                        orderClass.setComments(partDataClient[11]);
                                       

                                        ebBciBcrpConsultasRecord.setOrderCci(orderClass, 0);
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }

                                    
                                    ebBciBcrpConsultasRecord.setCodeArrivalBank(arrivalBankClass, cont);

                                    

                                    try {
                                        ebConsultasTable.write(operacion.getNumRefLBTR(), ebBciBcrpConsultasRecord);
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                } else if (nameConsult.equals("Operaciones Recibidas")) {
                    nameWCLog = "WCOPRECIBIDAS";

                    EbBciBcrpCwReceivedOperationsRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwReceivedOperationsRecord(
                            this);
                    final EbBciBcrpCwReceivedOperationsTable ebConsultasTable = new EbBciBcrpCwReceivedOperationsTable(
                            this);
                    int cont = 0;
                    String fecha = "";
                    String datosCliente = "";

                    dateWc = ebBciBcrpConsultasSelectRecord.getFechaLiquidacion().getValue();
                    try {
                        Date date1 = new SimpleDateFormat("yyyyMMdd").parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    } catch (Exception e) {
                        Date date1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    }

                    com.techmill.integration.cw.operacionesRecibidas.Result result = new com.techmill.integration.cw.operacionesRecibidas.Result();
                    ArrayList<Operaciones> lstOperacionesRecibidas = new ArrayList<>();
                    
                    
                    RootOperacionesRecibidas rootIntegration = new RootOperacionesRecibidas();
                    final OperacionesRecibidas classInteg = new OperacionesRecibidas();

                    try {
                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord.setOut("fecha:" + fecha.concat("sid:" + sid));
                        integrationLogsRecord.setTxId("SYSTEM");

                        rootIntegration = classInteg.processRequest(sid, fecha);
                        integrationLogsRecord.setIn(rootIntegration.toString());
                        if (rootIntegration.isOk()) {

                            result = rootIntegration.getResult();
                            lstOperacionesRecibidas = result.getOperaciones();

                            for (Operaciones operacionRecibida : lstOperacionesRecibidas) {
                                com.temenos.t24.api.tables.ebbcibcrpcwreceivedoperations.CodeArrivalBankClass arrivalBankClass = new com.temenos.t24.api.tables.ebbcibcrpcwreceivedoperations.CodeArrivalBankClass();
                                arrivalBankClass.setCodeArrivalBank(operacionRecibida.getCodBancoDestino());
                                arrivalBankClass.setCodeOriginBank(operacionRecibida.getCodBancoOrigen());
                                arrivalBankClass.setCodeConcept(operacionRecibida.getCodConcepto());
                                arrivalBankClass.setCodeCurrency(operacionRecibida.getCodMoneda());
                                arrivalBankClass.setConfirmEntry(operacionRecibida.getConfirmaAbono());
                                arrivalBankClass.setAcctArrival(operacionRecibida.getCuentaDestino());
                                arrivalBankClass.setAcctOrigin(operacionRecibida.getCuentaOrigen());

                                try {
                                    datosCliente = operacionRecibida.getDatosCliente();
                                    
                                } catch (Exception e) {

                                }

                                arrivalBankClass.setSettlementState(operacionRecibida.getEstadoLiquidacion());

                                try {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                                    String dateTipCam = simpleDateFormat
                                            .format(operacionRecibida.getFechaLiquidacion());
                                    arrivalBankClass.setSettlementDate(dateTipCam);

                                } catch (Exception e) {

                                }
                                arrivalBankClass.setSettlementHour(operacionRecibida.getHoraLiquidacion());
                                arrivalBankClass.setPaymentInstructions(operacionRecibida.getInstruccionesPago());
                                arrivalBankClass.setOperationAmount(operacionRecibida.getMontoOperacion());
                                arrivalBankClass.setRefLbtrNum(operacionRecibida.getNumRefLBTR());
                                arrivalBankClass.setRefNumOrigin(operacionRecibida.getNumRefOrigen());
                                arrivalBankClass.setPriority(operacionRecibida.getPrioridad());
                                arrivalBankClass.setChangeType(operacionRecibida.getTipoCambio());
                                
                                
                                
                                arrivalBankClass.setChangeType(operacionRecibida.getTipoCambio());
                                arrivalBankClass.setChangeType(operacionRecibida.getTipoCambio());
                                arrivalBankClass.setChangeType(operacionRecibida.getTipoCambio());
                                arrivalBankClass.setChangeType(operacionRecibida.getTipoCambio());
                                

                                ebBciBcrpConsultasRecord.setCodeArrivalBank(arrivalBankClass, cont);
                                try {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                                    String dateTipCam = simpleDateFormat
                                            .format(operacionRecibida.getFechaNegociacionCavali());
                                    ebBciBcrpConsultasRecord.setCavNegDate(dateTipCam);

                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                
                                ebBciBcrpConsultasRecord.setCavRefNumber(operacionRecibida.getNumRefCavali());
                                ebBciBcrpConsultasRecord.setSabCode(operacionRecibida.getCodigoSAB());
                                ebBciBcrpConsultasRecord.setCavParticipantType(operacionRecibida.getTipoParticipanteCavali());
                                ebBciBcrpConsultasRecord.setSabAcctInterbnk(operacionRecibida.getCuentaInterbancariaSAB());
                               
                                
                                
                                try {
                                    String informacion = datosCliente;
                                    String[] partDataClient = informacion.split("\\|");
                                    com.temenos.t24.api.tables.ebbcibcrpcwreceivedoperations.OrderCciClass orderClass = new com.temenos.t24.api.tables.ebbcibcrpcwreceivedoperations.OrderCciClass();
                                    orderClass.setOrderCci(partDataClient[0]);
                                    orderClass.setOrderName(partDataClient[1]);
                                    orderClass.setOrderAddress(partDataClient[2]);
                                    orderClass.setOrderDocType(partDataClient[3]);
                                    orderClass.setOrderNumDoc(partDataClient[4]);
                                    orderClass.setBenefCci(partDataClient[5]);
                                    orderClass.setBenefName(partDataClient[6]);
                                    orderClass.setBenefAddress(partDataClient[7]);
                                    orderClass.setBenefTypeDoc(partDataClient[8]);
                                    orderClass.setBenefNumDoc(partDataClient[9]);
                                    orderClass.setIndicatorItf(partDataClient[10]);
                                    orderClass.setComments(partDataClient[11]);
                                   

                                    ebBciBcrpConsultasRecord.setOrderCci(orderClass, 0);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }

                                
                                

                                try {
                                    ebConsultasTable.write(operacionRecibida.getNumRefLBTR(), ebBciBcrpConsultasRecord);
                                } catch (Exception e) {

                                    System.out.println(e.getMessage());
                                }
                            }


                        }
                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                } else if (nameConsult.equals("Tipo de Cambio")) {
                    nameWCLog = "WCTIPO_DE_CAMBIO";

                    EbBciBcrpCwChangeTypeRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwChangeTypeRecord(this);
                    final EbBciBcrpCwChangeTypeTable ebConsultasTable = new EbBciBcrpCwChangeTypeTable(this);
                    int cont = 0;
                    String fecha = "";
                    String codCurrency = "";
                    com.techmill.integration.cw.tipoDeCambio.Result resultTipoCambio = new com.techmill.integration.cw.tipoDeCambio.Result();

                    dateWc = ebBciBcrpConsultasSelectRecord.getFecha().getValue();
                    codCurrency = ebBciBcrpConsultasSelectRecord.getEstado().getValue();
                    try {
                        Date date1 = new SimpleDateFormat("yyyyMMdd").parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    } catch (Exception e) {
                        Date date1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    }

                    RootTipodeCambio roTipoCambio = new RootTipodeCambio();
                    final TipoDeCambio tipoCambioInteg = new TipoDeCambio();

                    try {
                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord
                                .setOut("Sid: " + sid.concat("codCurrency: " + codCurrency.concat("fecha:" + fecha)));
                        integrationLogsRecord.setTxId(userId);

                        roTipoCambio = tipoCambioInteg.processRequest(sid, codCurrency, fecha);
                        integrationLogsRecord.setIn(roTipoCambio.toString());
                        if (roTipoCambio.isOk()) {

                            resultTipoCambio = roTipoCambio.getResult();

                        }

                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }

                    final com.temenos.t24.api.tables.ebbcibcrpcwchangetype.CodeCurrencyClass codeCurrencyClass = new com.temenos.t24.api.tables.ebbcibcrpcwchangetype.CodeCurrencyClass();
                    codeCurrencyClass.setCodeCurrency(resultTipoCambio.getCodMoneda());
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                        String dateTipCam = simpleDateFormat.format(resultTipoCambio.getFecha());
                        codeCurrencyClass.setDate(dateTipCam);

                    } catch (Exception e) {

                    }
                    codeCurrencyClass.setValueChangeType(resultTipoCambio.getValorTipoCambio());
                    ebBciBcrpConsultasRecord.setCodeCurrency(codeCurrencyClass, cont);

                    try {
                        ebConsultasTable.write(resultTipoCambio.getCodMoneda(), ebBciBcrpConsultasRecord);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                } else if (nameConsult.equals(NamesWCEnum.COMPRAVENTA.getText())) {
                    nameWCLog = "WCCOMPRAVENTA";

                    EbBciBcrpCwCompraVentaRecord ebBciBcrpConsultasRecord = new EbBciBcrpCwCompraVentaRecord(this);
                    final EbBciBcrpCwCompraVentaTable ebConsultasTable = new EbBciBcrpCwCompraVentaTable(this);
                    String fecha = "";
                    
                    com.techmill.integration.cw.operacionesCvme.Result resultCompraVenta = new com.techmill.integration.cw.operacionesCvme.Result();

                    dateWc = ebBciBcrpConsultasSelectRecord.getFecha().getValue();
                    try {
                        Date date1 = new SimpleDateFormat("yyyyMMdd").parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    } catch (Exception e) {
                        Date date1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(dateWc);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.'000'XXX");
                        fecha = df.format(date1);
                    }

                    RootOperacionesCvme roCompraVenta = new RootOperacionesCvme();
                    final OperacionesCvme tipoCambioInteg = new OperacionesCvme();

                    try {
                        integrationLogsRecord.setAppName(nameWCLog);
                        integrationLogsRecord
                                .setOut("fecha:" + fecha);
                        integrationLogsRecord.setTxId(userId);

                        roCompraVenta = tipoCambioInteg.processRequest(sid, fecha);
                        integrationLogsRecord.setIn(roCompraVenta.toString());
                        if (roCompraVenta.isOk()) {

                            resultCompraVenta = roCompraVenta.getResult();
                            ArrayList<OperacionCvme> lstOperacionesCvme = resultCompraVenta.getOperaciones();
                            for (OperacionCvme operaCompraVenta : lstOperacionesCvme) {
                                ebBciBcrpConsultasRecord.setCodeArrivalBank(operaCompraVenta.getCodBancoDestino());
                                ebBciBcrpConsultasRecord.setCodeOriginBank(operaCompraVenta.getCodBancoOrigen());
                                ebBciBcrpConsultasRecord.setCodeConcept(operaCompraVenta.getCodConcepto());
                                ebBciBcrpConsultasRecord.setAcctArrival(operaCompraVenta.getCuentaDestino());
                                ebBciBcrpConsultasRecord.setAcctOrigin(operaCompraVenta.getCuentaOrigen());
                                
                                try {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                                    String dateTipCam = simpleDateFormat.format(operaCompraVenta.getFechaLiquidacion());
                                    ebBciBcrpConsultasRecord.setSettlementDate(dateTipCam);

                                } catch (Exception e) {
                                    integrationLogsRecord.setFlagErr("YES");
                                    System.out.println(e.getMessage());
                                }

                                ebBciBcrpConsultasRecord.setPaymentInstructions(operaCompraVenta.getInstruccionesPago());
                                ebBciBcrpConsultasRecord.setMeAmount(operaCompraVenta.getMontoME());
                                ebBciBcrpConsultasRecord.setMnAmount(operaCompraVenta.getMontoMN());
                                ebBciBcrpConsultasRecord.setBcrCvRefNum(operaCompraVenta.getNumRefCompraVentaBCR());
                                ebBciBcrpConsultasRecord.setRefOrigNum(operaCompraVenta.getNumRefOrigen());
                                ebBciBcrpConsultasRecord.setChangeType(operaCompraVenta.getTipoCambio());
                                try {
                                    ebConsultasTable.write(operaCompraVenta.getNumRefCompraVentaBCR(), ebBciBcrpConsultasRecord);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }

                            }

                        }

                    } catch (Exception e2) {
                        integrationLogsRecord.setFlagErr("YES");
                        System.out.println(e2.getMessage());
                    }
                }

                try {
                    integrationLogsTable.write(nameWCLog + "-" + mydate, integrationLogsRecord);
                } catch (Exception e) {
                }

                String rdm = RandomStringUtils.randomAlphanumeric(3);
                ebBciBcrpConsultasSelectRecord.setSid(rdm);
                currentRecords.add(ebBciBcrpConsultasSelectRecord.toStructure());

            } catch (Exception e4) {
                e4.getMessage();
            }
        }
    }

}
