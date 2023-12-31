package SmarterContactManager.SmarterContactManager.dao;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import SmarterContactManager.SmarterContactManager.entities.Contact;
import SmarterContactManager.SmarterContactManager.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{

	//pagination...
	
	@Query("from Contact as c where c.user.id = :userId")
	public Page<Contact> findContactByUser(@Param("userId")int userId , Pageable pageable); //current page and contact per page in the pageable
	
	//search
	public List<Contact> findByNameContainingAndUser(String name , User user);
}
