package siorsys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import siorsys.model.Food;
import siorsys.model.Order;
import siorsys.model.User;
import siorsys.service.FoodService;
import siorsys.service.OrderService;
import siorsys.service.SessionService;
import siorsys.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@SessionAttributes({"cart"})
public class MenuController {

    @Autowired
    OrderService orderService;
    @Autowired
    FoodService foodService;
    @Autowired
    SessionService sessionService;
    @Autowired
    UserService userService;

    @RequestMapping("/menu")
    public String menu(
            HttpServletRequest request,
            HttpServletResponse response,
            ModelMap map, @RequestParam(name = "type", defaultValue = "0", required = false) int type) throws Exception {
        HttpSession currentSession = request.getSession(false);
        if (sessionService.isValidSession(currentSession)) {
            if (!map.containsAttribute("cart")) map.addAttribute("cart", new ArrayList<Food>());
            else
                map.addAttribute("total", foodService.calculateTotal((List<Food>) currentSession.getAttribute("cart")));
            User currentUser = sessionService.getUserFromSession(currentSession);
            if (type == 0)
                map.addAttribute("menu", foodService.getAllFood());
            else map.addAttribute("menu", foodService.getFoodByType(type));

            map.addAttribute("username", currentUser.getLogin());
            if (sessionService.isUserAdmin(currentUser)) map.addAttribute("admin", true);
            return "menu";
        }
        response.sendRedirect("/");
        return "home";
    }

    @RequestMapping("/admin")
    public String admin(HttpServletResponse response,
                        HttpServletRequest request,
                        ModelMap map) throws Exception {
        if (sessionService.isUserAdmin(sessionService.getUserFromSession(request.getSession()))) {
            map.addAttribute("food", foodService.getAllFood());
            map.addAttribute("users", userService.getAllUsers());
            return "admin";
        }
        response.sendRedirect("/");
        return "home";
    }

    @RequestMapping(value = "/addProduct-{id}", method = RequestMethod.POST)
    public String addProduct(@PathVariable int id, HttpServletRequest request,
                             @ModelAttribute("cart") ArrayList<Food> cart,
                             ModelMap map) throws Exception {
        HttpSession session = request.getSession(false);
        if (sessionService.isValidSession(session)) {
            Food food = foodService.getById(id);
            if (cart != null) cart.add(food);
        }
        return "redirect:menu";
    }

    @RequestMapping(value = "/clear-cart")
    public String clearCart(ModelMap map) {
        map.addAttribute("cart", new ArrayList<Food>());
        return "redirect:menu";
    }

    @RequestMapping(value = "/checkout")
    public String checkout(HttpServletRequest request,
                           ModelMap map) {
        HttpSession currentSession = request.getSession(false);
        if (sessionService.isValidSession(currentSession)) {
            List<Food> foodInCart = (List<Food>) currentSession.getAttribute("cart");
            double total = foodService.calculateTotal(foodInCart);
            User currentUser = sessionService.getUserFromSession(currentSession);
            if (foodInCart.size() != 0) {
                Order order = new Order();
                order.setTotal(total);
                order.setCreatorId(currentUser.getId());
                StringBuilder orderBuilder = new StringBuilder(foodInCart.get(0).getTitle());
                for (int i = 1; i < foodInCart.size(); i++) {
                    orderBuilder.append(":").append(foodInCart.get(i).getTitle());
                }
                order.setOrdered(orderBuilder.toString());
                System.out.println(order.getOrdered());
                orderService.save(order);
                map.addAttribute("cart", new ArrayList<Food>());
                map.addAttribute("successful",true);
            }

        }
        return "redirect:menu";
    }

}
