package SmarterContactManager.SmarterContactManager.controller;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import SmarterContactManager.SmarterContactManager.dao.UserRepository;
import SmarterContactManager.SmarterContactManager.entities.User;
import SmarterContactManager.SmarterContactManager.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/")
	public String home(Model model) {
		
		model.addAttribute("title" , "Home - smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title" , "About - smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
	
		model.addAttribute("title" , "Register - smart Contact Manager");
		model.addAttribute("user" , new User());
		return "register";
	}
	
	//handle the register
	@RequestMapping(value = "do_register" , method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user ,

			BindingResult result,
			@RequestParam(value = "agreement" , defaultValue = "false") boolean agreement , 
			Model model,
			HttpSession session) {
		
		try {

			if(result.hasErrors()) {
				model.addAttribute("user" , user);
				return "register";
			}
			
			if(!agreement) {
				System.out.println("You have not check the terms and conditions");
				throw new Exception("You have not check the terms and conditions");
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			//save with the repository in the db
			this.userRepository.save(user);
			
			model.addAttribute("user" , new User());
			
			session.setAttribute("message", new Message("Sucessfully Registered" , "alert-success"));
			
		}
		catch(Exception e) {
			model.addAttribute("user" , user);
			session.setAttribute("message", new Message("something went Wrong !!! " + e.getMessage(), "alert-danger"));
			e.printStackTrace();
		}
		return "register";
	}

	@GetMapping("signin")
	public String customerLogin(Model model) {
		model.addAttribute("title" , "Login-page");
		return "login";
	}
}
