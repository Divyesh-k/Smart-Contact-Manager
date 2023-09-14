package SmarterContactManager.SmarterContactManager.controller;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.hibernate.hql.internal.ast.tree.SessionFactoryAwareNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import SmarterContactManager.SmarterContactManager.dao.ContactRepository;
import SmarterContactManager.SmarterContactManager.dao.UserRepository;
import SmarterContactManager.SmarterContactManager.entities.Contact;
import SmarterContactManager.SmarterContactManager.entities.User;
import SmarterContactManager.SmarterContactManager.helper.Message;



@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	public UserRepository repository;
	
	@Autowired
	public ContactRepository contactRepository;
	
	@Autowired
	public BCryptPasswordEncoder encoder;
	
	@ModelAttribute //for automatic add common data to the response
	public void commonData(Model model , Principal principal) {
		String username = principal.getName();
		
		//get the user with username
		User user = repository.getUserByUserName(username);
 		
		//put it into model
		model.addAttribute("user" , user);
	}
	
	@GetMapping("/index")
	public String dashboard(Model model , Principal principal) {

		model.addAttribute("title" , "user Dashboard");
		return "normal/user_dashboard";
	}
	
	//open add form
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title" , "Add contact");
		model.addAttribute("contact" , new Contact());
		return "normal/add-contact-form";
	}
	
	//process contact and contact form
	//add the contacct into the user and add into the contacts
	@PostMapping(value = "/process-contact" )
	public String process_contact(@ModelAttribute Contact contact ,
			Principal principal , HttpSession session){
		
		try {

			String name = principal.getName();
			
			User user = this.repository.getUserByUserName(name);
			
//			file management
//			if(file.isEmpty()) {
//				//if the file is empty then try our message
//				
//				System.out.println("file is empty");
//				
//			}else {
//				//upload the file to folder and update the name to contact
//				contact.setimageUrl(file.getOriginalFilename());
//				
//				File savefile = new ClassPathResource("static/img").getFile();
//				
//				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
//				
//				Files.copy(file.getInputStream() , path , StandardCopyOption.REPLACE_EXISTING);
//				
//				System.out.println("image is uploaded");
//			}
			contact.setImageUrl("contact.jpg");
			
			contact.setUser(user);
			
			user.getContacts().add(contact);
			
			this.repository.save(user);
			
			//messessage success.....
			session.setAttribute("message", new Message("Your contact is added successfully add!! add More.." , "success"));
			
		}catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
			session.setAttribute("message", new Message("Something went wrong !! try again later" , "danger"));

		}
		
		return "normal/add-contact-form";
	}
	
	
	//show contact handler
	//per page = [n] (here it is 5)
	//current page = 0 [page]
	@GetMapping("/show-contact/{page}")
	public String showContact(Model model , Principal principal , @PathVariable("page")Integer page) {
		
		model.addAttribute("title" , "show-contact");
		
		String name = principal.getName();
		
		User userByUserName = this.repository.getUserByUserName(name);
		
		//give the information from the page request 
		Pageable pageable = PageRequest.of(page, 5);
		
		Page<Contact> contacts = this.contactRepository.findContactByUser(userByUserName.getId() , pageable);
		
		model.addAttribute("contacts" , contacts);
		model.addAttribute("currentPage" , page);
		model.addAttribute("totalPages" , contacts.getTotalPages());
		
		return "normal/show-contact";
		
	}
	
	
	//handler for the personal view
	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId")Integer cId , Model model , Principal principal) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		
		Contact contact = contactOptional.get();
		
		String name = principal.getName();
		User user = this.repository.getUserByUserName(name);
		
		
		if(user.getId() == contact.getUser().getId()) 
			model.addAttribute("contact" , contact);
		
		return "normal/contact-detail";
	}
	
	
	//delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cidInteger , Model model , Principal principal , HttpSession session) {
		
		Optional<Contact> cOptional = this.contactRepository.findById(cidInteger);
		
		Contact contact = cOptional.get();
		
		String name = principal.getName();
		User user = this.repository.getUserByUserName(name);
		
		//delete img also on the path of 
		// /img
//		contact.getImageUrl();
		
		//check for the crash
		if(user.getId() == contact.getUser().getId()) {

			user.getContacts().remove(contact);
			this.repository.save(user);
			
		}
		
		session.setAttribute("deletemsg" , new Message("Contact deleted Successfully..." , "success"));
		
		return "redirect:/user/show-contact/0";
	}
	
	//open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(Model model , @PathVariable("cid") Integer cid) {
		model.addAttribute("title", "Smart Contact Manager - Update form");
		
		Contact contact = this.contactRepository.findById(cid).get();
		
		model.addAttribute("contact" , contact);
		
		return "normal/update-form";
	}
	
	
	//processing the update
	@PostMapping("/update-contact")
	public String processUpdate(@ModelAttribute("contact")Contact contact ,
			Model model ,
			Principal principal,
			HttpSession session
			) {
		
		//work on image
		Contact oldContact = this.contactRepository.findById(contact.getCid()).get();
		
		if(oldContact.getImageUrl().equals(null)) {
			contact.setImageUrl("contact.jpg");
		}else {
			contact.setImageUrl(oldContact.getImageUrl());
		}
		
		model.addAttribute("title" , "processing the form");
		
		User user = this.repository.getUserByUserName(principal.getName());
		
		contact.setUser(user);
		
		this.contactRepository.save(contact);
		
		session.setAttribute("updatemsg", new Message("Contact updated Successfully..." , "success"));
		
		return "redirect:/user/" + contact.getCid() + "/contact";
	}
	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {

		model.addAttribute("title" , "smart contact manager - Dashboard");
		return "normal/profile";
	}
	
	//open setting handler
	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldps")String oldPassword ,
			@RequestParam("newps")String newPassword,
			Principal principal,
			HttpSession session) {
		
		User curruser = this.repository.getUserByUserName(principal.getName());
		
		if(this.encoder.matches(oldPassword, curruser.getPassword())) {
			//change the password
			curruser.setPassword(this.encoder.encode(newPassword));
			this.repository.save(curruser);
			
			session.setAttribute("message", new Message("successfully changed" , "success"));
		}else {

			session.setAttribute("message", new Message("Your password does not match" , "danger"));
			return "normal/settings";
		}
		
		return "redirect:/user/index";
	}
}



















