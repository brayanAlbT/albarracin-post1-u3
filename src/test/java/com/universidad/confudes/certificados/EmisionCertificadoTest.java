package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmisionCertificadoTest {

    @Test
    void ordenColaboradorOrquestaLasCuatroEtapasSinExcepcion() {
        var validador = new ValidadorAsistencia();
        var generador = new GeneradorCertificadoPDF();
        var firma = new FirmaDigitalService();
        var correo = new EnvioCorreoService();
        ServicioCertificados colaborador = new FachadaCertificados(validador, generador, firma, correo);

        // La operación simple expuesta por su colaborador debe ejecutarse sin lanzar excepciones
        assertDoesNotThrow(() -> {
            colaborador.emitir(new SolicitudCertificado("EVT-001", "PART-123", "Carlos Gómez", "carlos@mail.com"));
        });
    }

    @Test
    void controladorCertificadosSoloDependeDeUnColaborador() {
        // Verificación de diseño: ControladorCertificados debe declarar un solo
        // constructor con un solo parámetro tras la refactorización.
        var constructores = ControladorCertificados.class.getDeclaredConstructors();
        assertEquals(1, constructores.length);
        assertEquals(1, constructores[0].getParameterCount());
    }
}
