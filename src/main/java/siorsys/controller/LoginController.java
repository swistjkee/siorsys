package siorsys.controller;

import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import siorsys.model.User;
import siorsys.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Controller
@SessionAttributes("user")
public class LoginController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(HttpServletRequest request) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println(request.getRemoteAddr() + " " + dateFormat.format(Calendar.getInstance().getTime()));
        if (request.getSession() != null) {
            if (request.getSession().getAttribute("user") != null) {
                User user = (User) request.getSession().getAttribute("user");
                if (user != null) return "menu";
            }
        }
        return "home";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter("login");
        String hashedPass = hashMD5(request.getParameter("pass"));
        User user = userService.getUserByLogin(login);
        if (user != null && user.getPass().equals(hashedPass)) {
            request.getSession().setAttribute("user", user);
            response.sendRedirect("/menu");
            return "menu";
        }

        return "home";
    }

    @RequestMapping(value = "/logout")
    public String logout(SessionStatus status, HttpServletResponse response) throws IOException {
        status.setComplete();
        response.sendRedirect("/");
        return "home";
    }


    private static String hashMD5(String input) {
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            return HexUtils.toHexString(MD5.digest(input.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
