package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.healper.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@RequestMapping("/")
	public String home(Model m) {

		m.addAttribute("tittle", "Home - smart contact manager");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model m) {

		m.addAttribute("tittle", "About - smart contact manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model m) {

		m.addAttribute("tittle", "Register - smart contact manager");
		m.addAttribute("user", new User());
		return "signup";
	}

	// Handler for registering user
	@RequestMapping(value = "/do-register", method = RequestMethod.POST)
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {

			if (result.hasErrors()) {
				System.out.println(result);
				return "signup";
			}

			if (!agreement) {
				System.out.println("You have not agreed terms and conditions");
				throw new Exception(" You have not agreed terms and conditions");
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("agreement : " + agreement);
			System.out.println("User : " + user);

			User result1 = userRepository.save(user);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));

		} catch (Exception e) {
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Somthing went wring !!" + e.getMessage(), "alert-danger"));
			return "signup";
		}

		return "signup";
	}


	// Handler for custom login
	@RequestMapping("/signin")
	public String customLogin(Model m) {

		m.addAttribute("tittle", "Signin - smart contact manager");
		return "login";
	}

}
