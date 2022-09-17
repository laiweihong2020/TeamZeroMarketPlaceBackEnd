import Entity.Order;
import Enums.UserRoles;
import MockClasses.MockListingRepository;
import MockClasses.MockOrderRepository;
import Model.OrderModel;
import Util.JWTUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderModelTest {
    private MockOrderRepository orderRepo;
    private MockListingRepository listingRepo;
    private OrderModel orderModel;

    public OrderModelTest() {
        this.orderRepo = new MockOrderRepository();
        this.listingRepo = new MockListingRepository();
        this.orderModel = new OrderModel(orderRepo, listingRepo);
    }

    @Test
    public void cancelOrderInvalidJWT() {
        boolean isSuccessful = orderModel.cancelOrders(new ArrayList<>(), "");
        assertFalse(isSuccessful);
    }

    @Test
    public void cancelOrderTokenWIthInvalidRoles() {
        String jwt = JWTUtil.generateToken("1", new HashMap<>());
        // This jwt should have no roles assigned to it

        boolean isSuccessful = orderModel.cancelOrders(new ArrayList<>(), jwt);
        assertFalse(isSuccessful);
    }

    @Test
    public void NullOrders() {
        Map<String, String> claimMap = new HashMap<>();
        claimMap.put("role", UserRoles.SELLER.toString());
        String jwt = JWTUtil.generateToken("1", claimMap);

        List<Order> orderList = new ArrayList<>();
        orderList.add(null);

        boolean isSuccessful = orderModel.cancelOrders(orderList, jwt);
        assertTrue(isSuccessful); // Order Model should just skip the null order
    }

    @Test
    public void cancelOrderCustomerCancelOwnOrder() {
        this.orderRepo.isUser = true;
        Map<String, String> claimMap = new HashMap<>();
        claimMap.put("role", UserRoles.CUSTOMER.toString());
        String jwt = JWTUtil.generateToken("1", claimMap);

        List<Order> orderList = new ArrayList<>();
        Order order = new Order();
        order.setOrderId(1);
        order.setUserId(1);
        orderList.add(order);

        boolean isSuccessful = orderModel.cancelOrders(orderList, jwt);
        assertTrue(isSuccessful);
    }

    @Test
    public void cancelOrderCustomerCancelOthersOrder() {
        this.orderRepo.isUser = false;
        Map<String, String> claimMap = new HashMap<>();
        claimMap.put("role", UserRoles.CUSTOMER.toString());
        String jwt = JWTUtil.generateToken("1", claimMap);

        List<Order> orderList = new ArrayList<>();
        Order order = new Order();
        order.setOrderId(1);
        order.setUserId(1);
        orderList.add(order);

        boolean isSuccessful = orderModel.cancelOrders(orderList, jwt);
        assertTrue(isSuccessful);
    }

    @Test
    public void cancelOrderSellerCancelOwnOrder() {
        this.listingRepo.isGroup = true;
        Map<String, String> claimMap = new HashMap<>();
        claimMap.put("role", UserRoles.SELLER.toString());
        claimMap.put("groupId", "1");
        String jwt = JWTUtil.generateToken("1", claimMap);

        List<Order> orderList = new ArrayList<>();
        Order order = new Order();
        order.setOrderId(1);
        order.setUserId(1);
        orderList.add(order);

        boolean isSuccessful = orderModel.cancelOrders(orderList, jwt);
        assertTrue(isSuccessful);
    }

    @Test
    public void cancelOrderSellerCancelOtherOrder() {
        this.listingRepo.isGroup = false;
        Map<String, String> claimMap = new HashMap<>();
        claimMap.put("role", UserRoles.SELLER.toString());
        claimMap.put("groupId", "1");
        String jwt = JWTUtil.generateToken("1", claimMap);

        List<Order> orderList = new ArrayList<>();
        Order order = new Order();
        order.setOrderId(1);
        order.setUserId(1);
        orderList.add(order);

        boolean isSuccessful = orderModel.cancelOrders(orderList, jwt);
        assertFalse(isSuccessful);
    }

    @Test
    public void cancelOrderAdminCancelOrder() {
        Map<String, String> claimMap = new HashMap<>();
        claimMap.put("role", UserRoles.ADMIN.toString());
        String jwt = JWTUtil.generateToken("1", claimMap);

        List<Order> orderList = new ArrayList<>();
        Order order = new Order();
        order.setOrderId(1);
        order.setUserId(1);
        orderList.add(order);

        boolean isSuccessful = orderModel.cancelOrders(orderList, jwt);
        assertTrue(isSuccessful);
    }

    @Test
    public void cancelOrderUnknownRoleOrder() {
        Map<String, String> claimMap = new HashMap<>();
        claimMap.put("role", "Something");
        String jwt = JWTUtil.generateToken("1", claimMap);

        List<Order> orderList = new ArrayList<>();
        Order order = new Order();
        order.setOrderId(1);
        order.setUserId(1);

        boolean isSuccessful = orderModel.cancelOrders(orderList, jwt);
        assertFalse(isSuccessful);
    }
}
