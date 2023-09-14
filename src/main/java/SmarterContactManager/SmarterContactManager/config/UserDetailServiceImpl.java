package SmarterContactManager.SmarterContactManager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import SmarterContactManager.SmarterContactManager.dao.UserRepository;
import SmarterContactManager.SmarterContactManager.entities.User;

public class UserDetailServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//fetching user from db
		
		User user = repository.getUserByUserName(username);
		
		if(user == null) {
			throw new UsernameNotFoundException("Could not found!");
		}
		
		CustomerUserDetails cutomCustomerUserDetails = new CustomerUserDetails(user);
		
		return cutomCustomerUserDetails;
	}

}
