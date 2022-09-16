package Mapper;

import Entity.OrderItem;
import Injector.IInjector;
import Util.Util;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderItemMapper extends GeneralMapper<OrderItem> {
    @Override
    public boolean insert(OrderItem TEntity) {
        try {
            insertOperation(TEntity, false);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private void insertOperation(OrderItem oi, boolean shouldReturn) throws SQLException {
        PreparedStatement statement;

        if(conn == null) {
            conn = Util.getConnection();
        }

        statement = conn.prepareStatement(
                "INSERT INTO orderitems (orderid, listingid, quantity, unitprice) " +
                        "VALUES (?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS
        );

        statement.setInt(1, oi.getOrderId());
        statement.setInt(2, oi.getListingId());
        statement.setInt(3, oi.getQuantity());
        statement.setBigDecimal(4, oi.getUnitPrice().getNumberStripped());
        statement.execute();
    }

    @Override
    public boolean modify(OrderItem TEntity) {
        try {
            if(conn == null) {
                conn = Util.getConnection();
            }

            PreparedStatement statement = conn.prepareStatement(
                    "UPDATE orderitems as oi set " +
                            "orderid=oi2.orderid, " +
                            "listingid=oi2.listingid, " +
                            "quantity=oi2.quantity, " +
                            "unitprice=oi2.unitprice " +
                            "from (values " +
                            "(?, ?, ?, ?)" +
                            ") as oi2(orderId, listingId, quantity, unitPrice) " +
                            "where oi.listingid=oi2.listingid and oi.orderid=oi2.orderid;"
            );
            statement.setInt(1, TEntity.getOrderId());
            statement.setInt(2, TEntity.getListingId());
            statement.setInt(3, TEntity.getQuantity());
            statement.setBigDecimal(4, TEntity.getUnitPrice().getNumberStripped());
            statement.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public OrderItem find(IInjector injector, List<Object> queryParam) {
        OrderItem oi = new OrderItem();
        try {
            ResultSet rs = getResultSet(injector, queryParam);
            if(rs.next()) {
                oi.setOrderId(rs.getInt("orderId"));
                oi.setListingId(rs.getInt("listingId"));
                oi.setQuantity(rs.getInt("quantity"));
                oi.setUnitPrice(Money.of(rs.getBigDecimal("unitPrice"), Monetary.getCurrency("AUD")));
            }
        } catch (SQLException e) {
            return null;
        }
        return oi;
    }

    @Override
    public List<OrderItem> findMulti(IInjector injector, List<Object> queryParam) {
        List<OrderItem> oiList = new ArrayList<>();
        try {
            ResultSet rs = getResultSet(injector, queryParam);
            while(rs.next()) {
                OrderItem oi = new OrderItem();
                oi.setOrderId(rs.getInt("orderId"));
                oi.setListingId(rs.getInt("listingId"));
                oi.setQuantity(rs.getInt("quantity"));
                oi.setUnitPrice(Money.of(rs.getBigDecimal("unitPrice"), Monetary.getCurrency("AUD")));
                oiList.add(oi);
            }
        } catch (SQLException e) {
            return null;
        }
        return oiList;
    }
}
