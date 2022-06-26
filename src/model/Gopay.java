package model;

import database.Connect;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Gopay extends Card implements SistemPembayaran {
    Connect connect = Connect.getConnection();
    public Gopay(int saldo, String namaKartu) {
        this.saldo = saldo;
        this.namaKartu = namaKartu;
    }
    @Override

    public void topUp(int saldoTopUp) {
    	this.saldo += saldoTopUp;
        String query = String.format("UPDATE card SET card_balance = %d WHERE card_type = '%s'", this.saldo, this.namaKartu);
        connect.executeUpdate(query);
    }

    @Override
    public void transfer(int saldoTransfer, Card jago) {
        if (this.saldo < saldoTransfer) {
            System.out.println("Saldomu tidak cukup oppa, silahkan top up terlebih dahulu");
            return;
        } else {
            this.saldo -= saldoTransfer;
          
            String query2 = String.format("UPDATE card SET card_balance = %d WHERE card_type = '%s'", this.saldo, this.namaKartu);
            connect.executeUpdate(query2);

            int saldoNow = jago.getSaldo();
            saldoNow += saldoTransfer;
            jago.setSaldo(saldoNow);
            System.out.println("Saldo Gopay anda sisa Rp. "+ this.saldo);
            String query = String.format("UPDATE card SET card_balance = %d WHERE card_type = '%s'", saldoNow, jago.namaKartu);
            connect.executeUpdate(query);
        }
    }


    @Override
    public int cekSaldo() {
        String query = String.format("SELECT card_balance FROM card WHERE card_type = '%s' ", this.namaKartu);
        ResultSet rs = connect.executeQuery(query);

        try {
            rs.next();
            return rs.getInt("card_balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
