package org.example.cinema.entity;

public class Seat {
    public static final String AVAILABLE = "AVAILABLE";
    public static final String SOLD = "SOLD";

    private int id;
    private int roomId;
    private String seatRow;
    private int seatNumber;

    /**
     * Not a stored column: derived per show time from the sold tickets
     * (a seat is SOLD when a tblTicket4 row exists for it).
     */
    private String status = AVAILABLE;

    public Seat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSold() {
        return SOLD.equals(status);
    }

    public String getLabel() {
        return seatRow + seatNumber;
    }
}
