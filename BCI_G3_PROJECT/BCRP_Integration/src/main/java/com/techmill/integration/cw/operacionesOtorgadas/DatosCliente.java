package com.techmill.integration.cw.operacionesOtorgadas;

public class DatosCliente {

    private String dataCliente;
    private String ksimDataCliente;
    private String firmaDataCliente;

    public String getDataCliente() {
        return dataCliente;
    }

    public void setDataCliente(String dataCliente) {
        this.dataCliente = dataCliente;
    }

    public String getKsimDataCliente() {
        return ksimDataCliente;
    }

    public void setKsimDataCliente(String ksimDataCliente) {
        this.ksimDataCliente = ksimDataCliente;
    }

    public String getFirmaDataCliente() {
        return firmaDataCliente;
    }

    public void setFirmaDataCliente(String firmaDataCliente) {
        this.firmaDataCliente = firmaDataCliente;
    }

    @Override
    public String toString() {
        return "DatosCliente [dataCliente=" + dataCliente + ", ksimDataCliente=" + ksimDataCliente
                + ", firmaDataCliente=" + firmaDataCliente + "]";
    }
}
