package com.universidad.confudes.acceso;

import com.universidad.confudes.certificados.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class AccesoDescargaMasivaTest {

    @AfterEach
    void limpiarRol() {
        System.clearProperty("confudes.rol");
    }

    @Test
    void rechazaAParticipanteSinLlegarAEmitir() {
        System.setProperty("confudes.rol", "PARTICIPANTE");
        ServicioCertificados base = new FachadaCertificados(
            new ValidadorAsistencia(),
            new GeneradorCertificadoPDF(),
            new FirmaDigitalService(),
            new EnvioCorreoService()
        );
        ServicioCertificados controlado = new CertificadosSecurityProxy(base);
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertThrows(SecurityException.class, () -> controlado.emitir(solicitud));
    }

    @Test
    void permiteAOrganizador() {
        System.setProperty("confudes.rol", "ORGANIZADOR");
        ServicioCertificados base = new FachadaCertificados(
            new ValidadorAsistencia(),
            new GeneradorCertificadoPDF(),
            new FirmaDigitalService(),
            new EnvioCorreoService()
        );
        ServicioCertificados controlado = new CertificadosSecurityProxy(base);
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertDoesNotThrow(() -> controlado.emitir(solicitud));
    }
}
