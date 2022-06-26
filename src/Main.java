/*
Anggota :
DAMARIO - 2440028792
TUASAMU, RAFFAEL HIZQYA BAKHTIAR ALI MAULANA - 2440117122
CHRISTOPHER CHANDRA WIDJAJA - 2440025292

Design Pattern : 
- Singleton --> Terdapat pada implementasi DB class database.Connect
- Factory --> Terdapat pada implementasi CardFactory untuk membuat card gopay dan jago.

DataBase Transaction :
- Terdapat pada implementasi method makeCard, TopUp, cekSaldo, Tranfer.

Implementasi OOP concept: 
- Inheritance (Done)
- Interface (Done)
- Abstract (Done)
- Encapsulation (Done)

*NOTE : untuk menggunakan aplikasi ini, dibutuhkan XAMPP, DB mySQL dengan nama "gojek" dan import dari "gojek.sql" untuk 
memenuhi kebutuhan DataBase.
*/
import database.Connect;
import model.Card;
import model.Gopay;
import model.Jago;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    // Scanner to get input from user
    static Scanner sc = new Scanner(System.in);

    // db connection
    static Connect connect;

    // Cards
    static ArrayList<Card> cards = new ArrayList<>();

    // card factory
    static CardFactory cardFactory;
    public static void main(String[] args) {
        connect = Connect.getConnection();
        cardFactory = new CardFactory();
        updateCard();

        // display and get user menu choice
        int menu = 0;
        do {
            menu();
            menu = sc.nextInt();
            sc.nextLine();

            // menu application
            switch (menu) {
                case 1 -> {
                    cards.add(makeCard());}
                case 2 -> {
                    menuTransferSaldo();
                }
                case 3 -> {
                    menuCekSaldo();
                }
                case 4 -> {
                    menuTopUp();
                }
            }


        } while (menu != 5);
    }

    private static void updateCard() {
        String query = String.format("SELECT * FROM card");
        ResultSet rs  = connect.executeQuery(query);

        try {
            while (rs.next()) {
                if (rs.getString("card_type").equals("gopay")) {
                    cards.add(new Gopay(rs.getInt("card_balance"), rs.getString("card_type")));
                } else  if( rs.getString("card_type").equals("jago")) {
                    cards.add(new Jago(rs.getInt("card_balance"), rs.getString("card_type")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void menu() {
        System.out.println("1. Buat Kartu");
        System.out.println("2. Transfer Saldo");
        System.out.println("3. Cek Saldo");
        System.out.println("4. Top Up");
        System.out.println("5. Exit");
    }

    private static void menuTransferSaldo() {
        if (!checkPunyaKartuJago() || !checkPunyaKartuGopay()) {
            System.out.println("Kamu harus punya dua kartu oppa, coba bikin dulu ya");
            return;
        }

        System.out.println("Mau transfer dari kartu apa oppa?");
        System.out.println("1. Gopay");
        System.out.println("2. Jago");
        int menu = sc.nextInt();sc.nextLine();

        System.out.println("Berapa oppa?");
        System.out.printf("Rp. ");
        int saldo = sc.nextInt();sc.nextLine();
        
        Gopay cardGopay = null;
        Jago cardJago = null;
        
        
        for (Card card : cards) {
			if(card instanceof Gopay) {
				cardGopay = (Gopay)card;
			}
			else if (card instanceof Jago) {
				cardJago = (Jago)card;
			}
		}
        
        if(menu == 1) {
        	cardGopay.transfer(saldo, cardJago);
        }else {
        	cardJago.transfer(saldo, cardGopay);
        }
    }

    private static Card makeCard() {

        System.out.println("Mau buat kartu apa oppa?");
        System.out.println("1. Gopay");
        System.out.println("2. Jago");
        int menu = sc.nextInt();sc.nextLine();

        if (menu == 1) {
            boolean punya = checkSudahPunya("gopay");
            if (!punya) {
                Card card = cardFactory.makeCard("gopay");
                return card;
            }
        } else {
            boolean punya = checkSudahPunya("jago");
            if (!punya) {
                Card card = cardFactory.makeCard("jago");
                return card;
            }
        }
        System.out.println("Kamu sudah punya kartu ini oppa");

        return null;
    }
    private static void menuCekSaldo() {

        System.out.println("Mau check kartu apa oppa?");
        System.out.println("1. Gopay");
        System.out.println("2. Jago");
        int menu = sc.nextInt();sc.nextLine();

        if (menu == 1) {
            if (!checkPunyaKartuGopay()) {
                System.out.println("Bikin kartu gopay dulu oppa");
                return;
            }
            for (Card card :
                    cards) {
                if (card instanceof Gopay cardGopay) {
                    System.out.println("Saldo anda Rp. "+ cardGopay.cekSaldo()); 
                }
            }
        } else {
            if (!checkPunyaKartuJago()) {
                System.out.println("Bikin kartu jago dulu oppa");
                return;
            }
            for (Card card :
                    cards) {
                if (card instanceof Jago cardJago) {
                    System.out.println("Saldo anda Rp. "+ cardJago.cekSaldo());
                }
            }
        }

    }
    private static void menuTopUp() {

        System.out.println("Mau topUp kartu apa oppa?");
        System.out.println("1. Gopay");
        System.out.println("2. Jago");
        int menu = sc.nextInt();sc.nextLine();

        System.out.println("Berapa oppa?");
        System.out.print("Rp. ");
        int saldo = sc.nextInt();sc.nextLine();

        if (menu == 1) {
            if (!checkPunyaKartuGopay()) {
                System.out.println("Bikin kartu gopay dulu oppa");
                return;
            }
            for (Card card :
                    cards) {
                if (card instanceof Gopay cardGopay) {
                    cardGopay.topUp(saldo);
                }
            }
        } else {
            if (!checkPunyaKartuJago()) {
                System.out.println("Bikin kartu jago dulu oppa");
                return;
            }
            for (Card card :
                    cards) {
                if (card instanceof Gopay cardJago) {
                    cardJago.topUp(saldo);
                }
            }
        }

    }

    private static boolean checkSudahPunya(String cek) {
        ArrayList<String> cards = new ArrayList<>();

        String query = String.format("SELECT card_type FROM card WHERE card_type = '%s' ", cek);
        ResultSet rs  = connect.executeQuery(query);

        try {
            while (rs.next()) {
                cards.add(rs.getString("card_type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String card :
                cards) {
            if (card.equals(cek)) {
                return true;
            }
        }
        return false;
    }
    private static boolean checkPunyaKartuGopay() {
        boolean punya = true;

        punya = checkSudahPunya("gopay");
        return punya;
    }
    private static boolean checkPunyaKartuJago() {
        boolean punya = true;

        punya = checkSudahPunya("jago");
        return punya;
    }

}
