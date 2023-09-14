package SmarterContactManager.SmarterContactManager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import SmarterContactManager.SmarterContactManager.dao.ContactRepository;
import SmarterContactManager.SmarterContactManager.dao.UserRepository;
import SmarterContactManager.SmarterContactManager.entities.Contact;
import SmarterContactManager.SmarterContactManager.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userepository;
	@Autowired
	private ContactRepository contactRepository;
	
	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query")String queryString , Principal principal){
		
//		System.out.println(queryString);
		
		User user = this.userepository.getUserByUserName(principal.getName());
		
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(queryString, user);
		
		return ResponseEntity.ok(contacts);
	}
	
}
