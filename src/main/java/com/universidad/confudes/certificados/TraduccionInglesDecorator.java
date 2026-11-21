package com.universidad.confudes.certificados;

public class TraduccionInglesDecorator extends CertificadoDecorator {

    public TraduccionInglesDecorator(ServicioCertificados envoltorio) {
        super(envoltorio);
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] pdfBase = super.emitir(solicitud);
        return UtilidadesPDF.traducirAIngles(pdfBase);
    }
}
