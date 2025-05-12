package org.prolab2_1.prolab2_1.sistem;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Transfer {
    private String transferStopId; //aktarma yapılacak durak ID'si
    private int transferSure; //aktarma suresi dakika
    private double transferUcret; //aktarma ucreti tl

    @JsonCreator
    public Transfer(
            @JsonProperty("transferStopId") String transferStopId,
            @JsonProperty("transferSure") int transferSure,
            @JsonProperty("transferUcret") double transferUcret
    ) {
        this.transferStopId = transferStopId;
        this.transferSure = transferSure;
        this.transferUcret = transferUcret;
    }
    public Transfer() {
    }

    public String getTransferStopId() {
        return transferStopId;
    }
    public void setTransferStopId(String transferStopId) {
        this.transferStopId = transferStopId;
    }

    public int getTransferSure() {
        return transferSure;
    }
    public void setTransferSure(int transferSure) {
        this.transferSure = transferSure;
    }

    public double getTransferUcret() {
        return transferUcret;
    }
    public void setTransferUcret(double transferUcret) {
        this.transferUcret = transferUcret;
    }
}
/*json dosyasında 'transferStopId' gibi dogrudan alanlar oldugu icin parametreli constructor+@jsoncreator tek seferde
bu verileri Transfer nesnesine alır */