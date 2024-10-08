package com.sd38.gymtiger.controller.user;

import com.google.gson.JsonObject;
import com.sd38.gymtiger.model.Address;
import com.sd38.gymtiger.model.Bill;
import com.sd38.gymtiger.model.BillDetail;
import com.sd38.gymtiger.model.Cart;
import com.sd38.gymtiger.model.Customer;
import com.sd38.gymtiger.model.PaymentMethod;
import com.sd38.gymtiger.model.SessionCart;
import com.sd38.gymtiger.model.Voucher;
import com.sd38.gymtiger.response.MomoPaymentResponse;
import com.sd38.gymtiger.response.VNPaymentResponse;
import com.sd38.gymtiger.response.ZaloPaymentResponse;
import com.sd38.gymtiger.service.AddressService;
import com.sd38.gymtiger.service.BillService;
import com.sd38.gymtiger.service.CartService;
import com.sd38.gymtiger.service.CustomerService;
import com.sd38.gymtiger.service.PaymentMethodService;
import com.sd38.gymtiger.service.PaymentService;
import com.sd38.gymtiger.service.VoucherService;
import com.sd38.gymtiger.utils.GHNUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;

@Controller
public class UserBillController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private BillService billService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private GHNUtil ghnUtil;

    JsonObject jsonData = new JsonObject();

    @GetMapping("/checkout")
    public String checkOut(Principal principal, Model model, HttpSession session, RedirectAttributes attributes) {
        if (principal == null) {
            SessionCart sessionCart = (SessionCart) session.getAttribute("sessionCart");
            if (sessionCart == null){
                return "redirect:/cart";
            }
            cartService.reloadCartDetailSession(sessionCart);
            session.setAttribute("sessionCart", sessionCart);
            if (sessionCart.getCartDetails().isEmpty()){
                attributes.addFlashAttribute("error", "Các sản phẩm trong giỏ hàng của bạn đã hết hàng");
                return "redirect:/cart";
            }
            session.setAttribute("totalItems", sessionCart.getTotalItems());
            List<Voucher> listVoucher = voucherService.getVoucherByCartPrice(sessionCart.getTotalPrice());
            model.addAttribute("cart", sessionCart);
            model.addAttribute("listVoucher", listVoucher);
            model.addAttribute("loggedIn", false);
            model.addAttribute("email", "");
        } else {
            Customer customer = customerService.findByEmail(principal.getName());
            Cart cart = cartService.getCart(principal.getName());
            if (cart == null){
                return "redirect:/cart";
            }
            cartService.reloadCartDetail(cart);
            if (cart.getCartDetails().isEmpty()){
                attributes.addFlashAttribute("error", "Các sản phẩm trong giỏ hàng của bạn đã hết hàng");
                return "redirect:/cart";
            }
            session.setAttribute("totalItems", cart.getTotalItems());
            Address defaultAddress = addressService.findAccountDefaultAddress(customer.getId());
            List<Address> listAddress = addressService.findAccountAddress(customer.getId());
            List<Voucher> listVoucher = voucherService.getVoucherByCartPrice(cart.getTotalPrice());
            model.addAttribute("cart", cart);
            model.addAttribute("listVoucher", listVoucher);
            model.addAttribute("loggedIn", true);
            model.addAttribute("email", principal.getName());
            model.addAttribute("defaultAddress", defaultAddress);
            model.addAttribute("listAddress", listAddress);
        }
        return "/user/checkout";
    }

    @GetMapping("/orders")
    public String getOrders(Model model, Principal principal, @RequestParam(required = false) String status) {
        if (principal == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByEmail(principal.getName());
        List<Bill> listBill;
        if (status == null || status.trim().isEmpty()){
            listBill = billService.getAllOrders(customer.getId());
        } else {
            listBill = billService.getStatusOrders(Integer.valueOf(status), customer.getId());
        }
        model.addAttribute("orders", listBill);
        return "/user/order";
    }

    @GetMapping("/order-detail/{id}")
    public String getOrderDetail(@PathVariable Integer id, Model model, Principal principal, RedirectAttributes attributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByEmail(principal.getName());
        Bill bill = billService.getOneBill(id);
        if (bill == null){
            attributes.addFlashAttribute("error", "Không có thông tin đơn hàng tương ứng");
            return "redirect:/orders";
        }
        if (customer.getId().equals(bill.getCustomer().getId())){
            List<BillDetail> lstBillDetails = billService.getLstDetailByBillId(id);
            List<PaymentMethod> listPaymentMethod = paymentMethodService.getAllBillPaymentMethod(id);
            model.addAttribute("order", bill);
            model.addAttribute("lstBillDetails", lstBillDetails);
            model.addAttribute("listPaymentMethod", listPaymentMethod);
            return "/user/order-detail";
        } else {
            attributes.addFlashAttribute("error", "Bạn không có quyền xem đơn hàng này");
            return "redirect:/orders";
        }
    }

    @RequestMapping(value = "/cancel-order/{id}", method = {RequestMethod.PUT, RequestMethod.GET})
    public String cancelOrder(@PathVariable Integer id, Principal principal, RedirectAttributes attributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByEmail(principal.getName());
        Bill bill = billService.getOneBill(id);
        if (bill == null){
            attributes.addFlashAttribute("error", "Bạn không có quyền huỷ đơn này");
            return "redirect:/orders";
        }
        if (customer.getId().equals(bill.getCustomer().getId())){
            boolean check = billService.userCancelOrder(id);
            if (check){
                attributes.addFlashAttribute("mess", "Huỷ đơn hàng thành công");
            } else {
                attributes.addFlashAttribute("error", "Bạn không có quyền huỷ đơn này");
            }
        } else {
            attributes.addFlashAttribute("error", "Bạn không có quyền huỷ đơn này");
        }
        return "redirect:/orders";
    }

    @RequestMapping(value = "/detail-cancel-order/{id}", method = {RequestMethod.PUT, RequestMethod.GET})
    public String detailCancelOrder(@PathVariable Integer id, Principal principal, RedirectAttributes attributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByEmail(principal.getName());
        Bill bill = billService.getOneBill(id);
        if (bill == null){
            attributes.addFlashAttribute("error", "Bạn không có quyền huỷ đơn này");
            return "redirect:/orders";
        }
        if (customer.getId().equals(bill.getCustomer().getId())){
            boolean check = billService.userCancelOrder(id);
            if (check){
                attributes.addFlashAttribute("mess", "Huỷ đơn hàng thành công");
            } else {
                attributes.addFlashAttribute("error", "Bạn không có quyền huỷ đơn này");
            }
            return "redirect:/order-detail/" + id;
        } else {
            attributes.addFlashAttribute("error", "Bạn không có quyền huỷ đơn này");
            return "redirect:/orders";
        }
    }

    @PostMapping("/add-order")
    public String createOrder(Principal principal,
                              RedirectAttributes attributes,
                              HttpSession session,
                              @RequestParam("name") String name,
                              @RequestParam("email") String email,
                              @RequestParam("phoneNumber") String phoneNumber,
                              @RequestParam("specificAddress") String specificAddress,
                              @RequestParam("city") String city,
                              @RequestParam("district") String district,
                              @RequestParam("ward") String ward,
                              @RequestParam("payment") String payment,
                              @RequestParam(value = "voucher", required = false) Integer voucher,
                              HttpServletRequest request) throws IOException, URISyntaxException {
        if (principal == null){
            SessionCart sessionCart = (SessionCart) session.getAttribute("sessionCart");
            cartService.reloadCartDetailSession(sessionCart);
            session.setAttribute("sessionCart", sessionCart);
            if (sessionCart.getCartDetails().isEmpty()){
                attributes.addFlashAttribute("error", "Các sản phẩm trong giỏ hàng của bạn đã hết hàng");
                return "redirect:/cart";
            }
            if (payment.equals("VNPAY")){
                BigDecimal shippingFee = ghnUtil.calculateShippingFee(city, district, ward, sessionCart.getTotalItems());
                //Long dok
                if (voucher == null){
                    BigDecimal totalPrice = sessionCart.getTotalPrice().add(shippingFee);
                    jsonData = paymentService.vnpayCreate(request, totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, null);
                } else {
                    Voucher uuDai = voucherService.getVoucherById(voucher);
                    if (uuDai.getStatus() != 1){
                        attributes.addFlashAttribute("error", "Voucher đã hết số lượng hoặc hết hạn");
                        return "redirect:/checkout";
                    }
                    if (sessionCart.getTotalPrice().compareTo(uuDai.getMinimumPrice()) < 0){
                        attributes.addFlashAttribute("error", "Đơn hàng không đủ giá tối thiểu của voucher");
                        return "redirect:/checkout";
                    }
                    BigDecimal totalPrice = sessionCart.getTotalPrice().add(shippingFee).subtract(uuDai.getValue());
                    jsonData = paymentService.vnpayCreate(request, totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, String.valueOf(voucher));
                }
                return "redirect:" + jsonData.get("payUrl").toString().replaceAll("\"", "");
            }
            if (payment.equals("Momo")){
                BigDecimal shippingFee = ghnUtil.calculateShippingFee(city, district, ward, sessionCart.getTotalItems());
                //Long dok
                if (voucher == null){
                    BigDecimal totalPrice = sessionCart.getTotalPrice().add(shippingFee);
                    jsonData = paymentService.MomoPayCreate(totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, null);
                } else {
                    Voucher uuDai = voucherService.getVoucherById(voucher);
                    if (uuDai.getStatus() != 1){
                        attributes.addFlashAttribute("error", "Voucher đã hết số lượng hoặc hết hạn");
                        return "redirect:/checkout";
                    }
                    if (sessionCart.getTotalPrice().compareTo(uuDai.getMinimumPrice()) < 0){
                        attributes.addFlashAttribute("error", "Đơn hàng không đủ giá tối thiểu của voucher");
                        return "redirect:/checkout";
                    }
                    BigDecimal totalPrice = sessionCart.getTotalPrice().add(shippingFee).subtract(uuDai.getValue());
                    jsonData = paymentService.MomoPayCreate(totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, String.valueOf(voucher));
                }
                return "redirect:" + jsonData.get("payUrl").toString().replaceAll("\"", "");
            }
            if (payment.equals("ZaloPay")){
                BigDecimal shippingFee = ghnUtil.calculateShippingFee(city, district, ward, sessionCart.getTotalItems());
                //Long dok
                if (voucher == null){
                    BigDecimal totalPrice = sessionCart.getTotalPrice().add(shippingFee);
                    jsonData = paymentService.zalopayCreate(totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, null);
                } else {
                    Voucher uuDai = voucherService.getVoucherById(voucher);
                    if (uuDai.getStatus() != 1){
                        attributes.addFlashAttribute("error", "Voucher đã hết số lượng hoặc hết hạn");
                        return "redirect:/checkout";
                    }
                    if (sessionCart.getTotalPrice().compareTo(uuDai.getMinimumPrice()) < 0){
                        attributes.addFlashAttribute("error", "Đơn hàng không đủ giá tối thiểu của voucher");
                        return "redirect:/checkout";
                    }
                    BigDecimal totalPrice = sessionCart.getTotalPrice().add(shippingFee).subtract(uuDai.getValue());
                    jsonData = paymentService.zalopayCreate(totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, String.valueOf(voucher));
                }
                return "redirect:" + jsonData.get("payUrl").toString().replaceAll("\"", "");
            }
            if (voucher != null){
                Voucher uuDai = voucherService.getVoucherById(voucher);
                if (uuDai.getStatus() != 1){
                    attributes.addFlashAttribute("error", "Voucher đã hết số lượng hoặc hết hạn");
                    return "redirect:/checkout";
                }
            }
            billService.placeOrderSession(sessionCart, email, name, specificAddress, ward, district, city, phoneNumber, payment, voucher);
            attributes.addFlashAttribute("mess", "Đặt hàng thành công! Vui lòng kiểm tra mail của bạn để biết thêm chi tiết.");
            session.removeAttribute("totalItems");
            return "redirect:/home";
        } else {
            Cart cart = cartService.getCart(principal.getName());
            cartService.reloadCartDetail(cart);
            if (cart.getCartDetails().isEmpty()){
                attributes.addFlashAttribute("error", "Các sản phẩm trong giỏ hàng của bạn đã hết hàng");
                return "redirect:/cart";
            }
            if (payment.equals("VNPAY")){
                BigDecimal shippingFee = ghnUtil.calculateShippingFee(city, district, ward, cart.getTotalItems());
                //Long dok
                if (voucher == null){
                    BigDecimal totalPrice = cart.getTotalPrice().add(shippingFee);
                    jsonData = paymentService.vnpayCreate(request, totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, null);
                } else {
                    Voucher uuDai = voucherService.getVoucherById(voucher);
                    if (uuDai.getStatus() != 1){
                        attributes.addFlashAttribute("error", "Voucher đã hết số lượng hoặc hết hạn");
                        return "redirect:/checkout";
                    }
                    if (cart.getTotalPrice().compareTo(uuDai.getMinimumPrice()) < 0){
                        attributes.addFlashAttribute("error", "Đơn hàng không đủ giá tối thiểu của voucher");
                        return "redirect:/checkout";
                    }
                    BigDecimal totalPrice = cart.getTotalPrice().add(shippingFee).subtract(uuDai.getValue());
                    jsonData = paymentService.vnpayCreate(request, totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, String.valueOf(voucher));
                }
                return "redirect:" + jsonData.get("payUrl").toString().replaceAll("\"", "");
            }
            if (payment.equals("Momo")){
                BigDecimal shippingFee = ghnUtil.calculateShippingFee(city, district, ward, cart.getTotalItems());
                //Long dok
                if (voucher == null){
                    BigDecimal totalPrice = cart.getTotalPrice().add(shippingFee);
                    jsonData = paymentService.MomoPayCreate(totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, null);
                } else {
                    Voucher uuDai = voucherService.getVoucherById(voucher);
                    if (uuDai.getStatus() != 1){
                        attributes.addFlashAttribute("error", "Voucher đã hết số lượng hoặc hết hạn");
                        return "redirect:/checkout";
                    }
                    if (cart.getTotalPrice().compareTo(uuDai.getMinimumPrice()) < 0){
                        attributes.addFlashAttribute("error", "Đơn hàng không đủ giá tối thiểu của voucher");
                        return "redirect:/checkout";
                    }
                    BigDecimal totalPrice = cart.getTotalPrice().add(shippingFee).subtract(uuDai.getValue());
                    jsonData = paymentService.MomoPayCreate(totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, String.valueOf(voucher));
                }
                return "redirect:" + jsonData.get("payUrl").toString().replaceAll("\"", "");
            }
            if (payment.equals("ZaloPay")){
                BigDecimal shippingFee = ghnUtil.calculateShippingFee(city, district, ward, cart.getTotalItems());
                //Long dok
                if (voucher == null){
                    BigDecimal totalPrice = cart.getTotalPrice().add(shippingFee);
                    jsonData = paymentService.zalopayCreate(totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, null);
                } else {
                    Voucher uuDai = voucherService.getVoucherById(voucher);
                    if (uuDai.getStatus() != 1){
                        attributes.addFlashAttribute("error", "Voucher đã hết số lượng hoặc hết hạn");
                        return "redirect:/checkout";
                    }
                    if (cart.getTotalPrice().compareTo(uuDai.getMinimumPrice()) < 0){
                        attributes.addFlashAttribute("error", "Đơn hàng không đủ giá tối thiểu của voucher");
                        return "redirect:/checkout";
                    }
                    BigDecimal totalPrice = cart.getTotalPrice().add(shippingFee).subtract(uuDai.getValue());
                    jsonData = paymentService.zalopayCreate(totalPrice.longValue(), specificAddress, ward, district, city, name, phoneNumber, email, String.valueOf(voucher));
                }
                return "redirect:" + jsonData.get("payUrl").toString().replaceAll("\"", "");
            }
            if (voucher != null){
                Voucher uuDai = voucherService.getVoucherById(voucher);
                if (uuDai.getStatus() != 1){
                    attributes.addFlashAttribute("error", "Voucher đã hết số lượng hoặc hết hạn");
                    return "redirect:/checkout";
                }
            }
            billService.placeOrder(cart, name, specificAddress, ward, district, city, phoneNumber, payment, voucher);
            session.removeAttribute("totalItems");
            attributes.addFlashAttribute("mess", "Đặt hàng thành công!");
        }
        return "redirect:/orders";
    }

    @GetMapping("/vnpay/return")
    public String vnpayReturn(Principal principal, HttpSession session, VNPaymentResponse VNPaymentResponse, RedirectAttributes attributes) {
        if (principal == null) {
            if (VNPaymentResponse.getVnp_ResponseCode().equals("00")) {
                SessionCart sessionCart = (SessionCart) session.getAttribute("sessionCart");
                String payment = "Thanh toán qua VNPAY";
                String specificAddress = jsonData.get("specificAddress").toString().replaceAll("\"", "");
                String ward = jsonData.get("ward").toString().replaceAll("\"", "");
                String district = jsonData.get("district").toString().replaceAll("\"", "");
                String city = jsonData.get("city").toString().replaceAll("\"", "");
                String name = jsonData.get("name").toString().replaceAll("\"", "");
                String phoneNumber = jsonData.get("phoneNumber").toString().replaceAll("\"", "");
                String email = jsonData.get("email").toString().replaceAll("\"", "");
                String voucher = jsonData.get("voucher").toString().replaceAll("\"", "");
                if (voucher.equals("null")){
                    billService.placeOrderSession(sessionCart, email, name, specificAddress, ward, district, city, phoneNumber, payment, null);
                } else {
                    billService.placeOrderSession(sessionCart, email, name, specificAddress, ward, district, city, phoneNumber, payment, Integer.valueOf(voucher));
                }
                attributes.addFlashAttribute("mess", "Đặt hàng thành công! Vui lòng kiểm tra mail của bạn để biết thêm chi tiết.");
                session.removeAttribute("totalItems");
                return "redirect:/home";
            } else {
                attributes.addFlashAttribute("error", "Thanh toán không thành công");
                return "redirect:/checkout";
            }
        } else {
            if (VNPaymentResponse.getVnp_ResponseCode().equals("00")) {
                Cart cart = cartService.getCart(principal.getName());
                String payment = "Thanh toán qua VNPAY";
                String specificAddress = jsonData.get("specificAddress").toString().replaceAll("\"", "");
                String ward = jsonData.get("ward").toString().replaceAll("\"", "");
                String district = jsonData.get("district").toString().replaceAll("\"", "");
                String city = jsonData.get("city").toString().replaceAll("\"", "");
                String name = jsonData.get("name").toString().replaceAll("\"", "");
                String phoneNumber = jsonData.get("phoneNumber").toString().replaceAll("\"", "");
                String voucher = jsonData.get("voucher").toString().replaceAll("\"", "");
                if (voucher.equals("null")){
                    billService.placeOrder(cart, name, specificAddress, ward, district, city, phoneNumber, payment, null);
                } else {
                    billService.placeOrder(cart, name, specificAddress, ward, district, city, phoneNumber, payment, Integer.valueOf(voucher));
                }
                session.removeAttribute("totalItems");
                attributes.addFlashAttribute("mess", "Đặt hàng thành công!");
                return "redirect:/orders";
            } else {
                attributes.addFlashAttribute("error", "Thanh toán không thành công");
                return "redirect:/checkout";
            }
        }
    }

    @GetMapping("/momo/return")
    public String momoReturn(Principal principal, HttpSession session,MomoPaymentResponse momoPaymentResponse, RedirectAttributes attributes) {
        if (principal == null){
            if (momoPaymentResponse.getResultCode().equals("0")) {
                SessionCart sessionCart = (SessionCart) session.getAttribute("sessionCart");
                String payment = "Thanh toán qua Momo";
                String specificAddress = jsonData.get("specificAddress").toString().replaceAll("\"", "");
                String ward = jsonData.get("ward").toString().replaceAll("\"", "");
                String district = jsonData.get("district").toString().replaceAll("\"", "");
                String city = jsonData.get("city").toString().replaceAll("\"", "");
                String name = jsonData.get("name").toString().replaceAll("\"", "");
                String phoneNumber = jsonData.get("phoneNumber").toString().replaceAll("\"", "");
                String email = jsonData.get("email").toString().replaceAll("\"", "");
                String voucher = jsonData.get("voucher").toString().replaceAll("\"", "");
                if (voucher.equals("null")){
                    billService.placeOrderSession(sessionCart, email, name, specificAddress, ward, district, city, phoneNumber, payment, null);
                } else {
                    billService.placeOrderSession(sessionCart, email, name, specificAddress, ward, district, city, phoneNumber, payment, Integer.valueOf(voucher));
                }
                attributes.addFlashAttribute("mess", "Đặt hàng thành công! Vui lòng kiểm tra mail của bạn để biết thêm chi tiết.");
                session.removeAttribute("totalItems");
                return "redirect:/home";
            } else {
                attributes.addFlashAttribute("error", "Thanh toán không thành công");
                return "redirect:/checkout";
            }
        } else {
            if (momoPaymentResponse.getResultCode().equals("0")) {
                Cart cart = cartService.getCart(principal.getName());
                String payment = "Thanh toán qua Momo";
                String specificAddress = jsonData.get("specificAddress").toString().replaceAll("\"", "");
                String ward = jsonData.get("ward").toString().replaceAll("\"", "");
                String district = jsonData.get("district").toString().replaceAll("\"", "");
                String city = jsonData.get("city").toString().replaceAll("\"", "");
                String name = jsonData.get("name").toString().replaceAll("\"", "");
                String phoneNumber = jsonData.get("phoneNumber").toString().replaceAll("\"", "");
                String voucher = jsonData.get("voucher").toString().replaceAll("\"", "");
                if (voucher.equals("null")){
                    billService.placeOrder(cart, name, specificAddress, ward, district, city, phoneNumber, payment, null);
                } else {
                    billService.placeOrder(cart, name, specificAddress, ward, district, city, phoneNumber, payment, Integer.valueOf(voucher));
                }
                session.removeAttribute("totalItems");
                attributes.addFlashAttribute("mess", "Đặt hàng thành công!");
                return "redirect:/orders";
            } else {
                attributes.addFlashAttribute("error", "Thanh toán không thành công");
                return "redirect:/checkout";
            }
        }
    }

    @GetMapping("/zalo/return")
    public String zaloReturn(Principal principal, HttpSession session, ZaloPaymentResponse zaloPaymentResponse, RedirectAttributes attributes) {
        if (principal == null){
            if (zaloPaymentResponse.getStatus().equals("1")) {
                SessionCart sessionCart = (SessionCart) session.getAttribute("sessionCart");
                String payment = "Thanh toán qua ZaloPay";
                String specificAddress = jsonData.get("specificAddress").toString().replaceAll("\"", "");
                String ward = jsonData.get("ward").toString().replaceAll("\"", "");
                String district = jsonData.get("district").toString().replaceAll("\"", "");
                String city = jsonData.get("city").toString().replaceAll("\"", "");
                String name = jsonData.get("name").toString().replaceAll("\"", "");
                String phoneNumber = jsonData.get("phoneNumber").toString().replaceAll("\"", "");
                String email = jsonData.get("email").toString().replaceAll("\"", "");
                String voucher = jsonData.get("voucher").toString().replaceAll("\"", "");
                if (voucher.equals("null")){
                    billService.placeOrderSession(sessionCart, email, name, specificAddress, ward, district, city, phoneNumber, payment, null);
                } else {
                    billService.placeOrderSession(sessionCart, email, name, specificAddress, ward, district, city, phoneNumber, payment, Integer.valueOf(voucher));
                }
                attributes.addFlashAttribute("mess", "Đặt hàng thành công! Vui lòng kiểm tra mail của bạn để biết thêm chi tiết.");
                session.removeAttribute("totalItems");
                return "redirect:/home";
            } else {
                attributes.addFlashAttribute("error", "Thanh toán không thành công");
                return "redirect:/checkout";
            }
        } else {
            if (zaloPaymentResponse.getStatus().equals("1")) {
                Cart cart = cartService.getCart(principal.getName());
                String payment = "Thanh toán qua ZaloPay";
                String specificAddress = jsonData.get("specificAddress").toString().replaceAll("\"", "");
                String ward = jsonData.get("ward").toString().replaceAll("\"", "");
                String district = jsonData.get("district").toString().replaceAll("\"", "");
                String city = jsonData.get("city").toString().replaceAll("\"", "");
                String name = jsonData.get("name").toString().replaceAll("\"", "");
                String phoneNumber = jsonData.get("phoneNumber").toString().replaceAll("\"", "");
                String voucher = jsonData.get("voucher").toString().replaceAll("\"", "");
                if (voucher.equals("null")){
                    billService.placeOrder(cart, name, specificAddress, ward, district, city, phoneNumber, payment, null);
                } else {
                    billService.placeOrder(cart, name, specificAddress, ward, district, city, phoneNumber, payment, Integer.valueOf(voucher));
                }
                session.removeAttribute("totalItems");
                attributes.addFlashAttribute("mess", "Đặt hàng thành công!");
                return "redirect:/orders";
            } else {
                attributes.addFlashAttribute("error", "Thanh toán không thành công");
                return "redirect:/checkout";
            }
        }
    }
}
