package com.jlw.user;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jlw.audit.AuditTrail;
import com.jlw.audit.AuditTrailRepository;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AuditTrailRepository auditTrailRepository;
	
	
	public User createUser(User user) {
		User createdUser=userRepository.save(user);
		
	 String creater=SecurityContextHolder.getContext().getAuthentication().getName();
    AuditTrail audit=new AuditTrail();
	  audit.setCreatedBy(creater);
	  audit.setCreatedAt(LocalDateTime.now());
	  audit.setActionType("post");
	  audit.setAffectedClassName("User");
	  audit.setAffectedRecordId(createdUser.getId());
	  audit.setAffectedRecordName(createdUser.getName());
	  audit.setModifiedAt(LocalDateTime.now());
	  audit.setModifiedBy(creater);
	  audit.setModifiedfield("-");
	  
	  
	  auditTrailRepository.save(audit);
//	 System.out.println(audit.getAffectedRecordName());
		
		return createdUser;
		
	}
	
	public User UpdateUser( User user) throws UserPrincipalNotFoundException {
	  Optional<User> userDb= userRepository.findById(user.getId());
	       
	  if(userDb.get()!=null) {
		  
			
			 String creater=SecurityContextHolder.getContext().getAuthentication().getName();
		    AuditTrail audit=new AuditTrail();
			  audit.setCreatedBy("-");
			  audit.setCreatedAt(LocalDateTime.now());
			  audit.setActionType("update");
			  audit.setAffectedClassName("User");
			  audit.setAffectedRecordId(user.getId());
			  audit.setAffectedRecordName(user.getName());
			  audit.setModifiedAt(LocalDateTime.now());
			  audit.setModifiedBy(creater);
			  
			  String fieldName="";
			  String oldField="";
			  String newField="";
			  if(!user.getName().equals(userDb.get().getName())) {
				  fieldName="name";
				  oldField=userDb.get().getName();
				  newField=user.getName();
				  
			  }else {
				  fieldName="email";
				  oldField=userDb.get().getEmail();
				  newField=user.getEmail();
				  
			  }
			  
			  
			  
			  
			  audit.setModifiedfield(fieldName+" -"+oldField+" changed into "+newField);
			  
//			  System.out.println(audit.getModifiedfield());
			  
			  auditTrailRepository.save(audit);
		  
		  
		  
		  
		  
		  
		  
		  return userRepository.save(user);
	  }
	  else {
		  throw new UserPrincipalNotFoundException("User not found with the id "+user.getId());
	  }
		
		
		
	}

	public String deleteUser(int id) throws UserPrincipalNotFoundException {
		Optional<User> userDb= userRepository.findById(id);
		
		
		
		
		
		 if(userDb.get()!=null) {
			 
				
			 String creater=SecurityContextHolder.getContext().getAuthentication().getName();
		    AuditTrail audit=new AuditTrail();
			  audit.setCreatedBy(creater);
			  audit.setCreatedAt(LocalDateTime.now());
			  audit.setActionType("deleted");
			  audit.setAffectedClassName("User");
			  audit.setAffectedRecordId(userDb.get().getId());
			  audit.setAffectedRecordName(userDb.get().getName());
			  audit.setModifiedAt(LocalDateTime.now());
			  audit.setModifiedBy(creater);
			  audit.setModifiedfield("-");
			 
			   userRepository.deleteById(id);
			   return "user deleted sucessfully";
		  }
		  else {
			  throw new UserPrincipalNotFoundException("User not found with the id "+id);
		  }
	  }
}
