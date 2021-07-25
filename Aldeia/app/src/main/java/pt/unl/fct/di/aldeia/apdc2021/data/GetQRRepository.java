package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.QRCode;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;

public class GetQRRepository {
    private static volatile GetQRRepository instance;

    private final ParticipantQRCodeDataSource participantQRCodeDataSource;
    private final LeaveQRCodeDataSource leaveQRCodeDataSource;

    // private constructor : singleton access
    private GetQRRepository(ParticipantQRCodeDataSource participantQRCodeDataSource, LeaveQRCodeDataSource leaveQRCodeDataSource) {
        this.leaveQRCodeDataSource = leaveQRCodeDataSource;
        this.participantQRCodeDataSource = participantQRCodeDataSource;

    }

    public static GetQRRepository getInstance(ParticipantQRCodeDataSource participantQRCodeDataSource, LeaveQRCodeDataSource leaveQRCodeDataSource) {
        if (instance == null) {
            instance = new GetQRRepository(participantQRCodeDataSource,leaveQRCodeDataSource);
        }
        return instance;
    }



    public Result<QRCode> participantQRCode(GetEventData data) {
        Result<QRCode> result = participantQRCodeDataSource.getQRCode(data);
        return result;
    }

    public Result<QRCode> leaveQRCode(GetEventData data) {
        Result<QRCode> result = leaveQRCodeDataSource.getQRCode(data);
        return result;
    }
}
