import database.Connect;
import model.Card;
import model.Gopay;
import model.Jago;

public class CardFactory {
    Connect connect = Connect.getConnection();
    String query;

    public Card makeCard(String cardType) {
        if (cardType.toLowerCase().equals("gopay")) {
            query = "INSERT INTO card VALUES (NULL, 'gopay', 0)";
            connect.executeUpdate(query);
            return new Gopay(0, "gopay");

        } else if (cardType.toLowerCase().equals("jago")) {
            query = "INSERT INTO card VALUES (NULL, 'jago', 0)";
            connect.executeUpdate(query);
            return new Jago(0, "jago");

        }
        return null;
    }
}
