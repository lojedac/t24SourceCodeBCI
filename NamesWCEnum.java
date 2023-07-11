package com.bci;

/**
 * TODO: Document me!
 *
 * @author Diego Maigualca
 *
 */
public enum NamesWCEnum {

    STOCKBROKERS("Agentes de Bolsa"),
    CONCEPTS("Conceptos"),
    ENTITIES("Entidades Financieras"),
    CURRENCIES("Moneda"),
    SALES("Saldos Cuenta Corriente"),
    CTASCTES("Cuentas Corrientes Entidad"),
    FCOLLECTION("Cobro Tarifa"),
    TARIFARIO("Tarifas por el uso del LBTR"),
    FDISPONIBLES("Facilidades Disponibles"),
    MCTASCTES("Movimientos Cuenta Corriente"),
    OPOTORGADAS("Operaciones Otorgadas"),
    OPRECIBIDAS("Operaciones Recibidas"),
    COMPRAVENTA("Compra y Venta de Moneda Extranjera");
    
    private final String text;

    
    NamesWCEnum(String text){
        this.text = text;

    }

    public String getText() {
        return text;
    }
    
    
}
