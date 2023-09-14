package SmarterContactManager.SmarterContactManager.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import SmarterContactManager.SmarterContactManager.helper.Message;
import SmarterContactManager.SmarterContactManager.services.EmailService;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService emailService;

	Random random = new Random(1000);
	
	//forgot form handler
	@GetMapping("/forgot")
	public String forgotform() {
		return "forgot-form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email")String email , HttpSession session) {
		System.out.println(email);
		
		//generating otp of 4 digit
		
		int otp = random.nextInt(999999);
		
		System.out.println("otp:" + otp);
		
//		send otp to the mail
		
		String subject = "OTP from SCM";
		String message = "<h1> OTP = " + otp + "</h1>";
		String to = email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if(flag) {
			session.setAttribute("message", new Message("Otp is successfully send to your registered email!!" , "success"));			
			return "verify-otp";
		}
		else {
			session.setAttribute("message", new Message("Something went wrong" , "danger"));			
			return "forgot-form";
		}
		
	}
	
}
