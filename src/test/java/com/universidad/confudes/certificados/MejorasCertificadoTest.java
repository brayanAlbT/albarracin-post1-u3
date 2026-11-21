package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MejorasCertificadoTest {
    private final SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana Ríos", "ana@correo.com");

    @Test
    void emiteSinNingunaMejoraActivada() {
        ServicioCertificados base = new FachadaCertificados(
            new ValidadorAsistencia(),
            new GeneradorCertificadoPDF(),
            new FirmaDigitalService(),
            new EnvioCorreoService()
        );
        assertDoesNotThrow(() -> base.emitir(solicitud));
    }

    @Test
    void combinaLasTresMejorasSinCrearUnaClaseNueva() {
        ServicioCertificados base = new FachadaCertificados(
            new ValidadorAsistencia(),
            new GeneradorCertificadoPDF(),
            new FirmaDigitalService(),
            new EnvioCorreoService()
        );
        ServicioCertificados conTodo = new TraduccionInglesDecorator(
            new CodigoQRDecorator(
                new MarcaDeAguaDecorator(base, "CONF-UDES 2026"),
                "https://confudes.edu.co/verify"
            )
        );
        assertDoesNotThrow(() -> conTodo.emitir(solicitud));
    }

    @Test
    void unaSolaMejoraFuncionaDeFormaIndependiente() {
        ServicioCertificados base = new FachadaCertificados(
            new ValidadorAsistencia(),
            new GeneradorCertificadoPDF(),
            new FirmaDigitalService(),
            new EnvioCorreoService()
        );
        ServicioCertificados soloMarcaDeAgua = new MarcaDeAguaDecorator(base, "CONF-UDES 2026");
        assertDoesNotThrow(() -> soloMarcaDeAgua.emitir(solicitud));
    }
}
