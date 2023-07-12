package com.bci;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.temenos.t24.api.records.eblookup.EbLookupRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebbciccegroundreturn.CodeClass;
import com.temenos.t24.api.tables.ebbciccegroundreturn.EbBciCceGroundReturnRecord;

/**
 * TODO: Document me!
 *
 * @author David Barahona
 * @mail david.barahona@nagarro.com
 */
public class BciCCEInValidaciones {
    public static List<List<String>> listaArchivos(String fileName) {
        byte countArch = 0;
        List<String> lines = Collections.emptyList();
        List<String> arch1 = new ArrayList<String>();
        List<String> arch2 = new ArrayList<String>();
        List<List<String>> listArchivos = new ArrayList<List<String>>();

        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Error en lectura de archivo
            e.printStackTrace();
        }

        Iterator<String> itr = lines.iterator();
        while (itr.hasNext()) {
            String line = itr.next();
            if (countArch == 0) {
                arch1.add(line);
                if (line.substring(0, 1).equals("9"))
                    countArch++;
            } else {
                arch2.add(line);
            }
        }
        listArchivos.add(arch1);
        listArchivos.add(arch2);
        return listArchivos;
    }

    public static String validaciones(List<String> arch, String today, String lastWorkingDay,
            EbBciCceGroundReturnRecord ebBciCceGroundReturnRecord, DataAccess da) {
        String msg = "";


        int contaErr = 0;
        int contErrSeq7 = 0;
        // Variables para EndLotHeader
        long totLinesLH = 0;
        long totLinesELH = 0;
        long totEntidadAcred = 0;
        long totEntidadAcredELH = 0;
        long totRegIndividuales = 0;
        long totRegIndividualesELH = 0;
        long totImportes = 0;
        long totImportesELH = 0;
        long totComisiones = 0;
        long totComisionesELH = 0;
        int countLH = 0;
        String entidadOrigELH = "";
        // Variables para EndFileHeader
        byte countFH = 0;
        byte countEFL = 0;
        int countEFH = 0;
        int flagVald = 0;
        long totLinesFH = 0;
        long totLinesEFH = 0;
        long sumtotEntidadAcredELH = 0;
        long totEntidadAcredEFH = 0;
        long sumtotRegIndividualesEFH = 0;
        long totRegIndividualesEFH = 0;
        long sumtotImportesELH = 0;
        long totImportesEFH = 0;
        long sumtotComisionesELH = 0;
        long totComisionesEFH = 0;
        int tipoTransferInt = 0;

        // list
        List<String> list = new ArrayList<>();

        // Variables para FileHeader
        String destinInmedi = "";

        // Variables para LoteHeader
        String entidadOrigLH = "";
        String secUnivoc = "";
        boolean flagTipoTransfer = false;

        // contador
        int contTmp = 0;

        // codigoTransac
        String codigoTransac = "";
        String tipoLote = "";

        // Variables para registro individual
        String contRegInd = "";

        // Variables para registro individual
        String contRegAdd = "";

        boolean isTRT = false;
        // Variables para devueltos
        boolean isDevult = false;

        boolean isDevultTRT = false;
        // Variables para presentados
        boolean isPresent = false;
        // Variables para presentados
        boolean isConfir = false;
        // Archivo 1

        // Variables CR NUEVOS
        String transfConfirma = "";

        if (arch.size() == 0) {
            msg = "Error \" Archivo vacío \"";
        }

        for (String line : arch) {

            if (line.length() != 200) {
                contTmp++;
                msg = "Linea " + contTmp + " - " + "Longitud incorrecta en archivo debe ser 200 caracteres::"
                        + line.length();
                return msg;
            }

            if (line.substring(0, 1).equals("1")) {
                contaErr++;
                countFH++;
                destinInmedi = line.substring(6, 14);

                if (!destinInmedi.equals("00630063")) {
                    msg = "Linea: " + contaErr
                            + " - Error en el tipo de Destino inmediato \"Debe ser 00630063\" :: "
                            + destinInmedi;
                }

                if (line.substring(3, 6).equals("TRT")) {
                    isTRT = true;
                }

                // Validacion si es devuelto
                if (line.substring(1, 2).equals("2") || line.substring(1, 2).equals("5")
                        || line.substring(1, 2).equals("6")) {
                    isDevult = true;
                    if (!line.substring(2, 3).equals("2") && !line.substring(2, 3).equals("1")) {
                        msg = "Linea: " + contaErr + " - " + "Moneda incorrecta" + "::" + line.substring(2, 3);
                        flagVald = 1;
                    }
                } else if (line.substring(1, 2).equals("1")) {
                    isPresent = true;
                } else if (line.substring(1, 2).equals("7")) {
                    isConfir = true;
                }
                if (flagVald == 1) {
                    if (!line.substring(3, 6).equals("TRT") && !line.substring(3, 6).equals("TRM")
                            && !line.substring(3, 6).equals("TRI")) {
                        msg = "Linea:" + contaErr + " - " + "Código de aplicación erróneo debe ser TRI/TRM/TRT" + "::"
                                + line.substring(3, 6);
                    }
                    if (line.substring(3, 6).equals("TRT")) {
                        isDevultTRT = true;
                    }
                }
            }
            if (line.substring(0, 1).equals("5")) {
                contaErr++;
                countLH++;
                totLinesLH++;
                totLinesFH++;
                String tmp = "";
                tipoLote = line.substring(3, 5);
                entidadOrigLH = line.substring(85, 100);
                String entidadOrigenLH = line.substring(86, 89);

                try {
                    list = da.selectRecords("", "EB.BCI.CCE.PARTICIPANTS.BANK.NAME", "",
                            "WITH @ID EQ" + list);
                    System.out.println("la lista es: " + list);
                    
                    System.out.println("la entidad es: " + entidadOrigELH);
                    
                    int sizeList = list.size();
                    System.out.println("la longitud de la lista es: " + sizeList);
                    
                    if (sizeList == 0) {
                        msg = "Linea:" + contaErr + " - " + "Campo Entidad no está en tabla de Entidades" + "::"
                                + line.substring(85, 89);
                    } else if (isConfir) {
                        
                        /*
                        EbBciCceParticipantsBankNameRecord bnkRecord = new EbBciCceParticipantsBankNameRecord(
                                da.getRecord("EB.BCI.CCE.PARTICIPANTS.BANK.NAME", entidadOrigenLH));
                        transfConfirma = bnkRecord.getTransConfirma().getValue();
                        if (transfConfirma.equals("NO")) {
                            msg = "Error \" en la entidad ingresada, la configuración está desactivada para la confirmación \"";
                            return msg;
                        }
                        */
                    }

                } catch (Exception e) {

                }

                String tipoTransferStr = line.substring(66, 69);

                try {
                    tipoTransferInt = Integer.parseInt(tipoTransferStr);
                } catch (Exception e) {
                    msg = "Linea:" + contaErr + " - "
                            + "Error en el campo: Tipo de tranferencia, solo se permiten números, " + "::"
                            + line.substring(66, 69);
                }

                if (tipoTransferStr.equals("224") || tipoTransferStr.equals("225")) {
                    flagTipoTransfer = true;

                } else if (tipoTransferInt >= 220 || tipoTransferInt <= 223) {
                    flagTipoTransfer = false;
                }

                String fechaPresentacion = line.substring(69, 77);
                String fechaLiquidacion = line.substring(77, 85);
                if (isDevult) {
                    if (isTRT) {
                        if (!fechaPresentacion.equals(lastWorkingDay) && !fechaLiquidacion.equals(today)) {
                            System.out.println(fechaPresentacion + " " + fechaLiquidacion);
                            msg = "Linea:" + contaErr + " - "
                                    + "Error en fecha de Presentacion-Liquidacion para turno TRT" + "::"
                                    + fechaPresentacion + "::" + lastWorkingDay + "-" + fechaLiquidacion + "::" + today;
                            System.out.println(msg);
                        }
                    } else {
                        if (!fechaPresentacion.equals(fechaLiquidacion) && !fechaPresentacion.equals(today)) {
                            System.out.println(fechaPresentacion + " " + fechaLiquidacion);
                            msg = "Linea:" + contaErr + " - "
                                    + "Error en fecha de Presentacion-Liquidacion para turno TRI TRM" + "::"
                                    + fechaPresentacion + "-" + fechaLiquidacion + "-" + today;
                            System.out.println(msg);
                        }
                    }
                    if (!line.substring(3, 5).equals("31")) {
                        msg = "Linea:" + contaErr + " - " + "Tipo de lote erróneo" + "::" + line.substring(3, 5);
                    }
                }
                if (isPresent) {
                    if (!line.substring(3, 5).equals("32")) {
                        msg = "Linea:" + contaErr + " - " + "Tipo de lote erróneo" + "::" + line.substring(3, 5);
                    }
                } else if (isConfir) {
                    if (!line.substring(3, 5).equals("33")) {
                        msg = "Linea:" + contaErr + " - " + "Tipo de lote erróneo" + "::" + line.substring(3, 5);
                    }
                }
                try {
                    tmp = line.substring(1, 3);
                    int lineInt = Integer.parseInt(line.substring(1, 3));
                    if (lineInt <= 0) {
                        msg = "Linea:" + contaErr + " - " + "El número de archivo debe ser mayor a 0" + "::"
                                + line.substring(1, 3);
                    }
                    tmp = line.substring(93, 100);
                    int loteNumber = Integer.parseInt(line.substring(93, 100));

                    if (loteNumber <= 0) {
                        msg = "Linea:" + contaErr + " - " + "El número de archivo debe ser mayor a 0" + "::"
                                + line.substring(93, 100);
                    }

                    tmp = line.substring(66, 69);
                    int tipoTransfer = Integer.parseInt(line.substring(66, 69));
                    if (tipoTransfer < 220 || tipoTransfer > 225) {
                        msg = "Linea:" + contaErr + " - " + "Tipo de Transferencia erróneo" + "::"
                                + line.substring(66, 69);

                    }

                } catch (Exception e) {
                    msg = "Linea:" + contaErr + " - " + "Error en tipo de dato de campos, solo se aceptan números"
                            + "::" + tmp;
                }

            } else if (line.substring(0, 1).equals("6")) {
                contaErr++;
                totLinesLH++;
                totLinesFH++;
                totRegIndividuales++;
                sumtotRegIndividualesEFH++;
                String tmp = "";
                try {
                    tmp = line.substring(3, 11);
                    totEntidadAcred += Long.parseLong(line.substring(3, 11));
                    tmp = line.substring(33, 48);
                    totImportes += Long.parseLong(line.substring(33, 48));
                    tmp = line.substring(49, 64);
                    totComisiones += Long.parseLong(line.substring(49, 64));
                } catch (Exception e) {
                    msg = "Linea:" + contaErr + " " + "Error el lectura de datos \"Posiciones Incorrectas\"" + "::"
                            + tmp;
                    System.out.println(msg);
                    break;
                }

                String codigoTarifa = line.substring(11, 12);
                if (codigoTarifa.equals(" ") || codigoTarifa.isEmpty()) {
                    msg = "Linea:" + contaErr + " " + "Error en código de tarifa \"Este campo es mandatorio\"" + "::"
                            + codigoTarifa;
                } else {
                    int contCodiTarifa = 0;
                    try {
                        List<String> idEbLookUp = new ArrayList<String>();
                        idEbLookUp = da.selectRecords("", "EB.LOOKUP", "", "WITH VIRTUAL.TABLE EQ L.RATE.CODE");
                        for (String string : idEbLookUp) {
                            EbLookupRecord ebLookupRecord = new EbLookupRecord(da.getRecord("EB.LOOKUP", string));
                            String ebLOkkupId = ebLookupRecord.getLookupId().getValue();
                            if (codigoTarifa.equals(ebLOkkupId)) {
                                contCodiTarifa++;
                            }

                        }
                        if (contCodiTarifa == 0) {
                            msg = "Linea:" + contaErr + " " + "Error en código de tarifa " + "::" + codigoTarifa;
                        }

                    } catch (Exception e) {
                        msg = "Linea:" + contaErr + " "
                                + "Error en código de tarifa \"No existe el campo en el EbLookUp (L.RATE.CODE) \""
                                + "::" + codigoTarifa;
                    }
                }

                codigoTransac = line.substring(1, 3);
                if (!tipoLote.equals(codigoTransac)) {
                    System.out.println("NO SON IGUALES" + tipoLote + ": " + codigoTransac);

                }

                try {
                    int importInt = Integer.parseInt(line.substring(33, 48));
                } catch (Exception e) {
                    msg = "Linea:" + contaErr + " " + "Error en el importe, \"solo se admite valores numéricos\"" + "::"
                            + line.substring(33, 48);
                }

                /*
                 * try { int registroAdicional =
                 * Integer.parseInt(line.substring(184, 185)); if
                 * (registroAdicional == 0) { msg = "Linea:" + " " +
                 * "Error en el registro adicional, \"solo se admite valores numéricos debe ser 1\""
                 * + "::" + line.substring(184, 185); if (!flagTipoTransfer) {
                 * msg = "Linea:" + " " +
                 * "Error en el registro adicional, \"solo se admite valores para transferencias 224 y 225\""
                 * + "::" + line.substring(184, 185); } if (!isConfir) { msg =
                 * "Linea:" + " " +
                 * "Error en el registro adicional, \"solo se admite valores para presentados y devueltos\""
                 * + "::" + line.substring(184, 185); } }
                 * 
                 * else if (registroAdicional == 1 && isPresent) { msg =
                 * "Linea:" + " " +
                 * "Error en el registro adicional, \"solo se admite valores numéricos debe ser 0\""
                 * + "::" + line.substring(184, 185); if (flagTipoTransfer) {
                 * msg = "Linea:" + " " +
                 * "Error en el registro adicional, \"solo se admite valores para transferencias 224 y 225\""
                 * + "::" + line.substring(184, 185); } if (isConfir) { msg =
                 * "Linea:" + " " +
                 * "Error en el registro adicional, \"solo se admite valores para presentados y devueltos\""
                 * + "::" + line.substring(184, 185); }
                 * 
                 * }
                 * 
                 * 
                 * } catch (Exception e) { msg = "Linea:" + contaErr + " " +
                 * "Error en el registro adicional, \"solo se admite valores numéricos debe ser 1 ó 0\""
                 * + "::" + line.substring(184, 185); }
                 */

                try {
                    Long importInt = Long.parseLong(line.substring(185, 200));
                } catch (Exception e) {
                    msg = "Linea:" + contaErr + " "
                            + "Error en el contador de registro, \"solo se admite valores numéricos\"" + "::"
                            + line.substring(185, 200);
                }

                try {
                    contRegInd = line.substring(193, 200);
                    int contRegIndInt = Integer.parseInt(contRegInd);
                } catch (Exception e) {
                    msg = "Linea:" + contaErr + " "
                            + "Error en el contador de registro, \"solo se admite valores numéricos\"" + "::"
                            + contRegInd;
                }

            } else if (line.substring(0, 1).equals("7")) {
                contaErr++;
                String tipoDocumentBene = "";
                totLinesLH++;
                totLinesFH++;

                try {
                    if (line.substring(0, 3).equals("705") && (isDevult || isConfir)) {
                        contErrSeq7++;
                        String confirmAbono = line.substring(182, 183);

                        try {
                            tipoDocumentBene = line.substring(3, 4);
                            int tipoDocumentBeneInt = Integer.parseInt(line.substring(3, 4));
                            System.out.println("tipoDocumentBeneInt");
                            System.out.println(tipoDocumentBeneInt);
                            if (tipoDocumentBeneInt < 1 || tipoDocumentBeneInt > 6) {
                                msg = "Linea: " + contaErr
                                        + " - Error el tipo de Documento del Beneficiario \"Debe ser (>=1 y <=6)\" ::"
                                        + tipoDocumentBeneInt;
                            }
                        } catch (Exception e) {
                            if (!tipoDocumentBene.isEmpty() && !tipoDocumentBene.equals(" ")) {
                                msg = "Linea: " + contaErr
                                        + " - Error \"solo se aceptan valores numéricos - Debe ser blancos ó (>=1 y <=6)\"";
                            }

                        }
                        System.out.println("Documento del Beneficiario: " + tipoDocumentBene);

                        try {
                            contRegAdd = line.substring(193, 200);
                            int contRegAddInt = Integer.parseInt(contRegAdd);
                        } catch (Exception e) {
                            msg = "Linea:" + contaErr + " "
                                    + "Error en el contador de registro, \"solo se admite valores numéricos\"" + "::"
                                    + contRegAdd;
                        }

                        if (!contRegAdd.equals(contRegInd)) {
                            msg = "Linea: " + contaErr
                                    + " - Error Contador del registro adicional distinto del individual ::" + contRegAdd
                                    + "-" + contRegInd;
                        }

                    }

                    if (line.substring(0, 3).equals("705") && !isConfir) {
                        String confirmAbono = "";
                        confirmAbono = line.substring(182, 183);
                        try {
                            
                            if (!confirmAbono.equals(" ")) {
                                try {
                                    int confAbonInt = Integer.parseInt(confirmAbono);
                                    if (confAbonInt != 1 && confAbonInt != 0) {
                                        msg = "Linea:" + contaErr + " En el campo confirmación de abono"
                                                + " Solo se aceptan valores de 0/1/nulo :: " + confirmAbono;
                                    }
                                } catch (Exception e) {
                                    msg = "Linea:" + contaErr + " En el campo confirmación de abono"
                                            + " Solo se aceptan valores de 0/1/nulo :: " + confirmAbono;
                                }
                                
                            }
                            
                        } catch (Exception e) {
                            
                                                                                                                                                            
                                                   
                        }

                    }
                    
                    
                    if (line.substring(0, 3).equals("705") && isConfir) {
                        String confirmAbono = "";
                        confirmAbono = line.substring(182, 183);
                        try {
                            int confAbonInt = Integer.parseInt(confirmAbono);
                            if (confAbonInt != 1) {
                                msg = "Linea:" + contaErr + " En el campo confirmación de abono"
                                        + " Solo se aceptan valores de 1 :: " + confirmAbono;
                            }
                            
                        } catch (Exception e) {
                            msg = "Linea:" + contaErr + " En el campo confirmación de abono"
                                    + " Solo se aceptan valores de 1 :: " + confirmAbono;
                        }
                    }
                    

                    if (line.substring(0, 3).equals("799") && contErrSeq7 != 0) {

                        secUnivoc = line.substring(73, 80);
                        /*
                         * try { DataAccess da = new DataAccess();
                         * BciCceMappingFieldValuesRecord mapRec = new
                         * BciCceMappingFieldValuesRecord(da.getRecord(
                         * "EB.BCI.CCE.MAPPING.FIELD.VALUES", secUnivoc));
                         * String ppOrderEntryId = mapRec.getE
                         * 
                         * } catch (Exception e) { msg = "Linea:" + line +
                         * " en el campo:" + "secuencia unívoca" + secUnivoc +
                         * " No existe esa secuencia univoca"; }
                         */

                        String codeReturnLine = line.substring(3, 6);
                        List<CodeClass> listCode = new ArrayList<>();
                        int cont = 0;
                        listCode = ebBciCceGroundReturnRecord.getCode();
                        for (CodeClass codeClass : listCode) {
                            String codeReturn = codeClass.getCode().getValue();
                            if (codeReturn.equals(codeReturnLine)) {
                                cont = 1;
                            }
                        }
                        if (cont == 0) {
                            msg = "Linea:" + contaErr + " - " + "No existe código de devolución" + line.substring(3, 6);
                        }
                    } else if (line.substring(0, 3).equals("799") && contErrSeq7 == 0) {
                        msg = "Linea:" + contaErr + " - " + "Registro adicional obligatorio no encontrado ";
                    }

                    if (!line.substring(1, 3).equals("99") && !line.substring(1, 3).equals("05")) {
                        msg = "Linea:" + contaErr + " - " + "Código de registro adicional no permitido" + "::"
                                + line.substring(1, 3);
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } else if (line.substring(0, 1).equals("8")) {
                contaErr++;
                totLinesLH++;
                totLinesFH++;
                entidadOrigELH = line.substring(94, 109);
                if (!entidadOrigLH.equals(entidadOrigELH)) {
                    msg = "Linea: " + contaErr + " - " + "Entidad origen en Fin de Lote distinta de Cabecera de Lote"
                            + entidadOrigLH + "-" + entidadOrigELH;
                }
                String tmp = "";

                try {
                    tmp = line.substring(1, 11);
                    totLinesELH = Long.parseLong(line.substring(1, 11));
                    tmp = line.substring(11, 26);
                    totEntidadAcredELH = Long.parseLong(line.substring(11, 26));
                    tmp = line.substring(26, 41);
                    sumtotEntidadAcredELH += totEntidadAcredELH;
                    totRegIndividualesELH = Long.parseLong(line.substring(26, 41));
                    tmp = line.substring(41, 56);
                    totImportesELH = Long.parseLong(line.substring(41, 56));
                    sumtotImportesELH += totImportesELH;
                    tmp = line.substring(56, 71);
                    totComisionesELH = Long.parseLong(line.substring(56, 71));
                    sumtotComisionesELH += totComisionesELH;
                } catch (Exception e) {
                    msg = "Linea:" + contaErr + " - " + "ELH - Error el lectura de datos \"Posiciones Incorrectas\""
                            + tmp;
                }
                if (totLinesELH != totLinesLH) {
                    // System.out.println(totLinesELH +"-"+totLinesLH);
                    msg = "Linea:" + contaErr + " - " + "ELH - Error en el total de registros" + "::" + totLinesELH
                            + "-" + totLinesLH;
                }
                if (totEntidadAcred != totEntidadAcredELH) {
                    // System.out.println(totEntidadAcred
                    // +"-"+totEntidadAcredELH);
                    msg = "Linea:" + contaErr + " - " + "ELH - Error en Suma \"Entidad a debitar/acreditar\"" + "::"
                            + totEntidadAcred + "-" + totEntidadAcredELH;
                }
                if (totRegIndividuales != totRegIndividualesELH) {
                    // System.out.println(totRegIndividuales+"-"+totRegIndividualesELH);
                    msg = "Linea:" + contaErr + " - "
                            + "ELH - Error en el total de operaciones (reg individuales) que componen el Lote" + "::"
                            + totRegIndividuales + "-" + totRegIndividualesELH;
                }
                if (totImportes != totImportesELH) {
                    // System.out.println(totImportes +"-"+totImportesELH);
                    msg = "Linea:" + contaErr + " - "
                            + "ELH - Error en sumatoria del campo importe del registro individual del lote" + "::"
                            + totImportes + "-" + totImportesELH;
                }
                if (totComisiones != totComisionesELH) {
                    msg = "Linea:" + contaErr + " - "
                            + "ELH - Error en sumatoria de comisiones de registros individuales" + "::" + totComisiones
                            + "-" + totComisionesELH;
                }

                try {
                    int entidadOrigen = Integer.parseInt(line.substring(94, 102));
                } catch (Exception e) {
                    msg = "Linea:" + contaErr + " - " + line.substring(94, 102) + " - "
                            + "ELH - Error en el campo Entidad, debe ser numérico";
                }

                totLinesLH = 0;
                totEntidadAcred = 0;
                totImportes = 0;
                totRegIndividuales = 0;
                totComisiones = 0;

            } else if (line.substring(0, 1).equals("9")) {
                contaErr++;
                String tmp = "";
                countEFL++;
                try {
                    tmp = line.substring(1, 7);
                    countEFH = Integer.parseInt(line.substring(1, 7));
                    tmp = line.substring(7, 17);
                    totLinesEFH = Long.parseLong(line.substring(7, 17));
                    tmp = line.substring(17, 32);
                    totEntidadAcredEFH = Long.parseLong(line.substring(17, 32));
                    tmp = line.substring(32, 47);
                    totRegIndividualesEFH = Long.parseLong(line.substring(32, 47));
                    tmp = line.substring(47, 62);
                    totImportesEFH = Long.parseLong(line.substring(47, 62));
                    tmp = line.substring(62, 77);
                    totComisionesEFH = Long.parseLong(line.substring(62, 77));
                } catch (Exception e) {
                    msg = "Linea:" + contaErr + " - " + "ELH - Error el lectura de datos \"Posiciones Incorrectas\"";
                }
                if (countLH != countEFH) {
                    msg = "Linea:" + contaErr + " - " + "EFH - Error en numero de Lotes" + "::" + countLH + "::"
                            + countEFH;
                    // System.out.println(msg);
                }
                if (totLinesEFH != (totLinesFH + 2)) {
                    msg = "Linea:" + contaErr + " - " + "EFH - Error en el total de registros" + "::" + totLinesEFH
                            + "-" + (totLinesFH + 2);
                    // System.out.println(msg);
                }
                if (sumtotEntidadAcredELH != totEntidadAcredEFH) {
                    msg = "Linea:" + contaErr + " - " + "EFH - Error en suma del campo “Totales de control ELH”" + "::"
                            + sumtotEntidadAcredELH + "-" + totEntidadAcredEFH;
                    // System.out.println(msg);
                }
                if (sumtotRegIndividualesEFH != totRegIndividualesEFH) {
                    msg = "Linea:" + contaErr + " - "
                            + "EFH - Error en el total de operaciones (ELH) que componen el Lote"
                            + sumtotRegIndividualesEFH + "-" + totRegIndividualesEFH;
                    // System.out.println(msg);
                }
                if (sumtotImportesELH != totImportesEFH) {
                    System.out.println(sumtotImportesELH + "-" + totImportesEFH);
                    msg = "Linea:" + contaErr + " - " + "EFH - Error en el total de importes (ELH) que componen el Lote"
                            + "::" + sumtotImportesELH + "-" + totImportesEFH;
                    // System.out.println(msg);
                }
                if (sumtotComisionesELH != totComisionesEFH) {
                    msg = "Linea:" + contaErr + " " + sumtotComisionesELH + " - "
                            + "EFH - Error en suma de comisiones (ELH) que componen el Lote";
                    // System.out.println(msg);
                }
            }
        }
        if (countEFL != countFH) {
            msg = "Error en numero Inicio \"Tipo de Registro\" -Fin de Archivo";
        }
        System.out.println(msg);
        return msg;
    }

    public static String removeSpecialChar(String line) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Ä", "A");
        map.put("Á", "A");
        map.put("Ë", "E");
        map.put("É", "E");
        map.put("Ï", "I");
        map.put("Í", "I");
        map.put("Ö", "O");
        map.put("Ó", "O");
        map.put("Ü", "U");
        map.put("Ú", "U");
        map.put("|", " ");
        map.put("< ", " ");
        map.put("> ", " ");
        map.put("@", " ");
        map.put("~", " ");
        map.put("", " ");
        map.put("€", " ");
        map.put("ƒ", " ");
        map.put("„", " ");
        map.put("…", " ");
        map.put("†", " ");
        map.put("‡", " ");
        map.put("ˆ", " ");
        map.put("‰", " ");
        map.put("Š", " ");
        map.put("‹", " ");
        map.put("Œ", " ");
        map.put("Ž", " ");
        map.put("‘", " ");
        map.put("’", " ");
        map.put("“", " ");
        map.put("”", " ");
        map.put("•", " ");
        map.put("˜", " ");
        map.put("™", " ");
        map.put("›", " ");
        map.put("Ÿ", " ");
        map.put("¢", " ");
        map.put("£", " ");
        map.put("¤", " ");
        map.put("¥", " ");
        map.put("¦", " ");
        map.put("§", " ");
        map.put("©", " ");
        map.put("ª", " ");
        map.put("«", " ");
        map.put("¬", " ");
        map.put("­", " ");
        map.put("®", " ");
        map.put("¯", " ");
        map.put("±", " ");
        map.put("²", " ");
        map.put("³", " ");
        map.put("´", " ");
        map.put("µ", " ");
        map.put("¶", " ");
        map.put("·", " ");
        map.put("¸", " ");
        map.put("¹", " ");
        map.put("»", " ");
        map.put("¼", " ");
        map.put("½", " ");
        map.put("¾", " ");
        map.put("×", " ");
        map.put("÷", " ");
        map.put("À", "A");
        map.put("Â", "A");
        map.put("Ã", "A");
        map.put("Å", "A");
        map.put("Æ", " ");
        map.put("Ç", " ");
        map.put("È", "E");
        map.put("Ê", "E");
        map.put("Ì", "I");
        map.put("Î", "I");
        map.put("Ò", "O");
        map.put("Ô", "O");
        map.put("Õ", "O");
        map.put("Ø", " ");
        map.put("ß", " ");
        map.put("Ù", "U");
        map.put("Û", "U");
        map.put("Ý", "Y");
        map.put("Ð", " ");
        map.put("Þ", " ");
        map.put("Ñ", "N");
        // map.put("\\", " ");
        map.put(",", " ");
        map.put("/", " ");
        map.put("_", " ");
        map.put(".", " ");
        map.put(":", " ");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            line = line.replace(entry.getKey().toString(), entry.getValue().toString());
        }
        return line;
    }

}
