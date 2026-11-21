package com.universidad.confudes.certificados;

public class MarcaDeAguaDecorator extends CertificadoDecorator {
    private final String textoMarca;

    public MarcaDeAguaDecorator(ServicioCertificados envoltorio, String textoMarca) {
        super(envoltorio);
        this.textoMarca = textoMarca;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] pdfBase = super.emitir(solicitud);
        return UtilidadesPDF.aplicarMarcaDeAgua(pdfBase, textoMarca);
    }
}
