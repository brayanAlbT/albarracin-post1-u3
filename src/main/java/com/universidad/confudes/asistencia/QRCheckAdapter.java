package com.universidad.confudes.asistencia;

import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import com.universidad.confudes.externo.qrcheck.QRCheckRequest;
import com.universidad.confudes.externo.qrcheck.QRCheckResponse;
import org.springframework.stereotype.Service;

/**
 * Patrón Estructural: Adapter (Adaptador de Objetos)
 * Adapta la interfaz incompatible del SDK de terceros QRCheckClient
 * al contrato interno ServicioAsistencia requerido por ConfUDES.
 */
@Service
public class QRCheckAdapter implements ServicioAsistencia {
    private final QRCheckClient qrCheckClient;

    public QRCheckAdapter(QRCheckClient qrCheckClient) {
        this.qrCheckClient = qrCheckClient;
    }

    @Override
    public ResultadoCheckIn registrarAsistencia(String eventoId, String participanteId, String credencialQR) {
        long idEventoNumerico;
        try {
            String digitos = eventoId.replaceAll("\\D+", "");
            idEventoNumerico = digitos.isEmpty() ? Math.abs((long) eventoId.hashCode()) : Long.parseLong(digitos);
        } catch (NumberFormatException e) {
            idEventoNumerico = Math.abs((long) eventoId.hashCode());
        }

        QRCheckRequest request = new QRCheckRequest(credencialQR, idEventoNumerico);
        QRCheckResponse response = qrCheckClient.validar(request);

        boolean exitoso = response.getCodigoRespuesta() == 200;
        return new ResultadoCheckIn(exitoso, response.getDetalle());
    }
}
