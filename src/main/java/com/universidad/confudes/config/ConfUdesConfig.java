package com.universidad.confudes.config;

import com.universidad.confudes.certificados.EnvioCorreoService;
import com.universidad.confudes.certificados.FirmaDigitalService;
import com.universidad.confudes.certificados.GeneradorCertificadoPDF;
import com.universidad.confudes.certificados.ValidadorAsistencia;
import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfUdesConfig {

    @Bean
    public QRCheckClient qrCheckClient() {
        return new QRCheckClient();
    }

    @Bean
    public ValidadorAsistencia validadorAsistencia() {
        return new ValidadorAsistencia();
    }

    @Bean
    public GeneradorCertificadoPDF generadorCertificadoPDF() {
        return new GeneradorCertificadoPDF();
    }

    @Bean
    public FirmaDigitalService firmaDigitalService() {
        return new FirmaDigitalService();
    }

    @Bean
    public EnvioCorreoService envioCorreoService() {
        return new EnvioCorreoService();
    }
}
