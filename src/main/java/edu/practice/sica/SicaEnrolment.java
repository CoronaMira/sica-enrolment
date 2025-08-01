package edu.practice.sica;

import com.zkteco.biometric.SicaEnrollmentTool;
import edu.practice.sica.service.FingerprintService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.SwingUtilities;


@SpringBootApplication
public class SicaEnrolment {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(SicaEnrolment.class)
                .headless(false)
                .run(args);
        FingerprintService fingerprintService = context.getBean(FingerprintService.class);

        SwingUtilities.invokeLater(() -> {
            new SicaEnrollmentTool(fingerprintService);
        });
    }
}



