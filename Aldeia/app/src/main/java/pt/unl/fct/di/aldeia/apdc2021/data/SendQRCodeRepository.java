package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EarnedAmmount;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SendQRCodeData;

public class SendQRCodeRepository {
    private static volatile SendQRCodeRepository instance;

    private final SendPresenceConfirmationDataSource sendPresenceConfirmationDataSource;
    private final SendLeaveConfirmationDataSource sendLeaveConfirmationDataSource;


    // private constructor : singleton access
    private SendQRCodeRepository(SendPresenceConfirmationDataSource sendPresenceConfirmationDataSource, SendLeaveConfirmationDataSource sendLeaveConfirmationDataSource) {
        this.sendPresenceConfirmationDataSource=sendPresenceConfirmationDataSource;
        this.sendLeaveConfirmationDataSource=sendLeaveConfirmationDataSource;

    }

    public static SendQRCodeRepository getInstance(SendPresenceConfirmationDataSource sendPresenceConfirmationDataSource, SendLeaveConfirmationDataSource sendLeaveConfirmationDataSource) {
        if (instance == null) {
            instance = new SendQRCodeRepository(sendPresenceConfirmationDataSource,sendLeaveConfirmationDataSource);
        }
        return instance;
    }


    public Result<Void> sendConfirmationQRCode(SendQRCodeData data) {
        return sendPresenceConfirmationDataSource.sendPresenceConfirmation(data);
    }

    public Result<EarnedAmmount> sendLeaveQRCode(SendQRCodeData data) {
        return sendLeaveConfirmationDataSource.sendLeaveConfirmation(data);
    }
}
