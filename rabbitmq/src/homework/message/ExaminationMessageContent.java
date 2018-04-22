package homework.message;

import homework.ExaminationStatus;
import homework.InjuryType;

public class ExaminationMessageContent implements MessageContent {
    private InjuryType injuryType;
    private String patientName;
    private ExaminationStatus examinationStatus;

    public ExaminationMessageContent() {}

    public ExaminationMessageContent(InjuryType injuryType, String patientName, ExaminationStatus examinationStatus) {
        this.injuryType = injuryType;
        this.patientName = patientName;
        this.examinationStatus = examinationStatus;
    }

    public InjuryType getInjuryType() {
        return injuryType;
    }

    public void setInjuryType(InjuryType injuryType) {
        this.injuryType = injuryType;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public ExaminationStatus getExaminationStatus() {
        return examinationStatus;
    }

    public void setExaminationStatus(ExaminationStatus examinationStatus) {
        this.examinationStatus = examinationStatus;
    }

    @Override
    public String toString() {
        return injuryType + ", " + patientName + ", " + examinationStatus;
    }
}
