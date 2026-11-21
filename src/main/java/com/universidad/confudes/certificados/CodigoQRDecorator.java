package com.universidad.confudes.certificados;

public class CodigoQRDecorator extends CertificadoDecorator {
    private final String urlVerificacion;

    public CodigoQRDecorator(ServicioCertificados envoltorio, String urlVerificacion) {
        super(envoltorio);
        this.urlVerificacion = urlVerificacion;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] pdfBase = super.emitir(solicitud);
        return UtilidadesPDF.insertarCodigoQR(pdfBase, urlVerificacion);
    }
}
