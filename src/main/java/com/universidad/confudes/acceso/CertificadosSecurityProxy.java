package com.universidad.confudes.acceso;

import com.universidad.confudes.certificados.ServicioCertificados;
import com.universidad.confudes.certificados.SolicitudCertificado;

/**
 * Patrón Estructural: Proxy (Protection Proxy)
 * Controla el acceso a la operación costosa de emisión de certificados,
 * interceptando la solicitud y verificando que el usuario posea rol ORGANIZADOR o ADMIN
 * antes de delegar en el servicio real.
 */
public class CertificadosSecurityProxy implements ServicioCertificados {
    private final ServicioCertificados servicioReal;

    public CertificadosSecurityProxy(ServicioCertificados servicioReal) {
        this.servicioReal = servicioReal;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        String rol = ContextoUsuario.rolActual();
        if (!"ORGANIZADOR".equalsIgnoreCase(rol) && !"ADMIN".equalsIgnoreCase(rol)) {
            throw new SecurityException("Acceso denegado: se requiere rol ORGANIZADOR o ADMIN para esta operación");
        }
        return servicioReal.emitir(solicitud);
    }
}
