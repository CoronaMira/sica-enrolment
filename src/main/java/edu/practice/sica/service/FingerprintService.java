package edu.practice.sica.service;

import com.zkteco.biometric.FingerprintSensorEx;
import edu.practice.sica.entity.Fingerprint;
import edu.practice.sica.repository.FingerprintRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FingerprintService {

    private final FingerprintRepository fingerprintRepository;

    public FingerprintService(FingerprintRepository fingerprintRepository) {
        this.fingerprintRepository = fingerprintRepository;
    }


    @Transactional
    public Fingerprint createFingerprint(Fingerprint fingerprint) {
        return fingerprintRepository.save(fingerprint);
    }


    public List<Fingerprint> getFingerprintsByStudentId(Integer studentId) {
        return fingerprintRepository.findByStudentId(studentId);
    }


    public Optional<Fingerprint> getFingerprintById(Integer fingerprintId) {
        return fingerprintRepository.findById(fingerprintId);
    }

    public List<Fingerprint> getAllFingerprints() {
        return fingerprintRepository.findAll();
    }


    @Transactional
    public void deleteFingerprint(Integer fingerprintId) {
        // Opcional: verificar si existe antes de borrar para poder lanzar una excepción si no se encuentra.
        if (fingerprintRepository.findById(fingerprintId).isEmpty()) {
            throw new IllegalStateException("Fingerprint with ID " + fingerprintId + " not found.");
        }
        fingerprintRepository.delete(fingerprintId);
    }

    public Optional<Fingerprint> identify(byte[] capturedTemplate, long mhDB) {

        List<Fingerprint> allFingerprints = fingerprintRepository.findAll(); // Asumiendo que tienes un método así.

        for (Fingerprint storedFingerprint : allFingerprints) {
            byte[] storedTemplate = storedFingerprint.getFingerprintData();

            int score = FingerprintSensorEx.DBMatch(mhDB, capturedTemplate, storedTemplate);

            if (score > 55) {
                log.info("¡Coincidencia encontrada! Devuelve el registro de la huella.");
                return Optional.of(storedFingerprint);
            }
        }
        return Optional.empty();
    }
}