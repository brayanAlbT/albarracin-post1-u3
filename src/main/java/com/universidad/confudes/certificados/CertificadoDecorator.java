package com.universidad.confudes.certificados;

/**
 * Patrón Estructural: Decorator (Decorador Base)
 * Mantiene una referencia al componente ServicioCertificados y conforma su misma interfaz.
 */
public abstract class CertificadoDecorator implements ServicioCertificados {
    protected final ServicioCertificados envoltorio;

    public CertificadoDecorator(ServicioCertificados envoltorio) {
        this.envoltorio = envoltorio;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        return envoltorio.emitir(solicitud);
    }
}
