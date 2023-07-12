# BCI_G3_PROJECT

Este es el repositorio principal de GitLab para el proyecto BCI_G3_PROJECT, que contiene múltiples subproyectos Java, cada uno con su propio dominio funcional, pero todos unificados por su interacción con el sistema LBTR del Banco Central de Perú.

## Subproyectos

### BCRP_lntegration
Proyecto Maven para la deserialización de JSON a objetos Java y la definición de endpoints para las APIs LBTR.

### BciBcrpAuthenticationLogOn
Módulo Java que implementa los protocolos de autenticación requeridos por LBTR.

### BciBcrpCompraVentaME
Módulo Java para la transacción de compra y venta de divisas en el sistema LBTR.

### BciBcrpMensajeriaNoFinanciera
Módulo Java que implementa la lógica de negocios para el envío y recepción de mensajes no financieros a través de LBTR.

### BciBcrpStatusAbonoVal
Módulo Java que se encarga de la validación del estado de las transferencias en LBTR. Proporciona mensajes de error en caso de fallas.

### BciBcrpTransferPO
Módulo Java que verifica el estado de las transferencias LBTR.

### BciBcrpTransferencias
Módulo Java para la gestión de las transacciones de transferencia LBTR.

### BciBcrpWebConsultas
Módulo Java para la implementación de consultas operacionales LBTR a través de interfaces web.

### Bci13g3Anulacion
Módulo Java para la gestión de la cancelación de las transferencias LBTR.

## Instalación y Uso

Por favor, consulte la documentación de cada subproyecto para obtener instrucciones detalladas de configuración, compilación, despliegue y uso.

## Contribución

Apreciamos su interés en mejorar el proyecto BCI_G3_PROJECT. Consulte las directrices de contribución antes de enviar un pull request.


# Autores

Este proyecto no fuese posible, sin la ayuda y participacion de este excelente equipo de trabajo.

* [@damaigualcaNagarro](https://gitlab.com/damaigualcaNagarro)
* [@David_KtL](https://gitlab.com/David_KtL)
* [@andrea.vaca](https://gitlab.com/andrea.vaca)
* [@dsgallegos](https://gitlab.com/dsgallegos)
* [@HaroldNag](https://gitlab.com/HaroldNag)


## Licencia

BCI_G3_PROJECT está licenciado bajo Nagarro EC. 
